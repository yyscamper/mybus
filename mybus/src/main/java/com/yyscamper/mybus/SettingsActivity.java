package com.yyscamper.mybus;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.widget.CheckBox;


import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    public static String KEY_SELECT_BUS_EARLY_LATE_PROMPT = "enable_early_miss_bus_prompt";
    public static String KEY_SELECT_STARTUP_SCREEN = "startup_screen_select";
    public static String KEY_SELECT_FAV_BUS = "fav_bus_select";

    SettingFragment mSettingFragment;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettingFragment = new SettingFragment();
        getFragmentManager().beginTransaction().replace(android.R.id.content, mSettingFragment).commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase(KEY_SELECT_FAV_BUS)) {
            ListPreference pref = (ListPreference)mSettingFragment.findPreference(key);
            pref.setSummary(pref.getEntry());
        }
        else if (key.equalsIgnoreCase(KEY_SELECT_STARTUP_SCREEN)) {
            ListPreference pref = (ListPreference)mSettingFragment.findPreference(key);
            pref.setSummary(pref.getEntry());
        }
        /*
        else if (key.equalsIgnoreCase(KEY_SELECT_STARTUP_SCREEN)) {
            CheckBoxPreference pref = (CheckBoxPreference)mSettingFragment.findPreference(key);
            if (pref.isChecked()) {
                pref.setSummary(getString(R.string.selected_bus_early_late_prompt));
            }
            else {
                pref.setSummary(getString(R.string.unselected_bus_early_late_prompt));
            }
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSettingFragment.getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        SharedPreferences prefManger = PreferenceManager.getDefaultSharedPreferences(this);
        ListPreference pref = (ListPreference)mSettingFragment.findPreference(KEY_SELECT_STARTUP_SCREEN);
        pref.setSummary(pref.getEntry());

        pref = (ListPreference)mSettingFragment.findPreference(KEY_SELECT_FAV_BUS);
        pref.setSummary(prefManger.getString(KEY_SELECT_FAV_BUS, ""));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        mSettingFragment.getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public static class SettingFragment extends PreferenceFragment {
        CheckBoxPreference mPreferenceSelectEarlyLateBusPrompt;
        ListPreference mPreferenceSelectStartupScreen;
        ListPreference mPreferenceSelectFavBus;

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            mPreferenceSelectFavBus = (ListPreference)findPreference(SettingsActivity.KEY_SELECT_FAV_BUS);
            mPreferenceSelectEarlyLateBusPrompt = (CheckBoxPreference)findPreference(SettingsActivity.KEY_SELECT_BUS_EARLY_LATE_PROMPT);
            mPreferenceSelectStartupScreen = (ListPreference)findPreference(SettingsActivity.KEY_SELECT_STARTUP_SCREEN);
            String[] buses = GetFavBusChooseEntries();
            mPreferenceSelectFavBus.setEntries(buses);
            mPreferenceSelectFavBus.setEntryValues(buses);

            /*
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            mPreferenceSelectEarlyLateBusPrompt.setChecked(sp.getBoolean(SettingsActivity.KEY_SELECT_BUS_EARLY_LATE_PROMPT, true));
            mPreferenceSelectStartupScreen.setEntryValues(
                    sp.getInt(SettingsActivity.KEY_SELECT_STARTUP_SCREEN, 0)
            );
            */
        }

        public String[] GetFavBusChooseEntries() {
            ArrayList<String> listFavs = new ArrayList<String>();
            BusPair[] pairs = BusManager.getAll();
            for (BusPair p : pairs) {
                Bus bus = p.getBus1();
                if (bus != null) {
                    listFavs.add(bus.getQueryKey());
                }

                bus = p.getBus2();
                if (bus != null) {
                    listFavs.add(bus.getQueryKey());
                }
            }

            String[] arr = new String[listFavs.size()];
            listFavs.toArray(arr);
            return arr;
        }

    }
};