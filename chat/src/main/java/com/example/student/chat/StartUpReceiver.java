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
        Intent sIntent = new Intent(context, ChattingService.class);
        context.startService(sIntent);

        Intent aIntent = new Intent(context, RestartReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, aIntent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(), 6000, pendingIntent);
    }
}
