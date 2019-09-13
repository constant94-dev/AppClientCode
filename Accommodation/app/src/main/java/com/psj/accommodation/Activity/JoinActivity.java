package com.psj.accommodation.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.psj.accommodation.Data.Join;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// TODO 회원가입 화면
public class JoinActivity extends AppCompatActivity {

	public static final String TAG = "JoinActivity";

	EditText UserName, UserEmail, UserPassword;
	Button JoinResultBtn, MailAccessBtn;
	AlertDialog alertDialog;
	Join joinData;
	String InputEmail, SuccessEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join);

		UserEmail = findViewById(R.id.UserEmail);
		UserPassword = findViewById(R.id.UserPassword);
		UserName = findViewById(R.id.UserName);
		MailAccessBtn = findViewById(R.id.MailAccessBtn);
		JoinResultBtn = findViewById(R.id.JoinResultBtn);

		Bundle extras = getIntent().getExtras();

		// 이메일 인증 성공하고 받는 SuccessEmail
		if (extras == null) {
			SuccessEmail = "값없음";
			Log.i("이메일", "" + SuccessEmail);

		} else {
			SuccessEmail = extras.getString("SuccessEmail");
			Log.i("이메일", "" + SuccessEmail);
			UserEmail.setText(SuccessEmail);
			UserEmail.setEnabled(false);
			MailAccessBtn.setText("인증 완료");
			MailAccessBtn.setEnabled(false);

		}


	} // onCreate() 끝

	@Override
	protected void onResume() {
		super.onResume();

		// 이메일 키입력 이벤트
		UserEmail.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				// 이메일 입력 값을 전역변수로 공유하기 위해서
				InputEmail = UserEmail.getText().toString();
			}
		});

		// 이메일 인증 버튼 클릭 이벤트
		MailAccessBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "" + InputEmail);

				// 이메일 입력 값 체크 && 이메일 형식 체크 조건 시작
				if (InputEmail != null && android.util.Patterns.EMAIL_ADDRESS.matcher(InputEmail).matches()) {

					// 레트로핏 서버 URL 설정해놓은 객체 생성
					RetroClient retroClient = new RetroClient();
					// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
					ApiService apiService = retroClient.getApiClient().create(ApiService.class);

					// 인터페이스 ApiService에 선언한 checkEmail()를 호출합니다
					Call<String> call = apiService.checkEmail(InputEmail);

					// onResponse() 메서드를 이용해 응답을 전달받아서 요청에 대한 결과를 받을 수 있습니다
					call.enqueue(new Callback<String>() {
						@Override
						public void onResponse(Call<String> call, Response<String> response) {

							Toast.makeText(JoinActivity.this, "서버에서 받은 응답 값 : " + response.body(), Toast.LENGTH_LONG).show();

							if (response.body().equals("계정존재")) {
								failMail();
							} else if (response.body().equals("계정없음")) {
								successMail();
							} else if (response.body().equals("POST값없음")) {
								Log.i(TAG, "POST값없음");
								noData();
							} else if (response.body().equals("SQL값없음")) {
								successMail();
							}

						}

						// 응답 실패 (네트워크 오류가 발생했을 때 등)
						@Override
						public void onFailure(Call<String> call, Throwable throwable) {

							Log.i("onFailure", "" + throwable);
							Toast.makeText(JoinActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();

						}

					}); // enqueue() 끝

				} else {
					mailType();
				} // 이메일 입력 값 체크 && 이메일 형식 체크 조건 끝

			} // onClick() 끝
		}); // MailAccessBtn setOnClickListener() 끝


		// 회원가입 버튼 클릭을 했을 때 사용자가 입력한 데이터를 MySQL DB에 삽입하기 위한 http 요청을 처리할 것입니다
		JoinResultBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// 사용자가 입력한 데이터를 임시저장 하기위한 객체
				joinData = new Join();
				joinData.setName(UserName.getText().toString());
				joinData.setEmail(UserEmail.getText().toString());
				joinData.setPassword(UserPassword.getText().toString());

				// 레트로핏 서버 URL 설정해놓은 객체 생성
				RetroClient retroClient = new RetroClient();
				// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
				ApiService apiService = retroClient.getApiClient().create(ApiService.class);

				// 메일 인증 성공했을 때 데이터베이스 INSERT 할 수 있는 조건 시작
				if (MailAccessBtn.getText().toString().equals("인증 완료")) {
					// 인터페이스 ApiService에 선언한 inserData()를 호출합니다
					Call<String> call = apiService.signUpUser(joinData.getEmail(), joinData.getPassword(), joinData.getName());

					// onResponse() 메서드를 이용해 응답을 전달받아서 요청에 대한 결과를 받을 수 있습니다
					call.enqueue(new Callback<String>() {
						@Override
						public void onResponse(Call<String> call, Response<String> response) {


							Toast.makeText(JoinActivity.this, "서버에서 받은 응답 값 : " + response.body().toString(), Toast.LENGTH_LONG).show();

							if (response.body().equals("계정생성")) {
								Log.i(TAG, "계정생성");
								successSign();
							} else if (response.body().equals("계정생성실패")) {
								Log.i(TAG, "계정생성실패");
								failSign();
							} else if (response.body().equals("POST값없음")) {
								Log.i(TAG, "POST값없음");
								noData();
							}

						} // onResponse() 끝

						// 응답 실패 (네트워크 오류가 발생했을 때 등)
						@Override
						public void onFailure(Call<String> call, Throwable throwable) {

							Log.i("onFailure", "" + throwable);
							Toast.makeText(JoinActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();

						} // onFailure() 끝
					}); // enqueue() 끝
				} else {
					mailing();
				} // 메일 인증 성공했을 때 데이터베이스 INSERT 할 수 있는 조건 끝
			} // onClick() 끝
		}); // JoinResultBtn setOnClickListener() 끝


	} // onResume() 끝

	// 이메일 사용가능할 때 알림 기능
	public void successMail() {
		AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);

		builder.setTitle("메일 인증").setMessage("사용할 수 있는 이메일입니다");
		builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent mailAuthIntent = new Intent(JoinActivity.this, MailAuthActivity.class);
				mailAuthIntent.putExtra("UserEmail", InputEmail);
				startActivity(mailAuthIntent);
				finish();
			}
		});

		alertDialog = builder.create();

		alertDialog.show();

	}


	// 이메일 사용불가능할 때 알림 기능
	public void failMail() {
		AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);

		builder.setTitle("메일 인증").setMessage("존재하는 이메일입니다");
		builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(JoinActivity.this, "다시 입력해주세요", Toast.LENGTH_SHORT).show();
			}
		});

		alertDialog = builder.create();

		alertDialog.show();

	}

	// 회원가입 성공했을 때 알림 기능
	public void successSign() {
		AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);

		builder.setTitle("계정 생성").setMessage("회원가입을 축하드립니다");
		builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent loginIntent = new Intent(JoinActivity.this, LoginActivity.class);
				startActivity(loginIntent);
				finish();
			}
		});

		alertDialog = builder.create();

		alertDialog.show();

	}


	// 회원가입 실패했을 때 알림 기능
	public void failSign() {
		AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);

		builder.setTitle("계정 생성").setMessage("회원가입이 실패했습니다");
		builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(JoinActivity.this, "다시 입력해주세요", Toast.LENGTH_SHORT).show();
			}
		});

		alertDialog = builder.create();

		alertDialog.show();

	}

	// 회원가입 입력을 완료하지 못했을 때 알림 기능
	public void noData() {
		AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);

		builder.setTitle("계정 생성").setMessage("입력을 완료해주세요");
		builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(JoinActivity.this, "다시 입력해주세요", Toast.LENGTH_SHORT).show();
			}
		});

		alertDialog = builder.create();

		alertDialog.show();
	}

	// 이메일 형식이 올바르지 않을 때 알림 기능
	public void mailType() {
		AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);

		builder.setTitle("메일 인증").setMessage("올바르지 않은 이메일 형식입니다");
		builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(JoinActivity.this, "다시 입력해주세요", Toast.LENGTH_SHORT).show();
			}
		});

		alertDialog = builder.create();

		alertDialog.show();
	}

	// 이메일 인증 받지 않았을 때 알림 기능
	public void mailing() {
		AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);

		builder.setTitle("메일 인증").setMessage("메일 인증을 진행해주세요");
		builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(JoinActivity.this, "메일 인증 진행하세요", Toast.LENGTH_SHORT).show();
			}
		});

		alertDialog = builder.create();

		alertDialog.show();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (alertDialog != null) {
			alertDialog.dismiss();
		}


	}
} // JoinActivity 클래스 끝
