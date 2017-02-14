package com.song.sjweibo.entity;
/**
 * “我的”微博Fragment的数据实现
 * @author lenovo
 *
 */
public class UserItem {

	public UserItem(boolean isShowTopDivider, int leftImg, String subhead, String caption) {
		this.isShowTopDivider = isShowTopDivider;
		this.leftImg = leftImg;
		this.subhead = subhead;
		this.caption = caption;
	}

	//是否显示顶部的分割部分
	private boolean isShowTopDivider;
	//左边的图像
	private int leftImg;
	//主体信息
	private String subhead;
	//主体信息
	private String caption;

	public boolean isShowTopDivider() {
		return isShowTopDivider;
	}

	public void setShowTopDivider(boolean isShowTopDivider) {
		this.isShowTopDivider = isShowTopDivider;
	}

	public int getLeftImg() {
		return leftImg;
	}

	public void setLeftImg(int leftImg) {
		this.leftImg = leftImg;
	}

	public String getSubhead() {
		return subhead;
	}

	public void setSubhead(String subhead) {
		this.subhead = subhead;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

}
