package com.song.sjweibo.entity;

import android.graphics.Bitmap;

public class BrowserPic {

	private PicUrls pic;
	private boolean isOriginalPic;
	private Bitmap bitmap;

	public PicUrls getPic() {
		return pic;
	}

	public void setPic(PicUrls pic) {
		this.pic = pic;
	}

	public boolean isOriginalPic() {
		return isOriginalPic;
	}

	public void setOriginalPic(boolean isOriginalPic) {
		this.isOriginalPic = isOriginalPic;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

}
