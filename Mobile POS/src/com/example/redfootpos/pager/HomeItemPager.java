package com.example.redfootpos.pager;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.adapter.HomeItemAdapter;
import com.viewpagerindicator.TabPageIndicator;

public class HomeItemPager {
	
	private FragmentActivity act;
	private MainAct core;
	private View view;
	private ViewPager pager, HomePager;
	
	public HomeItemPager(View v, FragmentActivity act, MainAct core, ViewPager pager){
		this.view = v;
		this.act = act;
		this.core = core;
		this.pager = pager;
	}
	
	public void initialize(){
		//
		HomePager = (ViewPager)view.findViewById(R.id.home_pager);
		HomePager.setAdapter(new HomeItemAdapter(act , core, pager));
		
		TabPageIndicator indicator = (TabPageIndicator)view.findViewById(R.id.item_indicator);
		indicator.setViewPager(HomePager);

	}
	
	public void setProperties(){
		
	}

}
