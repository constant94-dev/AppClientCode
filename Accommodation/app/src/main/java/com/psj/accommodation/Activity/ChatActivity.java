package com.psj.accommodation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.psj.accommodation.R;

public class ChatActivity extends AppCompatActivity {

	public static final String TAG = "ChatActivity";

	ImageView chatAdd;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		Log.i(TAG, "onCreate : 실행");

		chatAdd = findViewById(R.id.chatAdd);


	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume : 실행");

		// 채팅방 추가 이미지 클릭 이벤트트
		chatAdd.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent chatSearchIntent = new Intent(ChatActivity.this, ChatSearchActivity.class);
				startActivity(chatSearchIntent);
				finish();
			}
		});

	}
} // ChatActivity 클래스 끝
