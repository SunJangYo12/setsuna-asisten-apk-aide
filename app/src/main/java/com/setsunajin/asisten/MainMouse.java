package com.setsunajin.asisten;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ActionMenuView;
import android.widget.Toast;

import com.setsunajin.asisten.memori.MainMemori;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class MainMouse extends AccessibilityService {
    private View cursorView;
    private Toast toast;
    private WindowManager wm;
    WindowManager.LayoutParams params;
    private int x, y = 0;
    private DatagramSocket udpSocket;
    private int screenHeight, screenWidth = 0;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {}
    @Override
    public void onInterrupt() {}

    @Override
    public void onCreate() {
        super.onCreate();
        ServiceWindow sc = new ServiceWindow();
        screenHeight = sc.getScreenHeight();
        screenWidth = sc.getScreenWidth();

        cursorView = View.inflate(getBaseContext(), R.layout.activity_mouse, null);
        toast = new Toast(this);
        toast.setView(cursorView);
        toast.setGravity(0, x, y);
        //toast.show();

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        params = new WindowManager.LayoutParams(ActionMenuView.LayoutParams.WRAP_CONTENT,
                ActionMenuView.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = x;
        params.y = y;
        wm.addView(cursorView, params);

        Log.i("mouse", "sx :"+screenWidth+" sy:"+screenHeight);

        try {
            udpSocket = new DatagramSocket(9999);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] buffer = new byte[1];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    while (true) {
                        try {
                            udpSocket.receive(packet);
                            String message = new String(packet.getData()).trim();
                            final int event = Integer.parseInt(message);
                            new Handler(getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    onMouseMove(new MouseEvent(event));
                                }
                            });
                        } catch (IOException e) {}
                    }
                }
            }).start();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wm.removeViewImmediate(cursorView);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    private void click() {
        Log.d("Mouse", String.format("Click [%d, %d]", x, y));
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) return;
        AccessibilityNodeInfo nearestNodeToMouse = findSmallestNodeAtPoint(nodeInfo, x+10, y+70);
        if (nearestNodeToMouse != null) {
            logNodeHierachy(nearestNodeToMouse, 0);
            nearestNodeToMouse.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        nodeInfo.recycle();
    }

    public void onMouseMove(MouseEvent event) {
        switch (event.direction) {
            case MouseEvent.MOVE_LEFT:
                if (x >= 0)
                    x -= 10;
                break;
            case MouseEvent.MOVE_RIGHT:
                if (x != screenWidth)
                    x += 10;
                break;
            case MouseEvent.MOVE_UP:
                if (y >= 0)
                    y -= 10;
                break;
            case MouseEvent.MOVE_DOWN:
                if (y != screenHeight)
                    y += 10;
                break;
            case MouseEvent.LEFT_CLICK:
                click();
                break;
            default:
                break;
        }
        toast = new Toast(MainMouse.this);
        toast.setView(cursorView);
        toast.setGravity(0, x, y);
        Log.i("mouse", ""+x+" "+y);
        //toast.show();
        params.x = x;
        params.y = y;
        wm.updateViewLayout(cursorView, params);
    }

    private static void logNodeHierachy(AccessibilityNodeInfo nodeInfo, int depth) {
        Rect bounds = new Rect();
        nodeInfo.getBoundsInScreen(bounds);

        StringBuilder sb = new StringBuilder();
        if (depth > 0) {
            for (int i=0; i<depth; i++) {
                sb.append("  ");
            }
            sb.append("\u2514 ");
        }
        sb.append(nodeInfo.getClassName());
        sb.append(" (" + nodeInfo.getChildCount() +  ")");
        sb.append(" " + bounds.toString());
        if (nodeInfo.getText() != null) {
            sb.append(" - \"" + nodeInfo.getText() + "\"");
        }
        for (int i=0; i<nodeInfo.getChildCount(); i++) {
            AccessibilityNodeInfo childNode = nodeInfo.getChild(i);
            if (childNode != null) {
                logNodeHierachy(childNode, depth + 1);
            }
        }
    }

    private static AccessibilityNodeInfo findSmallestNodeAtPoint(AccessibilityNodeInfo sourceNode, int ax, int ay) {
        Rect bounds = new Rect();
        sourceNode.getBoundsInScreen(bounds);

        if (!bounds.contains(ax, ay)) {
            return null;
        }

        for (int i=0; i<sourceNode.getChildCount(); i++) {
            AccessibilityNodeInfo nearestSmaller = findSmallestNodeAtPoint(sourceNode.getChild(i), ax, ay);
            if (nearestSmaller != null) {
                return nearestSmaller;
            }
        }
        return sourceNode;
    }
}

class MouseEvent {
    public static final int
            MOVE_UP = 0,
            MOVE_DOWN = 1,
            MOVE_LEFT = 2,
            MOVE_RIGHT = 3,
            LEFT_CLICK = 4;

    public final int direction;
    public MouseEvent(int direction) {
        this.direction = direction;
    }
}
