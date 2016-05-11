package com.tt.main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.jsoup.helper.DataUtil;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.example.coffemachinev3.R;
import com.tt.util.HttpService;
import com.tt.util.ParseJasonWeather;
import com.tt.util.SharePreferenceUtil;

public class MassageFragment extends Fragment {

	TextView t_time,t_date;
	TextView t_msg1,t_msg2;//t_msg3;
	TextView t_city,t_temper,t_weather;//t_msg3;
	ImageView img_weather;
	//Timer clockTime =new Timer();
	Date curDate=null;
	SimpleDateFormat formatDate = new SimpleDateFormat ("yyyy.MM.dd;HH:mm;");
//	SimpleDateFormat formatTime = new SimpleDateFormat ("HH:mm");
	private final int Msg_updateTime=1001;
	private final int Msg_updateMsg=1002;
	private final int Msg_initMap=1003;

	SharePreferenceUtil sharePreferenceUtil;
	private final String Tag="MsgFrag";
	private final String MsgKey1="key1";
	private final String MsgKey2="key2";
	private final String MsgKey3="key3";
	Context context=null;
	//for weather
	Timer myTimer=null;
	WeatherTimerTask weatherTimerTask=null;
	ClockTimerTask clockTimerTask=null;
	
	int weather_cnt=0;
	//boolean hasGetLocation=false;
	//boolean hasGetWeather=false;
	
	final int WEATHER_FREQ_FIRST=1;//没有获取过，一分钟查询一次
	final int WEATHER_FREQ_AFTER=120;//已经获取过，2小时更新一次
	private final int WeatherTimerDuar=60*1000;
	private final int TimeTimerDuar=30*1000;
	int weather_freq=WEATHER_FREQ_FIRST;
	BMapManager mBMapMan = null;
	LocationListener mLocationListener = null;
	MKSearch mSearch = null;
	String provinceName, cityName;
	String npCityId = "";
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	
        View rootView = inflater.inflate(R.layout.fragment_msg, container, false);
        context=getActivity();
        sharePreferenceUtil=new SharePreferenceUtil(getActivity(), "Message");
        t_time=(TextView)rootView.findViewById(R.id.t_time);
       // t_week=(TextView)rootView.findViewById(R.id.t_week);
        t_date=(TextView)rootView.findViewById(R.id.t_date);
        t_msg1=(TextView)rootView.findViewById(R.id.t_msg1);
        t_msg2=(TextView)rootView.findViewById(R.id.t_msg2);
        t_city=(TextView)rootView.findViewById(R.id.t_location);
        t_weather=(TextView)rootView.findViewById(R.id.t_weather);
        t_temper=(TextView)rootView.findViewById(R.id.t_temper);
        img_weather=(ImageView)rootView.findViewById(R.id.img_weather);
      // t_msg3=(TextView)rootView.findViewById(R.id.t_msg3);
        setMessage();
        curDate = new Date(System.currentTimeMillis());//获取当前时间
        
        initTimer();
       
        return rootView;
    }
    @Override
    public void onStart(){
    	if(hasNetWork(context)){
    		initBaiDuMap();
    	}
    	super.onStart();
    }
	@Override
	public void onPause() {
		stopMapLocation();
		super.onPause();
	}

	@Override
	public void onResume() {
		startMapLocation();
		super.onResume();
	}
    
    
    
    void setMessage(){
    	String msg=sharePreferenceUtil.getStringValue(MsgKey1);
    	if(msg!=null){
    		t_msg1.setText(msg);
    	}
    	 msg=sharePreferenceUtil.getStringValue(MsgKey2);
     	if(msg!=null){
     		t_msg2.setText(msg);
    	}
//	   	 msg=sharePreferenceUtil.getStringValue(MsgKey3);
//	    if(msg!=null){
//	    	t_msg3.setText(msg);
//	   	}
    }

    String getWeek(int day){
    	switch(day){
    	case 0:
    		return  "星期日";
    	case 1:
    		return  "星期一";
    	case 2:
    		return  "星期二";
    	case 3:
    		return  "星期三";
    	case 4:
    		return  "星期四";
    	case 5:
    		return  "星期五";
    	case 6:
    		return  "星期六";
    				
    	}
    	return "";
    }

    private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
				case Msg_updateTime:
					String date=(String) msg.obj;
					String[] times=date.split(";");
					if(times.length==3){
						t_time.setText(times[1]);
						
						Integer num= new Integer(times[2]);
						String week=getWeek(num);
						t_date.setText(times[0]+"/"+week);
						//t_week.setText(week);
						
					}
					break;
				case Msg_updateMsg:
					String msgs[]=msg.obj.toString().split("#");
					if(msgs.length>0){
						sharePreferenceUtil.setStringValue(MsgKey1,msgs[0]);
						t_msg1.setText(msgs[0]);
					}
					if(msgs.length>1){
						sharePreferenceUtil.setStringValue(MsgKey2,msgs[1]);
						t_msg2.setText(msgs[1]);
					}
//					if(msgs.length>2){
//						sharePreferenceUtil.setStringValue(MsgKey3,msgs[2]);
//						t_msg3.setText(msgs[2]);
//					}
					
					break;
				case Msg_initMap:
					initBaiDuMap();
					startMapLocation();
					break;
					
			}
			
			
			super.handleMessage(msg);
		}

    };
    
   void stopMapLocation(){
		if(mBMapMan!=null){
			mBMapMan.getLocationManager().removeUpdates(mLocationListener);
			mBMapMan.stop();
		}
   }
    void startMapLocation(){
		if(mBMapMan!=null){
			mBMapMan.getLocationManager().requestLocationUpdates(mLocationListener);
			mBMapMan.getLocationManager().enableProvider(
					MKLocationManager.MK_GPS_PROVIDER);
			mBMapMan.start();
		}
    }
	private void sendMsgToHandler(int what,String dsp){
		Message msg=new Message();
		msg.what=what;
		msg.obj=dsp;
		mHandler.sendMessage(msg);
	}
    
    public void setMsg(String msg){
    	sendMsgToHandler(Msg_updateMsg,msg);
    		
    }

    

    
    
	/**
	 * 
	 * 方法名：initBaiDuMap 功能：初始化百度地图 参数： 创建人：zhangtian 创建时间：2015-08-20
	 */
	private void initBaiDuMap() {
		mBMapMan = new BMapManager(getActivity().getApplicationContext());
		mBMapMan.init("14A97FC2DDF678193F61C19C0A20EA29C49DEF5C", new MKGeneralListener() {
			
			@Override
			public void onGetPermissionState(int arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onGetNetworkState(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		mBMapMan.start();
		initMyLocation();
	}

	/**
	 * 
	 * 方法名：initMyLocation 功能：启动定位 参数： 创建人：zhangtian 创建时间：2015-08-20
	 */
	//ProgressDialog progressDialog=null;
	private void initMyLocation() {
	//	progressDialog = ProgressDialog
		//		.show(context, null, "城市定位中...", true, true);
		mLocationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				if (location != null ) {
					//progressDialog.dismiss();
					//flag = false;
					
					GeoPoint myPt = new GeoPoint(
							(int) (location.getLatitude() * 1e6),
							(int) (location.getLongitude() * 1e6));
					initMapSerach();
					// 将当前坐标转化为地址获取当前城市名称
					mSearch.reverseGeocode(myPt);
				} else {
				}
			}

	
		};
	}

	private void initMapSerach() {
		// 初始化搜索模块，注册事件监听
		mSearch = new MKSearch();
		mSearch.init(mBMapMan, new MKSearchListener() {

			public void onGetPoiResult(MKPoiResult res, int type, int error) {

			}

			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
			}

			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
			}

			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
			}

			public void onGetAddrResult(MKAddrInfo res, int error) {
				if (error != 0 || res == null) {
				} else {
					String city = res.addressComponents.city;
					String pro = res.addressComponents.province;
					if (city != null) {
						//hasGetLocation=true;
						provinceName = pro.substring(0, pro.length() - 1);
						cityName = city.substring(0, city.length() - 1);
						stopMapLocation();//获取地址后断服务
//						progressDialog = ProgressDialog.show(
//								context, null, "查询中...", true,
//								true);
						QueryWeatherTask asyncTask = new QueryWeatherTask();
						asyncTask.execute("");
					} else {
						Toast.makeText(context, "获取数据失败",
								Toast.LENGTH_SHORT).show();
					}
				}
			}

			@Override
			public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {

			}
		});

	}
	private class QueryWeatherTask extends AsyncTask {
		@Override
		protected void onPostExecute(Object result) {
			//progressDialog.dismiss();
			if(result!=null){
				String weatherResult = (String)result;
				Log.d("weather",weatherResult);
				
				try {
					Map<String, String> weatherMap;
					weatherMap=ParseJasonWeather.getInformation(weatherResult);
					todayParse(weatherMap);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				//DataUtil.Alert(WeatherScreen.this,"查无天气信息");
			}
			super.onPostExecute(result);			
		}
			
		@Override
		protected Object doInBackground(Object... params) {
			Log.d("weather","cityName="+cityName);
			return HttpService.getWeather(cityName);
		}
	}
	/**
	 * 
	 * 方法名：todayParse 功能：今天天气 参数：
	 * 
	 * @param weather
	 *            创建人：zhangtian 创建时间：2015-08-18
	 */
	private void todayParse(Map<String, String> weatherMap) {
		if(weatherMap!=null){
			//hasGetWeather=true;
			weather_freq=WEATHER_FREQ_AFTER;
			//Log.e("weather","weatherMap="+weatherMap.toString());
			//SimpleDateFormat    sDateFormat    =   new    SimpleDateFormat("E");       
			//String    week    =    sDateFormat.format(new java.util.Date()); 
			
			if(weatherMap.containsKey("temp")){
				t_temper.setText(weatherMap.get("temp"));
			}
			if(weatherMap.containsKey("now_state")){
				t_weather.setText(weatherMap.get("now_state"));
			}
			if(weatherMap.containsKey("city_name")){
				t_city.setText(weatherMap.get("city_name"));
			}
			
		}
	}
    class ClockTimerTask extends TimerTask{

		@Override
		public void run() {
			curDate.setTime(System.currentTimeMillis());
			
			String date = formatDate.format(curDate);
			int week=curDate.getDay();
			date=date+week;
			//String time = formatTime.format(curDate);
			Message mesg=new Message(); 
			mesg.what=Msg_updateTime;
			mesg.obj=date;
			mHandler.sendMessage(mesg);

		}
    	
    }

	 private class WeatherTimerTask extends TimerTask{

			@Override
			public void run() {

				if(mBMapMan==null){ //先初始化定位
					if(hasNetWork(context)){
						Message mesg1=new Message(); 
						mesg1.what=Msg_initMap;
						mHandler.sendMessage(mesg1);
					}
				}else{  //然后才能更新天气
					if(weather_cnt==weather_freq){
						if(cityName==null)
						cityName="杭州";
						//Log.d("main","!!!!!!!!!Query weather!!!!!!!!!!!!");
						new QueryWeatherTask().execute("");
					}
					weather_cnt=(weather_cnt>weather_freq)?0:++weather_cnt;
				}
			}
			 
		 }
	 
	void cancelTimer(){
		 if(myTimer!=null){
			 myTimer.cancel();
			 myTimer=null;
		 } 
		 if(weatherTimerTask!=null){
			 weatherTimerTask.cancel();
			 weatherTimerTask=null;
		 }
		 if(clockTimerTask!=null){
			 clockTimerTask.cancel();
			 weatherTimerTask=null;
		 } 
	 }
	 
	 void initTimer(){
		 cancelTimer();
		 myTimer=new Timer();

		weatherTimerTask=new WeatherTimerTask();
		clockTimerTask=new ClockTimerTask();
		 myTimer.schedule(weatherTimerTask, 15000,WeatherTimerDuar);
		 myTimer.schedule(clockTimerTask, 10000,TimeTimerDuar);
	 }
		boolean  hasNetWork(Context context){
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo ethInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
			NetworkInfo activeInfo = manager.getActiveNetworkInfo();
			boolean isConnected=mobileInfo.isConnected()|wifiInfo.isConnected()|ethInfo.isConnected();
			
			return isConnected;
		}
	
	@Override
	public void onDestroy() {
		cancelTimer();
		mBMapMan=null;
		super.onDestroy();
	}
    
    
    
}
