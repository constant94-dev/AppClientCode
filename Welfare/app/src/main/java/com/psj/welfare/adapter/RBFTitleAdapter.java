package com.psj.welfare.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.psj.welfare.Data.ResultBenefitItem;
import com.psj.welfare.R;

import java.util.ArrayList;

public class RBFTitleAdapter extends RecyclerView.Adapter<RBFTitleAdapter.MyViewHolder> {

	public Context RBFTitleContext;
	private ArrayList<ResultBenefitItem> RBF_Data;
	private static View.OnClickListener onClickListener;

	// Provide a reference to the views for each data item
	// Complex data items may need more than one view per item, and
	// you provide access to all the views for a data item in a view holder
	public static class MyViewHolder extends RecyclerView.ViewHolder {
		// each data item is just a string in this case
		public TextView RBF_title;
		public View rootView;

		public MyViewHolder(View v) {
			super(v);
			RBF_title = v.findViewById(R.id.RBF_title);
			rootView = v;

			v.setClickable(true);
			v.setEnabled(true);
			v.setOnClickListener(onClickListener);
		}
	}

	// Provide a suitable constructor (depends on the kind of dataset)
	public RBFTitleAdapter(Context RBFTitleContext,ArrayList<ResultBenefitItem> RBFTitle_ListSet, View.OnClickListener onClick) {
		this.RBFTitleContext = RBFTitleContext;
		this.RBF_Data = RBFTitle_ListSet;
		this.onClickListener = onClick;
	}

	// Create new views (invoked by the layout manager)
	@Override
	public RBFTitleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
														   int viewType) {
		// create a new view
		View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item_rbftitle, parent, false);

		MyViewHolder vh = new MyViewHolder(v);
		return vh;
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(RBFTitleAdapter.MyViewHolder holder, int position) {
		// - get element from your dataset at this position
		// - replace the contents of the view with that element

		holder.RBF_title.setText(RBF_Data.get(position).getRBF_Title());

		// tag - label
		holder.rootView.setTag(position);
	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return RBF_Data.size();
	}

	public ResultBenefitItem getRBFTitle(int position) {
		return RBF_Data != null ? RBF_Data.get(position) : null;
	}
}
