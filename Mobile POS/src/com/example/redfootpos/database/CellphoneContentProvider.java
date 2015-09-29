package com.example.redfootpos.database;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class CellphoneContentProvider extends ContentProvider {
	 // =========================================================================
	 // TODO Variables
	 // =========================================================================
	public static final String AUTHORITY = "com.example.redfootpos.database.CellphoneContentProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/cellphones");
	
	CloudfoneDB db = null;
	
	private static final int SUGGESTIONS_CELLPHONE = 1;
	private static final int SEARCH_CELLPHONE = 2;
	private static final int GET_CELLPHONE = 3;
	
	UriMatcher uriMatcher = builUriMatcher();
	
	private UriMatcher builUriMatcher(){
		
		UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		
		// Suggestion items of Search Dialog is provided by this uri
        uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SUGGESTIONS_CELLPHONE);
 
        // This URI is invoked, when user presses "Go" in the Keyboard of Search Dialog
        // Listview items of SearchableActivity is provided by this uri
        // See android:searchSuggestIntentData="content://in.wptrafficanalyzer.searchdialogdemo.provider/countries" of searchable.xml
        uriMatcher.addURI(AUTHORITY, "cellphones", SEARCH_CELLPHONE);
 
        // This URI is invoked, when user selects a suggestion from search dialog or an item from the listview
        // Country details for CountryActivity is provided by this uri
        // See, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID in CountryDB.java
        uriMatcher.addURI(AUTHORITY, "cellphones/#", GET_CELLPHONE);
 
        return uriMatcher;
	}
	
	@Override
	public boolean onCreate() {
		db = new CloudfoneDB(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,	String[] selectionArgs, String sortOrder) {
		
		Cursor c = null;
		
		switch(uriMatcher.match(uri)){
        case SUGGESTIONS_CELLPHONE :
            c = db.contentProvider().getItems(selectionArgs);
            break;
        case SEARCH_CELLPHONE :
        	c = db.contentProvider().getItems(selectionArgs);
            break;
        case GET_CELLPHONE :
            String id = uri.getLastPathSegment();
            c = db.contentProvider().getItem(id);
		}

    return c;
	}

	@Override
	public String getType(Uri uri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,	String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

}
