
/**
© by newagesmb.com
@author joyal@newagesmb.com
created on May 6, 2015**/
package com.inhotelappltd.inhotel.tab;

import java.util.ArrayList;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.inhotelappltd.inhotel.R;

import com.inhotelappltd.inhotel.PulltoRefresh.PullToRefreshBase.Mode;
import com.inhotelappltd.inhotel.PulltoRefresh.PullToRefreshBase.OnRefreshListener2;
import com.inhotelappltd.inhotel.PulltoRefresh.PullToRefreshView;
import com.inhotelappltd.inhotel.common.ChatUserAdapter;
import com.inhotelappltd.inhotel.logger.Log;
import com.inhotelappltd.inhotel.server.ServerConnector;
import com.inhotelappltd.inhotel.utils.CommonActions;

public class GuestFragment  extends Fragment {

	SharedPreferences prefs;
	Editor edt;
	ProgressDialog progress_dialogue;
	PullToRefreshView pulltorefreshview;
	ListView list_pull;
    LinearLayout rl_full;
	ListView list;
	ChatUserAdapter adp;
	TextView tv_title;
	Dialog dialog;
	String acessCode;
	int status;
	int pageNo=0;
	ArrayList<ArrayList<String>> arr_chatlist=new ArrayList<ArrayList<String>>();
	ArrayList<String> arr_details;	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v1     = inflater.inflate(R.layout.activity_chat_fragment, container,false);
		
		tv_title    = (TextView) v1.findViewById(R.id.tv_title); 
		list        =(ListView) v1.findViewById(R.id.list);
		rl_full       =(LinearLayout) v1.findViewById(R.id.rl_full);
		progress_dialogue = new ProgressDialog(getActivity());			

		
		pulltorefreshview   =(PullToRefreshView) v1.findViewById(R.id.pulltorefreshview);
		list_pull=pulltorefreshview.getRefreshableView();
		list_pull.setEmptyView(v1.findViewById(R.id.tv_nodata));
		
		list.setVisibility(View.GONE);
		pulltorefreshview.setVisibility(View.VISIBLE);

		init();
		return v1;
	}
	public void init(){
		CommonActions.USER_STATUS = "block";
		prefs       = getActivity().getSharedPreferences("Login Credinals",Context.MODE_PRIVATE);
		edt         = prefs.edit();

		pageNo      = 0;
		arr_chatlist= new ArrayList<ArrayList<String>>();
		adp         = new ChatUserAdapter(getActivity(), arr_chatlist);	
	
		tv_title.setText("Guests");	
		
		pulltorefreshview.setMode(Mode.PULL_UP_TO_REFRESH);
		pulltorefreshview.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh() {
				
			}

			@Override
			public void onPullUpToRefresh() {
				doRefresh();				
			}
			
		});
		
		list_pull.setOnItemClickListener(new OnItemClickListener() {

			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				pageNo=0;
				
				Log.e("GUEST QUicktID",arr_chatlist.get(position-1).get(6)+"@@@");
				
				Intent  intent = new Intent(getActivity(),PublicViewActivity.class);
			
				Bundle b = new Bundle();			
				b.putString("profID", arr_chatlist.get(position-1).get(0));
				b.putString("QUicktID", arr_chatlist.get(position-1).get(6));
				intent.putExtras(b);
				startActivity(intent);
				
			}
		});
	
	
	
	

		/////////
		
	}
	
public void doRefresh(){	
	pageNo += 1;
	new GuestTask().execute(1);
	
}
	
		public void set_Acesscode_Alert() {				
	    rl_full.setVisibility(View.GONE);
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
						new GuestTask().execute(0);
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
	public class GuestTask extends AsyncTask<Integer, Integer, String> {

		
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
						"GetAllGuestUsers",
						"{\"activation_code\":\""+prefs.getString("activation_code", "")+"\",\"user_id\": \""+ prefs.getString("user_id", "")+"\","
								+ "\"page\": \"" + pageNo + "\",\"device_token\": \"\"}",
						"\"\"");
			}else{
				return null;
			}

		}

		@Override
		protected void onPostExecute(String result) {		
			try {
				if (status == 0) {			

					JSONObject jobj = new JSONObject(result);
					progress_dialogue.cancel();

					edt.putString("activation_status",
							jobj.getString("activation_status"));
					edt.commit();
					if (jobj.getString("activation_status").equalsIgnoreCase("Y")) {
						rl_full.setVisibility(View.VISIBLE);
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
													new GuestTask().execute(1);
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
				}else if (status == 1) {
					
					pulltorefreshview.onRefreshComplete();

					JSONObject jobj = new JSONObject(result);
					JSONArray jobj_details = (JSONArray) jobj.get("details");
					JSONObject j_arr;
					
					for(int i=0;i<jobj_details.length();i++){
						arr_details=new ArrayList<String>();
						j_arr=(JSONObject) jobj_details.get(i);
						
						arr_details.add(j_arr.getString("user_id"));//profile_id	
						arr_details.add("normal");//user_Type
						arr_details.add(j_arr.getString("first_name"));//profile_name						
						arr_details.add(j_arr.getString("image"));//profile_user_image
						arr_details.add(j_arr.getString("status"));//sent
						arr_details.add("");//message
						arr_details.add(j_arr.getString("quickblox_id"));//quickblox_id
						arr_details.add("guest");//quickblox_id
						
						arr_chatlist.add(arr_details);
						
						
					}
					
					setListview();
					if (progress_dialogue.isShowing()) {
						progress_dialogue.cancel();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				gotServerError("Database error. Try again");
			}
		}

	}
	public void setListview(){
		//list.setAdapter(adp);	
		Log.e("GUEST",""+arr_chatlist.size()+list.getAdapter());
		adp    = new ChatUserAdapter(getActivity(), arr_chatlist);			
		list_pull.setAdapter(adp);		
		adp.notifyDataSetChanged();
		
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
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();		
		arr_chatlist.clear();
		adp.notifyDataSetChanged();
		if (prefs.getString("activation_status", "").equalsIgnoreCase("N")) {
			set_Acesscode_Alert();
		}else{
			if (new CommonActions(getActivity()).isNetworkConnected()) {

				progress_dialogue.setMessage("Loading...");
				progress_dialogue.show();
				new GuestTask().execute(1);
			} else {
				new CommonActions(getActivity()).showToast("Please check your internet connection");
			}
		}
	}
	
	
		
	}


	


