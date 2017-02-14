package com.song.sjweibo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.song.sjweibo.R;
//下划线指示器控件
public class UnderlineIndicatorView extends LinearLayout {

	private int mCurrentPosition;

	public UnderlineIndicatorView(Context context) {
		this(context, null);
	}

	public UnderlineIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		//设置为水平
		setOrientation(HORIZONTAL);
		//平均分配四个控件
		int count = 4;
		for (int i = 0; i < count; i++) {
			View view = new View(context);
			
			//设置宽度=0.weight=1让其平均分布
			LayoutParams params = new LayoutParams(0, LayoutParams.MATCH_PARENT);
			params.weight = 1;
			
			view.setLayoutParams(params);
			view.setBackgroundResource(R.color.transparent);
			addView(view);
		}
	}
	
	//创建一个无动画效果的设置。方便对两个菜单栏动画进行同步操作，隐藏的部分就调用无动画的方法进行状态更新
	public void setCurrentItemWithoutAnim(int item) {
		//更新背景颜色
		final View oldChild = getChildAt(mCurrentPosition);
		final View newChild = getChildAt(item);

		oldChild.setBackgroundResource(R.color.transparent);
		newChild.setBackgroundResource(R.color.orange);
		//更新当前选中位置
		mCurrentPosition = item;
		invalidate();
	}

	public void setCurrentItem(int item) {
		//分别获取当前视图和移动后的视图
		final View oldChild = getChildAt(mCurrentPosition);
		final View newChild = getChildAt(item);

		//新建位移动画
		TranslateAnimation translateAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, item - mCurrentPosition,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0);
		//设置动画的持续时间，单位为毫秒
		translateAnimation.setDuration(200);
		//设置监听，在动画结束后更新它的选中状态
		translateAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				
			}

			//动画重复
			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				//移动完成后更改位置背景
				oldChild.setBackgroundResource(R.color.transparent);
				newChild.setBackgroundResource(R.color.orange);
			}
		});
		//执行这个动画
		oldChild.setAnimation(translateAnimation);
		//更新position动态
		mCurrentPosition = item;
		//因为视图发生了变化，调用invalidate进行视图的更新
		invalidate();
	}
}