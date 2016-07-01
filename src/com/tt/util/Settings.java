package com.tt.util;

import android.content.Context;

public  class Settings{
private final static String  IsDebugName="isDebug";
private final static String  IsHeatingName="isHeating";
private final static String DropcupMode="dropcupMode";
private final static String NeedBean="needBean";
private final static String CoffeesCnt="coffeesCnt";
//private static 	boolean isDebug=false;
//private static boolean dropcupMode=false ;   //杯子模式，false:检查到有杯子就打咖啡，true：落杯后打咖啡


static SharePreferenceUtil myPreferenceUtil=null;

static public boolean getIsDebug(Context context){
		if(myPreferenceUtil==null){
			myPreferenceUtil=new SharePreferenceUtil(context, "coffee");
		}
		return myPreferenceUtil.getBoolValue(IsDebugName);

	}


static	public void setIsDebug(Context context,boolean is){
		if(myPreferenceUtil==null){
			myPreferenceUtil=new SharePreferenceUtil(context, "coffee");
		}
		myPreferenceUtil.setBoolValue(IsDebugName, is);
	}

static public boolean getIsHeating(Context context){
	if(myPreferenceUtil==null){
		myPreferenceUtil=new SharePreferenceUtil(context, "coffee");
	}
	return myPreferenceUtil.getBoolValue(IsHeatingName);	
}
static	public void setIsHeating(Context context,boolean is){
	if(myPreferenceUtil==null){
		myPreferenceUtil=new SharePreferenceUtil(context, "coffee");
	}
	myPreferenceUtil.setBoolValue(IsHeatingName, is);
}



static	public boolean getDropcupMode(Context context){
		if(myPreferenceUtil==null){
			myPreferenceUtil=new SharePreferenceUtil(context, "coffee");
		}
		return myPreferenceUtil.getBoolValue(DropcupMode);
		
	}
static	public void setDropcupMode(Context context,boolean is){
		if(myPreferenceUtil==null){
			myPreferenceUtil=new SharePreferenceUtil(context, "coffee");
		}
		myPreferenceUtil.setBoolValue(DropcupMode, is);
	}
static	public boolean getNeedBean(Context context){
	if(myPreferenceUtil==null){
		myPreferenceUtil=new SharePreferenceUtil(context, "coffee");
	}
	return myPreferenceUtil.getBoolValue(NeedBean);
	
}
static	public void setNeedBean(Context context,boolean is){
	if(myPreferenceUtil==null){
		myPreferenceUtil=new SharePreferenceUtil(context, "coffee");
	}
	myPreferenceUtil.setBoolValue(NeedBean, is);
}

static public int getCoffees(Context context){
	if(myPreferenceUtil==null){
		myPreferenceUtil=new SharePreferenceUtil(context, "coffee");
	}
	return myPreferenceUtil.getIntValue(CoffeesCnt);
	
}

static	public void setCoffees(Context context,int cnt){
	if(myPreferenceUtil==null){
		myPreferenceUtil=new SharePreferenceUtil(context, "coffee");
	}
	myPreferenceUtil.setIntValue(CoffeesCnt, cnt);
}



}