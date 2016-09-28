package com.inhotelappltd.inhotel;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;

import com.inhotelappltd.inhotel.R;

import com.inhotelappltd.inhotel.logger.Log;
import com.inhotelappltd.inhotel.tab.ChatFragment;
import com.inhotelappltd.inhotel.tab.GuestFragment;
import com.inhotelappltd.inhotel.tab.HotelFragment;
import com.inhotelappltd.inhotel.tab.SettingFragment;

public class TabActivity extends FragmentActivity {

	FragmentTabHost mTabHost;
	static int m_tabPosition;
	SharedPreferences prefs;
	Editor edt;

	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stubs
		super.onCreate(arg0);		
	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tab);

		mTabHost = (FragmentTabHost) findViewById(R.id.m_tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.m_tabFrameLayout);

		prefs = getSharedPreferences("Login Credinals", MODE_PRIVATE);
		edt = prefs.edit();
		edt.putString("splash", "No");
		edt.commit();

		View chat = LayoutInflater.from(this).inflate(R.layout.tab_view_chat,null);
		View hotel = LayoutInflater.from(this).inflate(R.layout.tab_view_hotel,null);
		
		View guests = LayoutInflater.from(this).inflate(R.layout.tab_view_guests,null);
		View settings = LayoutInflater.from(this).inflate(R.layout.tab_view_settings,null);

		mTabHost.addTab(mTabHost.newTabSpec("chat").setIndicator(chat),ChatFragment.class,null);
		mTabHost.addTab(mTabHost.newTabSpec("hotel").setIndicator(hotel),HotelFragment.class,null);
		mTabHost.addTab(mTabHost.newTabSpec("guests").setIndicator(guests),GuestFragment.class,null);
		mTabHost.addTab(mTabHost.newTabSpec("settings").setIndicator(settings),SettingFragment.class,null);

		/*if (AcessCodeActivity.chat == 0) {
			mTabHost.setCurrentTab(3);
		} else if (AcessCodeActivity.chat == 1) {
			mTabHost.setCurrentTab(0);
		} else if (AcessCodeActivity.chat == 2) {
			mTabHost.setCurrentTab(2);
		}*/
		mTabHost.getTabWidget().getChildAt(3).setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {			    	
		    
		    	FragmentManager fm = getSupportFragmentManager();
		    	for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {    
		    	    fm.popBackStack();
		    	}
		    	mTabHost.setCurrentTab(3);
		    	doFragmentTransaction(new SettingFragment(), "settings");                       
		    }
		});
		
	
		mTabHost.getTabWidget().getChildAt(2).setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {			    	
		    
		    	FragmentManager fm = getSupportFragmentManager();
		    	for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {    
		    	    fm.popBackStack();
		    	}
		    	mTabHost.setCurrentTab(2);
		    	doFragmentTransaction(new GuestFragment(), "guests");                       
		    }
		});
		
		mTabHost.getTabWidget().getChildAt(1).setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {			    	
		    
		    	FragmentManager fm = getSupportFragmentManager();
		    	for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {    
		    	    fm.popBackStack();
		    	}
		    	mTabHost.setCurrentTab(1);
		    	doFragmentTransaction(new HotelFragment(), "hotel");                       
		    }
		});
		
		mTabHost.getTabWidget().getChildAt(0).setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {			    	
		    
		    	FragmentManager fm = getSupportFragmentManager();
		    	for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {    
		    	    fm.popBackStack();
		    	}
		    	mTabHost.setCurrentTab(0);
		    	doFragmentTransaction(new ChatFragment(), "chat");                       
		    }
		});

		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			@Override
			public void onTabChanged(String tabId) {

				Log.e("tabId", "tabId*****" + tabId);

				/*if (tabId.equalsIgnoreCase("settings")) {
					FragmentTransaction transaction = getSupportFragmentManager()
							.beginTransaction();
					transaction.replace(R.id.m_tabFrameLayout,
							new SettingFragment());
					transaction.addToBackStack("null");
					transaction.commit();
				}
*/
				setTabColor(mTabHost);
			}

		});
		
		if(SplashScreenActivity.flag_alert){
		
			mTabHost.setCurrentTab(3);
			
		}else{
		
			
			if(prefs.getString("activation_code", "").equalsIgnoreCase("")){
				mTabHost.setCurrentTab(3);
			}else{
				mTabHost.setCurrentTab(2);
				
				if(prefs.getBoolean("staff_chat", false)){
					edt.putBoolean("staff_chat", false);
					mTabHost.setCurrentTab(0);
				}
			}
		}
		
		
		
		setTabColor(mTabHost);
	}
public void doFragmentTransaction(Fragment fragment, String tag) {		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.m_tabFrameLayout, fragment);
		transaction.addToBackStack(tag);
		transaction.commit();
	}
	// Change The Backgournd Color of Tabs
	public void setTabColor(TabHost tabhost) {
		for (int i = 0; i < tabhost.getTabWidget().getChildCount(); i++)
			tabhost.getTabWidget()
					.getChildAt(i)
					.setBackgroundColor(
							getResources().getColor(R.color.tab_grey)); // unselected

		tabhost.getTabWidget().getChildAt(tabhost.getCurrentTab())
				.setBackgroundColor(getResources().getColor(R.color.tab_blue)); // tab  selected
																			

		m_tabPosition = tabhost.getCurrentTab();
		Log.e("m_tabPosition", m_tabPosition + "!!");
	}

	@Override
	public void onBackPressed() {

	}
}
