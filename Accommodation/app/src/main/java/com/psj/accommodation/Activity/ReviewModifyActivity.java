package com.psj.accommodation.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.psj.accommodation.PublicString.AllPublicString.REQUEST_MODIFY;


// TODO 리뷰 수정 화면
public class ReviewModifyActivity extends AppCompatActivity {

	public static final String TAG = "ReviewModifyActivity";
	// 리뷰 데이터 임시저장 객체
	Review review;
	// 쉐어드에 저장된 사용자 이름 (세션)
	String sessionName = "";
	// 리뷰 고유 번호 저장 변수
	String PlaceNum = "";
	// 리뷰 이미지 경로 저장 변수
	String PlaceImage = "";
	// 갤러리에서 선택한 이미지 경로 저장 변수
	StringBuffer stringBuffer = new StringBuffer();
	// 갤러리에서 선택한 이미지 경로 리스트
	ArrayList<Uri> postUriList = new ArrayList<>();
	// 서버 경로
	String ServerImagePath = "";

	ImageView ModifyImage, Home;
	TextView ModifyName, ModifyTime, ModifyImageCount, ModifyReviewCount;
	RatingBar ModifyScore;
	Button ModifyOK, ModifyCancel;
	EditText ModifyPlaceReview;

	// 숙박 후기 최대 글자 수
	int MAX_COUNT = 100;
	// 숙박 후기 글자 수 저장
	int ING_COUNT = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review_modify);

		ModifyName = findViewById(R.id.ModifyName);
		ModifyTime = findViewById(R.id.ModifyTime);
		ModifyScore = findViewById(R.id.ModifyScore);
		ModifyOK = findViewById(R.id.ModifyOK);
		ModifyCancel = findViewById(R.id.ModifyCancel);
		ModifyImage = findViewById(R.id.ModifyImage);
		ModifyImageCount = findViewById(R.id.ModifyImageCount);
		Home = findViewById(R.id.Home);
		ModifyPlaceReview = findViewById(R.id.ModifyPlaceReview);
		ModifyReviewCount = findViewById(R.id.ModifyReviewCount);


		// 쉐어드에 저장된 사용자 이름 가져오기 기능
		getShard();

		// 데이터 수신
		Bundle getModify = getIntent().getExtras();

		// 인텐트 데이터 수신 조건 시작
		if (getModify == null) {
			Log.i(TAG, "데이터 수신 할거 없음");
		} else {

			Log.i(TAG, "데이터 수신 할거 있음");

			// String, float 형태 값 가져오기
			PlaceNum = getModify.getString("PlaceNum");
			PlaceImage = getModify.getString("PlaceImage");
			String PlaceTime = getModify.getString("PlaceTime");
			String PlaceName = getModify.getString("PlaceName");
			float PlaceScore = getModify.getFloat("PlaceScore");
			String PlaceReview = getModify.getString("PlaceReview");

			Log.i(TAG, "리뷰 번호 : " + PlaceNum);
			Log.i(TAG, "숙박 장소 이미지 경로 : " + PlaceImage);
			Log.i(TAG, "숙박 장소 : " + PlaceName);
			Log.i(TAG, "숙박 기간 : " + PlaceTime);
			Log.i(TAG, "평점 : " + PlaceScore);
			Log.i(TAG, "후기 : " + PlaceReview);

			String[] imagePath = PlaceImage.split(" ");

			Glide.with(this).load(Uri.parse("http://54.180.152.167/" + imagePath[0])).centerCrop().into(ModifyImage);
			ModifyName.setText(PlaceName);
			ModifyTime.setText(PlaceTime);
			ModifyScore.setRating(PlaceScore);
			ModifyPlaceReview.setText(PlaceReview);
			ModifyImageCount.setText("대표 이미지");


		} // 인텐트 데이터 수신 조건 끝

		ModifyScore.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				Toast.makeText(ReviewModifyActivity.this, "평점 : " + rating, Toast.LENGTH_SHORT).show();
			}
		});

	} // onCreate 끝

	@Override
	protected void onResume() {
		super.onResume();

		ModifyPlaceReview.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				Log.i(TAG, "PlaceReview beforeTextChanged : 실행");
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
				Log.i(TAG, "PlaceReview onTextChanged : 실행");

				ING_COUNT = charSequence.length();

				ModifyReviewCount.setText(ING_COUNT + "/100");

				if (charSequence.length() == MAX_COUNT) {
					ModifyReviewCount.setText(MAX_COUNT + "/100");
					ModifyPlaceReview.setSelection(MAX_COUNT);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				Log.i(TAG, "PlaceReview afterTextChanged : 실행");
			}
		});

		Home.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mainIntent = new Intent(ReviewModifyActivity.this, MainActivity.class);
				startActivity(mainIntent);
				finish();
			}
		});

		ModifyImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ReviewModifyActivity.this, AlbumSelectActivity.class);
				//set limit on number of images that can be selected, default is 10
				intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 5);
				startActivityForResult(intent, Constants.REQUEST_CODE);
			}
		});

		// 기간선택 텍스트 클릭 이벤트
		ModifyTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent calendarIntent = new Intent(ReviewModifyActivity.this, CalendarActivity.class);
				calendarIntent.putExtra("changeDate", REQUEST_MODIFY);
				startActivityForResult(calendarIntent, REQUEST_MODIFY);

			}
		});

		// 수정완료 버튼 클릭 이벤트
		ModifyOK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.i(TAG, "ServerImagePath 업데이트 경로1 : " + ServerImagePath);

				if (ServerImagePath.equals("")) {

					ServerImagePath = PlaceImage;

				} else if (ModifyTime.getText().equals("기간선택")) {

					Toast.makeText(ReviewModifyActivity.this, "기간을 선택하세요.", Toast.LENGTH_SHORT).show();
				} else if (ModifyName.getText().length() == 0) {

					Toast.makeText(ReviewModifyActivity.this, "장소명을 입력하세요", Toast.LENGTH_SHORT).show();
				} else if (ModifyPlaceReview.getText().length() == 0) {

					Toast.makeText(ReviewModifyActivity.this, "후기를 입력하세요", Toast.LENGTH_SHORT).show();

				} else {

					Toast.makeText(ReviewModifyActivity.this, "등록을 시작합니다.", Toast.LENGTH_SHORT).show();

					// 리뷰 임시저장 객체
					review = new Review();

					Log.i(TAG, "ServerImagePath 업데이트 경로2 : " + ServerImagePath);
					review.setPlaceImage(ServerImagePath);
					review.setPlaceReview(ModifyPlaceReview.getText().toString());
					review.setPlaceTime(ModifyTime.getText().toString());
					review.setPlaceName(ModifyName.getText().toString());
					review.setPlaceScore(ModifyScore.getRating());
					//review.setWriter(sessionName);

					// 레트로핏 서버 URL 설정해놓은 객체 생성
					RetroClient retroClient = new RetroClient();
					// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
					ApiService apiService = retroClient.getApiClient().create(ApiService.class);

					// 인터페이스 ApiService에 선언한 reviewUpdate()를 호출합니다
					Call<String> call = apiService.reviewUpdate(PlaceNum, review.getPlaceName(), review.getPlaceImage(), review.getPlaceTime(), review.getPlaceScore(), review.getPlaceReview());

					call.enqueue(new Callback<String>() {
						@Override
						public void onResponse(Call<String> call, Response<String> response) {
							Log.i(TAG, "onResponse 실행");

							Toast.makeText(ReviewModifyActivity.this, "서버에서 받은 응답 값 : " + response.body(), Toast.LENGTH_LONG).show();
							Log.i(TAG, "onResponse / " + response.body().toString());

							if (response.body().equals("리뷰수정성공")) {
								// 연산한 결과 값을 resultModifyIntent 에 담아서 ReviewDetailActivity 로 전달하고 현재 Activity 는 종료.
								Intent resultModifyIntent = new Intent(ReviewModifyActivity.this, MainActivity.class);
								startActivity(resultModifyIntent);
								finish();
							} else {
								Toast.makeText(ReviewModifyActivity.this, "리뷰수정실패..", Toast.LENGTH_LONG).show();
							}

						}

						@Override
						public void onFailure(Call<String> call, Throwable throwable) {
							Log.i("onFailure", "" + throwable);
							Toast.makeText(ReviewModifyActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
						}
					}); // enqueue 끝

				}

			} // onClick 끝
		}); // 수정완료 버튼 클릭 이벤트 끝

		// 수정취소 버튼 클릭 이벤트
		ModifyCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mainIntent = new Intent(ReviewModifyActivity.this, MainActivity.class);
				startActivity(mainIntent);
				finish();
			}
		});

	} // onResume 끝

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.i(TAG, "onActivityResult  실행");

		if (resultCode == RESULT_OK) {

			Log.i(TAG, "onActivityResult / resultCode == RESULT_OK");

			switch (requestCode) {
				// MainActivity 에서 요청할 때 보낸 요청 코드 (9001)
				case REQUEST_MODIFY:
					String startDate = data.getStringExtra("selectDateStart");
					String endDate = data.getStringExtra("selectDateEnd");
					ModifyTime.setText(startDate + " ~ " + endDate);
					break;
			}
		}

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
			ModifyImageCount.setText("대표 이미지");

			Log.i(TAG, "대표이미지 경로 : " + images.get(0).path);

			// 갤러리에서 처음 선택한 이미지를 대표이미지로 보여줌
			Glide.with(ReviewModifyActivity.this)
					.load(images.get(0).path)
					.placeholder(R.drawable.done_black).centerCrop().into(ModifyImage);
			// 안드로이드 갤러리 경로 출력
			Log.i(TAG, stringBuffer.toString());

			// 서버에 이미지 업로드 기능
			//uploadToServer(images.get(0).path);
			// 서버에 멀티 이미지 업로드 기능
			multiUploadToServer();

		} // 갤러리 접근 후 성공적으로 돌아왔을 때 조건 끝


	}

	// 쉐어드에 저장된 사용자 이름 가져오기 기능 (세션 활용)
	public void getShard() {
		SharedPreferences sharedPreferences = getSharedPreferences("sessionName", MODE_PRIVATE);
		sessionName = sharedPreferences.getString("name", "noName");
	} // getShard 끝

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
		Call<String> call = apiService.uploadImage(part, description);
		call.enqueue(new Callback<String>() {
			@Override
			public void onResponse(Call<String> call, Response<String> response) {
				Log.i(TAG, "onResponse : 실행");
				Log.i(TAG, "서버에서 받은 응답 값 : " + response.body().toString());
				ServerImagePath += response.body().toString();
			}

			@Override
			public void onFailure(Call<String> call, Throwable throwable) {
				Log.i("onFailure", "" + throwable);
				Toast.makeText(ReviewModifyActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
			}
		});
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
				Log.i(TAG, "서버에서 응답받은 값 " + response.body().toString());
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

} // ReviewModifyActivity 클래스 끝
