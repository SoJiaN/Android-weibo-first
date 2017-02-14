package com.song.sjweibo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.song.sjweibo.R;
import com.song.sjweibo.activity.UserInfoActivity;
import com.song.sjweibo.entity.Emotion;
/**
 * 微博文本变色，可点击文本和表情的实现
 * @author lenovo
 *	使用
 */
public class StringUtils {
	
	public static SpannableString getWeiboContent(final Context context, final TextView tv, String source) {
		return getWeiboContent(context, tv, source, true);
	}

	public static SpannableString getWeiboContent(final Context context, final TextView tv, String source, boolean clickable) {
		SpannableString spannableString = new SpannableString(source);
		Resources res = context.getResources();
		
		//(@用户部分)和(#话题#)部分	正则表示
		String regexLink = "(@[\u4e00-\u9fa5\\w]+)|(#[\u4e00-\u9fa5\\w]+#)";
		Pattern patternLink = Pattern.compile(regexLink);
		Matcher matcherLink = patternLink.matcher(spannableString);
		
		String regexEmoji = "\\[([\u4e00-\u9fa5\\w])+\\]";
		Pattern patternEmoji = Pattern.compile(regexEmoji);
		Matcher matcherEmoji = patternEmoji.matcher(spannableString);
		
		//为textview设置点击事件的特殊处理
		if(matcherLink.find() && clickable) {
			//如果找到了就setMovementMethod。并reset让他从头遍历
			tv.setMovementMethod(new LinkTouchMovementMethod());
			matcherLink.reset();
		}
		
		for(;;) { // 如果可以匹配到
			if(matcherLink.find()) {
				final String key = matcherLink.group(); // 获取匹配到的具体字符
				int start = matcherLink.start(); // 匹配字符串的开始位置
				
				if(clickable) {
					/*
					 *  @和#可点击
					 *  创建可点击的文本对象
					 */
					@SuppressWarnings("deprecation")
					/*
					 * 该类继承自ClickableSpan，创建匿名TouchableSpan对象
					 */
					TouchableSpan clickableSpan = new TouchableSpan(
							context.getResources().getColor(R.color.txt_at_blue), 
							context.getResources().getColor(R.color.txt_at_blue),
							context.getResources().getColor(R.color.bg_at_blue)) {
						@Override
						public void onClick(View widget) {
							if(key.startsWith("@")) {
								//@用户名部分的跳转操作，跳转到UserInfoActivity类中
								Intent intent = new Intent(context, UserInfoActivity.class);
								//传给个人中心页面的用户名要去掉@符号
								intent.putExtra("userName", key.substring(1));
								context.startActivity(intent);
							} else if(key.startsWith("#")) {
								//点击话题部分，显示Toast
								ToastUtils.showToast(context, "查看话题 :" + key, 0);
							} else if(tv.getParent() instanceof LinearLayout){
								((LinearLayout) tv.getParent()).performClick();
							}
						}
					};
					spannableString.setSpan(clickableSpan, start, start + key.length(), 
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				} else {
					// @和#不可点击
					int blueColor = context.getResources().getColor(R.color.txt_at_blue);
					ForegroundColorSpan colorSpan = new ForegroundColorSpan(blueColor);
					spannableString.setSpan(colorSpan, start, start + key.length(), 
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				
			} else if(matcherEmoji.find()) {
				String key = matcherEmoji.group(); // 获取匹配到的具体字符
				int start = matcherEmoji.start(); // 匹配字符串的开始位置
				
				//根据中文名得到图片资源
				Integer imgRes = Emotion.getImgByName(key);
				
				if(imgRes != null) {
					Options options = new Options();
					//得到bitmap的options属性
					options.inJustDecodeBounds = true;
					BitmapFactory.decodeResource(res, imgRes, options);
					//利用options.outWidth得到宽高
					int scale = (int) (options.outWidth / 32);
					options.inJustDecodeBounds = false;
					options.inSampleSize = scale;
					//得到经过Scale后的宽高设置后的Bitmap
					Bitmap bitmap = BitmapFactory.decodeResource(res, imgRes, options);
					
					ImageSpan span = new ImageSpan(context, bitmap);
					spannableString.setSpan(span, start, start + key.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			} else {
				break;
			}
		}
		return spannableString;
	}
	
	/*
	 * ClickableSpan默认是有下划线的，所以要重写该方法。
	 */
	private abstract static class TouchableSpan extends ClickableSpan {
		private boolean mIsPressed;
		private int mPressedBackgroundColor;
		private int mNormalTextColor;
		private int mPressedTextColor;

		public TouchableSpan(int normalTextColor, int pressedTextColor,
				int pressedBackgroundColor) {
			mNormalTextColor = normalTextColor;
			mPressedTextColor = pressedTextColor;
			mPressedBackgroundColor = pressedBackgroundColor;
		}

		public void setPressed(boolean isSelected) {
			mIsPressed = isSelected;
		}

		@Override
		public void updateDrawState(TextPaint ds) {
			super.updateDrawState(ds);
			ds.setColor(mIsPressed ? mPressedTextColor : mNormalTextColor);
			ds.bgColor = mIsPressed ? mPressedBackgroundColor : Color.TRANSPARENT;
			//没有下划线
			ds.setUnderlineText(false);
		}
	}
	
	static class LinkTouchMovementMethod extends LinkMovementMethod {
	    private TouchableSpan mPressedSpan;

	    @Override
	    public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent event) {
	    	//看看textview是啥？？？
	    	
	        if (event.getAction() == MotionEvent.ACTION_DOWN) {
	            mPressedSpan = getPressedSpan(textView, spannable, event);
	            
	            //查看是否是mPressedSpan是textview的全部还是一部分
	            
	            if (mPressedSpan != null) {
	                mPressedSpan.setPressed(true);
	                //将选择锚设置为开始，将选择边设置为停止。
	                Selection.setSelection(spannable, spannable.getSpanStart(mPressedSpan),
	                        spannable.getSpanEnd(mPressedSpan));
	            }
	        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
	            TouchableSpan touchedSpan = getPressedSpan(textView, spannable, event);
	            if (mPressedSpan != null && touchedSpan != mPressedSpan) {
	                mPressedSpan.setPressed(false);
	                mPressedSpan = null;
	                Selection.removeSelection(spannable);
	            }
	        } else {
	            if (mPressedSpan != null) {
	                mPressedSpan.setPressed(false);
	                super.onTouchEvent(textView, spannable, event);
	            }
	            mPressedSpan = null;
	            //从文本中删除光标
	            Selection.removeSelection(spannable);
	        }
	        return true;
	    }

	    private TouchableSpan getPressedSpan(TextView textView, Spannable spannable, MotionEvent event) {

	    	//点击位置xy坐标
	        int x = (int) event.getX();
	        int y = (int) event.getY();

	        //点击位置x坐标减去textview左侧和layout的填充距离
	        x -= textView.getTotalPaddingLeft();
	        y -= textView.getTotalPaddingTop();	
	        
	        //点击位置相对于textview的距离加上textview左边滚动出屏幕的textview距离
	        x += textView.getScrollX();
	        y += textView.getScrollY();

	        Layout layout = textView.getLayout();
	        
	        
	        /*
	         * 传入光标位置，得到行号
	         * 
	         * 获取对应于指定垂直位置的行号。
	         * 如果你要求一个高于0的位置，你得到0;
	         * 如果你要求在文本底部下面的位置，你得到最后一行
	         */
	        int line = layout.getLineForVertical(y);
	        
	        
	        /*
	         * 传入行号和水平浮动，得到字符偏移量
	         * 
	         *已在API级别1中添加
	         *int getOffsetForHorizo​​ntal（int line， 浮动水平）
	         *获取位置最接近指定水平位置的指定线上的字符偏移量。
	         * 参数
	         * 行int,水平浮动
	         * 返回       int
	         */
	        int off = layout.getOffsetForHorizontal(line, x);

	        /**
	         * 传入字符偏移量和对象类型，得到标记对象数组
	         * 
	         * 返回附加到此CharSequence的指定切片的标记对象数组，其类型为指定类型或其子类。
	         * 如果想要所有对象而不管类型，请为类型指定Object.class。
	         */
	        TouchableSpan[] link = spannable.getSpans(off, off, TouchableSpan.class);
	        TouchableSpan touchedSpan = null;
	        if (link.length > 0) {
	        	//??????????
	            touchedSpan = link[0];
	        }
	        return touchedSpan;
	    }

	}

}
