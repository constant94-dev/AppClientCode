package com.psj.accommodation.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.psj.accommodation.Data.ChatSearchItem;
import com.psj.accommodation.Data.ChattingItem;
import com.psj.accommodation.R;

import java.util.ArrayList;

// TODO MainActivity 에서 보내준 대량의 데이터를 보여주기 위한 중개자(중개업자) 클래스
// TODO MainActivity -> MainAdapter -> MainActivity
public class ChattingAdapter extends RecyclerView.Adapter<ChattingAdapter.ViewHolder> {

	public static final String TAG = "ChattingAdapter";
	public Context chattingContext;
	private ArrayList<ChattingItem> chattingData;


	// 서버 경로
	String ServerImagePath = "http://54.180.152.167/";


	public static class ViewHolder extends RecyclerView.ViewHolder {
		// 사용될 항목들 선언
		ImageView chattingImage;
		TextView chattingContent, chattingUser, chatAccessUser;
		LinearLayout LinearItem, LinearItemAccess, LinearItemBody;


		public ViewHolder(View v) {
			super(v);

			chattingUser = v.findViewById(R.id.chattingUser);
			chattingContent = v.findViewById(R.id.chattingContent);
			chattingImage = v.findViewById(R.id.chattingImage);
			LinearItem = v.findViewById(R.id.LinearItem);
			LinearItemBody = v.findViewById(R.id.LinearItemBody);
			LinearItemAccess = v.findViewById(R.id.LinearItemAccess);
			chatAccessUser = v.findViewById(R.id.chatAccessUser);


		}
	}

	// 생성자 - 넘어 오는 데이터타입에 유의해야 한다.
	public ChattingAdapter(Context chattingContext, ArrayList<ChattingItem> chattingDataSet) {

		this.chattingContext = chattingContext;
		this.chattingData = chattingDataSet;


	}

	// 리사이클러뷰 행을 표시하는데 사용되는 레이아웃 xml을 가져오는 역할
	@Override
	public ChattingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
		// create a new view
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting, parent, false);
		// set the view's size, margins, paddings and layout parameters

		ViewHolder viewholder = new ViewHolder(view);
		return viewholder;
	}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {


		if (chattingData.get(position).getChatSelf().equals(chattingData.get(position).getChatName())) {
			Log.i(TAG, "채팅 보낸사람과 받는사람에 이름이 같아!");

			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolder.LinearItemBody.getLayoutParams();

			layoutParams.gravity = Gravity.RIGHT;
			viewHolder.LinearItemBody.setLayoutParams(layoutParams);

		} else {
			Log.i(TAG, "채팅 보낸사람과 받는사람에 이름이 달라!");
			LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewHolder.LinearItemBody.getLayoutParams();

			layoutParams.gravity = Gravity.LEFT;
			viewHolder.LinearItemBody.setLayoutParams(layoutParams);
		}

		viewHolder.chattingUser.setText(chattingData.get(position).getChatName());
		viewHolder.chattingContent.setText(chattingData.get(position).getChatContent());
		Glide.with(chattingContext).load(ServerImagePath + chattingData.get(position).getChatImage()).centerCrop().into(viewHolder.chattingImage);
	}

	// 리사이클러뷰 보여줄 행 개수 리턴
	@Override
	public int getItemCount() {
		return chattingData.size();
	}


}
