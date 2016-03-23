package com.tt.main;

import com.example.coffemachinev2.R;
import com.loopj.android.http.MySSLSocketFactory;

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
import android_serialport_api.DeliveryProtocol.CallBack;

public class ProtocolTestFragment extends Fragment implements OnClickListener {
	EditText et_water,et_powder;
	Button btn_start,btn_hand,btn_dropCup,btn_status;
	TextView t_status;
	DeliveryProtocol deliveryController=null;
	byte status;
	Handler myHandler;
	private final String Tag="testFrag";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test, container, false);
        myHandler =new Handler();
        deliveryController=new DeliveryProtocol(getActivity());
        deliveryController.setCallBack(new CallBack(){



			@Override
			public void cupDroped() {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void cupStuck() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void noCupDrop() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void dropCupTimeOut() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void hasDirtyCup() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void powderDroped() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void sendTimeOut() {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void dealFinish() {
				// TODO Auto-generated method stub
				
			}
        	
        });
        et_water=(EditText)rootView.findViewById(R.id.et_leftWater);
        et_powder=(EditText)rootView.findViewById(R.id.et_leftPowder);
        btn_start=(Button)rootView.findViewById(R.id.btn_push);
        btn_hand=(Button)rootView.findViewById(R.id.btn_hand);
        btn_dropCup=(Button)rootView.findViewById(R.id.btn_dropCup);
        btn_status=(Button)rootView.findViewById(R.id.btn_status);
        t_status=(TextView)rootView.findViewById(R.id.t_state);
        btn_start.setOnClickListener(this);
        btn_hand.setOnClickListener(this);
        btn_dropCup.setOnClickListener(this);
        btn_status.setOnClickListener(this);
        
        return rootView;
    }


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_push:
			Log.e(Tag,"btn_push!!");
			int leftPowder=Integer.parseInt(et_powder.getText().toString());
			int leftWater=Integer.parseInt(et_water.getText().toString());
			deliveryController.cmd_pushLeftPowder(leftPowder,50, leftWater);
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
			deliveryController.cmd_readState();
			break;
		}
	}
}

