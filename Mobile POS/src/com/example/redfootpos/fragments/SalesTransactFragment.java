package com.example.redfootpos.fragments;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SalesTransactFragment extends Fragment{
	public MainAct core;
	public FragmentActivity act;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		act = getActivity();
		core = (MainAct)act;
//
		Bundle bund = this.getArguments();
		
		if(bund != null){
			String val = bund.getString("test");
			((TextView)core.findViewById(R.id.tvStocksRowProdName)).setText(val);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.sales_transact_fragment, container, false);
	}
	
}
