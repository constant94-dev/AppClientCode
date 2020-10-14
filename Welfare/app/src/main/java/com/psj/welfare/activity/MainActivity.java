package com.psj.welfare.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.psj.welfare.R;
import com.psj.welfare.adapter.MainTextVPAdapter;
import com.psj.welfare.custom.OnSingleClickListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/*
 * 메인액티비티는 앱의 전반적인 느낌을 보여주는 곳
 * 사용자가 메인화면을 보았을 때 어떤 서비스인지 알 수 있게 직관성이 있어야 하고 피로감이 없어야 한다
 * */
public class MainActivity extends AppCompatActivity {

	public static final String TAG = "MainActivity"; // 로그 찍을 때 사용하는 TAG

	// 네비게이션 바 Layout, View
	private DrawerLayout drawerLayout;
	private View drawerView;

	private ScrollView m_ScrollView; // 스크롤 View

	private LinearLayout scroll_top, profile_line, setting_line; // 스크롤 맨위로 이동 Layout, 네비게이션 바 프로필 항목 Layout

	// 메인 페이지 이미지 View
	private ImageView menu_img, m_child_img, m_student_img, m_old_img, m_pregnancy_img, m_disorder_img,
			m_cultural_img, m_multicultural_img, m_company_img,
			m_law_img, m_living_img, m_job_img, m_homeless_img, m_etc_img;
	// 메인 페이지 검색 View
	private TextView m_search;
	// 메인 페이지 관심사 버튼
	private Button m_child_btn, m_student_btn, m_old_btn, m_pregnancy_btn,
			m_disorder_btn, m_cultural_btn, m_multicultural_btn, m_company_btn,
			m_law_btn, m_living_btn, m_job_btn, m_homeless_btn, m_etc_btn, benefit_go;

	private ViewPager viewPager; // 메인 페이지 배너 문구
	private MainTextVPAdapter mainTextVPAdapter; // 메인 페이지 배너 문구 어댑터
	int currentPage = 0; // 메인 페이지 배너 넘버
	Timer timer; // 배너 문구 변경 시간 설정을 위한 객체
	long DELAY_MS = 500; // 딜레이 시간
	long PERIOD_MS = 3000; // 일정한 간격으로 지정한 작업 수행

	ArrayList<String> m_favorList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);



		scroll_top = findViewById(R.id.scroll_top);
		m_ScrollView = findViewById(R.id.m_ScrollView);
		profile_line = findViewById(R.id.profile_line);
		setting_line = findViewById(R.id.setting_line);
		drawerView = (View) findViewById(R.id.drawerView);
		menu_img = findViewById(R.id.menu_img);
		m_search = findViewById(R.id.m_search);


		m_child_btn = findViewById(R.id.m_child_btn);
		m_student_btn = findViewById(R.id.m_student_btn);
		m_old_btn = findViewById(R.id.m_old_btn);
		m_pregnancy_btn = findViewById(R.id.m_pregnancy_btn);
		m_disorder_btn = findViewById(R.id.m_disorder_btn);
		m_cultural_btn = findViewById(R.id.m_cultural_btn);
		m_multicultural_btn = findViewById(R.id.m_multicultural_btn);
		m_company_btn = findViewById(R.id.m_company_btn);
		m_law_btn = findViewById(R.id.m_law_btn);
		m_living_btn = findViewById(R.id.m_living_btn);
		m_job_btn = findViewById(R.id.m_job_btn);
		m_homeless_btn = findViewById(R.id.m_homeless_btn);
		m_etc_btn = findViewById(R.id.m_etc_btn);
		benefit_go = findViewById(R.id.benefit_go);

		m_child_img = findViewById(R.id.m_child_img);
		m_student_img = findViewById(R.id.m_student_img);
		m_old_img = findViewById(R.id.m_old_img);
		m_pregnancy_img = findViewById(R.id.m_pregnancy_img);
		m_disorder_img = findViewById(R.id.m_disorder_img);
		m_cultural_img = findViewById(R.id.m_cultural_img);
		m_multicultural_img = findViewById(R.id.m_multicultural_img);
		m_company_img = findViewById(R.id.m_company_img);
		m_law_img = findViewById(R.id.m_law_img);
		m_living_img = findViewById(R.id.m_living_img);
		m_job_img = findViewById(R.id.m_job_img);
		m_homeless_img = findViewById(R.id.m_homeless_img);
		m_etc_img = findViewById(R.id.m_etc_img);

		// 메인 페이지 배너 문구 자동 슬라이드를 위한 로직
		viewPager = findViewById(R.id.viewPager);
		mainTextVPAdapter = new MainTextVPAdapter(getApplicationContext());
		viewPager.setAdapter(mainTextVPAdapter);

		final Handler handler = new Handler();
		final Runnable update = new Runnable() {
			@Override
			public void run() {
				if (currentPage == 2) {
					currentPage = 0;
				}
				viewPager.setCurrentItem(currentPage++, true);
			} // run end
		}; // Runnable end

		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				handler.post(update);
			} // run end
		}, DELAY_MS, PERIOD_MS); // timer end

	} // onCreate end

	@Override
	protected void onStart() {
		super.onStart();

		// 글라이드 라이브러리를 사용하여 이미지 세팅
		Glide.with(this).load(R.drawable.child).into(m_child_img);
		Glide.with(this).load(R.drawable.student).into(m_student_img);
		Glide.with(this).load(R.drawable.old).into(m_old_img);
		Glide.with(this).load(R.drawable.pregnancy).into(m_pregnancy_img);
		Glide.with(this).load(R.drawable.disorder).into(m_disorder_img);
		Glide.with(this).load(R.drawable.cultural).into(m_cultural_img);
		Glide.with(this).load(R.drawable.multicultural).into(m_multicultural_img);
		Glide.with(this).load(R.drawable.company).into(m_company_img);
		Glide.with(this).load(R.drawable.law).into(m_law_img);
		Glide.with(this).load(R.drawable.living).into(m_living_img);
		Glide.with(this).load(R.drawable.job).into(m_job_img);
		Glide.with(this).load(R.drawable.homeless).into(m_homeless_img);
		Glide.with(this).load(R.drawable.etc).into(m_etc_img);


	} // onStart end

	@Override
	protected void onResume() {
		super.onResume();

		setting_line.setOnClickListener(new OnSingleClickListener() {
			@Override
			public void onSingleClick(View v) {
				Intent m_intent = new Intent(MainActivity.this, SettingActivity.class);
				startActivity(m_intent);
				finish();
			}
		});

		// 네비게이션바 프로필 클릭 했을 때
		profile_line.setOnClickListener(new OnSingleClickListener() {
			@Override
			public void onSingleClick(View v) {
				Intent m_intent = new Intent(MainActivity.this, ProfileActivity.class);
				startActivity(m_intent);
				finish();
			}
		});

		// 키워드 검색 클릭 했을 때
		m_search.setOnClickListener(new OnSingleClickListener() {
			@Override
			public void onSingleClick(View v) {
				Log.e(TAG, "메인 키워드 검색 클릭!");
				Intent m_intent = new Intent(MainActivity.this, SearchActivity.class);
				startActivity(m_intent);
				finish();
			}
		});

		benefit_go.setOnClickListener(new OnSingleClickListener() {
			@Override
			public void onSingleClick(View v) {
				m_favorList.add(0, "전체");
				Intent m_intent = new Intent(MainActivity.this, ResultBenefitActivity.class);
				m_intent.putStringArrayListExtra("favor_btn", m_favorList);
				startActivity(m_intent);
				finish();
			}
		});

		// 네비게이션 바 이미지 클릭 했을 때
		menu_img.setOnClickListener(new OnSingleClickListener() {
			@Override
			public void onSingleClick(View v) {
				// 드로어레이아웃 보여주기
				drawerLayout.openDrawer(drawerView);
			}
		});

		// 맨 위로 이동 클릭 했을 때
		scroll_top.setOnClickListener(new OnSingleClickListener() {
			@Override
			public void onSingleClick(View v) {
				Log.i(TAG, "맨위로 이동 클릭!");

				// 스크롤 맨위로 0.8초만에 이동
				m_ScrollView.post(new Runnable() {
					@Override
					public void run() {
						ObjectAnimator.ofInt(m_ScrollView, "scrollY", 0).setDuration(800).start();
					} // run end
				}); // post end
			}
		});


		// 아기·어린이 혜택 버튼 클릭 했을 떄
		m_child_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.i(TAG, "버튼 클릭 상태 : " + v.isSelected());

				if (v.isSelected()) {
					m_favorList.remove("아기·어린이");
					Log.i(TAG, "버튼 클릭 상태 비활성화");
					m_child_btn.setSelected(!m_child_btn.isSelected());
				} else {
					m_favorList.add("아기·어린이");
					Log.i(TAG, "버튼 클릭 활성화!!");
					m_child_btn.setSelected(true);
				}
			}
		});

		// 학생·청년 혜택 클릭
		m_student_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "버튼 클릭 상태 : " + v.isSelected());

				if (v.isSelected()) {
					m_favorList.remove("학생·청년");
					m_student_btn.setSelected(!m_student_btn.isSelected());
				} else {
					m_favorList.add("학생·청년");
					m_student_btn.setSelected(true);
				}
			}
		});

		// 중장년·노인 혜택 클릭
		m_old_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "버튼 클릭 상태 : " + v.isSelected());

				if (v.isSelected()) {
					m_favorList.remove("중장년·노인");
					m_old_btn.setSelected(!m_old_btn.isSelected());
				} else {
					m_favorList.add("중장년·노인");
					m_old_btn.setSelected(true);
				}
			}
		});

		// 육아·임신 혜택 클릭
		m_pregnancy_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "버튼 클릭 상태 : " + v.isSelected());

				if (v.isSelected()) {
					m_favorList.remove("육아·임신");
					m_pregnancy_btn.setSelected(!m_pregnancy_btn.isSelected());
				} else {
					m_favorList.add("육아·임신");
					m_pregnancy_btn.setSelected(true);
				}
			}
		});

		// 장애인 혜택 클릭
		m_disorder_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "버튼 클릭 상태 : " + v.isSelected());

				if (v.isSelected()) {
					m_favorList.remove("장애인");
					m_disorder_btn.setSelected(!m_disorder_btn.isSelected());
				} else {
					m_favorList.add("장애인");
					m_disorder_btn.setSelected(true);
				}
			}
		});

		// 문화·생활 혜택 클릭
		m_cultural_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "버튼 클릭 상태 : " + v.isSelected());

				if (v.isSelected()) {
					m_favorList.remove("문화·생활");
					m_cultural_btn.setSelected(!m_cultural_btn.isSelected());
				} else {
					m_favorList.add("문화·생활");
					m_cultural_btn.setSelected(true);
				}
			}
		});

		// 다문화 혜택 클릭
		m_multicultural_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "버튼 클릭 상태 : " + v.isSelected());

				if (v.isSelected()) {
					m_favorList.remove("다문화");
					m_multicultural_btn.setSelected(!m_multicultural_btn.isSelected());
				} else {
					m_favorList.add("다문화");
					m_multicultural_btn.setSelected(true);
				}
			}
		});

		// 기업·자영업자 혜택 클릭
		m_company_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "버튼 클릭 상태 : " + v.isSelected());

				if (v.isSelected()) {
					m_favorList.remove("기업·자영업자");
					m_company_btn.setSelected(!m_company_btn.isSelected());
				} else {
					m_favorList.add("기업·자영업자");
					m_company_btn.setSelected(true);
				}
			}
		});

		// 법률 혜택 클릭
		m_law_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "버튼 클릭 상태 : " + v.isSelected());

				if (v.isSelected()) {
					m_favorList.remove("법률");
					m_law_btn.setSelected(!m_law_btn.isSelected());
				} else {
					m_favorList.add("법률");
					m_law_btn.setSelected(true);
				}
			}
		});

		// 주거 혜택 클릭
		m_living_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "버튼 클릭 상태 : " + v.isSelected());

				if (v.isSelected()) {
					m_favorList.remove("주거");
					m_living_btn.setSelected(!m_living_btn.isSelected());
				} else {
					m_favorList.add("주거");
					m_living_btn.setSelected(true);
				}
			}
		});

		// 취업·창업 혜택 클릭
		m_job_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "버튼 클릭 상태 : " + v.isSelected());

				if (v.isSelected()) {
					m_favorList.remove("취업·창업");
					m_job_btn.setSelected(!m_job_btn.isSelected());
				} else {
					m_favorList.add("취업·창업");
					m_job_btn.setSelected(true);
				}
			}
		});

		// 기초생활수급·노숙인 혜택 클릭
		m_homeless_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "버튼 클릭 상태 : " + v.isSelected());

				if (v.isSelected()) {
					m_favorList.remove("저소득층");
					m_homeless_btn.setSelected(!m_homeless_btn.isSelected());
				} else {
					m_favorList.add("저소득층");
					m_homeless_btn.setSelected(true);
				}
			}
		});

		// 기타 혜택 클릭
		m_etc_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "버튼 클릭 상태 : " + v.isSelected());

				if (v.isSelected()) {
					m_favorList.remove("기타");
					m_etc_btn.setSelected(!m_etc_btn.isSelected());
				} else {
					m_favorList.add("기타");
					m_etc_btn.setSelected(true);
				}
			}
		});

	} // onResume end


} // MainActivity class end
