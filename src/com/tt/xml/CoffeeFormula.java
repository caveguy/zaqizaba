package com.tt.xml;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.util.Log;
import android.util.Xml;


public class CoffeeFormula {
	
	private static String objiect="coffee";
	private  static String id="id";
	private  static String name="name";
	private  static String need_coffee="need_coffee";
	private  static String sugar_level="sugar_level";
	private  static String sugar_preWater="sugar_preWater";
	private  static String sugar_water="sugar_water";
	private  static String milk_level="milk_level";
	private  static String milk_preWater="milk_preWater";
	private  static String milk_water="milk_water";
	private  static String chocolate_level="chocolate_level";
	private  static String chocolate_preWater="chocolate_preWater";
	private  static String chocolate_water="chocolate_water";
	
	
	
	
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
	public static List<Coffee> getPersons(InputStream xml) throws Exception{
		List<Coffee> coffees = null;
		Coffee coffee = null;
		XmlPullParser pullParser = Xml.newPullParser();
		pullParser.setInput(xml, "UTF-8");//ΪPull����������Ҫ������XML����
		int event = pullParser.getEventType();
		while(event != XmlPullParser.END_DOCUMENT){
			switch (event) {
			case XmlPullParser.START_DOCUMENT:
				coffees = new ArrayList<Coffee>();
				break;
				
			case XmlPullParser.START_TAG:
				if(objiect.equals(pullParser.getName())){
					int id = new Integer(pullParser.getAttributeValue(0));
					coffee = new Coffee();
					coffee.setId(id);
				}
				else if(name.equals(pullParser.getName())){
					String name = pullParser.nextText();
					coffee.setName(name);
				}
				else if(need_coffee.equals(pullParser.getName())){
					int value =new Integer(pullParser.nextText());
					coffee.setNeedCoffee(value);
				}
				else if(sugar_level.equals(pullParser.getName())){
					String name = pullParser.nextText();
					coffee.setSugarLever(name);
				}
				else if(sugar_preWater.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setSugarPreWater(value);
				}
				else if(sugar_water.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setSugarWater(value);
				}
				else if(milk_level.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setMilkLever(value);
				}
				else if(milk_preWater.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setMilkPreWater(value);
				}
				else if(milk_water.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setMilkWater(value);
				}
				else if(chocolate_level.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setChocolateLever(value);
				}
				else if(chocolate_preWater.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setChocolatePreWater(value);
				}
				else if(chocolate_water.equals(pullParser.getName())){
					int value = new Integer(pullParser.nextText());
					coffee.setChocolateWater(value);
				}
	
				break;
				
			case XmlPullParser.END_TAG:
				if(objiect.equals(pullParser.getName())){
					coffees.add(coffee);
					coffee = null;
				}
				break;
			}
			event = pullParser.next();
		}
		return coffees;
	}
	/**
	 * ��������
	 * @param persons ����
	 * @param out �������
	 * @throws Exception
	 */
	public static void save(List<Coffee> persons, OutputStream out) throws Exception{
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(out, "UTF-8");
		serializer.startDocument("UTF-8", true);
		serializer.startTag(null, "persons");
		for(Coffee person : persons){
			serializer.startTag(null, "person");
			serializer.attribute(null, "id", person.getId().toString());
			
			serializer.startTag(null, "name");
			serializer.text(person.getName());
			serializer.endTag(null, "name");
			
			serializer.startTag(null,sugar_level);
			serializer.text(person.getSugarLever().toString());
			serializer.endTag(null, sugar_level);
			serializer.startTag(null,sugar_preWater);
			serializer.text(person.getSugarPreWater().toString());
			serializer.endTag(null, sugar_preWater);
			serializer.startTag(null,sugar_water);
			serializer.text(person.getSugarWater().toString());
			serializer.endTag(null, sugar_water);
			serializer.startTag(null,milk_level);
			serializer.text(person.getMilkLever().toString());
			serializer.endTag(null, milk_level);
			serializer.startTag(null,milk_preWater);
			serializer.text(person.getMilkPreWater().toString());
			serializer.endTag(null, milk_preWater);
			serializer.startTag(null,milk_water);
			serializer.text(person.getMilkWater().toString());
			serializer.endTag(null, milk_water);
			
			serializer.endTag(null, "person");
		}
		serializer.endTag(null, "persons");
		serializer.endDocument();
		out.flush();
		out.close();
	}
	
	
	public static List<Coffee>  getCoffeeFormula(Context contex) throws Exception{
		InputStream xml = contex.getClass().getClassLoader().getResourceAsStream("coffee.xml");
		return CoffeeFormula.getPersons(xml);
//		for(Coffee person : persons){
//			Log.i(TAG, person.toString());
//		}
	}
}
