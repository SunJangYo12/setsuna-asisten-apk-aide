package com.setsunajin.asisten.task;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;

public class StatsProcessor {
	final private int NUM_COUNTERS = 4;
	final private String DEV_FILE = "/proc/self/net/dev";
	final private String WIFI_DEV = "tiwlan0";
	final private String CELL_DEV = "rmnet0";

	private int mSamplingInterval;
	private WifiManager mWifi;
	private TelephonyManager mCellular;
	private ConnectivityManager mCx;

	private Vector<StatCounter> mCounters;
	private Vector<TextView> mCounterViews;
	private Vector<TextView> mInfoViews;

	private static String cell_label = "";
	private static String wifi_label = "";


	StatsProcessor(int sampling_interval,
				   TelephonyManager cellular,
				   WifiManager wifi,
				   ConnectivityManager cx) {
		mSamplingInterval = sampling_interval;
		mCellular = cellular;
		mWifi = wifi;
		mCx = cx;
		mCounters = new Vector<StatCounter>();
		for (int i = 0; i < NUM_COUNTERS; ++i) {
			mCounters.addElement(new StatCounter("B"));
		}
	}

	public void reset() {
		for (int i=0; i < NUM_COUNTERS; ++i ) {
			mCounters.get(i).reset();
			if (mCounterViews != null) {
				mCounters.get(i).paint(mCounterViews.get(i));
			}
		}
	}

	public Vector<StatCounter> getCounters() {
		return mCounters;
	}

	public void linkDisplay(Vector<TextView> counter_views,
							Vector<TextView> info_views,
							GraphView graph) {
		mCounterViews = counter_views;
		mInfoViews = info_views;
		for (int i=0; i < NUM_COUNTERS; ++i ) {
			mCounters.get(i).paint(mCounterViews.get(i));
		}
		processNetStatus();
	}

	public void unlinkDisplay() {
		mCounterViews = null;
		mInfoViews = null;
	}

	public boolean processUpdate() {
		processNetStatus();
		return processIfStats();
	}

	public String getProcessUpdate(String control) {
		if (control.equals("wifi")) {
			return wifi_label;
		} else if (control.equals("gsm")) {
			return cell_label;
		}
		return "";
	}

	public boolean processIfStats() {
		FileReader fstream;
		try {
			fstream = new FileReader(DEV_FILE);
		} catch (FileNotFoundException e) {
			Log.e("MonNet", "Could not read " + DEV_FILE);
			return false;
		}
		BufferedReader in = new BufferedReader(fstream, 500);
		String line;
		String[] segs;
		try {
			while ((line = in.readLine()) != null) {

				if (line.startsWith(CELL_DEV)) {
					segs = line.trim().split("[: ]+");
					updateStatCounter(segs[1], 0);
					updateStatCounter(segs[9], 1);
				} else if (line.startsWith(WIFI_DEV)) {
					segs = line.trim().split("[: ]+");
					updateStatCounter(segs[1], 2);
					updateStatCounter(segs[9], 3);
				}
			}
			return true;
		} catch (IOException e) {
			Log.e("MonNet", e.toString());
			return false;
		}
	}

	private void updateStatCounter(String text, int index) {
		if (mCounters.get(index).update(text, mSamplingInterval)) {
			if (mCounterViews != null) {
				mCounters.get(index).paint(mCounterViews.get(index));
			}
		}
	}

	private void processNetStatus() {
		if (mInfoViews != null) {
			NetworkInfo cell_cx = mCx.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifi_cx = mCx.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			WifiInfo wifi_info = mWifi.getConnectionInfo();

			// Cellular data
			if (cell_cx != null && cell_cx.getState() == NetworkInfo.State.CONNECTED) {
				if (mCellular.isNetworkRoaming()) {
					cell_label += "ROAMING ";
				}
				cell_label += mCellular.getNetworkOperatorName();
				cell_label += getCellularType(mCellular.getNetworkType());
			}
			mInfoViews.get(0).setText(cell_label);
			mInfoViews.get(0).setTextColor(Color.GREEN);

			// Wifi
			if (wifi_cx != null && wifi_cx.getState() == NetworkInfo.State.CONNECTED) {			
				wifi_label = wifi_info.getSSID();
			}
			mInfoViews.get(1).setText(wifi_label);
			mInfoViews.get(1).setTextColor(Color.GREEN);
		}
	}

	private String getCellularType(int type) {
		switch (type) {
			case TelephonyManager.NETWORK_TYPE_GPRS:
				return " GPRS";
			case TelephonyManager.NETWORK_TYPE_EDGE:
				return " EDGE";
			case TelephonyManager.NETWORK_TYPE_UMTS:
				return " UMTS";
			default:
				return "";
		}
	}
}
