package com.inhotelappltd.inhotel;

import java.util.List;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inhotelappltd.inhotel.R;

import com.inhotelappltd.inhotel.gcm.PlayServicesHelper;
import com.inhotelappltd.inhotel.logger.Log;
import com.inhotelappltd.inhotel.quickblox.BaseActivity;
import com.inhotelappltd.inhotel.quickblox.ChatService;
import com.inhotelappltd.inhotel.quickblox.SystemUtils;
import com.inhotelappltd.inhotel.server.ServerConnector;
import com.inhotelappltd.inhotel.utils.CommonActions;
import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.users.model.QBUser;

public class AcessCodeActivity extends BaseActivity {

	AlertDialog.Builder dialog1;
	EditText et_code;
	ImageView bt_continue, iv_close;
	int status;
	String code = "";
	ProgressDialog progress_dialogue;
	SharedPreferences prefs;
	Editor edt;
	TextView tv_signin;
	RelativeLayout rl_securecode;
	public static int my_quickbloxID = 0;
	QBUser user;
	PlayServicesHelper playServicesHelper;

	private QBChatService chatService;
	private static final String TAG = AcessCodeActivity.class.getSimpleName();
	public static final int AUTO_PRESENCE_INTERVAL_IN_SECONDS = 30;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_acess_code);
		setAnimation(true);
		 progress_dialogue = new ProgressDialog(AcessCodeActivity.this);	
		dialog1 = new AlertDialog.Builder(this);
    	dialog1.setMessage("OOOOOOOOOO");
		
		et_code	    = (EditText) findViewById(R.id.et_code);
		iv_close    = (ImageView) findViewById(R.id.iv_close);
		bt_continue = (ImageView) findViewById(R.id.bt_continue);
		tv_signin   = (TextView ) findViewById(R.id.tv_signin);
		prefs       =  getSharedPreferences("Login Credinals", MODE_PRIVATE);		
		edt 	    =  prefs.edit();
		rl_securecode = (RelativeLayout) findViewById(R.id.rl_securecode);
		
		rl_securecode.setVisibility(View.GONE);
        String text 	=   "<font color=#9ac5bd>Sign in with different account </font> <font color=#ffffff><b>Sign In</b></font>";		
		
		tv_signin.setText(Html.fromHtml(text));
	

		if (new CommonActions(AcessCodeActivity.this).isNetworkConnected()) {

			doQuickBloxConnection();
		} else {
			new CommonActions(AcessCodeActivity.this)
					.showToast("Please check your internet connection");
		}

		tv_signin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (new CommonActions(AcessCodeActivity.this)
						.isNetworkConnected()) {

					progress_dialogue.setMessage("Loading...");
					progress_dialogue.show();
				/*	progress_dialogue = ProgressDialog.show(
							AcessCodeActivity.this, "Please wait", "Loading");*/
					new AcessCodeTask().execute(2);
				} else {
					new CommonActions(AcessCodeActivity.this)
							.showToast("Please check your internet connection");
				}
			}
		});
		 
		
		bt_continue.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				//et_code.setText("8063693452");//peter
		       // et_code.setText("8476976789"); //newagesme been
				//et_code.setText("7031571964");//crux			
				//et_code.setText("3622954115");//  1380479981
				
				if(!et_code.getText().toString().equalsIgnoreCase("")){
					code=et_code.getText().toString();
					if (new CommonActions(AcessCodeActivity.this).isNetworkConnected()) {
						progress_dialogue.setMessage("Authenticating Acess Code....");
						progress_dialogue.show();

						//progress_dialogue = ProgressDialog.show(AcessCodeActivity.this, "Please wait","Authenticating Acess Code....");
						
						new AcessCodeTask().execute(0);
					} else {
						new CommonActions(AcessCodeActivity.this).showToast("Please check your internet connection");
					}
				}else{
					new AlertDialog.Builder(AcessCodeActivity.this).setTitle("Warning") 
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
		
		iv_close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				edt.putString("activation_status", "N");
				edt.putString("activation_code", "");
				edt.commit();
				
				startActivity(new Intent(
						AcessCodeActivity.this,TabActivity.class));
			}
		});
	}
	
	 public void addConnectionListener(ConnectionListener listener){
	        chatService.addConnectionListener(listener);
	    }

	    public void removeConnectionListener(ConnectionListener listener){
	        chatService.removeConnectionListener(listener);
	    }
	    ConnectionListener chatConnectionListener = new ConnectionListener() {
	        @Override
	        public void connected(XMPPConnection connection) {
	            Log.i(TAG, "connected");
	        }

	        @Override
	        public void authenticated(XMPPConnection connection) {
	            Log.i(TAG, "authenticated");
	        }

	        @Override
	        public void connectionClosed() {
	            Log.i(TAG, "connectionClosed");
	        }

	        @Override
	        public void connectionClosedOnError(final Exception e) {
	            Log.i(TAG, "connectionClosedOnError: " + e.getLocalizedMessage());
	        }

	        @Override
	        public void reconnectingIn(final int seconds) {
	            if(seconds % 5 == 0) {
	                Log.i(TAG, "reconnectingIn: " + seconds);
	            }
	        }

	        @Override
	        public void reconnectionSuccessful() {
	            Log.i(TAG, "reconnectionSuccessful");
	        }

	        @Override
	        public void reconnectionFailed(final Exception error) {
	            Log.i(TAG, "reconnectionFailed: " + error.getLocalizedMessage());
	        }
	    };
	//QUICKBLOX
	public void  doQuickBloxConnection(){
		ChatService.getInstance().logout();
		SystemUtils.USER_LOGIN="inhotel_"+prefs.getString("user_id", "");
		SystemUtils.USER_PASSWORD="inhotel_" + prefs.getString("user_id", "") +"123";
		user = new QBUser();
		user.setLogin(SystemUtils.USER_LOGIN);
		user.setPassword(SystemUtils.USER_PASSWORD);
		chatService = QBChatService.getInstance();
		chatService.addConnectionListener(chatConnectionListener);
		progress_dialogue.setMessage("Loading.... Please wait for a while.");
		progress_dialogue.show();
	   //   progress_dialogue = ProgressDialog.show(AcessCodeActivity.this, "Chat Login","");
		 // Create REST API session
        
        QBAuth.createSession(user, new QBEntityCallbackImpl<QBSession>() {
            @Override
            public void onSuccess(QBSession session, Bundle args) {

                user.setId(session.getUserId());
                Log.e("createSession","si=ucess");
                chatService.login(user, new QBEntityCallbackImpl() {
                    @Override
                    public void onSuccess() {

                        // Start sending presences
                        //
                        try {
                            chatService.startAutoSendPresence(AUTO_PRESENCE_INTERVAL_IN_SECONDS);
                        } catch (SmackException.NotLoggedInException e) {
                            e.printStackTrace();
                        }
                        progress_dialogue.dismiss();
                        Log.e("BACK LOGIN", "onSuccess22222");
                        AcessCodeActivity.my_quickbloxID=user.getId();
       				    if(my_quickbloxID!=0){
       						new AcessCodeTask().execute(1);
       					} 
       			
                    }

                    @Override
                    public void onError(List errors) {
                    	
                    	Log.e("BACK LOGIN", "hreeeeeeeeeeeeeee"+errors);              	
                    
                    	if (errors.get(0).toString().equalsIgnoreCase("Unauthorized")) {
        					Log.e("@11",
        							"login****************** NEED REGISTRATION"
        									+ errors.get(0).toString());
        					quickBloxRegistration();
        				} else {
        					progress_dialogue.dismiss();
        					showQiuickbloxReconnect();
        				}
                        
                    }
                });
            }

            @Override
            public void onError(List<String> errors) {
            	
            	Log.e("createSession","dsfs"+errors);
            	
            	if (errors.get(0).toString().equalsIgnoreCase("Unauthorized")) {
					Log.e("@11",
							"login****************** NEED REGISTRATION"
									+ errors.get(0).toString());
					quickBloxRegistration();
				} else {
					progress_dialogue.cancel();
					showQiuickbloxReconnect();
				}
            }
        });
        
      
		
		/*	
		
		
		progress_dialogue = ProgressDialog.show(AcessCodeActivity.this, "Please wait","Loading..");
		
			if (!QBChatService.isInitialized()) {
			    QBChatService.init(this);			  
			}
		
		ChatService.initIfNeed(AcessCodeActivity.this);		
	
		
		
		SystemUtils.USER_LOGIN="inhotel_"+prefs.getString("user_id", "");
		SystemUtils.USER_PASSWORD="inhotel_" + prefs.getString("user_id", "") +"123";
		
		
		user = new QBUser();
		user.setLogin(SystemUtils.USER_LOGIN);
		user.setPassword(SystemUtils.USER_PASSWORD);        
	      

		//Log.e("@@@@@@@@@@@@@@@@@11","login****************** Initiate");
		
	
		ChatService.getInstance().login(user, new QBEntityCallbackImpl() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				super.onSuccess();	
				
				Log.e("@@@@@@@@@@@@@@@@@11","login****************** Suces");
				progress_dialogue.cancel();

				AcessCodeActivity.my_quickbloxID=user.getId();
				 if(my_quickbloxID!=0){
						new AcessCodeTask().execute(1);
					} 
			
				
			}

			@Override
			public void onError(List errors) {
				// TODO Auto-generated method stub
				super.onError(errors);	
				
				
				
				AlertDialog.Builder dialog = new AlertDialog.Builder(AcessCodeActivity.this);
	            dialog.setMessage("Register: " + errors).create().show();
								
	            Log.e("@@@@@@@@@@@@@@@@@11","login****************** ERROR"+errors.get(0).toString());
				if(errors.get(0).toString().equalsIgnoreCase("Unauthorized")){
					Log.e("@@@@@@@@@@@@@@@@@11","login****************** NEED REGISTRATION"+errors.get(0).toString());
					quickBloxRegistration();
				}else{
					progress_dialogue.cancel();
					 Log.e("@@@@@@@@@@@@@@@@@####","login* ERROR"+errors.get(0).toString());
				   AlertDialog.Builder dialog = new AlertDialog.Builder(AcessCodeActivity.this);
					dialog.setPositiveButton("Reconnect", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							doQuickBloxConnection();
						}
					});
					dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							
						}
					});
			        dialog.setMessage("Chat Server is not connecting !! ").create().show();
					
			        Log.e("@@@@@@@@@@@@@@@@@11@@","login********JJJ********** Fail"+errors.get(0).toString());
					
				
				}
				
				
			}

			

		});
	*/}
	  public void showQiuickbloxReconnect(){
      	AcessCodeActivity.this.runOnUiThread(new Runnable() {
      		  public void run() {
    			  AlertDialog.Builder dialog = new AlertDialog.Builder(
							AcessCodeActivity.this);
					dialog.setPositiveButton("Reconnect",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialog,
										int which) {
									dialog.cancel();
									doQuickBloxConnection();
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
	public void quickBloxRegistration(){

		
		
		// ChatService.initIfNeed(AcessCodeActivity.this);
		final QBChatService chatService = QBChatService.getInstance();

		Log.e("register first","USERS"+SystemUtils.USER_LOGIN+ "****"+SystemUtils.USER_PASSWORD);
		
		user = new QBUser();
		user.setLogin(SystemUtils.USER_LOGIN);
		user.setPassword(SystemUtils.USER_PASSWORD);
		Log.e("@@@@@@@@@@@@@@@@@22","Reg****************** Initiates");
		 my_quickbloxID=0;
		ChatService.initIfNeed(AcessCodeActivity.this);
		ChatService.getInstance().register(user,new QBEntityCallbackImpl() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				super.onSuccess();
				
				Log.e("@@@@@@@@@@@@@@@@@22","Reg****************** Sucess");
				
				CommonActions.MY_QUICKBLOX=my_quickbloxID;
				if(my_quickbloxID!=0){
					new AcessCodeTask().execute(1);
				}
				
				progress_dialogue.cancel();
				doQuickBloxConnection();
				
			}

			@Override
			public void onError(List errors) {
				// TODO Auto-generated method stub
				super.onError(errors);
				progress_dialogue.cancel();
				//doQuickBloxConnection();
				Log.e("@@@@@@@@@@@@@@@@@22","Reg****************** Error"+errors);
				/* AlertDialog.Builder dialog = new AlertDialog.Builder(AcessCodeActivity.this);
	            dialog.setMessage("Register: " + errors).create().show();*/
				
				showQiuickbloxReconnect();
			}
			
		});
	}
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		setAnimation(false);
	}

	
	
	//Intent Transition Animation
	public void setAnimation(boolean flg) {

		if (flg) {
			// opening transition animations
			overridePendingTransition(R.anim.activity_open_translate,
					R.anim.activity_close_scale);

		} else {
			// closing transition animations
			overridePendingTransition(R.anim.activity_open_scale,
					R.anim.activity_close_translate);
		}

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
					return new ServerConnector(AcessCodeActivity.this)
							.getResponseFromServer(
									CommonActions.appURL,
									"checkActivationCodeExpires",
									"{\"activation_code\": \""
											+code+ "\",\"user_id\": \""
										+ prefs.getString("user_id", "")
										+ "\"}", "\"\"");

			}else if(status == 1){
				return new ServerConnector(AcessCodeActivity.this)
				.getResponseFromServer(
						CommonActions.appURL,
						"updateQuickBloxId",
						"{\"quickblox_id\": \""
								+my_quickbloxID+ "\",\"user_id\": \""
							+ prefs.getString("user_id", "")
							+ "\"}", "\"\"");




				
			}else 	if (status == 2) {
				return new ServerConnector(AcessCodeActivity.this).getResponseFromServer(
						CommonActions.appURL,
						"logout",
						"{\"user_id\": \""
								+ prefs.getString("user_id", "")
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
					
					if(jobj.getString("activation_status").equalsIgnoreCase("Y")){	
						
						
						edt.putString("activation_status", jobj.getString("activation_status"));
						edt.putString("activation_code", code);		
						edt.putString("activation_code_user", code);
						edt.commit();
						
						startActivity(new Intent(AcessCodeActivity.this,TabActivity.class));
						finish();
						/*new AlertDialog.Builder(AcessCodeActivity.this).setTitle("Message")
						.setMessage(jobj.getString("message"))
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
								
							}
						})

						.show();*/	
						
					}else{
						edt.putString("activation_status", jobj.getString("activation_status"));
						edt.putString("activation_code", "");	
						edt.putString("activation_code_user", "");
						edt.commit();
						gotServerError(jobj.getString("message"));
						
						rl_securecode.setVisibility(View.VISIBLE);
					}
				}else if(status == 1){
					progress_dialogue.cancel();
					playServicesHelper = new PlayServicesHelper(AcessCodeActivity.this);
					
					doAcessCodeAuthentication();
				}else if(status == 2){
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
					
						
						startActivity(new Intent(AcessCodeActivity.this,SplashScreenActivity.class)); 
						 finish();
										
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				gotServerError("Database error. Try again");
			}
			

		}
	}
		
		public void doAcessCodeAuthentication(){
			
			Log.e("doAcessCodeAuthentication #####",prefs.getString("activation_code_user", "")+" ******");
			
			if(!prefs.getString("activation_code_user", "").equalsIgnoreCase("")){
				code=prefs.getString("activation_code_user", "");
				if (new CommonActions(AcessCodeActivity.this).isNetworkConnected()) {

					
					progress_dialogue.setMessage("Authenticating Acess Code....");
					progress_dialogue.show();
					//progress_dialogue = ProgressDialog.show(AcessCodeActivity.this, "Please wait","Authenticating Acess Code....");
					new AcessCodeTask().execute(0);
				} else {
					new CommonActions(AcessCodeActivity.this).showToast("Please check your internet connection");
				}
			}else{
				if(progress_dialogue.isShowing()){
					progress_dialogue.create();
				}
				rl_securecode.setVisibility(View.VISIBLE);
			}
			
		}
		
		/*
		 * Method to manage the exeption occured due to server down
		 */
		public void gotServerError(String msg) {
			if(progress_dialogue.isShowing()){
				progress_dialogue.cancel();
			}
			new AlertDialog.Builder(AcessCodeActivity.this).setTitle("Message")
					.setMessage(msg)
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					})

					.show();
		}
}