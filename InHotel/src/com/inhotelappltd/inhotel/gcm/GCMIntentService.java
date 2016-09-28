package com.inhotelappltd.inhotel.gcm;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.inhotelappltd.inhotel.R;

import com.inhotelappltd.inhotel.tab.ConversationActivity;
import com.inhotelappltd.inhotel.utils.CommonActions;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.inhotelappltd.inhotel.TabActivity;

public class GCMIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;

    private static final String TAG = GCMIntentService.class.getSimpleName();

    private NotificationManager notificationManager;
    String msg_qblx,id_qblx,user_id,user_type,drink;
    SharedPreferences prefs;
    Editor edt;
    
    public GCMIntentService() {
        super(CommonActions.GCM_INTENT_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "new push");
    	
        Bundle extrasz = intent.getExtras();
        GoogleCloudMessaging googleCloudMessaging = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = googleCloudMessaging.getMessageType(intent);
        prefs       =  getSharedPreferences("Login Credinals", MODE_PRIVATE);        
        
		edt 	    =  prefs.edit();
        
		
		  
        if (!extrasz.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
               // processNotification(CommonActions.GCM_SEND_ERROR, extras);
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
               // processNotification(CommonActions.GCM_DELETED_MESSAGE, extras);
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            	
            	
            	for (String key: extrasz.keySet())
      		  {
      		    Log.e ("myApplication", key + "  is a key in the bundle "+ extrasz.get(key));
      		  }
            	
            	ActivityManager am = (ActivityManager) this
   						.getSystemService(ACTIVITY_SERVICE);
   				List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
   				ComponentName componentInfo = taskInfo.get(0).topActivity;
   			
            	Log.e("CLASS",taskInfo.get(0).topActivity.getClassName());
            	if(!taskInfo.get(0).topActivity.getClassName().equalsIgnoreCase("com.inhotelappltd.inhotel.tab.ConversationActivity")){	
            		
            		
                	if(extrasz.get("recp_quickblox_id") != null){
                		Log.e("Check ID FROM GCM ",extrasz.get("recp_quickblox_id").toString());
						msg_qblx = extrasz.get("message").toString();
						id_qblx = extrasz.get("recp_quickblox_id").toString();
						user_id = extrasz.get("to_id").toString();
						user_type = extrasz.get("user_Type").toString();
                   	if(extrasz.get("type") != null){
                    	drink       = extrasz.get("type").toString();
                    	if(!drink.equalsIgnoreCase("accept_reject_drink")){
                          	 processNotification(CommonActions.GCM_RECEIVED);     
                          	}
                   	}
                       // Post notification of received message.  
                   	
       				}else{
       					
       					if(extrasz.get("message") != null){
       						msg_qblx =  extrasz.get("message").toString();
                    		Log.e("sdfds@@",extrasz.get("fron")+"sdfdsf"+extrasz.get("message"));
                    		if(extrasz.get("type") != null){
                    		drink       = extrasz.get("type").toString();
                    		if(!drink.equalsIgnoreCase("accept_reject_drink")){
                    			processNotificationtochat();   
                              	}
                    		}
                    		
                    		
       					}
                		
                	}
            		
            	}
            	
            	
            	if(extrasz.get("type") != null){
            		  drink       = extrasz.get("type").toString();
                      
            		  if(drink.equalsIgnoreCase("accept_reject_drink")){
            			     msg_qblx =  extrasz.get("message").toString();
                        	 id_qblx  = extrasz.get("recp_quickblox_id").toString();
                        	 user_id  = extrasz.get("to_id").toString();
                        	 user_type  = extrasz.get("user_Type").toString();
                         	  drink       = extrasz.get("type").toString();
            			     Log.e("TAG","PUSH FIICISh"+id_qblx);
            			     
            			  Intent intentNewPush = new Intent(CommonActions.NEW_PUSH_EVENT);
                   	   /* intent.putExtra(CommonActions.EXTRA_MESSAGE, msg_qblx);
       			        intent.putExtra("type", user_type);
       			        intent.putExtra("profID",  user_id);
       			        intent.putExtra("QUicktID", id_qblx);
       			        intent.putExtra("page",  "drink");
       			        */
       			    edt.putString("d_msg", msg_qblx);
       			   edt.putString("d_type", user_type);
       			   edt.putString("d_profID", user_id);
       			   edt.putString("d_QUicktID", id_qblx);
       			   edt.putString("d_page", drink);
       			   edt.commit();
       			     
       			        
       			        LocalBroadcastManager.getInstance(this).sendBroadcast(intentNewPush);
                        	}
            		  
                    	
            	}
            	  
      		 /*else if(taskInfo.get(0).topActivity.getClassName().equalsIgnoreCase("com.inhotelappltd.inhotel.tab.ConversationActivity")){
					Log.e("sdsd","%%%%");
					// notify about new push
			        //
			        Intent intentNewPush = new Intent(CommonActions.NEW_PUSH_EVENT);
			        intent.putExtra(CommonActions.EXTRA_MESSAGE, msg_qblx);
			        intent.putExtra("typeo", user_type);
			        intent.putExtra("profID",  user_id);
			        intent.putExtra("QUicktID", id_qblx );
			        intent.putExtra("page",  "chat");
			        LocalBroadcastManager.getInstance(this).sendBroadcast(intentNewPush);
				}*/
				
					/*  if(extras.getString("message")!=null){
						  String[] Msg= extras.getString("message").split("-#-");
						  String type = Msg[1];
						  
						  profID  = Msg[2];
						  QUicktID = Msg[3];
						// notify about new push
					        //
						  
						    edt.putString("profID_Q", profID);
							edt.putString("QUicktID_Q", QUicktID);						
							edt.commit();
						  
					 
		                Log.e(TAG, "Received: " + extras.toString()); 
					  */
					  
				  
				 
            
        }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void processNotification(String type) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

       // final String messageValue = extras.getString("message");
        edt.putString(CommonActions.EXTRA_MESSAGE, msg_qblx);
        edt.putString("type", user_type);
        edt.putString("profID",  user_id);
        edt.putString("QUicktID", id_qblx);
        edt.putString("page",  "chat");
        
        edt.commit();
        
        Log.e("FROM GCM PASES",id_qblx);
        Intent intent = new Intent(this, ConversationActivity.class);
        intent.putExtra(CommonActions.EXTRA_MESSAGE, msg_qblx);
        intent.putExtra("type", user_type);
        intent.putExtra("profID",  user_id);
        intent.putExtra("QUicktID", id_qblx );
        intent.putExtra("page",  "chat");    
        
     
        
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
        .setSmallIcon(getProperNotificationIcon()).setContentTitle(CommonActions.GCM_NOTIFICATION)
        .setDefaults(Notification.DEFAULT_SOUND).setAutoCancel(true)
        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg_qblx)).setContentText(msg_qblx);

        mBuilder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());


        /*// notify about new push
        //
        Intent intentNewPush = new Intent(CommonActions.NEW_PUSH_EVENT);
        intent.putExtra(CommonActions.EXTRA_MESSAGE, msg_qblx);
        intent.putExtra("typeo", user_type);
        intent.putExtra("profID",  user_id);
        intent.putExtra("QUicktID", id_qblx );
        intent.putExtra("page",  "chat");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentNewPush);*/

       
    }
    private void processNotificationtochat() {
    	edt.putBoolean("staff_chat",true );
    	edt.commit();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

       // final String messageValue = extras.getString("message");

        Intent intent = new Intent(this, TabActivity.class);     
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
        .setSmallIcon(getProperNotificationIcon()).setContentTitle(CommonActions.GCM_NOTIFICATION)
        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.notification_icon))
     
        .setDefaults(Notification.DEFAULT_SOUND).setAutoCancel(true)
        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg_qblx)).setContentText(msg_qblx);

        mBuilder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());


        /*// notify about new push
        //
        Intent intentNewPush = new Intent(CommonActions.NEW_PUSH_EVENT);
        intent.putExtra(CommonActions.EXTRA_MESSAGE, msg_qblx);
        intent.putExtra("typeo", user_type);
        intent.putExtra("profID",  user_id);
        intent.putExtra("QUicktID", id_qblx );
        intent.putExtra("page",  "chat");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intentNewPush);*/

       
    }
    
    /**
    * Get proper notification icon
    * 
    * @return
    */
	public int getProperNotificationIcon() {
		if (isLollipop()) {
			return R.drawable.notification_icon_lolipop;
		} else {
			return R.drawable.notification_icon;
		}
		
		/*if (entry.targetSdk >= Build.VERSION_CODES.LOLLIPOP) {
		    entry.icon.setColorFilter(mContext.getResources().getColor(android.R.color.white));
		} else {
		    entry.icon.setColorFilter(null);
		}*/
		
	}

	public static boolean isLollipop() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			return true;
		} else {
			return false;
		}
     }
}