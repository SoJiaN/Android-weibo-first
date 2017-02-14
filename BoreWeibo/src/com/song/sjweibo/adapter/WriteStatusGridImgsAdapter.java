package com.song.sjweibo.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.song.sjweibo.R;

public class WriteStatusGridImgsAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<Uri> datas;
	private GridView gv;

	public WriteStatusGridImgsAdapter(Context context, ArrayList<Uri> datas, GridView gv) {
		this.context = context;
		this.datas = datas;
		this.gv = gv;
	}

	@Override
	public int getCount() {
		return datas.size() > 0 ? datas.size() + 1 : 0;
	}

	@Override
	public Uri getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		//使用viewHolder工具类
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_grid_image, null);
			holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
			holder.iv_delete_image = (ImageView) convertView.findViewById(R.id.iv_delete_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		//获取当前控件宽度
		int horizontalSpacing = gv.getHorizontalSpacing();
		//减去水平间隙然后除以三得到每个控件的宽度
		int width = (gv.getWidth() - horizontalSpacing * 2 
				- gv.getPaddingLeft() - gv.getPaddingRight()) / 3;
		
		//设置宽高
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
		holder.iv_image.setLayoutParams(params);
		
		if(position < getCount() - 1) {
			// 前几个位置
			Uri item = getItem(position);
			holder.iv_image.setImageURI(item);
			
			holder.iv_delete_image.setVisibility(View.VISIBLE);
			holder.iv_delete_image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					datas.remove(position);
					notifyDataSetChanged();
				}
			});
		} else {
			//最后一个位置是加号按钮
			holder.iv_image.setImageResource(R.drawable.compose_pic_add_more);
			holder.iv_delete_image.setVisibility(View.GONE);
		}

		return convertView;
	}

	public static class ViewHolder {
		public ImageView iv_image;
		public ImageView iv_delete_image;
	}

}
