package com.psj.accommodation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.psj.accommodation.R;


// TODO 어플 시작될 때 보여지는 스플래시 화면
public class SplashActivity extends AppCompatActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			// 2초 기다림
			Thread.sleep(2000);
			// 화면 이동
			Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
			startActivity(mainIntent);
			// 현재화면 파괴(Destroy)
			finish();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
