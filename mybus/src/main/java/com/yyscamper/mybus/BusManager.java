package com.yyscamper.mybus;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.text.format.Time;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by yuanf on 2014-03-23.
 */
public class BusManager {
    static Hashtable<String, BusPair> mAllBusPairs;
    static String mSpliter = ",|、|，\\||;";
    public BusManager() {

    }

    private static Time[] tempBuildTimeArray(int size, int startHour, int startMin, int intervalMin) {
        Time[] arr = new Time[size];
        int hour = startHour;
        int min = startMin;
        for (int i = 0; i < size; i++) {
            arr[i] = Bus.buildTimeType(hour, min);
            min += intervalMin;
            if (min >= 60) {
                min = min % 60;
                hour++;
            }
        }
        return arr;
    }

    public static void init(InputStream inStream) throws Throwable {
        mAllBusPairs = new Hashtable<String, BusPair>();

        ArrayList<BusPair> pairs = loadFromFile(inStream);
        if (pairs != null) {
            for (BusPair p : pairs) {
                addBusPair(p);
            }
        }

        /*
        String[] allStops1 = new String[] {
                "南翔北", "星火路", "杨子路", "真南路众仁路", "瑞林路芳华路"
        };

        String[] allStops2 = new String[] {
                "嘉定北", "南翔镇", "古猗园路", "德华路", "南翔北火车站"
        };

        Bus bus1 = new Bus("南翔四路", allStops1, tempBuildTimeArray(20, 5, 20, 15));
        Bus bus2 = new Bus("南翔四路", allStops2, tempBuildTimeArray(22, 6, 10, 25));
        BusPair busPair1 = new BusPair("南翔四路", bus1, bus2);
        BusPair busPair2 = new BusPair("嘉定52路", bus2, bus1);
        addBusPair(busPair1);
        addBusPair(busPair2);
        */
    }

    public static boolean addBusPair(BusPair busPair) {
        if (mAllBusPairs.containsKey(busPair.getName()))
            return false;
        mAllBusPairs.put(busPair.getName(), busPair);
        return true;
    }

    public static BusPair[] getAll() {
        BusPair[] arr = new BusPair[mAllBusPairs.values().size()];
        mAllBusPairs.values().toArray(arr);
        return arr;
    }

    public static BusPair get(String name) {
        if (mAllBusPairs.containsKey(name))
            return mAllBusPairs.get(name);
        return null;
    }

    public static BusPair queryBus(String key) {
        for (BusPair p : mAllBusPairs.values()) {
            if (p.getBus1() != null && p.getBus1().getQueryKey().equals(key)) {
                p.setSelectedBus(1);
                return p;
            }
            else if (p.getBus2() != null && p.getBus2().getQueryKey().equals(key)) {
                p.setSelectedBus(2);
                return p;
            }
        }
        return null;
    }

    private static int toInt(String str, int offset, int endOffset) {
        int val = 0;
        char[] strBytes = str.toCharArray();
        for (int i = offset; i < endOffset; i++) {
            if (strBytes[i] == ' ')
                continue;
            val = val * 10 + (strBytes[i] - '0');
        }
        return val;
    }

    private static Time[] parseXmlBusTimesData(String str) {
        int sep1, sep2;
        int shour, smin, ehour, emin, interval;
        String[] arr = str.split(mSpliter);
        ArrayList<Time> listTimes = new ArrayList<Time>();
        for (String s : arr) {
            sep1 = s.indexOf('_');
            if (sep1 >= 0) { // this is range format
                sep2 = s.indexOf('_', sep1+1);
                if (sep2 < 0 || sep2 == s.length()-1) { //invalid range format
                    return null;
                }
                int c = s.indexOf(':', 0);
                if (c < 0 || c > sep1)
                    return null;
                shour = toInt(s, 0, c);
                smin = toInt(s, c+1, sep1);

                c = s.indexOf(':', sep1+1);
                if (c < 0 || c > sep2)
                    return null;
                ehour = toInt(s, sep1+1, c);
                emin = toInt(s, c+1, sep2);

                interval = toInt(s, sep2+1, s.length());

                while (shour < ehour || shour==ehour && smin <= emin) {
                    Time t = Bus.buildTimeType(shour, smin);
                    listTimes.add(t);
                    smin += interval;
                    if (smin >= 60) {
                        shour += (smin/60);
                        smin = smin % 60;
                    }
                }
            }
            else { //this is item format, one by one
                sep1 = s.indexOf(":");
                if (sep1 < 0) {
                    return null;
                }
                int hour = toInt(s, 0, sep1);
                int min = toInt(s, sep1 + 1, s.length());
                Time t = Bus.buildTimeType(hour, min);
                listTimes.add(t);
            }
        }

        Time[] tarr = new Time[listTimes.size()];
        listTimes.toArray(tarr);
        return tarr;
    }

    private static String[] getInvStrArray(String[] str) {
        String[] invArr = new String[str.length];
        int j = 0;
        for (int i = str.length-1; i >= 0; i--) {
            invArr[j++] = str[i];
        }
        return invArr;
    }

    private static BusPair buildBusPairFromXmlData(String pairName, String bus1Name, String bus1Stops, String bus1Times,
                                            String bus2Name, String bus2Stops, String bus2Times) {
        String[] arrBus1Stops = bus1Stops.split(mSpliter);
        String[] arrBus2Stops = bus2Stops.split(mSpliter);
        if (arrBus1Stops.length > 0 && arrBus1Stops[0].equalsIgnoreCase("$inv2")) {
            arrBus1Stops = getInvStrArray(arrBus2Stops);
        }
        if (arrBus2Stops.length > 0 && arrBus2Stops[0].equalsIgnoreCase("$inv1")) {
            arrBus2Stops = getInvStrArray(arrBus1Stops);
        }
        Time[] arrBus1Times = parseXmlBusTimesData(bus1Times);
        Time[] arrBus2Times = parseXmlBusTimesData(bus2Times);
        Bus bus1 = new Bus(bus1Name, arrBus1Stops, arrBus1Times);
        Bus bus2 = new Bus(bus2Name, arrBus2Stops, arrBus2Times);

        BusPair pair = new BusPair(pairName, bus1, bus2);
        return pair;
    }

    public static ArrayList<BusPair> loadFromFile(InputStream inStream) throws Throwable {
        String tagName;
        String pairName = null;
        String bus1Name = null, bus1Stops = null, bus1Times = null;
        String bus2Name = null, bus2Stops = null, bus2Times = null;

        ArrayList<BusPair> listBusPairs = new ArrayList<BusPair>();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(inStream, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    tagName = parser.getName();

                    if (tagName.equals("bus_pairs")) {
                        pairName = parser.getAttributeValue(null, "name");
                        break;
                    }

                    if (pairName != null && tagName.equals("bus1")) {
                        bus1Name = parser.getAttributeValue(null, "name");
                        bus1Stops = parser.getAttributeValue(null, "stops");
                        bus1Times = parser.getAttributeValue(null, "times");
                        break;
                    }

                    if (pairName != null && tagName.equals("bus2")) {
                        bus2Name = parser.getAttributeValue(null, "name");
                        bus2Stops = parser.getAttributeValue(null, "stops");
                        bus2Times = parser.getAttributeValue(null, "times");
                        break;
                    }

                    break;

                case XmlPullParser.END_TAG:
                    if (pairName == null)
                        break;
                    tagName = parser.getName();
                    if (tagName.equals("bus_pairs")) {
                        if (pairName != null
                                && bus1Name != null && bus1Stops != null && bus1Times != null
                                && bus2Name != null && bus2Stops != null && bus2Times != null) {
                            BusPair pair = buildBusPairFromXmlData(pairName,
                                    bus1Name, bus1Stops, bus1Times,
                                    bus2Name, bus2Stops, bus2Times);
                            listBusPairs.add(pair);
                            pairName = bus1Name = bus1Stops = bus1Times = bus2Times = bus2Name = bus2Stops = null;
                        }
                    }
                    break;

                default:
                    break;
            }

            eventType = parser.next();
        }
        return listBusPairs;
    }
}
