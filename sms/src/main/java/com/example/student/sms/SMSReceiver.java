package com.example.student.sms;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SMSReceiver extends BroadcastReceiver {

    private static final String TAG = "SMSReceiver";
    public SMSReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"Sms Received");
        final Bundle bundle = intent.getExtras();
        String smsBody = "";
        String smsAddress = "";
        if(bundle != null){
            final Object[] pdus = (Object[]) bundle.get("pdus");
            final SmsMessage[] messages = new SmsMessage[pdus.length];
            for(int i=0; i<pdus.length; i++){
                messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                try{
                    smsBody = new String(messages[i].getMessageBody());
                    smsAddress = messages[i].getOriginatingAddress();

                    final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                    final List<ActivityManager.RunningTaskInfo> rti = am.getRunningTasks(1);

                    boolean isNotify = false;
                    String mode = "read";
                    if(rti != null && rti.size()>0){
                        ComponentName topActivity = rti.get(0).topActivity;
                        if(topActivity.getClassName().equals("com.example.student.sms.SMSReadActivity")){

                            MyApplication app = (MyApplication) context.getApplicationContext();
                            String displayPhoneNumber = app.displayPhoneNumber;
                            if(displayPhoneNumber != null && displayPhoneNumber.equals(smsAddress)){
                                isNotify = true;
                            }

                        }
                    }

                    final SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
                    final long date = System.currentTimeMillis();
                    final ContentValues values = new ContentValues();
                    values.put(SmsTable.PHONE_NUMBER, smsAddress);
                    values.put(SmsTable.DATE, date);
                    values.put(SmsTable.CONTENT, smsBody);
                    if(!isNotify){
                        values.put(SmsTable.STATE, "1");//Received.
                    }

                    final long _id = db.insert(SmsTable.TABLE_NAME, null, values);
                    Log.d(TAG, "A message inserted to database with id = " + _id);
                    final Cursor cursor = db.query(SmsTable.TABLE_NAME,
                            new String[]{SmsTable.PHONE_NUMBER},
                            //SmsTable.STATE + " = '0'",
                            null,
                            null,
                            null,
                            null,
                            null);
                    final int size = cursor.getCount();
                    Log.d(TAG, size+" messages selected. ");
                    final ArrayList<String> phones = new ArrayList<>();
                    if(size > 1){
                        while(cursor.moveToNext()){
                            phones.add(cursor.getString(0));
                        }
                    }
                    db.close();
                    if(isNotify){
                        final NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                        builder.setSmallIcon(R.mipmap.ic_launcher);
                        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.sms));
                        builder.setTicker(smsAddress + " " + smsBody);
                        builder.setWhen(System.currentTimeMillis());
                        builder.setContentTitle("Unread messages");
                        builder.setContentText(smsAddress + " " + smsBody);

                        final Intent newIntent= new Intent(context, SMSReadActivity.class);
                        newIntent.putExtra("phoneNumber", smsAddress);
                        newIntent.putExtra("mode", mode);
                        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(pendingIntent);

                        if(size>1){
                            builder.setNumber(size);
                        }

                        if(size>1){
                            if(Build.VERSION.SDK_INT > 15){ //inbox style
                                NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle(builder);
                                if(size>3){
                                    for(int j=0; j<3; j++){
                                        style.addLine(phones.get(j));
                                    }
                                    style.setSummaryText((size-3)+" more messages");
                                }
                                else{
                                    for(String phone: phones){
                                        style.addLine(phone);
                                    }
                                }
                                builder.setStyle(style);
                            }
                            else{
                                builder.setContentText(size + " more message");
                            }
                        }
                        builder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
                        builder.setAutoCancel(true);
                        builder.setOngoing(false);
                        nm.notify(0, builder.build());
                    }
                    else{ //isNotify == false
                        final Intent rIntent = new Intent("com.multi.ACTION_READ_ACTIVITY");
                        rIntent.putExtra(SmsTable.ID, _id);
                        rIntent.putExtra(SmsTable.DATE, date);
                        rIntent.putExtra(SmsTable.PHONE_NUMBER, smsAddress);
                        rIntent.putExtra(SmsTable.STATE, "1");
                        rIntent.putExtra(SmsTable.CONTENT, smsBody);
                        context.sendBroadcast(rIntent);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
