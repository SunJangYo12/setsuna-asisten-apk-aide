package com.setsunajin.asisten;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.RemoteViews;
import android.widget.Toast;

import android.os.Handler;
import android.os.Looper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.webkit.*;

import android.media.AudioManager;
import android.view.*;
import com.setsunajin.asisten.memori.*;

import android.provider.Settings;
import android.net.Uri;

public class MainBrowserNotif extends Activity {

    public static String rUrl = "http://192.168.43.53:8080";
    private static final String CHANNEL_ID = "MEDIA_CHANNEL_ID";
    private static final String ACTION_PREV = "ACTION_PREV";
    private static final String ACTION_PAUSE = "ACTION_PAUSE";
    private static final String ACTION_NEXT = "ACTION_NEXT";
    private static final String ACTION_VOLUME_UP = "ACTION_VOLUME_UP";
    private static final String ACTION_VOLUME_DOWN = "ACTION_VOLUME_DOWN";
    private Handler handler;
	
	private AudioManager audioManager;
	private SharedMemori shMemori;
    private Boolean sharedNotifi;

    private static final int REQUEST_OVERLAY = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
        handler = new Handler(Looper.getMainLooper());
		shMemori = new SharedMemori(this);
		
		String sharedIp = shMemori.getStrSharedMemori("remote_audacious");
		if (sharedIp != "") {
			rUrl = "http://"+sharedIp;
		}
		
		Toast.makeText(this, "remote ip "+rUrl, Toast.LENGTH_LONG).show();
        // Create and set the WebView
        WebView webView = new WebView(this);
		webView.getSettings().setDomStorageEnabled(true);
		
		webView.setWebChromeClient(new WebChromeClient());
		webView.setWebViewClient(new WebViewClient());
		webView.clearCache(true);
		webView.clearHistory();
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		
        webView.loadUrl(rUrl);
        setContentView(webView);


        sharedNotifi = shMemori.getSharedMemori("notifi_audacious");
        if (sharedNotifi)
        {
            // Register BroadcastReceiver
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_PREV);
            filter.addAction(ACTION_PAUSE);
            filter.addAction(ACTION_NEXT);
            filter.addAction(ACTION_VOLUME_UP);
            filter.addAction(ACTION_VOLUME_DOWN);
            registerReceiver(mediaActionReceiver, filter);

            showMediaNotification();
            Toast.makeText(this, "Notification media", Toast.LENGTH_LONG).show();
        }


        Boolean sharedOverlay = shMemori.getSharedMemori("overlay_audacious");
        if (sharedOverlay)
        {
            Toast.makeText(this, "Overlay media", Toast.LENGTH_LONG).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY);
            } else {
                startFloatingService();
            }
        }
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OVERLAY) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) {
                startFloatingService();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void startFloatingService() {
        Intent serviceIntent = new Intent(this, FloatingWindowService.class);
        startService(serviceIntent);
    }



    private void showMediaNotification() {
        // Create intents for media buttons
        Intent prevIntent = new Intent(ACTION_PREV);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(ACTION_PAUSE);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(this, 1, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 2, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent volumeUpIntent = new Intent(ACTION_VOLUME_UP);
        PendingIntent volumeUpPendingIntent = PendingIntent.getBroadcast(this, 3, volumeUpIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent volumeDownIntent = new Intent(ACTION_VOLUME_DOWN);
        PendingIntent volumeDownPendingIntent = PendingIntent.getBroadcast(this, 4, volumeDownIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create RemoteViews
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.activity_browser_notif);

        notificationLayout.setOnClickPendingIntent(R.id.notif_btn_prev, prevPendingIntent);
        notificationLayout.setOnClickPendingIntent(R.id.notif_btn_pause, pausePendingIntent);
        notificationLayout.setOnClickPendingIntent(R.id.notif_btn_next, nextPendingIntent);
        notificationLayout.setOnClickPendingIntent(R.id.notif_btn_volume_up, volumeUpPendingIntent);
        notificationLayout.setOnClickPendingIntent(R.id.notif_btn_volume_down, volumeDownPendingIntent);

        // Build the notification
        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_media_play) // Ganti dengan icon notifikasi Anda
                .setContentTitle("Media Notification")
                .setContentText("Control media playback and volume")
                .setCustomContentView(notificationLayout)
                .setPriority(Notification.PRIORITY_HIGH);

        // Get the NotificationManager and show the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            // Tombol Volume Naik ditekan
            // Tambahkan logika Anda di sini
			
			sendGetRequest(rUrl+"/control?command=volumeup");
            return true; // Kembalikan true jika Anda ingin mencegah aksi default
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // Tombol Volume Turun ditekan
            // Tambahkan logika Anda di sini
			sendGetRequest(rUrl+"/control?command=volumedown");
            return true; // Kembalikan true jika Anda ingin mencegah aksi default
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            // Tombol Volume dilepaskan
            // Tambahkan logika Anda di sini jika diperlukan
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private final BroadcastReceiver mediaActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                switch (action) {
                    case ACTION_PREV:
                        // Handle previous action
                        showToast("Previous");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                sendGetRequest(rUrl+"/control?command=prev");
                            }
                        });
                        break;
                    case ACTION_PAUSE:
                        // Handle pause action
                        showToast("Pause");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                sendGetRequest(rUrl+"/control?command=playpause");
                            }
                        });
                        break;
                    case ACTION_NEXT:
                        // Handle next action
                        showToast("Next");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                sendGetRequest(rUrl+"/control?command=next");
                            }
                        });
                        break;
                    case ACTION_VOLUME_UP:
                        // Handle volume up action
                        showToast("Volume Up");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                sendGetRequest(rUrl+"/control?command=volumeup");
                            }
                        });
                        break;
                    case ACTION_VOLUME_DOWN:
                        // Handle volume down action
                        showToast("Volume Down");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                sendGetRequest(rUrl+"/control?command=volumedown");
                            }
                        });
                        break;
                }
            }
        }

        private void showToast(String message) {
            Toast.makeText(MainBrowserNotif.this, message, Toast.LENGTH_SHORT).show();
        }
    };

    private void sendGetRequest(final String urlString) {
        // Create a new thread to perform the network request
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();

                    // Handle the result here (for example, display it in the WebView)
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //webView.loadData(result.toString(), "text/html", "UTF-8");

                            Toast.makeText(MainBrowserNotif.this, "success send method", Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainBrowserNotif.this, "err: "+e, Toast.LENGTH_SHORT).show();
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensure notifications are created if the channel does not exist
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Media Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (sharedNotifi)
        {
            // Unregister the BroadcastReceiver
            unregisterReceiver(mediaActionReceiver);
        }
    }
}
