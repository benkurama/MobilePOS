package com.example.redfootpos.object;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.redfootpos.model.Item;

public class ItemsDatabase {
	// ---------------------------------------------------------------------
	// VARIABLES
	// ---------------------------------------------------------------------
	private Context core = null;
	private SQLiteDatabase db = null;
	private Cursor cursor = null;
	
	private static final String DB_NAME = "REDFOOTTECH.AutoConfig";
	private static final String DB_TABLE = "Items";
	
	private static final String CREATE_TABLE = "" +
			"CREATE TABLE IF NOT EXISTS " +DB_TABLE+ "( " +
			"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"url TEXT, " +
			"width INTEGER, " +
			"height INTEGER, " +
			"price TEXT, " +
			"name TEXT" +
			")";
	
	private static final String INSERT_SAMPLE = "" +
			"INSERT INTO "+DB_TABLE+ "( url, width, height, price, name) " +
					"values ('google.com', 500, 250, 100, 'CloudFone')";
	
	// ---------------------------------------------------------------------
	// CONSTRUCTORS
	// ---------------------------------------------------------------------
	public ItemsDatabase(Context core){
		this.core = core;
	}
	// ---------------------------------------------------------------------
	// MAIN FUNCTIONS
	// ---------------------------------------------------------------------
	public void initializedDB(){
		
		db = core.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
		db.execSQL(CREATE_TABLE);
		//	
		
	}
	
	public void closeDB(){
		
		if(cursor != null)
			cursor.close();
		
		if(db != null)
			db.close();
		
		cursor = null;
		db = null;
		
	}
	// ---------------------------------------------------------------------
	// SUB FUNCTIONS
	// ---------------------------------------------------------------------
	public void populateDataSamples(){
		
		db.execSQL("INSERT INTO "+DB_TABLE+ "( url, width, height, price, name) " +
				"values ('http://s26.postimg.org/5n627wwhl/cloudfoneice350e_1372844042_1.jpg', " +
				"320, " +
				"307, " +
				"'1', " +
				"'cloudfoneice')");
		
		db.execSQL("INSERT INTO "+DB_TABLE+ "( url, width, height, price, name) " +
				"values ('http://91-img.com/pictures/cloudfone-excite-504d-mobile-phone-large-1.jpg', " +
				"300, " +
				"400, " +
				"'5', " +
				"'cloudfone-excite-504d-mobile-phone')");
		
		db.execSQL("INSERT INTO "+DB_TABLE+ "( url, width, height, price, name) " +
				"values ('http://4.bp.blogspot.com/-W16ur0DCvL0/Uv9KgXkQxGI/AAAAAAAACqc/eAx5dZptwKs/s1600/CloudFone-Android-Phone-Pricelist-2014.jpg', " +
				"526, " +
				"417, " +
				"'8', " +
				"'CloudFone-Android-Phone-Pricelist-2014')");
		
		db.execSQL("INSERT INTO "+DB_TABLE+ "( url, width, height, price, name) " +
				"values ('http://4.bp.blogspot.com/-D4EuemhdEf4/UJbrzd3ghQI/AAAAAAAAPtQ/n7uOYcR5Fmc/s1600/CloudFone+Ice+++Turbo+smartfone+iloilo-philippines.jpg', " +
				"433, " +
				"409, " +
				"'2', " +
				"'CloudFone+Ice+++Turbo+smartfone+iloilo-philippines')");
		
		db.execSQL("INSERT INTO "+DB_TABLE+ "( url, width, height, price, name) " +
				"values ('https://filipinotechaddict.files.wordpress.com/2014/09/10672321_844617085571537_8848677519218400637_n.jpg', " +
				"403, " +
				"403, " +
				"'4', " +
				"'10672321_844617085571537')");
	}
	
	public ArrayList<Item> selectAllItems(){
		
		db = core.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
		//
		ArrayList<Item> ItemList = new ArrayList<Item>();
		//
		try {
			cursor = db.rawQuery("select * from "+DB_TABLE+"", null);
			
			if (cursor.getCount() != 0) {
				
				for(cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()){
					
					Item item = new Item();
					
					item.url = cursor.getString(cursor.getColumnIndex("url"));
					item.width = cursor.getInt(cursor.getColumnIndex("width"));
					item.height = cursor.getInt(cursor.getColumnIndex("height"));
					item.price = cursor.getString(cursor.getColumnIndex("price"));
					item.name = cursor.getString(cursor.getColumnIndex("name"));
					
					ItemList.add(item);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		//
		closeDB();
		
		return ItemList;
	}
}
