package com.tt.xml;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DebugFinder {
	//public static String xml_path=Environment.getExternalStorageDirectory().toString()+"/coffeeConfig.xml";
	public static String xml_path="/storage/udisk/debug.xml";


	private final static String Tag="coffeedebug";
	private  final static String PassWord="debug";
	private  final static String Pd="coffeedebug";
	String password="";
	
	public DebugFinder(Context c) {
		context=c;
		parse();
	}
	
	

	/**
	 * 
	 * ��ȡ����
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public void getConfigs(InputStream xml) throws Exception{
		int order=0;

		XmlPullParser pullParser = Xml.newPullParser();
		pullParser.setInput(xml, "UTF-8");//ΪPull����������Ҫ������XML����
		int event = pullParser.getEventType();
		while(event != XmlPullParser.END_DOCUMENT){
			switch (event) {
			
			
//			case XmlPullParser.START_DOCUMENT:
//				coffees = new ArrayList<Coffee>();
//				break;
				
			case XmlPullParser.START_TAG:
				int num_value=pullParser.getAttributeCount();
				
				if(PassWord.equals(pullParser.getName())){
					password=pullParser.nextText();
				}
				

	
				break;
				
			case XmlPullParser.END_TAG:
//				else if(Clean_time.equals(pullParser.getName())){
//					times.add(time);
//					time = null;
//				}
				break;
			}
			event = pullParser.next();
		}
	//	return coffees;
	}

	Context context;
	public void parse() {
		InputStream xml=null;
		Log.e("Coffee",xml_path);
		try{
		//	 xml = context.getClass().getClassLoader().getResourceAsStream("coffeeConfig.xml");
			xml=new FileInputStream(xml_path);
			getConfigs(xml);
		}
		catch(Exception e){
			Log.e(Tag,"parse error="+e.getMessage());
		//	 xml = context.getClass().getClassLoader().getResourceAsStream(xml_name);
		}
		
		

	}

	

	
	public  boolean  isDebugMode(){
		return Pd.equals(password);
	}
	

}
