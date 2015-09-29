package com.example.redfootpos.fragments;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.Vars;
import com.example.redfootpos.Vars.type;
import com.example.redfootpos.adapter.StocksRefAdapter;
import com.example.redfootpos.model.Stocks;
import com.example.redfootpos.utils.JsonParser;
import com.example.redfootpos.utils.Utils;

public class StocksReceiveFragment extends Fragment{
	
	private FragmentActivity act;
	private MainAct core;
	private ListView lvStocksRef;
	private EditText etStocksDR;
	private Button btnStocksSearch, btnStockRefCancel, btnStockRefSave;
	
	private View footer;
	
	private ArrayList<Stocks> StockList = new ArrayList<Stocks>();
	private StocksRefAdapter adapter;
	
	private String UpdateMessage = "";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,	Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_stock_receive, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		act = getActivity();
		core =(MainAct)act;
		
		lvStocksRef = (ListView)core.findViewById(R.id.lvStocksRef);
		
		View headerSearch = null;
		LayoutInflater inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		headerSearch = inflater.inflate(R.layout.header_stocks_reference, null);
		
		footer = inflater.inflate(R.layout.footer_stocks_reference, null);
		
		etStocksDR = (EditText)headerSearch.findViewById(R.id.etStocksDR);
		etStocksDR.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
		
		etStocksDR.setText("");
		//
		btnStockRefCancel = (Button)footer.findViewById(R.id.btnStocksRefCance);
		btnStockRefSave = (Button)footer.findViewById(R.id.btnStocksRefSave);
		
		btnStocksSearch = (Button)headerSearch.findViewById(R.id.btnStocksSearch);
		//
		lvStocksRef.addHeaderView(headerSearch);
		//
		lvStocksRef.setAdapter(null);
		
		btnStocksSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DoSearchRef();
			}
		});
		
		btnStockRefCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearListView();
			}
		});
		
		btnStockRefSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveStocks();
			}
		});
	}
	
	@SuppressLint("HandlerLeak")
	private void DoSearchRef(){
		
		final String direcSupplier = etStocksDR.getText().toString().trim();
		final String kioskid = Vars.KioskID(core, "", type.GET);
		
		final ProgressDialog dialog = ProgressDialog.show(core, "Please Wait...", "Loading Stocks from Reference No."); 
		@SuppressWarnings("unused")
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				dialog.dismiss();
				//
				SetResult();
			}
		};
		Thread thread =  new Thread(){
			@Override
			public void run() {
				super.run();
				//
				
				StockList = JsonParser.GetStocksByReference(direcSupplier, kioskid);
				//
				handler.sendEmptyMessage(0);
			}
		};
		thread.start();
	}
	
	private void SetResult(){
		//
		if(StockList.size() != 0){
			//
			int ItemCount = StockList.size();
			
			lvStocksRef.removeFooterView(footer);
			lvStocksRef.addFooterView(footer);
			
			adapter = new StocksRefAdapter(core, StockList, ItemCount);
			lvStocksRef.setAdapter(adapter);
			
			if(StockList.get(0).Checked.equals("t")){
				btnStockRefSave.setEnabled(false);
			} else {
				btnStockRefSave.setEnabled(true);
			}
		} else {
			clearListView();
			Utils.me.MessageBox(core, "No Record(s) Found");
		}
	}
	
	private void clearListView(){
		lvStocksRef.removeFooterView(footer);
		lvStocksRef.setAdapter(null);
		etStocksDR.setText("");
	}
	
	private void setUpdateResult(){
		//
		if(UpdateMessage.equals("Success")){
			Utils.me.MessageBox(core, "Update Success");
			clearListView();
		} else {
			Utils.me.MessageBox(core, UpdateMessage);
		}
	}
	
	@SuppressLint("HandlerLeak")
	private void saveStocks(){
		//
		
		final ProgressDialog dialog = ProgressDialog.show(core, "Please Wait...", "Saving Stock List");
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				dialog.dismiss();
				//
				setUpdateResult();
			}
		};
		Thread process = new Thread(){
			@Override
			public void run() {
				super.run();
				UpdateMessage = JsonParser.PostingStockList(StockList, core);
				//
				handler.sendEmptyMessage(0);
			}
		};
		process.start();
	}
}
