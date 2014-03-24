package com.yyscamper.mybus;

/**
 * Created by yuanf on 2014-03-23.
 */
public class BusPair {
    public String mName;
    public Bus mBus1;
    public Bus mBus2;
    public Bus mSelectedBus;

    public BusPair(String name, Bus bus1, Bus bus2) {
        mName = name;
        mBus1 = bus1;
        mBus2 = bus2;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Bus getBus1() {
        return mBus1;
    }

    public Bus getBus2() {
        return mBus2;
    }

    public Bus getSelectedBus() {
        return mSelectedBus;
    }

    public void setSelectedBus(int idx) {
        if (idx == 2) {
            mSelectedBus = mBus2;
        }
        else {
            mSelectedBus = mBus1;
        }
    }
}
