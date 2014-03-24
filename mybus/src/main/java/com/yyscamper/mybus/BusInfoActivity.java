package com.yyscamper.mybus;

import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;


public class BusInfoActivity extends Activity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    BusPair mCurrBusPair;
    Bus     mCurrBus;
    MenuItem mMenuBusStopTitle;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private void setBusTitle() {
        //setTitle(mCurrBus.getName() + "(" + mCurrBus.getStartStop() + ")");
        if (mCurrBus != null)
            setTitle(mCurrBus.getName());
        else
            setTitle("未能找到该公交信息");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_info);

        String busPairName = getIntent().getStringExtra("bus_pair_name");
        mCurrBusPair = BusManager.get(busPairName);
        if (mCurrBusPair != null) {
            mCurrBus = mCurrBusPair.getSelectedBus();
        }

        setBusTitle();

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager(), mCurrBus);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bus_info, menu);
        return true;
    }

    private void setBusStopTitle() {
        if (mCurrBus != null) {
            TextView tv = (TextView)mMenuBusStopTitle.getActionView().findViewById(R.id.textViewValue);
            //tv.setText( "(" + mCurrBus.getStartStop() + "-" + mCurrBus.getEndStop() + ")");

            if (mCurrBus.getStartStop() != null)
                tv.setText("(" + mCurrBus.getStartStop() + ")");
            else
                tv.setText("(未知的起点站)");
        }
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mMenuBusStopTitle = menu.findItem(R.id.bus_stop_title);
        setBusStopTitle();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_set_fav) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor edit = prefs.edit();
            edit.putString(SettingsActivity.KEY_SELECT_FAV_BUS, mCurrBus.getQueryKey());
            edit.commit();
            Toast.makeText(this, getString(R.string.add_fav_success) ,Toast.LENGTH_SHORT).show();
            return true;
        }
        else if (id == R.id.action_next_bus) {
            if (mCurrBusPair != null) {
                if (mCurrBus == mCurrBusPair.getBus1())
                    mCurrBus = mCurrBusPair.getBus2();
                else
                    mCurrBus = mCurrBusPair.getBus1();
            }
            mSectionsPagerAdapter.onNotifyBusChange(mCurrBus);
            setBusTitle();
            setBusStopTitle();
        }
        else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        Bus mCurrBus;
        BusTimeFragment mBusTimeFrag;
        BusStopFragment mBusStopFrag;

        public SectionsPagerAdapter(FragmentManager fm, Bus bus) {
            super(fm);
            mCurrBus = bus;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a BusStopFragment (defined as a static inner class below).
            if (position == 0) {
                mBusTimeFrag = BusTimeFragment.newInstance(position + 1, mCurrBus);
                return mBusTimeFrag;
            }
            else {
                mBusStopFrag = BusStopFragment.newInstance(position + 1, mCurrBus);
                return mBusStopFrag;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }

        public void onNotifyBusChange( Bus bus) {
            mCurrBus = bus;
            mBusTimeFrag.onNotifyBusChanged(bus);
            mBusStopFrag.onNotifyBusChanged(bus);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class BusStopFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static Bus mCurBus;
        private ListView mViewBusStopList;
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static BusStopFragment newInstance(int sectionNumber, Bus bus) {
            BusStopFragment fragment = new BusStopFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            mCurBus = bus;
            return fragment;
        }

        public BusStopFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_bus_stop_info, container, false);
            mViewBusStopList = (ListView)rootView.findViewById(R.id.listViewAllBusStops);
            refreshViewData();
            return rootView;
        }

        private void refreshViewData() {
            MyStringListItemAdapter adapter = new MyStringListItemAdapter(getActivity().getApplicationContext(),
                    mCurBus.getAllBusStops());
            mViewBusStopList.setAdapter(adapter);
        }

        public void onNotifyBusChanged(Bus bus) {
            mCurBus = bus;
            refreshViewData();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class BusTimeFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private ListView mViewBusTimeList;
        private static Bus mCurBus;
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static BusTimeFragment newInstance(int sectionNumber, Bus curBus) {
            BusTimeFragment fragment = new BusTimeFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            mCurBus = curBus;
            return fragment;
        }

        public BusTimeFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_bus_time_info, container, false);
            mViewBusTimeList = (ListView)rootView.findViewById(R.id.listViewAllBusTimes);
            refreshViewData();
            return rootView;
        }

        private void refreshViewData() {
            MyStringListItemAdapter adapter = new MyStringListItemAdapter(getActivity().getApplicationContext(),
                    convertTimeArray(mCurBus.getAllBusTime()));
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),
            //        android.R.layout.simple_selectable_list_item, convertTimeArray(mCurBus.getAllBusTime()));

            mViewBusTimeList.setAdapter(adapter);

            int ret = selectNextBusTime(mCurBus);
            int sel = (ret < 0) ? 0 : ret;

            adapter.setSelectedItem(sel);
            if (sel > 1) {
                mViewBusTimeList.setSelection(sel - 2);
            }
            else {
                mViewBusTimeList.setSelection(0);
            }
            mViewBusTimeList.smoothScrollToPosition(sel);

            SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(getActivity());
            boolean enablePrompt = setting.getBoolean(SettingsActivity.KEY_SELECT_BUS_EARLY_LATE_PROMPT, true);
            if (enablePrompt) {
                if (ret == -1) //too early
                    Toast.makeText(this.getActivity(), getString(R.string.msg_get_on_bus) ,Toast.LENGTH_SHORT).show();
                else if (ret == -2) {
                    Toast.makeText(this.getActivity(), getString(R.string.msg_miss_last_bus) ,Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void showMessageDialog(String message, String title) {
            AlertDialog.Builder diag = new AlertDialog.Builder(this.getActivity());
            diag.setTitle(title);
            diag.setMessage(message);
            diag.create();
            diag.show();
        }
        private String[] convertTimeArray(Time[] arr) {
            String[] result = new String[arr.length];
            for (int i = 0; i < arr.length; i++) {
                result[i] = String.format("%02d:%02d", arr[i].hour, arr[i].minute);
            }
            return result;
        }

        public void onNotifyBusChanged(Bus bus) {
            mCurBus = bus;
            refreshViewData();
        }

        private int selectNextBusTime(Bus bus) {
            Calendar cal = Calendar.getInstance();
            Time tcur = Bus.buildTimeType(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
            //Time tcur = Bus.buildTimeType(9, 15);
            Time[] allTimes = bus.getAllBusTime();

            if (Time.compare(tcur, bus.getStartTime()) <= 0) //the early of the day
                return -1;
            else if (Time.compare(tcur, bus.getEndTime()) > 0) { //the very late of the day
                return -2; //The first time.
            }
            else {
                for (int i = 0; i < allTimes.length; i++) {
                    if (Time.compare(tcur, allTimes[i]) <= 0) {
                        return i;
                    }
                }
            }
            return 0;
        }
    }

}
