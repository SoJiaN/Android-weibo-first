package com.song.sjweibo.utils;

import android.graphics.Bitmap;

import com.song.sjweibo.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
/**
 * 封装UniversalImageLoader的所有DisplayOption到这个工具类中
 * @author lenovo
 *
 */
public class ImageOptHelper {
	
	//默认的option
	public static DisplayImageOptions getImgOptions() {
		DisplayImageOptions imgOptions = new DisplayImageOptions.Builder()
		//硬盘缓存
			.cacheOnDisc(true)
			//内存缓存
			.cacheInMemory(true)
			//图片颜色类型
			.bitmapConfig(Bitmap.Config.RGB_565)
			//在加载时显示的图片
			.showImageOnLoading(R.drawable.timeline_image_loading)
			//空地址对应的图片
			.showImageForEmptyUri(R.drawable.timeline_image_loading)
			//加载失败的图片
			.showImageOnFail(R.drawable.timeline_image_failure)
			.build();
		return imgOptions;
	}
	
	//头像的显示option
	public static DisplayImageOptions getAvatarOptions() {
		DisplayImageOptions	avatarOptions = new DisplayImageOptions.Builder()
			.cacheOnDisk(true)
			.cacheInMemory(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.showImageOnLoading(R.drawable.avatar_default)
			.showImageForEmptyUri(R.drawable.avatar_default)
			.showImageOnFail(R.drawable.avatar_default)
			//使图片以圆角边的形式进行展现，传入的参数为圆角边半径
			.displayer(new RoundedBitmapDisplayer(999))
			.build();
		return avatarOptions;
	}
	
	//指定圆角半径的option，传入的参数为半径的值
	public static DisplayImageOptions getCornerOptions(int cornerRadiusPixels) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheOnDisk(true)
			.cacheInMemory(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.showImageOnLoading(R.drawable.timeline_image_loading)
			.showImageForEmptyUri(R.drawable.timeline_image_loading)
			.showImageOnFail(R.drawable.timeline_image_loading)
			.displayer(new RoundedBitmapDisplayer(cornerRadiusPixels)).build();
		return options;
	}
}
