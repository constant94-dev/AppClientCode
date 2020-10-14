package com.psj.welfare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.psj.welfare.Data.WelfareCaseItem;

import com.psj.welfare.R;

import java.util.ArrayList;

public class WelfareCaseAdapter extends RecyclerView.Adapter<WelfareCaseAdapter.ViewHolder> {

	// item class(MainItem)를 정의해 놓았음
	public Context mainContext;
	private ArrayList<WelfareCaseItem> mainData;

	// 생성자
	public WelfareCaseAdapter(Context MainContext, ArrayList<WelfareCaseItem> main_welfareCaseDataSet) {
		this.mainContext = MainContext;
		this.mainData = main_welfareCaseDataSet;
	}


	public class ViewHolder extends RecyclerView.ViewHolder {

		private ImageView welfareCase_Image;

		public ViewHolder(@NonNull View itemView) {
			super(itemView);
			welfareCase_Image = itemView.findViewById(R.id.welfareCase_Image);

		}
	}


	@NonNull
	@Override
	public WelfareCaseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		// create a new view
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_welfarecase, parent, false);
		// set the view's size, margins, paddings and layout parameters

		ViewHolder viewholder = new ViewHolder(view);
		return viewholder;
	}

	@Override
	public void onBindViewHolder(@NonNull WelfareCaseAdapter.ViewHolder viewHolder, int position) {
		Glide.with(mainContext).load(mainData.get(position).getWelfareImage()).into(viewHolder.welfareCase_Image);

	}

	@Override
	public int getItemCount() {
		return mainData.size();
	}


}
