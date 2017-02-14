package com.song.sjweibo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.song.sjweibo.activity.MainActivity;
import com.song.sjweibo.activity.MainInterface;
/**
 * 让所有Fragment类继承该类，封装intent方法，绑定Activity为MainInterface
 * @author lenovo
 *
 */
public class BaseFragment extends Fragment {
	
	protected MainInterface activity;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		activity =  (MainInterface)getActivity();
	}
	
	protected void intent2Activity(Class<? extends Activity> tarActivity) {
		Intent intent = new Intent(activity, tarActivity);
		startActivity(intent);
	}
}
