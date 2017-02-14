package com.song.sjweibo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.song.sjweibo.R;
import com.song.sjweibo.BaseFragment;
import com.song.sjweibo.utils.TitleBuilder;
import com.song.sjweibo.utils.ToastUtils;
/**
 * 
 * @author lenovo
 * 设置HomeFragment后进行MessageFragment的设置。得到view中include的设置
 */
public class MessageFragment extends BaseFragment {
	private View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view=View.inflate(activity, R.layout.frag_message, null);
		//通过builder模式进行titlebar的设置
		new TitleBuilder(view)
		.setTitleText("Message")
		.setRightImage(R.drawable.ic_launcher)
		.setRightOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ToastUtils.showToast(activity, "right click", Toast.LENGTH_SHORT);
			}
		});
		return view;
	}

}
