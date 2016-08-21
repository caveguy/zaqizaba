package com.tt.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	 
	public SharePreferenceUtil(Context context, String file) {
	 
	sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
	editor = sp.edit();
	 
	}

	public void setStringValue(String key,String value) {
	 
	// TODO Auto-generated method stub
	editor.putString(key, value);
	editor.commit();
	 
	}
	
	public String getStringValue(String key) { 
		if(sp.contains(key))
			return sp.getString(key, "");
		else 
			return null;
		
	}
	public String getStringValue(String key,String def) { 
		if(sp.contains(key))
			return sp.getString(key, def);
		else 
			return null;
		
	}
	public void setIntValue(String key,int value) {
	 
	// TODO Auto-generated method stub
	editor.putInt(key, value);
	editor.commit();
	 
	}

	public int getIntValue(String key) {
		if(sp.contains(key))
			return sp.getInt(key, 0);
		else {
			return 0;
		}
	}
	 
	public void setBoolValue(String key,boolean value) {
		 
	// TODO Auto-generated method stub
	editor.putBoolean(key, value);
	editor.commit();
	 
	}

	public boolean getBoolValue(String key) { 
		if(sp.contains(key))
			return sp.getBoolean(key, false);
		else {
			return false;
		}
	}
	 
	}