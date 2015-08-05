package com.example.student.chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class InviteActivity extends Activity implements OnClickListener {
	
	Handler handler;
	
	ProgressDialog myDialog;

	Button btn;
	EditText et;
	
	String email;
	String serverIp;
	String serverHttpPort;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_invite);

	    btn = (Button) findViewById(R.id.invite_btn);
	    btn.setOnClickListener(this);
	    et = (EditText) findViewById(R.id.invite_email);
	    
	    Intent intent = getIntent();
	    email = intent.getStringExtra("email");
	    serverIp = intent.getStringExtra("serverIp");
	    serverHttpPort = intent.getStringExtra("serverHttpPort");
	    
	    handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 1) {
					myDialog.cancel();
					Intent intent = getIntent();
					intent.putExtra("friendemail", et.getText().toString().trim());
					setResult(RESULT_OK, intent);
					finish();
				} else if(msg.what == 0) {
					myDialog.cancel();
					Toast.makeText(InviteActivity.this, "Fail to invite the friend.", Toast.LENGTH_SHORT);
				}
			}
	    	
	    };

	}

	@Override
	public void onClick(View v) {
		if(v == btn) {
			myDialog = ProgressDialog.show(this, "" , " Loading...", true, true);
			
			InviteThread iThread = new InviteThread();
			iThread.start();
		}
	}
	
	class InviteThread extends Thread {
		public void run() {
			Message msg = new Message();

			try {
				ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> temp = new HashMap<String, String>();
				temp.put("key", "type");
				temp.put("value", "ADDFRIEND");
				list.add(temp);
				
				temp = new HashMap<String, String>();
				temp.put("key", "email");
				temp.put("value", email);
				list.add(temp);
				
				temp = new HashMap<String, String>();
				temp.put("key", "friendemail");
				temp.put("value", et.getText().toString().trim());
				list.add(temp);
				
				temp = new HashMap<String, String>();
				temp.put("key", "linkstatus");
				temp.put("value", "2");
				list.add(temp);
				
				String result = HttpUtil.sendHttpPost(serverIp, serverHttpPort, list);
				
				if(result.equals("OK")) {
					msg.what = 1;
					handler.sendMessage(msg);
				} else {
					msg.what = 0;
					handler.sendMessage(msg);
				}

			} catch (Exception e) {
				Log.d("kkang", "Exception : " + e.getMessage());
				msg.what = 0;
				handler.sendMessage(msg);
			}

		}
	}

}
