package com.inhotelappltd.inhotel.tab;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.inhotelappltd.inhotel.R;

import com.inhotelappltd.inhotel.common.ChatUserAdapter;
import com.inhotelappltd.inhotel.logger.Log;
import com.inhotelappltd.inhotel.quickblox.ChatService;
import com.inhotelappltd.inhotel.quickblox.SystemUtils;
import com.inhotelappltd.inhotel.server.ServerConnector;
import com.inhotelappltd.inhotel.utils.CommonActions;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.model.QBUser;

public class ChatFragment extends Fragment {
	SharedPreferences prefs;
	Editor edt;
	Dialog dialog;
	String acessCode;
	int status;
	
	ProgressDialog progress_dialogue;
	Date d;
	ListView list;
	ChatUserAdapter adp;
	TextView tv_title;
	ArrayList<ArrayList<String>> arr_chatlist=new ArrayList<ArrayList<String>>();
	ArrayList<String> arr_details;
	
	ArrayList<String> arr_det_fnl=new ArrayList<String>();
	ArrayList<ArrayList<String>> arr_userlist_fnl =new ArrayList<ArrayList<String>>();	
	
	//tv_nodata

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v1     = inflater.inflate(R.layout.activity_chat_fragment, container,false);
		prefs       = getActivity().getSharedPreferences("Login Credinals",Context.MODE_PRIVATE);
		edt         = prefs.edit();
		progress_dialogue = new ProgressDialog(getActivity());	
		arr_chatlist= new ArrayList<ArrayList<String>>();
		adp         = new ChatUserAdapter(getActivity(), arr_userlist_fnl);	
		tv_title    = (TextView) v1.findViewById(R.id.tv_title); 
		list        = (ListView) v1.findViewById(R.id.list);
 		tv_title.setText("Messaging");
 		ChatService.initIfNeed(getActivity());
		
		

		list.setOnItemClickListener(new  OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {				
				
				Log.e("CHAT",position+"@@position@@"+arr_userlist_fnl.get(position).get(7));
				Bundle b = new Bundle();
				Intent  intent = new Intent(getActivity(),ConversationActivity.class);
				//AcessCodeActivity.chat  =  1;		 
				b.putString("profID", arr_userlist_fnl.get(position).get(0));
				b.putString("QUicktID",  arr_userlist_fnl.get(position).get(6));
				b.putString("page",  "chat");
				b.putString("type", arr_userlist_fnl.get(position).get(1));
				
				
				
				   edt.putString("type", arr_userlist_fnl.get(position).get(1));
				   edt.putString("profID", arr_userlist_fnl.get(position).get(0));
				   edt.putString("QUicktID", arr_userlist_fnl.get(position).get(6));
				   edt.putString("page", arr_userlist_fnl.get(position).get(1));
				   edt.commit();
				
				
				intent.putExtras(b);
				startActivity(intent);				
				
			}
		});

		
		return v1;
	}

	

	public void set_Acesscode_Alert() {

		dialog = new Dialog(getActivity(),android.R.style.Theme_DeviceDefault_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		dialog.getWindow().clearFlags(LayoutParams.FLAG_DIM_BEHIND);
		dialog.setContentView(getActivity().getLayoutInflater().inflate(
				R.layout.custom_acess_code, null));

		final EditText et_code = (EditText) dialog.findViewById(R.id.et_code);
		ImageView bt_continue = (ImageView) dialog
				.findViewById(R.id.bt_continue);
		ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);

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
			
				hideKeyboard();
				if(!et_code.getText().toString().equalsIgnoreCase("")){
					acessCode = et_code.getText().toString();
					if (new CommonActions(getActivity()).isNetworkConnected()) {
						dialog.cancel();
						progress_dialogue.setMessage("Loading...");
						progress_dialogue.show();
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
					})

					.show();
				}		

			}

		});

		dialog.show();

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

			} else if (status == 1) {
				return new ServerConnector(getActivity())
				.getResponseFromServer(CommonActions.appURL,
						"getAllChatUsers",
						"{\"user_id\": \""+ prefs.getString("user_id", "")+"\","
								+ "\"activation_code\": \"" + prefs.getString("activation_code", "") + "\",\"device_token\": \"\"}",
						"\"\"");
			}else{
				return null;
			}

		}

		@Override
		protected void onPostExecute(String result) {
			Log.e("result", result);
			try {
				if (status == 0) {
					Log.e("result", result);

					JSONObject jobj = new JSONObject(result);
					progress_dialogue.cancel();

					edt.putString("activation_status",jobj.getString("activation_status"));
					edt.commit();
					if (jobj.getString("activation_status").equalsIgnoreCase("Y")) {
						list.setEnabled(true);
						list.setClickable(true);
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
												if (new CommonActions(getActivity()).isNetworkConnected()) {

													progress_dialogue.setMessage("Loading...");
													progress_dialogue.show();
													new AcessCodeTask().execute(1);
												} else {
													new CommonActions(getActivity()).showToast("Please check your internet connection");
												}
											}
										})

								.show();

					} else {
						list.setEnabled(false);
						list.setClickable(false);
						edt.putString("activation_code", "");
						edt.commit();
						gotServerError(jobj.getString("message"));
					}
				}else if (status == 1) {					
					
					JSONObject jobj = new JSONObject(result);
					JSONArray jobj_details = (JSONArray) jobj.get("details");
					JSONObject j_arr;
					arr_chatlist= new ArrayList<ArrayList<String>>();
					arr_chatlist.clear();
					for(int i=0;i<jobj_details.length();i++){
						arr_details=new ArrayList<String>();
						j_arr=(JSONObject) jobj_details.get(i);
						
						arr_details.add(j_arr.getString("user_id"));//profile_id	
						arr_details.add("");//user_Type
						arr_details.add(j_arr.getString("first_name"));//profile_name						
						arr_details.add(j_arr.getString("image"));//profile_user_image
						arr_details.add("");//sent
						arr_details.add("");//message
						arr_details.add(j_arr.getString("quickblox_id"));//quickblox_id
						arr_details.add(j_arr.getString("user_Type"));
						
						
						arr_chatlist.add(arr_details);
						
						
					}				
					
					startQuickblox();
				
				}
			} catch (JSONException e) {
			progress_dialogue.cancel();
				e.printStackTrace();
				gotServerError("Database error. Try again");
			}
		}

	}
	
	public void startQuickblox(){
		
		if (!QBChatService.isInitialized()) {
            QBChatService.init(getActivity());
        }
		
		
		   
        SystemUtils.USER_LOGIN="inhotel_"+prefs.getString("user_id", "");
  		SystemUtils.USER_PASSWORD="inhotel_" + prefs.getString("user_id", "") +"123";
          
          
          final QBUser user = new QBUser(SystemUtils.USER_LOGIN, SystemUtils.USER_PASSWORD);
          QBAuth.createSession(user, new QBEntityCallbackImpl<QBSession>() {
              @Override
              public void onSuccess(QBSession session, Bundle params) {
            	  getDialogs();
              }
              
              @Override
              public void onError(List<String> errors) {
            	  if(progress_dialogue.isShowing()){
            		  progress_dialogue.cancel();
            	  }
            	  
            	
					Log.e("getDailogu@e", errors + "errors");
					startQuickblox();
              }
          });  
		
		
		
		
		
		
		
		/*
		
		QBUser user = new QBUser();
		user.setLogin(SystemUtils.USER_LOGIN);
		user.setPassword(SystemUtils.USER_PASSWORD);
		QBAuth.createSession(user, new QBEntityCallbackImpl<QBSession>() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				super.onSuccess();		
				
				Log.e("getDailogue chatFRag","onSuccess");
				
			}
			
		@Override
		public void onError(List<String> errors) {
			// TODO Auto-generated method stub
			super.onError(errors);
			Log.e("getDailogue chatFrag",errors+"errors");
			progress_dialogue.cancel();
			showQiuickbloxReconnect();
		}
			
		});
		
		
	*/}
	
/*	arr_details.add(j_arr.getString("user_id"));//profile_id	
	arr_details.add("");//user_Type
	arr_details.add(j_arr.getString("first_name"));//profile_name						
	arr_details.add(j_arr.getString("image"));//profile_user_image
	arr_details.add("");//sent
	arr_details.add("");//message
	arr_details.add(j_arr.getString("quickblox_id"));//quickblox_id
	
	arr_chatlist.add(arr_details);*/
	public void getChatList_fromQuickblox(List<QBDialog> dialogsList){
			
		   
        int opponentID, each_user;
        
        arr_userlist_fnl.clear();
		// Quickblox count
		for (int i = 0; i < dialogsList.size(); i++) {

			opponentID = ChatService.getInstance().getOpponentIDForPrivateDialog(dialogsList.get(i));
			// Our server count
			for (int j = 0; j < arr_chatlist.size(); j++) {

				each_user = Integer.valueOf(arr_chatlist.get(j).get(6));

				if (opponentID == each_user) {
              Log.e("MSG",dialogsList.get(i).getLastMessage()+"&&&&&"+dialogsList.get(i).getFCreatedAt());
              
              
					long timestamp = dialogsList.get(i)
							.getLastMessageDateSent();
					DateFormat df = new SimpleDateFormat("yyyy-dd-MM hh:mm:ss");
					d = new Date(timestamp * 1000);

					df = new SimpleDateFormat("MM-dd-yyyy");
					Log.w("FOR<ATED", df.format(d));
			
             
					
					arr_det_fnl=new ArrayList<String>();					
					
					arr_det_fnl.add(arr_chatlist.get(j).get(0));//profile_id	
					arr_det_fnl.add(arr_chatlist.get(j).get(7));//user_Type
					arr_det_fnl.add(arr_chatlist.get(j).get(2));//profile_name						
					arr_det_fnl.add(arr_chatlist.get(j).get(3));//profile_user_image
					arr_det_fnl.add("Posted on - "+df.format(d));//sent
					arr_det_fnl.add(dialogsList.get(i).getLastMessage());//message 
					arr_det_fnl.add(arr_chatlist.get(j).get(6));//quickblox_id
					arr_det_fnl.add("chat");//quickblox_id
					
					arr_userlist_fnl.add(arr_det_fnl);				
					
				}
				
			}
			
		}
		
		
		setListview();
		
		
	}
	
	public void setListview(){
		Log.e("CHAT LISTVIEW",arr_userlist_fnl.size()+":@@@");
		for(int i=0; i<arr_userlist_fnl.size();i++){
			
			
		}
		
		
			progress_dialogue.cancel();
		
		
		
		adp    = new ChatUserAdapter(getActivity(),arr_userlist_fnl);	
		
		list.setAdapter(adp);
		adp.notifyDataSetChanged();
	/*	if(list.getAdapter() == null){
			
		}else{
			
		}*/
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
	
	
	
	//***************************************************** QUICKBLOX**********************
	
	 private void getDialogs(){
	       // progressBar.setVisibility(View.VISIBLE);

	        // Get dialogs
	        //
	        ChatService.getInstance().getDialogs(new QBEntityCallbackImpl() {
	            @Override
	            public void onSuccess(Object object, Bundle bundle) {
	              //  progressBar.setVisibility(View.GONE);

	                final List<QBDialog> dialogsList = (ArrayList<QBDialog>)object;
	                Log.e("**************dialogs****",dialogsList+"@@@@@@");
	                
	                getChatList_fromQuickblox(dialogsList);	                	               
	            }

	            @Override
	            public void onError(List errors) {
	                //progressBar.setVisibility(View.GONE);
	            	progress_dialogue.dismiss();
	            	Log.e("GETDAT","ERR"+errors);
	            //	showQiuickbloxReconnect();
	               /* AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
	                dialog.setMessage("JOYAL: " + errors).create().show();*/
	            }
	        });
	    }
	 
	  public void showQiuickbloxReconnect(){
	      	getActivity().runOnUiThread(new Runnable() {
	      		  public void run() {
	    			  AlertDialog.Builder dialog = new AlertDialog.Builder(
								getActivity());
						dialog.setPositiveButton("Reconnect",
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialog,
											int which) {
										dialog.cancel();
										startQuickblox();
									}
								});
						dialog.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(
											DialogInterface dialog,
											int which) {
										dialog.cancel();

									}
								});
						dialog.setMessage("Chat Server is not connecting !! ");
						dialog.show();
	    		  }
	    		});
	      }
	 
	 @Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if (!QBChatService.isInitialized()) {
            QBChatService.init(getActivity());
        }
		
		if (prefs.getString("activation_status", "").equalsIgnoreCase("N")) {
			set_Acesscode_Alert();
		}else{
			if (new CommonActions(getActivity()).isNetworkConnected()) {

				progress_dialogue.setMessage("Loading...");
				progress_dialogue.show();
				new AcessCodeTask().execute(1);
			} else {
				new CommonActions(getActivity()).showToast("Please check your internet connection");
			}
		}
	}

}
