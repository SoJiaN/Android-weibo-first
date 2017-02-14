package com.song.sjweibo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import com.song.sjweibo.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;

public class EmptyLoadingLayout extends LoadingLayout {
	
	public EmptyLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);
	}

	@Override
	protected int getDefaultDrawableResId() {
		return R.color.transparent;
	}

	@Override
	protected void onLoadingDrawableSet(Drawable imageDrawable) {
		
	}

	@Override
	protected void onPullImpl(float scaleOfLayout) {
		
	}

	@Override
	protected void pullToRefreshImpl() {
		
	}

	@Override
	protected void refreshingImpl() {
		
	}

	@Override
	protected void releaseToRefreshImpl() {
		
	}

	@Override
	protected void resetImpl() {
		
	}


}
