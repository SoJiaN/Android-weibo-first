package com.song.sjweibo.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.song.sjweibo.utils.DisplayUtils;

public class WrapHeightImageView extends ImageView {

	public WrapHeightImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public WrapHeightImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WrapHeightImageView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int wMeasureSpec = widthMeasureSpec;
		int hMeasureSpec = heightMeasureSpec;
		
		System.out.println("width " + MeasureSpec.toString(wMeasureSpec));
		System.out.println("height " + MeasureSpec.toString(hMeasureSpec));
		
		Drawable drawable = getDrawable();
		if(drawable != null) {
			int width = DisplayUtils.getScreenWidthPixels((Activity)getContext());
	        int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
	        hMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		}
		
		System.out.println("after width " + MeasureSpec.toString(wMeasureSpec));
		System.out.println("after height " + MeasureSpec.toString(hMeasureSpec));
		
		super.onMeasure(wMeasureSpec, hMeasureSpec);
	}
}
