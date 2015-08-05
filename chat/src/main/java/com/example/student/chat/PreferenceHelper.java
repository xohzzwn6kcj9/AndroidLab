package com.example.student.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by student on 2015-08-05.
 */
public class PreferenceHelper {

    public static String KEY_LOGIN_ID;
    public static String KEY_NICKNAME;
    public static String KEY_SERVER_IP;
    public static String KEY_SERVER_PORT;
    public static String KEY_SERVER_HTTP_PORT;

    private static final String DEFAULT_EMPTY_STRING = "";

    static final void init(Context context){
        KEY_LOGIN_ID = context.getResources().getString(R.string.preference_key_login_id);
        KEY_NICKNAME = context.getResources().getString(R.string.preference_key_nickname);
        KEY_SERVER_IP = context.getResources().getString(R.string.preference_key_server_ip);
        KEY_SERVER_PORT = context.getResources().getString(R.string.preference_key_server_port);
        KEY_SERVER_HTTP_PORT = context.getResources().getString(R.string.preference_key_server_http_port);
    }

    public static PreferenceHelper getInstance(Context context){
        return new PreferenceHelper(context);
    }

    private final Context context;
    private final SharedPreferences prefs;

    private PreferenceHelper(Context context){
        this.context = context;
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public final String getString(String key){
        return this.prefs.getString(key, DEFAULT_EMPTY_STRING);
    }
    public final String getLoginId(){
        return getString(KEY_LOGIN_ID);
    }
    public final String getServerIp(){
        return getString(KEY_SERVER_IP);
    }
    public final String getServerPort(){
        return getString(KEY_SERVER_PORT);
    }
    public final String getServerHttpPort(){
        return getString(KEY_SERVER_HTTP_PORT);
    }

}
