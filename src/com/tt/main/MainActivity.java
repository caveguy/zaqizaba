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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogCatHelper.getInstance(this).start();
        if (savedInstanceState == null) {
        	
            getFragmentManager().beginTransaction()
            .add(R.id.frag_video, new VideoFragment()).commit();
            
            massageFrag=new MassageFragment();
            getFragmentManager().beginTransaction()
                    .add(R.id.frag_msg,massageFrag) .commit(); 
            coffeeFrag=new CoffeeFragment();
            getFragmentManager().beginTransaction()
            .add(R.id.frag_work, coffeeFrag) .commit();
            coffeeFrag.setCallBack(new CallBack(){

				@Override
				public void updateMsg(String msg) {
					massageFrag.setMsg(msg);
				}

            });

        }
    }

	@Override
	protected void onDestroy() {
		 LogCatHelper.getInstance(this).stop();
		super.onDestroy();
	}





}
