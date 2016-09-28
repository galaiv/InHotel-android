package com.inhotelappltd.inhotel.tab;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inhotelappltd.inhotel.R;

import com.inhotelappltd.inhotel.common.ListviewImageLoader;
import com.inhotelappltd.inhotel.logger.Log;
import com.inhotelappltd.inhotel.server.ServerConnector;
import com.inhotelappltd.inhotel.utils.CommonActions;

public class PublicViewActivity extends Activity {
	// cb_block
	String profID,chatID;
	CheckBox cb_block;
	SharedPreferences prefs;
	Editor edt;
	TextView tv_back, tv_title, tv_save, et_desc, tv_status;
	TextView tv_name, tv_toValue, tv_roomno, tv_fromValue;
	ImageView iv_pic;
	ListView lst;
	int status;
	
	ArrayList<ArrayList<String>> arr_reason_new = new ArrayList<ArrayList<String>>();
	ArrayList<ArrayList<String>> arr_reason = new ArrayList<ArrayList<String>>();
	ArrayList<String> arr_data;
	ProgressDialog progress_dialogue;
	String name = "", status_des = "", from = "", to = "", room = "",
			desc = "", vacancy_list = "";
	ChoiceAdapter adp;
	private File mFileTemp;
	RelativeLayout rl_room;
	Bundle b;
	Uri uri;
	Bitmap bitmapx;
	public static final String TAG = "CreateRestaurantActivity";
	public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
	public static final int REQUEST_CODE_GALLERY = 0x1;
	public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
	public static final int REQUEST_CODE_CROP_IMAGE = 0x3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_public_view);

		b = getIntent().getExtras();
		cb_block = (CheckBox) findViewById(R.id.cb_block);
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_save = (TextView) findViewById(R.id.tv_save);
		iv_pic = (ImageView) findViewById(R.id.iv_pic);
		et_desc = (TextView) findViewById(R.id.et_desc);
		tv_status = (TextView) findViewById(R.id.tv_status);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_toValue = (TextView) findViewById(R.id.tv_toValue);
		tv_roomno = (TextView) findViewById(R.id.tv_roomno);
		tv_fromValue = (TextView) findViewById(R.id.tv_fromValue);
		rl_room = (RelativeLayout ) findViewById(R.id.rl_room);
		
		lst = (ListView) findViewById(R.id.lst);
		prefs = getSharedPreferences("Login Credinals", Context.MODE_PRIVATE);
		edt = prefs.edit();
		tv_back.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back_button,
				0, 0, 0);
		tv_back.setText("Back");
		tv_save.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.chat,
				0);
		tv_save.setPadding(0, 0, 15, 0);
		tv_title.setText("Account");
		adp = new ChoiceAdapter(PublicViewActivity.this);
		// Image PIck
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mFileTemp = new File(Environment.getExternalStorageDirectory(),
					TEMP_PHOTO_FILE_NAME);
		} else {
			mFileTemp = new File(PublicViewActivity.this.getFilesDir(),
					TEMP_PHOTO_FILE_NAME);
		}
		
		tv_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle b = new Bundle();
				Intent  intent = new Intent(PublicViewActivity.this,ConversationActivity.class);
				//AcessCodeActivity.chat  =  1;		 
				b.putString("profID", profID);
				b.putString("QUicktID", chatID);
				b.putString("page",  "guest");
				b.putString("type", "normal");
				
				
				edt.putString("profID", profID);
				edt.putString("QUicktID", chatID);
				edt.putString("page",  "guest");
				edt.putString("type", "normal");
				
				edt.commit();
				
				intent.putExtras(b);
				startActivity(intent);
				
				
				
			}
		});
		tv_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				
			}
		});
		et_desc.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Disable the parent Scrolling
				et_desc.setMovementMethod(new ScrollingMovementMethod());			
				v.getParent().requestDisallowInterceptTouchEvent(true);
				v.setEnabled(true);
				return false;
			}
		});
		
		tv_status.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			
				tv_status.setHorizontalScrollBarEnabled(true);
				tv_status.setMovementMethod(new ScrollingMovementMethod());
				/*tv_status.setHorizontalScrollBarEnabled(true);
				tv_status.setScrollbarFadingEnabled(true);
				tv_status.setHorizontallyScrolling(true);
				tv_status.setMovementMethod(new ScrollingMovementMethod());
				tv_status.setEnabled(true);*/
			
			}
		});
		
		lst.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				v.setEnabled(true);
				return false;
			}
			
		});
		
		cb_block.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.e("DD", "DDD"+cb_block.isChecked());
				String msg="";
				
				if(CommonActions.USER_STATUS.equalsIgnoreCase("block")){
					msg="Do you want to block this user ?";
				}else{
					msg="Do you want to unblock this user ?";
				}
				//	CommonActions.USER_STATUS = "block";
				new AlertDialog.Builder(PublicViewActivity.this)
                .setTitle("Message")
				.setMessage(msg)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								progress_dialogue = ProgressDialog.show(PublicViewActivity.this,
										"Please wait", "Loading");
								new AccountTask().execute(2);
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						if(CommonActions.USER_STATUS.equalsIgnoreCase("block")){
							cb_block.setChecked(false);
						}else{
							cb_block.setChecked(true);
						}
					
						dialog.cancel();
					}
				})

				.show();
				
				
				
				
			}
		});
		if (b != null) {
			profID = b.getString("profID");
			chatID = b.getString("QUicktID");
			
			Log.e("PUblic@@",profID+" profID:"+chatID);
			
			if(chatID.length()<2){
				tv_save.setVisibility(View.INVISIBLE);
			}
			progress_dialogue = ProgressDialog.show(PublicViewActivity.this,
					"Please wait", "Loading");
			new AccountTask().execute(1);
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	/**
	 * Task for downloading Menu management data
	 * 
	 * @author joyal
	 * 
	 */
	public class AccountTask extends AsyncTask<Integer, Integer, String> {

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			status = params[0];
			if (status == 0) {

				return new ServerConnector(PublicViewActivity.this)
						.getResponseFromServer(
								CommonActions.appURL,
								"get_user_details",
								"{\"user_id\": \"" + profID
										+ "\",\"other_id\": \""
										+ prefs.getString("user_id", "")
										+ "\"}", "\"\"");
			} else if (status == 1) {
				return new ServerConnector(PublicViewActivity.this)
						.getResponseFromServer(CommonActions.appURL,
								"vacancy_reason_list", "{}", "\"\"");
			}else if (status == 2) {

				return new ServerConnector(PublicViewActivity.this)
						.getResponseFromServer(
								CommonActions.appURL,
								"block_guest",
								"{\"user_id\": \"" + prefs.getString("user_id", "")
										+ "\",\"blocked_profile_id\": \""
										+ profID
										+ "\"}", "\"\"");
			}  else {
				return null;
			}

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			JSONArray j_array = null;
			JSONObject jobj_details;
			try {
				if (status == 0) {

					JSONObject jobj = new JSONObject(result);

					JSONObject jobj_detailsz = (JSONObject) jobj.get("details");
				

					name = jobj_detailsz.getString("first_name");
					status_des = jobj_detailsz.getString("status");
					from = jobj_detailsz.getString("access_start_date_time");
					to = jobj_detailsz.getString("access_end_date_time");
					room = jobj_detailsz.getString("room");
					desc = jobj_detailsz.getString("profile_description");
					
					if(jobj_detailsz.getString("room_enable").equalsIgnoreCase("Y")){
						rl_room.setVisibility(View.VISIBLE);
					}else{
						rl_room.setVisibility(View.GONE);
					}

					tv_name.setText(name);
					tv_status.setText(status_des);
					tv_fromValue.setText(from);
					tv_toValue.setText(to);
					tv_roomno.setText(room);
					et_desc.setText(desc);
					
					if(jobj_detailsz.getString("blocked_status").equalsIgnoreCase("Y")){
						cb_block.setChecked(true);
					}else{
						cb_block.setChecked(false);
					}
					
                     new ListviewImageLoader(PublicViewActivity.this).loadRoundedShape(iv_pic,jobj_detailsz.getString("image"));
					

					JSONObject jobj_vac;
					JSONArray jobj_arrVacancy = (JSONArray) jobj.get("user_vacancy");
					
					if (jobj_arrVacancy.length() != 0) {

						for (int j = 0; j < jobj_arrVacancy.length(); j++) {
							jobj_vac = (JSONObject) jobj_arrVacancy.get(j);

							if (!jobj_vac.getString("vecancy_id").equalsIgnoreCase("")) {
								for (int i = 0; i < arr_reason_new.size(); i++) {
									if (arr_reason_new.get(i).get(0).equalsIgnoreCase(jobj_vac.getString("vecancy_id"))) {
										arr_reason_new.get(i).set(2, "Y");
									}
								}
							}
						}
						Log.e("!!!!!STA", arr_reason_new.size()+" TOTAL");
						arr_reason.clear();
						
						for (int i = 0; i < arr_reason_new.size(); i++) {
							if(arr_reason_new.get(i).get(2).equalsIgnoreCase("Y")){
								arr_reason.add(arr_reason_new.get(i));
							}							
							
						}
					}

					lst.setAdapter(adp);

					progress_dialogue.cancel();

				} else if (status == 1) {

					arr_reason = new ArrayList<ArrayList<String>>();
					JSONObject jobj = new JSONObject(result);
					j_array = (JSONArray) jobj.get("details");

					for (int i = 0; i < j_array.length(); i++) {
						arr_data = new ArrayList<String>();
						jobj_details = (JSONObject) j_array.get(i);

						arr_data.add(jobj_details.getString("id"));
						arr_data.add(jobj_details.getString("title").trim());
						arr_data.add("N");

						arr_reason_new.add(arr_data);
					}

					new AccountTask().execute(0);

					

				}else if(status == 2){
					progress_dialogue.cancel();
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
		new AlertDialog.Builder(PublicViewActivity.this).setTitle("Message")
				.setMessage(msg)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})

				.show();
	}

	

	/****************************************** ADAPTER ********************************************/

	public class ChoiceAdapter extends BaseAdapter {
		LayoutInflater laytout_inflator;
		Context context;
		int count = 0;

		public ChoiceAdapter(Context context) {
			laytout_inflator = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		}

		@Override
		public int getCount() {
			return arr_reason.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup arg2) {

			int pox = position;

			// TODO Auto-generated method stub
			ViewHolder view_holder = new ViewHolder();

			if (convertView == null) {
				convertView = laytout_inflator.inflate(
						R.layout.custom_list_vacancy, null);
				view_holder = intitViews(convertView);
				convertView.setTag(view_holder);
			} else {
				view_holder = (ViewHolder) convertView.getTag();
			}

			if (arr_reason.get(pox).get(1).equalsIgnoreCase("Nature")) {

				view_holder.tv_type.setText("Nature");
				view_holder.iv_icon.setImageResource(R.drawable.nature_icon);

				if (arr_reason.get(pox).get(2).equalsIgnoreCase("N")) {
					view_holder.cb_validation.setVisibility(View.INVISIBLE);
				} else {
					view_holder.cb_validation.setVisibility(View.VISIBLE);
				}

			} else if (arr_reason.get(pox).get(1).equalsIgnoreCase("Culture")) {

				view_holder.tv_type.setText("Culture");
				view_holder.iv_icon.setImageResource(R.drawable.culture_icon);

				if (arr_reason.get(pox).get(2).equalsIgnoreCase("N")) {
					view_holder.cb_validation.setVisibility(View.INVISIBLE);
				} else {
					view_holder.cb_validation.setVisibility(View.VISIBLE);
				}
			} else if (arr_reason.get(pox).get(1)
					.equalsIgnoreCase("Business Fiera")) {
				view_holder.tv_type.setText("Business Fiera");
				view_holder.iv_icon.setImageResource(R.drawable.fiera);
				if (arr_reason.get(pox).get(2).equalsIgnoreCase("N")) {
					view_holder.cb_validation.setVisibility(View.INVISIBLE);
				} else {
					view_holder.cb_validation.setVisibility(View.VISIBLE);
				}
			} else if (arr_reason.get(pox).get(1).equalsIgnoreCase("Concert")) {
				view_holder.tv_type.setText("Concert");
				view_holder.iv_icon.setImageResource(R.drawable.concert);
				if (arr_reason.get(pox).get(2).equalsIgnoreCase("N")) {
					view_holder.cb_validation.setVisibility(View.INVISIBLE);
				} else {
					view_holder.cb_validation.setVisibility(View.VISIBLE);
				}
			} else if (arr_reason.get(pox).get(1).equalsIgnoreCase("Sport")) {
				view_holder.tv_type.setText("Sport");
				view_holder.iv_icon.setImageResource(R.drawable.sports);
				if (arr_reason.get(pox).get(2).equalsIgnoreCase("N")) {
					view_holder.cb_validation.setVisibility(View.INVISIBLE);
				} else {
					view_holder.cb_validation.setVisibility(View.VISIBLE);
				}
			} else if (arr_reason.get(pox).get(1)
					.equalsIgnoreCase("Family Vacation")) {
				view_holder.tv_type.setText("Family Vacation");
				view_holder.iv_icon
						.setImageResource(R.drawable.family_vacation);
				if (arr_reason.get(pox).get(2).equalsIgnoreCase("N")) {
					view_holder.cb_validation.setVisibility(View.INVISIBLE);
				} else {
					view_holder.cb_validation.setVisibility(View.VISIBLE);
				}

			}

			return convertView;
		}

		/**
		 * Method for initializing the view holder object
		 * 
		 * @param view
		 * @return
		 */
		public ViewHolder intitViews(View view) {
			ViewHolder view_holder = new ViewHolder();
			view_holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
			view_holder.tv_type = (TextView) view.findViewById(R.id.tv_type);
			view_holder.cb_validation = (ImageView) view
					.findViewById(R.id.cb_validation);
			return view_holder;
		}

		/**
		 * View holder class
		 */
		public class ViewHolder {
			ImageView iv_icon;
			TextView tv_type;
			ImageView cb_validation;
		}
	}

	/* Setting profile pic */

	/**
	 * Method to add photo from the Gallery or Camera
	 */
	private void addPhoto() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(
				PublicViewActivity.this);
		myAlertDialog.setTitle("Choose an Option");
		myAlertDialog.setMessage("How do you want to set your picture?");

		myAlertDialog.setPositiveButton("Gallery",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {

						openGallery();
					}
				});

		myAlertDialog.setNegativeButton("Camera",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {

						takePicture();
					}
				});
		myAlertDialog.show();
	}

	private void takePicture() {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		try {
			Uri mImageCaptureUri = null;
			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state)) {
				mImageCaptureUri = Uri.fromFile(mFileTemp);
				intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
						mImageCaptureUri);
				intent.putExtra("return-data", true);
				startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
			} else {
				new CommonActions(PublicViewActivity.this)
						.showToast("Media is not mounted");
			}

		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void openGallery() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		/*
		 * if (resultCode != RESULT_OK) {
		 * 
		 * return; }
		 */

		switch (requestCode) {

		case REQUEST_CODE_GALLERY:

			try {

				InputStream inputStream = PublicViewActivity.this
						.getContentResolver().openInputStream(data.getData());
				FileOutputStream fileOutputStream = new FileOutputStream(
						mFileTemp);
				copyStream(inputStream, fileOutputStream);
				fileOutputStream.close();
				inputStream.close();
				uri = data.getData();
				bitmapx = BitmapFactory.decodeFile(mFileTemp.getPath());
				/*iv_pic.setImageBitmap(CircleImageCreator.getRoundedShape(160,
						bitmapx));*/

			} catch (Exception e) {

			}

			break;
		case REQUEST_CODE_TAKE_PICTURE:
			bitmapx = BitmapFactory.decodeFile(mFileTemp.getPath());
			/*iv_pic.setImageBitmap(CircleImageCreator.getRoundedShape(160,
					bitmapx));
*/
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public static void copyStream(InputStream input, OutputStream output)
			throws IOException {

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

}
