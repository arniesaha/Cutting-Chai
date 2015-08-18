package com.cuttingchai.assignment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.audiocompass.assignment.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AndroidExplorer extends ListActivity {
	
	static final int ID_JPGDIALOG = 0;
	
	String filename;
	String exifAttribute;
	TextView exifText;
	ImageView bmImage;
	BitmapFactory.Options bmOptions; 
	File jpgFile;
	Dialog jpgDialog;
	
	private List<String> item = null;
	private List<String> path = null;
	private String root="/";
	private TextView myPath;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);
        myPath = (TextView)findViewById(R.id.path);
        getDir(root);
    }
    
    private void getDir(String dirPath)
    {
    	myPath.setText("Location: " + dirPath);
    	
    	item = new ArrayList<String>();
    	path = new ArrayList<String>();
    	
    	File f = new File(dirPath);
    	File[] files = f.listFiles();
    	
    	if(!dirPath.equals(root))
    	{

    		item.add(root);
    		path.add(root);
    		
    		item.add("../");
    		path.add(f.getParent());
            
    	}
    	
    	for(int i=0; i < files.length; i++)
    	{
    			File file = files[i];
    			path.add(file.getPath());
    			if(file.isDirectory())
    				item.add(file.getName() + "/");
    			else
    				item.add(file.getName());
    	}

    	ArrayAdapter<String> fileList =
    		new ArrayAdapter<String>(this, R.layout.row, item);
    	setListAdapter(fileList);
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		File file = new File(path.get(position));
		
		if (file.isDirectory())
		{
			if(file.canRead())
				getDir(path.get(position));
			else
			{
				new AlertDialog.Builder(this)
				.setIcon(R.drawable.rate)
				.setTitle("[" + file.getName() + "] folder can't be read!")
				.setPositiveButton("OK", 
						new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
							}
						}).show();
			}
		}
		else
		{
			exifAttribute = null;
			filename = file.getName();
			String ext = filename.substring(filename.lastIndexOf('.')+1, filename.length());

			if(ext.equals("JPG")||ext.equals("jpg") || ext.equals("PNG")||ext.equals("png")  || ext.equals("JPEG")||ext.equals("jpeg"))
			{
				try {
					ExifInterface exif = new ExifInterface(file.toString());
					exifAttribute = getExif(exif);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					;
				}
				jpgFile = file;
				showDialog(ID_JPGDIALOG);
			}
			else{
				new AlertDialog.Builder(this)
			    .setIcon(R.drawable.rate)
			    .setTitle("[" + filename + "]")
			    .setMessage(exifAttribute)
			    .setPositiveButton("OK", 
			      new DialogInterface.OnClickListener() {
			       
			       @Override
			       public void onClick(DialogInterface dialog, int which) {
			        // TODO Auto-generated method stub
			       }
			      }).show();
			}
		}
	}
	
    private String getExif(ExifInterface exif)
    {
     String myAttribute=null;
     myAttribute += getTagString(ExifInterface.TAG_DATETIME, exif);
     myAttribute += getTagString(ExifInterface.TAG_FLASH, exif);
     myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE, exif);
     myAttribute += getTagString(ExifInterface.TAG_GPS_LATITUDE_REF, exif);
     myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE, exif);
     myAttribute += getTagString(ExifInterface.TAG_GPS_LONGITUDE_REF, exif);
     myAttribute += getTagString(ExifInterface.TAG_IMAGE_LENGTH, exif);
     myAttribute += getTagString(ExifInterface.TAG_IMAGE_WIDTH, exif);
     myAttribute += getTagString(ExifInterface.TAG_MAKE, exif);
     myAttribute += getTagString(ExifInterface.TAG_MODEL, exif);
     myAttribute += getTagString(ExifInterface.TAG_ORIENTATION, exif);
     myAttribute += getTagString(ExifInterface.TAG_WHITE_BALANCE, exif);
     return myAttribute;
    }
    
    private String getTagString(String tag, ExifInterface exif)
    {
     return(tag + " : " + exif.getAttribute(tag) + "\n");
    }

	@Override
	protected Dialog onCreateDialog(int id) {
		jpgDialog = null;;
		switch(id){
		case ID_JPGDIALOG:

			Context mContext = this;
			jpgDialog = new Dialog(mContext);

			jpgDialog.setContentView(R.layout.jpgdialog);
			exifText = (TextView) jpgDialog.findViewById(R.id.text);
			bmImage = (ImageView)jpgDialog.findViewById(R.id.image);
			bmOptions = new BitmapFactory.Options();
			bmOptions.inSampleSize = 2;
			
			Button okDialogButton = (Button)jpgDialog.findViewById(R.id.okdialogbutton);
			okDialogButton.setOnClickListener(okDialogButtonOnClickListener);
			
			break;
		default:
			break;
		}
		return jpgDialog;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		// TODO Auto-generated method stub

		switch(id){
		case ID_JPGDIALOG:
			dialog.setTitle("[" + filename + "]");
			exifText.setText(exifAttribute);
			Bitmap bm = BitmapFactory.decodeFile(jpgFile.getPath(), bmOptions);
			bmImage.setImageBitmap(bm); 
			
			break;
		default:
			break;
		}
	}
    
	private Button.OnClickListener okDialogButtonOnClickListener
		= new Button.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				jpgDialog.dismiss();
			}
	};
}