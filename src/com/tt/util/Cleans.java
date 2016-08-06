package com.tt.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public  class Cleans {
   private final String IntentStr="android.tt.action.alarm_clean";
	private final String Tag="Cleans";
	private static final int INTERVAL = 1000 * 60 * 60 * 24;// 24h
	private final String Key_time="clean_time";
	private final String Key_which="which";
	AlarmReceiver alarmReceiver=null;
	Context context=null;
	private  int cleanCnt=0;
	private  int duratuon=20;
	private  int water_ml=20;
	private int all_Alarm=0;
//	private  int which_alram=0;
    public Cleans(Context c){
		context=c;
		registerRebootReceiver();
	}
	public  void  setDuaration(int du){
		duratuon=du;
	}
	public  void  setWater(int ml){
		water_ml=ml;
	}
	public  void  setTimes(List<String> times){
		int i=0;
		all_Alarm=times.size();
		for(String time :times){
			setAlarmReboot( context, time,i++);
		}
	}

	public  boolean  dealClean(){
		Log.i(Tag, "cleanCnt="+cleanCnt+" duratuon="+duratuon);
		if((++cleanCnt)%duratuon==0){
			return true;
		}
		return false;
	}
	public  int getWater(){
		return  water_ml;
	}
	///////////////////////回调接口////////////////////////////////

	CleanCallBack callBack=null;
	public  void setCallBack(CleanCallBack call) {
		// TODO Auto-generated method stub
		callBack = call;
	}

	public interface CleanCallBack {

		void onClean();

	}

	private void cleanCallBack(){
		Log.e(Tag,"!!!!clickCallBack");
		if(callBack!=null)
			callBack.onClean();
	}
	void registerRebootReceiver(){
		alarmReceiver = new AlarmReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(IntentStr);
		context.registerReceiver(alarmReceiver, filter);
	}



	public class AlarmReceiver extends BroadcastReceiver {
		private String TAG="AlarmReceiver";
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String time=intent.getStringExtra(Key_time);
			int which=intent.getIntExtra(Key_which,0);

			if(isTime(time)&&which<all_Alarm){
				Log.i(TAG,"in time");
				cleanCallBack();
			}


		}


		private boolean isTime(String time){
			//	Calendar calendar = Calendar.getInstance();
			Date nowDate=new Date(System.currentTimeMillis());
			int nowHour=nowDate.getHours();
			int nowMinute=nowDate.getMinutes();
			int nowSecond=nowDate.getSeconds();

			long allInSecond=nowHour*3600+nowMinute*60+nowSecond;
			//long setTimeLong=calendar.getTimeInMillis();;
			int hour=0;
			int minute=0;
			int second=0;
			if(time==null){
				return false;
			}
			String subTime[] =time.split(":");
			if(subTime.length==0)
				return false;

			if(subTime.length>0){
				hour=new Integer(subTime[0]);
			}
			if(subTime.length>1){
				minute=new Integer(subTime[1]);
			}
			if(subTime.length>2){
				second=new Integer(subTime[2]);
			}
			long allInSecond_set=hour*3600+minute*60+second;

			if(Math.abs(allInSecond-allInSecond_set)<120){
				return true;
			}
			return false;
		}


	}
	 void  cancelAlarm(int which){
		Intent intent = new Intent();
		intent.setAction(IntentStr);
		//			Intent intent = new Intent(this, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context.getApplicationContext(),
				which, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarm = (AlarmManager) context.getApplicationContext()
				.getSystemService(Context.ALARM_SERVICE);
		alarm.cancel(sender);
	}

	void cancelAlarm(){
		for(int i=0;i<all_Alarm;i++){
			cancelAlarm(i);
		}
	}


	 long    getAlarmTime(String time){


		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		Date nowDate=new Date(System.currentTimeMillis());
		int nowHour=nowDate.getHours();
		int nowMinute=nowDate.getMinutes();
		int nowSecond=nowDate.getSeconds();
		long setTimeLong=calendar.getTimeInMillis();

		int hour=0;
		int minute=0;
		int second=0;
		String subTime[] =time.split(":");
		if(subTime.length==0){
			Log.i(Tag,"get alarmTime failed!");
			return -1;
		}

		if(subTime.length>0){
			hour=new Integer(subTime[0]);
		}
		if(subTime.length>1){
			minute=new Integer(subTime[1]);
		}
		if(subTime.length>2){
			second=new Integer(subTime[2]);
		}

		Log.i(Tag, "getAlarmTime set  hour="+hour+"minute="+minute+"second="+second);
		Log.i(Tag, "getAlarmTime nowHour="+nowHour+"nowMinute="+nowMinute+"nowSecond="+nowSecond);

		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);

		setTimeLong=calendar.getTimeInMillis();
		Log.i(Tag, "setTimeLong ="+setTimeLong);
		if(hour<nowHour){ //设置的时间在过去，就加上一天
			setTimeLong+= INTERVAL;
		}else if(hour==nowHour){
			if(minute<nowMinute){
				setTimeLong+= INTERVAL;
			}else if(minute==nowMinute){
				if(second <nowSecond){
					setTimeLong+= INTERVAL;
				}
			}
		}
		Log.i(Tag, "setTimeLong after ="+setTimeLong);
		return setTimeLong;
	}


	public boolean  setAlarmReboot(Context context,String time,int which){
		Intent intent = new Intent();
		intent.setAction(IntentStr);

		intent.putExtra(Key_time, time);
		intent.putExtra(Key_which, which);
		PendingIntent sender = PendingIntent.getBroadcast(context.getApplicationContext(),
				which, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		// Schedule the alarm!
		AlarmManager alarm = (AlarmManager) context.getApplicationContext()
				.getSystemService(Context.ALARM_SERVICE);

		long setTimeLong=getAlarmTime(time);
		if(setTimeLong==-1){
			Log.i(Tag,"setAlarmReboot failed!");
			return false;
		}
		alarm.setRepeating(AlarmManager.RTC, setTimeLong,INTERVAL, sender);
		//alarm.setExact(AlarmManager.RTC, setTimeLong, sender);
		return true;

	}
}