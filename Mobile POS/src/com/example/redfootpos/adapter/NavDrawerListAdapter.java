package com.example.redfootpos.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.redfootpos.R;
import com.example.redfootpos.model.NavDrawerItem;

public class NavDrawerListAdapter extends BaseAdapter{
	
	private Context context;
	private ArrayList<NavDrawerItem> navDrawerItems;
	
	public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
		this.context = context;
		this.navDrawerItems = navDrawerItems;
	}

	public NavDrawerItem setData(int pos){
		return navDrawerItems.get(pos);
	}
	
	@Override
	public int getCount() {
		// 
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		// 
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		// 
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.drawer_list_item, null);
		}
		
		ImageView imgIcon = (ImageView)convertView.findViewById(R.id.icon);
		TextView txtTitle = (TextView)convertView.findViewById(R.id.title);
		TextView txtCount = (TextView)convertView.findViewById(R.id.counter);
		LinearLayout llDivider = (LinearLayout)convertView.findViewById(R.id.llDivider);
		
		imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
		txtTitle.setText(navDrawerItems.get(position).getTitle());
		
		if (navDrawerItems.get(position).getCounterVisibility()) {
			txtCount.setText(navDrawerItems.get(position).getCount());
		}else {
			txtCount.setVisibility(View.GONE);
		}
		
		if(navDrawerItems.get(position).getDividerVisibility()){
			llDivider.setVisibility(View.VISIBLE);
		} else {
			llDivider.setVisibility(View.GONE);
		}
		
		return convertView;
	}

}
