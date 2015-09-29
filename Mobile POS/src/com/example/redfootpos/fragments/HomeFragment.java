package com.example.redfootpos.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bulletnoid.android.widget.StaggeredGridView.StaggeredGridView;
import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.adapter.STGVAdapter;

public class HomeFragment extends Fragment{
	
	StaggeredGridView stgv;
	STGVAdapter adapter;
	
	FragmentActivity act;
	MainAct core;
	
	View rootView, ActView;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// 
		//View v = inflater.inflate(R.layout.fragment_home, container, false);
		
		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_home, container, false);
			
		}else{
			((ViewGroup)rootView.getParent()).removeView(rootView);
		}
		
		return rootView;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// 
		super.onActivityCreated(savedInstanceState);
		
		if (ActView == null) {
			
//			act = getActivity();
//			core = (MainAct)act;
//			
//			
//			stgv = (StaggeredGridView)core.findViewById(R.id.stgv);
//			int margin = getResources().getDimensionPixelOffset(R.dimen.stgv_margin);
//			
//			stgv.setItemMargin(margin);
//			stgv.setPadding(margin, 0, margin,0);
//			
//			stgv.setHeaderView(new Button(core));
//			 
//			View footerView;
//			LayoutInflater inflater = (LayoutInflater)core.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//			footerView = inflater.inflate(R.layout.layout_loading_footer, null);
//			stgv.setFooterView(footerView);
//			
//			adapter = new STGVAdapter(core, core.getApplication(), act);
//			stgv.setAdapter(adapter);
//			adapter.notifyDataSetChanged();
//			
//			stgv.setOnLoadmoreListener(new StaggeredGridView.OnLoadmoreListener() {
//	            @Override
//	            public void onLoadmore() {
//	                new LoadMoreTask().execute();
//	            }
//	        });
			
			ActView = rootView;
		} else {
			//Toast.makeText(core, "Same State", Toast.LENGTH_SHORT).show();
			
		}
		
	}
	


	public class LoadMoreTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter.getMoreItem();
            adapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }

    }

}
