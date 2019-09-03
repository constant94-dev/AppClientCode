package com.psj.accommodation.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.psj.accommodation.Data.Join;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// TODO 로그인 화면
public class LoginActivity extends AppCompatActivity {

	EditText LoginEmail, LoginPassword;
	Button LoginBtn, JoinBtn;
	AlertDialog alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		LoginBtn = findViewById(R.id.LoginBtn);
		JoinBtn = findViewById(R.id.JoinBtn);
		LoginEmail = findViewById(R.id.LoginEmail);
		LoginPassword = findViewById(R.id.LoginPassword);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// 로그인 버튼 클릭
		LoginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Join joinData = new Join();
				joinData.setEmail(LoginEmail.getText().toString());
				joinData.setPassword(LoginPassword.getText().toString());

				// 레트로핏 서버 URL 설정해놓은 객체 생성
				RetroClient retroClient = new RetroClient();
				// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
				ApiService apiService = retroClient.getApiClient().create(ApiService.class);

				// 인터페이스 ApiService에 선언한 checkUser()를 호출합니다
				Call<String> call = apiService.loginUser(joinData.getEmail(), joinData.getPassword());

				// onResponse() 메서드를 이용해 응답을 전달받아서 요청에 대한 결과를 받을 수 있습니다
				call.enqueue(new Callback<String>() {
					@Override
					public void onResponse(Call<String> call, Response<String> response) {

						Toast.makeText(LoginActivity.this, "서버에서 받은 응답 값 : " + response.body().toString(), Toast.LENGTH_LONG).show();

						if (response.body().equals("계정있어!")) {
							successLogin();
						} else if (response.body().equals("계정없어!")) {
							failEmail();
						} else if (response.body().equals("비밀번호없어!")) {
							failPassword();
						} else if (response.body().equals("POST값없어!")) {
							noData();
						} else if (response.body().equals("SQL값없어!")) {
							noUser();
						}

					}

					// 응답 실패 (네트워크 오류가 발생했을 때 등)
					@Override
					public void onFailure(Call<String> call, Throwable throwable) {

						Log.i("onFailure", "" + throwable);
						Toast.makeText(LoginActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();

					}
				}); // enqueue() 끝

			}
		});

		// 회원가입 버튼 클릭
		JoinBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent joinIntent = new Intent(LoginActivity.this, JoinActivity.class);
				startActivity(joinIntent);
				finish();
			}
		});

	} // onResume() 끝

	// 로그인 성공했을 때 알림 기능
	public void successLogin() {
		AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

		builder.setTitle("").setMessage("로그인 되었습니다");
		builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(loginIntent);
				finish();
			}
		});

		alertDialog = builder.create();

		alertDialog.show();

	}


	// 이메일 입력 실패했을 때 알림 기능
	public void failEmail() {
		AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

		builder.setTitle("").setMessage("잘못된 이메일입니다");
		builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(LoginActivity.this, "다시 입력해주세요", Toast.LENGTH_SHORT).show();
			}
		});

		alertDialog = builder.create();

		alertDialog.show();

	}

	// 비밀번호 입력 실패했을 때 알림 기능
	public void failPassword() {
		AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

		builder.setTitle("").setMessage("잘못된 비밀번호입니다");
		builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(LoginActivity.this, "다시 입력해주세요", Toast.LENGTH_SHORT).show();
			}
		});

		alertDialog = builder.create();

		alertDialog.show();

	}

	// 계정이 없을 때 알림 기능
	public void noUser() {
		AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

		builder.setTitle("").setMessage("계정을 만들어주세요");
		builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(LoginActivity.this, "다시 입력해주세요", Toast.LENGTH_SHORT).show();
			}
		});

		alertDialog = builder.create();

		alertDialog.show();
	}

	// 입력을 완료하지 않았을 때 알림 기능
	public void noData() {
		AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

		builder.setTitle("").setMessage("입력을 완료해주세요");
		builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(LoginActivity.this, "다시 입력해주세요", Toast.LENGTH_SHORT).show();
			}
		});

		alertDialog = builder.create();

		alertDialog.show();
	}


} // LoginActivity 클래스 끝