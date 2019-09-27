package com.psj.accommodation.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.psj.accommodation.Activity.RetroClient;
import com.psj.accommodation.Activity.ReviewDetailActivity;
import com.psj.accommodation.Data.CommentItem;
import com.psj.accommodation.Data.SearchItem;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

// TODO 데이터베이스에서 검색한 결과를 전달하여 대량의 데이터를 보여주기 위한 중개자(중개업자) 클래스
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

	public static final String TAG = "SearchAdapter";

	Context searchContext;

	String serverURL = "http://54.180.152.167/";

	// 검색 아이템 리스트
	private static ArrayList<SearchItem> searchItemData;

	public static class ViewHolder extends RecyclerView.ViewHolder {
		// 사용될 항목들 선언
		ImageView SearchItemImage;
		TextView SearchItemTitle, SearchItemWriter;
		View rootView;

		public ViewHolder(View v) {
			super(v);

			Log.i(TAG, "ViewHolder : 실행");

			SearchItemImage = v.findViewById(R.id.SearchItemImage);
			SearchItemTitle = v.findViewById(R.id.SearchItemTitle);
			SearchItemWriter = v.findViewById(R.id.SearchItemWriter);
			rootView = v;

		}
	} // ViewHolder 끝

	// 생성자 - 넘어 오는 데이터타입에 유의해야 한다.
	public SearchAdapter(Context context, ArrayList<SearchItem> searchItemList) {

		Log.i(TAG, "CommentAdapter : 실행");

		this.searchContext = context;
		this.searchItemData = searchItemList;

	} // SearchAdapter 생성자 끝

	// 리사이클러뷰 행을 표시하는데 사용되는 레이아웃 xml을 가져오는 역할
	@Override
	public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

		Log.i(TAG, "onCreateViewHolder : 실행");

		// create a new view
		// 레이아웃 XML 파일을 View 객체로 만들고(LayoutInflater) 해당 XML 파일을 Context 로부터 불러오고(from) 추가하고 싶은 레이아웃 파일 ID, attachToRoot 가 true 일 경우 추가할
		// View 의 부모 뷰 attachToRoot true 일 경우 root 에 넘긴 뷰의 자식으로 추가되고 false 일 경우 root 가 생성되는 View의 LayoutParam 으로만 사용된다(inflate)
		// 아이템이 들어있는 XML 파일의 레이아웃이 LinearLayout 으로 선언 되어 있기 때문에 LinearLayout 에 담아준다
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);

		// set the view's size, margins, paddings and layout parameters
		// 뷰 홀더에 담아서 리턴해준다
		ViewHolder viewholder = new ViewHolder(view);

		return viewholder;
	} // onCreateViewHolder 끝

	// 리사이클러뷰 행에 보여질 ImageView 와 TextView 등을 설정
	@Override
	public void onBindViewHolder(@NonNull final SearchAdapter.ViewHolder viewHolder, final int position) {

		Log.i(TAG, "onBindViewHolder : 실행");


		String[] imagePath = searchItemData.get(position).getSearchImage().split(" ");

		Log.i(TAG, "검색한 이미지 경로 첫번째 : " + serverURL + imagePath[0]);

		// get element from your dataset at this position
		// replace the contents of the view with that element
		Glide.with(searchContext).load(Uri.parse(serverURL + imagePath[0])).centerCrop().into(viewHolder.SearchItemImage);

		viewHolder.SearchItemTitle.setText(searchItemData.get(position).getSearchName());
		viewHolder.SearchItemWriter.setText(searchItemData.get(position).getSearchWriter());

		viewHolder.rootView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "검색 결과 아이템 클릭 !");

				Intent detailIntent = new Intent(searchContext, ReviewDetailActivity.class);

				// 화면 이동하면서 전달할 데이터
				detailIntent.putExtra("PlaceNum", searchItemData.get(position).getSearchNum());
				detailIntent.putExtra("PlaceImage", searchItemData.get(position).getSearchImage());
				detailIntent.putExtra("PlaceName", searchItemData.get(position).getSearchName());
				detailIntent.putExtra("PlaceTime", searchItemData.get(position).getSearchTime());
				detailIntent.putExtra("PlaceScore", searchItemData.get(position).getSearchScore());
				detailIntent.putExtra("Writer", searchItemData.get(position).getSearchWriter());
				detailIntent.putExtra("PlaceReview", searchItemData.get(position).getSearchReview());

				searchContext.startActivity(detailIntent);
			}
		});

	} // onBindViewHolder 끝

	// 리사이클러뷰 보여줄 행 개수 리턴
	@Override
	public int getItemCount() {

		Log.i(TAG, "getItemCount : 실행");

		return searchItemData.size();
	} // getItemCount 끝

} // SearchAdapter 클래스 끝
