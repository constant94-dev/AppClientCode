package com.psj.accommodation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.psj.accommodation.Adapter.SearchAdapter;
import com.psj.accommodation.Data.SearchItem;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// TODO 검색 화면
public class SearchActivity extends AppCompatActivity {

	public static final String TAG = "SearchActivity";

	ImageView SearchBack, SearchImg;
	EditText SearchContent;
	TextView SearchNo;

	private RecyclerView searchRecyclerView;
	private RecyclerView.Adapter searchAdapter;
	private RecyclerView.LayoutManager searchLayoutManager;
	private String searchJsonString = "";

	// 검색 아이템 리스트
	private static ArrayList<SearchItem> searchItemList;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		Log.i(TAG, "onCreate : 실행");

		SearchBack = findViewById(R.id.SearchBack);
		SearchImg = findViewById(R.id.SearchImg);
		SearchContent = findViewById(R.id.SearchContent);
		SearchNo = findViewById(R.id.SearchNo);


	}

	@Override
	protected void onStart() {
		super.onStart();

		Log.i(TAG, "onStart : 실행");
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.i(TAG, "onResume : 실행");

		// 뒤로가기 이미지 클릭 이벤트
		SearchBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mainIntent = new Intent(SearchActivity.this, MainActivity.class);
				startActivity(mainIntent);
				finish();
			}
		});

		// 검색 이미지 클릭 이벤트
		SearchImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "검색 이미지 클릭!");
				Log.i(TAG, "검색 키워드 : " + SearchContent.getText().toString());

				String searchKeyWord = SearchContent.getText().toString();

				// 레트로핏 서버 URL 설정해놓은 객체 생성
				RetroClient retroClient = new RetroClient();
				// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
				ApiService apiService = retroClient.getApiClient().create(ApiService.class);

				// 인터페이스 ApiService에 선언한 reviewSearch()를 호출합니다
				Call<JsonObject> call = apiService.reviewSearch(searchKeyWord);

				// onResponse() 메서드를 이용해 응답을 전달받아서 요청에 대한 결과를 받을 수 있습니다
				call.enqueue(new Callback<JsonObject>() {
					@Override
					public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

						//Toast.makeText(SearchActivity.this, "서버에서 받은 응답 값 : " + response.body(), Toast.LENGTH_LONG).show();
						Log.i(TAG, "서버에서 받은 응답 값 : " + response.body().toString());

						// ArrayList 객체 생성
						searchItemList = new ArrayList<>();

						searchRecyclerView = findViewById(R.id.search_recycler_view);
						searchRecyclerView.setHasFixedSize(true); // 옵션

						// Linear layout manager 사용
						searchLayoutManager = new LinearLayoutManager(getApplicationContext());
						searchRecyclerView.setLayoutManager(searchLayoutManager);

						// Adapter 셋팅
						searchAdapter = new SearchAdapter(getApplicationContext(), searchItemList);
						searchRecyclerView.setAdapter(searchAdapter);

						searchJsonString = response.body().toString();

						if (searchJsonString.contains("null")) {
							SearchNo.setText("검색 결과가 없습니다.");
						} else {
							showResult();
						}


					}

					// 응답 실패 (네트워크 오류가 발생했을 때 등)
					@Override
					public void onFailure(Call<JsonObject> call, Throwable throwable) {
						Log.i("onFailure", "" + throwable);
						Toast.makeText(SearchActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
					}
				}); // enqueue() 끝
			}
		}); // 검색 이미지 클릭 이벤트 끝
	}

	@Override
	protected void onPause() {
		super.onPause();

		Log.i(TAG, "onPause : 실행");
	}

	@Override
	protected void onStop() {
		super.onStop();

		Log.i(TAG, "onStop : 실행");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		Log.i(TAG, "onDestroy : 실행");
	}

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
			JSONObject jsonObject = new JSONObject(searchJsonString);
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
				searchItemList.add(new SearchItem(image, name, writer, num, score, review, time));
				// 어댑터에게 새로 삽입된 아이템이 있다는걸 알려준다
				searchAdapter.notifyItemInserted(0);
			}


		} catch (JSONException e) {

			Log.d(TAG, "showResult : ", e);
		}

	} // showResult() 끝

} // SearchActivity 클래스 끝
