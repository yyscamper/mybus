package com.yyscamper.mybus;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity {

    ListView mViewBusPairs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewBusPairs = (ListView)findViewById(R.id.listViewBusPairs);

        try {
            InputStream input = getAssets().open("default_bus_data.xml");
            BusManager.init(input);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
        BusPairInfoListAdapter adapter = new BusPairInfoListAdapter(getApplicationContext(), BusManager.getAll());
        mViewBusPairs.setAdapter(adapter);

        mViewBusPairs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BusPair busPair = (BusPair)mViewBusPairs.getAdapter().getItem(i);
                busPair.setSelectedBus(1);
                Intent intent = new Intent(MainActivity.this, BusInfoActivity.class);
                intent.putExtra("bus_pair_name", busPair.getName());
                startActivity(intent);
            }
        });

        SharedPreferences setting = PreferenceManager.getDefaultSharedPreferences(this);
        String favBus = setting.getString(SettingsActivity.KEY_SELECT_FAV_BUS, null);
        String startScreen = setting.getString(SettingsActivity.KEY_SELECT_STARTUP_SCREEN, null);
        if (startScreen != null && startScreen.equals("1") && favBus != null) {
            BusPair pair = BusManager.queryBus(favBus);
            if (pair != null) {
                Intent intent = new Intent(MainActivity.this, BusInfoActivity.class);
                intent.putExtra("bus_pair_name", pair.getName());
                startActivity(intent);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
