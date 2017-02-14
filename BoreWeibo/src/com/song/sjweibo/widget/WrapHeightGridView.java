package com.song.sjweibo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
/**
 * HomeFragment的适配器StatusAdapter
 * StatusAdapter的图片显示控件GridView(包含imageVeiw)
 * 实现高度自适应的GridView控件 
 * @author lenovo
 *
 */
public class WrapHeightGridView extends GridView {

	public WrapHeightGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public WrapHeightGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WrapHeightGridView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int heightSpec;

		if (getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
			heightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		} else {
			heightSpec = heightMeasureSpec;
		}

		super.onMeasure(widthMeasureSpec, heightSpec);
	}
}
