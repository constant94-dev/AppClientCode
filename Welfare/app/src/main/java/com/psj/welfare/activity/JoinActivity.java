package com.psj.welfare.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.psj.welfare.R;
import com.psj.welfare.custom.OnSingleClickListener;

/*
* 회원가입 액티비티이다
* 너의 혜택은 자체 회원가입이고 사용자에게 필요한 정보를 입력받아야 한다
* 필요한 정보 구상중.....
* */
public class JoinActivity extends AppCompatActivity {

	public static final String TAG = "JoinActivity";


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join);
		Log.i(TAG, "onCreate 실행!");

	} // onCreate end

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart 실행!");

	} // onStart end

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume 실행!");


	} // onResume end

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy 실행!");

	} // onDestroy end

} // MYLoginActivity class end
