package com.yyscamper.mybus;

import android.text.format.Time;

import java.util.*;
/**
 * Created by yuanf on 2014-03-23.
 */
public class Bus {
    String mName;
    String[] mAllStops;
    Time[] mAllBusTimes;
    int mID;
    String mQueryKey; //BusPairName + StartStop + EndStop
    private static int mNextID = 0;

    public Bus(String name, String[] allStops, Time[] allBusTimes) {
        mName = name;
        mAllStops = allStops;
        mAllBusTimes = allBusTimes;
        mID = mNextID;
        mNextID++;
        mQueryKey = String.format("%s(%s-%s)", name, allStops[0], allStops[allStops.length-1]);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String[] getAllBusStops() {
        return mAllStops;
    }

    public Time[] getAllBusTime() {
        return mAllBusTimes;
    }

    public String getStartStop() {
        if (mAllStops == null || mAllStops.length == 0)
            return null;
        return mAllStops[0];
    }

    public String getEndStop() {
        if (mAllStops == null || mAllStops.length == 0)
            return null;
        return mAllStops[mAllStops.length-1];
    }

    public Time getStartTime() {
        if (mAllBusTimes == null || mAllBusTimes.length == 0) {
            return null;
        }
        return mAllBusTimes[0];
    }

    public Time getEndTime() {
        if (mAllBusTimes == null || mAllBusTimes.length == 0)
            return null;
        return mAllBusTimes[mAllBusTimes.length-1];
    }

    public static Time buildTimeType(int hour, int min) {
        Time t = new Time();
        t.set(0, min, hour, 1, 1, 2000);
        return t;
    }

    public int getID() { return mID; }
    public void setID(int id) { mID = id; }
    public String getQueryKey() {return mQueryKey;}
}
