package android_serialport_api;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

public class SerialPortUtil {
	
	//public SerialPortFinder mSerialPortFinder = new SerialPortFinder();
	private SerialPort mSerialPort = null;	
	 
	
//	public SerialPortUtil(){
//		super();
//	}
	
	
//	static SerialPortUtil Ins;
	public SerialPort getSerialPort(String portName,int baudrate) throws SecurityException, IOException, InvalidParameterException {
		if (mSerialPort == null) {

			String path =portName;//getString(R.string.uartName) ;			
			//String path = "/dev/s3c2410_serial1";
			//int baudrate = 115200;
			/* Open the serial port */
			try{
			mSerialPort = new SerialPort(new File(path), baudrate, 0);
		} catch (Exception e) {
			//DisplayError(R.string.error_security);
		}
		}
		return mSerialPort;
	}

	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}
}
