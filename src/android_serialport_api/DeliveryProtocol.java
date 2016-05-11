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
	final byte Cmd_Light=0x22;
	
//	final byte Cmd_greedLight=0x23;
	final byte Cmd_setLeftPowder=0x2a;
	final byte Cmd_setLeftWater=0x2b;
	final byte Cmd_setLeftPreWater=0x27;
	final byte Cmd_setCenterPowder=0x2c;
	final byte Cmd_setCenterWater=0x2d;
	final byte Cmd_setCenterPreWater=0x28;
	final byte Cmd_setRightPowder=0x2e;
	final byte Cmd_setRightWater=0x2f;
	final byte Cmd_setRightPreWater=0x29;
	final byte Cmd_pushPowder=0x30;
	
	final byte Cmd_readState=0x50;
	final byte Cmd_readErros=(byte) 0x51;//
	final byte Cmd_readLower8bits=(byte) 0xb9;

	final byte Cmd_readHIght8bits=(byte) 0xba;//?
	final byte Cmd_setOutputLower8Bits=(byte) 0xc0;//
	final byte Cmd_setOutputHight8Bits=(byte) 0xc1;//
//	final byte Cmd_readBusy=(byte) 0x50;//

	final byte BIT0=(byte) 0x01;
	final byte BIT1=(byte) 0x02;
	final byte BIT2=(byte) 0x04;
	final byte BIT3=(byte) 0x08;
	final byte BIT4=(byte) 0x10;
	final byte BIT5=(byte) 0x20;
	final byte BIT6=(byte) 0x40;
	final byte BIT7=(byte) 0x80;
	final byte Bit_detectCup=BIT2;
	final byte DropCup_finish =(byte) 0x0;
	final byte DropCup_nocup =(byte) 0x01;
	final byte DropCup_timeOut =(byte) 0x02;
	final byte DropCup_stuck =(byte) 0x03;
	final byte DropCup_dirtyCup =(byte) 0x04;
	final byte DropCup_busy =(byte) 0x07;
//	final byte DropPowder_finish =(byte) 0x07;
//	final byte DropCup_nocup =(byte) 0x01;
	final int SendTimerDuaration=200;
	final int AckTimerDuaration=150;
	final int QueryTimerDuaration=500;
	final int ErrorQueryTimerDuaration=5*1000;
	
	final byte Query_dirtyCup=0x11;
	final byte Query_cupToken=0x22;	
	final byte Query_hasCup=0x33;
	
	final byte RedLight=BIT0;
	final byte GreenLight=BIT1;
	final int Max_ackRetryCnt=3;
	boolean isDebug=true;
	boolean isFinished=false;
	//boolean hasResult=false;
	//boolean isQueryCupDrop=false;
	byte curState=0;
	Timer sendTimer=null;
	Timer ackTimer=null;
	Timer queryTimer=null;
	final String TAG="DeliveryProtocol";
	AckTimerTask ackTimerTask=null;
	QueryTimerTask queryTimerTask=null;
	SendTimerTask sendTimerTask=null;
	byte query_what=0;
	boolean isConnect=false;
//	boolean inQueryState=false;  //是否处在查询状态，
//	boolean inAckState=false;  //是否处在应答状态，
	
	//int canNext=0;
	ArrayList<byte[]> sendList=new ArrayList<byte[]>();
	
	void getAck(){
		hasAck=true;	
		cancelAckTimerTask();
		onGetAckCallBack();
	}
	public DeliveryProtocol(Context c){
		context=c;
		//computeCrcTable();
		initSerialPort();
		startSendTimer();
		startQueryErrorTask();
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
		parseInput(buffer,size);
	}
	
	private void parseInput(byte[] data,int num){
		if(num<4)
			return ;
		//Log.d("protocol","parseInput!!!num="+num);
		//Log.d("rec","data[3]="+(int)data[3]);
	//	Log.d("rec","getCheckSun(data,4)="+getCheckSun(data,4));
		//if(data[0]==(byte)0xaa){
		if(data[0]==(byte)0xaa&&data[3]==getCheckSun(data,4)){
			//Log.d("rec","rec right!!");
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
						break;
					case Cmd_readErros:
						dealReply_error(reply);
						break;
					case Cmd_readLower8bits:
						dealReply_OutPutState(reply);
						break;
						
				}
		}
	}
	

	
//	void finished(){
//		isFinished=true;
//		cupDropedCallBack();
//	}

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
			startQueryTimer(Cmd_readState);
		}

	}
	
	void dealReply_OutPutState(byte data){
		
		Log.d(TAG, "dealReply_OutPutState!! data="+data);
		if((data&Bit_detectCup)!=0){  //没有杯子
			
			if(query_what==Query_dirtyCup){ //查询脏杯子是否拿走
				
				cmd_dropCup();
			}
			else if(query_what==Query_cupToken){ //查询咖啡是否拿走
				
				tradeFinishCallBack();
			}
		}else{ //有杯子，继续检测
			if(query_what==Query_hasCup){
				cancelQueryTimerTask();
				cupReadyCallBack();
			}
		}
	
	}
	
	
	void dealReply_set(byte data){

	}
	void dealReply_error(byte data){
		boolean isError=(data&BIT0)!=0;
		if(isError){
			noWaterCallBack();
		}
//		if(isError){
//			startQueryTimer(Cmd_readErros);
//		}else{
//			cancelQueryTimerTask();
//		}
	}

	void dealReply_status(byte data){
		//readStatusCallBack(data);
		if(curState==Cmd_dropCup){
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
		}else if(curState==Cmd_pushPowder){	
			byte powderState=(byte) (data&0x18);
			if(powderState==0){
				dropPowderCallBack();
			}	
		}
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
	
	private void startQueryTimer(byte cmd){
		Log.d(TAG, "startQueryTimer query_what="+cmd);
		query_what=cmd;
		if(queryTimer==null){
			queryTimer=new Timer();
		}
		cancelQueryTimerTask();
		queryTimerTask=new QueryTimerTask();
		queryTimerTask.inQueryState=true;
		queryTimer.schedule(queryTimerTask, QueryTimerDuaration,QueryTimerDuaration);	
	}	
	private void startErrorQueryTimer(){
	//	Log.d(TAG, "startQueryTimer query_what="+cmd);
		query_what=Cmd_readErros;
		if(queryTimer==null){
			queryTimer=new Timer();
		}
		cancelQueryTimerTask();
		queryTimerTask=new QueryTimerTask();
		queryTimerTask.inQueryState=true;
		queryTimer.schedule(queryTimerTask, QueryTimerDuaration,ErrorQueryTimerDuaration);	
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
			Log.d(TAG,"onQueryTime inQueryState="+inQueryState+"query_what="+query_what);
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
	        	    	Log.e(TAG, "onSendTime !sendList.isEmpty() &&sendData!=null");
	        			sendCmd(sendData);
	        			startAckTimer();
	        			break;
	        			}
	        		}
      
	        }
	    }
	}
	void onQueryTime(){
	
			switch(query_what){
			case Query_dirtyCup:
			case Query_cupToken:
			case Query_hasCup:	  //
				cmd_ReadCupIsToke();
				break;
			case Cmd_readState:
				cmd_readState();
				break;
			case Cmd_readErros:
				cmd_readError();
				break;
			}
	}
	public void startQueryErrorTask(){
		startErrorQueryTimer();
		//startQueryTimer(Cmd_readErros);
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
		void noWater();
		void dropCupTimeOut();
		void hasDirtyCup();
		void powderDroped();
		void sendTimeOut();
		void tradeFinish();
		void startDropCup();
		void onDisable();
		void onGetConnect();
		void cupReady();//为了不落杯的程序准备
	//	void cupNotReady();//为了不落杯的程序准备
	}
	
	private void tradeFinishCallBack(){
	//	cmd_cancelLight(); //交易结束后关灯
		Log.d(TAG,"!!!!tradeFinishCallBack");
		cancelQueryTimerTask();
		if(callBack!=null)
			callBack.tradeFinish();
	}
	private void noWaterCallBack(){
		Log.d(TAG,"!!!!noWaterCallBack");
		if(callBack!=null)
			callBack.noWater();
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
		cancelQueryTimerTask();
		if(callBack!=null)
			callBack.cupDroped();
		
	}
	private void cupReadyCallBack(){
		Log.d(TAG,"!!!!cupReadyCallBack");
		if(callBack!=null)
			callBack.cupReady();
		
	}
//	private void cupNotReadyCallBack(){
//		//cancelQueryTimerTask();
//		if(callBack!=null)
//			callBack.cupNotReady();
//		
//	}
	
	private void noCupCallBack(){
		Log.d(TAG,"!!!!noCupCallBack");
		cancelQueryTimerTask();
		if(callBack!=null){
			callBack.onDisable();
			callBack.noCupDrop();
		}
		
	}
	private void dropCupTimeOutCallBack(){
		Log.d(TAG,"!!!!dropCupTimeOutCallBack");
		cancelQueryTimerTask();
		if(callBack!=null)
			callBack.dropCupTimeOut();
		
	}
	private void cupStuckCallBack(){
		Log.d(TAG,"!!!!cupStuckCallBack");
		cancelQueryTimerTask();
		if(callBack!=null)
			callBack.cupStuck();
		
	}
	private void hasDirtyCupCallBack(){
		Log.d(TAG,"!!!!hasDirtyCupCallBack");
		cancelQueryTimerTask();
		
		if(callBack!=null)
			callBack.hasDirtyCup();
		//cmd_ReadCupIsToke();
		startQueryTimer(Query_dirtyCup);
		
	}
	/*
	 * 
	 * 落粉完成 ，此时应该开始查询杯子是否被取走
	 */
	private void dropPowderCallBack(){
		Log.d(TAG,"!!!!dropPowderCallBack");
		cancelQueryTimerTask();
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




	/*
	 *查询状态
	 */
	public void cmd_readState(){
		packCmd(Cmd_readState,(byte) 0);
	}
	/*
	 *查询错误(缺水)
	 */
	public void cmd_readError(){
		packCmd(Cmd_readErros,(byte) 0);
	}
	/*
	 * 握手
	 */
	public void cmd_handShake(){
		packCmd(Cmd_handshake,(byte) 0);
	}
	/*
	 * 落杯
	 * 正常流程开始的第一个函数
	 */
	public void cmd_dropCup(){
		curState=Cmd_dropCup;
		cancelQueryTimerTask();
		startDropCupCallBack();
	//	cmd_redLight();//从开始落杯开始显示红灯
		packCmd(Cmd_dropCup,(byte) 0);
	}
	/*
	 * 查询杯子是否拿走
	 */
	private void cmd_ReadCupIsToke(){
		packCmd(Cmd_readLower8bits,(byte) 0);
	}
	
	public void  cmd_QueryCupToken(){
		startQueryTimer(Query_cupToken);
	}
	
	/*
	 * 查询杯子是否放好
	 * 为不需要落杯的程序准备
	 */
	public void cmd_isCupReady(){
		startQueryTimer(Query_hasCup);
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
	
	
	
	//糖
	public void cmd_pushLeftPowder(int power,int preWater,int water){
		curState=Cmd_pushPowder;
		packCmd(Cmd_setLeftWater,(byte) water);		
		packCmd(Cmd_setLeftPreWater,(byte) preWater);		
		packCmd(Cmd_setLeftPowder,(byte) power);
		packCmd(Cmd_pushPowder,BIT0);
	}
	//奶
	public void cmd_pushCenterPowder(int power,int preWater,int water){
		curState=Cmd_pushPowder;
		packCmd(Cmd_setCenterPreWater,(byte) preWater);
		packCmd(Cmd_setCenterWater,(byte) water);
		
		packCmd(Cmd_setCenterPowder,(byte) power);
		packCmd(Cmd_pushPowder,BIT1);
	}
	//巧克力
	public void cmd_pushRightPowder(int power,int preWater,int water){
		curState=Cmd_pushPowder;
		packCmd(Cmd_setRightPreWater,(byte) preWater);
		packCmd(Cmd_setRightWater,(byte) water);		
		packCmd(Cmd_setRightPowder,(byte) power);
		packCmd(Cmd_pushPowder,BIT2);
	}
	public void cmd_pushWater(int time){
		curState=Cmd_pushPowder;
		packCmd(Cmd_setRightWater,(byte) time);
		packCmd(Cmd_pushPowder,BIT3);
	}
	
	public void cmd_redLight(){
		packCmd(Cmd_Light,RedLight);
	}
	public void cmd_cancelLight(){
		packCmd(Cmd_Light,(byte)0);
	}
	
	public void cmd_greenLight(){
		packCmd(Cmd_Light,GreenLight);
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
