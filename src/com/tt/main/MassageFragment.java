package com.tt.main;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.coffemachinev3.R;
import com.tt.util.SharePreferenceUtil;

public class MassageFragment extends Fragment {

	TextView t_time,t_date;
	TextView t_msg1,t_msg2;//t_msg3;
	
	Timer clockTime =new Timer();
	Date curDate=null;
	SimpleDateFormat formatDate = new SimpleDateFormat ("yyyy.MM.dd;HH:mm;");
//	SimpleDateFormat formatTime = new SimpleDateFormat ("HH:mm");
	private final int Msg_updateTime=1001;
	private final int Msg_updateMsg=1002;
	SharePreferenceUtil sharePreferenceUtil;
	private final String Tag="MsgFrag";
	private final String MsgKey1="key1";
	private final String MsgKey2="key2";
	private final String MsgKey3="key3";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_msg, container, false);
        sharePreferenceUtil=new SharePreferenceUtil(getActivity(), "Message");
        t_time=(TextView)rootView.findViewById(R.id.t_time);
       // t_week=(TextView)rootView.findViewById(R.id.t_week);
        t_date=(TextView)rootView.findViewById(R.id.t_date);
        t_msg1=(TextView)rootView.findViewById(R.id.t_msg1);
        t_msg2=(TextView)rootView.findViewById(R.id.t_msg2);
      // t_msg3=(TextView)rootView.findViewById(R.id.t_msg3);
        setMessage();
        curDate = new Date(System.currentTimeMillis());//获取当前时间
        
        clockTime.schedule(new ClockTimerTask(), 1000,60*1000);
        return rootView;
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
			}
			
			
			super.handleMessage(msg);
		}

    };
	private void sendMsgToHandler(int what,String dsp){
		Message msg=new Message();
		msg.what=what;
		msg.obj=dsp;
		mHandler.sendMessage(msg);
	}
    
    public void setMsg(String msg){
    	sendMsgToHandler(Msg_updateMsg,msg);
    		
    }

    
    public void  cleanTimer(){
    	if(clockTime!=null){
    		clockTime.cancel();
    		clockTime=null;
    	}
    }
    
    
	@Override
	public void onDestroy() {
		cleanTimer();
		super.onDestroy();
	}
    
    
    
}
