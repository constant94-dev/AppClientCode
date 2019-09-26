package com.psj.accommodation.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.psj.accommodation.R;

public class DetailSlideAdapter extends PagerAdapter {

	public static final String TAG = "DetailSlideAdapter";
	private String[] imagePath;
	// 레이아웃 XML 파일을 해당 View 객체화 한다
	private LayoutInflater layoutInflater;
	private Context context;

	// 서버 경로
	String ServerImagePath = "http://54.180.152.167/";

	public DetailSlideAdapter(Context context, String[] imagePath) {
		this.context = context;
		this.imagePath = imagePath;
	}

	@Override
	public int getCount() {
		// 이미지 개수
		return imagePath.length;
	}

	@Override
	public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
		return view == ((LinearLayout) object);
	}

	@NonNull
	@Override
	public Object instantiateItem(@NonNull ViewGroup container, int position) {

		Log.i(TAG, "instantiateItem " + position + "번째 : " + imagePath[position]);
		Log.i(TAG, "instantiateItem " + position + "번째 : " + ServerImagePath + imagePath[position]);

		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.detailslide, container, false);
		ImageView DetailSlideImage = view.findViewById(R.id.DetailSlideImage);
		TextView DetailImageCount = view.findViewById(R.id.DetailImageCount);
		//Glide.with(context).load(R.drawable.add_photo_black).centerCrop().into(DetailSlideImage);
		Glide.with(context).load(Uri.parse(ServerImagePath + imagePath[position])).centerCrop().into(DetailSlideImage);
		//DetailSlideImage.setImageURI(Uri.parse(ServerImagePath + imagePath[position]));
		DetailImageCount.setText((position + 1) + " / " + imagePath.length);
		container.addView(view);

		return view;
	}

	// 아이템 객체를 파괴하는 기능
	@Override
	public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {


		container.invalidate();


	}
}