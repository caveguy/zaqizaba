package com.tt.main;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetChangedReciever extends BroadcastReceiver {
	public static CallBack callBack=null;
	

	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//Toast.makeText(context, intent.getAction(), 1).show();
		netChangedCallBack(hasNetWork(context));
		//Toast.makeText(context, "mobile:"+mobileInfo.isConnected()+"\n"+"wifi:"+wifiInfo.isConnected()
		//		        +"\n"+"active:"+activeInfo.getTypeName(), 1).show();
	}  //如果无网络连接activeInfo为null

	
	boolean  hasNetWork(Context context){
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		NetworkInfo ethInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
		NetworkInfo activeInfo = manager.getActiveNetworkInfo();
		
		boolean isConnected=mobileInfo.isConnected()|wifiInfo.isConnected()|ethInfo.isConnected();
		
		return isConnected;
	}
	
	
	public static void setCallBack(CallBack call) {
		callBack = call;
	}

	public interface CallBack {
		
		void netWorkChanged(boolean connected);

	}
	
	public static void netChangedCallBack(boolean connected ){
		if(callBack!=null)
			callBack.netWorkChanged(connected);
	}
	
}