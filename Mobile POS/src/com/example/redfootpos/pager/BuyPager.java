package com.example.redfootpos.pager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.Vars;
import com.example.redfootpos.Vars.method;
import com.example.redfootpos.Vars.type;
import com.example.redfootpos.database.CloudfoneDB;
import com.example.redfootpos.interfaces.IPaymentPost;
import com.example.redfootpos.model.Item;
import com.example.redfootpos.model.SalesDetails;
import com.example.redfootpos.model.SalesInfo;
import com.example.redfootpos.object.SendMailService;
import com.example.redfootpos.utils.DialogUtils;
import com.example.redfootpos.utils.JsonParser;
import com.handmark.pulltorefresh.library.internal.Utils;
import com.itextpdf.text.SplitCharacter;
import com.squareup.picasso.Picasso;

public class BuyPager implements IPaymentPost{
	 // =========================================================================
	 // TODO Variables
	 // =========================================================================
	private View view;
	private static FragmentActivity act;
	private static MainAct core;
	
	private static TextView tvBuyTitle;
	private static TextView tvBuyPrice;
	private static TextView tvBuyDescript;
	public static TextView tvSwipeStatus;
	private static ImageView ivBuyItem;
	public static Button btnBuySwipeNow;
	
	public static TextView tvSwipeAccount, tvSwipeName, tvSwipeType, tvSwipeCvv, TvSwipeDate, tvBuyImeiSel;
	public static Button btnSwipeCheckout, btnCheckoutCF, btnBuySelIMEI;
	public static Spinner spnPaymentMode, spnBuyQuantity, spnSelectIMEI, spnTakenBy;
	public static LinearLayout llCashFrom, llSwipeForm, llSwipeButton, llBuyIMEI;
	
	public static EditText etFirstNameCF, etLastNameCF, etAddressCF, etEmailCF, etContactNoCF, etInvoiceNo;
	public static TextView tvTotalAmount;
	private static String[] arrNumberPicker;
	private static int quantityCount = 0;
	
	private static String TotalAm;
	private static SalesDetails sd;
	
	private static SalesInfo si;
	private static ArrayList<SalesDetails> sdList;
	private static ArrayList<IMEITemp> tempImeiList = new ArrayList<IMEITemp>();
	private static ArrayList<IMEITemp> ImeiSelList = new ArrayList<IMEITemp>();
	private static ArrayList<StaffTmp> staffListClass = new ArrayList<StaffTmp>();
	
	private static CheckBox cbPark;
	private static String parkRemarks = "";
	private static String phoneType;
	
	private static String AmtChange;
	
	private static String selPrice;
	
	public boolean isInserted = false;
	public static boolean isValidateSeries = false;
	public static int ValidInt = 0;
	 // =========================================================================
	 // TODO Constructors
	 // =========================================================================
	public BuyPager(){};
	
	public BuyPager(View v, FragmentActivity act, MainAct core){
		this.view = v;
		this.act = act;
		this.core = core;
		
		
	}
	 // =========================================================================
	 // TODO Main Functions
	 // =========================================================================
	public void initialize(){
		//
		tvBuyTitle = (TextView)view.findViewById(R.id.tvBuyName);
		tvBuyPrice = (TextView)view.findViewById(R.id.tvBuyPriceAmnt);
		tvBuyDescript = (TextView)view.findViewById(R.id.tvBuyDescription);
		ivBuyItem = (ImageView)view.findViewById(R.id.ivCartImage);
		tvSwipeStatus = (TextView)view.findViewById(R.id.tvSwipeStatus);
		btnBuySwipeNow = (Button)view.findViewById(R.id.btnBuySwipeNow);
		
		tvSwipeAccount = (TextView)view.findViewById(R.id.tvSwipeAccount);
		tvSwipeName = (TextView)view.findViewById(R.id.tvSwipeName);
		tvSwipeType = (TextView)view.findViewById(R.id.tvSwipeType);
		tvSwipeCvv = (TextView)view.findViewById(R.id.tvSwipeCvv);
		TvSwipeDate = (TextView)view.findViewById(R.id.tvSwipeDate);
		btnSwipeCheckout = (Button)view.findViewById(R.id.btnSwipeCheckout);
		
		spnPaymentMode = (Spinner)view.findViewById(R.id.spnPaymentMode);
		spnSelectIMEI = (Spinner)view.findViewById(R.id.spnSelectIMEI);
		spnTakenBy = (Spinner)view.findViewById(R.id.spnTakenBy);
		llCashFrom = (LinearLayout)view.findViewById(R.id.llCashForm);
		llSwipeForm = (LinearLayout)view.findViewById(R.id.llSwipeForm);
		llSwipeButton = (LinearLayout)view.findViewById(R.id.llSwipeButton);
		llBuyIMEI = (LinearLayout)view.findViewById(R.id.llBuyIMEI);
		
		btnCheckoutCF = (Button)view.findViewById(R.id.btnCheckoutCF);
		etFirstNameCF = (EditText)view.findViewById(R.id.etFirstNameCF);
		etLastNameCF = (EditText)view.findViewById(R.id.etLastNameCF);
		etAddressCF = (EditText)view.findViewById(R.id.etAddressCF);
		etEmailCF = (EditText)view.findViewById(R.id.etEmailaddCF);
		etContactNoCF = (EditText)view.findViewById(R.id.etContactNoCF);
		etInvoiceNo = (EditText)view.findViewById(R.id.etInvoiceNumCF);
		
		btnBuySelIMEI = (Button)view.findViewById(R.id.btnBuySelIMEI);
		tvBuyImeiSel = (TextView)view.findViewById(R.id.tvBuyImeiSel);
		
		// setup Widget to All Caps
		etInvoiceNo.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
		etFirstNameCF.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
		etLastNameCF.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
		etAddressCF.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
		etEmailCF.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
		etContactNoCF.setFilters(new InputFilter[] {new InputFilter.AllCaps(), new InputFilter.LengthFilter(11) });
		
		
		//
		tvTotalAmount = (TextView)view.findViewById(R.id.tvTotalAmount);
		//
		cbPark = (CheckBox)view.findViewById(R.id.cbPark);
		//
		llCashFrom.setVisibility(View.GONE);
		llSwipeForm.setVisibility(View.GONE);
		
		spnBuyQuantity = (Spinner)view.findViewById(R.id.spnBuyQuantList);
		
		sd = new SalesDetails();
		
		spnPaymentMode.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				
				switch (position) {
				case 0:
					llCashFrom.setVisibility(View.VISIBLE);
					llSwipeForm.setVisibility(View.GONE);
					btnBuySwipeNow.setVisibility(View.GONE);
					break;

				case 1:
					llCashFrom.setVisibility(View.GONE);
					llSwipeForm.setVisibility(View.VISIBLE);
					btnBuySwipeNow.setVisibility(View.VISIBLE);
					break;
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		btnBuySwipeNow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				core.startSwipeCard();
			}
		});
		
		btnCheckoutCF.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (validateCashPayment()) {
					
					//submitPayment(PaymentType.Checkout);
					callPrompDialog();
					
				} else {
					MessageBox("Check your Fields: (Missing Input)");
				}
				//sendEmailTest();
			}
		});
	
		//
		cbPark.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(cbPark.isChecked()){
					ParkMessage();
				}
			}
		});
		
		
	}
	
	public static void setProperties(final Item item){
		/////
		ImeiSelList.clear();
		/////
		ArrayList<String> quant = new ArrayList<String>();
		
		for(int x = 1; x <= item.Stockin; x++){
			//
			quant.add(x+"");
		}
		arrNumberPicker = quant.toArray(new String[quant.size()]);
		//arrNumberPicker = new String[]{"1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"};
		
		ArrayAdapter<String> spnAdapter = new ArrayAdapter<String>(core, android.R.layout.simple_spinner_item, arrNumberPicker);
		spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnBuyQuantity.setAdapter(spnAdapter);
		
		/////
		final String imei = item.Imei;
		
		Boolean isExist = imei.contains(",");
		
		if(isExist){ // if array is more than one
			final String[] imeiArr = imei.split("\\,");
			
			btnBuySelIMEI.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					SelectIMEIDial(imeiArr);
								    	
				}
			});
		} else { // if array is one only
			if(imei.length() > 0){
				//
				btnBuySelIMEI.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						String[] imeiArrSingle = new String[1];
						imeiArrSingle[0] = imei;
						SelectIMEIDial(imeiArrSingle);
									    	
					}
				});
			}
		}
		//// Test Code
//		btnBuySelIMEI.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				
//				new CloudfoneDB(core).openToWrite().UpdateItemStock(1, 75,"123123123IME2,123123123IME3");	    
//				com.example.redfootpos.utils.Utils.me.MessageBox(core, "Test Succeed");
//			}
//		});
		phoneType = item.type;
		////
		selPrice = com.example.redfootpos.utils.Utils.me.PriceCompare(item.price, item._discounted);
		
		int price = com.example.redfootpos.utils.Utils.me.CurrencyStringToInt(selPrice);
		String priceStr = com.example.redfootpos.utils.Utils.me.FormatCurrencyToString(price, "PHP ");
		
		tvBuyTitle.setText(item.name);
		tvBuyPrice.setText(priceStr);
		tvBuyDescript.setText(item.Description);
		Picasso.with(act).load(item.url).into(ivBuyItem);
		
		tvTotalAmount.setText(priceStr);
		//
		sd.itemid = item.ID+"";
		sd.unitprice = selPrice;
		sd.qty = "1";
		sd.prodname =  item.name;
		
		int discPrice = com.example.redfootpos.utils.Utils.me.CurrencyStringToInt(item._discounted);
		
		if(discPrice != 0){
			int orgPrice = com.example.redfootpos.utils.Utils.me.CurrencyStringToInt(item.price);
			int discounted = orgPrice - discPrice;
			sd.discount = discounted+"";
		} else {
			sd.discount = "0";
		}
		
		TotalAm = selPrice;
		//
		tvSwipeAccount.setText(":");
		
		if (core.isReaderConnected) {
			btnBuySwipeNow.setEnabled(true);
			BuyPager.tvSwipeStatus.setText("UniPay Connected.");
		} else {
			btnBuySwipeNow.setEnabled(false);
			BuyPager.tvSwipeStatus.setText("UniPay Disconnected.");
		}
		//
			BuyPager.btnSwipeCheckout.setEnabled(false);
			setBlank(":");
			
		spnBuyQuantity.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				int number = Integer.parseInt(arrNumberPicker[position]);
				String cleanText = selPrice.replace(",", "");
				double d = Double.parseDouble(cleanText);
				int price = (int)d;
				
				int total = price * number;
				
				NumberFormat format = NumberFormat.getCurrencyInstance(Locale.ENGLISH);
				String val = format.format(total);
				
				val = val.replace("¤", "PHP ");
				tvTotalAmount.setText(val);
				tvBuyPrice.setText(val);
				//
				TotalAm = val.replace("PHP ", "");
				sd.qty = number+"";
				// clear IMEI Settings
				ImeiSelList.clear();
				tvBuyImeiSel.setText("");
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});	
		
		// Populate Staff by KIOSK
		ArrayAdapter<String> spnStaffAdap = new ArrayAdapter<String>(core, android.R.layout.simple_spinner_item, PopulateStaff());
		spnStaffAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spnTakenBy.setAdapter(spnStaffAdap);
		//
		if(!item.type.equals("accessory")){
			llBuyIMEI.setVisibility(View.VISIBLE);
		} else {
			llBuyIMEI.setVisibility(View.GONE);
		}
		//
		etInvoiceNo.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				
				if(!hasFocus){	
					//
					String val = etInvoiceNo.getText().toString();
					if(val.contains("#")){
						
						String[] sp = val.split("#");
						
						ValidationInvoiceServer(sp[0], sp[1]);
					} else {
						etInvoiceNo.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_failed, 0);
					}
					//
					
				}
			}
		});
	}
	 // =========================================================================
	 // TODO Sub Functions
	 // =========================================================================
	public static void setBlank(String msg){
		//
		tvSwipeAccount.setText(msg);
		tvSwipeName.setText(msg);
		tvSwipeType.setText(msg);
		tvSwipeCvv.setText(msg);
		TvSwipeDate.setText(msg);
		
		etFirstNameCF.setText("");
		etLastNameCF.setText("");
		etAddressCF.setText("");
		etEmailCF.setText("");
		etContactNoCF.setText("");
	}
	
	private boolean validateCashPayment(){
		
		
		if (etFirstNameCF.getText().toString().equals("") ||
				etLastNameCF.getText().toString().equals("")
				/*etAddressCF.getText().toString().equals("") ||*/
				
				){
			
			return false;
		}
		
		if(etEmailCF.getText().toString().equals("") &&
				etContactNoCF.getText().toString().equals("")){
			return false;
		}
		
		if (!phoneType.equals("accessory")) {
			if (ImeiSelList.size() == 0) {
				return false;
			}
			
			int quant = Integer.parseInt(spnBuyQuantity.getSelectedItem().toString());
			//
			if(ImeiSelList.size() != quant){
				return false;
			}
		}
		
		if(spnTakenBy.getSelectedItemPosition() == 0){
			return false;
		}
		
		if(!isValidateSeries){
			return false;
		}
		
		return true;
	}
	
	private void MessageBox(String msg){
		
		Builder dialog = new AlertDialog.Builder(core);
		dialog.setTitle("Message Dialog");
		dialog.setMessage(msg);
		
		dialog.setNeutralButton("OK", null);
		
		dialog.show();
		
	}
	
	private void ParkMessage(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(core);
		dialog.setTitle("Reserved Data");
		dialog.setMessage("Input Remarks, (optional)");
		//
		final EditText input = new EditText(core);
		dialog.setView(input);
		//
		dialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				 
				String invno = etInvoiceNo.getText().toString();
				String Fname = etFirstNameCF.getText().toString();
				String Lname = etLastNameCF.getText().toString();
				
				if(!invno.equals("") && !Fname.equals("") && !Lname.equals("")){
					//cbPark.setEnabled(false);
					 
					 parkRemarks = input.getText().toString();
					 submitPayment(PaymentType.Park);
				}else {
					com.example.redfootpos.utils.Utils.me.MessageBox(core, "Invoice #\nFirst Name\nLast Name is required");
					cbPark.setChecked(false);
				}
			}
		});
		
		dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				cbPark.setChecked(false);
			}
		});
		dialog.show();
	}
	
	@SuppressLint("HandlerLeak")
	private void submitPayment(final PaymentType pType){
		////
		
		si = new SalesInfo();
		
		si.invno = etInvoiceNo.getText().toString();
		si.name = etFirstNameCF.getText().toString() + " " + etLastNameCF.getText().toString();
		/*si.address = etAddressCF.getText().toString();*/
		si.address = "";
		si.emailadd = etEmailCF.getText().toString();
		String contact = etContactNoCF.getText().toString(); 
		if(!contact.equals("")){
			si.contact = contact;
		} else {
			si.contact = "0";
		}
		 
		si.custno = "99999";
		si.total = TotalAm;
		si.empid = Vars.EmpID(core, "", type.GET);
		//
		int takenby = spnTakenBy.getSelectedItemPosition();
		si._takenby = staffListClass.get(takenby).empcode;
		//
		switch (pType) {
		case Checkout:
			si.status = "Closed";
			si.remarks = "";
			break;
		case Park:
			si.status = "Parked";
			si.remarks = parkRemarks;
			break;
		}
		
		
		if (!phoneType.equals("accessory")) {
			
			if (pType != PaymentType.Park) {
				String imei = "";
				for (IMEITemp in : ImeiSelList) {
					imei += in.imei + ",";
				}
				int p = imei.length();
				String imeiClear = imei.substring(0, p - 1);
				//
				sd.imei_ = imeiClear;
			} else{
				sd.imei_ = "";
			}
			
		} else {
			sd.imei_ = "";
		}
		
		sd.invno = si.invno;
		
		
		sdList = new ArrayList<SalesDetails>();
		sdList.add(sd);
		////
		final ProgressDialog dialog = ProgressDialog.show(core, "Please wait...", "Posting Payment Process");
		final Handler handler = new Handler(){
			public void handleMessage(Message msg) 
			{
				dialog.dismiss();
				//
				setResult(pType);
				//
			}
		};
		Thread update = new Thread(){
			@Override
			public void run() {
				super.run();
				//
				isInserted = JsonParser.PostPaymentToServer(si, sdList, core);
				//
				handler.sendEmptyMessage(0);
			}
		};
		update.start();
	}
	
	private void setResult(PaymentType pType){
		
		switch(pType){
		case Checkout:
			if(isInserted){
				
				// Stocks and IMEI adjustment
				int stockDiff = Integer.parseInt(spnBuyQuantity.getSelectedItem().toString());
				
				new CloudfoneDB(core).openToWrite().UpdateItemStock(stockDiff, Integer.parseInt(sdList.get(0).itemid), sdList.get(0).imei_);
				
				// Send a text Composition
				DialogUtils.me.GenerateReciept("Payment Status", "Checkout Successful", core, si, sdList, AmtChange);
				///
				//MessageBox("Payment Successful");
				core.pager.setCurrentItem(0);
				core.getActionBar().setDisplayShowHomeEnabled(true);
				core.setTitle("Home");
			} else {
				MessageBox("Payment Failed. Check your Invoice/Fields if duplicate");
			}
			break;
		case Park:
			if(isInserted){
				MessageBox("Parked Succeed");
				btnCheckoutCF.setEnabled(false);
				//
//				int stockDiff = Integer.parseInt(spnBuyQuantity.getSelectedItem().toString());
//				int prodid = Integer.parseInt(sdList.get(0).itemid);
//				
//				new CloudfoneDB(core).openToWrite().UpdateStock(stockDiff, prodid);
				//
				Vars.ParkedMethods(core, method.ADD);
				//
				core.pager.setCurrentItem(0);
				core.getActionBar().setDisplayShowHomeEnabled(true);
				core.setTitle("Home");
			} else {
				MessageBox("Parked Failed/Check Invoice if already used.");
				cbPark.setChecked(false);
			}
			 break;
		}
	}
	
	private enum PaymentType{
		Checkout,
		Park
	}
	
	private void callPrompDialog(){
		
		DialogUtils.me.PaymentReceived(core, "Payment", "Enter Amount Received",TotalAm, this, null);
		
	}
	
	private static String[] PopulateStaff(){
		//
		ArrayList<String> list001 = new ArrayList<String>();
		
		list001.add("0: --- select agent ---");
		
		String stafflist = Vars.Staff(core, "", type.GET);
		String[] staffArr = stafflist.split("\\,");
		
		for(String staff : staffArr){
			//
			list001.add(staff);
		}
		//
		staffListClass.clear();
		//
		for(String taff : list001){
			String[] val = taff.split("\\:");
			StaffTmp ins = new StaffTmp();
			
			ins.empcode = val[0];
			ins.name = val[1];
			
			staffListClass.add(ins);
		}
		//
		
		int count = staffListClass.size();
		
		String[] arrs = new String[count];
		
		for(int x = 0; x < count; x++){
			arrs[x] = staffListClass.get(x).name;
		}
		
		return arrs;
	}
	
	@SuppressLint("HandlerLeak")
	private static void ValidationInvoiceServer(final String prefix, final String digits){
		//
		final ProgressDialog dialog = ProgressDialog.show(core, "Please Wait...", "Validating Invoice");
		@SuppressWarnings("unused")
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				dialog.dismiss();
				//
				ResultValidation();
			}
		};
		Thread process = new Thread(){
			@Override
			public void run() {
				super.run();
				
				ValidInt = JsonParser.ValidatingInvoice(core, prefix, digits);
				handler.sendEmptyMessage(0);
			}
		};
		process.start();
	}
	
	private static void ResultValidation(){
		
		if(ValidInt == 1000){
			isValidateSeries = false;
			com.example.redfootpos.utils.Utils.me.MessageBox(core, "Already Exist");
		} else if(ValidInt > 0 && ValidInt < 1000){
			isValidateSeries = true;
		} else if(ValidInt == 0){
			isValidateSeries = false;
		}
		//
		if(isValidateSeries){
			etInvoiceNo.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_succes, 0);
		} else {
			etInvoiceNo.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_failed, 0);
		}
	}
	 // =========================================================================
	 // TODO Implementation
	 // =========================================================================
	@Override // for Payment interface method
	public void CheckoutNow(String change) {
		
		AmtChange = change;
		submitPayment(PaymentType.Checkout);
		
	}
	 // =========================================================================
	 // TODO Inner Class
	 // =========================================================================
	public static  void SelectIMEIDial(final String[] imeiList){
		
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(core);
        builderSingle.setIcon(R.drawable.ic_launcher);
        builderSingle.setTitle("Select IMEI");
        //
        final int totalQuant = Integer.parseInt(spnBuyQuantity.getSelectedItem().toString());
        //
        quantityCount = 0;
        //
        tempImeiList.clear();
        //
        for(String strImei : imeiList){
        	//
        	IMEITemp ins = new IMEITemp();
        	ins.imei = strImei;
        	ins.isSelect = false;
        	
        	tempImeiList.add(ins);
        }
        //String[] sample = new String[]{"one","Two","three"};
        
        builderSingle.setMultiChoiceItems(imeiList, null, new OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				//
				if(isChecked){
					quantityCount++;
				} else {
					if(quantityCount > 0){
						quantityCount--;
					}
				}
				//
				if(totalQuant < quantityCount){
					
					final AlertDialog alert = (AlertDialog)dialog;
					final ListView list = alert.getListView();
					list.setItemChecked(which, false);
					
					quantityCount = Integer.parseInt(spnBuyQuantity.getSelectedItem().toString());
					
					return;
				}
				//
				tempImeiList.get(which).isSelect = isChecked;
				tempImeiList.get(which).position = which;
				//
				
				
			}
		});
        
        builderSingle.setPositiveButton("Select", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.dismiss();
                    	ImeiSelList.clear();
                    	
                    	int x = 0;
                    	for(IMEITemp im : tempImeiList){
                    		if(im.isSelect){
                    			ImeiSelList.add(im);
                    			x++;
                    		}
                    	}
                    	// validation
        				if(x < totalQuant){
        					com.example.redfootpos.utils.Utils.me.MessageBox(core, "Total Quantity is not equal to Selected IMEI");
        					ImeiSelList.clear();
        					return;
        				}
                    	//
                    	String msgSamp = "";
                    	for(IMEITemp in : ImeiSelList){
                    		msgSamp += in.imei + "\n";
                    	}
                    	msgSamp = msgSamp.substring(0, msgSamp.length() - 1);
                    	
                    	tvBuyImeiSel.setText(msgSamp);
                    }
                });
        
        builderSingle.show();
	}
	
	public static class IMEITemp{
		
		public String imei;
		public Boolean isSelect;
		public int position;
		
		public IMEITemp(){
			
			this.imei = "";
			this.isSelect = false;
			this.position = 0;
		}
	}
	
	public static class StaffTmp{
		public String name;
		public String empcode;
		
		public StaffTmp(){
			this.name = "";
			this.empcode = "";
		}
	}
}
