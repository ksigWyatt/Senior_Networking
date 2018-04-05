package com.sn.stepcounter.stepcounter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.sn.stepcounter.stepcounter.Fragment_heartRate;
import com.sn.stepcounter.stepcounter.Fragment_steps;

/**
 * Created by LoLJay on 4/4/2018.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;
    public ViewPagerAdapter(Context context, FragmentManager fragmentManager){
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(position ==0){
            return  new Fragment_steps();
        }else if(position ==1){
            return  new Fragment_heartRate();
        }else if(position ==2){
            return  new Fragment_history();
        }else{
            return  null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Steps";
            case 1:
                return "HeartRate";
            case 2:
                return "History";

            default:
                return null;
        }
    }
}
