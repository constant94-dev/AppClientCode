package com.psj.accommodation.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.psj.accommodation.Activity.MainActivity;
import com.psj.accommodation.Activity.RetroClient;
import com.psj.accommodation.Adapter.MainAdapter;
import com.psj.accommodation.Data.MainItem;
import com.psj.accommodation.Interface.ApiService;
import com.psj.accommodation.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainDateFragment extends Fragment {

	public static final String TAG = "MainDateFragment";

	private RecyclerView mainRecyclerView;
	private RecyclerView.Adapter mainAdapter;
	private RecyclerView.LayoutManager mainLayoutManager;


	// 메인 아이템 리스트
	private static ArrayList<MainItem> mainDateItemList;

	public MainDateFragment() {
		// Required empty public constructor
	}


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);

		Log.i(TAG, "onAttach 실행");


	}

	// 초기화해야하는 자원들을 onCreate 생명주기에서 해준다
	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "onCreate 실행");


	}

	// 레이아웃을 inflate 하는 곳 버튼 , EditText, TextView 초기화 할 수 있는 생명주기
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		Log.i(TAG, "onCreateView 실행");

		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.activity_fragment_date, container, false);

		mainRecyclerView = (RecyclerView) rootView.findViewById(R.id.main_recycler_date);
		mainRecyclerView.setHasFixedSize(true); // 옵션

		// Linear layout manager 사용
		mainLayoutManager = new LinearLayoutManager(getActivity());
		mainRecyclerView.setLayoutManager(mainLayoutManager);

		// ArrayList 객체 생성
		mainDateItemList = new ArrayList<>();


		// Adapter 셋팅
		mainAdapter = new MainAdapter(getActivity(), mainDateItemList);
		mainRecyclerView.setAdapter(mainAdapter);

		return rootView;

	}

	// 프래그먼트가 모두 생성된 상태 뷰를 변경하는 작업이 가능한 생명주기
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Log.i(TAG, "onActivityCreated 실행");


	}

	// 유저에게 프래그먼트가 보이도록 해주는 생명주기
	@Override
	public void onStart() {
		super.onStart();

		Log.i(TAG, "onStart 실행");
	}

	// 유저에게 프래그먼트가 보여지고 유저와 상호작용이 가능하게 되는 생명주기
	@Override
	public void onResume() {
		super.onResume();


		Log.i(TAG, "onResume 실행");
	}

	// 다른 화면으로 전환될 때 불러오는 생명주기
	@Override
	public void onPause() {
		super.onPause();

		Log.i(TAG, "onPause 실행");
	}

	// 다른 화면으로 전환되면 불러오는 생명주기
	@Override
	public void onStop() {
		super.onStop();

		Log.i(TAG, "onStop 실행");
	}

	// 프래그먼트와 관련된 뷰가 제거되는 생명주기
	@Override
	public void onDestroyView() {
		super.onDestroyView();

		Log.i(TAG, "onDestroyView 실행");
	}

	// 프래그먼트 생성될 때는 초기화할 자원들과 초기화할 뷰를 순서로 생성했다면
	// 프래그먼트 파괴될 때는 뷰를 제거하고 호출하는 생명주기
	@Override
	public void onDestroy() {
		super.onDestroy();

		Log.i(TAG, "onDestroy 실행");
	}

	// 프래그먼트가 액티비티로부터 해제되어질때 호출하는 생명주기기
	@Override
	public void onDetach() {
		super.onDetach();

		Log.i(TAG, "onDestroy 실행");
	}



} // MainDateFragment 클래스 끝
