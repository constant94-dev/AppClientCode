package com.psj.accommodation.Data;


// TODO 메인 액티비티 리사이클러뷰 데이터 임시저장 클래스
public class MainItem {


	String InfoText, InfoTime, InfoScore;

	public MainItem(String infoText, String infoTime, String infoScore) {
		this.InfoText = infoText;
		this.InfoTime = infoTime;
		this.InfoScore = infoScore;
	}

	// 숙박하는 곳 이름 저장 기능
	public String getInfoText() {
		return InfoText;
	}

	// 숙박 기간 저장 기능
	public String getInfoTime() {
		return InfoTime;
	}

	// 숙박한 곳 평점(텍스트) 저장 기능
	public String getInfoScore() {
		return InfoScore;
	}

	// 이미지 저장 기능
	public void setReviewImage() {

	}

	// 평점(별점) 저장 기능
	public void setReviewRatingBar() {

	}

} // MainItem 클래스 끝
