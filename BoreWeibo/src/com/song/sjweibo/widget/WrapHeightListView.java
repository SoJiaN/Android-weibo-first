package com.song.sjweibo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;
/**
 * 使用Scrollview中嵌套GridView的布局方式
 * @author lenovo
 *
 */
public class WrapHeightListView extends ListView {

	public WrapHeightListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public WrapHeightListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WrapHeightListView(Context context) {
		super(context);
	}

	/**
	 * 使用int值的类型值
	 */
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
