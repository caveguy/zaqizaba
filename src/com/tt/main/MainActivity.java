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
import com.tt.util.LogCatHelper;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogCatHelper.getInstance(this).start();
        if (savedInstanceState == null) {
        	
            getFragmentManager().beginTransaction()
            .add(R.id.frag_video, new VideoFragment()).commit();
            getFragmentManager().beginTransaction()
                    .add(R.id.frag_msg, new MassageFragment()) .commit();  
            getFragmentManager().beginTransaction()
            .add(R.id.frag_work, new CoffeeFragment()) .commit();
        }
    }

	@Override
	protected void onDestroy() {
		 LogCatHelper.getInstance(this).stop();
		super.onDestroy();
	}





}
