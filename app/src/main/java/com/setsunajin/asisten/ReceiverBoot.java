package com.setsunajin.asisten;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.setsunajin.asisten.memori.MainMemori;
import com.setsunajin.asisten.memori.SharedMemori;

public class ReceiverBoot extends BroadcastReceiver {
    public static String dataTemp = "";
    public static String dataVolt = "";
    public static String dataAmp = "";
    public static String dataCpu = "";
    

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            context.startService(new Intent(context, ServicesBoot.class));
        }
        if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
            MainFileManager shell = new MainFileManager();
            String amp = shell.executer("cat /sys/class/power_supply/battery/current_now");
            String[] a = amp.split("(?<=\\G.{1})");
            String[] b = amp.split("(?<=\\G.{3})");
            if (a[0].equals("-"))
                b = amp.split("(?<=\\G.{4})");

            float BatteryTemp = (float)(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0))/10;
            float voltase     = (float)(intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0))/100;
            dataTemp = ""+BatteryTemp+(char)0x00B0+"C";
            dataVolt = ""+voltase+" V";
            dataAmp  = ""+b[0];
            if (voltase <= 34.5)
                toastText(context, dataVolt, Color.YELLOW, Gravity.TOP | Gravity.LEFT);
            else
                if (BatteryTemp >= 40.5)
                    toastText(context, dataTemp, Color.RED, Gravity.TOP | Gravity.RIGHT);
        }

        notifiBoot(context, dataVolt, dataTemp, dataAmp+" mA");
    }
	

    public void notifiBoot(Context context, String volt, String temp, String amp)
    {
        MainActivity weton = new MainActivity();
        MainMemori memori = new MainMemori();
        int lenNote = 0;

        if (new SharedMemori(context).getSharedMemori("notifi_catatan")) {
            lenNote = memori.getStringCatatan(context).size();
        }

        Intent maini;
        if (lenNote != 0) {
            maini = new Intent(context, MainCatatan.class);
            volt = volt+"  note[ "+lenNote+" ]";
        } else {
            maini = new Intent(context, MainCatatan.class);
            
        }

        Intent browi = new Intent(context, MainBrowser.class);
        Intent touchi = new Intent(context, MainApkExtrak.class);
        Intent komi = new Intent(context, MainKompas.class);

        PendingIntent browp = PendingIntent.getActivity(context, 0, browi, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent mainp = PendingIntent.getActivity(context, 0, maini, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent touchp = PendingIntent.getActivity(context, 0, touchi, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent kompp = PendingIntent.getActivity(context, 0, komi, PendingIntent.FLAG_UPDATE_CURRENT);


        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notifi_boot);
        contentView.setImageViewResource(R.id.notifi_boot_image, R.drawable.setting);
        contentView.setImageViewResource(R.id.notifi_boot_alat, R.drawable.compas);
        contentView.setImageViewResource(R.id.notifi_boot_main, R.drawable.icon_html);
        contentView.setTextViewText(R.id.notifi_boot_title, weton.getWeton(4));
        contentView.setTextViewText(R.id.notifi_boot_text, new StringBuilder().append(volt+"\n")
                .append(temp+"\n")
                .append(amp+"   CPU: "+dataCpu+"\n"));

        contentView.setOnClickPendingIntent(R.id.notifi_boot_image, mainp);
        contentView.setOnClickPendingIntent(R.id.notifi_boot_main, browp);

        if (new SharedMemori(context).getSharedMemori("touch")) {
            contentView.setOnClickPendingIntent(R.id.notifi_boot_alat, touchp);
        }
        else {
            contentView.setOnClickPendingIntent(R.id.notifi_boot_alat, kompp);
        }

        int ic = R.drawable.trans;
        if (lenNote != 0  &&  weton.getWeton(0).equals("Senin")) {
            ic = R.drawable.ic_menu_html;
        } else {
            if (weton.getWeton(0).equals("Minggu")) {
                ic = R.drawable.transm;
            }
        }
        Notification.Builder mBuilder = new Notification.Builder(context)
                .setSmallIcon(ic)
                .setPriority(Notification.PRIORITY_MAX)
                .setContent(contentView);

        Notification notification = mBuilder.build();
        notification.flags |= Notification.FLAG_NO_CLEAR;

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.notify(7, notification);
    }
    public void toastText(Context context, String data, int warna, int letak)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate(R.layout.toast_text, null);

        TextView text = (TextView) layout.findViewById(R.id.toast_text_toast);
        text.setText(data);
        text.setTextColor(Color.BLUE);
        text.setTextSize(13);
        text.setGravity(Gravity.CENTER);

        final Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(letak, 0, 0);
        toast.setView(text);
        toast.setView(layout);

        View toastView = toast.getView();
        toastView.setBackgroundColor(warna);

        CountDownTimer hitungMundur = new CountDownTimer(3000, 100)
        {
            public void onTick(long millisUntilFinished)
            {
                toast.show();
            }
            public void onFinish()
            {
                toast.cancel();
            }
        }.start();
    }
}
