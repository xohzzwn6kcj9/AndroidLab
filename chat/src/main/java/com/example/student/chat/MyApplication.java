package com.example.student.chat;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by student on 2015-08-05.
 */
public class MyApplication extends Application {

    boolean isOnline = true;
    public boolean isOnline() {
        updateNetworkStatus();
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public void updateNetworkStatus(){
        final ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        final NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        this.isOnline = wifi.isConnected() || mobile.isConnected();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PreferenceHelper.init(this);
    }
}
