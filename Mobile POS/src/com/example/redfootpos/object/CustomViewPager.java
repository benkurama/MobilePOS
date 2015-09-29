package com.example.redfootpos.object;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewPager extends ViewPager{
	
	private boolean isPagingEnabled = false;
	
	public CustomViewPager(Context context) {
		super(context);
		// 
	}

	public CustomViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		// 
		return isPagingEnabled && super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 
		return isPagingEnabled && super.onTouchEvent(event);
	}
	
	public void setPagingEnabled(boolean state){
		this.isPagingEnabled = state;
	}
}
