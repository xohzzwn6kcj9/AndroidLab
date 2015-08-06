package com.example.student.lbs;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class MainActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO Auto-generated method stub
        setContentView(R.layout.activity_main);

        //구동중인 Service 목록 확인
        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);

        //최대 100개의 목록을 가져와라
        List<RunningServiceInfo> rs = am.getRunningServices(100);
        RunningServiceInfo rsi = null;
        boolean isMyService=false;

        for (int i = 0; i < rs.size(); i++) {
            rsi = rs.get(i);
            Log.d("kkang", "Process " + rsi.process + " with component " + rsi.service.getClassName());
            if (rsi.service.getClassName().equals("com.example.pjt_lbs_exam1.LocationService")) {
                isMyService = true;
                break;
            }
        }

        if (!isMyService) {
            Log.d("kkang", "Start ChattingService");
            Intent mIntent = new Intent(this,LocationService.class);

            startService(mIntent);
        }


        SharedPreferences pref=getSharedPreferences("myPref", MODE_PRIVATE);
        final String password=pref.getString("password", "");
        if(password == null || password.length() < 1){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.emo_im_yelling);
            builder.setTitle("알림");
            builder.setMessage("처음 이용시 비밀번호 설정을 하셔야 합니다.!!");
            builder.setCancelable(false);
            builder.setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog,
                                            int which) {
                            // TODO Auto-generated method stub
                            Intent intent=new Intent(MainActivity.this,PasswordSettingActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }else {
            final EditText passView=(EditText)findViewById(R.id.intro_pass);
            Button bt=(Button)findViewById(R.id.intro_bt);
            bt.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    String inputPassword=passView.getText().toString();
                    if(inputPassword != null && inputPassword.equals(password)){
                        Intent intent=new Intent(MainActivity.this,LocationListActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setIcon(R.drawable.emo_im_yelling);
                        builder.setTitle("알림");
                        builder.setMessage("비밀번호가 다릅니다. 다시 시도해 주세요");
                        builder.setCancelable(false);
                        builder.setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                }
            });
        }
    }

}
