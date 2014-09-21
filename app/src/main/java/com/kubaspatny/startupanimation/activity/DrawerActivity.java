package com.kubaspatny.startupanimation.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;

import com.kubaspatny.startupanimation.R;
import com.kubaspatny.startupanimation.fragment.LatestMessagesFragment;
import com.kubaspatny.startupanimation.fragment.NavigationDrawerFragment;
import com.kubaspatny.startupanimation.fragment.PagerFragment;
import com.kubaspatny.startupanimation.fragment.PlaceholderFragment;
import com.kubaspatny.startupanimation.fragment.SendMessageFragment;

public class DrawerActivity extends FragmentActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private int mFragmentId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        getActionBar().setIcon(R.drawable.ic_menu);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

//        if (!mNavigationDrawerFragment.isDrawerOpen()) {
//            // Only show items in the action bar relevant to this screen
//            // if the drawer is not showing. Otherwise, let the drawer
//            // decide what to show in the action bar.
//
//
//            int menu_res;
//
//            switch(mFragmentId){
//                case 1:
//                    menu_res = R.menu.menu_fragment_1;
//                    break;
//                case 2:
//                    menu_res = R.menu.menu_fragment_2;
//                    break;
//                case 3:
//                    menu_res = R.menu.menu_fragment_3;
//                    break;
//                default:
//                    menu_res = R.menu.main;
//            }
//
//            getMenuInflater().inflate(menu_res, menu);
//
//
//            restoreActionBar();
//            return true;
//        }
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ---------------------------------------------------------------------------------------------

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        Fragment newFragment;

        switch (position){
            case 0:
                newFragment = PagerFragment.newInstance();
                break;
            default:
                newFragment = PlaceholderFragment.newInstance(position + 1);
        }

        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, newFragment)
                .commit();
    }

    public void onSectionAttached(int number) {
        mFragmentId = number;

        switch (number) {
            case 1:
                mTitle = getString(R.string.section_1);
                break;
            case 2:
                mTitle = getString(R.string.section_2);
                break;
            case 3:
                mTitle = getString(R.string.section_3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

}
