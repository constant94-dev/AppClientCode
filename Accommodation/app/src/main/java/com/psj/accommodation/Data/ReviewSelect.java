package com.psj.accommodation.Data;

import com.google.gson.annotations.SerializedName;

// TODO 리뷰 데이터 임시저장 클래스
// TODO HTTP 통신을한 요청(request)에 대한 String,float 형태 응답(response)을 받기 위한 폼
public class ReviewSelect {

	@SerializedName("image")
	private String placeImage;
	public String getPlaceImage() {
		return placeImage;
	}

	@SerializedName("name")
	private String placeName;
	public String getPlaceName() {
		return placeName;
	}

	@SerializedName("time")
	private String placeTime;
	public String getPlaceTime() {
		return placeTime;
	}

	@SerializedName("score")
	private float placeScore;
	public float getPlaceScore() {
		return placeScore;
	}

	@SerializedName("writer")
	private String writer;
	public String getWriter() {
		return writer;
	}

} // ReviewSelect 클래스 끝
