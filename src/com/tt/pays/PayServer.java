package com.tt.pays;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.loopj.android.http.RequestParams;
import com.tt.httpUtils.AsyncHttp;
import com.tt.httpUtils.HttpCallback;
import com.tt.util.GetMacAddress;
import com.tt.util.JsonHelper;
/**
 * 服务器及交易处理
 * 创建时间：2016-07-01
 *
 */
public class PayServer {
	private ServerCallback callback=null;
	private Context context=null;
	private String apkVer=null;
	private String ctlVer=null;
	private String machineId=null;
	private String machine_key=null;
	
	private String goodsNum=null;
	private String techSerial=null;
	private String textSerial=null;
	private String stocks=null;
	private String errors=null;
	private String zfbTradeno=null;
	private String weixinTradeno=null;
	private String cur_goodId=null;
	private String cur_price=null;
	private String buyerid=null;
	private String payType="";
	private boolean isLogin=false;
	private final String PayType_zfb="alipay";
	private final String PayType_weixin="weixin";
	boolean inPayQuery=false;
//	boolean paySuccess=false;
	/*
	 * 
	 2.1 注册                register
	 2.2 心跳                heart
	 2.3 取得工艺参数         （未完成）
	 2.4 取得支付宝二维码字符串   alipayready
	 2.5 支付宝支付是否成功      alipaystatus
	 2.6 取得微信支付二维码字符串 weixinpayready
	 2.7 微信支付是否成功       weixinpaystatus
	 2.8 上传销售数据          saledata
	 2.9 获取文本信息         （未完成）
	 2.10 查看新版本           lastversion
	 */
	private final String url_comm="http://114.55.128.131/blservice/";
	private final String url_extra_login="register";
	private final String url_extra_heartbeat="heart";
	private final String url_extra_zfb="alipayready";
	private final String url_extra_weixin="weixinpayready";
	private final String url_extra_zfb_state="alipaystatus";
	private final String url_extra_weixin_state="weixinpaystatus";
	private final String url_extra_finish="saledata";
	private final String url_extra_tech="";
	private final String url_extra_text="";
	private final String url_extra_ver="lastversion";
	
	private final String Index_deviceid="deviceid";
	private final String Index_time="time";
	private final String Index_random="rstr";
	private final String Index_sign="sign";
	private final String Index_apkver="apkver";
	private final String Index_ctlver="controllerver";
	private final String Index_coffeeNum="coffeetype";
	private final String Index_techserial="techserial";
	private final String Index_textserial="textserial";
	private final String Index_servertechserial="servertechserial";
	private final String Index_servertextserial="servertextserial";
	private final String Index_stock="stock";
	private final String Index_error="errors";
	private final String Index_code="code";
	private final String Index_msg="msg";
	private final String Index_goodsId="goodsId";
	private final String Index_price="price";
	private final String Index_outTradeno="outtradeno";
	private final String Index_tradeState="tradestatus";
	private final String Index_saleTime="saletime";
	private final String Index_payType="paytype";
	private final String Index_qrCode="msg";
	private final String Index_buyerid="buyerid";
	private final String Index_oneyuan="oneyuan";
	private final String Index_fivejiao="fivejiao";
	private final String Index_bills="bills";
	private final String Index_coins="coins";

	private final int Duration_beat=1000*60*3;
	private final String Tag="BLService===";
//	private final byte Query_zfb=0x01;
//	private final byte Query_weixin=0x02;
//
//	
//	private byte query_what=0;
	
	private Timer serverTimer=null;
	private BeatTask beatTask=null;
	private ZfbPayStateTask zfbPayStateTask=null;
	private WeixinPayStateTask weixinPayStateTask=null;
	Handler myHandler=new Handler();
	
	class BeatTask extends TimerTask{
		
		@Override
		public void run() {
			myHandler.post(heatBeatRunable);	
		}
		
	};
	
	Runnable heatBeatRunable=new Runnable(){

		@Override
		public void run() {
			if(isLogin)
				heatBeat();
			else{
				login();
			}
		}
		
	};
	Runnable queryZfbRunable=new Runnable(){
		
		@Override
		public void run() {
			queryZfbState(); 
			
		}
		
	};
	Runnable queryWeixinRunable=new Runnable(){
		
		@Override
		public void run() {
			queryWeixinState();
			
		}
		
	};
	
	
	class ZfbPayStateTask extends TimerTask{
		boolean isRun=false;
		@Override
		public void run() {
         		if(isRun){
         			myHandler.post(queryZfbRunable);	    	
         		}
		}
	};
	class WeixinPayStateTask extends TimerTask{
		boolean isRun=false;
		@Override
		public void run() {
			if(isRun){
				myHandler.post(queryWeixinRunable);	
			}
		}
		
	};
	void startBeatTask(){
		if(serverTimer==null){
			serverTimer=new Timer();
		}

		if(beatTask==null){
			beatTask=new BeatTask();
			serverTimer.schedule(beatTask, 500, Duration_beat);	
		}
	}
	void startZfbPayStateTask(){
		if(!inPayQuery)
			return;
		if(serverTimer==null){
			serverTimer=new Timer();
		}
		if(zfbPayStateTask!=null){
			zfbPayStateTask.isRun=false;
			if(zfbPayStateTask.cancel())
				zfbPayStateTask=null;
		}
		zfbPayStateTask=new ZfbPayStateTask();
		zfbPayStateTask.isRun=true;
		serverTimer.schedule(zfbPayStateTask, 1000);	
		
	}
	void startWeixinPayStateTask(){
		if(!inPayQuery)
			return;
		if(serverTimer==null){
			serverTimer=new Timer();
		}
		if(weixinPayStateTask!=null){
			weixinPayStateTask.isRun=false;
			if(weixinPayStateTask.cancel())
				weixinPayStateTask=null;
		}
		weixinPayStateTask=new WeixinPayStateTask();
		weixinPayStateTask.isRun=true;
		serverTimer.schedule(weixinPayStateTask, 1000);	
	}
	void cancelBeatTask(){
		if(beatTask!=null){
			if(beatTask.cancel())
			beatTask=null;
		}
	}

	
	boolean dealCommBack(Map map ,StringBuffer  msg){
		if(map.containsKey(Index_code)){
			String rcode=(String) map.get(Index_code);
			if(rcode.equals("1")){
				return true;
			}else{
				if(map.containsKey(Index_msg)){
					msg.append((String) map.get(Index_msg));
				}
			}
		}
		return false;
	}
	Map dealCommBack(int code,JSONObject response ,StringBuffer  msg){
		boolean success=false;
		Map map=null;
		if (code == 200) {
			try {
				Log.i(Tag, response.toString());
				map=JsonHelper.toMap(response.toString());
				if(map!=null){
					if(dealCommBack(map , msg)){
						success=true;
					}
				}
			} catch (JSONException e) {
				msg.append(e.getMessage());
			}
		}else{
			msg.append(code);
		}
		if(!success){
			map=null;
		}
		
		return map;
	}
	void onBeatSuccess(Map map ){

		if(map.containsKey(Index_servertechserial)){
			String temp=(String) map.get(Index_servertechserial);
			if(!temp.equals(techSerial)){
				techSerial=temp;
				askTechXml(techSerial);
				//callback.onHaveNewTech(techSerial);
			}
			
		}
		if(map.containsKey(Index_servertextserial)){
			String temp=(String) map.get(Index_servertextserial);
			if(!temp.equals(textSerial)){
				textSerial=temp;
				askText(textSerial);
				//callback.onHaveNewTech(textSerial);
			}
			
		}
	}

	
	void onGetZfbQrSuccess(Map map ){
		Log.i(Tag,"onGetZfbQrSuccess type======== ");
		if(map.containsKey(Index_qrCode)){
			String temp=(String) map.get(Index_qrCode);
			if(callback!=null){
				callback.onGetZfbQrCode(temp);
			}	
		}
		if(map.containsKey(Index_outTradeno)){
			zfbTradeno =(String) map.get(Index_outTradeno);
		}
		
	}
	void onGetWeixinQrSuccess(Map map ){
		Log.i(Tag,"onGetWeixinQrSuccess type======== ");
		
		if(map.containsKey(Index_qrCode)){
			String temp=(String) map.get(Index_qrCode);
			if(callback!=null){
				callback.onGetWeixinQrCode(temp);
			}	
		}
		if(map.containsKey(Index_outTradeno)){
			weixinTradeno =(String) map.get(Index_outTradeno);
		}
	}
	boolean onzfbPayState(Map map ){
		if(map.containsKey(Index_tradeState)){
			String temp=(String) map.get(Index_tradeState);
			
			if(temp.equals("success")){
				payType=PayType_zfb;
				if(map.containsKey(Index_buyerid)){
					buyerid=(String) map.get(Index_buyerid);
				}
				if(callback!=null){
					callback.onPaySuccess(PayType_zfb,buyerid);
				}
				return true;
			}
		}
		return false;
	}
	boolean onWeixinPayState(Map map ){
		if(map.containsKey(Index_tradeState)){
			String temp=(String) map.get(Index_tradeState);
			
			if(temp.equals("success")){
				payType=PayType_weixin;
				if(map.containsKey(Index_buyerid)){
					buyerid=(String) map.get(Index_buyerid);
				}
				if(callback!=null){
					callback.onPaySuccess(PayType_weixin,buyerid);
				}
				return true;
			}	
		}
		return false;
	}
	
	private void onLoginFailed(String msg){
		isLogin=false;
		if(callback!=null)
			callback.onLoginFailed(msg);
	}
	private void onLoginSuccess(){
		isLogin=true;
		if(callback!=null)
			callback.onLoginSuccess();
	}
	private void onPostSaleSuccess(){
		  Log.e(Tag, "onPostSaleSuccess!!!");
	}
	private void onHeatbeatFailed(String msg){
		  Log.e(Tag, "onHeatbeatFailed!!!");
	}
	private void onGetZfbQrFailed(String msg){
		  Log.e(Tag, "onGetZfbQrFailed!!!");
	}
	private void onGetWeixinQrFailed(String msg){
		 Log.e(Tag, "onGetWeixinQrFailed!!!");
	}
	private void onGetZfbPayStateFailed(String msg){
		Log.e(Tag, "onGetZfbPayStateFailed!!!");
	}
	private void onGetWeixinPayStateFailed(String msg){
		Log.e(Tag, "onGetWeixinPayStateFailed!!!");
	}
	private void onPostSaleFailed(String msg){
		Log.e(Tag, "onPostSaleFailed!!!");
	}
	
	
	HttpCallback loginCallback=new HttpCallback(){
		@Override
		public void onHttpResult(int code, JSONObject response) {
			Map map=null;
			StringBuffer  msg=new StringBuffer();
			map=dealCommBack(code,response, msg);
			if(map!=null){
				onLoginSuccess();
			}else{
				onLoginFailed( msg.toString());

			}
		}

		@Override
		public void onHttpResult(int code, String response) {
		
		}	
	};
	HttpCallback heatBeatCallback=new HttpCallback(){
		@Override
		public void onHttpResult(int code, JSONObject response) {
			Map map=null;
			StringBuffer  msg=new StringBuffer();
			map=dealCommBack(code,response, msg);
			if(map!=null){
				onBeatSuccess(map);
			}else{
				onHeatbeatFailed(msg.toString());
			}
		}

		@Override
		public void onHttpResult(int code, String response) {
		
		}	
	};
	HttpCallback zfbQrCallback=new HttpCallback(){
		@Override
		public void onHttpResult(int code, JSONObject response) {
			Map map=null;
			StringBuffer  msg=new StringBuffer();
			map=dealCommBack(code,response, msg);
			if(map!=null){
				
				onGetZfbQrSuccess(map);
				inPayQuery=true;
				startZfbPayStateTask();
			}else{
				onGetZfbQrFailed(msg.toString());
			}
		}
		
		@Override
		public void onHttpResult(int code, String response) {
			
		}	
	};
	HttpCallback weixinQrCallback=new HttpCallback(){
		@Override
		public void onHttpResult(int code, JSONObject response) {
			Map map=null;
			StringBuffer  msg=new StringBuffer();
			map=dealCommBack(code,response, msg);
			if(map!=null){
				inPayQuery=true;
				onGetWeixinQrSuccess(map);
				startWeixinPayStateTask();
			}else{
				onGetWeixinQrFailed(msg.toString());
			}
		}
		
		@Override
		public void onHttpResult(int code, String response) {
			
		}	
	};
	HttpCallback zfbPayStateCallback=new HttpCallback(){
		@Override
		public void onHttpResult(int code, JSONObject response) {
			Map map=null;
			
			StringBuffer  msg=new StringBuffer();
			map=dealCommBack(code,response, msg);
			if(map!=null){
				
				if(onzfbPayState(map)){
					inPayQuery=false;
				}
			}else{
				onGetZfbPayStateFailed(msg.toString());
			}
		
			startZfbPayStateTask();
			
		}
		
		@Override
		public void onHttpResult(int code, String response) {
			
		}	
	};
	HttpCallback weixinPayStateCallback=new HttpCallback(){
		@Override
		public void onHttpResult(int code, JSONObject response) {
			Map map=null;
			StringBuffer  msg=new StringBuffer();
			map=dealCommBack(code,response, msg);
			if(map!=null){
				if(onWeixinPayState(map)){
					inPayQuery=false;
				}
			}else{
				onGetWeixinPayStateFailed(msg.toString());
			}

			startWeixinPayStateTask();
		
		}
		
		@Override
		public void onHttpResult(int code, String response) {
			
		}	
	};
	HttpCallback postSaleMsgCallback=new HttpCallback(){
		@Override
		public void onHttpResult(int code, JSONObject response) {
			Map map=null;
			StringBuffer  msg=new StringBuffer();
			map=dealCommBack(code,response, msg);
			if(map!=null){
				onPostSaleSuccess();
			}else{
				onPostSaleFailed(msg.toString());
			}
		}
		
		@Override
		public void onHttpResult(int code, String response) {
			
		}	
	};
	HttpCallback textCallback=new HttpCallback(){
		@Override
		public void onHttpResult(int code, JSONObject response) {

		}
		
		@Override
		public void onHttpResult(int code, String response) {
			
		}	
	};	
	HttpCallback techCallback=new HttpCallback(){
		@Override
		public void onHttpResult(int code, JSONObject response) {
			
		}
		
		@Override
		public void onHttpResult(int code, String response) {
			
		}	
	};	
	HttpCallback apkVerCallback=new HttpCallback(){
		@Override
		public void onHttpResult(int code, JSONObject response) {
			
		}
		
		@Override
		public void onHttpResult(int code, String response) {
			
		}	
	};	
	public PayServer(Context c,String key){
		context=c;
		machineId=GetMacAddress.getMacAddress();
		machine_key=key;
		
	}
	public void setMachineKey(String key){
		machine_key=key;
	}
	
	private String getTimeInM(){
		long now_timeL= System.currentTimeMillis();
		return now_timeL+"";
	}
	SimpleDateFormat formatDate = new SimpleDateFormat ("yyyyMMddHHmmss");
	private String getSaleTime(){
		Date date= new Date(System.currentTimeMillis());//获取当前时间
		String time = formatDate.format(date);
		
		return time;
	}
	private void addedCommParas(RequestParams params){
		if(params==null){
			params = new RequestParams();
		}
		String time= getTimeInM();
		String random=time.substring(time.length()-9, time.length()-1);
		Log.i(Tag, "random="+random);
		String md5= MD5(machineId+time+random+machine_key);
		params.add(Index_deviceid, machineId);
		params.add(Index_time,time);
		params.add(Index_random, random);
		params.add(Index_sign, md5);
	}
	

	private void login(){	
		RequestParams params = new RequestParams();
		addedCommParas(params);
		params.add(Index_apkver,apkVer);
		params.add(Index_ctlver,ctlVer);
		params.add(Index_coffeeNum,goodsNum);
		params.add(Index_techserial,techSerial);
		params.add(Index_textserial,textSerial);		
		postParams(url_extra_login,params,loginCallback);
		
	}
	public void login(String apkVer,String ctlVer,String goodsNum,String techSerial,String textSerial){
		this.apkVer=apkVer;
		this.ctlVer=ctlVer;
		this.goodsNum=goodsNum;
		this.techSerial=techSerial;
		this.textSerial=textSerial;	
		login();
	}
	private void heatBeat(){	
		RequestParams params = new RequestParams();
		addedCommParas(params);
		params.add(Index_stock,stocks);
		if(errors!=null)
			params.add(Index_error,errors);	
		params.add(Index_oneyuan,"0");	
		params.add(Index_fivejiao,"0");	
		params.add(Index_coins,"none");	
		params.add(Index_bills,"none");	
		postParams(url_extra_heartbeat,params,heatBeatCallback);
	}
	public void heatBeat(String stock,String error){
		stocks=stock;
		errors=error;
		heatBeat();
		startBeatTask();
	}
	public void tryLogin(){
		startBeatTask();
	}
	
	public void getZfbQr(String id,String price){	
		Log.i(Tag, "getZfbQr id="+id+"price="+price);
		RequestParams params = new RequestParams();
		addedCommParas(params);
		cur_goodId=id;
		cur_price=price;
		params.add(Index_goodsId,id);
		params.add(Index_price,price);
		postParams(url_extra_zfb,params,zfbQrCallback);
		
	}
	public void getWeixinQr(String id,String price){	
		Log.i(Tag, "getWeixinQr id="+id+"price="+price);
		RequestParams params = new RequestParams();
		addedCommParas(params);
		params.add(Index_goodsId,id);
		params.add(Index_price,price);
		
		postParams(url_extra_weixin,params,weixinQrCallback);
	}
	private void queryZfbState(){	
		RequestParams params = new RequestParams();
		addedCommParas(params);
		params.add(Index_outTradeno,zfbTradeno);
		postParams(url_extra_zfb_state,params,zfbPayStateCallback);
	}
	private void queryWeixinState(){	
		RequestParams params = new RequestParams();
		addedCommParas(params);
		params.add(Index_outTradeno,weixinTradeno);
		postParams(url_extra_weixin_state,params,weixinPayStateCallback);
	}
	public void updateSale(){	
		RequestParams params = new RequestParams();
		addedCommParas(params);
		
		params.add(Index_saleTime,getSaleTime());
		params.add(Index_goodsId,cur_goodId);
		params.add(Index_price,cur_price);
		params.add(Index_payType,payType);
		if(payType==PayType_zfb){
			params.add(Index_outTradeno,zfbTradeno);
		}else{
			params.add(Index_outTradeno,weixinTradeno);
		}
		postParams(url_extra_finish,params,weixinPayStateCallback);
	}
	public  void askText(String serial){	
		RequestParams params = new RequestParams();
		addedCommParas(params);
		params.add(Index_servertextserial,serial);
		postParams(url_extra_text,params,textCallback);
	}
	public  void askTechXml(String serial){	
		RequestParams params = new RequestParams();
		addedCommParas(params);
		params.add(Index_servertechserial,serial);
		postParams(url_extra_tech,params,techCallback);
	}
	public  void askapkVerXml(String curVer){	
		RequestParams params = new RequestParams();
		addedCommParas(params);
		params.add(Index_apkver,curVer);
		postParams(url_extra_ver,params,apkVerCallback);
	}
	

	private void postParams(String urlExt,RequestParams params,HttpCallback callback) {
		String url=url_comm+urlExt;
		AsyncHttp asyncHttp = new AsyncHttp(context);
		asyncHttp.PostHttpClient(url,params,callback);
	}

    


	private  String MD5(String s) {

        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str).toUpperCase();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
	

	
	public void setEventCallBack(ServerCallback back){
		callback=back;
	}


	


	public void updateState(Map<String,Integer> stock,List<String> error){
		
	}
	public void updateState(Map<String,Integer> stock,Map<String,Object> bills,List<String> error){
		
	}
	public void requestFormula(){
		
	}
	public void requestZfb(int goodsId,float price){
		
	}
	
	public void requestWeixin(int goodsId,float price){
		
	}
	/*
	 * 交易完成后需要主动调用
	 */
	public void tradeFinished(){
		
	}
	
	 public void cancelPay(){
		inPayQuery=false;
	 }
	public boolean isLogin(){
		return isLogin;
	}
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		cancelBeatTask();
	}
	
	
}
