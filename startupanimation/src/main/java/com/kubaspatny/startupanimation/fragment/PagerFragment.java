package com.kubaspatny.startupanimation.fragment;



import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.kubaspatny.startupanimation.R;
import com.kubaspatny.startupanimation.activity.DrawerActivity;
import com.kubaspatny.startupanimation.adapter.MessagesPagerAdapter;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * Use the {@link PagerFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class PagerFragment extends Fragment {

    public PagerFragment() {
    }

    public static PagerFragment newInstance(){
        return new PagerFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View result = inflater.inflate(R.layout.fragment_pager, container, false);
        ViewPager pager = (ViewPager) result.findViewById(R.id.pager);
        pager.setAdapter(buildAdapter());

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) result.findViewById(R.id.tabs);
        tabs.setShouldExpand(true);
        tabs.setIndicatorColor(getResources().getColor(R.color.nuntius_main));
        tabs.setUnderlineColor(getResources().getColor(R.color.nuntius_main));
        tabs.setViewPager(pager);

        return(result);
    }

    private PagerAdapter buildAdapter() {
        return(new MessagesPagerAdapter(getActivity(), getChildFragmentManager()));
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((DrawerActivity) activity).onSectionAttached(3);

    }


}
