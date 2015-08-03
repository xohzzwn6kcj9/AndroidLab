package com.example.student.sms;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by student on 2015-08-03.
 */
public final class SMSUtil {
    private SMSUtil(){}
    public static String dateFormat(long millis){
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(millis));
    }
    public static SMSMessage sendSMS(Context context, String address, String body, String mode){
        
    }
}
