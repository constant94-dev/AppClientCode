package com.psj.welfare.activity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nhn.android.naverlogin.OAuthLogin;
import com.psj.welfare.Data.WelfareCaseItem;
import com.psj.welfare.Data.WelfareNewsItem;
import com.psj.welfare.Data.WelfareFavorItem;
import com.psj.welfare.R;
import com.psj.welfare.adapter.WelfareCaseAdapter;
import com.psj.welfare.adapter.WelfareNewsAdapter;
import com.psj.welfare.adapter.WelfareFlavorAdapter;

import java.util.ArrayList;

/*
 * 메인 테스트 액티비티는 구현해 놓은 메인 액티비티를 수정이 필요할 때
 * 수정된 화면을 미리보기 할 때 사용한다
 * */
public class MainTestActivity extends AppCompatActivity {

	public static final String TAG = "MainTestActivity";

	private RecyclerView m_NewsRecyclerView;
	private RecyclerView.Adapter m_NewsAdapter;

	private RecyclerView m_CaseRecyclerView;
	private RecyclerView.Adapter m_CaseAdapter;

	private RecyclerView m_FavorRecyclerView;
	private RecyclerView.Adapter m_FavorAdapter;

	int position_News = 0;
	int position_Case = 0;
	int position_favor = 0;
	String accessToken = "";
	String platform = "";

	private DrawerLayout drawerLayout;
	private ScrollView m_ScrollView;
	private LinearLayout scroll_top, profile_line;
	private View drawerView;
	private ImageView menu_img;
	private Button welfare_go;


	// 메인 아이템 리스트
	private ArrayList<WelfareNewsItem> m_NewsList;
	private ArrayList<WelfareCaseItem> m_CaseList;
	private ArrayList<WelfareFavorItem> m_FavorList;
	MainTestActivity mainTestActivity;
	AlertDialog.Builder alertDialogBuilder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maintest);

		if (getIntent() != null) {
			Intent mainIntent = getIntent();
			platform = mainIntent.getStringExtra("PlatForm");
		} else {
			Log.i(TAG, "인텐트 전달 받은 값이 없어요!");
		}



		scroll_top = findViewById(R.id.scroll_top);
		m_ScrollView = findViewById(R.id.m_ScrollView);
		profile_line = findViewById(R.id.profile_line);
		drawerView = (View) findViewById(R.id.drawerView);
		menu_img = findViewById(R.id.menu_img);



		// ArrayList 객체 생성
		m_CaseList = new ArrayList<>();
		m_NewsList = new ArrayList<>();
		m_FavorList = new ArrayList<>();



		alertDialogBuilder = new AlertDialog.Builder(this);

		// 제목 세팅
		alertDialogBuilder.setTitle("로그아웃");

		// 다이얼로그 세팅
		alertDialogBuilder
				.setMessage("로그아웃 되었습니다")
				.setCancelable(false)
				.setPositiveButton("확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

	} // onCreate end

	@Override
	protected void onStart() {
		super.onStart();

		LoginActivity loginActivity = new LoginActivity();

		// 구글 로그인되어 있는지 확인
		FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
		Log.i(TAG, "currentUser : " + currentUser);


		// 네이버 로그인되어 있는지 확인

		String n_Token = OAuthLogin.getInstance().getAccessToken(mainTestActivity);
		Log.i(TAG, "naverToken : " + n_Token);


	} // onStart end

	@Override
	protected void onResume() {
		super.onResume();

		menu_img.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawerLayout.openDrawer(drawerView);
			}
		});

		welfare_go.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "혜택 보러가기 클릭!");
				Intent m_intent = new Intent(MainTestActivity.this, FirstCategory.class);
				startActivity(m_intent);
				finish();
			}
		});

		scroll_top.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "맨위로 이동 클릭!");

				m_ScrollView.post(new Runnable() {
					@Override
					public void run() {
						ObjectAnimator.ofInt(m_ScrollView, "scrollY", 0).setDuration(800).start();
					} // run end
				}); // post end

			}
		});

		profile_line.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "프로필 클릭!");
				Intent m_intent = new Intent(MainTestActivity.this, ProfileActivity.class);
				startActivity(m_intent);
				finish();
			}
		});


	} // onResume end


} // MainActivity class end
