package com.setsunajin.asisten;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

public class ServicesBoot extends Service {
    private BroadcastReceiver broadcastReceiver;
    private ReceiverBoot receiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(Intent.ACTION_HEADSET_PLUG);
        filter.addAction(Intent.ACTION_SCREEN_ON);

        broadcastReceiver = new ReceiverBoot();
        registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        Toast.makeText(this, "ServiceBoot destroy ...", Toast.LENGTH_LONG).show();
    }
}
