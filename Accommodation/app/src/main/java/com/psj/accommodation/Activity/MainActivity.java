package com.psj.accommodation.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


import com.psj.accommodation.Adapter.MainAdapter;
import com.psj.accommodation.Data.MainItem;
import com.psj.accommodation.R;

import java.util.ArrayList;


// TODO 로그인 후 보여지는 메인 화면
public class MainActivity extends AppCompatActivity {

	private RecyclerView mainRecyclerView;
	private RecyclerView.Adapter mainAdapter;
	private RecyclerView.LayoutManager mainLayoutManager;

	TextView reviewRegister;

	// 메인 아이템 리스트
	private static ArrayList<MainItem> mainItemList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		reviewRegister = findViewById(R.id.ReviewRegisterText);

		//데이터준비-실제로는 ArrayList<>등을 사용해야 할듯 하다.
		//인터넷이나 폰에 있는 DB에서 아이템을 가져와 배열에 담아 주면 된다.

		// ArrayList 객체 생성
		mainItemList = new ArrayList<>();
		// ArrayList 값 추가
		mainItemList.add(new MainItem("라마다호텔", "2019-08-01 ~ 2019-08-03", "4.5"));
		mainItemList.add(new MainItem("신라호텔", "2019-08-01 ~ 2019-08-03", "5.0"));
		mainItemList.add(new MainItem("부산여관", "2019-08-01 ~ 2019-08-03", "4.0"));

		mainRecyclerView = findViewById(R.id.main_recycler_view);
		mainRecyclerView.setHasFixedSize(true); // 옵션

		// Linear layout manager 사용
		mainLayoutManager = new LinearLayoutManager(this);
		mainRecyclerView.setLayoutManager(mainLayoutManager);

		// Adapter 셋팅
		mainAdapter = new MainAdapter(mainItemList);
		mainRecyclerView.setAdapter(mainAdapter);


	}

	@Override
	protected void onResume() {
		super.onResume();

		// 등록 눌렀을 때
		reviewRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent reviewRegistIntent = new Intent(MainActivity.this, ReviewRegistActivity.class);
				startActivity(reviewRegistIntent);
				finish();
			}
		});
	}
}
