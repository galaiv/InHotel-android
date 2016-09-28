package com.inhotelappltd.inhotel;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.inhotelappltd.inhotel.R;

import com.inhotelappltd.inhotel.gcm.PlayServicesHelper;
import com.inhotelappltd.inhotel.http.MultipartEntityBuilder;
import com.inhotelappltd.inhotel.logger.Log;
import com.inhotelappltd.inhotel.logger.LogWrapper;
import com.inhotelappltd.inhotel.server.ServerConnector;
import com.inhotelappltd.inhotel.utils.CommonActions;
import com.google.android.gms.gcm.GoogleCloudMessaging;



public class SplashScreenActivity extends Activity {
	RelativeLayout  rl_credentials;
	TextView 		tv_signup,tv_forgot; 
	ImageView       bt_login;	
	Animation 		animFadein;
	EditText		et_username,et_password;
	String 			username="",password="",type="";
	CommonActions   obj_common;	
	int             status;
	SharedPreferences prefs;
	Editor          edt; 
	String          regid = "",splash="";	
	GoogleCloudMessaging gcm;
	ProgressDialog  progress_dialogue ;
	MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
	PlayServicesHelper playServicesHelper;
	public static boolean flag_alert= false;
	
	//bt_frgt_send  et_frgt_email 
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //progress_dialogue = new ProgressDialog(SplashScreenActivity.this,android.R.style.Theme_Material_Light_Dialog_Alert);			
        progress_dialogue = new ProgressDialog(SplashScreenActivity.this);			
        
        
         tv_signup		=	(TextView) 		 findViewById(R.id.tv_signup);      
        tv_forgot       =	(TextView) 		 findViewById(R.id.  tv_forgot);       
		rl_credentials	=	(RelativeLayout) findViewById(R.id.rl_credentials);	
		bt_login		= 	(ImageView) 	 findViewById(R.id.bt_login);	
		et_username		=   (EditText)		 findViewById(R.id.et_username);
		et_password		=   (EditText)		 findViewById(R.id.et_password);
		animFadein		=   AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
		obj_common		=   new CommonActions(this);
		prefs           =   getSharedPreferences("Login Credinals", MODE_PRIVATE);		
		edt 			=   prefs.edit();
		String text 	=   "<font color=#9ac5bd>Don't have an account </font> <font color=#ffffff><b>Sign up</b></font>";
		
		splash          =  prefs.getString("splash", "");
		tv_signup.setText(Html.fromHtml(text));
		
		gcm = GoogleCloudMessaging.getInstance(this);
		regid = getRegistrationId(this);
	
		if (regid.isEmpty()) {
			if (obj_common.isNetworkConnected()) {
				new GetGCMIDTask().execute();
			} else {
				Toast.makeText(this,
						"Make sure that you are connected to internet.",
						Toast.LENGTH_LONG).show();
			}
		}
		
		
		tv_forgot.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startActivity(new Intent(SplashScreenActivity.this,ForgotPAsswordActivity.class)); 
			
				
			}
		});
		
		
        
		LogWrapper logWrapper = new LogWrapper();
		Log.setLogNode(logWrapper);
		
		
		if(! prefs.getString("username", "").equalsIgnoreCase("") && ! prefs.getString("password", "").equalsIgnoreCase("")){
			username=prefs.getString("username", "");
			password=prefs.getString("password", "");
			
			startActivity(new Intent(SplashScreenActivity.this,AcessCodeActivity.class)); 
			finish();
			//doLogin();
		}
		Log.e("splash splash splash", splash);
		if(splash.equalsIgnoreCase("NO")){
			rl_credentials.setVisibility(View.VISIBLE);
			
			rl_credentials.startAnimation(animFadein);
			
			edt.putString("username", obj_common.getTextFrom(et_username));
			edt.putString("password", obj_common.getTextFrom(et_password));
			
			
			
		}else{
			// Creating a delay of 3 seconds
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {	
					rl_credentials.setVisibility(View.VISIBLE);
					rl_credentials.startAnimation(animFadein);						
				
					edt.putString("splash", "NO");
					edt.commit();
				
				}
			}, 3000);
		}
		
		
		
		bt_login.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				/*et_username.setText("devin@mail.com");
				et_password.setText("123456"); */                                                                                 
				
			/*	et_username.setText("testing4@mail.com");
				et_password.setText("123456");*/
				
			 /*  et_username.setText("pete@mail.com");
				et_password.setText("123456");	*/
				
				/*et_username.setText("anu@newagesmb.com");
			      et_password.setText("123456");*/	
				
				/*et_username.setText("guest2@hotel.com");
				et_password.setText("123456");*/
				
				username =  obj_common.getTextFrom(et_username);
				password =  obj_common.getTextFrom(et_password);
				
			    
				
				if(obj_common.getTextFrom(et_username).equalsIgnoreCase("")){
					  AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashScreenActivity.this);
					  
					  alertDialog .setTitle("Warning"); 
					alertDialog.setMessage("Please enter your email");
					alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					})

					.show();
				}else if(obj_common.getTextFrom(et_password).equalsIgnoreCase("")){
					new AlertDialog.Builder(SplashScreenActivity.this).setTitle("Warning")
					.setMessage("Please enter your password")
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) { 
							dialog.cancel();
						}
					})

					.show();
				}else{
					doLogin();
				}
				
			}
		});
		
		tv_signup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				startActivity(new Intent(SplashScreenActivity.this,RegistrationActivity.class)); 
				finish();
			
			}
		});
	}
    
    public void doLogin(){
    	type="login";
		mpEntity.addTextBody("username", username);
		mpEntity.addTextBody("password", password);
		mpEntity.addTextBody("gcm_id", regid);
		mpEntity.addTextBody("member_type", "1");
		
		if (new CommonActions(SplashScreenActivity.this).isNetworkConnected()) {
			
			progress_dialogue.setMessage("Loading...");
			progress_dialogue.show();
			
			new LoginTask().execute(0);
		} else {
			new CommonActions(SplashScreenActivity.this).showToast("Please check your internet connection");
		}
	
    }
    
    /**
	 * Task for downloading Menu management data
	 * 
	 * @author joyal
	 * 
	 */
	public class LoginTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected String doInBackground(Integer... params) {
			
			
			// TODO Auto-generated method stub
			status = params[0];
			if (status == 0) {
				
				return new ServerConnector(SplashScreenActivity.this)
				.doRegistration(type, mpEntity);
			} else {
				return null;
			}

		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (status == 0) {
					Log.e("result", result);

					JSONObject jobj=new JSONObject(result);

					if(jobj.getString("status").equalsIgnoreCase("true")){
						JSONObject jobj_details=(JSONObject) jobj.get("details");
						Log.e("user_id", jobj_details.getString("user_id"));
						
						edt.putString("user_id", jobj_details.getString("user_id"));
						edt.putString("username", obj_common.getTextFrom(et_username));
						edt.putString("password", obj_common.getTextFrom(et_password));
						edt.putString("fullname", jobj_details.getString("first_name")+" "+jobj_details.getString("last_name"));
						edt.putString("email", jobj_details.getString("email_address"));
						edt.putString("mem_type", jobj_details.getString("member_type"));
						edt.putString("activation_code", jobj.getString("activation_code"));
						edt.putString("enter", jobj_details.getString("enter_to_send"));						
						edt.putString("activation_code", jobj.getString("activation_code"));
						//
						edt.commit();						
						
						progress_dialogue.cancel();
						startActivity(new Intent(SplashScreenActivity.this,AcessCodeActivity.class)); 
						finish();
					}else{
						progress_dialogue.cancel();
						
						edt.putString("activation_status", jobj.getString("activation_status"));
						edt.putString("activation_code", jobj.getString("activation_code"));
						edt.commit();
						gotServerError(jobj.getString("message"));
					}

					
				}
			
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				gotServerError("Database error. Try again");
			}
			
			
			
		}
	}
	
	
	//.................................FOR GCM...................................
	/**
	 * Gets the current registration ID for application on GCM service. If
	 * result is empty, the app needs to register. *
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {

		String registrationId = prefs.getString(CommonActions.PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.e("LOG", "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(CommonActions.PROPERTY_APP_VERSION,Integer.MIN_VALUE);
		int currentVersion = getAppVersion(this);
		if (registeredVersion != currentVersion) {
			Log.e("Log", "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * Registers the application with GCM servers asynchronously. Stores the
	 * registration ID and app versionCode in the application's shared
	 * preferences.
	 */
	public class GetGCMIDTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			String msg = "";
			try {
				if (gcm == null) {
					gcm = GoogleCloudMessaging.getInstance(SplashScreenActivity.this);
				}
				regid = gcm.register(CommonActions.GOOGLE_GCM_SENDER_ID);
				msg = "Device registered, registration ID=" + regid;
				Log.e("Device registered", msg);
				storeRegistrationId(SplashScreenActivity.this, regid);
			} catch (IOException ex) {
				msg = "Error :" + ex.getMessage();
				Log.e("Device registered", msg);
			}
			return regid;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		int appVersion = getAppVersion(context);		
		edt.putString(CommonActions.PROPERTY_REG_ID, regId);
		edt.putInt(CommonActions.PROPERTY_APP_VERSION, appVersion);
		edt.commit();
	}
	
	/*
	 * Method to manage the exeption occured due to server down
	 */
	public void gotServerError(String msg) {
		if(progress_dialogue.isShowing()){
			progress_dialogue.cancel();
		}
		
		new AlertDialog.Builder(SplashScreenActivity.this).setTitle("Message")
				.setMessage(msg)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})

				.show();
	}
	
	
}
