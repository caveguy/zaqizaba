package android_serialport_api;


public class Send_Command {
	
	
	public static byte cmd0x0_keyValue=0;
	public static byte cmd0x0_keyState=0;
	public static byte cmd0x0_windowNumber=0;
	public static byte cmd0x0_powder=0;
	public static byte cmd0x0_temperature=0;
	public static byte cmd0x0_taste=0;
	public static byte cmd0x0_flow=0;
		
	public static boolean cmd0x2_hasFilter=false;
	
	public static byte cmd0x3_waterHardness=0;
	public static byte cmd0x4_tasteSetting=1;
	public static byte cmd0x5_temperaSetting=0;
	public static byte[] cmd0x6_timeSetting=new byte[3];
	public static byte[] cmd0x7_preSetOn=new byte[3];
	public static byte[] cmd0x8_preSetOff=new byte[3];
	public static byte cmd0xa_language=0;
	public static byte cmd0xb_flowUnit=0;
	public static byte cmd0xc_timeUnit=0;
	public static byte cmd0xd_coffeeType=0;
	public static byte[] cmd0x12_userParas=new byte[16];
	public static byte[] cmd0x13_allParas=new byte[36];
	public static byte[] cmd0x14_tasteParas=new byte[20];
	
	
	
	public static byte[] sendCmd(int cmd){
		switch(cmd){
		case 0: //模式设置
			return cmd0x0_SendModeSetting();
		case 1:		//字符串显示设置
			return cmd0x1_SendCharsDisp();
			
		case 9:		//杯数统计
		case 0xe:	//流量
		case 0xf:	//水流量
		case 0x10:	//奶泡时间
		case 0x11:	//显示too hot
		case 0x13:  //读取所有用户参数
		case 0x15:	//工厂设置
		case 0x16:	//编码器数据
		case 0x17:	//掉电
		case 0x19:	//窗口设置
		case 0x1a:	//操作更新
		case 0x1b:	//读取当前时间
		case 0x1c:	//锅炉温度传感器设置
		case 0x20:	//输入电源频率显示
		case 0x21:	//水传感器设置
		case 0x22:	//地盘传感器设置
		case 0x23:	//粉盖传感器设置
		case 0x24:	//又旋钮设置
		case 0x25:	//水流量测试
		case 0x26:	//压粉机构行程1
		case 0x27:	//压粉机构行程2
		case 0x28:	//磨豆电机测试
		case 0x2b:	//eeprom状态显示
		case 0x2d:	//除垢统计数据显示
		case 0x2e:	//系统清洗统计数据显示
		case 0x2f:	//清楚地盘杯数统计显示
		case 0x30:	//杯数统计总数数据显示
		case 0x31: 	//电磁阀测试
			return cmd_SendNoDataCmd(cmd);		
		case 2:
			return cmd0x2_SendIfHasFilter();	
		case 3:
			return cmd0x3_SendWaterHardness();
		case 4:
			return cmd0x4_SendTasteSetting();
		case 5:
			return cmd0x5_SendTemperaSetting();
		case 6:
			return cmd0x6_SendTimeSetting();
		case 7:
			return cmd0x7_SendPreSetOn();
		case 8:
			return cmd0x8_SendPreSetOff();
		case 0xa:
			return cmd0xa_SendLanguage();
		case 0xb:
			return cmd0xb_SendFlowUnit();
		case 0xc:
			return cmd0xc_SendTimeUnit();		
		case 0xd:
			return cmd0xd_SendCoffeeType();
		case 0x12:
			return cmd0x12_SendUserParas();
		case 0x14:
			return cmd0x14_SendTasteParas();		
			default:
				return "undefined cmd!!".getBytes();
		}

	}
	
	/**
	 * 计算校验和 校验和=｛0FFH -［ ∑（DATA0 + DATA1 + DATA2+．．．．+DATA5）］｝+1
	 * 
	 * @param parameter
	 * @return
	 */
	public static byte verifySum(byte[] parameter) {
		byte tmpR = 0;
		for (int tmp = 2; tmp <parameter.length-1 ; tmp++) {
			tmpR += parameter[tmp];
		}
//		tmpR = (byte) ((byte) 0xFF - tmpR);
//		tmpR = (byte) (tmpR + (byte) 0x01);
		return tmpR;
	}

	/**
	 * 发送没按键时的指令
	 * @param data
	 * @param err
	 * @param 
	 * @return 
	 */
	
    public enum CoffeeType {
       tenong,meishi;
    }
		

	
	public static void setCoffeeType(CoffeeType type,int flow){
		if(type==CoffeeType.tenong){
			cmd0x0_keyValue=8;
		}else if(type==CoffeeType.meishi){
			cmd0x0_keyValue=9;	
		}
		cmd0x0_keyState=1;
		cmd0x0_windowNumber=2;
		cmd0x0_powder=1;
		cmd0x0_temperature=1;
		cmd0x0_taste=1;
		cmd0x0_flow=(byte)flow;
	}
	public static byte[] cmd0x0_SendClean() {
		
		//EE 0B 00 03 05 01 02 EC
		byte[] data = new byte[] { (byte) 0xee,
			(byte) 0x00,(byte) 0x00, (byte) 0x00,
			(byte) 0x05,(byte) 0x01,(byte) 0x02,
			(byte) 0xec };
		data[2]=(byte)0x0;
		data[3]=(byte)0x03;//数据个数

		byte tmpByteVtmp =verifySum(data);		
		data[1] = tmpByteVtmp;
		return data;
	}
	public static byte[] cmd0x0_SendModeSetting() {
		
		//EE 13 13 00 EC EE 09 09 00 EC
		
		
		
		byte[] data = new byte[] { (byte) 0xee,
			(byte) 0x00,(byte) 0x00, (byte) 0x00,
			(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
			(byte) 0xec };
		data[2]=(byte)0x0;
		data[3]=(byte)0x07;//数据个数

		data[4]=cmd0x0_keyValue;
		data[5]=cmd0x0_keyState;
		data[6]=cmd0x0_windowNumber;
		data[7]=cmd0x0_powder;
		data[8]=cmd0x0_temperature;
		data[9]=cmd0x0_taste;
		data[10]=cmd0x0_flow;
		byte tmpByteVtmp =verifySum(data);		
		data[1] = tmpByteVtmp;
		return data;
	}
	
	
	
	public static byte[] cmd_SendNoDataCmd(int cmd) {
		
		byte[] data = new byte[] { (byte) 0xee,
			(byte) 0x00,(byte) 0x00, (byte) 0x00,
			(byte) 0xec };
		data[2]=(byte)cmd;//命令
		data[3]=(byte)0x0;//数据个数
		byte tmpByteVtmp =verifySum(data);		
		data[1] = tmpByteVtmp;
		return data;
	}	
	//test
	public static byte[] cmd0x1_SendCharsDisp() {
		
		byte[] data = new byte[] { (byte) 0xee,
			(byte) 0x00,(byte) 0x00, (byte) 0x00,
			(byte) 0xec };
		data[2]=(byte)0x01;//命令
		data[3]=(byte)0x0;//数据个数
		byte tmpByteVtmp =verifySum(data);		
		data[1] = tmpByteVtmp;
		return data;
	}	
	
	public static byte[] cmd0x1a_SendCharsDisp() {
		
		byte[] data = new byte[] { (byte) 0xee,
			(byte) 0x00,(byte) 0x00, (byte) 0x00,
			(byte) 0xec };
		data[2]=(byte)0x1a;//命令
		data[3]=(byte)0x0;//数据个数
		byte tmpByteVtmp =verifySum(data);		
		data[1] = tmpByteVtmp;
		return data;
	}	
	public static byte[] cmd0x2_SendIfHasFilter() {
		
		byte[] data = new byte[] { (byte) 0xee,
				(byte) 0x00,(byte) 0x00, (byte) 0x00,
				(byte) 0x00,
				(byte) 0xec };
		data[2]=(byte)0x02;//命令
		data[3]=(byte)0x01;//数据个数
		
		data[4]=(byte) (cmd0x2_hasFilter?1:0);
		
		byte tmpByteVtmp =verifySum(data);		
		data[1] = tmpByteVtmp;
		return data;
	}	
	
	public static byte[] cmd0x3_SendWaterHardness() {
		
		byte[] data = new byte[] { (byte) 0xee,
			(byte) 0x00,(byte) 0x00, (byte) 0x00, 
			(byte) 0x00,
			(byte) 0xec };
		data[2]=(byte)0x03;//命令
		data[3]=(byte)0x01;//数据个数
		data[4]=(byte) (cmd0x2_hasFilter?1:0);
		byte tmpByteVtmp =verifySum(data);		
		data[1] = tmpByteVtmp;
		return data;
	}	
	
	public static byte[] cmd0x4_SendTasteSetting() {
		
		byte[] data = new byte[] { (byte) 0xee,
			(byte) 0x00,(byte) 0x00, (byte) 0x00, 
			(byte) 0x00,
			(byte) 0xec };
		data[2]=(byte)0x04;//命令
		data[3]=(byte)0x01;//数据个数
		data[4]=(byte)cmd0x4_tasteSetting;
		byte tmpByteVtmp =verifySum(data);		
		data[1] = tmpByteVtmp;
		return data;
	}		
	
	public static byte[] cmd0x5_SendTemperaSetting() {
		
		byte[] data = new byte[] { (byte) 0xee,
			(byte) 0x00,(byte) 0x00, (byte) 0x00, 
			(byte) 0x00,
			(byte) 0xec };
		data[2]=(byte)0x05;//命令
		data[3]=(byte)0x01;//数据个数
		data[4]=(byte)cmd0x5_temperaSetting;
		byte tmpByteVtmp =verifySum(data);		
		data[1] = tmpByteVtmp;
		return data;
	}	
	
	
	public static byte[] cmd0x6_SendTimeSetting() {
		
		byte[] data = new byte[] { (byte) 0xee,
			(byte) 0x00,(byte) 0x00, (byte) 0x00, 
			(byte) 0x00,(byte) 0x00,(byte) 0x00,
			(byte) 0xec };
		data[2]=(byte)0x06;//命令
		data[3]=(byte)0x03;//数据个数
		for(int i=0;i<3;i++){
			data[4+i]=cmd0x6_timeSetting[i];
		}
		byte tmpByteVtmp =verifySum(data);		
		data[1] = tmpByteVtmp;
		return data;
	}	
	
	public static byte[] cmd0x7_SendPreSetOn() {
		
		byte[] data = new byte[] { (byte) 0xee,
			(byte) 0x00,(byte) 0x00, (byte) 0x00, 
			(byte) 0x00,(byte) 0x00,(byte) 0x00,
			(byte) 0xec };
		data[2]=(byte)0x07;//命令
		data[3]=(byte)0x03;//数据个数
		for(int i=0;i<3;i++){
			data[4+i]=cmd0x7_preSetOn[i];
		}
		byte tmpByteVtmp =verifySum(data);		
		data[1] = tmpByteVtmp;
		return data;
	}	
	public static byte[] cmd0x8_SendPreSetOff() {
		//EE 08 00 03 00 01 04 EC
		byte[] data = new byte[] { (byte) 0xee,
				(byte) 0x00,(byte) 0x00, (byte) 0x00, 
				(byte) 0x00,(byte) 0x00,(byte) 0x00,
				(byte) 0xec };
		data[2]=(byte)0x08;//命令
		data[3]=(byte)0x03;//数据个数
		for(int i=0;i<3;i++){
			data[4+i]=cmd0x8_preSetOff[i];
		}
		byte tmpByteVtmp =verifySum(data);		
		data[1] = tmpByteVtmp;
		return data;
	}	
	
//	public static byte[] cmd0x9_SendcupsNum() {
//		byte[] data = new byte[] { (byte) 0xee,
//			(byte) 0x00,(byte) 0x00, (byte) 0x00, 
//			(byte) 0xec };
//		data[2]=(byte)0x09;//命令
//		data[3]=(byte)0x0;//数据个数
//		byte tmpByteVtmp =verifySum(data);		
//		data[1] = tmpByteVtmp;
//		return data;
//	}
	public static byte[] cmd0xa_SendLanguage() {		
		byte[] data = new byte[] { (byte) 0xee,
			(byte) 0x00,(byte) 0x00, (byte) 0x00, 
			(byte) 0x00,
			(byte) 0xec };
		data[2]=(byte)0x0a;//命令
		data[3]=(byte)0x01;//数据个数
		data[4]=(byte)cmd0xa_language;
		byte tmpByteVtmp =verifySum(data);		
		data[1] = tmpByteVtmp;
		return data;
	}
	
	public static byte[] cmd0xb_SendFlowUnit() {
		
		byte[] data = new byte[] { (byte) 0xee,
			(byte) 0x00,(byte) 0x00, (byte) 0x00, 
			(byte) 0x00,
			(byte) 0xec };
		data[2]=(byte)0x0b;//命令
		data[3]=(byte)0x01;//数据个数
		data[4]=(byte)cmd0xb_flowUnit;
		byte tmpByteVtmp =verifySum(data);		
		data[1] = tmpByteVtmp;
		return data;
	}
	
	public static byte[] cmd0xc_SendTimeUnit() {
		
		byte[] data = new byte[] { (byte) 0xee,
			(byte) 0x00,(byte) 0x00, (byte) 0x00, 
			(byte) 0x00,
			(byte) 0xec };
		data[2]=(byte)0x0c;//命令
		data[3]=(byte)0x01;//数据个数
		data[4]=(byte)cmd0xc_timeUnit;
		byte tmpByteVtmp =verifySum(data);		
		data[1] = tmpByteVtmp;
		return data;
	}	
	public static byte[] cmd0xd_SendCoffeeType() {
		byte[] data = new byte[] { (byte) 0xee,
			(byte) 0x00,(byte) 0x00, (byte) 0x00, 
			(byte) 0x00,
			(byte) 0xec };
		data[2]=(byte)0x0d;//命令
		data[3]=(byte)0x01;//数据个数
		data[4]=(byte)cmd0xd_coffeeType;
		byte tmpByteVtmp =verifySum(data);		
		data[1] = tmpByteVtmp;
		return data;
	}		

	public static byte[] cmd0x12_SendUserParas() {
		
		byte[] data = new byte[] { (byte) 0xee,
				(byte) 0x00,(byte) 0x00, (byte) 0x00, 
				(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
				(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
				(byte) 0xec };
		data[2]=(byte)0x12;//命令
		data[3]=(byte)16;//数据个数
		for(int i=0;i<16;i++){
			data[4+i]=cmd0x12_userParas[i];
		}
		
		byte tmpByteVtmp =verifySum(data);		
		data[1] = tmpByteVtmp;
		return data;
	}
	
	public static byte[] cmd0x13_SendAllParas2() {
		
		byte[] data = new byte[] { (byte) 0xee,
				(byte) 0x00,(byte) 0x00, (byte) 0x00, 
				(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
				(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
				(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
				(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
				(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
				(byte) 0xec };
		data[2]=(byte)0x13;//命令
		data[3]=(byte)36;//数据个数
		for(int i=0;i<36;i++){
			data[4+i]=cmd0x13_allParas[i];
		}
		
		byte tmpByteVtmp =verifySum(data);		
		data[1] = tmpByteVtmp;
		return data;
	}	
	
	public static byte[] cmd0x14_SendTasteParas() {
		
		byte[] data = new byte[] { (byte) 0xee,
				(byte) 0x00,(byte) 0x00, (byte) 0x00, 
				(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
				(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
				(byte) 0x00,(byte) 0x00,(byte) 0x00,(byte) 0x00,
				(byte) 0xec };
		data[2]=(byte)0x14;//命令
		data[3]=(byte)20;//数据个数
		for(int i=0;i<20;i++){
			data[4+i]=cmd0x14_tasteParas[i];
		}
		
		byte tmpByteVtmp =verifySum(data);		
		data[1] = tmpByteVtmp;
		return data;
	}	

	
	

}
