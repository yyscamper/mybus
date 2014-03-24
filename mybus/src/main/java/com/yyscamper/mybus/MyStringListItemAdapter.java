package com.yyscamper.mybus;

import android.content.Context;
import android.graphics.Color;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.Calendar;

/**
 * Created by yuanf on 2014-03-23.
 */
public class MyStringListItemAdapter extends BaseAdapter {
    String[] mAllStrings;
    Context mContext;
    LayoutInflater mInflater;
    int mSelectedItem = -1;

    public MyStringListItemAdapter(Context context, String[] allStrings) {
        mAllStrings = allStrings;
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mAllStrings.length;
    }

    @Override
    public Object getItem(int postion) {
        return mAllStrings[postion];
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
            convertView = mInflater.inflate(R.layout.my_string_list_item, null);
        }
        String str = mAllStrings[position];
        TextView viewString = (TextView)convertView.findViewById(R.id.textViewString);
        viewString.setText(str);

        if (mSelectedItem == position) {
            convertView.setBackgroundColor(Color.BLUE);
        }
        else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }


    public void setSelectedItem(int pos) {
        mSelectedItem = pos;
        notifyDataSetChanged();
    }

    public int getSelectedItemPostion() {
        return mSelectedItem;
    }
}
