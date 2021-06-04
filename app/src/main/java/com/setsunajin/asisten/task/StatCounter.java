package com.setsunajin.asisten.task;

import java.text.DecimalFormat;
import android.widget.TextView;

public class StatCounter {
	final private long KILO = 1000;
	final private long MEGA = KILO * 1000;
	final private long GIGA = MEGA * 1000;

	final private DecimalFormat mFmt = new DecimalFormat("###,###.0");
	final private String mUnit;

	private String mLastUpdate;
	private long mBase;
	private long mValue;
	private HistoryBuffer mRateHistory;


	StatCounter(String unit) {
		mUnit = unit;
		mBase = 0;
		mValue = 0;
		mRateHistory = new HistoryBuffer();
		mLastUpdate = null;
	}
	final public void reset() {
		mBase = mValue;
	}

	final public boolean update(String val_str, int time_delta) {
		if (val_str == mLastUpdate) {
			mRateHistory.add(0);
			return false;
		}

		int val = Integer.parseInt(val_str);
		if (val < mBase || mLastUpdate == null) {
			mBase = 0; // wrap-around or reset
		} else {
			mRateHistory.add((int)((val - mBase) - (mValue - mBase)) / time_delta * 8);
		}
		mValue = val;	
		mLastUpdate = val_str;
		return true;
	}

	final public void paint(TextView view) {
		Long disp_val = mValue - mBase;
		if ( disp_val > GIGA) {
			view.setText(mFmt.format((double)disp_val / GIGA) + " G" + mUnit);
		} else if ( disp_val > MEGA) {
			view.setText(mFmt.format((double)disp_val / MEGA) + " M" + mUnit);
		} else if ( disp_val > KILO) {
			view.setText(mFmt.format((double)disp_val / KILO) + " k" + mUnit);
		} else {
			view.setText(Long.toString(disp_val) + " " + mUnit);
		}
	}

	final public HistoryBuffer getHistory() {
		return mRateHistory;
	}
}
