package com.psj.accommodation.Adapter;

import android.content.Context;
import android.content.Intent;
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

import com.psj.accommodation.Activity.MainActivity;
import com.psj.accommodation.Activity.ReviewDetailActivity;
import com.psj.accommodation.Data.MainItem;
import com.psj.accommodation.R;

import java.util.ArrayList;

// TODO MainActivity 에서 보내준 대량의 데이터를 보여주기 위한 중개자(중개업자) 클래스
// TODO MainActivity -> MainAdapter -> MainActivity
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

	public static final String TAG = "MainAdapter";
	public Context mainContext;
	// item class(MainItem)를 정의해 놓았음
	private ArrayList<MainItem> mainData;


	public static class ViewHolder extends RecyclerView.ViewHolder {
		// 사용될 항목들 선언
		public TextView InfoText, InfoTime, InfoWriter;
		public ImageView ReviewImage;
		public RatingBar ReviewScore;


		public ViewHolder(View v) {
			super(v);
			InfoText = v.findViewById(R.id.InfoText);
			InfoTime = v.findViewById(R.id.InfoTime);
			InfoWriter = v.findViewById(R.id.InfoWriter);
			ReviewImage = v.findViewById(R.id.ReviewImage);
			ReviewScore = v.findViewById(R.id.ReviewScore);


		}
	}

	// 생성자 - 넘어 오는 데이터타입에 유의해야 한다.
	public MainAdapter(Context mainContext, ArrayList<MainItem> mainDataSet) {
		this.mainData = mainDataSet;
		this.mainContext = mainContext;

	}

	// 리사이클러뷰 행을 표시하는데 사용되는 레이아웃 xml을 가져오는 역할
	@Override
	public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
		// create a new view
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, parent, false);
		// set the view's size, margins, paddings and layout parameters

		ViewHolder viewholder = new ViewHolder(view);
		return viewholder;
	}

	// 리사이클러뷰 행에 보여질 ImageView 와 TextView 등을 설정
	@Override
	public void onBindViewHolder(@NonNull MainAdapter.ViewHolder viewHolder, int position) {
		// get element from your dataset at this position
		// replace the contents of the view with that element

		Log.i(TAG, "작성자 : " + mainData.get(position).getInfoWriter());
		Log.i(TAG, "별점 : " + mainData.get(position).getInfoScore());

		viewHolder.InfoText.setText(mainData.get(position).getInfoText());
		viewHolder.InfoTime.setText(mainData.get(position).getInfoTime());
		viewHolder.ReviewScore.setRating(mainData.get(position).getInfoScore());
		viewHolder.InfoWriter.setText(mainData.get(position).getInfoWriter());

		viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent detailIntent = new Intent(mainContext, ReviewDetailActivity.class);
				mainContext.startActivity(detailIntent);
			}
		});


	}

	// 리사이클러뷰 보여줄 행 개수 리턴
	@Override
	public int getItemCount() {
		return mainData.size();
	}
}
