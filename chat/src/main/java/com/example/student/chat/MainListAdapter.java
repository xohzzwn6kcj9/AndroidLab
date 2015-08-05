package com.example.student.chat;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class MainListAdapter extends ArrayAdapter<Map<String, String>> {
	Context context;
	ArrayList<Map<String, String>> al;
	boolean isMain = true;
	
	public MainListAdapter(Context _context, ArrayList<Map<String, String>> _al, boolean _isMain) {
		super(_context, R.layout.item_main, _al);
		this.context = _context;
		this.al = _al;
		this.isMain = _isMain;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		if(row == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
			row = inflater.inflate(R.layout.item_main, parent, false);
		}
		
		LinearLayout userLayout = (LinearLayout) row.findViewById(R.id.user_layout);
		ImageView userIcon = (ImageView) row.findViewById(R.id.user_icon);
		ImageView statusIcon = (ImageView) row.findViewById(R.id.status_icon);
		TextView userId = (TextView) row.findViewById(R.id.user_id);
		TextView userStatus = (TextView) row.findViewById(R.id.user_status);
		
		String uStatus = al.get(position).get("status").toLowerCase();
		
		if(position == 0 && isMain) {
			row.setBackgroundColor(Color.DKGRAY);
			userId.setTextColor(Color.WHITE);
			userStatus.setTextColor(Color.WHITE);
			userLayout.setBackgroundResource(0);
			
		} else {
			row.setBackgroundColor(Color.WHITE);
			userId.setTextColor(Color.BLACK);
			userStatus.setTextColor(Color.BLACK);
			
		}
		
		String email = al.get(position).get("email");
		
		// User Image Icon
				String ess = Environment.getExternalStorageState();
				String filePath="";
				if(ess.equals(Environment.MEDIA_MOUNTED)) {
					filePath = Environment.getExternalStorageDirectory()
							.getAbsolutePath()
							+ "/.multitalk/" + email + ".png";
				}else {
                    //내장 메모리 file path
					filePath=context.getFileStreamPath(email+".png").getPath();
				}
				Log.d("kkang",filePath);
				
				try {
					File file = new File(filePath);
					if(file.exists())
						userIcon.setImageBitmap(BitmapFactory.decodeFile(filePath));
				} catch (Exception e) {
					e.printStackTrace();
				}
		
		// User Status Icon
		if(uStatus.equals("online")) {
			statusIcon.setImageResource(R.drawable.presence_online);
		} else if (uStatus.equals("away")) {
			statusIcon.setImageResource(R.drawable.presence_away);
		} else if (uStatus.equals("busy")) {
			statusIcon.setImageResource(R.drawable.presence_busy);
		} else if (uStatus.equals("invisible")) {
			statusIcon.setImageResource(R.drawable.presence_invisible);
		} else {
			statusIcon.setImageResource(R.drawable.presence_offline);
		}

		row.setDrawingCacheBackgroundColor(Color.BLACK);
		
		// Nickname + User ID
		String nickName = al.get(position).get("nickname");
		if(nickName != null && !nickName.equals("")) {
			userId.setText(nickName + "(" + email + ")");
		} else {
			userId.setText(email);
		}
		userStatus.setText(al.get(position).get("status"));
		
		return row;
	}
	
}
