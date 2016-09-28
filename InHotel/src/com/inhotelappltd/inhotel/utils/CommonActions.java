package com.inhotelappltd.inhotel.utils;
/*
 * Created by Joyal Reji
 * joyal@newagesmb.com
 * India
 */


import android.content.Context;
import android.net.ConnectivityManager;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

public class CommonActions {
	Context con;
	String value="";
	
	//STAGING URLS
	//public static String appURL="http://192.168.1.254/inhotel/client";	
	//public static String appURL="http://newagesme.com/inhotel/hotel_service";	
	public static String appURL="http://inhotel-app.com/hotel_service";
	public static String actionbarColor="";
	
	
	// GCM CONSTANTS

	public static String GOOGLE_API_KEY = "AIzaSyBwN6RmQPIsbpCdqtWr_liIt6QRNdVCbM0";//"AIzaSyBu8DaOKTy-f009nxid5LU6z-TvcJU1-HE";	
	public static String GOOGLE_GCM_SENDER_ID = "1057344312217";//1057344312217 
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	public static final String PROPERTY_APP_VERSION = "appVersion";
	
	public static int MY_QUICKBLOX = 0;
	public static String USER_STATUS="NULL";

	
	 // In GCM, the Sender ID is a project ID that you acquire from the API console
    public static final String PROJECT_NUMBER = "1057344312217";


    public static final String GCM_NOTIFICATION = "InHotel";
    public static final String GCM_DELETED_MESSAGE = "Deleted messages on server: ";
    public static final String GCM_INTENT_SERVICE = "GcmIntentService";
    public static final String GCM_SEND_ERROR = "Send error: ";
    public static final String GCM_RECEIVED = "Received: ";

    public static final String NEW_PUSH_EVENT = "new-push-event";
	
	
	
	public CommonActions(Context con) {		
		this.con = con;
	}

	/**
	 * Method for showing toast
	 * 
	 * @param Message
	 */
	public void showToast(String Message) {
		Toast.makeText(con, Message, Toast.LENGTH_LONG).show();
	}

	/**
	 * Returns the extracted text from the edit text given
	 * 
	 * @param editText
	 * @return
	 */
	public String getTextFrom(EditText editText) {
		value=editText.getText().toString();
		if(value.equalsIgnoreCase("")){
			return "";
		}else{
			return value;
		}		
	}

	/**
	 * Method for checking valid email id
	 * 
	 * @param target
	 * @return
	 */
	public boolean isValidEmail(CharSequence target) {
		return !TextUtils.isEmpty(target)
				&& android.util.Patterns.EMAIL_ADDRESS.matcher(target)
						.matches();
	}
	
	/*
	 * Method for escaping single qoutes
	 */
	public String encode_toDB(String val){
		
		if (null != val && !val.equalsIgnoreCase("")) {
			value=val.replace("'", "?");
		}else{
			value=val;
		}
		return value;		
	}
	
	/*
	 * Method for escaping single qoutes
	 */
	public String decode_fromDB(String val){		
		
		if(null != val && !val.equalsIgnoreCase("")){
			value=val.replace("?", "'");
		}else{
			value=val;
		}		
		
		return value;		
	}

	/**
	 * 
	 * 
	 * Returns true if Internet is available
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) con
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		//NetworkInfo ni = cm.getActiveNetworkInfo();
		return (cm.getActiveNetworkInfo() != null);
	}
}
