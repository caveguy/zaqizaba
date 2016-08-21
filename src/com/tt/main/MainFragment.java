package com.tt.main;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.coffemachinev3.R;
import com.tt.main.PayDialog.PayListener;
import com.tt.main.SugarDialog.ConfirmListener;
import com.tt.pays.PayServer;
import com.tt.pays.ServerCallback;
import com.tt.util.Cleans;
import com.tt.util.Encode;
import com.tt.util.Errors;
import com.tt.util.Errors.McError;
import com.tt.util.Settings;
import com.tt.util.Stocks;
import com.tt.util.TTLog;
import com.tt.util.ToastShow;
import com.tt.view.GuideFragmentAdapter;
import com.tt.view.MainViewPager;
import com.tt.xml.Coffee;
import com.tt.xml.CoffeeFormula;
import com.tt.xml.DebugFinder;
import com.tt.xml.MachineTemper;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.AssistProtocol;
import android_serialport_api.CoffeeMcProtocol;

/*
 * ������Fragment
 */
public class MainFragment extends Fragment {
//	private final int Handler_assiMcDisp=1001;
//	private final int Handler_netDisp=1002;
	private final int Handler_tPay=1003;
	private final int Handler_mcDisp=1004;
	private final int Handler_TradeTimeOut=1005;
	private final int Handler_tCoffee=1006;	
	private final int Handler_qr_weixin=1007;
	private final int Handler_qr_zhifubao=1008;
//	private final int Handler_ServerInitTimeOut=1009;
	private final int Handler_CloseTimer=1010;
	private final int Handler_temper=1011;
	private final String Tag="CoffeeFrag";
//	private final int CoffeeType1=0;
//	private final int CoffeeType2=1;
//	private final int CoffeeType3=2;
//	private final int CoffeeType4=3;
//	private final int CoffeeType5=4;
//	private final int CoffeeType6=5;
//	private final int CoffeeType7=6;

	private final int StepNone=0;  
	private final int StepPay=1; //等待支付
	private final int StepMaking=2; //正在制作
	private final int StepTakingCup=3; //等待取走
	private final byte CoffeeFinish=0x01;//咖啡完成
	private final byte PowderFinish=0x02;//出粉完成
	private final byte AllFinish=(byte) (CoffeeFinish|PowderFinish);
	private final int SugarChoiceCloseCnt=60;
	private final int PayCloseCnt=60*2;
	
	private final int CloseCnt_TakingCup=30;
	private final int TradeTimeOutDuaration=80;
	private final int SeverTimeOutDuaration=30;
	private final int WeixinPay=2;
	private final int AliPay=1;
	private int coffeeType=0;
	private GuideFragmentAdapter mAdapter;
	private MainViewPager mPager;
	CoffeeFragmentPage1 page1 ;
	CoffeeFragmentPage2 page2; 
	MaintainFragment maintainFrag;
	
	ImageView btn_l,btn_r;
	PageIndicator mIndicator;
	ToastShow myToast;
	Context context;
	int sweetness=0;
	SugarDialog sugarDialog;
	PayDialog payDialog;
	MakingStateDialog stateDialog;
	 TTLog mylog=null;
	 CoffeeFormula xmlConfig=null;
	Cleans cleans=null;
	byte makingStep=0;  //出粉跟出咖啡完成标志
	int tradeStep=0;    //整个交易步骤
//	boolean isDeliverEnable=false;  //辅助板是否工作正常
//	boolean isMcEnable=false;      //咖啡机是否工作正常
	
	//boolean needBean=true ;   //
	//private boolean dispDevLayout=false;
	
	private final int Msk_none=0;
	private final int Msk_dev=0x01;
	private final int Msk_maintain=0x02;
	private final int Msk_dev_leaving=0x04;//准备退出开发菜单
	private int dispMskLayout=Msk_none;
    private PayServer payServer=null;
	
	boolean isConnectToServer=false;
	boolean isMachineWork=false;
	boolean isAssistMcWork=false;
	
	boolean appealed=false;//是否已经申述

	
	
	byte mcWindowLast=0; //为了知道咖啡有没有制作完成
	private List<Coffee> coffeeFormula =null;
//	private List<CleanTime> cleanTimes=null;
	private int makeCnt=0;
//	HashMap<Integer,Integer> goodId=new HashMap<Integer,Integer>();
//	HashMap<Long,String>	goodName=new HashMap<Long,String>();
//	HashMap<Long,BigDecimal>	goodPrice=new HashMap<Long,BigDecimal>();
//	HashMap<Long,String[]>	goodFormula=new HashMap<Long,String[]>();//配方
	  //存放商品信息
	 private Handler myHandler =null;
//    private CoffeeDeviceInterfaceAdapter deviceInterfaceAdapter=null;
//    private CoffeeDeviceEvent coffeeDeviceEvent=null;
	int cur_goodId=-1;
	String tExtStateDisp=null;
    Timer closeTimer=null;
	CloseTimeTask closeTask=null;
	TimerOutTask timeOutTask=null;
	AssistProtocol assistProtocol=null;
//	private MachineProtocol myMachine=null;	
	private CoffeeMcProtocol coffeeMachine=null;	
	
//	 MaintainFragment.DevCallBack  devCallBack=null;
//	private PageIndicator mIndicator;
	public static RelativeLayout mainbg;
	static SetDevCallBack myCallback=null;
	
	
	 String oldAssisStr="";
	 String oldMcStr="";
	 String oldNetStr="";

	UpdateMsgCallBack msgCallBack=null;
	
//	static class AssistState{
//		//目前辅助板的几种不能工作的状态：
//		public static boolean hasCup=true;
//		public static boolean hasWater=true;
//		public static boolean isConnect=false;	
//		public static boolean getXml=false;
//		public static boolean getEnable(){
//			return hasCup&hasWater&isConnect&getXml;
//		}
//	}
	
	public  void setMsgCallBack(UpdateMsgCallBack call) {
		// TODO Auto-generated method stub
		msgCallBack = call;
	}

	public interface UpdateMsgCallBack {
		
		void updateMsg(String msg);

	}
	
	private void updateMsgCallBack(String msg){
		if(msgCallBack!=null)
			msgCallBack.updateMsg(msg);
	}



	
	public interface SetDevCallBack{
		void onMcStateChanged(String state);
//		void onNetStateChanged(String state);
//		void onAssisStateChanged(String state);
		void enterDevMode();
		void enterMaintainMode(boolean refund,String errors);
		void hide();
		void onGetNewVer(String ver,String path);
		void onTemperChanged(String t);
		
		void onEnableDebugChanged(boolean enable);
	//	void updateId(String id);
	}
	boolean getDebugMode(){
		boolean enable=false;

			DebugFinder debug = new DebugFinder(context);
			enable=debug.isDebugMode();
			return enable;
	}
	boolean  getCoffeeFormula(){
		boolean enable=false;
		try {
			if(xmlConfig==null) {
				xmlConfig = new CoffeeFormula(context);
			}

			coffeeFormula=xmlConfig.getCoffeeFormula();
//			cleanTimes=xmlConfig.getCleanTimes();
			initCleans(xmlConfig.getCleanTimes(),xmlConfig.getClean_duration(),xmlConfig.getClean_water());
			String ver=xmlConfig.getVerSerial();

			if(payServer!=null){
				payServer.setCurXmlVer(ver);
			}
			makeCnt=Settings.getCoffees(context);

			enable=true;
		
			//AssistState.getXml=true;
		} catch (Exception e) {
			Log.e(Tag, e.toString());
			//AssistState.getXml=false;		
			enable=false;
			e.printStackTrace();
		}finally{
			setEnable(enable, Errors.McError.Mc_error10);
		}
		return enable;
	}
	//在这里面设置定时清洗功能
	void setCleanAlarm(){
		
	}
	/**********跟开发选项及维护菜单打交道的接口**************
	 * 
	 * @return
	 */
//	private void updateIdCallBack(String msg){
//		//Log.e(Tag, "feedid="+msg);
//		if(myCallback!=null){
//			myCallback.updateId(msg);
//		}
//	}
	void setDevMcState(String state){
		if(myCallback!=null){
			myCallback.onMcStateChanged(state);
		}
	}
	void setDevTemper(String state){
		if(myCallback!=null){
			myCallback.onTemperChanged(state);
		}
	}

	void setEnableDebug(boolean enable){
		if(myCallback!=null){
			myCallback.onEnableDebugChanged(enable);
		}
	}
	/*
	 * dev模式是强制进入的，在maintain模式也可以进去
	 * 
	 */
	void enterDevMode(){
		//Log.e(Tag, "enterDevMode!!! ");
		dispMskLayout=Msk_dev;
		//dispDevLayout=true;
		if(myCallback!=null){
			myCallback.enterDevMode();
		}else{
			Log.e(Tag, "myCallback==null");
		}
	}
	
	void  enterMaintainMode(boolean refund,String errors){
		mylog.log_i( "enterMaintainMode");
		if((dispMskLayout&(Msk_maintain|Msk_dev))==0){
			dispMskLayout=Msk_maintain;
			mylog.log_i( "enterMaintainMode1");
			if(myCallback!=null){
				mylog.log_i( "enterMaintainMode2");
				myCallback.enterMaintainMode(refund,errors);
			}
		}
	}
	
	
	void leaveDevOrMaintainMode(){
		if(dispMskLayout!=Msk_none){
		mylog.log_i( "leaveDevOrMaintainMode");
		dispMskLayout=Msk_none;
			if(myCallback!=null){
				myCallback. hide();
			}
		}
	}
	/******************************end**************************************/
	
	
	public SetDevCallBack getBack() {
		return myCallback;
	}
	
	
	public void setCallBack(SetDevCallBack back) {
		this.myCallback = back;
	}

	public static MainFragment newInstance() {
		MainFragment fragment = new MainFragment();
		return fragment;
	}

	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_coffee_cantainer, container, false);
		 mylog=new TTLog(Tag,true);
		context=getActivity();
		initView(view);

	
		return view;
	}
	void firstInitErrors(){
		List<Errors.McError> errors=new ArrayList<>();
		errors.add(Errors.McError.Mc_error1);
		errors.add(Errors.McError.Mc_error2);
		errors.add(Errors.McError.Mc_error27);
		setEnable(false,errors);
	}
	
	
    @Override
	public void onStart() {
		//setMcEnable(false,context.getString(R.string.comErr));
		//setNetWorkEnable(false,context.getString(R.string.connectFailed));
		if(getCoffeeFormula()){
			//setGoodMsg();
		}
    	setEnableDebug(getDebugMode());
    	firstInitErrors();
    	 initSever();
    	initCoffeeMachine();
        initAssistMachine();
       

		restoreDevState();
		
		super.onStart();
	}
    
    void restoreDevState(){
    //	simulateGoodId(Settings.getIsDebug(context));
    	coffeeMachine.cmd_openBoiler(Settings.getIsHeating(context));
    }

    String getCurGoodName(){
    	for(Coffee coffee:coffeeFormula){
    		if(coffee.getId()==cur_goodId){
    			return coffee.getName();
    		}
    	}
    	return "";
    }
    String getGoodPrice(int id){
    	for(Coffee coffee:coffeeFormula){
    		if(coffee.getId()==id){
    			return coffee.getPrice();
    		}
    	}
    	return "";
    }
    String getCurGoodPrice(){
    	for(Coffee coffee:coffeeFormula){
    		if(coffee.getId()==cur_goodId){
    			return coffee.getPrice();
    		}
    	}
    	return "";
    }  
	void initCoffeeMachine(){
	coffeeMachine=new CoffeeMcProtocol(context);
	coffeeMachine.setCallBack(new CoffeeMcProtocol.CallBack() {
		
		@Override
		public void sendTimeOut() {
			//setMcEnable(false,context.getString(R.string.comErr));	
		
			setEnable(false,Errors.McError.Mc_error1,context.getString(R.string.comErr));
		}
		
		@Override
		public void onReady() {
			cleanMcErrors(context.getString(R.string.cmd1_ready));
			//setCoffeeMcEnable(true,null,context.getString(R.string.cmd1_ready));
			//setEnable(true,Errors.McError.Mc_error4,context.getString(R.string.cmd1_ready));

			
		}
		
		@Override
		public void onMaking() {
			setEnable(true,Errors.McError.Mc_error1,context.getString(R.string.dropPowder));
			//setMcEnable(true,context.getString(R.string.dropPowder));
			
		}
		
		@Override
		public void onGetConnect() {
			if(xmlConfig!=null){
				MachineTemper temp=xmlConfig.getTemper();
				if(temp!=null){	
					coffeeMachine.cmd_setTemper(temp.getTemper_goal(), temp.getTemper_backLash(), temp.getTemper_min());
				}
				//coffeeMachine.cmd_openBoiler(true);
				//setMcEnable(true,context.getString(R.string.cmd1_ready));
			}
			setEnable(true,Errors.McError.Mc_error1,context.getString(R.string.cmd1_ready));
		}
		
		@Override
		public void onFinish() {
			mc_coffeeDroped();
		}

		@Override
		public void onWaiting(String fault) {
			setEnable(false,Errors.McError.Mc_waiting,fault);
			
		}

		@Override
		public void onFault(List<McError> errors) {
			
			
			if(tradeStep==StepMaking){//在制作过程中出现错误，这个时候应该退款
				if(appealed==false){//一个订单只能申述一次，后面可能改为根据申述结果看
					appealed=true;
					//appeal();
					myHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							closeOder(); //从故障中恢复，直接关闭之前的订单	
						}
					},3000);
					
				}
			}
			setCoffeeMcEnable(false,errors);	
			//setEnable(false,errors);		
		}

		@Override
		public void onTemperChanged(byte temper) {
		
			sendMsgToHandler(Handler_temper,temper+"");
		}
	});
	}

void initAssistMachine(){
    	assistProtocol=new AssistProtocol(context);
    	assistProtocol.setCallBack(new AssistProtocol.CallBack(){

        	
			@Override
			public void cupDroped() {
				//杯子已经掉下，可以打咖啡了
				mc_makeCoffee(cur_goodId);
			}
			@Override
			public void cupStuck() {
				mc_cupStuck();
				
			}

			@Override
			public void dropCupTimeOut() {
				mc_dropCupTimeOut();
				
			}

			@Override
			public void hasDirtyCup() {
				mc_hasDirtyCup();
				
			}

			@Override
			public void powderDroped() {
				mc_powderDroped();
				
			}

			@Override
			public void sendTimeOut() {
				setEnable(false, Errors.McError.Mc_error2);
				
			}
			@Override
			public void tradeFinish() {
				mylog.log_i("***cup was taken away,deal finished ****");
				//如果杯子没有移走，此处不会执行
				
				myHandler.post(new Runnable() {
					@Override
					public void run() {
						tradeFinished();
					}
				});	
				
				
			}
			@Override
			public void startDropCup() {
				mc_startDropCup();
			}

			@Override
			public void cupReady() {
				resumeTimeOutTime();
				mc_makeCoffee(cur_goodId);
			}
		
			@Override
			public void onGetConnect() {
			//	AssistState.isConnect=true;
				setEnable(true,Errors.McError.Mc_error2);
			//	updateAssitMcEnable();			
			}


			@Override
			public void onFault(byte fault) {
			if(tradeStep==StepMaking){//在制作过程中出现错误，这个时候应该退款
					if(appealed==false){//一个订单只能申述一次，后面可能改为根据申述结果看
						appealed=true;
					//	appeal();
						myHandler.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								closeOder(); //从故障中恢复，直接关闭之前的订单	
							}
						},3000);
						
					}
			}
				
				mc_assistFault(fault);
			}



			@Override
			public void onKeyPressed(byte key) {
				if(key==AssistProtocol.Key4){
					enterDevMode();
				}
			}

        	
        });

        
    }
void initSever(){
    if(hasNetWork()){
    	initPayServer();
    }
    addNetworkChangedCallback();	
}


void tradeFinished(){
	dealClean();//处理清洗事件
	closeOder();
}

void existMask(){
	
}


	void initView(View view){
		myToast= new ToastShow(context);
        ArrowListener arrowListener=new ArrowListener();
        btn_l=(ImageView)view.findViewById(R.id.btn_l);
        btn_r=(ImageView)view.findViewById(R.id.btn_r);
        btn_l.setOnClickListener(arrowListener);
        btn_r.setOnClickListener(arrowListener);
		
		mAdapter = new GuideFragmentAdapter(getFragmentManager());
		mAdapter.setFraArrayList(initFragments());
		mPager = (MainViewPager) view.findViewById(R.id.viewPaper);
		mPager.setAdapter(mAdapter);
        mIndicator = (CirclePageIndicator)view.findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        
        setArrow(0);
        setDevCallBack();
        mIndicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				//myToast.toastShow("cur page="+arg0);
				setArrow(arg0);
				page1.setCoffeeIconRadio(0);
				page2.setCoffeeIconRadio(0);
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
        

	     myHandler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
		        	
		    		switch (msg.what) {
					case Handler_qr_weixin:
						updateQRPic(msg.obj.toString(),WeixinPay);
						break;
					case Handler_qr_zhifubao:
						updateQRPic(msg.obj.toString(),AliPay);
						break;
					case Handler_tPay:
						setStateDialogString(msg.obj.toString(),1);
						break;
					case Handler_tCoffee://制作状态
						setStateDialogString(msg.obj.toString(),0);
				
						break;
					case Handler_mcDisp://
						String dsp= msg.obj.toString();
						myToast.toastShow(dsp)	;
						setDevMcState(dsp);
						break;
					case Handler_temper://
						//String dsp= msg.obj.toString();
						//myToast.toastShow(dsp)	;
						setDevTemper(msg.obj.toString());
						//myToast.toastShow(dsp);
						break;
					case Handler_TradeTimeOut:
							//closeOder();
						 tradeFinished();
						//dispRetryDialog();//超时后显示是否重做对话框
						//myToast.toastShow(R.string.);
						break;
					case Handler_CloseTimer:
						closeOder();
						
						break;
//					case Handler_ServerInitTimeOut:
//						reStartApp();
//						break;
		        }
		        }


		    };
       
		    
	}
	public void setDevCallBack(){
		MaintainFragment.back=new MaintainFragment.DevCallBack() {
		
		@Override
		public void onDevModeChanged(boolean is) {
			//simulateGoodId(is);
		}
		
		@Override
		public void onBeanModeChanged(boolean need) {
			setBeanMode(need);
		}

		@Override
		public void leave() {
			mylog.log_i( "in mian frag leave");
			dispMskLayout=Msk_dev_leaving;
			updateEnable();		
		}

		@Override
		public void clean() {
		//	myMachine.sendCleanCmd();
			coffeeMachine.cmd_cleaning();
		}

			@Override
			public void askVer(String cur) {
				payServer.askapkVer(cur);
			}

			@Override
		public void onEnableHeating(boolean is) {
			coffeeMachine.cmd_openBoiler(is);	
		}
	}; 
	}
	String oldMcString=null;
	void setBeanMode(boolean need){
		//needBean=need;
	//	ParseReceiveCommand.setBeanMake(need);
	}
	

	
	
    void updateQRPic(String path,int type){
    	Log.i(Tag,"updateQRPic type= "+type);
    	if(payDialog!=null&&payDialog.isAlive()){
    		if(type==WeixinPay)
    			payDialog.setWeixinQr(path);
    		else if(type==AliPay){
    			payDialog.setZfbQr(path);
    		}
    	}
    }
	
	void setArrow(int page){
		if(page==0){
			btn_l.setVisibility(View.GONE);
			btn_r.setVisibility(View.VISIBLE);
		}else{
			btn_l.setVisibility(View.VISIBLE);
			btn_r.setVisibility(View.GONE);
		}
	}
	
	class ArrowListener	implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.btn_l:
						mPager.arrowScroll(1);
					break;
				case R.id.btn_r:
					mPager.arrowScroll(2);
					break;
			}
			
		}
		
	}
	
	
	@Override
	public void onStop() {
		super.onStop();
	}
	@Override
	public void onDestroy() {
		try {
			assistProtocol.finalize();
			coffeeMachine.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cleanTimer();
		super.onDestroy();
	}
	private ArrayList<Fragment> initFragments() {
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	
		 page1 = CoffeeFragmentPage1.newInstance();
		 page2 = CoffeeFragmentPage2.newInstance();
		 page1.setCheckedCallBack(new CoffeeFragmentPage1.CheckedCallBack() {
			
			@Override
			public void onCallback(int id) {
				
				if(setCurCoffeeId(id)){
					showSugarDialog(id);
				}
				//coffeeType=id;
			}
		});
		 page2.setCheckedCallBack(new CoffeeFragmentPage2.CheckedCallBack() {
			 
			 @Override
			 public void onCallback(int id) {
				 
				 if(setCurCoffeeId(id+4)){
					 showSugarDialog(id);
				 }
				 //coffeeType=id;
			 }
		 });
//		fragments.add(leftFragment);
		fragments.add(page1);
		fragments.add(page2);
		return fragments;

	}
	void setSweetness(int sweet){
		sweetness=sweet;
	}
	int getSweetness(){
		return sweetness;
	}
	
	void resetChoice(){
		payServer.cancelPay();
		page1.setCoffeeIconRadio(0);
		page2.setCoffeeIconRadio(0);
		closeSugarDialog();
		closePayDialog();
		closeStateDialog();
	}
	void showSugarDialog(int id){
		sugarDialog=new SugarDialog(context,id%4);
		sugarDialog.setConfirmListener(sugarListener);
		startCloseTimer(SugarChoiceCloseCnt);
		sugarDialog.show();
	}
	void closeSugarDialog(){
		//cancelCloseTimerTask();
		if(sugarDialog!=null)
			sugarDialog.closeDialog();
	}
	void showPayDialog(int id,int sweet){
		startCloseTimer(PayCloseCnt);
		//Log.e(Tag, "showPayDialog order="+id+"curgoodid="+cur_goodId+"getCurGoodName="+getCurGoodName());
		payDialog=new PayDialog(context,id%4,sweet,getCurGoodName());
		payDialog.setListener(payListener);
		payDialog.show();
		askQrPay(cur_goodId);
	}
	void closePayDialog(){
		if(payDialog!=null)
			payDialog.closeDialog();
	}
	void showStateDialog(int id,String state){
		 closePayDialog();
		stateDialog=new MakingStateDialog(context,id%4,state);
		stateDialog.show();
	}
	void closeStateDialog(){
		if(stateDialog!=null)
		stateDialog.closeDialog();
	}
	void setStateDialogString(String state,int which){
		if(stateDialog!=null&&stateDialog.isAlive()){
			if(which==0)
				stateDialog.setState(state);
			else{
				stateDialog.setState_ext(state);
			}
		}
	}
	
	SugarListener sugarListener=new SugarListener();
	 class SugarListener implements ConfirmListener{

			@Override
			public void onOKClick(int position, int choose) {
				setSweetness(choose);
				if(Settings.getIsDebug(context)){
					startMaking();
				}else{
					showPayDialog(position,choose);
				}
			}

			@Override
			public void onCancelClick(int position) {
				resetChoice();
				
			}
	 }
	 PayDialogListener payListener=new PayDialogListener();
	 class PayDialogListener implements PayListener{

		@Override
		public void onCancelClick() {
			resetChoice();
		}

		@Override
		public void onPay(boolean success) {
			
			startMaking();
		} 
	 }
	    
	    void startMaking(){  
	    	appealed=false;//支付后默认未申述
	    	cancelCloseTimerTask();
	    	if(tradeStep==StepMaking)//防止重复进入制作阶段
	    		return;
	    	tradeStep=StepMaking; //进入制作阶段
	    	startTimeOutTimer(TradeTimeOutDuaration,TimerOutTask.Event_trade_timeOut);
	    	if(Settings.getDropcupMode(context)){
	    		mc_dropCup();
	    	}else{
	    		mc_readCup();
	    	}
	    }
//	    /*
//	     * 为了不能注册的机子debug
//	     */
//	   void simulateGoodId(boolean is){
//		   if(is){
//			   if(goodId.isEmpty()){
//				   for(int i=0;i<7;i++){
//					   goodId.put(i, i+1);
//				   }
//			   }
//		   }
//	    }
	    
	    void updatePrice(){

	    }
	    
	    

	    
	    void setGoodMsg(){
	    	long id;
	    	if(coffeeFormula==null){
	    		return;
	    	}
	    	String[] name = new String[coffeeFormula.size()];
	    	String[] orgPrice=new String[coffeeFormula.size()];
	    	String[] price=new String[coffeeFormula.size()];
	    	int i=0;
	    	for(Coffee coffee:coffeeFormula){
	    		name[i]=coffee.getName();

	    		if(coffee.getPrice()!=null){
	    			price[i]="|￥"+coffee.getPrice();
	    		}
	    		if(coffee.getOrgPrice()!=null){
	    			orgPrice[i]=context.getString(R.string.orgPrice)+coffee.getOrgPrice();
	    		}
	    		i++;
	    		
	    	}

	    	page1.setIconNames(name);
	    	page1.setIconOrgPrice(orgPrice);
	    	page1.setIconPrice(price);
	    	if(name.length>4){
		    	String[] name2=new String[name.length-4];
		    	String[] orgPrice2=new String[name.length-4];
		    	String[] price2=new String[name.length-4];
		    	for( i=4;i<name.length;i++){
		    		name2[i-4]=name[i];
		    		orgPrice2[i-4]=orgPrice[i];
		    		price2[i-4]=price[i];
		    	}
		    	page2.setIconNames(name2);
		    	page2.setIconOrgPrice(orgPrice2);
		    	page2.setIconPrice(price2);
	    	}
	    }
	    

	    void heatBeat(){
	    	JSONObject json=new JSONObject();
	    
			try {
			json.put("water", Stocks.getCurWater(context));
			json.put("beans", Stocks.getCurBean(context));
			json.put("powder1",Stocks.getCurPowder1(context));
			json.put("powder2", Stocks.getCurPowder2(context));
			json.put("powder3", Stocks.getCurPowder3(context));
			json.put("cups", Stocks.getCurCup(context));
			Log.i(Tag, "stock="+json.toString());
			
			JSONArray  errors=new JSONArray();	
		//	JSONObject error=new JSONObject();	
			List<Errors.McError> errStrs=Errors.getErrorsToServer();
			for(Errors.McError e:errStrs){
				JSONObject error=new JSONObject();
				error.put("error", e.getValue());
				errors.put(error);
			}	
			Log.i(Tag, "errors="+errors.toString());
			payServer.heatBeat(json.toString(), errors.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    void initPayServer(){
	    	if(payServer==null){
	    		payServer=new PayServer(context, "123456uklopu9087");
	    		payServer.setEventCallBack(new ServerCallback() {
					
					@Override
					public void onTextUpdate(String text) {
						updateMsgCallBack(text);
						
					}
					
					@Override
					public void onPaySuccess(String type, String buyerId) {
						mylog.log_i("onPaySuccess!!!");
						myHandler.post(new Runnable() {
							@Override
							public void run() {
								myToast.toastShow(R.string.paySuccess);
							}
						});
						startMaking();
						
					}
					
					@Override
					public void onPayFailed() {
		
					}
					
					@Override
					public void onLoginSuccess() {
						setEnable(true, Errors.McError.Mc_error27);
						//setNetWorkEnable(true,context.getString(R.string.connectServer));
						myToast.toastShow("register success!");
						myHandler.post(new  Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								setGoodMsg();
							}
						});
						
						heatBeat();
					}
					
					@Override
					public void onLoginFailed(String msg) {
						setEnable(false, Errors.McError.Mc_error27);
						//setNetWorkEnable(false,context.getString(R.string.loginFailed));
						myToast.toastShow("register failed!  msg="+msg);
						payServer.tryLogin();
					}
					
					@Override
					public void onHaveNewText(String serial) {
						payServer.askText(serial);
						
					}
					
					@Override
					public void onHaveNewTech(String serial) {
						payServer.askTechXml(serial);
						
					}
					
					@Override
					public void onGetZfbQrCode(String qr) {
						Log.i(Tag,"onGetZfbQrCode===");
						getQtImage( qr,AliPay);
						
					}
					
					@Override
					public void onGetWeixinQrCode(String qr) {
						Log.i(Tag,"onGetWeixinQrCode===");
						getQtImage( qr,WeixinPay);
						
					}
					
					@Override
					public void onGetNewVersion(String ver, String path) {
						if(myCallback!=null){
							myCallback.onGetNewVer(ver, path);
						}
						
					}
					
					@Override
					public void onFormulaUpdate(String xml) {
					//	Log.i("Tag","onFormulaUpdate xml="+xml);
						if(xmlConfig==null){
						 Log.e(Tag,"onFormulaUpdate xmlConfig==null ");
							return ;
						}
							if(xmlConfig.updateXml(xml)){
								if(getCoffeeFormula()) {
									myHandler.post(new  Runnable() {

										@Override
										public void run() {
											setGoodMsg();
										}
									});
								}
							}
						}
					@Override
					public void onDisconnect(){
						setEnable(false, Errors.McError.Mc_error27);
						myToast.toastShow("onDisconnect");
						payServer.tryLogin();
					}


				});

	    	}
			payServer.login(context.getString(R.string.version), "v1.1", "8",xmlConfig==null?"10000":xmlConfig.getVerSerial(), "0");
	    }
	    

		private void sendMsgToHandler(int what,String dsp){
			Message msg=new Message();
			msg.what=what;
			msg.obj=dsp;
			myHandler.sendMessage(msg);
		}
		public void getQtImage(String url,int type) {

			final String filePath = context.getCacheDir() + File.pathSeparator + "qtImage"+type
					+ ".jpg";
			int widthPix = 300;
			int heightPix = 300;
			boolean blCreated = Encode.createQRImage(url, widthPix, heightPix,
					null, filePath);

			if (blCreated) {
				myToast.toastShow(R.string.createQrSuccess);
				if(type==WeixinPay)
					sendMsgToHandler(Handler_qr_weixin,filePath);
				else if(type==AliPay){
					sendMsgToHandler(Handler_qr_zhifubao,filePath);
				}

			} else {
				myToast.toastShow(R.string.createQrFailed);
			}
		}
	    
	    void askWeixinQrPay(int goodId){
	    	String price=getGoodPrice(goodId);
	    	payServer.getWeixinQr(goodId+"", price);
	    }
	    void askZfbQrPay(int goodId){
	    	String price=getGoodPrice(goodId);
	    	
	    	payServer.getZfbQr(goodId+"", price);
	    }
	    void askQrPay(int goodId){
	     askZfbQrPay(goodId);
	   	askWeixinQrPay(goodId);
	    }
	    
		void addNetworkChangedCallback(){
			
			
			NetChangedReciever.setCallBack(new NetChangedReciever.CallBack() {
				@Override
				public void netWorkChanged(boolean connected) {
					mylog.log_i( "!!!!!!!!!!!!!!netWorkChanged "+connected);
					myToast.toastShow("netWorkChanged "+connected);
					if(connected){
						//setNetWorkEnable(false,context.getString(R.string.hasnet));
						initPayServer();
					}else{
						setEnable(false, Errors.McError.Mc_error27);
					//	setNetWorkEnable(false,context.getString(R.string.nonet));
					}
				}
			});
		}

		
//		void reStartApp(){
//
//			Intent restartIntent = new Intent(context, MainActivity.class);
//			restartIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//			int pendingId = 1;
//			PendingIntent pendingIntent = PendingIntent.getActivity(context, pendingId, restartIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//			AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, pendingIntent);
//			//((Activity) context).finish();
//			android.os.Process.killProcess(android.os.Process.myPid());
//		}
		    
		    
		    void mc_dropCup(){
		    	myHandler.post(new Runnable() {		
					@Override
					public void run() {
						showStateDialog(coffeeType,context.getString(R.string.startDropCup));
					}
				});
		    	
		    	assistProtocol.cmd_dropCup(); 	
		    }
		    void mc_readCup(){
		    
		    	myHandler.post(new Runnable() {		
					@Override
					public void run() {
						pauseTimeOutTime();
						showStateDialog(coffeeType,context.getString(R.string.putCup));
					//	t_coffeeType.setText(R.string.putCup);
					}
				});
		    	assistProtocol.cmd_isCupReady();
		    }
		    /**
		     * 制作咖啡接口
		     *此函数触发出粉/出咖啡 
		     */
		    void mc_makeCoffee(int id){
		    	makingStep=0;
		    	//test
		    	sendMsgToHandler(Handler_tCoffee, context.getString(R.string.dropPowder));
		    	makeCoffee(id,sweetness);
		    }
		    
		    void makeCoffee(int id,int level){
		    	makingStep=AllFinish;//如果后面什么都不做，那么就是全部做完了
	    		if(coffeeFormula==null){
	    			return;
	    		}
	    		
	    		for(Coffee coffee:coffeeFormula){
	    			//if(coffee.getName().equals(context.getString(R.string.name_americano))){
	    			
	    			if(coffee.getId()==id){
	    				Log.i(Tag,"cur_goodId="+cur_goodId+ " choice coffee="+coffee.toString());
	    				if(coffee.getNeedCoffee()!=null){//需要打咖啡
		    				int needCoffee=new Integer(coffee.getNeedCoffee());
		    				if(needCoffee==1){ //出咖啡
		    					makingStep&=~CoffeeFinish;
		    					int powder= coffee.getCoffeePowder();
		    					int water=coffee.getCoffeeWater();
		    					int preWater=coffee.getCoffeePreWater();
		    					if(powder!=0&&water!=0){
		    						coffeeMachine.cmd_setCoffee(powder, water);
		    					}
		    					if(preWater!=0){
		    						coffeeMachine.cmd_setInfiltrateWater(preWater);
		    					}
		    					makeCnt++;
		    					Settings.setCoffees(context, makeCnt);
		    					coffeeMachine.cmd_making();
		    					//myMachine.dropCoffee();
		    				}
	    				}
	    				
	    				Integer sugarWater=coffee.getCh1Water();
	    				if(sugarWater!=null&&sugarWater!=0){//出糖水
	    					Integer sugar=0;
		    				String[] sugar_levels=coffee.getSugarLever().split(";");
		    				if(sugar_levels!=null&&sugar_levels.length>=4){
	    						sugar=new Integer(sugar_levels[level]);
		    				}
//		    					if(level==0){
//		    						//sugar=0;
//		    					}else if(sugar_levels!=null&&sugar_levels.length>=4){
//		    						sugar=new Integer(sugar_levels[level]);
//		    					}
		    					makingStep&=~PowderFinish;
		    					int ch1r=coffee.getCh1R_powder_level();
		    					int ch2l=coffee.getCh2L_powder_level();
		    					int ch2r=coffee.getCh2R_powder_level();
		    					int ch3l=coffee.getCh3L_powder_level();
		    					int ch3r=coffee.getCh3R_powder_level();
		    					int ch4l=coffee.getCh4L_powder_level();
		    					int ch4r=coffee.getCh4R_powder_level();
		    					assistProtocol.cmd_setPowder(sugar,ch1r,ch2l,ch2r ,ch3l,ch3r,ch4l,ch4r);
		    					assistProtocol.cmd_setWater(coffee.getCh1Water(), coffee.getCh2Water(), coffee.getCh3Water(),coffee.getCh4Water());
		    					assistProtocol.cmd_startDropPowder(coffee.getCh1Water()==0?false:true, coffee.getCh1Water()==0?false:true, coffee.getCh2Water()==0?false:true, coffee.getCh2Water()==0?false:true, coffee.getCh3Water()==0?false:true, coffee.getCh3Water()==0?false:true, coffee.getCh4Water()==0?false:true, coffee.getCh4Water()==0?false:true);
		    					//deliveryController.cmd_pushLeftPowder(sugar,coffee.getSugarPreWater(),coffee.getSugarWater());
		    				
	    				}
//		    				Integer milkWater=coffee.getCh1rWater();
//		    				if(milkWater!=null&&milkWater!=0){  //出奶水
//		    					makingStep&=~PowderFinish;
//		    					deliveryController.cmd_pushCenterPowder(coffee.getMilkLever(),coffee.getMilkPreWater(),coffee.getMilkWater());
//		    				}
//		    				Integer chocolateWater=coffee.getChocolateWater();
//		    				if(chocolateWater!=null&&chocolateWater!=0){  //出巧克力水
//		    					makingStep&=~PowderFinish;
//		    					deliveryController.cmd_pushRightPowder(coffee.getChocolateLever(),coffee.getChocolatePreWater(),coffee.getChocolateWater());
//		    				}
	    				}
	    					
		    		}
	    				
	    		}

		    void mc_assistFault(byte fault){
		    	
		    	boolean hasWater=(fault&AssistProtocol.Fault_noWater)==0?true:false;
		    	boolean hasCup=(fault&AssistProtocol.Fault_noCup)==0?true:false;  
		    	//mylog.log_e("mc_assistFault hasWater="+AssistState.hasWater+" hasCup="+AssistState.hasCup+"fault="+fault);
		    	//setAssitMcEnable(false,"");
		    	//updateAssitMcEnable();
		    
		    	setEnable(hasWater, Errors.McError.Mc_error26);
		    	setEnable(hasCup, Errors.McError.Mc_error25);
		    	
		    	
		    }

		    /**
		     * 
		     * 有脏杯子没有取走，提示用户拿走脏杯子
		     */
		    void mc_hasDirtyCup(){
		    	
		    	sendMsgToHandler(Handler_tCoffee, context.getString(R.string.hasDirtyCup));
		    	pauseTimeOutTime(); 	
		    }
		    
		    
		    
		    void mc_startDropCup(){
		    	resumeTimeOutTime();
		    	sendMsgToHandler(Handler_tCoffee, context.getString(R.string.startDropCup));

		    }
	/**
	 * 咖啡制作完成
	 * 等待取杯
	 */

		   void stepTakingCup(){
				myHandler.post(new Runnable() {
					@Override
					public void run() {
						payServer.updateSale();
					}
				});	
		   		tradeStep=StepTakingCup;
		   		tExtStateDisp=context.getString(R.string.takeCup);
				if(timeOutTask!=null){
					timeOutTask.closeCnt=CloseCnt_TakingCup;
				}
				assistProtocol.cmd_QueryCupToken();
		   }
		   
		    /**
		     * 出粉完成
		     * 
		     */
		    void mc_powderDroped(){
		    	//myToast.toastShow("出粉完成！！！！");
		    	makingStep|=PowderFinish;
		    	if(makingStep==AllFinish){
		    		stepTakingCup();
		    	}   
		    	
		    }
		    
		    void mc_coffeeDroped(){
		    	makingStep|=CoffeeFinish;
		    	
		    	if(makingStep==AllFinish){
		    		stepTakingCup();
		    	}  	

		    }
		    void mc_dropCupTimeOut(){
		    	mc_cupStuck();
		    	
		    }
		    void mc_cupStuck(){
		    	sendMsgToHandler(Handler_tCoffee, context.getString(R.string.cupStuck));
		    	assistProtocol.cmd_isCupReady();
		    }


		    
		    class CloseTimeTask extends TimerTask{
		    	
		    	//int closeCnt=0;
		    	boolean inTask=false;
				@Override
				public void run() {
					if(inTask){
						
						sendMsgToHandler(Handler_CloseTimer, "");
						
					}
				}
		    	
		    }
		    class TimerOutTask extends TimerTask{
		    	
		    	boolean inTask=false;
		    	int closeCnt=0;
		    	static final int Event_trade_timeOut=1;
		    	static final int Event_serverInit_timeOut=2;
		    	int event=0;
				@Override
				public void run() {
					//isTrading=false;
					if(inTask){
					myHandler.post(new Runnable() {
						
						@Override
						public void run() {
							
								if(closeCnt-->0){
									
									if(tradeStep==StepTakingCup){
										
										String dsp=context.getString(R.string.finished);
										sendMsgToHandler(Handler_tCoffee, dsp);
										String dsp2=tExtStateDisp+"("+closeCnt+"s)";
										sendMsgToHandler(Handler_tPay, dsp2);
									}else{
										String dsp=context.getString(R.string.alLeftTime)+closeCnt+"s";
										sendMsgToHandler(Handler_tPay, dsp);
									}
		
								}else{
									if(event==Event_trade_timeOut){
										tradeTimeOut();	
									}
//									else if(event==Event_serverInit_timeOut){
//										serverInitTimeOut();
//									}
								}
							}
						});	
					}
				}
		    	
		    }
	//交易超时应该退款
		    void tradeTimeOut(){
		    	//这个时候应该可以选择重试或者退款！！！
		    	sendMsgToHandler(Handler_TradeTimeOut,"");
		    }
//		    void serverInitTimeOut(){
//		    	reStartApp();
//		    	//sendMsgToHandler(Handler_ServerInitTimeOut,"");
//		    }
		    
		    void startCloseTimer(int cnt){	    	
		    	if(closeTimer==null){
		    		closeTimer=new Timer();
		    	}
		    	cancelCloseTimerTask();
	    		closeTask=new CloseTimeTask();
	    		//closeTask.closeCnt=cnt;
	    		closeTask.inTask=true;
	    		closeTimer.schedule(closeTask, 1000*cnt);
		    }

		    
		    void cancelCloseTimerTask(){
		    	if(closeTask!=null){
		    		closeTask.inTask=false;
		    		if(closeTask.cancel()){
		    			closeTask=null;
		    		}
		    	}
		    }
		    
		    void pauseTimeOutTime(){
		    	if(timeOutTask!=null)
		    		timeOutTask.inTask=false;
		    }
		    void resumeTimeOutTime(){
		    	if(timeOutTask!=null)
		    		timeOutTask.inTask=true;
		    }
			private void startTimeOutTimer(int cnt,int event){

			//	Log.d("ioctrl","startAckTimer############");
				if(closeTimer==null){
					closeTimer=new Timer();
				}
				cancelTimeOutTask();
				timeOutTask=new TimerOutTask();
				timeOutTask.inTask=true;
				timeOutTask.closeCnt=cnt;
				timeOutTask.event=event;
				closeTimer.schedule(timeOutTask, 1000,1000);
			}
		    void cancelTimeOutTask(){
		    	if(timeOutTask!=null){
		    		timeOutTask.inTask=false;
		    		if(timeOutTask.cancel()){
		    			timeOutTask=null;
		    		}
		    	}
		    }	    


			public void cleanTimer(){
				if(closeTimer!=null){
					closeTimer.cancel();
					closeTimer=null;
				}	
			} 


			 void setEnable(boolean  enable,Errors.McError error){
				 mylog.log_i("setEnable ="+enable+" msg="+error.getValue());
				 boolean changed=false;
				 if(!enable){
					 changed=Errors.addError(error); 
				 }else{
					 changed=Errors.removeError(error);
				 }
				 if(changed)
					 updateEnable();
			 }
			 void setEnable(boolean  enable,Errors.McError error,String state){
				 mylog.log_i("setEnable ="+enable+" msg="+error.getValue());
				 boolean changed=false;
				 if(!enable){
					 changed=Errors.addError(error); 
				 }else{
					 changed=Errors.removeError(error);
				 }
				 changed|= setMcStateText(state); 
				 if(changed)
					 updateEnable();
				 
				 
			 }
			 
			 
			 void setEnable(boolean  enable,List<Errors.McError> errors){
				 mylog.log_i("setEnable ="+enable+" errors="+errors.toString());
				 boolean changed=false;
				 if(!enable){
					 for(Errors.McError e:errors)
						 changed|=Errors.addError(e); 
				 }else{
					 for(Errors.McError e:errors)
					 	changed|=Errors.removeError(e);
				 }
				 
				 if(changed)
					 updateEnable();
			 }
			 
			 void cleanMcErrors(String state){
				// mylog.log_i("setCoffeeMcEnable ="+enable+" errors="+errors.toString());
				 boolean changed=false;
				 changed= Errors.setCoffeeMcErrors(null);
				 changed|= setMcStateText(state); 
				 if(changed)
					 updateEnable();

			 }
			 
			 void setCoffeeMcEnable(boolean  enable,List<Errors.McError> errors){
				 mylog.log_i("setCoffeeMcEnable ="+enable+" errors="+errors.toString());
				 boolean changed=false;

				 changed= Errors.setCoffeeMcErrors(errors);
				 
				 if(changed)
					 updateEnable();
			 }

			 boolean  setMcStateText(String msg){
				 mylog.log_i("setStateText msg="+msg);
				 if(msg==null)
					 return false;
				 if((!oldMcStr.equals(msg))){
					 oldMcStr=msg;
					 sendMsgToHandler(Handler_mcDisp, msg);
					 return true;
				 }
				 return false;
			 }

			 void updateEnable(){
				 String errors=null;
				 if(Errors.hasError()){
//					 if(false){
					 errors=Errors.getErrorsDetails(context);
					 leaveDevOrMaintainMode();
					 enterMaintainMode(appealed,errors);
				 }else{
					 leaveDevOrMaintainMode();
				 }
				 
//				 mylog.log_i( "updateEnable!!!!!!!!!!isMachineWork="+isMachineWork+" isConnectToServer="+isConnectToServer+" isAssistMcWork="+isAssistMcWork);
//				 setEnable(isMachineWork&&isConnectToServer&&isAssistMcWork);
			 }
				void closeOder(){
					tradeStep=StepNone;		
					cancelCloseTimerTask();
					cancelTimeOutTask();
					
//					deliveryController.cancelQueryTimerTask();
//					deliveryController.cmd_readError();//交易完成之后读取水位
					assistProtocol.cmd_handShake();
					resetChoice();
				}
				
				void setMakingState(String state){
					if(stateDialog!=null&&stateDialog.isAlive()){
						stateDialog.setState(state);
					}
				}
				boolean  setCurCoffeeId(int type){
					Log.i(Tag, "***********setCurCoffeeId="+type);
					if(type==-1){
						cur_goodId=-1;
						//t_coffeeType.setText(R.string.pleaseChooseCoffee);
						//setPayEnable(false);
						return false;
					}
					coffeeType=type;
					
					for(Coffee c:coffeeFormula){
						if(c.getOrder()==type){
							cur_goodId=c.getId();
							return true;
						}
					}

					
					return false;
				}
				
				boolean  hasNetWork(){
					ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
					NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					NetworkInfo ethInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
					//NetworkInfo activeInfo = manager.getActiveNetworkInfo();
					
					boolean isConnected=mobileInfo.isConnected()|wifiInfo.isConnected()|ethInfo.isConnected();
					
					return isConnected;
				}
	void dealClean(){
		if(cleans.dealClean()){
			coffeeMachine.cmd_cleaning();
			assistProtocol.cmd_cleaning(cleans.getWater());
		}
	}
	void initCleans(List<String> time,int cnt,int ml){
		if(cleans==null){
			cleans=new Cleans(context);
			cleans.setCallBack(new Cleans.CleanCallBack() {
				@Override
				public void onClean() {
					if(tradeStep==StepNone){
						coffeeMachine.cmd_cleaning();
						assistProtocol.cmd_cleaning(cleans.getWater());
					}
				}
			});
		}
		cleans.setDuaration(cnt);
		cleans.setWater(ml);
		cleans.setTimes(time);
	}
				
}
