package com.example.redfootpos.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.adapter.PageAdapter;

public class MainFragment extends Fragment{

	private MainAct core;
	private FragmentActivity act;
	private ViewPager pager;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// 
		//
		
		//
		//
		return inflater.inflate(R.layout.fragment_home_main, container, false);
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// 
		super.onActivityCreated(savedInstanceState);
		
		act = getActivity();
		core = (MainAct)act;
		
		pager = core.getRootViewPager();
		pager.setAdapter(new PageAdapter(act, pager, core));
		
	}
	
	
}
