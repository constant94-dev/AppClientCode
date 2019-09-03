package com.psj.accommodation.Activity;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.OrientationHelper;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.psj.accommodation.R;
import com.yongbeom.aircalendar.core.AirCalendarIntent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


// TODO 로그인 후 보여지는 메인 화면
public class ReviewRegistActivity extends AppCompatActivity {

	ImageView Home, Search, Chat, MyProfile;
	Button ReviewOK, ReviewCancel;
	TextView TimeChoice;
	private CalendarView calendarView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review_regist);

		Home = findViewById(R.id.Home);
		Search = findViewById(R.id.Search);
		Chat = findViewById(R.id.Chat);
		MyProfile = findViewById(R.id.MyProfile);
		ReviewOK = findViewById(R.id.ReviewOK);
		ReviewCancel = findViewById(R.id.ReviewCancel);
		TimeChoice = findViewById(R.id.TimeChoice);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// 숙박 기간 선택 클릭 이벤트
		TimeChoice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 달력 다이얼로그를 보여주고 날짜를 범위로 설정할 수 있다

			}
		});

		// 하단 홈 클릭 이벤트
		Home.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent MainIntent = new Intent(ReviewRegistActivity.this, MainActivity.class);
				startActivity(MainIntent);
				finish();
			}
		});

		// 리뷰 등록하기 클릭 이벤트
		ReviewOK.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent MainIntent = new Intent(ReviewRegistActivity.this, MainActivity.class);
				startActivity(MainIntent);
				finish();
			}
		});

		// 리뷰 등록취소 클릭 이벤트
		ReviewCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent MainIntent = new Intent(ReviewRegistActivity.this, MainActivity.class);
				startActivity(MainIntent);
				finish();
			}
		});


	} // onResume() 끝





} // ReviewRegistActivity 클래스 끝
