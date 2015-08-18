package com.cuttingchai.assignment;


import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.util.Log;
import android.widget.Toast;

import com.audiocompass.assignment.R;
import com.cuttingchai.assignment.utils.PhotoUtils;
import com.cuttingchai.assignment.utils.IndexDetailsDataSource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MainActivity extends BaseActivity implements ActionBar.TabListener{

	public ActionBar mActionBar;
	
	public static final int NUM_TABS =2;
	public static final int RECENT_POSITION = 0;
	public static final int CATEGORY_POSITION = 1;
	
	public static final String RECENT = "All Photos";
	public static final String CATEGORY = "Search";
	
	public static final String TAGNAME_IMAGE_GRID = "Image Grid";
	private Fragment[] mFragments = new Fragment[NUM_TABS];
	private IndexDetailsDataSource datasource;
	
	SharedPreferences prefs;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		prefs = getApplicationContext().getSharedPreferences("MyPref", 1); 
		if(prefs.getBoolean("indexStatus", false) == false)
		{
			datasource = new IndexDetailsDataSource(this);
		    datasource.open();
		    
		    ArrayList<String> imagePaths = getFilePaths();
	    	
	    	Log.d("Image File Count ",String.valueOf(imagePaths.size()));
			new MakeIndex().execute(imagePaths);
			
		}
		
		
		
		//Set up Fragment Holder
		setContentView(R.layout.activity_main);
		mActionBar = getSupportActionBar();
		//Set Actionbar as Tab navigation
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		// Add Tabs
		mActionBar.addTab(mActionBar.newTab().setText(RECENT).setTabListener(this));
		//mActionBar.addTab(mActionBar.newTab().setText(POPULAR).setTabListener(this));
		mActionBar.addTab(mActionBar.newTab().setText(CATEGORY).setTabListener(this));
		

	} 
	
	public ArrayList<String> getFilePaths()
    {


        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI; 
        String[] projection = {MediaStore.Images.ImageColumns.DATA}; 
        Cursor c = null;
        SortedSet<String> dirList = new TreeSet<String>();
        ArrayList<String> resultIAV = new ArrayList<String>();

         String[] directories = null; 
        if (u != null) 
        { 
            c = managedQuery(u, projection, null, null, null); 
        } 

        if ((c != null) && (c.moveToFirst())) 
        { 
            do 
            {
                String tempDir = c.getString(0);
                tempDir = tempDir.substring(0, tempDir.lastIndexOf("/"));
                try{
                    dirList.add(tempDir);
                }
                catch(Exception e)
                {

                }
            } 
            while (c.moveToNext());
            directories = new String[dirList.size()];
            dirList.toArray(directories);

        }

        for(int i=0;i<dirList.size();i++)
        {
            File imageDir = new File(directories[i]);
            File[] imageList = imageDir.listFiles();
            if(imageList == null)
                continue;
            for (File imagePath : imageList) { 
                try {

                        if(imagePath.isDirectory())
                        {
                            imageList = imagePath.listFiles();

                        }
                        if ( imagePath.getName().contains(".jpg")|| imagePath.getName().contains(".JPG")  
                                || imagePath.getName().contains(".jpeg")|| imagePath.getName().contains(".JPEG")                                    
                                || imagePath.getName().contains(".png") || imagePath.getName().contains(".PNG")
                                || imagePath.getName().contains(".gif") || imagePath.getName().contains(".GIF")
                                || imagePath.getName().contains(".bmp") || imagePath.getName().contains(".BMP")                         
        )
                        {



                            String path= imagePath.getAbsolutePath();
                        resultIAV.add(path);

                        }
                    }
            //  }
            catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return resultIAV;


    }
	
	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		int position = tab.getPosition();
		Fragment af = null;
		switch(position){
		case RECENT_POSITION:
			if(mFragments[position] == null){
				mFragments[position] = new ImageGridFragment();
				Bundle args = new Bundle();
				args.putString(ImageGridFragment.URL_KEY, PhotoUtils.AC_URL);
				mFragments[position].setArguments(args);
				ft.add(R.id.fragment_container, mFragments[position], RECENT);
			}
			af = mFragments[position];
			break;
		
			
		case CATEGORY_POSITION:
			if(mFragments[position] == null){
				Bundle args = new Bundle();
				mFragments[position] = new SearchImageGridFragment();
				mFragments[position].setArguments(args);
				ft.add(R.id.fragment_container, mFragments[position], getText(R.string.search).toString());
			}
			af = mFragments[position];
			break;
		
			
			default:
				return;
		
		}
		ft.attach(af);
		
	}


	@Override
	public void onBackPressed() {
				
	          super.onBackPressed();   
	    
	}
	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	
		ft.detach(mFragments[tab.getPosition()]);
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// Hide or show search view on Search tabl
		
		
	}

	 public interface HandleSearchCloseListener{
			public abstract void handleClose();
		}
	 
	 
	 private class MakeIndex extends AsyncTask<ArrayList<String>, Void, String> {
			ProgressDialog asyncDialog = new ProgressDialog(MainActivity.this);
	        

			@Override
			protected String doInBackground(ArrayList<String>... arg0) {
				// TODO Auto-generated method stub
				
				for (String s : arg0[0]){
		    	    Log.d("Filepath : ", s);
		    	    File file = new File(s);
		    	    Date lastModDate = new Date(file.lastModified());
					System.out.println("image link: "+file+"size: "+file.length());
					
					if(file.length() >= 500000){
						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);  
						String formattedDateString = formatter.format(lastModDate);
						try {
							Date dbDate = formatter.parse(formattedDateString);							
							datasource.createComment(Uri.fromFile(file).toString(),dbDate.getTime());
							System.out.println("DB Date: "+dbDate.getTime());
						} catch (ParseException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
								
					
		    	    
		    	}
				return null;
			}
	        
	        
	        
	        @Override
	        protected void onPostExecute(String result) {
	        	//hide the dialog
	            asyncDialog.dismiss();
	           
	           Toast.makeText(getBaseContext(), "Indexing complete", Toast.LENGTH_SHORT).show();
	           try{
	        	   	 datasource.close();
	        	   //Adding default Fav folder path in App Preferences
	   			     Editor editor = prefs.edit();
	   			     editor.putBoolean("indexStatus", true);
	   			     editor.commit();
	           }
	           catch(Exception e){
	        	   e.printStackTrace();
	           }
	        }

	        @Override
	        protected void onPreExecute() {
	        	//set message of the dialog
	            asyncDialog.setMessage(getString(R.string.loadingtype));
	            //show dialog
	            asyncDialog.show();
	            super.onPreExecute();
	        }

	        @Override
	        protected void onProgressUpdate(Void... values) {}



			
	    }


}

