package com.lazysleep.util;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

public class DbManager extends Application {
    private SQLiteDatabase db;
    private String DbPath;

    public void setDbPath(String dbPath) {
        this.DbPath = dbPath;
    }

    public SQLiteDatabase getSQLiteDatabase() {
        if (db == null) {
            db = SQLiteDatabase.openDatabase(DbPath, null, SQLiteDatabase.OPEN_READWRITE);
        }
        return db;
    }

}
