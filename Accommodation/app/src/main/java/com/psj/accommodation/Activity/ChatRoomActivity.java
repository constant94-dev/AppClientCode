package com.psj.accommodation.Activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.psj.accommodation.Adapter.ChatRoomAdapter;
import com.psj.accommodation.Data.ChatSearchItem;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;
import com.psj.accommodation.Service.ChatService;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomActivity extends AppCompatActivity {

	public static final String TAG = "ChatRoomActivity";
	private static final int RECEIVE_THREAD = 2;
	private static final int SEND_THREAD = 3;

	ImageView chatAdd, Home;

	private RecyclerView chatRecyclerView;
	private RecyclerView.Adapter chatAdapter;
	private RecyclerView.LayoutManager chatLayoutManager;

	ArrayList<String> sendUserInfo;
	private ArrayList<ChatSearchItem> chatRoomItemList;

	// 서버 접속 여부를 판별하기 위한 변수
	boolean isConnect = false;
	// 어플 종료시 스레드 중지를 위해...
	boolean isRunning = false;
	// 서버와 연결되어있는 소켓 객체
	Socket client_socket;

	String sessionName = "";
	String sessionEmail = "";
	String chatRoomJson = "";
	String profileJsonString = "";
	String profileEmail = "";
	String profileName = "";
	String profileImage = "";
	String sendRoomNum = "";

	Messenger serviceMessenger;
	boolean isChatService = false; // 서비스 중인지 확인용 변수

	// ChatRoomActivityHandler 클래스 시작
	class ChatRoomActivityHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			Log.i(TAG, "서비스에서 응답 왔다 msg.what : " + msg.what);

			switch (msg.what) {
				case ChatService.SET_SOCKET:
					serviceMessenger = msg.replyTo;
					Toast.makeText(ChatRoomActivity.this, "서비스에서 소켓 접속 응답 받음", Toast.LENGTH_SHORT).show();
					break;
				case RECEIVE_THREAD:
					Toast.makeText(ChatRoomActivity.this, "액티비티 자체에서 핸들러 전달 receive", Toast.LENGTH_SHORT).show();
					break;
				case SEND_THREAD:
					Toast.makeText(ChatRoomActivity.this, "액티비티 자체에서 핸들러 전달 send", Toast.LENGTH_SHORT).show();
					break;


			}

		}
	} // ChatRoomActivityHandler 클래스 끝

	// 핸들러 wrapping 한 메시지 객체
	Messenger chatRoomActivityMessenger = new Messenger(new ChatRoomActivityHandler());


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
			Log.i(TAG, "onServiceDisconnected");

			isChatService = false;
		}
	};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		Log.i(TAG, "onCreate : 실행");

		chatAdd = findViewById(R.id.chatAdd);
		Home = findViewById(R.id.Home);
		chatRecyclerView = findViewById(R.id.chat_recycler_view);

		// 쉐어드에 저장된 사용자 정보(이름,이메일) 가져오기
		getShard();

		// 사용자 본인의 프로필 정보 가져오기
		getSelfProfile();

		// 데이터베이스에 저장된 채팅방 정보 불러오기
		showChatRoom();

		chatRoomItemList = new ArrayList<>();

		// 리사이클러뷰 레이아웃 설정
		chatLayoutManager = new LinearLayoutManager(this);
		chatRecyclerView.setLayoutManager(chatLayoutManager);

		// ChatRoomAdapter 생성자 시작
		chatAdapter = new ChatRoomAdapter(this, chatRoomItemList, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getTag() != null) {

					final String position = (String) v.getTag();
					Log.i(TAG, "어댑터에서 가져온 태그 값 : " + position);
					String name = "";
					String image = "";

					for (int i = 0; i < chatRoomItemList.size(); i++) {
						if (chatRoomItemList.get(i).getChatRoomNum().equals(position)) {

							Log.i(TAG, "리사이클러뷰 방 번호로 검색한 이미지 경로 : " + chatRoomItemList.get(i).getChatSearchImage());
							Log.i(TAG, "리사이클러뷰 방 번호로 검색한 이름 : " + chatRoomItemList.get(i).getChatSearchName());

							image = chatRoomItemList.get(i).getChatSearchImage();
							name = chatRoomItemList.get(i).getChatSearchName();

						}
					}

					Intent chatRoomIntent = new Intent(ChatRoomActivity.this, ChattingActivity.class);
					// 채팅방 고유 번호
					chatRoomIntent.putExtra("chatRoomNum", position);
					// 채팅방 유저 이름
					chatRoomIntent.putExtra("chatRoomName", name);
					// 채팅방 유저 프로필 이미지
					chatRoomIntent.putExtra("chatRoomImage", image);
					startActivity(chatRoomIntent);

				}
			}
		}); // ChatRoomAdapter 생성자 끝

		chatRecyclerView.setAdapter(chatAdapter);


	} // onCreate 끝

	@Override
	protected void onResume() {
		super.onResume();

		Log.i(TAG, "onResume : 실행");

		// 홈 이미지 클릭 이벤트
		Home.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mainIntent = new Intent(ChatRoomActivity.this, MainActivity.class);
				startActivity(mainIntent);
				finish();
			}
		});

		// 채팅방 추가 이미지 클릭 이벤트
		chatAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent chatSearchIntent = new Intent(ChatRoomActivity.this, ChatSearchActivity.class);
				startActivity(chatSearchIntent);
				finish();
			}
		});

	} // onResume 끝


	// 데이터베이스에 저장된 채팅방 목록 가져오기 기능
	private void showChatRoom() {

		Log.i(TAG, "showChatRoom : 실행");

		Log.i(TAG, "데이터베이스에 저장된 목록 가져올 이름 sessionName : " + sessionName);

		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);

		// 인터페이스 ApiService에 선언한 chatRoomList()를 호출합니다
		Call<JsonObject> call = apiService.chatRoomList(sessionName);

		call.enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				Log.i("showChatRoom onResponse : 실행", response.body().toString());
				Toast.makeText(ChatRoomActivity.this, "서버에서 받은 응답 값 : ", Toast.LENGTH_LONG);

				// 리스트를 초기화 시킨다
				chatRoomItemList.clear();

				// 어댑터에게 데이터 세팅이 변경되었다고 알려준다
				chatAdapter.notifyDataSetChanged();

				chatRoomJson = response.body().toString();

				showChatResult();

			}

			@Override
			public void onFailure(Call<JsonObject> call, Throwable throwable) {
				Log.i("onFailure : 실행", " / " + throwable);
				Toast.makeText(ChatRoomActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
			} // onFailure 끝

		});


	} // showChatRoom 끝

	// 서버에서 json 형태로 가져온 값을 리사이클러뷰에 추가 시키는 방법
	private void showChatResult() {

		Log.i(TAG, "showChatResult : 실행");

		String TAG_JSON = "result";
		String TAG_NUM = "num";
		String TAG_NAMES = "names";
		String TAG_IMAGES = "images";
		String TAG_CREATOR = "creator";


		try {
			// 서버에서 가져온 json 데이터 처음 시작이 '{' 중괄호로 시작해서 JSONObject 에 담아준다
			JSONObject jsonObject = new JSONObject(chatRoomJson);
			// JSONArray [ 대괄호로 시작하니 jsonObject 에서 get 한 값을 JSONArray 에 담아준다
			JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

			// 반복문을 이용하여 알맞게 풀어준다
			for (int i = 0; i < jsonArray.length(); i++) {

				Log.i(TAG, "showChatResult 반복문 : 실행");

				JSONObject item = jsonArray.getJSONObject(i);

				String chatRoomNum = item.getString(TAG_NUM);
				String chatRoomNames = item.getString(TAG_NAMES);
				String chatRoomImages = item.getString(TAG_IMAGES);
				String chatRoomCreator = item.getString(TAG_CREATOR);

				Log.i(TAG, "채팅방 고유 번호 : " + chatRoomNum);
				Log.i(TAG, "채팅방 참여자들 : " + chatRoomNames);
				Log.i(TAG, "채팅방 프로필 이미지들 : " + chatRoomImages);
				Log.i(TAG, "채팅방 만든 사람 : " + chatRoomCreator);
				Log.i(TAG, "나의 프로필 이미지 경로 : " + profileImage);


				if (chatRoomNames.contains(sessionName)) {
					Log.i(TAG, "나 자신은 출력해줄 필요없다");
					chatRoomNames = chatRoomNames.replace(sessionName, chatRoomCreator);
					Log.i(TAG, "나의 이름을 방장에 이름으로 바꾸었다 -> " + chatRoomNames);

				}

				if (chatRoomImages.contains(profileImage)) {
					chatRoomImages = chatRoomImages.replace(profileImage, "");
					chatRoomImages = chatRoomImages.trim();
					Log.i(TAG, "나의 이미지를 지워버렸다 -> " + chatRoomImages);

				}

				Log.i(TAG, "어댑터에 데이터 추가 하기전 확인하는 이름들 -> " + chatRoomNames);
				Log.i(TAG, "어댑터에 데이터 추가 하기전 확인하는 이미지들 -> " + chatRoomImages);
				// 어댑터에 전달할 데이터 추가
				chatRoomItemList.add(new ChatSearchItem(chatRoomNum, chatRoomNames, chatRoomImages));

				// 어댑터에게 새로 삽입된 아이템이 있다는걸 알려준다
				chatAdapter.notifyItemInserted(0);


			} // 반복문을 이용하여 Json 풀어주기 끝

			// 바인드 서비스 시작
			bindServiceStart();


		} catch (JSONException e) {

			Log.d(TAG, "showChatResult : ", e);
		}

	} // showChatResult() 끝

	// 쉐어드에 저장된 사용자 이름 가져오기 기능 (세션 활용)
	public void getShard() {
		SharedPreferences sharedPreferences = getSharedPreferences("sessionName", MODE_PRIVATE);
		sessionName = sharedPreferences.getString("name", "noName");
		sessionEmail = sharedPreferences.getString("email", "noEmail");
	} // getShard 끝

	public void getSelfProfile() {

		Log.i(TAG, "getSelfProfile : 실행");

		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);

		// 인터페이스 ApiService에 선언한 chatRoomList()를 호출합니다
		Call<JsonObject> call = apiService.chattingGetProfile(sessionEmail);

		call.enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				Log.i("getSelfProfile onResponse : 실행", response.body().toString());
				Toast.makeText(ChatRoomActivity.this, "서버에서 받은 응답 값 : ", Toast.LENGTH_LONG);

				profileJsonString = response.body().toString();

				selfProfileResult();
			}

			@Override
			public void onFailure(Call<JsonObject> call, Throwable throwable) {
				Log.i("onFailure : 실행", " / " + throwable);
				Toast.makeText(ChatRoomActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
			}
		});
	} // getSelfProfile() 끝

	// 서버에서 json 형태로 가져온 값을 리사이클러뷰에 추가 시키는 방법
	private void selfProfileResult() {

		Log.i(TAG, "selfProfileResult : 실행");

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
		Intent bindIntent = new Intent(ChatRoomActivity.this, ChatService.class); // 다음넘어갈 컴포넌트 정의
		isChatService = bindService(bindIntent, serviceConn, Context.BIND_AUTO_CREATE); // 인텐트,서비스연결객체,플래그 전달 -->서비스 연결
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

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
	} // onDestroy() 끝

} // ChatRoomActivity 클래스 끝
