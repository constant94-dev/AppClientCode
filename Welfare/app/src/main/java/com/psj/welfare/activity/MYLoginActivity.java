package com.psj.welfare.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;
import com.psj.welfare.R;
import com.psj.welfare.api.ApiService;
import com.psj.welfare.api.RetroClient;
import com.psj.welfare.custom.CustomLoginButton;
import com.psj.welfare.custom.OnSingleClickListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
* 너의 혜택은 자체 로그인 액티비티는 사용자의 정보를 입력받아야 한다
* 자체 로그인 컨셉은 구상중.....
* */
public class MYLoginActivity extends AppCompatActivity {

	public static final String TAG = "MYLoginActivity";

	private ImageView password_icon, myLogin_close;
	private EditText password_input;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mylogin);
		Log.i(TAG, "onCreate 실행!");

		password_icon = findViewById(R.id.password_icon);
		password_input = findViewById(R.id.password_input);
		myLogin_close = findViewById(R.id.myLogin_close);
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

		// X 이미지 클릭
		myLogin_close.setOnClickListener(new OnSingleClickListener() {
			@Override
			public void onSingleClick(View v) {
				Log.i(TAG, "X 이미지 클릭!");
				Intent my_intent = new Intent(MYLoginActivity.this, LoginActivity.class);
				startActivity(my_intent);
				finish();
			}
		});

		// 비밀번호 숨기기 / 보이기 클릭
		password_icon.setOnClickListener(new OnSingleClickListener() {
			@Override
			public void onSingleClick(View v) {
				Log.i(TAG, "비밀번호 숨기기 클릭!");

				if (password_icon.getTag().equals("0")) { // 비밀번호 안 보이는 상황
					password_icon.setTag("1");
					password_icon.setImageResource(R.drawable.pw_visible_on); // 비밀번호 켜져있는 아이콘
					password_input.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					password_input.setTypeface(Typeface.createFromAsset(getAssets(), "millenniumbatang_regular.ttf"));
				} else { // 비밀번호 보이는 상황
					password_icon.setTag("0");
					password_icon.setImageResource(R.drawable.pw_visible_off); // 비밀번호 꺼져있는 아이콘
					password_input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					password_input.setTypeface(Typeface.createFromAsset(getAssets(), "millenniumbatang_regular.ttf"));
				} // if else end
				// 커서는 맨뒤로
				password_input.setSelection(password_input.getText().length());
			}
		});

	} // onResume end

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy 실행!");

	} // onDestroy end

} // MYLoginActivity class end
