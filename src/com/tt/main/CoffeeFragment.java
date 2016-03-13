package com.tt.main;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
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
import com.tt.util.RadioGroupV2;
import com.tt.util.RadioGroupV2.OnCheckedChangeListener;

public class CoffeeFragment extends Fragment implements OnClickListener ,OnCheckedChangeListener{
	EditText et_water,et_powder;
	Button btn_start,btn_hand,btn_dropCup,btn_status;
	TextView t_status;
	DeliveryProtocol deliveryController=null;
	private MachineProtocol myMachine=null;
	RadioGroupV2 coffeeGroup=null;
	RelativeLayout layout_qr;
	//RadioGroupV2   payGroup=null;
	CheckBox btn_pay1,btn_pay2,btn_pay3,btn_pay4;
	ImageView img_qr;
	
	HashMap<Integer,Long> goodId=new HashMap<Integer,Long>();
	HashMap<Long,String>	goodName=new HashMap<Long,String>();
	HashMap<Long,BigDecimal>	goodPrice=new HashMap<Long,BigDecimal>();
	  //存放商品信息
	
    private CoffeeDeviceInterfaceAdapter deviceInterfaceAdapter;
    private CoffeeDeviceEvent coffeeDeviceEvent;
	
	int oldCheckedId=0;
	byte status;
	//Handler myHandler;
	private final String Tag="CoffeeFrag";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_coffee, container, false);
        initView(rootView);
        initMachines();
  //      myHandler =new Handler();

        
        return rootView;
    }

    void initView(View view){
    	coffeeGroup=(RadioGroupV2)view.findViewById(R.id.radio_group);
    	coffeeGroup.setOnCheckedChangeListener(this);
    	
     //	payGroup=(RadioGroupV2)view.findViewById(R.id.radio_group2);
     //	payGroup.setOnCheckedChangeListener(this);
     //	btn_pay1=(RadioButton)view.findViewById(R.id.radio_pay1);
    	btn_pay1=(CheckBox)view.findViewById(R.id.radio_pay1);
    	btn_pay2=(CheckBox)view.findViewById(R.id.radio_pay2);
    	btn_pay3=(CheckBox)view.findViewById(R.id.radio_pay3);
    	btn_pay4=(CheckBox)view.findViewById(R.id.radio_pay4);
    	btn_pay1.setOnCheckedChangeListener(checkListener);
    	btn_pay2.setOnCheckedChangeListener(checkListener);
    	btn_pay3.setOnCheckedChangeListener(checkListener);
    	btn_pay4.setOnCheckedChangeListener(checkListener);
    	layout_qr=(RelativeLayout)view.findViewById(R.id.layou_qr);
    	img_qr=(ImageView)view.findViewById(R.id.img_qr);
    }
    
    
    
    void initMachines(){
    	myMachine=new MachineProtocol(getActivity());
    	mcSetCallBack();
    	myMachine.initMachine();
        deliveryController=new DeliveryProtocol(getActivity());
        deliveryController.setCallBack(new CallBack(){

			@Override
			public void delivered() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void noDeliver() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void readStatus(byte st) {
				// TODO Auto-generated method stub
				status=st;
				myHandler.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						t_status.setText(""+status);
					}
				});
				
			}
        	
        });
        
        coffeeDeviceEvent = new CoffeeDeviceEvent() {

        	@Override
            public void onLoad() {
                super.onLoad();
                /*获得设备商品列表*/
                
                
                
                new QueryDeviceGoodsAsyncTask(){
                    @Override
                    protected void onPostExecute(QueryDeviceGoodsRsp rsp) {
                        if(rsp.getErrcode()==0){
                        	int i=0;
                           for(DeviceGoods goods:rsp.getGoods()){
                        	   Long id=goods.getGoodsId();
                        	   goodId.put(i++, id);
                        	   goodName.put(id,goods.getGoodsName());
                        	   goodPrice.put(id, goods.getGoodsPrice());
                           }
                           setGoodMsg();
                        }
                    }
                }.execute(deviceInterfaceAdapter.getDevice().getFeedId());
            }
			@Override
			public void onPayFail() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPaySuccess() {
				// TODO Auto-generated method stub
				
			}
        	
        };
        deviceInterfaceAdapter = new CoffeeDeviceInterfaceAdapter(getActivity(),myHandler,coffeeDeviceEvent);
        

        

    	
    }
    
    void setGoodMsg(){
    	
    	
    }
    
    
	public void getQtImage(String url) {

		final String filePath = getActivity().getCacheDir() + File.pathSeparator + "qtImage"
				+ ".jpg";
		int widthPix = 400;
		int heightPix = 400;
		boolean blCreated = Encode.createQRImage(url, widthPix, heightPix,
				null, filePath);

		if (blCreated) {
		//	displayType();
			//creatResultThread();//只有在二维码生成成功后才会启动是否支付成功的查询。
			//myToast.toastShow(R.string.createQrSuccess);
			Message msg=new Message();
			msg.what=1000;
			msg.obj=filePath;
			myHandler.sendMessage(msg);
			//img_qr.setImageBitmap(BitmapFactory.decodeFile(filePath));
			

		} else {
		//	myToast.toastShow(R.string.createQrFailed);
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
                    Toast.makeText(getActivity(),"申请退款成功",Toast.LENGTH_LONG).show();
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
				
				 }
				 else if(cmd==0x19){
					myMachine.initMachine();
				 }
			}

			@Override
			public void onFault() {
			}

			@Override
			public void onWork() {

			}
		});
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_push:
			Log.e(Tag,"btn_push!!");
			int leftPowder=Integer.parseInt(et_powder.getText().toString());
			int leftWater=Integer.parseInt(et_water.getText().toString());
			deliveryController.cmd_pushLeftPowder(leftPowder, leftWater);
			break;
		case R.id.btn_hand:
			Log.e(Tag,"btn_hand!!");
			deliveryController.cmd_handShake();
			break;
		case R.id.btn_dropCup:
			Log.e(Tag,"btn_dropCup!!");
			deliveryController.cmd_dropCup();
			break;
		case R.id.btn_status:
			deliveryController.cmd_readBusy();
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroupV2 radioGroupV2, int checkedId) {
		if(radioGroupV2==coffeeGroup){
		if(oldCheckedId!=checkedId){
			switch(checkedId){
				case R.id.radio_1:
					Log.e(Tag,"radio_1 clicked!");
					break;
				case R.id.radio_2:
	
					break;
				case R.id.radio_3:

					break;
				case R.id.radio_4:
					
					break;
				case R.id.radio_5:
					
					break;
				case R.id.radio_6:
					
					break;
					
			}
		}
		}

		
		oldCheckedId=checkedId;
	}
	android.widget.CompoundButton.OnCheckedChangeListener checkListener
	=new android.widget.CompoundButton.OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			
			int id =buttonView.getId();
			if(isChecked){
				setPayIconRadio(id);
			}
			switch(id){
			case R.id.radio_pay1:
				if(isChecked){
					askPay(1l,2);
					layout_qr.setVisibility(View.VISIBLE);
				}
				else
					
					layout_qr.setVisibility(View.GONE);
				break;
			case R.id.radio_pay2:
				if(isChecked){
					askPay(2l,2);
					layout_qr.setVisibility(View.VISIBLE);
				}
				else
					layout_qr.setVisibility(View.GONE);
				break;
			case R.id.radio_pay3:
				
				break;
			case R.id.radio_pay4:
				
				break;
			
			}
			
		}
		
	};
	
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
		
		}
	}
	
	    private MyHandler myHandler = new MyHandler(getActivity()){
	        @Override
	        public void myHandleMessage(Message msg) {
	    		switch (msg.what) {
				case 1000:
					img_qr.setImageBitmap(BitmapFactory.decodeFile(msg.obj.toString()));
	        }
	    };
	    };

}