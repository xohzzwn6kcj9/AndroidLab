package com.example.student.lbs;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    private static final String DATABASE_NAME = "locationdb";
    private static final int DATABASE_VERSION = 6;
    private String SQL_TABLE_CREATE;
    private String TABLE_NAME;

    public static final String SQL_CREATE_SMS = "create table sms (_id integer primary key autoincrement,"
            + "phone_number text,"
            + "name text,"
            + "ischeck integer default 1)";//0 - 체크 안됨, 1- 체크됨

    public static final String SQL_CREATE_FENCE =" create table fence(_id integer primary key autoincrement,"
            +"fence_name text not null,"
            +"fence_latitude real not null,"
            +"fence_longitude real not null,"
            +"fence_radius integer," +//km 단위
            "fence_address text not null)";

    public static final String SQL_CREATE_LOG =	"create table location_log(_id integer primary key autoincrement,"
            +"date date not null,"
            +"fence_name text not null,"
            +"inout integer not null,"//0-in, 1-out
            +"lat real not null,"
            +"lon real not null)";



    private final Context mCtx;

    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("kkang","onCreate..................");
            db.execSQL(SQL_CREATE_SMS);
            db.execSQL(SQL_CREATE_FENCE);
            db.execSQL(SQL_CREATE_LOG);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS sms");
            db.execSQL("DROP TABLE IF EXISTS fence");
            db.execSQL("DROP TABLE IF EXISTS location_log");
            onCreate(db);
        }
    }

    public DBAdapter(Context ctx, String sql, String tableName) {
        this.mCtx = ctx;
        SQL_TABLE_CREATE = sql;
        TABLE_NAME = tableName;
    }

    public DBAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public long insertTable(ContentValues initialValues) {
        return mDb.insert(TABLE_NAME, null, initialValues);
    }

    public boolean deleteTable(String pkColumn, long pkData) {
        return mDb.delete(TABLE_NAME, pkColumn + "=" + pkData, null) > 0;
    }

    public Cursor selectTable(String[] columns, String selection, String[] selectionArgs, String groupBy, String having,
                              String orderBy) {
        return mDb.query(TABLE_NAME, columns, selection, selectionArgs,
                groupBy, having, orderBy);
    }

    public boolean updateTable(ContentValues args, String pkColumn, long pkData) {
        return mDb.update(TABLE_NAME, args, pkColumn + "=" + pkData, null) > 0;
    }

}
