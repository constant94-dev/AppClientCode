package com.psj.accommodation.Data;

import com.google.gson.annotations.SerializedName;

// TODO 리뷰 데이터 임시저장 클래스
// TODO HTTP 통신을한 요청(request)에 대한 String,float 형태 응답(response)을 받기 위한 폼
public class Review {

	String placeNum, placeName, placeTime, placeImage, writer;
	float placeScore;

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getPlaceTime() {
		return placeTime;
	}

	public void setPlaceTime(String timeChoice) {
		this.placeTime = timeChoice;
	}

	public String getPlaceImage() {
		return placeImage;
	}

	public void setPlaceImage(String placeImage) {
		this.placeImage = placeImage;
	}

	public float getPlaceScore() {
		return placeScore;
	}

	public void setPlaceScore(float placeScore) {
		this.placeScore = placeScore;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public void setPlaceNum(String placeNum) {
		this.placeNum = placeNum;
	}

	public String getPlaceNum() {
		return placeNum;
	}


} // Join 클래스 끝
