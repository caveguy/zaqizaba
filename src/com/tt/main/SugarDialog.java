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
import android.widget.LinearLayout;

import com.example.coffemachinev3.R;


public class SugarDialog implements OnClickListener{
	/**默认的对话框视图*/
	public static int DIALOG_UI = R.layout.dialog_sweetness ;
	
	/**默认的对话框占主屏幕多宽度的比例*/
//	public static float WIDTH_SCALE = 0.8F;
	
	/**OK按钮被点击*/
	public static final int OK = 0;
	/**取消按钮点击*/
	public static final int CANNEL = 1;
	private int choose;
	protected Context context;
	protected Dialog dialog;
	protected Button okBtn;
	protected Button cannelBtn;
	

	protected String title;
	protected String message;
	int place=0;
	CheckBox radio1,radio2,radio3,radio4;
	protected ConfirmListener listener;
	
	public SugarDialog(Context context){
		this.context = context;
	}
	public SugarDialog(Context context, int place){
		this(context);
		this.place=place;
//		this.title = t;
//		this.message = m;
	}
	
	public void setTitle(String t){
		this.title = t;
	}
	public void setMessage(String m){
		this.message = m;
	}
	public void setConfirmListener(ConfirmListener listener){
		this.listener = listener;
	}
	
	protected void createDialog(){
		View view = View.inflate(context, getMainXML(), null);
		initView( view);

	}
	

    void initView(View view){

    	radio1=(CheckBox)view.findViewById(R.id.radio_1);
    	radio2=(CheckBox)view.findViewById(R.id.radio_2);
    	radio3=(CheckBox)view.findViewById(R.id.radio_3);
    	radio4=(CheckBox)view.findViewById(R.id.radio_4);

    	radio1.setOnClickListener(this);
    	radio2.setOnClickListener(this);
    	radio3.setOnClickListener(this);
    	radio4.setOnClickListener(this);
    	radio1.setChecked(true);

		
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
		
//		LinearLayout ll = (LinearLayout)view.findViewById(R.id.dialog_live);
//		View liveView = getLiveView();
//		if(liveView != null){
//			ll.addView(liveView);
//		}
		
		win.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		win.setContentView(view);
		
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
		if(isButtonShow()){
			okBtn = (Button)view.findViewById(R.id.dialog_ok);
			cannelBtn = (Button)view.findViewById(R.id.dialog_cannel);
			okBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					dialog=null;
					afterClickOK();
				}
			});
			
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
	public void closeDialog(){
		dialog.dismiss();
		dialog=null;
	}
	
	public boolean isAlive(){
		return dialog==null?false:true;
	}
	/**
	 * @方法名称 :afterClickOK
	 * @功能描述 :确认按钮点击后触发，子类可以重写这个方法达到不同的效果
	 * @return :void
	 */
	public void afterClickOK(){
		if(listener != null)
			listener.onOKClick(place, choose);
	}
	public void afterClickCancel(){
		if(listener != null)
			listener.onCancelClick(place);
	}

	
	public interface ConfirmListener{		
		/**
		 * @方法名称 :onConfirmClick
		 * @功能描述 :当confirm对话框中的按钮被点击时
		 * @param position
		 * @return :void
		 */
		public void onOKClick(int position, int choose);
		public void onCancelClick(int position);
	}


	void setIconRadio(int id){
		switch(id){
			case R.id.radio_1:
				choose=0;
				radio2.setChecked(false);
				radio3.setChecked(false);
				radio4.setChecked(false);
				break;
			case R.id.radio_2:
				choose=1;
				radio1.setChecked(false);
				radio3.setChecked(false);
				radio4.setChecked(false);
				break;
			case R.id.radio_3:
				choose=2;
				radio1.setChecked(false);
				radio2.setChecked(false);
				radio4.setChecked(false);
				break;
			case R.id.radio_4:
				choose=3;
				radio1.setChecked(false);
				radio2.setChecked(false);
				radio3.setChecked(false);
				break;
			case 0:
				radio1.setChecked(false);
				radio2.setChecked(false);
				radio3.setChecked(false);
				radio4.setChecked(false);	
				break;
		
		}
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
		CheckBox box=(CheckBox)v;
		if(!box.isChecked()){
			box.setChecked(true);
		}else{
			setIconRadio(id);
		}
		
	}
}