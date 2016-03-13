package com.clear.vodmobile;

import com.clear.vodmobile.R;
import com.clear.vodmobile.player.VideoPlayerActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Intent videoIntent = new Intent(this, VideoPlayerActivity.class);
		videoIntent.putExtra("Type", "VoD");
		videoIntent.putExtra("URL", "http://vodresource.cleartv.cn/videos/720p_2012.mp4");
		this.startActivity(videoIntent);
	}
}
