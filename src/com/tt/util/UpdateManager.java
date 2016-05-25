package com.tt.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.coffemachinev3.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

/*
 *@author Eric 
 *@2015-11-7上午8:03:31
 */
public class UpdateManager {
	private final String TAG="UpdateManager";
	private final static  String url="http://caveguy.wicp.net";
	
	private static UpdateManager manager = null;
	Context context=null;
	int version = 0;
	int serverVersion = 0;
	String versionName=null;
	final static String fileName="/CoffeMachineV3.apk";
	final static String serverFileName="/CoffeMachineV3.zip";
	String serverVersionName=null;
	private UpdateManager(Context context){
		this.context=context;
		
		
	}
	public static UpdateManager getInstance(Context context){
		manager = new UpdateManager(context);
		return manager;
	}
	public static String getFileName(){
		return fileName;
	}
	//获取版本号
	public int getVersion(){
		
		try {  
			String pkName = context.getPackageName();
			version = context.getPackageManager().getPackageInfo(  
					pkName, 0).versionCode;  
        } catch (Exception e) { 
        	dispDialog(context.getString(R.string.error),context.getString(R.string.getVerError),context.getString(R.string.ok),null,null);
			Log.e(TAG,context.getString(R.string.getVerError));
        	// System.out.println("获取版本号异常！");
        }  
		return version;
	}
	
	//获取版本名
	public String getVersionName(){
		//String versionName = null;
		try {
			String pkName = context.getPackageName();
			versionName = context.getPackageManager().getPackageInfo(
					pkName, 0).versionName;
		} catch (Exception e) {
			dispDialog(context.getString(R.string.error),context.getString(R.string.getVerError),context.getString(R.string.ok),null,null);
			Log.e(TAG,context.getString(R.string.getVerError));
		}
		return versionName;
	}
	
	//获取服务器版本号
	public String getServerVersion(){
		String serverJson = null;
		byte[] buffer = new byte[128];
		
		try {
			URL serverURL = new URL(url+"/ver.aspx");
			HttpURLConnection connect = (HttpURLConnection) serverURL.openConnection();
			BufferedInputStream bis = new BufferedInputStream(connect.getInputStream());
			int n = 0;
			while((n = bis.read(buffer))!= -1){
				serverJson = new String(buffer);
			}
		} catch (Exception e) {
			serverJson=null;
			dispDialog(context.getString(R.string.error),context.getString(R.string.errorToserver),context.getString(R.string.ok),null,null);
			Log.e(TAG,context.getString(R.string.errorToserver)+e);
		}
		
		return serverJson;
	}	
	
	
	
	void dispDialog(String title,String msg,String btn1,String btn2,DialogInterface.OnClickListener listen){

        AlertDialog.Builder builder  = new Builder(context);  
        builder.setTitle(title ) ;  
        builder.setMessage(msg ) ;  
        if(btn1!=null){
        	if(listen==null){
        		builder.setPositiveButton(btn1,new DialogInterface.OnClickListener(){

    				@Override
    				public void onClick(DialogInterface dialog, int which) {
    					dialog.dismiss();
    				}
            		
            	}); 
        	}
        	else{
        		builder.setPositiveButton(btn1,listen);
        	}
        }
        if(btn2!=null)
        	builder.setNegativeButton(btn2,new DialogInterface.OnClickListener(){

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
        		
        	});  
        builder.show();
	}
	
	
	//比较服务器版本与本地版本弹出对话框
	public boolean compareVersion(){
		
		
		version=getVersion();
		new Thread(){
			public void run() {
				Looper.prepare();
				version=getVersion();
				versionName=getVersionName();
				String serverJson = manager.getServerVersion();
				if(serverJson!=null&&versionName!=null){
				
					//解析Json数据
					try {
						JSONArray array = new JSONArray(serverJson);
						JSONObject object = array.getJSONObject(0);
						String getServerVersion = object.getString("version");
						serverVersion=new Integer(getServerVersion);
						serverVersionName = object.getString("versionName");	
						
						onSeverVerChangedCallback(getServerVersion,serverVersionName);
						if(version < serverVersion){
							
							DialogInterface.OnClickListener listen=new DialogInterface.OnClickListener() {  
				                   @Override  
				                   public void onClick(DialogInterface dialog, int arg1) { 
				                       //开启线程下载apk
				                	   new Thread(){
				                		   public void run() {
				                			   Looper.prepare();
				                			   downloadApkFile(context);
				                			   Looper.loop();
				                		   };
				                	   }.start();
				                	   dialog.dismiss();
				                   } 
				                   
				               };
							
				               dispDialog(context.getString(R.string.update),context.getString(R.string.curVer)+versionName
				            		   +"\n"+context.getString(R.string.serverVer)+serverVersionName 
				            		   ,context.getString(R.string.doNow),context.getString(R.string.nextTime),listen);
//							dispDialog("版本更新","当前版本："+versionName
//				            		+"\n"+"服务器版本："+serverVersionName,"立即更新","下次再说",listen);
	
						}else{
							dispDialog(context.getString(R.string.update),context.getString(R.string.newest),context.getString(R.string.ok),null,null);
						//	dispDialog("版本信息","当前已经是最新版本","确定",null,null);
						}
					} catch (JSONException e) {
						e.printStackTrace();

						dispDialog(context.getString(R.string.error),context.getString(R.string.errorGetServerVer),context.getString(R.string.ok),null,null);
						Log.e(TAG,context.getString(R.string.errorGetServerVer));
					}
				}else{
					dispDialog(context.getString(R.string.error),context.getString(R.string.unknowError),context.getString(R.string.ok),null,null);
				}
				Looper.loop();
			};
			
		}.start();
		
		
		
		
		
		return false;
	}
	
	
	//下载apk文件
	public void downloadApkFile(Context context){
		String savePath = Environment.getExternalStorageDirectory()+fileName;
		String serverFilePath = url+serverFileName;
		try {
			if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){  
				URL serverURL = new URL(serverFilePath);
				HttpURLConnection connect = (HttpURLConnection) serverURL.openConnection();
				BufferedInputStream bis = new BufferedInputStream(connect.getInputStream());
				File apkfile = new File(savePath);
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(apkfile));
				
				int fileLength = connect.getContentLength();
				int downLength = 0;
				int progress = 0;
				int n;
				byte[] buffer = new byte[1024];
				while((n=bis.read(buffer, 0, buffer.length))!=-1){
					bos.write(buffer, 0, n);
					downLength +=n;
					progress = (int) (((float) downLength / fileLength) * 100);
					 onVerChangedCallback(progress);
//					Message msg = new Message();
//					msg.arg1 = progress;
//					MainActivity.handler.sendMessage(msg);
					//System.out.println("发送"+progress);
				}
				bis.close();
				bos.close();
				connect.disconnect();
	        } 
			
		} catch (Exception e) {
			dispDialog("错误","下载出错！","确定",null,null);
			Log.e(TAG,"下载出错！"+e);
		}
		

		/*AlertDialog.Builder builder  = new Builder(context);  
        builder.setTitle("下载apk" ) ;  
        builder.setMessage("正在下载" ) ;  
        builder.setPositiveButton("确定",null);  
        builder.show();*/
		
		
		
	}
	CallBack callBack=null;
	public  void setCallBack(CallBack call) {
		// TODO Auto-generated method stub
		callBack = call;
	}

	void onCurVerChangedCallback(String ver,String verName){
		if(callBack!=null){
			callBack.onCurVerChanged( ver,verName);
		}
	}
	void onSeverVerChangedCallback(String ver,String verName){
		if(callBack!=null){
			callBack.onServerVerChanged( ver,verName);
		}
	}
	void onVerChangedCallback(int gress){
		if(callBack!=null){
			callBack.updateProgress( gress);
		}
	}
	public interface CallBack {

		void onCurVerChanged(String ver,String verName);
		void onServerVerChanged(String ver,String verName);
		void updateProgress(int gress);
	
	}
	
}