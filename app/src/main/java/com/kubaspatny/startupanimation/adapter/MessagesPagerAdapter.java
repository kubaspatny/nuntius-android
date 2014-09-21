package com.kubaspatny.startupanimation.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.kubaspatny.startupanimation.fragment.LatestMessagesFragment;
import com.kubaspatny.startupanimation.fragment.PlaceholderFragment;
import com.kubaspatny.startupanimation.fragment.SendMessageFragment;

/**
 * Created by Kuba on 15/9/2014.
 */
public class MessagesPagerAdapter extends FragmentPagerAdapter {

    Context context = null;

    public MessagesPagerAdapter(Context context, FragmentManager mgr) {
        super(mgr);
        this.context = context;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Fragment getItem(int position) {

        switch(position){
            case 0:
                return LatestMessagesFragment.newInstance();
            case 1:
                return SendMessageFragment.newInstance();
            default:
                return PlaceholderFragment.newInstance(-1);
        }

    }

    @Override
    public String getPageTitle(int position) {

        switch(position){
            case 0:
                return "Latest Messages";
            case 1:
                return "Send Message";
            default:
                return "Error";
        }
    }

}