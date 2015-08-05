package com.example.student.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionChangeReceiver extends BroadcastReceiver {
    public ConnectionChangeReceiver() {
    }

    //IntroActivity에서 network 가능 상태 파악하고 있지만..
    //network 가능해서 업무 진행하는 도중에 얼마든지.. network불가능 상태가
    //될수도..
    //==>폰의 network 상태가 바뀌는 순간을 이벤트 모델로.. 받아서.. 판단..
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager=(ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile=manager.getNetworkInfo(
                ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi=manager.getNetworkInfo(
                ConnectivityManager.TYPE_WIFI);

        MyApplication app=(MyApplication)context.getApplicationContext();
        if(wifi.isConnected()){
            app.setIsOnline(true);
        }else if(mobile.isConnected()){
            app.setIsOnline(true);
        }else {
            app.setIsOnline(false);
        }
    }
}
