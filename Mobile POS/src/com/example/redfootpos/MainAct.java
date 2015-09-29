package com.example.redfootpos;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Text;

import paypal.payflow.PayflowAPI;

import IDTech.MSR.XMLManager.StructConfigParameters;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView.OnQueryTextListener;

import com.example.redfootpos.Vars.type;
import com.example.redfootpos.adapter.NavDrawerListAdapter;
import com.example.redfootpos.database.CloudfoneDB;
import com.example.redfootpos.fragments.MainFragment;
import com.example.redfootpos.fragments.PeopleFragment;
import com.example.redfootpos.fragments.RetrieveSalesFragment;
import com.example.redfootpos.fragments.ShoppingCartFragment;
import com.example.redfootpos.fragments.UserInfoFragment;
import com.example.redfootpos.model.Item;
import com.example.redfootpos.object.CardData;
import com.example.redfootpos.object.GMail;
import com.example.redfootpos.object.ItemsDatabase;
import com.example.redfootpos.object.PaypalPay;
import com.example.redfootpos.object.ProfileDatabase;
import com.example.redfootpos.object.SendMailService;
import com.example.redfootpos.object.UniMagTopDialog;
import com.example.redfootpos.pager.BuyPager;
import com.example.redfootpos.pager.ItemPager;
import com.example.redfootpos.pager.ManageItemPager;
import com.example.redfootpos.utils.Utils;
import com.idtechproducts.unipay.UniPayReader;
import com.idtechproducts.unipay.UniPayReaderMsg;
import com.squareup.picasso.Picasso;

public class MainAct extends BaseActivity implements UniPayReaderMsg, LoaderCallbacks<Cursor>{
	// ---------------------------------------------------------------------
	// VARIABLES
	// ---------------------------------------------------------------------
	public ViewPager pager;
	
	private ProfileDatabase profileDatabase;
	
	private UniPayReader Unipay;
	private StructConfigParameters profile;
	private UniMagTopDialog dlgSwipeTopShow = null;
	
	private Handler handler;
	
	private boolean isUseAutoConfigProfileChecked = false;
	public boolean isReaderConnected = false;
	public boolean isAlreadySwipe = false;
	private boolean isWaitingForCommandResult = false;
	
	private String popupDialogMsg = null;
	private String strStatusInfo = null;
	private String strMSRData = null;
	
	private ImageView ivPhoneMark, ivTabletMark, ivAccessoryMark;
	
	private byte[] MSRData = null;
	// ---------------------------------------------------------------------
	// LifeCycles
	// ---------------------------------------------------------------------
	@Override
 	protected void onCreate(Bundle savedInstanceState) {
		//getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		// 
		super.onCreate(savedInstanceState);
		//
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		ivPhoneMark = (ImageView)findViewById(R.id.ivPhoneMark);
		ivTabletMark = (ImageView)findViewById(R.id.ivTabletMark);
		ivAccessoryMark = (ImageView)findViewById(R.id.ivAccessoryMark);
		
		//CloudfoneDB db = new CloudfoneDB(this);
		
		profileDatabase = new ProfileDatabase(this);
		profileDatabase.initializedDB();
		isUseAutoConfigProfileChecked = profileDatabase.getIsUseAutoConfigProfile();
		//////
		preparingDrawer();
		////// Temporary Commented
		//InitializeReader();
		
		handler = new Handler();
		//handler.postDelayed(readerCheck, 4000);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		
		if(Intent.ACTION_VIEW.equals(intent.getAction())){
			
				//Toast.makeText(this, "It works", Toast.LENGTH_SHORT).show();
			getSupportLoaderManager().restartLoader(0, null, this);
			
		}else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
		      String query = intent.getStringExtra(SearchManager.QUERY);
		      query = query+" item not found";
		      Toast.makeText(this, query, Toast.LENGTH_SHORT).show();
		}
		
		super.onNewIntent(intent);
	}
	// ---------------------------------------------------------------------
	// Overrides
	// ---------------------------------------------------------------------
	@Override
	protected int setPageFrag() {
		return 0;
	}

	@Override
	protected void onPause() {
		if (Unipay != null) {
			Unipay.stopSwipeCard();
		}
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		Unipay.release();
		profileDatabase.closeDB();
		super.onDestroy();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		
		Fragment frag = getSupportFragmentManager().findFragmentById(R.id.frame_container);
		
		if (frag instanceof MainFragment) {
			if (pager.getCurrentItem() == 0) {
				if (mDrawerToggle.onOptionsItemSelected(item)) {
					return true;
				}
			} else {

				pager.setCurrentItem(0);
				getActionBar().setDisplayShowHomeEnabled(true);
				this.setTitle("Home");

			} 
		} else{
			if (mDrawerToggle.onOptionsItemSelected(item)) {
				return true;
			}
		} 
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed() {
		//
		Fragment frag = getSupportFragmentManager().findFragmentById(R.id.frame_container);
		
		if (frag instanceof MainFragment) {
			int curPage = pager.getCurrentItem();
			if (curPage == 0) {
				//
				//super.onBackPressed();
				callExitDialog();
			} else {
				
				pager.setCurrentItem(0);
				getActionBar().setDisplayShowHomeEnabled(true);
				this.setTitle("Home");
			}
		} else {
			callExitDialog();
		} 
	}
	
	private OnPageChangeListener pageChange = new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int pos) {
			if(pos == 3 ){
				menuSearch.setVisible(false);
			} else {
				menuSearch.setVisible(true);
			}
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};
	// ---------------------------------------------------------------------
	// Main Functions
	// ---------------------------------------------------------------------
	public ViewPager getRootViewPager(){
		//
		pager = (ViewPager)findViewById(R.id.pager);
		pager.setOnPageChangeListener(pageChange);
		return pager;
	}

	public void InitializeReader(){
		//
		if (Unipay != null) {
			Unipay.unregisterListen();
			Unipay.release();
			Unipay = null;
		}
		//
		Unipay = new UniPayReader(this, this);
		Unipay.setVerboseLoggingEnable(true);
		Unipay.registerListen();
		
		String fileNameWithPath = getXMLFileFromRaw("idt_unimagcfg_default.xml");
		if (!isFileExist(fileNameWithPath)) 
			fileNameWithPath = null;
		
		
		if (isUseAutoConfigProfileChecked) {
			if (profileDatabase.updateProfileFromDB()) {
				profile  = profileDatabase.getProfile();
				Unipay.connectWithProfile(profile);
				//Toast.makeText(this, "AutoConfig profile has been loaded", Toast.LENGTH_SHORT).show();
			}
		} else {
			new UnipayAsync().execute(fileNameWithPath);
		}
	}
	
	private void prepareToSendCommand(int cmdID){
		//
		isWaitingForCommandResult = true;
		
		switch (cmdID) {
		case UniPayReaderMsg.cmdEnableSwipingMSRCard:
			strStatusInfo = "Enabling Swiping MSR Card, wait for response";
			break;
			
		case UniPayReaderMsg.cmdCancelSwipingMSRCard:
			strStatusInfo = "Canceling Swiping MSR Card, wait for response";
			break;
		default:
			break;
		}
		handler.post(doUpdateStatusInfo);
		
	}
	
	private void preparingDrawer(){
		
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.app_name, R.string.app_name){
			public void onDrawerClosed(View view){
				getActionBar().setTitle(getPageTitle());
			}
			
			public void onDrawerOpened(View drawerView){
				//getActionBar().setTitle(getTitle());
				String username = Vars.UserName(MainAct.this, "", type.GET);
				getActionBar().setTitle("User: "+username);
				//invalidateOptionsMenu();
				// Counter for Shopping Cart page
				int count = new CloudfoneDB(getBaseContext()).openToRead().getAllMarksToCount();
				NavDrawerListAdapter adap = (NavDrawerListAdapter) mDrawerList.getAdapter();	
				
				adap.setData(2).setCounterVisibility(true);
				adap.setData(2).setCount(count+"");
				// Counter for Retrieve Sales Page
				adap.setData(3).setCounterVisibility(true);
				int pCounter = Vars.ParkedCount(getBaseContext(),0,type.GET);
				adap.setData(3).setCount(pCounter+"");
				//
				mDrawerList.setAdapter(adap);
				mDrawerList.setItemChecked(NavPos, true);
				mDrawerList.setSelection(NavPos);
				
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				// TODO Auto-generated method stub
				super.onDrawerSlide(drawerView, slideOffset);
				
				if(slideOffset == 1){
					//
					if(drawerView instanceof LinearLayout){
						/////
						ArrayList<Item> list = new CloudfoneDB(getBaseContext()).openToRead().selectAllMarksByPhone();
						if(list.size() != 0){
							//
							Picasso.with(getApplicationContext()).load(list.get(0).url).into(ivPhoneMark);
							ivPhoneMark.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									//
									Fragment frag = getSupportFragmentManager().findFragmentById(R.id.frame_container);
									if (frag instanceof MainFragment) {
										pager.setCurrentItem(3);
										mDrawerLayout.closeDrawers();
										int PhoneMarks = 1;
										ManageItemPager.setProperties(PhoneMarks);
										turnPageToMisc();
									} 
								}
							});
						} else {
							ivPhoneMark.setImageResource(android.R.color.transparent);
							ivPhoneMark.setOnClickListener(null);
						}
						/////
						list = new CloudfoneDB(getBaseContext()).openToRead().selectAllMarksByTablet();
						if(list.size() != 0){
							//
							Picasso.with(getApplicationContext()).load(list.get(0).url).into(ivTabletMark);
							ivTabletMark.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									//
									Fragment frag = getSupportFragmentManager().findFragmentById(R.id.frame_container);
									if (frag instanceof MainFragment) {
										pager.setCurrentItem(3);
										mDrawerLayout.closeDrawers();
										int TabletMarks = 2;
										ManageItemPager.setProperties(TabletMarks);
										turnPageToMisc();
									} 
								}
							});
						} else {
							ivTabletMark.setImageResource(android.R.color.transparent);
							ivTabletMark.setOnClickListener(null);
						}
						/////
						list = new CloudfoneDB(getBaseContext()).openToRead().selectAllMarksByAccessory();
						if(list.size() != 0){
							//
							Picasso.with(getApplicationContext()).load(list.get(0).url).into(ivAccessoryMark);
							ivAccessoryMark.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									//
									Fragment frag = getSupportFragmentManager().findFragmentById(R.id.frame_container);
									if (frag instanceof MainFragment) {
										pager.setCurrentItem(3);
										mDrawerLayout.closeDrawers();
										int AccessoryMarks = 3;
										ManageItemPager.setProperties(AccessoryMarks);
										turnPageToMisc();
									} 
								}
							});
						} else {
							ivAccessoryMark.setImageResource(android.R.color.transparent);
							ivAccessoryMark.setOnClickListener(null);
						}
						/////
					}
					//
				} 
			}
			
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}
	// ---------------------------------------------------------------------
	// Sub Functions
	// ---------------------------------------------------------------------
	private String getXMLFileFromRaw(String fileName ){
		//the target filename in the application path
		String fileNameWithPath = null;
		fileNameWithPath = fileName;
	
		try {
			InputStream in = getResources().openRawResource(R.raw.idt_unimagcfg_default);
			int length = in.available();
			byte [] buffer = new byte[length];
			in.read(buffer);    	   
			in.close();
			this.deleteFile(fileNameWithPath);
			FileOutputStream fout = openFileOutput(fileNameWithPath, MODE_PRIVATE);
			fout.write(buffer);
			fout.close();
    	   
			// to refer to the application path
			File fileDir = this.getFilesDir();
			fileNameWithPath = fileDir.getParent() + java.io.File.separator + fileDir.getName();
			fileNameWithPath += java.io.File.separator+"idt_unimagcfg_default.xml";
	   	   
		} catch(Exception e){
			e.printStackTrace();
			fileNameWithPath = null;
		}
		return fileNameWithPath;
	}
	
	private boolean isFileExist(String path){
		
		if(path == null)
			return false;
		
		File file = new File(path);
		if(!file.exists())
			return false;
			
		return true;
	}
	
	private void doShowSwipeTopDlg(){
		if (dlgSwipeTopShow == null) {
			dlgSwipeTopShow = new UniMagTopDialog(this);
		}
		dlgSwipeTopShow.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		dlgSwipeTopShow.setTitle("Unipay Power Up");
		dlgSwipeTopShow.setContentView(R.layout.dlg_swipe_top_view);
		((TextView)dlgSwipeTopShow.findViewById(R.id.tvTitleTopDlg)).setText(popupDialogMsg);
		((Button)dlgSwipeTopShow.findViewById(R.id.btnCancelTopDlg)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cancelSwipeAction();
			}
		});
		dlgSwipeTopShow.show();
		
	}
	
	public void startSwipeCard(){
		
		if (Unipay != null) {
			if (!isWaitingForCommandResult) {
				if (Unipay.sendCommandEnableSwipingMSRCard()) {
					prepareToSendCommand(UniPayReaderMsg.cmdEnableSwipingMSRCard);
				}
			}
		}

	}
	
	private void cancelSwipeAction(){
		Unipay.stopSwipeCard();
		//
		if (Unipay.sendCommandCancelSwipingMSRCard()) {
			prepareToSendCommand(UniPayReaderMsg.cmdCancelSwipingMSRCard);
		}
		//
		if (dlgSwipeTopShow != null) {
			dlgSwipeTopShow.dismiss();
		}
	}
	
	private String getHexStringFromBytes(byte []data)
    {
		if(data.length<=0) 
			return null;
		StringBuffer hexString = new StringBuffer();
		String fix = null;
		for (int i = 0; i < data.length; i++) {
			fix = Integer.toHexString(0xFF & data[i]);
			if(fix.length()==1)
				fix = "0"+fix;
			hexString.append(fix);
		}
		fix = null;
		fix = hexString.toString();
		return fix;
    }
	
	private void hideSwipeTopDialog(){
		
		if (dlgSwipeTopShow != null) {
			dlgSwipeTopShow.hide();
			dlgSwipeTopShow.dismiss();
			dlgSwipeTopShow = null;
		}
		
		//BuyPager.tvSwipeStatus.setText(strStatusInfo);
	}
	
	private void ShowCardData(){
		//
		CardData card = new CardData(MSRData);
		Map<String,String> details = card.getDetails();
		String swipeData = card.getSwipeData();
		
		if(details != null){
			
			char[] numArr = details.get("accountnumber").toCharArray();
			String resCon = "";
			for (int i = 0; i < numArr.length; i++) {
				
				if (numArr.length - 4 > i) {
					resCon += "*";
				} else {
					resCon += numArr[i];
				}
			}
			
			BuyPager.tvSwipeStatus.setText("Swipe Success.");
			//
			BuyPager.tvSwipeAccount.setText(": "+resCon);
			BuyPager.tvSwipeName.setText(": "+details.get("accountname"));
			BuyPager.tvSwipeType.setText(": "+details.get("cardtype"));
			BuyPager.tvSwipeCvv.setText(": "+details.get("cvv"));
			BuyPager.TvSwipeDate.setText(": "+details.get("month")+"/"+details.get("year"));
			
		} else {
			BuyPager.tvSwipeStatus.setText("Swipe Again... cannot retrieve data from card");
		}
		//
		BuyPager.btnSwipeCheckout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PaymentTest();
			}
		});
	}
	
	public void PaymentTest(){
		
		PayPalPayment();
	}
	
	private void PayPalPayment(){
		//
		new PaypalPay(this, ";5105105105105100=15121011000012345678?");
	}
	
	private void turnPageToMisc(){
		this.getActionBar().setDisplayShowHomeEnabled(false);
        this.setTitle("Micellaneous");
	}
	
	private void callExitDialog(){
		Builder dial = new AlertDialog.Builder(this);
		
		dial.setTitle("Exit");
		dial.setMessage("Exit the POS App?");
		dial.setCancelable(false);
		dial.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.exit(0);
			}
		});
		dial.setNegativeButton("NO", null);
		dial.show();
	}
	
	// ---------------------------------------------------------------------
	// Runnables
	// ---------------------------------------------------------------------
	private Runnable doShowSwipeTopDllg = new Runnable() {
		
		@Override
		public void run() {
			doShowSwipeTopDlg();
		}
	};
	private Runnable doUpdateStatusInfo = new Runnable() {
		@Override
		public void run() {
			if (pager.getCurrentItem() == 2) {
				BuyPager.tvSwipeStatus.setText(strStatusInfo);
			}
		}
	};
	private Runnable doHideSwipeTopDlg = new Runnable() {
		@Override
		public void run() {
			hideSwipeTopDialog();
		}
	};
	private Runnable doShowCardData = new Runnable() {
		@Override
		public void run() {
			//
			ShowCardData();
		}
	};
	// ---------------------------------------------------------------------
	// Implementations
	// ---------------------------------------------------------------------
	@Override
	public boolean getUserGrant(int type, String msg) {
		// 
		switch(type)
		{
		case UniPayReaderMsg.typeToPowerupUniPay:
			//pop up dialog to get the user grant
			//Toast.makeText(this,"getUserGrant()1", Toast.LENGTH_SHORT).show();
			break;
		case UniPayReaderMsg.typeToUpdateXML:
			//pop up dialog to get the user grant
			//Toast.makeText(this,"getUserGrant()2", Toast.LENGTH_SHORT).show();
			break;
		case UniPayReaderMsg.typeToOverwriteXML:
			//pop up dialog to get the user grant
			//Toast.makeText(this,"getUserGrant()3", Toast.LENGTH_SHORT).show();
			break;
		case UniPayReaderMsg.typeToReportToIdtech:
			//pop up dialog to get the user grant
			//Toast.makeText(this,"getUserGrant()4", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
		return true;
	}
	///// -------------------------
	@Override
	public void onReceiveMsgAutoConfigCompleted(StructConfigParameters profile) {
		// 
	}

	@Override
	public void onReceiveMsgAutoConfigProgress(int progressVal) {
		// 
	}

	@Override
	public void onReceiveMsgAutoConfigProgress(int percent, double result, String profilename) {
		// 
	}
	///// -------------------------
	@Override
	public void onReceiveMsgCommandResult(int cmdID, byte[] cmdReturn) {
		// 
		isWaitingForCommandResult = false;
		//
		if (cmdReturn.length > 1) {
			if (cmdReturn[0] == 6 && cmdReturn[1] == (byte)0x56) {
				//
				strStatusInfo = "Failed to send command. Attached reader is in boot loader mode. Format:<"+getHexStringFromBytes(cmdReturn)+">";
				handler.post(doUpdateStatusInfo);
				return;
			}
		}
		//
		switch (cmdID) {
		/** COMMAND LIST OF MSR*/
		//MSR 01
		case UniPayReaderMsg.cmdEnableSwipingMSRCard:
			
			if (cmdReturn[0] == 0) {
				//->
				strStatusInfo = "Enable Swiping MSR Card timeout.";
				//-<
			} else if(cmdReturn[0] == 2 && cmdReturn[cmdReturn.length-1] == 3){
				///
				if (cmdReturn.length > 6) {
					short respLenght = cmdReturn[2];
					respLenght = (short) (((respLenght<<8)&0xff00)|cmdReturn[1]);
					
					if (cmdReturn.length == respLenght + 6) {
						if (cmdReturn[3] == 6) {
							///////->
							if (!isWaitingForCommandResult) {
								if (Unipay.startSwipeCard()) {
									//
									
								} else {
									Toast.makeText(this, "Cannot swipe", Toast.LENGTH_SHORT).show();
								}
							}
							///////-<
						} else if(cmdReturn[3] == 21){
							//->
							strStatusInfo = "Enable Swiping MSR Card Failed.Error Info: "+Unipay.getErrorCode(cmdReturn);
							//-<
						}
					} else {
						//->
						strStatusInfo = "Enable Swiping MSR Card Failed. Resp length error.";
						//-<
					}
				}
			}else {
				strStatusInfo = "Enabling Swiping MSR Card failed.";
			}
			
			break;
		//MSR 02
		case UniPayReaderMsg.cmdCancelSwipingMSRCard:
			
			if (cmdReturn[0] == 0) {
				strStatusInfo = "Cancel Swiping MSR Card timeout";
			} else if(cmdReturn[0] == 2 && cmdReturn[cmdReturn.length-1] == 3){
				///
				if (cmdReturn.length > 6) {
					short respLenght = cmdReturn[2];
					respLenght = (short) (((respLenght<<8)&0xff00)|cmdReturn[1]);
					
					if (cmdReturn.length == respLenght + 6) {
						if (cmdReturn[3] == 6) {
							///////->
							strStatusInfo = "Cancel Swiping MSR Card Succeed.";
							///////-<
						} else if(cmdReturn[3] == 21){
							strStatusInfo = "Cancel Swiping MSR Card Failed.Error Info: "+Unipay.getErrorCode(cmdReturn);
						}
					} else {
						strStatusInfo = "Enable Swiping MSR Card Failed. Resp length error.";
					}
				}
			} else {
				strStatusInfo = "Cancel Swiping MSR Card failed.";
			}
			break;

		default:
			break;
		}
		
		handler.post(doUpdateStatusInfo);
	}
	///// -------------------------
	@Override 
	public void onReceiveMsgToConnect() {
		// 
		Toast.makeText(this, "Unipay Connecting...", Toast.LENGTH_SHORT).show();
		if (pager.getCurrentItem() == 2) {
			BuyPager.tvSwipeStatus.setText("UniPay Audio Jack Detected, waiting for response...\nPlease wait...");
		}
	}
	@Override
	public void onReceiveMsgConnected() {
		// 
		if (menuSwipe != null) {
			menuSwipe.setIcon(R.drawable.swipe_enable);
		}
		Toast.makeText(this, "Unipay Connected", Toast.LENGTH_SHORT).show();
		//
		isReaderConnected = true;
		if (pager.getCurrentItem() == 2) {
			BuyPager.btnBuySwipeNow.setEnabled(true);
			BuyPager.tvSwipeStatus.setText("UniPay Connected.");
			
		}
		
	}
	@Override
	public void onReceiveMsgDisconnected() {
		//
		isReaderConnected = false;
		if (menuSwipe != null) {
			menuSwipe.setIcon(R.drawable.swipe_disable);
		}
		
		if (pager.getCurrentItem() == 2) {
			BuyPager.btnBuySwipeNow.setEnabled(false);
			BuyPager.tvSwipeStatus.setText("UniPay Disconnected.");
		}
		
		BuyPager.btnSwipeCheckout.setEnabled(false);
	}
	///// -------------------------
	@Override
	public void onReceiveMsgToSwipeCard() {
		// 
		isAlreadySwipe = false;
		popupDialogMsg = "Please swipe the card.";
		handler.post(doShowSwipeTopDllg);
	}
	@Override
	public void onReceiveMsgProcessingCardData() {
		// 
		strStatusInfo = "Card is being process. Please wait.";
		handler.post(doHideSwipeTopDlg);
	}
	@Override
	public void onReceiveMsgCardData(byte flagOfCardData, byte[] cardData) {
		// 
		byte flag = (byte) (flagOfCardData&0x04);
		//
		if(flag == 0x00){
			//
			if (flagOfCardData == 0x02 && cardData[2] == 0x15 && cardData[cardData.length-1] == 0x03) {
				//
				MSRData = null;
				byte[]cmdReturn = new byte[cardData.length +1];
				System.arraycopy(cardData, 0, cmdReturn, 1, cardData.length);
				cmdReturn[0] = flagOfCardData;
				strStatusInfo = "Timeout when swipe MSR card.\nError Info: " +Unipay.getErrorCode(cmdReturn)+"\n"+getHexStringFromBytes(cmdReturn);
				//
				handler.post(doHideSwipeTopDlg);
				handler.post(doUpdateStatusInfo);
			} else {
				//
				strMSRData = new String (cardData);
			}
		} else if (flag == 0x04){
			strMSRData = new String (cardData);
		}
		//
		isAlreadySwipe = true;
		BuyPager.btnSwipeCheckout.setEnabled(true);
		
		handler.post(doShowCardData);
		
		MSRData = new byte[cardData.length];
		System.arraycopy(cardData, 0, MSRData, 0, cardData.length);
	}
	///// -------------------------
	@Override
	public void onReceiveMsgFailureInfo(int index, String msg) {
		// 
		
	}
	@Override
	@Deprecated
	public void onReceiveMsgSDCardDFailed(String msg) {
		// 
		
	}
	@Override
	public void onReceiveMsgTimeout(String msg) {
		// 
	}
	///// -------------------------
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		
		Uri uri = getIntent().getData();
		return new CursorLoader(getBaseContext(), uri, null, null , null, null);
	}
	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cur) {
		
		Fragment frag = getSupportFragmentManager().findFragmentById(R.id.frame_container);
		
		if (frag instanceof MainFragment) {
			
			if (cur.moveToFirst()) {

				Item item = new Item();
				item.url = cur.getString(cur
						.getColumnIndex(CloudfoneDB.COL_URL));
				item.name = cur.getString(cur
						.getColumnIndex(CloudfoneDB.COL_NAME));
				item.width = cur.getInt(cur
						.getColumnIndex(CloudfoneDB.COL_WIDTH));
				item.height = cur.getInt(cur
						.getColumnIndex(CloudfoneDB.COL_HEIGHT));
				item.price = cur.getString(cur
						.getColumnIndex(CloudfoneDB.COL_PRICE));
				item.Description = cur.getString(cur
						.getColumnIndex(CloudfoneDB.COL_DESC));
				item.mark = cur.getInt(cur
						.getColumnIndex(CloudfoneDB.COL_MARK));
				item.type = cur.getString(cur
						.getColumnIndex(CloudfoneDB.COL_TYPE));
				item.ID = cur.getInt(cur
						.getColumnIndex(CloudfoneDB.COL_ID));
				
				item._discounted = cur.getString(cur
						.getColumnIndex(CloudfoneDB.COL_DISCOUNTED));
				
				item.Imei = cur.getString(cur
						.getColumnIndex(CloudfoneDB.COL_IMEI));
				
				item.Stockin = cur.getInt(cur
						.getColumnIndex(CloudfoneDB.COL_STOCKIN));
				//
				ItemPager.setProperties(item);
				pager.setCurrentItem(1);

				this.getActionBar().setDisplayShowHomeEnabled(false);
				this.setTitle("Item");

				searchView.onActionViewCollapsed();
				//
			}
		}
	}
	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		
	}
	// ---------------------------------------------------------------------
	// Inner Class
	// ---------------------------------------------------------------------
	private class UnipayAsync extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... params) {
			Unipay.setXMLFileNameWithPath(params[0]);
			Unipay.loadingConfigurationXMLFile(true);
			return null;
		}
		
	}

}
