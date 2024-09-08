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

public class MainBrowserNotif extends Activity {

    private static final String rUrl = "http://192.168.43.53:8080";
    private static final String CHANNEL_ID = "MEDIA_CHANNEL_ID";
    private static final String ACTION_PREV = "ACTION_PREV";
    private static final String ACTION_PAUSE = "ACTION_PAUSE";
    private static final String ACTION_NEXT = "ACTION_NEXT";
    private static final String ACTION_VOLUME_UP = "ACTION_VOLUME_UP";
    private static final String ACTION_VOLUME_DOWN = "ACTION_VOLUME_DOWN";
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler(Looper.getMainLooper());

        // Create and set the WebView
        WebView webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(rUrl);
        setContentView(webView);

        // Register BroadcastReceiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PREV);
        filter.addAction(ACTION_PAUSE);
        filter.addAction(ACTION_NEXT);
        filter.addAction(ACTION_VOLUME_UP);
        filter.addAction(ACTION_VOLUME_DOWN);
        registerReceiver(mediaActionReceiver, filter);

        // Show media notification
        showMediaNotification();
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
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the BroadcastReceiver
        unregisterReceiver(mediaActionReceiver);
    }
}