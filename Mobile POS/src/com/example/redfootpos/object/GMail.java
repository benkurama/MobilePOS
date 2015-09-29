package com.example.redfootpos.object;

import java.security.Security;
import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.example.redfootpos.MainAct;
import com.example.redfootpos.utils.Utils;

import android.content.Context;
import android.os.AsyncTask;


public class GMail extends javax.mail.Authenticator{
		// =========================================================================
		// TODO Variables
		// =========================================================================
		private String _user;
		private String _pass;
		
		private String[] _to;
		private String _from;
		
		private String _port;
		private String _sport;
		
		private String _host;
		
		private String _subject;
		private String _body;
		
		private boolean _auth;
		
		private boolean _debuggable;
		
		private Multipart _multipart;
		
		static {
			Security.addProvider(new JSSEProvider()); 
		}
		// =========================================================================
		// TODO Getter & Setter
		// =========================================================================
		public String getBody(){
			return _body;
		}
		public void setBody(String body){
			_body = body;
		}
		public void setTo(String[] toArr) {
			this._to = toArr;
		}
		public void setFrom(String string) {
			this._from = string;
		}
		public void setSubject(String string) {
			this._subject = string;
		}
		// =========================================================================
		// TODO Constructor
		// =========================================================================
		public GMail(){
			//
			_host = "smtp.gmail.com";
			_port = "465";
			_sport = "465";
			
			_user = "alvin.sison@redfoottech.com";
			_pass = "redfoot123_";
			
			_from = "benkurama@gmail.com";
			_subject = "";
			
			_debuggable = false;
			_auth = true;
			
			_multipart = new MimeMultipart();
			// There is something wrong with MailCap, javamail can not find a
			// handler for the multipart/mixed part, so this bit needs to be added.
			MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
			mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
			mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
			mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
			mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
			mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
			CommandMap.setDefaultCommandMap(mc);
		}
		
		public GMail(String user, String pass){
			this();
			_user = user;
			_pass = pass;
		}
		// =========================================================================
		// TODO Main Functions
		// =========================================================================
		public boolean send(Context core) throws Exception{
			Properties props = _setProperties();

			if (!_user.equals("") && !_pass.equals("") && _to.length > 0
					&& !_from.equals("") && !_subject.equals("")
					&& !_body.equals("")) {
				Session session = Session.getInstance(props, this);
				
				new SendTask(session, core).execute();

				return true;
			} else {
				return false;
			}
		}
		
		public void addAttachment(String filename) throws Exception{
			BodyPart messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(filename);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(filename);
			
			_multipart.addBodyPart(messageBodyPart);
		}
		// =========================================================================
		// TODO Implementation
		// =========================================================================
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			// TODO Auto-generated method stub
			return new PasswordAuthentication(_user, _pass);
		}
		// =========================================================================
		// TODO Sub Functions
		// =========================================================================
		private Properties _setProperties() {
			Properties props = new Properties();

			props.put("mail.smtp.host", _host);

			if (_debuggable) {
				props.put("mail.debug", "true");
			}

			if (_auth) {
				props.put("mail.smtp.auth", "true");
			}

			props.put("mail.smtp.port", _port);
			props.put("mail.smtp.socketFactory.port", _sport);
			props.put("mail.smtp.socketFactory.class",
					"javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.fallback", "false");

			return props;
		}
		// =========================================================================
		// TODO Inner Class
		// =========================================================================
		class SendTask extends AsyncTask<String, String, String>{
			
			private Session ses;
			private Context core;
			private Boolean isSend = true;
			
			
			public SendTask(Session ss, Context core){
				this.ses = ss;
				this.core = core;
			}

			@Override
			protected String doInBackground(String... params) {
				// 
				
				String emailRes = "Invoice receipt is sent.";
				try {
					MimeMessage msg = new MimeMessage(ses);
					
					msg.setFrom(new InternetAddress(_from));
					InternetAddress[] addressTo = new InternetAddress[_to.length];
					
					for (int i = 0; i < _to.length; i++) {
						addressTo[i] = new InternetAddress(_to[i]);
					}
					
					msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);
					msg.setSubject(_subject);
					msg.setSentDate(new Date());
					
					BodyPart messageBodyPart = new MimeBodyPart();
					messageBodyPart.setText(_body);
					_multipart.addBodyPart(messageBodyPart);
					
					msg.setContent(_multipart);
					
					Transport.send(msg);
				} catch (Exception e) {
					
					e.printStackTrace();
					isSend = false;
					emailRes = "Email send Failed";
				}
				
				return emailRes;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				
				if(isSend){
					new Notify(core,"Email send success", result);
				} else {
					new Notify(core,"Email send failed", result);
				}
			}
		}
}
