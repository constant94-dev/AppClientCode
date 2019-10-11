package com.psj.accommodation.Data;

import java.io.Serializable;
import java.util.ArrayList;

// TODO 검색 데이터 임시저장 클래스
// TODO HTTP 통신을한 요청(request)에 대한 String 형태 응답(response)을 받기 위한 폼
public class ChatSearchItem implements Serializable {

	private String chatSearchImage;
	private String chatSearchEmail;
	private String chatSearchName;
	private String chatRoomContent;
	private String chatRoomNum;


	public ChatSearchItem() {

	}

	public ChatSearchItem(String chatRoomNum, String chatSearchName, String chatSearchImage) {
		this.chatRoomNum = chatRoomNum;
		this.chatSearchName = chatSearchName;
		this.chatSearchImage = chatSearchImage;

	}

	public ChatSearchItem(String chatSearchName, String chatSearchImage) {

		this.chatSearchName = chatSearchName;
		this.chatSearchImage = chatSearchImage;
	}

	public String getChatRoomNum() {
		return chatRoomNum;
	}

	public void setChatRoomNum(String chatRoomNum) {
		this.chatRoomNum = chatRoomNum;
	}

	public void setChatSearchImage(String chatSearchImage) {
		this.chatSearchImage = chatSearchImage;
	}

	public void setChatSearchEmail(String chatSearchEmail) {
		this.chatSearchEmail = chatSearchEmail;
	}

	public void setChatSearchName(String chatSearchName) {
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

	public String getChatRoomContent() {
		return chatRoomContent;
	}

	public void setChatRoomContent(String chatRoomContent) {
		this.chatRoomContent = chatRoomContent;
	}


} // ChatSearchItem 클래스 끝
