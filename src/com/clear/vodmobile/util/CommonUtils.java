/**
 * Created on 2015-11-23
 *
 * @author: wgq
 */
package com.clear.vodmobile.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Point;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.lidroid.xutils.util.LogUtils;

public class CommonUtils {
	
	private static final String TAG = "CommonUtils";  
	
    /**
     * TODO, FIXME, check if cache dir will be cleared by system ...
     * 
     * TODO, FIXME, api 19 以上，会提供 getExternalCacheDirs 返回多个外部存储
     * 
     * @param context
     * @param dirName Only the folder name, not full path.
     * @return app_cache_path/dirName
     */
	public static String getDiskCacheDir(Context context, String dirName) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File externalCacheDir = context.getExternalCacheDir();
            if (externalCacheDir != null) {
                cachePath = externalCacheDir.getPath();
            }
        }   
        if (cachePath == null) {
            File cacheDir = context.getCacheDir();
            if (cacheDir != null && cacheDir.exists()) {
                cachePath = cacheDir.getPath();
            }
        }
        
        return cachePath + File.separator + dirName;
    }
	
	/**

     * TODO, FIXME, api 19 以上，会提供 getExternalFilesDirs 返回多个外部存储
     * 
     * @param context
     * @param dirName Only the folder name, not full path.
     * @return app_cache_path/dirName
     */
	public static String getDiskFilesDir(Context context, String dirName) {
        String filesPath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File externalFilesDir = context.getExternalFilesDir(null);
            if (externalFilesDir != null) {
            	filesPath = externalFilesDir.getPath();
            }
        }
        if (filesPath == null) {
            File filesDir = context.getFilesDir();
            if (filesDir != null && filesDir.exists()) {
            	filesPath = filesDir.getPath();
            }
        }
        
        return filesPath + File.separator + dirName;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static long getAvailableSpace(String dir) {
    	File directory = new File(dir);
        try {
            final StatFs stats = new StatFs(directory.getPath());
            int curApiVersion = android.os.Build.VERSION.SDK_INT;
            if(curApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            	return (long) stats.getBlockSizeLong() * (long) stats.getAvailableBlocksLong();
            }
            else {
            	return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
            }
        } catch (Throwable e) {
            LogUtils.e(e.getMessage(), e);
            return -1;
        }
    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static long getTotalSpace(String dir) {
    	File directory = new File(dir);
        try {
            final StatFs stats = new StatFs(directory.getPath());
            int curApiVersion = android.os.Build.VERSION.SDK_INT;
            if(curApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            	return (long) stats.getBlockSizeLong() * (long) stats.getBlockCountLong();
            }
            else {
            	return (long) stats.getBlockSize() * (long) stats.getBlockCount();
            }
        } catch (Throwable e) {
            LogUtils.e(e.getMessage(), e);
            return -1;
        }
    }
    
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	public static int getAvailableSpacePercent(String dir) {
    	File directory = new File(dir);
        try {
            final StatFs stats = new StatFs(directory.getPath());
            int curApiVersion = android.os.Build.VERSION.SDK_INT;
            if(curApiVersion >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            	return (int) (stats.getAvailableBlocksLong() * 100 / stats.getBlockCountLong());
            }
            else {
            	return stats.getAvailableBlocks() * 100 / stats.getBlockCount();
            }
        } catch (Throwable e) {
            LogUtils.e(e.getMessage(), e);
            return -1;
        }
    }
    
    public static String getID(Context ctx) {
    	/* first check mac */
    	String ID = null;
    	WifiManager wifi = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
    	if (!wifi.isWifiEnabled()) {
            wifi.setWifiEnabled(true);
    	}
    	
    	WifiInfo info = wifi.getConnectionInfo();  
        ID = info.getMacAddress();  
        if(ID != null && ID.length() > 0)
        	return ID;
    	
        ID = getMacByBusybox();
        if(ID != null && ID.length() > 0)
        	return ID;
        
        /* TODO, FIXME, if mac is not available, use some other, SN... */
        		
        return "Unknown";
    }
    

    public static String getMacByBusybox(){   
        String result = null;     
        String Mac = null;
        result = callCmd("busybox ifconfig", "HWaddr");
         
        if(result == null){
            return null;
        }
         
        if(result.length() > 0 && result.contains("HWaddr") == true){
            Mac = result.substring(result.indexOf("HWaddr")+6, result.length()-1);
            Log.i(TAG, "Mac:"+Mac+" Mac.length: "+Mac.length());
            Mac = Mac.replaceAll(" ", "");
            result = Mac;        
        }
        else {
        	result = null;
        }
        
        return result;
    }   
    
     
    public static String callCmd(String cmd, String filter) {
        String result = null;   
        String line = "";   
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());   
            BufferedReader br = new BufferedReader (is);   
             
            while ((line = br.readLine ()) != null && line.contains(filter)== false) {   
                //result += line;
                //L.d(TAG,"cmd res line: "+line);
            }
             
            result = line;
            //L.i(TAG,"result: "+result);
        }   
        catch(Exception e) {   
            e.printStackTrace();   
        }   
        return result;   
    }

	/** 
     * GZIP decompress 
     *  
     * @param data 
     * @return 
     * @throws Exception 
     */  
    public static byte[] gzipDecompress(byte[] data) throws Exception {  
        ByteArrayInputStream bais = new ByteArrayInputStream(data);  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        GZIPInputStream gis = new GZIPInputStream(bais); 
        
	    int count;  
	    byte buf[] = new byte[10 * 1024];  
	    
	    while ((count = gis.read(buf, 0, 10 * 1024)) != -1) {  
	    	baos.write(buf, 0, count);  
	    }  
	  
        byte result[] = baos.toByteArray();  
  
        baos.flush();  
        baos.close(); 
        gis.close(); 
        bais.close();  
  
        return result;  
    }  
    
    public static byte[] gzipDecompress(InputStream is) throws Exception {  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        GZIPInputStream gis = new GZIPInputStream(is); 
        
	    int count;  
	    byte buf[] = new byte[10 * 1024];  
	    
	    while ((count = gis.read(buf, 0, 10 * 1024)) != -1) {  
	    	baos.write(buf, 0, count);  
	    }  
	  
        byte result[] = baos.toByteArray();  
  
        baos.flush();  
        baos.close(); 
        gis.close();  
  
        return result;  
    }  
    
    public static byte[] gzipCompress(byte[] data) throws Exception {  
        ByteArrayInputStream bais = new ByteArrayInputStream(data);  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
  
        gzipCompress(bais, baos);  
  
        byte[] output = baos.toByteArray();  
  
        baos.flush();  
        baos.close();  
        bais.close();  
  
        return output;  
    }  
  
    
    public static void gzipCompress(InputStream is, OutputStream os)  
            throws Exception {  
      
        GZIPOutputStream gos = new GZIPOutputStream(os);  
      
        int count;  
        byte data[] = new byte[10 * 1024];  
        while ((count = is.read(data, 0, 10 * 1024)) != -1) {  
            gos.write(data, 0, count);  
        }  
      
        gos.finish();  
      
        gos.flush();  
        gos.close();  
    }  
    
    /**
     * 返回的值区间，不包括 maxNum
     * 
     * TODO, FIXME, 是否随机分布够均匀 
     */
    public static int getRandomInt(int maxNum) {
    	Random r = new Random();
    	return r.nextInt(maxNum);
    }
    
    /* colorStr #ARGB, eg. #FF112233 */
    public static int getColorFromStr(String colorStr) {
    	int c = Color.WHITE;

    	if(colorStr.length() > 8) {
	    	try {
	    		c = Color.argb(
	    			Integer.parseInt(colorStr.substring(1, 3), 16),
	    			Integer.parseInt(colorStr.substring(3, 5), 16),
	    			Integer.parseInt(colorStr.substring(5, 7), 16),
	    			Integer.parseInt(colorStr.substring(7), 16));
	    	}catch(NumberFormatException e) {
	    		Log.w(TAG, "Parse color hex string fail " + colorStr);
	    	}
    	}
    	else {
    		try {
	    		c = Color.rgb(
	    			Integer.parseInt(colorStr.substring(1, 3), 16),
	    			Integer.parseInt(colorStr.substring(3, 5), 16),
	    			Integer.parseInt(colorStr.substring(5), 16));
	    	}catch(NumberFormatException e) {
	    		Log.w(TAG, "Parse color hex string fail " + colorStr);
	    	}
    	}
    	return c;
 
    }
    
    public static String getLocalIPAddres()  {
    	
    	try {
		    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
		    		en.hasMoreElements();){ 
		        NetworkInterface intf = en.nextElement(); 
		        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); 
		        		enumIpAddr.hasMoreElements();){ 
		            InetAddress inetAddress = enumIpAddr.nextElement(); 
		            if(!inetAddress.isLoopbackAddress() 
		            		&& (inetAddress instanceof Inet4Address)){ 
		            	return inetAddress.getHostAddress().toString();
		            }
		        }
		    }
    	}catch(SocketException e) {
    		
    	}
    	
	    return "0.0.0.0";
	} 
    
    public static String getVersionName(Context ctx){
		PackageManager packageManager =ctx.getPackageManager();
        PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(ctx.getPackageName(),0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "Unknown";
		}
		return packInfo.versionName;
		
	}
    
    public static int getVersionCode(Context ctx){
		PackageManager packageManager =ctx.getPackageManager();
        PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(ctx.getPackageName(),0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG,"get version code error!");
			return -1;
		}
		return packInfo.versionCode;
	}
    
    public static String getVersionNameAndCode(Context ctx){
    	return getVersionName(ctx) + "(" + getVersionCode(ctx) + ")";
    }
    
    public static boolean checkHttpFormat(String url) {
        if (url == null || url.length() <= "http://".length()) {
            return false;
        }
        
        try {
            URL tUrl = new URL(url);
            if (tUrl.getProtocol().equalsIgnoreCase("http")) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "url format fail: " + url);
        }
        
        return false;
    }
    
    public static int getScreenWidth(Activity ctx) {

    	WindowManager windowManager = ctx.getWindowManager();    
        Display display = windowManager.getDefaultDisplay();    
        Point size = new Point();
		display.getSize(size);   
		Log.i(TAG, "windows size: " + size.x + "x" + size.y);
		return size.x;
    }
    
    public static int getScreenHeight(Activity ctx) {

    	WindowManager windowManager = ctx.getWindowManager();    
        Display display = windowManager.getDefaultDisplay();    
        Point size = new Point();
		display.getSize(size);   
		Log.i(TAG, "windows size: " + size.x + "x" + size.y);
		return size.y;
    }
}
