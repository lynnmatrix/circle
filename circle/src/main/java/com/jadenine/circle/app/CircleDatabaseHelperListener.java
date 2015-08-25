package com.jadenine.circle.app;

import android.database.sqlite.SQLiteDatabase;

import com.raizlabs.android.dbflow.DatabaseHelperListener;

/**
 * Created by linym on 8/25/15.
 */
class CircleDatabaseHelperListener implements DatabaseHelperListener {
    @Override
    public void onOpen(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        int currentVersion = oldVersion;
        if (1 == currentVersion) {
            sqLiteDatabase.execSQL("drop table if exists Bomb;");
            sqLiteDatabase.execSQL("drop table if exists DirectMessageEntity;");
            currentVersion = 2;
        }
        if (2 == currentVersion) {
            sqLiteDatabase.execSQL("drop table if exists Bomb ;");
            sqLiteDatabase.execSQL("drop table if exists Timeline;");
            sqLiteDatabase.execSQL("drop table if exists TimelineRangeCursor;");
            sqLiteDatabase.execSQL("drop table if exists DirectMessageEntity;");
            sqLiteDatabase.execSQL("drop table if exists UserApEntity;");
            currentVersion = 3;
        }
    }
}
