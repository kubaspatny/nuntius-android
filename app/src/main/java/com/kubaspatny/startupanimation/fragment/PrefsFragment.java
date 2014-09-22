package com.kubaspatny.startupanimation.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.kubaspatny.startupanimation.R;
import com.kubaspatny.startupanimation.activity.DrawerActivity;

/**
 * Created by Kuba on 21/9/2014.
 */
public class PrefsFragment extends Fragment {

    public static final String PREFS_NAME = "NuntiusPreferences";
    public static final String SHOW_NOTIFICATIONS = "show_notifications";

    public PrefsFragment(){
    }

    public static PrefsFragment newInstance(){
        return new PrefsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_preferences, container, false);

        CheckBox checkBox = (CheckBox) rootView.findViewById(R.id.preference_notification_checkbox);

        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
        checkBox.setChecked(settings.getBoolean(SHOW_NOTIFICATIONS, true));

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(SHOW_NOTIFICATIONS, b);
                editor.commit();

            }

        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((DrawerActivity) activity).onSectionAttached(2); // 2 == SETTINGS
    }

}
