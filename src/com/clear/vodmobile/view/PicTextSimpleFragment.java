/**
 * Created on 2016-3-14
 *
 * @author: wgq
 */
package com.clear.vodmobile.view;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.clear.vodmobile.Global;
import com.clear.vodmobile.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class PicTextSimpleFragment extends Fragment {
	private static String TAG = "PicTextSimpleFragment";
	
	private JSONObject menuJson;
	private JSONObject contentJson;
	
	private ArrayList<SimplePicTextInfo> contents = new ArrayList<SimplePicTextInfo>();
	
	private ListView listView;
	private SimplePicTextListAdapter adapter;

	
	public PicTextSimpleFragment(JSONObject menuJson) {
		this.menuJson = menuJson;
	}
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
        View view = inflater.inflate(R.layout.view_pictext_simple, null);  
        
        loadJson();
        
        return view;  
    }  
	
	protected void loadJson() {
		String url = null;
		try {
			url = Global.getInstance().getMainServerPrefix() + menuJson.getString("Json_URL");
		} catch (JSONException e) {
			Log.e(TAG, "get json url for simple pic text failed");
			return ;
		}
		
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.GET,
			url,
			null,
		    new RequestCallBack<String>(){

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					Log.e(TAG, "get json for simple pic text failed");
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					Log.i(TAG, "simple pic text json: " + arg0.result);
					
					JSONTokener jsonParser = new JSONTokener(arg0.result); 
					try {
						contentJson = (JSONObject) jsonParser.nextValue();
					}catch (Exception e) {
						Log.e(TAG, "parse simple pic text json failed:\n" + arg0.result);
						contentJson = null;
						return ;
					}
					
					parsePicTextJson();
				}
			});
	}
	
	public void parsePicTextJson() {
		try {
			JSONArray contentJsonArray = contentJson.getJSONArray("Content");
			for (int i = 0; i < contentJsonArray.length(); i++) {
				JSONObject picTextJson = contentJsonArray.getJSONObject(i);
				SimplePicTextInfo picText = new SimplePicTextInfo();
				picText.Introduce = picTextJson.getString("Introduce");
				picText.IntroduceEng = picTextJson.getString("Introduce_eng");
				picText.PicURL = Global.getInstance().getMainServerPrefix() + picTextJson.getString("Picurl");
				contents.add(picText);
			}
		} catch (JSONException e) {
			Log.e(TAG, "get movie failed");
			contentJson = null;
			return ;
		}
		
		initViews();
	}
	
	public void initViews() {
		Activity activity = this.getActivity();
		if(activity == null)
			return ;
		listView = (ListView) activity.findViewById(R.id.simple_pictext_list);
		adapter = new SimplePicTextListAdapter(activity, contents);
		listView.setAdapter(adapter);
	}
}
