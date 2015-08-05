package com.example.student.chat;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckInviteActivity extends  ListActivity {
	
	TextView tv;
	
	ProgressDialog myDialog;
	
	Handler handler;

	String email;
	String serverIp;
	String serverPort;
	String serverHttpPort;
	
	String[] datas;
	ArrayAdapter<String> aa;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
   
	    setContentView(R.layout.activity_check_invite);
	
	    myDialog = ProgressDialog.show(this, "" , " Loading...", true, true);
	    
	    email = getIntent().getStringExtra("email");
	    serverIp = getIntent().getStringExtra("serverIp");
	    serverPort = getIntent().getStringExtra("serverPort");
	    serverHttpPort = getIntent().getStringExtra("serverHttpPort");
	    
	    tv = (TextView) findViewById(R.id.check_invite_tv);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0) {
                    //에러가 난경우
                    myDialog.cancel();
                } else if(msg.what == 1) {
                    //초청 리스트에 대한 데이터 요청이 정상 처리된경우
                    myDialog.cancel();
                    makeList();
                } else if(msg.what == 10) {
                    //에러가 난경우
                    myDialog.cancel();
                } else if(msg.what == 11) {
                    //서버의 초청 리스트에 초청 허용 한경우 다시 초청 리스트를 수정해서 받기 위함
                    CheckInviteMe cimThread = new CheckInviteMe();
                    cimThread.start();
                }
            }
        };
	    
	    CheckInviteMe cimThread = new CheckInviteMe();
	    cimThread.start();

	}

	private void removeList(int position) {
	    myDialog = ProgressDialog.show(this, "" , " Loading...", true, true);
	    AllowInvite aiThread = new AllowInvite(datas[position]);
	    aiThread.start();
	}

private void makeList() {
    if(datas.length > 0 && !datas[0].equals("")) {
        //초청 리스트가 있는경우
        tv.setText("someone invite you");
        aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datas);
        setListAdapter(aa);
    } else {
        //없는경우는 MainActivity 를 띄우고 자신은 finish
        Intent intent = new Intent(CheckInviteActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		final int pst = position;
		new AlertDialog.Builder(this)
		.setTitle("Allow the invite")
		.setMessage("Could you allow the invite?")
		.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
                //서버에 초청 호락을 함
				removeList(pst);
			}
		})
		.show();
	}

    //백버튼을 제어하는 부분... 해당 어플리케이션도 finish
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(CheckInviteActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	class CheckInviteMe extends Thread {
		
		public void run() {
			Message msg = new Message();

			try {
				ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> temp = new HashMap<String, String>();
				temp.put("key", "type");
				temp.put("value", "GETINVITEME");
				list.add(temp);
				
				temp = new HashMap<String, String>();
				temp.put("key", "email");
				temp.put("value", email);
				list.add(temp);
				
				String result = HttpUtil.sendHttpPost(serverIp, serverHttpPort, list);

				Log.d("kkang", result);
				
				datas = result.split("\\|");
				
				msg.what = 1;
				handler.sendMessage(msg);

			} catch (Exception e) {
				Log.d("kkang", "Exception : " + e.getMessage());
				msg.what = 0;
				handler.sendMessage(msg);
			}
		}
	}
	
	class AllowInvite extends Thread {
		String friendemail;
		
		AllowInvite(String _friendemail) {
			friendemail = _friendemail;
		}
		
		public void run() {
			Message msg = new Message();

			try {
				
				ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> temp = new HashMap<String, String>();
				temp.put("key", "type");
				temp.put("value", "ALLOWINVITE");
				list.add(temp);
				
				temp = new HashMap<String, String>();
				temp.put("key", "email");
				temp.put("value", email);
				list.add(temp);
				
				temp = new HashMap<String, String>();
				temp.put("key", "friendemail");
				temp.put("value", friendemail);
				list.add(temp);
				
				String result = HttpUtil.sendHttpPost(serverIp, serverHttpPort, list);
				
				
				if(result.equals("OK")) {
					msg.what = 11;
					handler.sendMessage(msg);
				} else {
					msg.what = 10;
					handler.sendMessage(msg);
				}

			} catch (Exception e) {
				Log.d("kkang", "Exception : " + e.getMessage());
				msg.what = 10;
				handler.sendMessage(msg);
			}
		}
	}

}
