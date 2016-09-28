package com.inhotelappltd.inhotel;

import org.json.JSONException;
import org.json.JSONObject;

import com.inhotelappltd.inhotel.R;

import com.inhotelappltd.inhotel.logger.Log;
import com.inhotelappltd.inhotel.server.ServerConnector;
import com.inhotelappltd.inhotel.utils.CommonActions;
import com.qb.gson.JsonObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ForgotPAsswordActivity extends Activity {
	RelativeLayout rl_resetPassword;
	TextView       tv_forgot;
	EditText       et_frgt_email;
	Button         bt_frgt_send;
	CommonActions  obj_common;
	int            status;
	SharedPreferences prefs;
	ProgressDialog progress_dialogue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);
		  obj_common       =  new CommonActions(this);
		  rl_resetPassword =  (RelativeLayout) findViewById(R.id.rl_resetPassword);
		  tv_forgot        =  (TextView) 		 findViewById(R.id.tv_forgot);
		  et_frgt_email    =  (EditText)       findViewById(R.id.et_frgt_email );
		  bt_frgt_send     =  (Button)         findViewById(R.id.bt_frgt_send);
		  prefs            =  getSharedPreferences("Login Credinals", MODE_PRIVATE);
		  bt_frgt_send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (!obj_common.isValidEmail(et_frgt_email.getText())) {
					new AlertDialog.Builder(ForgotPAsswordActivity.this)
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
				}else{
					progress_dialogue = ProgressDialog.show(ForgotPAsswordActivity.this, "Please wait",	"Loading");					
					new ForgotPasswordTask().execute(1);
				}
				
				
			}
		});
		  
		//
	}
	
	
	/**
	 * Task for downloading Menu management data
	 * 
	 * @author joyal
	 * 
	 */
	public class ForgotPasswordTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			status = params[0];
			if(status == 1){
				return new ServerConnector(ForgotPAsswordActivity.this)
				.getResponseFromServer(
						CommonActions.appURL,
						"forgotPassword",
						"{\"email_address\": \""
							+ et_frgt_email.getText().toString()
							+ "\"}", "\"\"");

			


				
			} else {
				return null;
			}

		}

		@Override
		protected void onPostExecute(String result) {
			try {
				 if(status == 1){
					progress_dialogue.cancel();
					JSONObject jobj = new JSONObject(result);
					
					
					new AlertDialog.Builder(ForgotPAsswordActivity.this).setTitle("Message")
					.setMessage(jobj.getString("message"))
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							finish();
						}
					})

					.show();
				
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
		new AlertDialog.Builder(ForgotPAsswordActivity.this).setTitle("Message")
				.setMessage(msg)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						
					}
				})

				.show();
	}
}
