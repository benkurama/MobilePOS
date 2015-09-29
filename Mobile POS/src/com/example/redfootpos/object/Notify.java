package com.example.redfootpos.object;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Notify {
	
	public NotificationManager notification;
	
	public static final int NOTIFICATION_ID = 1;

	@SuppressWarnings("deprecation")
	public Notify(Context core, String title, String msg){
		
		notification = (NotificationManager)core.getSystemService(Context.NOTIFICATION_SERVICE);
		
		Notification notify= null;
		PendingIntent startIntent = null;
		//
		notify = new Notification(android.R.drawable.ic_dialog_email,"MobilePOS Email Status", System.currentTimeMillis());
		startIntent = PendingIntent.getActivity(core.getApplicationContext(), 0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
		
		notify.flags |= Notification.DEFAULT_LIGHTS |  Notification.FLAG_AUTO_CANCEL;
		//
		
		notify.setLatestEventInfo(core.getApplicationContext(), title, msg, startIntent);
		
		notification.notify(NOTIFICATION_ID, notify);
	}
	
	public void cancelNotify(){
		notification.cancel(NOTIFICATION_ID);
	}
}
