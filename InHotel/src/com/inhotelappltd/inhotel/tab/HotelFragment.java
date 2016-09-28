package com.inhotelappltd.inhotel.tab;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inhotelappltd.inhotel.R;

import com.inhotelappltd.inhotel.logger.Log;
import com.inhotelappltd.inhotel.server.ServerConnector;
import com.inhotelappltd.inhotel.utils.CommonActions;

public class HotelFragment extends Fragment {
	SharedPreferences prefs;
	Editor edt;
	Dialog dialogA;
	String acessCode,expreience="";
	int status;
	LinearLayout ll_main;
	ProgressDialog progress_dialogue;
	TextView tv_back,tv_title,tv_save,tv_hname,tv_hdiscription,tv_haddress,tv_phoneValue,tv_emailValue,tv_websiteValue;	
	EditText et_experience;
	ImageView iv_send;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v1 = inflater.inflate(R.layout.activity_hotel_fragment, container,
				false);

		prefs               = getActivity().getSharedPreferences("Login Credinals",Context.MODE_PRIVATE);
		edt                 = prefs.edit();
		tv_back				= (TextView) v1.findViewById(R.id.tv_back);
		tv_title			= (TextView) v1.findViewById(R.id.tv_title); 
		tv_save	    		= (TextView) v1.findViewById(R.id.tv_save); 
		tv_hname	    	= (TextView) v1.findViewById(R.id.tv_hname);
		tv_hdiscription	    = (TextView) v1.findViewById(R.id.tv_hdiscription);
		tv_haddress	    	= (TextView) v1.findViewById(R.id.tv_haddress);
		tv_phoneValue	    = (TextView) v1.findViewById(R.id.tv_phoneValue);
		tv_emailValue	    = (TextView) v1.findViewById(R.id.tv_emailValue); 
		tv_websiteValue	    = (TextView) v1.findViewById(R.id.tv_websiteValue); 
		iv_send             = (ImageView) v1.findViewById(R.id.iv_send); 
		et_experience       = (EditText)  v1.findViewById(R.id.et_experience);
		ll_main             = (LinearLayout) v1.findViewById(R.id.ll_main);
		tv_title.setText(R.string.htv1);
		
		Log.e("activation_status", prefs.getString("activation_status", "")+"dds"+prefs.getString("activation_code", ""));
		
		et_experience.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				  //Disable the parent Scrolling	  
				  v.getParent().requestDisallowInterceptTouchEvent(true);
				  v.setEnabled(true);				
				return false;
			}
		});
		
		tv_hdiscription.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Disable the parent Scrolling
				tv_hdiscription.setMovementMethod(new ScrollingMovementMethod());			
				v.getParent().requestDisallowInterceptTouchEvent(true);
				v.setEnabled(true);
				return false;
			}
		});
		
		

		iv_send.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {			
				expreience=et_experience.getText().toString();
				
				if(!expreience.equalsIgnoreCase("")){
					if (new CommonActions(getActivity()).isNetworkConnected()) {

						progress_dialogue = ProgressDialog.show(getActivity(), "Please wait","Loading");
						new AcessCodeTask().execute(2);
					} else {
						new CommonActions(getActivity()).showToast("Please check your internet connection");
					}
				}else{
					new AlertDialog.Builder(getActivity()).setTitle("Warning") 
					.setMessage("Please enter your experience.")
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					})

					.show();
				}		
				
			}
		});
		
		/*for (int i = 0; i < ll_main.getChildCount(); i++) {
		    View child = ll_main.getChildAt(i);
		    child.setEnabled(false);
		}*/
		if (prefs.getString("activation_status", "").equalsIgnoreCase("N")) {
			tv_hname.setText("My Hotel");
			iv_send.setEnabled(false);
			et_experience.setEnabled(false);
			set_Acesscode_Alert();
		}else{
			iv_send.setEnabled(true);
			et_experience.setEnabled(true);
			acessCode=prefs.getString("activation_code", "");
			
			if (new CommonActions(getActivity()).isNetworkConnected()) {
				progress_dialogue = ProgressDialog.show(getActivity(), "Please wait","Loading");
				new AcessCodeTask().execute(1);
			} else {
				new CommonActions(getActivity()).showToast("Please check your internet connection");
			}
		}
		return v1;
	}

	public void set_Acesscode_Alert() {

		dialogA = new Dialog(getActivity(),android.R.style.Theme_DeviceDefault_Light);
		dialogA.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogA.setCanceledOnTouchOutside(false);
		dialogA.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		dialogA.getWindow().clearFlags(LayoutParams.FLAG_DIM_BEHIND);
		dialogA.setContentView(getActivity().getLayoutInflater().inflate(
				R.layout.custom_acess_code, null));
		ImageView iv_close  = (ImageView) dialogA.findViewById(R.id.iv_close);
		
		iv_close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialogA.dismiss();
				hideKeyboard();
			}
		});
		final EditText et_code = (EditText) dialogA.findViewById(R.id.et_code);
		ImageView bt_continue = (ImageView) dialogA
				.findViewById(R.id.bt_continue);

		bt_continue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				acessCode = et_code.getText().toString();
				
				hideKeyboard();
				
				if(!et_code.getText().toString().equalsIgnoreCase("")){
					acessCode = et_code.getText().toString();
					if (new CommonActions(getActivity()).isNetworkConnected()) {

						progress_dialogue = ProgressDialog.show(getActivity(), "Please wait","Loading");
						new AcessCodeTask().execute(0);
					} else {
						new CommonActions(getActivity()).showToast("Please check your internet connection");
					}
				}else{
					new AlertDialog.Builder(getActivity()).setTitle("Warning") 
					.setMessage("Please enter the access code")
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					}).show();
				}		

			}

		});

		dialogA.show();

	}
	

	/**
	 * Task for downloading Menu management data
	 * 
	 * @author joyal
	 * 
	 */
	public class AcessCodeTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			status = params[0];
			if (status == 0) {
				return new ServerConnector(getActivity())
						.getResponseFromServer(CommonActions.appURL,
								"checkActivationCodeExpires",
								"{\"activation_code\": \"" + acessCode + "\",\"user_id\": \""
										+ prefs.getString("user_id", "")
										+ "\"}",
								"\"\"");

			}else if (status == 1) {
				return new ServerConnector(getActivity())
				.getResponseFromServer(CommonActions.appURL,
						"hotel_details",
						"{\"activation_code\": \"" + acessCode + "\"}",
						"\"\"");
			}else if (status == 2) {
			
				
				//try {
					expreience=URLEncoder.encode(expreience);	
				
					Log.e("EXPERINCE",expreience);
					return new ServerConnector(getActivity()) 
					.getResponseFromServer(CommonActions.appURL,
							"shareExperiance",
							"{\"activation_code\": \"" + acessCode + "\","
									+ "\"experiance\": \"" + expreience + "\",\"user_id\": \"" + prefs.getString("user_id", "") + "\"}",
							"\"\"");
				/*} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}*/
				
			} else {
				return null;
			}

		}

		@Override
		protected void onPostExecute(String result) {
			Log.e("result", result+"!!!");
			try {
				if (status == 0) {
					Log.e("result", result);

					JSONObject jobj = new JSONObject(result);
					progress_dialogue.cancel();

					edt.putString("activation_status",jobj.getString("activation_status"));
					edt.commit();
					if (jobj.getString("activation_status").equalsIgnoreCase("Y")) {
						iv_send.setEnabled(true);
						et_experience.setEnabled(true);
						
						edt.putString("activation_code", acessCode);
						edt.commit();
						
						new AlertDialog.Builder(getActivity())
								.setTitle("Message")
								.setMessage(jobj.getString("message"))
								.setPositiveButton("Ok",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.cancel();
												dialogA.cancel();
												acessCode=prefs.getString("activation_code", "");
												if (new CommonActions(getActivity()).isNetworkConnected()) {

													progress_dialogue = ProgressDialog.show(getActivity(), "Please wait","Loading");
													new AcessCodeTask().execute(1);
												} else {
													new CommonActions(getActivity()).showToast("Please check your internet connection");
												}
												
											}
										})

								.show();

					} else {
						edt.putString("activation_code", "");
						edt.commit();
						gotServerError(jobj.getString("message"));
					}
				}else if(status == 1){
					
					JSONObject jobj = new JSONObject(result);
					
					if(jobj.getString("status").equalsIgnoreCase("true")){
						JSONObject jobj_details=(JSONObject) jobj.get("details");
						
						tv_hname.setText(jobj_details.getString("title"));
						tv_hdiscription.setText(jobj_details.getString("description"));
						tv_haddress.setText(jobj_details.getString("address"));
						tv_phoneValue.setText(jobj_details.getString("contact_number"));
						tv_emailValue.setText(jobj_details.getString("email_address"));
						tv_websiteValue.setText(jobj_details.getString("website"));
						
					
					}
					
					progress_dialogue.cancel();
				}else if(status == 2){
					
					JSONObject jobj = new JSONObject(result);
					progress_dialogue.cancel();
					new AlertDialog.Builder(getActivity())
					.setTitle("Message")
					.setMessage(jobj.getString("message"))
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialog,
										int which) {
									dialog.cancel();
									et_experience.setText("");
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
		new AlertDialog.Builder(getActivity()).setTitle("Message")
				.setMessage(msg)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})

				.show();
	}
	private void hideKeyboard() {   
	    // Check if no view has focus:
	  View view = ((Activity) getActivity()).getCurrentFocus();
	   if (view != null) {
	       InputMethodManager inputManager = (InputMethodManager) ((Activity) getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
	       inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	        
	        ((Activity) getActivity()).  getWindow().setSoftInputMode(
	        	    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
}
