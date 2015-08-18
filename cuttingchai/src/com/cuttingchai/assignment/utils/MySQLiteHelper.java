package com.cuttingchai.assignment.utils;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

  public static final String CC_REGISTER = "ccindex";
  public static final String COLUMN_PATH = "path";
  public static final String COLUMN_DATE = "date";
  

  private static final String DATABASE_NAME = "ccindex.db";
  private static final int DATABASE_VERSION = 1;

  
//Database creation sql statement
 private static final String DATABASE_CREATE = "create table "
     + CC_REGISTER + "(" + COLUMN_PATH
     + " text primary key, " + COLUMN_DATE
     + " integer not null);";

  public MySQLiteHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(DATABASE_CREATE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(MySQLiteHelper.class.getName(),
        "Upgrading database from version " + oldVersion + " to "
            + newVersion + ", which will destroy all old data");
    db.execSQL("DROP TABLE IF EXISTS " + CC_REGISTER);
    onCreate(db);
  }

} 