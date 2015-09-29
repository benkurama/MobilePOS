package com.example.redfootpos.fragments;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.Vars;
import com.example.redfootpos.adapter.RemittanceAdapter;
import com.example.redfootpos.model.Remittance;
import com.example.redfootpos.model.SalesInfo;
import com.example.redfootpos.utils.JsonParser;
import com.example.redfootpos.utils.Utils;

public class RemittanceFragment extends Fragment{
	
	private MainAct core;
	private FragmentActivity act;
	private View footer;
	
	private RemittanceAdapter adap;
	
	private ListView lvRemittance;
	private Button btnRemitSetDate, btnRemitPopulate, btnRemitFtrSubmit;
	private EditText etRemitInvDate;
	
	private String SuccessRemitInvno;;
	private ArrayList<Remittance> siList = new ArrayList<Remittance>();  

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

			return inflater.inflate(R.layout.fragment_remittance, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
		act = getActivity();
		core =(MainAct)act;
		
		lvRemittance = (ListView)core.findViewById(R.id.lvRemittance);
		
		
		LayoutInflater inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//
		View header = null;
		header = inflater.inflate(R.layout.header_remittance, null);
		
		btnRemitSetDate = (Button)header.findViewById(R.id.btnRemitSetDate);
		btnRemitPopulate = (Button)header.findViewById(R.id.btnRemitPopulate);
		etRemitInvDate = (EditText)header.findViewById(R.id.etRemitInvDate);
		etRemitInvDate.setEnabled(false);
		
		footer = inflater.inflate(R.layout.footer_remittance, null);
		
		btnRemitFtrSubmit = (Button)footer.findViewById(R.id.btnRemitFtrSubmit);
		//
		
		//
		btnRemitSetDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CallDatePicker();
			}
		});
		
		btnRemitPopulate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CallServerInvoiceDetails();
			}
		});
		
		btnRemitFtrSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SubmitRemittanceServer();
			}
		});
		
		lvRemittance.addHeaderView(header);
		//lvRemittance.addFooterView(footer);
		
		lvRemittance.setAdapter(null);
		
	}
	
	 // =========================================================================
	 // TODO Main Functions
	 // =========================================================================
	@SuppressLint("HandlerLeak")
	private void CallServerInvoiceDetails(){
		//
		final String date = etRemitInvDate.getText().toString();
		//
		final ProgressDialog dialog = ProgressDialog.show(core, "Please Wait...", "Searching Invoice Content");
		final Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				//
				dialog.dismiss();
				SetSearchResult();
			}
		};
		Thread process = new Thread(){
			@Override
			public void run() {
				super.run();
				//
				siList = JsonParser.GetSalesInfoByInvDate(date, core);
				handler.sendEmptyMessage(0);
			}
		};
		process.start();
	}
	
	@SuppressLint("HandlerLeak")
	private void SubmitRemittanceServer(){
		//
		ArrayList<Remittance> collection = adap.GetAdapterList();
		final ArrayList<Remittance> selRemitance = new ArrayList<Remittance>();
		
		for(Remittance rem : collection){
			if(rem.isselect){
				selRemitance.add(rem);
			}
		}
		//
		SuccessRemitInvno = "";
		//
		final ProgressDialog dialog = ProgressDialog.show(core, "Please Wait...", "Submit Remittance");
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				dialog.dismiss();
				SetRemittanceResult();
			}
		};
		Thread process = new Thread(){
			@Override
			public void run() {
				super.run();
				SuccessRemitInvno = JsonParser.SubmitRemitServer(selRemitance, core);
				//
				handler.sendEmptyMessage(0);
			}
		};
		process.start();
	}
	 // =========================================================================
	 // TODO Sub Functions
	 // =========================================================================
	private void CallDatePicker(){
		//
		Calendar calendar = Calendar.getInstance();
		int year, month, day;
		
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		
		DatePickerDialog dpd = new DatePickerDialog(core, dateSet, year, month, day);
		dpd.show();
		
	}
	
	private void SetSearchResult(){
		
		if(siList.size() != 0){
			adap = new RemittanceAdapter(core, siList);
			lvRemittance.removeFooterView(footer);
			lvRemittance.addFooterView(footer);
			//
			lvRemittance.setAdapter(adap);
		} else {
			lvRemittance.removeFooterView(footer);
			lvRemittance.setAdapter(null);
			Utils.me.MessageBox(core, "No Record(s) found");
		}
	}
	
	private void SetRemittanceResult(){
		
		if(SuccessRemitInvno.length() != 0){
			Utils.me.MessageBox(core, SuccessRemitInvno+": Remittance Success!");
			//core.displayView(0);
			CallServerInvoiceDetails();
		} else {
			Utils.me.MessageBox(core, "Null/Failed to Remit!");
		}
	}
	 // =========================================================================
	 // TODO Implementation
	 // =========================================================================
	private DatePickerDialog.OnDateSetListener dateSet = new DatePickerDialog.OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				
				monthOfYear +=1;
				String month = String.valueOf(monthOfYear);
				String day = String.valueOf(dayOfMonth);
					
				month = month.length() == 1 ? "0"+month:month;
				day = day.length() == 1 ? "0"+day:day;
				
				String res = year+"-"+month+ "-"+ day;
				
				etRemitInvDate.setText(res);
				//
				CallServerInvoiceDetails();
			}
		};
	}
