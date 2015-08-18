package com.cuttingchai.assignment.utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.DateFormat;

public class IndexDetailsDataSource {

  // Database fields
  private SQLiteDatabase database;
  private MySQLiteHelper dbHelper;
  private String[] allColumns = { MySQLiteHelper.COLUMN_PATH,
      MySQLiteHelper.COLUMN_DATE };

  public IndexDetailsDataSource(Context context) {
    dbHelper = new MySQLiteHelper(context);
  }

  public void open() throws SQLException {
    database = dbHelper.getWritableDatabase();
  }

  public void close() {
    dbHelper.close();
  }

  public void createComment(String path, Long date) {
	  
	
    ContentValues values = new ContentValues();
    values.put(MySQLiteHelper.COLUMN_PATH, path);
    values.put(MySQLiteHelper.COLUMN_DATE, date);
    
    
    try
    {
    long insertId = database.insert(MySQLiteHelper.CC_REGISTER, null,
        values);
    Cursor cursor = database.query(MySQLiteHelper.CC_REGISTER,
        allColumns, MySQLiteHelper.COLUMN_PATH + " = " + insertId, null,
        null, null, null);

    if( cursor != null)
    {
    cursor.moveToFirst();
    cursorToComment(cursor);
    cursor.close();
    }
    
  
   
    //UserDetails newComment = 
   
   
   
    }
    
    catch (SQLiteConstraintException e)
    {
    	e.printStackTrace();
    }
  
  }

  public void deleteComment(String path) {
    //long id = comment.getId();
    System.out.println("Path deleted: " + path);
    database.delete(MySQLiteHelper.CC_REGISTER, MySQLiteHelper.COLUMN_PATH
        + " = " + path, null);
  }
  


  public List<IndexDetails> getAllComments() {
    List<IndexDetails> comments = new ArrayList<IndexDetails>();

    Cursor cursor = database.query(MySQLiteHelper.CC_REGISTER,
        allColumns, null, null, null, null, null);
  
    
   
    cursor.moveToFirst();
    
    
    while (!cursor.isAfterLast()) {
    	IndexDetails comment = cursorToGet(cursor);
      comments.add(comment);
      cursor.moveToNext();
    }
    // Make sure to close the cursor
    cursor.close();
    return comments;
  }
  
  
  public List<IndexDetails> getByDate(Long date) {
	  List<IndexDetails> comments = new ArrayList<IndexDetails>();
	  
	
	  Cursor cursor = database.query(MySQLiteHelper.CC_REGISTER,
	        allColumns, MySQLiteHelper.COLUMN_DATE + " = " + date , null, null, null, null);
	  
	    //Cursor cursor = database.rawQuery("SELECT * FROM "+MySQLiteHelper.CC_REGISTER +" WHERE "+ MYSQLiteHelper.='"+ id.toString()+"'", null);

	    
	    System.out.println("Date parameter in sqlite function: "+date);
	    System.out.println("Sql query: "+cursor);
	   
	    cursor.moveToFirst();
	    
	    
	    while (!cursor.isAfterLast()) {
	    	IndexDetails comment = cursorToGet(cursor);
	      comments.add(comment);
	      cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close(); 
	  
	  return comments;
	    
  }

  public List<IndexDetails> getByDateAndFolder(Long date, String folder) {
	  List<IndexDetails> comments = new ArrayList<IndexDetails>();
	  
	
	  Cursor cursor = database.query(MySQLiteHelper.CC_REGISTER,
	        allColumns, MySQLiteHelper.COLUMN_DATE + " = " + date + " AND " + MySQLiteHelper.COLUMN_PATH + " LIKE '%"+folder+"%'" , null, null, null, null);
	  
	    //Cursor cursor = database.rawQuery("SELECT * FROM "+MySQLiteHelper.CC_REGISTER +" WHERE "+ MYSQLiteHelper.='"+ id.toString()+"'", null);

	    
	    System.out.println("Date parameter in sqlite function: "+date);
	    System.out.println("Sql query: "+cursor);
	   
	    cursor.moveToFirst();
	    
	    
	    while (!cursor.isAfterLast()) {
	    	IndexDetails comment = cursorToGet(cursor);
	      comments.add(comment);
	      cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close(); 
	  
	  return comments;
	    
  }
  
  private IndexDetails cursorToComment(Cursor cursor) {
	  IndexDetails comment = new IndexDetails();
	if (cursor.moveToFirst())
	{
    comment.setPath(cursor.getString(0));
    comment.setDate(cursor.getString(1));
    
	}
    return comment;
  }


private IndexDetails cursorToGet(Cursor cursor) {
	IndexDetails comment = new IndexDetails();
	
   comment.setPath(cursor.getString(0));
   comment.setDate(cursor.getString(1));
  
	
   return comment;
 }


} 
