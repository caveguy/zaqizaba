package com.tt.main;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import tp.ass.device.web.response.TPResponse;
import tp.device.DeviceInterface.MyHandler;
import tp.device.coffee.adapter.CoffeeDeviceInterfaceAdapter;
import tp.device.coffee.event.CoffeeDeviceEvent;
import tp.device.coffee.task.MakeOrderAsyncTask;
import tp.device.coffee.task.QueryDeviceGoodsAsyncTask;
import tp.device.coffee.task.RefundApplyAsyncTask;
import tp.lib.TPConstants;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.style.AlignmentSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android_serialport_api.DeliveryProtocol;
import android_serialport_api.MachineProtocol;
import android_serialport_api.ParseReceiveCommand;
import coffee.shop.po.DeviceGoods;
import coffee.shop.po.request.ApplyRefundReq;
import coffee.shop.po.request.MakeOrderReq;
import coffee.shop.po.response.MakeOrderRsp;
import coffee.shop.po.response.QueryDeviceGoodsRsp;

import com.example.coffemachinev3.R;
import com.tt.main.CoffeeFragmentPage1.CheckedCallBack;
import com.tt.main.PayDialog.PayListener;
import com.tt.main.SugarDialog.ConfirmListener;
import com.tt.util.Encode;
import com.tt.util.TTLog;
import com.tt.util.ToastShow;
import com.tt.view.GuideFragmentAdapter;
import com.tt.view.MainViewPager;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

/*
 * ������Fragment
 */
public class MainFragment extends Fragment {

	private final int Handler_netDisp=1002;
	private final int Handler_tPay=1003;
	private final int Handler_tCoffee=1006;
	private final int Handler_mcDisp=1004;
	private final int Handler_TimeOut=1005;
	private final int Handler_qr_weixin=1007;
	private final int Handler_qr_zhifubao=1008;
	private final String Tag="CoffeeFrag";
	private final int CoffeeType1=0;
	private final int CoffeeType2=1;
	private final int CoffeeType3=2;
	private final int CoffeeType4=3;
	private final int CoffeeType5=4;
	private final int CoffeeType6=5;

	private final int StepNone=0;  
	private final int StepPay=1; //等待支付
	private final int StepMaking=2; //正在制作
	private final int StepTakingCup=3; //等待取走
	private final byte CoffeeFinish=0x01;//咖啡完成
	private final byte PowderFinish=0x02;//出粉完成
	private final byte AllFinish=(byte) (CoffeeFinish|PowderFinish);
	private final int CloseCnt_pay=60*2;
	private final int CloseCnt_TakingCup=30;
	private final int TimeOutDuaration=80;
	private final int WeixinPay=2;
	private final int AliPay=1;
	private int coffeeType=0;
	private GuideFragmentAdapter mAdapter;
	private MainViewPager mPager;
	CoffeeFragmentPage1 page1 ;
	CoffeeFragmentPage2 page2; 
	ImageView btn_l,btn_r;
	PageIndicator mIndicator;
	ToastShow myToast;
	Context context;
	int sweetness=0;
	SugarDialog sugarDialog;
	PayDialog payDialog;
	MakingStateDialog stateDialog;
	 TTLog mylog=null;
	
	
	byte makingStep=0;  //出粉跟出咖啡完成标志
	int tradeStep=0;    //整个交易步骤
	boolean isDeliverEnable=false;  //辅助板是否工作正常
	boolean isMcEnable=false;      //咖啡机是否工作正常
	boolean dropcupMode=false ;   //杯子模式，false:检查到有杯子就打咖啡，true：落杯后打咖啡
	boolean needBean=true ;   //
	private boolean dispDevLayout=false;

	//目前辅助板的两种不能工作的状态：
	boolean hasCup=true;
	boolean hasWater=true;
	boolean isConnectToServer=false;
	boolean isMachineWork=false;
	boolean appealed=false;//是否已经申述
	boolean isDebug=false;
	HashMap<Integer,Long> goodId=new HashMap<Integer,Long>();
	HashMap<Long,String>	goodName=new HashMap<Long,String>();
	HashMap<Long,BigDecimal>	goodPrice=new HashMap<Long,BigDecimal>();
	HashMap<Long,String[]>	goodFormula=new HashMap<Long,String[]>();//配方
	  //存放商品信息
	 private MyHandler myHandler =null;
    private CoffeeDeviceInterfaceAdapter deviceInterfaceAdapter=null;
    private CoffeeDeviceEvent coffeeDeviceEvent=null;
	long cur_goodId=-1;
	String tPayDisp=null;
    Timer closeTimer=null;
	CloseTimeTask closeTask=null;
	TimerOutTask timeOutTask=null;
	DeliveryProtocol deliveryController=null;
	private MachineProtocol myMachine=null;	

//	private PageIndicator mIndicator;
	public static RelativeLayout mainbg;
	CallBack back;
	public interface CallBack{
		void onCallback();
	}
	public CallBack getBack() {
		return back;
	}

	public void setBack(CallBack back) {
		this.back = back;
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
	
    @Override
	public void onStart() {
        initMachines();
		super.onStart();
	}
    Integer getCurType(){
    	Iterator it = goodId.entrySet().iterator(); 
    	while (it.hasNext()) {  	  
    		 Map.Entry entry=(HashMap.Entry) it.next();  
    		    Integer key = (Integer)entry.getKey();  	    
    		    Long value = (Long)entry.getValue();  
    	    if( value==cur_goodId){
    	    	return key;
    	    }  
    	}
    	return 0;
    }
void initMachines(){
    	
    	myMachine=new MachineProtocol(context);
    	mcSetCallBack();
        deliveryController=new DeliveryProtocol(context);
        deliveryController.setCallBack(new DeliveryProtocol.CallBack(){

        	
			@Override
			public void cupDroped() {
				//杯子已经掉下，可以打咖啡了
				mc_makeCoffee(getCurType());
			}



			@Override
			public void cupStuck() {
				mc_cupStuck();
				
			}

			@Override
			public void noCupDrop() {
				mc_noCups();
				
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
				mc_toAssistControllerTimeOut();
				
			}
			@Override
			public void tradeFinish() {
				mylog.log_i("***cup was taken away,deal finished ****");
				myHandler.post(new Runnable() {
					@Override
					public void run() {
						closeOder();
					}
				});	
			}



			@Override
			public void startDropCup() {
				mc_startDropCup();
			}



			@Override
			public void onDisable() {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void onEnable() {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void cupReady() {
				resumeTimeOutTime();
				mc_makeCoffee(getCurType());
			}



			@Override
			public void noWater() {
				mc_noWater();
			}
        	
        });
        if(hasNetWork()){
        	initPayServer();
        }

        addNetworkChangedCallback();
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
        
        

	     myHandler = new MyHandler(context){
		        @Override
		        public void myHandleMessage(Message msg) {
		        	
		    		switch (msg.what) {
					case Handler_qr_weixin:
						updateQRPic(msg.obj.toString(),WeixinPay);
						break;
					case Handler_qr_zhifubao:
						updateQRPic(msg.obj.toString(),AliPay);
						break;
					case Handler_netDisp:	
						myToast.toastShow(msg.obj.toString());
						//t_netDetail.setText(msg.obj.toString());
						break;
					case Handler_tPay:
						//t_payType.setText(msg.obj.toString());
						break;
					case Handler_tCoffee:
						//t_coffeeType.setText(msg.obj.toString());
						break;
					case Handler_mcDisp://
						String dsp=(hasCup?"":(context.getString(R.string.noCup)+"|"))+
						(hasWater?"":(context.getString(R.string.noWater)+"|"))+
						msg.obj.toString();
						//t_mcDetail.setText(dsp);
						break;
					case Handler_TimeOut:
						//dispRetryDialog();//超时后显示是否重做对话框
						//myToast.toastShow(R.string.);
						break;
		        }
		        };
		    };
        

        
	}
	void mcSetCallBack(){
		 ParseReceiveCommand.setCallBack(new ParseReceiveCommand.CallBack() {
			
			@Override
			public void onParsed(int cmd) {
				// TODO Auto-generated method stub
//				 if(cmd==1){
//					String dispString= ParseReceiveCommand.getDispStringId(context);
//					if(layout_mask.getVisibility()==View.VISIBLE){
//						sendMsgToHandler(Handler_mcDisp, dispString);
//						
//					}
//					if(dispString!=oldMcString){
//						mylog.log_i("****Machine String****"+dispString);
//						if(dispString.equals(context.getString(R.string.cmd1_pressRinse))){
//							myMachine.sendCleanCmd();
//						}
////						else if(dispString.equals(getString(R.string.cmd1_ready))){
////						 if(tradeStep!=StepNone&&oldMcString.equals(getString(R.string.cmd1_espresso))){  //交易状态下，字符串变成准备就绪说明出咖啡完成
////								mc_coffeeDroped();
////							}
////						}
//						
//					}
//					oldMcString=dispString;
//				 }
//				 else if(cmd==0x19){
//					 byte windowstate=ParseReceiveCommand.getWindow();
//					// myToast.toastShow("cmd0x19="+windowstate);
//					 if(windowstate==2&&mcWindowLast==5){
//						 mc_coffeeDroped();
//						 
//						 
//					 }					 
//					 mcWindowLast=windowstate;
//					//myMachine.initMachine();
//				 }
			}

			@Override
			public void onFault(String msg) {
				isMachineWork=false;
				if(!dispDevLayout){
					setEnable(isMachineWork&&isConnectToServer);
					sendMsgToHandler(Handler_mcDisp, msg);
				}
				if(tradeStep==StepMaking){//在制作过程中出现错误，这个时候应该退款
					if(appealed==false){//一个订单只能申述一次，后面可能改为根据申述结果看
						appealed=true;
						
						
						appeal();
						myHandler.post(new Runnable() {
							
							@Override
							public void run() {
								closeOder(); //从故障中恢复，直接关闭之前的订单	
							}
						});
						
					}
				}
	
			}

			@Override
			public void onWork() {
				isMachineWork=true;
				if(!dispDevLayout){
					setEnable(isMachineWork&&isConnectToServer);
				}
			}
		});
	}
	
	
    void updateQRPic(String path,int type){
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
		back.onCallback();
		super.onDestroy();
	}
	private ArrayList<Fragment> initFragments() {
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
	
		 page1 = CoffeeFragmentPage1.newInstance();
		 page2 = CoffeeFragmentPage2.newInstance();
		 page1.setCheckedCallBack(new CheckedCallBack() {
			
			@Override
			public void onCallback(int id) {
				showSugarDialog(id);
				setCoffeeType(id);
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
		page1.setCoffeeIconRadio(0);
		page2.setCoffeeIconRadio(0);
	}
	void showSugarDialog(int id){
		sugarDialog=new SugarDialog(context,id);
		sugarDialog.setConfirmListener(sugarListener);
		sugarDialog.show();
	}
	void showPayDialog(int id,int sweet){
		payDialog=new PayDialog(context,id,sweet);
		payDialog.setListener(payListener);
		payDialog.show();
		askQrPay(cur_goodId);
	}
	void showStateDialog(int id,String state){
		stateDialog=new MakingStateDialog(context,id,state);
		stateDialog.show();
	}
	
	SugarListener sugarListener=new SugarListener();
	 class SugarListener implements ConfirmListener{

			@Override
			public void onOKClick(int position, int choose) {
				setSweetness(choose);
				showPayDialog(position,choose);
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
	    	
	    	cancelCloseTimerTask();
	    	tradeStep=StepMaking; //进入制作阶段
	    	startTimeOutTimer(TimeOutDuaration);
	    	if(dropcupMode){
	    		mc_dropCup();
	    	}else{
	    		mc_readCup();
	    	}
	    }
	    void updatePrice(){
	        new QueryDeviceGoodsAsyncTask(){
	            @Override
	            protected void onPostExecute(QueryDeviceGoodsRsp rsp) {
	            	
	            	//try{
	                    if(rsp.getErrcode()==0){
	                    	
	                    	int i=0;
	                       for(DeviceGoods goods:rsp.getGoods()){
	                    	   
	                    	   Long id=goods.getGoodsId();
	                    	   goodId.put(i++, id);
	                    	   goodName.put(id,goods.getGoodsName());
	                    	   goodPrice.put(id, goods.getGoodsPrice());
	                    	   //配方列表
//	                    	   String formula=goods.getConfig();
//	                    	   String[] formulas=formula.split(";");
	 //                   	   int int_formula[];
//	                    	   goodFormula.put(id, formulas);
	                    	   
	                    	   
	                       }
	                       //myToast.toastShow("rsp.getErrcode() i="+i);
	                       setGoodMsg();
	                    }
//	                }catch(Exception e){
//	                	Log.e(Tag, e.toString());
//	                }
	            }
	        }.execute(deviceInterfaceAdapter.getDevice().getFeedId());
	    }
	    
	    

	    
	    void setGoodMsg(){
	    	long id;
	    	String[] name = new String[goodId.size()];
	    	for(int i=0;i<goodId.size();i++){
				if(goodId.containsKey(i)){
					id=goodId.get(i);
					name[i]=goodName.get(id)+"|￥"+goodPrice.get(id).toString();
					
				}
	    	}
	    	page1.setIconNames(name);
	    	page2.setIconNames(name);
	    	
	    }
	    void initPayServer(){
	    	if(coffeeDeviceEvent==null){
	        coffeeDeviceEvent = new CoffeeDeviceEvent() {

	        	@Override
	            public void onLoad() {
	                super.onLoad();
	                /*获得设备商品列表*/
	                
	                isConnectToServer=true;
	             //Toast.makeText(getActivity(), "连接成功", Toast.LENGTH_LONG).show();
	                sendMsgToHandler(Handler_netDisp, context.getString(R.string.connectServer));
	              //  myToast.toastShow("连接服务器成功");
	                updatePrice();

	            }
				@Override
				public void onPayFail(Long arg0) {				
					myHandler.post(new Runnable() {	
						@Override
						public void run() {
							
							 myToast.toastShow("支付失败");	
							// layout_qr.setVisibility(View.GONE);
							// cancelCloseTimerTask();
						}
					});	
				}

				@Override
				public void onPaySuccess(Long arg0) {
					mylog.log_i("onPaySuccess!!!");
					myHandler.post(new Runnable() {
						@Override
						public void run() {
							
						//	t_payType.setText(R.string.paySuccess);
							myToast.toastShow(R.string.paySuccess);
						//	layout_qr.setVisibility(View.GONE);
							//cancelCloseTimer(); //不能取消，否则按钮状态没有清除
							
						}
					});
					startMaking();
					
				}
				@Override
				public void onReceiveTranspTransfer(String arg0) {
				//	mylog.log_i("onReceiveTranspTransfer ="+arg0);	
					String updatePrice=context.getString(R.string.update_Price);
					if(arg0.equals(updatePrice)){
						//更新价格
						 updatePrice();
					}else{
						//updateMsgCallBack(arg0);
					}
				}

	        	
	        };
	    	}
	    	if(deviceInterfaceAdapter==null){
	    		deviceInterfaceAdapter = new CoffeeDeviceInterfaceAdapter(context,myHandler,coffeeDeviceEvent);
	    	}
	    }
		private void sendMsgToHandler(int what,String dsp){
			Message msg=new Message();
			msg.what=what;
			msg.obj=dsp;
			myHandler.sendMessage(msg);
		}
		public void getQtImage(String url,int type) {

			final String filePath = context.getCacheDir() + File.pathSeparator + "qtImage"
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
	    
	    void askWeixinQrPay(long goodId){
	        /*下单*/
	        MakeOrderReq req = new MakeOrderReq();
	        req.setFeedId(deviceInterfaceAdapter.getDevice().getFeedId());
	        List<Long> goodsIds = new ArrayList<Long>();
	        goodsIds.add(goodId);
	        //goodsIds.add(2l);
	        req.setGoodsIds(goodsIds);
	        req.setPayType(WeixinPay);//1支付宝 2//微信
	        new MakeOrderAsyncTask(){
	            @Override
	            protected void onPostExecute(MakeOrderRsp rsp) {
	                if(rsp!=null && rsp.getErrcode()==0){
	                  String url= rsp.getQrCodeUrl();
	                   getQtImage( url,WeixinPay);
	                }
	            }
	           
	        }.execute(req);
	    }
	    void askZfbQrPay(long goodId){
	    	/*下单*/
	    	MakeOrderReq req = new MakeOrderReq();
	    	req.setFeedId(deviceInterfaceAdapter.getDevice().getFeedId());
	    	List<Long> goodsIds = new ArrayList<Long>();
	    	goodsIds.add(goodId);
	    	//goodsIds.add(2l);
	    	req.setGoodsIds(goodsIds);
	    	req.setPayType(AliPay);//1支付宝 2//微信
	    	new MakeOrderAsyncTask(){
	    		@Override
	    		protected void onPostExecute(MakeOrderRsp rsp) {
	    			if(rsp!=null && rsp.getErrcode()==0){
	    				String url= rsp.getQrCodeUrl();
	    				getQtImage( url,AliPay);
	    			}
	    		}
	    		
	    	}.execute(req);
	    }
	    void askQrPay(long goodId){
	    	askZfbQrPay(goodId);
	    	askWeixinQrPay(goodId);
	    }
	    
	    void appeal(){
	        /*退款申请*/
	        ApplyRefundReq refundReq = new ApplyRefundReq();
	        refundReq.setFeedId(deviceInterfaceAdapter.getDevice().getFeedId());
	        refundReq.setOrderId(System.currentTimeMillis());
	        refundReq.setPhone("15824135596");
	        new RefundApplyAsyncTask(){
	            @Override
	            protected void onPostExecute(TPResponse rsp) {
	                if(rsp.getErrcode()==TPConstants.Errcode.SUCCESS){
	                  //  Toast.makeText(getActivity(),"申请退款成功",Toast.LENGTH_LONG).show();
	                }
	            }
	        }.execute(refundReq);
	    }
		void addNetworkChangedCallback(){
			
			
			NetChangedReciever.setCallBack(new NetChangedReciever.CallBack() {
				@Override
				public void netWorkChanged(boolean connected) {
					Log.e(Tag, "!!!!!!!!!!!!!!netWorkChanged "+connected);
					myToast.toastShow("netWorkChanged "+connected);
					if(connected){
						isConnectToServer=true;
						 sendMsgToHandler(Handler_netDisp, context.getString(R.string.hasnet));
						initPayServer();
						if(isConnectToServer)
							updatePrice();
					}else{
						isConnectToServer=false;
						sendMsgToHandler(Handler_netDisp, context.getString(R.string.nonet));
					}
				}
			});
		}

		
 
		    
		    
		    void mc_dropCup(){
		    	myHandler.post(new Runnable() {		
					@Override
					public void run() {
						showStateDialog(coffeeType,context.getString(R.string.startDropCup));
					}
				});
		    	
		    	deliveryController.cmd_dropCup(); 	
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
		    	deliveryController.cmd_isCupReady();
		    }
		    /**
		     * 制作咖啡接口
		     *此函数触发出粉/出咖啡 
		     */
		    void mc_makeCoffee(int type){
		    	makingStep=0;
		    	//test
		    	myHandler.post(new Runnable() {		
					@Override
					public void run() {
						
					//	tCoffeeDisp=context.getString(R.string.dropPowder);
					//	t_coffeeType.setText(tCoffeeDisp);
						
						
						//t_payType.setText(R.string.dropPowder);
					}
				});
		    	switch(type){
		    	case CoffeeType1://美式
		    		myMachine.dropCoffee();
			    //	deliveryController.cmd_pushLeftPowder(70, 50,150);//落糖
			    	deliveryController.cmd_pushLeftPowder(70, 10,50);//落糖
			    	break;
		    	case CoffeeType2://卡布
		    		myMachine.dropCoffee();		
		    		deliveryController.cmd_pushCenterPowder(70, 10,30);
		    		deliveryController.cmd_pushLeftPowder(70, 10,30);
		    		break;
		    	case CoffeeType3://意式
		    		myMachine.dropCoffee();
		    		makingStep|=PowderFinish;
		    		break;
		    	case CoffeeType4://拿铁
		    		myMachine.dropCoffee();
		    		deliveryController.cmd_pushCenterPowder(70,10,40);
		    		deliveryController.cmd_pushLeftPowder(70,10,30);
		    		break;
		    	case CoffeeType5://糖
		    		makingStep|=CoffeeFinish;
		    		deliveryController.cmd_pushLeftPowder(70, 20,60);
		    		
		    		break;
		    	case CoffeeType6://奶
		    		makingStep|=CoffeeFinish;
		    		deliveryController.cmd_pushCenterPowder(70, 20,60);
		    		break;
		    	}
		    }
		    /**
		     *没有杯子了
		     *提示用户，并通知服务器做退款处理
		     */
		    
		    
		    
		    
		    void mc_noCups(){
		    	hasCup=false;
		    	myHandler.post(new Runnable() {		
					@Override
					public void run() {
					//	t_coffeeType.setText(R.string.noCup);
					}
				});
		    	myHandler.postDelayed(new Runnable() {		
					@Override
					public void run() {
						closeOder();
						sendMsgToHandler(Handler_mcDisp, ParseReceiveCommand.getDispStringId(context));
						setEnable(false);
						//mc_readCup();
					}
				},2000);
		    }
		    /**
		     *没有水了
		     *提示用户，并通知服务器做退款处理
		     */
		    void mc_noWater(){
		    	hasWater=false;
		    	myHandler.postDelayed(new Runnable() {		
					@Override
					public void run() {
						//t_payType.setText(R.string.noWater);
						sendMsgToHandler(Handler_mcDisp, ParseReceiveCommand.getDispStringId(context));
						setEnable(false);
					}
				},1000);

		    }

		    /**
		     * 
		     * 有脏杯子没有取走，提示用户拿走脏杯子
		     */
		    void mc_hasDirtyCup(){
		    	myHandler.post(new Runnable() {		
					@Override
					public void run() {
						
						//t_coffeeType.setText(R.string.hasDirtyCup);
						pauseTimeOutTime();
					}
				});
		    	
		    }
		    
		    
		    
		    void mc_startDropCup(){
		    	resumeTimeOutTime();
		    	myHandler.post(new Runnable() {		
		    		@Override
		    		public void run() {
		    	
		    			//t_coffeeType.setText(R.string.startDropCup);
		    		}
		    	});
		    	
		    }
	/**
	 * 咖啡制作完成
	 * 等待取杯
	 */
		   void stepTakingCup(){
		   		tradeStep=StepTakingCup;
		   		//deliveryController.cmd_cancelLight();
		   		//deliveryController.cmd_greenLight();
		   		//tPayDisp=context.getString(R.string.finished);
		   		tPayDisp=context.getString(R.string.takeCup);
//		    	String dsp=tPayDisp+"("+CloseCnt_TakingCup+"s)";
//				sendMsgToHandler(Handler_tPay, dsp);
		   		//sendMsgToHandler(Handler_tPay, tPayDisp); 
				if(timeOutTask!=null){
					timeOutTask.closeCnt=CloseCnt_TakingCup;
				}
		   		//startCloseTimer(CloseCnt_TakingCup);
		   		deliveryController.cmd_QueryCupToken();
		   }
		    
		   
//		   void addedCloseTask(int cnt){
//				if(timeOutTask!=null){
//					timeOutTask.closeCnt=CloseCnt_TakingCup;
//				}
//		   }
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
		    	myHandler.post(new Runnable() 
		    	{		
					@Override
					public void run() {
					
					//	t_coffeeType.setText(R.string.cupStuck);
					}
				});
		    	deliveryController.cmd_isCupReady();
		    }
		    /**
		     * 跟辅助板通信超时
		     * 
		     */
		    void mc_toAssistControllerTimeOut(){
		    	myHandler.post(new Runnable() {		
					@Override
					public void run() {
						
					//	t_coffeeType.setText(R.string.toAssisTimeOut);
					}
				});
		    	
		    }

		    
		    class CloseTimeTask extends TimerTask{
		    	
		    	int closeCnt=0;
		    	boolean inTask=false;
				@Override
				public void run() {
					//isTrading=false;
					myHandler.post(new Runnable() {
						
						@Override
						public void run() {
							if(inTask){
								if(closeCnt-->0){
									String dsp=tPayDisp+"("+closeCnt+"s)";
									sendMsgToHandler(Handler_tPay, dsp);
		
								}else{
									closeOder(); //超时后关闭交易
								}
							}
						}
					});	
				}
		    	
		    }
		    class TimerOutTask extends TimerTask{
		    	
		    	boolean inTask=false;
		    	int closeCnt=0;
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
										String dsp2=tPayDisp+"("+closeCnt+"s)";
										sendMsgToHandler(Handler_tPay, dsp2);
									}else{
										String dsp=context.getString(R.string.alLeftTime)+closeCnt+"s";
										sendMsgToHandler(Handler_tPay, dsp);
									}
		
								}else{
									tradeTimeOut();	
								}
							}
						});	
					}
				}
		    	
		    }
	//交易超时应该退款
		    void tradeTimeOut(){
		    	//这个时候应该可以选择重试或者退款！！！
		    	sendMsgToHandler(Handler_TimeOut, "");
		    }
		    
		    void startCloseTimer(int cnt){
		    	
		    	if(closeTimer==null){
		    		closeTimer=new Timer();
		    	}
		    	if(closeTask==null){
		    		closeTask=new CloseTimeTask();
		    		closeTask.closeCnt=cnt;
		    		closeTask.inTask=true;
		    		closeTimer.schedule(closeTask, 1000,1000);
		    	}else{
		    		if(closeTask.cancel()){
		    			closeTask=new CloseTimeTask();
		    			closeTask.inTask=true;
		    			closeTask.closeCnt=cnt;
		    			closeTimer.schedule(closeTask, 1000,1000);
		    		}
		    	}	
		    	
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
			private void startTimeOutTimer(int cnt){

			//	Log.d("ioctrl","startAckTimer############");
				if(closeTimer==null){
					closeTimer=new Timer();
				}
				cancelTimeOutTask();
				timeOutTask=new TimerOutTask();
				timeOutTask.inTask=true;
				timeOutTask.closeCnt=cnt;
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
			 void setEnable(boolean enable){
				 
			 }
				void closeOder(){
					tradeStep=StepNone;		
					cancelCloseTimerTask();
					cancelTimeOutTask();
					deliveryController.cancelQueryTimerTask();
					deliveryController.cmd_readError();//交易完成之后读取水位
						
//					layout_makingMask.setVisibility(View.GONE);
//					layout_qr.setVisibility(View.GONE);
//					t_coffeeType.setText(R.string.pleaseChooseCoffee);
//					t_payType.setText(R.string.pleaseChoosePay);
//					
//					setCoffeeIconRadio(0);
//					setPayIconRadio(0);
				}
				
				void setMakingState(String state){
					if(stateDialog!=null&&stateDialog.isAlive()){
						stateDialog.setState(state);
					}
				}
				void setCoffeeType(int type){
					
					if(type==-1){
						cur_goodId=-1;
						//t_coffeeType.setText(R.string.pleaseChooseCoffee);
						//setPayEnable(false);
						return ;
					}
					coffeeType=type;
					if(isDebug){
						startMaking();
					}
					//setPayEnable(true);
					if(goodId.containsKey(type)){
						cur_goodId=goodId.get(type);
					
					//	tCoffeeDisp="已选择"+goodName.get(cur_goodId)+"|￥"+goodPrice.get(cur_goodId).toString();
					//	t_coffeeType.setText(tCoffeeDisp);
					//	t_payType.setText(R.string.pleaseChoosePay);
						
					}
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
}
