package com.example.redfootpos.fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.Vars;
import com.example.redfootpos.Vars.type;
import com.example.redfootpos.model.EmployeeLog;
import com.example.redfootpos.utils.JsonParser;

public class UserInfoFragment extends Fragment{
	 // =========================================================================
	 // Variables
	 // =========================================================================
	public FragmentActivity act;
	public MainAct core;
	public ListView lvUserInfo;
	private ExpandableListView elvUserInfo;
	private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    
    private ArrayList<EmployeeLog> emplogArr = new ArrayList<EmployeeLog>();
	 // =========================================================================
	 // Overrides
	 // =========================================================================
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fragment_userinfo, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		act = getActivity();
		core = (MainAct)act;
		//
		elvUserInfo = (ExpandableListView)act.findViewById(R.id.elvUserinfo);
		
		callLogtimeServer();
	}
	 // =========================================================================
	 // Main Functions
	 // =========================================================================
	@SuppressLint("HandlerLeak")
	private void callLogtimeServer(){
		//
		final ProgressDialog dialog = ProgressDialog.show(core, "Please wait...", "Getting LogTime Record");
		final Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				dialog.dismiss();
				//
				prepareList();
				//
			}
		};
		Thread process = new Thread(){
			@Override
			public void run() {
				super.run();
				//
				emplogArr = JsonParser.GetLogtimeByUser(Vars.EmpID(core, "", type.GET));
				//
				handler.sendEmptyMessage(0);
			}
		};
		process.start();
	}
	 // =========================================================================
	 // Sub functions
	 // =========================================================================
	private void prepareList(){
		//
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		//
		listDataHeader.add("USER PROFILE");
		listDataHeader.add("LOGIN TIME");
		listDataHeader.add("LOGOUT TIME");
		//
		List<String> userprofile = new ArrayList<String>();
		userprofile.add("Employee ID : "+Vars.EmpID(core, "", type.GET));
		userprofile.add("Username : "+Vars.UserName(core, "", type.GET));
		userprofile.add("Full Name : "+Vars.FirstName(core, "", type.GET) +" "+Vars.LastName(core, "", type.GET));
		userprofile.add("Department : "+Vars.Department(core, "", type.GET));
		userprofile.add("User Level : "+Vars.UserLevel(core, "", type.GET));
		userprofile.add("Mobile No. : "+Vars.MobileNo(core, "", type.GET));
		userprofile.add("Email : "+Vars.Email(core, "", type.GET));
		//
		List<String> logintime = new ArrayList<String>();
		List<String> logouttime = new ArrayList<String>();
		
		for(EmployeeLog ins : emplogArr){
			if(ins.logtype.equals("I")){
				logintime.add(ins.logtime);
			} else if(ins.logtype.equals("O")){
				logouttime.add(ins.logtime);
			}
		}
		//
		listDataChild.put(listDataHeader.get(0), userprofile);
		listDataChild.put(listDataHeader.get(1), logintime);
		listDataChild.put(listDataHeader.get(2), logouttime);
		
		elvUserInfo.setAdapter(new ExListAdapter(core, listDataHeader, listDataChild));
		
		elvUserInfo.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				return false;
			}
		});
		
		elvUserInfo.setOnGroupClickListener(new OnGroupClickListener() {
			
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				
				switch (groupPosition) {
				case 0:
				case 1:
				case 2:
					return false;
				}
				return true;
			}
		});
		//
		elvUserInfo.expandGroup(0);
	}
	 // =========================================================================
	 // Inner class
	 // =========================================================================
	private class ExListAdapter extends BaseExpandableListAdapter{
		
		private Context _context;
	    private List<String> _listDataHeader; 
	    private HashMap<String, List<String>> _listDataChild;
	    
	    public ExListAdapter(Context context, List<String> listHeader, HashMap<String, List<String>> listChild){
	    	this._context = context;
	    	this._listDataHeader = listHeader;
	    	this._listDataChild = listChild;
	    }

		@Override
		public int getGroupCount() {
			
			return this._listDataHeader.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			
			return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return _listDataHeader.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			
			return _listDataChild.get(_listDataHeader.get(groupPosition)).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			
			String headTitle = (String)getGroup(groupPosition);
			if(convertView == null){
				LayoutInflater inflater = (LayoutInflater)this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.rowheader_layout, null);
			}
			TextView title = (TextView)convertView.findViewById(R.id.lblListHeader);
			
			title.setText(headTitle);
			
			
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			
			final String childText = (String)getChild(groupPosition, childPosition);
			
			if(convertView == null){
				LayoutInflater inflater = (LayoutInflater)_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.rowchild_layout, null);
			}
			
			TextView txtChild = (TextView)convertView.findViewById(R.id.lblListItem);
			txtChild.setText(childText);
			
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
	}
}
