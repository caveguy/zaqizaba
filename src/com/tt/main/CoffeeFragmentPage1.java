package com.tt.main;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.coffemachinev3.R;

public class CoffeeFragmentPage1 extends Fragment implements OnClickListener,android.widget.CompoundButton.OnCheckedChangeListener{

	CheckBox btn_coffee1,btn_coffee2,btn_coffee3,btn_coffee4;

	CheckedCallBack back=null;
	public interface CheckedCallBack{
		void onCallback(int id);
	}
	public CheckedCallBack getBack() {
		return back;
	}

	public void setCheckedCallBack(CheckedCallBack back) {
		this.back = back;
	}

	void checkedCallBack(int id){
		if(back!=null){
			back.onCallback(id);
		}
	}
	
	public static CoffeeFragmentPage1 newInstance() {
		CoffeeFragmentPage1 fragment = new CoffeeFragmentPage1();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_coffee_page1, container, false);
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
		
		if(name.length>0)
			btn_coffee1.setText(name[0]);
		if(name.length>1)
			btn_coffee2.setText(name[1]);
		if(name.length>2)
			btn_coffee3.setText(name[2]);
		if(name.length>3)
			btn_coffee4.setText(name[3]);
	}
	public void setCoffeeIconRadio(int id){
		switch(id){
			case R.id.radio_1:
				checkedCallBack(0);
				btn_coffee2.setChecked(false);
				btn_coffee3.setChecked(false);
				btn_coffee4.setChecked(false);
				break;
			case R.id.radio_2:
				checkedCallBack(1);
				btn_coffee1.setChecked(false);
				btn_coffee3.setChecked(false);
				btn_coffee4.setChecked(false);
				break;
			case R.id.radio_3:
				checkedCallBack(2);
				btn_coffee2.setChecked(false);
				btn_coffee1.setChecked(false);
				btn_coffee4.setChecked(false);
				break;
			case R.id.radio_4:
				checkedCallBack(3);
				btn_coffee1.setChecked(false);
				btn_coffee2.setChecked(false);
				btn_coffee3.setChecked(false);
				break;
			case 0:
			default:
				btn_coffee1.setChecked(false);
				btn_coffee2.setChecked(false);
				btn_coffee3.setChecked(false);
				btn_coffee4.setChecked(false);	
				break;
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
