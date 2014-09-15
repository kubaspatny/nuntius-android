package com.kubaspatny.startupanimation.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kubaspatny.startupanimation.R;
import com.kubaspatny.startupanimation.activity.DrawerActivity;

/**
 * Created by Kuba on 15/9/2014.
 */
public class PlaceholderFragment extends Fragment{

    private static final String ARG_SECTION_NUMBER = "section_number";

    public static PlaceholderFragment newInstance(int sectionNumber) {

        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_placeholder, container, false);

        switch(getArguments().getInt(ARG_SECTION_NUMBER)){
            case 1:
                rootView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
                ((TextView)rootView.findViewById(R.id.section_label)).setText(getString(R.string.section_1));
                break;
            case 2:
                rootView.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                ((TextView)rootView.findViewById(R.id.section_label)).setText(getString(R.string.section_2));
                break;
            case 3:
                rootView.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
                ((TextView)rootView.findViewById(R.id.section_label)).setText(getString(R.string.section_3));
                break;
            default:
                rootView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
                ((TextView)rootView.findViewById(R.id.section_label)).setText("Something's wrong!");
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((DrawerActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));

    }

}
