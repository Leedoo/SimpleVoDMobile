/**
 * Created on 2016-3-13
 *
 * @author: wgq
 */
package com.clear.vodmobile;

import com.clear.vodmobile.R;
import com.clear.vodmobile.player.ClearVideoView;

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
		String Name = intent.getStringExtra("Name");
		String URL = intent.getStringExtra("URL");
		Log.i(TAG, "Play\n");
		Log.i(TAG, "Type: " + Type);
		Log.i(TAG, "Name: " + Name);
		Log.i(TAG, "URL: " + URL);
		
		ClearVideoView vv = (ClearVideoView) findViewById(R.id.videoview);
		vv.setVideoPath(URL);
		vv.setMovieName(Name);
		vv.start();
		vv.enableMediaController(true);
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart called.");
    }

    @Override
    protected void onRestart() {
        Log.i(TAG, "onRestart called.");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "onResume called.");
        super.onResume();
    }

    /*
     * @Override public void onWindowFocusChanged(boolean hasFocus) {
     * super.onWindowFocusChanged(hasFocus); L.i(TAG,
     * "onWindowFocusChanged called."); }
     */

    @Override
    protected void onPause() {
        Log.i(TAG, "onPause called.");
        super.onPause();

    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop called.");
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestory called.");
        super.onDestroy();

    }
}
