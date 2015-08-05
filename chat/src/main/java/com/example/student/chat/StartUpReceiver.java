package com.example.student.chat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartUpReceiver extends BroadcastReceiver {
    public StartUpReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //부팅 완료 시점에 시스템에서 띄우는 intent에 의해 실행..

        //service 구동..
        Intent sIntent=new Intent(context, ChattingService.class);
        context.startService(sIntent);

        //한번 start된 서비스가 유저, system에 의해 종료된다..
        //주기적으로 servicr 구동 상태 파악해서.. 다시 살리는 로직..
        //AlarmService 이용해서 체크..
        //==>지정된 시간에.. 혹은 주기적인 시간에.. 우리가 원하는 무언가가
        //실행되게..
        Intent aIntent=new Intent(context, RestartReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(
                context, 0, aIntent, 0);

        AlarmManager am=(AlarmManager)context.getSystemService(
                Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(), 6000, pendingIntent);
    }
}
