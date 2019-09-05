package com.psj.accommodation.Activity;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;
import com.squareup.picasso.Picasso;
import com.yongbeom.aircalendar.core.AirCalendarIntent;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;


// TODO 로그인 후 보여지는 메인 화면
public class ReviewRegistActivity extends AppCompatActivity {

	// 화면 하단에 보여지는 이미지 (홈, 검색, 채팅, 프로필)
	ImageView Home, Search, Chat, MyProfile;
	// 숙박장소 이미지
	ImageView PlaceImage;
	// 등록 및 등록취소 버튼
	Button ReviewOK, ReviewCancel;
	// 기간선택 텍스트
	TextView TimeChoice;
	// 숙박장소 이름
	EditText PlaceName;
	// 평점
	RatingBar PlaceScore;

	// 액티비티 태그
	public static final String TAG = "ReviewRegistActivity";
	// 갤러리 사용 후 요청결과 코드드
	private static final int PICK_FROM_ALBUM = 1;
	// 카메라 사용 후 요청결과 코드
	private static final int PICK_FROM_CAMERA = 2;
	// 받아온 이미지 저장 객체
	private File tempFile;
	// 캘린더뷰 라이브러리 객체
	private CalendarView calendarView;
	// 다이얼로그 전역변수
	AlertDialog alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review_regist);

		Home = findViewById(R.id.Home);
		Search = findViewById(R.id.Search);
		Chat = findViewById(R.id.Chat);
		MyProfile = findViewById(R.id.MyProfile);
		ReviewOK = findViewById(R.id.ReviewOK);
		ReviewCancel = findViewById(R.id.ReviewCancel);
		TimeChoice = findViewById(R.id.TimeChoice);
		PlaceImage = findViewById(R.id.PlaceImage);
		PlaceName = findViewById(R.id.PlaceName);
		PlaceScore = findViewById(R.id.PlaceScore);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// 숙박 장소 이미지 클릭 이벤트
		PlaceImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				photochoice();
			}
		});

		// 숙박 기간 선택 클릭 이벤트
		TimeChoice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 달력 다이얼로그를 보여주고 날짜를 범위로 설정할 수 있다

			}
		});

		// 하단 홈 클릭 이벤트
		Home.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent MainIntent = new Intent(ReviewRegistActivity.this, MainActivity.class);
				startActivity(MainIntent);
				finish();
			}
		});

		// 리뷰 등록하기 클릭 이벤트
		ReviewOK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// 레트로핏 서버 URL 설정해놓은 객체 생성
				RetroClient retroClient = new RetroClient();
				// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
				ApiService apiService = retroClient.getApiClient().create(ApiService.class);

				// 인터페이스 ApiService에 선언한 checkUser()를 호출합니다
				Call<String> call;


				Intent MainIntent = new Intent(ReviewRegistActivity.this, MainActivity.class);
				startActivity(MainIntent);
				finish();
			}
		});

		// 리뷰 등록취소 클릭 이벤트
		ReviewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent MainIntent = new Intent(ReviewRegistActivity.this, MainActivity.class);
				startActivity(MainIntent);
				finish();
			}
		});


	} // onResume() 끝

	// 리뷰이미지 클릭 알림 기능
	public void photochoice() {

		AlertDialog.Builder builder = new AlertDialog.Builder(ReviewRegistActivity.this);

		builder.setTitle("이미지 업로드").setMessage("이미지 업로드 방법을 선택하세요");
		builder.setPositiveButton("갤러리", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(ReviewRegistActivity.this, "갤러리", Toast.LENGTH_SHORT).show();
				goToAlbum();
			}
		});

		builder.setNegativeButton("촬영", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(ReviewRegistActivity.this, "촬영", Toast.LENGTH_SHORT).show();
				takePhoto();
			}
		});

		alertDialog = builder.create();

		alertDialog.show();
	}

	// 카메라에서 찍은 사진을 저장할 파일 만들기
	public File createImageFile() throws IOException {

		Log.i(TAG, "createImageFile : 실행");

		// 이미지 파일 이름 (review_(시간)_)
		String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
		String imageFileName = "review_" + timeStamp + "_";

		// 이미지가 저장될 폴더 이름 (review)
		File storageDir = new File(Environment.getExternalStorageDirectory() + "/review/");
		if (!storageDir.exists()) storageDir.mkdirs();

		// 빈 파일 생성
		File image = File.createTempFile(imageFileName, ".jpg", storageDir);

		return image;
	}

	// 카메라에서 이미지 촬영하기 기능
	private void takePhoto() {

		Log.i(TAG, "takePhoto : 실행");

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		try {

			tempFile = createImageFile();
		} catch (IOException e) {
			Toast.makeText(this, "이미지 처리 오류! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
			finish();
			e.printStackTrace();
		}
		if (tempFile != null) {
			// 안드로이드 OS 누가 버전 이후부터는 file:// URI 의 노출을 금지하고 있습니다.
			// 만약 URI 를 그냥 사용하게 되면 FileUriExposedException 발생하게 됩니다.
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

				Uri photoUri = FileProvider.getUriForFile(this,
						"{package name}.provider", tempFile);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
				startActivityForResult(intent, PICK_FROM_CAMERA);

			} else {

				Uri photoUri = Uri.fromFile(tempFile);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
				startActivityForResult(intent, PICK_FROM_CAMERA);

			}
		}
	}

	private void goToAlbum() {

		Log.i(TAG, "goToAlbum : 실행");

		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(intent, PICK_FROM_ALBUM);
	}

	private void setCameraImage() {

		Log.i(TAG, "setCameraImage : 실행");

		Log.i(TAG, "tempFile 절대경로 " + tempFile.getAbsolutePath());
		Log.i(TAG, "tempFile " + tempFile);

		Picasso.with(this)
				.load(String.valueOf(tempFile.toURI()))
				.resize(500, 500)
				.rotate(90f)
				.into(PlaceImage);


	}

	private void setGallayImage() {

		Log.i(TAG, "setGallayImage : 실행");

		Picasso.with(this) // Input 부분
				.load(String.valueOf(tempFile.toURI())) // Operator 시작: URL에서 이미지를 불러옵니다.
				.placeholder(R.drawable.ic_done_gray) // 불러오는 시간 동안 보여줄 이미지 파일입니다.
				.error(R.drawable.ic_close_gray) // 불러오지 못하면 보여주는 이미지 파일입니다.
				.resize(500, 500) // 이미지의 크기를 100x100 사이즈로 리사이즈 해줍니다.
				.rotate(90f) // 사진 파일을 회전해줍시다. Operator 끝났습니다.
				.into(PlaceImage); // Output 부분: 변수 이름을 imageView라고 지정한 ImageView에 이미지를 보여줍니다.
	}

	// startActivityForResult() 반환 기능 requestCode 를 앨범 or 카메라에서 온 경우로 나눠서 처리하는 기능
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.i(TAG, "onActivityResult : 실행");

		if (resultCode != Activity.RESULT_OK) {

			// 카메라 촬영하지 않고 돌아왔을 때 처리 로직

			Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();

			if (tempFile != null) {
				if (tempFile.exists()) {
					if (tempFile.delete()) {
						Log.e(TAG, tempFile.getAbsolutePath() + " 삭제 성공");
						tempFile = null;
					}
				}
			}

			return;
		}

		if (requestCode == PICK_FROM_CAMERA) {

			Toast.makeText(this, "카메라 접근후 돌아왔습니다", Toast.LENGTH_SHORT).show();

			Log.i(TAG, "카메라 사용 후 data " + data.getData());
			setCameraImage();

		} else if (requestCode == PICK_FROM_ALBUM) {

			Uri photoUri = data.getData();

			Log.i(TAG, "photoUri : 실행 " + photoUri);

			Cursor cursor = null;

			try {

				/*
				 *  Uri 스키마를
				 *  content:/// 에서 file:/// 로  변경한다.
				 */
				String[] proj = {MediaStore.Images.Media.DATA};

				assert photoUri != null;
				cursor = getContentResolver().query(photoUri, proj, null, null, null);

				assert cursor != null;
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

				cursor.moveToFirst();


				tempFile = new File(cursor.getString(column_index));

				Log.i(TAG, "cursor.getString : 실행 " + tempFile);

			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}

			Toast.makeText(this, "갤러리 접근후 돌아왔습니다", Toast.LENGTH_SHORT).show();

			setGallayImage();

		}
	}
} // ReviewRegistActivity 클래스 끝
