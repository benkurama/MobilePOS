package com.example.redfootpos.fragments;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.model.FootTraffic;
import com.example.redfootpos.object.TextFocusChange;
import com.example.redfootpos.utils.JsonParser;
import com.example.redfootpos.utils.Utils;

public class FootTrafficFragment extends Fragment{
	 // =========================================================================
	 // TODO Variables
	 // =========================================================================
	private FragmentActivity act;
	private MainAct core;
	private EditText et8am, et9am, et10am, et11am, et12pm, et1pm, et2pm, et3pm, et4pm, et5pm,
			et6pm, et7pm, et8pm, et9pm, et10pm;
	private Button btnFTSubmit;

	private Boolean isSuccess = false;
	
	private ArrayList<FootTraffic> FTList = new ArrayList<FootTraffic>();
	 // =========================================================================
	 // TODO Overrides
	 // =========================================================================
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fragment_footprint, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//
		act = getActivity();
		core = (MainAct)act;
		//
		et8am = (EditText)core.findViewById(R.id.et8am);
		et9am = (EditText)core.findViewById(R.id.et9am);
		et10am = (EditText)core.findViewById(R.id.et10am);
		et11am = (EditText)core.findViewById(R.id.et11am);
		et12pm = (EditText)core.findViewById(R.id.et12pm);
		et1pm = (EditText)core.findViewById(R.id.et1pm);
		et2pm = (EditText)core.findViewById(R.id.et2pm);
		et3pm = (EditText)core.findViewById(R.id.et3pm);
		et4pm = (EditText)core.findViewById(R.id.et4pm);
		et5pm = (EditText)core.findViewById(R.id.et5pm);
		et6pm = (EditText)core.findViewById(R.id.et6pm);
		et7pm = (EditText)core.findViewById(R.id.et7pm);
		et8pm = (EditText)core.findViewById(R.id.et8pm);
		et9pm = (EditText)core.findViewById(R.id.et9pm);
		et10pm = (EditText)core.findViewById(R.id.et10pm);
		//
		btnFTSubmit = (Button)core.findViewById(R.id.btnFTSubmit);
		//
		et8am.setOnFocusChangeListener(new TextFocusChange(et8am));
		et9am.setOnFocusChangeListener(new TextFocusChange(et9am));
		et10am.setOnFocusChangeListener(new TextFocusChange(et10am));
		et11am.setOnFocusChangeListener(new TextFocusChange(et11am));
		et12pm.setOnFocusChangeListener(new TextFocusChange(et12pm));
		et1pm.setOnFocusChangeListener(new TextFocusChange(et1pm));
		et2pm.setOnFocusChangeListener(new TextFocusChange(et2pm));
		et3pm.setOnFocusChangeListener(new TextFocusChange(et3pm));
		et4pm.setOnFocusChangeListener(new TextFocusChange(et4pm));
		et5pm.setOnFocusChangeListener(new TextFocusChange(et5pm));
		et6pm.setOnFocusChangeListener(new TextFocusChange(et6pm));
		et7pm.setOnFocusChangeListener(new TextFocusChange(et7pm));
		et8pm.setOnFocusChangeListener(new TextFocusChange(et8pm));
		et9pm.setOnFocusChangeListener(new TextFocusChange(et9pm));
		et10pm.setOnFocusChangeListener(new TextFocusChange(et10pm));
		//
		btnFTSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SubmitTraffic();
			}
		});
		//
		LoadCounter();
	}
	 // =========================================================================
	 // TODO Main Functions
	 // =========================================================================
	@SuppressLint("HandlerLeak")
	private void SubmitTraffic(){
		//
		final FootTraffic ft = new FootTraffic();
		ft.i8am = et8am.getText().toString();
		ft.i9am = et9am.getText().toString();
		ft.i10am = et10am.getText().toString();
		ft.i11am = et11am.getText().toString();
		ft.i12pm = et12pm.getText().toString();
		
		ft.i1pm = et1pm.getText().toString();
		ft.i2pm = et2pm.getText().toString();
		ft.i3pm = et3pm.getText().toString();
		ft.i4pm = et4pm.getText().toString();
		ft.i5pm = et5pm.getText().toString();
		
		ft.i6pm = et6pm.getText().toString();
		ft.i7pm = et7pm.getText().toString();
		ft.i8pm = et8pm.getText().toString();
		ft.i9pm = et9pm.getText().toString();
		ft.i10pm = et10pm.getText().toString();
		
		//
		final ProgressDialog dialog = ProgressDialog.show(core, "Please Wait...", "Posting Foot Traffic");
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				//
				dialog.dismiss();
				//
				SetResult();
			}
		};
		
		
		Thread process = new Thread(){
			@Override
			public void run() {
				super.run();
				//
				isSuccess = JsonParser.PostFooterTraffic(ft, core);
				//
				handler.sendEmptyMessage(0);
			}
		};
		
		process.start();
	}
	
	@SuppressLint("HandlerLeak")
	private void LoadCounter(){
		//
		final ProgressDialog dialog = ProgressDialog.show(core, "Please Wait...", "Loading Counters");
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				dialog.dismiss();
				//
				SetFootPrintVal();
			}
		};
		Thread process = new Thread(){
			@Override
			public void run() {
				super.run();
				FTList = JsonParser.GetFooterTraffic(core);
				//
				handler.sendEmptyMessage(0);
			}
		};
		process.start();
	}
	 // =========================================================================
	 // TODO Sub Functions
	 // =========================================================================
	private void SetResult(){
		
		if(isSuccess){
			Utils.me.MessageBox(core, "Success Post");
			core.displayView(0);
		} else {
			Utils.me.MessageBox(core, "Failed Post");
		}
		
	}
	
	private void SetFootPrintVal(){
		
		if(FTList.size() != 0){
			///
			et8am.setText(FTList.get(0).i8am);
			et9am.setText(FTList.get(0).i9am);
			et10am.setText(FTList.get(0).i10am);
			et11am.setText(FTList.get(0).i11am);
			et12pm.setText(FTList.get(0).i12pm);
			et1pm.setText(FTList.get(0).i1pm);
			et2pm.setText(FTList.get(0).i2pm);
			et3pm.setText(FTList.get(0).i3pm);
			et4pm.setText(FTList.get(0).i4pm);
			et5pm.setText(FTList.get(0).i5pm);
			et6pm.setText(FTList.get(0).i6pm);
			et7pm.setText(FTList.get(0).i7pm);
			et8pm.setText(FTList.get(0).i8pm);
			et9pm.setText(FTList.get(0).i9pm);
			et10pm.setText(FTList.get(0).i10pm);
		}
	
	}
}
