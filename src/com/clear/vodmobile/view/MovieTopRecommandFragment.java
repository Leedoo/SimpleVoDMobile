/**
 * Created on 2016-3-14
 *
 * @author: wgq
 */
package com.clear.vodmobile.view;

import org.json.JSONObject;

import com.clear.vodmobile.R;
import com.clear.vodmobile.VideoPlayerActivity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class MovieTopRecommandFragment extends Fragment {
	
	public MovieTopRecommandFragment(JSONObject menuJson) {
		
	}
	
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {  
        View view = inflater.inflate(R.layout.view_vod, null);  
        
        ImageButton ib = (ImageButton) view.findViewById(R.id.mediacontroller_back);
        ib.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MovieTopRecommandFragment.this.getActivity(), VideoPlayerActivity.class);
				intent.putExtra("Type", "VoD");
				intent.putExtra("URL", "http://172.16.1.40/videos/720p_2012.mp4");
				startActivity(intent);
			}
		});
        
        return view;  
    }  
}
