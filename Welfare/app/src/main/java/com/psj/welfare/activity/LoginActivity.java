package com.psj.welfare.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonObject;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;
import com.psj.welfare.R;
import com.psj.welfare.api.ApiService;
import com.psj.welfare.api.RetroClient;
import com.psj.welfare.custom.CustomLoginButton;
import com.psj.welfare.custom.OnSingleClickListener;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * 로그인 액티비티는 SNS 로그인(구글, 네이버)과 너의 혜택은 자체 로그인이 있다
 * 로그인 컨셉 구상중....
 * */
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

	public static final String TAG = "LoginActivity";
	private static String OAUTH_CLIENT_ID = "ut1YBPff_TFRfD6VaCOU";
	private static String OAUTH_CLIENT_SECRET = "NVBvcS2oJn";
	private static String OAUTH_CLIENT_NAME = "네이버로 로그인하기";

	// 네이버 로그인 API 객체
	public static OAuthLoginButton n_oAuthLoginButton;
	public static OAuthLogin n_oAuthLoginInstance;

	// 파이어베이스 인증 객체
	private FirebaseAuth firebaseAuth;
	// 구글 API 클라이언트 객체
	private GoogleApiClient googleApiClient;
	// 구글 로그인 했을 때 결과 코드
	private static final int REQ_SIGN_GOOGLE = 100;
	// 구글 로그인한 유저 토큰 값
	FirebaseUser currentUser;
	// 구글 아이디 토큰 값 저장 변수
	String idToken;
	// 쉐어드를 전역에서 사용할 수 있게 하는 변수
	SharedPreferences preferences;

	public LoginActivity loginContext;


	private String accessToken = "";
	private String refreshToken = "";
	private long expireAt = 0;
	private String tokenType = "";

	private CustomLoginButton btn_NLogin, btn_GLogin, btn_MYLogin;
	private ImageView loginClose;
	private LinearLayout login_layout;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		loginClose = findViewById(R.id.loginClose);
		btn_NLogin = findViewById(R.id.btn_NLogin);
		btn_GLogin = findViewById(R.id.btn_GLogin);
		btn_MYLogin = findViewById(R.id.btn_MYLogin);
		login_layout = findViewById(R.id.login_layout);

		// 파이어베이스 FCM 디바이스 토큰값을 확인하는 로직
		FirebaseInstanceId.getInstance().getInstanceId()
				.addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
					@Override
					public void onComplete(@NonNull Task<InstanceIdResult> task) {
						if (!task.isSuccessful()) {
							Log.w("FCM LOG", "getInstanceId failed", task.getException());
							return;
						}
						String token = task.getResult().getToken();
						Log.d(TAG, "FCM 토큰 : " + token);
						Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();

						// 레트로핏 서버 URL 설정해놓은 객체 생성
						RetroClient retroClient = new RetroClient();
						// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
						ApiService apiService = retroClient.getApiClient().create(ApiService.class);

						// 인터페이스 ApiService에 선언한 fcmToken()를 호출합니다
						Call<String> call = apiService.fcmToken("tkdwns3340@naver.com", token);
						call.enqueue(new Callback<String>() {
							@Override
							public void onResponse(Call<String> call, Response<String> response) {
								if (response.isSuccessful()) {
									Log.i(TAG, "onResponse FCM 토큰 전달 성공 : " + response.body().toString());

								} else {
									Log.i(TAG, "onResponse 실패");
								}
							}

							@Override
							public void onFailure(Call<String> call, Throwable t) {
								Log.i(TAG, "onFailure : " + t.toString());

							}
						}); // call enqueue end
					} // onComplete end
				}); // addOnCompleteListener end

		// LoginActivity context 저장
		loginContext = LoginActivity.this;

		SharedPreferences preferences = getSharedPreferences(getString(R.string.login_shared), MODE_PRIVATE);

		SharedPreferences.Editor editor = preferences.edit();

		editor.remove("state");
		editor.remove("serverToken");
		editor.remove("email");
		editor.remove("snsToken");

		editor.commit();

		// 네이버 로그인 API 초기화
		n_oAuthLoginInstance = OAuthLogin.getInstance();
		n_oAuthLoginInstance.init(loginContext, OAUTH_CLIENT_ID, OAUTH_CLIENT_SECRET, OAUTH_CLIENT_NAME);

		// 구글 로그인 버튼을 눌렀을 때 동작하는 옵션들 설정
		GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build();

		googleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this, this)
				.addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
				.build();

		firebaseAuth = FirebaseAuth.getInstance();

		loginSharedGet();


		// 토큰을 삭제하기 위해서는 네이버 or 구글 서버와 통신해야 하기 때문에 스레드를 사용해야 한다
		// 네이버 로그인 API 클라이언트와 서버 토큰 삭제
//				NaverLogOut naverLogOut = new NaverLogOut();
//				naverLogOut.start();

		// 구글 로그인 API 토큰 삭제
//				GoogleLogOut googleLogOut = new GoogleLogOut();
//				googleLogOut.start();

	} // onCreate end

	class NaverLogOut extends Thread {
		@Override
		public void run() {
			super.run();
			Log.e(TAG, "네이버 로그아웃 스레드 실행!");
			n_oAuthLoginInstance.logoutAndDeleteToken(loginContext);
		}
	}

	class GoogleLogOut extends Thread {
		@Override
		public void run() {
			super.run();
			Log.e(TAG, "구글 로그아웃 스레드 실행!");
			firebaseAuth.getInstance().signOut();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.i(TAG, "onStart 실행!");
		// 구글 서버 토큰 존재하는지 확인
		currentUser = firebaseAuth.getCurrentUser();
		Log.i(TAG, "G_currentUser : " + currentUser);

		// 네이버 서버 토큰 존재하는지 확인
		String accessToken = n_oAuthLoginInstance.getAccessToken(loginContext);
		Log.i(TAG, "N_accessToken : " + accessToken);
	} // onStart end

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume start");

		// X 버튼 클릭
		loginClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "loginClose click");
				Intent l_Intent = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(l_Intent);
				finish();
			}
		});

		// 구글 로그인 버튼 클릭
		btn_GLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "btn_GLogin click");
				Intent l_intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
				startActivityForResult(l_intent, REQ_SIGN_GOOGLE);
			}
		});


		// 니모 로그인 버튼 클릭
		btn_MYLogin.setOnClickListener(new OnSingleClickListener() {
			@Override
			public void onSingleClick(View v) {
				Log.i(TAG, "btn_MYLogin click");
				Intent l_intent = new Intent(LoginActivity.this, MYLoginActivity.class);
				startActivity(l_intent);
				finish();
			}
		});

	} // onResume end

	@Override
	protected void onDestroy() {
		super.onDestroy();
	} // onDestroy end

	/* 구글 로그인 API 관련 로직 ↓ */
	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

	}

	private void resultLogin(final GoogleSignInAccount account) {
		AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
		firebaseAuth.signInWithCredential(authCredential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						// 로그인이 성공했을 때
						if (task.isSuccessful()) {

							Log.i(TAG, "구글 로그인 성공");
							String FamilyName = account.getFamilyName();
							String DisplayName = account.getDisplayName();
							String GivenName = account.getGivenName();
							final String email = account.getEmail();
							Log.i(TAG, "FamilyName : " + FamilyName + "\tDisplayName : " + DisplayName + "\tGivenName : " + GivenName + "\t이메일 : " + email);
							idToken = account.getIdToken();
							// 레트로핏 서버 URL 설정해놓은 객체 생성
							RetroClient retroClient = new RetroClient();
							// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
							ApiService apiService = retroClient.getApiClient().create(ApiService.class);
							Log.e(TAG, "account.getIdToken() -> " + idToken);
							// 인터페이스 ApiService에 선언한 googleUser()를 호출합니다
							Call<String> call = apiService.googleUser("GOOGLE", email, DisplayName, idToken);
							call.enqueue(new Callback<String>() {
								@Override
								public void onResponse(Call<String> call, Response<String> response) {
									if (response.isSuccessful()) {
										Log.i(TAG, "onResponse 구글 성공 : " + response.body().toString());
										String googleInfo = response.body().toString();
										// 구글 sns 로그인을 이미 시도 했는지 아니면 처음으로 sns 로그인을 하는지 판단하기 위한 로직
										SharedPreferences preferences = getSharedPreferences(getString(R.string.login_shared), MODE_PRIVATE);

										String result_server = preferences.getString("serverToken", "NoServerToken");
										String result_state = preferences.getString("state", "NoState");
										String result_sns = preferences.getString("snsToken", "NoSNSToken");
										String result_email = preferences.getString("email", "NoEmail");

										if (result_server.equals("NoServerToken") && result_state.equals("1")) {
											Log.e(TAG, "and_login.php 전송");
											// 이미 구글 sns 로그인을 시도해서 and_login.php 경로로 전송하는 로직
											reLogin(email, idToken);
											SharedPreferences.Editor editor = preferences.edit();
											editor.putString("snsToken", idToken);
											editor.putString("email", email);
											editor.commit();
										} else {
											Log.e(TAG, "and_register.php 전송");
											// 처음으로 구글 sns 로그인을 시도해서 and_register.php 경로로 전송하는 로직
											jsonParsing(googleInfo, email);

											Intent intent = new Intent(LoginActivity.this, MainActivity.class);
											startActivity(intent);
											finish();
										}


									} else {
										Log.i(TAG, "onResponse 실패");
									}
								}

								@Override
								public void onFailure(Call<String> call, Throwable t) {
									Log.i(TAG, "onFailure : " + t.toString());

								}
							}); // call enqueue end

						} else { // 로그인이 실패했을 때
							Log.i(TAG, "로그인 실패");
						}
					}
				});
	} // resultLogin end

	// 구글 로그인 인증을 요청 했을 때 결과 값을 되돌려 받는 곳
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// 구글 로그인 액티비티에서 넘어온 경우일 때 실행
		if (requestCode == REQ_SIGN_GOOGLE) {
			GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			// 인증결과가 성공적일 때
			if (googleSignInResult.isSuccess()) {
				// googleSignInAccount 라는 데이터는 구글로그인 정보를 담고있습니다 (닉네임, 프로필사진Url, 이메일주소 ..등)
				GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
				// 로그인 결과 값 출력 수행 기능
				resultLogin(googleSignInAccount);

			}

		}
	} // onActivityResult end


	// 로그인 후 서버에서 난독화된 토큰값을 쉐어드에 저장하는 기능
	// 난독화된 토큰값을 쉐어드에 저장하는 이유
	// 유저 정보를 알아내어 서비스를 이용하는 것을 막기 위함
	// 난독화된 토큰값이 존재하면 로그인 상태인지 비로그인 상태인지 체크할 수 있다
	private void loginSharedSave(String serverToken, String email) {
		preferences = getSharedPreferences(getString(R.string.login_shared), MODE_PRIVATE);

		// 데이터를 저장 or 편집하기 위해 Editor 변수를 선언한다
		SharedPreferences.Editor editor = preferences.edit();
		Log.e(TAG, "구글 서버 토큰 값 : " + idToken);
		Log.e(TAG, "네이버 서버 토큰 값 : " + n_oAuthLoginInstance.getAccessToken(this));
		if (idToken != null) {
			editor.putString("snsToken", idToken);
		} else {
			editor.putString("snsToken", n_oAuthLoginInstance.getAccessToken(this));
		}
		// key 값과 연결되는 value 값을 저장한다

		editor.putString("serverToken", serverToken);
		editor.putString("email", email);
		editor.putString("state", "1");

		// 메모리에 있는 데이터를 저장장치에 저장한다
		editor.commit();

	}

	// 로그인 상태를 확인하기 위한 쉐어드에 저장된 토큰값 불러오기 기능
	private void loginSharedGet() {
		SharedPreferences preferences = getSharedPreferences(getString(R.string.login_shared), MODE_PRIVATE);

		String result_sns = preferences.getString("snsToken", "NoSNSToken");
		String result_email = preferences.getString("email", "NoEmail");
		String result_server = preferences.getString("serverToken", "NoServerToken");
		String result_state = preferences.getString("state", "NoState");
		Log.e(TAG, "loginSharedGet : " + result_server);

		if (result_server.equals("NoServerToken") && result_sns.equals("NoSNSToken") && result_email.equals("NoEmail") && result_state.equals("NoState")) {
			Log.e(TAG, "첫 로그인을 진행해주세요!");
		} else if (!result_server.equals("NoServerToken") && !result_email.equals("NoEmail")) {
			Log.e(TAG, "로그인 되어있는 사용자 입니다!");
			tokenCheck(result_email, result_server);



			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		} else if (result_server.equals("NoServerToken") && result_state.equals("1")) {
			Log.e(TAG, "로그아웃 한 사용자 입니다!\n서버 토큰 값을 다시 요청해야 합니다!");
		}
	} // loginSharedGet end

	// 로그인 응답 내용이 json 구조라서 파싱을 하기 위한 기능
	private void jsonParsing(String login, String email) {

		try {
			JSONObject login_total = new JSONObject(login);
			String retCode_data, token_data;

			retCode_data = login_total.getString("retCode");
			Log.i(TAG, "retCode 내용 : " + retCode_data);

			if (retCode_data.equals("0")) {
				token_data = login_total.getString("token");
				Log.i(TAG, "token 내용 : " + token_data);

				loginSharedSave(token_data, email);


			} else if (retCode_data.equals("406")) {
				Log.i(TAG, "이미 로그인한 상태입니다!");
			}


		} catch (JSONException e) {
			e.printStackTrace();
		}
	} // jsonParsing end

	public void reLogin(String email, String token) {
		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);

		Log.e(TAG, "이메일 : " + email + "\n토큰 : " + token);
		// 인터페이스 ApiService에 선언한 ReUser()를 호출합니다
		Call<String> call = apiService.ReUser(email, token);
		call.enqueue(new Callback<String>() {
			@Override
			public void onResponse(Call<String> call, Response<String> response) {
				if (response.isSuccessful()) {
					Log.i(TAG, "onResponse 재로그인 성공 : " + response.body().toString());
					String relogin = response.body().toString();
					try {
						JSONObject login_total = new JSONObject(relogin);
						String token_data;
						token_data = login_total.getString("token");

						SharedPreferences preferences = getSharedPreferences(getString(R.string.login_shared), MODE_PRIVATE);

						// 데이터를 저장 or 편집하기 위해 Editor 변수를 선언한다
						SharedPreferences.Editor editor = preferences.edit();
						editor.putString("serverToken", token_data);

						editor.commit();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					startActivity(intent);
					finish();

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

	public void tokenCheck(String email, String token) {
		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);

		Log.e(TAG, "이메일 : " + email + "\n토큰 : " + token);
		// 인터페이스 ApiService에 선언한 tokenCheck()를 호출합니다
		Call<String> call = apiService.tokenCheck(email, token);
		call.enqueue(new Callback<String>() {
			@Override
			public void onResponse(Call<String> call, Response<String> response) {
				if (response.isSuccessful()) {
					Log.i(TAG, "onResponse tokenCheck 성공 : " + response.body().toString());
//					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//					startActivity(intent);
//					finish();

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

} // LoginActivity class end