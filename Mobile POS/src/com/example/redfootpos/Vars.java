package com.example.redfootpos;

import com.example.redfootpos.model.Item;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

public class Vars extends Application{
	
	public enum type {
		SET,
		GET
	}
	
	public enum method{
		ADD,
		SUB
	}
	
	public static Item selItem;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	public static void setSelectedItem(Item obj){
		selItem = obj;
	}
	public static Item getSelectedItem(){
		return selItem;
	}
	
	public static String EmpID(Context core, String empid, type type){
		switch (type) {
		case SET:
			PreferenceManager.getDefaultSharedPreferences(core).edit().putString("empid", empid).commit();
			return "";
		case GET:
			return PreferenceManager.getDefaultSharedPreferences(core).getString("empid", null);
		}
		return "";	
	}
	
	public static String UserName(Context core,String username, type type){
		switch (type) {
		case SET:
			PreferenceManager.getDefaultSharedPreferences(core).edit().putString("username", username).commit();
			return "";
		case GET:
			return PreferenceManager.getDefaultSharedPreferences(core).getString("username", null);
		}
		return "";	
	}
	
	public static String FirstName(Context core, String fname, type type){
		switch (type) {
		case SET:
			PreferenceManager.getDefaultSharedPreferences(core).edit().putString("fname", fname).commit();
			return "";
		case GET:
			return PreferenceManager.getDefaultSharedPreferences(core).getString("fname", null);
		}
		return "";
	}
	
	public static String LastName(Context core, String lname, type type){
		switch (type) {
		case SET:
			PreferenceManager.getDefaultSharedPreferences(core).edit().putString("lname", lname).commit();
			return "";
		case GET:
			return PreferenceManager.getDefaultSharedPreferences(core).getString("lname", null);
		}
		return "";
	}
	
	public static String KioskID(Context core, String kiosk, type type){
		switch (type) {
		case SET:
			PreferenceManager.getDefaultSharedPreferences(core).edit().putString("kioskid", kiosk).commit();
			return "";
		case GET:
			return PreferenceManager.getDefaultSharedPreferences(core).getString("kioskid", null);
		}
		return "";
	}
	
	public static String Password(Context core, String password, type type){
		switch (type) {
		case SET:
			PreferenceManager.getDefaultSharedPreferences(core).edit().putString("password", password).commit();
			return "";
		case GET:
			return PreferenceManager.getDefaultSharedPreferences(core).getString("password", null);
		}
		return "";
	}
	
	public static String Department(Context core, String department, type type){
		switch (type) {
		case SET:
			PreferenceManager.getDefaultSharedPreferences(core).edit().putString("department", department).commit();
			return "";
		case GET:
			return PreferenceManager.getDefaultSharedPreferences(core).getString("department", null);
		}
		return "";
	}
	
	public static String UserLevel(Context core, String userlevel, type type){
		switch (type) {
		case SET:
			PreferenceManager.getDefaultSharedPreferences(core).edit().putString("userlevel", userlevel).commit();
			return "";
		case GET:
			return PreferenceManager.getDefaultSharedPreferences(core).getString("userlevel", null);
		}
		return "";
	}
	
	public static String MobileNo(Context core, String contact, type type){
		switch (type) {
		case SET:
			PreferenceManager.getDefaultSharedPreferences(core).edit().putString("contact", contact).commit();
			return "";
		case GET:
			return PreferenceManager.getDefaultSharedPreferences(core).getString("contact", null);
		}
		return "";
	}
	
	public static String Email(Context core, String email, type type){
		switch (type) {
		case SET:
			PreferenceManager.getDefaultSharedPreferences(core).edit().putString("email", email).commit();
			return "";
		case GET:
			return PreferenceManager.getDefaultSharedPreferences(core).getString("email", null);
		}
		return "";
	}
	
	public static int ParkedCount(Context core, int count, type type){
		switch(type){
		case SET:
			PreferenceManager.getDefaultSharedPreferences(core).edit().putInt("parkedcount", count).commit();
			return 0;
		case GET:
			return PreferenceManager.getDefaultSharedPreferences(core).getInt("parkedcount", 0);
		}
		return 0;
	}
	
	public static void ParkedMethods(Context core, method method){
		//
		int count = PreferenceManager.getDefaultSharedPreferences(core).getInt("parkedcount", 0);
		//
		switch(method){
		case ADD:
			count += 1;
			PreferenceManager.getDefaultSharedPreferences(core).edit().putInt("parkedcount", count).commit();
			break;
		case SUB:
			count -= 1;
			PreferenceManager.getDefaultSharedPreferences(core).edit().putInt("parkedcount", count).commit();
			break;
		}
	}
	//
	public static boolean RefreshItems(Context core, boolean val, type type){
		switch(type){
		case SET:
			PreferenceManager.getDefaultSharedPreferences(core).edit().putBoolean("refresh", val).commit();
			return false;
		case GET:
			return PreferenceManager.getDefaultSharedPreferences(core).getBoolean("refresh", false);
		}
		
		return false;
	}
	
	public static String Staff(Context core, String staff, type type){
		switch (type) {
		case SET:
			PreferenceManager.getDefaultSharedPreferences(core).edit().putString("staff", staff).commit();
			return "";
		case GET:
			return PreferenceManager.getDefaultSharedPreferences(core).getString("staff", null);
		}
		return "";
	}
	
}
