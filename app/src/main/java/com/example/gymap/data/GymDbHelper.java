package com.example.gymap.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteOpenHelper;


public class GymDbHelper extends SQLiteOpenHelper {
    // Database Version
    public static final int DATABASE_VERSION = 1;

    //Name of the database file
    public static final String DATABASE_NAME = "gym.db";

    public GymDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    };

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create a new table
        String SQL_CREATE_GYM_TABLE =
                "CREATE TABLE " + GymContract.GymEntry.TABLE_NAME + " (" +
                        GymContract.GymEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        GymContract.GymEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                        GymContract.GymEntry.COLUMN_AGE + " INTEGER, " +
                        GymContract.GymEntry.COLUMN_GENDER + " INTEGER NOT NULL, " +
                        GymContract.GymEntry.COLUMN_WEIGHT + " INTEGER NOT NULL );";

        db.execSQL(SQL_CREATE_GYM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
