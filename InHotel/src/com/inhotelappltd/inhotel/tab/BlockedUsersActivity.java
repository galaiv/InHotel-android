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
import android.view.View;
import android.view.View.OnClickListener;
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

import com.inhotelappltd.inhotel.PulltoRefresh.PullToRefreshBase.Mode;
import com.inhotelappltd.inhotel.PulltoRefresh.PullToRefreshBase.OnRefreshListener2;
import com.inhotelappltd.inhotel.PulltoRefresh.PullToRefreshView;
import com.inhotelappltd.inhotel.common.ChatUserAdapter;
import com.inhotelappltd.inhotel.logger.Log;
import com.inhotelappltd.inhotel.server.ServerConnector;
import com.inhotelappltd.inhotel.utils.CommonActions;
import com.inhotelappltd.inhotel.AcessCodeActivity;
import com.inhotelappltd.inhotel.SplashScreenActivity;

public class BlockedUsersActivity extends Activity {
	SharedPreferences prefs;
	Editor edt;
	ProgressDialog progress_dialogue;
	ListView list;
	ChatUserAdapter adp;
	TextView tv_title, tv_back, tv_save;
	int status;
	String acessCode;
	Dialog dialog;
	int pageNo = 0;
	ArrayList<ArrayList<String>> arr_chatlist = new ArrayList<ArrayList<String>>();
	ArrayList<String> arr_details;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_fragment);

		init();
	}

	public void init() {
		CommonActions.USER_STATUS = "unblock";
		prefs = getSharedPreferences("Login Credinals", Context.MODE_PRIVATE);
		edt = prefs.edit();
	    progress_dialogue = new ProgressDialog(BlockedUsersActivity.this);	
		
		pageNo = 0;
		arr_chatlist = new ArrayList<ArrayList<String>>();
		adp = new ChatUserAdapter(this, arr_chatlist);
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_save = (TextView) findViewById(R.id.tv_save);
		list = (ListView) findViewById(R.id.list);
		
		list.setEmptyView(findViewById(R.id.tv_nodata));
		tv_title.setText("Blocked Users");
		tv_back.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back_button,
				0, 0, 0);
		tv_back.setText("Back");
		
		tv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {

				Intent intent = new Intent(BlockedUsersActivity.this,
						PublicViewActivity.class);
				//AcessCodeActivity.chat = 2;
				Bundle b = new Bundle();
				b.putString("profID", arr_chatlist.get(position).get(0));
				b.putString("QUicktID", "");
				intent.putExtras(b);
				startActivity(intent);

			}
		});

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		finish();
	}

	public void doRefresh() {
		Log.e("pageNo += 1;", pageNo + "");
		pageNo += 1;
		new BlockTask().execute(1);

	}

	/**
	 * Task for downloading Menu management data
	 * 
	 * @author joyal
	 * 
	 */
	public class BlockTask extends AsyncTask<Integer, Integer, String> {

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			status = params[0];
			if (status == 0) {
				return new ServerConnector(BlockedUsersActivity.this)
						.getResponseFromServer(
								CommonActions.appURL,
								"checkActivationCodeExpires",
								"{\"activation_code\": \"" + acessCode
										+ "\",\"user_id\": \""
										+ prefs.getString("user_id", "")
										+ "\"}", "\"\"");

			} else if (status == 1) {
				return new ServerConnector(BlockedUsersActivity.this)
						.getResponseFromServer(
								CommonActions.appURL,
								"GetAllBlockedUsers",
								"{\"activation_code\":\""
										+ prefs.getString("activation_code", "")
										+ "\",\"user_id\": \""
										+ prefs.getString("user_id", "")
										+ "\"," + "\"page\": \"" + pageNo
										+ "\",\"device_token\": \"\"}", "\"\"");
			} else {
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

					edt.putString("activation_status",
							jobj.getString("activation_status"));
					edt.commit();
					if (jobj.getString("activation_status").equalsIgnoreCase(
							"Y")) {
						list.setEnabled(true);
						list.setClickable(true);
						edt.putString("activation_code", acessCode);
						edt.commit();

						new AlertDialog.Builder(BlockedUsersActivity.this)
								.setTitle("Message")
								.setMessage(jobj.getString("message"))
								.setPositiveButton("Ok",
										new DialogInterface.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.cancel();
												if (new CommonActions(
														BlockedUsersActivity.this)
														.isNetworkConnected()) {

													progress_dialogue.setMessage("Loading...");
													progress_dialogue.show();
													new BlockTask().execute(1);
												} else {
													new CommonActions(
															BlockedUsersActivity.this)
															.showToast("Please check your internet connection");
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
				} else if (status == 1) {
					
					JSONObject jobj = new JSONObject(result);
					JSONArray jobj_details = (JSONArray) jobj.get("details");
					JSONObject j_arr;

					for (int i = 0; i < jobj_details.length(); i++) {
						arr_details = new ArrayList<String>();
						j_arr = (JSONObject) jobj_details.get(i);

						arr_details.add(j_arr.getString("user_id"));// profile_id
						arr_details.add("");// user_Type
						arr_details.add(j_arr.getString("first_name"));// profile_name
						arr_details.add(j_arr.getString("image"));// profile_user_image
						arr_details.add(j_arr.getString("status"));// sent
						arr_details.add("");// message
						arr_details.add("");// message
						arr_details.add("block");// message
						
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

	public void setListview() {
		// list.setAdapter(adp);
		Log.e("GUEST", "" + arr_chatlist.size() + list.getAdapter());
		adp = new ChatUserAdapter(this, arr_chatlist);
		list.setAdapter(adp);
		adp.notifyDataSetChanged();

	}

	public void set_Acesscode_Alert() {

		dialog = new Dialog(BlockedUsersActivity.this,
				android.R.style.Theme_DeviceDefault_Light);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(false);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(Color.TRANSPARENT));
		dialog.getWindow().clearFlags(LayoutParams.FLAG_DIM_BEHIND);
		dialog.setContentView(BlockedUsersActivity.this.getLayoutInflater()
				.inflate(R.layout.custom_acess_code, null));

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
				if (!et_code.getText().toString().equalsIgnoreCase("")) {
					acessCode = et_code.getText().toString();
					if (new CommonActions(BlockedUsersActivity.this)
							.isNetworkConnected()) {
						dialog.cancel();
						progress_dialogue.setMessage("Loading...");
						progress_dialogue.show();
						new BlockTask().execute(0);
					} else {
						new CommonActions(BlockedUsersActivity.this)
								.showToast("Please check your internet connection");
					}
				} else {
					new AlertDialog.Builder(BlockedUsersActivity.this)
							.setTitle("Warning")
							.setMessage("Please enter the acess code")
							.setPositiveButton("Ok",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
										}
									})

							.show();
				}

			}

		});

		dialog.show();

	}

	private void hideKeyboard() {
		// Check if no view has focus:
		View view = ((Activity) BlockedUsersActivity.this).getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) ((Activity) BlockedUsersActivity.this)
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}

		((Activity) BlockedUsersActivity.this).getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		arr_chatlist.clear();
		adp.notifyDataSetChanged();

		if (prefs.getString("activation_status", "").equalsIgnoreCase("N")) {
			set_Acesscode_Alert();
		} else {
			if (new CommonActions(this).isNetworkConnected()) {

				progress_dialogue.setMessage("Loading...");
				progress_dialogue.show();
				new BlockTask().execute(1);
			} else {
				new CommonActions(this)
						.showToast("Please check your internet connection");
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
		new AlertDialog.Builder(BlockedUsersActivity.this).setTitle("Message")
				.setMessage(msg)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})

				.show();
	}
}
