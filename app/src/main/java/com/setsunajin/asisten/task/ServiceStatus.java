package com.setsunajin.asisten.task;

import java.util.Vector;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.setsunajin.asisten.ReceiverBoot;

public class ServiceStatus extends Service {
	final private String TAG="NetMeterService";
	final private int SAMPLING_INTERVAL = 5;

	private NotificationManager mNM;
	private WifiManager wifi;

	/**
	 * 
	 * Binder implementation which passes through a reference to
	 * this service. Since this is a local service, the activity
	 * can then call directly methods on this service instance.
	 */
	public class NetMeterBinder extends Binder {
            ServiceStatus getService() {
                return ServiceStatus.this;
            }
        }
	private final IBinder mBinder = new NetMeterBinder();

	// various stats collection objects
	private StatsProcessor mStatsProc;
	private CpuMon mCpuMon;
	private PowerMon mPower;
	private ReceiverBoot rece;
	private Context context;
	private GraphView mGraph = null;
	private long mLastTime;
        private static int i = 0;

	// All the polling and display updating is driven from this
	// hander which is periodically executed every SAMPLING_INTERVAL seconds.
	public Handler mHandler = new Handler();
	private Runnable mRefresh = new Runnable() {
		public void run() {
		        long last_time = SystemClock.elapsedRealtime();
			if (last_time - mLastTime > 10 * SAMPLING_INTERVAL * 1000) {
				int padding = (int) ((last_time - mLastTime) / (SAMPLING_INTERVAL * 1000));
				mCpuMon.getHistory().pad(padding);

				Vector<StatCounter> counters = mStatsProc.getCounters();
				for (int i = 0; i < counters.size(); i++) {
					counters.get(i).getHistory().pad(padding);
				}
			}
			mLastTime = last_time;
			mStatsProc.processUpdate();
			mCpuMon.readStats();
			rece.dataCpu = mCpuMon.cpuPakai;
			if (mGraph != null) mGraph.refresh();
                        i+=1;
			mHandler.postDelayed(mRefresh, SAMPLING_INTERVAL * 1000);
		}
	};
	/**
	 * Reset the counters - triggered by the reset menu of the controller activity
	 */
	public void resetCounters() {
		mStatsProc.reset();
	}

	public void setDisplay(Vector<TextView> stats_views, Vector<TextView> info_views, Vector<TextView> cpu_views, GraphView graph)
	{
		mGraph = graph;
		mStatsProc.linkDisplay(stats_views, info_views, graph);
		mCpuMon.linkDisplay(cpu_views);
		graph.linkCounters(mStatsProc.getCounters(), mCpuMon.getHistory());
	}

	/**
	 * Framework method called when the service is first created.
	 */
	@Override
	public void onCreate() {
		context = this;
		wifi = (WifiManager)context.getSystemService(WIFI_SERVICE);
		TelephonyManager cellular = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		ConnectivityManager cx = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

		mStatsProc = new StatsProcessor(SAMPLING_INTERVAL, cellular, wifi, cx);
		mCpuMon = new CpuMon();
                mPower = new PowerMon(this);
		rece = new ReceiverBoot();

		mStatsProc.processUpdate();
		mStatsProc.reset();

		mLastTime = SystemClock.elapsedRealtime();
		mHandler.postDelayed(mRefresh, SAMPLING_INTERVAL * 1000);


		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	}

	/**
	 * Framework method called when the service is stopped/destroyed
	 
	@Override
        public void onDestroy() {
		//mHandler.removeCallbacks(mRefresh);
                mHandler.postDelayed(mRefresh, SAMPLING_INTERVAL * 1000);
	}*/

	/**
	 * Framework method called whenever an activity binds to this service.
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		Log.i(TAG, "onBind");
		return mBinder;
	}
	/**
	 * Framework method called when an activity binding to the service
	 * is broken.
	 */
	@Override
	public boolean onUnbind(Intent arg) {
		Log.i(TAG, "onUnbind");
		mStatsProc.unlinkDisplay();
		mCpuMon.unlinkDisplay();
		mGraph = null;
		return true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "ServiceTask destroy ...", Toast.LENGTH_LONG).show();
	}
}
