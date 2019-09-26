package com.psj.accommodation.Data;

// TODO 회원가입 데이터 임시저장 클래스
// TODO HTTP 통신을한 요청(request)에 대한 String 형태 응답(response)을 받기 위한 폼
public class CommentItem {

	String commentEmail, placeNum, commentData, commentTime, commentNum;

	public CommentItem(String commentNum, String placeNum, String commentEmail, String commentTime, String commentData) {
		this.commentNum = commentNum;
		this.placeNum = placeNum;
		this.commentEmail = commentEmail;
		this.commentTime = commentTime;
		this.commentData = commentData;
	}

	public String getcommentNum() {
		return commentNum;
	}

	public String getcommentEmail() {
		return commentEmail;
	}

	public String getplaceNum() {
		return placeNum;
	}

	public String getcommentData() {
		return commentData;
	}

	public String getcommentTime() {
		return commentTime;
	}


} // Join 클래스 끝
