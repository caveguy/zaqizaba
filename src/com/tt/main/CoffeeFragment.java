package com.tt.main;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android_serialport_api.DeliveryProtocol;

import com.example.coffemachinev2.R;
import com.tt.util.RadioGroupV2;
import com.tt.util.RadioGroupV2.OnCheckedChangeListener;

public class CoffeeFragment extends Fragment implements OnClickListener ,OnCheckedChangeListener{
	EditText et_water,et_powder;
	Button btn_start,btn_hand,btn_dropCup,btn_status;
	TextView t_status;
	DeliveryProtocol deliveryController=null;
	RadioGroupV2 radioGroup=null;
	int oldCheckedId=0;
	
	byte status;
	Handler myHandler;
	private final String Tag="testFrag";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_coffee, container, false);
        initView(rootView);
        myHandler =new Handler();

        
        return rootView;
    }

    void initView(View view){
    	radioGroup=(RadioGroupV2)view.findViewById(R.id.radio_group);
    	radioGroup.setOnCheckedChangeListener(this);
    	
    }
    
    

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_push:
			Log.e(Tag,"btn_push!!");
			int leftPowder=Integer.parseInt(et_powder.getText().toString());
			int leftWater=Integer.parseInt(et_water.getText().toString());
			deliveryController.cmd_pushLeftPowder(leftPowder, leftWater);
			break;
		case R.id.btn_hand:
			Log.e(Tag,"btn_hand!!");
			deliveryController.cmd_handShake();
			break;
		case R.id.btn_dropCup:
			Log.e(Tag,"btn_dropCup!!");
			deliveryController.cmd_dropCup();
			break;
		case R.id.btn_status:
			deliveryController.cmd_readBusy();
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroupV2 radioGroupV2, int checkedId) {
		if(oldCheckedId!=checkedId){
			switch(checkedId){
				case R.id.radio_1:
					Log.e(Tag,"radio_1 clicked!");
					break;
				case R.id.radio_2:
	
					break;
				case R.id.radio_3:

					break;
				case R.id.radio_4:
					
					break;
				case R.id.radio_5:
					
					break;
				case R.id.radio_6:
					
					break;
			}
		}
		
		oldCheckedId=checkedId;
	}
}

