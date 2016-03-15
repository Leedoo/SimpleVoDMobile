/**
 * Created on 2016-3-15
 *
 * @author: wgq
 */
package com.clear.vodmobile.view;

import java.util.List;

import com.clear.vodmobile.R;
import com.clear.vodmobile.VideoPlayerActivity;
import com.clear.vodmobile.util.ImageUtil;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LiveChannelListAdapter extends BaseAdapter  {
	private static String TAG = "LiveChannelListAdapter";
	
	private Context ctx;
	private List<LiveChannelInfo> listItems;
	private LayoutInflater listContainer;
	
	public final class ListViewChannelItem {
		public ImageView iv;
		public TextView tv;
	}
	
	private final class ChannelOnClickListener implements View.OnClickListener {
		private LiveChannelInfo channel;
		
		public ChannelOnClickListener(LiveChannelInfo channel) {
			this.channel = channel;
		}
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(ctx, VideoPlayerActivity.class);
			intent.putExtra("Type", "Live");
			intent.putExtra("Name", channel.Name);
			intent.putExtra("URL", channel.URL);
			ctx.startActivity(intent);
		}
	}
	
	
	public LiveChannelListAdapter(Context ctx, List<LiveChannelInfo> listItems) {
		this.ctx = ctx;
		this.listItems = listItems;
		listContainer = LayoutInflater.from(ctx); 
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return listItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Log.d(TAG, "live get view: " + pos + " convertView: " + convertView);
		
		ListViewChannelItem viewItem = null;
		if(convertView == null) {
			convertView = listContainer.inflate(R.layout.live_item, null);
			
			viewItem = new ListViewChannelItem();
			viewItem.iv = (ImageView) convertView.findViewById(R.id.channel_pic);
			viewItem.tv = (TextView) convertView.findViewById(R.id.channel_name);
		
			convertView.setTag(viewItem);
		}
		else {
			viewItem = (ListViewChannelItem) convertView.getTag();
		}
		
		ImageUtil.displayImage(listItems.get(pos).PicURL, viewItem.iv);
		
		//viewItem.iv.setOnClickListener(new ChannelOnClickListener(listItems.get(pos)));
		
		viewItem.tv.setText(listItems.get(pos).Name);
		
		return convertView;
	}

}
