package com.song.sjweibo.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.song.sjweibo.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.song.sjweibo.entity.PicUrls;
/**
 * 多图显示---数据类
 * 是GridView的Adapter适配器
 * @author lenovo
 * R.layout.item_grid_image是单张图片总布局
 * "@+id/iv_image"是要显示的图片
 * "@+id/iv_delete_image"是图片删除按钮
 */
public class StatusGridImgsAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<PicUrls> datas;
	private GridView gv;
	private ImageLoader imageLoader;

	//通过构造方法，将多图的集合传入
	public StatusGridImgsAdapter(Context context, ArrayList<PicUrls> datas, GridView gv) {
		this.context = context;
		this.datas = datas;
		this.gv = gv;
		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public PicUrls getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	/*计算每个items的宽高
	 * 这个方法会不停的调用直到各个item填满整个父布局：第三个参数的ViewGroup parent
	 * (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_grid_image, null);
			holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		/*计算items的宽高，因为是正方形，所以只计算宽度，高度和宽度相等
		 * 
		 */
		//得到items之间水平间距 
		int horizontalSpacing = gv.getHorizontalSpacing();
		/*得到宽度，并等于高度
		 * gv.getWidth()总宽度-所有水平间距horizontalSpacing * 2 -左右两边列边距
		 * 然后除以总列数3
		 */
		int width = (gv.getWidth() - horizontalSpacing * 2 
				- gv.getPaddingLeft() - gv.getPaddingRight()) / 3;
		//设置ImageVeiw宽高
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
		holder.iv_image.setLayoutParams(params);
		
		//数据绑定，将图片显示在iv_image上
		PicUrls item = getItem(position);
		imageLoader.displayImage(item.getThumbnail_pic(), holder.iv_image);

		return convertView;
	}

	public static class ViewHolder {
		public ImageView iv_image;
	}

}
