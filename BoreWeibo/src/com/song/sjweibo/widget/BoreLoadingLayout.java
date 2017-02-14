package com.song.sjweibo.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.song.sjweibo.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Orientation;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;

public class BoreLoadingLayout extends LoadingLayout {
	
	// 下拉完成后的帧动画,嚼包子
	private AnimationDrawable animationDrawableLoading;
	
	// 随着下拉动作变化的帧动画,塞包子
	private AnimationDrawable animationDrawable;
		
	public BoreLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs) {
		super(context, mode, scrollDirection, attrs);
		initFrame(context);
		
		mHeaderImage.setImageDrawable(animationDrawable);
	}

	private void initFrame(Context context) {
		Resources res = context.getResources();
		
		animationDrawableLoading = new AnimationDrawable();
		animationDrawableLoading.addFrame(
				res.getDrawable(R.drawable.dropdown_loading_00), 150);
		animationDrawableLoading.addFrame(
				res.getDrawable(R.drawable.dropdown_loading_01), 150);
		animationDrawableLoading.addFrame(
				res.getDrawable(R.drawable.dropdown_loading_02), 150);
		animationDrawableLoading.setOneShot(false);
		
		animationDrawable = new AnimationDrawable();
		animationDrawable.addFrame(
				res.getDrawable(R.drawable.dropdown_anim_00), 100);
		animationDrawable.addFrame(
				res.getDrawable(R.drawable.dropdown_anim_01), 100);
		animationDrawable.addFrame(
				res.getDrawable(R.drawable.dropdown_anim_02), 100);
		animationDrawable.addFrame(
				res.getDrawable(R.drawable.dropdown_anim_03), 100);
		animationDrawable.addFrame(
				res.getDrawable(R.drawable.dropdown_anim_04), 100);
		animationDrawable.addFrame(
				res.getDrawable(R.drawable.dropdown_anim_05), 100);
		animationDrawable.addFrame(
				res.getDrawable(R.drawable.dropdown_anim_06), 100);
		animationDrawable.addFrame(
				res.getDrawable(R.drawable.dropdown_anim_07), 100);
		animationDrawable.addFrame(
				res.getDrawable(R.drawable.dropdown_anim_08), 100);
		animationDrawable.addFrame(
				res.getDrawable(R.drawable.dropdown_anim_09), 100);
		animationDrawable.addFrame(
				res.getDrawable(R.drawable.dropdown_anim_10), 100);
	}

	@Override
	protected int getDefaultDrawableResId() {
		Log.i("PULL", "getDefaultDrawableResId()");
		return R.drawable.dropdown_anim_00;
	}

	@Override
	protected void onLoadingDrawableSet(Drawable imageDrawable) {
		Log.i("PULL", "onLoadingDrawableSet()");
	}

	// 正在下拉, 根据下拉距离播放指定帧数的塞包子进嘴巴动画
	// scaleOfLayout为下拉距离和下拉头布局的高度比例, 当scaleOfLayout > 1f时,送手后会进行刷新
	@Override
	protected void onPullImpl(float scaleOfLayout) {
		// 一共11帧动画,计算每一帧对应的scale数
		float scaleOfFrame = 1f / animationDrawable.getNumberOfFrames();
		// 当前下拉scale值除以每一帧scale计算出当前应该播放哪一帧动画
		int idx = (int) (scaleOfLayout / scaleOfFrame);
		// 超过最大数量时(对应scaleOfLayout > 1f),停留在最后一帧
		if(idx > animationDrawable.getNumberOfFrames() - 1) {
			idx = animationDrawable.getNumberOfFrames() - 1;
		}
		// 设置当前帧角标
		animationDrawable.selectDrawable(idx);
		Log.i("PULL", "onPullImpl() ... scaleOfLayout = " + scaleOfLayout + " ... idx = " + idx);
	}

	@Override
	protected void pullToRefreshImpl() {
		Log.i("PULL", "pullToRefreshImpl()");
	}

	// 下拉释放后开始刷新ing, 此时播放嚼啊嚼动画
	@Override
	protected void refreshingImpl() {
		Log.i("PULL", "refreshingImpl()");
		// 松手释放后,开始加载另一个loading动画
		mHeaderImage.clearAnimation();
		mHeaderImage.setImageDrawable(animationDrawableLoading);
		animationDrawableLoading.start();
	}

	@Override
	protected void releaseToRefreshImpl() {
		Log.i("PULL", "releaseToRefreshImpl()");
	}

	@Override
	protected void resetImpl() {
		Log.i("PULL", "resetImpl()");
		
		// 开始下拉前重置,加载设置吃包子动画
		mHeaderImage.clearAnimation();
		mHeaderImage.setImageDrawable(animationDrawable);
	}

}
