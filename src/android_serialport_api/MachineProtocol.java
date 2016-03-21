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

import com.example.coffemachinev2.R;

public class MachineProtocol {
	Context context;
	private final int Cmd0x1= 0x1;
	private final String TAG="MachineProtocol";
	private Handler myHandler=new Handler();
	SerialPortUtil serialPortUtil=null;
	private SerialPort mSerialPort;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread; 
	boolean isDebug=false;
	ArrayList<byte[]> sendList=new ArrayList<byte[]>();
	byte[] sendData=null;
	int queryCnt=0;
	static byte last_Cmd1_data0=0;
	final int SendTimerDuaration=300;
	MyTimerTask myTimerTask=null;
	Timer myTimer=null;
	Timer sendTimer=null;
	private final int InitCnt=10;
	
	public MachineProtocol(Context c){
		context=c;
		initSerialPort();
		initMachine();
		startSendTimer();
		startTimer();
		//myTimerTask=new MyTimerTask();
		
	}
	
	private void initSerialPort() {
		try {
			
			String path =context.getString(R.string.McUartName) ;	
			serialPortUtil = new SerialPortUtil();
			
			
			mSerialPort = serialPortUtil.getSerialPort(path,115200);
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
		if(isDebug)
			showLog(TAG+" Recivedata", buffer,size) ;
		int num=ParseReceiveCommand.ParseAllCmd(buffer,size);
		//Log.e(TAG+"revice", "num="+num);
		
		 if(num!=0){
			 
			 
//			 ParseReceiveCommand.setCallBack(new ParseReceiveCommand.CallBack() {
//				
//				@Override
//				public void onParsed(int cmd) {
//					// TODO Auto-generated method stub
//					 if(cmd==1){
//						String dispString= ParseReceiveCommand.getDispStringId(context);
//						//mySharePreference.setStringValue(MC_state, dispString);
//			             Message message = new Message();      
//			             message.what = Cmd0x1; 
//			             message.obj=dispString;
//			             myHandler.sendMessage(message); 
//			             last_Cmd1_data0=ParseReceiveCommand.cmd1_data0;
//					 }
//					 else if(cmd==0x19){
//						 sendCmd(0x13);//init
//					 }
//				}
//			});

		 }
		 


		
	}
	
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
	
	
	void writeToUartCached(byte[] data){
	    sendList.add(data);
	}
	void sendCmdId(int cmd){
		byte[] sendData=Send_Command.sendCmd(cmd);
		if(sendData!=null){
			if(isDebug)
				showLog("send",sendData,sendData.length);	
			writeToUartCached(sendData);
		
		}

//		if (mOutputStream != null) {
//			try {
//				
//				Log.d(TAG,"mOutputStream.write(sendArray)!!!");
//				byte[] sendData=Send_Command.sendCmd(cmd);
//				if(sendData!=null){
//					showLog("send",sendData,sendData.length);	
//				mOutputStream.write(sendData);
//				}
//			} catch (IOException e) {
//
//				e.printStackTrace();
//			}
//		}
	}
	
	
	
	public void showLog(String tag, byte[] showArr,int num) {
		int i;
	final StringBuilder stringBuilder = new StringBuilder();
//	for (byte byteChar : showArr)
	for (i=0;i<num;i++){
		byte byteChar=  showArr[i];
		stringBuilder.append(String.format("%02X ", byteChar));
		
	}
	Log.e(tag, "##" + stringBuilder.toString() + "\n");

 }
	
	
	
	




	public void sendCleanCmd() {
		
		byte[] sendData=Send_Command.cmd0x0_SendClean();
		if(sendData!=null){
			if(isDebug)
				showLog(TAG+"send",sendData,sendData.length);	
			writeToUartCached(sendData);
		}
//			if (mOutputStream != null) {
//			try {
//				Log.d(TAG,"mOutputStream.write(sendArray)!!!");
//				byte[] sendData=Send_Command.cmd0x0_SendClean();
//				if(sendData!=null){
//					showLog("send",sendData,sendData.length);	
//				mOutputStream.write(sendData);
//				}
//			} catch (IOException e) {
//	
//				e.printStackTrace();
//			}
//		}
	}

	public void setEspressoCoffee() {
		Send_Command.setCoffeeType(Send_Command.CoffeeType.tenong,0x1e);
	}

	public void setAmericanCoffee() {
		Send_Command.setCoffeeType(Send_Command.CoffeeType.meishi,0x78);
	}

	public void initMachine() {
		sendCmdId(0x13);

		sendCmdId(0x09);
		setEspressoCoffee();
		
	}

	public void dropCoffee() {
		// TODO Auto-generated method stub
		Log.e(TAG,"dropCoffee()*********************");
		sendCmdId(0);
	}
	public void sendQuery() {
		// TODO Auto-generated method stub
		sendCmdId(1);
	}
	
	private void sendCmd(byte[] send){
		sendData=send;
		if (mOutputStream != null) {
			try {
				if(sendData!=null){
					if(isDebug)
						showLog("send to uart",sendData,sendData.length);	
					mOutputStream.write(sendData);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void startSendTimer(){

		if(sendTimer==null){
			sendTimer=new Timer();
		}
		
		sendTimer.schedule(new sendTimerTask(), SendTimerDuaration,SendTimerDuaration);
	}
	void startTimer(){
		if(myTimer!=null){
			myTimer.cancel();
			myTimer=null;
		}
		myTimer=new Timer();
		myTimer.schedule(new MyTimerTask(), 2000,2000);
	}
	class MyTimerTask  extends TimerTask{

		@Override
		public void run() {
			
			queryCnt++;
			if(queryCnt>InitCnt){//重新初始化
				queryCnt=0;
				initMachine();
			}else{
				sendQuery();
			}
			
		}
		
		
		
	}
	class sendTimerTask  extends TimerTask{
		@Override
		public void run() {
			onSendTime();
		}	
	}
	
	void onSendTime(){
		//Log.e("io", "onSendTime ");
	    if(!sendList.isEmpty()){
	      //  if(hasAck){
	        	for(int i = 0;i<sendList.size();i++ ){
	        		sendData = (byte[])sendList.get(i);
	        		if(sendData!=null){
	        			sendList.remove(i);
	        	    //	Log.e("io", "onSendTime !sendList.isEmpty() &&sendData!=null");
	        			sendCmd(sendData);
	        			//startAckTimer();
	        			break;
	        			}
	        		}
	        }
	    //}
	}
	
	
	
	
	
	//程序结束时调用
	public void cleanTimer(){
		if(myTimer!=null){
			myTimer.cancel();
			myTimer=null;
		}
		if(sendTimer!=null){
			sendTimer.cancel();
			sendTimer=null;
		}

	}
}
