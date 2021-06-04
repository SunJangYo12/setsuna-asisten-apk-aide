package com.setsunajin.asisten.task;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.TextView;
import android.os.BatteryManager;

import com.setsunajin.asisten.MainFileManager;

public class PowerMon {
	public static String mOutput = null;
	public static String mAmpere = null;
	public static String mVoltase = null;
	public static String mTempe = null;
	public static int mLevel = 0;
	
	BroadcastReceiver mBattReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {

			context.unregisterReceiver(this);
			int rawlevel = intent.getIntExtra("level", -1);
			int scale = intent.getIntExtra("scale", -1);
			int status = intent.getIntExtra("status", -1);
			int health = intent.getIntExtra("health", -1);
			int level = -1;  // percentage, or -1 for unknown

			if (rawlevel >= 0 && scale > 0) {
				level = (rawlevel * 100) / scale;
			}
			
			if (mOutput != null) {
				mOutput = Integer.toString(level)+"%";
			}
			mLevel = level;

			MainFileManager exe = new MainFileManager();
			String amp = exe.executer("cat /sys/class/power_supply/battery/current_now");
			String[] a = amp.split("(?<=\\G.{1})");
			String[] b = amp.split("(?<=\\G.{3})");
			
			if (a[0].equals("-")){
				b = amp.split("(?<=\\G.{4})");
			}
			
			float BatteryTemp = (float)(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0))/10;
			float voltase     = (float)(intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0))/100;
			
			mTempe = ""+BatteryTemp+(char)0x00B0+"C";
			mVoltase = ""+voltase+" V";
			mAmpere  = ""+b[0]+" mA ";
			
		}
	};
	
	public PowerMon(Context context) {
		IntentFilter battFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		context.registerReceiver(mBattReceiver, battFilter);
	}
	public PowerMon() {
	}
}
