package com.clear.vodmobile;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.clear.vodmobile.Constant.ErrorCode;
import com.clear.vodmobile.R;
import com.clear.vodmobile.util.CommonUtils;
import com.clear.vodmobile.view.FragmentFactory;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final String TAG = "ClearVoDMobileMain";
	
	private FragmentManager fragManager;
	private RadioGroup radioGroup;
	
	private JSONObject entryJson = null;
	private JSONObject mainMenuJson;
	
	private ArrayList<FragmentData> fragmentDataset = new ArrayList<FragmentData>();

	
	private class FragmentData {
		public JSONObject menuJson = null;
		public RadioButton radioBtn = null;
		public BitmapDrawable icon = null;
		public BitmapDrawable iconFocused = null;
		
		public FragmentData() {
			
		}
	}
	
	private class AsyncIconLoad implements ImageLoadingListener {
		public FragmentData fragmentData;
		public boolean focusIcon;
		
		public AsyncIconLoad(FragmentData fragdata, boolean focusIcon) {
			this.fragmentData = fragdata;
			this.focusIcon = focusIcon;
		}

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
			// TODO Auto-generated method stub
			if(focusIcon) {
				fragmentData.iconFocused = new BitmapDrawable(arg2);
				fragmentData.iconFocused.setTargetDensity(480);
			}
			else {
				fragmentData.icon = new BitmapDrawable(arg2);
				fragmentData.icon.setTargetDensity(480);
			}
			
			if(fragmentData.icon != null && fragmentData.iconFocused != null) {
				//update icon
				fragmentData.radioBtn.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						/* default empty icon */
						StateListDrawable sld = new StateListDrawable();
						sld.addState(new int[]{android.R.attr.state_checked}, 
								fragmentData.iconFocused);
						sld.addState(new int[]{android.R.attr.state_pressed}, 
								fragmentData.iconFocused);
						sld.addState(new int[]{}, 
								fragmentData.icon);
						
						fragmentData.radioBtn.setCompoundDrawablesWithIntrinsicBounds(null, sld, null, null);
					}		
				});
			}
		}

		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onLoadingStarted(String arg0, View arg1) {
			// TODO Auto-generated method stub
			
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Global.getInstance().init(this);
		
		/* start to get jsons */
		startWork();
	}
	
	protected void showErrorPage(ErrorCode e) {
		/* TODO, FIXME, complete this ... */
		Log.e(TAG, "ERROR: " + e.getErrorCode() + "\n" + e.getErrorString());
	}
	
	protected void startWork() {
		HttpUtils http = new HttpUtils();
		http.send(HttpRequest.HttpMethod.GET,
			Global.getInstance().mainServer,
			null,
		    new RequestCallBack<String>(){

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					// TODO FIXME show error page 
					showErrorPage(Constant.ErrorCode.SERV_CONN_FAIL);
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					// TODO Auto-generated method stub
					Log.i(TAG, "entry json: " + arg0.result);
					
					JSONTokener jsonParser = new JSONTokener(arg0.result); 
					try {
						entryJson = (JSONObject) jsonParser.nextValue();
					}catch (Exception e) {
						Log.e(TAG, "parse entry json failed:\n" + arg0.result);
						showErrorPage(Constant.ErrorCode.SERV_INFO_PARSE_FAIL);
						entryJson = null;
						return ;
					}
					
					parseEntryJson();
				}
			});
	}
	
	protected void parseEntryJson() {
		/* TODO, FIXME, show welcome page ... */
		String url = null;
		try {
			url = Global.getInstance().getMainServerPrefix() 
					+ entryJson.getString("MainView_Json_URL");
		
		}catch (JSONException e) {
			Log.e(TAG, "get main menu url failed");
			showErrorPage(Constant.ErrorCode.SERV_INFO_PARSE_FAIL);
			entryJson = null;
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
					showErrorPage(Constant.ErrorCode.SERV_CONN_FAIL);
				}

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					// TODO Auto-generated method stub
					Log.i(TAG, "main json: " + arg0.result);
					
					JSONTokener jsonParser = new JSONTokener(arg0.result); 
					try {
						mainMenuJson = (JSONObject) jsonParser.nextValue();
					}catch (Exception e) {
						Log.e(TAG, "parse main menu json failed:\n" + arg0.result);
						showErrorPage(Constant.ErrorCode.SERV_INFO_PARSE_FAIL);
						entryJson = null;
						return ;
					}
					
					parseMainJson();
				}
			});
		
	}
	
	protected void parseMainJson() {
		try {
			JSONArray contents = mainMenuJson.getJSONArray("Content");
			for (int i = 0; i < contents.length(); i++) {
				JSONObject frag = contents.getJSONObject(i);
				FragmentData fragData = new FragmentData();
				fragData.menuJson = frag;
				fragmentDataset.add(fragData);
			}
		} catch (JSONException e) {
			Log.e(TAG, "get first menus failed");
			showErrorPage(Constant.ErrorCode.SERV_INFO_PARSE_FAIL);
			entryJson = null;
			return ;
		}
		
		initViews();
	}
	
	protected void initViews() {
		LinearLayout rootView = (LinearLayout) findViewById(R.id.rootview);
        
        fragManager = getFragmentManager();
        
		radioGroup = new RadioGroup(this);
		radioGroup.setOrientation(LinearLayout.HORIZONTAL);
		radioGroup.setBackgroundResource(R.drawable.bg_listab);
		
		for(int i = 0; i < fragmentDataset.size(); i++) {
			RadioButton rb = new RadioButton(this);
			
			rb.setBackgroundResource(R.drawable.bottombar_itembg_selector);
			rb.setButtonDrawable(null);
			rb.setGravity(Gravity.CENTER);
			
			/* TODO, FIXME, replace with default empty icon */
			StateListDrawable sld = new StateListDrawable();
			sld.addState(new int[]{android.R.attr.state_checked}, 
					getResources().getDrawable(R.drawable.listab_a_on));
			sld.addState(new int[]{android.R.attr.state_pressed}, 
					getResources().getDrawable(R.drawable.listab_a_on));
			sld.addState(new int[]{}, 
					getResources().getDrawable(R.drawable.listab_a_off));
			
			rb.setCompoundDrawablesWithIntrinsicBounds(null, sld, null, null);

			rb.setTextSize(14);
			rb.setTextColor(getResources().getColorStateList(R.color.bottombar_textcolor_selector));
			rb.setPadding(0, 6, 0, 6);
			
			fragmentDataset.get(i).radioBtn = rb;
			
			try {
				rb.setText(fragmentDataset.get(i).menuJson.getString("Name"));
			
				/* start async thread to get icons */
				ImageLoader.getInstance().loadImage(
						Global.getInstance().getMainServerPrefix() + 
						fragmentDataset.get(i).menuJson.getString("Icon_URL"), 
						new AsyncIconLoad(fragmentDataset.get(i), false));
						
				ImageLoader.getInstance().loadImage(
						Global.getInstance().getMainServerPrefix() + 
						fragmentDataset.get(i).menuJson.getString("Icon_focus_URL"), 
						new AsyncIconLoad(fragmentDataset.get(i), true));
				
			} catch (JSONException e) {
				Log.e(TAG, "get fragment name failed");
			}

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					CommonUtils.getScreenWidth(this) / fragmentDataset.size(), 
					LinearLayout.LayoutParams.WRAP_CONTENT,
					1);
			radioGroup.addView(rb, lp);
		}
	 
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				Log.i(TAG, checkedId + " radio button selected");
				
				Fragment fragment = FragmentFactory.getInstanceByType( 
						fragmentDataset.get((checkedId - 1)%fragmentDataset.size())
						.menuJson);
				
				if(fragment != null) {
					FragmentTransaction transaction = fragManager.beginTransaction();
					transaction.replace(R.id.content, fragment);
					transaction.commit();
				}
			}
		});
		
		rootView.addView(radioGroup, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT));
		
		if(radioGroup.getChildCount() > 0) {
			RadioButton rb = (RadioButton) radioGroup.getChildAt(0);
			rb.setChecked(true);
		}
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
        Global.getInstance().finit();
        
        super.onDestroy();
    }
}
