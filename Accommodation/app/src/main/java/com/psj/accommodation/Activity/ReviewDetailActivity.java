package com.psj.accommodation.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// TODO 리뷰 상세정보 화면
public class ReviewDetailActivity extends AppCompatActivity {

	public static final String TAG = "ReviewDetailActivity";
	ImageView Home, DetailChange, DetailShare, DetailImage;
	TextView DetailName, DetailTime, DetailScore;

	// 쉐어드에 저장된 사용자 이름 (세션)
	String sessionName = "";
	// 리뷰 고유 번호 저장 변수
	String PlaceNum = "";
	// 리뷰 이미지 경로 저장 변수
	String PlaceImage = "";
	// 리뷰 평점 저장 변수
	float PlaceScore;
	// 다이얼로그 객체
	AlertDialog alertDialog;
	// 서버 경로
	String ServerImagePath = "http://54.180.152.167/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review_detail);

		Home = findViewById(R.id.Home);
		DetailName = findViewById(R.id.DetailName);
		DetailTime = findViewById(R.id.DetailTime);
		DetailScore = findViewById(R.id.DetailScore);
		DetailChange = findViewById(R.id.DetailChange);
		DetailImage = findViewById(R.id.DetailImage);

		// 쉐어드에 저장된 사용자 이름 가져오기 기능
		getShard();

		// 데이터 수신
		Bundle getDetail = getIntent().getExtras();

		// 인텐트 데이터 수신 조건 시작
		if (getDetail == null) {
			Log.i(TAG, "데이터 수신 할거 없음");
		} else {

			Log.i(TAG, "데이터 수신 할거 있음");

			// String, float 형태 값 가져오기
			PlaceNum = getDetail.getString("PlaceNum");
			PlaceImage = getDetail.getString("PlaceImage");
			String PlaceTime = getDetail.getString("PlaceTime");
			String PlaceName = getDetail.getString("PlaceName");
			PlaceScore = getDetail.getFloat("PlaceScore");
			String Writer = getDetail.getString("Writer");

			Log.i(TAG, "리뷰 번호 : " + PlaceNum);
			Log.i(TAG, "숙박 장소 이미지 경로 : " + PlaceImage);
			Log.i(TAG, "숙박 장소 : " + PlaceName);
			Log.i(TAG, "숙박 기간 : " + PlaceTime);
			Log.i(TAG, "평점 : " + PlaceScore);
			Log.i(TAG, "작성자 : " + Writer);

			String score = String.valueOf(PlaceScore);
			String[] imagePath = PlaceImage.split(" ");

			Glide.with(this).load(Uri.parse(ServerImagePath + imagePath[0])).centerCrop().into(DetailImage);
			DetailName.setText(PlaceName);
			DetailTime.setText(PlaceTime);
			DetailScore.setText(score);

			Log.i(TAG, "쉐어드에 저장된 사용자 이름 : " + sessionName);

			if (!sessionName.equals(Writer)) {
				DetailChange.setVisibility(View.INVISIBLE);
				Log.i(TAG, "로그인한 사용자와 작성자가 다릅니다.");
			} else {
				Log.i(TAG, "로그인한 사용자와 작성자가 같습니다.");
			}


		} // 인텐트 데이터 수신 조건 끝

	} // onCreate 끝

	@Override
	protected void onResume() {
		super.onResume();

		Home.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mainIntent = new Intent(ReviewDetailActivity.this, MainActivity.class);
				startActivity(mainIntent);
				finish();
			}
		}); // Home 이미지 클릭 이벤트 끝

		DetailChange.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeData();
			}
		}); // 편집 이미지 클릭 이벤트 끝

	} // onResume 끝

	// 편집 이미지 클릭 후 수정 or 삭제 알림 기능
	public void changeData() {

		AlertDialog.Builder builder = new AlertDialog.Builder(ReviewDetailActivity.this);

		builder.setTitle("").setMessage("어떤 작업을 원하세요?");
		builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(ReviewDetailActivity.this, "삭제 작업을 진행할게요", Toast.LENGTH_SHORT).show();

				// 레트로핏 서버 URL 설정해놓은 객체 생성
				RetroClient retroClient = new RetroClient();
				// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
				ApiService apiService = retroClient.getApiClient().create(ApiService.class);

				// 인터페이스 ApiService에 선언한 reviewDelete()를 호출합니다
				Call<String> call = apiService.reviewDelete(PlaceNum);

				call.enqueue(new Callback<String>() {
					@Override
					public void onResponse(Call<String> call, Response<String> response) {
						Log.i(TAG, "onResponse 실행");

						Toast.makeText(ReviewDetailActivity.this, "서버에서 받은 응답 값 : " + response.body(), Toast.LENGTH_LONG).show();
						Log.i(TAG, "onResponse / " + response.body().toString());

						if (response.body().equals("리뷰삭제성공")) {
							Log.i(TAG, "리뷰삭제 성공하였습니다");
							Intent mainIntent = new Intent(ReviewDetailActivity.this, MainActivity.class);
							startActivity(mainIntent);
							finish();
						}
					}

					@Override
					public void onFailure(Call<String> call, Throwable throwable) {
						Log.i("onFailure", "" + throwable);
						Toast.makeText(ReviewDetailActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
					}
				}); // enqueue 끝

			} // onClick 끝
			// setPositiveButton 끝
		}).setNegativeButton("수정", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(ReviewDetailActivity.this, "수정작업을 진행할게요", Toast.LENGTH_SHORT).show();
				Intent modifyIntent = new Intent(ReviewDetailActivity.this, ReviewModifyActivity.class);
				modifyIntent.putExtra("PlaceNum", PlaceNum);
				modifyIntent.putExtra("PlaceImage", PlaceImage);
				modifyIntent.putExtra("PlaceName", DetailName.getText().toString());
				modifyIntent.putExtra("PlaceTime", DetailTime.getText().toString());
				modifyIntent.putExtra("PlaceScore", PlaceScore);
				startActivity(modifyIntent);

			}
		}); // setNegativeButton 끝

		alertDialog = builder.create();

		alertDialog.show();
	} // changeData 끝


	// 쉐어드에 저장된 사용자 이름 가져오기 기능 (세션 활용)
	public void getShard() {
		SharedPreferences sharedPreferences = getSharedPreferences("sessionName", MODE_PRIVATE);
		sessionName = sharedPreferences.getString("name", "noName");
	} // getShard 끝


} // ReviewDetailActivity 클래스 끝
