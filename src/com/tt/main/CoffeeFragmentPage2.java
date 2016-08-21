package com.tt.main;

import android.app.Fragment;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.coffemachinev3.R;

public class CoffeeFragmentPage2 extends Fragment implements OnClickListener,android.widget.CompoundButton.OnCheckedChangeListener{

	CheckBox btn_coffee1,btn_coffee2,btn_coffee3,btn_coffee4;
	TextView t_name1,t_name2,t_name3,t_name4;
	TextView t_org1,t_org2,t_org3,t_org4;
	TextView t_price1,t_price2,t_price3,t_price4;
	private final String Tag="CoffeeFragmentPage2";
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
      	t_name1=(TextView)view.findViewById(R.id.t_name1);
    	t_name2=(TextView)view.findViewById(R.id.t_name2);
    	t_name3=(TextView)view.findViewById(R.id.t_name3);
    	t_name4=(TextView)view.findViewById(R.id.t_name4);
    	t_org1=(TextView)view.findViewById(R.id.t_orgprice1);
    	t_org2=(TextView)view.findViewById(R.id.t_orgprice2);
    	t_org3=(TextView)view.findViewById(R.id.t_orgprice3);
    	t_org4=(TextView)view.findViewById(R.id.t_orgprice4);
    	t_price1=(TextView)view.findViewById(R.id.t_price1);
    	t_price2=(TextView)view.findViewById(R.id.t_price2);
    	t_price3=(TextView)view.findViewById(R.id.t_price3);
    	t_price4=(TextView)view.findViewById(R.id.t_price4);
    	t_org1.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG); 
    	t_org2.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
    	t_org3.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG); 
    	t_org4.getPaint().setFlags(Paint. STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
    }
	public void setIconNames(String[] name){
		if(name==null){
			Log.e(Tag,"setIconNames name==null！！！！");
		}
		if(btn_coffee1==null){
			Log.e(Tag,"setIconNames btn_coffee1==null！！！！");
		}

		try {
			if (name.length > 0)
				t_name1.setText(name[0]);
			if (name.length > 1)
				t_name2.setText(name[1]);
			if (name.length > 2)
				t_name3.setText(name[2]);
			if (name.length > 3)
				t_name4.setText(name[3]);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void setIconOrgPrice(String[] name){
		if(name==null){
			Log.e(Tag,"setIconOrgPrice name==null！！！！");
		}
		if(btn_coffee1==null){
			Log.e(Tag,"setIconOrgPrice btn_coffee1==null！！！！");
		}

		try {
			if (name.length > 0)
				t_org1.setText(name[0]);
			if (name.length > 1)
				t_org2.setText(name[1]);
			if (name.length > 2)
				t_org3.setText(name[2]);
			if (name.length > 3)
				t_org4.setText(name[3]);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void setIconPrice(String[] name){
		if(name==null){
			Log.e(Tag,"setIconPrice name==null！！！！");
		}
		if(btn_coffee1==null){
			Log.e(Tag,"setIconPrice btn_coffee1==null！！！！");
		}

		try {
			if (name.length > 0)
				t_price1.setText(name[0]);
			if (name.length > 1)
				t_price2.setText(name[1]);
			if (name.length > 2)
				t_price3.setText(name[2]);
			if (name.length > 3)
				t_price4.setText(name[3]);
		}catch(Exception e){
			e.printStackTrace();
		}
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
