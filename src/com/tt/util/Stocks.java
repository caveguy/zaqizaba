package com.tt.util;

import android.content.Context;

public  class Stocks{
	public final static String  Bean_max="Bean_max";
	public final static String  Water_max="Water_max";
	public final static String  Cup_max="Cup_max";
	public final static String  Powder1_max="Powder1_max";
	public final static String  Powder2_max="Powder2_max";
	public final static String  Powder3_max="Powder3_max";
	public final static String  Bean_min="Bean_min";
	public final static String  Water_min="Water_min";
	public final static String  Cup_min="Cup_min";
	public final static String  Powder1_min="Powder1_min";
	public final static String  Powder2_min="Powder2_min";
	public final static String  Powder3_min="Powder3_min";
	public final static String  Bean_cur="Bean_cur";
	public final static String  Water_cur="Water_cur";
	public final static String  Cup_cur="Cup_cur";
	public final static String  Powder1_cur="Powder1_cur";
	public final static String  Powder2_cur="Powder2_cur";
	public final static String  Powder3_cur="Powder3_cur";
//private static 	boolean isDebug=false;
//private static boolean dropcupMode=false ;   //杯子模式，false:检查到有杯子就打咖啡，true：落杯后打咖啡


static SharePreferenceUtil myPreferenceUtil=null;

static public int getIntValue(Context context,String name){
		if(myPreferenceUtil==null){
			myPreferenceUtil=new SharePreferenceUtil(context, "stocks");
		}
		return myPreferenceUtil.getIntValue(name);

	}


static	public void setIntValue(Context context,String name ,int value){
		if(myPreferenceUtil==null){
			myPreferenceUtil=new SharePreferenceUtil(context, "stocks");
		}
		myPreferenceUtil.setIntValue(name, value);
	}


}