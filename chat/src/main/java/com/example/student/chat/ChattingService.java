package com.example.student.chat;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;

public class ChattingService extends Service {
	
	Socket socket;
	BufferedInputStream bin;
	BufferedOutputStream bout;

	SocketThread st;
	ReadThread rt;
	
	boolean isCheck = true;
	boolean isConnected = false;
	boolean isRead = true;
	
	String email = "";
	String serverIp = "";
	String serverPort = "";


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.d("ChattingService", "Service started. ");
		PreferenceHelper prefs = PreferenceHelper.getInstance(this);
		email = prefs.getLoginId();
		serverIp = prefs.getServerIp();
		serverPort = prefs.getServerPort();
		st = new SocketThread(serverIp, Integer.parseInt(serverPort));
		st.start();

		registerReceiver(chatReceiver, new IntentFilter(getResources().getString(R.string.intent_to_service)));
	}

	BroadcastReceiver chatReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			try{
				Log.d("kkang.chatReceiver", "service.onReceive()");
				bout.write(("2|"+intent.getStringExtra("from")+"|"+intent.getStringExtra("to")+"|"+
						intent.getStringExtra("message")).getBytes());
				bout.flush();
			} catch(Exception e){
				e.printStackTrace();
				isConnected=false;
				try{
					rt.stop();
				}
				catch(Exception e2){

				}
			}
		}
	};

	private void stopSocket() {
		isCheck = false;
		isConnected = false;
		
		if (socket != null) {
			isRead=false;

			try {
				bout.close();
			} catch (IOException e) {
			}
			try {
				bin.close();
			} catch (IOException e) {
			}
			try {
				socket.close();
			} catch (IOException e) {
			}

		}

	}

	


	private boolean isChatActivityTop(){
		boolean isTop = false;

		ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		
		List<RunningTaskInfo> rti = am.getRunningTasks(1);
		
		if(rti != null && rti.size() > 0) {
			ComponentName topActivity = rti.get(0).topActivity;
			
			Log.d("kkang", topActivity.getPackageName() + "." + topActivity.getClassName());
			
			if(topActivity.getClassName().equals("com.example.student.chat.ChatActivity")) {
				isTop = true;
			}
		}
		return isTop;
	}

	private void handleReadMessage(String from, String message, String datetime){
		if(!isChatActivityTop()) {

				NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

				Intent newIntent = new Intent(this, ChatActivity.class);
				newIntent.putExtra("friend", from);
				newIntent.putExtra("datetime", datetime);
				newIntent.putExtra("message", message);
				newIntent.putExtra("status", "online");
				newIntent.putExtra("email", email);

				PendingIntent pIntent = PendingIntent.getActivity(this, 0,newIntent, 0);

				NotificationCompat.Builder builder = new NotificationCompat.Builder(ChattingService.this);
                builder.setSmallIcon(R.mipmap.ic_launcher);

				builder.setTicker(from+" "+message);
				builder.setContentTitle(from);
				builder.setContentText(message);
				builder.setContentIntent(pIntent);

				builder.setAutoCancel(true);

				Notification noti = builder.build();
				manager.notify(111, noti);

			
			
		} else {
			Intent bcIntent = new Intent(getResources().getString(R.string.intent_to_activity));
			bcIntent.putExtra("email", email);
			bcIntent.putExtra("from", from);
			bcIntent.putExtra("message", message);
			bcIntent.putExtra("status", "online");
			bcIntent.putExtra("datetime", datetime);
			sendBroadcast(bcIntent);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(chatReceiver);
		stopSocket();
	}

	class SocketThread extends Thread{
		String url;
		int port;
		public SocketThread(String url, int port){
			this.url = url;
			this.port = port;
		}

		@Override
		public void run() {
			Log.d("SocketThread", "Socket Thread Started. ");
			while(isCheck){
				try{
					MyApplication app = (MyApplication) getApplicationContext();
					if(!isConnected && !email.equals("") && app.isOnline()){
						socket = new Socket();
						SocketAddress remoteAddr = new InetSocketAddress(url, port);
						socket.connect(remoteAddr, 10000);

						bout = new BufferedOutputStream(socket.getOutputStream());
						bin = new BufferedInputStream(socket.getInputStream());

						if(rt!=null){
							isRead = false;
						}

						rt = new ReadThread();
						rt.start();

						bout.write(("1|" + email).getBytes());
						bout.flush();

						isConnected=true;
						Log.d("SocketThread", "conneected successfully.  ");
					}
					else{
						// do nothing
						Thread.sleep(1000);
					}
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	class ReadThread extends Thread {
		@Override
		public void run() {
			Log.d("ReadThread", "read thread start");
			byte[] buffer = null;
			while(isRead){
				buffer = new byte[1024];
				try{
					String message = null;
					int size = bin.read(buffer);
					if(size>0){
						message = new String(buffer, 0, size, "utf-8");
						if(message !=null && !message.isEmpty()){
							String[] messages = message.split("\\|", -1);
							if(messages.length>0){
								if(messages[0].equals("3") && messages.length > 3){
									handleReadMessage(messages[1], messages[2], messages[3]);
								}
							}
						}
					}
					else{
						isRead = false;
						isConnected = false;
					}
				}
				catch(Exception e){
					e.printStackTrace();
					isRead = false;
					isConnected = false;
				}

			}
		}
	}
}
