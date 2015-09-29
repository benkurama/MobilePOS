package com.example.redfootpos.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import android.app.SearchManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

import com.example.redfootpos.model.DataSet;
import com.example.redfootpos.model.Item;
import com.example.redfootpos.model.SalesDetails;
import com.example.redfootpos.utils.JsonParser;

public class CloudfoneDB {
 // =========================================================================
 // TODO Variables
 // =========================================================================
	public static String MY_DATABASE_NAME = "POSDB.db";
	public static final String CELLPHONE_TABLE = "cellphone";
	public static int MY_DATABASE_VERSION = 1;
	//
	public static final String COL_ID = "_id";
	public static final String COL_NAME = "name";
	public static final String COL_URL = "url";
	public static final String COL_WIDTH = "width";
	public static final String COL_HEIGHT = "height";
	public static final String COL_PRICE = "price";
	public static final String COL_DESC = "description";
	public static final String COL_MARK = "mark";
	public static final String COL_TYPE = "type";
	public static final String COL_PRODCODE = "prodcode";
	public static final String COL_IMEI = "imei";
	public static final String COL_COLORCODE = "colorcode";
	public static final String COL_STOCKIN = "stockin";
	public static final String COL_DISCOUNTED = "discounted";
	// 
	// for CONTENT PROVIDER
	private HashMap<String, String> mAliasMap;
	//
	public SQLiteDatabase sqLiteDatabase;
	public SQLiteHelper sqLiteHelper;
	public Context context;
	
	ArrayList<Item> listItems = new ArrayList<Item>();
 // =========================================================================
 // TODO Class Object for SQLiteHelper
 // =========================================================================
	public class SQLiteHelper extends SQLiteOpenHelper {
		public SQLiteHelper(Context context, String name,CursorFactory factory, int version) {
			super(context, name, factory, version);
		}
		
		
 // =========================================================================
 // TODO Activity Life Cycle
 // =========================================================================
	@Override
	public void onCreate(SQLiteDatabase db) {
		//Login Table
//		db.execSQL("create table Login (id integer primary key autoincrement,username text,password text,name text,email text)");
//		db.execSQL("insert into Login (username,password,name,email) values ('benkurama','pass123','Alvin','benkurama@gmail.com')");
//		db.execSQL("insert into Login (username,password,name,email) values ('admin','asdf','Admin','benkurama@gmail.com')");
		
		// Table Creation
		String sql = "" +
				" create table " + CELLPHONE_TABLE + " ( " +
						COL_ID + " integer, " +
						COL_NAME + " text, " +
						COL_URL + " text, " +
						COL_WIDTH + " int, " +
						COL_HEIGHT + " int, " +
						COL_PRICE + " text, " +
						COL_DESC + " text, " +
						COL_MARK + " int, " +
						COL_TYPE + " text," +
						COL_PRODCODE + " text," +
						COL_IMEI + " text," +
						COL_COLORCODE + " text," +
						COL_STOCKIN + " text," +
						COL_DISCOUNTED + " text" +
								")";
		db.execSQL(sql);
		
		// Insert Test	
//		for(int i=0; i < DataSet.ItemName.length; i++ ){
//			//
//			sql = "insert into " + CELLPHONE_TABLE + " ( " +
//					COL_NAME +", " +
//					COL_URL + ", " +
//					COL_WIDTH + ", " +
//					COL_HEIGHT + ", " +
//					COL_PRICE +	", " +
//					COL_DESC + 	", " +
//					COL_MARK + " , " +
//					COL_TYPE + " ) " +
//					"values ( " +
//					" '" + DataSet.ItemName[i] + "' ," +
//					" '" + DataSet.url[i] + "' ," +
//					" " + DataSet.width[i] + " ," +
//					" " + DataSet.height[i] + " ," +
//					" '" + DataSet.price[i] + "' ," +
//					" '" + DataSet.Description[i] + "' ," +
//					" " + "0" + " ," +
//					" '" + DataSet.Type[i] + "' )"
//					;
//			
//			db.execSQL(sql);
//		}
		
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}	
	
 // =========================================================================
 // TODO Main Functions
 // =========================================================================	
	public CloudfoneDB(Context c){
		context = c;
	}
	/// Default Functions
	public CloudfoneDB openToRead() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MY_DATABASE_NAME, null, MY_DATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		return this;	
	}
	public CloudfoneDB openToWrite() throws android.database.SQLException {
		sqLiteHelper = new SQLiteHelper(context, MY_DATABASE_NAME, null,MY_DATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getWritableDatabase();
		return this;	
	}
	// for CONTENT PROVIDER
	public CloudfoneDB contentProvider() throws android.database.SQLException  {
		sqLiteHelper = new SQLiteHelper(context, MY_DATABASE_NAME, null, MY_DATABASE_VERSION);
		sqLiteDatabase = sqLiteHelper.getReadableDatabase();
		
		mAliasMap = new HashMap<String, String>();
		 // Unique id for the each Suggestions ( Mandatory )
        mAliasMap.put("_ID", COL_ID + " as " + "_id" );
        // Text for Suggestions ( Mandatory )
        mAliasMap.put( SearchManager.SUGGEST_COLUMN_TEXT_1, COL_NAME + " as " + SearchManager.SUGGEST_COLUMN_TEXT_1);
        // This value will be appended to the Intent data on selecting an item from Search result or Suggestions ( Optional )
        mAliasMap.put( SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, COL_ID + " as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID );
		return this;
	}
	//
	public void close(){
		sqLiteHelper.close();
	}
 // =========================================================================
 // TODO Inner Class
 // =========================================================================

 // =========================================================================
 // TODO Content Provider Methods
 // =========================================================================
	/** Returns Countries */
    public Cursor getItems(String[] selectionArgs){
 
        String selection = COL_NAME + " like ? ";
 
        if(selectionArgs!=null){
            selectionArgs[0] = "%"+selectionArgs[0] + "%";
        }
 
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setProjectionMap(mAliasMap);
 
        queryBuilder.setTables(CELLPHONE_TABLE);
 
        Cursor c = queryBuilder.query(sqLiteDatabase,
            new String[] { "_ID",
                            SearchManager.SUGGEST_COLUMN_TEXT_1 ,
                            SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID } ,
            selection,
            selectionArgs,
            null,
            null,
            COL_NAME + " asc ",null
        );
        //
        
        //
        return c;
    }
    
    /** Return Country corresponding to the id */
    public Cursor getItem(String id){
 
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
 
        queryBuilder.setTables(CELLPHONE_TABLE);
 
        Cursor c = queryBuilder.query(sqLiteDatabase,
            new String[] { COL_ID, COL_NAME, COL_URL, COL_WIDTH, COL_HEIGHT, COL_PRICE, COL_DESC, COL_MARK, COL_TYPE, COL_DISCOUNTED, COL_IMEI , COL_STOCKIN} ,
            COL_ID+" = ? and "+COL_STOCKIN+" > 0", new String[] { id } , null, null, null ,"1"
        );
        //
        
        //
        return c;
    }
 // =========================================================================
 // TODO Implementation
 // =========================================================================
	public ArrayList<Item> selectAllItems(){
		
		ArrayList<Item> list = new ArrayList<Item>();
		
		try {
			Cursor cur = sqLiteDatabase.rawQuery("select * from "+ CELLPHONE_TABLE+"", null);
			
			if (cur.getCount() != 0) {
				
				for(cur.moveToFirst(); !(cur.isAfterLast()); cur.moveToNext()){
					
					Item item = new Item();
					
					item.url = cur.getString(cur.getColumnIndex(COL_URL));
					item.width = cur.getInt(cur.getColumnIndex(COL_WIDTH));
					item.height = cur.getInt(cur.getColumnIndex(COL_HEIGHT));
					item.price = cur.getString(cur.getColumnIndex(COL_PRICE));
					item.name = cur.getString(cur.getColumnIndex(COL_NAME));
					item.Description = cur.getString(cur.getColumnIndex(COL_DESC));
					
					item.ID = cur.getInt(cur.getColumnIndex(COL_ID));
					item.mark = cur.getInt(cur.getColumnIndex(COL_MARK));
					item.type = cur.getString(cur.getColumnIndex(COL_TYPE));
					
					list.add(item);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			
			close();
		}
		
		return list;
	}
	
	public ArrayList<Item> selectPhoneItems(){
		
		ArrayList<Item> list = new ArrayList<Item>();
		
		try {
			Cursor cur = sqLiteDatabase.rawQuery("select * from "+ CELLPHONE_TABLE+" where "+COL_TYPE+" = 'phone' and "+COL_STOCKIN+" > 0 order by name", null);
			
			if (cur.getCount() != 0) {
				
				for(cur.moveToFirst(); !(cur.isAfterLast()); cur.moveToNext()){
					
					Item item = new Item();
					
					item.url = cur.getString(cur.getColumnIndex(COL_URL));
					item.width = cur.getInt(cur.getColumnIndex(COL_WIDTH));
					item.height = cur.getInt(cur.getColumnIndex(COL_HEIGHT));
					item.price = cur.getString(cur.getColumnIndex(COL_PRICE));
					item.name = cur.getString(cur.getColumnIndex(COL_NAME));
					item.Description = cur.getString(cur.getColumnIndex(COL_DESC));
					
					item.ID = cur.getInt(cur.getColumnIndex(COL_ID));
					item.mark = cur.getInt(cur.getColumnIndex(COL_MARK));
					item.type = cur.getString(cur.getColumnIndex(COL_TYPE));
					
					item.Stockin = cur.getInt(cur.getColumnIndex(COL_STOCKIN));
					item.Imei = cur.getString(cur.getColumnIndex(COL_IMEI));
					item._discounted = cur.getString(cur.getColumnIndex(COL_DISCOUNTED));
					
					list.add(item);
				}
			} 
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			
			close();
		}
		
		return list;
	}
	
	public void insertPhoneItems(ArrayList<Item> collection, String type){
		
		try{
			ContentValues cv;
			//
			for(Item instance: collection){
				cv = new ContentValues();
				cv.put(COL_ID, instance.ID);
				cv.put(COL_NAME, instance.name);
				cv.put(COL_URL, instance.url);
				cv.put(COL_WIDTH, instance.width);
				cv.put(COL_HEIGHT, instance.height);
				cv.put(COL_PRICE, instance.price);
				cv.put(COL_DESC, instance.Description);
				cv.put(COL_MARK, "0");
				cv.put(COL_TYPE, type);
				cv.put(COL_PRODCODE, instance.Prodcode);
				cv.put(COL_IMEI, instance.Imei);
				cv.put(COL_COLORCODE, instance.Colorcode);
				cv.put(COL_STOCKIN, instance.Stockin);
				cv.put(COL_DISCOUNTED, instance._discounted);
				//
				sqLiteDatabase.insert(CELLPHONE_TABLE, null, cv);
				
			}
			
		} catch (Exception e){
			
		} finally{
			close();
		}
		
	}
	
	public boolean deletePhoneItems(String type){
		
		int isdelete = 0;
		
		try {
			isdelete = sqLiteDatabase.delete(CELLPHONE_TABLE, COL_TYPE + " = ?" , new String [] {type});
		} catch (Exception e) {
			
		} finally{
			close();
		}
		return isdelete > 0;
	}
	
	public ArrayList<Item> selectTabletItems(){
		
		ArrayList<Item> list = new ArrayList<Item>();
		
		try {
			Cursor cur = sqLiteDatabase.rawQuery("select * from "+ CELLPHONE_TABLE+" where "+COL_TYPE+" = 'tablet' and "+COL_STOCKIN+" > 0 order by name", null);
			
			if (cur.getCount() != 0) {
				
				for(cur.moveToFirst(); !(cur.isAfterLast()); cur.moveToNext()){
					
					Item item = new Item();
					
					item.url = cur.getString(cur.getColumnIndex(COL_URL));
					item.width = cur.getInt(cur.getColumnIndex(COL_WIDTH));
					item.height = cur.getInt(cur.getColumnIndex(COL_HEIGHT));
					item.price = cur.getString(cur.getColumnIndex(COL_PRICE));
					item.name = cur.getString(cur.getColumnIndex(COL_NAME));
					item.Description = cur.getString(cur.getColumnIndex(COL_DESC));
					
					item.ID = cur.getInt(cur.getColumnIndex(COL_ID));
					item.mark = cur.getInt(cur.getColumnIndex(COL_MARK));
					item.type = cur.getString(cur.getColumnIndex(COL_TYPE));
					
					item.Stockin = cur.getInt(cur.getColumnIndex(COL_STOCKIN));
					item.Imei = cur.getString(cur.getColumnIndex(COL_IMEI));
					item._discounted = cur.getString(cur.getColumnIndex(COL_DISCOUNTED));
					
					list.add(item);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			
			close();
		}
		
		return list;
	}

	public ArrayList<Item> selectAccessoryItems(){
	
	ArrayList<Item> list = new ArrayList<Item>();
	
	try {
		Cursor cur = sqLiteDatabase.rawQuery("select * from "+ CELLPHONE_TABLE+" where "+COL_TYPE+" = 'accessory' and "+COL_STOCKIN+" > 0 order by name", null);
		
		if (cur.getCount() != 0) {
			
			for(cur.moveToFirst(); !(cur.isAfterLast()); cur.moveToNext()){
				
				Item item = new Item();
				
				item.url = cur.getString(cur.getColumnIndex(COL_URL));
				item.width = cur.getInt(cur.getColumnIndex(COL_WIDTH));
				item.height = cur.getInt(cur.getColumnIndex(COL_HEIGHT));
				item.price = cur.getString(cur.getColumnIndex(COL_PRICE));
				item.name = cur.getString(cur.getColumnIndex(COL_NAME));
				item.Description = cur.getString(cur.getColumnIndex(COL_DESC));
				
				item.ID = cur.getInt(cur.getColumnIndex(COL_ID));
				item.mark = cur.getInt(cur.getColumnIndex(COL_MARK));
				item.type = cur.getString(cur.getColumnIndex(COL_TYPE));
				
				item.Stockin = cur.getInt(cur.getColumnIndex(COL_STOCKIN));
				item.Imei = cur.getString(cur.getColumnIndex(COL_IMEI));
				item._discounted = cur.getString(cur.getColumnIndex(COL_DISCOUNTED));
				
				list.add(item);
			}
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally{
		
		close();
	}
	
	return list;
}
	
	public void setMarkOnItem(int id){
		
		String strFilter = COL_ID + "=" + id;
		ContentValues cv = new ContentValues();
		cv.put(COL_MARK, 1);
		
		sqLiteDatabase.update(CELLPHONE_TABLE, cv, strFilter, null);
		
		close();
	}
	
	public void setMarkOutItem(int id){
		
		String strFilter = COL_ID + "=" + id;
		ContentValues cv = new ContentValues();
		cv.put(COL_MARK, 0);
		
		sqLiteDatabase.update(CELLPHONE_TABLE, cv, strFilter, null);
		
		close();
	}
	
	public ArrayList<Item> selectAllMarks(){
		
		ArrayList<Item> list = new ArrayList<Item>();
		
		try {
			Cursor cur = sqLiteDatabase.rawQuery("select * from "+ CELLPHONE_TABLE+" where "+COL_MARK+" = 1", null);
			
			if (cur.getCount() != 0) {
				
				for(cur.moveToFirst(); !(cur.isAfterLast()); cur.moveToNext()){
					
					Item item = new Item();
					
					item.url = cur.getString(cur.getColumnIndex(COL_URL));
					item.width = cur.getInt(cur.getColumnIndex(COL_WIDTH));
					item.height = cur.getInt(cur.getColumnIndex(COL_HEIGHT));
					item.price = cur.getString(cur.getColumnIndex(COL_PRICE));
					item.name = cur.getString(cur.getColumnIndex(COL_NAME));
					item.Description = cur.getString(cur.getColumnIndex(COL_DESC));
					
					item.ID = cur.getInt(cur.getColumnIndex(COL_ID));
					item.mark = cur.getInt(cur.getColumnIndex(COL_MARK));
					item.Imei = cur.getString(cur.getColumnIndex(COL_IMEI));
					item.Stockin = cur.getInt(cur.getColumnIndex(COL_STOCKIN));
					item.type = cur.getString(cur.getColumnIndex(COL_TYPE));
					item._discounted = cur.getString(cur.getColumnIndex(COL_DISCOUNTED));
					
					list.add(item);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			
			close();
		}
		
		return list;
	}
	
	public int getAllMarksToCount(){
		//
		Cursor cur = sqLiteDatabase.rawQuery("select * from "+ CELLPHONE_TABLE+" where "+COL_MARK+" = 1", null);
		
		int count = cur.getCount();
		
		close();
		return count;
	}
	
	public ArrayList<Item> selectAllMarksByPhone(){
		//
		ArrayList<Item> list = new ArrayList<Item>();
		
		try {
			Cursor cur = sqLiteDatabase.rawQuery("select * from "+ CELLPHONE_TABLE+" where "+COL_MARK+" = 1 and type = 'phone'", null);
			
			if (cur.getCount() != 0) {
				
				for(cur.moveToFirst(); !(cur.isAfterLast()); cur.moveToNext()){
					
					Item item = new Item();
					
					item.url = cur.getString(cur.getColumnIndex(COL_URL));
					item.width = cur.getInt(cur.getColumnIndex(COL_WIDTH));
					item.height = cur.getInt(cur.getColumnIndex(COL_HEIGHT));
					item.price = cur.getString(cur.getColumnIndex(COL_PRICE));
					item.name = cur.getString(cur.getColumnIndex(COL_NAME));
					item.Description = cur.getString(cur.getColumnIndex(COL_DESC));
					
					item.ID = cur.getInt(cur.getColumnIndex(COL_ID));
					item.mark = cur.getInt(cur.getColumnIndex(COL_MARK));
					
					list.add(item);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			
			close();
		}
		
		return list;
	}
	
	public ArrayList<Item> selectAllMarksByTablet(){
		//
		ArrayList<Item> list = new ArrayList<Item>();
		
		try {
			Cursor cur = sqLiteDatabase.rawQuery("select * from "+ CELLPHONE_TABLE+" where "+COL_MARK+" = 1 and type = 'tablet'", null);
			
			if (cur.getCount() != 0) {
				
				for(cur.moveToFirst(); !(cur.isAfterLast()); cur.moveToNext()){
					
					Item item = new Item();
					
					item.url = cur.getString(cur.getColumnIndex(COL_URL));
					item.width = cur.getInt(cur.getColumnIndex(COL_WIDTH));
					item.height = cur.getInt(cur.getColumnIndex(COL_HEIGHT));
					item.price = cur.getString(cur.getColumnIndex(COL_PRICE));
					item.name = cur.getString(cur.getColumnIndex(COL_NAME));
					item.Description = cur.getString(cur.getColumnIndex(COL_DESC));
					
					item.ID = cur.getInt(cur.getColumnIndex(COL_ID));
					item.mark = cur.getInt(cur.getColumnIndex(COL_MARK));
					
					list.add(item);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			
			close();
		}
		
		return list;
	}
	
	public ArrayList<Item> selectAllMarksByAccessory(){
		//
		ArrayList<Item> list = new ArrayList<Item>();
		
		try {
			Cursor cur = sqLiteDatabase.rawQuery("select * from "+ CELLPHONE_TABLE+" where "+COL_MARK+" = 1 and type = 'accessory'", null);
			
			if (cur.getCount() != 0) {
				
				for(cur.moveToFirst(); !(cur.isAfterLast()); cur.moveToNext()){
					
					Item item = new Item();
					
					item.url = cur.getString(cur.getColumnIndex(COL_URL));
					item.width = cur.getInt(cur.getColumnIndex(COL_WIDTH));
					item.height = cur.getInt(cur.getColumnIndex(COL_HEIGHT));
					item.price = cur.getString(cur.getColumnIndex(COL_PRICE));
					item.name = cur.getString(cur.getColumnIndex(COL_NAME));
					item.Description = cur.getString(cur.getColumnIndex(COL_DESC));
					
					item.ID = cur.getInt(cur.getColumnIndex(COL_ID));
					item.mark = cur.getInt(cur.getColumnIndex(COL_MARK));
					
					list.add(item);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			
			close();
		}
		
		return list;
	}
	
	public Item GetItemsByID(int id){
		
		Item item = null;
		try {
			Cursor cur = sqLiteDatabase.rawQuery("select * from "+ CELLPHONE_TABLE +" where "+COL_ID+" = "+id, null);
			//
			cur.moveToFirst();
			item = new Item();
			
			//
			item.url = cur.getString(cur.getColumnIndex(COL_URL));
			item.width = cur.getInt(cur.getColumnIndex(COL_WIDTH));
			item.height = cur.getInt(cur.getColumnIndex(COL_HEIGHT));
			item.price = cur.getString(cur.getColumnIndex(COL_PRICE));
			item.name = cur.getString(cur.getColumnIndex(COL_NAME));
			item.Description = cur.getString(cur.getColumnIndex(COL_DESC));
			
			item.ID = cur.getInt(cur.getColumnIndex(COL_ID));
			item.mark = cur.getInt(cur.getColumnIndex(COL_MARK));
			item.type = cur.getString(cur.getColumnIndex(COL_TYPE));
			item.Stockin =cur.getInt(cur.getColumnIndex(COL_STOCKIN));
			item.Imei = cur.getString(cur.getColumnIndex(COL_IMEI));
			
		} catch (Exception e) {
			
		} finally{
			close();
		}
		
		return item;
	}
	

	public void UpdateItemStock(int stockDiff, int prodid, String imei) {
		// 
		int CurrentStock = 0;
		String CurrImei = "";
		try{
			Cursor cur = sqLiteDatabase.rawQuery("select * from "+ CELLPHONE_TABLE +" where "+COL_ID+" = "+prodid, null);
			cur.moveToFirst();
			CurrentStock = cur.getInt(cur.getColumnIndex(COL_STOCKIN));
			CurrImei = cur.getString(cur.getColumnIndex(COL_IMEI));
		}catch(Exception e){
			
		}
		//
		CurrentStock = CurrentStock - stockDiff;
		if(CurrImei.contains(",")){
			
			String[] ImeiValArr = imei.split("\\,");
			
			for(String str : ImeiValArr){
				//
				CurrImei = CurrImei.replace(str, "");
				
				if(CurrImei.contains(",,")){
					CurrImei = CurrImei.replace(",,",",");
				}
			}
			
			String strImei = CurrImei.substring(CurrImei.length()-1, CurrImei.length());
			
			if(strImei.equals(",")){
				CurrImei = CurrImei.substring(0, CurrImei.length() - 1);
			}
			
			if(CurrImei.length() > 0){
				strImei = CurrImei.substring(0,1);
				
				if(strImei.equals(",")){
					CurrImei = CurrImei.substring(1, CurrImei.length());
				}
			} 
			
		} else {
			CurrImei = "";
		}
		//
		try{
			ContentValues cv = new ContentValues();
			cv.put(COL_STOCKIN, CurrentStock);
			cv.put(COL_IMEI, CurrImei);
			sqLiteDatabase.update(CELLPHONE_TABLE, cv, COL_ID+ " = "+ prodid, null);
		}catch(Exception e){
			
		} finally{
			close();
		}
		
	}
	
	public void UpdateStock(int stockDiff, int prodid){
		//
		int CurrentStock = 0;
		try{
			Cursor cur = sqLiteDatabase.rawQuery("select * from "+ CELLPHONE_TABLE +" where "+COL_ID+" = "+prodid, null);
			cur.moveToFirst();
			CurrentStock = cur.getInt(cur.getColumnIndex(COL_STOCKIN));
		}catch(Exception e){
			
		}
		//
		CurrentStock = CurrentStock - stockDiff;
		//
		try{
			ContentValues cv = new ContentValues();
			cv.put(COL_STOCKIN, CurrentStock);
			sqLiteDatabase.update(CELLPHONE_TABLE, cv, COL_ID+ " = "+ prodid, null);
		}catch(Exception e){
			
		} finally{
			close();
		}
		//
	}
}
