package com.psj.accommodation.Data;

import java.io.Serializable;

// TODO 채팅 데이터 임시저장 클래스
public class ChattingItem {

	private String chatNum;
	private String chatEmail;
	private String chatName;
	private String chatImage;
	private String chatContent;
	private String chatAccess;


	public ChattingItem() {

	}

	public ChattingItem(String chatNum, String chatName, String chatImage, String chatContent) {

		this.chatNum = chatNum;
		this.chatImage = chatImage;
		this.chatName = chatName;
		this.chatContent = chatContent;
	}

	public ChattingItem(String chatNum, String chatAccess, String chatName, String chatImage, String chatContent) {

		this.chatNum = chatNum;
		this.chatAccess = chatAccess;
		this.chatImage = chatImage;
		this.chatName = chatName;
		this.chatContent = chatContent;
	}

	public ChattingItem(String chatNum, String chatName, String chatImage) {
		this.chatNum = chatNum;
		this.chatImage = chatImage;
		this.chatName = chatName;
	}

	public ChattingItem(String chatName) {

		this.chatName = chatName;
	}

	public String getChatContent() {
		return chatContent;
	}

	public String getChatAccess() {
		return chatAccess;
	}

	public void setChatAccess(String chatAccess) {
		this.chatAccess = chatAccess;
	}

	public void setChatContent(String chatContent) {
		this.chatContent = chatContent;
	}

	public String getChatEmail() {
		return chatEmail;
	}

	public String getChatImage() {
		return chatImage;
	}

	public String getChatName() {
		return chatName;
	}

	public String getChatNum() {
		return chatNum;
	}

	public void setChatEmail(String chatEmail) {
		this.chatEmail = chatEmail;
	}

	public void setChatImage(String chatImage) {
		this.chatImage = chatImage;
	}

	public void setChatName(String chatName) {
		this.chatName = chatName;
	}

	public void setChatNum(String chatNum) {
		this.chatNum = chatNum;
	}

} // ChattingItem 클래스 끝