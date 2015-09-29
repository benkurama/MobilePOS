package com.example.redfootpos.pager;

import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.R;
import com.example.redfootpos.database.CloudfoneDB;
import com.example.redfootpos.model.Item;
import com.example.redfootpos.utils.Utils;
import com.squareup.picasso.Picasso;

public class ItemPager {
	
	private View view;
	private static FragmentActivity act;
	private static MainAct core;
	
	private static TextView tvPriceTag;
	private static ImageView ivItemImage;
	private static TextView tvItemName;
	private static TextView tvItemStocks;
	private static Button btnBuyItem, btnSaveItem;
	private static Item item;
	private static TextView tvDesc;
	
	public ItemPager(){}
	
	public ItemPager(View v, FragmentActivity act, MainAct core){
		this.view = v;
		this.act = act;
		this.core = core; 
	}
	
	public void initialize(){
		
		tvPriceTag = (TextView)view.findViewById(R.id.tvPriceTag);
		ivItemImage = (ImageView)view.findViewById(R.id.ivItemImage);
		tvItemName = (TextView)view.findViewById(R.id.tvItemName);
		btnBuyItem = (Button)view.findViewById(R.id.btnBuyItem);
		btnSaveItem = (Button)view.findViewById(R.id.btnSaveItem);
		tvDesc = (TextView)view.findViewById(R.id.tvItemDesc);
		tvItemStocks = (TextView)view.findViewById(R.id.tvItemStocks);
		//
		btnBuyItem.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				core.pager.setCurrentItem(2);
				//core.getActionBar().setDisplayHomeAsUpEnabled(false);
				core.setTitle("Buy Items");
				BuyPager.setProperties(ItemPager.item);
			}
		});
		
		
	}
	
	public static void setProperties(final Item item){
		
		String selPrice = Utils.me.PriceCompare(item.price, item._discounted);
		
		int price = Utils.me.CurrencyStringToInt(selPrice);
		String priceStr = Utils.me.FormatCurrencyToString(price, "PHP ");
		
		tvPriceTag.setText(priceStr);
		tvItemName.setText(item.name);
		tvDesc.setText(item.Description);
		tvItemStocks.setText("Available Stocks: "+ item.Stockin);
		
		Picasso.with(act).load(item.url).into(ivItemImage);
		//
		
		//
		ItemPager.item = item;
		
		if(item.mark == 1){
			btnSaveItem.setText("EditList");
			btnSaveItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					callManageEdit();
				}
			});
		} else {
			btnSaveItem.setText("Save");
			
			btnSaveItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					btnSaveItem.setText("EditList");
					new CloudfoneDB(core).openToWrite().setMarkOnItem(item.ID);
					btnSaveItem.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							callManageEdit();
						}
					});
				}
			});
		}
	}
	
	private static void callManageEdit(){
		core.mDrawerLayout.openDrawer(Gravity.RIGHT);
	}
}
