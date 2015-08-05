package com.example.student.chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class MyStatusActivity extends Activity implements OnClickListener,
		OnItemSelectedListener {

	SharedPreferences prefs;

	Handler handler;
	ImageView iv;
	Spinner spin;
	Button doneBtn;
	Button cancelBtn;
	EditText nickname;
	
	ProgressDialog myDialog;
	
	String email;
	String serverIp = "";
	String serverHttpPort = "";

	String[] items = {"Invisible", "Online", "Busy"};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_status);
		
		email = getIntent().getStringExtra("email");
	    serverIp = getIntent().getStringExtra("serverIp");
	    serverHttpPort = getIntent().getStringExtra("serverHttpPort");
	    
		spin = (Spinner) findViewById(R.id.my_status_sp);
		spin.setOnItemSelectedListener(this);

		ArrayAdapter<String> aa = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, items);
		aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spin.setAdapter(aa);

		iv = (ImageView) findViewById(R.id.my_status_iv);
		doneBtn = (Button) findViewById(R.id.my_status_done);
		cancelBtn = (Button) findViewById(R.id.my_status_cancel);
		nickname = (EditText) findViewById(R.id.my_status_nickname);

		doneBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		iv.setOnClickListener(this);

		prefs = PreferenceManager.getDefaultSharedPreferences(MyStatusActivity.this);
		
		nickname.setText(prefs.getString("Nickname", ""));
		
		for(int i=0;i<items.length;i++) {
			if(items[i].equals(prefs.getString("Status", "Online"))) {
				spin.setSelection(i);
				break;
			}
		}
//Add0 Start------------------
		//유저 이미지 저장된게 있다면 그걸로 사진 출력..
		//폰에 따라 외장 메모리 없는 경우도 있고..
		//외장 메모리.. 모든 app에서 access가능해서.. 보안상 문제되는 경우는
		//app 내부 디렉토리에 일부러 저장하는 경우도..
		String ess= Environment.getExternalStorageState();
		String filePath="";
		if(ess.equals(Environment.MEDIA_MOUNTED)){
			filePath=Environment.getExternalStorageDirectory()
					.getAbsolutePath()+"/.multitalk/"+email+".png";
		}else {
			//app 내부 디렉토리에..
			//data/data/<package_name>/~~~
			//perference file, db file
			filePath=getFileStreamPath(email+".png").getPath();
		}
		try{
			File file=new File(filePath);
			if(file.exists())
				iv.setImageBitmap(BitmapFactory.decodeFile(filePath));
		}catch(Exception e){
			e.printStackTrace();
		}
//Add0 End ----------------------		
		handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if(msg.what == 1) {
					myDialog.cancel();
					setResult(RESULT_OK);
					finish();
				} else if(msg.what == 0) {
					myDialog.cancel();
					Toast.makeText(MyStatusActivity.this, "Fail to save status.", Toast.LENGTH_SHORT).show();
				}
			}
			
		};
	}
	//add 1--------------------

	//end--------------------------

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
//Add2 start ---------------------------------------
				//gallery 목록 사진 선택.. 되돌리는건 선택 이미지의 id 값만..
				//ContentProvider 이용해서 원하는 데이터 획득..
				Uri selected=data.getData();
				//획득 column
				String[] filePathColumn={MediaStore.Images.Media.DATA};

				Cursor cursor=null;
				if(Build.VERSION.SDK_INT>19){
					//lollipop 의 gallery app의 id 값이 image:111형식으로..
					String txt=selected.getLastPathSegment();
					String[] datas=txt.split(":");
					String id=datas[1];

					cursor=getContentResolver().query(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							filePathColumn,
							MediaStore.Images.Media._ID+"="+id,
							null,null);
				}else {
					//path에 id값이 들어가면 별도 조건을 안주어도..
					//path에서 id 추출해서 조건으로 사용해줌으로..
					cursor=getContentResolver().query(
							selected,
							filePathColumn, null, null, null);
				}

				cursor.moveToFirst();
				int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
				String filePath=cursor.getString(columnIndex);
				cursor.close();

				try{
					//gallery app에 저장된 이미지는 너무 사이즈가 크다..
					//그렇게 크게 나오지도 않을텐데..
					//==>사이즈를 줄여서...
					BitmapFactory.Options options=new BitmapFactory.Options();
					options.inSampleSize=10;//10분의 1로 줄여서 읽어들인다..

					Bitmap bitmap=BitmapFactory.decodeFile(filePath, options);
					//일단 화면 출력..
					iv.setImageBitmap(bitmap);

					//file write..
					writeFile(bitmap);
				}catch(Exception e){
					e.printStackTrace();
				}

//Add2 end ----------------------------------------------
			}
		}
	}

	private void writeFile(Bitmap bitmap){
		//외장. 내장?
		//원본 이미지에 사이즈 줄어든걸 그대로 다시 write 하면 안된다..
		//별도의 디렉토리에 별도의 파일로 write..
		//우리의 데이터은 코드적으로만 표현된 이미지.. 파일 write할려면..
		//file 포멧은?
		String ess=Environment.getExternalStorageState();
		if(ess.equals(Environment.MEDIA_MOUNTED)){
			try{
				File file=new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath()+"/.multitalk");
				//FileOutputStream을 이용해서.. file write.. 디렉토리 파일
				//안만들고 들어가되되지 않나?==>우리는..파일 포멧때문에...
				//직접 io로 write하지 않고 api의 도움.. 파일 없으면 에러..
				if(!file.exists())
					file.mkdir();

				file=new File(Environment.getExternalStorageDirectory()
						.getAbsolutePath()+"/.multitalk/"+email+".png");
				if(!file.exists())
					file.createNewFile();

				FileOutputStream out=new FileOutputStream(file);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

			}catch(Exception e){
				e.printStackTrace();
			}
		}else {
			try{
				FileOutputStream out=openFileOutput(email+".png", MODE_PRIVATE);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (v == iv) {

//Add1 Start --------------------------
			//이미지를 교체하기 위해서 클릭한경우..
			//gallery app의 목록 activity를 intent로..
			Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			startActivityForResult(intent, 0);
//Add1 End-----------------------------
		} else if (v == doneBtn) {
			myDialog = ProgressDialog.show(this, "" , " Loading...", true, true);
			MyStatusThread thread = new MyStatusThread();
			thread.start();
		} else if (v == cancelBtn) {
			setResult(RESULT_CANCELED);
			finish();
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
	
	class MyStatusThread extends Thread {
		public void run() {
			Message msg = new Message();

			try {
				Editor editor = prefs.edit();
				
				editor.putString("Nickname", nickname.getText().toString());
				editor.putString("Status", items[spin.getSelectedItemPosition()]);
				editor.commit();
				
				ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
				HashMap<String, String> temp = new HashMap<String, String>();
				temp.put("key", "type");
				temp.put("value", "MODIFY");
				list.add(temp);
				
				temp = new HashMap<String, String>();
				temp.put("key", "email");
				temp.put("value", email);
				list.add(temp);
				
				temp = new HashMap<String, String>();
				temp.put("key", "nickname");
				temp.put("value", nickname.getText().toString());
				list.add(temp);
				
				temp = new HashMap<String, String>();
				temp.put("key", "status");
				temp.put("value", String.valueOf(spin.getSelectedItemPosition()));
				list.add(temp);
				
				String result = HttpUtil.sendHttpPost(serverIp, serverHttpPort, list);
				
				if(result.toUpperCase().equals("OK")) {
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
