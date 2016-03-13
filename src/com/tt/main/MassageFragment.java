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

public class MassageFragment extends Fragment {

	TextView t_time,t_week,t_date;
	TextView t_msg1,t_msg2,t_msg3;

	private final String Tag="MsgFrag";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_msg, container, false);

        t_time=(TextView)rootView.findViewById(R.id.t_time);
        t_week=(TextView)rootView.findViewById(R.id.t_week);
        t_date=(TextView)rootView.findViewById(R.id.t_date);
        t_msg1=(TextView)rootView.findViewById(R.id.t_msg1);
        t_msg2=(TextView)rootView.findViewById(R.id.t_msg2);
        t_msg3=(TextView)rootView.findViewById(R.id.t_msg3);

        
        return rootView;
    }

}

