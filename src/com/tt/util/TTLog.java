package com.tt.util;

import android.util.Log;

public class TTLog {
	private boolean isDebug=true;
	private String Tag="TTLog";
	public TTLog(){
		super();
	}
	public TTLog(String tag,boolean debug){
		isDebug=debug;
		Tag=tag;
	}
	public  void log_i(String out){
		if(isDebug){
			Log.i(Tag,out);
		}
	}
	public  void log_d(String out){
		if(isDebug){
			Log.d(Tag,out);
		}
	}
	public  void log(String out){
		if(isDebug){
			Log.i(Tag,out);
		}
	}
	public  void log_e(String out){
		if(isDebug){
			Log.e(Tag,out);
		}
	}
	public  void log_w(String out){
		if(isDebug){
			Log.w(Tag,out);
		}
	}
}
