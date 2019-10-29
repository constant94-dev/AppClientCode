package com.psj.accommodation.Adapter;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.psj.accommodation.Data.ChatSearchItem;
import com.psj.accommodation.R;

import java.util.ArrayList;

// TODO MainActivity 에서 보내준 대량의 데이터를 보여주기 위한 중개자(중개업자) 클래스
// TODO MainActivity -> MainAdapter -> MainActivity
public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {

	public static final String TAG = "ChatRoomAdapter";
	public Context chatContext;
	private ArrayList<ChatSearchItem> chatData;
	private String nameAndCount = "";
	static View.OnClickListener onClickListener;

	// 서버 경로
	String ServerImagePath = "http://54.180.152.167/";


	public static class ViewHolder extends RecyclerView.ViewHolder {
		// 사용될 항목들 선언
		ImageView chatImageOne, chatImageTwo, chatImageThree, chatImageFour;
		TextView chatNameAndCount, chatNowContent;
		LinearLayout chatLinear;

		public View rootView;

		public ViewHolder(View v) {
			super(v);
			chatImageOne = v.findViewById(R.id.chatImageOne);
			chatImageTwo = v.findViewById(R.id.chatImageTwo);
			chatImageThree = v.findViewById(R.id.chatImageThree);
			chatImageFour = v.findViewById(R.id.chatImageFour);
			chatNameAndCount = v.findViewById(R.id.chatNameAndCount);
			chatNowContent = v.findViewById(R.id.chatNowContent);
			chatLinear = v.findViewById(R.id.chatLinear);

			// 내가 선택한 아이템이 몇번째 인지 알아내기 위한 준비 작업

			rootView = v;
			// 버튼이 아니기 때문에 클릭을 할 수 있게 설정한다
			v.setClickable(true);
			// 활성화 상태인지 아닌지를 설정한다 setClickable 보다 큰 개념이다
			v.setEnabled(true);
			v.setOnClickListener(onClickListener);

			chatImageOne.setBackground(new ShapeDrawable(new OvalShape()));
			if (Build.VERSION.SDK_INT >= 21) {
				chatImageOne.setClipToOutline(true);
			}

			chatImageTwo.setBackground(new ShapeDrawable(new OvalShape()));
			if (Build.VERSION.SDK_INT >= 21) {
				chatImageTwo.setClipToOutline(true);
			}

			chatImageThree.setBackground(new ShapeDrawable(new OvalShape()));
			if (Build.VERSION.SDK_INT >= 21) {
				chatImageThree.setClipToOutline(true);
			}


		}
	}

	// 생성자 - 넘어 오는 데이터타입에 유의해야 한다.
	public ChatRoomAdapter(Context chatContext, ArrayList<ChatSearchItem> chatDataSet, View.OnClickListener onClick) {

		this.chatContext = chatContext;
		this.chatData = chatDataSet;
		onClickListener = onClick;

	}

	// 리사이클러뷰 행을 표시하는데 사용되는 레이아웃 xml을 가져오는 역할
	@Override
	public ChatRoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
		// create a new view
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatroom, parent, false);
		// set the view's size, margins, paddings and layout parameters

		ViewHolder viewholder = new ViewHolder(view);
		return viewholder;
	}

	// 리사이클러뷰 행에 보여질 ImageView 와 TextView 등을 설정
	@Override
	public void onBindViewHolder(@NonNull final ChatRoomAdapter.ViewHolder viewHolder, final int position) {

		String[] name = chatData.get(position).getChatSearchName().split(" ");
		String[] image = chatData.get(position).getChatSearchImage().split(" ");

		Log.i(TAG, "name -> " + name.length);
		Log.i(TAG, "image -> " + image.length);

		if (name.length == 1 && image.length == 1) {
			Log.i(TAG, "채팅 초대 인원 1명 입니다");

			viewHolder.chatImageOne.setVisibility(View.VISIBLE);

			Log.i(TAG, "초대 인원 1명 이미지 경로 : " + image[0]);
			Glide.with(chatContext).load(Uri.parse(ServerImagePath + image[0])).centerCrop().override(50, 50).into(viewHolder.chatImageOne);

			viewHolder.chatNameAndCount.setText(chatData.get(position).getChatSearchName() + "  " + (name.length + 1));

		} else if (name.length == 2 && image.length == 2) {
			Log.i(TAG, "채팅 초대 인원 2명 입니다");

			viewHolder.chatImageOne.setVisibility(View.VISIBLE);
			viewHolder.chatImageTwo.setVisibility(View.VISIBLE);

			Glide.with(chatContext).load(Uri.parse(ServerImagePath + image[0])).centerCrop().override(50, 50).into(viewHolder.chatImageOne);
			Glide.with(chatContext).load(Uri.parse(ServerImagePath + image[1])).centerCrop().override(50, 50).into(viewHolder.chatImageTwo);

			viewHolder.chatNameAndCount.setText(chatData.get(position).getChatSearchName() + "  " + (name.length + 1));

		} else if (name.length == 3 && image.length == 3) {
			Log.i(TAG, "채팅 초대 인원 3명입니다");

			viewHolder.chatImageOne.setVisibility(View.VISIBLE);
			viewHolder.chatImageTwo.setVisibility(View.VISIBLE);
			viewHolder.chatImageThree.setVisibility(View.VISIBLE);

			Glide.with(chatContext).load(Uri.parse(ServerImagePath + image[0])).centerCrop().into(viewHolder.chatImageOne);
			Glide.with(chatContext).load(Uri.parse(ServerImagePath + image[1])).centerCrop().into(viewHolder.chatImageTwo);
			Glide.with(chatContext).load(Uri.parse(ServerImagePath + image[2])).centerCrop().into(viewHolder.chatImageThree);

			viewHolder.chatNameAndCount.setText(chatData.get(position).getChatSearchName() + "  " + (name.length + 1));

		} else if (name.length > 3 && image.length > 3) {
			Log.i(TAG, "채팅 초대 인원 4명 이상 입니다");

			viewHolder.chatImageOne.setVisibility(View.VISIBLE);
			viewHolder.chatImageTwo.setVisibility(View.VISIBLE);
			viewHolder.chatImageThree.setVisibility(View.VISIBLE);
			viewHolder.chatImageFour.setVisibility(View.VISIBLE);

			Glide.with(chatContext).load(Uri.parse(ServerImagePath + image[0])).centerCrop().into(viewHolder.chatImageOne);
			Glide.with(chatContext).load(Uri.parse(ServerImagePath + image[1])).centerCrop().into(viewHolder.chatImageTwo);
			Glide.with(chatContext).load(Uri.parse(ServerImagePath + image[2])).centerCrop().into(viewHolder.chatImageThree);
			Glide.with(chatContext).load(Uri.parse(ServerImagePath + image[3])).centerCrop().into(viewHolder.chatImageFour);

			viewHolder.chatNameAndCount.setText(chatData.get(position).getChatSearchName() + "  " + (name.length + 1));
		}


		// 어댑터에 데이터를 액티비티로 전달하기 위해서 설정한 태그 값
		viewHolder.rootView.setTag(chatData.get(position).getChatRoomNum());
		Log.i(TAG, "채팅 방 아이템 태그 설정 값 : " + viewHolder.rootView.getTag());


	}

	// 리사이클러뷰 보여줄 행 개수 리턴
	@Override
	public int getItemCount() {
		return chatData.size();
	}
}
