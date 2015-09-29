package com.example.redfootpos.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.Vars;

public class ItemDescAct extends Fragment{

	private TextView tvPriceTag;
	private FragmentActivity act;
	private MainAct core;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// 
		super.onActivityCreated(savedInstanceState);
		
		act = getActivity();
		core = (MainAct)act;
		
		tvPriceTag = (TextView)core.findViewById(R.id.tvPriceTag);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 
		return inflater.inflate(R.layout.act_item_desc, container, false);
	}

}
