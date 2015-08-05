package com.example.student.chat;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class ChatListAdapter extends ArrayAdapter<Map<String, String>> {

	Context context;
	ArrayList<Map<String, String>> al;
	
	public ChatListAdapter(Context _context, ArrayList<Map<String, String>> objects) {
		super(_context, R.layout.item_chat, objects);
		this.al = objects;
		this.context = _context;
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if(row == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
			row = inflater.inflate(R.layout.item_chat, parent, false);
		}
		
		ImageView iv = (ImageView) row.findViewById(R.id.chat_user_icon);
		TextView tv = (TextView) row.findViewById(R.id.chat_msg);
		Log.d("kkang","00000:"+al.get(position).get("from")+":"+al.get(position).get("msg"));
		String filePath = Environment
		.getExternalStorageDirectory().getAbsolutePath()
		+ "/.multitalk/"
		+ al.get(position).get("from") + ".png";
		
		File file = new File(filePath);
		if(file.exists()) {
			Log.d("kkang",al.get(position).get("from")+":"+al.get(position).get("msg")+":1111111111");
			iv.setImageBitmap(BitmapFactory.decodeFile(filePath));
		}

		
		if(al.get(position).get("from").equals(al.get(position).get("email"))) {
			row.setBackgroundColor(Color.LTGRAY);
		} else {
			row.setBackgroundColor(Color.WHITE);
		}
        //add~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		//Html 문자열 효과를 적용해서 chat 글을 출력..
		tv.setText(
				Html.fromHtml(//문자열의 html 태그에 대한 ui 적용..
						String.format(//문자열 패턴에 동적데이터 삽입시켜서..
								context.getString(R.string.chat_dialog),//패턴..%1$s
								al.get(position).get("from"),//첫번째 데이터
								al.get(position).get("msg").replace("\n","<br>"),
								al.get(position).get("datetime"))));
		//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		return row;
	}
}
