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
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android_serialport_api.DeliveryProtocol;
import android_serialport_api.DeliveryProtocol.CallBack;
import android_serialport_api.MachineProtocol;
import android_serialport_api.ParseReceiveCommand;
import coffee.shop.po.DeviceGoods;
import coffee.shop.po.request.ApplyRefundReq;
import coffee.shop.po.request.MakeOrderReq;
import coffee.shop.po.response.MakeOrderRsp;
import coffee.shop.po.response.QueryDeviceGoodsRsp;

import com.example.coffemachinev2.R;
import com.tt.util.Encode;
import com.tt.util.TTLog;
import com.tt.util.ToastShow;

public class CoffeeFragment extends Fragment implements OnClickListener,android.widget.CompoundButton.OnCheckedChangeListener {

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
	private boolean dispDevLayout=false;

	private final int WeixinPay=2;
	private final int AliPay=1;
	private final int Handler_mcDisp=1002;
	private final int Handler_qr=1001;
	private final int Handler_tPay=1003;
	private final int Handler_tMask=1004;
	private final long NoGoodSelected=-1;
	CloseTimeTask closeTask=null;
	TextView t_coffeeType,t_payType;
	TextView t_mcDetail;
	DeliveryProtocol deliveryController=null;
	private MachineProtocol myMachine=null;	
	ToastShow myToast;
	RelativeLayout layout_qr;
	LinearLayout layout_mask;
	CheckBox btn_coffee1,btn_coffee2,btn_coffee3,btn_coffee4,btn_coffee5,btn_coffee6;
	CheckBox btn_pay1,btn_pay2,btn_pay3,btn_pay4;
	CheckBox btn_debug;
	ImageView img_qr;
	Button btn_cancel,btn_other,btn_clean,btn_mskCancel;
	Timer closeTimer=null;
	long cur_goodId=-1;
	String tPayDisp=null;
	byte makingStep=0;  //出粉跟出咖啡完成标志
	int tradeStep=0;    //整个交易步骤
	boolean isDeliverEnable=false;  //辅助板是否工作正常
	boolean isMcEnable=false;      //咖啡机是否工作正常
	boolean dropcupMode=false ;   //杯子模式，false:检查到有杯子就打咖啡，true：落杯后打咖啡
	RadioButton radioCup1,radioCup2;
	boolean isDebug=false;
	byte mcWindowLast=0;
	HashMap<Integer,Long> goodId=new HashMap<Integer,Long>();
	HashMap<Long,String>	goodName=new HashMap<Long,String>();
	HashMap<Long,BigDecimal>	goodPrice=new HashMap<Long,BigDecimal>();
	  //存放商品信息
	
    private CoffeeDeviceInterfaceAdapter deviceInterfaceAdapter;
    private CoffeeDeviceEvent coffeeDeviceEvent;
    TTLog mylog=null;
	int oldCheckedId=0;
	byte status;
	private String oldMcString=null;
	//Handler myHandler;
	private final String Tag="CoffeeFrag";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_coffee, container, false);
        
        mylog=new TTLog(Tag,true);
        
        initView(rootView);
        initMachines();
  //      myHandler =new Handler();
//        Button mybutton = null;
//        mybutton.setText("hello");
        
        return rootView;
    }

    void initView(View view){
    	myToast =new ToastShow(getActivity());
    	btn_pay1=(CheckBox)view.findViewById(R.id.radio_pay1);
    	btn_pay2=(CheckBox)view.findViewById(R.id.radio_pay2);
    	btn_pay3=(CheckBox)view.findViewById(R.id.radio_pay3);
    	btn_pay4=(CheckBox)view.findViewById(R.id.radio_pay4);
     	btn_coffee1=(CheckBox)view.findViewById(R.id.radio_1);
     	btn_coffee2=(CheckBox)view.findViewById(R.id.radio_2);
     	btn_coffee3=(CheckBox)view.findViewById(R.id.radio_3);
     	btn_coffee4=(CheckBox)view.findViewById(R.id.radio_4);
     	btn_coffee5=(CheckBox)view.findViewById(R.id.radio_5);
     	btn_coffee6=(CheckBox)view.findViewById(R.id.radio_6);
    	btn_pay1.setOnCheckedChangeListener(this);
    	btn_pay2.setOnCheckedChangeListener(this);
    	btn_pay3.setOnCheckedChangeListener(this);
    	btn_pay4.setOnCheckedChangeListener(this);
    	btn_coffee1.setOnCheckedChangeListener(this);
    	btn_coffee2.setOnCheckedChangeListener(this);
    	btn_coffee3.setOnCheckedChangeListener(this);
    	btn_coffee4.setOnCheckedChangeListener(this);
    	btn_coffee5.setOnCheckedChangeListener(this);
    	btn_coffee6.setOnCheckedChangeListener(this);
    	layout_qr=(RelativeLayout)view.findViewById(R.id.layou_qr);
    	img_qr=(ImageView)view.findViewById(R.id.img_qr);
    	btn_cancel=(Button)view.findViewById(R.id.btn_cancel);
    	btn_other=(Button)view.findViewById(R.id.btn_other);
    	btn_cancel.setOnClickListener(this);
    	btn_other.setOnClickListener(this);
    	t_coffeeType=(TextView)view.findViewById(R.id.t_coffeeType);
    	t_payType=(TextView)view.findViewById(R.id.t_payType);
    	layout_mask=(LinearLayout)view.findViewById(R.id.layout_mask);
    	btn_mskCancel=(Button)view.findViewById(R.id.btn_mskCancel);
    	btn_clean=(Button)view.findViewById(R.id.btn_clean);
    	btn_debug=(CheckBox)view.findViewById(R.id.btn_debug);
    	btn_debug.setOnCheckedChangeListener(this);
    	
    	btn_mskCancel.setOnClickListener(this);
    	btn_clean.setOnClickListener(this);
    	
    	t_mcDetail=(TextView)view.findViewById(R.id.t_mcDetail);
    	radioCup1=(RadioButton)view.findViewById(R.id.radio_cup1);
    	radioCup2=(RadioButton)view.findViewById(R.id.radio_cup2);
    	if(dropcupMode){
    		radioCup1.setChecked(true);	
    	}else{
    		radioCup2.setChecked(true);
    	}
    	radioCup1.setOnCheckedChangeListener(this);
    	radioCup2.setOnCheckedChangeListener(this);
    	//setPayEnable(false);
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
    	myMachine=new MachineProtocol(getActivity());
    	mcSetCallBack();
        deliveryController=new DeliveryProtocol(getActivity());
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
				mc_makeCoffee(getCurType());
			}
        	
        });
        
        coffeeDeviceEvent = new CoffeeDeviceEvent() {

        	@Override
            public void onLoad() {
                super.onLoad();
                /*获得设备商品列表*/
                
             //Toast.makeText(getActivity(), "连接成功", Toast.LENGTH_LONG).show();
                myToast.toastShow("连接服务器成功");
                updatePrice();

            }
			@Override
			public void onPayFail(Long arg0) {				
				myHandler.post(new Runnable() {	
					@Override
					public void run() {
						
						 myToast.toastShow("支付失败");	
						 layout_qr.setVisibility(View.GONE);
						 cancelCloseTimer();
					}
				});	
			}

			@Override
			public void onPaySuccess(Long arg0) {
				mylog.log_i("onPaySuccess!!!");
				myHandler.post(new Runnable() {
					@Override
					public void run() {
						t_payType.setText(R.string.paySuccess);
						myToast.toastShow(R.string.paySuccess);
						layout_qr.setVisibility(View.GONE);
						cancelCloseTimer();
						//cancelCloseTimer(); //不能取消，否则按钮状态没有清除
						
					}
				});
				startMaking();
				
			}
			@Override
			public void onReceiveTranspTransfer(String arg0) {
				mylog.log_i("onReceiveTranspTransfer ="+arg0);	
				String updatePrice=getActivity().getString(R.string.update_Price);
				if(arg0.equals(updatePrice)){
					//更新价格
					 updatePrice();
				}else{
					updateMsgCallBack(arg0);
				}
			}

        	
        };
        deviceInterfaceAdapter = new CoffeeDeviceInterfaceAdapter(getActivity(),myHandler,coffeeDeviceEvent);
	
    }
    void startMaking(){
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
                       }
                       //myToast.toastShow("rsp.getErrcode() i="+i);
                       setGoodMsg();
                    }
//                }catch(Exception e){
//                	Log.e(Tag, e.toString());
//                }
            }
        }.execute(deviceInterfaceAdapter.getDevice().getFeedId());
    }
    
    
    void setGoodMsg(){
    	myHandler.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				setIconNames();
			}
		});
    	
    }
    
    
	public void getQtImage(String url) {

		final String filePath = getActivity().getCacheDir() + File.pathSeparator + "qtImage"
				+ ".jpg";
		int widthPix = 600;
		int heightPix = 600;
		boolean blCreated = Encode.createQRImage(url, widthPix, heightPix,
				null, filePath);

		if (blCreated) {
			myToast.toastShow(R.string.createQrSuccess);
			sendMsgToHandler(Handler_qr,filePath);
			

		} else {
			myToast.toastShow(R.string.createQrFailed);
		}
	}
    
    void askPay(long goodId,int payType){
        /*下单*/
        MakeOrderReq req = new MakeOrderReq();
        req.setFeedId(deviceInterfaceAdapter.getDevice().getFeedId());
        List<Long> goodsIds = new ArrayList<Long>();
        goodsIds.add(goodId);
        //goodsIds.add(2l);
        req.setGoodsIds(goodsIds);
        req.setPayType(payType);//1支付宝 2//微信
        new MakeOrderAsyncTask(){
            @Override
            protected void onPostExecute(MakeOrderRsp rsp) {
                if(rsp!=null && rsp.getErrcode()==0){
                  String url= rsp.getQrCodeUrl();
                   getQtImage( url);
                }
            }
           
        }.execute(req);
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
    
    
	void mcSetCallBack(){
		 ParseReceiveCommand.setCallBack(new ParseReceiveCommand.CallBack() {
			
			@Override
			public void onParsed(int cmd) {
				// TODO Auto-generated method stub
				 if(cmd==1){
					String dispString= ParseReceiveCommand.getDispStringId(getActivity());
					if(layout_mask.VISIBLE==View.VISIBLE){
						sendMsgToHandler(Handler_tMask, dispString)	;
						
					}
					if(dispString!=oldMcString){
						mylog.log_i("****Machine String****"+dispString);
						if(dispString.equals(getString(R.string.cmd1_pressRinse))){
							myMachine.sendCleanCmd();
						}
//						else if(dispString.equals(getString(R.string.cmd1_ready))){
//						 if(tradeStep!=StepNone&&oldMcString.equals(getString(R.string.cmd1_espresso))){  //交易状态下，字符串变成准备就绪说明出咖啡完成
//								mc_coffeeDroped();
//							}
//						}
						
					}
					oldMcString=dispString;
				 }
				 else if(cmd==0x19){
					 byte windowstate=ParseReceiveCommand.getWindow();
					// myToast.toastShow("cmd0x19="+windowstate);
					 if(windowstate==2&&mcWindowLast==5){
						 mc_coffeeDroped();
					 }					 
					 mcWindowLast=windowstate;
					//myMachine.initMachine();
				 }
			}

			@Override
			public void onFault() {
				setEnble(false);
			}

			@Override
			public void onWork() {
				if(!dispDevLayout){
					setEnble(true);
				}
			}
		});
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_cancel:
			//还需要向服务器上报取消订单
			closeOder();
			break;
		case R.id.btn_other:
			useOtherPay();
			break;
		case R.id.btn_hand:
			mylog.log_i("btn_hand!!");
			deliveryController.cmd_handShake();
			break;
		case R.id.btn_clean:
			myMachine.sendCleanCmd();
		case R.id.btn_mskCancel:
			leaveDevMode();
			break;
//		case R.id.btn_dropCup:
//			Log.e(Tag,"btn_dropCup!!");
//			deliveryController.cmd_dropCup();
//			break;
//		case R.id.btn_status:
//			deliveryController.cmd_readState();
//			break;
		}
	}



		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			
			int id =buttonView.getId();
			if(isChecked){
				setPayIconRadio(id);
				setCoffeeIconRadio(id);
			}
			switch(id){
			case R.id.radio_pay1:

				if(isChecked){
					if(cur_goodId==NoGoodSelected){
						myToast.toastShow(R.string.pleaseChooseCoffee);
						setPayIconRadio(0);
						break;
					}
					setWeixinpay();
				}
				else{
					cancelCloseTimer();
					layout_qr.setVisibility(View.GONE);
					t_payType.setText(R.string.pleaseChoosePay);
				}
				break;
			case R.id.radio_pay2:
				if(isChecked){
					if(cur_goodId==NoGoodSelected){
						myToast.toastShow(R.string.pleaseChooseCoffee);
						setPayIconRadio(0);
						break;
					}
					setAlipay();
				}
				else{
					cancelCloseTimer();
					layout_qr.setVisibility(View.GONE);
					t_payType.setText(R.string.pleaseChoosePay);
				}
				break;
			case R.id.radio_pay3:

				if(isChecked){
					if(cur_goodId==NoGoodSelected){
						myToast.toastShow(R.string.pleaseChooseCoffee);
						setPayIconRadio(0);
						break;
					}
					setAppealPay();
				}
				else
				;	
				break;
			case R.id.radio_pay4:
				if(isChecked){
					if(cur_goodId==NoGoodSelected){
						myToast.toastShow(R.string.pleaseChooseCoffee);
						setPayIconRadio(0);
						break;
					}
					setCardPay();
				}
				else
				;	
				break;
			case R.id.radio_1:
				if(isChecked){
					setCoffeeType(0);
				}
				else{
					setCoffeeType(-1);
				}
				break;
			case R.id.radio_2:
				if(isChecked){
					setCoffeeType(1);
				}
				else{
					setCoffeeType(-1);
				}
				break;
			case R.id.radio_3:
				if(isChecked){
					setCoffeeType(2);
				}
				else{
					setCoffeeType(-1);
				}
				break;
			case R.id.radio_4:
				if(isChecked){
					setCoffeeType(3);
				}
				else{
					setCoffeeType(-1);
				}
				break;
			case R.id.radio_5:
				if(isChecked){
					setCoffeeType(4);
				}
				else{
					setCoffeeType(-1);
				}
				break;
			case R.id.radio_6:
				if(isChecked){
					setCoffeeType(5);
				}
				else{
					setCoffeeType(-1);
				}
				break;
			case R.id.radio_cup1:
				if(isChecked){
					dropcupMode=true;
				}else{
					dropcupMode=false;
				}
				break;
			case R.id.radio_cup2:
				if(!isChecked){
					dropcupMode=true;
				}else{
					dropcupMode=false;
				}
				break;
			case R.id.btn_debug:
				if(isChecked){
					isDebug=true;
				}else{
					isDebug=false;
				}
				break;
			}

		
			
		}

		void setIconNames(){
			long id=0;
			if(goodId.containsKey(0)){
				id=goodId.get(0);
				String name=goodName.get(id);
				BigDecimal price=goodPrice.get(id);
				name=name+"|￥"+price.toString();
				btn_coffee1.setText(name);
			}
			if(goodId.containsKey(1)){
				id=goodId.get(1);
				String name=goodName.get(id);
				BigDecimal price=goodPrice.get(id);
				name=name+"|￥"+price.toString();
				btn_coffee2.setText(name);
			}
			if(goodId.containsKey(2)){
				id=goodId.get(2);
				String name=goodName.get(id);
				BigDecimal price=goodPrice.get(id);
				name=name+"|￥"+price.toString();
				btn_coffee3.setText(name);
			}
			if(goodId.containsKey(3)){
				id=goodId.get(3);
				String name=goodName.get(id);
				BigDecimal price=goodPrice.get(id);
				name=name+"|￥"+price.toString();
				btn_coffee4.setText(name);
			}
			if(goodId.containsKey(4)){
				id=goodId.get(4);
				String name=goodName.get(id);
				BigDecimal price=goodPrice.get(id);
				name=name+"|￥"+price.toString();
				btn_coffee5.setText(name);
			}
			if(goodId.containsKey(5)){
				id=goodId.get(5);
				String name=goodName.get(id);
				BigDecimal price=goodPrice.get(id);
				name=name+"|￥"+price.toString();
				btn_coffee6.setText(name);
			}
		}
//		void setPayEnable(boolean enable){
//			btn_pay1.setEnabled(enable);
//			btn_pay2.setEnabled(enable);
//			btn_pay3.setEnabled(enable);
//			btn_pay4.setEnabled(enable);
//		}
		
		void setCoffeeType(int type){
			
			if(type==-1){
				cur_goodId=-1;
				t_coffeeType.setText(R.string.pleaseChooseCoffee);
				//setPayEnable(false);
				return ;
			}else if(isDebug){
				startMaking();
			}
			//setPayEnable(true);
			if(goodId.containsKey(type)){
				cur_goodId=goodId.get(type);
			
				String disp="已选择"+goodName.get(cur_goodId)+"|￥"+goodPrice.get(cur_goodId).toString();
				
				t_coffeeType.setText(disp);
				t_payType.setText(R.string.pleaseChoosePay);
				
			}
		}
		

		
		
		void closeOder(){
			//isTrading=false;
			cancelCloseTimer();
			tradeStep=StepNone;
			layout_qr.setVisibility(View.GONE);
			t_coffeeType.setText(R.string.pleaseChooseCoffee);
			t_payType.setText(R.string.pleaseChoosePay);
			
			setCoffeeIconRadio(0);
			setPayIconRadio(0);
		}
		void useOtherPay(){
			cancelCloseTimer();
			layout_qr.setVisibility(View.GONE);
			t_payType.setText(R.string.pleaseChoosePay);
			setPayIconRadio(0);
		}
		void setWeixinpay(){		
			//isTrading=true;
			tradeStep=StepPay;
			 startCloseTimer(CloseCnt_pay); 
			askPay(cur_goodId,WeixinPay);
			tPayDisp=getActivity().getString(R.string.chooseWeixin);
	    	String dsp=tPayDisp+"("+CloseCnt_pay+"s)";
			t_payType.setText(dsp);
			layout_qr.setVisibility(View.VISIBLE);
		}
		void setAlipay(){
			//isTrading=true;
		//	makingStep=0;
			tradeStep=StepPay;
			startCloseTimer(CloseCnt_pay);
			askPay(cur_goodId,AliPay);
			tPayDisp=getActivity().getString(R.string.chooseZfb);
			String dsp=tPayDisp+"("+CloseCnt_pay+"s)";
			t_payType.setText(dsp);
			layout_qr.setVisibility(View.VISIBLE);
		}
		void setAppealPay(){
			
		}
		void setCardPay(){
			
		}
	
	void setPayIconRadio(int id){
		switch(id){
			case R.id.radio_pay1:
				btn_pay2.setChecked(false);
				btn_pay3.setChecked(false);
				btn_pay4.setChecked(false);
				break;
			case R.id.radio_pay2:
				btn_pay1.setChecked(false);
				btn_pay3.setChecked(false);
				btn_pay4.setChecked(false);
				break;
			case R.id.radio_pay3:
				btn_pay1.setChecked(false);
				btn_pay2.setChecked(false);
				btn_pay4.setChecked(false);
				break;
			case R.id.radio_pay4:
				btn_pay1.setChecked(false);
				btn_pay2.setChecked(false);
				btn_pay3.setChecked(false);
				break;
			case 0:
				btn_pay1.setChecked(false);
				btn_pay2.setChecked(false);
				btn_pay3.setChecked(false);	
				btn_pay4.setChecked(false);	
		
		}
	}
	void setCoffeeIconRadio(int id){
		switch(id){
			case R.id.radio_1:
				btn_coffee2.setChecked(false);
				btn_coffee3.setChecked(false);
				btn_coffee4.setChecked(false);
				btn_coffee5.setChecked(false);
				btn_coffee6.setChecked(false);
				break;
			case R.id.radio_2:
				btn_coffee1.setChecked(false);
				btn_coffee3.setChecked(false);
				btn_coffee4.setChecked(false);
				btn_coffee5.setChecked(false);
				btn_coffee6.setChecked(false);
				break;
			case R.id.radio_3:
				btn_coffee2.setChecked(false);
				btn_coffee1.setChecked(false);
				btn_coffee4.setChecked(false);
				btn_coffee5.setChecked(false);
				btn_coffee6.setChecked(false);
				break;
			case R.id.radio_4:
				btn_coffee1.setChecked(false);
				btn_coffee2.setChecked(false);
				btn_coffee3.setChecked(false);
				btn_coffee5.setChecked(false);
				btn_coffee6.setChecked(false);
				break;
			case R.id.radio_5:
				btn_coffee1.setChecked(false);
				btn_coffee2.setChecked(false);
				btn_coffee3.setChecked(false);
				btn_coffee4.setChecked(false);
				btn_coffee6.setChecked(false);
				break;
			case R.id.radio_6:
				btn_coffee1.setChecked(false);
				btn_coffee2.setChecked(false);
				btn_coffee3.setChecked(false);
				btn_coffee4.setChecked(false);
				btn_coffee5.setChecked(false);
				break;
			case 0:
				btn_coffee1.setChecked(false);
				btn_coffee2.setChecked(false);
				btn_coffee3.setChecked(false);
				btn_coffee4.setChecked(false);
				btn_coffee5.setChecked(false);		
				btn_coffee6.setChecked(false);		
		
		}
	}	
	
		private void sendMsgToHandler(int what,String dsp){
			Message msg=new Message();
			msg.what=what;
			msg.obj=dsp;
			myHandler.sendMessage(msg);
		}
	
	
	    private MyHandler myHandler = new MyHandler(getActivity()){
	        @Override
	        public void myHandleMessage(Message msg) {
	        	
	    		switch (msg.what) {
				case Handler_qr:
					img_qr.setImageBitmap(BitmapFactory.decodeFile(msg.obj.toString()));
					break;
				case Handler_mcDisp:
					myToast.toastShow(msg.obj.toString());
					break;
				case Handler_tPay:
					t_payType.setText(msg.obj.toString());
					break;
				case Handler_tMask:
					t_mcDetail.setText(msg.obj.toString());
	        }
	        };
	    };

	    
	    
	    void mc_dropCup(){
	    	tradeStep=StepMaking; //进入制作阶段
	    	deliveryController.cmd_dropCup();
	    }
	    void mc_readCup(){
	    	tradeStep=StepMaking; //进入制作阶段
	    	myHandler.post(new Runnable() {		
				@Override
				public void run() {
					t_payType.setText(R.string.putCup);
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
					t_payType.setText(R.string.dropPowder);
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
	    	myHandler.post(new Runnable() {		
				@Override
				public void run() {
					t_payType.setText(R.string.noCup);
				}
			});
	    	myHandler.postDelayed(new Runnable() {		
				@Override
				public void run() {
					mc_readCup();
				}
			},2000);
	    }

	    /**
	     * 
	     * 有脏杯子没有取走，提示用户拿走脏杯子
	     */
	    void mc_hasDirtyCup(){
	    	myHandler.post(new Runnable() {		
				@Override
				public void run() {
					t_payType.setText(R.string.hasDirtyCup);
				}
			});
	    	
	    }
	    void mc_startDropCup(){
	    	myHandler.post(new Runnable() {		
	    		@Override
	    		public void run() {
	    			t_payType.setText(R.string.startDropCup);
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
	   		tPayDisp=getActivity().getString(R.string.finished);
	    	String dsp=tPayDisp+"("+CloseCnt_TakingCup+"s)";
			sendMsgToHandler(Handler_tPay, dsp);
	   		//sendMsgToHandler(Handler_tPay, tPayDisp); 
	   		startCloseTimer(CloseCnt_TakingCup);
	   		deliveryController.cmd_QueryCupToken();
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
	    	myHandler.post(new Runnable() 
	    	{		
				@Override
				public void run() {
					t_payType.setText(R.string.cupStuck);
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
					t_payType.setText(R.string.toAssisTimeOut);
				}
			});
	    	
	    }

	    
	    class CloseTimeTask extends TimerTask{
	    	
	    	int closeCnt=0;
			@Override
			public void run() {
				//isTrading=false;
				myHandler.post(new Runnable() {
					
					@Override
					public void run() {
						if(closeCnt-->0){
							String dsp=tPayDisp+"("+closeCnt+"s)";
							sendMsgToHandler(Handler_tPay, dsp);

						}else{
							closeOder(); //超时后关闭交易
						}
					}
				});	
			}
	    	
	    }
	    
	    void startCloseTimer(int cnt){
	    	
	    	if(closeTimer==null){
	    		closeTimer=new Timer();
	    	}
	    	if(closeTask==null){
	    		closeTask=new CloseTimeTask();
	    		closeTask.closeCnt=cnt;
	    		closeTimer.schedule(closeTask, 1000,1000);
	    	}else{
	    		if(closeTask.cancel()){
	    			closeTask=new CloseTimeTask();
	    			//closeTimer.schedule(closeTask, CloseTime);
	    			closeTask.closeCnt=cnt;
	    			closeTimer.schedule(closeTask, 1000,1000);
	    		}
	    	}
	    	
	    	
	    }
	    void cancelCloseTimer(){
	    	if(closeTask!=null){
	    		closeTask.cancel();
	    		closeTask=null;
	    	}
	    }
		public void cleanTimer(){
			if(closeTimer!=null){
				closeTimer.cancel();
				closeTimer=null;
			}	
		}
	    
		@Override
		public void onDestroy() {
			deliveryController.cleanTimer();
			myMachine.cleanTimer();
			cleanTimer();
			super.onDestroy();
		}
		void enterDevMode(){
			dispDevLayout=true;
			myToast.toastShow("enter dev mode");
			layout_mask.setVisibility(View.VISIBLE);
			btn_mskCancel.setVisibility(View.VISIBLE);
			radioCup1.setVisibility(View.VISIBLE);
			radioCup2.setVisibility(View.VISIBLE);
		}
		void leaveDevMode(){
			dispDevLayout=false;
			layout_mask.setVisibility(View.GONE);
		}
		
	    void setEnble(boolean enable){
	    	if(enable){	
	    		myHandler.post(new Runnable() {
	    			@Override
	    			public void run() {
	    				layout_mask.setVisibility(View.GONE);
	    			}
	    		});
	    		
	    		
	    	}else{
	    		myHandler.post(new Runnable() {
	    			@Override
	    			public void run() {
	    	    		//if(!isTrading)//支付窗
	    	    		if(tradeStep==StepNone)//支付窗
	    	    		{
	    	    			layout_mask.setVisibility(View.VISIBLE);
	    	    			btn_mskCancel.setVisibility(View.GONE);
	    	    			radioCup1.setVisibility(View.GONE);
	    	    			radioCup2.setVisibility(View.GONE);
	    	    		}
	    			}
	    		});

	    	}
	    }
		
		
		
		///////////////////////回调接口////////////////////////////////

		CallBack callBack=null;
		public  void setCallBack(CallBack call) {
			// TODO Auto-generated method stub
			callBack = call;
		}

		public interface CallBack {
			
			void updateMsg(String msg);

		}
		
		private void updateMsgCallBack(String msg){
			if(callBack!=null)
				callBack.updateMsg(msg);
		}

		
		
		
	    
}