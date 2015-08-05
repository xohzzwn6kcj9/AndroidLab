package com.example.student.chat;

import android.app.Application;

/**
 * Created by student on 2015-08-05.
 */
public class MyApplication extends Application{
    boolean isOnline=true;

    public boolean isOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }
}
