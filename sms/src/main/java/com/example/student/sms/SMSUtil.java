package com.example.student.sms;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by student on 2015-08-03.
 */
public final class SMSUtil {
    private static final String TAG = "SMSUtil";
    private SMSUtil(){}
    public static String dateFormat(long millis){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(millis));
    }
    public static SMSMessage sendSMS(Context context, String address, String body, String mode){
        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        final String myNumber = telephonyManager.getLine1Number();
        Log.d(TAG, "myNumber="+myNumber);
        final SmsManager sms = SmsManager.getDefault();
        if(mode.equals("write")){
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            sms.sendTextMessage(address, myNumber, body, pendingIntent, null);
        }
        else{//read
            sms.sendTextMessage(address, myNumber, body, null, null);
        }
        final SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        final long date = System.currentTimeMillis();
        final ContentValues values = new ContentValues();
        values.put(SmsTable.PHONE_NUMBER, address);
        values.put(SmsTable.DATE, date);
        values.put(SmsTable.STATE, 2);
        values.put(SmsTable.CONTENT, body);
        final long id = db.insert(SmsTable.TABLE_NAME, null, values);

        final SMSMessage message = new SMSMessage();
        message.id = id;
        message.date = dateFormat(date);
        message.phoneNumber = address;
        message.state = 2;
        message.content = body;
        return message;
    }
}
