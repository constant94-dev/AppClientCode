package com.psj.accommodation.Data;

// TODO 검색 데이터 임시저장 클래스
// TODO HTTP 통신을한 요청(request)에 대한 String 형태 응답(response)을 받기 위한 폼
public class SearchItem {

	String searchImage, searchName, searchWriter, searchNum, searchReview, searchTime;
	float searchScore;

	public SearchItem(String searchImage, String searchName, String searchWriter, String searchNum, float searchScore, String searchReview, String searchTime) {
		this.searchImage = searchImage;
		this.searchName = searchName;
		this.searchWriter = searchWriter;
		this.searchNum = searchNum;
		this.searchScore = searchScore;
		this.searchReview = searchReview;
		this.searchTime = searchTime;
	}

	public String getSearchNum() {
		return searchNum;
	}

	public String getSearchReview() {
		return searchReview;
	}

	public float getSearchScore() {
		return searchScore;
	}

	public String getSearchTime() {
		return searchTime;
	}

	public String getSearchWriter() {
		return searchWriter;
	}

	public String getSearchImage() {
		return searchImage;
	}

	public String getSearchName() {
		return searchName;
	}

} // Join 클래스 끝
