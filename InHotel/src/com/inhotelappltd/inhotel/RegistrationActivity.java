package com.inhotelappltd.inhotel;

import java.util.List;

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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.inhotelappltd.inhotel.R;

import com.inhotelappltd.inhotel.common.PointerPopupWindow;
import com.inhotelappltd.inhotel.http.MultipartEntityBuilder;
import com.inhotelappltd.inhotel.logger.Log;
import com.inhotelappltd.inhotel.quickblox.ChatService;
import com.inhotelappltd.inhotel.quickblox.SystemUtils;
import com.inhotelappltd.inhotel.server.ServerConnector;
import com.inhotelappltd.inhotel.utils.CommonActions;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.model.QBUser;

public class RegistrationActivity extends Activity {

	EditText et_name, et_email, et_phone, et_password, et_Confirmpassword;
	ImageView bt_signup;
	int status;
	CommonActions obj_common;
	String type = "", regID = "";
	SharedPreferences prefs;
	Editor edt;
	int x = 0;
	ProgressDialog progress_dialogue;
	MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
	QBUser user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		setAnimation(true);
		 progress_dialogue = new ProgressDialog(RegistrationActivity.this,android.R.style.Theme_Material_Light_Dialog_Alert);			
		SplashScreenActivity.flag_alert= true;
		et_name = (EditText) findViewById(R.id.et_name);
		et_email = (EditText) findViewById(R.id.et_email);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_password = (EditText) findViewById(R.id.et_password);
		et_Confirmpassword = (EditText) findViewById(R.id.et_Confirmpassword);
		bt_signup = (ImageView) findViewById(R.id.bt_signup);
		obj_common = new CommonActions(this);
		prefs = getSharedPreferences("Login Credinals", MODE_PRIVATE);
		edt = prefs.edit();
		regID = prefs.getString("CommonActions.PROPERTY_REG_ID", "");

		edt.putString("splash", "No");
		edt.commit();
		Log.e("reg VregID", regID);

		et_phone.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
				
				if (start == 0) {
					x = 0;
					
				}				
			
				if (x == 0) {
					x++;
					hideKeyboard();
					toshow_phonePopup();
				}
				
				

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				
			}

			@Override
			public void afterTextChanged(Editable s) {

			}

		});

		/*
		 * PointerPopupWindow p = create();
		 * p.setAlignMode(PointerPopupWindow.AlignMode.AUTO_OFFSET);
		 * p.showAsPointer(v);
		 */

		bt_signup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (obj_common.getTextFrom(et_name).equalsIgnoreCase("")) {

					new AlertDialog.Builder(RegistrationActivity.this,android.R.style.Theme_Material_Light_Dialog_Alert)
							.setTitle("Warning")
							.setMessage("Please enter your name")
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
										}
									})

							.show();
				} else if (!obj_common.isValidEmail(et_email.getText())) {
					new AlertDialog.Builder(RegistrationActivity.this,android.R.style.Theme_Material_Light_Dialog_Alert)
							.setTitle("Warning")
							.setMessage("Please enter a valid email")
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
										}
									})

							.show();
				} /*else if (obj_common.getTextFrom(et_phone)
						.equalsIgnoreCase("")) {

					new AlertDialog.Builder(RegistrationActivity.this)
							.setTitle("Warning")
							.setMessage("Please enter your phone number")
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
										}
									})

							.show();

				}*/ else if (obj_common.getTextFrom(et_password)
						.equalsIgnoreCase("")) {

					new AlertDialog.Builder(RegistrationActivity.this,android.R.style.Theme_Material_Light_Dialog_Alert)
							.setTitle("Warning")
							.setMessage("Password must be atleast 6 characters")
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
										}
									})

							.show();

				} else if (obj_common.getTextFrom(et_password).length() < 6) {
					new AlertDialog.Builder(RegistrationActivity.this,android.R.style.Theme_Material_Light_Dialog_Alert)
							.setTitle("Warning")
							.setMessage("Password must be atleast 6 characters")
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
										}
									})

							.show();
				} else if (obj_common.getTextFrom(et_Confirmpassword)
						.equalsIgnoreCase("")) {
					new AlertDialog.Builder(RegistrationActivity.this,android.R.style.Theme_Material_Light_Dialog_Alert)
							.setTitle("Warning")
							.setMessage("Passwords are not matching")
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
										}
									})

							.show();
				} else if (!obj_common.getTextFrom(et_Confirmpassword).equals(
						obj_common.getTextFrom(et_password))) {
					new AlertDialog.Builder(RegistrationActivity.this,android.R.style.Theme_Material_Light_Dialog_Alert)
							.setTitle("Warning")
							.setMessage("Passwords are not matching")
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
										}
									})

							.show();
				} else {

					type = "registration";
					mpEntity.addTextBody("first_name",
							obj_common.getTextFrom(et_name));
					mpEntity.addTextBody("email_address",
							obj_common.getTextFrom(et_email));
					mpEntity.addTextBody("phone",
							obj_common.getTextFrom(et_phone));
					mpEntity.addTextBody("password",
							obj_common.getTextFrom(et_Confirmpassword));
					mpEntity.addTextBody("member_type", "1");
					mpEntity.addTextBody("gcm_id", regID);

					if (new CommonActions(RegistrationActivity.this)
							.isNetworkConnected()) {

						progress_dialogue.setMessage("Loading...");
						progress_dialogue.show();
						new RegistrationTask().execute(0);
					} else {
						new CommonActions(RegistrationActivity.this)
								.showToast("Please check your internet connection");
					}
				}

			}
		});

	}

	// EditTextWacther Implementation

	public void toshow_phonePopup() {
		//View v1 = (EditText) findViewById(R.id.et_phone);
		PointerPopupWindow p = create();
		p.setAlignMode(PointerPopupWindow.AlignMode.CENTER_FIX);
		p.showAsPointer(findViewById(R.id.et_password),-20,-20);
	}

	/**
	 * create a PointerPopupWindow as the original PopupWindow way
	 * 
	 * @return
	 */
	private PointerPopupWindow create() {
		// warning: you must specify the window width explicitly(do not use
		// WRAP_CONTENT or MATCH_PARENT)
		PointerPopupWindow p = new PointerPopupWindow(this, getResources()
				.getDimensionPixelSize(R.dimen.popup_width));
		TextView textView = new TextView(this);
		textView.setGravity(Gravity.CENTER);
		textView.setPadding(5, 10, 5, 10);
		textView.setText("The phone number is for security reasons and it won’t be published on the app");
		textView.setTextSize(16);
		textView.setTextColor(Color.WHITE);
		p.setContentView(textView);
		p.setBackgroundDrawable(new ColorDrawable(0xb3111213));
		p.setPointerImageRes(R.drawable.ic_popup_pointer);
		return p;
	}

	@Override
	public void onBackPressed() {

		setAnimation(false);

		startActivity(new Intent(RegistrationActivity.this,
				SplashScreenActivity.class));
		finish();
	}

	// Intent Transition Animation
	public void setAnimation(boolean flg) {

		if (flg) {
			// opening transition animations
			overridePendingTransition(R.anim.activity_open_translate,
					R.anim.activity_close_scale);

		} else {
			// closing transition animations
			overridePendingTransition(R.anim.activity_open_scale,
					R.anim.activity_close_translate);
		}

	}

	/**
	 * Task for downloading Menu management data
	 * 
	 * @author joyal
	 * 
	 */
	public class RegistrationTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			status = params[0];
			if (status == 0) {

				return new ServerConnector(RegistrationActivity.this)
						.doRegistration(type, mpEntity);
			}else if(status == 1){
				return new ServerConnector(RegistrationActivity.this)
				.getResponseFromServer(
						CommonActions.appURL,
						"updateQuickBloxId",
						"{\"quickblox_id\": \""
								+AcessCodeActivity.my_quickbloxID+ "\",\"user_id\": \""
							+ prefs.getString("user_id", "")
							+ "\"}", "\"\"");




				
			} else {
				return null;
			}

		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (status == 0) {
					Log.e("result", result);

					JSONObject jobj = new JSONObject(result);

					if (jobj.getString("status").equalsIgnoreCase("true")) {
						JSONObject jobj_details = (JSONObject) jobj
								.get("details");

						edt.putString("user_id",
								jobj_details.getString("user_id"));
						edt.putString("username",
								jobj_details.getString("email_address"));
						edt.putString("password",
								jobj_details.getString("password"));
						edt.putString("fullname",
								jobj_details.getString("first_name"));
						edt.putString("email",
								jobj_details.getString("email_address"));
						edt.putString("mem_type",
								jobj_details.getString("member_type"));
						edt.putString("enter", "0");
						edt.putString("activation_status",
								jobj.getString("activation_status"));
						edt.putString("activation_code",
								jobj.getString("activation_code"));

						edt.commit();
						
						//doQuickbloxRegistration();
						progress_dialogue.cancel();
						startActivity(new Intent(RegistrationActivity.this,
								AcessCodeActivity.class));
						finish();

						/**/
					} else {
						progress_dialogue.cancel();

						gotServerError(jobj.getString("message"));
					}

				}else if(status == 1){
					progress_dialogue.cancel();
					startActivity(new Intent(RegistrationActivity.this,
							AcessCodeActivity.class));
					finish();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				gotServerError("Database error. Try again");
			}

		}
	}

	/*
	 * Method to manage the exeption occured due to server down
	 */
	public void gotServerError(String msg) {
		if (progress_dialogue.isShowing()) {
			progress_dialogue.cancel();
		}
		new AlertDialog.Builder(RegistrationActivity.this,android.R.style.Theme_Material_Light_Dialog_Alert).setTitle("Message")
				.setMessage(msg)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})

				.show();
	}
	
	public void doQuickbloxRegistration(){
		
       ChatService.initIfNeed(this);
		
		SystemUtils.USER_LOGIN="inhotel_"+prefs.getString("user_id", "");
		SystemUtils.USER_PASSWORD="inhotel_" + prefs.getString("user_id", "") +"123";
		
		
		user = new QBUser();
		user.setLogin(SystemUtils.USER_LOGIN);
		user.setPassword(SystemUtils.USER_PASSWORD);  
		

		Log.e("register first","USERS"+SystemUtils.USER_LOGIN+ "****"+SystemUtils.USER_PASSWORD);
	
		ChatService.getInstance().register(user,new QBEntityCallbackImpl() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				super.onSuccess();
				
				Log.e("register first","my_quickbloxID"+AcessCodeActivity.my_quickbloxID);
				
				CommonActions.MY_QUICKBLOX=AcessCodeActivity.my_quickbloxID;
				if(CommonActions.MY_QUICKBLOX !=0){
					new RegistrationTask().execute(1);
				}
				
				
			}

			@Override
			public void onError(List errors) {
				// TODO Auto-generated method stub
				super.onError(errors);
				progress_dialogue.cancel();
				//doQuickbloxRegistration();
				Log.e("register","Suuu"+errors);
			}

		});
	
		
	}

	private void hideKeyboard() {
		// Check if no view has focus:
		View view = (RegistrationActivity.this).getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) (RegistrationActivity.this)
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}

		(RegistrationActivity.this).getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
}
