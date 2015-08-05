package com.example.student.chat;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendThread extends Thread {
	Handler handler;
	String serverIp;
	String serverHttpPort;
	String email;
	
	public FriendThread(Handler _handler, String _serverIp, String _serverHttpPort, String _email) {
		handler = _handler;
		serverIp = _serverIp;
		serverHttpPort = _serverHttpPort;
		email = _email;
	}
	
	public void run() {
		Message msg = new Message();

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> temp = new HashMap<String, String>();

		try {
			temp.put("key", "type");
			temp.put("value", "GETFRIENDS");
			list.add(temp);
			
			temp = new HashMap<String, String>();
			temp.put("key", "email");
			temp.put("value", email);
			list.add(temp);
		
			String result = HttpUtil.sendHttpPost(serverIp, serverHttpPort, list);

			Log.d("kkang", result.toString());
			
			String[] datas = result.toString().split("\\\\");
			String[] items;
			
			list = new ArrayList<HashMap<String, String>>();
			
			for (int i = 0; i < datas.length; i++) {
				items = datas[i].split("\\|", -1);
				
				if (items.length == 4) {
					temp = new HashMap<String, String>();
					temp.put("nickname", items[0]);
					temp.put("email", items[1]);
					
					if(items[2].equals("0")) {
                        //Block 된 친구
						temp.put("status", "Block");
					} else if (items[2].equals("1")) {
                        //Block 되지 않은 친구라면
						if(items[3].equals("0")) {
							temp.put("status", "Offline");
						} else if(items[3].equals("1")) {
							temp.put("status", "Online");
						} else if(items[3].equals("2")) {
							temp.put("status", "away");
						} else {
							temp.put("status", "Offline");
						}
					} else if (items[2].equals("2")) {
                        //초청 상태 친구
						temp.put("status", "Waiting to add as a friend");
					} else {
						temp.put("status", "Offline");
					}
					list.add(temp);
				}
			}
			msg.what = 1;
			msg.obj = list;
			handler.sendMessage(msg);
		} catch (Exception e) {
			Log.d("kkang", "Exception : " + e.getMessage());
			msg.what = 0;
			handler.sendMessage(msg);
		}
	}

}
