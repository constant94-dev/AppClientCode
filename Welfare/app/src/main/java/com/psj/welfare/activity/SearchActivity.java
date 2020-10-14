package com.psj.welfare.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.psj.welfare.R;
import com.psj.welfare.api.ApiService;
import com.psj.welfare.api.RetroClient;
import com.psj.welfare.custom.OnSingleClickListener;
import com.psj.welfare.fragment.FragmentSearch;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {

	public static final String TAG = "SearchActivity";

	private ImageView search_backImg;
	private EditText searching;
	private LinearLayout search_main;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		Log.e(TAG, "onCreate 실행");

		search_backImg = findViewById(R.id.search_backImg);
		searching = findViewById(R.id.searching);
		search_main = findViewById(R.id.search_main);

	} // onCreate end

	@Override
	protected void onStart() {
		super.onStart();
		Log.e(TAG, "onStart 실행");


	} // onStart end

	@Override
	protected void onResume() {
		super.onResume();
		Log.e(TAG, "onResume 실행");

		// 안드로이드 EditText 키보드에서 검색 버튼 추가 코드
		searching.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					Log.e(TAG, "키보드에서 검색 버튼 클릭 했어요!");
					Log.e(TAG, "키워드 : " + searching.getText());

					// 검색 버튼 클릭 되었을 때 처리하는 기능
					performSearch(searching.getText().toString());

					return true;

				}

				return false;

			}

		});

		search_backImg.setOnClickListener(new OnSingleClickListener() {
			@Override
			public void onSingleClick(View v) {
				Log.e(TAG, "검색 페이지 뒤로가기 버튼 클릭!");
				Intent s_intent = new Intent(SearchActivity.this, MainActivity.class);
				startActivity(s_intent);
				finish();
			}
		});

	} // onResume end

	// 모바일 키보드에서 검색 버튼 눌렀을 때 다음 처리는 어떻게 할 것인지
	public void performSearch(String search) {
		search_main.setVisibility(View.GONE);
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		FragmentSearch fragmentSearch = new FragmentSearch();
		fragmentSearch.searchData(search);
		transaction.replace(R.id.search_frame, fragmentSearch);
		transaction.commit();

		// 모바일 키보드 숨기기 로직
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searching.getWindowToken(), 0);
	}

} // SearchActivity class end
