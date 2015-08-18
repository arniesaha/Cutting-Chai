package com.cuttingchai.assignment;

import android.app.Application;
import android.content.Context;

import org.acra.*;
import org.acra.annotation.*;

import com.audiocompass.assignment.R;
import com.cuttingchai.assignment.utils.IndexDetailsDataSource;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

@ReportsCrashes(formKey = "", // will not be used
mailTo = "arniesaha@gmail.com",
customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT },                
mode = ReportingInteractionMode.TOAST,
resToastText = R.string.crash_toast_text)

public class WallpaperApplication extends Application {
	
	
	
	public void onCreate() {
		
		super.onCreate();
		
		
		
		// The following line triggers the initialization of ACRA
        //ACRA.init(this);

		initImageLoader(getApplicationContext());

	}

	
	
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		//  ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

}
