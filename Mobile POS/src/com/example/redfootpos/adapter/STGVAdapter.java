package com.example.redfootpos.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Application;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.Vars;
import com.example.redfootpos.Vars.type;
import com.example.redfootpos.database.CloudfoneDB;
import com.example.redfootpos.model.DataSet;
import com.example.redfootpos.model.Item;
import com.example.redfootpos.object.STGVImageView;
import com.example.redfootpos.pager.BuyPager;
import com.example.redfootpos.pager.ItemPager;
import com.example.redfootpos.utils.JsonParser;
import com.example.redfootpos.utils.Utils;
import com.squareup.picasso.Picasso;

public class STGVAdapter extends BaseAdapter {
 // =========================================================================
 // TODO Variables
 // =========================================================================
    private MainAct mContext;
    private Application mAppContext;
    private FragmentActivity act;
    private DataSet mData = new DataSet();
    private ArrayList<Item> mItems = new ArrayList<Item>();
    private int itemType;
    //
    private ViewPager pager;
// =========================================================================
// TODO Constructor
// =========================================================================
    public STGVAdapter(MainAct context, Application app, FragmentActivity activity, ViewPager pager, int type) {
        mContext = context;
        mAppContext = app;
        act = activity;
        this.pager = pager;
        this.itemType = type;
        
        boolean isRefreshItem = Vars.RefreshItems(mContext, false, com.example.redfootpos.Vars.type.GET);
        
        if(!isRefreshItem){
        	getMoreItem();
        } else {
        	getNewOnceItems();
        }
        //
        
    }
// =========================================================================
// TODO Overrides
// =========================================================================
    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        final Item item = mItems.get(position);

        String url = item.url;
        
        if (convertView == null) {
            Holder holder = new Holder();
            view = View.inflate(mContext, R.layout.cell_stgv, null);
            holder.img_content = (STGVImageView) view.findViewById(R.id.img_content);
            holder.tv_info = (TextView) view.findViewById(R.id.tv_name);
            holder.tv_price = (TextView)view.findViewById(R.id.tv_price);
            holder.btn_buy = (Button)view.findViewById(R.id.btnBuyItem);
            holder.btn_save = (Button)view.findViewById(R.id.btnItemSave);
            holder.flCell = (FrameLayout)view.findViewById(R.id.flMenu);
            holder.tv_price_disc = (TextView)view.findViewById(R.id.tv_price_disc);
            //
			
            //
            view.setTag(holder);
        } else {
            view = convertView;
        }

        final Holder holder = (Holder) view.getTag();
       
        /**
         * StaggeredGridView has bugs dealing with child TouchEvent
         * You must deal TouchEvent in the child view itself
         **/
        holder.img_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            	ItemPager.setProperties(item);
            	pager.setCurrentItem(1);
            	
            	mContext.getActionBar().setDisplayShowHomeEnabled(false);
            	mContext.setTitle("Item");
            	
            }
        });

        
        holder.tv_info.setText(item.name);
        
        int priceInt = Utils.me.CurrencyStringToInt(item.price);
        String priceStr = Utils.me.FormatCurrencyToString(priceInt, "SRP: PHP ");
        
        holder.tv_price.setText(priceStr);
        
        String discPrice = item._discounted;
        
        
        if(!discPrice.equals("0.0")){
        	//
        	int discPriceInt = Utils.me.CurrencyStringToInt(item._discounted);
        	String discPricesTR = Utils.me.FormatCurrencyToString(discPriceInt, "DISC: PHP ");
        	holder.tv_price_disc.setText(discPricesTR);
        } else {
        	holder.tv_price_disc.setVisibility(View.GONE);
        }
        
        holder.img_content.mHeight = item.height;
        holder.img_content.mWidth = item.width;

        Picasso.with(mAppContext).load(url).into(holder.img_content);
        
        holder.btn_buy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//
				pager.setCurrentItem(2);
				
				BuyPager.setProperties(item);
				
				mContext.getActionBar().setDisplayShowHomeEnabled(false);
            	mContext.setTitle("Buy Item");
			}
		});
        
        if(item.mark == 1){
        	holder.btn_save.setText("EditList");
        	holder.btn_save.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mContext.mDrawerLayout.openDrawer(Gravity.RIGHT);
				}
			});
        } else {
        	holder.btn_save.setText("Save");
        	holder.btn_save.setOnClickListener(new OnClickListener() {
    			
    			@Override
    			public void onClick(View v) {
    				holder.btn_save.setText("EditList");
    				new CloudfoneDB(mContext).openToWrite().setMarkOnItem(item.ID);
    				holder.btn_save.setOnClickListener(new OnClickListener() {
    					@Override
    					public void onClick(View v) {
    						mContext.mDrawerLayout.openDrawer(Gravity.RIGHT);
    					}
    				});
    			}
    		});
        }
        
        return view;
    }
// =========================================================================
// TODO Main Functions
// =========================================================================
    @SuppressWarnings("unchecked")
  	public void getMoreItem() {
      	
      	ArrayList<Item> prodItems = new ArrayList<Item>();
      	ArrayList<Integer> mark = new ArrayList<Integer>();
      	
      	switch(itemType){
      	case 1: 
      		
      		prodItems = new CloudfoneDB(mContext).openToRead().selectPhoneItems();
      		
      		if(prodItems.size() == 0){
      		// FOR OFFLINE PREVIEW ONLY 'TEMPORARILY'
      			if(Vars.UserName(mContext, "", type.GET).equals("offline")){
      				AccessLocalSample();
      			} else{
      				new AccessFromServer(mark).execute("phone");
      			}
      			
      		} else {
      		// FOR OFFLINE PREVIEW ONLY 'TEMPORARILY'
      			if(Vars.UserName(mContext, "", type.GET).equals("offline")){
      				AccessLocalSample();
      			} else{
      				new AccessFromLocal().execute(prodItems);
      			}
      		}
      		
      		break;
      	case 2:
      		prodItems = new CloudfoneDB(mContext).openToRead().selectTabletItems();
      		
      		if(prodItems.size() == 0){
      			new AccessFromServer(mark).execute("tablet");
      		} else {
      			new AccessFromLocal().execute(prodItems);
      		}
      		break;
      	case 3:
      		prodItems = new CloudfoneDB(mContext).openToRead().selectAccessoryItems();
      		
      		if(prodItems.size() == 0){
      			new AccessFromServer(mark).execute("accessory");
      		} else {
      			new AccessFromLocal().execute(prodItems);
      		}
      		break;
      	}
      }
    
	public void getNewOnceItems(){
      	
      	switch(itemType){
      	case 1:
      		PullToRefresh();
      		break;
      	case 2:
      		PullToRefresh();
      		break;
      	case 3:
      		PullToRefresh();
      		// finally neccessary to set false after all three categories are refreshed
      		Vars.RefreshItems(mContext, false, type.SET);
      		break;
      	}
    }
    
    public void PullToRefresh(){
      	
      switch (itemType) {
  		case 1:
  			mItems.clear();
  			//
  			ArrayList<Item> listPhone = new ArrayList<Item>();
  			listPhone = new CloudfoneDB(mContext).openToRead().selectPhoneItems();
  			ArrayList<Integer> markPhone = new ArrayList<Integer>();
  			
  			for(Item item : listPhone){
  				if(item.mark == 1){
  					markPhone.add(item.ID);
  				}
  			}

  			new CloudfoneDB(mContext).openToWrite().deletePhoneItems("phone");
  			new AccessFromServer(markPhone).execute("phone");
  			
  			break;
  		case 2:
  			mItems.clear();
  			//
  			ArrayList<Item> listTablet = new ArrayList<Item>();
  			listTablet = new CloudfoneDB(mContext).openToRead().selectTabletItems();
  			ArrayList<Integer> markTablet = new ArrayList<Integer>();
  			
  			for(Item item : listTablet){
  				if(item.mark == 1){
  					markTablet.add(item.ID);
  				}
  			}

  			new CloudfoneDB(mContext).openToWrite().deletePhoneItems("tablet");
  			new AccessFromServer(markTablet).execute("tablet");
  			break;
  		case 3:
  			mItems.clear();
  			//
  			ArrayList<Item> listAccessory = new ArrayList<Item>();
  			listAccessory = new CloudfoneDB(mContext).openToRead().selectAccessoryItems();
  			ArrayList<Integer> markAccessory = new ArrayList<Integer>();
  			
  			for(Item item : listAccessory){
  				if(item.mark == 1){
  					markAccessory.add(item.ID);
  				}
  			}

  			
  			new CloudfoneDB(mContext).openToWrite().deletePhoneItems("accessory");
  			new AccessFromServer(markAccessory).execute("accessory");
  			break;
  		}
      }
// =========================================================================
// TODO Sub Functions
// =========================================================================
    @SuppressWarnings("unchecked")
	private void AccessLocalSample(){
    	
    	ArrayList<Item> itemList = new ArrayList<Item>();
    	Item item = new Item();
		
		item.ID = 5;
		item.name = "EYES BIBE";
		item.price = "10000";
		item.type = "phone";
		item.url = "http://cloudfonemobile.com/uploads/products/2014-12-01/Ice_352e_front.png";
		item.width = 240;
		item.height = 408;
		item.Description = "Most Affordable 3.5 Smartphone in the Market";
		
		item.Unitcode = "PC";
		item.Prodcode = "PROD004";
		item.Imei = "";
		item.Colorcode = "DEF";
		item.Stockin = 3;
		item._discounted = "0";
		
		itemList.add(item);
		
		new AccessFromLocal().execute(itemList);
		
		
	}
// =========================================================================
// TODO Inner Class
// =========================================================================
    class Holder {
    	STGVImageView img_content;
        TextView tv_info;
        TextView tv_price;
        TextView tv_price_disc;
        
        Button btn_buy;
        Button btn_save;
        
        FrameLayout flCell;
    }
    
    private class AccessFromServer extends AsyncTask<String, String, ArrayList<Item>>{
    	
    	ArrayList<Integer> marks = new ArrayList<Integer>();
    	
    	public AccessFromServer(ArrayList<Integer> mark) {
			this.marks = mark;
		}
    	
		@Override
		protected ArrayList<Item> doInBackground(String... params) {
			ArrayList<Item> items = new ArrayList<Item>();
			
			String kioskid = Vars.KioskID(mContext, "", type.GET);
			items = JsonParser.GetAllData(params[0], kioskid);
			
			if(items.size() != 0){
				//
				new CloudfoneDB(mContext).openToWrite().insertPhoneItems(items, params[0]);
				
				for(int x = 0; x < marks.size(); x++){
					new CloudfoneDB(mContext).openToWrite().setMarkOnItem(marks.get(x));
				}
				items.clear();
				
				if(params[0].equals("phone")){
					items = new CloudfoneDB(mContext).openToRead().selectPhoneItems();
				}else if(params[0].equals("tablet")){
					items = new CloudfoneDB(mContext).openToRead().selectTabletItems();
				} else if(params[0].equals("accessory")){
					items = new CloudfoneDB(mContext).openToRead().selectAccessoryItems();
				}
			} 
			
			return items;
		}
		
		

		@Override
		protected void onPostExecute(ArrayList<Item> result) {
			
			super.onPostExecute(result);
			
			for(Item item : result){
				mItems.add(item);
			}
			
			notifyDataSetChanged();
		}
    }
    

    
    
	//
    private class AccessFromLocal extends AsyncTask<ArrayList<Item>, String, ArrayList<Item>>{

		@Override
		protected ArrayList<Item> doInBackground(ArrayList<Item>... params) {
			ArrayList<Item> items = new ArrayList<Item>();
			
			items = params[0];
			return items;
		}

		@Override
		protected void onPostExecute(ArrayList<Item> result) {
			super.onPostExecute(result);
			
			if(result.size() != 0){
				for(Item item : result){
					mItems.add(item);
					notifyDataSetChanged();
				}
			} else {
				Item item = new Item();
				item.name = "Null";
				mItems.add(item);
				notifyDataSetChanged();
			}
		}
    }
}
