package com.tt.util;



import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ParseJasonWeather {
  final static String log="jason";
	public static Map<String, String> getInformation(String jonString)
			throws Exception {
		//List<Map<String, Object>> all = new ArrayList<Map<String, Object>>();
		Map<String, String> map = new HashMap<String, String>();
	
		JSONObject jsonObject = JSONObject.fromObject(jonString);		
		JSONArray getJsonArray=JSONArray.fromObject(jsonObject.optString("weather"));
		JSONObject weather =getJsonArray.getJSONObject(0);//获取json数组中的第一项  
		if(weather.containsKey("city_name"))
			map.put("city_name", weather.optString("city_name"));
	//	String updatetime=weather.optString("last_update");
	//	updatetime=updatetime.substring(updatetime.indexOf("T")+1, updatetime.indexOf("+"));
		
	//	map.put("last_update", updatetime);
		
		JSONObject now_weather = weather.getJSONObject("now");//获取此刻的天气
		if(now_weather.containsKey("text"))
			map.put("now_state", now_weather.optString("text")); //获取当前天气
		if(now_weather.containsKey("temperature"))
			map.put("now_temp", now_weather.optString("temperature")+"℃"); //获取当前天气
		//map.put("now_humidity", now_weather.optString("humidity")+"%"); //获取当前湿度
		
		//Log.e(log,"now_humidity="+now_weather.optString("humidity"));	
		
//		map.put("wind_direction", now_weather.optString("wind_direction")); //获取当前风向
//		map.put("wind_scale", now_weather.optString("wind_scale")); //获取当前风级
		
		//String now_pm25 = now_weather.getJSONObject("air_quality").getJSONObject("city")
		//		.optString("pm25");//获取pm25
		//map.put("now_pm25", now_pm25);
		

		JSONArray futureArray=JSONArray.fromObject(weather.optString("future"));
		JSONObject today=futureArray.getJSONObject(0);
		String highT=null,lowT=null;
		
		if(today.containsKey("high"))
			highT=today.optString("high");
		if(today.containsKey("low"))
			lowT=today.optString("low");
		//Log.e(log,"temp="+highT+"℃-"+lowT+"℃");
		map.put("temp", lowT+"℃-"+highT+"℃");


		return map;

	}

}
