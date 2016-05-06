package com.tt.main;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.coffemachinev3.R;


public class MakingStateDialog implements OnClickListener{
	/**默认的对话框视图*/
	public static int DIALOG_UI = R.layout.dialog_make_state ;
	


	/**取消按钮点击*/
	public static final int CANNEL = 1;
	protected Context context;
	protected Dialog dialog;
	
	protected Button cannelBtn;
	private String state;
	private TextView t_state;
	protected int id;
	int place=0;


	
	public MakingStateDialog(Context context){
		this.context = context;
	}
	public MakingStateDialog(Context context, int place,String str_state){
		this(context);
		this.place=place;
		state=str_state;
	}
	


	
	protected void createDialog(){
		View view = View.inflate(context, getMainXML(), null);
		initView( view);
		dialog = new Dialog(context);
		dialog.show();
		Window win = dialog.getWindow();
		//将dialog的背景透明化
		win.setBackgroundDrawable(new ColorDrawable(0));
		win.setGravity(getGravity());
		LinearLayout layout_bg=(LinearLayout)view.findViewById(R.id.layout_bg);
		LinearLayout.LayoutParams params= (android.widget.LinearLayout.LayoutParams) layout_bg.getLayoutParams();
		int left=0;
		switch(place){
			case 0:
				default:
				left=context.getResources().getDimensionPixelSize(R.dimen.dialog1_leftMargin);
				layout_bg.setBackground(context.getResources().getDrawable(R.drawable.bg_notice_l));
				break;
			case 1:
				left=context.getResources().getDimensionPixelSize(R.dimen.dialog2_leftMargin);
				layout_bg.setBackground(context.getResources().getDrawable(R.drawable.bg_notice_r));
				break;
			case 2:
				left=context.getResources().getDimensionPixelSize(R.dimen.dialog3_leftMargin);
				layout_bg.setBackground(context.getResources().getDrawable(R.drawable.bg_notice_l));
				break;
			case 3:
				left=context.getResources().getDimensionPixelSize(R.dimen.dialog4_leftMargin);
				layout_bg.setBackground(context.getResources().getDrawable(R.drawable.bg_notice_r));
				break;
		}
		
		params.setMargins(left, params.topMargin, params.rightMargin, params.bottomMargin);
		layout_bg.setLayoutParams(params);
		win.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		win.setContentView(view);
	}
	



    void initView(View view){
    	t_state=(TextView)view.findViewById(R.id.t_state);
//    	String state=;
   	t_state.setText(state);
		
//		initButton(view);
    }
	public void setState(String str){
		this.state=str;
		t_state.setText(state);
	}
	
	public void show(){
		if(dialog == null)
			createDialog();
		else
			dialog.show();
	}

	public int getMainXML(){
		return DIALOG_UI;
	}
	
	public int getGravity(){
		return Gravity.CENTER;
	}
	

	@Override
	public void onClick(View v) {
		int id =v.getId();

	}
	public void closeDialog(){
		dialog.dismiss();
		dialog=null;
	}
	
	public boolean isAlive(){
		return dialog==null?false:true;
	}
}