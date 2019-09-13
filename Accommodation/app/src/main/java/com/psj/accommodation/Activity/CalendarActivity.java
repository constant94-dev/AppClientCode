package com.psj.accommodation.Activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.applikeysolutions.cosmocalendar.view.CalendarView;
import com.psj.accommodation.Adapter.MainAdapter;
import com.psj.accommodation.Data.MainItem;
import com.psj.accommodation.Data.ReviewSelect;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// TODO 숙박 기간 선택 화면 캘린더 라이브러리 사용
public class CalendarActivity extends AppCompatActivity {
	public static final String TAG = "MainActivity";
	private CalendarView calendarView;
	Button checkDate, resultDate;
	String selectDateStart = "";
	String selectDateEnd = "";


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calender);
		checkDate = findViewById(R.id.checkDate);
		resultDate = findViewById(R.id.resultDate);
		initViews();

		Log.i(TAG, "onCreate 실행");
	}

	// 캘린더뷰 라이브러리 초기값 세팅 기능
	private void initViews() {
		calendarView = (CalendarView) findViewById(R.id.calendar_view);
		calendarView.setCalendarOrientation(OrientationHelper.HORIZONTAL);

		calendarView.setSelectionType(SelectionType.RANGE);

		Log.i(TAG, "initViews 실행");
	}


	@Override
	protected void onResume() {
		super.onResume();

		// 날짜 확인 버튼 클릭 이벤트
		checkDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 내가 선택한 날짜를 리스트에 저장한다
				List<Calendar> days = calendarView.getSelectedDates();

				// 날짜를 입력받을 문자열 변수
				String result = "";
				// 반복을 사용하여 선택한 날짜에 년,월,일을 가져온다
				for (int i = 0; i < days.size(); i++) {
					Calendar calendar = days.get(i);
					//Log.i(TAG, "반복문 실행 : " + days.get(i).toString());
					final int day = calendar.get(Calendar.DAY_OF_MONTH);
					final int month = calendar.get(Calendar.MONTH);
					final int year = calendar.get(Calendar.YEAR);
					String week = new SimpleDateFormat("EE").format(calendar.getTime());

					if (i == 0) {
						String day_full = year + "년 " + (month + 1) + "월 " + day + "일 ";
						result += (day_full + "\n");
					} else if (i == days.size() - 1) {
						String day_full = year + "년 " + (month + 1) + "월 " + day + "일 ";
						result += (day_full + "\n");
					}

				}
				// result는 날짜 선택 기간 확인용
				Toast.makeText(CalendarActivity.this, result, Toast.LENGTH_LONG).show();
			}
		});

		// 숙박 기간 선택 완료 버튼 클릭 이벤트
		resultDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// 내가 선택한 날짜를 리스트에 저장한다
				List<Calendar> days = calendarView.getSelectedDates();

				// 반복을 사용하여 선택한 날짜에 년,월,일을 가져온다
				for (int i = 0; i < days.size(); i++) {
					Calendar calendar = days.get(i);
					//Log.i(TAG, "반복문 실행 : " + days.get(i).toString());
					final int day = calendar.get(Calendar.DAY_OF_MONTH);
					final int month = calendar.get(Calendar.MONTH);
					final int year = calendar.get(Calendar.YEAR);
					//String week = new SimpleDateFormat("EE").format(calendar.getTime());

					if (i == 0) {
						String day_full = year + "년 " + (month + 1) + "월 " + day + "일";
						selectDateStart = day_full;
					} else if (i == days.size() - 1) {
						String day_full = year + "년 " + (month + 1) + "월 " + day + "일";
						selectDateEnd = day_full;
					}

				}


				// 인텐트를 리뷰 등록 화면으로 이동
				Log.i(TAG, "숙박 시작 날짜 : " + selectDateStart);
				Log.i(TAG, "숙박 끝 날짜 : " + selectDateEnd);

				Intent reviewRegistIntent = new Intent(CalendarActivity.this, ReviewRegistActivity.class);
				reviewRegistIntent.putExtra("selectDateStart", selectDateStart);
				reviewRegistIntent.putExtra("selectDateEnd", selectDateEnd);
				startActivity(reviewRegistIntent);
				finish();

			}
		});

	} // onResume() 끝


} // CalendarActivity 클래스 끝
