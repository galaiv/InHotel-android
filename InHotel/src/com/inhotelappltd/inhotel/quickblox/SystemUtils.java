
/**
© by newagesmb.com
@author joyal@newagesmb.com
created on May 13, 2015**/
package com.inhotelappltd.inhotel.quickblox;

import java.util.List;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.inhotelappltd.inhotel.logger.Log;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.users.model.QBUser;

public class SystemUtils extends Application{
	private static final String TAG = SystemUtils.class.getSimpleName();

    public static final String APP_ID = "23119";
    public static final String AUTH_KEY = "PgYNyVxGKQdKWVj";
    public static final String AUTH_SECRET = "bY6bx-pGbLJfSR5";

    public static String USER_LOGIN = "";
    public static String USER_PASSWORD = "";
    QBUser currentUser;
    private static SystemUtils instance;
    public static SystemUtils getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "onCreate");

        instance = this;

        // Initialise QuickBlox SDK
        //
        QBSettings.getInstance().fastConfigInit(APP_ID, AUTH_KEY, AUTH_SECRET);
        
      //  doLogin();

    }
    
  

    private void doLogin() {
        // Login to REST API
        //
        final QBUser user = new QBUser();
        user.setLogin(SystemUtils.USER_LOGIN);
        user.setPassword(SystemUtils.USER_PASSWORD);

        ChatService.initIfNeed(this);

        ChatService.getInstance().login(user, new QBEntityCallbackImpl() {

            @Override
            public void onSuccess() {
                // Go to Dialogs screen
                //
                /*Intent intent = new Intent(SplashActivity.this, DialogsActivity.class);
                startActivity(intent);
                finish();*/
            	
            	 /*AlertDialog.Builder dialog = new AlertDialog.Builder(SystemUtils.this);
                 dialog.setMessage("SUCESS").create().show();*/
            	Log.e("Sucess","Suuu");
            }

            @Override
            public void onError(List errors) {
               /* AlertDialog.Builder dialog = new AlertDialog.Builder(SystemUtils.this);
                dialog.setMessage("chat login errors: " + errors).create().show();*/
            	
            	Log.e("onError","onError"+errors);
            	
				ChatService.getInstance().register(user,new QBEntityCallbackImpl() {
							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								super.onSuccess();
							}

							@Override
							public void onError(List errors) {
								// TODO Auto-generated method stub
								super.onError(errors);
								
							}

						});
			}
        });
		
	}

	public int getAppVersion() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
}

