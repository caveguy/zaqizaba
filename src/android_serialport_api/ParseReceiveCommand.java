package android_serialport_api;

import android.content.Context;

import com.example.coffemachinev2.R;


public  class  ParseReceiveCommand {
	private final static int DataStart=4;
	private final static int CmdByte=2;
	private final static int DataLenthByte=3;
	static CallBack callBack=null;
	//public static int queryCnt=0;

	
    public static String getDispStringId(Context context){
    	switch(cmd1_data0){
	    	case 0x1:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_ready);	    		
	    	case 0x2:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_heating);	    		
	    	case 0x3:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_rinsing);    		
	    	case 0x4:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_openTab);	    		
	    	case 0x5:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_close_tab);	    		
	    	case 0x6:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_too_hot);	    		
	    	case 0x7:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_mild);	    		
	    	case 0x8:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_strong);	    		
	    	case 0x9:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_2cpus);	    		
	    	case 0xa:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_cleaning);	    		
	    	case 0xb:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_emptying);	    		
	    	case 0xc:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_decalc_on);    		
	    	case 0xd:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_add_tablet);	    		
	    	case 0xe:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_no_tray);    		
	    	case 0xf:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_waiting);
	    	case 0x10:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_fault);
	    	case 0x11:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_fault1);
	    	case 0x12:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_fault2);
	    	case 0x13:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_fault3);
	    	case 0x14:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_fault4);
	    	case 0x15:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_fault5);
	    	case 0x16:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_fault6);
	    	case 0x17:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_standby);
	    	case 0x18:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_hold);
	    	case 0x19:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_powder);
	    	case 0x1a:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_normal);
	    	case 0x1b:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_steam);
	    	case 0x1c:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_myCoffee);
	    	case 0x1d:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_fill_powder);
	    	case 0x1e:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_water);
	    	case 0x1f:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_espresso);
	    	case 0x20:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_americano);
	    	case 0x30:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_filterReady);
	    	case 0x31:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_insertOpenTab);
	    	case 0x32:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_rinsingFilter);
	    	case 0x33:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_pressRinse);
	    	case 0x34:
	    		normalCallBack();//faultCallBack();
	    		return context.getString(R.string.cmd1_fillWater);
	    	case 0x35:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_fillingSystem);
	    	case 0x36:
	    		normalCallBack();//faultCallBack();
	    		return context.getString(R.string.cmd1_fillBeans);//这个要谨慎，因为无豆时也可以打咖啡
	    	case 0x37:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_selectCupButton);
	    	case 0x38:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_steamReady);
	    	case 0x39:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_cleanReady);
	    	case 0x3a:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_emptyTRay);
	    	case 0x3b:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_trayMissing);
	    	case 0x3c:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_chgFilterOpenTab);
	    	case 0x3d:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_decalcytyReady);
	    	case 0x3e:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_solventInTank);
	    	case 0x3f:
	    		normalCallBack();
	    		return context.getString(R.string.cmd1_addMorePowder);
	    	case 0x40:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_emptyGround);

	    	default:
	    		faultCallBack();
	    		return context.getString(R.string.cmd1_unknow);
    	}
    }
	
	
	
	public static byte cmd1_data0=0; //状态
	//public static byte[] cmd1_data=new byte[2]; //状态
	
	
	
	
	public static byte[] cmd6_timeSet=new byte[3];  
	public static byte[] cmd7_presetOn_time=new byte[3];  
	public static byte[] cmd8_presetOff_time=new byte[3]; 
	public static byte[] cmd9_cups=new byte[6];  	
	public static byte[] cmdc_timeUnit=new byte[1]; 	
	public static byte[] cmdd_coffeeType=new byte[1]; 	
	public static byte[] cmde_cofferFlow=new byte[3]; 	
	public static byte[] cmdf_waterFlow=new byte[2]; 		
	public static byte[] cmd10_milkTime=new byte[2]; 		
	public static byte[] cmd11_tooHot=new byte[1]; 	
	public static byte[] cmd12_settingParas=new byte[16]; 	
	public static byte[] cmd13_allSettingParas=new byte[36]; 	
	public static byte[] cmd14_tasteSettingParas=new byte[20]; 	
	public static byte[] cmd16_encode=new byte[1]; 	
	public static byte[] cmd19_windowSetting=new byte[1]; 	
	public static byte[] cmd1b_currentTime=new byte[3]; 
	
	
	public static byte[] cmd1c_sensorsTest=new byte[2]; 
	public static byte[] cmd20_powerFreq=new byte[2]; 	
	public static byte[] cmd21_waterSensorsTest=new byte[2]; 	
	public static byte[] cmd22_domainSensorsTest=new byte[2]; 			
	public static byte[] cmd23_CoverSensorsTest=new byte[2]; 	
	public static byte[] cmd24_rightKnobSensorsTest=new byte[2]; 
	public static byte[] cmd25_waterFlowTest=new byte[2]; 
	public static byte[] cmd26_machineRoute1=new byte[2]; 
	public static byte[] cmd27_machineRoute2=new byte[2]; 
	public static byte[] cmd28_motorTest=new byte[2]; 
	public static byte[] cmd2b_eepromState=new byte[2]; 
	public static byte[] cmd2d_descalingCount=new byte[2]; 
	public static byte[] cmd2e_systemCleanCount=new byte[2]; 
	public static byte[] cmd2f_cupsCleanCount=new byte[2]; 
	public static byte[] cmd30_allCupsCount=new byte[2]; 
	public static byte[] cmd31_magneticValveTest=new byte[2]; 
	
/****
 * 解析数据，返回命令号	
 * @param data
 * @return
 */
	
	public static int parseCmds(byte[] data) {
		int cmd=data[2];
		switch(cmd){
		case 1:
			cmd1_data0=data[DataStart];
		//	queryCnt--;
//			for(int i=0;i<data[DataLenthByte];i++){
//				cmd1_data0=data[DataStart+i];
//			}
			break;
		case 6:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd6_timeSet[i]=data[DataStart+i];
			}
			break;
		case 7:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd7_presetOn_time[i]=data[DataStart+i];
			}
			break;
		case 8:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd8_presetOff_time[i]=data[DataStart+i];
			}
			break;
		case 0xc:
			for(int i=0;i<data[DataLenthByte];i++){
				cmdc_timeUnit[i]=data[DataStart+i];
			}
			break;
		case 0xd:
			for(int i=0;i<data[DataLenthByte];i++){
				cmdd_coffeeType[i]=data[DataStart+i];
			}
			break;
		case 0xe:
			for(int i=0;i<data[DataLenthByte];i++){
				cmde_cofferFlow[i]=data[DataStart+i];
			}
			break;
		case 0xf:
			for(int i=0;i<data[DataLenthByte];i++){
				cmdf_waterFlow[i]=data[DataStart+i];
			}
			break;
		case 0x10:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd10_milkTime[i]=data[DataStart+i];
			}
			break;
		case 0x11:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd11_tooHot[i]=data[DataStart+i];
			}
			break;
		case 0x12:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd12_settingParas[i]=data[DataStart+i];
			}
			break;
		case 0x13:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd13_allSettingParas[i]=data[DataStart+i];
			}
			break;
		case 0x14:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd14_tasteSettingParas[i]=data[DataStart+i];
			}
			break;
		case 0x16:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd16_encode[i]=data[DataStart+i];
			}
			break;
		case 0x19:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd19_windowSetting[i]=data[DataStart+i];
			}
			break;
		case 0x1b:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd1b_currentTime[i]=data[DataStart+i];
			}
			break;
		case 0x1c:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd1c_sensorsTest[i]=data[DataStart+i];
			}
			break;
		case 0x20:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd20_powerFreq[i]=data[DataStart+i];
			}
			break;
		case 0x21:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd21_waterSensorsTest[i]=data[DataStart+i];
			}
			break;
		case 0x22:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd22_domainSensorsTest[i]=data[DataStart+i];
			}
			break;
		case 0x23:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd23_CoverSensorsTest[i]=data[DataStart+i];
			}
			break;
		case 0x24:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd24_rightKnobSensorsTest[i]=data[DataStart+i];
			}
			break;
		case 0x25:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd25_waterFlowTest[i]=data[DataStart+i];
			}
			break;
		case 0x26:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd26_machineRoute1[i]=data[DataStart+i];
			}
			break;
		case 0x27:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd27_machineRoute2[i]=data[DataStart+i];
			}
			break;
		case 0x28:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd28_motorTest[i]=data[DataStart+i];
			}
			break;
		case 0x2b:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd2b_eepromState[i]=data[DataStart+i];
			}
			break;
		case 0x2d:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd2d_descalingCount[i]=data[DataStart+i];
			}
			break;
		case 0x2e:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd2e_systemCleanCount[i]=data[DataStart+i];
			}
			break;
		case 0x2f:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd2f_cupsCleanCount[i]=data[DataStart+i];
			}
			break;
		case 0x30:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd30_allCupsCount[i]=data[DataStart+i];
			}
			break;
		case 0x31:
			for(int i=0;i<data[DataLenthByte];i++){
				cmd31_magneticValveTest[i]=data[DataStart+i];
			}
			break;
		}
		
		return cmd;
	}
	

	
	public static String ToSBC(String input) { 
		// 半角转全角： 
		char[] c = input.toCharArray(); 
		for (int i = 0; i< c.length; i++) { 
		if (c[i] == 32) { 
		c[i] = (char) 12288; 
		continue; 
		} 
		if (c[i]< 127) 
		c[i] = (char) (c[i] + 65248); 
		} 
		return new String(c); 
		}

/**   
     * 全角转换为半角   
     *    
     * @param input   
     * @return   
     */   
    public static String ToDBC(String input) {    
        char[] c = input.toCharArray();    
        for (int i = 0; i < c.length; i++) {    
            if (c[i] == 12288) {    
                c[i] = (char) 32;    
                continue;    
            }    
            if (c[i] > 65280 && c[i] < 65375)    
                c[i] = (char) (c[i] - 65248);    
        }    
        return new String(c);    
    }  

    /**   
     * 去除特殊字符或将所有中文标号替换为英文标号   
     *    
     * @param str   
     * @return   
     */   /*
    public static String stringFilter(String str) {    
        str = str.replaceAll("【", "[").replaceAll("】", "]")    
                .replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号    
        String regEx = "[『』]"; // 清除掉特殊字符    
        Pattern p = Pattern.compile(regEx);    
        Matcher m = p.matcher(str);    
        return m.replaceAll("").trim();    
    }  
   */
    
    


	
	
	
	
	
	
	



//	public static int ParseAllCmd(byte[] data,int num) {
//		//Log.e("weather", "cmd num="+num);
//		if(verifySumAndFormat(data,num))
//		{
//			return parseCmds(data);		
//			
//		}
//		return -1;
//	}
	public static int ParseACmd(byte[] data,int num) {
		//Log.e("weather", "cmd num="+num);
		if(verifySumAndFormat(data,num))
		{
			return parseCmds(data);		
			
		}
		return -1;
	}
	
	
	
	public static int ParseAllCmd(byte[] data,int num){
		int next_start=0;
		int left_num=num;
		int this_num=0;
		int  order_num=0;
		while(left_num>4){
			int datalength=data[next_start+3];
			this_num=datalength+5;
			if(data[next_start]==(byte)0xED&&data[next_start+this_num-1]==(byte)0xEC){
				byte[] temp_byte=new byte[this_num];
				for(int i=0;i<this_num;i++){
					temp_byte[i]=data[next_start+i];
				}
				int cmd=temp_byte[2];
				ParseACmd(temp_byte,this_num);
//				if(callBack!=null){
//					callBack.onParsed(cmd);
//				}
				parseCallBack(cmd);
				order_num++;
				next_start+=this_num;
				left_num-=this_num;
			}else{
				break;
			}
		}
		return order_num;
	}
	//校验和，这里的num是整个数组的长度
	public static boolean verifySumAndFormat(byte[] data,int num) {
		// int tmpL = parameter.length-1;
	
		byte tmpR = 0;
//		int datalength=data[3];
//		num=datalength+5;
		if(data[0]==(byte)0xED&&data[num-1]==(byte)0xEC)
		{
			for (int tmp = 2; tmp < num-1; tmp++) {
				tmpR += data[tmp];
			}
//			tmpR = (byte) ((byte) 0xFF - tmpR);
//			tmpR = (byte) (tmpR + (byte) 0x01);
			if(tmpR==data[1])
				return true;
			else 
				return false;
		}
		else 
			return false;
	}
	
	private static int bcdToInt(byte bcd)
	{
		int myNum;
		myNum=((bcd&0xf0)>>4)*10+(bcd&0x0f);
		return myNum;
	}

	
	public CallBack getCallBack() {
		return callBack;
	}

	public static void setCallBack(CallBack call) {
		// TODO Auto-generated method stub
		callBack = call;
	}

	static void faultCallBack(){
		if(callBack!=null){
			callBack.onFault();	
		}
	}
	static void normalCallBack(){
		if(callBack!=null){
			callBack.onWork();	
		}
	}
	
	static void parseCallBack(int cmd){
		if(callBack!=null){
			callBack.onParsed(cmd);	
		}
	}
	
	
	public interface CallBack {
		void onParsed(int cmd);
		void onFault();
		void onWork();
	}
		
}
