package com.example.student.chat;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.List;

public class IntroActivity extends AppCompatActivity {


    private static final int EMPT_EMAIL = 0;
    private static final int EMAIL_OK = 1;
    private static final int SERVER_CONFIG_ERROR = 2;

    private static final int REQUEST_CODE_PREFENCE = 10;

    public static final String INTENT_EXTRA_EMAIL = "email";
    public static final String INTENT_EXTRA_SERVER_IP = "serverIp";
    public static final String INTENT_EXTRA_SERVER_PORT = "serverPort";
    public static final String INTENT_EXTRA_SERVER_HTTP_PORT = "serverHttpPort";

    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case EMPT_EMAIL:
                    startSettingPreference();
                    break;
                case EMAIL_OK:
                    startCheckInviteActivity();
                    break;
                case SERVER_CONFIG_ERROR:
                    startSettingPreference();
                    break;
            }
        }

    private void startCheckInviteActivity() {
        final Intent intent = new Intent(IntroActivity.this, CheckInviteActivity.class);
        intent.putExtra(INTENT_EXTRA_EMAIL, email);
        intent.putExtra(INTENT_EXTRA_SERVER_IP, serverIp);
        intent.putExtra(INTENT_EXTRA_SERVER_PORT, serverPort);
        intent.putExtra(INTENT_EXTRA_SERVER_HTTP_PORT, serverHttpPort);
        startActivity(intent);
        finish();
    }

    private void startSettingPreference() {
        final Intent intent = new Intent(IntroActivity.this, SettingPreferences.class);
        startActivityForResult(intent, REQUEST_CODE_PREFENCE);
        toastMessage("edit email..");
    }
};

    String email;
    String serverIp;
    String serverPort;
    String serverHttpPort;

    class CheckThread extends Thread {
        @Override
        public void run() {
            checkStatus();
        }
    }
    private void checkStatus() {
        Message message = new Message();

        PreferenceHelper prefs = PreferenceHelper.getInstance(this);
        email = prefs.getLoginId();
        if(email.isEmpty()){
            message.what=EMPT_EMAIL;
        }
        else{
            message.what= EMAIL_OK;
            serverIp = prefs.getServerIp();
            serverPort = prefs.getServerPort();
            serverHttpPort = prefs.getServerHttpPort();
            if(serverIp.isEmpty() || serverPort.isEmpty() || serverHttpPort.isEmpty()){
                message.what=SERVER_CONFIG_ERROR;
            }
            else if(!isMyService()){
                final Intent intent = new Intent(IntroActivity.this, ChattingService.class);
                startService(intent);
            }
        }
        handler.sendMessage(message);
    }
    private boolean isMyService(){
        final ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> rs = am.getRunningServices(100);
        ActivityManager.RunningServiceInfo rsi = null;
        for(int i=0; i<rs.size(); i++){
            rsi = rs.get(i);
            if(rsi.service.getClassName().equals("com.example.student.chat.ChattingService")){
                return true;
            }
        }
        return false;
    }
    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);


        MyApplication app = (MyApplication) getApplicationContext();
        app.updateNetworkStatus();
        if (app.isOnline()) {
            CheckThread t = new CheckThread();
            t.start();
        }
        else{
            toastMessage("network error");
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        checkStatus();
    }

    private boolean isOnline(){
        return ((MyApplication)getApplicationContext()).isOnline();
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
