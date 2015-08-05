package com.example.student.chat;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingPreferences extends PreferenceActivity {

	SharedPreferences prefs;
	EditTextPreference loginId;
	EditTextPreference nickname;
	EditTextPreference serverIp;
	EditTextPreference serverPort;
	EditTextPreference serverHttpPort;
	
	ProgressDialog myDialog;
	
	Handler handler;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
//-----1 start--------------------------

		addPreferencesFromResource(R.xml.settings);

		loginId=(EditTextPreference)findPreference(PreferenceHelper.KEY_LOGIN_ID);
		nickname=(EditTextPreference)findPreference(PreferenceHelper.KEY_NICKNAME);
		serverIp=(EditTextPreference)findPreference(PreferenceHelper.KEY_SERVER_IP);
		serverPort=(EditTextPreference)findPreference(PreferenceHelper.KEY_SERVER_PORT);
		serverHttpPort=(EditTextPreference)findPreference(PreferenceHelper.KEY_SERVER_HTTP_PORT);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(ospchlistener);
		if(!prefs.getString(PreferenceHelper.KEY_LOGIN_ID, "").isEmpty()){
			loginId.setSummary(prefs.getString(PreferenceHelper.KEY_LOGIN_ID, ""));
		}

	  //-1 end-------------------------------------
	    if(!prefs.getString("Nickname", "").equals(""))
	    	nickname.setSummary(prefs.getString("Nickname", ""));

	    if(!prefs.getString("ServerIP", "").equals(""))
	    	serverIp.setSummary(prefs.getString("ServerIP", ""));

	    if(!prefs.getString("ServerPort", "").equals(""))
	    	serverPort.setSummary(prefs.getString("ServerPort", ""));

	    if(!prefs.getString("ServerHttpPort", "").equals(""))
	    	serverHttpPort.setSummary(prefs.getString("ServerHttpPort", ""));
	    
	    handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 1) {
					myDialog.cancel();
					
				} else if(msg.what == 0) {
					myDialog.cancel();
					Toast.makeText(SettingPreferences.this, "Fail to add (or modifiy) user information.", Toast.LENGTH_SHORT).show();
				} else if(msg.what == 2) {
					myDialog.cancel();
					Toast.makeText(SettingPreferences.this, "The email already exists.", Toast.LENGTH_SHORT).show();
				}
				else{
					Toast.makeText(SettingPreferences.this, "Unexpected Error.", Toast.LENGTH_SHORT).show();
				}
			}
	    	
	    };

	}
	
	OnSharedPreferenceChangeListener ospchlistener = new OnSharedPreferenceChangeListener() {
		
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			Log.e("kkang", "key="+key);
			if(key.equals("LoginID")) {
				Log.e("kkang", "login id changed. ");
				//2 start ----------------------------------------
				if(!prefs.getString(PreferenceHelper.KEY_LOGIN_ID, "").isEmpty()) {
					loginId.setSummary(prefs.getString(PreferenceHelper.KEY_LOGIN_ID, ""));
					myDialog = ProgressDialog.show(SettingPreferences.this, "", "wait...");
					UserSettingThread thread = new UserSettingThread(prefs.getString("LoginID", ""), "", true);
					thread.start();
				}
				//2 end -----------------------------------------
			} else if (key.equals("Nickname")) {

			    if(!prefs.getString("Nickname", "").equals("")) {
			    	nickname.setSummary(prefs.getString("Nickname", ""));
			    	
			    	if(prefs.getString("LoginID", "").equals("")) {
			    		Toast.makeText(SettingPreferences.this, "Please edit the email first!", Toast.LENGTH_SHORT).show();
			    	} else {
				    	myDialog = ProgressDialog.show(SettingPreferences.this, "" , " Loading...", true, true);
				    	UserSettingThread usThread = new UserSettingThread(prefs.getString("LoginID", ""), prefs.getString("Nickname", ""), false);
				    	usThread.start();
			    	}
			    	
			    } else {
			    	nickname.setSummary("Please put your nickname");
			    }
			} else if (key.equals("ServerIP")) {
			    if(!prefs.getString("ServerIP", "").equals(""))
			    	serverIp.setSummary(prefs.getString("ServerIP", ""));
			    else
			    	serverIp.setSummary("Please put Server IP");
			} else if (key.equals("ServerPort")) {
			    if(!prefs.getString("ServerPort", "").equals(""))
			    	serverPort.setSummary(prefs.getString("ServerPort", ""));
			    else
			    	serverPort.setSummary("Please put Server Port");
			} else if (key.equals("ServerHttpPort")) {
			    if(!prefs.getString("ServerHttpPort", "").equals(""))
			    	serverHttpPort.setSummary(prefs.getString("ServerHttpPort", ""));
			    else
			    	serverHttpPort.setSummary("Please put Server HTTP Port");
			}
		}
	};
	//---3 start --------------------------------

	@Override
	protected void onDestroy() {
		super.onDestroy();
		prefs.unregisterOnSharedPreferenceChangeListener(ospchlistener);
	}


	//---3 end--------------------------------------
	
	class UserSettingThread extends Thread {
		
		String email = "";
		String nickname = "";
		boolean isAdd = false;
		
		UserSettingThread(String _email, String _nickname, boolean _isAdd) {

			email = _email;
			nickname = _nickname;
			isAdd = _isAdd;
		}
		
		public void run() {
			Message msg = new Message();

			try {
				
				ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> temp = new HashMap<String, String>();
				
				if(isAdd) {
					temp.put("key", "type");
					temp.put("value", "REGISTER");
					list.add(temp);
				} else {
					temp.put("key", "type");
					temp.put("value", "MODIFY");
					list.add(temp);
					
					temp = new HashMap<String, String>();
					temp.put("key", "nickname");
					temp.put("value", nickname);
					list.add(temp);

					temp = new HashMap<String, String>();
					temp.put("key", "icon");
					temp.put("value", "");
					list.add(temp);
				}
				temp = new HashMap<String, String>();
				temp.put("key", "email");
				temp.put("value", email);
				list.add(temp);
				
				temp = new HashMap<String, String>();
				temp.put("key", "status");
				temp.put("value", "1");
				list.add(temp);
//------------ 3 start ---------------------------------------				

				String result = HttpUtil.sendHttpPost(
						prefs.getString(PreferenceHelper.KEY_SERVER_IP, ""),
						prefs.getString(PreferenceHelper.KEY_SERVER_HTTP_PORT, ""),
						list);
				Log.d("kkang", "result="+result);
				if(result.equals("OK")){
					msg.what=1;
					handler.sendMessage(msg);
				}
				else if(result.equals("EXISTS")){
					msg.what=2;
					handler.sendMessage(msg);
				}
				else{
					msg.what=0;
					handler.sendMessage(msg);
				}

//--------4 end ------------------------------------------------
			} catch (Exception e) {
				Log.d("kkang", "Exception : " + e.getMessage());
				msg.what = 0;
				handler.sendMessage(msg);
			}

		}
	}
}