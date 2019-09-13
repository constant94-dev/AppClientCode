package com.psj.accommodation.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.psj.accommodation.R;


// TODO 리뷰 상세정보 화면
public class ReviewDetailActivity extends AppCompatActivity {

	ImageView Home;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review_detail);

		Home = findViewById(R.id.Home);

	} // onCreate 끝

	@Override
	protected void onResume() {
		super.onResume();

		Home.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mainIntent = new Intent(ReviewDetailActivity.this, MainActivity.class);
				startActivity(mainIntent);
				finish();
			}
		});
	} // onResume 끝
}
