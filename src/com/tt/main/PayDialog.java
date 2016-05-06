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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.coffemachinev3.R;


public class PayDialog implements OnClickListener{
	/**默认的对话框视图*/
	public static int DIALOG_UI = R.layout.dialog_pay ;
	


	/**取消按钮点击*/
	public static final int CANNEL = 1;
	protected Context context;
	protected Dialog dialog;
	
	protected Button cannelBtn;
	private Button btn_ic,btn_num;
	private ImageView img_zfb,img_weixin;
	private TextView t_state;
	protected int id;
	int sweetness=0;
	int place=0;
	CheckBox radio1,radio2,radio3,radio4;
	protected PayListener listener;
	
	public PayDialog(Context context){
		this.context = context;
	}
	public PayDialog(Context context, int place,int sweet){
		this(context);
		this.place=place;
		sweetness=sweet;
//		this.title = t;
//		this.message = m;
	}
	

	public void setListener(PayListener listener){
		this.listener = listener;
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
	
	String getCoffee(){
		switch(place){
		
		case 0:
			return context.getString(R.string.espresso);
		case 1:
			return context.getString(R.string.american);
		case 2:
			return context.getString(R.string.cappuccino);
		case 3:
			return context.getString(R.string.latte);
		case 4:
			return context.getString(R.string.water);

		}
		return "";
	}
	String getSweet(){
		switch(sweetness){
		
		case 0:
			return context.getString(R.string.no);
		case 1:
			return context.getString(R.string.low);
		case 2:
			return context.getString(R.string.medium);
		case 3:
			return context.getString(R.string.high);

			
		}
		return "";
	}

    void initView(View view){

    	img_zfb=(ImageView)view.findViewById(R.id.img_qr_zfb);
    	img_weixin=(ImageView)view.findViewById(R.id.img_qr_weixin);
    	t_state=(TextView)view.findViewById(R.id.t_state);
    	String state="您已选中："+getCoffee()+",甜度为："+getSweet()+",请尽快完成支付";
    	t_state.setText(state);
		
		initButton(view);
    }
	
	
	public void show(){
		if(dialog == null)
			createDialog();
		else
			dialog.show();
	}
	
	/**
	 * @方法名称 :initButton
	 * @功能描述 :初始化按钮
	 * 	
	 * @param view
	 * @return :void
	 */
	protected void initButton(View view){
		cannelBtn = (Button)view.findViewById(R.id.btn_cancel);
		if(isButtonShow()){

			
			cannelBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					dialog=null;
					afterClickCancel();
				}
			});
		}else{
			View v = view.findViewById(R.id.dialog_button_group);
			((LinearLayout)view).removeView(v);
		}
	}
	
	/**
	 * @方法名称 :isButtonShow
	 * @功能描述 :如果子类不需要显示按钮，可以重写这个方法。
	 * @return
	 * @return :boolean
	 */
	protected boolean isButtonShow(){
		return true;
	}
	
	/**
	 * @方法名称 :getMainXML
	 * @功能描述 :获得主视图id
	 * @return
	 * @return :int
	 */
	public int getMainXML(){
		return DIALOG_UI;
	}
	
	public int getGravity(){
		return Gravity.CENTER;
	}
	


	public void afterClickCancel(){
		if(listener != null)
			listener.onCancelClick();
	}

	
	public interface PayListener{		
		/**
		 * @方法名称 :onConfirmClick
		 * @功能描述 :当confirm对话框中的按钮被点击时
		 * @param position
		 * @return :void
		 */
		public void onCancelClick();
		
		public void onPay(boolean success);
	}



//	@Override
//	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//		int id =buttonView.getId();
//		if(isChecked){
//			setIconRadio(id);
//		}else{
//			buttonView.setChecked(true);
//		}
//	}
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