package com.example.redfootpos.pager;


import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import com.bulletnoid.android.widget.StaggeredGridView.StaggeredGridView;
import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.adapter.STGVMark;

public class ManageItemPager {
	
	private View view;
	private static FragmentActivity act;
	private static MainAct core;
	
	private static StaggeredGridView stgvMark;
	private static STGVMark adapter;
	
	
	public ManageItemPager(){}
	
	public ManageItemPager(View v, FragmentActivity act, MainAct core){
		this.view = v;
		this.act = act;
		this.core = core;
	}
	
	public void initialize(){
		stgvMark = (StaggeredGridView)view.findViewById(R.id.stgvMark);
	}
	
	public static void setProperties(int type){
		//
		int margin = act.getResources().getDimensionPixelOffset(R.dimen.stgv_margin);
		
		stgvMark.setItemMargin(margin);
		stgvMark.setPadding(margin, 0, margin,0);
		
		View footerView;
		LayoutInflater inflater = (LayoutInflater)core.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		footerView = inflater.inflate(R.layout.layout_no_more_item, null);
		stgvMark.setFooterView(footerView);
		
		adapter = new STGVMark(core, core.getApplication(), act, type);
		stgvMark.setAdapter(adapter);
		
		adapter.notifyDataSetChanged();
		
		stgvMark.setOnLoadmoreListener(new StaggeredGridView.OnLoadmoreListener() {
            @Override
            public void onLoadmore() {
                //new LoadMoreTask().execute();
            }
        });
	}
	
	public static void refresh(){
		//stgvMark.setAdapter(null);
		
	}
	///////
}
