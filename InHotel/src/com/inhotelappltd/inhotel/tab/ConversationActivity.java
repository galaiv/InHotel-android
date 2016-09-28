package com.inhotelappltd.inhotel.tab;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.VideoView;

import com.inhotelappltd.inhotel.R;

import com.inhotelappltd.inhotel.common.ListviewImageLoader;
import com.inhotelappltd.inhotel.http.MultipartEntityBuilder;
import com.inhotelappltd.inhotel.logger.Log;
import com.inhotelappltd.inhotel.quickblox.BaseActivity;
import com.inhotelappltd.inhotel.quickblox.Chat;
import com.inhotelappltd.inhotel.quickblox.ChatService;
import com.inhotelappltd.inhotel.quickblox.PrivateChatImpl;
import com.inhotelappltd.inhotel.quickblox.SystemUtils;
import com.inhotelappltd.inhotel.server.ServerConnector;
import com.inhotelappltd.inhotel.utils.CommonActions;
import com.inhotelappltd.inhotel.DrinkViewActivity;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBPrivateChatManager;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialog;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.helper.StringifyArrayList;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.messages.QBMessages;
import com.quickblox.messages.model.QBEnvironment;
import com.quickblox.messages.model.QBEvent;
import com.quickblox.messages.model.QBNotificationType;
import com.quickblox.users.model.QBUser;

public class ConversationActivity extends BaseActivity {
	ArrayList<String> arr_chatArrayList;
	TextView tv_back, tv_title;
	int status, page = 0;
	SharedPreferences prefs;
	String chatID,profID, nav, drinkID, type, status_flag, drink_id;
	Editor edt;
	ListView messagesContainer;
	boolean video_flag = false, vdeo_popup_flag = false;

	ProgressDialog progress_dialogue;
	ArrayList<ArrayList<String>> arr_drink = new ArrayList<ArrayList<String>>();
	ArrayList<String> arr_drink_list;
	ArrayList<String> arr_det = new ArrayList<String>();
	RelativeLayout rl_1, rl_full, rl_totalView, rl_staff;
	Bitmap thumbnail;
	ImageView iv_drink, iv_send, iv_video;
	ChatAdapter adapter;
	private static final String TAG = "CONVERSTION ACTI JJJJOUAS";
	private static final String TAGMSG = "MSG";
	private Chat chat;
	public static final String EXTRA_DIALOG = "dialog";
	private final String PROPERTY_SAVE_TO_HISTORY = "save_to_history";
	QBChatService chatService;;
	private QBDialog dataSource;
	EditText et_msg;
	private static final int READ_REQUEST_CODE = 42;
	private File mFileTemp;
	FrameLayout fl_video;
	VideoView vv_video;
	FrameLayout rl_totalframe;
	MultipartEntityBuilder mpEntity = MultipartEntityBuilder.create();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversation);

		
		 progress_dialogue = new ProgressDialog(ConversationActivity.this);	

			
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		rl_1 = (RelativeLayout) findViewById(R.id.rl_1);
		rl_full = (RelativeLayout) findViewById(R.id.rl_full);
		rl_staff = (RelativeLayout) findViewById(R.id.rl_staff);

		rl_totalView = (RelativeLayout) findViewById(R.id.rl_totalView);
		vv_video = (VideoView) findViewById(R.id.vv_video);
		iv_drink = (ImageView) findViewById(R.id.iv_drink);
		iv_send = (ImageView) findViewById(R.id.iv_send);
		iv_video = (ImageView) findViewById(R.id.iv_video);
		rl_totalframe = (FrameLayout) findViewById(R.id.rl_totalframe);
		arr_chatArrayList = new ArrayList<String>();
		et_msg = (EditText) findViewById(R.id.et_msg);
		fl_video = (FrameLayout) findViewById(R.id.fl_video);
		
		messagesContainer = (ListView) findViewById(R.id.messagesContainer);
		adapter = new ChatAdapter(ConversationActivity.this,
				new ArrayList<QBChatMessage>(), arr_det);
		tv_back.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back_button,
				0, 0, 0);
		tv_back.setText("Back");
		tv_title.setText("Conversation");
		// adp_conv = new ConversationAdapter(ConversationActivity.this,
		// arr_conv);

		iv_video.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, 1);

			}
		});

		iv_drink.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Bundle b = new Bundle();
				Intent intent = new Intent(ConversationActivity.this,
						DrinkViewActivity.class);
				b.putString("profID", profID);
				b.putString("QUicktID", chatID);
				intent.putExtras(b);
				startActivity(intent);
				finish();

			}
		});

		

		et_msg.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					sendMEssage();
					return false;
				} else {
					return true;
				}

			}

		});
		/*
		 * et_msg.setOnKeyListener(new OnKeyListener() {
		 * 
		 * @Override public boolean onKey(View v, int keyCode, KeyEvent event) {
		 * // TODO Auto-generated method stub if(prefs.getString("enter",
		 * "0").equalsIgnoreCase("1")){ sendMEssage(); return false; }else{
		 * return true;
		 * 
		 * }
		 * 
		 * 
		 * }
		 * 
		 * });
		 */
		iv_send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				sendMEssage();
			}
		});
		tv_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();

			}
		});
		

	}
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		prefs = getSharedPreferences("Login Credinals", MODE_PRIVATE);
		edt = prefs.edit();
		
		Log.e("@@@@@@@@@@@@@@@@@@@@@", "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		Log.e("TEST @ Conversation", prefs.getString("QUicktID","")+"- SENDER ");

		
			
			
			profID = prefs.getString("profID","");
			chatID = prefs.getString("QUicktID","");
			nav = prefs.getString("page","");

			if (null != nav) {
				if (nav.equalsIgnoreCase("drink")) {
					drinkID = prefs.getString("drinkID","");

				} else if (nav.equalsIgnoreCase("chat")) {

					if (null !=prefs.getString("type","")) {

						Log.e("GOT IT", "TYPE!!!!!" + prefs.getString("type",""));

						if (prefs.getString("type","").equalsIgnoreCase("staff")) {
							rl_staff.setVisibility(View.GONE);
						} else {
							rl_staff.setVisibility(View.VISIBLE);
						}
					}

				}
			} else {

				nav = "guest";
				profID = prefs.getString("profID_Q", "0");
				chatID = prefs.getString("QUicktID_Q", "0");

			}
			
			
			if (prefs.getString("enter", "0").equalsIgnoreCase("0")) {
				et_msg.setImeOptions(EditorInfo.IME_ACTION_NEXT);
				et_msg.setHorizontallyScrolling(true);
				et_msg.setSingleLine(false);
			} else {
				et_msg.setImeOptions(EditorInfo.IME_ACTION_SEND);
			}
			Log.e("COMVERSIO Before", profID + " ------" + chatID);

			if (new CommonActions(ConversationActivity.this)
					.isNetworkConnected()) {
				progress_dialogue.setMessage("Loading Chat...");
				progress_dialogue.show();
				progress_dialogue.setCanceledOnTouchOutside(false);
				new ChatTask().execute(0);
			} else {
				new CommonActions(ConversationActivity.this)
						.showToast("Please check your internet connection");
			}

		
			ChatService.initIfNeed(this);
		// Register to receive push notifications events
		LocalBroadcastManager.getInstance(this).registerReceiver(mPushReceiver,
				new IntentFilter(CommonActions.NEW_PUSH_EVENT));

		ChatService.initIfNeed(this);
		ChatService.getInstance().addConnectionListener(chatConnectionListener);
		
		
	}
	
	private void initChat() {
		ChatService.initIfNeed(this);
		if (!QBChatService.isInitialized()) {
            QBChatService.init(this);
        }
		
		getDailogue();

		/*
		 * QBUser user = new QBUser(); user.setLogin(SystemUtils.USER_LOGIN);
		 * user.setPassword(SystemUtils.USER_PASSWORD);
		 * QBAuth.createSession(user, new QBEntityCallbackImpl<QBSession>() {
		 * 
		 * @Override public void onSuccess() { // TODO Auto-generated method
		 * stub super.onSuccess(); Log.e("createSession","SUCESS");
		 * progress_dialogue.cancel(); }
		 * 
		 * @Override public void onError(List<String> errors) { // TODO
		 * Auto-generated method stub super.onError(errors);
		 * Log.e("createSession onError","onError"+errors);
		 * progress_dialogue.cancel(); showQiuickbloxReconnect(); }
		 * 
		 * });
		 */

		Log.e("initChat", "initChat CALLED >>>>>" + nav);
	}

	public void showQiuickbloxReconnect() {
		ConversationActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				AlertDialog.Builder dialog = new AlertDialog.Builder(
						ConversationActivity.this);
				dialog.setPositiveButton("Reconnect",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();
								initChat();
							}
						});
				dialog.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.cancel();

							}
						});
				dialog.setMessage("Chat Server is not connecting !! ");
				dialog.show();
			}
		});
	}

	// Our handler for received Intents.
	//
	private BroadcastReceiver mPushReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			Log.e("BroadcastReceiver","BroadcastReceiver"+intent);
			// Get extra data included in the Intent
			/*String message = intent.getStringExtra(CommonActions.EXTRA_MESSAGE);
			String type = intent.getStringExtra("type");
			profID = intent.getStringExtra("profID");
			chatID = intent.getStringExtra("QUicktID");
			nav = intent.getStringExtra("page");*/
			
			
			/*edt.putString("d_msg", msg_qblx);
			   edt.putString("d_type", msg_qblx);
			   edt.putString("d_profID", msg_qblx);
			   edt.putString("d_QUicktID", msg_qblx);
			   edt.putString("d_page", msg_qblx);*/
			
			
			String message =prefs.getString("d_msg", "");
			String type = prefs.getString("d_type", "");
			profID = prefs.getString("d_profID", "");
			chatID = prefs.getString("d_QUicktID", "");
			nav = prefs.getString("d_page", "");
			
			
			if(nav.equalsIgnoreCase("drink")){
				
			}

			Log.e(TAG, chatID+"!!!!!!!!!!!!!!!!!!!!Receiving event PUSH INTENT "
					+ type);
		
				showMessage(null, false);
			

		}
	};

	public void getDailogue() {
		//
              Log.e("getDailogue starts",chatID+" null");
              
            SystemUtils.USER_LOGIN="inhotel_"+prefs.getString("user_id", "");
      		SystemUtils.USER_PASSWORD="inhotel_" + prefs.getString("user_id", "") +"123";
      		
      		if(chatID!=null){
      		  
                final QBUser user = new QBUser(SystemUtils.USER_LOGIN, SystemUtils.USER_PASSWORD);
                QBAuth.createSession(user, new QBEntityCallbackImpl<QBSession>() {
                    @Override
                    public void onSuccess(QBSession session, Bundle params) {
                  	  
                  	  QBPrivateChatManager privateChatManager = QBChatService.getInstance()
                				.getPrivateChatManager();
                		privateChatManager.createDialog(Integer.valueOf(chatID),
                				new QBEntityCallbackImpl<QBDialog>() {
                					@Override
                					public void onSuccess(QBDialog dialog, Bundle args) {
                						Log.e("getDailogue onSuccess", "onSuccess");
                						dataSource = dialog;
                						Integer opponentID = ChatService.getInstance()
                								.getOpponentIDForPrivateDialog(dialog);

                						CommonActions.MY_QUICKBLOX = ChatService.getInstance()
                								.getCurrentUser().getId();
                						chat = new PrivateChatImpl(ConversationActivity.this,
                								opponentID);

                						Log.e("getDailogue", drinkID + "getDailogue>>>>>" + nav);
                						if (nav.equalsIgnoreCase("drink")) {

                							nav = "";

                							if (!drinkID.equalsIgnoreCase("")
                									&& !drinkID.equalsIgnoreCase(null)) {
                								drink_toQuickblox();
                							} else {
                								loadChatHistory();
                							}

                						} else {
                							loadChatHistory();
                						}

                					}

                					@Override
                					public void onError(List<String> errors) {
                						progress_dialogue.cancel();
                						Log.e("getDailogue", errors + "errors");
                						//showQiuickbloxReconnect();
                					}
                				});
                    }
                 
                    @Override
                    public void onError(List<String> errors) {
                  	  progress_dialogue.cancel();
  						Log.e("getDailogu@e", errors + "errors");
  						//showQiuickbloxReconnect();
                    }
                });      
      		}else{
      		  progress_dialogue.cancel();
      		}       
        
	}

	
	private void loadChatHistory() {
		QBRequestGetBuilder customObjectRequestBuilder = new QBRequestGetBuilder();
		customObjectRequestBuilder.setPagesLimit(100);
		customObjectRequestBuilder.sortDesc("date_sent");
		QBDialog qbDialog = new QBDialog(dataSource.getDialogId().toString());

		QBChatService.getDialogMessages(qbDialog, customObjectRequestBuilder,
				new QBEntityCallbackImpl<ArrayList<QBChatMessage>>() {
					@Override
					public void onSuccess(ArrayList<QBChatMessage> messages,
							Bundle args) {
						Log.e("HISTORT onSuccess", "HISTORY onSuccess");
						progress_dialogue.dismiss();
						adapter = new ChatAdapter(ConversationActivity.this,
								new ArrayList<QBChatMessage>(), arr_det);
						messagesContainer.setAdapter(adapter);

						for (int i = messages.size() - 1; i >= 0; --i) {
							QBChatMessage msg = messages.get(i);

							Log.e("HISTORT", "HISTORY" + msg);
							showMessage(msg, true);
						}

					}

					@Override
					public void onError(List<String> errors) {
						Log.e("HISTORT onError", "HISTORY" + errors);
						progress_dialogue.dismiss();
						adapter = null;
						adapter.notifyDataSetChanged();

					}
				});

	}

	ConnectionListener chatConnectionListener = new ConnectionListener() {
		@Override
		public void connected(XMPPConnection connection) {
			Log.e(TAG, "connected");
		}

		@Override
		public void authenticated(XMPPConnection connection) {
			Log.e(TAG, "authenticated");
		}

		@Override
		public void connectionClosed() {
			Log.e(TAG, "connectionClosed");
		}

		@Override
		public void connectionClosedOnError(final Exception e) {
			Log.e(TAG, "connectionClosedOnError: " + e.getLocalizedMessage());

		}

		@Override
		public void reconnectingIn(final int seconds) {
			if (seconds % 5 == 0) {
				Log.e(TAG, "reconnectingIn: " + seconds);
			}
		}

		@Override
		public void reconnectionSuccessful() {
			Log.e(TAG, "reconnectionSuccessful");

		}

		@Override
		public void reconnectionFailed(final Exception error) {
			Log.e(TAG, "reconnectionFailed: " + error.getLocalizedMessage());
		}
	};

	@Override
	public void onStartSessionRecreation() {

	}

	@Override
	public void onFinishSessionRecreation(final boolean success) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				nav = "";
				new ChatTask().execute(0);
				/*
				 * if (success) { initChat(); }
				 */
			}
		});
	}

	public void sendMEssage() {
		hideKeyboard();
		// 3161436
		// chat = new PrivateChatImpl(ConversationActivity.this, 3161436);

		String messageText = et_msg.getText().toString();
		if (TextUtils.isEmpty(messageText)) {
			return;
		}

		// Send chat message
		//
		QBChatMessage chatMessage = new QBChatMessage();
		chatMessage.setBody(messageText);
		chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
		chatMessage.setDateSent(new Date().getTime() / 1000);

		try {
			chat.sendMessage(chatMessage);
		} catch (XMPPException e) {
			Log.e(TAG, "failed to send a message", e);
		} catch (SmackException sme) {
			Log.e(TAG, "failed to send a message", sme);
		}

		pushMessage(arr_det.get(1) + " : " + messageText, "MSG",false);
		et_msg.setText("");

		showMessage(chatMessage, true);
		progress_dialogue.cancel();

	}

	private void hideKeyboard() {
		// Check if no view has focus:
		View view = ((Activity) ConversationActivity.this).getCurrentFocus();
		if (view != null) {
			InputMethodManager inputManager = (InputMethodManager) ((Activity) ConversationActivity.this)
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(view.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
		}

		((Activity) ConversationActivity.this).getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	public void showMessage(QBChatMessage message, boolean flg) {

		if (flg) {
			adapter.add(message);
		} else {
			new ChatTask().execute(0);
		}

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
				scrollDown();
			}
		});
	}

	private void scrollDown() {
		messagesContainer.setSelection(messagesContainer.getCount() - 1);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// chatService.destroy();
		ChatService.getInstance().removeConnectionListener(
				chatConnectionListener);
	}

	@Override
	public void onBackPressed() {

		if (!vdeo_popup_flag) {
			if (video_flag) {
				video_flag = false;
				vv_video.stopPlayback();

				rl_full.setVisibility(View.VISIBLE);
				fl_video.setVisibility(View.GONE);

				adapter.notifyDataSetChanged();
			} else {
				try {
					if (chat != null) {
						chat.release();
					}

				} catch (XMPPException e) {
					Log.e(TAG, "failed to release chat", e);
				}
				super.onBackPressed();
			}

		}

	}

	/**
	 * Task for downloading Menu management data
	 * 
	 * @author joyal
	 * 
	 */
	public class ChatTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected String doInBackground(Integer... params) {

			status = params[0];
			if (status == 0) {
				return new ServerConnector(ConversationActivity.this)
						.getResponseFromServer(
								CommonActions.appURL,
								"MessageDetails",
								"{\"to_id\": \""
										+ prefs.getString("user_id", "")
										+ "\"," + "\"from_id\": \"" + profID
										+ "\"," + "\"pageNo\":\"" + 0 + "\" }",
								"\"\"");
			} else if (status == 1) {
				return new ServerConnector(ConversationActivity.this)
						.doRegistration(type, mpEntity);
			} else if (status == 2) {
				return new ServerConnector(ConversationActivity.this)
						.getResponseFromServer(
								CommonActions.appURL,
								"updateDrinksStatus_ad",
								"{\"status_flag\": \"" + status_flag + "\","
										+ "\"drink_id\": \"" + drink_id + "\"}",
								"\"\"");
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			try {

				if (status == 0) {

					JSONObject jobj = new JSONObject(result);
					JSONObject jarr_det = jobj.getJSONObject("user_details");

					if (jobj.getString("to_room_status").equalsIgnoreCase("Y")
							&& jobj.getString("from_room_status")
									.equalsIgnoreCase("Y")) {
						iv_drink.setVisibility(View.VISIBLE);
					} else {
						iv_drink.setVisibility(View.INVISIBLE);
					}

					arr_det = new ArrayList<String>();

					arr_det.add(jarr_det.getString("to_user"));// toID
					arr_det.add(jarr_det.getString("to_name"));// toname
					arr_det.add(jarr_det.getString("to_image"));// toimage
					arr_det.add(jarr_det.getString("from_user"));// fromid
					arr_det.add(jarr_det.getString("from_name"));// fromname
					arr_det.add(jarr_det.getString("from_image"));// fromimage

					JSONArray jarr_drink = jobj.getJSONArray("details");
					JSONObject jobj_drink = new JSONObject();
					JSONObject jobj_drink_offer;
					arr_drink = new ArrayList<ArrayList<String>>();

					if (jarr_drink.length() > 0) {
						for (int i = 0; i < jarr_drink.length(); i++) {
							jobj_drink = (JSONObject) jarr_drink.get(i);
							arr_drink_list = new ArrayList<String>();

							if (!jobj_drink.getString("drink_offerings_id")
									.equalsIgnoreCase("")
									|| !jobj_drink.getString(
											"drink_offerings_id")
											.equalsIgnoreCase("0")) {

								jobj_drink_offer = jobj_drink
										.getJSONObject("drink_offer");

								arr_drink_list.add(jobj_drink
										.getString("message_id"));// message_id
								arr_drink_list.add(jobj_drink
										.getString("drink_offerings_id"));// drink_offerings_id

								arr_drink_list.add(jobj_drink_offer
										.getString("to_user_id"));// to user
								arr_drink_list.add(jobj_drink_offer
										.getString("user_id"));// from user
								arr_drink_list.add(jobj_drink_offer
										.getString("drink_title"));// drink_title
								arr_drink_list.add(jobj_drink_offer
										.getString("image"));// image
								arr_drink_list.add(jobj_drink_offer
										.getString("status_flag"));// status_flag
								/*
								 * arr_drink_list.add(jobj_drink.getString(
								 * "from_name"));//from_name
								 * arr_drink_list.add(jobj_drink
								 * .getString("to_name"));//from_name
								 */
								arr_drink.add(arr_drink_list);

							}

						}

					}

					if (isSessionActive()) {

						initChat();
					}

				} else if (status == 1) {
					new ChatTask().execute(0);

					/*
					 * if (isSessionActive()) { initChat(); }
					 */
				} else if (status == 2) {
					pushMessage("","accept_reject_drink" ,false);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				gotServerError("Database error. Try again");
			}

		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		// The ACTION_OPEN_DOCUMENT intent was sent with the request code
		// READ_REQUEST_CODE. If the request code seen here doesn't match, it's
		// the
		// response to some other intent, and the code below shouldn't run at
		// all.

		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {
				// String selectedVideoPath = getPath(data.getData());
				showVideoPreview(data.getData());

			}
		}

	}

	public void drink_toQuickblox() {
		nav = "";
		// Send chat message
		/*
		 * Integer opponentID = ChatService.getInstance()
		 * .getOpponentIDForPrivateDialog(dataSource); chat = new
		 * PrivateChatImpl(ConversationActivity.this, opponentID);
		 */

		QBChatMessage chatMessage = new QBChatMessage();
		chatMessage.setBody("Sent a drink");
		chatMessage.setProperty(PROPERTY_SAVE_TO_HISTORY, "1");
		chatMessage.setDateSent(new Date().getTime() / 1000);

		try {
			chat.sendMessage(chatMessage);
		} catch (XMPPException e) {
			Log.e(TAG, "failed to send a message", e);
		} catch (SmackException sme) {
			Log.e(TAG, "failed to send a message", sme);
		}

		et_msg.setText("");

		type = "drink";

		if (!chatMessage.getId().equalsIgnoreCase("")) {

			Log.e("DRINK",
					prefs.getString("user_id", "") + " TOID" + profID
							+ " drinkID " + drinkID + " message_id"
							+ chatMessage.getId());

			mpEntity.addTextBody("from_id", prefs.getString("user_id", ""));
			mpEntity.addTextBody("to_id", profID);
			mpEntity.addTextBody("message", "");
			mpEntity.addTextBody("drinks_id", drinkID);
			mpEntity.addTextBody("message_id", chatMessage.getId());
			mpEntity.addTextBody("quantity", "1");

			new ChatTask().execute(1);
		}

		pushMessage(arr_det.get(1) + ": Sent a drink.","MSG", false);

	}

	public void showVideoPreview(final Uri path) {
		vdeo_popup_flag = true;
		rl_full.setVisibility(View.GONE);
		rl_totalframe.setVisibility(View.VISIBLE);

		View myView = getLayoutInflater().inflate(R.layout.custom_video_popup,
				null);

		rl_totalframe.addView(myView);

		/*
		 * final Dialog dialog = new
		 * Dialog(ConversationActivity.this,android.R.style
		 * .Theme_DeviceDefault);
		 * dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 * dialog.setCanceledOnTouchOutside(false);
		 * dialog.getWindow().setBackgroundDrawable( new
		 * ColorDrawable(Color.TRANSPARENT));
		 * dialog.getWindow().clearFlags(LayoutParams.FLAG_DIM_BEHIND);
		 * dialog.setContentView
		 * (ConversationActivity.this.getLayoutInflater().inflate(
		 * R.layout.custom_video_popup, null));
		 */
		final VideoView vv_video_popup = (VideoView) myView
				.findViewById(R.id.vv_video);

		TextView tv_cancel_v = (TextView) myView.findViewById(R.id.tv_back);
		TextView tv_upload_v = (TextView) myView.findViewById(R.id.tv_save);
		TextView tv_title_v = (TextView) myView.findViewById(R.id.tv_title);
		tv_cancel_v.setText("Cancel");
		tv_title_v.setText("Video");
		tv_upload_v.setText("Upload");

		vv_video_popup.setVideoURI(path);
		vv_video_popup.requestFocus();
		vv_video_popup.setMediaController(new MediaController(
				ConversationActivity.this));
		vv_video_popup.setZOrderOnTop(true);
		vv_video_popup.setVisibility(View.VISIBLE);

		/* vv_video.setOnCompletionListener(this); */

		vv_video_popup.setOnPreparedListener(new OnPreparedListener() {
			public void onPrepared(MediaPlayer mp) {
				vv_video_popup.start();

			}
		});

		tv_upload_v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// dialog.cancel();
				rl_full.setVisibility(View.VISIBLE);
				rl_totalframe.setVisibility(View.GONE);
				vv_video_popup.setVisibility(View.GONE);
				vdeo_popup_flag = false;

				progress_dialogue.setMessage("Uploading...");
				progress_dialogue.show();
				progress_dialogue.setCanceledOnTouchOutside(false);			
				
				if (getPath(path)!=null) {
					video_upload_quickblox(getPath(path));
				}
			}
		});
		
		

		tv_cancel_v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				rl_full.setVisibility(View.VISIBLE);
				rl_totalframe.setVisibility(View.GONE);
				vv_video_popup.setVisibility(View.GONE);
				vdeo_popup_flag = false;

				vv_video_popup.stopPlayback();
				// dialog.cancel();
			}
		});

		// dialog.show();
	}

	public void video_upload_quickblox(final String path) {
		// File file = new File(data.getData().toString());
			Log.e("path", path);
		File VideoPhoto = new File(path);
		Boolean fileIsPublic = false;
		
		QBContent.uploadFileTask(VideoPhoto, fileIsPublic, null,
				new QBEntityCallbackImpl<QBFile>() {
					@Override
					public void onSuccess(QBFile file, Bundle params) {

						Log.e("UPLOADED VIDEO GO FOR MSG", "VIDEO_MSG" + file);
						String state = Environment.getExternalStorageState();
						if (Environment.MEDIA_MOUNTED.equals(state)) {
							mFileTemp = new File(Environment
									.getExternalStorageDirectory(), "InHotel");

							if (!mFileTemp.exists()) {
								mFileTemp.mkdir();
							}

							File myFile = new File(mFileTemp + "/"
									+ file.getId() + ".mp4");

							try {
								FileInputStream fis = new FileInputStream(path);

								byte[] buffer = new byte[1024];
								int bufferLength = 0;
								int downloadedSize = 0;
								FileOutputStream fileOutput;

								fileOutput = new FileOutputStream(myFile);

								// now, read through the input buffer and write
								// the
								// contents to the file
								while ((bufferLength = fis.read(buffer)) > 0) {
									// add the data in the buffer to the file in
									// the
									// file output stream (the file on the sd
									// card
									fileOutput.write(buffer, 0, bufferLength);
									// add up the size so we know how much is
									// downloaded
									downloadedSize += bufferLength;
									// this is where you would do something to
									// report the prgress, like this maybe
									// updateProgress(downloadedSize,
									// totalSize);

								}
								fileOutput.close();

							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (Exception ex) {
								System.out.println("ex: " + ex);
							}

						} else {
							// alert("Please insert SD Card to download this file. ");

						}

						// create a message
						QBChatMessage chatMessage = new QBChatMessage();
						chatMessage.setProperty("save_to_history", "1");
						chatMessage.setBody("Sent a Video");
						// attach a photo
						QBAttachment attachment = new QBAttachment("video");
						attachment.setId(file.getId().toString());
						chatMessage.addAttachment(attachment);

						try {
							chat.sendMessage(chatMessage);
						} catch (XMPPException e) {
							Log.e(TAG, "failed to send a message", e);
						} catch (SmackException sme) {
							Log.e(TAG, "failed to send a message", sme);
						}

						et_msg.setText("");

						pushMessage(arr_det.get(1) + ": Sent a Video.","MSG", false);
						showMessage(chatMessage, false);
						// progress_dialogue.cancel();
						// send a message
						// ...
					}

					@Override
					public void onError(List<String> errors) {
						// error

						progress_dialogue.cancel();

						new CommonActions(ConversationActivity.this)
								.showToast("Something went wrong !! Please try again");

					}
				});
	}

	/**
	 * Returns the path of the given uri
	 * 
	 * @param uri
	 * @return the path of the passed uri
	 */
	private String getPath(Uri uri) {
		String[] projection = { MediaStore.Video.Media.DATA,
				MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		cursor.moveToFirst();
		String filePath = cursor.getString(cursor
				.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
		int fileSize = cursor.getInt(cursor
				.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
		long duration = TimeUnit.MILLISECONDS.toSeconds(cursor.getInt(cursor
				.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)));

		return filePath;
	}

	/*
	 * Method to manage the exeption occured due to server down
	 */
	public void gotServerError(String msg) {
		if (progress_dialogue.isShowing()) {
			progress_dialogue.cancel();
		}
		new AlertDialog.Builder(ConversationActivity.this).setTitle("Message")
				.setMessage(msg)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})

				.show();
	}

	public void playVideo(Uri uri) {

		try {
			video_flag = true;

			rl_full.setVisibility(View.GONE);
			fl_video.setVisibility(View.VISIBLE);
			/*
			 * MediaPlayer mediaPlayer = MediaPlayer.create(this, uri);
			 * 
			 * mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			 * mediaPlayer.start();
			 */

			DisplayMetrics dm = new DisplayMetrics();
			this.getWindowManager().getDefaultDisplay().getMetrics(dm);
			int height = dm.heightPixels;
			int width = dm.widthPixels;
			vv_video.setMinimumWidth(width);

			vv_video.setMinimumHeight(height);
			vv_video.requestFocus();
			vv_video.setVideoURI(uri);
			vv_video.setMediaController(new MediaController(
					ConversationActivity.this));

			vv_video.setOnPreparedListener(new OnPreparedListener() {
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					vv_video.start();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// ***************************************ADAPTER*******************************************

	public class ChatAdapter extends BaseAdapter {

		public List<QBChatMessage> chatMessages;
		private Activity context;
		ArrayList<String> arr_det;
		SimpleDateFormat df = new SimpleDateFormat("MM-dd-yyyy");
		Calendar c = Calendar.getInstance();
		boolean my_drink_flg = false;;
		QBChatMessage chatMessage_each = null;
		Integer fileId;

		public ChatAdapter(Activity context, List<QBChatMessage> chatMessages,
				ArrayList<String> arr_det) {
			this.context = context;
			this.chatMessages = chatMessages;
			this.arr_det = arr_det;
		}

		@Override
		public int getCount() {
			if (chatMessages != null) {
				return chatMessages.size();
			} else {
				return 0;
			}
		}

		@Override
		public QBChatMessage getItem(int position) {
			if (chatMessages != null) {
				return chatMessages.get(position);
			} else {
				return null;
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder holder;
			chatMessage_each = getItem(position);
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if (convertView == null) {
				convertView = vi.inflate(R.layout.list_item_message, null);
				holder = createViewHolder(convertView);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			QBUser currentUser = ChatService.getInstance().getCurrentUser();

			boolean isOutgoing = chatMessage_each.getSenderId() == null
					|| chatMessage_each.getSenderId().equals(
							currentUser.getId());

			for (int j = 0; j < arr_drink.size(); j++) {
				if (chatMessage_each.getId().equalsIgnoreCase(
						arr_drink.get(j).get(0))) {
					my_drink_flg = true;
					setAlignment(holder, isOutgoing, true, arr_drink.get(j),
							position);
					break;
				} else {
					my_drink_flg = false;
				}

			}

			if (!my_drink_flg) {
				setAlignment(holder, isOutgoing, false, null, position);
			}

			holder.tv_cht_me_time.setText(getTimeText(chatMessage_each));

			if (null != chatMessage_each.getAttachments()) {
				if (chatMessage_each.getAttachments().size() > 0) {

					Log.e("COMING VIDEO", chatMessage_each + "!!!!");

					holder.tv_cht_me_message.setText("Sent a Video.");
					holder.rl_me_video.setVisibility(View.VISIBLE);

					holder.rl_me_video
							.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {

									QBChatMessage msg_clicked = adapter
											.getItem(position);
									Log.e("Video click", "Clicked"
											+ msg_clicked);
									for (QBAttachment attachment : msg_clicked
											.getAttachments()) {
										fileId = Integer.valueOf(attachment
												.getId());
										Log.e("Video clickdsf", "Clicked "
												+ fileId);

										String state = Environment
												.getExternalStorageState();
										if (Environment.MEDIA_MOUNTED
												.equals(state)) {
											mFileTemp = new File(
													Environment
															.getExternalStorageDirectory(),
													"InHotel");

											if (!mFileTemp.exists()) {

												mFileTemp.mkdir();

											}

											File myFile = new File(mFileTemp
													+ "/" + fileId + ".3gp");

											if (myFile.exists()) {

												playVideo(Uri.fromFile(myFile));
											} else {

												// Download the video from
												// quickblox
												progress_dialogue.setMessage("Downloading...");
												progress_dialogue.show();
												progress_dialogue
														.setCanceledOnTouchOutside(false);
												download_vdo_quickblox(fileId,
														true);
											}

										} else {
											alert("Please insert SD Card to download this file. ");

										}

									}
									Log.e("Video click", "C################");
								}

							});
				} else {
					holder.tv_cht_me_message.setVisibility(View.VISIBLE);
					holder.rl_me_video.setVisibility(View.GONE);
				}

			} else {
				holder.tv_cht_me_message.setVisibility(View.VISIBLE);
				holder.rl_me_video.setVisibility(View.GONE);
			}

			holder.iv_nothanks.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					holder.iv_accept.setVisibility(View.INVISIBLE);
					holder.iv_nothanks.setVisibility(View.INVISIBLE);

					for (ArrayList<String> user : arr_drink) {

						if (user.get(0).equalsIgnoreCase(
								chatMessages.get(position).getId())) {

							drink_id = user.get(1);
							status_flag = "D";

							user.set(6, "D");

							new ChatTask().execute(2);
							break;
						}

					}

				}
			});
			holder.iv_accept.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					holder.iv_accept.setVisibility(View.INVISIBLE);
					holder.iv_nothanks.setVisibility(View.INVISIBLE);

					for (ArrayList<String> user : arr_drink) {

						if (user.get(0).equalsIgnoreCase(
								chatMessages.get(position).getId())) {

							drink_id = user.get(1);
							status_flag = "A";
							user.set(6, "A");
							new ChatTask().execute(2);
							break;
						}

					}

				}
			});

			return convertView;
		}

		public void add(QBChatMessage message) {
			chatMessages.add(message);
		}

		public void add(List<QBChatMessage> messages) {
			chatMessages.addAll(messages);
		}

		private void setAlignment(final ViewHolder holder, boolean isOutgoing,
				boolean drink, final ArrayList<String> drink_detail,
				final int pox) {
			if (!isOutgoing) {
				holder.contentWithBG
						.setBackgroundResource(R.drawable.bubbl_you);

				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.contentWithBG
						.getLayoutParams();
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				layoutParams.setMargins((int) context.getResources()
						.getDimension(R.dimen.y_bg_left), 0, (int) context
						.getResources().getDimension(R.dimen.y_right), 0);
				holder.contentWithBG.setLayoutParams(layoutParams);

				RelativeLayout.LayoutParams lp_iv = (RelativeLayout.LayoutParams) holder.iv_profile
						.getLayoutParams();
				lp_iv.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
				lp_iv.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				lp_iv.setMargins(0, 0, 15, 0);
				holder.iv_profile.setLayoutParams(lp_iv);

				RelativeLayout.LayoutParams lp_name = (RelativeLayout.LayoutParams) holder.tv_cht_me_name
						.getLayoutParams();
				lp_name.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
				lp_name.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				lp_name.setMargins(
						(int) context.getResources().getDimension(
								R.dimen.margin_10),
						(int) context.getResources().getDimension(
								R.dimen.margin_10), 0, 0);
				holder.tv_cht_me_name.setLayoutParams(lp_name);

				lp_name = (RelativeLayout.LayoutParams) holder.tv_cht_me_message
						.getLayoutParams();
				lp_name.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
				lp_name.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				lp_name.setMargins(
						(int) context.getResources().getDimension(
								R.dimen.margin_10),
						(int) context.getResources().getDimension(
								R.dimen.margin_10),
						(int) context.getResources().getDimension(
								R.dimen.margin_20), 10);
				holder.tv_cht_me_message.setLayoutParams(lp_name);

				lp_name = (RelativeLayout.LayoutParams) holder.tv_cht_me_time
						.getLayoutParams();
				lp_name.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
				lp_name.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				lp_name.setMargins(
						(int) context.getResources().getDimension(
								R.dimen.margin_10),
						(int) context.getResources().getDimension(
								R.dimen.margin_10),
						(int) context.getResources().getDimension(
								R.dimen.margin_20), 0);
				holder.tv_cht_me_time.setLayoutParams(lp_name);

				holder.tv_cht_me_name.setTextColor(R.color.chat_line1_you);
				holder.tv_cht_me_time.setTextColor(R.color.chat_line1_you);
				holder.tv_cht_me_name.setText(arr_det.get(4));
				new ListviewImageLoader(context).loadImage(holder.iv_profile,
						arr_det.get(5));
				holder.tv_cht_me_message.setText(Uri
						.decode(((QBChatMessage) chatMessage_each).getBody()));
				if (drink) {
					holder.rl_drink.setVisibility(View.VISIBLE);
					holder.tv_name_drink.setText(drink_detail.get(4));
					new ListviewImageLoader(context).loadImage(holder.iv_drink,
							drink_detail.get(5));

					holder.tv_cht_me_message.setText("offered a drink");
					/*
					 * if (drink_detail.get(6).equalsIgnoreCase("A")) {
					 * 
					 * }else if (drink_detail.get(6).equalsIgnoreCase("D")) {
					 * 
					 * }else
					 */

					if (drink_detail.get(6).equalsIgnoreCase("P")) {
						holder.iv_accept.setVisibility(View.VISIBLE);
						holder.iv_nothanks.setVisibility(View.VISIBLE);
					} else {
						holder.iv_accept.setVisibility(View.INVISIBLE);
						holder.iv_nothanks.setVisibility(View.INVISIBLE);
					}
				} else {
					holder.rl_drink.setVisibility(View.GONE);
				}

			} else {
				holder.contentWithBG.setBackgroundResource(R.drawable.bubbl_me);

				RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.contentWithBG
						.getLayoutParams();
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
				layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				layoutParams.setMargins((int) context.getResources()
						.getDimension(R.dimen.y_right), 0, (int) context
						.getResources().getDimension(R.dimen.y_bg_left), 0);
				holder.contentWithBG.setLayoutParams(layoutParams);

				/*
				 * RelativeLayout.LayoutParams lp =
				 * (RelativeLayout.LayoutParams)
				 * holder.content.getLayoutParams();
				 * lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
				 * lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				 * holder.content.setLayoutParams(lp);
				 */

				RelativeLayout.LayoutParams lp_iv = (RelativeLayout.LayoutParams) holder.iv_profile
						.getLayoutParams();
				lp_iv.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
				lp_iv.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				lp_iv.setMargins(15, 0, 0, 0);
				holder.iv_profile.setLayoutParams(lp_iv);

				RelativeLayout.LayoutParams lp_name = (RelativeLayout.LayoutParams) holder.tv_cht_me_name
						.getLayoutParams();
				lp_name.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
				lp_name.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				lp_name.setMargins(22, (int) context.getResources()
						.getDimension(R.dimen.margin_10), (int) context
						.getResources().getDimension(R.dimen.margin_10), 0);
				holder.tv_cht_me_name.setLayoutParams(lp_name);

				lp_name = (RelativeLayout.LayoutParams) holder.tv_cht_me_message
						.getLayoutParams();
				lp_name.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
				lp_name.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				lp_name.setMargins(30, (int) context.getResources()
						.getDimension(R.dimen.margin_10), (int) context
						.getResources().getDimension(R.dimen.margin_10), 0);
				holder.tv_cht_me_message.setLayoutParams(lp_name);

				lp_name = (RelativeLayout.LayoutParams) holder.tv_cht_me_time
						.getLayoutParams();
				lp_name.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
				lp_name.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				lp_name.setMargins(
						(int) context.getResources().getDimension(
								R.dimen.margin_20),
						(int) context.getResources().getDimension(
								R.dimen.margin_10),
						(int) context.getResources().getDimension(
								R.dimen.margin_10), 0);
				holder.tv_cht_me_time.setLayoutParams(lp_name);

				holder.tv_cht_me_name.setTextColor(R.color.chat_line1_me);
				holder.tv_cht_me_time.setTextColor(R.color.chat_line1_me);
				holder.tv_cht_me_name.setText(arr_det.get(1));
				new ListviewImageLoader(context).loadImage(holder.iv_profile,
						arr_det.get(2));

				/*
				 * image-5 status_flag -6 drink_title -4 drink_offerings_id -1
				 */
				if (drink) {
					holder.rl_drink.setVisibility(View.VISIBLE);
					holder.tv_name_drink.setText(drink_detail.get(4));
					new ListviewImageLoader(context).loadImage(holder.iv_drink,
							drink_detail.get(5));

					holder.iv_accept.setVisibility(View.INVISIBLE);
					holder.iv_nothanks.setVisibility(View.INVISIBLE);

					if (drink_detail.get(6).equalsIgnoreCase("A")) {
						holder.tv_cht_me_message
								.setText("Accepted the drink offer");
					} else if (drink_detail.get(6).equalsIgnoreCase("D")) {
						holder.tv_cht_me_message.setText(arr_det.get(4)
								+ " said Thanks, but no thanks");
					} else if (drink_detail.get(6).equalsIgnoreCase("P")) {
						holder.tv_cht_me_message.setText("offered a drink");
					} else {
						holder.tv_cht_me_message.setText(Uri
								.decode(((QBChatMessage) chatMessage_each)
										.getBody()));
					}

					/*
					 * if (arr_chat.get(position).get(13).equalsIgnoreCase("A"))
					 * { view_holder.tv_cht_me_message
					 * .setText("Accepted the drink offer"); } else if
					 * (arr_chat.get(position).get(13) .equalsIgnoreCase("D")) {
					 * view_holder.tv_cht_me_message.setText(arr_chat.get(
					 * position).get(3) + " said Thanks, but no thanks"); } else
					 * if (arr_chat.get(position).get(13)
					 * .equalsIgnoreCase("P")) { view_holder.tv_cht_me_message
					 * .setText( arr_chat.get(position).get(3)+
					 * "You offered a drink"); }
					 */

				} else {
					holder.rl_drink.setVisibility(View.GONE);
					holder.tv_cht_me_message.setText(Uri
							.decode(((QBChatMessage) chatMessage_each)
									.getBody()));
				}

			}

		}

		private ViewHolder createViewHolder(View v) {
			ViewHolder holder = new ViewHolder();
			holder.contentWithBG = (RelativeLayout) v
					.findViewById(R.id.contentWithBG);
			holder.rl_drink = (RelativeLayout) v.findViewById(R.id.rl_drink);

			holder.rl_me_video = (RelativeLayout) v
					.findViewById(R.id.rl_me_video);
			holder.iv_profile = (ImageView) v.findViewById(R.id.iv_profile);
			holder.tv_cht_me_name = (TextView) v
					.findViewById(R.id.tv_cht_me_name);
			holder.tv_cht_me_message = (TextView) v
					.findViewById(R.id.tv_cht_me_message);
			holder.tv_cht_me_time = (TextView) v
					.findViewById(R.id.tv_cht_me_time);
			holder.iv_drink = (ImageView) v.findViewById(R.id.iv_drink);
			holder.tv_name_drink = (TextView) v
					.findViewById(R.id.tv_name_drink);
			holder.iv_accept = (ImageView) v.findViewById(R.id.iv_accept);
			holder.iv_nothanks = (ImageView) v.findViewById(R.id.iv_thanks);

			return holder;
		}

		private String getTimeText(QBChatMessage message) {

			String str[], str1[];
			if (message.getProperties().get("updated_at") == null
					|| message.getProperties().get("updated_at").equals("")) {

				return df.format(c.getTime());
			} else {
				str = message.getProperties().get("updated_at").toString()
						.split("T");

				str1 = str[0].split("-");

				return (str1[1] + "-" + str1[2] + "-" + str1[0]);

			}

		}

		private class ViewHolder {

			public LinearLayout content;
			public RelativeLayout contentWithBG, rl_me_video, rl_drink;
			public TextView tv_cht_me_name, tv_cht_me_message, tv_cht_me_time,
					tv_name_drink;
			ImageView iv_profile, iv_drink, iv_accept, iv_nothanks;

		}
	}

	public void download_vdo_quickblox(final Integer fileId, final boolean flg) {
		// download a file
		QBContent.downloadFileTask(fileId,
				new QBEntityCallbackImpl<InputStream>() {
					@Override
					public void onSuccess(InputStream inputStream, Bundle params) {

						File file = null;
						try {
							file = new File(mFileTemp, fileId + ".3gp");
							file.createNewFile();

							byte[] buffer = new byte[1024];
							int bufferLength = 0;
							int downloadedSize = 0;
							FileOutputStream fileOutput;

							fileOutput = new FileOutputStream(file);

							// now, read through the input buffer and write the
							// contents to the file
							while ((bufferLength = inputStream.read(buffer)) > 0) {
								// add the data in the buffer to the file in the
								// file output stream (the file on the sd card
								fileOutput.write(buffer, 0, bufferLength);
								// add up the size so we know how much is
								// downloaded
								downloadedSize += bufferLength;
								// this is where you would do something to
								// report the prgress, like this maybe
								// updateProgress(downloadedSize, totalSize);

							}
							fileOutput.close();

						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception ex) {
							System.out.println("ex: " + ex);
						}

						progress_dialogue.cancel();

						if (flg) {
							playVideo(Uri.fromFile(file));
						}

					}

					@Override
					public void onError(List<String> errors) {
						Log.e("downloadFile errors", "" + errors);
						progress_dialogue.cancel();
						alert("File is missing.");
					}
				});
	}

	/*
	 * Method to manage the exeption occured due to server down
	 */
	public void alert(String msg) {
		progress_dialogue.cancel();
		new AlertDialog.Builder(ConversationActivity.this).setTitle("Message")
				.setIcon(R.drawable.warning).setTitle("Warning !")
				.setMessage(msg)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				})

				.show();
	}

	

	// ***************************************PushNotificatiom>>>>>>>>>>>>>>>

	public void pushMessage(final String lastMessage,final String drink, boolean flg) {

		Log.e("PUSH FROM ME", String.valueOf(CommonActions.MY_QUICKBLOX) + "recp_quickblox_id uiser");
		// recipients
		StringifyArrayList<Integer> userIds = new StringifyArrayList<Integer>();
		userIds.add(Integer.valueOf(chatID));

		QBEvent event = new QBEvent();
		event.setUserIds(userIds);
		if (flg) {			
			event.setEnvironment(QBEnvironment.DEVELOPMENT);
		} else {			
			event.setEnvironment(QBEnvironment.PRODUCTION);
		}
		
		//event.setEnvironment(QBEnvironment.DEVELOPMENT);

		event.setNotificationType(QBNotificationType.PUSH);

		JSONObject json = new JSONObject();
		try {
			json.put("message", lastMessage);
			json.put("recp_quickblox_id",String.valueOf(CommonActions.MY_QUICKBLOX));
			json.put("user_Type", "normal");
			json.put("to_id",prefs.getString("user_id", ""));
			json.put("type", drink);
			//

		} catch (Exception e) {
			e.printStackTrace();
		}

		event.setMessage(json.toString());

		QBMessages.createEvent(event, new QBEntityCallbackImpl<QBEvent>() {
			@Override
			public void onSuccess(QBEvent qbEvent, Bundle args) {
				Log.e("TO ADMIN", qbEvent + "##########onSuccess" + args);
			}

			@Override
			public void onError(List<String> errors) {
				Log.e("TO ADMIN", "##########onError" + errors);
				pushMessage(lastMessage, drink,true);
			}
		});

	}
}