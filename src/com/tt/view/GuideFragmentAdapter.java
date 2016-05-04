package com.tt.view;

import java.util.ArrayList;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;




public class GuideFragmentAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> fraArrayList=new ArrayList<Fragment>();

	public GuideFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fraArrayList.get(position);
    }
    
    public ArrayList<Fragment> getFraArrayList() {
		return fraArrayList;
	}

	public void setFraArrayList(ArrayList<Fragment> fraArrayList) {
		this.fraArrayList = fraArrayList;
	}

	@Override
    public int getCount() {
        return fraArrayList.size();
    }

}