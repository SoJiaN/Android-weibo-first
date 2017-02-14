package com.song.sjweibo.fragment;

import java.util.ArrayList;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * 
 * @author lenovo
 * 创建本类为实现MainInterface中Fragment的切换效果，并创建hide和show方法
 */
public class FragmentController {
	private int containerId;
	private android.support.v4.app.FragmentManager fm;
	private static FragmentController contorller;
	private ArrayList<Fragment> fragments;
	
	//使用单例模式创建Fragmentcontorller的单一对象
	public static FragmentController getInstance(FragmentActivity activity,int containerId){
		if(contorller==null){
			contorller=new FragmentController(activity,containerId);
		}else{
			
		}
		return contorller;
	}
	
	//单例模式，私有化构造方法
	private FragmentController (FragmentActivity activity,int containerId){
		this.containerId=containerId;
		fm=activity.getSupportFragmentManager();
		initFragment();
	}

	//实例化FragmentTransaction，并向其中添加Fragment，组后提交
	private void initFragment() {
		// TODO Auto-generated method stub
		fragments=new ArrayList<Fragment>();
		fragments.add(new HomeFragment());
		fragments.add(new MessageFragment());
		fragments.add(new SearchFragment());
		fragments.add(new UserFragment());
		
		android.support.v4.app.FragmentTransaction ft=fm.beginTransaction();
		for(Fragment fragment:fragments){
			ft.add(containerId, fragment);
		}
		ft.commitAllowingStateLoss();
	}
	
	//通过FragmentTransaction.hide(containerId)的方式隐藏某个Fragment并提交
	public void hideFragment(){
		android.support.v4.app.FragmentTransaction ft=fm.beginTransaction();
		for(Fragment fragment:fragments){
			if(fragment!=null){
				ft.hide(fragment);
			}
		}
		ft.commitAllowingStateLoss();
	}
	
	//通过FragmentTransaction.show(containerId)的方式显示某个Fragment并提交
	public void showFragment(int position){
		hideFragment();
		Fragment fragment=fragments.get(position);
		android.support.v4.app.FragmentTransaction ft=fm.beginTransaction();
		ft.show(fragment);
		ft.commitAllowingStateLoss();
	}
	
	//使用ArrayList.get(int index)的方法得到某个Fragment
	public Fragment getFragment(int position){
		return fragments.get(position);
	}
	
}
