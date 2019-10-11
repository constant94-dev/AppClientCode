package com.psj.accommodation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.psj.accommodation.Adapter.ChatRoomAdapter;
import com.psj.accommodation.Data.ChatSearchItem;
import com.psj.accommodation.Data.CommentItem;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRoomActivity extends AppCompatActivity {

	public static final String TAG = "ChatRoomActivity";

	ImageView chatAdd, Home;

	private RecyclerView chatRecyclerView;
	private RecyclerView.Adapter chatAdapter;
	private RecyclerView.LayoutManager chatLayoutManager;

	private ArrayList<ChatSearchItem> chatSearchItemList = null;
	private ArrayList<ChatSearchItem> chatRoomItemList;

	ChatSearchItem chatSearchItemData = new ChatSearchItem();

	String name = "";
	String image = "";
	String chatRoomJson = "";

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		Log.i(TAG, "onCreate : 실행");

		chatAdd = findViewById(R.id.chatAdd);
		Home = findViewById(R.id.Home);
		chatRecyclerView = findViewById(R.id.chat_recycler_view);

		chatRoomItemList = new ArrayList<>();

		// 리사이클러뷰 레이아웃 설정
		chatLayoutManager = new LinearLayoutManager(this);
		chatRecyclerView.setLayoutManager(chatLayoutManager);

		// ChatRoomAdapter 생성자 시작
		chatAdapter = new ChatRoomAdapter(this, chatRoomItemList, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getTag() != null) {

					final int position = (int) v.getTag();
					Log.i(TAG, "어댑터에서 가져온 태그 값 : " + position);

					Intent chatRoomIntent = new Intent(ChatRoomActivity.this, ChattingActivity.class);
					chatRoomIntent.putExtra("chatItem", chatRoomItemList);
					startActivity(chatRoomIntent);

				}
			}
		}); // ChatRoomAdapter 생성자 끝

		chatRecyclerView.setAdapter(chatAdapter);

		// 데이터베이스에 저장된 채팅방 정보 불러오기
		showChatRoom();

		// 데이터 수신
		chatSearchItemList = (ArrayList<ChatSearchItem>) getIntent().getSerializableExtra("chatSearchItem");

		if (chatSearchItemList == null) {
			Log.i(TAG, "데이터 수신 할거 없음");
		} else {
			Log.i(TAG, "데이터 수신 할거 있음");


			for (ChatSearchItem chatSearchItem : chatSearchItemList) {
				Log.i(TAG, "전달 받은 이메일 : " + chatSearchItem.getChatSearchEmail());

				name += chatSearchItem.getChatSearchName() + " ";
				image += chatSearchItem.getChatSearchImage() + " ";

			} // 데이터 수신 값 체크 반복문 끝

			Log.i(TAG, "내가 새로 저장한 이름 리스트 : " + name);
			Log.i(TAG, "내가 새로 저장한 이미지 리스트 : " + image);

			Log.i(TAG, "데이터베이스에 채팅방 정보 저장 시작");

			// 데이터베이스에 채팅방 정보 저장
			insertChatRoom();

			Log.i(TAG, "데이터베이스에 채팅방 정보 저장 끝");


		} // 데이터 수신 끝


	} // onCreate 끝

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume : 실행");

		Home.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mainIntent = new Intent(ChatRoomActivity.this, MainActivity.class);
				startActivity(mainIntent);
				finish();
			}
		});

		// 채팅방 추가 이미지 클릭 이벤트트
		chatAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent chatSearchIntent = new Intent(ChatRoomActivity.this, ChatSearchActivity.class);
				startActivity(chatSearchIntent);
				finish();
			}
		});

	} // onResume 끝

	private void insertChatRoom() {

		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);

		// 인터페이스 ApiService에 선언한 chatRoomInsert()를 호출합니다
		Call<String> call = apiService.chatRoomInsert(name, image);

		call.enqueue(new Callback<String>() {
			@Override
			public void onResponse(Call<String> call, Response<String> response) {
				Log.i("onResponse : 실행", response.body().toString());
				Toast.makeText(ChatRoomActivity.this, "서버에서 받은 응답 값 : ", Toast.LENGTH_LONG);

				chatRoomItemList.add(0, new ChatSearchItem(name, image));


				chatAdapter.notifyItemInserted(0);

			} // onResponse 끝

			@Override
			public void onFailure(Call<String> call, Throwable throwable) {
				Log.i("onFailure : 실행", " / " + throwable);
				Toast.makeText(ChatRoomActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
			} // onFailure 끝

		}); // enqueue 끝
	}


	// 데이터베이스에 저장된 채팅방 목록 가져오기 기능
	private void showChatRoom() {

		Log.i(TAG, "showChatRoom : 실행");

		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);

		// 인터페이스 ApiService에 선언한 chatRoomList()를 호출합니다
		Call<JsonObject> call = apiService.chatRoomList();

		call.enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				Log.i("onResponse : 실행", response.body().toString());
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


		try {
			// 서버에서 가져온 json 데이터 처음 시작이 '{' 중괄호로 시작해서 JSONObject 에 담아준다
			JSONObject jsonObject = new JSONObject(chatRoomJson);
			// JSONArray [ 대괄호로 시작하니 jsonObject 에서 get 한 값을 JSONArray 에 담아준다
			JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

			// 반복문을 이용하여 알맞게 풀어준다
			for (int i = 0; i < jsonArray.length(); i++) {

				Log.i(TAG, "반복문 : 실행");

				JSONObject item = jsonArray.getJSONObject(i);

				String chatRoomNum = item.getString(TAG_NUM);
				String chatRoomNames = item.getString(TAG_NAMES);
				String chatRoomImages = item.getString(TAG_IMAGES);


				Log.i(TAG, "채팅방 고유 번호 : " + chatRoomNum);
				Log.i(TAG, "채팅방 참여자들 : " + chatRoomNames);
				Log.i(TAG, "채팅방 프로필 이미지들 : " + chatRoomImages);


				// 어댑터에 전달할 데이터 추가
				chatRoomItemList.add(new ChatSearchItem(chatRoomNum, chatRoomNames, chatRoomImages));

				// 어댑터에게 새로 삽입된 아이템이 있다는걸 알려준다
				chatAdapter.notifyItemInserted(0);

			} // 반복문을 이용하여 Json 풀어주기 끝


		} catch (JSONException e) {

			Log.d(TAG, "showResult : ", e);
		}

	} // showChatResult() 끝

} // ChatRoomActivity 클래스 끝
