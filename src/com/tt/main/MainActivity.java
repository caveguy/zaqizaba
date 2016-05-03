package com.tt.main;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.coffemachinev2.R;
import com.tt.main.CoffeeFragment.CallBack;
import com.tt.util.LogCatHelper;

public class MainActivity extends Activity {
	MassageFragment massageFrag;
	CoffeeFragment coffeeFrag;
	VideoFragment.CallBack videocallback;
	CoffeeFragment.CallBack coffeecallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogCatHelper.getInstance(this).start();
        if (savedInstanceState == null) {

            massageFrag=new MassageFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.frag_msg,massageFrag) .commitAllowingStateLoss(); 
//            coffeeFrag=new CoffeeFragment();
//            coffeecallback=new CallBack(){
//
//				@Override
//				public void updateMsg(String msg) {
//					massageFrag.setMsg(msg);
//				}
//
//            };
//            coffeeFrag.setCallBack(coffeecallback);
//            getFragmentManager().beginTransaction()
//            .add(R.id.frag_work, coffeeFrag) .commitAllowingStateLoss();


        	VideoFragment videoFragment=new VideoFragment();
            videocallback=new VideoFragment.CallBack() {
				
				@Override
				public void logoClicked() {
				//	coffeeFrag.enterDevMode();
				}
			};
            videoFragment.setCallBack(videocallback);	
            getFragmentManager().beginTransaction()
            .add(R.id.frag_video, videoFragment).commit();

            
        }
    }

	@Override
	protected void onDestroy() {
		 LogCatHelper.getInstance(this).stop();
		super.onDestroy();
	}





}
