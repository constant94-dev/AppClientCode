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

import com.bumptech.glide.Glide;
import com.psj.accommodation.Activity.ReviewDetailActivity;
import com.psj.accommodation.Data.MainItem;
import com.psj.accommodation.R;

import java.util.ArrayList;

// TODO MainActivity 에서 보내준 대량의 데이터를 보여주기 위한 중개자(중개업자) 클래스
// TODO MainActivity -> MainAdapter -> MainActivity
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

	public static final String TAG = "MainAdapter";
	public Context mainContext;
	// item class(MainItem)를 정의해 놓았음
	private ArrayList<MainItem> mainData;

	// 서버 경로
	String ServerImagePath = "http://54.180.152.167/";


	public static class ViewHolder extends RecyclerView.ViewHolder {
		// 사용될 항목들 선언



		public ViewHolder(View v) {
			super(v);


		}
	}

	// 생성자 - 넘어 오는 데이터타입에 유의해야 한다.
	public ChatAdapter(Context mainContext, ArrayList<MainItem> mainDataSet) {
		this.mainData = mainDataSet;
		this.mainContext = mainContext;

	}

	// 리사이클러뷰 행을 표시하는데 사용되는 레이아웃 xml을 가져오는 역할
	@Override
	public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
		// create a new view
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_maincard, parent, false);
		// set the view's size, margins, paddings and layout parameters

		ViewHolder viewholder = new ViewHolder(view);
		return viewholder;
	}

	// 리사이클러뷰 행에 보여질 ImageView 와 TextView 등을 설정
	@Override
	public void onBindViewHolder(@NonNull final ChatAdapter.ViewHolder viewHolder, final int position) {
		// get element from your dataset at this position
		// replace the contents of the view with that element



	}

	// 리사이클러뷰 보여줄 행 개수 리턴
	@Override
	public int getItemCount() {
		return mainData.size();
	}
}
