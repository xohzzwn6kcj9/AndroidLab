package com.example.student.sms;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by student on 2015-08-03.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String TAG = "DbHelper";

    public static final int CURRENT_VERSION = 1;
    public static final String DB_NAME = "smsdb";

    private static DbHelper instance;
    public static DbHelper getInstance(Context context){
        if(instance == null){
            instance = new DbHelper(context, DB_NAME, null, CURRENT_VERSION);
        }
        return instance;
    }

    private DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "creating database");
        final String tableSql = "create table "+SmsTable.TABLE_NAME + " ("
                + SmsTable.ID + " integer primary key autoincrement, "
                + SmsTable.PHONE_NUMBER + " text, "
                + SmsTable.DATE + " integer, "
                + SmsTable.STATE + " integer default 0, " //
                + SmsTable.CONTENT + " text)";
        db.execSQL(tableSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table sms");
        onCreate(db);
    }
}
