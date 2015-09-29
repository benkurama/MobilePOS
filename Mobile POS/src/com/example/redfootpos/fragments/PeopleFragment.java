package com.example.redfootpos.fragments;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.R.layout;
import com.example.redfootpos.database.CellphoneContentProvider;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class PeopleFragment extends Fragment implements LoaderCallbacks<Cursor>{
	
	private MainAct core;
	private FragmentActivity act;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// 
		return inflater.inflate(R.layout.fragment_find_people, container, false);
	}
	
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		
		super.onActivityCreated(savedInstanceState);
		
		act = getActivity();
		core = (MainAct)act;
		
		act.getSupportLoaderManager().initLoader(0, null, this);
		
	}


	

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		
		Uri uri = CellphoneContentProvider.CONTENT_URI;
		
		
		return new CursorLoader(act.getBaseContext(), uri, null, null , new String[]{""}, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cur) {
		// TODO Auto-generated method stub
		
		int count = cur.getCount();
		
		Toast.makeText(core, count+"", Toast.LENGTH_LONG).show();
		
		if(count != 0){
			if(cur.moveToFirst()){
				Toast.makeText(core, cur.getString(cur.getColumnIndex(cur.getColumnName(1))), Toast.LENGTH_LONG).show();
			}
			
		} else {
			Toast.makeText(core, count+"", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
