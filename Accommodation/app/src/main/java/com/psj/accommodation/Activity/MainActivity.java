package com.psj.accommodation.Activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.psj.accommodation.Adapter.MainAdapter;
import com.psj.accommodation.Data.MainItem;
import com.psj.accommodation.R;

import java.util.ArrayList;


// TODO 로그인 후 보여지는 메인 화면
public class MainActivity extends AppCompatActivity {

	public static final String TAG = "MainActivity";
	String[] PERMISSIONS = {"android.permission.CAMERA",
			"android.permission.WRITE_EXTERNAL_STORAGE"};
	static final int PERMISSIONS_REQUEST_CODE = 1000; // 퍼미션 결과 값 받을 때 구분해주는 변수

	private RecyclerView mainRecyclerView;
	private RecyclerView.Adapter mainAdapter;
	private RecyclerView.LayoutManager mainLayoutManager;

	TextView reviewRegister;

	// 메인 아이템 리스트
	private static ArrayList<MainItem> mainItemList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		reviewRegister = findViewById(R.id.ReviewRegisterText);

		Log.i(TAG, "onCreate : 실행");

		// 안드로이드 SDK 버전 23 이상이라면 권한 요청해야됨
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

			Log.i(TAG, "SDK 버전 23 이상 : 실행");

			// 퍼미션 상태 확인
			if (hasPermissions(PERMISSIONS)) {

				Log.i(TAG, "퍼미션 상태 허가됨 : 실행");
				Toast.makeText(MainActivity.this, "카메라/외부저장소 읽기,쓰기 권한 허가됨", Toast.LENGTH_SHORT);
			} else {

				Log.i(TAG, "퍼미션 상태 허가 안됨 : 실행");

				// 퍼미션 허가 안되어있다면 사용자에게 요청
				requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
			}
		} else {
			Log.i(TAG, "SDK 버전 23 이하 : 실행");
			// 안드로이드 SDK 버전 23 이전이라면 권한요청할 필요없다
			Toast.makeText(MainActivity.this, "SDK 버전 23 이하입니다", Toast.LENGTH_SHORT);
			return;
		}


		// 데이터준비 실제로는 ArrayList<>등을 사용해야 할듯 하다.
		// DB에서 아이템을 가져와 배열에 담아 주면 된다.

		// ArrayList 객체 생성
		mainItemList = new ArrayList<>();
		// ArrayList 값 추가
		mainItemList.add(new MainItem("라마다호텔", "2019-08-01 ~ 2019-08-03", "4.5"));
		mainItemList.add(new MainItem("신라호텔", "2019-08-01 ~ 2019-08-03", "5.0"));
		mainItemList.add(new MainItem("부산여관", "2019-08-01 ~ 2019-08-03", "4.0"));

		mainRecyclerView = findViewById(R.id.main_recycler_view);
		mainRecyclerView.setHasFixedSize(true); // 옵션

		// Linear layout manager 사용
		mainLayoutManager = new LinearLayoutManager(this);
		mainRecyclerView.setLayoutManager(mainLayoutManager);

		// Adapter 셋팅
		mainAdapter = new MainAdapter(mainItemList);
		mainRecyclerView.setAdapter(mainAdapter);


	} // onCreate() 끝

	@Override
	protected void onResume() {
		super.onResume();

		// 등록 눌렀을 때
		reviewRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent reviewRegistIntent = new Intent(MainActivity.this, ReviewRegistActivity.class);
				startActivity(reviewRegistIntent);
				finish();
			}
		});
	} // onResume() 끝

	// 권한 상태 확인 기능
	private boolean hasPermissions(String[] permissions) {

		Log.i(TAG, "hasPermissions : 실행");

		int permsResult;

		for (String perms : permissions) {
			permsResult = ContextCompat.checkSelfPermission(this, perms);
			Log.i(TAG, "hasPermissions 퍼미션 체크중... " + perms);
			if (permsResult == PackageManager.PERMISSION_DENIED) {
				// 허가 안된 퍼미션 발견
				Log.i(TAG, "hasPermissions 허가 안된 퍼미션 발견 : 실행");
				return false;
			}

		} // 반복문 끝

		Log.i(TAG, "hasPermissions 모든 퍼미션 허가  : 실행");
		// 모든 퍼미션 허가됨
		return true;
	} // 권한 상태 확인 기능 끝


	// permission 요청한 결과값 받는 기능
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		Log.i(TAG, "onRequestPermissionsResult : 실행");

		switch (requestCode) {

			case PERMISSIONS_REQUEST_CODE:
				// 퍼미션 요청에 대한 결과값이 0보다 클 때 조건 시작
				if (grantResults.length > 0) {
					boolean cameraPermissionAccepted = grantResults[0]
							== PackageManager.PERMISSION_GRANTED;

					boolean writePermissionAccepted = grantResults[1]
							== PackageManager.PERMISSION_GRANTED;

					if (!cameraPermissionAccepted || !writePermissionAccepted) {
						Log.i(TAG, "onRequestPermissionsResult 퍼미션 허가 해주세요 : 실행");
						showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
						return;
					} else {

						Log.i(TAG, "onRequestPermissionsResult 모든 퍼미션 허가됨 : 실행");
						Toast.makeText(MainActivity.this, "카메라/외부저장소 읽기,쓰기 권한 허가됨", Toast.LENGTH_SHORT);
					}
				} else {
					Log.i(TAG, "onRequestPermissionsResult 퍼미션 요청이 없습니다 : 실행");
					Toast.makeText(MainActivity.this, "퍼미션 요청에 대한 결과값이 없습니다", Toast.LENGTH_SHORT);
				} // 퍼미션 요청에 대한 결과값이 0보다 클 때 조건 끝
				break;
		} // switch() 끝

	} // onRequestPermissionsResult() 끝

	// permission 허가를 위한 다이얼로그 기능
	@TargetApi(Build.VERSION_CODES.M)
	private void showDialogForPermission(String msg) {

		Log.d(TAG, "showDialogForPermission : 실행");

		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("알림");
		builder.setMessage(msg);
		builder.setCancelable(false);
		builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
			}
		});
		builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		});
		builder.create().show();
	} // showDialogForPermission() 끝

} // MainActivity 클래스 끝
