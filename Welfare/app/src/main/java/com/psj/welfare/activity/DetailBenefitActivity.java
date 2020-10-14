package com.psj.welfare.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.psj.welfare.Data.FirstCategoryItem;
import com.psj.welfare.R;
import com.psj.welfare.api.ApiService;
import com.psj.welfare.api.RetroClient;
import com.psj.welfare.custom.OnSingleClickListener;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
 * 상세 액티비티는 사용자가 자세한 혜택의 내용을 확인할 수 있어야한다
 * 자세한 내용일지라도 사용자의 피로도를 줄여 줄 수 있는 방법이 적용되어야 한다
 * */
public class DetailBenefitActivity extends AppCompatActivity {

	public static final String TAG = "DetailBenefitActivity"; // 로그 찍을 때 사용하는 TAG

	private String detail_data; // 상세 페이지 타이틀

	// 대상, 내용, 기간 텍스트 View
	private TextView detail_title, detail_contact, detail_period;
	// 타이틀과 연관된 이미지 View
	private ImageView detail_img, back_img;
	// 장문(글)을 표현하기 위한 텍스트 View
	private ReadMoreTextView detail_target, detail_contents;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detailbenefit);

		Log.e(TAG, "onCreate 실행");

		detail_img = findViewById(R.id.detail_img);
		detail_title = findViewById(R.id.detail_title);
		detail_target = findViewById(R.id.detail_target);
		detail_contents = findViewById(R.id.detail_contents);
		detail_contact = findViewById(R.id.detail_contact);
		detail_period = findViewById(R.id.detail_period);
		back_img = findViewById(R.id.back_img);

		if (getIntent().hasExtra("RBF_title")) {

			Intent RBF_intent = getIntent();
			detail_data = RBF_intent.getStringExtra("RBF_title");

			Log.e(TAG, "상세 페이지에서 보여줄 타이틀 : " + detail_data);
			Glide.with(this).load(R.drawable.detail01).into(detail_img);
		}


		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);
		Log.e(TAG, "상세 내용 불러올 정책 제목 : " + detail_data.toString());
		// 인터페이스 ApiService에 선언한 detailData()를 호출합니다
		Call<JsonObject> call = apiService.detailData(detail_data);
		call.enqueue(new Callback<JsonObject>() {
			@Override
			public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
				if (response.isSuccessful()) {
					Log.i(TAG, "onResponse 성공 : " + response.body().toString());
					String detail = response.body().toString();
					jsonParsing(detail);

				} else {
					Log.i(TAG, "onResponse 실패");

				}
			}

			@Override
			public void onFailure(Call<JsonObject> call, Throwable t) {
				Log.i(TAG, "onFailure : " + t.toString());

			}
		}); // call enqueue end


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

		detail_target.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e(TAG, "클릭됨!");
			}
		});

		back_img.setOnClickListener(new OnSingleClickListener() {
			@Override
			public void onSingleClick(View v) {
				Intent d_intent = new Intent(DetailBenefitActivity.this, MainActivity.class);
				startActivity(d_intent);
				finish();
			}
		});

	} // onResume end

	private void jsonParsing(String detail) {

		try {
			JSONObject jsonObject_total = new JSONObject(detail);
			String retBody_data;

			retBody_data = jsonObject_total.getString("retBody");

			Log.i(TAG, "retBody 내용 : " + retBody_data);

			JSONObject jsonObject_detail = new JSONObject(retBody_data);

			String name;
			String target;
			String contents;
			String period;
			String contact;

			name = jsonObject_detail.getString("welf_name");
			target = jsonObject_detail.getString("welf_target");
			contents = jsonObject_detail.getString("welf_contents");
			period = jsonObject_detail.getString("welf_period");
			contact = jsonObject_detail.getString("welf_contact");

			Pattern line_pattern = Pattern.compile("^;");
			Pattern comma_pattern = Pattern.compile(";;");

			String target_line = target.replace("^;", "\n");
			String target_comma = target_line.replace(";;", ",");
			Log.e(TAG, "특수기호 변환 후 : " + target_comma);

			String contents_line = contents.replace("^;", "\n");
			String contents_comma = contents_line.replace(";;", ",");
			Log.e(TAG, "특수기호 변환 후 : " + contents_comma);

			String contact_line = contact.replace("^;", "\n");
			String contact_comma = contact_line.replace(";;", ",");
			Log.e(TAG, "특수기호 변환 후 : " + contact_comma);

			detail_title.setText(name);
			detail_target.setText(target_comma);
			detail_contents.setText(contents_comma);
			detail_period.setText(period);
			detail_contact.setText(contact_comma);

			setReadMoreText(detail_target, detail_contents);


		} catch (JSONException e) {
			e.printStackTrace();
		}
	} // jsonParsing end

	// 상세 페이지 긴 글을 축소 / 확장 기능
	public void setReadMoreText(ReadMoreTextView target, ReadMoreTextView contents) {

		target.setTrimCollapsedText("더보기");
		target.setTrimExpandedText("\t...간략히");
		target.setTrimLength(20);
		target.setColorClickableText(ContextCompat.getColor(this, R.color.colorMainBlue));

		contents.setTrimCollapsedText("더보기");
		contents.setTrimExpandedText("\t...간략히");
		contents.setTrimLength(20);
		contents.setColorClickableText(ContextCompat.getColor(this, R.color.colorMainBlue));


	}


} // DetailBenefitActivity class end
