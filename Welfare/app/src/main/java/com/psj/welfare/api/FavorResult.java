package com.psj.welfare.api;

import com.google.gson.annotations.SerializedName;

public class FavorResult {

	@SerializedName("아기·어린이")
	String baby;
	@SerializedName("장애인")
	String disorder;

	public String getBaby() {
		return baby;
	}

	public String getDisorder() {
		return disorder;
	}
}
