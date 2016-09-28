package com.inhotelappltd.inhotel.tab;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inhotelappltd.inhotel.R;

import com.inhotelappltd.inhotel.logger.Log;
import com.inhotelappltd.inhotel.quickblox.ChatService;
import com.inhotelappltd.inhotel.server.ServerConnector;
import com.inhotelappltd.inhotel.utils.CommonActions;
import com.inhotelappltd.inhotel.SplashScreenActivity;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallbackImpl;

public class SettingFragment extends Fragment {
	
	//tv_back  tv_title  tv_save tv_con_status cb_enter tv_last_status
	//cb_status  rl_acces_to_hotel  rl_password  rl_signout  rl_account
	int               status;
	TextView		  tv_back,tv_title,tv_save,tv_last_status;
	CheckBox          cb_status,cb_enter,cb_room;
	Dialog 			  dialog,dialogP;
	RelativeLayout    rl_acces_to_hotel,rl_password,rl_signout,rl_account,rl_blocked_users;
	SharedPreferences prefs;
	ProgressDialog    progress_dialogue;
	Editor            edt;
	String            newPass="",enter="",chat_status="Y",status_time="",acessCode="",room_enable="N";
	 QBChatService chatService;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View v1 = inflater.inflate(R.layout.frag_settings, container,false);
		
		tv_back				= (TextView) v1.findViewById(R.id.tv_back);
		tv_title			= (TextView) v1.findViewById(R.id.tv_title); 
		tv_save	    		= (TextView) v1.findViewById(R.id.tv_save); 
		tv_last_status      = (TextView) v1.findViewById(R.id.tv_last_status);	
		cb_status           = (CheckBox) v1.findViewById(R.id.cb_status); 
		cb_enter            = (CheckBox) v1.findViewById(R.id.cb_enter); 
		cb_room             = (CheckBox) v1.findViewById(R.id.cb_room);
		rl_password			= (RelativeLayout) v1.findViewById(R.id.rl_password); 
		rl_signout			= (RelativeLayout) v1.findViewById(R.id.rl_signout); 
		rl_account			= (RelativeLayout) v1.findViewById(R.id.rl_account); 
		rl_blocked_users    = (RelativeLayout) v1.findViewById(R.id.rl_blocked_users); 
		rl_acces_to_hotel	= (RelativeLayout) v1.findViewById(R.id.rl_acces_to_hotel);
		
		prefs               =  getActivity().getSharedPreferences("Login Credinals",Context.MODE_PRIVATE);
		edt 			    =  prefs.edit();
		
	
		tv_title.setText("Settings");
		
		if(SplashScreenActivity.flag_alert){
			updateSettings();
			SplashScreenActivity.flag_alert = false;
			new AlertDialog.Builder(getActivity())

			.setMessage("Please update your profile.")
			.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							FragmentTransaction transaction = getFragmentManager()
									.beginTransaction();
							transaction.replace(R.id.m_tabFrameLayout,
									new AccountFragment());
							transaction.addToBackStack("null");
							transaction.commit();
						}
					})
			.setNegativeButton("Not now", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.cancel();
				}
			})

			.show();
		}else{
			manageView();
		}
		
		
		return v1;
	}

	private void manageView() {
		//CHANGE PASSWORD
		
		rl_password.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialogP = new Dialog(getActivity(),android.R.style.Theme_DeviceDefault_Light); 
				dialogP.requestWindowFeature(Window.FEATURE_NO_TITLE); 
				dialogP.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				dialogP.getWindow().clearFlags(LayoutParams.FLAG_DIM_BEHIND);				
				dialogP.setContentView(getActivity().getLayoutInflater().inflate(R.layout.custom_change_password, null));
				
				ImageView bt_continue         = (ImageView) dialogP.findViewById(R.id.bt_continue);
				final EditText  et_opass	  = (EditText)	dialogP.findViewById(R.id.et_opass);
				final EditText  et_newpass	  = (EditText)	dialogP.findViewById(R.id.et_newpass);
				final EditText  et_confpass	  = (EditText)	dialogP.findViewById(R.id.et_confpass);
				final ImageView iv_close      = (ImageView) dialogP.findViewById(R.id.iv_close);
				
				iv_close.setOnClickListener(new OnClickListener() {					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialogP.dismiss();
					}
				});
				
				
				
				bt_continue.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						String oldPass=prefs.getString("password", "");
						Log.e("PASS",oldPass+"#####A");
						
						if(et_opass.getText().toString().equalsIgnoreCase("")){
							gotServerError("Old password required");
						}else if(!et_opass.getText().toString().equalsIgnoreCase(oldPass)){
							gotServerError("Old password is incorrect");
						}else if(et_newpass.getText().toString().equalsIgnoreCase("")){
							gotServerError("New password required");
						}else if(et_newpass.getText().toString().length() < 6){
							gotServerError("Password must be atleast 6 characters");
						}else if(et_confpass.getText().toString().equalsIgnoreCase("")){
							gotServerError("Confirm password required");
						}else if(!et_newpass.getText().toString().equalsIgnoreCase(et_confpass.getText().toString())){
							gotServerError("Passwords are not matching");
						}else{
							progress_dialogue = ProgressDialog.show(getActivity(), "Please wait","Loading");
							newPass=et_newpass.getText().toString();
							new SettingTask().execute(0);
						}
						
						
					}
				});
				
				//dialog.setContentView(alert);
				dialogP.show();
				
			}
			
		});
		
		
		//ACESS CODE
		rl_acces_to_hotel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				dialog = new Dialog(getActivity(),android.R.style.Theme_DeviceDefault_Light); 
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
				dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				dialog.getWindow().clearFlags(LayoutParams.FLAG_DIM_BEHIND);
				
				dialog.setContentView(getActivity().getLayoutInflater().inflate(R.layout.custom_acesscode_alert, null));
				/*LayoutInflater li = (LayoutInflater) getActivity().getSystemService(
						getActivity().LAYOUT_INFLATER_SERVICE);*/
				//View alert = li.inflate(R.layout.custom_acesscode_alert, null, false);
				ImageView bt_continue = (ImageView) dialog.findViewById(R.id.bt_continue);
				final EditText  et_code	  = (EditText)	dialog.findViewById(R.id.et_code);
				ImageView iv_close  = (ImageView) dialog.findViewById(R.id.iv_close);
				
				iv_close.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						hideKeyboard();
					}
				});
				
				bt_continue.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {					
						acessCode = et_code.getText().toString();
						hideKeyboard();
						if(!et_code.getText().toString().equalsIgnoreCase("")){
							acessCode = et_code.getText().toString();
							if (new CommonActions(getActivity()).isNetworkConnected()) {
								dialog.cancel();
								progress_dialogue = ProgressDialog.show(getActivity(), "Please wait","Loading");
								new SettingTask().execute(4);
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
							})

							.show();
						}
						
					}
				});
				
				//acessCode
				//dialog.setContentView(alert);
				dialog.show();
				
				
			}
		});
		
		rl_blocked_users.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent  intent = new Intent(getActivity(),BlockedUsersActivity.class);			
				startActivity(intent);
				
			}
		});
		
		rl_signout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			try{
				chatService = QBChatService.getInstance();
				chatService.logout(new QBEntityCallbackImpl() {
					 
				    @Override
				    public void onSuccess() {
				        // success
				 
				    	Log.e("CHAT",":********DESTROY");
				        chatService.destroy();
				    }
				 
				    @Override
				    public void onError(final List list) {
				    	Log.e("CHAT",":********ERRRRR"+list);
				    }
				});
			} catch (Exception e) {
				 e.printStackTrace();
			} 
				 
				
				
				if (new CommonActions(getActivity()).isNetworkConnected()) {

					progress_dialogue = ProgressDialog.show(getActivity(), "Please wait","Loading");
					new SettingTask().execute(1);
				} else {
					new CommonActions(getActivity()).showToast("Please check your internet connection");
				}
				
			}
		});
		cb_status.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(cb_status.isChecked()){
					tv_last_status.setText("Now");
					chat_status="Y";
				}else{
					//String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
					// textView is the TextView view that should display it
								
					
					DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					dateFormatter.setLenient(false);
					Date today = new Date();
					String s = dateFormatter.format(today);
					tv_last_status.setText(s);	
					chat_status="N";
				}
				start_settingsupdation();
			}
		});
		
		cb_room.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(cb_room.isChecked()){
					room_enable ="Y";
					
				}else{
					room_enable ="N";
				}
				start_settingsupdation();
			}
		});
		
		
		cb_enter.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(cb_enter.isChecked()){
					enter="1";
				}else{
					enter="0";
				}
				start_settingsupdation();
			}
			
		});
		rl_account.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.e("PASS", "#####A");
				FragmentTransaction transaction = getFragmentManager()
						.beginTransaction();
				transaction.replace(R.id.m_tabFrameLayout,
						new AccountFragment());
				transaction.addToBackStack("null");
				transaction.commit();

			}
		});
		
		
		if (new CommonActions(getActivity()).isNetworkConnected()) {

			progress_dialogue = ProgressDialog.show(getActivity(), "Please wait","Loading");
			new SettingTask().execute(2);
		} else {
			new CommonActions(getActivity()).showToast("Please check your internet connection");
		}
		
	}
	
	public void start_settingsupdation(){
		Log.e("start_settingsupdation",enter+"@@@@@"+chat_status);
		
		
		if (new CommonActions(getActivity()).isNetworkConnected()) {

			progress_dialogue = ProgressDialog.show(getActivity(), "Please wait","Loading");
			new SettingTask().execute(3);
		} else {
			new CommonActions(getActivity()).showToast("Please check your internet connection");
		}
	}
	
	/*
	 * Method to manage the exeption occured due to server down
	 */
	public void gotServerError(String msg) {
	
		new AlertDialog.Builder(getActivity()).setTitle("Message")
				.setMessage(msg)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})

				.show();
	}
	
	public void updateSettings(){
		
		if(enter.equalsIgnoreCase("1")){
			cb_enter.setChecked(true);
		}else{
			cb_enter.setChecked(false);
		}
		
		if(chat_status.equalsIgnoreCase("Y")){
			cb_status.setChecked(true);
		}else{
			cb_status.setChecked(false);
		}
		
		
		if(room_enable.equalsIgnoreCase("Y")){
			cb_room.setChecked(true);
		}else{
			cb_room.setChecked(false);
		}
		
		tv_last_status.setText(status_time);
		
		
		
	}
	
	 /**
	 * Task for downloading Menu management data
	 * 
	 * @author joyal
	 * 
	 */
	public class SettingTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			status = params[0];
			if (status == 0) {
			
				return new ServerConnector(getActivity())
				.getResponseFromServer(
						CommonActions.appURL,
						"change_password",
						"{\"user_id\": \""
								+ prefs.getString("user_id", "")
								+ "\",\"password\": \""
								+ newPass+ "\"}", "\"\"");

			}else 	if (status == 1) {
				return new ServerConnector(getActivity()).getResponseFromServer(
						CommonActions.appURL,
						"logout",
						"{\"user_id\": \""
								+ prefs.getString("user_id", "")
								+ "\",\"device_token\": \"\"}", "\"\"");
				
			}else 	if (status == 2) {
				return new ServerConnector(getActivity()).getResponseFromServer(
						CommonActions.appURL,
						"get_user_details",
						"{\"user_id\": \""
								+ prefs.getString("user_id", "")
								+ "\",\"activation_code\":\""+prefs.getString("activation_code", "")+"\"}", "\"\"");
			}else 	if (status == 3) {
				return new ServerConnector(getActivity()).getResponseFromServer(
						CommonActions.appURL,
						"updateDetails",
						"{\"user_id\": \""
								+ prefs.getString("user_id", "")
								+ "\",\"chat_status\": \""+chat_status+"\",\"enter_to_send\": \""+enter+"\","
										+ "\"room_enable\": \""+room_enable+"\"}", "\"\"");
			}else 	if (status == 4) {
				return new ServerConnector(getActivity())
				.getResponseFromServer(CommonActions.appURL,
						"checkActivationCodeExpires",
						"{\"activation_code\": \"" + acessCode + "\",\"user_id\": \""
								+ prefs.getString("user_id", "")
								+ "\"}",
						"\"\"");
				
			} else {
				return null;
			}

		}

		@Override
		protected void onPostExecute(String result) {
			
		try{
			if (status == 0) {
				progress_dialogue.cancel();
				JSONObject jobj=new JSONObject(result);
				
				new AlertDialog.Builder(getActivity()).setTitle("Message")
				.setMessage(jobj.getString("message"))
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						
						dialog.cancel();
					}
				})

				.show();
				
				if(jobj.getString("status").equalsIgnoreCase("true")){
					//
					edt.putString("password", newPass);
				}
			}else if(status == 1){
				progress_dialogue.cancel();
				JSONObject jobj=new JSONObject(result);
				
				
				/*	edt.putString("user_id", "");
					edt.putString("username", "");
					edt.putString("password","");
					edt.putString("fullname", "");
					edt.putString("email", "");
					edt.putString("mem_type", "");
					edt.putString("splash", "No");
					
					edt.putString("activation_status", "N");
					edt.putString("activation_code", "");
					
					edt.commit();*/
					
				edt.clear();
				edt.commit();     	
				
					
					startActivity(new Intent(getActivity(),SplashScreenActivity.class)); 
					getActivity().finish();
									
				
			}else if(status==2){
				
				JSONObject jobj=new JSONObject(result);

				if(jobj.getString("status").equalsIgnoreCase("true")){
					JSONObject jobj_details=(JSONObject) jobj.get("details");
					edt.putString("enter", jobj_details.getString("enter_to_send"));
					edt.commit();
					enter       = jobj_details.getString("enter_to_send");				
					chat_status  = jobj_details.getString("chat_status");
					status_time = jobj_details.getString("status_modified");
					room_enable = jobj_details.getString("room_enable");
					progress_dialogue.cancel();
					updateSettings();
				} 
			}else if(status==3){
				progress_dialogue.cancel();
				
				edt.putString("enter", enter);
				edt.commit();
				
				JSONObject jobj=new JSONObject(result);
				
				new AlertDialog.Builder(getActivity()).setTitle("Message")
				.setMessage(jobj.getString("message"))
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {						
						dialog.cancel();
					}
				})

				.show();
			}else if(status == 4){

				Log.e("result", result);

				JSONObject jobj = new JSONObject(result);
				progress_dialogue.cancel();

				edt.putString("activation_status",jobj.getString("activation_status"));
				edt.commit();
				if (jobj.getString("activation_status").equalsIgnoreCase("Y")) {					
					
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
											acessCode=prefs.getString("activation_code", "");
																						
										}
									})

							.show();

				} else {
					edt.putString("activation_code", "");
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
