package com.psj.accommodation.Activity;


import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;


import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;

import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.bumptech.glide.Glide;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;
import com.psj.accommodation.Data.Review;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;


import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.psj.accommodation.PublicString.AllPublicString.REQUEST_MODIFY;
import static com.psj.accommodation.PublicString.AllPublicString.REQUEST_REGIST;


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
	// 숙박장소 이미지 개수 출력, 숙박 후기 글자 수 출력
	TextView PlaceImageCount, ReviewCount;
	// 숙박장소 이름
	EditText PlaceName;
	// 평점
	RatingBar PlaceScore;
	// 숙박 후기
	EditText PlaceReview;
	// 숙박 후기 최대 글자 수
	int MAX_COUNT = 100;
	// 숙박 후기 글자 수 저장
	int ING_COUNT = 0;

	// 액티비티 태그
	public static final String TAG = "ReviewRegistActivity";

	// 다이얼로그 전역변수
	AlertDialog alertDialog;
	// 리뷰 데이터 임시저장 객체
	Review review;
	// 쉐어드에 저장된 사용자 이름 (세션)
	String sessionName = "";
	// 갤러리에서 선택한 이미지 경로 저장할 객체
	StringBuffer stringBuffer = new StringBuffer();
	// 갤러리에서 선택한 이미지 경로 리스트
	ArrayList<Uri> postUriList = new ArrayList<>();
	// 서버에 저장된 이미지 경로 저장 변수
	String ServerImagePath = "";

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
		PlaceImageCount = findViewById(R.id.PlaceImageCount);
		PlaceReview = findViewById(R.id.PlaceReview);
		ReviewCount = findViewById(R.id.ReviewCount);

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
			Log.i(TAG, "데이터 수신 할거 있음");
		}


		// 세션 값 가져오기 기능
		getShard();

	}

	@Override
	protected void onResume() {
		super.onResume();

		// 숙박 후기 글자 수 체크 이벤트
		PlaceReview.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				Log.i(TAG, "PlaceReview beforeTextChanged : 실행");
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
				Log.i(TAG, "PlaceReview onTextChanged : 실행");

				Log.i(TAG, "ING_COUNT : " + ING_COUNT);
				Log.i(TAG, "charSequence.length() : " + charSequence.length());

				ING_COUNT = charSequence.length();

				ReviewCount.setText(ING_COUNT + "/100");

				if (charSequence.length() == MAX_COUNT) {
					ReviewCount.setText(MAX_COUNT + "/100");
					PlaceReview.setSelection(MAX_COUNT);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				Log.i(TAG, "PlaceReview afterTextChanged : 실행");
			}
		});

		// 숙박 장소 이미지 클릭 이벤트
		PlaceImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				goToAlbum();
			}
		});

		// 기간 선택 텍스트 클릭 이벤트
		PlaceTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent calendarIntent = new Intent(ReviewRegistActivity.this, CalendarActivity.class);
				calendarIntent.putExtra("changeDate", REQUEST_REGIST);
				startActivityForResult(calendarIntent, REQUEST_REGIST);
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
				Log.i(TAG, "PlaceReview : " + PlaceReview.getText());
				//Log.i(TAG, "stringBuffer length : " + stringBuffer.length());

				if (stringBuffer.length() == 0) {

					Toast.makeText(ReviewRegistActivity.this, "이미지를 선택해주세요", Toast.LENGTH_SHORT).show();

				} else if (PlaceTime.getText().equals("기간선택")) {

					Toast.makeText(ReviewRegistActivity.this, "기간을 선택하세요", Toast.LENGTH_SHORT).show();

				} else if (PlaceName.getText().length() == 0) {

					Toast.makeText(ReviewRegistActivity.this, "장소명을 입력하세요", Toast.LENGTH_SHORT).show();

				} else if (PlaceReview.getText().length() == 0) {

					Toast.makeText(ReviewRegistActivity.this, "후기를 입력하세요", Toast.LENGTH_SHORT).show();

				} else {

					Toast.makeText(ReviewRegistActivity.this, "등록을 시작합니다", Toast.LENGTH_SHORT).show();

					// 리뷰 임시저장 객체
					review = new Review();

					review.setPlaceReview(PlaceReview.getText().toString());
					review.setPlaceImage(ServerImagePath);
					review.setPlaceTime(PlaceTime.getText().toString());
					review.setPlaceName(PlaceName.getText().toString());
					review.setPlaceScore(PlaceScore.getRating());
					review.setWriter(sessionName);

					// 레트로핏 서버 URL 설정해놓은 객체 생성
					RetroClient retroClient = new RetroClient();
					// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
					ApiService apiService = retroClient.getApiClient().create(ApiService.class);

					// 인터페이스 ApiService에 선언한 reviewInsert()를 호출합니다
					Call<String> call = apiService.reviewInsert(review.getPlaceName(), review.getPlaceTime(), review.getPlaceScore(), review.getPlaceImage(), review.getWriter(), review.getPlaceReview());
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
								MainIntent.putExtra("PlaceImage", review.getPlaceImage());
								MainIntent.putExtra("PlaceTime", review.getPlaceTime());
								MainIntent.putExtra("PlaceName", review.getPlaceName());
								MainIntent.putExtra("PlaceScore", review.getPlaceScore());
								MainIntent.putExtra("PlaceReview", review.getPlaceReview());
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

		alertDialog = builder.create();

		alertDialog.show();
	}

	private void goToAlbum() {

		Log.i(TAG, "goToAlbum : 실행");

		Intent intent = new Intent(this, AlbumSelectActivity.class);
		//set limit on number of images that can be selected, default is 10
		intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 5);
		startActivityForResult(intent, Constants.REQUEST_CODE);


	}


	// startActivityForResult() 반환 기능 requestCode 를 앨범 or 달력 처리하는 기능
	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.i(TAG, "onActivityResult : 실행");

		// 갤러리 접근 후 성공적으로 돌아왔을 때 조건 시작
		if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {
			Log.i(TAG, "갤러리 접근 후 성공적으로 돌아옴");

			postUriList.clear();
			stringBuffer.setLength(0);

			ArrayList<Image> images = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);

			for (int i = 0, l = images.size(); i < l; i++) {
				stringBuffer.append(images.get(i).path + "\n");
				postUriList.add(Uri.parse(images.get(i).path));
				Log.i(TAG, "갤러리에서 선택한 이미지 경로 : " + postUriList.get(i));
			} // 반복문 끝
			Log.i(TAG, "갤러리에서 선택한 이미지 경로 개수 : " + postUriList.size());
			// 갤러리에서 선택한 이미지가 2개 이상 일때 보여줄 개수
			PlaceImageCount.setText("대표 이미지");

			Log.i(TAG, "대표이미지 경로 : " + images.get(0).path);

			// 갤러리에서 처음 선택한 이미지를 대표이미지로 보여줌
			Glide.with(ReviewRegistActivity.this)
					.load(images.get(0).path)
					.placeholder(R.drawable.done_black).centerCrop().into(PlaceImage);
			// 안드로이드 갤러리 경로 출력
			Log.i(TAG, stringBuffer.toString());

			// 서버에 이미지 업로드 기능
			//uploadToServer(images.get(0).path);
			// 서버에 멀티 이미지 업로드 기능
			multiUploadToServer();
			//Log.i(TAG, "PlaceImage : " + PlaceImage.getDrawable());
		} // 갤러리 접근 후 성공적으로 돌아왔을 때 조건 끝

		if (requestCode == REQUEST_REGIST && resultCode == RESULT_OK && data != null) {
			String startDate = data.getStringExtra("selectDateStart");
			String endDate = data.getStringExtra("selectDateEnd");
			PlaceTime.setText(startDate + " ~ " + endDate);
		}


	} // onActivityResult() 끝

	// 쉐어드에 저장된 사용자 이름 가져오기 기능 (세션 활용)
	public void getShard() {
		SharedPreferences sharedPreferences = getSharedPreferences("sessionName", MODE_PRIVATE);
		sessionName = sharedPreferences.getString("name", "noName");
	} // getShard() 끝

	// 서버에 이미지 업로드 기능
	private void uploadToServer(String filePath) {

		Log.i(TAG, "uploadToServer : 실행");

		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);

		Log.i(TAG, "filePath : " + filePath);

		//Create a file object using file path
		File file = new File(filePath);
		Log.i(TAG, "file : " + file);

		// Create a request body with file and image media type
		RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
		Log.i(TAG, "fileReqBody : " + fileReqBody);


		// Create MultipartBody.Part using file request-body,file name and part name
		MultipartBody.Part part = MultipartBody.Part.createFormData("upload", file.getName(), fileReqBody);
		Log.i(TAG, "part : " + part);


		//Create request body with text description and text media type
		RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");
		Log.i(TAG, "description : " + description);

		// 인터페이스 ApiService에 선언한 uploadImage()를 호출합니다
//		Call<String> call = apiService.uploadImage(part, description);
//		call.enqueue(new Callback<String>() {
//			@Override
//			public void onResponse(Call<String> call, Response<String> response) {
//				Log.i(TAG, "onResponse : 실행");
//				Log.i(TAG, "서버에서 받은 응답 값 : " + response.body().toString());
//				ServerImagePath += response.body().toString();
//			}
//
//			@Override
//			public void onFailure(Call<String> call, Throwable throwable) {
//				Log.i("onFailure", "" + throwable);
//				Toast.makeText(ReviewRegistActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
//			}
//		});
	} // 서버에 이미지 업로드 기능 끝

	// 서버로 전달할 멀티 이미지 업로드 기능
	private void multiUploadToServer() {
		Log.i(TAG, "multiUploadToServer : 실행");


		ArrayList<MultipartBody.Part> imageList = new ArrayList<>();

		for (int i = 0; i < postUriList.size(); i++) {
			imageList.add(prepareFilePart("uploadFile_" + (i + 1), postUriList.get(i).toString()));
			//Where selectedFiles is selected file URI list
		}

		RequestBody totalFiles = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(imageList.size()));

		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);

		// 인터페이스 ApiService에 선언한 multiUploadImage()를 호출합니다
		Call<String> multiUploadImage = apiService.multiUploadImage(imageList, totalFiles);

		multiUploadImage.enqueue(new Callback<String>() {
			@Override
			public void onResponse(Call<String> call, Response<String> response) {
				Log.i(TAG, "onResponse 실행");
				Log.i(TAG, "서버에서 응답 받은 값 : " + response.body().toString());
				ServerImagePath = response.body().toString();
			}

			@Override
			public void onFailure(Call<String> call, Throwable t) {
				Log.e(TAG, "onFailure 실행" + t.toString());
			}
		});

	} // 서버로 전달할 멀티 이미지 업로드 기능 끝

	// 서버로 전달할 파일 세부 설정 기능
	public static MultipartBody.Part prepareFilePart(String partName, String fileUri) {
		File file = new File(fileUri);
		String mimeType = URLConnection.guessContentTypeFromName(file.getName());
		Log.e("mimeType", mimeType);
		//create RequestBody instance from file
		RequestBody requestFile = RequestBody.create(MediaType.parse(mimeType), file);
		//MultipartBody.Part is used to send also the actual file name
		return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
	} // 서버로 전달할 파일 세부 설정 기능 끝


} // ReviewRegistActivity 클래스 끝

