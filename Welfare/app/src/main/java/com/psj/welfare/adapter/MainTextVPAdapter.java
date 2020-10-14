package com.psj.welfare.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.psj.welfare.R;

public class MainTextVPAdapter extends PagerAdapter {

	public static final String TAG = "MainTextVPAdapter";

	private Context context = null;

	// Context 를 전달받아 context 에 저장하는 생성자 추가.
	public MainTextVPAdapter(Context context) {
		this.context = context;
	}

	@NonNull
	@Override
	public Object instantiateItem(@NonNull ViewGroup container, int position) {
		// position 값을 받아 주어진 위치에 페이지를 생성한다

		Log.e(TAG, "instantiateItem 실행!");
		View view = null;

		String[] m_banner_text = new String[2];
		m_banner_text[0] = "서울시 거주 20대 여성이 받을 수 있는 혜택은 3700개";
		m_banner_text[1] = "서울시 거주 30대 남성이 받을 수 있는 혜택은 900개";


		if (context != null) {
			Log.e(TAG, "instantiateItem context not null!");
			// LayoutInflater 를 통해 "/res/layout/item_m_viewpager.xml" 을 뷰로 생성.
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.item_m_viewpager, container, false);

			TextView m_banner_title = view.findViewById(R.id.title);
			m_banner_title.setText(m_banner_text[position]);

			// 메인 문구 색상 변경 시작
			String m_word = m_banner_text[position].toString();
			Log.e(TAG, "메인 배너 텍스트 : " + m_word);
			SpannableString spannableString = new SpannableString(m_word);

			if (m_word.contains("3700개")) {
				String m_Accent = "3700개";
				int start = m_word.indexOf(m_Accent);
				int end = start + m_Accent.length();
				spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff2b2b")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				m_banner_title.setText(spannableString);
			} else if (m_word.contains("900개")) {
				String m_Accent = "900개";
				int start = m_word.indexOf(m_Accent);
				int end = start + m_Accent.length();
				spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff2b2b")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

				m_banner_title.setText(spannableString);
			} else {
				Log.e(TAG, "확인 할 문자가 없네요~");
			}
			// 메인 문구 색상 변경 끝

		} else {
			Log.e(TAG, "instantiateItem context null!");
		}

		Log.e(TAG, "instantiateItem addView!");
		// 뷰페이저에 추가
		container.addView(view);


		return view;
	}

	@Override
	public int getCount() {
		// 사용 가능한 뷰의 개수를 return 한다
		// 전체 페이지 수는 2개로 고정한다
		return 2;
	}

	@Override
	public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
		// 페이지 뷰가 생성된 페이지의 object key 와 같은지 확인한다
		// 해당 object key 는 instantiateItem 메소드에서 리턴시킨 오브젝트이다
		// 즉, 페이지의 뷰가 생성된 뷰인지 아닌지를 확인하는 것
		return view == object;
	}

	@Override
	public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
		// position 값을 받아 주어진 위치의 페이지를 삭제한다
		container.invalidate();
	}
}
