package com.psj.accommodation.Activity;


import android.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;


import android.os.Bundle;

import android.provider.MediaStore;
import android.support.annotation.Nullable;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;

import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.psj.accommodation.Data.Review;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;


import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// TODO 로그인 후 보여지는 메인 화면
public class ReviewRegistActivity extends AppCompatActivity {

	// 화면 하단에 보여지는 이미지 (홈, 검색, 채팅, 프로필)
	ImageView Home, Search, Chat, MyProfile;
	// 숙박장소 이미지
	ImageView PlaceImage;
	// 등록 및 등록취소 버튼
	Button ReviewOK, ReviewCancel;
	// 기간선택 텍스트
	TextView PlaceTime;
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
	// 받아온 이미지 저장 객체 (NullPointerException 방지를 위한 임의 객체 생성)
	private File tempFile = new File("test");
	// 다이얼로그 전역변수
	AlertDialog alertDialog;
	// 리뷰 데이터 임시저장 객체
	Review review;
	// 쉐어드에 저장된 사용자 이름 (세션)
	String sessionName = "";
	// 숙박 시작 날짜
	String selectDateStart = "";
	// 숙박 끝 날짜
	String selectDateEnd = "";

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
		PlaceTime = findViewById(R.id.PlaceTime);
		PlaceImage = findViewById(R.id.PlaceImage);
		PlaceName = findViewById(R.id.PlaceName);
		PlaceScore = findViewById(R.id.PlaceScore);

		PlaceScore.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				Toast.makeText(ReviewRegistActivity.this, "평점 : " + rating, Toast.LENGTH_SHORT).show();
			}
		});

		// 데이터 수신
		Bundle getCalendar = getIntent().getExtras();

		if (getCalendar == null) {
			Log.i(TAG, "데이터 수신 할거 없음");
		} else {
			// String 형태 값 가져오기
			selectDateStart = getCalendar.getString("selectDateStart");
			selectDateEnd = getCalendar.getString("selectDateEnd");

			Log.i(TAG, "숙박 시작 날짜 : " + selectDateStart);
			Log.i(TAG, "숙박 끝 날짜 : " + selectDateEnd);

			PlaceTime.setText(selectDateStart + " ~ " + selectDateEnd);
		}


		// 세션 값 가져오기 기능
		getShard();

	}

	@Override
	protected void onResume() {
		super.onResume();

		// 숙박 장소 이미지 클릭 이벤트
		PlaceImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				photoChoice();
			}
		});

		// 기간 선택 텍스트 클릭 이벤트
		PlaceTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent calendarIntent = new Intent(ReviewRegistActivity.this, CalendarActivity.class);
				startActivity(calendarIntent);
				finish();
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


				Log.i(TAG, "ReviewOK : 실행");
				Log.i(TAG, "PlaceTime : " + PlaceTime.getText());
				Log.i(TAG, "PlaceName : " + PlaceName.getText());

				if (PlaceTime.getText().equals("기간선택")) {
					Log.i(TAG, "PlaceTime2 : " + PlaceTime.getText());
					Toast.makeText(ReviewRegistActivity.this, "기간을 선택하세요.", Toast.LENGTH_SHORT).show();
				} else if (PlaceName.getText().length() == 0) {

					Toast.makeText(ReviewRegistActivity.this, "장소명을 입력하세요", Toast.LENGTH_SHORT).show();
				} else {

					Toast.makeText(ReviewRegistActivity.this, "등록을 시작합니다.", Toast.LENGTH_SHORT).show();

					// 리뷰 임시저장 객체
					review = new Review();

					review.setPlaceTime(PlaceTime.getText().toString());
					review.setPlaceName(PlaceName.getText().toString());
					review.setPlaceScore(PlaceScore.getRating());
					review.setWriter(sessionName);

					// 레트로핏 서버 URL 설정해놓은 객체 생성
					RetroClient retroClient = new RetroClient();
					// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
					ApiService apiService = retroClient.getApiClient().create(ApiService.class);

					// 인터페이스 ApiService에 선언한 reviewInsert()를 호출합니다
					Call<String> call = apiService.reviewInsert(review.getPlaceName(), review.getPlaceTime(), review.getPlaceScore(), review.getPlaceImage(), review.getWriter());
					// onResponse() 메서드를 이용해 응답을 전달받아서 요청에 대한 결과를 받을 수 있습니다
					call.enqueue(new Callback<String>() {
						@Override
						public void onResponse(Call<String> call, Response<String> response) {

							Toast.makeText(ReviewRegistActivity.this, "서버에서 받은 응답 값 : " + response.body(), Toast.LENGTH_LONG).show();
							Log.i("onResponse", "" + response.body().toString());
							review.setPlaceNum(response.body().toString());

							if (!response.body().equals("")) {

								Intent MainIntent = new Intent(ReviewRegistActivity.this, MainActivity.class);
								MainIntent.putExtra("PlaceNum", review.getPlaceNum());
								MainIntent.putExtra("PlaceTime", review.getPlaceTime());
								MainIntent.putExtra("PlaceName", review.getPlaceName());
								MainIntent.putExtra("PlaceScore", review.getPlaceScore());
								MainIntent.putExtra("Writer", review.getWriter());
								startActivity(MainIntent);
								finish();
							}


						}

						// 응답 실패 (네트워크 오류가 발생했을 때 등)
						@Override
						public void onFailure(Call<String> call, Throwable throwable) {
							Log.i("onFailure", "" + throwable);
							Toast.makeText(ReviewRegistActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
						}
					}); // enqueue() 끝
				}


			} // onClick() 끝
		}); // ReviewOK 클릭 이벤트 끝

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
	public void photoChoice() {

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

//	// 카메라에서 찍은 사진을 저장할 파일 만들기
//	public File createImageFile() throws IOException {
//
//		Log.i(TAG, "createImageFile : 실행");
//
//		// 이미지 파일 이름 (review_(시간)_)
//		String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
//		String imageFileName = "review_" + timeStamp + "_";
//
//		// 이미지가 저장될 폴더 이름 (review)
//		File storageDir = new File(Environment.getExternalStorageDirectory() + "/review/");
//		if (!storageDir.exists()) storageDir.mkdirs();
//
//		// 빈 파일 생성
//		File image = File.createTempFile(imageFileName, ".jpg", storageDir);
//
//		return image;
//	}

	// 카메라에서 이미지 촬영하기 기능
	private void takePhoto() {

		Log.i(TAG, "takePhoto : 실행");

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


	}

	private void goToAlbum() {

		Log.i(TAG, "goToAlbum : 실행");

		Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		//startActivityForResult(intent, PICK_FROM_ALBUM);
	}


	// startActivityForResult() 반환 기능 requestCode 를 앨범 or 카메라에서 온 경우로 나눠서 처리하는 기능
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.i(TAG, "onActivityResult : 실행");

		// 카메라 촬영하지 않고 돌아왔을 때 처리 로직
		if (resultCode != Activity.RESULT_OK) {

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

		// 카메라 조건 시작
		if (requestCode == PICK_FROM_CAMERA) {


		} // 카메라 조건 끝
		// 갤러리 조건 시작
		else if (requestCode == PICK_FROM_ALBUM) {


		} // 갤러리 조건 끝
	} // onActivityResult() 끝

	public void getShard() {
		SharedPreferences sharedPreferences = getSharedPreferences("sessionName", MODE_PRIVATE);
		sessionName = sharedPreferences.getString("name", "noName");
	}


} // ReviewRegistActivity 클래스 끝
