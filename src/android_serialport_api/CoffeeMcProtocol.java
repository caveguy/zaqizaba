package android_serialport_api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.coffemachinev3.R;
import com.tt.util.Errors;

public class CoffeeMcProtocol {
	Context context;
	byte[] sendData=null;
	boolean hasAck=true;  //默认有回复，为了让第一条信息发送出去
	int ackCnt=0;
	SerialPortUtil serialPortUtil=null;
	private SerialPort mSerialPort;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread; 
	
	final static byte BIT0=(byte) 0x01;
	final static byte BIT1=(byte) 0x02;
	final static byte BIT2=(byte) 0x04;
	final static byte BIT3=(byte) 0x08;
	final static byte BIT4=(byte) 0x10;
	final static byte BIT5=(byte) 0x20;
	final static byte BIT6=(byte) 0x40;
	final static byte BIT7=(byte) 0x80;	
	
	final byte Cmd_handshake=0x01;
	final byte Cmd_making=0x02;
	final byte Cmd_clean=0x03;
	final byte Cmd_setTemper=0x04;
	final byte Cmd_setCoffee=0x05;
	final byte Cmd_setInfiltrateWater=0x06;
	final byte Cmd_openBoiler=0x07;
	final byte Cmd_test=0x30;
	//final byte Cmd_in_normal=0x21;
	//final byte Cmd_in_test=0x31;
    final byte Start_byte=(byte) 0xaa;
    final byte End_byte=0x55;
    final byte Data_start=4;
    final byte Min_length=5;
	final byte Normal_size=Min_length+16;
	final byte Test_size=Min_length+0x04;
	
    
    final byte State_Ready=0x01;
    final byte State_making=0x05;
    
    final byte State_heating=0x02;
    final byte State_rinsing=0x03;
    final byte State_tooHot=0x04;
    final byte State_cleaning=0x06;
    final byte State_emptying=0x07;
    final byte State_fillingSystem=0x35;
    final byte State_waiting=0x08;
    
    final byte State_fault1=BIT0;
    final byte State_fault2=BIT1;
    final byte State_fault3=BIT2;
    final byte State_fault4=BIT3;
    final byte State_fault5=BIT4;
    final byte State_fault6=BIT5;
    final byte State_fault7=BIT6;
	boolean hasFault=false;

	
	byte fault_state=0; //0代表没有错误
	byte old_fault_state=0; //0代表没有错误
	//发送数据
	byte cmd0x4_d0_goalTemper=0;
	byte cmd0x4_d1_backlash=0;
	byte cmd0x4_d2_minTemper=0;
	
	byte cmd0x5_d0_powder=0;
	byte cmd0x5_d1_water=0;
	byte cmd0x5_d2_reserve=0;
	byte cmd0x5_d3_reserve=0;
	
	byte cmd0x6_d0_infiltrateWater=0;
	byte cmd0x7_d0_openBoiler=0;

	byte cmd0x30_d0_testType=0;

	
//****************接收解析*******************************/
	byte in_cmd0x21D0_runningState=0;
	byte in_runningState_old=0;
	byte in_cmd0x21D1_powder=0;
	byte in_cmd0x21D2_temper=0;
	byte in_cmd0x21D3_infiltrateWater=0;
	byte in_cmd0x21D4_water=0;
	byte in_cmd0x21D5_curTemper=0;
	byte in_cmd0x21D5_old_curTemper=0;
	byte in_cmd0x21D6_fault=0;
	
	byte in_cmd0x31_d0_testType=0;
	byte in_cmd0x31_d1_testState=0;
	int in_cmd0x31_d2_d3_value=0;
	
	


	
		
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
	final String TAG="CoffeeMcProtocol";
	AckTimerTask ackTimerTask=null;
	QueryTimerTask queryTimerTask=null;
	SendTimerTask sendTimerTask=null;
	boolean isConnect=false;
	

	ArrayList<byte[]> sendList=new ArrayList<byte[]>();
	
	void getAck(){
		//Log.e(TAG,"getAck");
		hasAck=true;	
		cancelAckTimerTask();
		onGetAckCallBack();
	}
	public CoffeeMcProtocol(Context c){
		context=c;
		//computeCrcTable();
		initSerialPort();
		startSendTimer();
		startQueryTimer();
		//myTimerTask=new MyTimerTask();
	}
	
	private void initSerialPort() {
		try {
			String path =context.getString(R.string.McUartName) ;	
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
		if(size>=Min_length){
			splite( buffer, size);
//			if(!getInput(buffer,size)){
//				Log.d(TAG,"getInput  failed!!!");
		}
	}
	
	void splite(final byte[] buffer, final int size){
		int i=0;
		boolean find=false;
		int cmd_lenth=1;
	//	Log.e(TAG, "splite!!!!!!!!!!!!");
		while(i<=size-Min_length){
			find=false;
			if(buffer[i]==Start_byte){//一条指令开始

				int data_len=(int)buffer[3+i];
				//int check_order=i+4+data_len;
				int end_order=i+4+data_len;
				 cmd_lenth=Min_length+data_len;
				if(end_order<=size-1){
					if(buffer[end_order]==End_byte){
						find=true;
						byte[] oneCmd=new byte[cmd_lenth];
						for(int j=0;j<cmd_lenth;j++){
							oneCmd[j]=buffer[j+i];
						}
						getInput(oneCmd,cmd_lenth);
					
					}
				}
			}
			if(find){
				i+=cmd_lenth;
			}else{
				i+=1;
			}
		}
		
	}
	

	private boolean getInput(byte[] data,int num){
		int data_len=data[3];
		int check_order=1;
		int end_order=4+data_len;
		int all_lenth=Min_length+data_len;
		if(all_lenth>num){
			return false;
		}
		if(data[0]==(byte)0xaa&&data[end_order]==0x55&&(data[check_order]==getCheckSun(data))){
			getAck();
//			if(data[2]>=Cmd_handshake&&data[2]<=Cmd_openBoiler){
//
//			}
			if(data[2]==(byte)Cmd_test){
				parseTest(data);
				return true;
			}else {
				parseNormal(data);
				return true;	
			}
			
		}
		return false;
	}
	
	/*
	 * 
	 * 	
	byte in_cmd0x21D0_runningState=0;
	byte in_cmd0x21D1_powder=0;
	byte in_cmd0x21D2_temper=0;
	byte in_cmd0x21D3_infiltrateWater=0;
	byte in_cmd0x21D4_water=0;
	 */
	void parseNormal(byte[] data){
		in_cmd0x21D0_runningState=data[Data_start];
		in_cmd0x21D1_powder=data[Data_start+1];
		in_cmd0x21D2_temper=data[Data_start+2];
		in_cmd0x21D3_infiltrateWater=data[Data_start+3];
		in_cmd0x21D4_water=data[Data_start+4];
		in_cmd0x21D5_curTemper=data[Data_start+5];
		in_cmd0x21D6_fault=data[Data_start+6];
		dealNormalInput();
	}
	/*
	 * 	byte in_cmd0x31_d0_testType=0;
	byte in_cmd0x31_d1_testState=0;
	int in_cmd0x31_d2_d3_value=0;
	 */
	void parseTest(byte[] data){
		in_cmd0x31_d0_testType=data[Data_start];
		in_cmd0x31_d1_testState=data[Data_start+1];
		in_cmd0x31_d2_d3_value=data[Data_start+2]+(int)data[Data_start+3]<<8;
	}
	/*
	 *     final byte State_Ready=0x01;
    final byte State_heating=0x02;
    final byte State_rinsing=0x03;
    final byte State_tooHot=0x04;
    final byte State_making=0x05;
    final byte State_cleaning=0x06;
    final byte State_emptying=0x07;
    final byte State_waiting=0x08;
    final byte State_fault1=0x09;
    final byte State_fault2=0x0a;
    final byte State_fault3=0x0b;
    final byte State_fault4=0x0c;
    final byte State_fault5=0x0d;
    final byte State_fault6=0x0e;
    final byte State_fault7=0x0f;
    final byte State_fillingSystem=0x35;
	 */
	void dealNormalInput(){
		if(in_runningState_old!=in_cmd0x21D0_runningState){
			
			if(in_runningState_old==State_making){
				finishCallBack();
			}
			switch(in_cmd0x21D0_runningState){
			case State_Ready:	
				readyCallBack();	
				break;
			case State_making:
				onMakingCallBack();
				break;
				
			case State_heating:
			case State_rinsing:
			case State_tooHot:
			case State_cleaning:
			case State_emptying:
			case State_waiting:
			case State_fillingSystem:
				onWaitingCallBack(in_cmd0x21D0_runningState);
				
				break;
			default:
				Log.w(TAG, " unknown state: in_cmd0x21D0_runningState="+in_cmd0x21D0_runningState);
			}

			
			
		  onFaultCallBack(in_cmd0x21D6_fault);
		}
		if(in_cmd0x21D5_old_curTemper!=in_cmd0x21D5_curTemper){
			temperCallBack(in_cmd0x21D5_curTemper);
		}
		in_cmd0x21D5_old_curTemper=in_cmd0x21D5_curTemper;
		in_runningState_old=in_cmd0x21D0_runningState;
	}



//	byte getCheckSun(byte[] data,int len){	
//		int length=data[3];
//		byte ck=0;
//		for(int i=Data_start;i<Data_start+length;i++){
//			ck=(byte) (ck+data[i]);
//		}
//		return ck;
//	}

	byte getCheckSun(byte[] data){	
		int length=data[3];
		byte ck=0;
		int start=2;
		for(int i=start;i<start+length+2;i++){
			ck=(byte) (ck+data[i]);
		}
		return ck;
	}
	
	

	private void packCmd_handshake(){
		byte[] data = new byte[] { 
				(byte) Start_byte,
				(byte) 0,//校验
				(byte) Cmd_handshake,
				(byte) 0,//数据长度
				
				(byte)End_byte};
		data[1]=getCheckSun(data);
		writeToUartCached(data);
	}
	private void packCmd_making(){
		byte[] data = new byte[] { 
				(byte) Start_byte,
				(byte) 0,//校验
				(byte) Cmd_making,
				(byte) 0,//数据长度
				(byte)End_byte};
		data[1]=getCheckSun(data);
		writeToUartCached(data);
	}
	private void packCmd_cleaning(){
		byte[] data = new byte[] { 
				(byte) Start_byte,
				(byte) 0,//校验
				(byte) Cmd_clean,
				(byte) 0,//数据长度	
				(byte)End_byte};
		data[1]=getCheckSun(data);
		writeToUartCached(data);
	}
	private void packCmd_setTemper(byte temper,byte backlash,byte min){
		cmd0x4_d0_goalTemper=temper;
		cmd0x4_d1_backlash=backlash;
		cmd0x4_d2_minTemper=min;
		byte[] data = new byte[] { 
				(byte) Start_byte,
				(byte) 0,//校验
				(byte) Cmd_setTemper,
				(byte) 3,//数据长度
				(byte) cmd0x4_d0_goalTemper,//d0
				(byte) cmd0x4_d1_backlash,//d1
				(byte) cmd0x4_d2_minTemper,//d2
				(byte)End_byte};
		data[1]=getCheckSun(data);
		writeToUartCached(data);
	}
	private void packCmd_setCoffee(byte powder,byte water,byte reserve1,byte reserve2){
			cmd0x5_d0_powder=powder;
			cmd0x5_d1_water=water;
			cmd0x5_d2_reserve=reserve1;
			cmd0x5_d3_reserve=reserve2;
		byte[] data = new byte[] { 
				(byte) Start_byte,
				(byte) 0,//校验
				(byte) Cmd_setCoffee,
				(byte) 4,//数据长度
				(byte) cmd0x5_d0_powder,//d0
				(byte) cmd0x5_d1_water,//d1
				(byte) cmd0x5_d2_reserve,//d2
				(byte) cmd0x5_d3_reserve,//d2
				(byte)End_byte};
		data[1]=getCheckSun(data);
		writeToUartCached(data);
	}
	private void packCmd_setInfiltrateWater(byte water){
		cmd0x6_d0_infiltrateWater=water;

		byte[] data = new byte[] { 
				(byte) Start_byte,
				(byte) 0,//校验
				(byte) Cmd_setInfiltrateWater,
				(byte) 1,//数据长度
				(byte) cmd0x6_d0_infiltrateWater,//d0
				(byte)End_byte};
		data[1]=getCheckSun(data);
		writeToUartCached(data);
	}
	private void packCmd_openBoiler(boolean  open){
		if(open){
			cmd0x7_d0_openBoiler=(byte) 0xa0;
		}else{
			cmd0x7_d0_openBoiler=(byte) 0x50;
		}
		byte[] data = new byte[] { 
				(byte) Start_byte,
				(byte) 0,//校验
				(byte) Cmd_openBoiler,
				(byte) 1,//数据长度
				(byte) cmd0x7_d0_openBoiler,//d0
				(byte)End_byte};
		data[1]=getCheckSun(data);
		writeToUartCached(data);
	}


	public void  cmd_handshake(){
		packCmd_handshake();
	}

	public void cmd_making(){
		packCmd_making();
	}
	public void cmd_cleaning(){
		packCmd_cleaning();
	}

	public void cmd_setTemper(int temper,int backlash,int minTemper){
		Log.e(TAG, "temper="+temper+" backlash="+backlash+" minTemper="+minTemper);
		packCmd_setTemper((byte) temper,(byte) backlash,(byte) minTemper);
	}

	public void cmd_setCoffee(int powder, int water ){
		packCmd_setCoffee((byte)powder,(byte)water,(byte)0,(byte)0);
	}
	public void cmd_setInfiltrateWater(int water){
		packCmd_setInfiltrateWater((byte)water);
	}
	
	public void cmd_openBoiler(boolean  open){
		packCmd_openBoiler(open);
	}
	
	private void showLog(String tag, byte[] showArr,int num) {
		int i;
	final StringBuilder stringBuilder = new StringBuilder();
	for (i=0;i<num;i++){
		byte byteChar=  showArr[i];
		stringBuilder.append(String.format("%02X ", byteChar));
		
	}
	Log.d(tag, "##" + stringBuilder.toString() + "\n");

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
			//Log.d(TAG,"onQueryTime inQueryState="+inQueryState+"query_what="+query_step);
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
		void onWaiting(String fault);
		void onFault(List<Errors.McError> errors);
		void onReady();
		void onMaking();
		void onFinish();
		void onGetConnect();
		void sendTimeOut();
		void onTemperChanged(byte temper);
	}

	void onMakingCallBack(){
		if(callBack!=null)
			callBack.onMaking();
	}
	
	private void finishCallBack(){
	//	cmd_cancelLight(); //交易结束后关灯
		Log.d(TAG,"!!!!finishCallBack");
		if(callBack!=null)
			callBack.onFinish();
		
	}
	private void readyCallBack(){
		Log.i(TAG,"!!!!onReady");
		if(callBack!=null)
			callBack.onReady();
		
	}
	private void temperCallBack(byte temper){
		Log.i(TAG,"!!!!temperCallBack");
		if(callBack!=null)
			callBack.onTemperChanged(temper);
		
	}
	/*
	 *     final byte State_heating=0x02;
    final byte State_rinsing=0x03;
    final byte State_tooHot=0x04;
    final byte State_cleaning=0x06;
    final byte State_emptying=0x07;
    final byte State_waiting=0x08;
    final byte State_fault1=0x09;
    final byte State_fault2=0x0a;
    final byte State_fault3=0x0b;
    final byte State_fault4=0x0c;
    final byte State_fault5=0x0d;
    final byte State_fault6=0x0e;
    final byte State_fault7=0x0f;
    final byte State_fillingSystem=0x35;
	 */
	private void onWaitingCallBack(byte fault){
		String msg=null;
		switch(fault){
		case State_heating:
			msg=context.getString(R.string.cmd1_heating);
			break;
		case State_rinsing:
			msg=context.getString(R.string.cmd1_rinsing);
			break;
		case State_tooHot:
			msg=context.getString(R.string.cmd1_too_hot);
			break;
		case State_cleaning:
			msg=context.getString(R.string.cmd1_cleaning);
			break;
		case State_emptying:
			msg=context.getString(R.string.cmd1_emptying);
			break;
		case State_waiting:
			msg=context.getString(R.string.cmd1_waiting);
			break;
		case State_fillingSystem:
			msg=context.getString(R.string.cmd1_fillingSystem);
			break;
			default:
				msg=context.getString(R.string.cmd1_unknow);	
			
		}
			if(callBack!=null){
			callBack.onWaiting(msg);
		
		}
	}

	private void onFaultCallBack(byte fault){
		if(fault!=0){
			hasFault=true;
		}
		List<Errors.McError> errors=new ArrayList();
		if((fault&State_fault1)!=0){
			errors.add(Errors.McError.Mc_error4);
		}
		if((fault&State_fault2)!=0){
			errors.add(Errors.McError.Mc_error5);
		}
		if((fault&State_fault3)!=0){
			errors.add(Errors.McError.Mc_error6);
		}
		if((fault&State_fault4)!=0){
			errors.add(Errors.McError.Mc_error7);
		}
		if((fault&State_fault5)!=0){
			errors.add(Errors.McError.Mc_error8);
		}
		if((fault&State_fault6)!=0){
			errors.add(Errors.McError.Mc_error9);
		}
		
		
		if(hasFault&&callBack!=null){
			callBack.onFault( errors);
		}
		
		if(fault==0){//为了清错误
			hasFault=false;
		}
	}

//    onCleanFault(){
//    	
//    }
	
	private void onGetAckCallBack(){
	
		if(!isConnect){
			isConnect=true;
			if(callBack!=null){
				Log.i(TAG,"!!!!onGetConnect");
				callBack.onGetConnect();
			}
		}
	}



	

	
	
	private void sendTimeOutCallBack(){
		Log.i(TAG,"!!!!sendTimeOutCallBack");
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
