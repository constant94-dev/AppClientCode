package com.psj.accommodation.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import static android.content.Context.MODE_PRIVATE;

// TODO MainActivity 에서 보내준 대량의 데이터를 보여주기 위한 중개자(중개업자) 클래스
// TODO MainActivity -> MainAdapter -> MainActivity
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

	public static final String TAG = "MainAdapter";
	public Context detailContext;
	// item class(MainItem)를 정의해 놓았음
	private ArrayList<CommentItem> commentData;
	//RecyclerView.Adapter commentAdapter;

	static View.OnClickListener onClickListener;

	String sessionName = "";
	String sessionEmail = "";

	public static class ViewHolder extends RecyclerView.ViewHolder {
		// 사용될 항목들 선언
		TextView commentContent, commentTime, commentEmail, commentModify, commentDelete;
		public View rootView;


		public ViewHolder(View v) {
			super(v);

			Log.i(TAG, "ViewHolder : 실행");

			commentEmail = v.findViewById(R.id.commentEmail);
			commentTime = v.findViewById(R.id.commentTime);
			commentContent = v.findViewById(R.id.commentContent);
			commentModify = v.findViewById(R.id.commentModify);
			commentDelete = v.findViewById(R.id.commentDelete);

			// 내가 선택한 아이템이 몇번째 인지 알아내기 위한 준비 작업

			rootView = v;
			// 버튼이 아니기 때문에 클릭을 할 수 있게 설정한다
			v.setClickable(true);
			// 활성화 상태인지 아닌지를 설정한다 setClickable 보다 큰 개념이다
			v.setEnabled(true);
			v.setOnClickListener(onClickListener);


			//commentModify.setVisibility(View.INVISIBLE);

		}
	}

	// 생성자 - 넘어 오는 데이터타입에 유의해야 한다.
	public CommentAdapter(Context detailContext, ArrayList<CommentItem> commentDataSet, View.OnClickListener onClick) {

		Log.i(TAG, "CommentAdapter : 실행");

		this.commentData = commentDataSet;
		this.detailContext = detailContext;
		onClickListener = onClick;

		getShard();

	}

	// 리사이클러뷰 행을 표시하는데 사용되는 레이아웃 xml을 가져오는 역할
	@Override
	public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

		Log.i(TAG, "onCreateViewHolder : 실행");

		// create a new view
		// 레이아웃 XML 파일을 View 객체로 만들고(LayoutInflater) 해당 XML 파일을 Context 로부터 불러오고(from) 추가하고 싶은 레이아웃 파일 ID, attachToRoot 가 true 일 경우 추가할
		// View 의 부모 뷰 attachToRoot true 일 경우 root 에 넘긴 뷰의 자식으로 추가되고 false 일 경우 root 가 생성되는 View의 LayoutParam 으로만 사용된다(inflate)
		// 아이템이 들어있는 XML 파일의 레이아웃이 LinearLayout 으로 선언 되어 있기 때문에 LinearLayout 에 담아준다
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);

		// set the view's size, margins, paddings and layout parameters
		// 뷰 홀더에 담아서 리턴해준다
		ViewHolder viewholder = new ViewHolder(view);

		return viewholder;
	}

	// 리사이클러뷰 행에 보여질 ImageView 와 TextView 등을 설정
	@Override
	public void onBindViewHolder(@NonNull final CommentAdapter.ViewHolder viewHolder, final int position) {

		Log.i(TAG, "onBindViewHolder : 실행");

		// get element from your dataset at this position
		// replace the contents of the view with that element

		Log.i(TAG, "댓글 작성한 사용자 이메일 : " + commentData.get(position).getcommentEmail());
		Log.i(TAG, "댓글 작성한 날짜 : " + commentData.get(position).getcommentTime());
		Log.i(TAG, "댓글 내용 : " + commentData.get(position).getcommentData());

		if (commentData.get(position).getWriter().equals(sessionName)) {
			viewHolder.commentModify.setVisibility(View.VISIBLE);
			viewHolder.commentDelete.setVisibility(View.VISIBLE);
		} else {
			viewHolder.commentModify.setVisibility(View.GONE);
			viewHolder.commentDelete.setVisibility(View.GONE);
		}

		viewHolder.commentEmail.setText(commentData.get(position).getWriter());
		viewHolder.commentTime.setText(commentData.get(position).getcommentTime());
		viewHolder.commentContent.setText(commentData.get(position).getcommentData());

		// 어댑터에 데이터를 액티비티로 전달하기 위해서 설정한 태그 값
		viewHolder.rootView.setTag(position);
		Log.i(TAG, "댓글 수정 아이템 태그 설정 값 : " + viewHolder.rootView.getTag());

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

		Log.i(TAG, "getItemCount : 실행");

		return commentData.size();
	}

	// 쉐어드에 저장된 사용자 이름 가져오기 기능 (세션 활용)
	public void getShard() {
		SharedPreferences sharedPreferences = detailContext.getSharedPreferences("sessionName", MODE_PRIVATE);
		sessionName = sharedPreferences.getString("name", "noName");
		sessionEmail = sharedPreferences.getString("email", "noName");


	} // getShard 끝

}
