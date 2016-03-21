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

public class SimplePicTextListAdapter extends BaseAdapter  {
	private static String TAG = "SimplePicTextListAdapter";
	
	private Context ctx;
	private List<SimplePicTextInfo> listItems;
	private LayoutInflater listContainer;

	public SimplePicTextListAdapter(Context ctx, List<SimplePicTextInfo> listItems) {
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
		Log.d(TAG, "SimplePicText get view: " + pos + " arg1: " + convertView);
		
		//if(convertView == null) {
			convertView = listContainer.inflate(R.layout.simple_pic_text_item, null);
		//}

		ImageUtil.displayImage(listItems.get(pos).PicURL, 
				(ImageView)convertView.findViewById(R.id.simple_pictext_pic));
		
		((TextView) convertView.findViewById(R.id.simple_pictext_text))
			.setText(listItems.get(pos).Introduce);
		
		return convertView;
	}

}
