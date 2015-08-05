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
        //Alaram에 의해 주기적으로 실행되면서.. service 구동 상태 파악..
        boolean isMyService=false;
        ActivityManager am=(ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
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
            Intent intent1=new Intent(context,
                    ChattingService.class);
            context.startService(intent1);
        }
    }
}
