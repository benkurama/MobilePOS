package com.example.redfootpos;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.redfootpos.Vars.type;
import com.example.redfootpos.adapter.NavDrawerListAdapter;
import com.example.redfootpos.fragments.FootTrafficFragment;
import com.example.redfootpos.fragments.MainFragment;
import com.example.redfootpos.fragments.RemittanceFragment;
import com.example.redfootpos.fragments.RetrieveSalesFragment;
import com.example.redfootpos.fragments.ReturnFragment;
import com.example.redfootpos.fragments.SalesHistoryFragment;
import com.example.redfootpos.fragments.ShoppingCartFragment;
import com.example.redfootpos.fragments.StocksReceiveFragment;
import com.example.redfootpos.fragments.UserInfoFragment;
import com.example.redfootpos.model.NavDrawerItem;
import com.example.redfootpos.utils.JsonParser;
import com.example.redfootpos.utils.Utils;
import com.touchmenotapps.widget.radialmenu.menu.v2.RadialMenuItem;
import com.touchmenotapps.widget.radialmenu.menu.v2.RadialMenuRenderer;
import com.touchmenotapps.widget.radialmenu.menu.v2.RadialMenuRenderer.OnRadailMenuClick;

public abstract class BaseActivity extends FragmentActivity{
	// ---------------------------------------------------------------------
	// VARIABLES
	// ---------------------------------------------------------------------
	private CharSequence mTitle;
	private CharSequence mDrawerTitle;
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	
	public DrawerLayout mDrawerLayout;
	public ListView mDrawerList;
	private ArrayList<NavDrawerItem> navDrawerItems;
	
	private NavDrawerListAdapter adapter;
	public ActionBarDrawerToggle mDrawerToggle;
	public MenuItem menuSwipe, menuSearch;
	
	public SearchView searchView;
	
	public int NavPos = 0;
		
	protected abstract int setPageFrag(); 
	// ---------------------------------------------------------------------
	// LIFECYCLE
	// ---------------------------------------------------------------------
	@SuppressLint("Recycle")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// 
		super.onCreate(savedInstanceState);
		
		Utils.me.ActiveErrorLog(this);
		
		
		setContentView(R.layout.act_drawer_layout);
		
		mTitle = mDrawerTitle = getTitle();
		
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
		
		mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
		mDrawerList = (ListView)findViewById(R.id.list_slidermenu);
		
		navDrawerItems = new ArrayList<NavDrawerItem>();
		
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1), true));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
		
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(8, -1)));
		
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], navMenuIcons.getResourceId(9, -1)));
		
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[10], navMenuIcons.getResourceId(10, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[11], navMenuIcons.getResourceId(11, -1)));
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[12], navMenuIcons.getResourceId(12, -1)));
		
		navMenuIcons.recycle();
		
		adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
		mDrawerList.setAdapter(adapter);
		//
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		//
		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());
		
		displayView(setPageFrag());
		//

		//
	}
	// ---------------------------------------------------------------------
	// OVERRIDES
	// ---------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// 
		getMenuInflater().inflate(R.menu.pos_menu, menu);
		
		if (menu != null) {
			menuSwipe = menu.findItem(R.id.swipeIcon);
			menuSwipe.setEnabled(false);	
			//
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			searchView = (SearchView) menu.findItem(R.id.search).getActionView();
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		
			menuSearch = menu.findItem(R.id.search);
		}
		//	
		
		//
		return true;
	}

	@Override
	public void setTitle(CharSequence title) {
		// 
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}
	/**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// 
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// 
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	/***
     * Called when invalidateOptionsMenu() is triggered
     */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
//		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
//		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);
	}
	
	// ---------------------------------------------------------------------
	// MAIN FUNCTIONS
	// ---------------------------------------------------------------------
	public void displayView(int position){
		//
		Fragment frag = null;
		Boolean isLogout = false;
		
		switch (position) {
		case 0:
			frag = new MainFragment();
			
			if (menuSearch != null) {
				menuSearch.setVisible(true);
			}
			getActionBar().setDisplayShowHomeEnabled(true);
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, findViewById(R.id.llRightMenu));
			break;
		case 1:
			onSearchRequested();
			//
			frag = new MainFragment();
			
			if (menuSearch != null) {
				menuSearch.setVisible(true);
			}
			getActionBar().setDisplayShowHomeEnabled(true);
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, findViewById(R.id.llRightMenu));
			break;
		case 2:
			frag = new ShoppingCartFragment();
			PageSettings();
			break;
		case 3:
			frag = new RetrieveSalesFragment();
			PageSettings();
			break;
		case 4:
			frag = new ReturnFragment();
			PageSettings();
			break;
		case 5:
			frag = new SalesHistoryFragment();
			PageSettings();
			break;
		case 7:
			frag = new StocksReceiveFragment();
			PageSettings();
			break;
		case 8:
			frag = new FootTrafficFragment();
			PageSettings();
			break;
		case 9:
			frag = new RemittanceFragment();
			PageSettings();
			break;
		case 11:
			frag = new UserInfoFragment();
			PageSettings();
			break;
		case 12:
			isLogout = true;
			break;
		}
		
		if (frag != null) {
			FragmentManager fragManager = getSupportFragmentManager();
			fragManager.beginTransaction().replace(R.id.frame_container, frag).commit();
			
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			NavPos = position;
			mDrawerLayout.closeDrawer(mDrawerList);
			
		} else {
			
			if(isLogout){
				
				callLogoutDialog();
			}
		}
	}
	// ---------------------------------------------------------------------
	// SUB FUNCTIONS
	// ---------------------------------------------------------------------
	public CharSequence getPageTitle(){
		return mTitle;
	}
	
	@SuppressLint("HandlerLeak")
	public void callLogoutDialog(){
		
		final ProgressDialog dialog = ProgressDialog.show(this, "Please wait...", "Logout is process");
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				dialog.dismiss();
				//
				logoutStart();
				//
			}
		};
		Thread process = new Thread(){
			@Override
			public void run() {
				super.run();
				//
				String empid = Vars.EmpID(getBaseContext(), "", type.GET);
				String fullname = Vars.FirstName(getBaseContext(), "", type.GET) + " " + Vars.LastName(getBaseContext(), "", type.GET);
				
				JsonParser.LogoutTrack(empid, fullname);
				//
				handler.sendEmptyMessage(0);
			}
		};
		process.start();
	}
	
	private void logoutStart(){
		Intent intent = new Intent(this, LoginAct.class);
		startActivity(intent);
		//this.finish();
	}
	
	private void PageSettings(){
		 //
		menuSearch.setVisible(false);
		getActionBar().setDisplayShowHomeEnabled(true);
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, findViewById(R.id.llRightMenu));
	}
	// ---------------------------------------------------------------------
	// IMPLEMENTATION
	// ---------------------------------------------------------------------
	private class SlideMenuClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
			// 
			displayView(position);
		}
	}
	// ---------------------------------------------------------------------
	// FINAL
}