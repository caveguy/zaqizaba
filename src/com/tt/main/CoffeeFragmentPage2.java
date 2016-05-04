package com.tt.main;

import java.math.BigDecimal;

import tp.device.DeviceInterface.MyHandler;
import android.app.Fragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.coffemachinev3.R;
import com.tt.util.ToastShow;

public class CoffeeFragmentPage2 extends Fragment implements OnClickListener,android.widget.CompoundButton.OnCheckedChangeListener{

	CheckBox btn_coffee1,btn_coffee2,btn_coffee3,btn_coffee4;

	CallBack back;
	public interface CallBack{
		void onCallback();
	}
	public CallBack getBack() {
		return back;
	}

	public void setBack(CallBack back) {
		this.back = back;
	}

	public static CoffeeFragmentPage2 newInstance() {
		CoffeeFragmentPage2 fragment = new CoffeeFragmentPage2();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_coffee_page2, container, false);
		initView(view);
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

	
    void initView(View view){

     	btn_coffee1=(CheckBox)view.findViewById(R.id.radio_1);
     	btn_coffee2=(CheckBox)view.findViewById(R.id.radio_2);
     	btn_coffee3=(CheckBox)view.findViewById(R.id.radio_3);
     	btn_coffee4=(CheckBox)view.findViewById(R.id.radio_4);

    	btn_coffee1.setOnCheckedChangeListener(this);
    	btn_coffee2.setOnCheckedChangeListener(this);
    	btn_coffee3.setOnCheckedChangeListener(this);
    	btn_coffee4.setOnCheckedChangeListener(this);

    }
	public void setIconNames(String[] name){
		btn_coffee1.setText(name[0]);
		btn_coffee2.setText(name[0]);
		btn_coffee3.setText(name[0]);
		btn_coffee4.setText(name[0]);
	}
	void setCoffeeIconRadio(int id){
		switch(id){
			case R.id.radio_1:
				btn_coffee2.setChecked(false);
				btn_coffee3.setChecked(false);
				btn_coffee4.setChecked(false);
				break;
			case R.id.radio_2:
				btn_coffee1.setChecked(false);
				btn_coffee3.setChecked(false);
				btn_coffee4.setChecked(false);
				break;
			case R.id.radio_3:
				btn_coffee2.setChecked(false);
				btn_coffee1.setChecked(false);
				btn_coffee4.setChecked(false);
				break;
			case R.id.radio_4:
				btn_coffee1.setChecked(false);
				btn_coffee2.setChecked(false);
				btn_coffee3.setChecked(false);
				break;
			case 0:
				btn_coffee1.setChecked(false);
				btn_coffee2.setChecked(false);
				btn_coffee3.setChecked(false);
				btn_coffee4.setChecked(false);	
		
		}
	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		int id =buttonView.getId();
		if(isChecked){
			setCoffeeIconRadio(id);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
}
