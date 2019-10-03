package com.psj.accommodation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.psj.accommodation.R;

public class ChatRoomActivity extends AppCompatActivity {

	public static final String TAG = "ChatRoomActivity";

	ImageView chatRoomMore, chatElement, chatSend;
	EditText chatContent;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chatroom);

		Log.i(TAG, "onCreate : 실행");

		chatRoomMore = findViewById(R.id.chatRoomMore);
		chatElement = findViewById(R.id.chatElement);
		chatSend = findViewById(R.id.chatSend);
		chatContent = findViewById(R.id.chatContent);

	}

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
				Log.i(TAG, "채팅내용 전송");
			}
		});

	}

} // ChatActivity 클래스 끝
