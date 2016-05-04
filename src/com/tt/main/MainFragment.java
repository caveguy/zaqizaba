package com.tt.main;

import java.util.ArrayList;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnDragListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.coffemachinev3.R;
import com.tt.util.SharePreferenceUtil;
import com.tt.view.GuideFragmentAdapter;
import com.tt.view.MainViewPager;

/*
 * ������Fragment
 */
public class MainFragment extends Fragment {
	private GuideFragmentAdapter mAdapter;
	private MainViewPager mPager;
	CoffeeFragmentPage1 page1 ;
	CoffeeFragmentPage2 page2; 
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
		mAdapter = new GuideFragmentAdapter(getFragmentManager());
		mAdapter.setFraArrayList(initFragments());
		mPager = (MainViewPager) view.findViewById(R.id.viewPaper);
		mPager.setAdapter(mAdapter);
		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
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

//		mIndicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
//		mIndicator.setViewPager(mPager, 0);
//		mIndicator.setOnPageChangeListener(new OnPageChangeListener() {
//			
//			@Override
//			public void onPageSelected(int arg0) {
//				MainActivity.timeCount=0;
//				
//			}
//			
//			@Override
//			public void onPageScrolled(int arg0, float arg1, int arg2) {
//				MainActivity.timeCount=0;
//				
//			}
//			
//			@Override
//			public void onPageScrollStateChanged(int arg0) {
//				MainActivity.timeCount=0;
//				
//			}
//		});
		return view;
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
		 page1.set
//		fragments.add(leftFragment);
		fragments.add(page1);
		fragments.add(page2);
		return fragments;

	}
}
