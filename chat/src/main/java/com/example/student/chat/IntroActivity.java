package com.example.student.chat;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

public class IntroActivity extends AppCompatActivity {

    Handler handler;//thread - handler..

    //user 설정 데이터..
    String email;
    String serverIp;
    String serverPort;
    String serverHttpPort;

    //Activity의  ANR문제때문에..  thread-handler 프로그램이 되는거지만..
    //시간이 오래걸리는 업무가 아니라고 하더라도. 업무의 결과에 의해
    //다양하게 화면이 변경이 되는경우.. thread-handler구조가 편하다는 이유..
    class CheckThread extends Thread {
        @Override
        public void run() {
            checkStatus();
        }
    }

    private void checkStatus(){
        //thread는 UI 구성요소인 View 객체를 접근할수 없다..
        //main thread에게 UI Update의뢰시 넘길 객체.. VO
        //what : int - 요청의 구분자.. 개발자 임의 숫자..
        //obj : Object - main thread에게 넘길 데이터..
        Message message=new Message();

        //설정쪽에서 데이터를 Preference로 저장할거다..
        //Preference : Map(key-value)==>file(xml) ==>개발자 임의 파일명을주어
        //몇개의 파일도 이용 가능하다..
        //getDefaultSharedPreferences 을 이용하면 app package명을 이용한
        //기본 파일을 이용..
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(
                this);

        email=prefs.getString("LoginID","");
        if(email.equals("")){
            message.what=0;
        }else {
            message.what=1;//정상상황..

            serverIp=prefs.getString("ServerIP","");
            serverPort=prefs.getString("ServerPort","");
            serverHttpPort=prefs.getString("ServerHttpPort","");

            if(serverIp.equals("") || serverPort.equals("") ||
                    serverHttpPort.equals("")){
                message.what=2;//서버 설정 안된경우..
            }else {
                //모든 설정이 잘 되어 있는 경우..

                //서버와 실제 체팅은 Socket으로.. 하루종일 connection을 유지해서
                //==>Service에서 구현..
                //==>Service를 boot complete 시점에.. start....
                //==>Service는 유저에 의해.. System에 의해 종료된다..
                //==>우리의 service가 구동중인지 체크하고 가겠다..

                boolean isMyService=false;
                ActivityManager am=(ActivityManager)
                        getSystemService(ACTIVITY_SERVICE);
                List<ActivityManager.RunningServiceInfo> rs=
                        am.getRunningServices(100);
                ActivityManager.RunningServiceInfo rsi=null;
                for(int i=0;i<rs.size();i++){
                    rsi=rs.get(i);
                    if(rsi.service.getClassName().equals(
                            "com.example.student.chat.ChattingService")){
                        isMyService=true;
                        break;
                    }
                }

                if(!isMyService){
                    Intent intent=new Intent(IntroActivity.this,
                            ChattingService.class);
                    startService(intent);
                }
            }
        }

        //main thread에게 화면 update의뢰..
        handler.sendMessage(message);

    }

    private void toastMessage(String message){
        Toast t=Toast.makeText(this, message, Toast.LENGTH_SHORT);
        t.show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        //handler의 sendMessage에 의한 화면 update의뢰를 처리할려면..
        //Handler의 서브 클래스 만들어야..
        handler=new Handler(){
            //thread가 sendMessage 하는 순간.. main thread에 의해 자동 호출..
            //thread가 넘긴 Message객체 매게변수로 전달..
            @Override
            public void handleMessage(Message msg) {
                if(msg.what==0){
                    //email 설정 안된경우..
                    Intent intent=new Intent(IntroActivity.this,
                            SettingPreferences.class);
                    startActivityForResult(intent, 10);
                    toastMessage("edit email......");
                }else if(msg.what==1){
                    //정상 설정된경우..
                    Intent intent=new Intent(IntroActivity.this,
                            CheckInviteActivity.class);
                    intent.putExtra("email",email);
                    intent.putExtra("serverIp",serverIp);
                    intent.putExtra("serverPort",serverPort);
                    intent.putExtra("serverHttpPort",serverHttpPort);
                    startActivity(intent);
                    finish();
                }else if(msg.what==2){
                    //network 설정 안된경우..
                    Intent intent=new Intent(IntroActivity.this,
                            SettingPreferences.class);
                    startActivityForResult(intent, 10);
                    toastMessage("edit network info......");
                }
            }
        };

        //app전역에서 network이 발생.. 현 유저 폰의 network 가능 상태파악
        //네트웍 가능? wifi? 3G?
        //wifi 활성화. 비활성화.. 가용wifi 있으면 접속.. wifi access point 변경
        //신호세기 변경 감지 가능..
        ConnectivityManager manager=(ConnectivityManager)
                getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mobile=manager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi=manager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI);

        boolean isOnline=false;
        if(wifi.isConnected()){
            isOnline=true;
        }else if(mobile.isConnected()){
            isOnline=true;
        }

        MyApplication app=(MyApplication)getApplicationContext();
        if(isOnline){
            CheckThread t=new CheckThread();
            t.start();
            app.setIsOnline(true);
        }else {
            app.setIsOnline(false);
            toastMessage("network error...");
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        checkStatus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_intro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
