package com.example.redfootpos.fragments;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.Vars;
import com.example.redfootpos.Vars.type;
import com.example.redfootpos.model.ReturnDetails;
import com.example.redfootpos.model.ReturnInfo;
import com.example.redfootpos.model.SalesDetails;
import com.example.redfootpos.model.SalesInfo;
import com.example.redfootpos.utils.JsonParser;
import com.example.redfootpos.utils.Utils;
import com.itextpdf.text.pdf.PdfDiv.PositionType;

public class ReturnFragment extends Fragment{
	 // =========================================================================
	 // Variables
	 // =========================================================================
	public FragmentActivity act;
	public MainAct core;
	
	public EditText etRetReference, etRetInvNum, etRetCustName, etRetAddress, etRetNotes, etRetQty, etRetUnitPrice, etRetReason, etRetCustNo;
	public Button btnRetDate, btnRetItemSel, btnRetItemReturn;
	private Spinner spnRetItems;
	private TableLayout tblSel;
	
	private SalesInfo siImp;
	private ArrayList<SalesDetails> sdList = new ArrayList<SalesDetails>();
	private ArrayList<SalesDetails> sdSelectProd = new ArrayList<SalesDetails>();
	private ArrayList<ReturnDetails> rdListVar = new ArrayList<ReturnDetails>();
	
	private ArrayAdapter<String> spinAdapt;
	
	private int SelItemPos = 0;
	private boolean isRIInserted = false;
	 // =========================================================================
	 // TODO Overrides
	 // =========================================================================
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//
		return inflater.inflate(R.layout.fragment_return, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//
		act = getActivity();
		core = (MainAct)act;
		//
		etRetReference = (EditText)core.findViewById(R.id.etRetReference);
		etRetInvNum = (EditText)core.findViewById(R.id.etRetInvNum);
		etRetCustName = (EditText)core.findViewById(R.id.etRetCustName);
		etRetCustNo = (EditText)core.findViewById(R.id.etRetCustNo);
		etRetAddress = (EditText)core.findViewById(R.id.etRetAddress);
		etRetNotes = (EditText)core.findViewById(R.id.etRetNotes);
		etRetQty = (EditText)core.findViewById(R.id.etRetQty);
		etRetUnitPrice = (EditText)core.findViewById(R.id.etRetUnitPrice);
		etRetReason = (EditText)core.findViewById(R.id.etRetReason);
		
		btnRetDate = (Button)core.findViewById(R.id.btnRetDate);
		btnRetItemSel = (Button)core.findViewById(R.id.btnRetItemSel);
		btnRetItemReturn = (Button)core.findViewById(R.id.btnRetItemToReturn);
		
		tblSel = (TableLayout)core.findViewById(R.id.tblRetItemSel);
		
		spnRetItems = (Spinner)core.findViewById(R.id.spnRetItems);
		//
		etRetReference.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
		etRetInvNum.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
		etRetCustName.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
		etRetCustNo.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
		etRetAddress.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
		etRetNotes.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
		etRetQty.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
		etRetUnitPrice.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
		etRetReason.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
		//
		etRetCustName.setEnabled(false);
		etRetCustNo.setEnabled(false);
		tblSel.setStretchAllColumns(true);
		//
		btnRetDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CallDatePicker();
			}
		});
		btnRetItemSel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
					setTableSel();
				
			}
		});
		//
		etRetInvNum.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus){
					String invno = etRetInvNum.getText().toString();
					CallInvoiceDetails(invno);
					tblSel.removeAllViews();
					sdSelectProd.clear();
				}
			}
		});
	
		spnRetItems.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				//
				String unitprice = sdList.get(position).unitprice;
				String qty = sdList.get(position).qty;
				
				etRetUnitPrice.setText(unitprice);
				etRetQty.setText(qty);
				
				SelItemPos = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
		
		btnRetItemReturn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PostReturnToServer();
			}
		});
	}
	 
	private DatePickerDialog.OnDateSetListener mDateSet = new DatePickerDialog.OnDateSetListener() {
		
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			
			monthOfYear +=1;
			String month = String.valueOf(monthOfYear);
			String day = String.valueOf(dayOfMonth);
				
			month = month.length() == 1 ? "0"+month:month;
			day = day.length() == 1 ? "0"+day:day;
			
			String res = year+"-"+month+ "-"+ day;
			
			btnRetDate.setText(res);
		}
	};
	// =========================================================================
	 // TODO Main Functions
	 // =========================================================================
	private void CallDatePicker(){
		//
		Calendar calendar = Calendar.getInstance(); 
		int year, month, day;
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		
		DatePickerDialog dpd = new DatePickerDialog(core, mDateSet, year, month, day);
		dpd.show();
		
	}
	
	
	@SuppressLint("HandlerLeak")
	private void CallInvoiceDetails(final String invno){
		//
		final ProgressDialog dialog = ProgressDialog.show(core, "Please Wait...", "Getting information fro Invoice No.");
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				dialog.dismiss();
				//
				SetInvNoResult();
			}
		};
		Thread process = new Thread(){
			@Override
			public void run() {
				super.run();
				//
				siImp = JsonParser.GetReturnDetails(invno);
				//
				handler.sendEmptyMessage(0);
			}
		};
		process.start();
	}
	 
	private void PostReturnToServer(){
		//
		
		if(ValidateInput()){
			if (ValidateItemSel()) {
				//
				ReturnInfo ri = new ReturnInfo();
				//ri.retno = getStr(etRetReference);
				ri.retdate = btnRetDate.getText().toString();
				ri.custname = getStr(etRetCustName);
				ri.custno = getStr(etRetCustNo);
				//ri.address = getStr(etRetAddress);
				ri.address = "";
				ri.notes = getStr(etRetNotes);
				ri.empid = Vars.EmpID(core, "", type.GET);
				ri.kioskid = Vars.KioskID(core, "", type.GET);
				//
				//ArrayList<ReturnDetails> rdList = PopulateSelItems();
				
				//
				ServerInsertReturn(ri, rdListVar);
			} else {
				Utils.me.MessageBox(core, "Error: No Item Selected");
			}
		} else {
			Utils.me.MessageBox(core, "Error : Check your Fields/Date");
		}
		
		
	}
	
	@SuppressLint("HandlerLeak")
	private void ServerInsertReturn(final ReturnInfo ri, final ArrayList<ReturnDetails> rdList){
		//
		final ProgressDialog dialog = ProgressDialog.show(core, "Please Wait...", "Process Return");
		final Handler hand = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				//
				SetReturnPostResult();
				//
				dialog.dismiss();
			}
		};
		Thread process = new Thread(){
			@Override
			public void run() {
				super.run();
				isRIInserted = JsonParser.PostReturnToServer(ri, rdList);
				hand.sendEmptyMessage(0);
			}
		};
		process.start();
	}
	// =========================================================================
	 // TODO Sub Functions
	 // =========================================================================
	private void SetInvNoResult(){
		//
		etRetCustName.setText(siImp.name);
		
		etRetCustNo.setText(siImp.custno);
		etRetAddress.setText(siImp.address);
		
		SetInvDetails(siImp.invno);
	}
	
	@SuppressLint("HandlerLeak")
	private void SetInvDetails(final String invno){
		//
		final ProgressDialog dialog = ProgressDialog.show(core, "Please Wait... ", "Getting Items");
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				dialog.dismiss();
				//
				SetInvDetails();
			}
		};
		Thread process = new Thread(){
			@Override
			public void run() {
				super.run();
				sdList = JsonParser.GetSalesDetails(invno);
				handler.sendEmptyMessage(0);
			}
		};
		process.start();
	}
	
	private void SetInvDetails(){
		//
		int count = sdList.size();
		
		if(count != 0){
			//
			String[] itemArr = new String[count];
			
			int x = 0;
			for(SalesDetails sd : sdList){
				//
				itemArr[x] = sd.prodname;
				//
				x++;
			}
			
			spinAdapt = new ArrayAdapter<String>(core, android.R.layout.simple_spinner_item, itemArr);
			spinAdapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spnRetItems.setAdapter(spinAdapt);
		}
	}
	
	private void setTableSel(){
		//
		boolean isExist = true;
		
		if(sdList.size() == 0){
			isExist = false;
		}
		
		if(getStr(etRetReason).length() == 0){
			isExist = false;
			Utils.me.MessageBox(core, "Reason Field is Required");
		} 
		
		for(ReturnDetails rdDup : rdListVar){
			String nameProd = spnRetItems.getSelectedItem().toString();
			
			if(rdDup.ProdName_.equals(nameProd)){
				isExist = false;
				break;
			}
			
		}
		//
		if (isExist) {
			tblSel.removeAllViews();
			//
			sdList.get(SelItemPos).qty = etRetQty.getText().toString();
			sdList.get(SelItemPos).unitprice = etRetUnitPrice.getText().toString();
			//
			ReturnDetails rd = new ReturnDetails();
			rd.RetQty = getStr(etRetQty);
			rd.UnitPx = getStr(etRetUnitPrice);
			rd.InvNo = sdList.get(SelItemPos).invno;
			rd.ProdID = sdList.get(SelItemPos).itemid;
			rd.Unit = "PCS";
			rd.Reason = getStr(etRetReason);
			rd.ProdName_ = sdList.get(SelItemPos).prodname;
			
			rdListVar.add(rd);
			sdSelectProd.add(sdList.get(SelItemPos));
			//
			TableRow trd = new TableRow(core);
			//
			TextView t1d = new TextView(core);
			t1d.setText("Items");
			trd.addView(t1d);
			//
			TextView t2d = new TextView(core);
			t2d.setText("QTY");
			trd.addView(t2d);
			//
			TextView t3d = new TextView(core);
			t3d.setText("Price");
			trd.addView(t3d);
			//
			TextView t4d = new TextView(core);
			t4d.setText("Reason");
			trd.addView(t4d);
			//
			tblSel.addView(trd);
			//
			for (ReturnDetails rdIns : rdListVar) {
				//
				TableRow tr = new TableRow(core);
				//
				TextView t1 = new TextView(core);
				t1.setText(rdIns.ProdName_);

				tr.addView(t1);
				//
				TextView t2 = new TextView(core);
				t2.setText(rdIns.RetQty);

				tr.addView(t2);
				//
				int price = Utils.me.CurrencyStringToInt(rdIns.UnitPx);
				String priceStr = Utils.me.FormatCurrencyToString(price, "");
				TextView t3 = new TextView(core);
				t3.setText(priceStr);

				tr.addView(t3);
				//
				TextView t4 = new TextView(core);
				t4.setText(rdIns.Reason);
				
				tr.addView(t4);
				//
				tblSel.addView(tr);
			}
			//
			etRetReason.setText("");
		}
		///
	}

	private String getStr(EditText et){
		return et.getText().toString();
	}
	
	private boolean ValidateInput(){
		
		boolean isValid = true;
		
		//String ref = getStr(etRetReference);
		String inv = getStr(etRetInvNum);
		String add = getStr(etRetAddress);
		String reason = getStr(etRetReason);
		String date = btnRetDate.getText().toString();
		
		
		if(inv.isEmpty() || date.equals("Select Date")){
			//
			isValid = false;
		}
		
		return isValid;
	}
	
	private boolean ValidateItemSel(){
		boolean isValid = false;
		
		if(sdSelectProd.size() != 0){
			isValid = true;
		}
		
		return isValid;
	}
	
	private void SetReturnPostResult(){
		
		if(isRIInserted){
			Utils.me.MessageBox(core, "Insert Success");
			core.displayView(0);
		} else {
			Utils.me.MessageBox(core, "Insert Failed");
		}
	}
	
	private ArrayList<ReturnDetails> PopulateSelItems(){
		//
		ArrayList<ReturnDetails> rdList = new ArrayList<ReturnDetails>();
		
		for(SalesDetails sd : sdSelectProd){
			ReturnDetails rd = new ReturnDetails();
			
			//
			rd.InvNo = getStr(etRetInvNum);
			rd.ProdID = sd.itemid;
			rd.RetQty = sd.qty;
			rd.Unit = "PCS";
			rd.UnitPx = sd.unitprice;
			rd.Reason = getStr(etRetReason);
		
			rdList.add(rd);
		}
		
		return rdList;
	}
}
