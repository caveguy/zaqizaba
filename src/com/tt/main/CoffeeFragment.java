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
	

	private final int WeixinPay=2;
	private final int AliPay=1;
	private final int Handler_mcDisp=1002;
	private final int Handler_qr=1001;
	private final int Handler_tPay=1003;
	private final long NoGoodSelected=-1;
	CloseTimeTask closeTask=null;
	TextView t_coffeeType,t_payType;
	DeliveryProtocol deliveryController=null;
	private MachineProtocol myMachine=null;	
	ToastShow myToast;
	RelativeLayout layout_qr;
	LinearLayout layout_mask;
	CheckBox btn_coffee1,btn_coffee2,btn_coffee3,btn_coffee4,btn_coffee5,btn_coffee6;
	CheckBox btn_pay1,btn_pay2,btn_pay3,btn_pay4;
	ImageView img_qr;
	Button btn_cancel,btn_other;
	Timer closeTimer=null;
	long cur_goodId=-1;
	String tPayDisp=null;
	byte makingStep=0;  //出粉跟出咖啡完成标志
	int tradeStep=0;    //整个交易步骤
	
	
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
    	//setPayEnable(false);
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
    	    			layout_mask.setVisibility(View.VISIBLE);
    			}
    		});

    	}
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
        	
        });
        
        coffeeDeviceEvent = new CoffeeDeviceEvent() {

        	@Override
            public void onLoad() {
                super.onLoad();
                /*获得设备商品列表*/
                
             //Toast.makeText(getActivity(), "连接成功", Toast.LENGTH_LONG).show();
                myToast.toastShow("连接服务器成功");
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
	                           myToast.toastShow("rsp.getErrcode() i="+i);
	                           setGoodMsg();
	                        }
//	                    }catch(Exception e){
//	                    	Log.e(Tag, e.toString());
//	                    }
                    }
                }.execute(deviceInterfaceAdapter.getDevice().getFeedId());
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
				mc_dropCup();
			}
			@Override
			public void onReceiveTranspTransfer(String arg0) {
				mylog.log_i("onReceiveTranspTransfer ="+arg0);	
				updateMsgCallBack(arg0);
			}

        	
        };
        deviceInterfaceAdapter = new CoffeeDeviceInterfaceAdapter(getActivity(),myHandler,coffeeDeviceEvent);
	
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
					if(dispString!=oldMcString){
						mylog.log_i("****Machine String****"+dispString);
						if(dispString.equals(getString(R.string.cmd1_pressRinse))){
							myMachine.sendCleanCmd();
						}
						else if(dispString.equals(getString(R.string.cmd1_ready))){
						 if(tradeStep!=StepNone){  //交易状态下，字符串变成准备就绪说明出咖啡完成
								mc_coffeeDroped();
							}
						}
						
					}
					oldMcString=dispString;
				 }
				 else if(cmd==0x19){
					myMachine.initMachine();
				 }
			}

			@Override
			public void onFault() {
				setEnble(false);
			}

			@Override
			public void onWork() {
				setEnble(true);
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
			makingStep=0;
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
			makingStep=0;
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
	        }
	        };
	    };

	    
	    
	    void mc_dropCup(){
	    	tradeStep=StepMaking; //进入制作阶段
	    	deliveryController.cmd_dropCup();
	    }
	    /**
	     * 制作咖啡接口
	     *此函数触发出粉/出咖啡 
	     */
	    void mc_makeCoffee(int type){
	    	//test
	    	myHandler.post(new Runnable() {		
				@Override
				public void run() {
					t_payType.setText(R.string.dropPowder);
				}
			});
	    	switch(type){
	    	case CoffeeType1:
	    		myMachine.dropCoffee();
		    	deliveryController.cmd_pushWater(150);
		    	//deliveryController.cmd_pushLeftPowder(70, 50,150);
		    	break;
	    	case CoffeeType2:
	    		myMachine.dropCoffee();
	    		deliveryController.cmd_pushCenterPowder(70, 50,150);
	    		deliveryController.cmd_pushLeftPowder(70, 50,150);
	    		break;
	    	case CoffeeType3:
	    		myMachine.dropCoffee();
	    		break;
	    	case CoffeeType4:
	    		myMachine.dropCoffee();
	    		//deliveryController.cmd_pushRightPowder(70,50,150);
	    		deliveryController.cmd_pushLeftPowder(70,50,150);
	    		break;
	    	case CoffeeType5:
	    		deliveryController.cmd_pushCenterPowder(70, 50,150);
	    		break;
	    	case CoffeeType6:
	    		deliveryController.cmd_pushLeftPowder(70, 50,150);
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
	    	
	    }
	    /**
	     * 卡杯了
	     * 提示用户手动取杯，并通知服务器
	     */
	    void mc_cupStucked(){
	    	myHandler.post(new Runnable() {		
				@Override
				public void run() {
					t_payType.setText(R.string.cupStuck);
				}
			});
	    	
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
	   		deliveryController.cmd_cancelLight();
	   		deliveryController.cmd_greenLight();
	   		tPayDisp=getActivity().getString(R.string.finished);
	    	String dsp=tPayDisp+"("+CloseCnt_TakingCup+"s)";
			sendMsgToHandler(Handler_tPay, dsp);
	   		//sendMsgToHandler(Handler_tPay, tPayDisp); 
	   		startCloseTimer(CloseCnt_TakingCup);
	   }
	    
	    /**
	     * 出粉完成
	     * 
	     */
	    void mc_powderDroped(){
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
	    	
	    }
	    void mc_cupStuck(){
	    	
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
						if(closeTask.closeCnt-->0){
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