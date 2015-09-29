package com.example.redfootpos.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;

import com.example.redfootpos.Vars;
import com.example.redfootpos.Vars.type;
import com.example.redfootpos.model.Employee;
import com.example.redfootpos.model.EmployeeLog;
import com.example.redfootpos.model.FootTraffic;
import com.example.redfootpos.model.Item;
import com.example.redfootpos.model.Remittance;
import com.example.redfootpos.model.ReturnDetails;
import com.example.redfootpos.model.ReturnInfo;
import com.example.redfootpos.model.SalesDetails;
import com.example.redfootpos.model.SalesInfo;
import com.example.redfootpos.model.Stocks;
import com.example.redfootpos.model.Top10Product;
import com.itextpdf.text.pdf.StringUtils;

public class JsonParser {
	
	public static ArrayList<Item> GetAllData(String type, String kioskid){
		
		String VALID_KEY = "Valid";
		String PRODUCT_KEY = "collection";
		
		ArrayList<Item> itemList = new ArrayList<Item>();
		
		
		String URL = Configs.URLSERVER;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>(); //?
		params.add(new BasicNameValuePair("type", type));
		params.add(new BasicNameValuePair("kioskid", kioskid));
		
		JsonParsing jsonParser = new JsonParsing();
        JSONObject json = jsonParser.getJSONFromUrl(URL, "POST", params);
        
        try {
        	
        	if(json != null){
        		Object res = json.get(VALID_KEY);
        		
        		if(res.toString().equals("1")){
        			
        			JSONArray rows = json.getJSONArray(PRODUCT_KEY);
        			
        			for (int x=0; x < rows.length(); x++){
        				
        				Item item = new Item();
        				JSONObject data = rows.getJSONObject(x);
        				
        				item.ID = data.getInt("ID");
        				item.name = data.getString("Name");
        				item.price = data.getString("Price");
        				item.type = data.getString("Category");
        				item.url = data.getString("ImgUrl");
        				item.width = data.getInt("ImgWidth");
        				item.height = data.getInt("ImgHeight");
        				item.Description = data.getString("Specs");
        				
        				item.Unitcode = data.getString("UnitCode");
        				item.Prodcode = data.getString("ProdCode");
        				item.Imei = data.getString("IMEI");
        				item.Colorcode = data.getString("ColorCode");
        				item.Stockin = data.getInt("_StockIn");
        				item._discounted = data.getString("_Discount");
        				
        				itemList.add(item);
        			}
        			
        		} 
        	} 
        	
        }catch(Exception e){
        	e.printStackTrace();
        }
        
        return itemList;
	}
	
	public static boolean PostPaymentToServer(SalesInfo si, ArrayList<SalesDetails> sdList, Context core){
		////
		String VALID_KEY = "Valid";
		boolean isSIInserted = false;
		boolean isSDInserted = false;
		
		String kioskid = Vars.KioskID(core, "", type.GET);
		String status = si.status;
		//////////
		String URL = Configs.URLSERVER;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>(); //?
		JsonParsing jsonParser = new JsonParsing();
		params.add(new BasicNameValuePair("type", "sales"));
		params.add(new BasicNameValuePair("method", "InsertSalesInfo"));
		params.add(new BasicNameValuePair("invno", si.invno));
		params.add(new BasicNameValuePair("custname", si.name));
		params.add(new BasicNameValuePair("address", si.address));
		params.add(new BasicNameValuePair("emailadd", si.emailadd));
		params.add(new BasicNameValuePair("contactno", si.contact));
		params.add(new BasicNameValuePair("custno", si.custno));
		params.add(new BasicNameValuePair("total", si.total));
		params.add(new BasicNameValuePair("empid", si.empid));
		params.add(new BasicNameValuePair("status", status));
		params.add(new BasicNameValuePair("remarks", si.remarks));
		params.add(new BasicNameValuePair("kioskid", kioskid));
		params.add(new BasicNameValuePair("takenby", si._takenby));
		
		//
        JSONObject json = jsonParser.getJSONFromUrl(URL, "POST", params);
        
        try{
        	if(json != null){
        		Object res = json.get(VALID_KEY);
        		
        		if(res.toString().equals("1")){
        			isSIInserted = true;
        		}
        	}
        }catch(Exception e){
        	e.printStackTrace();
        }
        //////////
        
        if (isSIInserted) {
			try {
				//
				for (SalesDetails sd : sdList) {
					String URL2 = Configs.URLSERVER;

					List<NameValuePair> params2 = new ArrayList<NameValuePair>(); //?
					JsonParsing jsonParser2 = new JsonParsing();
					params2.add(new BasicNameValuePair("type", "sales"));
					params2.add(new BasicNameValuePair("method","InsertSalesDetails"));
					params2.add(new BasicNameValuePair("invno", sd.invno));
					params2.add(new BasicNameValuePair("prodid", sd.itemid));
					params2.add(new BasicNameValuePair("qty", sd.qty));
					params2.add(new BasicNameValuePair("unitpx", sd.unitprice));
					params2.add(new BasicNameValuePair("prodname", sd.prodname));
					params2.add(new BasicNameValuePair("imei", sd.imei_));
					params2.add(new BasicNameValuePair("discount", sd.discount));
					
					params2.add(new BasicNameValuePair("invstatus", status));

					JSONObject json2 = jsonParser2.getJSONFromUrl(URL2, "POST",
							params2);

					if (json2 != null) {
						Object res = json2.get(VALID_KEY);

						if (res.toString().equals("1")) {
							isSDInserted = true;
						} else {
							isSDInserted = false;
							break;
						}
					}
				}
				///

				///
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		//////////
        return isSIInserted && isSDInserted;
	}
	
	@SuppressWarnings("null")
	public static Employee ValidateUsername(String username){
		//
		String VALID_KEY = "Valid";
		String USER_KEY = "collection";
		
		Employee emp = null;
		
//		try {
//			username = URLEncoder.encode(username, "utf-8");
//		} catch (UnsupportedEncodingException e1) {
//			
//			e1.printStackTrace();
//		}
		//
		
		// FOR OFFLINE PREVIEW ONLY 'TEMPORARILY' 
		if(username.equals("offline")){
			
			emp = new Employee();
			emp.empid = "EMP1000";
			emp.username = username;
			emp.password = "pass123";
			emp.firstname = "alvin";
			emp.lastname = "sison";
			emp.kioskid = "K1000";
			emp.department = "Developer";
			emp.userlevel = "creator";
			emp.mobileno = "09198532613";
			emp.email = "benkurama@gmail.com";
			
			emp.stafflist_ = "emp001:ben adams";
			
			return emp;
		}
		
		String URL = Configs.URLSERVER;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("type","user"));
		params.add(new BasicNameValuePair("username",username));
		
		
		JsonParsing jsonParser = new JsonParsing();
        JSONObject json = jsonParser.getJSONFromUrl(URL, "POST", params);
        
        try {
			if(json != null){
				//
				Object valid = json.get(VALID_KEY);
				if(valid.toString().equals("1")){
					//
					JSONArray rows = json.getJSONArray(USER_KEY);
					JSONObject data =  rows.getJSONObject(0);
					
					emp = new Employee();
					emp.empid = data.getString("EmpID");
					emp.username = data.getString("Username");
					emp.password = data.getString("Password");
					emp.firstname = data.getString("Firstname");
					emp.lastname = data.getString("Lastname");
					emp.kioskid = data.getString("KioskID");
					emp.department = data.getString("Department");
					emp.userlevel = data.getString("UserLevel");
					emp.mobileno = data.getString("MobileNo");
					emp.email = data.getString("Email");
					
					emp.stafflist_ = data.getString("StaffList_");
					//
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return emp;
	}
	
	public static void LogoutTrack(String empid, String empname){
//		try {
//			empid = URLEncoder.encode(empid,"utf-8");
//			empname = URLEncoder.encode(empname,"utf-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		//
//		String URL = "http://192.168.1.20/MobileDataFeed/Default.aspx?type=userlog&logtype=O&empid="+empid+"&empname="+empname;
		String URL = Configs.URLSERVER;
		//
		List<NameValuePair> params = new ArrayList<NameValuePair>(); //?
		params.add(new BasicNameValuePair("type", "userlog"));
		params.add(new BasicNameValuePair("logtype", "O"));
		params.add(new BasicNameValuePair("empid", empid));
		params.add(new BasicNameValuePair("empname", empname));
		
		JsonParsing jsonParser = new JsonParsing();
        JSONObject json = jsonParser.getJSONFromUrl(URL, "POST", params);
	}
	//
	public static ArrayList<EmployeeLog> GetLogtimeByUser(String empid){
		//
		String VALID_KEY = "Valid";
		String LOGTRACK_KEY = "collection";
		ArrayList<EmployeeLog> collection = new ArrayList<EmployeeLog>();
		
		String URL = Configs.URLSERVER;
	//
		List<NameValuePair> params = new ArrayList<NameValuePair>(); //?
		params.add(new BasicNameValuePair("type", "getuserlog"));
		params.add(new BasicNameValuePair("empid", empid));
		
		JsonParsing jsonParser = new JsonParsing();
        JSONObject json = jsonParser.getJSONFromUrl(URL, "POST", params);
        
        try{
        	if(json != null){
        		Object valid = json.get(VALID_KEY);
        		if(valid.toString().equals("1")){
        			JSONArray rows = json.getJSONArray(LOGTRACK_KEY);
        			
        			for(int x = 0;x<rows.length(); x++){
        				JSONObject data = rows.getJSONObject(x);
        				EmployeeLog instance = new EmployeeLog();
        				
        				instance.empid = data.getString("EmpID");
        				instance.empname = data.getString("EmpName");
        				instance.logtime = data.getString("LogTime");
        				instance.logtype = data.getString("LogType");
        				
        				collection.add(instance);
        			}
        		}
        	}
        }catch(Exception e){
        	
        }
        
        return collection;
	}
	//
	public static ArrayList<SalesInfo> GetRemarkedSales(Context core){
		//
		String VALID_KEY = "Valid";
		String REMARKED_ITEM = "collection";
		ArrayList<SalesInfo> collection = new ArrayList<SalesInfo>();
		
		String empid = Vars.EmpID(core, "", type.GET);
		String kioskid = Vars.KioskID(core, "", type.GET);
		
		String URL = Configs.URLSERVER;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>(); //?
		params.add(new BasicNameValuePair("type", "sales"));
		params.add(new BasicNameValuePair("method", "GetRemarkedSalesInfo"));
		params.add(new BasicNameValuePair("empid", empid));
		params.add(new BasicNameValuePair("kioskid", kioskid));
		//
		JsonParsing jsonParser = new JsonParsing();
        JSONObject json = jsonParser.getJSONFromUrl(URL, "POST", params);
        
        try {
			if(json != null){
				Object valid = json.get(VALID_KEY);
				if(valid.toString().equals("1")){
					//
					JSONArray rows = json.getJSONArray(REMARKED_ITEM);
					
					for(int x = 0;x < rows.length(); x++){
						JSONObject data = rows.getJSONObject(x);
						SalesInfo instance = new SalesInfo();
						//
						instance.invno = data.getString("InvNo");
						instance.name = data.getString("CustName");
						instance.address = data.getString("Address");
						instance.emailadd = data.getString("EmailAdd");
						instance.contact = data.getString("ContactNo");
						instance.custno = data.getString( "CustNo");
						instance.total = data.getString("TotalAmount");
						instance.empid = data.getString("EmpID");
						instance.status = data.getString("Status");
						instance.remarks = data.getString("Remarks");
						//
						collection.add(instance);
					}
				}
			}
		} catch (Exception e) {
			
		}
        
        return collection;
	}
	
	public static ArrayList<SalesDetails> GetSalesDetails(String invno){
		
		String VALID_KEY = "Valid";
		String SALESDETAILS = "collection";
		ArrayList<SalesDetails> listSD = new ArrayList<SalesDetails>();
		//
		String URL = Configs.URLSERVER;
		//
		List<NameValuePair> params = new ArrayList<NameValuePair>(); //?
		params.add(new BasicNameValuePair("type", "sales"));
		params.add(new BasicNameValuePair("method", "GetSalesDetailsByInvoice"));
		params.add(new BasicNameValuePair("invno", invno));
		//
		JsonParsing jsonParser = new JsonParsing();
        JSONObject json = jsonParser.getJSONFromUrl(URL, "POST", params);
        //
        try{
        	if(json != null){
        		Object valid = json.get(VALID_KEY);
        		if(valid.toString().equals("1")){
        			// 
        			JSONArray row = json.getJSONArray(SALESDETAILS);
        			
        			for(int x = 0; x < row.length(); x++){
        				//
        				JSONObject data = row.getJSONObject(x);
        				SalesDetails sd = new SalesDetails();
        				
        				sd.invno = data.getString("InvNo");
        				sd.itemid = data.getString("ProdID");
        				sd.qty = data.getString("Qty");
        				sd.unitprice = data.getString("UnitPx");
        				sd.prodname = data.getString("ProdName_");
        				sd.discount = data.getString("Discount");
        				
        				listSD.add(sd);
        			}
        		}
        	}
        }catch(Exception e){
        	
        }
        
        return listSD;
	}
	
	public static boolean UpdateSalesInfo(SalesInfo si, ArrayList<SalesDetails> sdList){
		//
		String VALID_KEY = "Valid";
		boolean isSIInserted = false;
		boolean isSDInserted = false;
		String status = si.status;
		//////////
		String URL = Configs.URLSERVER;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>(); //?
		JsonParsing jsonParser = new JsonParsing();
		params.add(new BasicNameValuePair("type", "sales"));
		params.add(new BasicNameValuePair("method", "UpdateSalesInfo"));
		params.add(new BasicNameValuePair("invno", si.invno));
		params.add(new BasicNameValuePair("custname", si.name));
		params.add(new BasicNameValuePair("address", si.address));
		params.add(new BasicNameValuePair("emailadd", si.emailadd));
		params.add(new BasicNameValuePair("contact", si.contact));
		params.add(new BasicNameValuePair("custno", si.custno));
		params.add(new BasicNameValuePair("total", si.total));
		params.add(new BasicNameValuePair("status", si.status));
		
		params.add(new BasicNameValuePair("takenby", si._takenby));
		
		JSONObject json = jsonParser.getJSONFromUrl(URL, "POST", params);
		
		try{
			if(json!=null){
				Object valid = json.get(VALID_KEY);
				if (valid.toString().equals("1")) {
					isSIInserted = true;
				}
			}
		}catch(Exception e){
			
		}
		////
		if (isSIInserted) {
			////
			try {
				//
				for (SalesDetails sd : sdList) {
					String URL2 = Configs.URLSERVER;
					List<NameValuePair> params2 = new ArrayList<NameValuePair>(); //?
					JsonParsing jsonParser2 = new JsonParsing();
					params2.add(new BasicNameValuePair("type", "sales"));
					params2.add(new BasicNameValuePair("method", "InsertSalesDetails"));
					params2.add(new BasicNameValuePair("invno", sd.invno));
					params2.add(new BasicNameValuePair("prodid", sd.itemid));
					params2.add(new BasicNameValuePair("qty", sd.qty));
					params2.add(new BasicNameValuePair("unitpx", sd.unitprice));
					params2.add(new BasicNameValuePair("prodname", sd.prodname));
					params2.add(new BasicNameValuePair("imei", sd.imei_));
					params2.add(new BasicNameValuePair("invstatus", status));

					JSONObject json2 = jsonParser2.getJSONFromUrl(URL2, "POST",
							params2);

					if (json2 != null) {
						Object res = json2.get(VALID_KEY);

						if (res.toString().equals("1")) {
							isSDInserted = true;
						}
					}
				}
				///
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		//
		return isSIInserted && isSDInserted;
	}
	
	public static boolean CancelledSalesInfo(String invno){
		//
		String VALID_KEY = "Valid";
		boolean isCancelled = false;
		
		String URL = Configs.URLSERVER;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		JsonParsing jsonParsing = new JsonParsing();
		
		params.add(new BasicNameValuePair("type", "sales"));
		params.add(new BasicNameValuePair("method", "CancelledSalesInfo"));
		params.add(new BasicNameValuePair("invno", invno));
		
		JSONObject json = jsonParsing.getJSONFromUrl(URL, "POST", params);
		//
		try{
			if(json != null){
				Object valid = json.get(VALID_KEY);
				if(valid.toString().equals("1")){
					isCancelled = true;
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return isCancelled;
	}
	
	public static ArrayList<Top10Product> Top10Prod(String from, String to){
		//
		String VALID_KEY = "Valid";
		String TOP10 = "collection";
		ArrayList<Top10Product> topProd = new ArrayList<Top10Product>();
		
		String URL = Configs.URLSERVER;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		JsonParsing jsonParsing = new JsonParsing();
		
		params.add(new BasicNameValuePair("type", "saleshistory"));
		params.add(new BasicNameValuePair("method", "top10product"));
		
		params.add(new BasicNameValuePair("from", from));
		params.add(new BasicNameValuePair("to", to));
		
		JSONObject json = jsonParsing.getJSONFromUrl(URL, "POST", params);
		//
		try{
			if(json != null){
				Object valid = json.get(VALID_KEY);
				if(valid.toString().equals("1")){
					//
					JSONArray row = json.getJSONArray(TOP10);
					//
					for(int x = 0; x < row.length(); x++){
						JSONObject data = row.getJSONObject(x);
						Top10Product ins = new Top10Product();
						
						ins.ProdID = data.getInt("ProdID");
						ins.ProdName = data.getString("ProdName");
						ins.TotalQty = data.getInt("TotalQty");
						ins.TotalAmt = data.getString("TotalAmt");
						
						topProd.add(ins);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return topProd;
	}
	
	public static ArrayList<SalesInfo> GetSalesReport(String from, String to, String empid){
		//
		String VALID_KEY = "Valid";
		String SALESREP = "collection";
		ArrayList<SalesInfo> collection = new ArrayList<SalesInfo>();
		
		//String URL = "http://192.168.1.20/MobileDataFeed/Default.aspx";
		String URL = Configs.URLSERVER;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		JsonParsing jsonParsing = new JsonParsing();
		
		params.add(new BasicNameValuePair("type", "saleshistory"));
		params.add(new BasicNameValuePair("method", "salesreport"));
		params.add(new BasicNameValuePair("from", from));
		params.add(new BasicNameValuePair("to", to));
		params.add(new BasicNameValuePair("empid", empid));
		
		JSONObject json = jsonParsing.getJSONFromUrl(URL, "POST", params);
		
		try{
			if(json != null){
				Object valid = json.get(VALID_KEY);
				if(valid.toString().equals("1")){
					//
					JSONArray row = json.getJSONArray(SALESREP);
					//
					for(int x = 0; x < row.length(); x++){
						//
						JSONObject data = row.getJSONObject(x);
						SalesInfo ins = new SalesInfo();
						
						ins.invno = data.getString("InvNo");
						ins.name = data.getString("CustName");
						ins.address = data.getString("Address");
						ins.emailadd = data.getString("EmailAdd");
						ins.contact = data.getString("ContactNo");
						ins.custno = data.getString("CustNo");
						ins.total = data.getString("TotalAmount");
						ins.empid = data.getString("EmpID");
						ins.status = data.getString("Status");
						ins.remarks = data.getString("Remarks");
						
						collection.add(ins);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return collection;
	}

	public static ArrayList<SalesInfo> GetSalesReportByKiosk(String from, String to, String kioskid){
		//
		String VALID_KEY = "Valid";
		String SALESREP = "collection";
		ArrayList<SalesInfo> collection = new ArrayList<SalesInfo>();
		
		String URL = Configs.URLSERVER;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		JsonParsing jsonParsing = new JsonParsing();
		
		params.add(new BasicNameValuePair("type", "saleshistory"));
		params.add(new BasicNameValuePair("method", "saleskiosk"));
		params.add(new BasicNameValuePair("from", from));
		params.add(new BasicNameValuePair("to", to));
		params.add(new BasicNameValuePair("kioskid", kioskid));
		
		JSONObject json = jsonParsing.getJSONFromUrl(URL, "POST", params);
		
		try{
			if(json != null){
				Object valid = json.get(VALID_KEY);
				if(valid.toString().equals("1")){
					//
					JSONArray row = json.getJSONArray(SALESREP);
					//
					for(int x = 0; x < row.length(); x++){
						//
						JSONObject data = row.getJSONObject(x);
						SalesInfo ins = new SalesInfo();
						
						ins.invno = data.getString("InvNo");
						ins.name = data.getString("CustName");
						ins.address = data.getString("Address");
						ins.emailadd = data.getString("EmailAdd");
						ins.contact = data.getString("ContactNo");
						ins.custno = data.getString("CustNo");
						ins.total = data.getString("TotalAmount");
						ins.empid = data.getString("EmpID");
						ins.status = data.getString("Status");
						ins.remarks = data.getString("Remarks");
						
						ins._empname = data.getString("_EmpName");
						
						collection.add(ins);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return collection;
	}
	
	public static ArrayList<Stocks> GetStocksByReference(String directSupplier, String kioskid){
		
		String VALID_KEY = "Valid";
		String STOCKS = "collection";
		ArrayList<Stocks> collection = new ArrayList<Stocks>();
		
		String URL = Configs.URLSERVER;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		JsonParsing jsonParsing = new JsonParsing();
		
		params.add(new BasicNameValuePair("type", "stocks"));
		params.add(new BasicNameValuePair("method", "get"));
		params.add(new BasicNameValuePair("directsupplier", directSupplier));
		params.add(new BasicNameValuePair("kioskid", kioskid));
		
		JSONObject json = jsonParsing.getJSONFromUrl(URL, "POST", params);
		
		try{
			if(json != null){
				Object valid = json.get(VALID_KEY);
				if(valid.toString().equals("1")){
					//
					JSONArray row = json.getJSONArray(STOCKS);
					//
					for(int x = 0; x < row.length(); x++){
						//
						JSONObject data = row.getJSONObject(x);
						Stocks ins = new Stocks();
						//
						ins.ProdID = data.getString("ProdID");
						ins.ProdName = data.getString("ProdName");
						ins.Reference = data.getString("Reference");
						ins.Remarks = data.getString("Remarks");
						ins.StockIn = data.getString("StockIn");
						ins.StockList = data.getString("StockList");
						ins.DistSupp = data.getString("DistReciept");
						
						ins.Checkby = data.getString("CheckBy");
						ins.Checked = data.getString("Checked");
						//
						collection.add(ins);
					}
				}
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return collection;
	}
	
	public static String PostingStockList(ArrayList<Stocks> StockList, Context core){
		//
		String VALID_KEY = "Valid";
		String directsupplier = StockList.get(0).DistSupp;
		String kioskid = Vars.KioskID(core, "", type.GET);
		String checkby = Vars.EmpID(core, "", type.GET);
		
		String errorMes = "";
		
		String URL = Configs.URLSERVER;
		
		for(Stocks stock : StockList){
			//
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			JsonParsing jsonParsing = new JsonParsing();
			
			params.add(new BasicNameValuePair("type", "stocks"));
			params.add(new BasicNameValuePair("method", "set"));
			params.add(new BasicNameValuePair("directsupplier", directsupplier));
			params.add(new BasicNameValuePair("kioskid", kioskid));
			params.add(new BasicNameValuePair("stocklist", stock.StockList));
			params.add(new BasicNameValuePair("prodid", stock.ProdID));
			params.add(new BasicNameValuePair("checkby", checkby));
			
			JSONObject json = jsonParsing.getJSONFromUrl(URL, "POST", params);
			
			try {
				if (json != null) {
					//
					Object valid = json.get(VALID_KEY);
					if (valid.toString().equals("1")) {
						
					}
				}
			} catch (Exception e) {
				errorMes += stock.ProdName + "/Failed, ";
				continue;
			}
		}
		return errorMes == "" ? "Success": errorMes ;
	}
	
	public static Boolean PostFooterTraffic(FootTraffic ft, Context core){
		//
		String VALID_KEY = "Valid";
		String kioskid = Vars.KioskID(core, "", type.GET);
		String empcode = Vars.EmpID(core, "", type.GET);
		Boolean isSuccess = false;
		//
		String URL = Configs.URLSERVER;
		//
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		JsonParsing jsonParsing = new JsonParsing();
		
		params.add(new BasicNameValuePair("type", "foottraffic"));
		params.add(new BasicNameValuePair("method", "set"));
		params.add(new BasicNameValuePair("8am", ft.i8am));
		params.add(new BasicNameValuePair("9am", ft.i9am));
		params.add(new BasicNameValuePair("10am", ft.i10am));
		params.add(new BasicNameValuePair("11am", ft.i11am));
		params.add(new BasicNameValuePair("12pm", ft.i12pm));
		params.add(new BasicNameValuePair("1pm", ft.i1pm));
		params.add(new BasicNameValuePair("2pm", ft.i2pm));
		params.add(new BasicNameValuePair("3pm", ft.i3pm));
		params.add(new BasicNameValuePair("4pm", ft.i4pm));
		params.add(new BasicNameValuePair("5pm", ft.i5pm));
		params.add(new BasicNameValuePair("6pm", ft.i6pm));
		params.add(new BasicNameValuePair("7pm", ft.i7pm));
		params.add(new BasicNameValuePair("8pm", ft.i8pm));
		params.add(new BasicNameValuePair("9pm", ft.i9pm));
		params.add(new BasicNameValuePair("10pm", ft.i10pm));
		
		params.add(new BasicNameValuePair("kioskid", kioskid));
		params.add(new BasicNameValuePair("empcode", empcode));
		//
		JSONObject json = jsonParsing.getJSONFromUrl(URL, "POST", params);
		//
		try {
			if (json != null) {
				//
				Object valid = json.get(VALID_KEY);
				if (valid.toString().equals("1")) {
					isSuccess = true;
				}
			}
		} catch(Exception e){
			isSuccess = false;
		}
		
		return isSuccess;
	}
	
	public static ArrayList<FootTraffic> GetFooterTraffic(Context core){
		//
		String VALID_KEY = "Valid";
		String kioskid = Vars.KioskID(core, "", type.GET);
		String today = Utils.me.GetDateNow();
		
		String COUNTERS = "collection";
		ArrayList<FootTraffic> collection = new ArrayList<FootTraffic>();
		
		String URL = Configs.URLSERVER;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		JsonParsing jsonParsing = new JsonParsing();
		
		params.add(new BasicNameValuePair("type", "foottraffic"));
		params.add(new BasicNameValuePair("method", "get"));
		params.add(new BasicNameValuePair("kioskid", kioskid));
		params.add(new BasicNameValuePair("date", today));
		
		JSONObject json = jsonParsing.getJSONFromUrl(URL, "POST", params);
		//
		try{
			if(json != null){
				Object valid = json.get(VALID_KEY);
				if(valid.toString().equals("1")){
					//
					JSONArray row = json.getJSONArray(COUNTERS);
					//
					for(int x = 0; x < row.length(); x++){
						//
						JSONObject data = row.getJSONObject(x);
						FootTraffic ins = new FootTraffic();
						//
						ins.i8am = data.getString("i8am");
						ins.i9am = data.getString("i9am");
						ins.i10am = data.getString("i10am");
						ins.i11am = data.getString("i11am");
						ins.i12pm = data.getString("i12pm");
						
						ins.i1pm = data.getString("i1pm");
						ins.i2pm = data.getString("i2pm");
						ins.i3pm = data.getString("i3pm");
						ins.i4pm = data.getString("i4pm");
						ins.i5pm = data.getString("i5pm");
						ins.i6pm = data.getString("i6pm");
						ins.i7pm = data.getString("i7pm");
						ins.i8pm = data.getString("i8pm");
						ins.i9pm = data.getString("i9pm");
						ins.i10pm = data.getString("i10pm");
						
						//
						collection.add(ins);
					}
				}
				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return collection;
	}
	
	public static ArrayList<FootTraffic> GetFootTrafficReport(String from, String to, String kioskid){
		//
		String VALID_KEY = "Valid";
		String COUNTERS = "collection";
		ArrayList<FootTraffic> ftList = new ArrayList<FootTraffic>();
		
		String URL = Configs.URLSERVER;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		JsonParsing jsonParsing = new JsonParsing();
		
		params.add(new BasicNameValuePair("type", "foottraffic"));
		params.add(new BasicNameValuePair("method", "report"));
		params.add(new BasicNameValuePair("from", from));
		params.add(new BasicNameValuePair("to", to));
		params.add(new BasicNameValuePair("kioskid", kioskid));
		
		JSONObject json = jsonParsing.getJSONFromUrl(URL, "POST", params);
		
		try{
			if(json != null){
				Object valid = json.get(VALID_KEY);
				if(valid.toString().equals("1")){
					//
					JSONArray row = json.getJSONArray(COUNTERS);
					//
					for(int x = 0; x < row.length(); x++){
						//
						JSONObject data = row.getJSONObject(x);
						FootTraffic ins = new FootTraffic();
						
						ins.ClassName_ = data.getString("ClassName_");
						ins.KioskName_ = data.getString("KioskName_");
						ins.DaysIn_ = data.getString("DaysIn_");
						ins.TrafficCounter_ = data.getString("TrafficCounter_");
						
						ftList.add(ins);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return ftList;
	}
	
	public static SalesInfo GetReturnDetails(String invno){
		//
		String VALID_KEY = "Valid";
		String COUNTERS = "collection";
		SalesInfo si = new SalesInfo();
		
		
		
		String URL = Configs.URLSERVER;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		JsonParsing jsonParsing = new JsonParsing();
		
		params.add(new BasicNameValuePair("type", "return"));
		params.add(new BasicNameValuePair("method", "getinvdetails"));
		params.add(new BasicNameValuePair("invno", invno));
		
		JSONObject json = jsonParsing.getJSONFromUrl(URL, "POST", params);
		
		try{
			if(json != null){
				Object valid = json.get(VALID_KEY);
				if(valid.toString().equals("1")){
					//
					JSONArray row = json.getJSONArray(COUNTERS);
					//
					for(int x = 0; x < row.length(); x++){
						//
						JSONObject data = row.getJSONObject(x);
						SalesInfo ins = new SalesInfo();
						
						ins.invno = data.getString("InvNo");
						ins.name = data.getString("CustName");
						ins.custno = data.getString("CustNo");
						ins.address = data.getString("Address");
					
						si = ins;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//
		return si;
	}
	
	public static boolean PostReturnToServer(ReturnInfo ri, ArrayList<ReturnDetails> rdList){
		//
		String VALID_KEY = "Valid";
		String RETURNSTATUS = "collection";
		String ReturnID = "";
		
		boolean isRIInserted = true;
		boolean isRDInserted = true;
		
		String URL = Configs.URLSERVER;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		JsonParsing jsonParsing = new JsonParsing();
		
		params.add(new BasicNameValuePair("type", "return"));
		params.add(new BasicNameValuePair("method", "insertreturninfo"));
		params.add(new BasicNameValuePair("custno", ri.custno));
		params.add(new BasicNameValuePair("custname", ri.custname));
		params.add(new BasicNameValuePair("retdate", ri.retdate));
		params.add(new BasicNameValuePair("address", ri.address));
		params.add(new BasicNameValuePair("notes", ri.notes));
		params.add(new BasicNameValuePair("empid", ri.empid));
		params.add(new BasicNameValuePair("kioskid", ri.kioskid));
		
		JSONObject json = jsonParsing.getJSONFromUrl(URL, "POST", params);
		
		try{
        	if(json != null){
        		Object res = json.get(VALID_KEY);
        		
        		if(!res.toString().equals("0")){
        			//isRIInserted = true;
        			ReturnID = res.toString();
        		}
        	}
        }catch(Exception e){
        	//e.printStackTrace();
        	isRIInserted = false;
        }
        if (isRIInserted) {
			//////////
			try {
				for (ReturnDetails rd : rdList) {
					//
					String URL2 = Configs.URLSERVER;
					//
					List<NameValuePair> params2 = new ArrayList<NameValuePair>(); //?
					JsonParsing jsonParser2 = new JsonParsing();
					params2.add(new BasicNameValuePair("type", "return"));
					params2.add(new BasicNameValuePair("method","insertreturndetails"));
					params2.add(new BasicNameValuePair("retno", ReturnID));
					params2.add(new BasicNameValuePair("invno", rd.InvNo));
					params2.add(new BasicNameValuePair("prodid", rd.ProdID));
					params2.add(new BasicNameValuePair("retqty", rd.RetQty));
					params2.add(new BasicNameValuePair("unit", rd.Unit));
					params2.add(new BasicNameValuePair("unitpx", rd.UnitPx));
					params2.add(new BasicNameValuePair("reason", rd.Reason));

					JSONObject json2 = jsonParser2.getJSONFromUrl(URL2, "POST",
							params2);

					if (json2 != null) {
						Object res = json2.get(VALID_KEY);

						if (!res.toString().equals("0")) {

						} else {
							
							break;
						}
					}
				}
			} catch (Exception e) {
				//e.printStackTrace();
				isRDInserted = false;
			}
		}
        
		return isRIInserted || isRDInserted;
	}
	
	public static ArrayList<Remittance> GetSalesInfoByInvDate(String date, Context core){
		//
		String VALID_KEY = "Valid";
		String SALESINFO = "collection";
		String kioskid = Vars.KioskID(core,"", type.GET);
		
		ArrayList<Remittance> siArr = new ArrayList<Remittance>();
		
		String URL = Configs.URLSERVER;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		JsonParsing jsonParsing = new JsonParsing();
		
		params.add(new BasicNameValuePair("type", "remit"));
		params.add(new BasicNameValuePair("method", "getinvno"));
		params.add(new BasicNameValuePair("invdate", date));
		params.add(new BasicNameValuePair("kioskid", kioskid));
		
		JSONObject json = jsonParsing.getJSONFromUrl(URL, "POST", params);
		
		try{
			if(json != null){
				Object valid = json.get(VALID_KEY);
				if(valid.toString().equals("1")){
					//
					JSONArray row = json.getJSONArray(SALESINFO);
					//
					for(int x=0; x < row.length(); x++){
						//
						JSONObject data = row.getJSONObject(x);
						Remittance remit = new Remittance();
						
						remit.invno = data.getString("InvNo");
						remit.custname = data.getString("CustName");
						remit.invamt = data.getString("TotalAmount");	
						remit.takenby = data.getString("_TakenBy");
						remit._remit = data.getString("_Remit");
						
						siArr.add(remit);
					}
				}
			} 
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return siArr;
	}
	
	public static String SubmitRemitServer(ArrayList<Remittance> remitArr, Context core){
		//
		String VALID_KEY = "Valid";
		String kioskid = Vars.KioskID(core, "", type.GET);
		String invnoInserted = "";
		//
		for(Remittance rem : remitArr){
			
			String URL = Configs.URLSERVER;
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			JsonParsing jsonParsing = new JsonParsing();
			
			params.add(new BasicNameValuePair("type", "remit"));
			params.add(new BasicNameValuePair("method", "setremit"));
			params.add(new BasicNameValuePair("invno", rem.invno));
			params.add(new BasicNameValuePair("invamt", rem.invamt));
			params.add(new BasicNameValuePair("takenby", rem.takenby));
			params.add(new BasicNameValuePair("kioskid", kioskid));
			//
			JSONObject json = jsonParsing.getJSONFromUrl(URL, "POST", params);
	        
	        try{
	        	if(json != null){
	        		Object res = json.get(VALID_KEY);
	        		
	        		if(res.toString().equals("1")){
	        			invnoInserted += rem.invno+",";
	        		}
	        	}
	        }catch(Exception e){
	        	e.printStackTrace();
	        }
		}
		if(invnoInserted.length() != 0){
			invnoInserted = invnoInserted.substring(0, invnoInserted.length()-1);
		}
		//
		return invnoInserted;
	}
	
	public static int ValidatingInvoice(Context core, String prefix, String digits){
		//
		String VALID_KEY = "Valid";
		String kioskid = Vars.KioskID(core, "", type.GET);
		int val = 0;
		//
		String URL = Configs.URLSERVER;
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		JsonParsing jsonParsing = new JsonParsing();
		
		params.add(new BasicNameValuePair("type", "validation"));
		params.add(new BasicNameValuePair("method", "validateseries"));
		params.add(new BasicNameValuePair("kioskid", kioskid));
		params.add(new BasicNameValuePair("prefix", prefix));
		params.add(new BasicNameValuePair("digits", digits));
		
		JSONObject json = jsonParsing.getJSONFromUrl(URL, "POST", params);
		
		try{
        	if(json != null){
        		Object res = json.get(VALID_KEY);
        		
        		val = Integer.parseInt(res.toString());
        		
        	} 
        }catch(Exception e){
        	e.printStackTrace();
        }
		
		return val;
	}
	
	public static int PostLoggedStatus(Context core, String status){
		//
		String VALID_KEY = "Valid";
		String EmpID = "";
		
		if(!TextUtils.isEmpty(Vars.EmpID(core, "", type.GET))){
			EmpID = Vars.EmpID(core, "", type.GET); 			
		} else {
			return 2000;
		}
		//
		String URL = Configs.URLSERVER;
		//
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		JsonParsing jsonParsing = new JsonParsing();
		
		params.add(new BasicNameValuePair("type", "loggedstatus"));
		params.add(new BasicNameValuePair("method", "post"));
		params.add(new BasicNameValuePair("empid", EmpID));
		params.add(new BasicNameValuePair("status", status));
		//
		JSONObject json = jsonParsing.getJSONFromUrl(URL, "POST", params);
		
		int result = 0;
		
		try{
        	if(json != null){
        		Object res = json.get(VALID_KEY);
        		
        		result = Integer.parseInt(res.toString());
        	} 
        }catch(Exception e){
        	e.printStackTrace();
        }
		
		return result;
	}
}
