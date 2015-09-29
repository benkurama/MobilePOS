package com.example.redfootpos;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.redfootpos.Vars.type;
import com.example.redfootpos.model.Catalyst;
import com.example.redfootpos.model.Employee;
import com.example.redfootpos.object.PDFMaker;
import com.example.redfootpos.object.PDFMaker.fonttype;
import com.example.redfootpos.utils.JsonParser;
import com.example.redfootpos.utils.Utils;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class LoginAct extends FragmentActivity{
	
	private Button btnLoginLog;
	private EditText etLoginUsername, etLoginPassword;
	//private Users user;
	private Employee emp;
	
	private int intRes = 0;
	
	private Handler handler = new Handler();
	

	@Override
	protected void onCreate(Bundle arg0) {
		
		super.onCreate(arg0);
		// Active Error Log - >
		
		Utils.me.ActiveErrorLog(this);
		// - <
		setContentView(R.layout.act_login_page);
		//
		btnLoginLog = (Button)findViewById(R.id.btnLoginLog);
		etLoginUsername = (EditText)findViewById(R.id.etLoginUserid);
		etLoginPassword = (EditText)findViewById(R.id.etLoginPassword);
		//
		btnLoginLog.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// call methods for validating user account
				callServerValidation(etLoginUsername.getText().toString().trim());
				//PDFWriter();
				
			}
		});
		
		// get username and set to textbox
		String username = "";
		if(!TextUtils.isEmpty(Vars.UserName(this, "", type.GET))){
			username = Vars.UserName(this, "", type.GET);
			etLoginUsername.setText(username);
		}
		// temporary set
		//etLoginPassword.setText("redfoot");
		
		
		// FOR OFFLINE PREVIEW ONLY 'TEMPORARILY'
		if(!username.equals("offline")){
			CallServerLoggedStatus("f");
		}
		
		// testing only
		//handler.postDelayed(runnable, 1000);
	}
	//
	@SuppressWarnings("static-access")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == event.KEYCODE_BACK){
			// disable keyback button for not returning main page
			return false;
		}
		return true;
	}

	private void GoToMain(){
		//
		Intent intent = new Intent(this, MainAct.class);
		startActivity(intent);
		this.finish();
	}
	
	@SuppressLint("HandlerLeak")
	private void callServerValidation(final String username){
		//
		final ProgressDialog dialog = ProgressDialog.show(this, "Please wait...", "Validate User Account");
		final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				dialog.dismiss();
				//
				setResult();
				//
			}
		};
		Thread thread = new Thread(){
			@Override
			public void run() {
				super.run();
				//
				emp = JsonParser.ValidateUsername(username);
				//
				handler.sendEmptyMessage(0);
			};
		};
		thread.start();
	}
	
	@SuppressLint("HandlerLeak")
	private void CallServerLoggedStatus(final String status){
		//
		final ProgressDialog dialog = ProgressDialog.show(this, "Please Wait...", "posting logged status");
		final Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				dialog.dismiss();
				
				if(status.equals("t")){
					setSecondResult();
				}
				
			}
		};
		Thread process = new Thread(){
			@Override
			public void run() {
				super.run();
				intRes = JsonParser.PostLoggedStatus(LoginAct.this, status);
				handler.sendEmptyMessage(0);
			}
		};
		process.start();
	}
	
	private void setResult(){
		
		if(emp != null){
			String password = etLoginPassword.getText().toString();
			if(emp.password.equals(password)){
				setGlobalVars(emp);
				//GoToMain();
				
				// FOR OFFLINE PREVIEW ONLY 'TEMPORARILY'
				if(!emp.username.equals("offline")){
					CallServerLoggedStatus("t");
				} else{
					GoToMain();
				}
				
			} else {
				Toast.makeText(this, "Password is Invalid", Toast.LENGTH_LONG).show();
			}
		} else {
			Toast.makeText(this, "Username invalid or slow connection", Toast.LENGTH_LONG).show();
		}
	}
	
	private void setGlobalVars(Employee employee){
		// functionality for detecting login if new user is log, Vars refreshitems is enabled
		String empid = Vars.EmpID(this, "", type.GET);
		
		if(!TextUtils.isEmpty(empid)){
			if(!empid.equals(employee.empid)){
				Vars.RefreshItems(this, true, type.SET);
			}
		} else {
			Vars.RefreshItems(this, true, type.SET);
		}
		//
		
		//
		Vars.EmpID(this, employee.empid, type.SET);
		Vars.UserName(this, employee.username, type.SET);
		Vars.Password(this, employee.password, type.SET);
		Vars.FirstName(this, employee.firstname, type.SET);
		Vars.LastName(this, employee.lastname, type.SET);
		Vars.KioskID(this, employee.kioskid, type.SET);
		Vars.Department(this, employee.department, type.SET);
		Vars.UserLevel(this, employee.userlevel, type.SET);
		Vars.MobileNo(this, employee.mobileno, type.SET);
		Vars.Email(this, employee.email, type.SET);
		Vars.Staff(this, employee.stafflist_, type.SET);
		//
	}

	private void setSecondResult(){
		
		switch(intRes){
		case 1000:
			Utils.me.MessageBox(this, "System not allow concurrent user sessions.");
			break;
		case 2000:
			Utils.me.MessageBox(this, "First time user is welcome.");
			break;
		case 0:
			Utils.me.MessageBox(this, "Server Error, Please Try Again");
			break;
		default:
			GoToMain();
			break;
		}
	}
	// reserved codes
	private void PDFWriter(){
		//
		
		PDFMaker pdf = new PDFMaker("Reciept");
		pdf.AddMetaData("POS Receipt", "Your Official Receipt", "Mobile POS");
		
		pdf.InitParagraph();
		pdf.AddContent("Title of the document",fonttype.catFont);
		pdf.newLine(2);
		pdf.AddContent("super robot wars",fonttype.smallBold);
		pdf.newLine(2);
		pdf.AddContent("Warning... Warning", fonttype.redFont);
		pdf.post();
		pdf.newPage();
		// create category & subcategory format
		Anchor anchor = new Anchor("First Chapter", PDFMaker.catFont);
		anchor.setName("First Chapter");
		Chapter chapPart = new Chapter(new Paragraph(anchor),1);
		
		Paragraph subPara = new Paragraph("Subcategory 1", PDFMaker.subFont);
		Section subCatPart = chapPart.addSection(subPara);
		subCatPart.add(new Paragraph("Hello"));
		//
		subPara = new Paragraph("Subcategory 2", PDFMaker.subFont);
		subCatPart = chapPart.addSection(subPara);
		subCatPart.add(new Paragraph("Paragraph 1"));
		subCatPart.add(new Paragraph("Paragraph 2"));
		subCatPart.add(new Paragraph("Paragraph 3"));
		// create list format
		List list = new List(true, false, 10);
		list.add(new ListItem("First Point"));
		list.add(new ListItem("Second Point"));
		list.add(new ListItem("Third Point"));
		subCatPart.add(list);
		// Create table format
		
		PdfPTable table = new PdfPTable(3);
		
		PdfPCell c1 = new PdfPCell(new Phrase("Table Header 1"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		
		c1 = new PdfPCell(new Phrase("Table Header 2"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		
		c1 = new PdfPCell(new Phrase("Table Header 3"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		
		table.setHeaderRows(1);
		table.addCell("1.0");
	    table.addCell("1.1");
	    table.addCell("1.2");
	    table.addCell("2.1");
	    table.addCell("2.2");
	    table.addCell("2.3");
	    
	    subCatPart.add(table);
		//
		pdf.post(chapPart);
		
		//
		pdf.close();
		
		Utils.me.MessageBox(this, "PDF Success");
		
		
		PDFMaker.OpenPDF(this, "Reciept.pdf");
	}
	
	private Runnable runnable = new Runnable() {
		
		@Override
		public void run() {
			// 
			//Utils.me.MessageBox(getApplicationContext(), "Welcome");
			String name = etLoginUsername.getText().toString();
			name += "sayo ";
			etLoginUsername.setText(name);
			//
			handler.postDelayed(runnable, 9000);
		}
	};
	
}
