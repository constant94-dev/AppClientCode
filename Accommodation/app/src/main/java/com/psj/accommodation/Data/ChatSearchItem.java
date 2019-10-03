package com.psj.accommodation.Data;

// TODO 검색 데이터 임시저장 클래스
// TODO HTTP 통신을한 요청(request)에 대한 String 형태 응답(response)을 받기 위한 폼
public class ChatSearchItem {

	String chatSearchImage, chatSearchEmail, chatSearchName;


	public ChatSearchItem(String chatSearchImage, String chatSearchEmail, String chatSearchName) {
		this.chatSearchImage = chatSearchImage;
		this.chatSearchEmail = chatSearchEmail;
		this.chatSearchName = chatSearchName;

	}

	public String getChatSearchEmail() {
		return chatSearchEmail;
	}

	public String getChatSearchImage() {
		return chatSearchImage;
	}

	public String getChatSearchName() {
		return chatSearchName;
	}


} // ChatSearchItem 클래스 끝
