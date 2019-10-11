package com.psj.accommodation.Activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.JsonObject;
import com.psj.accommodation.Adapter.DetailSlideAdapter;
import com.psj.accommodation.Adapter.MainAdapter;
import com.psj.accommodation.Data.MainItem;
import com.psj.accommodation.Data.Review;
import com.psj.accommodation.Data.ReviewSelect;
import com.psj.accommodation.Fragment.MainDateFragment;
import com.psj.accommodation.Fragment.MainScoreFragment;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// TODO 로그인 후 보여지는 메인 화면
public class MainActivity extends AppCompatActivity {

	public static final String TAG = "MainActivity";
	String[] PERMISSIONS = {"android.permission.CAMERA",
			"android.permission.WRITE_EXTERNAL_STORAGE"};
	static final int PERMISSIONS_REQUEST_CODE = 1000; // 퍼미션 결과 값 받을 때 구분해주는 변수

	private RecyclerView mainRecyclerView;
	private RecyclerView.Adapter mainAdapter;
	private RecyclerView.LayoutManager mainLayoutManager;
	private String mainJsonString;

	ImageView Home, Search, Chat, MyProfile;
	TextView reviewRegister, main_fragment_date, main_fragment_score;

	// 메인 아이템 리스트
	private static ArrayList<MainItem> mainItemList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		reviewRegister = findViewById(R.id.ReviewRegisterText);
		Home = findViewById(R.id.Home);
		Search = findViewById(R.id.Search);
		Chat = findViewById(R.id.Chat);
		MyProfile = findViewById(R.id.MyProfile);
		main_fragment_date = findViewById(R.id.main_fragment_date);
		main_fragment_score = findViewById(R.id.main_fragment_score);

		Log.i(TAG, "onCreate : 실행");


		// ArrayList 객체 생성
		mainItemList = new ArrayList<>();

		mainRecyclerView = findViewById(R.id.main_recycler_view);
		mainRecyclerView.setHasFixedSize(true); // 옵션

		// Linear layout manager 사용
		mainLayoutManager = new LinearLayoutManager(this);
		mainRecyclerView.setLayoutManager(mainLayoutManager);

		// Adapter 셋팅
		mainAdapter = new MainAdapter(getApplicationContext(), mainItemList);
		mainRecyclerView.setAdapter(mainAdapter);

		getReviewTimeData();

		// 안드로이드 SDK 버전 23 이상이라면 권한 요청해야됨
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

			Log.i(TAG, "SDK 버전 23 이상 : 실행");

			// 퍼미션 상태 확인
			if (hasPermissions(PERMISSIONS)) {

				Log.i(TAG, "퍼미션 상태 허가됨 : 실행");
				Toast.makeText(MainActivity.this, "카메라/외부저장소 읽기,쓰기 권한 허가됨", Toast.LENGTH_SHORT);
			} else {

				Log.i(TAG, "퍼미션 상태 허가 안됨 : 실행");

				// 퍼미션 허가 안되어있다면 사용자에게 요청
				requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
			}
		} else {
			Log.i(TAG, "SDK 버전 23 이하 : 실행");
			// 안드로이드 SDK 버전 23 이전이라면 권한요청할 필요없다
			Toast.makeText(MainActivity.this, "SDK 버전 23 이하입니다", Toast.LENGTH_SHORT);
			return;
		}


		// 데이터 수신
		Bundle getRegister = getIntent().getExtras();

		// 인텐트 데이터 수신 조건 시작
		if (getRegister == null) {
			Log.i(TAG, "데이터 수신 할거 없음");
		} else {

			Log.i(TAG, "데이터 수신 할거 있음");

			// String, float 형태 값 가져오기
			String PlaceNum = getRegister.getString("PlaceNum");
			String PlaceImage = getRegister.getString("PlaceImage");
			String PlaceTime = getRegister.getString("PlaceTime");
			String PlaceName = getRegister.getString("PlaceName");
			float PlaceScore = getRegister.getFloat("PlaceScore");
			String PlaceReview = getRegister.getString("PlaceReview");
			String Writer = getRegister.getString("Writer");

			// 데이터준비 실제로는 ArrayList<>등을 사용해야 할듯 하다.
			// DB에서 아이템을 가져와 배열에 담아 주면 된다.

			// 첫번째 줄에 삽입됨
			// 0을 빼고 notifyItemInserted 0 만 해주어도 리사이클러뷰 맨위에 삽입 된다
			mainItemList.add(0, new MainItem(PlaceNum, PlaceName, PlaceTime, PlaceScore, PlaceReview, PlaceImage, Writer));

			// 변경된 값 리사이클러뷰 적용하기
			mainAdapter.notifyItemInserted(0);


		} // 인텐트 데이터 수신 조건 끝


	} // onCreate() 끝

	@Override
	protected void onStart() {
		super.onStart();

		Log.i(TAG, "onStart : 실행");


	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.i(TAG, "onResume : 실행");

		main_fragment_date.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "main_fragment_date : 클릭");

				main_fragment_date.setTextColor(main_fragment_date.getResources().getColor(R.color.colorBlue));
				main_fragment_score.setTextColor(main_fragment_score.getResources().getColor(R.color.colorBlack));

				getReviewTimeData();

			}
		});

		main_fragment_score.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "main_fragment_score : 클릭");

				main_fragment_score.setTextColor(main_fragment_score.getResources().getColor(R.color.colorBlue));
				main_fragment_date.setTextColor(main_fragment_date.getResources().getColor(R.color.colorBlack));

				getReviewScoreData();
			}
		});


		Home.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mainIntent = new Intent(MainActivity.this, MainActivity.class);
				startActivity(mainIntent);
				finish();
			}
		});

		Search.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
				startActivity(searchIntent);
				finish();
			}
		});

		Chat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent chatIntent = new Intent(MainActivity.this, ChatRoomActivity.class);
				startActivity(chatIntent);
				finish();
			}
		});

		MyProfile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
				startActivity(profileIntent);
				finish();
			}
		});


		// 등록 눌렀을 때
		reviewRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent reviewRegistIntent = new Intent(MainActivity.this, ReviewRegistActivity.class);
				startActivity(reviewRegistIntent);
				finish();
			}
		});
	} // onResume() 끝

	// 권한 상태 확인 기능
	private boolean hasPermissions(String[] permissions) {

		Log.i(TAG, "hasPermissions : 실행");

		int permsResult;

		for (String perms : permissions) {
			permsResult = ContextCompat.checkSelfPermission(this, perms);
			Log.i(TAG, "hasPermissions 퍼미션 체크중... " + perms);
			if (permsResult == PackageManager.PERMISSION_DENIED) {
				// 허가 안된 퍼미션 발견
				Log.i(TAG, "hasPermissions 허가 안된 퍼미션 발견 : 실행");
				return false;
			}

		} // 반복문 끝

		Log.i(TAG, "hasPermissions 모든 퍼미션 허가  : 실행");
		// 모든 퍼미션 허가됨
		return true;
	} // 권한 상태 확인 기능 끝


	// permission 요청한 결과값 받는 기능
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		Log.i(TAG, "onRequestPermissionsResult : 실행");

		switch (requestCode) {

			case PERMISSIONS_REQUEST_CODE:
				// 퍼미션 요청에 대한 결과값이 0보다 클 때 조건 시작
				if (grantResults.length > 0) {
					boolean cameraPermissionAccepted = grantResults[0]
							== PackageManager.PERMISSION_GRANTED;

					boolean writePermissionAccepted = grantResults[1]
							== PackageManager.PERMISSION_GRANTED;

					if (!cameraPermissionAccepted || !writePermissionAccepted) {
						Log.i(TAG, "onRequestPermissionsResult 퍼미션 허가 해주세요 : 실행");
						showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
						return;
					} else {

						Log.i(TAG, "onRequestPermissionsResult 모든 퍼미션 허가됨 : 실행");
						Toast.makeText(MainActivity.this, "카메라/외부저장소 읽기,쓰기 권한 허가됨", Toast.LENGTH_SHORT);
					}
				} else {
					Log.i(TAG, "onRequestPermissionsResult 퍼미션 요청이 없습니다 : 실행");
					Toast.makeText(MainActivity.this, "퍼미션 요청에 대한 결과값이 없습니다", Toast.LENGTH_SHORT);
				} // 퍼미션 요청에 대한 결과값이 0보다 클 때 조건 끝
				break;
		} // switch() 끝

	} // onRequestPermissionsResult() 끝

	// permission 허가를 위한 다이얼로그 기능
	@TargetApi(Build.VERSION_CODES.M)
	private void showDialogForPermission(String msg) {

		Log.d(TAG, "showDialogForPermission : 실행");

		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("알림");
		builder.setMessage(msg);
		builder.setCancelable(false);
		builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
			}
		});
		builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		});
		builder.create().show();
	} // showDialogForPermission() 끝

	// 레트로핏 사용하여 데이터베이스 데이터 가져오기
	public void getReviewTimeData() {
		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);

		// 인터페이스 ApiService에 선언한 reviewSelect()를 호출합니다
		Call<JsonObject> call = apiService.reviewSelect();

		call.enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				Log.i(TAG, "onResponse : 실행");
				Log.i(TAG, "서버에서 응답 받은 값 : " + response.body().toString());

				// 리스트를 초기화 시킨다
				mainItemList.clear();
				// 어댑터에게 데이터 세팅이 변경되었다고 알려준다
				mainAdapter.notifyDataSetChanged();

				mainJsonString = response.body().toString();
				showResult();
			}

			@Override
			public void onFailure(Call<JsonObject> call, Throwable throwable) {
				Log.i("onFailure", "" + throwable);
				Toast.makeText(MainActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
			}
		});


	} // getReviewData() 끝

	// 레트로핏 사용하여 데이터베이스 데이터 가져오기
	public void getReviewScoreData() {
		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);

		// 인터페이스 ApiService에 선언한 reviewSelect()를 호출합니다
		Call<JsonObject> call = apiService.reviewSelectScore();

		call.enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				Log.i(TAG, "onResponse : 실행");
				Log.i(TAG, "서버에서 응답 받은 값 : " + response.body().toString());

				// 리스트를 초기화 시킨다
				mainItemList.clear();
				// 어댑터에게 데이터 세팅이 변경되었다고 알려준다
				mainAdapter.notifyDataSetChanged();

				mainJsonString = response.body().toString();
				showResult();
			}

			@Override
			public void onFailure(Call<JsonObject> call, Throwable throwable) {
				Log.i("onFailure", "" + throwable);
				Toast.makeText(MainActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
			}
		});


	} // getReviewData() 끝


	// 서버에서 json 형태로 가져온 값을 리사이클러뷰에 추가 시키는 방법
	private void showResult() {

		Log.i(TAG, "showResult : 실행");

		String TAG_JSON = "result";
		String TAG_NUM = "num";
		String TAG_NAME = "name";
		String TAG_TIME = "time";
		String TAG_SCORE = "score";
		String TAG_WRITER = "writer";
		String TAG_IMAGE = "image";
		String TAG_REVIEW = "review";


		try {
			// 서버에서 가져온 json 데이터 처음 시작이 '{' 중괄호로 시작해서 JSONObject 에 담아준다
			JSONObject jsonObject = new JSONObject(mainJsonString);
			// JSONArray [ 대괄호로 시작하니 jsonObject 에서 get 한 값을 JSONArray 에 담아준다
			JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

			// 반복문을 이용하여 알맞게 풀어준다
			for (int i = 0; i < jsonArray.length(); i++) {

				JSONObject item = jsonArray.getJSONObject(i);

				String num = item.getString(TAG_NUM);
				String name = item.getString(TAG_NAME);
				String time = item.getString(TAG_TIME);
				float score = (float) item.getDouble(TAG_SCORE);
				String writer = item.getString(TAG_WRITER);
				String image = item.getString(TAG_IMAGE);
				String review = item.getString(TAG_REVIEW);

				Log.i(TAG, "반복문 : 실행");
				Log.i(TAG, i + "번째 -> 리뷰번호 : " + num + "/ 리뷰제목 : " + name + "/ 리뷰작성시간 : " + time + "/ 리뷰평점 : " + score + "/ 리뷰작성자 : " + writer + "/ 리뷰이미지경로 : " + image);

				// 어댑터에 전달할 데이터 추가
				mainItemList.add(new MainItem(num, name, time, score, image, review, writer));
				// 어댑터에게 새로 삽입된 아이템이 있다는걸 알려준다
				mainAdapter.notifyItemInserted(0);
			}


		} catch (JSONException e) {

			Log.d(TAG, "showResult : ", e);
		}

	} // showResult() 끝

} // MainActivity 클래스 끝
