/**
 * Created on 2016-3-14
 *
 * @author: wgq
 */
package com.clear.vodmobile;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


public class Global {
	static private Global singleInstance = null;
	
	public String mainServer = "http://192.168.18.101/nativevod/now/main.json";
	 
	private Global() {
	}
	 
	public static Global getInstance() {
        if (singleInstance == null) {
            singleInstance = new Global();
        }
        return singleInstance;
    }
	
	public void init(Context ctx) {
		/* init universal image loader */
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(ctx);
        ImageLoader.getInstance().init(configuration);
	}
	
	public void finit() {
		ImageLoader.getInstance().destroy();
	}
	
	public String getMainServerPrefix() {
		return mainServer.substring(0, mainServer.lastIndexOf('/'));
	}
}
