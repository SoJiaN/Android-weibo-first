package com.song.sjweibo.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.song.sjweibo.R;
import com.song.sjweibo.utils.DisplayUtils;

@SuppressLint("NewApi")
public class GroupSettingListView extends LinearLayout {
	
	public interface OnSettingItemClickLister {
		void onItemClick(ViewGroup parent, View item, int position);
	}
	
	private OnSettingItemClickLister onSettingItemClickLister;

	public void setOnSettingItemClickLister(OnSettingItemClickLister onSettingItemClickLister) {
		this.onSettingItemClickLister = onSettingItemClickLister;
	}

	private int[] mIndexs;
	private int[] mImgs;
	private String[] mInfos;
	
	public GroupSettingListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GroupSettingListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GroupSettingListView(Context context) {
		super(context);
	}
	
	public void setAdapterData(int[] indexs, int[] imgs, String[] infos) {
		if(imgs != null && infos != null && imgs.length != infos.length) {
			throw new RuntimeException("imgs.length must be same as info.length");
		}
		
		this.mIndexs = indexs;
		this.mImgs = imgs;
		this.mInfos = infos;
		
		initList();
	}
	
	private void initList() {
		setOrientation(VERTICAL);
		
		if(mIndexs == null) {
			mIndexs = new int[]{mInfos.length};
		}
		
		int start = 0;
		for(int i=0; mIndexs!=null && i<=mIndexs.length; i++) {
			if(i > 0) {
				View view = createEmptyDivider();
				addView(view);
			}
			
			int end = i == mIndexs.length ? mInfos.length : mIndexs[i];
			
			LinearLayout groupLl = new LinearLayout(getContext());
			groupLl.setBackgroundColor(Color.WHITE);
			groupLl.setOrientation(VERTICAL);
			LayoutParams params = new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			groupLl.setLayoutParams(params);
			
			for(int j=start; j<end; j++) {
				final View item = createItem(mImgs[j], mInfos[j]);
				groupLl.addView(item);
				
				final int position = j;
				item.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(onSettingItemClickLister != null) {
							onSettingItemClickLister.onItemClick(
									GroupSettingListView.this, item, position);
						}
					}
				});
				
				if(j < end - 1) {
					View lineDivider = createLineDivider(dp2px(56));
					groupLl.addView(lineDivider);
				}
			}
			
			if(groupLl.getChildCount() > 0) {
				View lineDivider1 = createLineDivider(0);
				addView(lineDivider1);
				
				addView(groupLl);
				
				View lineDivider2 = createLineDivider(0);
				addView(lineDivider2);
			}
			
			start = end;
		}
	}
	
	private int dp2px(int dp) {
		return DisplayUtils.dp2px(getContext(), dp);
	}
	
	private View createEmptyDivider() {
		View view = new View(getContext());
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, dp2px(16));
		view.setLayoutParams(params);
		return view;
	}
	
	private View createLineDivider(int leftMarging) {
		View view = new View(getContext());
		view.setBackgroundResource(R.color.divider_gray);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 1);
		params.setMargins(0, 0, 0, 0);
		view.setLayoutParams(params);
		return view;
	}
	
	private View createItem(int img, String info) {
		LinearLayout linearLayout = new LinearLayout(getContext());
		linearLayout.setGravity(Gravity.CENTER_VERTICAL);
		linearLayout.setOrientation(LinearLayout.HORIZONTAL);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, dp2px(48));
		linearLayout.setLayoutParams(params);
		
		linearLayout.setClickable(true);
		linearLayout.setBackgroundResource(R.drawable.bg_white2gray_sel);
		
	    ImageView iv_setting_left = new ImageView(getContext());
		LayoutParams ivParams = new LayoutParams(dp2px(16), dp2px(16));
		ivParams.setMargins(dp2px(12), 0, dp2px(12), 0);
		iv_setting_left.setLayoutParams(ivParams);
		
		TextView tv_setting_mid = new TextView(getContext());
		LayoutParams tvParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		tvParams.gravity = Gravity.CENTER_VERTICAL;
		tvParams.weight = 1;
		tv_setting_mid.setLayoutParams(tvParams);
		
		ImageView iv_setting_right = new ImageView(getContext());
		iv_setting_right.setLayoutParams(ivParams);
		iv_setting_right.setImageResource(R.drawable.rightarrow);
		
		linearLayout.addView(iv_setting_left);
		linearLayout.addView(tv_setting_mid);
		linearLayout.addView(iv_setting_right);

		if(img == -1) {
			iv_setting_left.setVisibility(View.GONE);
		} else {
			iv_setting_left.setVisibility(View.VISIBLE);
			iv_setting_left.setImageResource(img);
		}
		
		tv_setting_mid.setText(info + "");

		return linearLayout;
	}
}

