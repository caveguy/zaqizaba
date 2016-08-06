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

public class CoffeeFormula {
	//public static String xml_path=Environment.getExternalStorageDirectory().toString()+"/coffeeConfig.xml";
	public static String xml_path="/storage/udisk/coffeeConfig.xml";
	public static String xml_name="coffeeConfig.xml";

	private final static String Tag="CoffeeFormula";
	private  final static String Coffee="coffee";
	private  final static String Coffees="coffees";
	private  final static String Cleans="cleans";
	private  final static String Clean_cnt="cnt";
	private  final static String Clean_time="time";
	private  final static String objiect_temper="temperature";
	private  final static String Ver="serial";
	private  final static String id="id";
	private  final static String name="name";
	private  final static String price="price";
	private  final static String orgPrice="org_price";
	//咖啡机配置
	private  final static String need_coffee="need_coffee";
	private  final static String coffee_powder="coffee_powder";
	private  final static String coffee_water="coffee_water";
	private  final static String coffee_preWater="coffee_preWater";
	//辅助机配置
	private  final static String sugar_level="sugar_level";
	private  final static String ch1R_powder_level="ch1R_powder_level";
	private  final static String ch2L_powder_level="ch2L_powder_level";
	private  final static String ch2R_powder_level="ch2R_powder_level";
	private  final static String ch3L_powder_level="ch3L_powder_level";
	private  final static String ch3R_powder_level="ch3R_powder_level";
	private  final static String ch4L_powder_level="ch4L_powder_level";
	private  final static String ch4R_powder_level="ch4R_powder_level";
	private  final static String ch1_water="ch1_water";
	private  final static String ch2_water="ch2_water";
	private  final static String ch3_water="ch3_water";
	private  final static String ch4_water="ch4_water";

	 MachineTemper coffeeTemper=null;
	List<Coffee> coffees = null;
	List<String> times = null;
	int clean_duration=20;
	int clean_water=20;
	String verSerial=null;
	public CoffeeFormula(Context c) {
		context=c;
		// parse();
	}
	
	
	
	
	public static boolean setName(List<Coffee> list,Integer id,String name){
		for(Coffee coffee:list){
			if(coffee.getId()==id){
				coffee.setName(name);
				return true;
			}
		}
		return false;
	}
	public static boolean setPrice(List<Coffee> list,int id,String price){
		for(Coffee coffee:list){
			if(coffee.getId()==id){
				coffee.setPrice(price);
				return true;
			}
		}
		return false;
	}
	public static boolean setOrgPrice(List<Coffee> list,int id,String price){
		for(Coffee coffee:list){
			if(coffee.getId()==id){
				coffee.setOrgPrice(price);
				return true;
			}
		}
		return false;
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
		Coffee coffee = null;
		//CleanTime time = null;
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
				
				if(Coffees.equals(pullParser.getName())){
					coffees = new ArrayList<Coffee>();
				}
				else if(Cleans.equals(pullParser.getName())){
					times = new ArrayList<String>();
					if(num_value>0)
						clean_duration=new Integer(pullParser.getAttributeValue(0));
					if(num_value>1)
						clean_water=new Integer(pullParser.getAttributeValue(1));
				}
				else if(Ver.equals(pullParser.getName())){
					verSerial=pullParser.nextText();
				}


				else if(objiect_temper.equals(pullParser.getName())){
					int goal=90;
					int backlash=10;
					int min=70;
					if(num_value>0)
						goal = new Integer(pullParser.getAttributeValue(0));
					if(num_value>1)
						backlash = new Integer(pullParser.getAttributeValue(1));
					if(num_value>2)
						min = new Integer(pullParser.getAttributeValue(2));
					if(goal*backlash*min!=0){
						coffeeTemper =new MachineTemper(goal,backlash,min);
					}
					
				}
				else if(Clean_time.equals(pullParser.getName())){
					String value = pullParser.nextText();
					times.add(value);
					//int id = new Integer(pullParser.getAttributeValue(0));
					//time = new CleanTime(value);
				}
				
				else if(Coffee.equals(pullParser.getName())){
					int id = new Integer(pullParser.getAttributeValue(0));
					coffee = new Coffee();
					coffee.setId(id);
					coffee.setorder(order++);
					
				}

				else if(name.equals(pullParser.getName())){
					String name = pullParser.nextText();
					coffee.setName(name);
				}
				else if(price.equals(pullParser.getName())){
					String value = pullParser.nextText();
					coffee.setPrice(value);
				}
				else if(orgPrice.equals(pullParser.getName())){
					String value = pullParser.nextText();
					coffee.setOrgPrice(value);
				}
				else if(need_coffee.equals(pullParser.getName())){
					int value =new Integer(pullParser.nextText());
					coffee.setNeedCoffee(value);
				}
				else if(coffee_powder.equals(pullParser.getName())){
					int value =new Integer(pullParser.nextText());
					coffee.setCoffeePowder(value);
				}
				else if(coffee_water.equals(pullParser.getName())){
					int value =new Integer(pullParser.nextText());
					coffee.setCoffeeWater(value);
				}
				else if(coffee_preWater.equals(pullParser.getName())){
					int value =new Integer(pullParser.nextText());
					coffee.setCoffeePreWater(value);
				}
				else if(sugar_level.equals(pullParser.getName())){
					String name = pullParser.nextText();
					coffee.setSugarLever(name);
				}
				else if(ch1R_powder_level.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setCh1R_powder_level(value);
				}
				else if(ch2L_powder_level.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setCh2L_powder_level(value);
				}
				else if(ch2R_powder_level.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setCh2R_powder_level(value);
				}
				else if(ch3L_powder_level.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setCh3L_powder_level(value);
				}
				else if(ch3R_powder_level.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setCh3R_powder_level(value);
				}
				else if(ch4L_powder_level.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setCh4L_powder_level(value);
				}
				else if(ch4R_powder_level.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setCh4R_powder_level(value);
				}
				else if(ch1_water.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setCh1Water(value);
				}
				else if(ch2_water.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setCh2Water(value);
				}
				else if(ch3_water.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setCh3Water(value);
				}
				else if(ch4_water.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setCh4Water(value);
				}

	
				break;
				
			case XmlPullParser.END_TAG:
				if(Coffee.equals(pullParser.getName())){
					coffees.add(coffee);
					coffee = null;
				}
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
	/**
	 * ��������
	 * @param coffees ����
	 * @param out �������
	 * @throws Exception
	 */
	public  void save(List<Coffee> coffees, OutputStream out) throws Exception{
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(out, "UTF-8");
		serializer.startDocument("UTF-8", true);
		serializer.startTag(null, "coffees");
		for(Coffee coffee : coffees){
			serializer.startTag(null, Coffee);
			serializer.attribute(null, "id", coffee.getId().toString());
			
			serializer.startTag(null, name);
			serializer.text(coffee.getName());
			serializer.endTag(null, name);
			
			serializer.startTag(null,sugar_level);
			serializer.text(coffee.getSugarLever().toString());
			serializer.endTag(null, sugar_level);
			
			serializer.startTag(null,ch1R_powder_level);
			serializer.text(coffee.getCh1R_powder_level().toString());
			serializer.endTag(null, ch1R_powder_level);
			
			serializer.startTag(null,ch2L_powder_level);
			serializer.text(coffee.getCh2L_powder_level().toString());
			serializer.endTag(null, ch2L_powder_level);
			serializer.startTag(null,ch2R_powder_level);
			serializer.text(coffee.getCh2R_powder_level().toString());
			serializer.endTag(null, ch2R_powder_level);
			
			serializer.startTag(null,ch3L_powder_level);
			serializer.text(coffee.getCh3L_powder_level().toString());
			serializer.endTag(null, ch3L_powder_level);
			serializer.startTag(null,ch3R_powder_level);
			serializer.text(coffee.getCh3R_powder_level().toString());
			serializer.endTag(null, ch3R_powder_level);
			serializer.startTag(null,ch4L_powder_level);
			serializer.text(coffee.getCh4L_powder_level().toString());
			serializer.endTag(null, ch4L_powder_level);
			serializer.startTag(null,ch4R_powder_level);
			serializer.text(coffee.getCh4R_powder_level().toString());
			serializer.endTag(null, ch4R_powder_level);
			
			serializer.startTag(null,ch1_water);
			serializer.text(coffee.getCh1Water().toString());
			serializer.endTag(null, ch1_water);
			serializer.startTag(null,ch2_water);
			serializer.text(coffee.getCh2Water().toString());
			serializer.endTag(null, ch2_water);
			serializer.startTag(null,ch3_water);
			serializer.text(coffee.getCh3Water().toString());
			serializer.endTag(null, ch3_water);
			serializer.startTag(null,ch4_water);
			serializer.text(coffee.getCh4Water().toString());
			serializer.endTag(null, ch4_water);
	
			serializer.endTag(null, Coffee);
		}
		serializer.endTag(null, "coffees");
		serializer.endDocument();
		out.flush();
		out.close();
	}
	Context context;
	public void parse() throws Exception{
		InputStream xml=null;
		Log.e("Coffee",xml_path);
		try{
		//	 xml = context.getClass().getClassLoader().getResourceAsStream("coffeeConfig.xml");
			xml=new FileInputStream(xml_path);
		}
		catch(Exception e){
			Log.e(Tag,"getCoffeeFormula error="+e.getMessage());
			 xml = context.getClass().getClassLoader().getResourceAsStream(xml_name);
		}
		
		getConfigs(xml);

	}
	public boolean updateXml(String xml){
		boolean done=false;
		try{
			FileOutputStream fout = new FileOutputStream(xml_path);
			byte [] bytes = xml.getBytes();
			fout.write(bytes);
			fout.close();
			done=true;
		}
		catch(Exception e){
			e.printStackTrace();
			try{
				FileOutputStream fout =context.openFileOutput(xml_name, context.MODE_PRIVATE);
				byte [] bytes = xml.getBytes();
				fout.write(bytes);
				fout.close();
				done=true;
			}
			catch(Exception ee){
				ee.printStackTrace();
			}
		}
		return done;
	}
	
	
	public  List<Coffee>  getCoffeeFormula() throws Exception {
		parse();
		return coffees;
	}
	public  List<String>  getCleanTimes() {
		return times;
		
	}
	public int getClean_duration(){
		return clean_duration;
	}
	public int getClean_water(){
		return clean_water;
	}

	public String getVerSerial(){
		return verSerial;
	}
	
	public  MachineTemper getTemper(){
		return coffeeTemper;
	}
	

}
