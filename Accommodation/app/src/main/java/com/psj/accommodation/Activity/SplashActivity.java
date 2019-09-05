package com.psj.accommodation.Activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.psj.accommodation.R;


// TODO 어플 시작될 때 보여지는 스플래시 화면
public class SplashActivity extends AppCompatActivity {


	public static final String TAG = "SplashActivity";

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
	} // onCreate() 끝


} // SplashActivity 클래스 끝
