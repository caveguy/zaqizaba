package com.tt.main;

import java.util.HashMap;
import java.util.Map;

import com.example.coffemachinev3.R;
import com.tt.util.Stocks;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class StockFragment extends Fragment implements OnClickListener {
	Button btn_cancel,btn_ok,btn_reset;
	EditText[] edits=new EditText[18];
	Context context=null;
	private int MaxNum=18;
	//Map<Integer,EditText > edits=new HashMap<Integer,EditText>();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("StockFragment","onCreateView");
		View view = inflater.inflate(R.layout.fragment_stock, container, false);
		context=getActivity();
		initView(view);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	void initView(View view){
	//	showDialog("test hahaha");
		int i=0;
		edits[i++]=(EditText)view.findViewById(R.id.edt_1_1);
		edits[i++]=(EditText)view.findViewById(R.id.edt_1_2);
		edits[i++]=(EditText)view.findViewById(R.id.edt_1_3);
		edits[i++]=(EditText)view.findViewById(R.id.edt_2_1);
		edits[i++]=(EditText)view.findViewById(R.id.edt_2_2);
		edits[i++]=(EditText)view.findViewById(R.id.edt_2_3);
		edits[i++]=(EditText)view.findViewById(R.id.edt_3_1);
		edits[i++]=(EditText)view.findViewById(R.id.edt_3_2);
		edits[i++]=(EditText)view.findViewById(R.id.edt_3_3);
		edits[i++]=(EditText)view.findViewById(R.id.edt_4_1);
		edits[i++]=(EditText)view.findViewById(R.id.edt_4_2);
		edits[i++]=(EditText)view.findViewById(R.id.edt_4_3);
		edits[i++]=(EditText)view.findViewById(R.id.edt_5_1);
		edits[i++]=(EditText)view.findViewById(R.id.edt_5_2);
		edits[i++]=(EditText)view.findViewById(R.id.edt_5_3);
		edits[i++]=(EditText)view.findViewById(R.id.edt_6_1);
		edits[i++]=(EditText)view.findViewById(R.id.edt_6_2);
		edits[i++]=(EditText)view.findViewById(R.id.edt_6_3);
		for( i=0;i<MaxNum;i++){
			edits[i].addTextChangedListener(watcher);
		}
		
		btn_ok=(Button)view.findViewById(R.id.btn_ok);
		btn_cancel=(Button)view.findViewById(R.id.btn_cancel);
		btn_reset=(Button)view.findViewById(R.id.btn_reset);
		btn_ok.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		btn_reset.setOnClickListener(this);
	}
	TextWatcher watcher=new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			if(s.length()==0){
				showDialog(context.getString(R.string.hasEmpty));
			}
			
		}
	};
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_ok:
			break;
		case R.id.btn_cancel:
			break;
		case R.id.btn_reset:
			break;
			
		}
		
	}
	
	void showDialog(String msg){
        AlertDialog dialog;  
        AlertDialog.Builder builder = new AlertDialog.Builder(context);  
        builder.setTitle(context.getString(R.string.alert_title)).setIcon(android.R.drawable.stat_notify_error);  
        builder.setMessage(msg);  
        builder.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener(){  
            @Override 
            public void onClick(DialogInterface dialog, int which) {  
                // TODO Auto-generated method stub  
                  
            }                     
        });  
        dialog = builder.create();  
        dialog.show();  
      
	}
	void saveData(){
		String[] values = new String[MaxNum];
		for(int i=0;i<MaxNum;i++){
			values[i]=edits[i].getText().toString();
			if(values[i].length()==0){
				showDialog(context.getString(R.string.hasEmpty));
				return;
			}
		}
		
		Stocks.setIntValue(context, Stocks.Bean_min, new Integer(values[0]));
		Stocks.setIntValue(context, Stocks.Bean_max, new Integer(values[1]));
		Stocks.setIntValue(context, Stocks.Bean_cur, new Integer(values[2]));
		Stocks.setIntValue(context, Stocks.Water_min, new Integer(values[3]));
		Stocks.setIntValue(context, Stocks.Water_max, new Integer(values[4]));
		Stocks.setIntValue(context, Stocks.Water_cur, new Integer(values[5]));
		Stocks.setIntValue(context, Stocks.Cup_min, new Integer(values[6]));
		Stocks.setIntValue(context, Stocks.Cup_max, new Integer(values[7]));
		Stocks.setIntValue(context, Stocks.Cup_cur, new Integer(values[8]));
		Stocks.setIntValue(context, Stocks.Powder1_min, new Integer(values[9]));
		Stocks.setIntValue(context, Stocks.Powder1_max, new Integer(values[10]));
		Stocks.setIntValue(context, Stocks.Powder1_cur, new Integer(values[11]));
		Stocks.setIntValue(context, Stocks.Powder2_min, new Integer(values[12]));
		Stocks.setIntValue(context, Stocks.Powder2_max, new Integer(values[13]));
		Stocks.setIntValue(context, Stocks.Powder2_cur, new Integer(values[14]));
		Stocks.setIntValue(context, Stocks.Powder3_min, new Integer(values[15]));
		Stocks.setIntValue(context, Stocks.Powder3_max, new Integer(values[16]));
		Stocks.setIntValue(context, Stocks.Powder3_cur, new Integer(values[17]));	
		getFragmentManager().popBackStack();
	}
	void resetData(){
		
	}
	void cancel(){
		getFragmentManager().popBackStack();
	}
	
	
	public static StockFragment newInstance() {
		StockFragment fragment = new StockFragment();
		return fragment;
	}
}
