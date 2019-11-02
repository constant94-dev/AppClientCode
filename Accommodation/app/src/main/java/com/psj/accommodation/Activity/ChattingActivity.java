package com.psj.accommodation.Activity;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.psj.accommodation.Adapter.ChatRoomAdapter;
import com.psj.accommodation.Adapter.ChattingAdapter;
import com.psj.accommodation.Data.ChatSearchItem;

import com.psj.accommodation.Data.ChattingItem;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;
import com.psj.accommodation.Service.ChatService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChattingActivity extends AppCompatActivity {

	public static final String TAG = "ChattingActivity";
	//public static final int RECEIVE_CHATTING = 100;

	ImageView chatRoomMore, chatElement, chatSend, chatRoomBack;
	EditText chatContent;

	public static Context chattingContext;

	// 서버 접속 여부를 판별하기 위한 변수
	boolean isConnect = false;
	// 어플 종료시 스레드 중지를 위해...
	boolean isRunning = false;
	// 서버와 연결되어있는 소켓 객체
	Socket client_socket;
	// 사용자 닉네임( 내 닉넴과 일치하면 내가보낸 말풍선으로 설정 아니면 반대설정)
	String user_nickname;

	String chatRoomImage = "";
	String chatRoomName = "";
	String chatRoomNum = "";

	private ArrayList<ChattingItem> chattingItemList;

	private RecyclerView chattingRecyclerView;
	private RecyclerView.Adapter chattingAdapter;
	private RecyclerView.LayoutManager chattingLayoutManager;

	// 쉐어드에서 가져온 이름과 이메일
	String sessionName = "";
	String sessionEmail = "";

	// AWS 서버 IP 주소
	String serverPath = "54.180.152.167";

	private ChatService chatService;
	Messenger serviceMessenger;
	boolean isChatService = false; // 서비스 중인지 확인용 변수

	// ChattingActivityHandler 클래스 시작
	class ChattingActivityHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			Log.i(TAG, "서비스에서 응답 왔다 msg.what : " + msg.what);

			switch (msg.what) {
				case ChatService.RECEIVE_THREAD:
					serviceMessenger = msg.replyTo;
					Toast.makeText(ChattingActivity.this, "서비스에서 리시브 스레드 응답 받음", Toast.LENGTH_SHORT).show();
					break;
				case ChatService.RECEIVE_CHATTING:
					serviceMessenger = msg.replyTo;
					Log.i(TAG, "ChattingActivity -> " + msg.replyTo);
					String UIData = msg.getData().getString("UIData");
					Log.i(TAG, "서비스에서 채팅 액티비티로 전달된 데이터 -> " + UIData);

					int targetRoomNum = UIData.indexOf("roomNum");
					String resultRoomNum = UIData.substring(targetRoomNum, (UIData.substring(targetRoomNum).indexOf("sendUser")));
					int targetSendUser = UIData.indexOf("sendUser");
					Log.i(TAG, "유저가 제대로 구분이 안되는 이유가 뭐지 ????????? " + UIData.indexOf("sendMessage"));
					String resultSendUser = UIData.substring(targetSendUser, UIData.indexOf("sendMessage"));
					int targetSendMessage = UIData.indexOf("sendMessage");
					String resultSendMessage = UIData.substring(targetSendMessage);

					Log.i(TAG, "Service 에서 채팅액티비티로 전달된 데이터 방번호 -> " + resultRoomNum);
					Log.i(TAG, "Service 에서 채팅액티비티로 전달된 데이터 유저 -> " + resultSendUser);
					Log.i(TAG, "Service 에서 채팅액티비티로 전달된 데이터 메시지 -> " + resultSendMessage);

					String[] resultRoomNumSplit = resultRoomNum.split(":");
					String[] resultSendUserSplit = resultSendUser.split(":");
					String[] resultSendMessageSplit = resultSendMessage.split(":");

					Log.i(TAG, "가공된 방번호 - > " + resultRoomNumSplit[1]);
					Log.i(TAG, "가공된 채팅 보낸 유저이름 - > " + resultSendUserSplit[1]);
					Log.i(TAG, "가공된 메시지 - > " + resultSendMessageSplit[1]);

					// 채팅서버에서 응답한 데이터를 Service 클래스에서 확인 후 UI 세팅을 해주고 싶은 액티비티로 데이터를 핸들러로 전달했다
					// 핸들러로 전달받은 채팅보낸 유저이름을 데이터베이스에 저장된 프로필정보를 가져와 리사이클러뷰에 세팅한다
					if (chatRoomNum.equals(resultRoomNumSplit[1])) {
						UISetting(resultRoomNumSplit[1], resultSendUserSplit[1], resultSendMessageSplit[1]);
					} else {
						Log.i(TAG, "채팅서버에서 응답한 방번호가 다릅니다!");
					}


					break;

			}

		}
	} // ChatRoomActivityHandler 클래스 끝

	// 핸들러 wrapping 한 메시지 객체
	Messenger chattingActivityMessenger = new Messenger(new ChattingActivityHandler());


	ServiceConnection serviceConn = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// 서비스와 연결되었을 때 호출되는 메서드
			Log.i(TAG, "onServiceConnected : 실행");

			serviceMessenger = new Messenger(service);

			try {

				Message chattingActivity_msg = Message.obtain(null, ChatService.RECEIVE_CHATTING);
				chattingActivity_msg.replyTo = chattingActivityMessenger;
				Log.i(TAG, "채팅 액티비티 메신저 주소 -> " + chattingActivityMessenger.toString());
				serviceMessenger.send(chattingActivity_msg);

			} catch (RemoteException e) {
				e.printStackTrace();
			}


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
		setContentView(R.layout.activity_chatroom);

		Log.i(TAG, "onCreate : 실행");

		chatRoomMore = findViewById(R.id.chatRoomMore);
		chatElement = findViewById(R.id.chatElement);
		chatSend = findViewById(R.id.chatSend);
		chatContent = findViewById(R.id.chatContent);
		chatRoomBack = findViewById(R.id.chatRoomBack);
		chattingRecyclerView = findViewById(R.id.chatRoom_recycler_view);


		// 쉐어드에 저장된 정보 가져오기 (이름,이메일)
		getShard();

		chattingItemList = new ArrayList<>();

		// 리사이클러뷰 레이아웃 설정
		chattingLayoutManager = new LinearLayoutManager(this);
		chattingRecyclerView.setLayoutManager(chattingLayoutManager);

		// ChatRoomAdapter 생성자 시작
		chattingAdapter = new ChattingAdapter(getApplicationContext(), chattingItemList);

		chattingRecyclerView.setAdapter(chattingAdapter);


		// 데이터 수신
		Bundle getChatting = getIntent().getExtras();

		if (getChatting == null) {
			Log.i(TAG, "데이터 수신 할거 없음");
		} else {
			Log.i(TAG, "데이터 수신 할거 있음");

			chatRoomNum = getChatting.getString("chatRoomNum");
			chatRoomName = getChatting.getString("chatRoomName");
			chatRoomImage = getChatting.getString("chatRoomImage");

		} // 데이터 수신 끝

		// android.os.NetworkOnMainThreadException 에러가 발생하는 이유
		// 메인 쓰레드에서 네트워크 연산을 실행했을 때 발생
		// 에러 해결방법 AsyncTask 를 사용해주면 된다
		// init();

		bindServiceStart();

	} // onCreate 끝

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume : 실행");

		chatElement.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "이미지 추가 작업");
			}
		});

		chatSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "채팅 전송");

				try {

					Message s_msg = Message.obtain(null, ChatService.SEND_THREAD);
					s_msg.replyTo = chattingActivityMessenger;
					Bundle bundle = new Bundle();
					bundle.putString("sendInfo", "chatting:" + chatRoomNum + ":" + chatRoomName + ":" + sessionName + ":content" + chatContent.getText().toString());
					s_msg.setData(bundle);
					serviceMessenger.send(s_msg);

					LastMessage(chatRoomNum, chatContent.getText().toString());

					chatContent.setText("");

				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});

		chatRoomBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent chatRoomIntent = new Intent(ChattingActivity.this, ChatRoomActivity.class);
				startActivity(chatRoomIntent);
				finish();
			}
		});


	} // onResume 끝


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
	}

	public void LastMessage(String chatRoomNum, String lastMessage) {
		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);

		Log.i(TAG, "마지막 메시지 저장할 방 번호 -> " + chatRoomNum);
		Log.i(TAG, "마지막 메시지 -> " + lastMessage);

		// 인터페이스 ApiService에 선언한 chatRoomList()를 호출합니다
		Call<String> call = apiService.chatRoomUpdate(chatRoomNum, lastMessage);

		call.enqueue(new Callback<String>() {
			@Override
			public void onResponse(Call<String> call, Response<String> response) {
				Log.i(TAG, "onResponse : 실행 -> " + response.body().toString());
			}

			@Override
			public void onFailure(Call<String> call, Throwable throwable) {
				Log.i(TAG, "onFailure : 실행 -> " + throwable);
				Toast.makeText(ChattingActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
			}
		});
	}

	public void UISetting(final String roomNum, final String sendUser, final String sendMessage) {
		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);

		// 인터페이스 ApiService에 선언한 chatRoomList()를 호출합니다
		Call<String> call = apiService.sendUserGetImage(sendUser);

		call.enqueue(new Callback<String>() {
			@Override
			public void onResponse(Call<String> call, Response<String> response) {
				Log.i(TAG, "onResponse : 실행 -> " + response.body().toString());

				String sendUserImage = response.body().toString();

				Log.i(TAG, "리사이클러뷰 세팅 시작.....");

				chattingItemList.add(0, new ChattingItem(roomNum, sendUser, sendUserImage, sendMessage, sessionName));

				chattingAdapter.notifyItemInserted(0);

				Log.i(TAG, "리사이클러뷰 세팅 끝.....");

			}

			@Override
			public void onFailure(Call<String> call, Throwable throwable) {
				Log.i(TAG, "onFailure : 실행 -> " + throwable);
				Toast.makeText(ChattingActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
			}

		});


	}


	// 쉐어드에 저장된 사용자 이름/이메일 가져오기 기능 (세션 활용)
	public void getShard() {
		SharedPreferences sharedPreferences = getSharedPreferences("sessionName", MODE_PRIVATE);
		sessionName = sharedPreferences.getString("name", "noName");
		sessionEmail = sharedPreferences.getString("email", "noEmail");
	} // getShard() 끝

	public void bindServiceStart() {
		Intent bindIntent = new Intent(ChattingActivity.this, ChatService.class); // 다음넘어갈 컴포넌트 정의
		isChatService = bindService(bindIntent, serviceConn, Context.BIND_AUTO_CREATE); // 인텐트,서비스연결객체,플래그 전달 --> 서비스 연결

	}


} // ChattingActivity 클래스 끝
