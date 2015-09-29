package com.example.redfootpos.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JsonParsing {
	 // =========================================================================
	 // TODO Variables
	 // =========================================================================	
		public InputStream is = null;
		public JSONObject jObj = null;
		public String  json = "";

	 // =========================================================================
	 // TODO Constructor 1
	 // =========================================================================		
		public JsonParsing(){
			json = "";
		}
	 // =========================================================================
	 // TODO Constructor 2
	 // =========================================================================		
		public JSONObject getJSONFromUrl(String url,String method,List<NameValuePair> params) {
			
	 // =========================================================================
	 // TODO Funtion Get
	 // =========================================================================		
		if(method == "GET"){
			
			 // Making HTTP request
	        try {
	            // defaultHttpClient
	            DefaultHttpClient httpClient = new DefaultHttpClient();
	            HttpPost httpPost = new HttpPost(url);
	 
	            HttpResponse httpResponse = httpClient.execute(httpPost);
	            HttpEntity httpEntity = httpResponse.getEntity();
	            is = httpEntity.getContent();           
	 
	        } catch (UnsupportedEncodingException e) {
	            e.printStackTrace();
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	 // =========================================================================
	 // TODO Function Post
	 // =========================================================================
		if(method == "POST"){
			
			try {
				
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
			
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("ERROR", "ERROR IN POSTING");
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		// =========================================================================
	    try {
	    	
	        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
	        StringBuilder sb = new StringBuilder();
	        String line = null;
	        
	        while ((line = reader.readLine()) != null) {
	        	// -- bekurama added codes 
	        	if(true){
	        		sb.append(line);
	        	} else {
	        		sb.append("false");
	        		break;
	        	}
	            
	        }

	        json = sb.toString();
	       
	    } catch (Exception e) {
	    	
	        Log.e("Buffer Error", "Error converting result " + e.toString());
	    }
	    // try parse the string to a JSON object
	    //--
	    try {
	    	
	        jObj = new JSONObject(json);
	     
	    } catch (JSONException e) {
	    	
	        Log.e("JSON Parser", "Error parsing data " + e.toString()); 
	    }
	        return jObj;
		}
	 // =========================================================================
	 // TODO Final Destination
}
