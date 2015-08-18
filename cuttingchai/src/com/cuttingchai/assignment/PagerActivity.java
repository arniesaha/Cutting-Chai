package com.cuttingchai.assignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.audiocompass.assignment.R;
import com.cuttingchai.assignment.utils.Photo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import eu.janmuller.android.simplecropimage.CropImage;



public class PagerActivity extends BaseActivity implements OnClickListener {
	public static final String PHOTOS = "photos";
	public static final String INIT_POSITION = "init_position";
	protected static final String TAG = PagerActivity.class.getSimpleName();
	private ViewPager mPager;
	private Button mWallpaper;
	private Button mEditSet;
	private Button mSave;
	private List<Photo> mPhotos = null;
	private int mCurrentPosition;
	public Context mContext;
	private ImagePagerAdapter mPagerAdapter;
	DisplayImageOptions options;
	File jpgFile;
	Dialog jpgDialog;
	String filename;
	String exifAttribute;
	TextView exifText;
	ImageView bmImage;
	BitmapFactory.Options bmOptions; 
	//This For Image Crop
    /**
     *  Uri for set image crop option .
     */
    private Uri mImageCaptureUri;
    private File      mFileTemp;
    
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    /**
     *  int for set key and get key from result activity . 
     */
    public final int CROP_FROM_CAMERA = 0;
    public static final int REQUEST_CODE_CROP_IMAGE   = 0x3;
    static final int ID_JPGDIALOG = 0;
    
    

    //This is Crop Method.

    /**
     * Method for apply Crop .
     * @param filePath -  String path of file .
     */
    private void doCrop(String filePath){
    	try{
        //New Flow
        mImageCaptureUri = Uri.fromFile(new File(filePath));
        
        // create explicit intent
        Intent intent = new Intent(this, CropImage.class);

        // tell CropImage activity to look for image to crop 
        
        intent.putExtra(CropImage.IMAGE_PATH, filePath);

        // allow CropImage activity to rescale image
        intent.putExtra(CropImage.SCALE, true);

        // if the aspect ratio is fixed to ratio 3/2
        intent.putExtra(CropImage.ASPECT_X, 3);
        intent.putExtra(CropImage.ASPECT_Y, 2);

        // start activity CropImage with certain request code and listen
        // for result
        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
        
        }
    	catch (Exception ex) 
    	{
    		Log.d("Error","Error in Crop Function-->"+ex.toString());
    	}
    }
    
   
	
	
	@Override
	protected void onCreate(Bundle arg) {
		super.onCreate(arg);
		
		setContentView(R.layout.activity_pager);
		if(!imageLoader.isInited())
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		mPhotos = getIntent().getParcelableArrayListExtra(PHOTOS);
		mCurrentPosition = getIntent().getIntExtra(INIT_POSITION, 0);
		mContext = getApplicationContext();
		mPager = (ViewPager)findViewById(R.id.pager);
		mWallpaper = (Button)findViewById(R.id.wallpaper);
		mEditSet = (Button)findViewById(R.id.edit_set);
		mSave = (Button)findViewById(R.id.save);
		
		mWallpaper.setOnClickListener(this);
		mEditSet.setOnClickListener(this);
		mSave.setOnClickListener(this);

		mPagerAdapter = new ImagePagerAdapter();
		mPager.setAdapter(mPagerAdapter);
		mPager.setCurrentItem(mCurrentPosition);
		
		
		findViewById(R.id.forward).setOnClickListener(this);
		findViewById(R.id.backward).setOnClickListener(this);
		
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
		.resetViewBeforeLoading(true)
		.cacheOnDisc(true)
		.imageScaleType(ImageScaleType.EXACTLY)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.considerExifParams(true)
		.displayer(new FadeInBitmapDisplayer(300))
		.build();
		
		
		
		
		
		String state = Environment.getExternalStorageState();
    	if (Environment.MEDIA_MOUNTED.equals(state)) {
    		mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
    	}
    	else {
    		mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
    	}


		
	}
@Override
protected void onSaveInstanceState(Bundle outState) {
	outState.putInt(INIT_POSITION, mCurrentPosition);
	
	super.onSaveInstanceState(outState);
}


/**
 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
 */

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) 
{
	  if (resultCode != RESULT_OK) {

	        return;
	    }  

	    switch (requestCode) {

	        case REQUEST_CODE_CROP_IMAGE:

	            String path = data.getStringExtra(CropImage.IMAGE_PATH);

	            // if nothing received
	            if (path == null) {

	                return;
	            }

	            // cropped bitmap
	            Bitmap bitmap = BitmapFactory.decodeFile(path);
	            
	            DisplayMetrics displayMetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
				int height = displayMetrics.heightPixels;
			    int width = displayMetrics.widthPixels << 1; // best wallpaper width is twice screen width
			    
			    WallpaperManager m=WallpaperManager.getInstance(getApplicationContext());
			    try {
			    	m.setBitmap(bitmap);
			    	m.suggestDesiredDimensions(width, height);
			    	Toast.makeText(getApplicationContext(), "Wallpaper Set Successfully", Toast.LENGTH_SHORT).show();
			    	
			    } catch (IOException e) {
			    	// TODO Auto-generated catch block
			    	e.printStackTrace();
			    }
  			  
	            
  			    
  			    
  			    
	            break;
	    }
	    super.onActivityResult(requestCode, resultCode, data);
    

}


	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		if(mPhotos.size() == 0)return;
		String url = mPhotos.get(mCurrentPosition).getmImageURL();
		switch(viewId){
		case R.id.save:
			imageLoader.loadImage(url, new SimpleImageLoadingListener(){
				@Override
				public void onLoadingComplete(String imageUri, View view,
						Bitmap loadedImage) {
					//String path = saveImage(loadedImage, false);
					if(imageUri != null) {
						//MediaStore.Images.Media.insertImage(getContentResolver(), path, null, null);
						//Toast.makeText(mContext, "Image Saved succesfully", Toast.LENGTH_SHORT).show();
						exifAttribute = null;
						File file = new File(Uri.parse(imageUri).getPath());
						filename = file.getName();
						
					
							try {
								ExifInterface exif = new ExifInterface(file.toString());
								exifAttribute = getExif(exif);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								
							}
							jpgFile = file;
						
							// custom dialog
							final Dialog jpgDialog = new Dialog(PagerActivity.this);
							jpgDialog.setContentView(R.layout.jpgdialog);
							jpgDialog.setTitle("EXIF Details");

							// set the custom dialog components - text, image and button
							
							
							exifText = (TextView) jpgDialog.findViewById(R.id.text);
							bmImage = (ImageView)jpgDialog.findViewById(R.id.image);
							bmOptions = new BitmapFactory.Options();
							bmOptions.inSampleSize = 2;
							exifText.setText("EXIF Details");
							bmImage.setImageResource(R.drawable.ic_launcher);
							
							jpgDialog.setTitle("[" + filename + "]");
							exifText.setText(exifAttribute);
							Bitmap bm = BitmapFactory.decodeFile(jpgFile.getPath(), bmOptions);
							bmImage.setImageBitmap(bm); 

							Button okDialogButton = (Button)jpgDialog.findViewById(R.id.okdialogbutton);
							
							// if button is clicked, close the custom dialog
							okDialogButton.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									jpgDialog.dismiss();
								}
							});

							jpgDialog.show();
						  
						
						}
					
					super.onLoadingComplete(imageUri, view, loadedImage);
					
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
			});
			break;
			
		
			
		case R.id.wallpaper:
			imageLoader.loadImage(url, new SimpleImageLoadingListener(){
				@Override
				public void onLoadingComplete(String imageUri, View view,
						Bitmap loadedImage) {
					
					
					DisplayMetrics displayMetrics = new DisplayMetrics();
					getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
					int height = displayMetrics.heightPixels;
					int width = displayMetrics.widthPixels << 1; // best wallpaper width is twice screen width

					WallpaperManager wallpaperManager = WallpaperManager.getInstance(mContext);
					
					
					try {
						wallpaperManager.setBitmap(loadedImage);
						wallpaperManager.suggestDesiredDimensions(width, height);

						Toast.makeText(mContext, "Wallpaper Set Successfully" , Toast.LENGTH_LONG).show();
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					super.onLoadingComplete(imageUri, view, loadedImage);
				}
			});
			break;
		case R.id.edit_set:
			imageLoader.loadImage(url, new SimpleImageLoadingListener(){
				@SuppressLint("NewApi") @Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) 
				
				{
					
					String path = saveImage(loadedImage, true);
					doCrop(path);
					if(path == null)return;
					super.onLoadingComplete(imageUri, view, loadedImage);
				}
			});
			break;
				
		
			
		case R.id.forward:
			mPager.setCurrentItem(mCurrentPosition+1);
			break;
			
		case R.id.backward:
			mPager.setCurrentItem(mCurrentPosition-1);
			break;
		}
		
	}

	public void sendSMS(String url){
		Intent i = new Intent(android.content.Intent.ACTION_SENDTO);


		i.putExtra("sms_body", "Awesome image\n"+ url);

		i.setType("vnd.android-dir/mms-sms");
		 i.setData(Uri.parse("sms:"));
		startActivity(Intent.createChooser(i, "Send link via SMS"));
		
	}
	
	public void frieShareIntent(String path ){
		Intent imageShareIntent = new Intent(Intent.ACTION_SEND);
		imageShareIntent.setType("image/*");
		imageShareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+path));
		startActivity(Intent.createChooser(imageShareIntent, "Sharing Photo"));
		
	}
	public void frieShareIntent(String path, String appName){
		Intent imageShareIntent = new Intent(Intent.ACTION_SEND);
		imageShareIntent.setType("image/*");
		imageShareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+path));
		List<ResolveInfo> infos = getPackageManager().queryIntentActivities(imageShareIntent, PackageManager.MATCH_DEFAULT_ONLY);
		
		Log.d(TAG, "Apps #:" + infos.size());
		Intent fireIntent = null;
		
		for (ResolveInfo info : infos) {
			if(info.activityInfo.packageName.contains(appName)){
				fireIntent = new Intent(Intent.ACTION_SEND);
				fireIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+path));
				fireIntent.setType("image/*");
				Log.d(TAG, info.activityInfo.packageName);
				fireIntent.setPackage(info.activityInfo.packageName);
		        break;
			}
		}
		String app = "";
		if(appName.equals("facebook"))
			app = "Facebook";
		else if(appName.equals("twitter"))
			app = "Twitter";
		else if(appName.equals("plus"))
			app = "Google +";
		else if(appName.equals("insta"))
			app = "Instagram";
		else if(appName.equals("pinterest"))
			app = "Pinterest";
		
			
		if(fireIntent == null)Toast.makeText(mContext, "No " + app +" App found" , Toast.LENGTH_LONG).show();
		else 
			startActivity(Intent.createChooser(fireIntent, "Sharing Photo"));
		
	}
	private class ImagePagerAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return mPhotos.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return obj.equals(view);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager)container).removeView((View)object);
		}

		@Override
		public Object instantiateItem(ViewGroup view, int position) {
			View imageLayout = getLayoutInflater().inflate(R.layout.item_pager_image, view, false);
			ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);
			
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);

			imageLoader.displayImage(mPhotos.get(position).getmImageURL(), imageView, options, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					spinner.setVisibility(View.VISIBLE);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					String message = null;
					switch (failReason.getType()) {
						case IO_ERROR:
							message = "Input/Output error";
							break;
						case DECODING_ERROR:
							message = "Image can't be decoded";
							break;
						case NETWORK_DENIED:
							message = "Downloads are denied";
							break;
						case OUT_OF_MEMORY:
							message = "Out Of Memory error";
							break;
						case UNKNOWN:
							message = "Unknown error";
							break;
					}
					Log.d(TAG, message);
					
				
					spinner.setVisibility(View.GONE);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					spinner.setVisibility(View.GONE);
				}
			});

			view.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			super.setPrimaryItem(container, position, object);
			mCurrentPosition = position;
		}
		
	}

	private String saveImage(Bitmap bitmap, boolean cache){
		
		boolean failure = true;
		File file = null;
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			if(cache)
				file = getExternalCacheDir();
			else
				file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		if(!file.exists())file.mkdirs();
		}
		else Toast.makeText(mContext, "SD card is not mounted", Toast.LENGTH_LONG).show();
		
		if(file != null){
			String name = "IMG_" + System.currentTimeMillis() + ".png";
			file = new File(file, name);
				try {
					file.createNewFile();
					FileOutputStream out = new FileOutputStream(file.getAbsolutePath());
					bitmap.compress(CompressFormat.PNG, 100, out);
					out.flush();
					out.close();
					out = null;
					failure = false;
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			
		}
		if(failure){
			Log.d(TAG, "Failed to create File");
			return null;
			}
		
		return file.getAbsolutePath();
	}
   
	private String favImage(Bitmap bitmap, boolean cache){
		
		boolean failure = true;
		 File file = null;
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			if(cache)
				file = getExternalCacheDir();
			else
			{
				file = new File (Environment.getExternalStorageDirectory() + "/BestDogsWallpaperFavorites");
				
			}
			
		if(!file.exists())
	    	{
			    File wallpaperDirectory = new File(Environment.getExternalStorageDirectory().getPath()+"/BestDogsWallpaperFavorites/");
			    wallpaperDirectory.mkdirs();
		    }
		}
		else Toast.makeText(mContext, "SD card is not mounted", Toast.LENGTH_LONG).show();
		
		if(file != null){
			String name = "IMG_" + System.currentTimeMillis() + ".png";
			file = new File(file, name);
				try {
					file.createNewFile();
					FileOutputStream out = new FileOutputStream(file.getAbsolutePath());
					bitmap.compress(CompressFormat.PNG, 100, out);
					out.flush();
					out.close();
					out = null;
					failure = false;
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
			
		}
		if(failure){
			Log.d(TAG, "Failed to create File");
			return null;
			}
		
		return file.getAbsolutePath();
	}
	
}
