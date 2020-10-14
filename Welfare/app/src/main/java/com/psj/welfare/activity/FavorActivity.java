package com.psj.welfare.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.psj.welfare.R;
import com.psj.welfare.api.ApiService;
import com.psj.welfare.api.RetroClient;
import com.psj.welfare.custom.OnSingleClickListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
* 관심사 설정 액티비티는 원하는 혜택 알림을 받기 위해서 사용자가 체크해야 한다
* 원하는 혜택을 체크하면 사용자는 해당되는 혜택을 FCM 알림으로 받을 수 있다
* */
public class FavorActivity extends AppCompatActivity {

	public static final String TAG = "FavorActivity";
	TextView favor_student, favor_old, favor_child;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settingfavor);
		Log.i(TAG, "onCreate 실행");

		favor_student = findViewById(R.id.favor_student);
		favor_old = findViewById(R.id.favor_old);
		favor_child = findViewById(R.id.favor_child);

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

		// 관심사 설정을 클릭하면 사용자가 관심사를 선택하는 화면을 제공하고
		// 선택완료가 되면 사용자 계정과 관심사를 서버에 전송한다
		// 알림 기능의 활용하는 정보이다

		favor_student.setOnClickListener(new OnSingleClickListener() {
			@Override
			public void onSingleClick(View v) {
				// 학생·청년 선택
				// 레트로핏 서버 URL 설정해놓은 객체 생성
				RetroClient retroClient = new RetroClient();
				// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
				ApiService apiService = retroClient.getApiClient().create(ApiService.class);

				// 인터페이스 ApiService에 선언한 UserFavor()를 호출합니다
				Call<String> call = apiService.UserFavor("tkdwns3340@naver.com","다문화");
				call.enqueue(new Callback<String>() {
					@Override
					public void onResponse(Call<String> call, Response<String> response) {
						if (response.isSuccessful()) {
							Log.i(TAG, "onResponse 성공 : " + response.body().toString());


						} else {
							Log.i(TAG, "onResponse 실패");
						}
					}

					@Override
					public void onFailure(Call<String> call, Throwable t) {
						Log.i(TAG, "onFailure : " + t.toString());

					}
				}); // call enqueue end
			}
		});

		favor_child.setOnClickListener(new OnSingleClickListener() {
			@Override
			public void onSingleClick(View v) {
				// 아기·어린이 선택

			}
		});

		favor_old.setOnClickListener(new OnSingleClickListener() {
			@Override
			public void onSingleClick(View v) {
				// 중장년·노인 선택

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
