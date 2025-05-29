package com.setsunajin.asisten;


import android.app.Activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.setsunajin.asisten.memori.MainMemori;
import com.setsunajin.asisten.memori.SharedMemori;
import com.setsunajin.asisten.task.MainTaskManager;
import com.setsunajin.asisten.task.ServiceStatus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends Activity {

    // Used to load the 'native-lib' library on application startup.
   /* static {
        System.loadLibrary("main");
    }*/

    public static String homePath = "/data/data/com.setsunajin.asisten";

    public static String consoleLog = "";
    public TextView consoleText;
    private EditText edtNote;
    private ScrollView consoleScroll;
    private MainMemori memori;
    private Button btnApkExtrak;
    private Button btnObjviewer;
    private Button btnKeyboard;
    private Button btnMouse;
    private Button btnBrowser;
    private Button btnFilemanager;
    private Button btnTerminal;
	private Button btnRemote, btnRemoteClip;
    private Button btnTask;
    private Button btnServer;
	private Button btnReadjson;
    private Button btnSqlite;
    private Button btnSubdomner;
    private Button btnPengaturan;
    private Button btnExit;
    private Button btnExitfull;
    private boolean showDBHistory = true;

    @Override
    protected void onPause() {
        consoleLog = "["+new SimpleDateFormat("HH:mm:ss").format(new Date())+"] ";
        super.onPause();
    }

    @Override
    protected void onResume() {
        consoleLog = "["+new SimpleDateFormat("HH:mm:ss").format(new Date())+"] ";
        consoleText.append(consoleLog+"MainActivtiy resume ... \n");
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.FIRST, 1, 1, "About").setIcon(R.drawable.icon_css);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Tentang programer");
            builder.setMessage("Original kode by sunjangyo12@gmail.com\nInspirasi dari stackoverflow.com dll");
            builder.setPositiveButton("OK", null);
            AlertDialog dialog = builder.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        consoleLog = "["+new SimpleDateFormat("HH:mm:ss").format(new Date())+"] ";

        consoleText = findViewById(R.id.console);
        memori = new MainMemori();

        

        consoleScroll = (ScrollView) findViewById(R.id.console_scroll);
        consoleFocus();

        if (showDBHistory) {
            for (int i=memori.getStringHistory(this).size() - 1; i>=0; i--) {
                consoleText.append(memori.getStringHistory(this).get(i));
            }
        }
        consoleText.append(consoleLog+ "MainActivity started ...\n");
        //consoleText.append(consoleLog+ stringFromJNI()+" ...\n");

        btnExit = (Button)findViewById(R.id.main_btn_exit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLog(MainActivity.this, consoleLog+"Exit without services destroyed ...\n");
                consoleText.append(consoleLog+"Exit without services destroyed ...\n");
                consoleFocus();

                MainActivity.this.finish();
            }
        });

        btnExitfull = (Button)findViewById(R.id.main_btn_exitfull);
        btnExitfull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLog(MainActivity.this, consoleLog+"Application exit All services destroyed ...\n");
                consoleText.append(consoleLog+"Application exit All services destroyed ...\n");
                consoleFocus();

                stopService(new Intent(MainActivity.this, ServicesBoot.class));
                stopService(new Intent(MainActivity.this, ServiceStatus.class));
                MainActivity.this.finish();
            }
        });
		
		btnReadjson = (Button)findViewById(R.id.main_btn_readjson);
        btnReadjson.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					startActivity(new Intent(MainActivity.this, MainReadJson.class));
				}
			});
			
		btnRemoteClip = (Button)findViewById(R.id.main_btn_remoteclip);
        btnRemoteClip.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					startActivity(new Intent(MainActivity.this, MainRemoteClip.class));
				}
			});
		

        btnFilemanager = (Button)findViewById(R.id.main_btn_filemanager);
        btnFilemanager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consoleText.append(consoleLog+"Filemanager opening ...\n");
                setLog(MainActivity.this, consoleLog+"Filemanager opening ...\n");
                consoleFocus();

                startActivity(new Intent(MainActivity.this, MainFileManager.class));
            }
        });

        btnApkExtrak = (Button)findViewById(R.id.main_btn_apkextrak);
        btnApkExtrak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consoleText.append(consoleLog+"Apk Extraktor opening ...\n");
                setLog(MainActivity.this, consoleLog+"Apk Extraktor opening ...\n");
                consoleFocus();

                new MainApkExtrak().setTouch = false;
                startActivity(new Intent(MainActivity.this, MainApkExtrak.class));
            }
        });

        btnSubdomner = (Button)findViewById(R.id.main_btn_subdomner);
        btnSubdomner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consoleText.append(consoleLog+"Browser Subdomner opening ...\n");
                setLog(MainActivity.this, consoleLog+"Browser Subdomner opening ...\n");
                consoleFocus();

                startActivity(new Intent(MainActivity.this, MainSubdomner.class));
            }
        });

        btnTerminal = (Button)findViewById(R.id.main_btn_terminal);
        btnTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consoleText.append(consoleLog+"Linux Shell opening ...\n");
                setLog(MainActivity.this, consoleLog+"Linux Shell opening ...\n");
                consoleFocus();

                startActivity(new Intent(MainActivity.this, MainTerminal.class));
            }
        });
		
		btnRemote = (Button)findViewById(R.id.main_btn_remote);
        btnRemote.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					consoleText.append(consoleLog+"Remote audacious opening ...\n");
					setLog(MainActivity.this, consoleLog+"Remote audacious opening ...\n");
					consoleFocus();

					startActivity(new Intent(MainActivity.this, MainBrowserNotif.class));
				}
			});

        btnSqlite = (Button)findViewById(R.id.main_btn_sqlite);
        btnSqlite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String outLog = consoleLog+"Sqlite manager opening ...\n";
                setLog(MainActivity.this, outLog);
                consoleText.append(outLog);
                consoleFocus();

                startActivity(new Intent(MainActivity.this, MainMemori.class));
            }
        });

        btnTask = (Button)findViewById(R.id.main_btn_task);
        btnTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String outLog = consoleLog+"Taskmanager opening ...\n";
                setLog(MainActivity.this, outLog);
                consoleText.append(outLog);
                consoleFocus();

                startActivity(new Intent(MainActivity.this, MainTaskManager.class));
            }
        });

        btnPengaturan = (Button)findViewById(R.id.main_btn_pengaturan);
        btnPengaturan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consoleFocus();

                String outLog = consoleLog+"Pengaturan opening ...\n";
                setLog(MainActivity.this, outLog);
                consoleText.append(outLog);
                startActivity(new Intent(MainActivity.this, MainPengaturan.class));
            }
        });

        btnObjviewer = (Button)findViewById(R.id.main_btn_objviewer);
        btnObjviewer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consoleFocus();
				Toast.makeText(MainActivity.this, "Coming soon...", Toast.LENGTH_LONG).show();
            }
        });

        btnKeyboard = (Button)findViewById(R.id.main_btn_keyboard);
        btnKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String outLog = consoleLog+"Keyboard remote opening ...\n";
                setLog(MainActivity.this, outLog);
                consoleText.append(outLog);
                consoleFocus();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Usage Remote keyboard");
                builder.setMessage("Untuk mengaktifkan keyboard tcp ini \n\nklik setting>" +
                        "setelan tambahan>bahasa masukan>keyboard saat ini>pilih keyboard apk ini\n\n" +
                        "dan untuk remote Gunakana perintah ini di remote PC : telnet 10.42.0.101 9090");
                builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startService(new Intent(MainActivity.this, MainKeyboard.class));
                    }
                });
                AlertDialog dialog = builder.show();
            }
        });

        btnMouse = (Button)findViewById(R.id.main_btn_mouse);
        btnMouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String outLog = consoleLog+"Mouse Remote opening ...\n";
                setLog(MainActivity.this, outLog);
                consoleText.append(outLog);
                consoleFocus();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Usage Remote Mouse");
                builder.setMessage("untuk menggunakan remote mouse ini pastikan izin system alert windows " +
                        "sudah terallow dengan klik setings>aplikasi terinstall>pilih apk ini>perizinan lainya>checklist tampil jendela popup" +
                        "\n\ndan accesbilitas diberi ijin untuk aplikasi ini\n\n" +
                        "NOTE: jika popup system belum diijinkan aplikasi rawan crash!!");
                builder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startService(new Intent(MainActivity.this, MainMouse.class));
                    }
                });
                builder.setNegativeButton("Stop",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        stopService(new Intent(MainActivity.this, MainMouse.class));
                    }
                });
                AlertDialog dialog = builder.show();
            }
        });

        btnBrowser = (Button)findViewById(R.id.main_btn_browser);
        btnBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                consoleFocus();

                String outLog = consoleLog+"Browser opening ...\n";
                setLog(MainActivity.this, outLog);
                consoleText.append(outLog);
                startActivity(new Intent(MainActivity.this, MainBrowser.class));
            }
        });
		
		final Button btnKoogps = (Button)findViewById(R.id.main_btn_koogps);
        btnKoogps.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				consoleFocus();

				String outLog = consoleLog+"Gps koordinat opening ...\n";
				setLog(MainActivity.this, outLog);
				consoleText.append(outLog);
				Intent intent = new Intent(MainActivity.this, MainCatatan.class);
				intent.putExtra("shift", "gps");
				startActivity(intent);
			}
		});

        if (new SharedMemori(this).getSharedMemori("services"))
        {
            consoleText.append(consoleLog + "ServiceStatus starting ... \n");
            startService(new Intent(this, ServiceStatus.class));
            consoleText.append(consoleLog + "ServiceBoot starting ... \n");
            startService(new Intent(this, ServicesBoot.class));
        }
    }

    public void alertAddCatatan(Context context) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("Tambah Note");
        builder1.setCancelable(false);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate(R.layout.alert_catatan_add, null);

        edtNote = (EditText) layout.findViewById(R.id.alert_cata_add_amEdit);

        Button btSave = (Button) layout.findViewById(R.id.alert_cata_add_amSave);
        btSave.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                memori.setCatatan("new", edtNote.getText().toString(), "05:00", MainActivity.this);
                Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_LONG).show();
                edtNote.setText("");
            }
        });
        Button btCancel = (Button) layout.findViewById(R.id.alert_cata_add_amCancel);

        builder1.setView(layout);
        final AlertDialog alert11 = builder1.create();
        alert11.show();
        btCancel.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                alert11.dismiss();
            }
        });
    }

    private void consoleFocus() {
        consoleScroll.post(new Runnable() {
            @Override
            public void run() {
                consoleScroll.fullScroll(View.FOCUS_DOWN);
            }
        });
    }
    public static void setLog(Context context, String data) {
        new MainMemori().setHistory("new", data, "", context);
    }
    public String getWeton(int index){
        final KalenderKu kal = new KalenderKu();
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        String[] jawa = kal.MasehiToJawa(mYear, mMonth, mDay);// (mDay, mMonth, mYear);

        return jawa[index];
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //public native String stringFromJNI();
}

class KalenderKu {
    //utility function

    private double intPart(double floatNum){
        if ((float)floatNum < -0.0000001){
            return (double) Math.ceil(floatNum-0.0000001);
        }
        return (double)Math.floor(floatNum +0.0000001);
    }

    private static int JGREG= 15 + 31*(10+12*1582);
    //private static double HALFSECOND = 0.5;


    private static double toJulian(int y, int m, int d) {
        int year=y;
        int month=m; // jan=1, feb=2,...
        int day=d;
        int julianYear = year;
        if (year < 0) julianYear++;
        int julianMonth = month;
        if (month > 2) {
            julianMonth++;
        }
        else {
            julianYear--;
            julianMonth += 13;
        }

        double julian = (java.lang.Math.floor(365.25 * julianYear)
                + java.lang.Math.floor(30.6001*julianMonth) + day + 1720995.0);
        if (day + 31 * (month + 12 * year) >= JGREG) {
            // change over to Gregorian calendar
            int ja = (int)(0.01 * julianYear);
            julian += 2 - ja + (0.25 * ja);
        }
        return java.lang.Math.floor(julian);
    }

    public String[] MasehiToJawa(int year, int month, int day) {
        double julian = toJulian(year, month, day);
        double d=day;
        double m=month;
        double y=year;
        int mYear;
        int mMonth;
        int mDay;
        int sDay;

        String[] bulanjawa = {"Sura","Sapar","Mulud","Bakdamulud","Jumadilawal","Jumadilakhir",
                "Rejeb","Ruwah","Pasa","Sawal","Dulkaidah","Besar"};

        String[] bulanmasehi = new String[]{
                "Januari", "Februari", "Maret", "April","Mei","Juni",
                "Juli","Agustus","September","Oktober","November","Desember"
        };

        String[] harimasehi = new String[]{
                "Minggu","Senin", "Selasa","Rabu","Kamis","Jum'at","Sabtu"
        };

        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);

        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        sDay = c.get(Calendar.DAY_OF_WEEK);

        c.set(Calendar.DAY_OF_MONTH, 1);
        int startDayofMonth = c.get(Calendar.DAY_OF_WEEK);

        if(julian>=1937808 && julian<=536838867) {

            double mPart = (m-13)/12;
            double jd = intPart((1461*(y+4800+intPart(mPart)))/4)+
                    intPart((367*(m-1-12*(intPart(mPart))))/12)-
                    intPart((3*(intPart((y+4900+intPart(mPart))/100)))/4)+d-32075;

            double l = jd-1948440+10632;
            double n = intPart((l-1)/10631);
            l = l-10631*n+354;
            double j = (intPart((10985-l)/5316))*(intPart((50*l)/17719))+(intPart(l/5670))*(intPart((43*l)/15238));
            l = l-(intPart((30-j)/15))*(intPart((17719*j)/50))-(intPart(j/16))*(intPart((15238*j)/43))+29;

            m = (double)intPart((24*l)/709);
            d = (double)l-intPart((709*m)/24);
            y = (double)30*n+j-30;

            /*
             *
			 untuk menghitung tahun jawa Be, alip, ehe dsb...
			 double yj = y;//+512; Tahun jawa = Tahun Hijriyah + 512
			 double i = yj;
			 double yn=0.;
			 if (i >= 8) {
			 while (i > 7){
			 i = i - 8;
			 yn = i;
			 }
			 } else {
			 yn = i;
			 }
			 */

            if(julian<=1948439) y--;
        }
        return new String[] {
                harimasehi[sDay-1],                     //hari Masehi   0
                bulanmasehi[mMonth],                    //Bulan Masehi  1
                String.valueOf(mDay),                   //Tgl masehi    2
                String.valueOf(mYear),                  //Thn masehi    3
                HariPasaran(mYear, mMonth, mDay),       //nama pasaran  4
                String.valueOf((int)d),                 //Tanggal Jawa  5
                bulanjawa[(int)m-1],                    //Bulan Jawa    6
                String.valueOf((int)y),                 //Tahun Jawa    7
                String.valueOf((int)startDayofMonth)    //Awal hari     8
        };
    }

    String HariPasaran(int year, int month, int day){
        String[] pasaran = new String[]{
                "Pahing", "Pon", "Wage","kliwon","Legi"
        };

        Calendar tglInit = Calendar.getInstance();
        tglInit.set(1900, 12, 1);
        Calendar tglDicari = Calendar.getInstance();
        tglDicari.set(year, month, day);

        long miliday = 24 * 60 * 60 * 1000;

        long tglDicariMilis = tglDicari.getTimeInMillis();
        long tglInitMilis = tglInit.getTimeInMillis();
        long selisih =  (tglDicariMilis-tglInitMilis)/miliday;
        long hasil = selisih % 5;
        return String.valueOf(pasaran[(int)hasil]);
    }
}
