package com.psj.accommodation.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.psj.accommodation.Adapter.ChatSearchAdapter;
import com.psj.accommodation.Adapter.SearchAdapter;
import com.psj.accommodation.Data.ChatSearchItem;
import com.psj.accommodation.Data.SearchItem;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatSearchActivity extends AppCompatActivity {

	public static final String TAG = "ChatSearchActivity";

	EditText UserSearch;
	Button SearchBtn, InviteBtn;
	ImageView InviteImage, UserSearchCancel;
	TextView InviteName;

	private RecyclerView searchRecyclerView;
	private RecyclerView.Adapter searchAdapter;
	private RecyclerView.LayoutManager searchLayoutManager;

	// 검색 아이템 리스트
	private static ArrayList<ChatSearchItem> searchItemList = null;

	String sessionName = "";
	String sessionEmail = "";

	String[] searchUser;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatsearch);
		Log.i(TAG, "onCreate : 실행");

		UserSearch = findViewById(R.id.UserSearch);
		SearchBtn = findViewById(R.id.SearchBtn);
		UserSearchCancel = findViewById(R.id.UserSearchCancel);
		InviteBtn = findViewById(R.id.InviteBtn);
		InviteImage = findViewById(R.id.InviteImage);
		InviteName = findViewById(R.id.InviteName);
		searchRecyclerView = findViewById(R.id.chatSearch_recycler_view);

		getShard();

		// ArrayList 객체 생성
		searchItemList = new ArrayList<>();

		searchLayoutManager = new LinearLayoutManager(getApplicationContext());
		((LinearLayoutManager) searchLayoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
		searchRecyclerView.setLayoutManager(searchLayoutManager);

		// Adapter 셋팅
		searchRecyclerView.setHasFixedSize(true); // 옵션
		searchAdapter = new ChatSearchAdapter(getApplicationContext(), searchItemList);
		searchRecyclerView.setAdapter(searchAdapter);


	} // onCreate 끝

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume : 실행");

		// 취소 이미지 클릭 이벤트
		UserSearchCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent chatIntent = new Intent(ChatSearchActivity.this, ChatActivity.class);
				startActivity(chatIntent);
				finish();
			}
		});

		// 검색 버튼 클릭 이벤트
		SearchBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "친구검색....");

				Log.i(TAG, "검색할 이메일 : " + UserSearch.getText().toString());

				// 레트로핏 서버 URL 설정해놓은 객체 생성
				RetroClient retroClient = new RetroClient();
				// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
				ApiService apiService = retroClient.getApiClient().create(ApiService.class);

				// 인터페이스 ApiService에 선언한 chatSearch()를 호출합니다
				Call<String> call = apiService.chatSearch(UserSearch.getText().toString());

				call.enqueue(new Callback<String>() {
					@Override
					public void onResponse(Call<String> call, Response<String> response) {
						Log.i(TAG, "onResponse : 실행");
						Log.i(TAG, "서버에서 받은 응답 값 : " + response.body().toString());

						searchUser = response.body().split(" ");

						if (response.body().equals("유저없음")) {
							InviteName.setVisibility(View.VISIBLE);
							InviteBtn.setVisibility(View.GONE);
							InviteImage.setVisibility(View.GONE);
							InviteName.setText("검색한 유저가 존재하지 않습니다");
						} else if (searchUser[2].equals(sessionEmail)) {
							Log.i(TAG, "본인은 초대할 수 없습니다");
							InviteName.setVisibility(View.VISIBLE);
							InviteBtn.setVisibility(View.GONE);
							InviteImage.setVisibility(View.GONE);
							InviteName.setText("본인은 초대할 수 없습니다");
						} else {

							InviteImage.setBackground(new ShapeDrawable(new OvalShape()));
							if (Build.VERSION.SDK_INT >= 21) {
								InviteImage.setClipToOutline(true);
							}

							Glide.with(ChatSearchActivity.this).load("http://54.180.152.167/" + searchUser[1]).centerCrop().into(InviteImage);
							InviteName.setText(searchUser[0]);

							InviteName.setVisibility(View.VISIBLE);
							InviteImage.setVisibility(View.VISIBLE);
							InviteBtn.setVisibility(View.VISIBLE);

						}

					} // onResponse 끝

					@Override
					public void onFailure(Call<String> call, Throwable throwable) {
						Log.i("onFailure", "" + throwable);
						Toast.makeText(ChatSearchActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
					}
				});


			}
		}); // 검색 버튼 클릭 이벤트 끝

		// 친구 초대 버튼 클릭 이벤트
		InviteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				int invite = 0;

				if (searchItemList != null){
					// 초대 중복 방지를 위한 반복문 시작
					for (int i = 0; i < searchItemList.size(); i++) {
						if (searchItemList.get(i).getChatSearchEmail().equals(searchUser[2])) {
							Log.i(TAG, "초대 목록에 있습니다");
							Toast.makeText(ChatSearchActivity.this, "초대 목록에 있습니다", Toast.LENGTH_SHORT).show();
							invite = 1;

						}
					} // 초대 중복 방지를 위한 반복문 끝
				} // 초대 목록 중복일때 중복 끝



				Log.i(TAG, "초대 목록에 없습니다 : " + invite);

				if (invite == 0) {



					Log.i(TAG, "초대 목록에 없습니다");
					// searchUser[0] -> 이름 / searchUser[1] -> 이미지 경로 / searchUser[2] -> 이메일
					searchItemList.add(0, new ChatSearchItem(searchUser[1], searchUser[2], searchUser[0]));

					searchAdapter.notifyItemInserted(0);


				} // 초대 목록 중복 아닐때 조건 끝


			}
		}); // 친구 초대 버튼 클릭 이벤트 끝

	} // onResume 끝

	// 쉐어드에 저장된 사용자 이름 가져오기 기능 (세션 활용)
	public void getShard() {
		SharedPreferences sharedPreferences = getSharedPreferences("sessionName", MODE_PRIVATE);
		sessionName = sharedPreferences.getString("name", "noName");
		sessionEmail = sharedPreferences.getString("email", "noEmail");
	} // getShard 끝

} // ChatActivity 클래스 끝
