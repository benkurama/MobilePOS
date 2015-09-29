package com.example.redfootpos.utils;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.redfootpos.Vars;
import com.example.redfootpos.Vars.type;
import com.example.redfootpos.database.CloudfoneDB;
import com.example.redfootpos.fragments.ShoppingCartFragment;
import com.example.redfootpos.model.Item;
import com.example.redfootpos.model.SalesDetails;
import com.example.redfootpos.model.SalesInfo;
import com.example.redfootpos.object.NumberWithCommaTextWatcher;
import com.example.redfootpos.object.PDFMaker;
import com.example.redfootpos.object.SendMailService;
import com.example.redfootpos.pager.BuyPager;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

public enum DialogUtils {
me;
	public void PaymentReceived(final Context core, String title, String message, final String totalPrice, final BuyPager pager, final ShoppingCartFragment cart){
		//
		AlertDialog.Builder dialog = new AlertDialog.Builder(core);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setCancelable(false);
		//
		final EditText input = new EditText(core);
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		
		input.setTextScaleX(2);
		input.addTextChangedListener(new NumberWithCommaTextWatcher(input));
		
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT
				);
		input.setLayoutParams(lp);
		dialog.setView(input);
		
		// dialog.setIcon(R.drawable.money);
		dialog.setPositiveButton("Generate Change", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//
				
				int amtReceived = Utils.me.CurrencyStringToInt(input.getText().toString());
				int amtTotal = Utils.me.CurrencyStringToInt(totalPrice);
				//
				int change = amtReceived - amtTotal;
				String currChange = Utils.me.FormatCurrencyToString(change,"");
				//
				if(change >= 0){
					GenerateChange(core, currChange, pager, cart);
				} else {
					ErrorMessage(core, "Insufficient Amount Received "+currChange);
				}
				
			}
		});
		dialog.setNegativeButton("Cancel", null);
		//
		dialog.show();
	}
	
	public void GenerateChange(final Context core, final String change, final BuyPager pager, final ShoppingCartFragment cart){
		//
		AlertDialog.Builder changeDial = new AlertDialog.Builder(core);
		changeDial.setTitle("Generate Change");
		changeDial.setMessage("Customer change is PHP " + change);
		changeDial.setCancelable(false);
		
		changeDial.setPositiveButton("Check Out Now", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(pager != null){
					pager.CheckoutNow(change);
				} else {
					cart.CheckoutNow(change);
				}
				
			}
		});
		changeDial.setNegativeButton("Cancel", null);
		changeDial.show();
	}
	
	public void ErrorMessage(Context core, String msg){
		//
		AlertDialog.Builder errorDial = new AlertDialog.Builder(core);
		errorDial.setTitle("Error Detected");
		errorDial.setMessage(msg);
		errorDial.setCancelable(false);
		
		errorDial.setNegativeButton("OK", null);
		
		errorDial.show();
	}
	
	public void GenerateReciept(String title, String message, final Context core, final SalesInfo si, final ArrayList<SalesDetails> sdList, String change){
		//
		final String Filename = "Official_Reciept";
		CreatePDF(si, sdList, core, Filename, change);
		//
		AlertDialog.Builder dialog = new AlertDialog.Builder(core);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.setCancelable(false);
		
		dialog.setNeutralButton("View Receipt", null);
		dialog.setPositiveButton("Send Receipt ", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				SendReceipt(si, sdList, core);
			}
		});
		
		final AlertDialog dial = dialog.create();
		dial.show();
		dial.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				PDFMaker.OpenPDF(core, Filename);
			}

		});
		dial.show();
	}
	
	private void CreatePDF(SalesInfo si, ArrayList<SalesDetails> sdList, Context core, String filename, String change) {
		
		int amtChange = Utils.me.CurrencyStringToInt(change);
		int total = Utils.me.CurrencyStringToInt(si.total);
		
		int CustReceived = total + amtChange;
		
		String CustAmount = Utils.me.FormatCurrencyToString(CustReceived, "");
		//
		PDFMaker pdf = new PDFMaker(filename);
		pdf.AddMetaData("Official Receipt", "Customer Receipt", "Mobile POS");
		
		Paragraph prag = new Paragraph();
		
		prag.add(new Paragraph("CUSTOMER OFFICIAL RECEIPT",PDFMaker.catFont));
		prag.add(new Paragraph("Agent by: "+Vars.UserName(core, "", type.GET),PDFMaker.subFont));
		
		pdf.addEmptyLine(prag, 2);
		prag.add(new Paragraph("Sales Information", PDFMaker.blueFont));
		pdf.addEmptyLine(prag, 1);
		 
		prag.add(new Paragraph("Invoice Number: "+si.invno, PDFMaker.smallBold));
		prag.add(new Paragraph("Customer Name: "+si.name, PDFMaker.smallBold));
		prag.add(new Paragraph("Address: "+si.address, PDFMaker.smallBold));
		prag.add(new Paragraph("Email Address: "+si.emailadd, PDFMaker.smallBold));
		prag.add(new Paragraph("Contact: "+si.contact, PDFMaker.smallBold));
		prag.add(new Paragraph("Customer Number: "+si.custno, PDFMaker.smallBold));
		pdf.addEmptyLine(prag, 1);
		prag.add(new Paragraph("Total Amount: "+si.total, PDFMaker.smallBold));
		prag.add(new Paragraph("Customer Amount Received: "+CustAmount, PDFMaker.smallBold));
		prag.add(new Paragraph("Customer Change: "+change, PDFMaker.smallBold));
		//
		pdf.addEmptyLine(prag, 2);
		prag.add(new Paragraph("Sales Details", PDFMaker.blueFont));
		pdf.addEmptyLine(prag, 1);
		//
		int Column = 4;
		PdfPTable table = new PdfPTable(Column);
		table.setTotalWidth(520);
		table.setLockedWidth(true);
		
		PdfPCell cell = new PdfPCell();
		
		cell = new PdfPCell(new Phrase("Invoice No.", PDFMaker.smallBold));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setBorder(Rectangle.BOTTOM);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Product Name", PDFMaker.smallBold));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setBorder(Rectangle.BOTTOM);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Total Quantity", PDFMaker.smallBold));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setBorder(Rectangle.BOTTOM);
		table.addCell(cell);
		
		cell = new PdfPCell(new Phrase("Total Unit Price", PDFMaker.smallBold));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setBorder(Rectangle.BOTTOM);
		table.addCell(cell);
		
		table.setHeaderRows(1);
		
		boolean isWhite = true;
		
		for(SalesDetails sd : sdList){
			//
			cell = new PdfPCell(new Phrase(sd.invno));
			cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			formatCell(cell, isWhite);
			table.addCell(cell);
			//
			int id = Integer.parseInt(sd.itemid);
			Item item = new CloudfoneDB(core).openToRead().GetItemsByID(id);
			//
			cell = new PdfPCell(new Phrase(item.name));
			cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
			formatCell(cell, isWhite);
			table.addCell(cell);
			
			cell = new PdfPCell(new Phrase(sd.qty));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			formatCell(cell, isWhite);
			table.addCell(cell);
			//
			int quant = Utils.me.CurrencyStringToInt(sd.qty);
			int price = Utils.me.CurrencyStringToInt(sd.unitprice);
			
			int totalprice = quant * price;
			String TotPrice = Utils.me.FormatCurrencyToString(totalprice, "");
			//
			cell = new PdfPCell(new Phrase(TotPrice));
			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			formatCell(cell, isWhite);
			table.addCell(cell);
			
			isWhite = !isWhite;
		}
		prag.add(table);
		//
		pdf.post(prag);
		//
		pdf.close();
	
	}
	
	private void formatCell(PdfPCell cell, boolean isWhite){
		cell.setBorder(Rectangle.NO_BORDER);
		if(isWhite){
			cell.setBackgroundColor(new BaseColor(255, 255, 255));
		} else {
			cell.setBackgroundColor(new BaseColor(250, 250, 250));
		}
		cell.setPadding(5);
	}
	
	private void SendReceipt(SalesInfo si, ArrayList<SalesDetails> sdlist, Context core){
		
		// Send a text Composition
		String contactno = si.contact;
		String agent = Vars.UserName(core, "", type.GET);
		String invnno = si.invno;
		String totalam = si.total;
		
		String msgText = "MobilePOS Payment Receipt from Agent "+agent+" w/ Invoice # "+invnno+", total amount of "+totalam;
		
		try {
			SmsManager.getDefault().sendTextMessage(contactno, null, msgText, null, null);
		} catch (Exception e) {
			throw null;
		}
		
		// Send to an email Composition
		String emailadd = si.emailadd;
		
		String filepath = PDFMaker.GetFilePath();
		
		Intent intent = new Intent(core, SendMailService.class);
		intent.putExtra(SendMailService.EMAILADD, emailadd);
		intent.putExtra(SendMailService.USER, agent);
		intent.putExtra(SendMailService.INVNO, invnno);
		intent.putExtra(SendMailService.TOTALAM, totalam);
		intent.putExtra(SendMailService.ATTACHMENT_PATH, filepath);
		
		core.startService(intent);
	}
	
}
