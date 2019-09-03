package com.psj.accommodation.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.psj.accommodation.Data.Join;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MailAuthActivity extends AppCompatActivity {

	public static final String TAG = "MailAuthActivity";
	EditText MailAuth;
	Button MailAuthBtn;
	String UserEmail;
	String ResponseMailAuth;
	int AuthIndex;
	AlertDialog alertDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mailauth);

		MailAuth = findViewById(R.id.MailAuth);
		MailAuthBtn = findViewById(R.id.MailAuthBtn);

		Intent mailIntent = getIntent();
		if (mailIntent != null) {
			UserEmail = mailIntent.getStringExtra("UserEmail");
			Log.i("이메일", "" + UserEmail);
		} else {
			Toast.makeText(MailAuthActivity.this, "전달된 값없음", Toast.LENGTH_SHORT);
		}

		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);


		// 인터페이스 ApiService에 선언한 mailAuth()를 호출합니다
		Call<String> call = apiService.mailAuth(UserEmail);

		// onResponse() 메서드를 이용해 응답을 전달받아서 요청에 대한 결과를 받을 수 있습니다
		call.enqueue(new Callback<String>() {
			@Override
			public void onResponse(Call<String> call, Response<String> response) {

				Toast.makeText(MailAuthActivity.this, "서버에서 받은 응답 값 : " + response.body(), Toast.LENGTH_LONG).show();
				Log.i(TAG, "서버에서 받은 응답 값 : " + response.body().toString());
				AuthIndex = response.body().length();
				Log.i("응답받은 문자열 전체 길이 : ", "" + AuthIndex);
				ResponseMailAuth = response.body().substring(AuthIndex - 10, AuthIndex);
				Log.i("분리한 문자열 마지막 10자리 : ", "" + ResponseMailAuth);
			}

			// 응답 실패 (네트워크 오류가 발생했을 때 등)
			@Override
			public void onFailure(Call<String> call, Throwable throwable) {

				Log.i("onFailure", "" + throwable);
				Toast.makeText(MailAuthActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();

			}
		});
	} // onCreate() 끝

	@Override
	protected void onResume() {
		super.onResume();

		MailAuthBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.i("사용자가 입력한 인증코드 : ", "" + MailAuth.getText().toString());

				String authResult = MailAuth.getText().toString();
				Log.i("사용자가 입력한 인증코드 변수저장 값 : ", "" + authResult);
				if (ResponseMailAuth.equals(authResult)) {
					successMail();
				} else if (!(ResponseMailAuth.equals(authResult))) {
					failMail();
				}
			}
		});


	} // onResume() 끝

	// 메일 인증 성공했을 때 알림 기능
	public void successMail() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MailAuthActivity.this);

		builder.setTitle("메일 인증").setMessage("이메일 인증이 완료되었습니다");
		builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent joinIntent = new Intent(MailAuthActivity.this, JoinActivity.class);
				joinIntent.putExtra("SuccessEmail", UserEmail);
				startActivity(joinIntent);
				finish();

			} // onClick() 확인 끝
		}); // setPositiveButton() 끝

		alertDialog = builder.create();

		alertDialog.show();

	} // successMail() 끝


	// 메일 인증 실패했을 때 알림 기능
	public void failMail() {
		AlertDialog.Builder builder = new AlertDialog.Builder(MailAuthActivity.this);

		builder.setTitle("메일 인증").setMessage("인증코드가 틀렸습니다");
		builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(MailAuthActivity.this, "다시 입력해주세요", Toast.LENGTH_SHORT).show();
			}
		});

		alertDialog = builder.create();

		alertDialog.show();

	}


}
