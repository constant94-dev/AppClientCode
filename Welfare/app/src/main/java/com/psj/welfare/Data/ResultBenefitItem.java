package com.psj.welfare.Data;

public class ResultBenefitItem {

	String RBF_btn;
	String RBF_Title;
	int RBF_btnColor;
	String RBF_titleNum;

	public ResultBenefitItem() {

	}

	public ResultBenefitItem(String RBF_btn, int RBF_btnColor) {
		this.RBF_btn = RBF_btn;
		this.RBF_btnColor = RBF_btnColor;
	}

	public ResultBenefitItem(String RBF_Title) {
		this.RBF_Title = RBF_Title;

	}

	public String getRBF_titleNum() {
		return RBF_titleNum;
	}

	public void setRBF_titleNum(String RBF_titleNum) {
		this.RBF_titleNum = RBF_titleNum;
	}

	public void setRBF_btn(String RBF_btn) {
		this.RBF_btn = RBF_btn;
	}

	public void setRBF_Title(String RBF_Title) {
		this.RBF_Title = RBF_Title;
	}

	public String getRBF_btn() {
		return RBF_btn;
	}

	public String getRBF_Title() {
		return RBF_Title;
	}

	public void setRBF_btnColor(int RBF_btnColor) {
		this.RBF_btnColor = RBF_btnColor;
	}

	public int getRBF_btnColor() {
		return RBF_btnColor;
	}
} // class end
