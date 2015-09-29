package com.example.redfootpos.adapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.database.CloudfoneDB;
import com.example.redfootpos.model.Catalyst;
import com.example.redfootpos.model.Item;
import com.example.redfootpos.model.SalesDetails;
import com.example.redfootpos.pager.BuyPager.IMEITemp;
import com.example.redfootpos.utils.JsonParser;
import com.example.redfootpos.utils.Utils;
import com.squareup.picasso.Picasso;

public class ItemCartAdapter extends BaseAdapter implements Catalyst{
	 // =========================================================================
	 // Variables
	 // =========================================================================
	private ArrayList<Item> listItems = new ArrayList<Item>();
	private ArrayList<SalesDetails> listSD = new ArrayList<SalesDetails>(); 
	private MainAct core;
	private FragmentActivity act;
	private Application appCore;
	private TextView tvTotal;
	private boolean isRetSales;
	private String InvNo;
	
	private String selPrice;
	
	private static String[] arrNumberPicker;
	
	 // =========================================================================
	 // Constructors
	 // =========================================================================
	
	public ItemCartAdapter(MainAct core, FragmentActivity act, Application appcore, TextView total, boolean isRetrieval, String invno){
		this.core = core;
		this.act = act;
		this.appCore = appcore;
		this.tvTotal = total;
		this.isRetSales = isRetrieval;
		this.InvNo = invno;
		
		GetAllMarkItems();
		//arrNumberPicker = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"};
		arrNumberPicker = new String[]{"1"};
		getTotalOrder();
	}
	 // =========================================================================
	 // Overrides
	 // =========================================================================
	@Override
	public int getCount() {
		
		return listItems == null ? 0 : listItems.size();
	}

	@Override
	public Object getItem(int position) {
		
		return null;
	}

	@Override
	public long getItemId(int position) {
	
		return 0;
	}

	@Override
	public boolean isEnabled(int position) {
		
		return false;
	}

	@SuppressWarnings("static-access")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		//
		View view = null;
		final Item item = listItems.get(position);
		
		if(convertView == null){
			Holder hold = new Holder();
			view = view.inflate(act, R.layout.cell_shopping_cart, null);
			
			hold.ivImage = (ImageView)view.findViewById(R.id.ivCartImage);
			hold.tvTitle = (TextView)view.findViewById(R.id.tvCartName);
			hold.tvPrice = (TextView)view.findViewById(R.id.tvCartPriceAmnt);
			hold.tvDesc = (TextView)view.findViewById(R.id.tvCartDesc);
			hold.ivClose = (ImageView)view.findViewById(R.id.ivCartClose);
			hold.spnQuantity = (Spinner)view.findViewById(R.id.spnCartQuantity);
			hold.btnImeiSet = (Button)view.findViewById(R.id.btnCartSetIMEI);
			hold.llLayoutIMEI = (LinearLayout)view.findViewById(R.id.llLayoutIMEI);
			hold.tvIMEI = (TextView)view.findViewById(R.id.tvListIMEI);
			//
			
			//
			ArrayList<String> quant = new ArrayList<String>();
			
			for(int x = 1; x <= item.Stockin; x++){
				//
				quant.add(x+"");
			}
			arrNumberPicker = quant.toArray(new String[quant.size()]);
			//
			ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(core, android.R.layout.simple_spinner_item, arrNumberPicker);
			spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			hold.spnQuantity.setAdapter(spnAdapter);
			//
			if(!isRetSales){
				listItems.get(position)._spnQuantityPos = 0;
			} 
			//
			view.setTag(hold);
		} else {
			view = convertView;
		}
		
		String priceStr = "";
		
		final Holder holder = (Holder)view.getTag();
		
		holder.tvTitle.setText(item.name);
		
		
		if (!isRetSales) {
			selPrice = Utils.me.PriceCompare(item.price, item._discounted);
			
			int priceImt = Utils.me.CurrencyStringToInt(selPrice);
			priceStr = Utils.me.FormatCurrencyToString(priceImt, "PHP ");
			//
			int discPrice = Utils.me.CurrencyStringToInt(item._discounted);
			if(discPrice != 0){
				int origPrice = Utils.me.CurrencyStringToInt(item.price);
				int discounted = origPrice - discPrice;
				item._discount = discounted+"";
			} else {
				item._discount = "0";
			}
		} else {
			selPrice = item.price;
		}
		
		
		//
		holder.tvPrice.setText(priceStr);
		holder.tvDesc.setText(item.Description);
		//
		
		Picasso.with(appCore).load(item.url).into(holder.ivImage);
		//
		holder.ivClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteSelItem(position);
			}
		});
		
		//
		
		holder.spnQuantity.setSelection(listItems.get(position)._spnQuantityPos);
		
		
		holder.spnQuantity.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				listItems.get(position)._spnQuantityPos = pos;
				//
				getLocalTotal(pos, selPrice, holder.tvPrice, holder.spnQuantity);
				
				holder.tvIMEI.setText("");
				listItems.get(position)._selIMEI = "";
				listItems.get(position)._selQuantity = Integer.parseInt(holder.spnQuantity.getSelectedItem().toString());
				getTotalOrder();
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		//
		
		//
		if(!item.type.equals("accessory")){
			holder.llLayoutIMEI.setVisibility(View.VISIBLE);
			holder.btnImeiSet.setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View v) {
					SelectIMEIDial(item.Imei, holder.tvIMEI, position, holder.spnQuantity);
				}
			});
		} else {
			holder.llLayoutIMEI.setVisibility(View.GONE);
		}
		//
		//getTotalOrder();
		//
		return view;
	}
	
	@Override
	public void Methods() {
		core.displayView(0);
	}
	 // =========================================================================
	 // Main Functions
	 // =========================================================================
	 public ArrayList<SalesDetails> GetSalesDetails(){
		 
		 ArrayList<SalesDetails> sdList = new ArrayList<SalesDetails>();
		 
		 for(Item item : listItems){
			 
			 String selPrice = null;
			if (!isRetSales) {
				selPrice = Utils.me.PriceCompare(item.price, item._discounted);
			} else {
				selPrice = item.price;
			}
			 
			 SalesDetails sd = new SalesDetails();
			 sd.itemid = item.ID+"";
			 sd.unitprice = selPrice;
			 //sd.qty = arrNumberPicker[item._spnQuantityPos];
			 sd.qty = item._selQuantity+"";
			 sd.prodname = item.name;
			 sd.imei_ = item._selIMEI;
			 sd.type = item.type;
			 sd.discount = item._discount;
			 
			 sdList.add(sd);
		 }
		 
		 return sdList;
	 }
	 // =========================================================================
	 // Sub Functions
	 // =========================================================================	
	private void GetAllMarkItems(){
		if(!isRetSales){
			listItems.addAll(new CloudfoneDB(core).openToRead().selectAllMarks());
		} else {
			GetSalesDetailsFromServer();
		}
	}
	
	private void deleteSelItem(final int pos){
		
		Builder dialog = new AlertDialog.Builder(core);
		dialog.setTitle("Are you sure?");
		dialog.setMessage("Do you want to remove this item from your cart?");
		dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//
				int id = listItems.get(pos).ID;
				new CloudfoneDB(core).openToWrite().setMarkOutItem(id);
				//
				listItems.remove(pos);
				
				if(listItems.size() == 0){
					//EmptyCartMessage();
					Utils.me.EmptyCartMessage(core, ItemCartAdapter.this, "No Item Found, back to main");
				}
				notifyDataSetChanged();
				getTotalOrder();
				
				//core.displayView(2);// Shopping cart Page
			}
		});
		dialog.setNegativeButton("NO", null);
		dialog.show();
	}
	
	private void getTotalOrder(){
		
		int Amount = 0;
		
		for(Item item : listItems){
			
			String selPrice = null;
			if (!isRetSales) {
				selPrice = Utils.me.PriceCompare(item.price, item._discounted);
			} else {
				selPrice = item.price;
			}
			
			String value = selPrice.replace(",", "");
			double d = Double.parseDouble(value);
			int currency = (int)d;
			//
			//int quant = Integer.parseInt(arrNumberPicker[item._spnQuantityPos]);
			int quant = item._selQuantity;
			
			currency = currency * quant;
			//
			Amount += currency; 
		}
		
		//
		NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
		String cur = format.format(Amount);
		//
		cur = cur.replace("¤", "PHP ");
		//tvTotal.setText("PHP "+ String.format("%,d", Amount));
		tvTotal.setText(cur);
	}
	
	private void getLocalTotal(int quantPos, String price, TextView tvPrice, Spinner spnQuant){
		
		//int quant = Integer.parseInt(arrNumberPicker[quantPos]);
		int quant = Integer.parseInt(spnQuant.getSelectedItem().toString());
		String cleanPrice = price.replace(",", "");
		double d = Double.parseDouble(cleanPrice);
		int priceNum = (int)d;
		
		priceNum = priceNum * quant;
		
		NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
		String val = format.format(priceNum);
		val = val.replace("¤", "PHP ");
		
		tvPrice.setText(val);
	}
	
	@SuppressLint("HandlerLeak")
	private void GetSalesDetailsFromServer(){
		//
		final ProgressDialog dialog = ProgressDialog.show(core, "Please Wait...", "Get Sales Data");
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				dialog.dismiss();
				//
				setResult();
			}
		};
		
		Thread process = new Thread(){
			@Override
			public void run() {
				super.run();
				listSD = JsonParser.GetSalesDetails(InvNo);
				//
				handler.sendEmptyMessage(0);
			}
		};
		process.start();
	}
	
	private void setResult(){
		for(SalesDetails sd : listSD){
			int id = Integer.parseInt(sd.itemid);
			Item item;
			item = new CloudfoneDB(core).openToRead().GetItemsByID(id);
			int qty = Integer.parseInt(sd.qty);
			//item._spnQuantityPos = qty;
			item.Stockin = item.Stockin;
			item.type = item.type;
			item._spnQuantityPos = qty - 1;
			item.Imei = item.Imei;
			item._discount = sd.discount;
			item.price = sd.unitprice;
			//
			listItems.add(item);
			
		}
		this.notifyDataSetChanged();
	}
	
	private void SelectIMEIDial(String imei, final TextView tvimei, final int pos, Spinner spnQty){
		//
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(core);
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select IMEI");
        
        //
        final int[] quantLimit = {0};
        quantLimit[0]= 0;
        //
        final int[] totalQuant = {0};
        totalQuant[0] =	Integer.parseInt(spnQty.getSelectedItem().toString());
        //
        final ArrayList<IMEITemp> tempIMEIArr = new ArrayList<IMEITemp>();
        String[] imeiArr;
        
        //
        if(imei.contains(",")){
        	imeiArr = imei.split("\\,");
        } else {
        	String[] imeiArrSingle = new String[1];
			imeiArrSingle[0] = imei;
        	imeiArr = imeiArrSingle;
        }
        //
        for(String strImei : imeiArr){
        	//
        	IMEITemp ins = new IMEITemp();
        	ins.imei = strImei;
        	ins.isSelect = false;
        	
        	tempIMEIArr.add(ins);
        }
        //
        builderSingle.setMultiChoiceItems(imeiArr, null, new OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				// validation
				if(isChecked){
					quantLimit[0]++;
				} else {
					if(quantLimit[0] > 0){
						quantLimit[0]--;
					}
				}
				
				if(totalQuant[0] < quantLimit[0]){
					final AlertDialog alert = (AlertDialog)dialog;
					final ListView list = alert.getListView();
					list.setItemChecked(which, false);
					//
					quantLimit[0] = totalQuant[0];
					
					return;
				}
				//
				tempIMEIArr.get(which).isSelect = isChecked;
				tempIMEIArr.get(which).position = which;
			}
		});
        
        //
        builderSingle.setPositiveButton("Select", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//
				String concat = "";
				String concat2 = "";
				
				int x= 0;
				for(IMEITemp temp : tempIMEIArr){
					if(temp.isSelect){
						concat += temp.imei + "\n";
						concat2 += temp.imei + ",";
						x++;
					}
				}
				// validation
				if(x < totalQuant[0]){
					Utils.me.MessageBox(core, "Total Quantity is not equal to Selected IMEI");
					tvimei.setText("");
					listItems.get(pos)._selIMEI = "";
					return;
				}
				//
				if(concat.length() > 0){
					concat = concat.substring(0, concat.length() - 1);
					concat2 = concat2.substring(0, concat2.length() - 1);
				}
				
				listItems.get(pos)._selIMEI = concat2;
				
				tvimei.setText(concat);
			}
		});
        //
        builderSingle.show();
	}
	
	// =========================================================================
	 // Inner Class
	 // =========================================================================
	private class Holder {
		TextView tvTitle , tvDesc, tvPrice, tvIMEI;
		ImageView ivImage, ivClose;
		Spinner spnQuantity;
		Button btnImeiSet;
		LinearLayout llLayoutIMEI;
	}
	
	public class IMEITemp{
		
		public String imei;
		public Boolean isSelect;
		public int position;
		
		public IMEITemp(){
			
			this.imei = "";
			this.isSelect = false;
			this.position = 0;
		}
	}
	 // =========================================================================
	 // Final
}
