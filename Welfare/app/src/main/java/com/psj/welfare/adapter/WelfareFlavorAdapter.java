package com.psj.welfare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.psj.welfare.Data.WelfareFavorItem;
import com.psj.welfare.R;

import java.util.ArrayList;

public class WelfareFlavorAdapter extends RecyclerView.Adapter<WelfareFlavorAdapter.ViewHolder> {

	// item class(MainItem)를 정의해 놓았음
	public Context mainContext;
	private ArrayList<WelfareFavorItem> mainData;

	// 생성자
	public WelfareFlavorAdapter(Context MainContext, ArrayList<WelfareFavorItem> main_welfareRecommendDataSet) {
		this.mainContext = MainContext;
		this.mainData = main_welfareRecommendDataSet;
	}


	public class ViewHolder extends RecyclerView.ViewHolder {

		private ImageView welfareRecommend_TopImage;
		;
		private ImageView welfareRecommend_Top2Image;
		;
		private ImageView welfareRecommend_bottomImage;
		;
		private ImageView welfareRecommend_bottom2Image;
		;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			welfareRecommend_TopImage = itemView.findViewById(R.id.welfareRecommend_TopImage);
			welfareRecommend_Top2Image = itemView.findViewById(R.id.welfareRecommend_Top2Image);
			welfareRecommend_bottomImage = itemView.findViewById(R.id.welfareRecommend_bottomImage);
			welfareRecommend_bottom2Image = itemView.findViewById(R.id.welfareRecommend_bottom2Image);

		}
	}


	@NonNull
	@Override
	public WelfareFlavorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		// create a new view
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_welfarefavor, parent, false);
		// set the view's size, margins, paddings and layout parameters

		ViewHolder viewholder = new ViewHolder(view);
		return viewholder;
	}

	@Override
	public void onBindViewHolder(@NonNull WelfareFlavorAdapter.ViewHolder viewHolder, int position) {
		Glide.with(mainContext).load(mainData.get(position).getWelfareImage()).into(viewHolder.welfareRecommend_TopImage);
		Glide.with(mainContext).load(mainData.get(position).getWelfareImage()).into(viewHolder.welfareRecommend_Top2Image);
		Glide.with(mainContext).load(mainData.get(position).getWelfareImage()).into(viewHolder.welfareRecommend_bottomImage);
		Glide.with(mainContext).load(mainData.get(position).getWelfareImage()).into(viewHolder.welfareRecommend_bottom2Image);

	}

	@Override
	public int getItemCount() {
		return mainData.size();
	}


}
