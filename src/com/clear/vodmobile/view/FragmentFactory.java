/**
 * Created on 2016-3-14
 *
 * @author: wgq
 */
package com.clear.vodmobile.view;

import org.json.JSONException;
import org.json.JSONObject;

import com.clear.vodmobile.util.JsonUtil;

import android.app.Fragment;
import android.util.Log;

public class FragmentFactory {
	
	private static final String TAG = "FragmentFactory";
	
	/* TODO, FIXME, BUG, 4.4 Fragment reuse. only walkaround */
	private static Fragment lastLiveFragment=null;
	private static Fragment lastMovieTopRecommandFragment=null;
	private static Fragment lastPicTextSimpleFragment=null;
	
	/* TODO, FIXME, keep the fragment with same type & menuJson */
	
	public static Fragment getInstanceByType(JSONObject menuJson) {
        Fragment fragment = null;  
        String type;
		try {
			type = menuJson.getString("Type");
		} catch (JSONException e) {
			Log.e(TAG, "unknown fragment type " + menuJson.toString());
			return null;
		}
		
        if(type.compareTo("Live") == 0) {
            fragment = new LiveFragment(menuJson);  
        }
        else if(type.compareTo("Movie_TopRecommend") == 0) {
            fragment = new MovieTopRecommandFragment(menuJson);  
        }
        else if(type.compareTo("PicText_Simple") == 0) {
            fragment = new PicTextSimpleFragment(menuJson);  
        }  
        return fragment;  
    }

}
