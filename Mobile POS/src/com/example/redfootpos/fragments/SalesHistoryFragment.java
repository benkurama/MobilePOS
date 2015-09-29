package com.example.redfootpos.fragments;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.Vars;
import com.example.redfootpos.Vars.type;
import com.example.redfootpos.database.CloudfoneDB;
import com.example.redfootpos.model.FootTraffic;
import com.example.redfootpos.model.Item;
import com.example.redfootpos.model.SalesInfo;
import com.example.redfootpos.model.Top10Product;
import com.example.redfootpos.object.PDFMaker;
import com.example.redfootpos.utils.JsonParser;
import com.example.redfootpos.utils.Utils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public class SalesHistoryFragment extends Fragment{
	 // =========================================================================
	 // Variables
	 // =========================================================================
	public FragmentActivity act;
	public MainAct core;
	
	private ExpandableListView elvSalesHistory;
	private List<String> listGroup;
	private HashMap<String, List<String>> listChild;
	private ArrayList<Top10Product> topProd = new ArrayList<Top10Product>();
	private ArrayList<SalesInfo> SalesReps = new ArrayList<SalesInfo>();
	private ArrayList<SalesInfo> SalesKiosk = new ArrayList<SalesInfo>();
	private ArrayList<FootTraffic> FootTrafficList = new ArrayList<FootTraffic>();
	
	private int option = 0;
	private TextView tv;
	
	private String strTop10From, strTop10To, strSalesRepfrom, strSalesRepTo,
	strKioskFrom, strKioskTo;
	 // =========================================================================
	 // Overrides
	 // =========================================================================
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		return inflater.inflate(R.layout.fragment_saleshistory, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		act = getActivity();
		core = (MainAct)act;
		//
		elvSalesHistory = (ExpandableListView)core.findViewById(R.id.elvSalesHistory);
		
		PrepareList();
		
		//elvSalesHistory.expandGroup(0);
	}
	 // =========================================================================
	 // Main Functions
	 // =========================================================================
	private void PrepareList(){
		//
		listGroup = new ArrayList<String>();
		listChild = new HashMap<String, List<String>>();
		//
		listGroup.add("TOP 10 PRODUCT SOLD");
		listGroup.add("DAILY REPORT BY SALES REP.");
		listGroup.add("DAILY REPORTY BY KIOSK");
		listGroup.add("FOOT TRAFFIC REPORY BY WEEK");
		//
		List<String> top10product = new ArrayList<String>();
		top10product.add("top10");
		
		List<String> top10agent = new ArrayList<String>();
		top10agent.add("salesreport");
		
		List<String> top10customer = new ArrayList<String>();
		top10customer.add("saleskiosk");
		
		List<String> footTraffic= new ArrayList<String>();
		footTraffic.add("foottraffic");
		//
		listChild.put(listGroup.get(0), top10product);
		listChild.put(listGroup.get(1), top10agent);
		listChild.put(listGroup.get(2), top10customer);
		listChild.put(listGroup.get(3), footTraffic);
		//
		elvSalesHistory.setAdapter(new SalesAdapter(core, listGroup, listChild));
		//
		elvSalesHistory.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
				
				return false;
			}
		});
		//
		elvSalesHistory.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,	int groupPosition, long id) {
				
				return false;
			}
		});
	}
	
	@SuppressLint("HandlerLeak")
	private void callTop10ProdServer(final String from, final String to){
		//
		final ProgressDialog dialog = ProgressDialog.show(core, "Please wait...", "fetching data from server");
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				dialog.dismiss();
				//
				SetResult();
			}
		};
		Thread process = new Thread(){
			@Override
			public void run() {
				super.run();
				topProd = JsonParser.Top10Prod(from, to);
				//
				handler.sendEmptyMessage(0);
			}
		};
		process.start();
	}
	
	@SuppressLint("HandlerLeak")
	private void callSalesReportServer(final String from,final String to,final String empid){
		//
		final ProgressDialog dialog = ProgressDialog.show(core, "Please wait...", "fetching data from server");
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				dialog.dismiss();
				//
				SetSalesReport();
			}
		};
		Thread process = new Thread(){
			@Override
			public void run() {
				super.run();
				SalesReps = JsonParser.GetSalesReport(from, to, empid);
				//
				handler.sendEmptyMessage(0);
			}
		};
		process.start();
	}
	
	@SuppressLint("HandlerLeak")
	private void callSalesKioskServer(final String from, final String to, final String kioskid){
		//
		final ProgressDialog dialog = ProgressDialog.show(core, "Please wait...", "fetching data from server");
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				//
				SetSalesKioskReport();
				//
				dialog.dismiss();
			}
		};
		Thread process = new Thread(){
			@Override
			public void run() {
				super.run();
				SalesKiosk = JsonParser.GetSalesReportByKiosk(from, to, kioskid);
				//
				handler.sendEmptyMessage(0);
			}
		};
		process.start();
		
	}
	
	@SuppressLint("HandlerLeak")
	private void callFootTrafficServer(final String from, final String to, final String kioskid){
		//
		@SuppressWarnings("unused")
		final ProgressDialog dialog = ProgressDialog.show(core, "Please wait...", "Get Records");
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				dialog.dismiss();
				SetFootTrafficReport();
			}
		};
		Thread process = new Thread(){
			@Override
			public void run() {
				super.run();
				FootTrafficList = JsonParser.GetFootTrafficReport(from, to, kioskid);
				handler.sendEmptyMessage(0);
			}
		};
		process.start();
	}
	
	private void SetResult(){
		
		if(topProd.size() != 0){
			PDFWriter();
		}else {
			Utils.me.MessageBox(core, "No Record(s) Found");
		}
		
	}
	
	private void SetSalesReport(){
		
		if(SalesReps.size() != 0){
			PDFWriteForSalesReport();
		}else {
			Utils.me.MessageBox(core, "No Record(s) Found");
		}
	}
	
	private void SetSalesKioskReport(){
		//
		if(SalesKiosk.size() != 0){
			PDFWriterForKioskReport();
		}else {
			Utils.me.MessageBox(core, "No Record(s) Found");
		}
	}
	
	private void SetFootTrafficReport(){
		
		if(FootTrafficList.size() != 0){
			//Utils.me.MessageBox(core, FootTrafficList.get(0).ClassName_);
			PDFWriterForFootTraffic();
		} else{
			Utils.me.MessageBox(core, "No Record(s) Found");
		}
	}
	
	private void PDFWriter(){
		
		String from = strTop10From;
		String to = strTop10To;
		
		String agent = "User: "+Vars.FirstName(core, "", type.GET) + " "+ Vars.LastName(core, "", type.GET);
		String username = Vars.UserName(core, "", type.GET);
		//
		
		String FileName = "Reciept_"+username+"_"+from+"_"+to;
		
		PDFMaker pdf = new PDFMaker(FileName);
		pdf.AddMetaData("POS Sales History", "TOP 10 PRODUCT SOLD", "Mobile POS");
		
		Paragraph prag = new Paragraph("TOP 10 PRODUCT SOLD", PDFMaker.catFont);
		//
		
		String date = "From: "+from+" - To: "+to;
		prag.add(new Paragraph(date, PDFMaker.subFont));
		//
		prag.add(new Paragraph(agent, PDFMaker.subFont));
		//
		pdf.addEmptyLine(prag, 2);
		
		int column = 5;
		PdfPTable table = new PdfPTable(column);
		
		table.setTotalWidth(520);
		table.setLockedWidth(true);
		
		PdfPCell cell = new PdfPCell();
		
		cell = new PdfPCell(new Phrase("Prod ID", PDFMaker.smallBold));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setBorder(Rectangle.BOTTOM);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Prod Name", PDFMaker.smallBold));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setBorder(Rectangle.BOTTOM);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Total Qty", PDFMaker.smallBold));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setBorder(Rectangle.BOTTOM);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Unit Price", PDFMaker.smallBold));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setBorder(Rectangle.BOTTOM);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Total Amt", PDFMaker.smallBold));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setBorder(Rectangle.BOTTOM);
		table.addCell(cell);
		
		table.setHeaderRows(1);
		
		boolean isWhite = true;
		
		int GrandTotal = 0;
		int QtyTotal = 0;
		
		for(Top10Product ins : topProd){
			//
			cell = new PdfPCell(new Phrase(ins.ProdID+""));
			cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			formatCell(cell, isWhite);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(ins.ProdName));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			formatCell(cell, isWhite);
			table.addCell(cell);
			
			QtyTotal += ins.TotalQty;
			
			cell = new PdfPCell(new Phrase(ins.TotalQty+""));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			formatCell(cell, isWhite);
			table.addCell(cell);
			
			Item item = new CloudfoneDB(core).openToRead().GetItemsByID(ins.ProdID);
			
			cell = new PdfPCell(new Phrase(item.price));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			formatCell(cell, isWhite);
			table.addCell(cell);
			
			int TotalAmtInt = Utils.me.CurrencyStringToInt(ins.TotalAmt);
			GrandTotal += TotalAmtInt;
			String TotalAmtStr = Utils.me.FormatCurrencyToString(TotalAmtInt, "");
			
			cell = new PdfPCell(new Phrase(TotalAmtStr));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			formatCell(cell, isWhite);
			table.addCell(cell);
			
			isWhite = !isWhite;
			
		}
		// For Footer Format
		cell = new PdfPCell(new Phrase(""));
		cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
		cell.setBorder(Rectangle.TOP);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase(""));
		cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
		cell.setBorder(Rectangle.TOP);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase(QtyTotal+"", PDFMaker.smallBold));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setBorder(Rectangle.TOP);
		table.addCell(cell);

		cell = new PdfPCell(new Phrase(""));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setBorder(Rectangle.TOP);
		table.addCell(cell);
		//
		String GrandTotalStr = Utils.me.FormatCurrencyToString(GrandTotal, "");
		//
		cell = new PdfPCell(new Phrase(GrandTotalStr, PDFMaker.smallBold));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setBorder(Rectangle.TOP);
		table.addCell(cell);
		// finally
	    prag.add(table);
	    
		pdf.post(prag);
		//
		pdf.close();
		
		PDFMaker.OpenPDF(core, FileName);
	}
	 
	private void PDFWriteForSalesReport(){
		
		String from = strSalesRepfrom;
		String to = strSalesRepTo;
		
		String agent = "User: "+Vars.FirstName(core, "", type.GET) + " "+ Vars.LastName(core, "", type.GET);
		String username = Vars.UserName(core, "", type.GET);
		String date = "From "+from+" to "+to;
		//
		String FileName = "SalesReport_"+username+"_"+from+"_"+to;
		//
		PDFMaker pdf = new PDFMaker(FileName);
		pdf.AddMetaData("Sales Report", "Sales Report By Agent", "Mobile POS");
		
		Paragraph prag = new Paragraph("SALES REPORT", PDFMaker.catFont);
		
		prag.add(new Paragraph(date, PDFMaker.subFont));
		prag.add(new Paragraph(agent, PDFMaker.subFont));
		
		pdf.addEmptyLine(prag, 2);
		
		PdfPTable table = new PdfPTable(4);
		
		table.setTotalWidth(520);
		table.setLockedWidth(true);
		
		PdfPCell cell = new PdfPCell();
		
		cell = new PdfPCell(new Phrase("Invoice No.", PDFMaker.smallBold));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setBorder(Rectangle.BOTTOM);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Customer Name", PDFMaker.smallBold));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setBorder(Rectangle.BOTTOM);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Email Add", PDFMaker.smallBold));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setBorder(Rectangle.BOTTOM);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Total Amount", PDFMaker.smallBold));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setBorder(Rectangle.BOTTOM);
		table.addCell(cell);
		
		table.setHeaderRows(1);
		boolean isWhite = true;
		
		int GrandTotal = 0;
		
		for(SalesInfo si : SalesReps){
			//
			cell = new PdfPCell(new Phrase(si.invno+""));
			cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			formatCell(cell, isWhite);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(si.name+""));
			cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			formatCell(cell, isWhite);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(si.emailadd+""));
			cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			formatCell(cell, isWhite);
			table.addCell(cell);
			
			int total = Utils.me.CurrencyStringToInt(si.total);
			GrandTotal += total;
			String totalStr = Utils.me.FormatCurrencyToString(total, "");
			
			cell = new PdfPCell(new Phrase(totalStr+""));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			formatCell(cell, isWhite);
			table.addCell(cell);
			
			isWhite = !isWhite;
		}
		
		prag.add(table);
		
		pdf.addEmptyLine(prag, 2);
		
		String GrandStr = Utils.me.FormatCurrencyToString(GrandTotal, "");
		
		Paragraph GrandTot = new Paragraph("Grand Total: "+GrandStr, PDFMaker.smallBold);
		GrandTot.setAlignment(Element.ALIGN_RIGHT);
		
		prag.add(GrandTot);
		
		pdf.post(prag);
		pdf.close();
		//                                                                
		PDFMaker.OpenPDF(core, FileName);
	}
	
	private void PDFWriterForKioskReport(){
		//
		String from = strKioskFrom;
		String to = strKioskTo;
		
		String agent = "User: "+Vars.FirstName(core, "", type.GET) + " "+ Vars.LastName(core, "", type.GET);
		String username = Vars.UserName(core, "", type.GET);
		String date = "From "+from+" to "+to;
		//
		String FileName = "SalesKioskReport_"+username+"_"+from+"_"+to;
//		//
		PDFMaker pdf = new PDFMaker(FileName);
		pdf.AddMetaData("Sales Kiosk Report", "Sales Report By Agent", "Mobile POS");
		
		Paragraph prag = new Paragraph("SALES REPORT", PDFMaker.catFont);
		
		prag.add(new Paragraph(date, PDFMaker.subFont));
		prag.add(new Paragraph("Agent: "+agent, PDFMaker.subFont));
		
		pdf.addEmptyLine(prag, 2);
		// get all empid data with duplicate
		List<String> empidList = new ArrayList<String>();
		for(SalesInfo si : SalesKiosk){
			empidList.add(si.empid);
		}
		
		empidList = Utils.me.DistinctList(empidList);
		// create a LIST for ArrayList SalesInfo
		List<ArrayList<SalesInfo>> container =  new ArrayList<ArrayList<SalesInfo>>();
		ArrayList<SalesInfo> load;
		
		int x = 0;
		for(String empid : empidList){
			//
			load =  new ArrayList<SalesInfo>();
			//
			for(SalesInfo si : SalesKiosk){
				//
				if(si.empid.equals(empid)){
					load.add(si);
				}
			}
			container.add(load);
			//
			x++;
		}
		//
		int GrandTotal = 0;
		for(int y = 0; y < container.size(); y++){
			
			String Title = "("+container.get(y).get(0).empid+") - " + container.get(y).get(0)._empname;
			
			prag.add(new Paragraph(Title, PDFMaker.subFont));
			pdf.addEmptyLine(prag, 1);
			//
			PdfPTable table = new PdfPTable(4);
			
			table.setTotalWidth(520);
			table.setLockedWidth(true);
			
			PdfPCell cell = new PdfPCell();
			
			cell = new PdfPCell(new Phrase("Invoice No.", PDFMaker.smallBold));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setBorder(Rectangle.BOTTOM);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase("Customer Name", PDFMaker.smallBold));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setBorder(Rectangle.BOTTOM);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase("Email Add", PDFMaker.smallBold));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			cell.setBorder(Rectangle.BOTTOM);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase("Total Amount", PDFMaker.smallBold));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setBorder(Rectangle.BOTTOM);
			table.addCell(cell);
			
			boolean isWhite = true;
			table.setHeaderRows(1);
			//
			
			int SubTotal = 0;
			//
			for(SalesInfo siList : container.get(y)){
				//
				cell = new PdfPCell(new Phrase(siList.invno+""));
				cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
				formatCell(cell, isWhite);
				table.addCell(cell);
				
				cell = new PdfPCell(new Phrase(siList.name+""));
				cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
				formatCell(cell, isWhite);
				table.addCell(cell);
				
				cell = new PdfPCell(new Phrase(siList.emailadd+""));
				cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
				formatCell(cell, isWhite);
				table.addCell(cell);
				
				int price = Utils.me.CurrencyStringToInt(siList.total);
				SubTotal += price;
				String curPrice = Utils.me.FormatCurrencyToString(SubTotal, "");
				
				cell = new PdfPCell(new Phrase(curPrice));
				cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
				formatCell(cell, isWhite);
				table.addCell(cell);
				
				isWhite = !isWhite;
				//
			}
			prag.add(table);
			//
			pdf.addEmptyLine(prag, 2);
			
			String SubTotalStr = Utils.me.FormatCurrencyToString(SubTotal, "");
			GrandTotal += SubTotal;
			
			Paragraph SubT = new Paragraph("Subtotal: "+SubTotalStr, PDFMaker.smallBold);
			SubT.setAlignment(Element.ALIGN_RIGHT);
			prag.add(SubT);
			
			pdf.addEmptyLine(prag, 2);
			
		}
		String GrandTotalStr = Utils.me.FormatCurrencyToString(GrandTotal, "");
		//
		Chunk c = new Chunk("Grand Total: "+GrandTotalStr, PDFMaker.smallBold);
		c.setBackground(BaseColor.CYAN,5,5,5,5);
		
		Paragraph prgGrand= new Paragraph(c);
		prgGrand.setAlignment(Element.ALIGN_RIGHT);
		
		prag.add(prgGrand);
		//
		pdf.post(prag);
		pdf.close();
		//                                                                
		PDFMaker.OpenPDF(core, FileName);
	}
	
	private void PDFWriterForFootTraffic(){
		//
		String agent = "User: "+Vars.FirstName(core, "", type.GET) + " "+ Vars.LastName(core, "", type.GET);
		String username = Vars.UserName(core, "", type.GET);
		
		String FileName = "FootTrafficReport_"+username;
		
		PDFMaker pdf = new PDFMaker(FileName);
		pdf.AddMetaData("Foot Traffic Report", "Foot Traffic", "Mobile POS");
		
		Paragraph prag = new Paragraph("FOOT TRAFFIC REPORT", PDFMaker.catFont);
		
		prag.add(new Paragraph("Agent: "+agent, PDFMaker.subFont));
		
		pdf.addEmptyLine(prag, 2);
		////
		int column = 4;
		PdfPTable table = new PdfPTable(column);
		
		table.setTotalWidth(520);
//		try {
//			table.setWidths(new int[]{400, 200, 100,100});
//		} catch (DocumentException e) {
//			e.printStackTrace();
//		}
		table.setLockedWidth(true);
		
		
		PdfPCell cell = new PdfPCell();
		
		cell = new PdfPCell(new Phrase("Class Name", PDFMaker.smallBold));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setBorder(Rectangle.BOTTOM);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Kiosk Name", PDFMaker.smallBold));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setBorder(Rectangle.BOTTOM);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Date", PDFMaker.smallBold));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setBorder(Rectangle.BOTTOM);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Total Traffic", PDFMaker.smallBold));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setBorder(Rectangle.BOTTOM);
		table.addCell(cell);
		
		table.setHeaderRows(1);
		////
		boolean isWhite = true;
		
		for(FootTraffic ft : FootTrafficList){
			//
			cell = new PdfPCell(new Phrase(ft.ClassName_+""));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			formatCell(cell, isWhite);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(ft.KioskName_+""));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			formatCell(cell, isWhite);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(ft.DaysIn_+""));
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
			formatCell(cell, isWhite);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(ft.TrafficCounter_+""));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			formatCell(cell, isWhite);
			table.addCell(cell);
			
			//
			isWhite = !isWhite;
		}
		
		// finally
	    prag.add(table);
		pdf.post(prag);
		
		pdf.close();
		//                                                                
		PDFMaker.OpenPDF(core, FileName);
	}
	// =========================================================================
	 // Sub functions
	 // =========================================================================
	private void formatCell(PdfPCell cell, boolean isWhite){
		cell.setBorder(Rectangle.NO_BORDER);
		if(isWhite){
			cell.setBackgroundColor(new BaseColor(255, 255, 255));
		} else {
			cell.setBackgroundColor(new BaseColor(250, 250, 250));
		}
		cell.setPadding(5);
	}
	
	private void CallDatePicker(TextView tv){
		
		this.tv = tv;
		
		Calendar calendar = Calendar.getInstance(); 
		int year, month, day;
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		
		DatePickerDialog dpd = new DatePickerDialog(core, mDateSet, year, month, day);
		dpd.show();
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
			
			tv.setText(res);
				
		}
	};
	
	@SuppressLint("SimpleDateFormat")
	private boolean DateValidation(String from, String to){
		
		boolean isValid = false;
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date fromDate = df.parse(from);
			Date toDate = df.parse(to);
			
			if(fromDate.before(toDate)){
				isValid = true;
			} else if(fromDate.equals(toDate)){
				isValid = true;
			}
			
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		
		return isValid;
		
	}
	 // =========================================================================
	 // Inner class
	 // =========================================================================
	private class SalesAdapter extends BaseExpandableListAdapter{
		
		private Context coreAdapter;
		private List<String> _listGroup;
		private HashMap<String, List<String>> _listChild;
		
		public SalesAdapter(Context context, List<String> listHeader, HashMap<String,List<String>> listchild){
			this.coreAdapter = context;
			this._listGroup = listHeader;
			this._listChild = listchild;
		}

		@Override
		public int getGroupCount() {
			return this._listGroup.size();
		}

		@SuppressLint("SimpleDateFormat")
		@Override
		public int getChildrenCount(int groupPosition) {
			return this._listChild.get(this._listGroup.get(groupPosition)).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return _listGroup.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return _listChild.get(_listGroup.get(groupPosition)).get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
			
			String headTitle = (String)getGroup(groupPosition);
			if(convertView == null){
				LayoutInflater inflater = (LayoutInflater)this.coreAdapter.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.rowheader_layout, null);
			}
			TextView title = (TextView)convertView.findViewById(R.id.lblListHeader);
			
			title.setText(headTitle);
			
			return convertView;
		}
		
		@Override
		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
			//
			final String childCateg = (String)getChild(groupPosition, childPosition);
			//
			
			if(childCateg.equals("top10")){
				//
				LayoutInflater inflater = (LayoutInflater)coreAdapter.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.rowchild_salesfilter_layout, null);
			
				final TextView tvFrom = (TextView)convertView.findViewById(R.id.tvSalesFilDateFrom);
				final TextView tvTo = (TextView)convertView.findViewById(R.id.tvSalesFilDateTo);
				
				Button btnFrom = (Button)convertView.findViewById(R.id.btnSalesFilFrom);
				btnFrom.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						CallDatePicker(tvFrom);
					}
				});
				
				Button btnTo = (Button)convertView.findViewById(R.id.btnSalesFilTo);
				btnTo.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						CallDatePicker(tvTo);
					}
				});
				
				Button btn = (Button)convertView.findViewById(R.id.btnSalesFilter);
				btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						String from = tvFrom.getText().toString();
						String to = tvTo.getText().toString();
						
						strTop10From = from;
						strTop10To = to;
						
						if(!from.equals("") && !to.equals("")){
							if(DateValidation(from, to)){
								callTop10ProdServer(from, to);
							} else {
								Utils.me.MessageBox(core, "Invalid Date Range");
							}
						} else{
							Utils.me.MessageBox(core, "Date Range is required");
						}
						
					}
				});
				
			}
			//
			if(childCateg.equals("salesreport")){
				
				LayoutInflater inflater = (LayoutInflater)coreAdapter.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.rowchild_salesfilter_layout, null);
			
				final TextView tvFrom = (TextView)convertView.findViewById(R.id.tvSalesFilDateFrom);
				final TextView tvTo = (TextView)convertView.findViewById(R.id.tvSalesFilDateTo);
				
				// set initial value
				tvFrom.setText(Utils.me.GetDateNow());
				tvTo.setText(Utils.me.GetDateNow());
				
				Button btnFrom = (Button)convertView.findViewById(R.id.btnSalesFilFrom);
				btnFrom.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						CallDatePicker(tvFrom);
					}
				});
				
				Button btnTo = (Button)convertView.findViewById(R.id.btnSalesFilTo);
				btnTo.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						CallDatePicker(tvTo);
					}
				});
				
				Button btn = (Button)convertView.findViewById(R.id.btnSalesFilter);
				btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						String from = tvFrom.getText().toString();
						String to = tvTo.getText().toString();
						String empid = Vars.EmpID(core, "", type.GET);
						
						strSalesRepfrom = from;
						strSalesRepTo = to;
						
						if(!from.equals("") && !to.equals("")){
							if(DateValidation(from, to)){
								callSalesReportServer(from, to, empid);
							} else {
								Utils.me.MessageBox(core, "Invalid Date Range");
							}
						} else{
							Utils.me.MessageBox(core, "Date Range is required");
						}
					}
				});
			}
			//
			if(childCateg.equals("saleskiosk")){
				//
				LayoutInflater inflater = (LayoutInflater)coreAdapter.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.rowchild_salesfilter_layout, null);
				
				final TextView tvFrom = (TextView)convertView.findViewById(R.id.tvSalesFilDateFrom);
				final TextView tvTo = (TextView)convertView.findViewById(R.id.tvSalesFilDateTo);
				
				// set initial value
				tvFrom.setText(Utils.me.GetDateNow());
				tvTo.setText(Utils.me.GetDateNow());
				
				Button btnFrom = (Button)convertView.findViewById(R.id.btnSalesFilFrom);
				btnFrom.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						CallDatePicker(tvFrom);
					}
				});
				
				Button btnTo = (Button)convertView.findViewById(R.id.btnSalesFilTo);
				btnTo.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						CallDatePicker(tvTo);
					}
				});
				
				Button btn = (Button)convertView.findViewById(R.id.btnSalesFilter);
				btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						String from = tvFrom.getText().toString();
						String to = tvTo.getText().toString();
						String kiosk = Vars.KioskID(core, "", type.GET);
						
						strKioskFrom = from;
						strKioskTo = to;
						
						if(!from.equals("") && !to.equals("")){
							if(DateValidation(from, to)){
								callSalesKioskServer(from, to, kiosk);
							} else {
								Utils.me.MessageBox(core, "Invalid Date Range");
							}
						} else{
							Utils.me.MessageBox(core, "Date Range is required");
						}
					}
				});
			}
			////
			if(childCateg.equals("foottraffic")){
				
				//
				LayoutInflater inflater = (LayoutInflater)coreAdapter.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.rowchild_salesfilter_layout, null);
				
				final TextView tvFrom = (TextView)convertView.findViewById(R.id.tvSalesFilDateFrom);
				final TextView tvTo = (TextView)convertView.findViewById(R.id.tvSalesFilDateTo);
				
				// set initial value
				tvFrom.setText(Utils.me.GetDateNow());
				tvTo.setText(Utils.me.GetDateNow());
				
				Button btnFrom = (Button)convertView.findViewById(R.id.btnSalesFilFrom);
				btnFrom.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						CallDatePicker(tvFrom);
					}
				});
				
				Button btnTo = (Button)convertView.findViewById(R.id.btnSalesFilTo);
				btnTo.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						CallDatePicker(tvTo);
					}
				});
				
				Button btn = (Button)convertView.findViewById(R.id.btnSalesFilter);
				btn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						String from = tvFrom.getText().toString();
						String to = tvTo.getText().toString();
						String kiosk = Vars.KioskID(core, "", type.GET);
						
						strKioskFrom = from;
						strKioskTo = to;
						
						if(!from.equals("") && !to.equals("")){
							if(DateValidation(from, to)){
								//callSalesKioskServer(from, to, kiosk);
								callFootTrafficServer(from, to, kiosk);
							} else {
								Utils.me.MessageBox(core, "Invalid Date Range");
							}
						} else{
							Utils.me.MessageBox(core, "Date Range is required");
						}
					}
				});
				
//				LayoutInflater inflater = (LayoutInflater)coreAdapter.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//				convertView = inflater.inflate(R.layout.rowchild_foottraffic_report, null);
//				
//				Button btnFTViewer = (Button)convertView.findViewById(R.id.btnFTPDFViewer);
//				
//				btnFTViewer.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						callFootTrafficServer();
//					}
//				});
				///
				
				
			}
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
	}
	
}
