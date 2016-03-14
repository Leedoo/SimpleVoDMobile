/**
 * Created on 2015-12-1
 *
 * @author: wgq
 */
package com.clear.vodmobile.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;


public class JsonUtil {
	private static final String TAG = "JsonUtil";
	
	public static String getValueByKey(String jsonStr, String key) {
		JSONTokener jsonParser = new JSONTokener(jsonStr); 
		try {
			JSONObject jsonObj = (JSONObject) jsonParser.nextValue();
			return jsonObj.getString(key);
		} catch (JSONException e) {
			Log.e(TAG, "get value by key failed: " + jsonStr);
			e.printStackTrace();
			return null;
		}
	}
	
	public static int getIntValueByKey(String jsonStr, String key) {
		JSONTokener jsonParser = new JSONTokener(jsonStr); 
		try {
			JSONObject jsonObj = (JSONObject) jsonParser.nextValue();
			return jsonObj.getInt(key);
		} catch (JSONException e) {
			Log.e(TAG, "get int value by key failed: " + jsonStr);
			e.printStackTrace();
			return Integer.MIN_VALUE;
		}
	}
}
