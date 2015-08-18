package com.cuttingchai.assignment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.net.ParseException;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cuttingchai.assignment.MainActivity.HandleSearchCloseListener;
import com.audiocompass.assignment.R;


public class SearchImageGridFragment extends IndexImageGridFragment implements OnCloseListener, HandleSearchCloseListener, OnQueryTextListener {
	
	private static final String TAG = SearchImageGridFragment.class.getSimpleName();
	private SearchView mSearchView;
	private EditText folderName;
	int mYear;
    int mMonth;
    int mDay;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "OnCreateView");
		View view= inflater.inflate(R.layout.searc_image_grid, null);
		mGridView = (GridView)view.findViewById(R.id.imageGrid);
		mSearchView = (SearchView)view.findViewById(R.id.search_view);
		mSearchView.setInputType(InputType.TYPE_CLASS_DATETIME);
		mSearchView.setQueryHint(getResources().getString(R.string.search_title));
		mSearchView.setIconifiedByDefault(false);
		mSearchView.setSubmitButtonEnabled(true);
		mSearchView.setOnQueryTextListener(this);
		mSearchView.setOnCloseListener(this);
		folderName   = (EditText) view.findViewById(R.id.foldername);
		DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		    public void onDateSet(DatePicker view, int year,
		            int monthOfYear, int dayOfMonth) {
		        mYear = year;
		        mMonth = monthOfYear;
		        mDay = dayOfMonth;
		        updateDisplay();
		    }
		};
		//Initialize Default Date for Date Picker
		final Calendar initialize = Calendar.getInstance();
		mYear = initialize.get(Calendar.YEAR);
		mMonth = initialize.get(Calendar.MONTH);
		mDay = initialize.get(Calendar.DAY_OF_MONTH);
		
		DatePickerDialog d = new DatePickerDialog(getActivity(),
		        R.style.Theme_AppCompat_Light, mDateSetListener, mYear, mMonth, mDay);
		d.show();
		
		/*setSearchViewOnClickListener(mSearchView, new OnClickListener() {
			 
			@Override
			public void onClick(View v) {
				
				
			}
		});*/
		
		
		return view;
	}
	
	private void updateDisplay() {

	    GregorianCalendar c = new GregorianCalendar(mYear, mMonth, mDay);
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
	    
	    mSearchView.setQuery(sdf.format(c.getTime()), false);

	}// updateDisplay
	
	/*public static void setSearchViewOnClickListener(View v, OnClickListener listener) {
		if (v instanceof ViewGroup) {
			ViewGroup group = (ViewGroup)v;
			int count = group.getChildCount();
			for (int i = 0; i < count; i++) {
				View child = group.getChildAt(i);
				if (child instanceof LinearLayout || child instanceof RelativeLayout) {
					setSearchViewOnClickListener(child, listener);
				}
	 
				if (child instanceof TextView) {
					TextView text = (TextView)child;
					text.setFocusable(false);
				}
				child.setOnClickListener(listener);
			}
		}
	}*/
	
	@Override
	public boolean onClose() {
		mSearchView.setVisibility(View.GONE);
		return true;
	}
	@Override
	public boolean onQueryTextChange(String arg0) {
		return false;
	}
	@Override
	public boolean onQueryTextSubmit(String arg) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
		
		boolean retval = false;
		try{
			
			Date date = format.parse(arg);  
			if(folderName.getText().toString().matches("")){
				mUrl = String.valueOf(date.getTime());
			}
			else{
				Log.d("folder-name", folderName.getText().toString());
				mUrl = "date="+String.valueOf(date.getTime())+"&folder="+folderName.getText().toString();
			}
				
			mPhotos.clear();
			mLoding = true;
			mCurrentPageNumber = 1;
			if(mCurrentAsyncTask != null && !mCurrentAsyncTask.isCancelled())
				mCurrentAsyncTask.cancel(true);
			loadPhotos();
			mLoding = false;
			//mSearchView.setVisibility(View.GONE);
			return retval;
			
		}catch(ParseException e){
			
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return retval;
		
	}
	@Override
	public void handleClose() {
		if(mSearchView.getVisibility() == View.VISIBLE)
			mSearchView.setVisibility(View.GONE);
		else 
			mSearchView.setVisibility(View.VISIBLE);
	}
	
	
	
	

}
