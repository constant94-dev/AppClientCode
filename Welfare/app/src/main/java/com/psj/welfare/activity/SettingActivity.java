package com.psj.welfare.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.psj.welfare.R;
import com.psj.welfare.api.ApiService;
import com.psj.welfare.api.RetroClient;
import com.psj.welfare.custom.OnSingleClickListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends AppCompatActivity {

	public static final String TAG = "SettingActivity";
	TextView setting_favor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		Log.i(TAG, "onCreate 실행");

		setting_favor = findViewById(R.id.setting_favor);

		// 로그인 상태 확인을 위해 쉐어드의 저장된 토큰 값 불러오기 기능
		loginSharedGet();
	} // onCreate end

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart 실행");

	} // onStart end

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume 실행");

		setting_favor.setOnClickListener(new OnSingleClickListener() {
			@Override
			public void onSingleClick(View v) {
				// 관심사 설정을 클릭하면 사용자가 관심사를 선택하는 화면을 제공하고
				// 선택완료가 되면 사용자 계정과 관심사를 서버에 전송한다
				// 알림 기능의 활용하는 정보이다
				Intent setting_intent = new Intent(SettingActivity.this, FavorActivity.class);
				startActivity(setting_intent);
				finish();
			}
		});

	} // onResume end

	// 로그인 상태를 확인하기 위한 쉐어드에 저장된 토큰값 불러오기 기능
	private void loginSharedGet() {
		SharedPreferences preferences = getSharedPreferences(getString(R.string.login_shared), MODE_PRIVATE);

		String result = preferences.getString("serverToken", "NoToken");
		Log.e(TAG, "loginSharedGet : " + result);


	} // loginSharedGet end


} // ProfileActivity class end
