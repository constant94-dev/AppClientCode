package com.psj.accommodation.Data;


// TODO 메인 액티비티 리사이클러뷰 데이터 임시저장 클래스
public class MainItem {


	String InfoNum, InfoText, InfoTime, InfoWriter, InfoImage;
	float InfoScore;


	public MainItem(String infoNum, String infoText, String infoTime, float infoScore, String infoImage, String infoWriter) {
		this.InfoNum = infoNum;
		this.InfoText = infoText;
		this.InfoTime = infoTime;
		this.InfoScore = infoScore;
		this.InfoImage = infoImage;
		this.InfoWriter = infoWriter;
	}

	// 숙박하는 곳 이름 저장 기능
	public String getInfoText() {
		return InfoText;
	}

	// 숙박 기간 저장 기능
	public String getInfoTime() {
		return InfoTime;
	}

	// 숙박한 곳 평점 저장 기능
	public float getInfoScore() {
		return InfoScore;
	}

	// 이미지 저장 기능
	public String getInfoImage() {
		return InfoImage;
	}


	public String getInfoWriter() {
		return InfoWriter;
	}

} // MainItem 클래스 끝
