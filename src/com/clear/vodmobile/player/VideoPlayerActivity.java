/**
 * Created on 2016-3-13
 *
 * @author: wgq
 */
package com.clear.vodmobile.player;

import com.clear.vodmobile.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class VideoPlayerActivity extends Activity {
	private static String TAG = "VideoPlayerActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_player);
		
		
		Intent intent = getIntent();
		String Type = intent.getStringExtra("Type");
		String URL = intent.getStringExtra("URL");
		Log.i(TAG, "Type: " + Type);
		Log.i(TAG, "URL: " + URL);
		
		ClearVideoView vv = (ClearVideoView) findViewById(R.id.videoview);
		vv.setVideoPath(URL);
		vv.start();
		vv.enableMediaController(true);
		
	}
}
