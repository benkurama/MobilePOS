package com.example.redfootpos.adapter;

import java.util.ArrayList;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.model.Stocks;

public class StocksRefAdapter extends BaseAdapter{
	 // =========================================================================
	 // TODO Variables
	 // =========================================================================
	private ArrayList<Stocks> StockList = new ArrayList<Stocks>();
	private MainAct core;
	private int ItemCount;
	private int IncItem;
	 // =========================================================================
	 // TODO Constructors
	 // =========================================================================
	public StocksRefAdapter(MainAct core, ArrayList<Stocks> stockList, int totalcount){
		
		this.core = core;
		this.StockList = stockList;
		this.ItemCount = totalcount;
		this.IncItem = 0;
	}
	 // =========================================================================
	 // TODO Overrides
	 // =========================================================================
	@Override
	public int getCount() {
		return StockList.size();
	}

	@Override
	public Object getItem(int position) {
		return StockList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("static-access")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		View view = null;
		
		Stocks stocks = StockList.get(position);
		
		if(convertView == null){
			Holder holder = new Holder();
			view = view.inflate(core, R.layout.row_stock_reference, null);
			
			holder.tvProdName = (TextView)view.findViewById(R.id.tvStocksRowProdName);
			holder.tvStockin = (TextView)view.findViewById(R.id.tvStocksRowStockIn);
			holder.etStockReceive = (EditText)view.findViewById(R.id.etStocksRowStockReceive);
			
			view.setTag(holder);
		} else {
			view = convertView;
		}
		
		final Holder holder = (Holder)view.getTag();
		
		holder.tvProdName.setText(stocks.ProdName);
		holder.tvStockin.setText(stocks.StockIn);
		
		//
		if(stocks.Checked.equals("t")){
			holder.etStockReceive.setEnabled(false);
		}
		//
		
//		String getValue = holder.etStockReceive.getText().toString();		
//		if(getValue.equals("0") || getValue.equals("")){
//			holder.etStockReceive.setText(stocks.StockList);
//		} 
		
		
		if(IncItem >= ItemCount){
			holder.etStockReceive.setText(stocks.StockList);
			
			//
			
			holder.etStockReceive.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,	int after) {
				
			}
			@Override
			public void afterTextChanged(Editable s) {
				StockList.get(position).StockList = s.toString();
			}
		});
		}
		
		IncItem++;
		return view;
	}
	 // =========================================================================
	 // TODO Main Function
	 // =========================================================================
	public ArrayList<Stocks> GetStockList(){
		return StockList;
	}
	 // =========================================================================
	 // TODO Inner Class
	 // =========================================================================
	private class Holder {
		TextView tvProdName, tvStockin;
		EditText etStockReceive;
	}
}
