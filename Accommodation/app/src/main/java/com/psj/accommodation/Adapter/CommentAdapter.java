package com.psj.accommodation.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.psj.accommodation.Activity.MainActivity;
import com.psj.accommodation.Activity.RetroClient;
import com.psj.accommodation.Activity.ReviewDetailActivity;
import com.psj.accommodation.Data.Comment;
import com.psj.accommodation.Data.CommentItem;
import com.psj.accommodation.Data.MainItem;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// TODO MainActivity 에서 보내준 대량의 데이터를 보여주기 위한 중개자(중개업자) 클래스
// TODO MainActivity -> MainAdapter -> MainActivity
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

	public static final String TAG = "MainAdapter";
	public Context detailContext;
	// item class(MainItem)를 정의해 놓았음
	private ArrayList<CommentItem> commentData;
	//RecyclerView.Adapter commentAdapter;
	// 서버 경로
	String ServerImagePath = "http://54.180.152.167/";


	public static class ViewHolder extends RecyclerView.ViewHolder {
		// 사용될 항목들 선언
		TextView commentContent, commentTime, commentEmail, commentModify, commentDelete;
		public View rootview;

		public ViewHolder(View v) {
			super(v);
			commentEmail = v.findViewById(R.id.commentEmail);
			commentTime = v.findViewById(R.id.commentTime);
			commentContent = v.findViewById(R.id.commentContent);
			commentModify = v.findViewById(R.id.commentModify);
			commentDelete = v.findViewById(R.id.commentDelete);

			rootview = v;


		}
	}

	// 생성자 - 넘어 오는 데이터타입에 유의해야 한다.
	public CommentAdapter(Context detailContext, ArrayList<CommentItem> commentDataSet) {
		this.commentData = commentDataSet;
		this.detailContext = detailContext;


	}

	// 리사이클러뷰 행을 표시하는데 사용되는 레이아웃 xml을 가져오는 역할
	@Override
	public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
		// create a new view
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
		// set the view's size, margins, paddings and layout parameters

		ViewHolder viewholder = new ViewHolder(view);
		return viewholder;
	}

	// 리사이클러뷰 행에 보여질 ImageView 와 TextView 등을 설정
	@Override
	public void onBindViewHolder(@NonNull final CommentAdapter.ViewHolder viewHolder, final int position) {
		// get element from your dataset at this position
		// replace the contents of the view with that element

		Log.i(TAG, "댓글 작성한 사용자 이메일 : " + commentData.get(position).getcommentEmail());
		Log.i(TAG, "댓글 작성한 날짜 : " + commentData.get(position).getcommentTime());
		Log.i(TAG, "댓글 내용 : " + commentData.get(position).getcommentData());


		viewHolder.commentEmail.setText(commentData.get(position).getcommentEmail());
		viewHolder.commentTime.setText(commentData.get(position).getcommentTime());
		viewHolder.commentContent.setText(commentData.get(position).getcommentData());

		viewHolder.commentDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.i(TAG, "댓글 삭제 클릭!");
				Log.i(TAG, "댓글 고유 번호 : " + commentData.get(position).getcommentNum());

				// 레트로핏 서버 URL 설정해놓은 객체 생성
				RetroClient retroClient = new RetroClient();
				// GET, POST 같은 서버에 데이터를 보내기 위해서 생성합니다
				ApiService apiService = retroClient.getApiClient().create(ApiService.class);

				// 인터페이스 ApiService에 선언한 commentDelete()를 호출합니다
				Call<String> call = apiService.commentDelete(commentData.get(position).getcommentNum());

				call.enqueue(new Callback<String>() {
					@Override
					public void onResponse(Call<String> call, Response<String> response) {
						Log.i(TAG, "onResponse 실행");

						Toast.makeText(detailContext, "서버에서 받은 응답 값 : " + response.body(), Toast.LENGTH_LONG).show();
						Log.i(TAG, "onResponse / " + response.body().toString());

						if (response.body().equals("댓글삭제성공")) {
							Log.i(TAG, "댓글삭제 성공하였습니다");
							Log.i(TAG, "어댑터에서 클릭한 포지션 : " + position);


							String removeData = String.valueOf(commentData.remove(position));
							Log.i(TAG, "어댑터 뷰에서 삭제한 리스트 값 : " + removeData);
							notifyItemRemoved(position);

							notifyItemRangeChanged(position, commentData.size());

						}
					}

					@Override
					public void onFailure(Call<String> call, Throwable throwable) {
						Log.i("onFailure", "" + throwable);
						Toast.makeText(detailContext, "onFailure / " + throwable.getMessage(), Toast.LENGTH_LONG).show();
					}
				}); // enqueue 끝
			}
		});


	}

	// 리사이클러뷰 보여줄 행 개수 리턴
	@Override
	public int getItemCount() {
		return commentData.size();
	}


}
