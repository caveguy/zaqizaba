package com.tt.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MainViewPager extends ViewPager {
	boolean onTouch=true;
	
	public boolean isOnTouch() {
		return onTouch;
	}
	public void setOnTouch(boolean onTouch) {
		this.onTouch = onTouch;
	}
	public MainViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		if(onTouch){
			return super.onInterceptTouchEvent(arg0);
		}else{
			return false;
			
		}
	}
	public boolean onTouchEvent(MotionEvent arg0) {
		if(onTouch){
			return super.onTouchEvent(arg0);
		}else{
			return false;
			
		}
	}

}