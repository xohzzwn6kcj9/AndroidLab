package com.example.student.chat;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends Activity implements OnClickListener {
	
	Handler handler;
	ProgressDialog myDialog;

	ListView lv;
	ArrayList<Map<String, String>> al;
	ChatListAdapter ca;
	
	Button btn;
	TextView msgtv;
	
	String email;
    String friend;
    String status;
    String serverIp;
    String serverHttpPort;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);


	    setContentView(R.layout.activity_chat);
	    
	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		serverIp = prefs.getString("ServerIP", "");
		serverHttpPort = prefs.getString("ServerHttpPort", "0");
		
	    Intent intent = getIntent();
	    email = intent.getStringExtra("email");
	    friend = intent.getStringExtra("friend");
	    status = intent.getStringExtra("status");

	    
	    String msg = intent.getStringExtra("message");

	    lv = (ListView) findViewById(R.id.chat_list);
	    al = new ArrayList<Map<String, String>>();
	    
	    btn = (Button) findViewById(R.id.send_btn);
	    btn.setOnClickListener(this);
	    msgtv = (TextView) findViewById(R.id.send_text);
	    
	    ca = new ChatListAdapter(this, al);
	    
	    
	    lv.setAdapter(ca);
//Add2-2------------------------------	    

//--------------------------------	    
	    if(msg != null && !msg.equals("")) {

	    	NotificationManager nm = 
		    	(NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		    nm.cancel(111);
	    }
	    	    
	    myDialog = ProgressDialog.show(this, "" , " Loading...", true, true);
	    
	    handler = new Handler() {

			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 1) {
					myDialog.cancel();
					al = (ArrayList<Map<String, String>>) msg.obj;
					Map<String, String> temp;
					for(int i=0;i<al.size();i++) {
						temp = al.get(i);
						addMessage(temp.get("message"), temp.get("email"), temp.get("friend"), temp.get("datetime"), false);
					}
					
					
				} else if(msg.what == 0) {
					myDialog.cancel();
					Toast.makeText(ChatActivity.this, "Fail to get chat historys.", Toast.LENGTH_SHORT);
				}
			}
	    	
	    };

	    HistoryThread hThread = new HistoryThread();
	    hThread.start();
	}
//Add2-1-----------------------------------	

////----------------------------
//Add1-2	--------------------

	private void addMessage(String msg, String from, String to, String datetime, boolean isHere){
		final HashMap<String, String> temp = new HashMap<>();
		temp.put("msg", msg);
		temp.put("from", from);
		temp.put("email", email);
		temp.put("datetime", datetime);
		ca.add(temp);
		if(ca.getCount() > 20){
			ca.remove(ca.getItem(0));
		}
		lv.setSelection(ca.getCount()-1);
		if(isHere){
			MyApplication app = (MyApplication) getApplicationContext();
			boolean isOnline = app.isOnline();
			if(isOnline){
				Intent intent = new Intent(getResources().getString(R.string.intent_to_service));
				intent.putExtra("from", from);
				intent.putExtra("to", to);
				intent.putExtra("message", msg);
				sendBroadcast(intent);
				Log.d("kkang", "activity sendBoroadcast");
			}
			else{
				Toast.makeText(this, "network error", Toast.LENGTH_SHORT).show();
			}
		}
	}
	final BroadcastReceiver chatReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d("kkang", "activity onReceive()");
			if(intent.getStringExtra("from")!=null){
				addMessage(intent.getStringExtra("msg"), intent.getStringExtra("from"), email,
						intent.getStringExtra("datetime"), false);
			}
		}
	};
//end ------------------------------

	@Override
	public void onClick(View v) {
//Add1-1----------------------
		if(v==btn){
			if(!msgtv.getText().toString().trim().equals("")){
				addMessage(msgtv.getText().toString(), email, friend, DateUtil.getNow("yyyy-MM-dd HH:mm:ss"), true);
				msgtv.setText("");
			}
		}

//end-----------------------
	}
	
	@Override
	protected void onPause() {
		unregisterReceiver(chatReceiver);
		onRetainNonConfigurationInstance();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		registerReceiver(chatReceiver, new IntentFilter(getResources().getString(R.string.intent_to_activity)));
		super.onResume();
	}
	
	class HistoryThread extends Thread {

        //최초의 채팅 문자열 history 받아오기
		public void run() {
			Message msg = new Message();

			try {
				
				ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> temp = new HashMap<String, String>();
				temp.put("key", "type");
				temp.put("value", "GETHISTORY");
				list.add(temp);
				
				temp = new HashMap<String, String>();
				temp.put("key", "email");
				temp.put("value", email);
				list.add(temp);
				
				temp = new HashMap<String, String>();
				temp.put("key", "friendemail");
				temp.put("value", friend);
				list.add(temp);
				

				Log.d("ChatActivity", "ip="+serverIp);
				String result = HttpUtil.sendHttpPost(serverIp, serverHttpPort, list);
				
				Log.d("kkang", result);
				
				String[] datas = result.toString().split("\\\\");
				String[] items;
				
				list = new ArrayList<HashMap<String, String>>();
				
				for (int i = 0; i < datas.length; i++) {
					items = datas[i].split("\\|", -1);
					
					if (items.length == 4) {
						temp = new HashMap<String, String>();
						temp.put("email", items[0]);
						temp.put("friend", items[1]);
						temp.put("message", items[2]);
						temp.put("datetime", items[3]);
						list.add(temp);
					}
				}
				msg.what = 1;
				msg.obj = list;
				handler.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
				Log.d("kkang", "Exception : " + e.getMessage());
				msg.what = 0;
				handler.sendMessage(msg);
			}

		}
	}

}
