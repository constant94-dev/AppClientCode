package com.psj.accommodation.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.psj.accommodation.Data.MainItem;
import com.psj.accommodation.R;

import java.util.ArrayList;

// TODO MainActivity 에서 보내준 대량의 데이터를 보여주기 위한 중개자(중개업자) 클래스
// TODO MainActivity -> MainAdapter -> MainActivity
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

	// item class(MainItem)를 정의해 놓았음
	private ArrayList<MainItem> mainData;

	public static class ViewHolder extends RecyclerView.ViewHolder {
		// 사용될 항목들 선언
		public TextView InfoText, InfoTime, InfoScore;

		public ViewHolder(View v) {
			super(v);
			InfoText = v.findViewById(R.id.InfoText);
			InfoTime = v.findViewById(R.id.InfoTime);
			InfoScore = v.findViewById(R.id.InfoScore);

		}
	}

	// 생성자 - 넘어 오는 데이터타입에 유의해야 한다.
	public MainAdapter(ArrayList<MainItem> mainDataSet) {
		mainData = mainDataSet;
	}

	@Override
	public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
		// create a new view
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, parent, false);
		// set the view's size, margins, paddings and layout parameters

		ViewHolder viewholder = new ViewHolder(view);
		return viewholder;
	}

	@Override
	public void onBindViewHolder(@NonNull MainAdapter.ViewHolder viewHolder, int position) {
		// get element from your dataset at this position
		// replace the contents of the view with that element

		viewHolder.InfoText.setText(mainData.get(position).getInfoText());
		viewHolder.InfoTime.setText(mainData.get(position).getInfoTime());
		viewHolder.InfoScore.setText(mainData.get(position).getInfoScore());

	}

	@Override
	public int getItemCount() {
		return mainData.size();
	}
}
