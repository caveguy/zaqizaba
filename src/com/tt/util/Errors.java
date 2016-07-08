package com.tt.util;

import java.util.ArrayList;
import java.util.List;

import com.example.coffemachinev3.R;

import android.content.Context;

public  class Errors{
	public final static String  Mc_error1="mc_error1";
	public final static String  Mc_error2="mc_error2";
	public final static String  Mc_error3="mc_error3";
	public final static String  Mc_error4="mc_error4";
	public final static String  Mc_error5="mc_error5";
	public final static String  Mc_error6="mc_error6";
	public final static String  Mc_error7="mc_error7";
	public final static String  Mc_error8="mc_error8";
	public final static String  Mc_error9="mc_error9";
	public final static String  Mc_error10="mc_error10";
	public final static String  Mc_error11="mc_error11";
	public final static String  Mc_error12="mc_error12";
	public final static String  Mc_error13="mc_error13";
	public final static String  Mc_error14="mc_error14";
	public final static String  Mc_error15="mc_error15";

    
	private static List<String> errors=new ArrayList();
	
	public static void addError(String error){
		for(String e:errors){
			if(e.equals(error)){
				return;
			}
		}
		errors.add(error);
	}

	public static void removeError(String error){
		for(String e:errors){
			if(e.equals(error)){
				errors.remove(e);
			}
		}
	}
	public static List<String>  getErrors(){
		return errors;
	}
	public String getErrorDetail(Context context,String error){
		String detail=null;
		switch(error){
		case Mc_error1:
			detail=context.getString(R.string.mc_error1);
			break;
		case Mc_error2:
			detail=context.getString(R.string.mc_error2);
			break;
		case Mc_error3:
			detail=context.getString(R.string.mc_error3);
			break;
		case Mc_error4:
			detail=context.getString(R.string.mc_error4);
			break;
		case Mc_error5:
			detail=context.getString(R.string.mc_error5);
			break;
		case Mc_error6:
			detail=context.getString(R.string.mc_error6);
			break;
		case Mc_error7:
			detail=context.getString(R.string.mc_error7);
			break;
		case Mc_error8:
			detail=context.getString(R.string.mc_error8);
			break;
		case Mc_error9:
			detail=context.getString(R.string.mc_error9);
			break;
		case Mc_error10:
			detail=context.getString(R.string.mc_error10);
			break;
		case Mc_error11:
			detail=context.getString(R.string.mc_error11);
			break;
		case Mc_error12:
			detail=context.getString(R.string.mc_error12);
			break;
		case Mc_error13:
			detail=context.getString(R.string.mc_error13);
			break;
		case Mc_error14:
			detail=context.getString(R.string.mc_error14);
			break;
		case Mc_error15:
			detail=context.getString(R.string.mc_error15);
			break;
			default:
				detail=context.getString(R.string.unknowError);	
		}
		return detail;
	}
}