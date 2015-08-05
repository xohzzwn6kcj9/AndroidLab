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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(prefs.getString("Status", "Online"))) {
                spin.setSelection(i);
                break;
            }
        }
//Add0 Start------------------
        String ess = Environment.getExternalStorageState();
        String filePath = "";
        if (ess.equals(Environment.MEDIA_MOUNTED)) {
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/.multitalk/" + email + ".png";
        } else {
            filePath = getFileStreamPath(email + ".png").getPath();
        }

        File file = new File(filePath);
        if (file.exists()) {
            iv.setImageBitmap(BitmapFactory.decodeFile(filePath));
        } else {

        }

//Add0 End ----------------------		
        handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    myDialog.cancel();
                    setResult(RESULT_OK);
                    finish();
                } else if (msg.what == 0) {
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
                Uri selected = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = null;

                if (Build.VERSION.SDK_INT > 19) { //lollypop
                    String text = selected.getLastPathSegment();
                    String[] datas = text.split(":");
                    String id = datas[1];

                    cursor = getContentResolver().query(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            filePathColumn,
                            MediaStore.Images.Media._ID + "=" + id,
                            null,
                            null
                    );
                } else {
                    cursor = getContentResolver().query(
                            selected,
                            filePathColumn, null, null, null);
                }
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                cursor.close();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 10;
                Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
                iv.setImageBitmap(bitmap);

                writeFile(bitmap);
//Add2 end ----------------------------------------------
            }
        }
    }

    private void writeFile(Bitmap bitmap) {
        //External? internal?
        String ess = Environment.getExternalStorageState();
        if (ess.equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.multitalk");
            if (!file.exists()) {
                file.mkdir();
            }
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.multitalk" + email + ".png");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                FileOutputStream out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else{
            FileOutputStream out = null;
            try {
                out = openFileOutput(email+".png", MODE_PRIVATE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
    }
        @Override
        public void onClick (View v){
            if (v == iv) {

//Add1 Start --------------------------
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 0);

//Add1 End-----------------------------
            } else if (v == doneBtn) {
                myDialog = ProgressDialog.show(this, "", " Loading...", true, true);
                MyStatusThread thread = new MyStatusThread();
                thread.start();
            } else if (v == cancelBtn) {
                setResult(RESULT_CANCELED);
                finish();
            }

        }

        @Override
        public void onItemSelected (AdapterView < ? > arg0, View arg1,int arg2,
        long arg3){
            // TODO Auto-generated method stub

        }

        @Override
        public void onNothingSelected (AdapterView < ? > arg0){
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

                    if (result.toUpperCase().equals("OK")) {
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
