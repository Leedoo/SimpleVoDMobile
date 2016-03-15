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
import android.widget.ImageButton;
import android.widget.ListView;

public class MovieTopRecommandFragment extends Fragment {
	private static String TAG = "MovieTopRecommandFragment";
	
	private JSONObject menuJson;
	private JSONObject moviesJson;
	
	private ArrayList<MovieInfo> movies = new ArrayList<MovieInfo>();
	
	private ListView listView;
	private MovieTopRecListAdapter adapter;

	
	public MovieTopRecommandFragment(JSONObject menuJson) {
		this.menuJson = menuJson;
	}
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
        View view = inflater.inflate(R.layout.view_movie_toprecommand, null);  
        
        loadJson();
        
        return view;  
    }  
	
	protected void loadJson() {
		String url = null;
		try {
			url = Global.getInstance().getMainServerPrefix() + menuJson.getString("Json_URL");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "get json url for movie top recommend failed");
			return ;
		}
		
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.GET,
			url,
			null,
		    new RequestCallBack<String>(){

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					// TODO FIXME show error page 
					Log.e(TAG, "get json for movie top recommend failed");
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					// TODO Auto-generated method stub
					Log.i(TAG, "movie top recommend json: " + arg0.result);
					
					JSONTokener jsonParser = new JSONTokener(arg0.result); 
					try {
						moviesJson = (JSONObject) jsonParser.nextValue();
					}catch (Exception e) {
						Log.e(TAG, "parse movie top recommend json failed:\n" + arg0.result);
						moviesJson = null;
						return ;
					}
					
					parseMoviesJson();
				}
			});
	}
	
	public void parseMoviesJson() {
		try {
			JSONArray contents = moviesJson.getJSONArray("Content");
			for (int i = 0; i < contents.length(); i++) {
				JSONObject movieJson = contents.getJSONObject(i);
				MovieInfo m = new MovieInfo();
				m.Name = movieJson.getString("Name");
				m.NameEng = movieJson.getString("NameEng");
				m.URL = Global.getInstance().getMainServerPrefix() + movieJson.getString("Address");
				m.PicURL = Global.getInstance().getMainServerPrefix() + movieJson.getString("Picurl");
				movies.add(m);
			}
		} catch (JSONException e) {
			Log.e(TAG, "get movie failed");
			moviesJson = null;
			return ;
		}
		
		initViews();
	}
	
	public void initViews() {
		Activity activity = this.getActivity();
		if(activity == null)
			return ;
		listView = (ListView) activity.findViewById(R.id.movie_list);
		adapter = new MovieTopRecListAdapter(this.getActivity(), movies);
		listView.setAdapter(adapter);
	}
}
