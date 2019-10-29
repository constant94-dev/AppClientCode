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

	ImageView chatRoomMore, chatElement, chatSend;
	EditText chatContent;

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

				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		});


	} // onResume 끝

	public void init() {
		// 접속 후

		// 입력한 문자열을 가져온다.
		String msg = chatContent.getText().toString();
		// 송신 스레드 가동
		SendToServerThread thread = new SendToServerThread(client_socket, msg);
		thread.start();

	}

	// 서버접속 처리하는 스레드 클래스 - 안드로이드에서 네트워크 관련 동작은 항상
	// 메인스레드가 아닌 스레드에서 처리해야 한다.
	class ConnectionThread extends Thread {

		@Override
		public void run() {
			try {

				Log.i(TAG, "ConnectionThread 실행");

				// 접속한다.
				final Socket socket = new Socket("54.180.152.167", 8888);
				client_socket = socket;
				// 사용자 이름과 프로필 이미지
				String nameANDimage = chatRoomNum + ":" + sessionName + ":";

				// 스트림을 추출
				OutputStream os = socket.getOutputStream();
				DataOutputStream dos = new DataOutputStream(os);
				// 사용자 이름과 프로필 이미지를 서버에 전송한다
				dos.writeUTF(nameANDimage);

				runOnUiThread(new Runnable() {
					@Override
					public void run() {

						// 접속 상태를 true로 셋팅한다.
						isConnect = true;
						// 메세지 수신을 위한 스레드 가동
						isRunning = true;
						MessageThread thread = new MessageThread(socket);
						thread.start();
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	} // ConnectionThread class 끝


	class MessageThread extends Thread {
		Socket socket;
		DataInputStream dis;

		public MessageThread(Socket socket) {
			try {

				Log.i(TAG, "MessageThread 실행");

				this.socket = socket;
				InputStream is = socket.getInputStream();
				dis = new DataInputStream(is);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				while (isRunning) {
					// 서버로부터 데이터를 수신받는다.
					final String msg = dis.readUTF();
					// 화면에 출력
					runOnUiThread(new Runnable() {
						@Override
						public void run() {

							Log.i(TAG, "서버로부터 받아온 데이터 : " + msg);


							if (msg.startsWith("name")) {

								Log.i(TAG, "메시지 시작 name");

								//String[] msgSplit = msg.split(" ");

								//Log.i(TAG, "메시지 어댑터에 전달 : " + msgSplit[1]);

								chattingItemList.add(0, new ChattingItem(msg));
								chattingAdapter.notifyItemInserted(0);


							} else if (msg.startsWith("message")) {

								String[] msgSplit = msg.split(":");

								Log.i(TAG, "세션 이름 : " + sessionName);
								Log.i(TAG, "분할한 이름 : " + msgSplit[1]);

								if (msgSplit[1].equals(sessionName)) {
									chattingItemList.add(0, new ChattingItem("num", msgSplit[0], msgSplit[1], "image", msgSplit[2], 1));
									chattingAdapter.notifyItemInserted(0);
								} else {
									chattingItemList.add(0, new ChattingItem("num", msgSplit[0], msgSplit[1], "image", msgSplit[2], 0));
									chattingAdapter.notifyItemInserted(0);
								}


							}

						}
					});
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	} // MessageThread class 끝

	// 서버에 데이터를 전달하는 스레드
	class SendToServerThread extends Thread {
		Socket socket;
		String msg;
		DataOutputStream dos;

		public SendToServerThread(Socket socket, String msg) {

			Log.i(TAG, "SendToServerThread 실행");

			try {
				this.socket = socket;
				this.msg = msg;
				OutputStream os = socket.getOutputStream();
				dos = new DataOutputStream(os);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				// 서버로 데이터를 보낸다.
				dos.writeUTF(msg);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						chatContent.setText("");
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	} // SendToServerThread class 끝

	public static class ChattingActivityUI extends Thread {

		String content;

		public ChattingActivityUI(String content) {
			this.content = content;
		}

		@Override
		public void run() {
			super.run();

			Log.i(TAG, "UI 출력 내용 -> " + content);
			// 리사이클러뷰에 적용시킨다

		}

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
