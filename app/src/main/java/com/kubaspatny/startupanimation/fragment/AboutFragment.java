package com.kubaspatny.startupanimation.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kubaspatny.startupanimation.R;
import com.kubaspatny.startupanimation.activity.DrawerActivity;

/**
 * Created by Kuba on 22/9/2014.
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {
    }

    public static AboutFragment newInstance(){
        return new AboutFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((DrawerActivity) activity).onSectionAttached(3); // 3 == ABOUT
    }

}
