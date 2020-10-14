package com.psj.welfare.adapter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.psj.welfare.Data.WelfareNewsItem;
import com.psj.welfare.R;

import java.util.ArrayList;

public class WelfareNewsAdapter extends RecyclerView.Adapter<WelfareNewsAdapter.ViewHolder> {

	public Context mainContext;
	// item class(MainItem)를 정의해 놓았음
	private ArrayList<WelfareNewsItem> mainData;

	public WelfareNewsAdapter(Context MainContext, ArrayList<WelfareNewsItem> main_welfareGatherDataSet) {
		this.mainContext = MainContext;
		this.mainData = main_welfareGatherDataSet;
	}

	public class ViewHolder extends RecyclerView.ViewHolder {

		public ImageView news_Image;


		public ViewHolder(@NonNull View itemView) {
			super(itemView);


			news_Image = itemView.findViewById(R.id.news_Image);

		}
	}


	@NonNull
	@Override
	public WelfareNewsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
		// create a new view
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_welfarenews, parent, false);
		// set the view's size, margins, paddings and layout parameters

		ViewHolder viewholder = new ViewHolder(view);
		return viewholder;
	}

	@Override
	public void onBindViewHolder(@NonNull WelfareNewsAdapter.ViewHolder viewHolder, int position) {
		Glide.with(mainContext).load(mainData.get(position).getNews_Image()).into(viewHolder.news_Image);


	}

	@Override
	public int getItemCount() {
		return mainData.size();
	}


}