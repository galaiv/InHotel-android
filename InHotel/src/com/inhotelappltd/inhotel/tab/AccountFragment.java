package com.inhotelappltd.inhotel.tab;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
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
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.method.KeyListener;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.inhotelappltd.inhotel.R;

import com.inhotelappltd.inhotel.common.CircleImageCreator;
import com.inhotelappltd.inhotel.http.MultipartEntityBuilder;
import com.inhotelappltd.inhotel.logger.Log;
import com.inhotelappltd.inhotel.server.ServerConnector;
import com.inhotelappltd.inhotel.utils.CommonActions;
import com.inhotelappltd.inhotel.utils.InternalStorageContentProvider;
import com.inhotelappltd.inhotel.RegistrationActivity;
import com.inhotelappltd.inhotel.SplashScreenActivity;

import eu.janmuller.android.simplecropimage.CropImage;

public class AccountFragment extends Fragment {
	TextView tv_back, tv_title, tv_save, tv_fromValue,
			tv_toValue, tv_roomno;
	EditText tv_name,tv_status,et_desc;
	ProgressDialog progress_dialogue;
	SharedPreferences prefs;
	String type="";
	ImageView iv_pic;
	Editor edt;	
	Bitmap bitmapx;
	ListView lst;
	int status;
	int pox;
	ArrayList<ArrayList<String>> arr_reason = new ArrayList<ArrayList<String>>();
	ArrayList<String> arr_data;
	String name="",status_des="",from="", to="",room="",desc="",vacancy_list ="";
	ChoiceAdapter adp;
	MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();
	
	public static final String TAG = "CreateRestaurantActivity";

	public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";

	public static final int REQUEST_CODE_GALLERY = 0x1;
	public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
	public static final int REQUEST_CODE_CROP_IMAGE = 0x3;
	KeyListener variable;
	private File mFileTemp;
	Uri uri;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v1 = inflater.inflate(R.layout.frag_account, container, false);
		
	    progress_dialogue = new ProgressDialog(getActivity());			


		tv_back   = (TextView) v1.findViewById(R.id.tv_back);
		tv_title  = (TextView) v1.findViewById(R.id.tv_title);
		tv_save   = (TextView) v1.findViewById(R.id.tv_save);
		iv_pic    = (ImageView) v1.findViewById(R.id.iv_pic);
		et_desc   = (EditText) v1.findViewById(R.id.et_desc);
		tv_status = (EditText) v1.findViewById(R.id.tv_status);
		tv_name   = (EditText) v1.findViewById(R.id.tv_name);
		tv_toValue= (TextView) v1.findViewById(R.id.tv_toValue);
		tv_roomno = (TextView) v1.findViewById(R.id.tv_roomno);
		tv_fromValue = (TextView) v1.findViewById(R.id.tv_fromValue);
		lst  =(ListView)v1.findViewById(R.id.lst);
		prefs = getActivity().getSharedPreferences("Login Credinals",Context.MODE_PRIVATE);
		edt = prefs.edit();
		tv_back.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back_button,0, 0, 0);
		tv_back.setText("Settings");
		tv_save.setText("Save");
		tv_title.setText("Account");
		
		
		variable = tv_status.getKeyListener(); 
		//tv_status.setKeyListener(null);
		adp=new ChoiceAdapter(getActivity());
		
		
		Bitmap rounded_icon = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.noimg_box);
		iv_pic.setImageBitmap(CircleImageCreator.getRoundedShapeNew(rounded_icon));
		
		tv_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				FragmentTransaction transaction = getFragmentManager()
						.beginTransaction();
				transaction.replace(R.id.m_tabFrameLayout,
						new SettingFragment());
				transaction.addToBackStack("null");
				transaction.commit();
			}
		});
		
		tv_status.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.e("DD","DDD");
				//tv_status.setKeyListener(variable); 
				v.getParent().requestDisallowInterceptTouchEvent(true);
				v.setEnabled(true);
				tv_status.setScrollbarFadingEnabled(true);
				tv_status.setHorizontallyScrolling(true);
				tv_status.setMovementMethod(new ScrollingMovementMethod()); 
				/*//tv_status.setScroller(new Scroller(getActivity())); //This may not be needed.
				tv_status.setHorizontalScrollBarEnabled(true);*/
							
				/*tv_status.setFocusable(true);
				tv_status.setClickable(true);
				
				tv_status.setEnabled(true);				
				tv_status.setCursorVisible(true);
				tv_status.setPressed(true);*/
				
			}
		});
		tv_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				progress_dialogue.setMessage("Loading...");
				progress_dialogue.show();
			
				updateAccount();
				
			}
		});

		et_desc.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Disable the parent Scrolling
				v.getParent().requestDisallowInterceptTouchEvent(true);
				v.setEnabled(true);
				return false;
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
		
		lst.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
			
				Log.e("List click",arr_reason.get(position).get(2)+"   position"+position);
				
				
				if (!prefs.getString("activation_status", "").equalsIgnoreCase("N")) {
					if (arr_reason.get(position).get(2).equalsIgnoreCase("N")) {
						arr_reason.get(position).set(2, "Y");
					} else {
						arr_reason.get(position).set(2, "N");
					}
					Log.e("List click","****"+arr_reason.get(position).get(2));
					
					//lst.setAdapter(adp);
					adp.notifyDataSetChanged();
				}else{
					gotServerError("Please activate your account.");
				}
				
			}
		});
		
		iv_pic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addPhoto();
				
			}
		});
		// Image PIck
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mFileTemp = new File(Environment.getExternalStorageDirectory(),	TEMP_PHOTO_FILE_NAME);
		} else {
			mFileTemp = new File(getActivity().getFilesDir(), TEMP_PHOTO_FILE_NAME);
		}

		if (new CommonActions(getActivity()).isNetworkConnected()) {

			progress_dialogue.setMessage("Loading...");
			progress_dialogue.show();
			new AccountTask().execute(1);
		} else {
			new CommonActions(getActivity())
					.showToast("Please check your internet connection");
		}

		return v1;
	}

	/**
	 * Task for downloading Menu management data
	 * 
	 * @author joyal
	 * 
	 */
	public class AccountTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected String doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			status = params[0];

			if (status == 0) {
				return new ServerConnector(getActivity())
						.getResponseFromServer(
								CommonActions.appURL,
								"get_user_details",
								"{\"user_id\": \""
										+ prefs.getString("user_id", "")
										+ "\",\"activation_code\": \""+prefs.getString("activation_code", "")
										+"\"}", "\"\"");
			} else if (status == 1) {
				return new ServerConnector(getActivity())
						.getResponseFromServer(CommonActions.appURL,
								"vacancy_reason_list", "{}", "\"\"");
			}else if (status == 2) {				
				return new ServerConnector(getActivity()).doRegistration(type, mpEntity);
			} else {
				return null;
			}

		}

		@Override
		protected void onPostExecute(String result) {
			
			Log.e("ACCOIUNT",result);
			JSONArray j_array = null;
			JSONObject jobj_details;
			try {
				if (status == 0) {
					
					JSONObject jobj = new JSONObject(result);
					
					JSONObject jobj_detailsz = (JSONObject) jobj.get("details");
					JSONArray  jobj_arrVacancy = (JSONArray) jobj.get("user_vacancy");
					
					new ImageLoadTask(jobj_detailsz.getString("image"), iv_pic).execute();
					name = jobj_detailsz.getString("first_name");
					status_des = jobj_detailsz.getString("status");
					from =jobj_detailsz.getString("access_start_date_time");
					to =jobj_detailsz.getString("access_end_date_time");
					room =jobj_detailsz.getString("room");
					desc = jobj_detailsz.getString("profile_description");
					
					
					tv_name.setText(name);
					tv_status.setText(status_des);
					tv_fromValue.setText(from);
					tv_toValue.setText(to);
					tv_roomno.setText(room);
					et_desc.setText(desc);
					
					
					
				
					
					JSONObject jobj_vac;
					if(jobj_arrVacancy.length()!=0){
						
						for(int j=0;j<jobj_arrVacancy.length();j++){
							jobj_vac=(JSONObject) jobj_arrVacancy.get(j);
							
							if(!jobj_vac.getString("vecancy_id").equalsIgnoreCase("")){
								for (int i = 0; i < arr_reason.size(); i++) {									
									if(arr_reason.get(i).get(0).equalsIgnoreCase(jobj_vac.getString("vecancy_id"))){									
										arr_reason.get(i).set(2, "Y");
									}								
								}						
							}							
						}
					
					}
					
					
					

					lst.setAdapter(adp);
					
					progress_dialogue.cancel();
						
				
				}else if(status==1){
				
					
					arr_reason=new ArrayList<ArrayList<String>>();
					JSONObject jobj = new JSONObject(result);
					j_array=(JSONArray) jobj.get("details");
				
					for(int i=0;i<j_array.length();i++){
						arr_data=new ArrayList<String>();						
						jobj_details=(JSONObject) j_array.get(i);
						
						arr_data.add(jobj_details.getString("id"));
						arr_data.add(jobj_details.getString("title").trim());
						arr_data.add("N");
						
					    arr_reason.add(arr_data);
					}
					
				new AccountTask().execute(0);
					
					progress_dialogue.cancel();
					
					
					
					
				}else if(status == 2){
					progress_dialogue.dismiss();
					JSONObject jobj = new JSONObject(result);
					new AlertDialog.Builder(getActivity()).setTitle("Message")
					.setMessage(jobj.getString("message"))
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							FragmentTransaction transaction = getFragmentManager()
									.beginTransaction();
							transaction.replace(R.id.m_tabFrameLayout,
									new SettingFragment());
							transaction.addToBackStack("null");
							transaction.commit();
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
	
	public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

		private String url;
		private ImageView imageView;

		public ImageLoadTask(String url, ImageView imageView) {
			this.url = url;
			this.imageView = imageView;
			
			Log.e("image",url);
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			try {
				
				URL urlConnection = new URL(url);
				HttpURLConnection connection = (HttpURLConnection) urlConnection
						.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				bitmapx = BitmapFactory.decodeStream(input);
				return bitmapx;
			} catch (Exception e) {
				e.printStackTrace();

				return BitmapFactory.decodeResource(getActivity()
						.getResources(), R.drawable.noimg_box);

			}

		}
		@Override
	    protected void onPostExecute(Bitmap result) {
	        super.onPostExecute(result);
	       // imageView.setImageBitmap(result);
	        
	        iv_pic.setImageBitmap(CircleImageCreator.getRoundedShapeNew(result));
	      
	    }
	}
	
	/******************************************  ADAPTER ********************************************/
	
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
			
			pox=position;
			
		
			// TODO Auto-generated method stub
			ViewHolder view_holder = new ViewHolder();
			
			if (convertView == null) {
				convertView = laytout_inflator.inflate(R.layout.custom_list_vacancy,null);
				view_holder = intitViews(convertView);
				convertView.setTag(view_holder);
			} else {
				view_holder = (ViewHolder) convertView.getTag();
			}
			
			
		
	
			if(arr_reason.get(pox).get(1).equalsIgnoreCase("Nature")){
			
				view_holder.tv_type .setText("Nature");
				view_holder.iv_icon.setImageResource(R.drawable.nature_icon);
				
				if(arr_reason.get(pox).get(2).equalsIgnoreCase("N")){									
					view_holder.cb_validation.setVisibility(View.INVISIBLE);					
				}else{									
					view_holder.cb_validation.setVisibility(View.VISIBLE);					
				}
				
			}else 	if(arr_reason.get(pox).get(1).equalsIgnoreCase("Culture")){
				
				view_holder.tv_type .setText("Culture");
				view_holder.iv_icon.setImageResource(R.drawable.culture_icon);
				
				if(arr_reason.get(pox).get(2).equalsIgnoreCase("N")){					
					view_holder.cb_validation.setVisibility(View.INVISIBLE);					
				}else{					
					view_holder.cb_validation.setVisibility(View.VISIBLE);	
				}
			}else 	if(arr_reason.get(pox).get(1).equalsIgnoreCase("Business Fiera")){				
				view_holder.tv_type .setText("Business Fiera");
				view_holder.iv_icon.setImageResource(R.drawable.fiera);
				if(arr_reason.get(pox).get(2).equalsIgnoreCase("N")){					
					view_holder.cb_validation.setVisibility(View.INVISIBLE);					
				}else{					
					view_holder.cb_validation.setVisibility(View.VISIBLE);	
				}
			}else 	if(arr_reason.get(pox).get(1).equalsIgnoreCase("Concert")){				
				view_holder.tv_type .setText("Concert");
				view_holder.iv_icon.setImageResource(R.drawable.concert);
				if(arr_reason.get(pox).get(2).equalsIgnoreCase("N")){					
					view_holder.cb_validation.setVisibility(View.INVISIBLE);					
				}else{					
					view_holder.cb_validation.setVisibility(View.VISIBLE);	
				}
			}else if(arr_reason.get(pox).get(1).equalsIgnoreCase("Sport")){				
				view_holder.tv_type .setText("Sport");
				view_holder.iv_icon.setImageResource(R.drawable.sports);
				if(arr_reason.get(pox).get(2).equalsIgnoreCase("N")){					
					view_holder.cb_validation.setVisibility(View.INVISIBLE);					
				}else{					
					view_holder.cb_validation.setVisibility(View.VISIBLE);	
				}
			}else if(arr_reason.get(pox).get(1).equalsIgnoreCase("Family Vacation")){				
				view_holder.tv_type .setText("Family Vacation");
				view_holder.iv_icon.setImageResource(R.drawable.family_vacation);
				if(arr_reason.get(pox).get(2).equalsIgnoreCase("N")){					
					view_holder.cb_validation.setVisibility(View.INVISIBLE);					
				}else{					
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
			view_holder.cb_validation = (ImageView) view.findViewById(R.id.cb_validation);
			return view_holder;
		}

		/**
		 * View holder class
		 */
		public  class ViewHolder {
			ImageView iv_icon;
			TextView tv_type;
			ImageView cb_validation;
		}
	}
	
	/*Setting profile pic*/
	
	/**
	 * Method to add photo from the Gallery or Camera
	 */
	private void addPhoto() {
		AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(getActivity());
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
	
	/**
	* Method to capture image from device camera
	*/
	private void takePicture() {

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		
		try {
		Uri mImageCaptureUri = null;
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
		mImageCaptureUri = Uri.fromFile(mFileTemp);
		} else {
		/*
		* The solution is taken from here:
		* http://stackoverflow.com/questions
		* /10042695/how-to-get-camera-result-as-a-uri-in-data-folder
		*/
		mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
		}
		intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
		mImageCaptureUri);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	private void openGallery() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
		
	}
	
	/**
	* Method to crop the image
	*/
	private void startCropImage() {
	Intent intent = new Intent(getActivity(), CropImage.class);
	intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
	intent.putExtra(CropImage.SCALE, true);
	intent.putExtra(CropImage.ASPECT_X, 2);
	intent.putExtra(CropImage.ASPECT_Y, 2);
	startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
	} 

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		  if (resultCode != Activity.RESULT_OK) {
			return;
			}

		switch (requestCode) {

		case REQUEST_CODE_GALLERY:

			try {

				InputStream inputStream = getActivity()
						.getContentResolver().openInputStream(data.getData());
				FileOutputStream fileOutputStream = new FileOutputStream(
						mFileTemp);
				copyStream(inputStream, fileOutputStream);
				fileOutputStream.close();
				inputStream.close();
				startCropImage();
				
			} catch (Exception e) {
				Log.e("error", "Error while creating temp file", e);
			}
			
			
			


			break;
		case REQUEST_CODE_TAKE_PICTURE:
			startCropImage();			
			
			
			break;
		case REQUEST_CODE_CROP_IMAGE:

				String selectedImagePath = data.getStringExtra(CropImage.IMAGE_PATH);
				if (selectedImagePath == null) {
				return;
				}
				bitmapx = BitmapFactory.decodeFile(mFileTemp.getPath());
				
				iv_pic.setImageBitmap(CircleImageCreator.getRoundedShapeNew(bitmapx));
				

				
				if (bitmapx != null) {
					Log.e("SET VALUE","TRUE IMAGE IS THERE");
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					bitmapx.compress(Bitmap.CompressFormat.PNG, 100, stream);
					byte[] byteArray = stream.toByteArray();
					ByteArrayBody bytebdy = new ByteArrayBody(byteArray, "pic.png");
					mpEntity.addPart("image", bytebdy);
				} else {
					mpEntity.addTextBody("image", "");
				}
				
						
				
				
				
				
				
				
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
	public void rotateImage(Bitmap bit,int angle){/*
		 Log.e("ANGLEEEEE",angle+"   SDFSFDSFDSF");
		 Matrix matrix = new Matrix();
		 matrix.postRotate((float)angle);
		final Bitmap rotated = Bitmap.createBitmap(bit , 0, 0, bit .getWidth(), bit .getHeight(), matrix, true);
		 iv_pic.setImageBitmap(CircleImageCreator.getRoundedShapeNew(rotated));
		// bitmapx = Bitmap.createBitmap(bit , 0, 0, bit .getWidth(), bit .getHeight(), matrix, false);
		 
		 new Thread(new Runnable() {
			    public void run() {
			    	try{
						 if (rotated != null) {
								Log.e("LAST","CA"+"++++");
								ByteArrayOutputStream stream = new ByteArrayOutputStream();				
								rotated.compress(Bitmap.CompressFormat.PNG, 100, stream);
								byte[] byteArray = stream.toByteArray();
								ByteArrayBody bytebdy = new ByteArrayBody(byteArray, "pic1"	+ ".png");
								mpEntity.addPart("image", bytebdy);
							} else {
								mpEntity.addTextBody("image", "");
							}
					 }catch(Exception e){
						 e.printStackTrace();
					 }
			    }
			  }).start();

	 */}
	

	
	public void updateAccount(){
		
		type="account";
		hideKeyboard();
		String vacancy="";
		
		for (int i = 0; i < arr_reason.size(); i++) {

			if (arr_reason.get(i).get(2).equalsIgnoreCase("Y")) {

				if (vacancy.equalsIgnoreCase("")) {
					int x = i + 1;
					vacancy = String.valueOf(x);
				} else {
					int x = i + 1;
					vacancy = vacancy + "," + String.valueOf(x);
				}
			}

		}
		
		
		mpEntity.addTextBody("user_id", prefs.getString("user_id", ""));
		mpEntity.addTextBody("first_name",tv_name.getText().toString());
		mpEntity.addTextBody("status", tv_status.getText().toString());
		mpEntity.addTextBody("desc", et_desc.getText().toString());
		mpEntity.addTextBody("vacancy_list", vacancy);
		mpEntity.addTextBody("member_type", "1");
		mpEntity.addTextBody("activation_code",prefs.getString("activation_code", ""));
		
		
		
			
		/* if (bitmapx == null) {
			 mpEntity.addTextBody("image", "");
			}*/
		
	/*	if (bitmapx != null) {
			Log.e("LAST",vacancy+"++++");
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmapx.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			ByteArrayBody bytebdy = new ByteArrayBody(byteArray, "pic1"	+ ".png");
			mpEntity.addPart("image", bytebdy);
		} else {
			mpEntity.addTextBody("image", "");
		}*/
		
		if (new CommonActions(getActivity()).isNetworkConnected()) {		
			new AccountTask().execute(2);
		} else {
			progress_dialogue.dismiss();
			new CommonActions(getActivity())
					.showToast("Please check your internet connection");
		}
	}
	private void hideKeyboard() {   
	    // Check if no view has focus:
	  View view = (getActivity()).getCurrentFocus();
	   if (view != null) {
	       InputMethodManager inputManager = (InputMethodManager) (getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
	       inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	        
	        (getActivity()).  getWindow().setSoftInputMode(
	        	    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
}
