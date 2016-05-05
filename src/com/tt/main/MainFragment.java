package com.tt.main;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.coffemachinev3.R;
import com.tt.main.CoffeeFragmentPage1.CheckedCallBack;
import com.tt.util.SharePreferenceUtil;
import com.tt.util.ToastShow;
import com.tt.view.GuideFragmentAdapter;
import com.tt.view.MainViewPager;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

/*
 * ������Fragment
 */
public class MainFragment extends Fragment {
	private GuideFragmentAdapter mAdapter;
	private MainViewPager mPager;
	CoffeeFragmentPage1 page1 ;
	CoffeeFragmentPage2 page2; 
	ImageView btn_l,btn_r;
	PageIndicator mIndicator;
	ToastShow myToast;
	Context context;
	
	SugarDialog sugarDialog;
//	private PageIndicator mIndicator;
	public static RelativeLayout mainbg;
	CallBack back;
	public interface CallBack{
		void onCallback();
	}
	public CallBack getBack() {
		return back;
	}

	public void setBack(CallBack back) {
		this.back = back;
	}

	public static MainFragment newInstance() {
		MainFragment fragment = new MainFragment();
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_coffee_cantainer, container, false);
		context=getActivity();
		initView(view);

		return view;
	}
	
	
	void initView(View view){
		myToast= new ToastShow(context);
        ArrowListener arrowListener=new ArrowListener();
        btn_l=(ImageView)view.findViewById(R.id.btn_l);
        btn_r=(ImageView)view.findViewById(R.id.btn_r);
        btn_l.setOnClickListener(arrowListener);
        btn_r.setOnClickListener(arrowListener);
		
		mAdapter = new GuideFragmentAdapter(getFragmentManager());
		mAdapter.setFraArrayList(initFragments());
		mPager = (MainViewPager) view.findViewById(R.id.viewPaper);
		mPager.setAdapter(mAdapter);
        mIndicator = (CirclePageIndicator)view.findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        setArrow(0);
        mIndicator.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				//myToast.toastShow("cur page="+arg0);
				setArrow(arg0);
				page1.setCoffeeIconRadio(0);
				page2.setCoffeeIconRadio(0);
				
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
        
        
        

        
	}
	
	
	void setArrow(int page){
		if(page==0){
			btn_l.setVisibility(View.GONE);
			btn_r.setVisibility(View.VISIBLE);
		}else{
			btn_l.setVisibility(View.VISIBLE);
			btn_r.setVisibility(View.GONE);
		}
	}
	
	class ArrowListener	implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.btn_l:
						mPager.arrowScroll(1);
					break;
				case R.id.btn_r:
					mPager.arrowScroll(2);
					break;
			}
			
		}
		
	}
	
	
	@Override
	public void onStop() {
		super.onStop();
	}
	@Override
	public void onDestroy() {
		back.onCallback();
		super.onDestroy();
	}
	private ArrayList<Fragment> initFragments() {
		ArrayList<Fragment> fragments = new ArrayList<Fragment>();
//		final MainLeftFragment leftFragment = MainLeftFragment.newInstance();
//		leftFragment.setClickCallBack(new ClickCallBack() {
//			
//			@Override
//			public void onitemClick(int res) {
//				leftFragment.dismis();
//				mainbg.setBackgroundResource(res);
//			}
//		});
		
		
		 page1 = CoffeeFragmentPage1.newInstance();
		 page2 = CoffeeFragmentPage2.newInstance();
		 page1.setCheckedCallBack(new CheckedCallBack() {
			
			@Override
			public void onCallback(int id) {
				sugarDialog=new SugarDialog(context,id);
				sugarDialog.show();
			}
		});
//		fragments.add(leftFragment);
		fragments.add(page1);
		fragments.add(page2);
		return fragments;

	}
}
