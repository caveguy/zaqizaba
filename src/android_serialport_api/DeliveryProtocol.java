package android_serialport_api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.coffemachinev2.R;

public class DeliveryProtocol {
	Context context;
	byte[] sendData=null;
	boolean hasAck=false;
	int ackCnt=0;
	private Handler myHandler=new Handler();
	SerialPortUtil serialPortUtil=null;
	private SerialPort mSerialPort;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread; 
	byte cmd_deliver=0x53;
	byte cmd_state=0x52;
	int col=0;
	int row=0;
	
	boolean isDebug=true;
	boolean finished=false;

	Timer ackTimer=null;

	
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
		if(num<5)
			return ;
		//Log.e("protocol","parseInput!!!num="+num);
		if(data[0]==(byte)0xaa&&data[num-1]==(byte)0xac){
			if(data[num-2]==getCheckSun(data,num)){
				hasAck=true;//收到回复
				if(data[1]==cmd_deliver){ //出货返回
					byte state=data[2];
					if(state==0x30){//idle
						//空闲说明指令被接收,开始发送查询指令
						send_checkState(col, row);
					}
					else if(state==0x31){//busy
						//延时一段时间后重新发送
						myHandler.postDelayed(new Runnable(){

							@Override
							public void run() {
								reSendData();
							}
							
						}, 1000);	
					}	
				}
				else if(data[1]==cmd_state){
					byte state=data[2];
					if(state==0x30){//busy
						myHandler.postDelayed(new Runnable(){
							@Override
							public void run() {
								reSendData();
							}
							
						}, 1000);
					}
					else if(state>0x30){//完成
						finished();
					}
				}
				

			}
		}
	}
	

	
	void finished(){
		finished=true;
		deliveredCallBack();
	}

	byte getCheckSun(byte[] data,int len){	
		int length=len-2;
		byte ck=0;
		for(int i=1;i<length;i++){
			ck=(byte) (ck^data[i]);
		}
		return ck;
	}

	byte getCheckSun(byte[] data){	
		int length=data.length-2;
		byte ck=0;
		for(int i=1;i<length;i++){
			ck=(byte) (ck^data[i]);
		}
		return ck;
	}
	
	

	
	private void send_deliverAGood(int col,int row){
		byte[] data = new byte[] { 
				(byte) 0xaa,
				(byte) cmd_deliver,
				(byte) (col+0x30), (byte)(row+0x30),
				(byte) 0,(byte)0,(byte) 0 ,
				(byte)0,
				(byte)0xac};
			data[7]=getCheckSun(data);
			sendCmd(data);
	}
	
	private void send_checkState(int col,int row){
		byte[] data = new byte[] { 
				(byte) 0xaa,
				(byte) cmd_state,
				(byte) (col+0x30), (byte)(row+0x30),
				(byte) 0,(byte)0,(byte) 0 ,
				(byte)0,
				(byte)0xac};
			data[7]=getCheckSun(data);
			sendCmd(data);
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
	
	
	
	
	private void sendCmd(byte[] data){
		sendData=data;
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
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	

	

	private void startAckTimer(){
		hasAck=false;
		ackCnt=0;
		Log.e("modbus","startAckTimer############");
		if(ackTimer!=null){
			ackTimer.cancel();
			ackTimer=null;
		}
		ackTimer=new Timer();
		ackTimer.schedule(new ackTimerTask(), 500,1000);
	}
	
	
	

	
	
	class ackTimerTask  extends TimerTask{
		@Override
		public void run() {
			if(!hasAck){
				ackCnt++;
				
				if(ackCnt>10){
					ackTimer.cancel();
					noDeliverCallBack(); //发送失败，出货失败
				}else{
					reSendData();
				}
			}else{
				ackTimer.cancel();
			}
		}	
	}
	

	
	
	
	
	///////////////////////回调接口////////////////////////////////

	CallBack callBack=null;
	public  void setCallBack(CallBack call) {
		// TODO Auto-generated method stub
		callBack = call;
	}

	public interface CallBack {
		
		void delivered();
		void noDeliver();
	}
	
	private void deliveredCallBack(){
		if(callBack!=null)
			callBack.delivered();
		
	}
	private void noDeliverCallBack(){
		if(callBack!=null)
			callBack.noDeliver();
		
	}
	//对外接口，出货
	public void cmd_deliver(int col,int row){
		this.col=col;
		this.row=row;
		send_deliverAGood(col,row);
		startAckTimer();
	}
}
