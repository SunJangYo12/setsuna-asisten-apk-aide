package com.setsunajin.asisten;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.WindowManager;
import android.widget.ActionMenuView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ServiceWindow extends Service {
    private WindowManager wm;
    private LinearLayout layoutView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams params3d = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutView = new LinearLayout(this);
        layoutView.setBackgroundColor(Color.TRANSPARENT);

        Button btn = new Button(this);
        btn.setText("cara membuat");
        layoutView.addView(btn, params3d);
        layoutView.setOrientation(LinearLayout.VERTICAL);
        layoutView.setLayoutParams(layoutParams);

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(ActionMenuView.LayoutParams.MATCH_PARENT, getScreenHeight() / 2,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, PixelFormat.TRANSLUCENT);

        wm.addView(layoutView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wm.removeViewImmediate(layoutView);
    }

    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }
}
