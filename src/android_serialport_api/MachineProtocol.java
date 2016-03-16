package android_serialport_api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

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
	boolean isDebug=true;

	int queryCnt=0;
	static byte last_Cmd1_data0=0;
	MyTimerTask myTimerTask=null;
	Timer myTimer=null;
	private final int InitCnt=10;
	
	public MachineProtocol(Context c){
		context=c;
		initSerialPort();
		initMachine();
		startTimer();
		//myTimerTask=new MyTimerTask();
		startTimer();
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
		//Log.d(TAG,"onDataReceived!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		if(isDebug)
		showLog(TAG+"Recivedata", buffer,size) ;
		int num=ParseReceiveCommand.ParseAllCmd(buffer,size);
		Log.e(TAG+"revice", "num="+num);
		
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
	
	private final ReentrantLock lock = new ReentrantLock();	
	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				int size;
				try {
					
					byte[] buffer = new byte[256];
					if (mInputStream == null)
						return;
					size = mInputStream.read(buffer);
					if (size > 0) {	
						if(size>buffer.length)
							size=buffer.length;
						lock.lock();
						onDataReceived(buffer, size);
						//oldbuffer=buffer;	
					}
				} catch (IOException e) {
		
					e.printStackTrace();
					return;
				}
				finally{
					lock.unlock();
				}
			}
		}
	}
	
	
	
	void sendCmd(int cmd){
		if (mOutputStream != null) {
			try {
				
				Log.d(TAG,"mOutputStream.write(sendArray)!!!");
				byte[] sendData=Send_Command.sendCmd(cmd);
				if(sendData!=null){
					showLog("send",sendData,sendData.length);	
				mOutputStream.write(sendData);
				}
			} catch (IOException e) {

				e.printStackTrace();
			}
		}
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
			if (mOutputStream != null) {
			try {
				Log.d(TAG,"mOutputStream.write(sendArray)!!!");
				byte[] sendData=Send_Command.cmd0x0_SendClean();
				if(sendData!=null){
					showLog("send",sendData,sendData.length);	
				mOutputStream.write(sendData);
				}
			} catch (IOException e) {
	
				e.printStackTrace();
			}
		}
	}

	public void setEspressoCoffee() {
		Send_Command.setCoffeeType(Send_Command.CoffeeType.tenong,0x1e);
	}

	public void setAmericanCoffee() {
		Send_Command.setCoffeeType(Send_Command.CoffeeType.meishi,0x78);
	}

	public void initMachine() {
		sendCmd(0x13);
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendCmd(0x09);
	}

	public void dropCoffee() {
		// TODO Auto-generated method stub
		Log.e(TAG,"dropCoffee()*********************");
		sendCmd(0);
	}
	public void sendQuery() {
		// TODO Auto-generated method stub
		sendCmd(1);
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
	//程序结束时调用
	public void cleanTimer(){
		myTimer.cancel();
		myTimer=null;

	}
}
