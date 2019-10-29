package com.psj.accommodation.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.psj.accommodation.Activity.ChattingActivity;
import com.psj.accommodation.Data.ChatSearchItem;
import com.psj.accommodation.Data.ChatUserInfo;
import com.psj.accommodation.Data.ChattingItem;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

// startService 와 다른 점은 서비스가 실행되고 있는 상황에서 서비스를 시작하면, onStartCommand()함수를 타지 않는다는 점
public class ChatService extends Service {

	private static final String TAG = "ChatService";
	public static final int SET_SOCKET = 1;
	public static final int RECEIVE_THREAD = 2;
	public static final int SEND_THREAD = 3;
	public static final int CHATTING_SEND_THREAD = 4;
	public static final int DISCONNECT = 0;


	// 서버 접속 여부를 판별하기 위한 변수
	boolean isConnect = false;
	// 어플 종료시 스레드 중지를 위해...
	boolean isRunning = false;
	// 서버와 연결되어있는 소켓 객체
	public static Socket client_socket;
	// 외부로 데이터를 전달하려면 바인더 사용
	// Binder 객체는 IBinder 인터페이스 상속구현 객체입니다
	// public class Binder extends Object implements IBinder
	private IBinder iBinder = new MyBinder();

	String SendInfo = "";

	Messenger activityMessenger = null;

	// ChatServiceHandler 클래스 시작
	class ChatServiceHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
				case SET_SOCKET:
					activityMessenger = msg.replyTo; // 액티비티로 부터 가져온 메시지 객체
					Toast.makeText(ChatService.this, "소켓 접속 기능 시작", Toast.LENGTH_SHORT).show();
					setSocket();
					break;
				case RECEIVE_THREAD:
					activityMessenger = msg.replyTo;
					Toast.makeText(ChatService.this, "수신 스레드 기능 시작", Toast.LENGTH_SHORT).show();
					receiveThread();
					break;
				case SEND_THREAD:
					activityMessenger = msg.replyTo;
					Toast.makeText(ChatService.this, "송신 스레드 기능 시작", Toast.LENGTH_SHORT).show();
					SendInfo = msg.getData().getString("sendInfo");
					sendThread();
					break;
				case DISCONNECT:
					activityMessenger = null;
					Toast.makeText(ChatService.this, "서비스 기능 종료", Toast.LENGTH_SHORT).show();
					break;
			}

		} // handleMessage() 끝

	} // ChatServiceHandler 클래스 끝

	// handler는 binder를 통하여 서로 넘겨줄수 없으니, handler를 wrapping할 수 있는 Messenger 객체를 이용한다
	Messenger chatServiceMessenger = new Messenger(new ChatServiceHandler());


	// 서비스 바인더 내부 클래스 선언
	public class MyBinder extends Binder {
		public ChatService getMyService() {

			Log.i(TAG, "getService 실행");

			return ChatService.this; //현재 서비스를 반환.
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// 액티비티에서 bindService() 를 실행하면 호출됨
		Log.i(TAG, "onBind 실행");
		// Handler를 직접 전달할 수 없으니 Messenger 객체에 담아 onBind()에서 Messenger 객체의 binder를 반환한다
		return chatServiceMessenger.getBinder();
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Log.i(TAG, "onCreate 실행");

		Toast.makeText(getApplicationContext(), "Service Created", Toast.LENGTH_SHORT).show();


	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.i(TAG, "onStartCommand 실행");


		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {

		Log.i(TAG, "onUnbind 실행");

		return super.onUnbind(intent);
	}


	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.i(TAG, "onDestroy 실행");
	}

	// 서버접속 처리하는 스레드 클래스 - 안드로이드에서 네트워크 관련 동작은 항상
	// 메인스레드가 아닌 스레드에서 처리해야 한다.
	// 소켓 접속 기능
	public void setSocket() {

		Log.i(TAG, "setSocket 실행!");

		new Thread(new Runnable() {
			@Override
			public void run() {

				try {
					// 접속한다
					Socket socket = new Socket("54.180.152.167", 8888);
					client_socket = socket;

					Log.i(TAG, "setSocket 클라이언트 소켓 정보 : " + client_socket.toString());

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();


		Log.i(TAG, "소켓 접속 Thread 이후 액티비티에 메시지 전달");

		try {

			Message msg = Message.obtain(null, SET_SOCKET);
			msg.replyTo = chatServiceMessenger;
			activityMessenger.send(msg);

		} catch (RemoteException e) {
			e.printStackTrace();
		}


	} // setSocket() 끝

	// 수신 스레드 기능
	public void receiveThread() {


		Log.i(TAG, "receiveThread 실행!");

		Log.i(TAG, "receiveThread 클라이언트 소켓 정보 : " + client_socket.toString());

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {


					while (true) {


						InputStream inputStream = client_socket.getInputStream();


						Log.i(TAG, "receive thread inputstream : " + inputStream);

						DataInputStream dataInputStream = new DataInputStream(inputStream);

						Log.i(TAG, "receive thread datainputstream" + dataInputStream);


						String receiveData = dataInputStream.readUTF();


						Log.i(TAG, "receiveThread 받은 데이터 : " + receiveData);

						ChattingActivity.ChattingActivityUI chattingActivityUI = new ChattingActivity.ChattingActivityUI(receiveData);
						chattingActivityUI.start();

						Log.i(TAG, "receiveThread 받은 데이터 액티비티로 메시지 전달 끝!");

					}


				} catch (Exception e) {


					e.printStackTrace();
				}
			}
		}).start();


	} // receiveThread() 끝

	// 송신 스레드 기능
	public void sendThread() {

		Log.i(TAG, "sendThread 실행!");

//		for (int i = 0; i < userInfo.size(); i++) {
//
//			Log.i(TAG, i + "번째 userInfo : " + userInfo.get(i));
//
//		}


		Log.i(TAG, "sendThread 클라이언트 소켓 정보 : " + client_socket.toString());

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					OutputStream outputStream = client_socket.getOutputStream();
					DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

					Log.i(TAG, "sendThread 채팅 서버에 보낼 문자열 : " + SendInfo);

					// get(0) -> 초대된 유저 이름
					// get(1) -> 초대된 유저 프로필 이미지
					// get(2) -> 생성한 채팅방 고유 번호

					dataOutputStream.writeUTF(SendInfo);


				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	} // sendThread() 끝

} // ChatService class 끝
