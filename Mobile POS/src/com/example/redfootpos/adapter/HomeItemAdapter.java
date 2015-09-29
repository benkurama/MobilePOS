package com.example.redfootpos.adapter;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.fragments.ItemDescAct;
import com.example.redfootpos.fragments.PeopleFragment;
import com.example.redfootpos.fragments.ShoppingCartFragment;
import com.example.redfootpos.pager.HomePager;

public class HomeItemAdapter extends PagerAdapter{
	
	private static final String[] CONTENT = new String[] { "Phones","Tablets","Accessories" };
	private int PAGE_COUNT = 2;
	private MainAct core;
	private FragmentActivity act;
	private ViewPager pager;
	
	private static final int PHONE = 1;
	private static final int TABLET = 2;
	private static final int ACCESSORY = 3;
	
	public HomeItemAdapter( FragmentActivity act, MainAct core, ViewPager pager) {
		
		this.act = act;
		this.core = core;
		this.pager = pager;
	}

	@Override
	public int getCount() {
		
		//return PAGE_COUNT;
		return CONTENT.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object o) {
		// TODO Auto-generated method stub
		return view == o ;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		
		//super.destroyItem(container, position, object);
		container.removeView((View)object);
	}

	
	
	@Override
    public CharSequence getPageTitle(int position) {
        return CONTENT[position % CONTENT.length].toUpperCase();
    }
	

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		
		View view = null;
		
		LayoutInflater inflater = act.getLayoutInflater();
		
		switch(position){
			case 0:
				view = inflater.inflate(R.layout.fragment_home, null);
				HomePager home = new HomePager(view, act, core, pager);
				home.initialize(PHONE);
			break;
			case 1:
				view = inflater.inflate(R.layout.fragment_home, null);
				HomePager home2 = new HomePager(view, act, core, pager);
				home2.initialize(TABLET);
				break;
			case 2:
				view = inflater.inflate(R.layout.fragment_home, null);
				HomePager home3 = new HomePager(view, act, core, pager);
				home3.initialize(ACCESSORY);
				break;
		}
		
		container.addView(view);
		
		//return super.instantiateItem(container, position);
		return view;
	}


	
	

}
