package com.inhotelappltd.inhotel;

import java.util.ArrayList;

import org.json.JSONArray;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.inhotelappltd.inhotel.R;

import com.inhotelappltd.inhotel.common.ChatUserAdapter;
import com.inhotelappltd.inhotel.logger.Log;
import com.inhotelappltd.inhotel.server.ServerConnector;
import com.inhotelappltd.inhotel.tab.ConversationActivity;
import com.inhotelappltd.inhotel.tab.PublicViewActivity;
import com.inhotelappltd.inhotel.utils.CommonActions;

public class DrinkViewActivity extends Activity {

	TextView tv_back, tv_title;
	ArrayList<ArrayList<String>> arr_chatlist = new ArrayList<ArrayList<String>>();
	int status;
	ArrayList<String> arr_details;
	SharedPreferences prefs;
	ProgressDialog progress_dialogue;
	Editor edt;
	ChatUserAdapter adp;
	ListView list;
	String profID,chatID,nav;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drink_view);
		prefs = getSharedPreferences("Login Credinals", Context.MODE_PRIVATE);
		edt = prefs.edit();
		tv_back  = (TextView) findViewById(R.id.tv_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		adp      = new ChatUserAdapter(this, arr_chatlist);	
		list     = (ListView) findViewById(R.id.list);
		tv_title.setText("Send a Drink");
	
		tv_back.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back_button,0, 0, 0);
		tv_back.setText("Back");
		
		Bundle b = getIntent().getExtras();
		if (b != null) {
			profID = b.getString("profID");
			chatID = b.getString("QUicktID");
			
		}
		

		if (new CommonActions(this).isNetworkConnected()) {
			progress_dialogue = ProgressDialog.show(this, "Please wait",
					"Loading");
			new DrinkTask().execute(0);
		} else {
			new CommonActions(this)
					.showToast("Please check your internet connection");
		}
		
		
		tv_back.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				onBackPressed() ;
				
			}
		});
		
		
    list.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Intent  intent = new Intent(DrinkViewActivity.this,ConversationActivity.class);
				//AcessCodeActivity.chat  =  1;
				Bundle b = new Bundle();			
				b.putString("profID", profID);
				b.putString("QUicktID", chatID);
				b.putString("page",  "drink");
				b.putString("drinkID",  arr_chatlist.get(position).get(0));
				
				
				edt.putString("profID", profID);
				edt.putString("QUicktID", chatID);
				edt.putString("page",  "drink");
				edt.putString("drinkID",  arr_chatlist.get(position).get(0));
				edt.commit();
				
				intent.putExtras(b);
				startActivity(intent);
				finish();
				
			}
		});
	}

	@Override
	public void onBackPressed() {
		Intent  intent = new Intent(DrinkViewActivity.this,ConversationActivity.class);
		//AcessCodeActivity.chat  =  1;
		Bundle b = new Bundle();			
		b.putString("profID", profID);
		b.putString("QUicktID", chatID);
		b.putString("page",  "drink");
		b.putString("drinkID", "");
		
		edt.putString("profID", profID);
		edt.putString("QUicktID", chatID);
		edt.putString("page",  "drink");
		edt.putString("drinkID", "");
		
		edt.commit();
		
		
		intent.putExtras(b);
		startActivity(intent);
		finish();
	}


	/**
	 * Task for downloading Menu management data
	 * 
	 * @author joyal
	 * 
	 */
	public class DrinkTask extends AsyncTask<Integer, Integer, String> {

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			status = params[0];
			if (status == 0) {
				return new ServerConnector(DrinkViewActivity.this)
						.getResponseFromServer(
								CommonActions.appURL,
								"drinks_list",
								"{\"activation_code\": \""
										+ prefs.getString("activation_code", "")
										+ "\"}", "\"\"");

			} else {
				return null;
			}

		}

		@Override
		protected void onPostExecute(String result) {
			Log.e("result", result);
			try {
				JSONObject jobj = new JSONObject(result);
				JSONArray jobj_details = (JSONArray) jobj.get("details");
				JSONObject j_arr;

				for (int i = 0; i < jobj_details.length(); i++) {
					arr_details = new ArrayList<String>();
					j_arr = (JSONObject) jobj_details.get(i);

					arr_details.add(j_arr.getString("id"));// DRINK_id
					arr_details.add("");// user_Type
					arr_details.add(j_arr.getString("title"));// profile_name
					arr_details.add(j_arr.getString("image"));// profile_user_image
					arr_details.add("$ " + j_arr.getString("price"));// sent
					arr_details.add(j_arr.getString("description"));// message
					arr_details.add("");// quickblox_id
					arr_details.add("drink");// drink

					arr_chatlist.add(arr_details);

				}

				setListview();
				if (progress_dialogue.isShowing()) {
					progress_dialogue.cancel();
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
		adp      = new ChatUserAdapter(this, arr_chatlist);	
		if(list.getAdapter() == null){
			list.setAdapter(adp);
		}else{
			adp.notifyDataSetChanged();
		}
	}
	/*
	 * Method to manage the exeption occured due to server down
	 */
	public void gotServerError(String msg) {
		if (progress_dialogue.isShowing()) {
			progress_dialogue.cancel();
		}
		new AlertDialog.Builder(DrinkViewActivity.this).setTitle("Message")
				.setMessage(msg)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})

				.show();
	}
}
