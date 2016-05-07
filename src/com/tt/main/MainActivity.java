package com.tt.main;

import android.app.Activity;
import android.os.Bundle;

import com.example.coffemachinev3.R;
import com.tt.util.LogCatHelper;

public class MainActivity extends Activity {
	MassageFragment massageFrag;
	MainFragment coffeeFrag;
	VideoFragment.CallBack videocallback;
//	CoffeeFragment.CallBack coffeecallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogCatHelper.getInstance(this).start();
        if (savedInstanceState == null) {

            massageFrag=new MassageFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.frag_msg,massageFrag) .commitAllowingStateLoss(); 
            coffeeFrag=new MainFragment();
//            coffeecallback=new CallBack(){
//
//				@Override
//				public void updateMsg(String msg) {
//					massageFrag.setMsg(msg);
//				}
//
//            };
          //  coffeeFrag.setCallBack(coffeecallback);
            getFragmentManager().beginTransaction()
            .add(R.id.frag_work, coffeeFrag) .commitAllowingStateLoss();


        	VideoFragment videoFragment=new VideoFragment();
            videocallback=new VideoFragment.CallBack() {
				
				@Override
				public void logoClicked() {
					coffeeFrag.enterDevMode();
				}
			};
            	
            getFragmentManager().beginTransaction()
            .add(R.id.frag_video, videoFragment).commit();

         	maintainFrag=new MaintainFragment();
            getFragmentManager().beginTransaction()
            .add(R.id.container, maintainFrag).commit();
            
            videoFragment.setCallBack(videocallback);
         	//coffeeFrag.setCallBack(maintainFrag.getMainCallBack());
        	//maintainFrag.setSelfBack(coffeeFrag.getDevCallBack());	
        }
    }

    MaintainFragment maintainFrag;
    void createDevFragment(){
   
    }
    
    
    
    
    
    
	@Override
	protected void onDestroy() {
		 LogCatHelper.getInstance(this).stop();
		super.onDestroy();
	}





}
