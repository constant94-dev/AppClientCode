package com.psj.accommodation.Data;

// TODO 회원가입 데이터 임시저장 클래스
// TODO HTTP 통신을한 요청(request)에 대한 String 형태 응답(response)을 받기 위한 폼
public class Comment {

	String commentEmail, placeNum, commentData, commentTime;

	public String getcommentTime() {
		return commentTime;
	}

	public void setcommentTime(String time) {
		this.commentTime = time;
	}

	public String getcommentEmail() {
		return commentEmail;
	}

	public void setcommentEmail(String email) {
		this.commentEmail = email;
	}

	public String getplaceNum() {
		return placeNum;
	}

	public void setplaceNum(String placeNum) {
		this.placeNum = placeNum;
	}

	public String getcommentData() {
		return commentData;
	}

	public void setcommentData(String comment) {
		this.commentData = comment;
	}


} // Join 클래스 끝
