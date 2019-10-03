package com.psj.accommodation.Adapter;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.psj.accommodation.Data.ChatSearchItem;
import com.psj.accommodation.Data.MainItem;
import com.psj.accommodation.R;

import java.util.ArrayList;

// TODO MainActivity 에서 보내준 대량의 데이터를 보여주기 위한 중개자(중개업자) 클래스
// TODO MainActivity -> MainAdapter -> MainActivity
public class ChatSearchAdapter extends RecyclerView.Adapter<ChatSearchAdapter.ViewHolder> {

	public static final String TAG = "ChatSearchAdapter";
	public Context searchContext;
	// item class(MainItem)를 정의해 놓았음
	private ArrayList<ChatSearchItem> searchData;

	// 서버 경로
	String ServerImagePath = "http://54.180.152.167/";


	public static class ViewHolder extends RecyclerView.ViewHolder {
		// 사용될 항목들 선언

		ImageView chatSearchItemImage;
		TextView chatSearchItemName;

		public ViewHolder(View v) {
			super(v);

			chatSearchItemImage = v.findViewById(R.id.chatSearchItemImage);
			chatSearchItemName = v.findViewById(R.id.chatSearchItemName);


			chatSearchItemImage.setVisibility(View.VISIBLE);
			chatSearchItemImage.setBackground(new ShapeDrawable(new OvalShape()));
			if (Build.VERSION.SDK_INT >= 21) {
				chatSearchItemImage.setClipToOutline(true);
			}

		}
	}

	// 생성자 - 넘어 오는 데이터타입에 유의해야 한다.
	public ChatSearchAdapter(Context searchContext, ArrayList<ChatSearchItem> searchDataSet) {
		this.searchData = searchDataSet;
		this.searchContext = searchContext;

	}

	// 리사이클러뷰 행을 표시하는데 사용되는 레이아웃 xml을 가져오는 역할
	@Override
	public ChatSearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
		// create a new view
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatsearch, parent, false);
		// set the view's size, margins, paddings and layout parameters

		ViewHolder viewholder = new ViewHolder(view);
		return viewholder;
	}

	// 리사이클러뷰 행에 보여질 ImageView 와 TextView 등을 설정
	@Override
	public void onBindViewHolder(@NonNull final ChatSearchAdapter.ViewHolder viewHolder, final int position) {
		// get element from your dataset at this position
		// replace the contents of the view with that element

		Log.i(TAG, "초대 유저 이미지 : " + searchData.get(position).getChatSearchImage());
		Log.i(TAG, "초대 유저 이름 : " + searchData.get(position).getChatSearchName());
		Log.i(TAG, "초대 유저 이메일 : " + searchData.get(position).getChatSearchEmail());


		Glide.with(searchContext).load("http://54.180.152.167/" + searchData.get(position).getChatSearchImage()).centerCrop().into(viewHolder.chatSearchItemImage);
		viewHolder.chatSearchItemName.setText(searchData.get(position).getChatSearchName());

	}

	// 리사이클러뷰 보여줄 행 개수 리턴
	@Override
	public int getItemCount() {
		return searchData.size();
	}
}
