package com.example.redfootpos.fragments;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.Vars;
import com.example.redfootpos.Vars.type;
import com.example.redfootpos.model.SalesInfo;
import com.example.redfootpos.object.SimpleCustomAdapter;
import com.example.redfootpos.utils.JsonParser;

public class RetrieveSalesFragment extends Fragment{
	public static final String INVNO = "invno";
	public static final String NAME = "name";
	public static final String ADDRESS = "address";
	public static final String EMAIL = "email";
	public static final String CONTACT = "contact";
	public static final String CUSTNO = "custno";
	public static final String TOTALAMOUNT = "totalamount";
	 // =========================================================================
	 // Variables
	 // =========================================================================
	private ListView lvRetrieveSales;
	public MainAct core;
	public FragmentActivity act;
	
	private ArrayList<SalesInfo> colSales = new  ArrayList<SalesInfo>();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_retrive_sales, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//
		act = getActivity();
		core = (MainAct)act;
		//
		lvRetrieveSales = (ListView)core.findViewById(R.id.lvRetrieveSales);
		//
		GetRemarkedSalesDialog();
	}
	
	@SuppressLint("HandlerLeak")
	private void GetRemarkedSalesDialog(){
		
		final ProgressDialog dialog = ProgressDialog.show(core, "Please wait...", "Get all Remarked item to server");
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				dialog.dismiss();
				//
				setResult();
				//
			}
		};
		Thread process = new Thread(){
			@Override
			public void run() {
				super.run();
				//
				colSales = JsonParser.GetRemarkedSales(core);
				//
				handler.sendEmptyMessage(0);
			}
		};
		process.start();
	}
	
	private void setResult(){
		
		ArrayList<HashMap<String,String>> mapArr = new ArrayList<HashMap<String, String>>();
		
		// count a parked Sales and save to preference
		int count = colSales.size();
		Vars.ParkedCount(core, count, type.SET);
		//
		if(colSales.size() != 0){
			//
			for(SalesInfo instance : colSales){
				//
				HashMap<String,String> map = new HashMap<String, String>();
				map.put("Title", instance.invno);
				map.put("Name", instance.name);
				mapArr.add(map);
			}
			//
			String[] Title = new String[]{"Title","Name"};
			int[] widge = new int[]{R.id.tvRetSalesTitle, R.id.tvRetSalesName};
			
			ListAdapter adapter = new SimpleCustomAdapter(act, mapArr, R.layout.row_retrieve_sales, Title, widge);
			lvRetrieveSales.setAdapter(adapter);
			//
			lvRetrieveSales.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					callFragment(position);
				}
				
			});
		}
	}
	
	@SuppressLint("Recycle")
	private void callFragment(int pos){
		
		Fragment frag = new ShoppingCartFragment();
		
		Bundle bund = new Bundle();
		SalesInfo instance = colSales.get(pos);
		
		bund.putString(INVNO, instance.invno);
		bund.putString(NAME, instance.name);
		bund.putString(ADDRESS, instance.address);
		bund.putString(EMAIL, instance.emailadd);
		bund.putString(CONTACT, instance.contact);
		bund.putString(CUSTNO, instance.custno);
		bund.putString(TOTALAMOUNT, instance.total);
		
		
		frag.setArguments(bund);
		
		FragmentManager fragmag = core.getSupportFragmentManager();
		fragmag.beginTransaction().setCustomAnimations(R.anim.slide_right_in, R.anim.slide_right_out,R.anim.slide_right_inback,R.anim.slide_right_outback).
		replace(R.id.frame_container,frag).commit();
	}

}
