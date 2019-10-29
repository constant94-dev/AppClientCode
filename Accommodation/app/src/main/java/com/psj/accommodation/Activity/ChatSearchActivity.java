package com.psj.accommodation.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
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
import com.psj.accommodation.Data.ChattingItem;
import com.psj.accommodation.Data.SearchItem;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;
import com.psj.accommodation.Service.ChatService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatSearchActivity extends AppCompatActivity {

	public static final String TAG = "ChatSearchActivity";

	EditText UserSearch;
	Button SearchBtn, InviteBtn, InviteResultBtn;
	ImageView InviteImage, UserSearchCancel;
	TextView InviteName;

	private RecyclerView chatSearchRecyclerView;
	private RecyclerView.Adapter chatSearchAdapter;
	private RecyclerView.LayoutManager chatSearchLayoutManager;

	String profileImage = "";
	String profileName = "";
	String profileEmail = "";
	String profileJsonString = "";
	String sessionName = "";
	String sessionEmail = "";

	String names = "";
	String images = "";

	String[] searchUser;

	ArrayList<ChatSearchItem> chatSearchItemList;

	Messenger serviceMessenger;
	boolean isChatService = false; // 서비스 중인지 확인용 변수

	// ChatSearchActivityHandler 클래스 시작
	class ChatSearchActivityHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			Log.i(TAG, "서비스에서 응답 왔다 msg.what : " + msg.what);

			switch (msg.what) {
				case ChatService.SET_SOCKET:
					serviceMessenger = msg.replyTo;
					Toast.makeText(ChatSearchActivity.this, "서비스에서 소켓 접속 응답 받음", Toast.LENGTH_SHORT).show();
					break;

			}

		}
	} // ChatSearchActivityHandler 클래스 끝

	// 핸들러 wrapping 한 메시지 객체
	Messenger ChatSearchActivityMessenger = new Messenger(new ChatSearchActivityHandler());

	ServiceConnection serviceConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// 서비스와 연결되었을 때 호출되는 메서드
			Log.i(TAG, "onServiceConnected : 실행");
			serviceMessenger = new Messenger(service);

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// 서비스와 연결이 끊어졌을 때 호출되는 메서드
			Log.i(TAG, "onServiceDisconnected : 실행");

			isChatService = false;
		}
	};

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
		InviteResultBtn = findViewById(R.id.InviteResultBtn);

		chatSearchRecyclerView = findViewById(R.id.chatSearch_recycler_view);

		// 쉐어드에 저장된 정보 가져오기 (이름, 이메일)
		getShard();

		// 본인 프로필 가져오기 기능
		getSelfProfile();

		chatSearchItemList = new ArrayList<>();

		chatSearchLayoutManager = new LinearLayoutManager(this);
		((LinearLayoutManager) chatSearchLayoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
		chatSearchRecyclerView.setLayoutManager(chatSearchLayoutManager);

		// 바인드 서비스 시작 기능
		bindServiceStart();

	} // onCreate 끝

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart : 실행");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume : 실행");

		// 취소 이미지 클릭 이벤트
		UserSearchCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "취소 이미지 클릭");

				Intent chatIntent = new Intent(ChatSearchActivity.this, ChatRoomActivity.class);
				startActivity(chatIntent);
				finish();
			}
		});

		// 검색 버튼 클릭 이벤트 시작
		SearchBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "친구검색 버튼 클릭");

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

						// searchUser[0] -> 이름 / searchUser[1] -> 이미지 경로 / searchUser[2] -> 이메일
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

							Log.i(TAG, "초대 가능한 유저 입니다");

							InviteImage.setBackground(new ShapeDrawable(new OvalShape()));
							if (Build.VERSION.SDK_INT >= 21) {
								InviteImage.setClipToOutline(true);
							}

							InviteName.setVisibility(View.VISIBLE);
							InviteImage.setVisibility(View.VISIBLE);
							InviteBtn.setVisibility(View.VISIBLE);

							Glide.with(ChatSearchActivity.this).load("http://54.180.152.167/" + searchUser[1]).centerCrop().into(InviteImage);
							InviteName.setText(searchUser[0]);


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

		// 친구 초대 버튼 클릭 이벤트 시작
		InviteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "친구초대 버튼 클릭");

				getInvite();


			}
		}); // 친구 초대 버튼 클릭 이벤트 끝

		// 친구 초대 완료 버튼 클릭 이벤트 시작
		InviteResultBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "초대완료 버튼 클릭");

				// 채팅방 생성할 때 초대한 사용자 정보(이름,이메일,이미지) 확인
				for (int i = 0; i < chatSearchItemList.size(); i++) {
					Log.i(TAG, i + " 번째 전달할 이메일 : " + chatSearchItemList.get(i).getChatSearchEmail());
					Log.i(TAG, i + " 번째 전달할 이름 : " + chatSearchItemList.get(i).getChatSearchName());
					Log.i(TAG, i + " 번째 전달할 이미지 : " + chatSearchItemList.get(i).getChatSearchImage());

					names += chatSearchItemList.get(i).getChatSearchName() + " ";
					images += chatSearchItemList.get(i).getChatSearchImage() + " ";
				}

				images += profileImage;


				insertChatRoom();


			}
		}); // 친구 초대 완료 버튼 클릭 이벤트 끝

	} // onResume 끝

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

		if (isChatService) {

			Message msg = Message.obtain(null, ChatService.DISCONNECT);

			try {
				serviceMessenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}

			unbindService(serviceConn);
			isChatService = false;
		}

	}

	// 초대할 사용자 리사이클러뷰에 출력 기능
	public void getInvite() {

		Log.i(TAG, "getInvite : 실행");

		Log.i(TAG, "초대할 사용자 리사이클러뷰 출력 시작 ~~");


		ChatSearchItem chatSearchItem = new ChatSearchItem();

		// 전역변수 searchUser[0] -> 이름 / searchUser[1] -> 이미지 경로 / searchUser[2] -> 이메일
		chatSearchItem.setChatSearchEmail(searchUser[2]);
		chatSearchItem.setChatSearchImage(searchUser[1]);
		chatSearchItem.setChatSearchName(searchUser[0]);

		// 중복 초대 체크 변수
		int overlap = 0;

		// 초대한 사용자 중복 없애기 위한 반복문 시작
		for (int i = 0; i < chatSearchItemList.size(); i++) {
			// 초대한 사용자 중복 없애기 위한 조건 시작
			if (chatSearchItemList.get(i).getChatSearchEmail().equals(chatSearchItem.getChatSearchEmail())) {
				Log.i(TAG, "초대한 사용자가 이미 목록에 존재해요 ~~");
				Toast.makeText(this, "초대 목록에 있는 사용자 입니다", Toast.LENGTH_SHORT).show();
				overlap = 1;
			}
			//Log.i(TAG, "초대한 사용자가 목록에 없어요 ~~");
		} // 초대한 사용자 중복 없애기 위한 반복문 끝


		if (overlap == 0) {
			chatSearchItemList.add(chatSearchItem);
			Log.i(TAG, "어댑터에 전달할 유저 정보 사이즈 : " + chatSearchItemList.size());

			Log.i(TAG, "초대할 사용자 리사이클러뷰 출력 시작 ~~");
			chatSearchAdapter = new ChatSearchAdapter(this, chatSearchItemList);
			chatSearchRecyclerView.setAdapter(chatSearchAdapter);

			// 채팅 초대 리스트를 추가 하고 어댑터에 전달한 후에 어댑터 전달된 데이터가 변경된것을 알림
			chatSearchAdapter.notifyDataSetChanged();
		}

		Log.i(TAG, "초대할 사용자 리사이클러뷰 출력 끝 ~~");

		// 한명 이상 초대한 사용자가 있을 때 버튼 활성화
		InviteResultBtn.setVisibility(View.VISIBLE);
	}

	private void insertChatRoom() {

		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);

		// 인터페이스 ApiService에 선언한 chatRoomInsert()를 호출합니다
		Call<String> call = apiService.chatRoomInsert(names, images, sessionName);

		call.enqueue(new Callback<String>() {
			@Override
			public void onResponse(Call<String> call, Response<String> response) {
				Log.i("onResponse : 실행", response.body().toString());
				Toast.makeText(ChatSearchActivity.this, "서버에서 받은 응답 값 : ", Toast.LENGTH_LONG);

				Intent chatIntent = new Intent(ChatSearchActivity.this, ChatRoomActivity.class);
				chatIntent.putExtra("createRoomNames", names);
				chatIntent.putExtra("createRoomImages", images);
				startActivity(chatIntent);
				finish();

			} // onResponse 끝

			@Override
			public void onFailure(Call<String> call, Throwable throwable) {
				Log.i("onFailure : 실행", " / " + throwable);
				Toast.makeText(ChatSearchActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
			} // onFailure 끝

		}); // enqueue 끝
	} // insertChatRoom() 끝

	public void getSelfProfile() {

		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);

		// 인터페이스 ApiService에 선언한 chatRoomList()를 호출합니다
		Call<JsonObject> call = apiService.chattingGetProfile(sessionEmail);

		call.enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				Log.i("onResponse : 실행", response.body().toString());
				Toast.makeText(ChatSearchActivity.this, "서버에서 받은 응답 값 : ", Toast.LENGTH_LONG);

				profileJsonString = response.body().toString();

				selfProfileResult();
			}

			@Override
			public void onFailure(Call<JsonObject> call, Throwable throwable) {
				Log.i("onFailure : 실행", " / " + throwable);
				Toast.makeText(ChatSearchActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
			}
		});
	} // getSelfProfile() 끝

	// 서버에서 json 형태로 가져온 값을 리사이클러뷰에 추가 시키는 방법
	private void selfProfileResult() {

		Log.i(TAG, "showResult : 실행");

		String TAG_JSON = "result";
		String TAG_EMAIL = "email";
		String TAG_NAME = "name";
		String TAG_IMAGE = "image";


		try {
			// 서버에서 가져온 json 데이터 처음 시작이 '{' 중괄호로 시작해서 JSONObject 에 담아준다
			JSONObject jsonObject = new JSONObject(profileJsonString);
			// JSONArray [ 대괄호로 시작하니 jsonObject 에서 get 한 값을 JSONArray 에 담아준다
			JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

			// 반복문을 이용하여 알맞게 풀어준다
			for (int i = 0; i < jsonArray.length(); i++) {

				JSONObject item = jsonArray.getJSONObject(i);

				profileEmail = item.getString(TAG_EMAIL);
				profileName = item.getString(TAG_NAME);
				profileImage = item.getString(TAG_IMAGE);

			}


		} catch (JSONException e) {

			Log.d(TAG, "showResult : ", e);
		}

	} // selfProfileResult() 끝

	public void bindServiceStart() {
		Intent bindIntent = new Intent(ChatSearchActivity.this, ChatService.class); // 다음넘어갈 컴포넌트 정의
		isChatService = bindService(bindIntent, serviceConn, Context.BIND_AUTO_CREATE); // 인텐트,서비스연결객체,플래그 전달 --> 서비스 연결
	}

	// 쉐어드에 저장된 사용자 이름 가져오기 기능 (세션 활용)
	public void getShard() {
		SharedPreferences sharedPreferences = getSharedPreferences("sessionName", MODE_PRIVATE);
		sessionName = sharedPreferences.getString("name", "noName");
		sessionEmail = sharedPreferences.getString("email", "noEmail");
	} // getShard 끝

} // ChatSearchActivity 클래스 끝
