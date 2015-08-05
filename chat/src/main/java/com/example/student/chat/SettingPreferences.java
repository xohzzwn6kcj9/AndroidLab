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
		//UI 출력.. 설정 내용 자동 저장..
		addPreferencesFromResource(R.xml.settings);

		//설정 객체 획득..
		loginId=(EditTextPreference)findPreference("LoginID");
		nickname=(EditTextPreference)findPreference("Nickname");
		serverIp=(EditTextPreference)findPreference("ServerIP");
		serverPort=(EditTextPreference)findPreference("ServerPort");
		serverHttpPort=(EditTextPreference)findPreference("ServerHttpPort");

		//설정 변경 순간의 이벤트 처리..
		//==>설정 내용 저장은 자동으로 해주지만.. 변경 순간 ui 변경은 우리가.
		prefs= PreferenceManager.getDefaultSharedPreferences(this);
		prefs.registerOnSharedPreferenceChangeListener(ospchlistener);

		//초기 activity 실행시 설정 내용이 있다면 summary 변경..
		if(!prefs.getString("LoginID","").equals("")){
			loginId.setSummary(prefs.getString("LoginID",""));
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
			}
	    	
	    };

	}
	
	OnSharedPreferenceChangeListener ospchlistener = new OnSharedPreferenceChangeListener() {
		
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if(key.equals("LoginID")) {
				//2 start ----------------------------------------
				if(!prefs.getString("LoginID","").equals("")) {
					loginId.setSummary(prefs.getString("LoginID", ""));

					//서버 연동..
					myDialog = ProgressDialog.show(SettingPreferences.this,
							"", "wait....", true, true);
					UserSettingThread thread = new UserSettingThread(
							prefs.getString("LoginID", ""), "", true);
					thread.start();
				}else {
					loginId.setSummary("edit LoginID");
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
	//설정 이벤트 등록은 꼭 필요 없는 순간 등록해제해야..

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
				String result=HttpUtil.sendHttpPost(
						prefs.getString("ServerIP",""),
						prefs.getString("ServerHttpPort",""),
						list);
				if(result.equals("OK")){
					msg.what=1;
					handler.sendMessage(msg);
				}else if(result.equals("EXIST")){
					msg.what=2;
					handler.sendMessage(msg);
				}else {
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
