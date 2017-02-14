package com.song.sjweibo.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.song.sjweibo.R;
import com.song.sjweibo.utils.EmotionUtils;

public class EmotionGvAdapter extends BaseAdapter {

	private Context context;
	private List<String> emotionNames;
	private int itemWidth;
	
	public EmotionGvAdapter(Context context, List<String> emotionNames, int itemWidth) {
		this.context = context;
		this.emotionNames = emotionNames;
		this.itemWidth = itemWidth;
	}
	
	@Override
	public int getCount() {
		return emotionNames.size() + 1;
	}

	@Override
	public String getItem(int position) {
		return emotionNames.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView iv = new ImageView(context);
		LayoutParams params = new LayoutParams(itemWidth, itemWidth);
		
		//设置每个item周围的距离
		iv.setPadding(itemWidth/8, itemWidth/8, itemWidth/8, itemWidth/8);
		
		iv.setLayoutParams(params);
		
		if(position == getCount() - 1) {
			//如果是最后一个就设置一个deleted回退按钮
			iv.setImageResource(R.drawable.emotion_delete_icon);
		} else {
			String emotionName = emotionNames.get(position);
			iv.setImageResource(EmotionUtils.getImgByName(emotionName));
		}
		
		
		return iv;
	}

}
