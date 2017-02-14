package com.song.sjweibo.activity;


import java.lang.reflect.Field;

import com.song.sjweibo.R;
import com.song.sjweibo.fragment.FragmentController;
import com.song.sjweibo.utils.ToastUtils;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
/**
 * 
 * @author lenovo
 * 本Application主Activity，将4个Fragment添加到其中，并实现切换效果，
 * 进入各个Fragment中继续相关设置
 */
public class MainInterface extends FragmentActivity implements OnCheckedChangeListener {
private RadioGroup rg_tab;
private ImageView iv_add;
private FragmentController con;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_interface);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
		con = FragmentController.getInstance(this, R.id.fl_content);
		con.showFragment(0);
		initView();
	}

	private void initView() {
		// TODO Auto-generated method stub
		rg_tab=(RadioGroup)findViewById(R.id.rg_tab);
		iv_add=(ImageView)findViewById(R.id.iv_add);
		rg_tab.setOnCheckedChangeListener((android.widget.RadioGroup.OnCheckedChangeListener) this);
		iv_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainInterface.this, WriteStatusActivity.class);
				startActivityForResult(intent, 110);
			}
		});
		
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch(checkedId){
		case R.id.rb_home:
			con.showFragment(0);
			break;
		case R.id.rb_search:
			con.showFragment(2);
			break;
		case R.id.rb_message:
			con.showFragment(1);
			break;
		case R.id.rb_user:
			con.showFragment(3);
			break;
		}
		
	}
	@Override
		public void onDetachedFromWindow() {
			// TODO Auto-generated method stub
			super.onDetachedFromWindow();
			try {
			       Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
			       childFragmentManager.setAccessible(true);
			       childFragmentManager.set(this, null);
			    } catch (NoSuchFieldException e) {
			       throw new RuntimeException(e);
			    } catch (IllegalAccessException e) {
			       throw new RuntimeException(e);
			    }
		}
}
