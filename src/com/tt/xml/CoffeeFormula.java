package com.tt.xml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

public class CoffeeFormula {
	public static String xml_path=Environment.getExternalStorageDirectory().toString()+"/coffeeConfig.xml";
	//public static String xml_path="/storage/udisk/coffeeConfig.xml";
	
	private final static String Tag="CoffeeFormula";
	private  final static String Coffee="coffee";
	private  final static String Coffees="coffees";
	private  final static String Cleans="cleans";
	private  final static String Clean_cnt="cnt";
	private  final static String Clean_time="time";
	private  final static String objiect_temper="temperature";
	private  final static String id="id";
	private  final static String name="name";
	private  final static String price="price";
	//咖啡机配置
	private  final static String need_coffee="need_coffee";
	private  final static String coffee_powder="coffee_powder";
	private  final static String coffee_water="coffee_water";
	private  final static String coffee_preWater="coffee_preWater";
	//辅助机配置
	private  final static String sugar_level="sugar_level";
	private  final static String ch1r_powder_level="ch1r_powder_level";
	private  final static String ch2l_powder_level="ch2l_powder_level";
	private  final static String ch2r_powder_level="ch2r_powder_level";
	private  final static String ch3l_powder_level="ch3l_powder_level";
	private  final static String ch3r_powder_level="ch3r_powder_level";
	private  final static String ch4l_powder_level="ch4l_powder_level";
	private  final static String ch4r_powder_level="ch4r_powder_level";
	private  final static String ch1_water="ch1_water";
	private  final static String ch2_water="ch2_water";
	private  final static String ch3_water="ch3_water";
	private  final static String ch4_water="ch4_water";

	 MachineTemper coffeeTemper=null;
	List<Coffee> coffees = null;
	List<CleanTime> times = null;
	int clean_duration=0;
	
	public CoffeeFormula(Context c) throws Exception{
		context=c;
		 parse();
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
	/**
	 * 
	 * ��ȡ����
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public void getConfigs(InputStream xml) throws Exception{
		
		Coffee coffee = null;
		CleanTime time = null;
		XmlPullParser pullParser = Xml.newPullParser();
		pullParser.setInput(xml, "UTF-8");//ΪPull����������Ҫ������XML����
		int event = pullParser.getEventType();
		while(event != XmlPullParser.END_DOCUMENT){
			switch (event) {
			
			
//			case XmlPullParser.START_DOCUMENT:
//				coffees = new ArrayList<Coffee>();
//				break;
				
			case XmlPullParser.START_TAG:
				if(Coffees.equals(pullParser.getName())){
					coffees = new ArrayList<Coffee>();
				}
				else if(Cleans.equals(pullParser.getName())){
					times = new ArrayList<CleanTime>();
					clean_duration=new Integer(pullParser.getAttributeValue(0));
				}
				else if(objiect_temper.equals(pullParser.getName())){
					int goal = new Integer(pullParser.getAttributeValue(0));
					int backlash = new Integer(pullParser.getAttributeValue(1));
					int min = new Integer(pullParser.getAttributeValue(2));
					if(goal*backlash*min!=0){
						coffeeTemper =new MachineTemper(goal,backlash,min);
					}
					
				}
				else if(Clean_time.equals(pullParser.getName())){
					String value = pullParser.nextText();
					//int id = new Integer(pullParser.getAttributeValue(0));
					time = new CleanTime(value);
				}
				
				else if(Coffee.equals(pullParser.getName())){
					int id = new Integer(pullParser.getAttributeValue(0));
					coffee = new Coffee();
					coffee.setId(id);
				}

				else if(name.equals(pullParser.getName())){
					String name = pullParser.nextText();
					coffee.setName(name);
				}
				else if(price.equals(pullParser.getName())){
					String value = pullParser.nextText();
					coffee.setPrice(value);
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
				else if(ch1r_powder_level.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setCh1r_powder_lever(value);
				}
				else if(ch2l_powder_level.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setCh2l_powder_lever(value);
				}
				else if(ch2r_powder_level.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setCh2r_powder_lever(value);
				}
				else if(ch3l_powder_level.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setCh3l_powder_lever(value);
				}
				else if(ch3r_powder_level.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setCh3r_powder_lever(value);
				}
				else if(ch4l_powder_level.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setCh4l_powder_lever(value);
				}
				else if(ch4r_powder_level.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setCh4r_powder_lever(value);
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
				else if(Clean_time.equals(pullParser.getName())){
					times.add(time);
					time = null;
				}
				break;
			}
			event = pullParser.next();
		}
	//	return coffees;
	}
	/**
	 * ��������
	 * @param persons ����
	 * @param out �������
	 * @throws Exception
	 */
	public  void save(List<Coffee> persons, OutputStream out) throws Exception{
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(out, "UTF-8");
		serializer.startDocument("UTF-8", true);
		serializer.startTag(null, "coffees");
		for(Coffee person : persons){
			serializer.startTag(null, Coffee);
			serializer.attribute(null, "id", person.getId().toString());
			
			serializer.startTag(null, name);
			serializer.text(person.getName());
			serializer.endTag(null, name);
			
			serializer.startTag(null,sugar_level);
			serializer.text(person.getSugarLever().toString());
			serializer.endTag(null, sugar_level);
			
			serializer.startTag(null,ch1r_powder_level);
			serializer.text(person.getCh1r_powder_lever().toString());
			serializer.endTag(null, ch1r_powder_level);
			
			serializer.startTag(null,ch2l_powder_level);
			serializer.text(person.getCh2l_powder_lever().toString());
			serializer.endTag(null, ch2l_powder_level);
			serializer.startTag(null,ch2r_powder_level);
			serializer.text(person.getCh2r_powder_lever().toString());
			serializer.endTag(null, ch2r_powder_level);
			
			serializer.startTag(null,ch3l_powder_level);
			serializer.text(person.getCh3l_powder_lever().toString());
			serializer.endTag(null, ch3l_powder_level);
			serializer.startTag(null,ch3r_powder_level);
			serializer.text(person.getCh3r_powder_lever().toString());
			serializer.endTag(null, ch3r_powder_level);
			serializer.startTag(null,ch4l_powder_level);
			serializer.text(person.getCh4l_powder_lever().toString());
			serializer.endTag(null, ch4l_powder_level);
			serializer.startTag(null,ch4r_powder_level);
			serializer.text(person.getCh4r_powder_lever().toString());
			serializer.endTag(null, ch4r_powder_level);
			
			serializer.startTag(null,ch1_water);
			serializer.text(person.getCh1Water().toString());
			serializer.endTag(null, ch1_water);
			serializer.startTag(null,ch2_water);
			serializer.text(person.getCh2Water().toString());
			serializer.endTag(null, ch2_water);
			serializer.startTag(null,ch3_water);
			serializer.text(person.getCh3Water().toString());
			serializer.endTag(null, ch3_water);
			serializer.startTag(null,ch4_water);
			serializer.text(person.getCh4Water().toString());
			serializer.endTag(null, ch4_water);
	
			serializer.endTag(null, Coffee);
		}
		serializer.endTag(null, "coffees");
		serializer.endDocument();
		out.flush();
		out.close();
	}
	Context context;
	void parse() throws Exception{
		InputStream xml=null;
		Log.e("Coffee",xml_path);
		try{
		//	 xml = context.getClass().getClassLoader().getResourceAsStream("coffeeConfig.xml");
			xml=new FileInputStream(xml_path);
		}
		catch(Exception e){
			Log.e(Tag,"getCoffeeFormula error="+e.getMessage());
			 xml = context.getClass().getClassLoader().getResourceAsStream("coffeeConfig.xml");
		}
		
		getConfigs(xml);

	}
	
	
	
	public  List<Coffee>  getCoffeeFormula() {
		return coffees;
	}
	public  List<CleanTime>  getCleanTimes() {
		return times;
		
	}
	
	public  MachineTemper getTemper(){
		return coffeeTemper;
	}
	

}
