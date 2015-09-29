package com.example.redfootpos.fragments;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.Vars;
import com.example.redfootpos.Vars.method;
import com.example.redfootpos.Vars.type;
import com.example.redfootpos.adapter.ItemCartAdapter;
import com.example.redfootpos.database.CloudfoneDB;
import com.example.redfootpos.interfaces.IPaymentPost;
import com.example.redfootpos.model.Catalyst;
import com.example.redfootpos.model.Item;
import com.example.redfootpos.model.SalesDetails;
import com.example.redfootpos.model.SalesInfo;
import com.example.redfootpos.pager.BuyPager.StaffTmp;
import com.example.redfootpos.utils.DialogUtils;
import com.example.redfootpos.utils.JsonParser;
import com.example.redfootpos.utils.Utils;

public class ShoppingCartFragment extends Fragment implements Catalyst, IPaymentPost{
	 // =========================================================================
	 // Variables
	 // =========================================================================
	private ListView lvTotalItem;
	public static MainAct core;
	public FragmentActivity act;
	public TextView tvCartTotal;
	private Button btnSetPaymetPos, btnCheckoutCart, btnVoidCart, btnCheckCart;
	
	private EditText etInvoiceCart, etFNameCart, etLNameCart, etAddressCart, etEmailCart, etContactCart;
	private CheckBox cbCartPark;
	private Spinner spnTakenbyCart;
	
	private String parkRemarks = "";
	
	private ItemCartAdapter adapter;
	public boolean isInserted = false;
	public boolean isCancelled = false;
	//
	private boolean isRetSales = false;
	private Bundle bundle;
	public boolean isValidateSeries = false;
	public static int ValidInt = 0;
	
	private String AmtChange;
	private SalesInfo si;
	private ArrayList<SalesDetails> sdList;
	private static ArrayList<StaffTmp> staffListClass = new ArrayList<StaffTmp>();
	
	 // =========================================================================
	 // Fragment View
	 // =========================================================================
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fragment_shopping_cart, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		//
		act = getActivity();
		core = (MainAct)act;
		//
		String InvNo = "";
		bundle = this.getArguments();
		if(bundle != null){

			isRetSales = true;
			InvNo = bundle.getString(RetrieveSalesFragment.INVNO);
		} 
		//
		lvTotalItem = (ListView)act.findViewById(R.id.lvTotalItem);
		tvCartTotal = (TextView)act.findViewById(R.id.tvCartOrderTotal);
		///
		View vHeader = null;
		LayoutInflater inflater = (LayoutInflater)act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		vHeader = inflater.inflate(R.layout.payment_method_header, null);
		///
		View vFooter = null;
		vFooter = inflater.inflate(R.layout.payment_method_footer, null);
		
		etInvoiceCart = (EditText)vFooter.findViewById(R.id.etInvoiceCart);
		etFNameCart = (EditText)vFooter.findViewById(R.id.etFirstNameCart);
		etLNameCart = (EditText)vFooter.findViewById(R.id.etLastNameCart);
		etAddressCart = (EditText)vFooter.findViewById(R.id.etAddressCart);
		etEmailCart = (EditText)vFooter.findViewById(R.id.etEmailAddCart);
		etContactCart = (EditText)vFooter.findViewById(R.id.etContactCart);
		cbCartPark = (CheckBox)vFooter.findViewById(R.id.cbCartPark);
		spnTakenbyCart = (Spinner)vFooter.findViewById(R.id.spnTakenByCart);
		//
		etInvoiceCart.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
		etFNameCart.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
		etLNameCart.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
		etAddressCart.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
		etEmailCart.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
		etContactCart.setFilters(new InputFilter[] {new InputFilter.AllCaps(), new InputFilter.LengthFilter(11)});
		
		btnVoidCart = (Button)vFooter.findViewById(R.id.btnCartVoid);
		btnCheckCart = (Button)vFooter.findViewById(R.id.btnCheckCart);
		
		btnCheckoutCart = (Button)vFooter.findViewById(R.id.btnCheckoutCart);
		btnCheckoutCart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (validateCashPayment()) {
					//SubmitCashPayment(PaymentType.Checkout);
					callPromptDialog();
				} else {
					Utils.me.MessageBox(core, "Check All Fields/IMEI (Missing)");
				}
			}
		});
		////
		lvTotalItem.addHeaderView(vHeader);
		lvTotalItem.addFooterView(vFooter);
		
		adapter = new ItemCartAdapter(core, act, core.getApplication(), tvCartTotal, isRetSales, InvNo);
		
		lvTotalItem.setAdapter(adapter);
		
		btnSetPaymetPos = (Button)vHeader.findViewById(R.id.btnSetPaymentMode);
		
		btnSetPaymetPos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				lvTotalItem.setSelection(lvTotalItem.getCount() - 1);
			}
		});
		
		cbCartPark.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(cbCartPark.isChecked()){
					ParkMessage();
				}
			}
		});
		//
		btnCheckCart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//
				String val = etInvoiceCart.getText().toString();
				if(val.contains("#")){
					
					String[] sp = val.split("#");
					
					ValidationInvoiceServer(sp[0], sp[1]);
				} else {
					etInvoiceCart.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_failed, 0);
				}
				//
			}
		});
		
		// Populate Staff by KIOSK
		ArrayAdapter<String> spnStaffAdap = new ArrayAdapter<String>(core, android.R.layout.simple_spinner_item, PopulateStaff());
		spnStaffAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spnTakenbyCart.setAdapter(spnStaffAdap);
		//
		
		//
		if(!isRetSales){
			if(adapter.getCount() == 0){
				Utils.me.EmptyCartMessage(core, this, "No Item Found, back to main");
			}
			btnVoidCart.setVisibility(View.GONE);
		}
		//
		if(isRetSales){/////\\\\\
			
			final String Invno = bundle.getString(RetrieveSalesFragment.INVNO);
			etInvoiceCart.setText(Invno);
			String fullname = bundle.getString(RetrieveSalesFragment.NAME);
			String[] nameArr = fullname.split(" ",2);
			etFNameCart.setText(nameArr[0]);
			etLNameCart.setText(nameArr[1]);

			//etAddressCart.setText(bundle.getString(RetrieveSalesFragment.ADDRESS));
			etEmailCart.setText(bundle.getString(RetrieveSalesFragment.EMAIL));
			etContactCart.setText(bundle.getString(RetrieveSalesFragment.CONTACT));
			cbCartPark.setChecked(true);
			cbCartPark.setEnabled(false);
			
			etInvoiceCart.setEnabled(false);
			//
			btnVoidCart.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CancelledSalesInfo(Invno);
				}
			});
		}
		//
		
	}
	 // =========================================================================
	 // Overrides
	 // =========================================================================
	@Override
	public void Methods() {
		core.displayView(0);
	}
	 // =========================================================================
	 // Main Function
	 // =========================================================================
	@SuppressLint("HandlerLeak")
	private void SubmitCashPayment(final PaymentType pType){
		//
		si = new SalesInfo();
		si.invno = etInvoiceCart.getText().toString();
		si.name = etFNameCart.getText().toString() + " " + etLNameCart.getText().toString();
		//si.address = etAddressCart.getText().toString();
		si.address = "";
		si.emailadd = etEmailCart.getText().toString();
		String contact = etContactCart.getText().toString(); 
		if(!contact.equals("")){
			si.contact = contact;
		} else {
			si.contact = "0";
		}
		 
		si.custno = "99999";
		String total = tvCartTotal.getText().toString();
		si.total = total.replace("PHP ", "");
		si.empid = Vars.EmpID(core, "", type.GET);
		//
		int takenby = spnTakenbyCart.getSelectedItemPosition();
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

		
		if(sdList == null){
			sdList = adapter.GetSalesDetails();
		}
		//
		for(int x = 0; x < sdList.size(); x++){
			sdList.get(x).invno = si.invno;
		}
		final ArrayList<SalesDetails> listSD = new ArrayList<SalesDetails>(sdList);
		
		final ProgressDialog dialog = ProgressDialog.show(core, "Please wait...", "Posting Payment Process");
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
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
				if(!isRetSales){
					isInserted = JsonParser.PostPaymentToServer(si, listSD, core);	
				} else {
					isInserted = JsonParser.UpdateSalesInfo(si, listSD);
				}
				//
				handler.sendEmptyMessage(0);
			}
		};
		update.start();
	}
	
	private void callPromptDialog(){
		//
		String TotalPrice = tvCartTotal.getText().toString();
		TotalPrice = TotalPrice.replace("PHP ", "");
		
		DialogUtils.me.PaymentReceived(core, "Payment", "Enter Amount Received",TotalPrice, null, this);
	}
	
	@Override
	public void CheckoutNow(String change) {
		//
		AmtChange = change;
		SubmitCashPayment(PaymentType.Checkout);
	}
	 // =========================================================================
	 // Sub Function
	 // =========================================================================
	private boolean validateCashPayment(){
		
		if(etInvoiceCart.getText().toString().equals("") || 
				etFNameCart.getText().toString().equals("") ||
				etLNameCart.getText().toString().equals("") 
				){
			return false;
		}
		
		if(etEmailCart.getText().toString().equals("") &&
				etContactCart.getText().toString().equals("")){
			return false;
		}
		
		sdList = adapter.GetSalesDetails();
		
		for(SalesDetails sd : sdList){
			if(!sd.type.equals("accessory")){
				if(sd.imei_.equals("")){
					return false;
				}
			}
		}
		
		if(spnTakenbyCart.getSelectedItemPosition() == 0){
			return false;
		}
		
		if(!isValidateSeries){
			return false;
		}
		
		return true;
		
	}
	
	private void setResult(PaymentType pType){
		
		switch (pType) {
		case Checkout:
			if(isInserted){
				
				//Utils.me.MessageBox(core, "Payment Successful");
				
				ArrayList<Item> itemWithMark = new ArrayList<Item>();
				itemWithMark = new CloudfoneDB(core).openToRead().selectAllMarks();
				
				for(Item item : itemWithMark){
					new CloudfoneDB(core).openToWrite().setMarkOutItem(item.ID);
				}
				//////
				
				//////Stocks and IMEI adjustment
				for(SalesDetails sd : sdList){
					//
					new CloudfoneDB(core).openToWrite().UpdateItemStock(Integer.parseInt(sd.qty), Integer.parseInt(sd.itemid), sd.imei_);
				}
				//////
				DialogUtils.me.GenerateReciept("Payment Status", "Checkout Successful", core, si, sdList, AmtChange);
				
				// Minus Counter for parked Items
				if(isRetSales){
					Vars.ParkedMethods(core, method.SUB);
				}
				//
				core.displayView(0);
			} else {
				Utils.me.MessageBox(core, "Payment Failed. Check your Invoice/Fields if duplicate");
			}
			break;
		case Park:
			if(isInserted){
				Utils.me.MessageBox(core, "Parked Succeed");
				ArrayList<Item> itemWithMark = new ArrayList<Item>();
				itemWithMark = new CloudfoneDB(core).openToRead().selectAllMarks();
				
				for(Item item : itemWithMark){
					new CloudfoneDB(core).openToWrite().setMarkOutItem(item.ID);
				}
				//
				Vars.ParkedMethods(core, method.ADD);
				//
				core.displayView(0);
			} else {
				Utils.me.MessageBox(core, "Parked Failed/Check Invoice if already used.");
				cbCartPark.setChecked(false);
			}
			break;
		}
		
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
				
				String invno = etInvoiceCart.getText().toString();
				String fname = etFNameCart.getText().toString();
				String lname = etLNameCart.getText().toString();
				
				if(!invno.equals("") && !fname.equals("") && !lname.equals("")){
					//cbCartPark.setEnabled(false);
					 
					 parkRemarks = input.getText().toString();
					 SubmitCashPayment(PaymentType.Park);
				}else {
					com.example.redfootpos.utils.Utils.me.MessageBox(core, "Invoice #\nFirst Name\nLast Name is required");
					cbCartPark.setChecked(false);
				}
			}
		});
		
		dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				cbCartPark.setChecked(false);
			}
		});
		dialog.show();
	}
	
	@SuppressLint("HandlerLeak")
	private void CancelledSalesInfo(final String invno){
		//
		final ProgressDialog dialog = ProgressDialog.show(core, "Please Wait...", "Cancelled process");
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				dialog.dismiss();
				//
				setCancelledResult();
			}
		};
		Thread process = new Thread(){
			@Override
			public void run() {
				super.run();
				isCancelled = JsonParser.CancelledSalesInfo(invno);
				//
				handler.sendEmptyMessage(0);
			}
		};
		process.start();
	}
	
	private void setCancelledResult(){
		if(isCancelled){
			Utils.me.MessageBox(core, "Cancelled Success");
			Vars.ParkedMethods(core, method.SUB);
			core.displayView(0);
		} else {
			Utils.me.MessageBox(core, "Cancelled Failed");
		}
	}
	
	private String[] PopulateStaff(){
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
	private  void ValidationInvoiceServer(final String prefix, final String digits){
		//
		final ProgressDialog dialog = ProgressDialog.show(core, "Please Wait...", "Validating Invoice");
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
	
	private  void ResultValidation(){
		
		if(ValidInt == 1000){
			isValidateSeries = false;
			com.example.redfootpos.utils.Utils.me.MessageBox(core, "Already Exist");
		} else if(ValidInt > 0 && ValidInt < 1000){
			isValidateSeries = true;
		} else if(ValidInt == 0){
			isValidateSeries = false;
		}
		
		if(isValidateSeries){
			
			etInvoiceCart.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_succes, 0);
		} else {
			etInvoiceCart.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_failed, 0);
		}
	}
	
	private enum PaymentType{
		Checkout,
		Park
	}
	 // =========================================================================
	 // Final
}
