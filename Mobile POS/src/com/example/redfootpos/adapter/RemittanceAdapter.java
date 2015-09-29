package com.example.redfootpos.adapter;

import java.util.ArrayList;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.model.Remittance;
import com.example.redfootpos.utils.Utils;

public class RemittanceAdapter extends BaseAdapter{
	 // =========================================================================
	 // TODO Variables
	 // =========================================================================
	private ArrayList<Remittance> RemitList = new ArrayList<Remittance>();
	private MainAct core;
	
	public RemittanceAdapter(MainAct core, ArrayList<Remittance> remitlist){
		this.RemitList = remitlist;
		this.core = core;
	}
	
	@Override
	public int getCount() {
		
		return RemitList.size();
	}

	@Override
	public Object getItem(int position) {
		
		return RemitList.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@SuppressWarnings("static-access")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		View view = null;
		
		Remittance remit = RemitList.get(position);
		
		if(convertView == null){
			Holder holder = new Holder();
			view = view.inflate(core, R.layout.row_remittance, null);
			//
			holder.tvRemitRowInvoice = (TextView)view.findViewById(R.id.tvRemitRowInvoice);
			holder.tvRemitRowCustomer = (TextView)view.findViewById(R.id.tvRemitRowCustomer);
			holder.tvRemitRowAmt = (TextView)view.findViewById(R.id.tvRemitRowAmt);
			holder.cbRemitRowSelect = (CheckBox)view.findViewById(R.id.cbRemitRowSelect);
			holder.tblRowRemitBG = (TableRow)view.findViewById(R.id.tblRowRemitBG);
			//
			view.setTag(holder);
		}else{
			view = convertView;
		}
		
		Holder hold = (Holder)view.getTag();
		
		hold.tvRemitRowInvoice.setText(remit.invno);
		hold.tvRemitRowCustomer.setText(remit.custname);
		
		int price = Utils.me.CurrencyStringToInt(remit.invamt);
		String priceStr = Utils.me.FormatCurrencyToString(price, "");
		
		hold.tvRemitRowAmt.setText(priceStr);
		
		if((position & 1) == 0){
			hold.tblRowRemitBG.setBackgroundColor(Color.rgb(255, 255, 255));
		} else {
			hold.tblRowRemitBG.setBackgroundColor(Color.rgb(235, 235, 255));
		}
		
		hold.cbRemitRowSelect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				RemitList.get(position).isselect = isChecked;
			}
		});
		//
		if(remit._remit.equals("yes")){
			
			hold.cbRemitRowSelect.setChecked(true);
			RemitList.get(position).isselect = false;
			hold.cbRemitRowSelect.setEnabled(false);
		} 
			
		return view;
	}
	 // =========================================================================
	 // TODO Main Functions
	 // =========================================================================
	public ArrayList<Remittance> GetAdapterList(){
		//
		return RemitList;
	}
	 // =========================================================================
	 // TODO Sub Functions
	 // =========================================================================
	
	 // =========================================================================
	 // TODO Inner Class
	 // =========================================================================
	private class Holder {
		TextView tvRemitRowInvoice, tvRemitRowCustomer, tvRemitRowAmt;
		CheckBox cbRemitRowSelect;
		TableRow tblRowRemitBG;
	}
}
