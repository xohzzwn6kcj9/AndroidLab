package com.example.student.chat;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class RestartReceiver extends BroadcastReceiver {
    public RestartReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!isMyService(context)){
            context.startService(new Intent(context, ChattingService.class));
        }
    }
    private boolean isMyService(Context context){
        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
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
}
