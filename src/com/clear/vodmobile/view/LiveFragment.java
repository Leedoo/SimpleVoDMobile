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
import com.clear.vodmobile.VideoPlayerActivity;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class LiveFragment extends Fragment {
	private static String TAG = "LiveFragment";
	
	private JSONObject menuJson;
	private JSONObject channelsJson;
	
	private ArrayList<LiveChannelInfo> channels = new ArrayList<LiveChannelInfo>();
	
	private ListView listView;
	private LiveChannelListAdapter adapter;

	
	public LiveFragment(JSONObject menuJson) {
		this.menuJson = menuJson;
	}
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
        View view = inflater.inflate(R.layout.view_live, null);  
        
        loadJson();
        
        return view;  
    }  
	
	protected void loadJson() {
		String url = null;
		try {
			url = Global.getInstance().getMainServerPrefix() + menuJson.getString("Json_URL");
		} catch (JSONException e) {
			Log.e(TAG, "get json url for live channel failed");
			return ;
		}
		
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.GET,
			url,
			null,
		    new RequestCallBack<String>(){

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					Log.e(TAG, "get json for live channel failed");
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					Log.i(TAG, "live channel json: " + arg0.result);
					
					JSONTokener jsonParser = new JSONTokener(arg0.result); 
					try {
						channelsJson = (JSONObject) jsonParser.nextValue();
					}catch (Exception e) {
						Log.e(TAG, "parse live channel json failed:\n" + arg0.result);
						channelsJson = null;
						return ;
					}
					
					parseChannelsJson();
				}
			});
	}
	
	public void parseChannelsJson() {
		try {
			JSONArray contents = channelsJson.getJSONArray("Content");
			for (int i = 0; i < contents.length(); i++) {
				JSONObject channelJson = contents.getJSONObject(i);
				LiveChannelInfo channel = new LiveChannelInfo();
				channel.ChannelNum = channelJson.getInt("ChannelNum");
				channel.Name = channelJson.getString("ChannelName");
				channel.NameEng = channelJson.getString("ChannelNameEng");
				channel.PicURL = Global.getInstance().getMainServerPrefix() + channelJson.getString("ChannelPic");
				channel.URL = ((JSONObject)channelJson.getJSONArray("ChannelSrc").get(0)).getString("Src");
				channels.add(channel);
			}
		} catch (JSONException e) {
			Log.e(TAG, "get channel failed");
			channelsJson = null;
			return ;
		}
		
		initViews();
	}
	
	public void initViews() {
		final Activity activity = this.getActivity();
		if(activity == null)
			return ;
		
		listView = (ListView) activity.findViewById(R.id.channel_list);
		adapter = new LiveChannelListAdapter(this.getActivity(), channels);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int pos, long id) {
				// TODO Auto-generated method stub
				Log.i(TAG, pos + " channel " + channels.get(pos).Name + " clicked");
				
				Intent intent = new Intent(activity, VideoPlayerActivity.class);
				intent.putExtra("Type", "Live");
				intent.putExtra("Name", channels.get(pos).Name);
				intent.putExtra("URL", channels.get(pos).URL);
				startActivity(intent);
			}
		});
	}
}
