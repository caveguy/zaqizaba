package android_serialport_api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.coffemachinev3.R;

public class AssistProtocol {
	Context context;
	byte[] sendData=null;
	boolean hasAck=true;  //默认有回复，为了让第一条信息发送出去
	int ackCnt=0;
	SerialPortUtil serialPortUtil=null;
	private SerialPort mSerialPort;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread; 
	
	public final static byte BIT0=(byte) 0x01;
	public final static byte BIT1=(byte) 0x02;
	public final static byte BIT2=(byte) 0x04;
	public final static byte BIT3=(byte) 0x08;
	public final static byte BIT4=(byte) 0x10;
	public final static byte BIT5=(byte) 0x20;
	public final static byte BIT6=(byte) 0x40;
	public final static byte BIT7=(byte) 0x80;	
	
	final byte Cmd_handshake=0x11;
	final byte Cmd_dropCup=0x21;
	final byte Cmd_Light=0x22;
	final byte Cmd_setPowderDegree=0x23;
	final byte Cmd_setWater=0x24;
	final byte Cmd_push=0x25;
	final byte Cmd_setTemper=0x26;

	final byte Cmd0x25_d0bit0_ch1LPush=BIT0;
	final byte Cmd0x25_d0bit1_ch1RPush=BIT1;
	final byte Cmd0x25_d0bit2_ch2LPush=BIT2;
	final byte Cmd0x25_d0bit3_ch2RPush=BIT3;
	final byte Cmd0x25_d0bit4_ch3LPush=BIT4;
	final byte Cmd0x25_d0bit5_ch3RPush=BIT5;
	final byte Cmd0x25_d0bit6_ch4LPush=BIT6;
	final byte Cmd0x25_d0bit7_ch4RPush=BIT7;
	
	//当前查询处于什么状态
	final byte QueryStep_handshake=BIT0; //闲时握手阶段
	final byte QueryStep_takingDirtyCup=BIT1; //脏杯子是否取走
	final byte QueryStep_isCupReady=BIT2; //是否准备好杯子--手动放杯模式
	final byte QueryStep_dropCup=BIT3;		//落杯阶段
	final byte QueryStep_flowing=BIT4;		//出粉/水阶段
	final byte QueryStep_takingCup=BIT5;	//取杯阶段
	final int ReciveLength=8;
	
	//错误bit
	public static  final byte Fault_noWater=BIT0;
	public static final byte Fault_noCup=BIT1;
	public static final byte Fault_timeOut=BIT2;
	public static final byte Fault_1heating=BIT3;
	public static final byte Fault_2heating=BIT4;
	final byte Key1=BIT0;
	final byte Key2=BIT1;
	final byte Key3=BIT2;
	final byte Key4=BIT3;
	
	byte fault_state=0; //0代表没有错误
	byte old_fault_state=0; //0代表没有错误
	
	//闪灯及开门
	byte cmd0x22_d0_redFrq=0;
	byte cmd0x22_d1_greenFrq=0;
	byte cmd0x22_d2_openDoor_light=0;
	//	设置粉溶度
	byte cmd0x23_d0_ch1L_degree=0;
	byte cmd0x23_d0_ch1R_degree=0;
	byte cmd0x23_d1_ch2L_degree=0;
	byte cmd0x23_d1_ch2R_degree=0;
	byte cmd0x23_d2_ch3L_degree=0;
	byte cmd0x23_d2_ch3R_degree=0;
	byte cmd0x23_d3_ch4L_degree=0;
	byte cmd0x23_d3_ch4R_degree=0;
	//设置出水量
	byte cmd0x24_d0_ch1_water=0x02;
	byte cmd0x24_d1_ch2_water=0x02;
	byte cmd0x24_d2_ch3_water=0x02;
	byte cmd0x24_d3_ch4_water=0x02;
	//启动出粉
	byte cmd0x25_d0_Flowing=0;
	
	//温度和回差
	byte cmd0x26_d0_ch1Temper=0;
	byte cmd0x26_d1_ch1Backlash=0;
	byte cmd0x26_d2_ch2Temper=0;
	byte cmd0x26_d3_ch2Backlash=0;
	
//****************接收解析*******************************/
	byte inD0_bit0_2_cupState=0;
	byte dropCupState_old=0;
	byte inD0_bit3_4_flowState=0;
	byte flowState_old=0;
	boolean inD0_bit5_firstHeatingState=false;
	boolean inD0_bit6_secondHeatingState=false;
	boolean inD0_bit7_waterState=false;
	boolean inD1_bit0_key1=false;
	boolean inD1_bit1_key2=false;
	boolean inD1_bit2_key3=false;
	boolean inD1_bit3_key4=false;

	boolean inD2_bit0_doorState=false;
	boolean inD3_bit1_noCup=false;
	boolean inD3_bit3_noDirtyCup=false;//
	byte inD2_H8bitInputState=0;
	byte inD3_L8bitInputState=0;
	byte query_step=QueryStep_handshake;

	
		
	final int SendTimerDuaration=300;
	final int AckTimerDuaration=200;
	final int QueryTimerDuaration=500;
//	final int ErrorQueryTimerDuaration=5*1000;
	
	final int Max_ackRetryCnt=3;
	boolean isDebug=true;
	boolean isFinished=false;
	byte curState=0;
	Timer sendTimer=null;
	Timer ackTimer=null;
	Timer queryTimer=null;
	final String TAG="AssistProtocol";
	AckTimerTask ackTimerTask=null;
	QueryTimerTask queryTimerTask=null;
	SendTimerTask sendTimerTask=null;
	boolean isConnect=false;
	
	
	
	
//	final byte Query_dirtyCup=0x11;
//	final byte Query_cupToken=0x22;	
//	final byte Query_hasCup=0x33;
	



//	byte query_what=0;

//	boolean inQueryState=false;  //是否处在查询状态，
//	boolean inAckState=false;  //是否处在应答状态，
	
	//int canNext=0;
	ArrayList<byte[]> sendList=new ArrayList<byte[]>();
	
	void getAck(){
		//Log.e(TAG,"getAck");
		hasAck=true;	
		cancelAckTimerTask();
		onGetAckCallBack();
	}
	public AssistProtocol(Context c){
		context=c;
		//computeCrcTable();
		initSerialPort();
		startSendTimer();
		startQueryTimer();
		//myTimerTask=new MyTimerTask();
	}
	
	private void initSerialPort() {
		try {
			String path =context.getString(R.string.IoUartName) ;	
			serialPortUtil = new SerialPortUtil();
			mSerialPort = serialPortUtil.getSerialPort(path,9600);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
			mReadThread = new ReadThread();
			mReadThread.start();
			
		} catch (Exception e) {
			//DisplayError(R.string.error_security);
		}
	}

	protected void onDataReceived(final byte[] buffer, final int size) {
		//Log.d(TAG,"onDataReceived!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		if(isDebug)
			showLog("Recivedata", buffer,size);
		//parseInput(buffer,size);
		if(size>=ReciveLength){
			getInput(buffer,ReciveLength);
		}
	}
	
	/*
	 * 
	 * 	
	byte inD0_bit0_2_cupState=0;
	byte inD0_bit3_4_flowState=0;
	byte inD0_bit5_firstHeatingState=0;
	byte inD0_bit6_secondHeatingState=0;
	byte inD0_bit7_waterState=0;
	byte inD1_bit0_key1=0;
	byte inD1_bit1_key2=0;
	byte inD1_bit2_key3=0;
	byte inD1_bit3_key4=0;
	byte inD2_H8bitInputState=0;
	byte inD3_L8bitInputState=0;
	byte inD2_bit0_doorState=0;
	byte inD3_bit1_noCup=0;
	byte inD3_bit3_dirtyCup=0;//
	 */
	private void getInput(byte[] data,int num){
		if(data[0]==(byte)0xaa&&data[1]==(byte)0x80){
			if(data[num-1]==getCheckSun(data,num)){
				getAck();
				inD0_bit0_2_cupState=(byte) (data[3]&(BIT0|BIT1|BIT2));
				inD0_bit3_4_flowState=(byte) ( (data[3]&(BIT3|BIT4))>>3);
				inD0_bit5_firstHeatingState= (data[3]&(BIT5))==0?false:true;
				inD0_bit6_secondHeatingState= (data[3]&(BIT6))==0?false:true;
				inD0_bit7_waterState= (data[3]&(BIT7))==0?false:true;
				inD1_bit0_key1= (data[4]&(BIT0))==0?true:false;
				inD1_bit1_key2= (data[4]&(BIT1))==0?true:false;
				inD1_bit2_key3= (data[4]&(BIT2))==0?true:false;
				inD1_bit3_key4= (data[4]&(BIT3))==0?true:false;
				inD2_H8bitInputState=(byte) (data[5]);
				inD3_L8bitInputState=(byte) (data[6]);
				inD2_bit0_doorState=(data[5]&(BIT0))==0?false:true;
				inD3_bit1_noCup= (data[6]&(BIT1))==0?false:true;
				inD3_bit3_noDirtyCup= (data[6]&(BIT3))==0?false:true;
				dealInput();
			}
		}
	}
	/*
	 * 
	 * 	//当前查询处于什么状态
	final byte QueryStep_handshake=BIT0; //闲时握手阶段
	final byte QueryStep_hasDirtyCup=BIT1; //是否有脏杯子--自动落杯模式
	final byte QueryStep_isCupReady=BIT2; //是否准备好杯子--手动放杯模式
	final byte QueryStep_dropCup=BIT3;		//落杯阶段
	final byte QueryStep_flowing=BIT4;		//出粉/水阶段
	final byte QueryStep_takingCup=BIT5;	//取杯阶段
	 */
	
	
	
	private void dealInput(){
		old_fault_state=fault_state;
		deal_comm();
		switch(query_step){
		case QueryStep_handshake: 
			deal_handShake();
			break;
		case QueryStep_takingDirtyCup:
			deal_isDirtyCupTaken();
			break;
		case QueryStep_isCupReady:
			deal_isCupReady();
			break;
		case QueryStep_dropCup:
			deal_dropCup();
			break;
		case QueryStep_flowing:
			deal_flowing();
			break;
		case QueryStep_takingCup:
			deal_takingCup();
			break;
		}
		
		if(fault_state!=old_fault_state){
			onFaultCallBack(fault_state);
		}
	}
	
	
	/*
	 * 
	 * 	
	byte inD0_bit0_2_cupState=0;
	byte inD0_bit3_4_flowState=0;
	byte inD0_bit5_firstHeatingState=0;
	byte inD0_bit6_secondHeatingState=0;
	byte inD0_bit7_waterState=0;
	byte inD1_bit0_key1=0;
	byte inD1_bit1_key2=0;
	byte inD1_bit2_key3=0;
	byte inD1_bit3_key4=0;
	byte inD2_H8bitInputState=0;
	byte inD3_L8bitInputState=0;
	byte inD2_bit0_doorState=0;
	byte inD3_bit1_noCup=0;
	byte inD3_bit3_dirtyCup=0;//
	 */
	
	/*
	 * 
	 * 在各阶段通用的数据处理放在此函数
	 * 应处理数据：
	 * 缺水状态：inD0_bit7_waterState
	 * 按钮3：inD1_bit2_key3
	 * 加热状态：inD0_bit5_firstHeatingState
	 * inD0_bit6_secondHeatingState
	 * 
	 */

	 void deal_comm(){
		 if(inD0_bit7_waterState){//缺水
			fault_state|=Fault_noWater;
		 }else{
			 fault_state&=~Fault_noWater; 
		 }
		 if(inD0_bit0_2_cupState==0x1){
			 fault_state|=Fault_noCup;
		 }else {
			 fault_state&=~Fault_noCup; 
		 }

		 if(inD0_bit5_firstHeatingState){//加热
			 fault_state|=Fault_1heating;
		 }else{
			 fault_state&=~Fault_1heating;
		 }
		 if(inD0_bit6_secondHeatingState){//加热
			 fault_state|=Fault_2heating;
		 }else{
			 fault_state&=~Fault_2heating;
		 }
		 if(inD1_bit2_key3){ //按键3
			 onKeyPressedCallBack(Key3);
		 }
		 
	 }

	 
	 /*
	  * 握手阶段需要处理的数据
	  * 	
	  * inD0_bit0_2_cupState
	  * inD0_bit7_waterState
	  * inD1_bit0_key1
	  * inD1_bit1_key2
	  * 
	  * inD1_bit3_key4
	  * inD2_bit0_doorState
	  * inD3_bit1_noCup
	  * 
	  */
	void deal_handShake(){
		 if(inD1_bit0_key1){
			 onKeyPressedCallBack(Key1);
		 }
		 if(inD1_bit1_key2){
			 onKeyPressedCallBack(Key2);
		 }
		 if(inD1_bit3_key4){
			 onKeyPressedCallBack(Key4);
		 }

		 
		 
	}
	void deal_isDirtyCupTaken(){

		if(inD3_bit3_noDirtyCup){
			cmd_dropCup();
		 }else {
			 
		 }
		 

	}
	void deal_isCupReady(){
		if(!inD3_bit3_noDirtyCup){
			cupReadyCallBack();
		}
	}

	void deal_dropCup(){
		switch(inD0_bit0_2_cupState){
		case 0:
			if(dropCupState_old==7){//只有从落杯忙到空闲的才认为落杯完成
				cupDropedCallBack();
			}
			break;
		case 1:
			fault_state|=Fault_noCup;
			break;
		case 2:
			dropCupTimeOutCallBack();
			break;
		case 3:
			cupStuckCallBack();
			break;
		case 4:
			hasDirtyCupCallBack();
			break;
			
		}
		dropCupState_old=inD0_bit0_2_cupState;
	}
	void deal_flowing(){
		if(inD0_bit3_4_flowState==0&&flowState_old==3){
			dropPowderCallBack();
		}
		flowState_old=inD0_bit3_4_flowState;
	}
	void deal_takingCup(){
		if(inD3_bit3_noDirtyCup){
		//if(inD0_bit0_2_cupState==0x04){
			tradeFinishCallBack();
		}
	}
	


	byte getCheckSun(byte[] data,int len){	
		int length=len-1;
		byte ck=0;
		for(int i=0;i<length;i++){
			ck=(byte) (ck+data[i]);
		}
		return ck;
	}

	byte getCheckSun(byte[] data){	
		int length=data.length-1;
		byte ck=0;
		for(int i=0;i<length;i++){
			ck=(byte) (ck+data[i]);
		}
		return ck;
	}
	
	

	
//	private void packCmd(byte cmd,byte arg){
//		byte[] data = new byte[] { 
//				(byte) 0xaa,
//				(byte) cmd,
//				(byte) arg,
//				(byte)0};
//			data[data.length-1]=getCheckSun(data);
//			writeToUartCached(data);
//	}
	private void packCmd_handshake(){
		byte[] data = new byte[] { 
				(byte) 0xaa,
				(byte) Cmd_handshake,
				(byte) 0,//数据长度
				(byte)0};
		data[data.length-1]=getCheckSun(data);
		writeToUartCached(data);
	}
	private void packCmd_dropCup(){
		byte[] data = new byte[] { 
				(byte) 0xaa,
				(byte) Cmd_dropCup,
				(byte) 0,//数据长度
				(byte)0};
		data[data.length-1]=getCheckSun(data);
		writeToUartCached(data);
	}
	private void packCmd_light_door(byte rFrq,byte gFrg,byte ctrl,byte reserve){
		byte[] data = new byte[] { 
				(byte) 0xaa,
				(byte) Cmd_Light,
				(byte) 4,//数据长度
				(byte)rFrq,
				(byte)gFrg,
				(byte)ctrl,
				(byte)reserve,
				(byte)0
				};
		data[data.length-1]=getCheckSun(data);
		writeToUartCached(data);
	}
	private void packCmd_setWater(byte ch1,byte ch2,byte ch3,byte ch4){
		byte[] data = new byte[] { 
				(byte) 0xaa,
				(byte) Cmd_setWater,
				(byte) 4,//数据长度
				(byte)ch1,
				(byte)ch2,
				(byte)ch3,
				(byte)ch4,
				(byte)0
		};
		data[data.length-1]=getCheckSun(data);
		writeToUartCached(data);
	}
	private void packCmd_setPowder(byte ch1l,byte ch1r,byte ch2l,byte ch2r,byte ch3l,byte ch3r,byte ch4l,byte ch4r){
		byte d0=(byte) ((ch1l&0xf)+((ch1r<<4)&0xf0));
		byte d1=(byte) ((ch2l&0xf)+((ch2r<<4)&0xf0));
		byte d2=(byte) ((ch3l&0xf)+((ch3r<<4)&0xf0));
		byte d3=(byte) ((ch4l&0xf)+((ch4r<<4)&0xf0));
		byte[] data = new byte[] { 
				(byte) 0xaa,
				(byte) Cmd_setPowderDegree,
				(byte) 4,//数据长度
				(byte)d0,
				(byte)d1,
				(byte)d2,
				(byte)d3,
				(byte)0
		};
		data[data.length-1]=getCheckSun(data);
		writeToUartCached(data);
	}
	
	private void packCmd_startDropPowder(boolean ch1l,boolean ch1r,boolean ch2l,boolean ch2r,boolean ch3l,boolean ch3r,boolean ch4l,boolean ch4r){
		byte ch=(byte) ((ch1l?BIT0:0)|(ch1r?BIT1:0)|(ch2l?BIT2:0)|(ch2r?BIT3:0)|(ch3l?BIT4:0)|(ch3r?BIT5:0)|(ch4l?BIT6:0)|(ch4r?BIT7:0));
		byte[] data = new byte[] { 
				(byte) 0xaa,
				(byte) Cmd_push,
				(byte) 4,//数据长度
				(byte)ch,
				(byte)0,
				(byte)0,
				(byte)0,
				(byte)0
		};
		data[data.length-1]=getCheckSun(data);
		writeToUartCached(data);
	}

	private void packCmd_setTemper(byte ch1temper,byte ch1backlash,byte ch2temper,byte ch2backlash){
		byte[] data = new byte[] { 
				(byte) 0xaa,
				(byte) Cmd_setWater,
				(byte) 4,//数据长度
				(byte)ch1temper,
				(byte)ch1backlash,
				(byte)ch2temper,
				(byte)ch2backlash,
				(byte)0
		};
		data[data.length-1]=getCheckSun(data);
		writeToUartCached(data);
	}

	public void  cmd_handshake(){
		packCmd_handshake();
	}
	public void  cmd_dropCup(){	
		startDropCupCallBack();
		packCmd_dropCup();
		setCurStep(QueryStep_dropCup);
	}
	public void  cmd_light_door(byte rFrq,byte gFrg,byte ctrl,byte reserve){
		//setCurStep(QueryStep_);
		packCmd_light_door( rFrq, gFrg,ctrl, reserve);
	}
	public void  cmd_setWater(int ch1,int ch2,int ch3,int ch4){
		
		packCmd_setWater( (byte)ch1,(byte) ch2, (byte)ch3, (byte)ch4);
		setCurStep(QueryStep_flowing);
	}
	public void  cmd_setPowder(int ch1l,int ch1r,int ch2l,int ch2r,int ch3l,int ch3r,int ch4l,int ch4r){
		packCmd_setPowder((byte)ch1l, (byte)ch1r, (byte)ch2l,(byte) ch2r, (byte)ch3l, (byte)ch3r, (byte)ch4l, (byte)ch4r);
		setCurStep(QueryStep_flowing);
	}
	public void  cmd_startDropPowder(boolean ch1l,boolean ch1r,boolean ch2l,boolean ch2r,boolean ch3l,boolean ch3r,boolean ch4l,boolean ch4r){		
		packCmd_startDropPowder( ch1l, ch1r, ch2l, ch2r, ch3l, ch3r, ch4l, ch4r);
		setCurStep(QueryStep_flowing);
	}

	public void  cmd_QueryCupToken(){
		setCurStep(QueryStep_takingCup);
	}
	
	/*
	 * 查询杯子是否放好
	 * 为不需要落杯的程序准备
	 */
	public void cmd_isCupReady(){
		setCurStep(QueryStep_isCupReady);
	}
	public void cmd_handShake(){
		setCurStep(QueryStep_handshake);
	}
	
	private void setCurStep(byte step){
		query_step=step;
	}
	private void showLog(String tag, byte[] showArr,int num) {
		int i;
	final StringBuilder stringBuilder = new StringBuilder();
	for (i=0;i<num;i++){
		byte byteChar=  showArr[i];
		stringBuilder.append(String.format("%02X ", byteChar));
		
	}
	Log.i(tag, "##" + stringBuilder.toString() + "\n");

 }
//	private final ReentrantLock lock = new ReentrantLock();	
	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				int size;
				try {
				//	lock.lock();
					byte[] buffer = new byte[256];
					if (mInputStream == null)
						return;
					size = mInputStream.read(buffer);
					if (size > 0) {										
						if(size>buffer.length)
							size=buffer.length;
						onDataReceived(buffer, size);
					}
				} catch (IOException e) {
		
					e.printStackTrace();
					return;
				}
				finally{
					//lock.unlock();
				}
			}
		}
	}
	
	
	
	
	private void sendCmd(byte[] send){
		sendData=send;
		if (mOutputStream != null) {
			try {
				if(sendData!=null){
					if(isDebug)
						showLog(TAG+"send",sendData,sendData.length);	
					mOutputStream.write(sendData);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void reSendData(){
		
		if (mOutputStream != null) {
			try {
				if(sendData!=null){
					if(isDebug)
						showLog(TAG+"reSendData",sendData,sendData.length);	
					mOutputStream.write(sendData);
					hasAck=false;
				//	Log.d("ioctrl","startAckTimer############");
					if(ackTimer==null){
						ackTimer=new Timer();
					}
					cancelAckTimerTask();
					ackTimerTask=new AckTimerTask();
					ackTimerTask.inAckState=true;
					ackTimer.schedule(ackTimerTask, AckTimerDuaration);
					//startAckTimer();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	

	

	private void startAckTimer(){
		hasAck=false;
		ackCnt=0;
	//	Log.d("ioctrl","startAckTimer############");
		if(ackTimer==null){
			ackTimer=new Timer();
		}
		cancelAckTimerTask();
		ackTimerTask=new AckTimerTask();
		ackTimerTask.inAckState=true;
		ackTimer.schedule(ackTimerTask, AckTimerDuaration);
	}



	private void startSendTimer(){

		if(sendTimer==null){
			sendTimer=new Timer();
		}
		sendTimerTask=new SendTimerTask();
		sendTimerTask.inSendState=true;
		sendTimer.schedule(sendTimerTask, SendTimerDuaration,SendTimerDuaration);
	}	
	
	private void startQueryTimer(){
		//Log.d(TAG, "startQueryTimer query_what="+step);
		//query_step=step;
		if(queryTimer==null){
			queryTimer=new Timer();
		}
		cancelQueryTimerTask();
		queryTimerTask=new QueryTimerTask();
		queryTimerTask.inQueryState=true;
		queryTimer.schedule(queryTimerTask, QueryTimerDuaration,QueryTimerDuaration);	
	}	

	
	
	public void cancelQueryTimerTask(){
		Log.d(TAG, "cancelQueryTimerTask!! ");
		if(queryTimerTask!=null){
			queryTimerTask.inQueryState=false;
			if(queryTimerTask.cancel()){//只有任务确实被取消了，才能让任务为null
				queryTimerTask=null;
			}
		}
	}
	
	
	
	
	private void cancelAckTimerTask(){
		
		if(ackTimerTask!=null){
			ackTimerTask.inAckState=false;
			if(ackTimerTask.cancel()){//只有任务确实被取消了，才能让任务为null
				ackTimerTask=null;
			}
		}
	}
	private void cancelSendTimerTask(){
		
		if(sendTimerTask!=null){
			sendTimerTask.inSendState=false;
			if(sendTimerTask.cancel()){//只有任务确实被取消了，才能让任务为null
				sendTimerTask=null;
			}
		}
	}
	
	
	class AckTimerTask  extends TimerTask{
		boolean inAckState=false;
		@Override
		public void run() {
			if(!hasAck&&inAckState){
			//	Log.d("io","AckTimerTask !hasAck");
				ackCnt++;				
				if(ackCnt>Max_ackRetryCnt){
					cancelAckTimerTask();
					sendTimeOutCallBack(); //发送超时
				}else{
					reSendData();
				}
			}else{
				//ackTimer.cancel();
			}
		}	
	}

	
	class QueryTimerTask  extends TimerTask{
		
		boolean inQueryState=false;
		@Override
		public void run() {
			Log.d(TAG,"onQueryTime inQueryState="+inQueryState+"query_what="+query_step);
			if(inQueryState){
				onQueryTime();
			}
		}	
	}
	void writeToUartCached(byte[] data){
			
		    sendList.add(data);

		}
	
	
	
	class SendTimerTask  extends TimerTask{
		boolean inSendState=false;
		@Override
		public void run() {
			if(inSendState){
				onSendTime();
			}
		}	
	}
	
	void onSendTime(){
		//Log.e("io", "onSendTime ");
		
	    if(!sendList.isEmpty()){

	    	
	        if(hasAck||(!isConnect)){
//	        	if(canNext!=0){
//	        		canNext--;
	        	for(int i = 0;i<sendList.size();i++ ){
	        		sendData = (byte[])sendList.get(i);
	        		if(sendData!=null){
	        			sendList.remove(i);
	        	   // 	Log.e(TAG, "onSendTime !sendList.isEmpty() &&sendData!=null");
	        			sendCmd(sendData);
	        			startAckTimer();
	        			break;
	        			}
	        		}
      
	        }
	    }
	}

	
	void onQueryTime(){
		cmd_handshake();
	}

	///////////////////////回调接口////////////////////////////////

	CallBack callBack=null;
	public  void setCallBack(CallBack call) {
		// TODO Auto-generated method stub
		callBack = call;
	}

	public interface CallBack {
		void onFault(byte fault);
		void onKeyPressed(byte key);
		void cupDroped();
		void cupStuck();
		void dropCupTimeOut();
		void hasDirtyCup();
		void powderDroped();
		void sendTimeOut();
		void tradeFinish();
		void startDropCup();
		void onGetConnect();
		void cupReady();//为了不落杯的程序准备
	}
	
	private void tradeFinishCallBack(){
	//	cmd_cancelLight(); //交易结束后关灯
		Log.d(TAG,"!!!!tradeFinishCallBack");
		if(callBack!=null)
			callBack.tradeFinish();
		
	}
	
	private void onFaultCallBack(byte fault){
			if(callBack!=null){
			callBack.onFault(fault);
		
		}
	}
	private void onKeyPressedCallBack(byte key){
		if(callBack!=null){
			callBack.onKeyPressed(key);
			
		}
	}


	private void onGetAckCallBack(){
	//	Log.d(TAG,"!!!!onEnableCallBack");
		if(!isConnect){
			isConnect=true;
			if(callBack!=null){
				callBack.onGetConnect();
			}
		}
	}
	private void startDropCupCallBack(){
		Log.d(TAG,"!!!!startDropCupCallBack");
		if(callBack!=null)
			callBack.startDropCup();
	}
	private void cupDropedCallBack(){
		Log.d(TAG,"!!!!cupDropedCallBack");
		//cancelQueryTimerTask();
		if(callBack!=null)
			callBack.cupDroped();
		
	}
	private void cupReadyCallBack(){
		
		Log.e(TAG,"!!!!cupReadyCallBack");
		if(callBack!=null)
			callBack.cupReady();
		
	}
//	private void cupNotReadyCallBack(){
//		//cancelQueryTimerTask();
//		if(callBack!=null)
//			callBack.cupNotReady();
//		
//	}
	

	private void dropCupTimeOutCallBack(){
		Log.d(TAG,"!!!!dropCupTimeOutCallBack");
		//cancelQueryTimerTask();
		if(callBack!=null)
			callBack.dropCupTimeOut();
		
	}
	private void cupStuckCallBack(){
		Log.d(TAG,"!!!!cupStuckCallBack");
		//cancelQueryTimerTask();
		if(callBack!=null)
			callBack.cupStuck();
		
	}
	private void hasDirtyCupCallBack(){
		Log.d(TAG,"!!!!hasDirtyCupCallBack");
		//cancelQueryTimerTask();
		
		if(callBack!=null)
			callBack.hasDirtyCup();
		//cmd_ReadCupIsToke();
		//startQueryTimer(Query_dirtyCup);
		setCurStep(QueryStep_takingDirtyCup);
	}
	/*
	 * 
	 * 落粉完成 ，此时应该开始查询杯子是否被取走
	 */
	private void dropPowderCallBack(){
		Log.d(TAG,"!!!!dropPowderCallBack");
		//cancelQueryTimerTask();
		if(callBack!=null){
			callBack.powderDroped();
		}
	//	startQueryTimer(Query_cupToken);
		
	}
	

	
	
	private void sendTimeOutCallBack(){
		Log.d(TAG,"!!!!sendTimeOutCallBack");
		isConnect=false;
		if(callBack!=null)
			callBack.sendTimeOut();
		
	}




	
	
	//程序结束时调用
	public void cleanTimer(){
		Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!cleanTimer");
		cancelQueryTimerTask();
		cancelAckTimerTask();
		cancelSendTimerTask();
		if(sendTimer!=null){
			sendTimer.cancel();
			sendTimer=null;
		}if(ackTimer!=null){
			ackTimer.cancel();
			ackTimer=null;
		}
		 if(queryTimer!=null){
			 queryTimer.cancel();
			 queryTimer=null;
		 }	 
		
	}
	public void closeSerialPort() {
	if (mSerialPort != null) {
		mSerialPort.close();
		mSerialPort = null;
	}
}
	
	
	
	@Override
	public void finalize() throws Throwable {
		cleanTimer();
		mReadThread.interrupt();
		closeSerialPort();
		super.finalize();
	}

}
