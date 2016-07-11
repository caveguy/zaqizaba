package com.tt.util;

import java.util.ArrayList;
import java.util.List;

import com.example.coffemachinev3.R;

import android.content.Context;

public  class Errors{
	
	/*
	 *     <string name="mc_error1">与咖啡机通信超时</string>
    <string name="mc_error2">与辅助机超时</string>
    <string name="mc_error3">卡杯</string>
    
    <string name="mc_error4">机器加热器或ntc错误</string>
    <string name="mc_error5">锅炉温度过高</string>
    <string name="mc_error6">水泵或流量计故障</string>
    <string name="mc_error7">压粉电机过流</string>
    <string name="mc_error8">压粉电机编码器或压粉电机走不到基准位</string>
    <string name="mc_error9">豆槽管道堵塞</string>
    <string name="mc_error10">获取工艺表失败</string>
    
    
    <string name="mc_error21">缺豆</string>
    <string name="mc_error22">粉槽1缺粉</string>
    <string name="mc_error23">粉槽2缺粉</string>
    <string name="mc_error24">粉槽3缺粉</string>
    <string name="mc_error25">缺纸杯</string>
	<string name="mc_error26">缺水</string>
    <string name="mc_error27">未连接服务器</string>
    
	 */
	
	
	
	public static  enum  McError{
		Mc_error1("mc_error1",R.string.mc_error1),Mc_error2("mc_error2",R.string.mc_error2),Mc_error3("mc_error3",R.string.mc_error3),Mc_error4("mc_error4",R.string.mc_error4),Mc_error5("mc_error5",R.string.mc_error5),
		Mc_error6("mc_error6",R.string.mc_error6),Mc_error7("mc_error7",R.string.mc_error7),Mc_error8("mc_error8",R.string.mc_error8),Mc_error9("mc_error9",R.string.mc_error9),Mc_error10("mc_error10",R.string.mc_error10),
		Mc_error21("mc_error21",R.string.mc_error21),Mc_error22("mc_error22",R.string.mc_error22),Mc_error23("mc_error23",R.string.mc_error23),Mc_error24("mc_error24",R.string.mc_error24),Mc_error25("mc_error25",R.string.mc_error25),
		Mc_error26("mc_error26",R.string.mc_error26),Mc_error27("mc_error27",R.string.mc_error27);
		 private final String value;
		 private final int  stringId;
		
        //构造器默认也只能是private, 从而保证构造函数只能在内部使用
		McError(String value,int id) {
            this.value = value;
            this.stringId=id;
        }
        
        public String getValue() {
            return value;
        }
        public String getDetail(Context c) {
        	return c.getString(stringId);
        }
//		public final static String  Mc_error1="mc_error1";
//		public final static String  Mc_error2="mc_error2";
//		public final static String  Mc_error3="mc_error3";
//		public final static String  Mc_error4="mc_error4";
//		public final static String  Mc_error5="mc_error5";
//		public final static String  Mc_error6="mc_error6";
//		public final static String  Mc_error7="mc_error7";
//		public final static String  Mc_error8="mc_error8";
//		public final static String  Mc_error9="mc_error9";
//		public final static String  Mc_error10="mc_error10";
//		public final static String  Mc_error11="mc_error11";
//		public final static String  Mc_error12="mc_error12";
//		public final static String  Mc_error13="mc_error13";
//		public final static String  Mc_error14="mc_error14";
//		public final static String  Mc_error15="mc_error15";
//	
//		public final static String[] Mc_errors={
//			Mc_error1,Mc_error2,Mc_error3,Mc_error4,Mc_error5,
//			Mc_error6,Mc_error7,Mc_error8,Mc_error9,Mc_error10
//			,Mc_error11,Mc_error12,Mc_error13,Mc_error14,Mc_error15
//			
//		};
	}
	private static List<McError> errors=new ArrayList();
	
	public static boolean addError(McError error){
		for(McError e:errors){
			if(e.equals(error)){
				return false;
			}
		}
		errors.add(error);
		return true;
	}

	public static boolean removeError(McError error){
		for(McError e:errors){
			if(e.equals(error)){
				errors.remove(e);
				return true;
			}
		}
		return false;
	}
	public static List<McError>  getErrors(){
		return errors;
	}
	public static List<McError>  getErrorsToServer(){
		List<McError> es=new ArrayList();
		for(McError e:errors){
			if(e.ordinal()<10){
				es.add(e);
			}
		}
		return es;
	}
	
	public static boolean hasError(){
		return errors.isEmpty()?false:true;
	}
	public static String  getErrorsDetails(Context c){
		StringBuilder all = new StringBuilder();
		int i=0;
		for(McError one:errors){
			if(i>0){
				all.append(";");
			}
			all.append(one.getDetail(c));
			i++;
		}
		if(all.length()!=0){
			all.append(".");
		}
		return all.toString();
	}

}