package com.song.sjweibo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.song.sjweibo.R;
import com.song.sjweibo.BaseFragment;

public class SearchFragment extends BaseFragment {
	private View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view=View.inflate(activity, R.layout.frag_search, null);
		return view;
	}

}
