package com.example.redfootpos.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bulletnoid.android.widget.StaggeredGridView.StaggeredGridView;
import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.pager.BuyPager;
import com.example.redfootpos.pager.HomeItemPager;
import com.example.redfootpos.pager.HomePager;
import com.example.redfootpos.pager.ItemPager;
import com.example.redfootpos.pager.ManageItemPager;

public class PageAdapter extends PagerAdapter{
	 // =========================================================================
	 // TODO Variables
	 // =========================================================================
	private FragmentActivity act;
	private int PAGE_COUNT = 4;
	private ViewPager pager;
	private MainAct core;
	
	
	public PageAdapter(FragmentActivity act, ViewPager pager, MainAct core){
		this.act = act;
		this.pager = pager;
		this.core = core;
	}
	
	
	@Override
	public int getCount() {
		// 
		return PAGE_COUNT;
	}

	@Override
	public boolean isViewFromObject(View view, Object o) {
		// 
		return view == o;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// 
		//super.destroyItem(container, position, object);
		container.removeView((View)object);
	}
	


	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// 
		
		View nView = null;
		LayoutInflater inflater = act.getLayoutInflater();
		
		switch (position) {
		case 0:

//			nView = inflater.inflate(R.layout.fragment_home, null);
//			HomePager home = new HomePager(nView, act, core, pager);
//			home.initialize();
			nView = inflater.inflate(R.layout.fragment_home_pager, null);
			HomeItemPager homeItem = new HomeItemPager(nView, act, core, pager);
			homeItem.initialize();
			break;
		case 1:
			nView = inflater.inflate(R.layout.act_item_desc, null);
			ItemPager item = new ItemPager(nView, act, core);
			item.initialize();
			break;
		case 2:
			nView = inflater.inflate(R.layout.act_buy_item,null);
			BuyPager buy = new BuyPager(nView, act, core);
			buy.initialize();
			break;
		case 3:
			nView = inflater.inflate(R.layout.act_manage_mark, null);
			ManageItemPager mip = new ManageItemPager(nView, act, core);
			mip.initialize();
			break;
			
			default:
			break;
		}
		//
		container.addView(nView);
		
		return nView;
	}
	
	

}
