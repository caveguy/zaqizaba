package com.tt.main;

import java.io.File;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.example.coffemachinev3.R;
import com.tt.util.GetMacAddress;
import com.tt.util.Settings;
import com.tt.util.UpdateManager;

public class MaintainFragment extends Fragment implements OnClickListener,android.widget.CompoundButton.OnCheckedChangeListener{

	TextView t_maintain,t_mcDetail,t_version,t_refund,t_id,t_error;//t_netDetail,t_assistDetail;
	RadioButton radioCup1,radioCup2;
//	RadioButton radio_needBean,radio_noBean;
	CheckBox btn_debug,btn_heating;
	SeekBar seekSound;
//	boolean dropcupMode=false ;   //杯子模式，false:检查到有杯子就打咖啡，true：落杯后打咖啡
	boolean needBean=true ;   	  //
	public static DevCallBack back=null;
	RelativeLayout layout_mask;
	LinearLayout layout_volume;
	Button btn_clean,btn_mskCancel,btn_update,btn_stock;
	private ProgressBar proBar;
	Handler myHandler=null;
	private final int Handler_net=1001;
	private final int Handler_mc=1002;
//	private final int Handler_error=1003;
	private final int Handler_leave=1004;
	private final int Handler_enterDev=1005;
	private final int Handler_enterMaintain=1006;
	private final int Handler_hide=1007;
	private final int Handler_id=1008;
	private final int Handler_progress=1009;
	private final int Handler_ver=1009;
	private com.tt.util.UpdateManager manager =null; 	
	Context context=null;
	
	//MainFragment.SetDevCallBack mainCallback=null;
	public interface DevCallBack{
		void onBeanModeChanged(boolean need);
	//	void ondropcupModeChanged(boolean drop);
		void onDevModeChanged(boolean is);
		void onEnableHeating(boolean is);
		void leave();
		void clean();
	}
	
	void leave(){
		//layout_mask.setVisibility(View.GONE);
		Log.e("Maintain", "leave-------");
		if(back!=null){
			back.leave();
		}
	}
	void clean(){
		if(back!=null){
			back.clean();
		}
	}
	void setBeanMode(boolean need){
		Settings.setNeedBean(context, need);
//		if(back!=null){
//			back.onBeanModeChanged(need);
//		}
	}
	void setdropcupMode(boolean drop){
		Settings.setDropcupMode(context, drop);
//		if(back!=null){
//			back.ondropcupModeChanged(drop);
//		}
	}
	void setDevMode(boolean is){
		Settings.setIsDebug(context, is);
		if(back!=null){
			back.onDevModeChanged(is);
		}
	}
	void setEnableHeating(boolean is){
		Settings.setIsHeating(context, is);
		if(back!=null){
			back.onEnableHeating(is);
		}
	}
	
	public DevCallBack getBack() {
		return back;
	}

	public void setSelfBack(DevCallBack back) {
		this.back = back;
	}
	void setMainCallBack(){
		
		MainFragment.myCallback=new MainFragment.SetDevCallBack() {

		@Override
		public void onMcStateChanged(String state) {
			//Log.e("maintain","onMcStateChanged="+state);
			Message message=new Message();
			message.what=Handler_mc;
			message.obj=state;
			myHandler.sendMessage(message);
			
			
		}

//		@Override
//		public void onNetStateChanged(String state) {
//			Message message=new Message();
//			message.what=Handler_net;
//			message.obj=state;
//			myHandler.sendMessage(message);
//			
//			
//		}
//		@Override
//		public void onAssisStateChanged(String state) {
//			Message message=new Message();
//			message.what=Handler_assis;
//			message.obj=state;
//			myHandler.sendMessage(message);
//			
//		}
		@Override
		public void enterDevMode() {
			
			
			Message message=new Message();
			message.what=Handler_enterDev;
			//message.obj=state;
			myHandler.sendMessage(message);

		}


		@Override
		public void hide() {
			Message message=new Message();
			message.what=Handler_hide;
			myHandler.sendMessage(message);
			
		}

//		@Override
//		public void updateId(String id) {
//			Message message=new Message();
//			message.what=Handler_id;
//			message.obj=id;
//			myHandler.sendMessage(message);
//		}

		@Override
		public void enterMaintainMode(boolean refund, String errors) {
			Log.e("maintain", "enterMaintainMode!!");
			Message message=new Message();
			Bundle bundle=new Bundle();
			bundle.putBoolean("refund", refund);
			bundle.putString("errors", errors);
			
			message.what=Handler_enterMaintain;
			message.obj=bundle;
			//message.obj=refund;
			myHandler.sendMessage(message);
			
			
		}


		

	}; 
	}
//	public MainFragment.SetDevCallBack getMainCallBack(){
//		return mainCallback;
//	}
	
	public static MaintainFragment newInstance() {
		MaintainFragment fragment = new MaintainFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_mask, container, false);
		initView(view);
		setMainCallBack();
		return view;
	}
	@Override
	public void onStop() {
		super.onStop();
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	void enterDev(){
		
		Log.e("maintain","enterDevMode=");
		layout_mask.setVisibility(View.VISIBLE);
		t_maintain.setText(getActivity().getString(R.string.devMode));
		btn_mskCancel.setVisibility(View.VISIBLE);
		btn_clean.setVisibility(View.VISIBLE);
		btn_update.setVisibility(View.VISIBLE);
		btn_stock.setVisibility(View.VISIBLE);
		radioCup1.setVisibility(View.VISIBLE);
		radioCup2.setVisibility(View.VISIBLE);
		btn_debug.setVisibility(View.VISIBLE);
		btn_heating.setVisibility(View.VISIBLE);
//		radio_needBean.setVisibility(View.VISIBLE);
//		radio_noBean.setVisibility(View.VISIBLE);
		t_version.setVisibility(View.VISIBLE);
		layout_volume.setVisibility(View.VISIBLE);
		proBar.setVisibility(View.GONE);
		t_id.setVisibility(View.VISIBLE);
		t_refund.setVisibility(View.GONE);
		
	}
	void enterMaintain(Boolean refund){
		Log.e("maintain","enterMaintain ");
		t_maintain.setText(getActivity().getString(R.string.maintain));
		layout_mask.setVisibility(View.VISIBLE);
		btn_mskCancel.setVisibility(View.GONE);
		btn_clean.setVisibility(View.GONE);
		radioCup1.setVisibility(View.GONE);
		radioCup2.setVisibility(View.GONE);
//		radio_needBean.setVisibility(View.GONE);
//		radio_noBean.setVisibility(View.GONE);
		btn_debug.setVisibility(View.GONE);
		btn_heating.setVisibility(View.GONE);
		btn_update.setVisibility(View.GONE);
		btn_stock.setVisibility(View.GONE);
		t_version.setVisibility(View.GONE);
		t_id.setVisibility(View.GONE);
		proBar.setVisibility(View.GONE);
		if(refund){//已经付款状态,应该提示退款
			t_refund.setVisibility(View.VISIBLE);
		}else{
			t_refund.setVisibility(View.GONE);
		}
		layout_volume.setVisibility(View.GONE);
		
	}
	void hide(){
		Log.e("maintain","hide-----");
		layout_mask.setVisibility(View.GONE);
	}
	
	
	public void setMcState(String state){
		t_mcDetail.setText(state);
	}
	public void setErrors(String error){
		t_error.setText(error);
	}
//	public void setNetState(String state){
//		t_netDetail.setText(state);
//	}
//	public void setAssistState(String state){
//		t_assistDetail.setText(state);
//	}
    void initView(View view){
    	context=getActivity();
    	layout_volume=(LinearLayout)view.findViewById(R.id.layout_volume);
    	audioManager=(AudioManager)context.getSystemService(Service.AUDIO_SERVICE);
    	layout_mask=(RelativeLayout)view.findViewById(R.id.layout_mask);
    	btn_debug=(CheckBox)view.findViewById(R.id.btn_debug);
    	btn_heating=(CheckBox)view.findViewById(R.id.btn_heating);
    	btn_debug.setOnCheckedChangeListener(this);
    	btn_debug.setChecked(Settings.getIsDebug(context));
    	btn_heating.setOnCheckedChangeListener(this);
    	btn_heating.setChecked(Settings.getIsHeating(context));

    	
    	t_maintain=(TextView)view.findViewById(R.id.t_maintain);
    	t_mcDetail=(TextView)view.findViewById(R.id.t_mcDetail);
    	t_error=(TextView)view.findViewById(R.id.t_error);
    	t_version=(TextView)view.findViewById(R.id.t_version);
    	t_id=(TextView)view.findViewById(R.id.t_id);
    	t_refund=(TextView)view.findViewById(R.id.t_refund);
    	//t_netDetail=(TextView)view.findViewById(R.id.t_netDetail);
    	radioCup1=(RadioButton)view.findViewById(R.id.radio_cup1);
    	radioCup2=(RadioButton)view.findViewById(R.id.radio_cup2);
    //	radio_needBean=(RadioButton)view.findViewById(R.id.radio_needBean);
    //	radio_noBean=(RadioButton)view.findViewById(R.id.radio_noneedBean);
    	btn_mskCancel=(Button)view.findViewById(R.id.btn_mskCancel);
    	btn_clean=(Button)view.findViewById(R.id.btn_clean);
    	btn_clean.setOnClickListener(this);
    	btn_mskCancel.setOnClickListener(this);
    	t_id.setText(GetMacAddress.getMacAddress());
    	if(Settings.getDropcupMode(context)){
    		radioCup1.setChecked(true);	
    	}else{
    		radioCup2.setChecked(true);
    	}
//    	if(needBean){
//    		setBeanMode(true);
//    		radio_needBean.setChecked(true);	
//    	}else{
//    		setBeanMode(false);
//    		radio_noBean.setChecked(true);
//    	}
    	radioCup1.setOnCheckedChangeListener(this);
    	radioCup2.setOnCheckedChangeListener(this);
//    	radio_needBean.setOnCheckedChangeListener(this);
//    	radio_noBean.setOnCheckedChangeListener(this);
//    	
		btn_update = (Button) view.findViewById(R.id.btn_update);
		btn_update.setOnClickListener(this);
		btn_stock = (Button) view.findViewById(R.id.btn_stock);
		btn_stock.setOnClickListener(this);
		proBar=(ProgressBar)view.findViewById(R.id.progressBar);

		
		manager=UpdateManager.getInstance(context);
		manager.setCallBack(new com.tt.util.UpdateManager.CallBack(){



			@Override
			public void updateProgress(int gress) {
				Message msg = new Message();
				msg.what=Handler_progress;
				msg.arg1 = gress;
				myHandler.sendMessage(msg);
				
			}

			@Override
			public void onCurVerChanged(String ver, String verName) {
				Message msg = new Message();
				msg.what=Handler_ver;
				msg.obj = ver;
				String disp="当前版本号:"+ver+"\n"+"当前版本名:"+verName;
				myHandler.sendMessage(msg);
				
			}

			@Override
			public void onServerVerChanged(String ver, String verName) {
				Message msg = new Message();
				msg.what=Handler_ver;
				msg.obj = ver;
				String disp="服务器版本号:"+ver+"\n"+"服务器版本名:"+verName;
				myHandler.sendMessage(msg);
				
			}
			
		});
    	seekSound=(SeekBar)view.findViewById(R.id.seek_sound);
    	seekSound.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(fromUser)
					setVolume(progress);	
			}
		});
    	seekSound.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
    	seekSound.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    	
    	myHandler =new Handler(){

			@Override
			public void handleMessage(Message msg) {
			        	
			    		switch (msg.what) {
						case Handler_mc:
							setMcState(msg.obj.toString());
							break;
//						case Handler_assis:
//							setAssistState(msg.obj.toString());
//							break;
						case Handler_enterDev:
							enterDev();
							break;
						case Handler_enterMaintain:
							Bundle bundle=(Bundle) msg.obj;
							boolean refund=false;
							String errors=null;
							if(bundle.containsKey("refund"))
							  refund=bundle.getBoolean("refund");
							enterMaintain(refund);
							if(bundle.containsKey("errors")){
								errors=bundle.getString("errors");
							    setErrors(errors);
							}
							break;
						case Handler_hide:
							hide(); 
							break;
						case Handler_id:
							t_id.setText(context.getString(R.string.deviceId) +(String)msg.obj);
							break;
						case Handler_progress:
							
							   proBar.setProgress(msg.arg1);
							   proBar.setVisibility(View.VISIBLE);
						       //textView.setText("下载进度："+msg.arg1);
						       if(msg.arg1 == 100){
						    	   Intent intent = new Intent(Intent.ACTION_VIEW); 
							       String path = Environment.getExternalStorageDirectory()+UpdateManager.getFileName();
							       intent.setDataAndType(Uri.fromFile(new File(path)), 
							    		   "application/vnd.android.package-archive");   
							       startActivity(intent);
						       }
						       break;
			    		}
							
				super.handleMessage(msg);
			}
    		
    	};

    }
    	

    	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int id =buttonView.getId();
	switch(id){
			case R.id.radio_cup1:
				if(isChecked){
					setdropcupMode(true);
				}else{
					setdropcupMode(false);
				}
				break;
				
//			case R.id.radio_needBean:
//				if(isChecked){
//					setBeanMode(true);
//				}else{
//					setBeanMode(false);
//				}
//				break;
				
			case R.id.btn_debug:
				if(isChecked){
					setDevMode(true);
				}else{
					setDevMode(false);
				}
				break;
			case R.id.btn_heating:
				if(isChecked){
					setEnableHeating(true);
				}else{
					setEnableHeating(false);
				}
				break;
//			case R.id.radio_cup2:
//				if(!isChecked){
//					dropcupMode=true;
//				}else{
//					dropcupMode=false;
//				}
//				break;
		}
	}
	AudioManager audioManager=null;
	void setVolume(int volume){
		if(audioManager==null){
			audioManager=(AudioManager)context.getSystemService(Service.AUDIO_SERVICE);
		}
	    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,volume,0);//tempVolume:音量绝对值
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.btn_mskCancel:
				leave();
			break;
			case R.id.btn_clean:
				clean();
			break;
			case R.id.btn_update:
				manager.compareVersion();
				break;
			case R.id.btn_stock:
				StockFragment stockFragment=StockFragment.newInstance();
				showSet(stockFragment);
				break;
		}
	}
	
	void showSet(Fragment fragment) {

		
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.add(R.id.layout_mask, fragment).commit();
		getFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.alphain, R.anim.alphaout)
			//	.add(android.R.id.content, fragment)
				.add(R.id.layout_mask, fragment)
				.addToBackStack(null).commit();

	}
	
}
