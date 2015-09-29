package com.example.redfootpos.object;

import java.util.UUID;

import paypal.payflow.PayflowAPI;
import paypal.payflow.SDKProperties;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;

import com.example.redfootpos.MainAct;

public class PaypalPay {
	
	private MainAct core;
	
	@SuppressWarnings("static-access")
	private ProgressDialogFragment prog = new ProgressDialogFragment().newInstance("Loading", "Payment is process...");
	
	public PaypalPay(MainAct core, String dataSwipe){
		this.core = core;
		//
		prog.show( ((MainAct)this.core).getSupportFragmentManager(), "progress");
		
		new PaymentGateway().execute(dataSwipe);
	}
	
	private class PaymentGateway extends AsyncTask<String, String, String>{
		
		@Override
		protected String doInBackground(String... params) {
			
			SDKProperties.setHostAddress("pilot-payflowpro.paypal.com");
			SDKProperties.setHostPort(443);
			SDKProperties.setTimeOut(45);
			//
			PayflowAPI pa = new PayflowAPI();
			
			String request = "USER=benkurama&VENDOR=benkurama&PARTNER=PayPal&PWD=Redfoot123_" +
					"&TRXTYPE=S&TENDER=C" +
					"&SWIPE="+params[0]+		
					"&FIRSTNAME=Red&LASTNAME=Foot&STREET=123 Main St.&ZIP=12345"+
					"&INVNUM=INV12345&PONUM=PO12345" +
					"&AMT=2.00";
			
			UUID uid = UUID.randomUUID();
			
	        String response = pa.submitTransaction(request, uid.toString());
	        
	        String transErrors = pa.getTransactionContext().toString();
	        if (transErrors != null && transErrors.length() > 0) {
	            response = transErrors;
	        }
	        
	        core.startService(new Intent(core, SendMailService.class));
			
			return response;
		}
		
		@Override
		protected void onPostExecute(String result) {
			//
			prog.dismiss();
			core.pager.setCurrentItem(0);
			core.setTitle("Home");
			
			super.onPostExecute(result);
		}
		
	}
}
