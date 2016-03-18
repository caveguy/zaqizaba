package android_serialport_api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.DropBoxManager;
import android.os.Handler;
import android.util.Log;

import com.example.coffemachinev2.R;

public class DeliveryProtocol {
	Context context;
	byte[] sendData=null;
	boolean hasAck=true;  //默认有回复，为了让第一条信息发送出去
	int ackCnt=0;
	private Handler myHandler=new Handler();
	SerialPortUtil serialPortUtil=null;
	private SerialPort mSerialPort;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread; 
	
	final byte Cmd_handshake=0x11;
	final byte Cmd_dropCup=0x21;
	final byte Cmd_redLight=0x22;
	final byte Cmd_greedLight=0x23;
	final byte Cmd_setLeftPowder=0x2a;
	final byte Cmd_setLeftWater=0x2b;
	final byte Cmd_setCenterPowder=0x2c;
	final byte Cmd_setCenterWater=0x2d;
	final byte Cmd_setRightPowder=0x2e;
	final byte Cmd_setRightWater=0x2f;
	final byte Cmd_pushPowder=0x30;
	final byte Cmd_readState=0x50;
	final byte Cmd_readLower8bits=(byte) 0xb9;
	final byte Cmd_readHIght8bits=(byte) 0xba;//?
	final byte Cmd_setOutputLower8Bits=(byte) 0xc0;//
	final byte Cmd_setOutputHight8Bits=(byte) 0xc1;//
//	final byte Cmd_readBusy=(byte) 0x50;//
	final byte Cmd_readErros=(byte) 0x51;//
	final byte BIT0=(byte) 0x01;
	final byte BIT1=(byte) 0x02;
	final byte BIT2=(byte) 0x04;
	final byte BIT3=(byte) 0x08;
	final byte BIT4=(byte) 0x10;
	final byte BIT5=(byte) 0x20;
	final byte BIT6=(byte) 0x40;
	final byte BIT7=(byte) 0x80;
	
	final byte DropCup_finish =(byte) 0x0;
	final byte DropCup_nocup =(byte) 0x01;
	final byte DropCup_timeOut =(byte) 0x02;
	final byte DropCup_stuck =(byte) 0x03;
	final byte DropCup_dirtyCup =(byte) 0x04;
	final byte DropCup_busy =(byte) 0x07;
//	final byte DropPowder_finish =(byte) 0x07;
//	final byte DropCup_nocup =(byte) 0x01;
	final int SendTimerDuaration=300;
	final int AckTimerDuaration=200;
	final int QueryTimerDuaration=1000;
		
	
	boolean isDebug=true;
	boolean isFinished=false;
	boolean hasResult=false;
	
	Timer sendTimer=null;
	Timer ackTimer=null;
	Timer queryTimer=null;
	AckTimerTask ackTimerTask=null;
	QueryTimerTask queryTimerTask=null;
	//int canNext=0;
	ArrayList<byte[]> sendList=new ArrayList<byte[]>();
	
	void getAck(){
		hasAck=true;

	//	canNext++;
		if(ackTimerTask!=null){
			ackTimerTask.cancel();
		}
	}
	public DeliveryProtocol(Context c){
		context=c;
		//computeCrcTable();
		initSerialPort();
		//myTimerTask=new MyTimerTask();
	}
	
	private void initSerialPort() {
		try {
			String path =context.getString(R.string.IoUartName) ;	
			serialPortUtil = new SerialPortUtil();
			mSerialPort = serialPortUtil.getSerialPort("/dev/ttymxc2",9600);
//			mSerialPort = serialPortUtil.getSerialPort(path,9600);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();

			/* Create a receiving thread */
			mReadThread = new ReadThread();
			mReadThread.start();
			startSendTimer();
		} catch (Exception e) {
			//DisplayError(R.string.error_security);
		}
	}

	protected void onDataReceived(final byte[] buffer, final int size) {
		//Log.d(TAG,"onDataReceived!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		if(isDebug)
			showLog("Recivedata", buffer,size);
		parseInput(buffer,size);
	}
	
	private void parseInput(byte[] data,int num){
		if(num<4)
			return ;
		//Log.e("protocol","parseInput!!!num="+num);
		//Log.e("rec","data[3]="+(int)data[3]);
	//	Log.e("rec","getCheckSun(data,4)="+getCheckSun(data,4));
		//if(data[0]==(byte)0xaa){
		if(data[0]==(byte)0xaa&&data[3]==getCheckSun(data,4)){
			Log.e("rec","rec right!!");
				getAck();//收到回复
				byte reply=data[2];	
				switch(data[1]){
					case Cmd_handshake:
						dealReply_handShake(reply);
						break;
	
					case Cmd_setLeftPowder:
					case Cmd_setLeftWater:
					case Cmd_setCenterPowder:
					case Cmd_setCenterWater:
					case Cmd_setRightPowder:
					case Cmd_setRightWater:
						dealReply_set(reply);
						break;
					case Cmd_dropCup:
					case Cmd_pushPowder:
						dealReply_actions(reply);
						break;
					case Cmd_readState:
						dealReply_status(reply);
						
						
						
				}
		}
	}
	

	
	void finished(){
		isFinished=true;
		cupDropedCallBack();
	}

	byte getCheckSun(byte[] data,int len){	
		int length=len-1;
		byte ck=0;
		for(int i=0;i<length;i++){
			ck=(byte) (ck^data[i]);
		}
		return ck;
	}

	byte getCheckSun(byte[] data){	
		int length=data.length-1;
		byte ck=0;
		for(int i=0;i<length;i++){
			ck=(byte) (ck^data[i]);
		}
		return ck;
	}
	
	

	
	private void packCmd(byte cmd,byte arg){
		byte[] data = new byte[] { 
				(byte) 0xaa,
				(byte) cmd,
				(byte) arg,
				(byte)0};
			data[3]=getCheckSun(data);
			writeToUartCached(data);
//			canNext=1;
			//sendCmd(data);
	}
	
	void dealReply_handShake(byte data){
		
	}
	void dealReply_actions(byte data){
		if(data==0){
			startQueryTimer();
		}
//		else{
//			reSendData();
//		}
	}
	void dealReply_set(byte data){
//		if(data!=0){
//			reSendData();
//		}
	}

	void dealReply_status(byte data){
		//readStatusCallBack(data);
		byte cupState=(byte) (data&0x07);
		switch(cupState){
		case DropCup_finish: //落杯完成
			cupDropedCallBack();		
			break;	
		case DropCup_nocup:
			noCupCallBack();
			break;
		case DropCup_timeOut:
			dropCupTimeOutCallBack();
			break;
		case DropCup_stuck:
			cupStuckCallBack();
			break;			
		case DropCup_dirtyCup:
			hasDirtyCupCallBack();
			
		case DropCup_busy:
			break;
			
		}
		byte powderState=(byte) (data&0x18);
		if(powderState==0){
			dropPowderCallBack();
		}	
		
	}
	

	private void showLog(String tag, byte[] showArr,int num) {
		int i;
	final StringBuilder stringBuilder = new StringBuilder();
	for (i=0;i<num;i++){
		byte byteChar=  showArr[i];
		stringBuilder.append(String.format("%02X ", byteChar));
		
	}
	Log.e(tag, "##" + stringBuilder.toString() + "\n");

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
					showLog("send",sendData,sendData.length);	
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
					showLog("send",sendData,sendData.length);	
					mOutputStream.write(sendData);
					startAckTimer();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	

	

	private void startAckTimer(){
		if(ackTimerTask!=null){
			ackTimerTask.cancel();
			ackTimerTask=null;
		}
		hasAck=false;
		ackCnt=0;
		Log.e("ioctrl","startAckTimer############");
		if(ackTimer==null){
			ackTimer=new Timer();
		}

		ackTimerTask=new AckTimerTask();
		ackTimer.schedule(ackTimerTask, AckTimerDuaration);
	}


//	private void startAckTimer(){
//		hasAck=false;
//		ackCnt=0;
//		Log.e("ioctrl","startAckTimer############");
//		if(ackTimer==null){
//			ackTimer=new Timer();
//		}
//		
//		ackTimer.schedule(new AckTimerTask(), ackTime);
//	}	

	private void startSendTimer(){

		if(sendTimer==null){
			sendTimer=new Timer();
		}
		
		sendTimer.schedule(new sendTimerTask(), SendTimerDuaration,SendTimerDuaration);
	}	
	
	private void startQueryTimer(){
		
		if(queryTimer==null){
			queryTimer=new Timer();
		}
		if(queryTimerTask==null){
			queryTimerTask=new QueryTimerTask();
		}
		queryTimer.schedule(queryTimerTask, QueryTimerDuaration,QueryTimerDuaration);
	}	
	
	private void cancelQueryTimer(){
		if(queryTimerTask!=null){
			queryTimerTask.cancel();
		}
	}
	
	class AckTimerTask  extends TimerTask{
		@Override
		public void run() {
			if(!hasAck){
				Log.e("io","AckTimerTask !hasAck");
				ackCnt++;
				
				if(ackCnt>10){
					ackTimer.cancel();
					sendTimeOutCallBack(); //发送超时
				}else{
					reSendData();
				}
			}else{
				//ackTimer.cancel();
			}
		}	
	}
	class sendTimerTask  extends TimerTask{
		@Override
		public void run() {
			onSendTime();
		}	
	}
	
	class QueryTimerTask  extends TimerTask{
		@Override
		public void run() {
			onQueryTime();
		}	
	}
	void writeToUartCached(byte[] data){
			
		    sendList.add(data);

		}
	
	
	
	void onSendTime(){
		//Log.e("io", "onSendTime ");
	    if(!sendList.isEmpty()){

	    	
	        if(hasAck){
//	        	if(canNext!=0){
//	        		canNext--;
	        	for(int i = 0;i<sendList.size();i++ ){
	        		sendData = (byte[])sendList.get(i);
	        		if(sendData!=null){
	        			sendList.remove(i);
	        	    	Log.e("io", "onSendTime !sendList.isEmpty() &&sendData!=null");
	        			sendCmd(sendData);
	        			startAckTimer();
	        			break;
	        			}
	        		}
      
	        }
	    }
	}
	void onQueryTime(){
		if(!hasResult){
			cmd_readState();
		}
	}
	
	///////////////////////回调接口////////////////////////////////

	CallBack callBack=null;
	public  void setCallBack(CallBack call) {
		// TODO Auto-generated method stub
		callBack = call;
	}

	public interface CallBack {
		
		void cupDroped();
		void cupStuck();
		void noCupDrop();
		void dropCupTimeOut();
		void hasDirtyCup();
		void powderDroped();
		void sendTimeOut();

	}
	
	private void cupDropedCallBack(){
		cancelQueryTimer();
		if(callBack!=null)
			callBack.cupDroped();
		
	}
	private void noCupCallBack(){
		cancelQueryTimer();
		if(callBack!=null)
			callBack.noCupDrop();
		
	}
	private void dropCupTimeOutCallBack(){
		cancelQueryTimer();
		if(callBack!=null)
			callBack.dropCupTimeOut();
		
	}
	private void cupStuckCallBack(){
		cancelQueryTimer();
		if(callBack!=null)
			callBack.cupStuck();
		
	}
	private void hasDirtyCupCallBack(){
		if(callBack!=null)
			callBack.hasDirtyCup();
		
	}
	private void dropPowderCallBack(){
		cancelQueryTimer();
		if(callBack!=null)
			callBack.powderDroped();
		
	}
	
	private void sendTimeOutCallBack(){
		if(callBack!=null)
			callBack.sendTimeOut();
		
	}




	/*
	 *查询状态
	 */
	public void cmd_readState(){
		packCmd(Cmd_readState,(byte) 0);
	}
	/*
	 * 握手
	 */
	public void cmd_handShake(){
		packCmd(Cmd_handshake,(byte) 0);
	}
	/*
	 * 落杯
	 */
	public void cmd_dropCup(){
		packCmd(Cmd_dropCup,(byte) 0);
	}
	/*
	 * 设置出粉
	 */
	public void cmd_setLeftPowder(int time){
		packCmd(Cmd_setLeftPowder,(byte) time);
	}
	public void cmd_setLeftWater(int time){
		packCmd(Cmd_setLeftWater,(byte) time);
	}
	public void cmd_setCenterPowder(int time){
		packCmd(Cmd_setCenterPowder,(byte) time);	
	}
	public void cmd_setCenterWater(int time){
		packCmd(Cmd_setCenterWater,(byte) time);	
	}
	public void cmd_setRightPowder(int time){
		packCmd(Cmd_setRightPowder,(byte) time);
	}
	public void cmd_setRightWater(int time){
		packCmd(Cmd_setRightWater,(byte) time);
	}

	public void cmd_pushLeftPowder(){
		packCmd(Cmd_pushPowder,BIT0);
	}
	public void cmd_pushLeftPowder(int power,int water){
		packCmd(Cmd_setLeftWater,(byte) water);		
		packCmd(Cmd_setLeftPowder,(byte) power);
		packCmd(Cmd_pushPowder,BIT0);
	}
	public void cmd_pushCenterPowder(int power,int water){
		packCmd(Cmd_setCenterWater,(byte) water);		
		packCmd(Cmd_setCenterPowder,(byte) power);
		packCmd(Cmd_pushPowder,BIT1);
	}
	public void cmd_pushRightPowder(int power,int water){
		packCmd(Cmd_setRightWater,(byte) water);		
		packCmd(Cmd_setRightPowder,(byte) power);
		packCmd(Cmd_pushPowder,BIT2);
	}
	public void cmd_pushWater(int time){
		packCmd(Cmd_setRightWater,(byte) time);
		packCmd(Cmd_pushPowder,BIT3);
	}
	
	//程序结束时调用
	public void cleanTimer(){
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

}
