package com.psj.accommodation.Activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	Socket member_socket;
	// 사용자 닉네임( 내 닉넴과 일치하면 내가보낸 말풍선으로 설정 아니면 반대설정)
	String user_nickname;

	private ArrayList<ChatSearchItem> chatItemList;
	private ArrayList<ChattingItem> chattingItemList;


	Socket c_socket;

	private RecyclerView chattingRecyclerView;
	private RecyclerView.Adapter chattingAdapter;
	private RecyclerView.LayoutManager chattingLayoutManager;

	// 쉐어드에서 가져온 이름과 이메일
	String sessionName = "";
	String sessionEmail = "";

	// 인텐트로 전달받은 채팅방 번호
	String chatRoomNum = "";
	// 인텐트로 전달받은 채팅방 참여자들 이름
	String chatRoomName = "";
	// 인텐트로 전달받은 채팅방 참여자들 프로필 이미지
	String chatRoomImage = "";
	// 인텐트로 전달받은 채팅방 참여자들 이메일
	String chatRoomEmail = "";

	// Json 데이터 저장 변수
	String chattingJson = "";
	// 핸들러로 전달한 채팅 내용
	String chatHandlerContent = "";

	// AWS 서버 IP 주소
	String serverPath = "54.180.152.167";

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
		chatItemList = (ArrayList<ChatSearchItem>) getIntent().getSerializableExtra("chatItem");

		if (chatItemList == null) {
			Log.i(TAG, "데이터 수신 할거 없음");
		} else {
			Log.i(TAG, "데이터 수신 할거 있음");
			for (ChatSearchItem chatSearchItem : chatItemList) {
				Log.i(TAG, "전달 받은 이메일 : " + chatSearchItem.getChatSearchEmail());
				Log.i(TAG, "전달 받은 이름 : " + chatSearchItem.getChatSearchName());
				Log.i(TAG, "전달 받은 이미지 : " + chatSearchItem.getChatSearchImage());
				Log.i(TAG, "전달 받은 고유 번호 : " + chatSearchItem.getChatRoomNum());

				chatRoomNum = chatSearchItem.getChatRoomNum();
				chatRoomName = chatSearchItem.getChatSearchName();
				chatRoomImage = chatSearchItem.getChatSearchImage();
				chatRoomEmail = chatSearchItem.getChatSearchEmail();
			}


		} // 데이터 수신 끝

		// android.os.NetworkOnMainThreadException 에러가 발생하는 이유
		// 메인 쓰레드에서 네트워크 연산을 실행했을 때 발생
		// 에러 해결방법 AsyncTask 를 사용해주면 된다
		init();


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

				if (isConnect == true) {   //접속 후

					// 입력한 문자열을 가져온다.
					String msg = chatContent.getText().toString();
					// 송신 스레드 가동
					SendToServerThread thread = new SendToServerThread(member_socket, msg);
					thread.start();


				}
			}
		});


	} // onResume 끝

	public void init() {
		if (isConnect == false) {   //접속전

			//서버에 접속한다.

			Log.i(TAG, "접속중...");

			// 접속 스레드 가동
			ConnectionThread thread = new ConnectionThread();
			thread.start();


		} else {                  // 접속 후

			// 입력한 문자열을 가져온다.
			String msg = chatContent.getText().toString();
			// 송신 스레드 가동
			SendToServerThread thread = new SendToServerThread(member_socket, msg);
			thread.start();
		}
	}

	// 버튼과 연결된 메소드
	public void btnMethod(View v) {
		if (isConnect == false) {   //접속전
			//사용자가 입력한 닉네임을 받는다.
			String nickName = sessionName;
			if (nickName.length() > 0 && nickName != null) {
				//서버에 접속한다.

				Log.i(TAG, "접속중...");

				// 접속 스레드 가동
				ConnectionThread thread = new ConnectionThread();
				thread.start();

			}
			// 닉네임이 입력되지않을경우 다이얼로그창 띄운다.
			else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("닉네임을 입력해주세요");
				builder.setPositiveButton("확인", null);
				builder.show();
			}
		} else {                  // 접속 후

			// 입력한 문자열을 가져온다.
			String msg = chatContent.getText().toString();
			// 송신 스레드 가동
			SendToServerThread thread = new SendToServerThread(member_socket, msg);
			thread.start();
		}
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
				member_socket = socket;
				// 쉐어드에 저장된 이름을 서버로 전달한다.
				String nickName = sessionName;
				user_nickname = nickName;     // 화자에 따라 말풍선을 바꿔주기위해
				// 스트림을 추출
				OutputStream os = socket.getOutputStream();
				DataOutputStream dos = new DataOutputStream(os);
				// 닉네임을 송신한다.
				dos.writeUTF(nickName);

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

								String[] msgSplit = msg.split(" ");

								String[] msgSplit2 = msg.split(":");


								chattingItemList.add(0, new ChattingItem("num", msgSplit[0], msgSplit[1], "image", msgSplit2[1]));
								chattingAdapter.notifyItemInserted(0);

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


	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			member_socket.close();
			isRunning = false;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 쉐어드에 저장된 사용자 이름/이메일 가져오기 기능 (세션 활용)
	public void getShard() {
		SharedPreferences sharedPreferences = getSharedPreferences("sessionName", MODE_PRIVATE);
		sessionName = sharedPreferences.getString("name", "noName");
		sessionEmail = sharedPreferences.getString("email", "noEmail");
	} // getShard() 끝


	// 서버에서 json 형태로 가져온 값을 리사이클러뷰에 추가 시키는 방법
	private void showChattingResult() {

		Log.i(TAG, "showChatResult : 실행");

		String TAG_JSON = "result";
		String TAG_EMAIL = "email";
		String TAG_NAMES = "name";
		String TAG_IMAGES = "image";


		try {
			// 서버에서 가져온 json 데이터 처음 시작이 '{' 중괄호로 시작해서 JSONObject 에 담아준다
			JSONObject jsonObject = new JSONObject(chattingJson);
			// JSONArray [ 대괄호로 시작하니 jsonObject 에서 get 한 값을 JSONArray 에 담아준다
			JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

			// 반복문을 이용하여 알맞게 풀어준다
			for (int i = 0; i < jsonArray.length(); i++) {

				Log.i(TAG, "반복문 : 실행");

				JSONObject item = jsonArray.getJSONObject(i);

				String chattingEmail = item.getString(TAG_EMAIL);
				String chattingName = item.getString(TAG_NAMES);
				String chattingImage = item.getString(TAG_IMAGES);


				Log.i(TAG, "채팅 입력한 유저 이메일 : " + chattingEmail);
				Log.i(TAG, "채팅 입력한 유저 이름 : " + chattingName);
				Log.i(TAG, "채팅 입력한 유저 프로필 이미지 : " + chattingImage);

				// 어댑터에 전달할 데이터 추가
				chattingItemList.add(new ChattingItem(chatRoomNum, chattingName, chattingImage, chatHandlerContent));
				// 어댑터에게 새로 삽입된 아이템이 있다는걸 알려준다
				chattingAdapter.notifyItemInserted(0);


			} // 반복문을 이용하여 Json 풀어주기 끝


		} catch (JSONException e) {

			Log.d(TAG, "showResult : ", e);
		}

	} // showChattingResult() 끝


} // ChattingActivity 클래스 끝
