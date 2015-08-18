package com.cuttingchai.assignment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.audiocompass.assignment.R;
import com.cuttingchai.assignment.utils.IndexDetails;
import com.cuttingchai.assignment.utils.IndexDetailsDataSource;
import com.cuttingchai.assignment.utils.Photo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;



public class FavImageGridFragment extends Fragment implements OnScrollListener, OnItemClickListener {

	public static final String URL_KEY = "url";
	public File[] listFile;
	public static final int THUMB_HEIGHT = 100;
	public static final int THUMB_WIDTH = 100;
	public static final int SPACE = 1;
	private static final int THRESHOLD = 8;
	protected String mUrl = null;
	protected GridView mGridView = null;
	private ImageGridViewAdapter mGridViewAdapter = null;
	private Context mContext = null;
	protected ArrayList<Photo> mPhotos = null;
	protected boolean mLoding = false;
	protected int mCurrentPageNumber = 1;
	private LayoutInflater mInflater = null;
	private ImageLoader imageLoader = null;
	DisplayImageOptions options;
	protected AsyncTask<String, Integer, List<Photo>> mCurrentAsyncTask;
	private IndexDetailsDataSource db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mPhotos = new ArrayList<Photo>();
		db = new IndexDetailsDataSource(getActivity());
		db.open();
		loadPhotos();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mGridView = (GridView) inflater.inflate(R.layout.image_grid, null);
		//initializeComponents();
		return mGridView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity().getApplicationContext();
		
		mGridViewAdapter = new ImageGridViewAdapter();
		mGridView.setAdapter(mGridViewAdapter);
		mGridView.setOnScrollListener(this);
		mGridView.setOnItemClickListener(this);
		imageLoader =((BaseActivity) getActivity()).imageLoader;
		if(!imageLoader.isInited())
			imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
		mInflater = getLayoutInflater(savedInstanceState);
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		
	}
	  private class ImageGridViewAdapter extends BaseAdapter {


		@Override
		public int getCount() {
			return mPhotos.size();
		}

		@Override
		public Object getItem(int position) {
			return mPhotos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			View view = convertView;
			if (view == null) {
				view = mInflater.inflate(R.layout.item_grid_image, parent, false);
				holder = new ViewHolder();
				assert view != null;
				holder.imageView = (ImageView) view.findViewById(R.id.image);
				holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
              for(int i=0;i<listFile.length;i++)
              {
	            	imageLoader.displayImage(mPhotos.get(position).setmThumbnailImageURL(Uri.fromFile(listFile[position]).toString()), holder.imageView, options, new SimpleImageLoadingListener() {
						 @Override
						 public void onLoadingStarted(String imageUri, View view) {
							 holder.progressBar.setProgress(0);
							 holder.progressBar.setVisibility(View.VISIBLE);
						 }

						 @Override
						 public void onLoadingFailed(String imageUri, View view,
								 FailReason failReason) {
							 holder.progressBar.setVisibility(View.GONE);
						 }

						 @Override
						 public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
							 holder.progressBar.setVisibility(View.GONE);
						 }
					 }, new ImageLoadingProgressListener() {
						 @Override
						 public void onProgressUpdate(String imageUri, View view, int current,
								 int total) {
							 holder.progressBar.setProgress(Math.round(100.0f * current / total));
						 }
					 }
);
			

              }
			

			
			return view;
		}
		class ViewHolder {
			ImageView imageView;
			ProgressBar progressBar;
		}
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if(scrollState == SCROLL_STATE_IDLE && view.getLastVisiblePosition() >= view.getCount() - THRESHOLD
				&& !mLoding ){
			mLoding = true;

			
			loadPhotos();
			
		}
		
	}

	protected void loadPhotos() {
			
	    
		List<IndexDetails> values;
		values = db.getAllComments();
		
		for(int i=0;i<values.size();i++)
		{		
			//listFile[i] = new File(values.get(i).getPath());
			System.out.print("Path Fragment: "+values.get(i).getPath()+ " Date: "+values.get(i).getDate());
			Photo toAdd = new Photo(Uri.parse(values.get(i).getPath()));
        	mPhotos.add(toAdd);
		}
		
		
		
		db.close();
		/*File file= new File(android.os.Environment.getExternalStorageDirectory(),"BestPhotoNowFavorites");

        if (file.isDirectory())
        {
            listFile = file.listFiles();


            for (int i = 0; i < listFile.length; i++)
            {            	
            	Photo toAdd = new Photo(Uri.fromFile(listFile[i]));
            	System.out.println("Uri path: "+Uri.fromFile(listFile[i]));
            	mPhotos.add(toAdd);
            }
           
        }*/
		
		
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent pagerIntent = new Intent(mContext, PagerActivity.class);
		pagerIntent.putParcelableArrayListExtra(PagerActivity.PHOTOS, mPhotos);
		pagerIntent.putExtra(PagerActivity.INIT_POSITION, position);
		startActivity(pagerIntent);
	}

}
