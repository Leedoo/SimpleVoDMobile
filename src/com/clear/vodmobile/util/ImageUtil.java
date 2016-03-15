/**
 * Created on 2015-12-5
 *
 * @author: wgq
 */
package com.clear.vodmobile.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.nostra13.universalimageloader.core.ImageLoader;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class ImageUtil {

	public static void displayImage(String URL, ImageView imgView) {
		/* TODO, FIXME, who should do the scale? imageview or uil ??? */
		imgView.setScaleType(ScaleType.FIT_XY);
		
		/*DisplayImageOptions options = new DisplayImageOptions.Builder()
		    .resetViewBeforeLoading(false)  // default
		    .delayBeforeLoading(1000)
		    .cacheInMemory(false) // default
		    .cacheOnDisk(false) // default
		    .considerExifParams(false) // default
		    .imageScaleType(ImageScaleType.EXACTLY) 
		    .build();
		
		ImageLoader.getInstance().displayImage(
				"file:/" + localUri, imgView, options);
		*/
		ImageLoader.getInstance().displayImage(URL, imgView);
	}
	
	@SuppressWarnings("deprecation")
	public static Drawable drawableFromUrlSync(String url) throws IOException {
	    Bitmap x;

	    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
	    connection.connect();
	    InputStream input = connection.getInputStream();

	    x = BitmapFactory.decodeStream(input);
	    return new BitmapDrawable(x);
	    
	}
}
