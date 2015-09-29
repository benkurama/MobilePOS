package com.example.redfootpos.adapter;

import java.util.ArrayList;

import javax.net.ssl.ManagerFactoryParameters;


import android.app.Application;
import android.support.v4.app.FragmentActivity;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.database.CloudfoneDB;
import com.example.redfootpos.model.Item;
import com.example.redfootpos.object.STGVImageView;
import com.example.redfootpos.pager.ManageItemPager;
import com.squareup.picasso.Picasso;

public class STGVMark extends BaseAdapter{
	
	private MainAct core;
	private Application coreApp;
	private FragmentActivity act;
	private ArrayList<Item> listItems = new ArrayList<Item>();
	private ArrayList<Holder> listHolder = new ArrayList<Holder>();
	private ActionMode ActMo;
	
	
	public STGVMark(MainAct core, Application app, FragmentActivity act, int type){
		this.core = core;
		this.coreApp = app;
		this.act = act;
		
		getMoreItem(type);
	}

	public void getMoreItem(int type){
		
		listItems.clear();
		switch(type){
			case 1:
				listItems.addAll(new CloudfoneDB(core).openToRead().selectAllMarksByPhone());
				break;
			case 2:
				listItems.addAll(new CloudfoneDB(core).openToRead().selectAllMarksByTablet());
				break;
			case 3:
				listItems.addAll(new CloudfoneDB(core).openToRead().selectAllMarksByAccessory());
				break;
		}
	}

	@Override
	public int getCount() {
		
		return listItems == null ? 0 : listItems.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		View view = null;
		Item items = listItems.get(position);
		
		if(convertView == null){
			//
			Holder hold = new Holder();
			view = View.inflate(core, R.layout.cell_mark_stgv, null);
			
			hold.tvName = (TextView)view.findViewById(R.id.tvMarkName);
			hold.stgvImg = (STGVImageView)view.findViewById(R.id.stgvImgMark);
			hold.tvPrice = (TextView)view.findViewById(R.id.tvMarkPrice);
			hold.cbSelect = (CheckBox)view.findViewById(R.id.cbMarkSelect);
			
			view.setTag(hold);
			
		} else {
			view = convertView;
		}
		///
		final Holder holder = (Holder)view.getTag();
		
		holder.tvName.setText(items.name);
		holder.tvPrice.setText(items.price);
		
		holder.stgvImg.mWidth = items.width;
		holder.stgvImg.mHeight = items.height;
		
		Picasso.with(coreApp).load(items.url).into(holder.stgvImg);
		
		holder.cbSelect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(holder.cbSelect.isChecked()){
					if(ActMo == null){
						ActMo = core.startActionMode(action);
						//
					}
				}
			}
		});
		
		holder.selectID = items.ID;
		holder.pos = position;
		
		listHolder.add(holder);
		
		int count = listHolder.size();
		
		//Toast.makeText(core, count+"", Toast.LENGTH_SHORT).show();
		///
		return view;
		
	}
	
	private ArrayList<Holder> getSel(){
		
		ArrayList<Holder> itm = new ArrayList<Holder>();
		
		for(int i = 0;i < listHolder.size(); i++){
			if(listHolder.get(i).cbSelect.isChecked()){
				//
				Holder hld = new Holder();
				hld.selectID = listHolder.get(i).selectID;
				hld.pos = listHolder.get(i).pos;
				
				itm.add(hld);
			}
		}
		
		return itm;
	}
	
	private void unselectCheckbox(){
		
		for(int x = 0; x < listHolder.size(); x++){
			
			if(listHolder.get(x).cbSelect.isChecked()){
				listHolder.get(x).cbSelect.setChecked(false);
			}
		}
		
	}
	
	private void exitManage(){
		core.pager.setCurrentItem(0);
		core.getActionBar().setDisplayShowHomeEnabled(true);
		core.setTitle("Home");
		core.mDrawerLayout.openDrawer(Gravity.RIGHT);
	}
	
	class Holder {
		
		STGVImageView stgvImg;
		TextView tvName;
		TextView tvPrice;
		CheckBox cbSelect;
		
		int selectID;
		int pos;
	}
	
	private ActionMode.Callback action = new Callback() {
		
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			
			return false;
		}
		
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			ActMo = null;
			unselectCheckbox();
		}
		
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = core.getMenuInflater();
			inflater.inflate(R.menu.select_marks, menu);
			
			mode.setTitle("Action Mode");
			mode.setSubtitle("Select Items");
			return true;
		}
		
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

			boolean ret = false;
			
			switch (item.getItemId()) {
			case R.id.remove:
				
				
				ArrayList<Holder> holder = getSel();
				
				for(int z = holder.size(); z != 0; z--){
					//
					int x = z-1;
					int intID = holder.get(x).selectID;
					new CloudfoneDB(core).openToWrite().setMarkOutItem(intID);
				}
				
//				listItems.clear();
//				listHolder.clear();
//				getMoreItem();
//				ret = true;
				mode.finish();
//				unselectCheckbox();
				break;
			}
			
			//notifyDataSetChanged();
			
			exitManage();
			
			return ret;
		}
		
		
		
	};

}
