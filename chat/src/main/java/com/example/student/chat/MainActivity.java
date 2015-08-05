package com.example.student.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements OnItemClickListener,
		OnItemLongClickListener {
	
	final static int DEFAULT_REQUEST_CODE = 0;
	final static int PREFERENCE_REQUEST_CODE = 1;

	Handler handler;
	ListView lv;
	ArrayList<Map<String, String>> al;
	
	ProgressDialog myDialog;
	
	SharedPreferences prefs;
	String email = "";
	String nickname = "";
	String status = "";
	String serverIp = "";
	String serverHttpPort = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		
		prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
		email = prefs.getString("LoginID", "");
		nickname = prefs.getString("Nickname", "");
		status = prefs.getString("Status", "Online");
		serverIp = prefs.getString("ServerIP", "");
		serverHttpPort = prefs.getString("ServerHttpPort", "");
		
		myDialog = ProgressDialog.show(this, "" , " Loading...", true, true);

		lv = (ListView) findViewById(R.id.main_list);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 1) {
					myDialog.cancel();
					makeList(msg.obj);
				} else if(msg.what == 0) {
					myDialog.cancel();
					Toast.makeText(MainActivity.this, "Fail to load friend list.", Toast.LENGTH_SHORT).show();
					makeList(new ArrayList<Map<String, String>>());
				}
				
			}
		};

		lv.setOnItemClickListener(this);
		lv.setOnItemLongClickListener(this);
		
		FriendThread fThread = new FriendThread(handler, serverIp, serverHttpPort, email);
		fThread.start();
	}

	@SuppressWarnings("unchecked")
	private void makeList(Object obj) {
		al = (ArrayList<Map<String, String>>) obj;
		
		HashMap<String, String> temp = new HashMap<String, String>();
		nickname = prefs.getString("Nickname", "");
		status = prefs.getString("Status", "Online");
		temp.put("email", email);
		temp.put("nickname", nickname);
		temp.put("status", status);
		
		al.add(0, temp);
		
		Log.d("kkang", al.toString());
		
		lv.setAdapter(new MainListAdapter(this, al, true));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if (position == 0) {
			Intent intent = new Intent(MainActivity.this, MyStatusActivity.class);
			intent.putExtra("email", email);
			intent.putExtra("serverIp", serverIp);
			intent.putExtra("serverHttpPort", serverHttpPort);
			startActivityForResult(intent, DEFAULT_REQUEST_CODE);
		} else {
			if(al.get(position).get("status").toUpperCase().startsWith("OFF")) {
				Toast.makeText(this, "He is offline.", Toast.LENGTH_SHORT).show();
			} else if(!al.get(position).get("status").toUpperCase().startsWith("WAIT")) {
				Intent intent = new Intent(MainActivity.this, ChatActivity.class);
				intent.putExtra("email", email);
				intent.putExtra("friend", al.get(position).get("email"));
				intent.putExtra("status", al.get(position).get("status"));
				intent.putExtra("serverIp", serverIp);
				intent.putExtra("serverHttpPort", serverHttpPort);
				startActivity(intent);
			}
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View position, int arg2, long arg3) {
		new AlertDialog.Builder(this).setTitle(al.get(arg2).get("email")).setItems(
				R.array.list, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int selectedIndex) {
						String[] list = getResources().getStringArray(
								R.array.list);
						
						Log.d("kkang", list[selectedIndex]);

					}
				}).show();
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuItem items = menu.add(0, 0, 0, "Add friend");
		items.setIcon(R.drawable.ic_menu_invite);
		items = menu.add(0, 1, 1, "Sign out");
		items.setIcon(R.drawable.ic_menu_close_clear_cancel);
		items = menu.add(0, 2, 1, "Setting");
		items.setIcon(R.drawable.ic_menu_manage);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case 0:	// Invite Friend
			Intent intent = new Intent(MainActivity.this, InviteActivity.class);
			intent.putExtra("email", email);
			intent.putExtra("serverIp", serverIp);
			intent.putExtra("serverHttpPort", serverHttpPort);
			startActivityForResult(intent, DEFAULT_REQUEST_CODE);
			break;
		case 1:	// Sign out
			try {
				Intent mIntent = new Intent();
				mIntent.setAction("com.multicampus.android.project.chat.ChattingService");
				stopService(mIntent);
			} catch (Exception e) {
				// if the service is not running, you will get a nullpointerexception.
			}
			finish();
			break;
		case 2:	// Setting
			Intent intent1 = new Intent(MainActivity.this, SettingPreferences.class);
			startActivityForResult(intent1, PREFERENCE_REQUEST_CODE);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case DEFAULT_REQUEST_CODE:
			if(resultCode == RESULT_OK) {
				myDialog = ProgressDialog.show(this, "" , " Loading...", true, true);
				FriendThread fThread = new FriendThread(handler, serverIp, serverHttpPort, email);
				fThread.start();
			}
			break;
		case PREFERENCE_REQUEST_CODE:
			myDialog = ProgressDialog.show(this, "" , " Loading...", true, true);
			email = prefs.getString("LoginID", "");
			nickname = prefs.getString("Nickname", "");
			status = prefs.getString("Status", "Online");
			serverIp = prefs.getString("ServerIP", "");
			serverHttpPort = prefs.getString("ServerHttpPort", "");

			FriendThread fThread = new FriendThread(handler, serverIp, serverHttpPort, email);
			fThread.start();
			break;
		}
	}
	
}