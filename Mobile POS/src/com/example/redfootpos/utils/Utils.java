package com.example.redfootpos.utils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.example.redfootpos.model.Catalyst;
import com.example.redfootpos.model.SalesInfo;
import com.example.redfootpos.object.ErrorExceptionView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.SlidingDrawer;

public enum Utils {
me;
	// =========================================================================
	// TODO Popup Message
	// =========================================================================
	public void MessageBox(Context core, String msg){
		Builder dialog = new AlertDialog.Builder(core);
		dialog.setTitle("Message Dialog");
		dialog.setMessage(msg);
		
		dialog.setNeutralButton("OK", null);
		
		dialog.show();	
	}

	public void EmptyCartMessage(Context core, final Catalyst cat, String msg){
		Builder dialog = new AlertDialog.Builder(core);
		dialog.setCancelable(false);
		dialog.setTitle("Alert!");
		dialog.setMessage(msg);
		dialog.setNegativeButton("OK", new Dialog.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				cat.Methods();
			}
		});
		dialog.show();
	}
	// =========================================================================
	// TODO Error Log
	// =========================================================================
	public void iLogCat(String msg1, String msg2){
		Log.i(msg1, msg2);
	}

	public void ActiveErrorLog(Context core){
		Thread.currentThread();
		Thread.setDefaultUncaughtExceptionHandler(new ErrorExceptionView(core));
	}
	 // =========================================================================
	 // TODO Conversions
	 // =========================================================================
	public int CurrencyStringToInt(String cur){
		String amt = cur.replace(",", "");
		double d = Double.parseDouble(amt);
		//
		return (int)d;
	}
	
	public String FormatCurrencyToString(int Amount,String replace){
		//
		NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
		String CustAmount = format.format(Amount); 
		CustAmount = CustAmount.replace("¤", replace);
		return CustAmount;
	}
	
	public String PriceCompare(String price, String discount){
		String select = price;
		if(!discount.equals("0.0")){
			select = discount;
		}
		return select;
	}
	// =========================================================================
	 // TODO Date & Time
	 // =========================================================================
	public String GetDateNow(){
		
		Calendar calendar = Calendar.getInstance(); 
		int year, month, day;
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		month +=1;
		day = calendar.get(Calendar.DAY_OF_MONTH);
		//
		String monthStr = String.valueOf(month);
		String dayStr = String.valueOf(day);
		
		monthStr = monthStr.length() == 1 ? "0"+monthStr : monthStr;
		dayStr = dayStr.length() == 1 ? "0"+dayStr : dayStr;
		//
		
		return year+"-"+monthStr+"-"+dayStr;
	}
	
	public List<String> GenerateMonthly(){
		//
		List<String> MonthArr = new ArrayList<String>();
		//
		Calendar calendar = Calendar.getInstance(); 
		int year;
		year = calendar.get(Calendar.YEAR);
		
		for(int x = 1; x <= 12; x++){
			//
			String monthStr = x+"";
			monthStr = monthStr.length() == 1 ? "0"+monthStr : monthStr;
			
			 MonthArr.add(year + "-" +monthStr);
		}
		
		return MonthArr;
		
	}
	 // =========================================================================
	 // TODO Filter Collection
	 // =========================================================================
	public List<String> DistinctList(List<String> list){
		// distinct data collected (remove duplicate) load to another variable
		Set<String> hs = new HashSet<String>();
		hs.addAll(list);
		list.clear();
		// load back to original variable
		list.addAll(hs);
		//
		return list;
	}
	
//	public <T extends SalesInfo> void GetAll(ArrayList<T> arrs){
//		
//		List<String> empidList = new ArrayList<String>();
//		for(T si : arrs){
//			empidList.add("");
//			
//		}
//	}
//	
//	public <T>void totalFuel(List<? extends T> list) {
//	    
//		int total = 0;
//	    
//		for(T v : list) {
//	        v.
//	    }
//	    
//	}
	
}
