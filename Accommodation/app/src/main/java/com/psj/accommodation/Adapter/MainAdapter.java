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
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

	public static final String TAG = "MainAdapter";
	public Context mainContext;
	// item class(MainItem)를 정의해 놓았음
	private ArrayList<MainItem> mainData;

	// 서버 경로
	String ServerImagePath = "http://54.180.152.167/";


	public static class ViewHolder extends RecyclerView.ViewHolder {
		// 사용될 항목들 선언
		public TextView InfoText, InfoTime, InfoWriter, InfoNum, ImageCount;
		public ImageView ReviewImage;
		public RatingBar ReviewScore;
		public CardView main_card_view;


		public ViewHolder(View v) {
			super(v);
			InfoText = v.findViewById(R.id.InfoText);
			InfoTime = v.findViewById(R.id.InfoTime);
			InfoWriter = v.findViewById(R.id.InfoWriter);
			ReviewImage = v.findViewById(R.id.ReviewImage);
			ReviewScore = v.findViewById(R.id.ReviewScore);
			main_card_view = v.findViewById(R.id.main_card_view);
			ImageCount = v.findViewById(R.id.ImageCount);

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
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_maincard, parent, false);
		// set the view's size, margins, paddings and layout parameters

		ViewHolder viewholder = new ViewHolder(view);
		return viewholder;
	}

	// 리사이클러뷰 행에 보여질 ImageView 와 TextView 등을 설정
	@Override
	public void onBindViewHolder(@NonNull final MainAdapter.ViewHolder viewHolder, final int position) {
		// get element from your dataset at this position
		// replace the contents of the view with that element
		String[] imagePath = mainData.get(position).getInfoImage().split(" ");

		Log.i(TAG, "작성자 : " + mainData.get(position).getInfoWriter());
		Log.i(TAG, "별점 : " + mainData.get(position).getInfoScore());
		Log.i(TAG, "이미지 경로 : " + ServerImagePath + mainData.get(position).getInfoImage());
		Log.i(TAG, "이미지 경로 분할 : " + ServerImagePath + imagePath[0]);
		Log.i(TAG, "이미지 경로 사이즈 : " + imagePath.length);
		Log.i(TAG, "후기 글 : " + mainData.get(position).getInfoReview());


		Glide.with(mainContext).load(Uri.parse(ServerImagePath + imagePath[0])).centerCrop().into(viewHolder.ReviewImage);
		//viewHolder.ReviewImage.setImageURI(Uri.parse(mainData.get(position).getInfoImage()));
		viewHolder.ImageCount.setText("1/" + imagePath.length);
		viewHolder.InfoText.setText(mainData.get(position).getInfoText());
		viewHolder.InfoTime.setText(mainData.get(position).getInfoTime());
		viewHolder.ReviewScore.setRating(mainData.get(position).getInfoScore());
		viewHolder.InfoWriter.setText(mainData.get(position).getInfoWriter());


		// 아이템 클릭시 상세리뷰 화면으로 이동하는 기능
		viewHolder.main_card_view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 리뷰 번호
				String PlaceNum = mainData.get(position).getInfoNum();
				//String PlaceImage = ServerImagePath + mainData.get(position).getInfoImage();
				// 화면 이동
				Intent detailIntent = new Intent(mainContext, ReviewDetailActivity.class);
				// 화면 이동하면서 전달할 데이터
				detailIntent.putExtra("PlaceNum", PlaceNum);
				detailIntent.putExtra("PlaceImage", mainData.get(position).getInfoImage());
				detailIntent.putExtra("PlaceName", viewHolder.InfoText.getText());
				detailIntent.putExtra("PlaceTime", viewHolder.InfoTime.getText());
				detailIntent.putExtra("PlaceScore", viewHolder.ReviewScore.getRating());
				detailIntent.putExtra("Writer", viewHolder.InfoWriter.getText());
				detailIntent.putExtra("PlaceReview", mainData.get(position).getInfoReview());
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
