package com.tt.main;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.coffemachinev3.R;
import com.tt.main.CoffeeFragmentPage1.CheckedCallBack;

public class MaintainFragment extends Fragment implements OnClickListener,android.widget.CompoundButton.OnCheckedChangeListener{

	TextView t_maintain,t_mcDetail,t_netDetail,t_version,t_refund;
	RadioButton radioCup1,radioCup2;
	RadioButton radio_needBean,radio_noBean;
	CheckBox btn_debug;
	boolean dropcupMode=false ;   //杯子模式，false:检查到有杯子就打咖啡，true：落杯后打咖啡
	boolean needBean=true ;   	  //
	public static DevCallBack back=null;
	LinearLayout layout_mask;
	Button btn_clean,btn_mskCancel;
	//MainFragment.SetDevCallBack mainCallback=null;
	public interface DevCallBack{
		void onBeanModeChanged(boolean need);
		void ondropcupModeChanged(boolean drop);
		void onDevModeChanged(boolean is);
		void leave();
		void clean();
	}
	
	void leave(){
		layout_mask.setVisibility(View.GONE);
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
		if(back!=null){
			back.onBeanModeChanged(need);
		}
	}
	void setdropcupMode(boolean drop){
		if(back!=null){
			back.ondropcupModeChanged(drop);
		}
	}
	void setDevMode(boolean is){
		if(back!=null){
			back.onDevModeChanged(is);
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
			Log.e("maintain","onMcStateChanged="+state);
			setMcState( state);
			
		}

		@Override
		public void onNetStateChanged(String state) {
			setNetState(state);
			
		}

		@Override
		public void enterDevMode() {
			Log.e("maintain","enterDevMode=");
			layout_mask.setVisibility(View.VISIBLE);
			t_maintain.setText(getActivity().getString(R.string.devMode));
			btn_mskCancel.setVisibility(View.VISIBLE);
			btn_clean.setVisibility(View.VISIBLE);
			radioCup1.setVisibility(View.VISIBLE);
			radioCup2.setVisibility(View.VISIBLE);
			btn_debug.setVisibility(View.VISIBLE);
			radio_needBean.setVisibility(View.VISIBLE);
			radio_noBean.setVisibility(View.VISIBLE);
			t_version.setVisibility(View.VISIBLE);
			t_refund.setVisibility(View.GONE);
		}

		@Override
		public void enterMaintainMode(boolean refund) {
			t_maintain.setText(getActivity().getString(R.string.maintain));
			layout_mask.setVisibility(View.VISIBLE);
			btn_mskCancel.setVisibility(View.GONE);
			btn_clean.setVisibility(View.GONE);
			radioCup1.setVisibility(View.GONE);
			radioCup2.setVisibility(View.GONE);
			radio_needBean.setVisibility(View.GONE);
			radio_noBean.setVisibility(View.GONE);
			btn_debug.setVisibility(View.GONE);
			t_version.setVisibility(View.GONE);
			if(refund){//已经付款状态,应该提示退款
				t_refund.setVisibility(View.VISIBLE);
			}else{
				t_refund.setVisibility(View.GONE);
			}
			
		}

		@Override
		public void hide() {
			// TODO Auto-generated method stub
			
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

	public void setMcState(String state){
		t_mcDetail.setText(state);
	}
	public void setNetState(String state){
		t_netDetail.setText(state);
	}
    void initView(View view){
    	layout_mask=(LinearLayout)view.findViewById(R.id.layout_mask);
    	btn_debug=(CheckBox)view.findViewById(R.id.btn_debug);
    	btn_debug.setOnCheckedChangeListener(this);
    	t_maintain=(TextView)view.findViewById(R.id.t_maintain);
    	t_mcDetail=(TextView)view.findViewById(R.id.t_mcDetail);
    	t_version=(TextView)view.findViewById(R.id.t_version);
    	t_refund=(TextView)view.findViewById(R.id.t_refund);
    	t_netDetail=(TextView)view.findViewById(R.id.t_netDetail);
    	radioCup1=(RadioButton)view.findViewById(R.id.radio_cup1);
    	radioCup2=(RadioButton)view.findViewById(R.id.radio_cup2);
    	radio_needBean=(RadioButton)view.findViewById(R.id.radio_needBean);
    	radio_noBean=(RadioButton)view.findViewById(R.id.radio_noneedBean);
    	btn_mskCancel=(Button)view.findViewById(R.id.btn_mskCancel);
    	btn_clean=(Button)view.findViewById(R.id.btn_clean);
    	btn_clean.setOnClickListener(this);
    	btn_mskCancel.setOnClickListener(this);
    	if(dropcupMode){
    		radioCup1.setChecked(true);	
    	}else{
    		radioCup2.setChecked(true);
    	}
    	if(needBean){
    		setBeanMode(true);
    		radio_needBean.setChecked(true);	
    	}else{
    		setBeanMode(false);
    		radio_noBean.setChecked(true);
    	}
    	radioCup1.setOnCheckedChangeListener(this);
    	radioCup2.setOnCheckedChangeListener(this);
    	radio_needBean.setOnCheckedChangeListener(this);
    	radio_noBean.setOnCheckedChangeListener(this);


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
				
			case R.id.radio_needBean:
				if(isChecked){
					setBeanMode(true);
				}else{
					setBeanMode(false);
				}
				break;
				
			case R.id.btn_debug:
				if(isChecked){
					setDevMode(true);
				}else{
					setDevMode(false);
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
		}
	}
	

	
}
