package com.tt.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.example.coffemachinev3.R;
import com.tt.util.LogCatHelper;

public class MainActivity extends Activity {
	MassageFragment massageFrag;
	MainFragment coffeeFrag;
	VideoFragment.CallBack videocallback;
	MainFragment.UpdateMsgCallBack  coffeecallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogCatHelper.getInstance(this).start();
        if (savedInstanceState == null) {

            massageFrag=new MassageFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.frag_msg,massageFrag) .commitAllowingStateLoss(); 
         	maintainFrag=new MaintainFragment();
            getFragmentManager().beginTransaction()
            .add(R.id.container, maintainFrag).commit();
            
            coffeeFrag=new MainFragment();
            getFragmentManager().beginTransaction()
            .add(R.id.frag_work, coffeeFrag) .commitAllowingStateLoss();
        	VideoFragment videoFragment=new VideoFragment();   	
            getFragmentManager().beginTransaction()
            .add(R.id.frag_video, videoFragment).commit();

            coffeecallback=new MainFragment.UpdateMsgCallBack(){

				@Override
				public void updateMsg(String msg) {
					massageFrag.setMsg(msg);
				}
            };
            videocallback=new VideoFragment.CallBack() {
				
				@Override
				public void logoClicked() {
				//	coffeeFrag.enterDevMode();
				}
			};
            coffeeFrag.setMsgCallBack(coffeecallback);
            videoFragment.setCallBack(videocallback);
         	//coffeeFrag.setCallBack(maintainFrag.getMainCallBack());
        	//maintainFrag.setSelfBack(coffeeFrag.getDevCallBack());	
        }
    }

    MaintainFragment maintainFrag;
    void createDevFragment(){
   
    }
    
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	 
        if (keyCode == KeyEvent.KEYCODE_BACK
                 && event.getRepeatCount() == 0) {
        	android.os.Process.killProcess(android.os.Process.myPid());
             return true;
         }
         return super.onKeyDown(keyCode, event);
     }
    
    
    
	@Override
	protected void onDestroy() {
		 LogCatHelper.getInstance(this).stop();
		super.onDestroy();
	}





}
