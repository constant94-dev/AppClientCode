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

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
* 프로필 액티비티는 사용자가 회원탈퇴 or 로그인/로그아웃 상태 체크해야 한다
* 프로필 정보 필요한 것 구상중....
* */
public class ProfileActivity extends AppCompatActivity {

	public static final String TAG = "ProfileActivity";

	private ImageView arrow_left;
	private TextView profile_login;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		Log.i(TAG, "onCreate 실행");

		arrow_left = findViewById(R.id.arrow_left);
		profile_login = findViewById(R.id.profile_login);


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

		arrow_left.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "뒤로가기 클릭!");
				Intent p_intent = new Intent(ProfileActivity.this, MainActivity.class);
				startActivity(p_intent);
				finish();
			}
		});

		profile_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				String state = profile_login.getText().toString();
				if (state.equals("로그인")) {
					Log.i(TAG, "로그인 클릭!");
					Intent p_intent = new Intent(ProfileActivity.this, LoginActivity.class);
					startActivity(p_intent);
					finish();
				} else {
					Log.i(TAG, "로그아웃 클릭!");
					loginSharedDelete();
					profile_login.setText("로그인");
				}
			}
		});


	} // onResume end

	// 로그인 상태를 확인하기 위한 쉐어드에 저장된 토큰값 불러오기 기능
	private void loginSharedGet() {
		SharedPreferences preferences = getSharedPreferences(getString(R.string.login_shared), MODE_PRIVATE);

		String result = preferences.getString("serverToken", "NoToken");
		Log.e(TAG, "loginSharedGet : " + result);

		if (result.equals("NoToken")) {
			Log.e(TAG, "로그인을 진행해주세요  : " + result);
			profile_login.setText("로그인");
		} else {
			Log.e(TAG, "로그인이 되어있네요 : " + result);
			profile_login.setText("로그아웃");

		}
	} // loginSharedGet end

	public void loginSharedDelete() {
		SharedPreferences preferences = getSharedPreferences(getString(R.string.login_shared), MODE_PRIVATE);

		String result_server = preferences.getString("serverToken", "NoServerToken");
		String result_email = preferences.getString("email", "NoEmail");

		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);

		Log.e(TAG, "이메일 : " + result_email + "\n서버의 삭제를 요청할 토큰 : " + result_server);
		// 인터페이스 ApiService에 선언한 logoutUser()를 호출합니다
		Call<String> call = apiService.logoutUser(result_email, result_server);
		call.enqueue(new Callback<String>() {
			@Override
			public void onResponse(Call<String> call, Response<String> response) {
				if (response.isSuccessful()) {
					Log.i(TAG, "onResponse 로그아웃 성공 : " + response.body().toString());


				} else {
					Log.i(TAG, "onResponse 실패");
				}
			}

			@Override
			public void onFailure(Call<String> call, Throwable t) {
				Log.i(TAG, "onFailure : " + t.toString());

			}
		}); // call enqueue end

		SharedPreferences.Editor editor = preferences.edit();
		Log.e(TAG, "클라이언트 로그아웃 처리를 위해서 클라이언트 토큰 삭제!");
		editor.remove("serverToken");

		editor.commit();
	} // loginSharedDelete() end


} // ProfileActivity class end
