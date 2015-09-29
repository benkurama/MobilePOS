package com.example.redfootpos.object;


import android.app.IntentService;
import android.content.Intent;

public class SendMailService extends IntentService{
	
	public static final String EMAILADD = "Email";
	public static final String USER = "User";
	public static final String INVNO = "Invno";
	public static final String TOTALAM = "Totalam";
	public static final String ATTACHMENT_PATH = "filepath";

	public SendMailService() {
		super("SendMailService");
		// 
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		//
		String email = intent.getExtras().getString(EMAILADD);
		String user = intent.getExtras().getString(USER);
		String invno = intent.getExtras().getString(INVNO);
		String totalam = intent.getExtras().getString(TOTALAM);
		String filepath = intent.getExtras().getString(ATTACHMENT_PATH);
		// 
		//GMail mail = new GMail("alvin.sison@redfoottech.com","redfoot123_");
		GMail mail = new GMail("mpos@redfoottech.com","red1234foot");
        //mail.setTo(new String[]{"alvin.sison@redfoottech.com"});
		mail.setTo(new String[]{email});
        mail.setFrom("mpos@redfoottech.com");
        mail.setSubject("Receipt from Mobile POS");
        
        String msgText = "MobilePOS Payment Receipt from Agent "+user+" w/ Invoice # "+invno+", total amount of "+totalam;
        
        mail.setBody(msgText);
        
        try {
        	//
        	mail.addAttachment(filepath);
        	//
			mail.send(this);
		} catch (Exception e) {
			// 
			e.printStackTrace();
		}
        
	}

}
