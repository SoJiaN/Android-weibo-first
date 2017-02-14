package com.song.sjweibo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.CheckBox;

public class InsideCheckBox extends CheckBox {
	public InsideCheckBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public InsideCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public InsideCheckBox(Context context) {
		super(context);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}
}
