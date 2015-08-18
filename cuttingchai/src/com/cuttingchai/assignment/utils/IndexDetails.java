package com.cuttingchai.assignment.utils;

import java.io.File;

public class IndexDetails {
  private String path;
  private String date;
 

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }
  

  // Will be used by the ArrayAdapter in the ListView
  @Override
  public String toString() {
	
	String val=String.valueOf(path)+" "+date;
    return val;
    
  }
} 