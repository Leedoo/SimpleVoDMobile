/**
 * Created on 2016-3-15
 *
 * @author: wgq
 */
package com.clear.vodmobile.view;

import java.util.List;

import com.clear.vodmobile.R;
import com.clear.vodmobile.util.ImageUtil;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MovieTopRecListAdapter extends BaseAdapter  {
	private static String TAG = "MovieTopRecListAdapter";
	
	private Context ctx;
	private List<MovieInfo> listItems;
	private LayoutInflater listContainer;
	
	public final class ListViewMovieItem {
		public ImageView iv;
		public TextView tv;
	}
	
	
	public MovieTopRecListAdapter(Context ctx, List<MovieInfo> listItems) {
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
		Log.d(TAG, "get view: " + pos + " arg1: " + convertView);
		
		ListViewMovieItem viewItem = null;
		if(convertView == null) {
			convertView = listContainer.inflate(R.layout.movie_item_pic_text, null);
			
			viewItem = new ListViewMovieItem();
			viewItem.iv = (ImageView) convertView.findViewById(R.id.movie_pic);
			viewItem.tv = (TextView) convertView.findViewById(R.id.movie_name);
		
			convertView.setTag(viewItem);
		}
		else {
			viewItem = (ListViewMovieItem) convertView.getTag();
		}
		
		ImageUtil.displayImage(listItems.get(pos).PicURL, viewItem.iv);
		
		viewItem.iv.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "movie clicked");
			}
		});
		
		viewItem.tv.setText(listItems.get(pos).Name);
		
		return convertView;
	}

}
