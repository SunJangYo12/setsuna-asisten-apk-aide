package com.setsunajin.asisten;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FloatingWindowService extends Service {

    private WindowManager windowManager;
    private LinearLayout floatingLayout;
    private WindowManager.LayoutParams params;

    private int initialX, initialY;
    private float initialTouchX, initialTouchY;

    private MainBrowserNotif base;

    @Override
    public void onCreate() {
        super.onCreate();

        // Layout horizontal kecil
        floatingLayout = new LinearLayout(this);
        floatingLayout.setOrientation(LinearLayout.HORIZONTAL);
        floatingLayout.setPadding(7, 7, 7, 7);
        floatingLayout.setBackgroundColor(0xAA000000);

        // Ukuran tombol kecil
        int buttonSize = 100;

        // Tombol Prev
        ImageButton prevButton = new ImageButton(this);
        prevButton.setImageResource(android.R.drawable.ic_media_previous);
        prevButton.setBackgroundColor(0x00000000);
        prevButton.setLayoutParams(new LinearLayout.LayoutParams(buttonSize, buttonSize));

        // Tombol Pause
        ImageButton pauseButton = new ImageButton(this);
        pauseButton.setImageResource(android.R.drawable.ic_media_pause);
        pauseButton.setBackgroundColor(0x00000000);
        pauseButton.setLayoutParams(new LinearLayout.LayoutParams(buttonSize, buttonSize));

        // Tombol Next
        ImageButton nextButton = new ImageButton(this);
        nextButton.setImageResource(android.R.drawable.ic_media_next);
        nextButton.setBackgroundColor(0x00000000);
        nextButton.setLayoutParams(new LinearLayout.LayoutParams(buttonSize, buttonSize));


        TextView txtVolDown = new TextView(this);
        txtVolDown.setBackgroundColor(0x00000000);
        txtVolDown.setLayoutParams(new LinearLayout.LayoutParams(buttonSize, buttonSize));
        txtVolDown.setText("Vol-");

        TextView txtVolUp = new TextView(this);
        txtVolUp.setBackgroundColor(0x00000000);
        txtVolUp.setLayoutParams(new LinearLayout.LayoutParams(buttonSize, buttonSize));
        txtVolUp.setText("Vol+");

        // Tambahkan tombol ke layout
        floatingLayout.addView(prevButton);
        floatingLayout.addView(pauseButton);
        floatingLayout.addView(nextButton);
        floatingLayout.addView(txtVolDown);
        floatingLayout.addView(txtVolUp);

        // Parameter floating window
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 200;
        params.y = 300;

        // Inisialisasi WindowManager
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(floatingLayout, params);

        // Drag listener
        floatingLayout.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingLayout, params);
                        return true;
                }
                return false;
            }
        });

        base = new MainBrowserNotif();


        // Click listeners versi Java lama
        prevButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendGetRequest(base.rUrl+"/control?command=prev");
                Toast.makeText(FloatingWindowService.this, "Previous clicked", Toast.LENGTH_SHORT).show();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendGetRequest(base.rUrl+"/control?command=playpause");

                Toast.makeText(FloatingWindowService.this, "Pause clicked", Toast.LENGTH_SHORT).show();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendGetRequest(base.rUrl+"/control?command=next");
                Toast.makeText(FloatingWindowService.this, "Next clicked "+base.rUrl, Toast.LENGTH_SHORT).show();
            }
        });

        txtVolDown.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendGetRequest(base.rUrl+"/control?command=volumedown");
                Toast.makeText(FloatingWindowService.this, "Vol- clicked "+base.rUrl, Toast.LENGTH_SHORT).show();
            }
        });

        txtVolUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendGetRequest(base.rUrl+"/control?command=volumeup");
                Toast.makeText(FloatingWindowService.this, "Vol+ clicked "+base.rUrl, Toast.LENGTH_SHORT).show();
            }
        });
    }

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

                    

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingLayout != null) windowManager.removeView(floatingLayout);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
