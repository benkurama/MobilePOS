package com.example.redfootpos.pager;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bulletnoid.android.widget.StaggeredGridView.StaggeredGridView;
import com.bulletnoid.android.widget.StaggeredGridView.StaggeredGridView.OnLoadmoreListener;
import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.adapter.STGVAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshStaggeredGridView;

public class HomePager {

	private View view;
	private PullToRefreshStaggeredGridView stgv;
	private FragmentActivity act;
	private MainAct core;
	private STGVAdapter adapter;
	private ViewPager pager;
	
	public HomePager(View v, FragmentActivity act, MainAct core, ViewPager pager){
		this.view = v;
		this.act = act;
		this.core = core;
		this.pager = pager;
	}
	
	public void initialize(int type){
		
		stgv = (PullToRefreshStaggeredGridView)view.findViewById(R.id.stgv);
		
		View footerView;
		LayoutInflater inflater = (LayoutInflater)core.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		footerView = inflater.inflate(R.layout.layout_loading_footer, null);
		stgv.getRefreshableView().setFooterView(footerView);
		
		adapter = new STGVAdapter(core, core.getApplication(), act, pager, type);
		stgv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
		
		stgv.setAdapter(adapter);
		
		stgv.getLoadingLayoutProxy().setReleaseLabel("Release to Refresh from Web service");
		
		stgv.setOnRefreshListener(new OnRefreshListener<StaggeredGridView>() {

			@Override
			public void onRefresh(
				PullToRefreshBase<StaggeredGridView> refreshView) {
				adapter.PullToRefresh();
				stgv.onRefreshComplete();
			}
		});
		
		stgv.setOnLoadmoreListener(new OnLoadmoreListener() {
			
			@Override
			public void onLoadmore() {
				
			}
		});
	}	
}
