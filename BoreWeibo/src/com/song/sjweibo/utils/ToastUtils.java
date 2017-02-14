package com.song.sjweibo.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	
	private static Toast toast;
	
	/**
	 * 显示Toast
	 * @param context
	 * @param text
	 * @param duration
	 */
	public static void showToast(Context context, CharSequence text, int duration) {
		if(toast == null) {    
			toast = Toast.makeText(context, text, duration);    
        } else {    
        	toast.setText(text);      
        	toast.setDuration(duration);    
        }    
		toast.show();
	}

}
