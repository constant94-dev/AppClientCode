package com.psj.accommodation.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.psj.accommodation.Adapter.CommentAdapter;
import com.psj.accommodation.Adapter.DetailSlideAdapter;

import com.psj.accommodation.Data.Comment;
import com.psj.accommodation.Data.CommentItem;
import com.psj.accommodation.Data.MainItem;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


// TODO 리뷰 상세정보 화면
public class ReviewDetailActivity extends AppCompatActivity {

	public static final String TAG = "ReviewDetailActivity";
	ImageView Home, DetailChange;
	TextView DetailName, DetailTime, DetailScore, DetailReview, DetailCommentLength, DetailCommentCount;
	EditText DetailComment;
	Button CommentBtn, CommentModifyBtn;

	// 쉐어드에 저장된 사용자 이름 (세션)
	String sessionName = "";
	// 쉐어드에 저장된 사용자 이메일 (세션)
	String sessionEmail = "";
	// 리뷰 고유 번호 저장 변수
	String PlaceNum = "";
	// 리뷰 이미지 경로 저장 변수
	String PlaceImage = "";
	// 리뷰 평점 저장 변수
	float PlaceScore;
	// 리뷰 후기 글 저장 변수
	String PlaceReview = "";
	// 다이얼로그 객체
	AlertDialog alertDialog;
	// 서버 경로
	String ServerImagePath = "http://54.180.152.167/";
	// 숙박 후기 최대 글자 수
	int MAX_COUNT = 100;
	// 숙박 후기 글자 수 저장
	int ING_COUNT = 0;
	// 상세 리뷰 이미지 슬라이드 어댑터 객체
	DetailSlideAdapter detailSlideAdapter;
	// 뷰 페이저 객체
	ViewPager viewPager;

	private RecyclerView commentRecyclerView;
	private RecyclerView.Adapter commentAdapter;
	private RecyclerView.LayoutManager commentLayoutManager;

	// 댓글 아이템 리스트
	private static ArrayList<CommentItem> commentItemList;

	// 서버에서 가져온 JSON 형태 댓글
	String commentJsonString = "";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review_detail);

		Home = findViewById(R.id.Home);
		DetailName = findViewById(R.id.DetailName);
		DetailTime = findViewById(R.id.DetailTime);
		DetailScore = findViewById(R.id.DetailScore);
		DetailChange = findViewById(R.id.DetailChange);
		DetailReview = findViewById(R.id.DetailReview);
		DetailComment = findViewById(R.id.DetailComment);
		CommentBtn = findViewById(R.id.CommentBtn);
		DetailCommentLength = findViewById(R.id.DetailCommentLength);
		CommentModifyBtn = findViewById(R.id.CommentModifyBtn);

		Log.i(TAG, "onCreate : 실행");

		// ArrayList 객체 생성
		commentItemList = new ArrayList<>();

		commentRecyclerView = findViewById(R.id.comment_recycler_view);
		commentRecyclerView.setHasFixedSize(true); // 옵션

		// Linear layout manager 사용
		commentLayoutManager = new LinearLayoutManager(this);
		commentRecyclerView.setLayoutManager(commentLayoutManager);

		// Adapter 셋팅
		commentAdapter = new CommentAdapter(getApplicationContext(), commentItemList, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getTag() != null) {

					CommentModifyBtn.setVisibility(View.VISIBLE);
					CommentBtn.setVisibility(View.GONE);

					final int position = (int) v.getTag();
					Log.i(TAG, "어댑터에서 가져온 태그 값 : " + position);
					String modify = commentItemList.get(position).getcommentData();
					Log.i(TAG, "수정 할 댓글 원본 : " + modify);
					DetailComment.setText(modify);

					final String commentNumCheck = commentItemList.get(position).getcommentNum();
					Log.i(TAG, "수정 할 댓글 고유 번호 : " + commentNumCheck);


					// 등록 버튼 사라지고 출력된 수정 버튼 클릭 이벤트
					CommentModifyBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {

							Log.i(TAG, "수정 할 댓글 수정본 : " + DetailComment.getText().toString());
							final String modifyComment = DetailComment.getText().toString();

							commentItemList.get(position).setcommentData(modifyComment);

							// 레트로핏 서버 URL 설정해놓은 객체 생성
							RetroClient retroClient = new RetroClient();
							// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
							ApiService apiService = retroClient.getApiClient().create(ApiService.class);

							// 인터페이스 ApiService에 선언한 commentUpdate()를 호출합니다
							Call<String> call = apiService.commentUpdate(commentNumCheck, modifyComment);

							call.enqueue(new Callback<String>() {
								@Override
								public void onResponse(Call<String> call, Response<String> response) {
									Log.i(TAG, "onResponse 실행");
									Toast.makeText(ReviewDetailActivity.this, "서버에서 받은 응답 값 : " + response.body(), Toast.LENGTH_LONG).show();
									Log.i(TAG, "onResponse : " + response.body().toString());

									// 수정하고 싶은 해당 아이템을 position 과 값을 설정하라
									commentItemList.set(position, commentItemList.get(position));

									// 수정한 아이템을 갱신해라
									commentAdapter.notifyItemChanged(position);

									DetailComment.setText("");

									CommentModifyBtn.setVisibility(View.GONE);
									CommentBtn.setVisibility(View.VISIBLE);

								}

								@Override
								public void onFailure(Call<String> call, Throwable throwable) {
									Log.i("onFailure", "" + throwable);
									Toast.makeText(ReviewDetailActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
								}
							}); // enqueue 끝
						}
					});

				} else {
					Log.i(TAG, "어댑터에서 가져온 태그 값 없어요");
				}
			}
		});

		// 리사이클러뷰에 어댑터 설정
		commentRecyclerView.setAdapter(commentAdapter);

		// 쉐어드에 저장된 사용자 이름 가져오기 기능
		getShard();

		// 데이터 수신
		Bundle getDetail = getIntent().getExtras();

		// 인텐트 데이터 수신 조건 시작
		if (getDetail == null) {
			Log.i(TAG, "데이터 수신 할거 없음");
		} else {

			Log.i(TAG, "데이터 수신 할거 있음");

			// String, float 형태 값 가져오기
			PlaceNum = getDetail.getString("PlaceNum");
			PlaceImage = getDetail.getString("PlaceImage");
			String PlaceTime = getDetail.getString("PlaceTime");
			String PlaceName = getDetail.getString("PlaceName");
			PlaceScore = getDetail.getFloat("PlaceScore");
			String Writer = getDetail.getString("Writer");
			PlaceReview = getDetail.getString("PlaceReview");

			Log.i(TAG, "리뷰 번호 : " + PlaceNum);
			Log.i(TAG, "숙박 장소 이미지 경로 : " + PlaceImage);
			Log.i(TAG, "숙박 장소 : " + PlaceName);
			Log.i(TAG, "숙박 기간 : " + PlaceTime);
			Log.i(TAG, "평점 : " + PlaceScore);
			Log.i(TAG, "작성자 : " + Writer);
			Log.i(TAG, "후기 : " + PlaceReview);

			String score = String.valueOf(PlaceScore);

			// 뷰페이저로 전달할 이미지 경로 저장할 배열 변수
			String[] imagePath = PlaceImage.split(" ");

			// 뷰페이저로 전달할 이미지 경로 확인 반복문
			for (int i = 0; i < imagePath.length; i++) {
				Log.i(TAG, "이미지 경로 반복문 " + i + "번째 : " + imagePath[i]);
			}

			viewPager = findViewById(R.id.DetailViewPager);
			detailSlideAdapter = new DetailSlideAdapter(getApplicationContext(), imagePath);
			viewPager.setAdapter(detailSlideAdapter);

			//Glide.with(this).load(Uri.parse(ServerImagePath + imagePath[0])).centerCrop().into(DetailImage);
			DetailName.setText(PlaceName);
			DetailTime.setText(PlaceTime);
			DetailScore.setText(score);
			DetailReview.setText(PlaceReview);

			Log.i(TAG, "쉐어드에 저장된 사용자 이름 : " + sessionName);

			if (!sessionName.equals(Writer)) {
				DetailChange.setVisibility(View.INVISIBLE);
				Log.i(TAG, "로그인한 사용자와 작성자가 다릅니다.");
			} else {
				Log.i(TAG, "로그인한 사용자와 작성자가 같습니다.");
			}

			getCommentData();


		} // 인텐트 데이터 수신 조건 끝

	} // onCreate 끝

	@Override
	protected void onResume() {
		super.onResume();

		// 댓글 텍스트 변화 기능
		DetailComment.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				Log.i(TAG, "beforeTextChanged 실행");
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
				Log.i(TAG, "onTextChanged 실행");

				Log.i(TAG, "ING_COUNT : " + ING_COUNT);
				Log.i(TAG, "charSequence.length() : " + charSequence.length());

				ING_COUNT = charSequence.length();

				DetailCommentLength.setText(ING_COUNT + "/100");

				if (charSequence.length() == MAX_COUNT) {
					DetailCommentLength.setText(MAX_COUNT + "/100");
					DetailComment.setSelection(MAX_COUNT);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				Log.i(TAG, "afterTextChanged 실행");
			}
		}); // 댓글 텍스트 변화 기능 끝

		Home.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mainIntent = new Intent(ReviewDetailActivity.this, MainActivity.class);
				startActivity(mainIntent);
				finish();
			}
		}); // Home 이미지 클릭 이벤트 끝

		DetailChange.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeData();
			}
		}); // 편집 이미지 클릭 이벤트 끝

		CommentBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				commentInsert();
			}
		}); // 댓글 등록 버튼 클릭 이벤트 끝

	} // onResume 끝

	// 댓글 등록 기능
	private void commentInsert() {
		Log.i(TAG, "commentInsert 실행");

		// 댓글 입력 길이 확인 조건 시작
		if (DetailComment.getText().length() == 0) {

			Log.i(TAG, "commentInsert 실행");
			Toast.makeText(ReviewDetailActivity.this, "댓글을 작성해주세요", Toast.LENGTH_SHORT).show();

		} else {

			Comment commentData = new Comment();

			commentData.setplaceNum(PlaceNum);
			commentData.setcommentData(DetailComment.getText().toString());
			commentData.setcommentEmail(sessionEmail);
			// 레트로핏 서버 URL 설정해놓은 객체 생성
			RetroClient retroClient = new RetroClient();
			// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
			ApiService apiService = retroClient.getApiClient().create(ApiService.class);

			// 인터페이스 ApiService에 선언한 commentInsert()를 호출합니다
			Call<String> call = apiService.commentInsert(commentData.getcommentEmail(), commentData.getplaceNum(), commentData.getcommentData(), sessionName);

			call.enqueue(new Callback<String>() {
				@Override
				public void onResponse(Call<String> call, Response<String> response) {
					Log.i(TAG, "onResponse 실행");
					Toast.makeText(ReviewDetailActivity.this, "서버에서 받은 응답 값 : " + response.body(), Toast.LENGTH_LONG).show();
					Log.i(TAG, "onResponse : " + response.body().toString());

					DetailComment.setText("");

					getCommentData();

				}

				@Override
				public void onFailure(Call<String> call, Throwable throwable) {
					Log.i("onFailure", "" + throwable);
					Toast.makeText(ReviewDetailActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
				}
			}); // enqueue 끝

		} // 댓글 입력 길이 확인 조건 끝

	} // commentInsert 기능 끝

	// 편집 이미지 클릭 후 수정 or 삭제 알림 기능
	public void changeData() {

		AlertDialog.Builder builder = new AlertDialog.Builder(ReviewDetailActivity.this);

		builder.setTitle("").setMessage("어떤 작업을 원하세요?");
		builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(ReviewDetailActivity.this, "삭제 작업을 진행할게요", Toast.LENGTH_SHORT).show();

				// 레트로핏 서버 URL 설정해놓은 객체 생성
				RetroClient retroClient = new RetroClient();
				// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
				ApiService apiService = retroClient.getApiClient().create(ApiService.class);

				// 인터페이스 ApiService에 선언한 reviewDelete()를 호출합니다
				Call<String> call = apiService.reviewDelete(PlaceNum);

				call.enqueue(new Callback<String>() {
					@Override
					public void onResponse(Call<String> call, Response<String> response) {
						Log.i(TAG, "onResponse 실행");

						Toast.makeText(ReviewDetailActivity.this, "서버에서 받은 응답 값 : " + response.body(), Toast.LENGTH_LONG).show();
						Log.i(TAG, "onResponse / " + response.body().toString());

						if (response.body().equals("리뷰삭제성공")) {
							Log.i(TAG, "리뷰삭제 성공하였습니다");
							Intent mainIntent = new Intent(ReviewDetailActivity.this, MainActivity.class);
							startActivity(mainIntent);
							finish();
						}
					}

					@Override
					public void onFailure(Call<String> call, Throwable throwable) {
						Log.i("onFailure", "" + throwable);
						Toast.makeText(ReviewDetailActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
					}
				}); // enqueue 끝

			} // onClick 끝
			// setPositiveButton 끝
		}).setNegativeButton("수정", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(ReviewDetailActivity.this, "수정작업을 진행할게요", Toast.LENGTH_SHORT).show();
				Intent modifyIntent = new Intent(ReviewDetailActivity.this, ReviewModifyActivity.class);
				modifyIntent.putExtra("PlaceNum", PlaceNum);
				modifyIntent.putExtra("PlaceImage", PlaceImage);
				modifyIntent.putExtra("PlaceName", DetailName.getText().toString());
				modifyIntent.putExtra("PlaceTime", DetailTime.getText().toString());
				modifyIntent.putExtra("PlaceScore", PlaceScore);
				modifyIntent.putExtra("PlaceReview", PlaceReview);
				startActivity(modifyIntent);

			}
		}); // setNegativeButton 끝

		alertDialog = builder.create();

		alertDialog.show();
	} // changeData 끝


	// 쉐어드에 저장된 사용자 이름 가져오기 기능 (세션 활용)
	public void getShard() {

		SharedPreferences sharedPreferences = getSharedPreferences("sessionName", MODE_PRIVATE);
		sessionName = sharedPreferences.getString("name", "noName");
		sessionEmail = sharedPreferences.getString("email", "noName");
	} // getShard 끝

	// 레트로핏 사용하여 데이터베이스 데이터 가져오기
	public void getCommentData() {

		Log.i(TAG, "getCommentData : 실행");

		// 레트로핏 서버 URL 설정해놓은 객체 생성
		RetroClient retroClient = new RetroClient();
		// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
		ApiService apiService = retroClient.getApiClient().create(ApiService.class);

		// 인터페이스 ApiService에 선언한 reviewSelect()를 호출합니다
		Call<String> call = apiService.commentSelect(PlaceNum);

		call.enqueue(new Callback<String>() {
			@Override
			public void onResponse(Call<String> call, Response<String> response) {
				Log.i(TAG, "onResponse : 실행");
				Log.i(TAG, "서버에서 응답 받은 값 : " + response.body().toString());

				// 리스트를 초기화 시킨다
				commentItemList.clear();
				// 어댑터에게 데이터 세팅이 변경되었다고 알려준다
				commentAdapter.notifyDataSetChanged();

				commentJsonString = response.body().toString();
				showResult();
			}

			@Override
			public void onFailure(Call<String> call, Throwable throwable) {
				Log.i("onFailure", "" + throwable);
				Toast.makeText(ReviewDetailActivity.this, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
			}
		});


	} // getReviewData() 끝

	// 서버에서 json 형태로 가져온 값을 리사이클러뷰에 추가 시키는 방법
	private void showResult() {

		Log.i(TAG, "showResult : 실행");

		String TAG_JSON = "result";
		String TAG_NUM = "num";
		String TAG_CNUM = "cnum";
		String TAG_EMAIL = "email";
		String TAG_TIME = "time";
		String TAG_COMMENT = "comment";
		String TAG_WRITER = "writer";


		try {
			// 서버에서 가져온 json 데이터 처음 시작이 '{' 중괄호로 시작해서 JSONObject 에 담아준다
			JSONObject jsonObject = new JSONObject(commentJsonString);
			// JSONArray [ 대괄호로 시작하니 jsonObject 에서 get 한 값을 JSONArray 에 담아준다
			JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

			// 반복문을 이용하여 알맞게 풀어준다
			for (int i = 0; i < jsonArray.length(); i++) {

				JSONObject item = jsonArray.getJSONObject(i);

				String commentNum = item.getString(TAG_CNUM);
				String placeNum = item.getString(TAG_NUM);
				String commentEmail = item.getString(TAG_EMAIL);
				String commentTime = item.getString(TAG_TIME);
				String comment = item.getString(TAG_COMMENT);
				String writer = item.getString(TAG_WRITER);

				Log.i(TAG, "반복문 : 실행");

				Log.i(TAG, i + "/ 댓글 고유 번호 : " + commentNum + "/ 리뷰 번호 : " + placeNum + "/ 댓글 작성한 사용자 이메일 : " + commentEmail + "/ 댓글 작성한 날짜 : " + commentTime + "/ 댓글 내용 : " + comment);
				Log.i(TAG, "쉐어드에 저장된 사용자 이름 : " + sessionName);

				// 어댑터에 전달할 데이터 추가
				commentItemList.add(new CommentItem(commentNum, placeNum, commentEmail, commentTime, comment, writer));
				// 어댑터에게 새로 삽입된 아이템이 있다는걸 알려준다
				commentAdapter.notifyItemInserted(0);
			}


		} catch (JSONException e) {

			Log.d(TAG, "showResult : ", e);
		}

	} // showResult() 끝

} // ReviewDetailActivity 클래스 끝
