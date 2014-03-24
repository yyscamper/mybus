package com.yyscamper.mybus;

import android.content.Context;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.Calendar;

/**
 * Created by yuanf on 2014-03-23.
 */
public class BusPairInfoListAdapter extends BaseAdapter {
    BusPair[] mAllBusPairs;
    Context mContext;
    LayoutInflater mInflater;

    public BusPairInfoListAdapter(Context context, BusPair[] allBusPairs) {
        mAllBusPairs = allBusPairs;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mAllBusPairs.length;
    }

    @Override
    public Object getItem(int postion) {
        return mAllBusPairs[postion];
    }

    @Override
    public long getItemId(int postion) {
        return postion;
    }

    @Override
    public int getViewTypeCount()
    {
        return 1;
    }

    @Override
    public int getItemViewType(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.bus_pair_info_list_item, null);
        }

        BusPair busPair = mAllBusPairs[position];

        TextView viewBusPairName = (TextView)convertView.findViewById(R.id.textViewBusPairName);
        TextView viewBus1Info = (TextView)convertView.findViewById(R.id.textViewBus1Info);
        TextView viewBus2Info = (TextView)convertView.findViewById(R.id.textViewBus2Info);

        viewBusPairName.setText(busPair.getName());
        viewBus1Info.setText(getBusInfoString(busPair.getBus1()));
        viewBus2Info.setText(getBusInfoString(busPair.getBus2()));

        return convertView;
    }

    private String getBusInfoString(Bus bus) {
        StringBuffer sb = new StringBuffer();
        sb.append(bus.getStartStop() == null ? "未知起点站" : bus.getStartStop());
        sb.append('-');
        sb.append(bus.getEndStop() == null ? "未知终点站" : bus.getEndStop());
        sb.append(" (");
        if (bus.getStartTime() != null) {
            sb.append(String.format("%02d:%02d", bus.getStartTime().hour, bus.getStartTime().minute));
        }
        else {
            sb.append("??:??");
        }
        sb.append('-');
        if (bus.getEndTime() != null) {
            sb.append(String.format("%02d:%02d", bus.getEndTime().hour, bus.getEndTime().minute));
        }
        else {
            sb.append("??:??");
        }
        sb.append(')');
        return sb.toString();
    }
}
