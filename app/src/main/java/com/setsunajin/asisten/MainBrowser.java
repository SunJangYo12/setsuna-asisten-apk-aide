package com.setsunajin.asisten;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;
import com.setsunajin.asisten.memori.SharedMemori;

import java.io.File;

public class MainBrowser extends Activity {

    /*static {
        System.loadLibrary("main");
    }*/
	private String lastMem;
	private String lastMemError;
   /* private native String xfilefb(String xpath, String xnama);
    private native void filefb(String xpath, String xnama);
    private native int filefbjum();
    private native void filefbDECODING(String xread, String xwrite);
    private native String filefbtes(String inku);
*/
    private EditText edHasil;
    private EditText edUrl;
    private Button btnSubmit;
    private String docPath = "";
    private String surl = "https://sunjangyo12.000webhostapp.com/login.php/";

    public static String outText = "";
    public static final String DEFAULT_URL = "http://localhost:8080";
    public static final int FILE_CHOOSER_RESULT = 0x01;
    private boolean prosesWeb = true;
    private static final String tag = "MainBrowser";
    private static final int DELAY = 1, ADD_DELAY = 2, CLEAR_DELAY = 3;
    private static final long DISPLAY_TIME = 4000L;
    private static final String GET_HTML = "GET_HTML";
    private static int filefbcounter = 0;
    private String[] filefbcounterlist;
    private static String url = "http://free.facebook.com";
    private String[] xsplit;
    private String titlePage = "";
    private String dataPost = "";
    private String filefbmetode = "";
    private String filefbpath = "";
    private String filefbnama = "";
    private String filefbsize = "";
    private int filefbjumlah = 0;
    private boolean savepage = false;
    private boolean uploadAuto = false;
    private boolean dataUrlError = false;
    private boolean runDecodingInbox = false;
    private boolean runDecodingPost = false;
    private boolean runfilefbpost = false;
    private boolean runfilefbupload = false;
    private boolean runCekfilefb = false;
    private WebView webView;
    private ProgressBar urlLoading;
    private ImageView favicon;
    private TextView htmlTitle;
    private RelativeLayout webTitlePanel;
    private AutoCompleteTextView gotoUrl;
    private boolean isLoading = false;
    private InputMethodManager imm;
    private ValueCallback<Uri> mFileChooserCallback;
    private ClipboardManager clip;
    private SharedMemori shMemori;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        docPath = new MainActivity().homePath+"/htdocs";
        File fmkdir = new File(docPath);
        if (!fmkdir.exists()) {
            fmkdir.mkdir();
        }

        shMemori = new SharedMemori(this);
        WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());
        clip = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        gotoUrl = (AutoCompleteTextView) findViewById(R.id.browser_goto_url);
        urlLoading = (ProgressBar) findViewById(R.id.browser_progressBar_url_loading);
        favicon = (ImageView) findViewById(R.id.browser_favicon);
        htmlTitle = (TextView) findViewById(R.id.browser_html_title);
        webTitlePanel = (RelativeLayout) findViewById(R.id.browser_webTitlePanel);
        webView = (WebView) findViewById(R.id.browser_web_view);

        webView.getSettings().setJavaScriptEnabled(true); // включаем поддержку JavaScript
        webView.addJavascriptInterface(new MyJavascriptInterface(), GET_HTML);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //webView.getSettings().setPluginsEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.setWebChromeClient(new MyWebChromeClient());
        webView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Log.d(tag, "onClick");
                if (h.hasMessages(DELAY)) {
                    h.sendEmptyMessage(ADD_DELAY);
                    return false;
                }
                webTitlePanel.setVisibility(View.VISIBLE);
                h.sendEmptyMessageDelayed(DELAY, DISPLAY_TIME);
                return false;
            }
        });
        webView.loadUrl("file:///sdcard/log_asisten.txt");

        gotoUrl.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    webView.loadUrl(gotoUrl.getText().toString());
                    return true;
                }
                return false;
            }
        });

        try {
            if (!getIntent().getStringExtra("filefbpath").equals(""))
            {
                filefbpath = getIntent().getStringExtra("filefbpath");
                filefbnama = getIntent().getStringExtra("filefbnama");
                filefbsize = getIntent().getStringExtra("filefbsize");

                filefbnama = splitSpasi(filefbnama);
                filefbpath = splitSpasi(filefbpath);

                FilefbTask task = new FilefbTask();
                task.metode = 0;
                task.applicationContext = MainBrowser.this;
                task.execute(new String[] { filefbpath, filefbnama });
            }
        }catch(Exception e) {}

    }

    public void toastText(Context context, String data, int warna, int letak)
    {
        LinearLayout layout = new LinearLayout(context);
        TextView text = new TextView(context);
        text.setText(data);
        text.setTextColor(Color.BLACK);
        text.setTextSize(13);
        text.setGravity(Gravity.BOTTOM);
        layout.addView(text);

        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(letak, 0, 0);
        toast.setView(text);
        toast.setView(layout);

        View toastView = toast.getView();
        toastView.setBackgroundColor(warna);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
    }

    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
            view.loadUrl(url);
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap fav) {
            if (imm != null) {
                imm.hideSoftInputFromWindow(gotoUrl.getWindowToken(), 0);
            }
            urlLoading.setVisibility(View.VISIBLE);
            gotoUrl.setText(url);
            htmlTitle.setText(url);
            favicon.setImageBitmap(fav);
            isLoading = true;
            MainBrowser.this.url = url;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            urlLoading.setVisibility(View.GONE);
            isLoading = false;

            if (savepage) {
                savepage = false;
                if (titlePage.equals("")) {
                    titlePage = url;
                }
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainBrowser.this);
                alertDialog.setTitle("Save as");

                final EditText input = new EditText(MainBrowser.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                input.setLayoutParams(lp);
                input.setText("/sdcard/"+titlePage+".mht");
                input.setTextColor(Color.BLACK);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Toast.makeText(MainBrowser.this, input.getText().toString(), Toast.LENGTH_LONG).show();
                        webView.saveWebArchive(input.getText().toString());
                    }
                });
                alertDialog.show();
            }

            if (runfilefbpost)
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainBrowser.this);
                alertDialog.setTitle("Wizard");
                alertDialog.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        runfilefbpost = false;
                        runfilefbupload = true;
                        webView.loadUrl(MainBrowser.this.url);
                    }
                });

                if (filefbmetode.equals("inbox"))
                {
                    alertDialog.setMessage("Arahkan url pada ID inbox yang dijadikan target penyimpanan\n\nNote: Alert ini akan muncul jika reload page");
                    alertDialog.show();
                }
                else {
                    alertDialog.setMessage("Arahkan url pada Postingan yang baru dibuat\n\nNote: Alert ini akan muncul jika berada dihalaman yg baru dibuat");
                    dataPost = "#FBDRIVE\\r\\rFilename="+filefbnama+"\\r\\rPathname="+filefbpath+"\\r\\rFilesize="+filefbsize+"\\r\\rEngineBuild=Ai-androidv1.3";
                    webView.loadUrl("javascript:document.forms[1].xc_message.value='"+dataPost+"'");

                    xsplit = url.split("story.php");
                    try {
                        if (!xsplit[1].equals(""))
                        {
                            alertDialog.show();
                        }
                    }catch(Exception e) {}
                }
            }
            if (runfilefbupload && !isLoading)
            {
                dataPost = new MainFileManager().notNexecuter("cat "+docPath+"/tempku.base64."+filefbcounter+".file");

                xsplit = url.split("error");
                try {
                    url = xsplit[1]+"\n\nJika muncul pesan merah berisi karakter melebihi 8.000 atau yang lain tandanya file lu terlalu besar, jadi terdeteksi SPAM oleh facebook solusinya coba coment menggunakan facebook lite original";
                    dataUrlError = true;
                }
                catch(Exception e){
                    dataUrlError = false;
                }

                if (dataPost.equals("") || dataUrlError)
                {
                    dataUrlError = false;
                    runfilefbupload = false;

                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainBrowser.this);
                    alertDialog.setTitle("Wizard");

                    if (dataPost.equals(""))
                        alertDialog.setMessage("Complete\n\nSemua Thread selesai, klik menu cek untuk memastikan semua file terupload");
                    else
                        alertDialog.setMessage(url);

                    alertDialog.show();
                }
                else {
                    dataPost = "-title-"+filefbnama+"-title--counter-"+filefbcounter+"-counter-content-"+dataPost+"-content-";

                    if (filefbmetode.equals("inbox"))
                    {
                        webView.loadUrl("javascript:document.forms[1].body.value='"+dataPost+"'");
                        webView.loadUrl("javascript:document.forms[1].submit()");

                        xsplit = url.split("send_success");
                        try {
                            if (xsplit[1].equals("&_rdr"))
                            {
                                toastText(MainBrowser.this, "______ PROCESS ______\n\nCounter:"+filefbcounter+"/"+filefbjumlah+"\n\n______________________", Color.YELLOW, Gravity.CENTER);
                                filefbcounter++;
                            }
                        }catch(Exception e){}
                    }
                    else
                    {
                        if (uploadAuto)
                        {
                            try {Thread.sleep(500); }catch(Exception e) {}
                            webView.loadUrl("javascript:document.forms[0].comment_text.value='"+dataPost+"'");
                            webView.loadUrl("javascript:document.forms[0].submit()");
                            toastText(MainBrowser.this, "______ PROCESS ______\n\nCounter:"+filefbcounter+"/"+filefbjumlah+"\n\n______________________", Color.YELLOW, Gravity.CENTER);
                            filefbcounter++;
                        }
                        else{
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainBrowser.this);
                            alertDialog.setTitle("Upload = "+filefbcounter+"/"+filefbjumlah);
                            alertDialog.setMessage("Counter ke satu bisanya tidak terkirim otomatis jadi gunakan ini untuk mengirim manual");

                            alertDialog.setPositiveButton("Auto", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    uploadAuto = true;
                                    webView.loadUrl("javascript:document.forms[0].comment_text.value='"+dataPost+"'");
                                    webView.loadUrl("javascript:document.forms[0].submit()");

                                    toastText(MainBrowser.this, "______ PROCESS ______\n\nCounter:"+filefbcounter+"/"+filefbjumlah+"\n\n______________________", Color.YELLOW, Gravity.CENTER);
                                    filefbcounter++;
                                }
                            });
                            alertDialog.setNegativeButton("Manual", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    webView.loadUrl("javascript:document.forms[0].comment_text.value='"+dataPost+"'");
                                    webView.loadUrl("javascript:document.forms[0].submit()");

                                    toastText(MainBrowser.this, "______ PROCESS ______\n\nCounter:"+filefbcounter+"/"+filefbjumlah+"\n\n______________________", Color.YELLOW, Gravity.CENTER);
                                    filefbcounter++;
                                }
                            });
                            alertDialog.show();
                        }
                    }
                }
            }
            if (runDecodingInbox) {
                Log.i("zzzz", "aaaaaaaaaaaaaaaaa: "+zz);
                webView.loadUrl("javascript:window." + GET_HTML + ".getProses(document.getElementsByTagName('html')[0].innerHTML);");
                zz++;
            }
            if (runDecodingPost) {
                webView.loadUrl("javascript:window." + GET_HTML + ".getDecodingFBDRIVE(document.getElementsByTagName('body')[0].innerText);");
            }
            if (runCekfilefb) {
                webView.loadUrl("javascript:window." + GET_HTML + ".getCounterFB(document.getElementsByTagName('body')[0].innerText);");
            }
        }
    }

    int zz = 0;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //super.onActivityResult(requestCode, resultCode, intent);
        //Log.d(tag, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FILE_CHOOSER_RESULT) {
            if (mFileChooserCallback == null) {
                //Log.d(tag, "callback null");
                return;
            }
            Uri result = (intent == null || resultCode != RESULT_OK) ? null : intent.getData();
            mFileChooserCallback.onReceiveValue(result);
            mFileChooserCallback = null;
            //Log.d(tag, "callback result: " + result);
        }
    }

    private class MyWebChromeClient extends WebChromeClient
    {
        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            favicon.setImageBitmap(icon);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            titlePage = title;
            htmlTitle.setText(title);
        }

        //@Override
        public void openFileChooser(ValueCallback<Uri> fileChooserCallback, String acceptType, String capture) {
            // Log.d(tag, "openFileChooser with: acceptType = " + acceptType + " capture = " + capture);
            mFileChooserCallback = fileChooserCallback;
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT, Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath()), MainBrowser.this, MainFileManager.class), FILE_CHOOSER_RESULT);
        }
    }
    private Handler h = new Handler() {
        private long up = 0;

        @Override
        public void handleMessage(android.os.Message message) {
            //Log.d(tag, "handleMessage");
            switch (message.what) {
                case DELAY:
                    if (up != 0) {
                        sendEmptyMessageDelayed(DELAY, DISPLAY_TIME - (System.currentTimeMillis() - up));
                        up = 0;
                        return;
                    }
                    webTitlePanel.setVisibility(View.GONE);
                    break;
                case ADD_DELAY:
                    up = System.currentTimeMillis();
                    break;
                case CLEAR_DELAY:
                    up = 0;
                    removeMessages(DELAY);
                    webTitlePanel.setVisibility(View.GONE);
            }
        }
    };

    public String getInfoFBDRIVE(String data, String index)
    {
        String patern = "";
        String out = "kosong";

        if (index.equals("nama"))
            patern = "Filename=";
        if (index.equals("size"))
            patern = "Filesize=";
        if (index.equals("engine"))
            patern = "EngineBuild=";

        if (!patern.equals("")) {
            String[] name = data.split(patern);
            String[] line = name[1].split("\n");
            return line[0];
        }
        else {
            return out;
        }
    }
    private String splitSpasi(String instring)
    {
        String sdata = instring+".split";
        StringBuffer sout = new StringBuffer();
        String[] spnama = sdata.split(" ");

        try {
            for (int i=0; i<spnama.length; i++)
            {
                sout.append(spnama[i]+"\\ ");
            }
        }catch(Exception e){}
        String[] sptnama = sout.toString().split(".split");

        return sptnama[0];
    }
    public static int[] sortbable(int[] A) {
        int MAX = A.length;
        int j, k;
        int temp;
        for (j=0; j<MAX-1; j++){
            for (k=MAX-1; k>=(j+1); k--){
                if (A[k] < A[k-1]) {
                    temp = A[k];
                    A[k] = A[k-1];
                    A[k-1] = temp;
                }
            }
        }
        return A;
    }

    private class MyJavascriptInterface
    {
        public void getCounterFB(String text) {
            lastMem = shMemori.getStrSharedMemori("cekfilefb");
            lastMemError = shMemori.getStrSharedMemori("cekfilefberror");

            try {
                String[] line = text.split("\n");
                StringBuffer scek = new StringBuffer();

                for (int i=0; i<line.length; i++)
                {
                    if (line[i].length() > 100)
                    {
                        filefbcounterlist = line[i].split("-counter-");
                        scek.append(filefbcounterlist[1]+"\n");
                    }
                }
                shMemori.setStrSharedMemori("cekfilefb", lastMem+scek.toString());
            }catch(Exception e) {
                Log.i("mikusan", ""+e);
            }
            try {
                String[] testComment = text.split("Lihat komentar sebelumnya…");
                if (!testComment[1].equals(""))
                {
                    runCekfilefb = true;
                    Toast.makeText(MainBrowser.this, "Klik komentar sebelumnya… untuk menghitung data", Toast.LENGTH_LONG).show();
                }
            }catch(Exception e) {
                runCekfilefb = false;
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainBrowser.this);
                alertDialog.setTitle("Cek");
                alertDialog.setMessage("Terupload: \n\n"+lastMem);

                alertDialog.setPositiveButton("Sort", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        StringBuffer sbout = new StringBuffer();
                        String[] datacek = lastMem.split("\n");
                        int[] idata = new int[datacek.length];

                        for (int i=0; i<datacek.length; i++)
                        {
                            idata[i] = Integer.parseInt(datacek[i]);
                        }
                        for (int i=0; i<sortbable(idata).length; i++)
                        {
                            if (sortbable(idata)[i] != i){
                                sbout.append(""+sortbable(idata)[i]+" -> "+i+"\n");
                                shMemori.setStrSharedMemori("cekfilefberror", lastMemError+" "+i);
                            }
                            else {
                                sbout.append(""+sortbable(idata)[i]+"\n");
                            }
                        }
                        alertDialog.setMessage("Terupload sort: \n\n"+sbout.toString());
                        alertDialog.show();
                    }
                });
                alertDialog.setNegativeButton("Resend error", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String[] dataerr = lastMemError.split(" ");

                        for (int i=0; i<dataerr.length; i++)
                        {
                            dataPost = new MainFileManager().notNexecuter("cat "+docPath+"/tempku.base64."+i+".file");
                            dataPost = "-title-"+filefbnama+"-title--counter-"+i+"-counter-content-"+dataPost+"-content-";

                            webView.loadUrl("javascript:document.forms[0].comment_text.value='"+dataPost+"'");
                            webView.loadUrl("javascript:document.forms[0].submit()");
                        }
                    }
                });
                alertDialog.show();
            }
        }
        public void getDecodingFBDRIVE(String html) {
            try {
                String[] testComment = html.split("Lihat komentar sebelumnya…");
                MainFileManager fileutils = new MainFileManager();

                try {
                    if (!testComment[1].equals(""))
                    {
                        String fpath = docPath;
                        fileutils.saveCode(html, "utf-8", fpath+"/base64");

                        String nama = getInfoFBDRIVE(fileutils.readFile(fpath+"/base64"), "nama");
                        Log.i("mikusan", nama);

                        String[] line = fileutils.readFile(fpath+"/base64").split("\n");
                        String[] counter = null;
                        String[] content = null;

                        for (int i=0; i<line.length; i++)
                        {
                            if (line[i].length() > 100)
                            {
                                counter = line[i].split("-counter-");
                                content = line[i].split("-content-");

                                fileutils.saveCode(content[1], "utf-8", fpath+"/"+nama+".file."+counter[1]);
                            }
                        }
                        Toast.makeText(MainBrowser.this, "Tekan pesan sebelumnya untuk proses", Toast.LENGTH_LONG).show();
                    }
                }catch(Exception e) {
                    runDecodingPost = false;
                    final String fpath = docPath;
                    fileutils.saveCode(html, "utf-8", fpath+"/base64");

                    final String nama = getInfoFBDRIVE(fileutils.readFile(fpath+"/base64"), "nama");
                    Log.i("mikusan", nama);

                    String[] line = fileutils.readFile(fpath+"/base64").split("\n");
                    String[] counter = null;
                    String[] content = null;

                    for (int i=0; i<line.length; i++)
                    {
                        if (line[i].length() > 100)
                        {
                            counter = line[i].split("-counter-");
                            content = line[i].split("-content-");

                            fileutils.saveCode(content[1], "utf-8", fpath+"/"+nama+".file."+counter[1]);
                        }
                    }
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainBrowser.this);
                    alertDialog.setTitle("Wizard");
                    alertDialog.setMessage("File sudah lengkap klik untuk proses decoding");
                    alertDialog.setCancelable(false);

                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            FilefbTask task = new FilefbTask();
                            task.metode = 1;
                            task.applicationContext = MainBrowser.this;
                            task.execute(new String[] { fpath, nama });
                        }
                    });
                    alertDialog.show();
                }
            }
            catch(Exception e) {
                Toast.makeText(MainBrowser.this, "Gagal save file decoding karena: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        public void getHtml(String html) {
            /*Intent intent = new Intent(Intent.ACTION_VIEW, null, MainBrowser.this, MainEditor.class);
            intent.putExtra(MainEditor.CODE, html);
            intent.putExtra(MainEditor.CODE_TYPE, MainEditor.HTML);
            startActivityForResult(intent, MainEditor.REQUEST_VIEW_SOURCE);*/
        }
        public void getProses(String text) {
            String[] prev1 = text.split("see_older");
            String[] prev2 = prev1[1].split("refid=12");
            String[] prev3 = prev2[0].split("/messages");

            String prev = "https://free.facebook.com/messages"+prev3[1]+"refid=12";

            Log.i("zzzz", ">>>"+prev);
            webView.loadUrl(prev);

            /*prosesWeb = true;
            webkitFilefbReceive(MainBrowser.this, prev);

            SharedPreferences.Editor editor = settings.edit();
            editor.putString("decodingInbox", prev);
            editor.commit();*/
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.FIRST, 1, 1, "Get HTML").setIcon(R.drawable.ic_menu_html);
        menu.add(Menu.FIRST, 2, 1, "Get TEXT").setIcon(R.drawable.ic_menu_cookies);
        menu.add(Menu.FIRST, 3, 1, "Get COOKIES").setIcon(R.drawable.ic_menu_cookies);
        menu.add(Menu.FIRST, 4, 1, "Brute FORM").setIcon(R.drawable.ic_menu_html);
        menu.add(Menu.FIRST, 5, 1, "FBDRIVE facebook").setIcon(R.drawable.ic_menu_html);
        menu.add(Menu.FIRST, 6, 1, "Halaman").setIcon(R.drawable.ic_menu_html);
        menu.add(Menu.FIRST, 7, 1, "Subdomner indoxploit").setIcon(R.drawable.ic_menu_html);
        menu.add(Menu.FIRST, 8, 1, "Exit").setIcon(R.drawable.ic_menu_html);
        menu.add(Menu.FIRST, 9, 1, "Remote shortcut").setIcon(R.drawable.ic_menu_html);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == 1)
        {
            webView.loadUrl("javascript:window." + GET_HTML + ".getHtml('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
        }
        if (item.getItemId() == 2)
        {
            webView.loadUrl("javascript:window." + GET_HTML + ".getHtml(document.getElementsByTagName('body')[0].innerText);");
        }
        if (item.getItemId() == 3)
        {
            /*CookieManager cman = CookieManager.getInstance();
            Intent intent = new Intent(Intent.ACTION_VIEW, null, this, MainEditor.class);
            intent.putExtra(MainEditor.CODE, cman.getCookie(url));
            intent.putExtra(MainEditor.CODE_TYPE, MainEditor.NONE);
            startActivityForResult(intent, MainEditor.REQUEST_VIEW_SOURCE);*/
        }
        if (item.getItemId() == 4)
        {
        }
        if (item.getItemId() == 5)
        {
            String[] aksi ={"1. Send File", "2. Receive File", "3. Cek data", "4. Hapus cache"};
            AlertDialog.Builder builderIndex = new AlertDialog.Builder(MainBrowser.this);
            builderIndex.setTitle("Filemanager using facebook");
            builderIndex.setItems(aksi, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0){
                        Intent intent = new Intent(MainBrowser.this, MainFileManager.class);
                        intent.putExtra("aksi", "filefb");
                        MainBrowser.this.startActivity(intent);
                    }
                    else if (item == 1) {
                        String[] aksi ={"1. Inbox DECODING", "2. Postingan DECODING"};
                        AlertDialog.Builder builderIndex = new AlertDialog.Builder(MainBrowser.this);
                        builderIndex.setTitle("Pilih Metode?");
                        builderIndex.setItems(aksi, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                if (item == 0)
                                {
                                    runDecodingInbox = true;
                                    //webView.loadUrl(settings.getString("decodingInbox", ""));
                                    webView.loadUrl("javascript:window." + GET_HTML + ".getProses(document.getElementsByTagName('html')[0].innerHTML);");
                                }
                                else if (item == 1)
                                {
                                    String[] testSiki = url.split("story.php");
                                    try {
                                        if (!testSiki[1].equals(""))
                                        {
                                            Log.i("mikusan", testSiki[1]);
                                            runDecodingPost = true;
                                            webView.loadUrl("javascript:window." + GET_HTML + ".getDecodingFBDRIVE(document.getElementsByTagName('body')[0].innerText);");
                                        }
                                    }catch(Exception e) {
                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainBrowser.this);
                                        alertDialog.setTitle("Alert!");
                                        alertDialog.setMessage("Arahkan page ke story postingan yang memiliki #FBDRIVE");

                                        alertDialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                            }
                                        });
                                        alertDialog.show();
                                    }
                                }

                            }
                        });
                        builderIndex.create().show();
                    }
                    else if (item == 2) {
                        runCekfilefb = true;
                        shMemori.setStrSharedMemori("cekfilefb", "");
                        shMemori.setStrSharedMemori("cekfilefberror", "");
                        webView.loadUrl("javascript:window." + GET_HTML + ".getCounterFB(document.getElementsByTagName('body')[0].innerText);");
                    }
                    else if (item == 3) {
                        //filefb("shell", "rm "+docPath+"/*.file");
                        //filefb("shell", "rm "+docPath+"/*.base64");
                       // filefb("shell", "rm "+docPath+"/tempku");
                        filefbcounter = 0;
                    }
                }
            });
            builderIndex.create().show();
        }
        if (item.getItemId() == 6) {
            String[] aksi ={"1. Save page", "2. Open page"};
            AlertDialog.Builder builderIndex = new AlertDialog.Builder(MainBrowser.this);
            builderIndex.setTitle("Manager halaman");
            builderIndex.setItems(aksi, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0)
                    {
                        savepage = true;
                        webView.loadUrl(url);
                    }
                    else if (item == 1) {
                        Intent intent = new Intent(MainBrowser.this, MainFileManager.class);
                        intent.putExtra("aksi", "openpage");
                        MainBrowser.this.startActivity(intent);
                    }
                }
            });
            builderIndex.create().show();
        }
        if (item.getItemId() == 7) {
            MainBrowser.this.finish();
            startActivity(new Intent(MainBrowser.this, MainSubdomner.class));
        }
        if (item.getItemId() == 8) {
            finish();
        }
        if (item.getItemId() == 9) {
            Toast.makeText(MainBrowser.this, "Remote audacious player", Toast.LENGTH_LONG).show();
            MainBrowser.this.startActivity(new Intent(MainBrowser.this, MainBrowserNotif.class));
        }
        return true;
    }

    @Override
    public void onBackPressed(){
        if (webView.canGoBack()) {
            webView.goBack();
        }
        else {
            finish();
        }
    }

    private class FilefbTask extends AsyncTask<String, Void, String>
    {
        private ProgressDialog dialog;
        private String outTemp = "";
        protected Context applicationContext;
        protected int metode;

        @Override
        protected void onPreExecute() {
            if (metode == 0)
                this.dialog = ProgressDialog.show(applicationContext, "Encoding File", "Please Wait...", true);
            if (metode == 1)
                this.dialog = ProgressDialog.show(applicationContext, "Decoding File", "Please Wait...", true);
        }

        @Override
        protected String doInBackground(String... urls) {
            if (metode == 0) {
               // filefb("shell", "cp "+urls[0]+"/"+urls[1]+" "+docPath+"/tempku");

               // filefb(docPath, "tempku");
                //filefbjumlah = filefbjum();
            }
            else if (metode == 1) {
                // urls[0] = fpath
                // urls[1] = nama

                String[] deco = new MainFileManager().executer("ls "+urls[0]).split("\n");
                String[] format = null;
                for (int i=0; i<deco.length; i++)
                {
                    format = deco[i].split("file");
                    try {
                        if (!format[1].equals(""))
                        {
                            Log.i("mikusan", deco[i]);
                            //filefbDECODING(urls[0]+"/"+deco[i], urls[0]+"/"+urls[1]+".base64");
                            //filefb("shell", "rm "+urls[0]+"/"+deco[i]);
                        }
                    }catch(Exception e){}
                }
               // filefb("shell", "cat "+urls[0]+"/"+urls[1]+".base64 | base64 -d > "+urls[0]+"/"+urls[1]);
               // filefb("shell", "rm "+urls[0]+"/base64");
                //filefb("shell", "rm "+urls[0]+"/*.base64");
                outTemp = urls[0]+"/"+urls[1];
            }
            return "load";
        }
        @Override
        protected void onPostExecute(String result) {
            this.dialog.cancel();
            if (metode == 0) {
                String[] aksi ={"1. Inbox URL", "2. Postingan URL"};
                AlertDialog.Builder builderIndex = new AlertDialog.Builder(MainBrowser.this);
                builderIndex.setTitle("Pilih Metode?");
                builderIndex.setCancelable(false);
                builderIndex.setItems(aksi, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        if (item == 0)
                        {
                            runfilefbpost = true;
                            filefbmetode = "inbox";
                            webView.loadUrl(url);
                        }
                        else if (item == 1)
                        {
                            runfilefbpost = true;
                            filefbmetode = "postingan";
                            webView.loadUrl(url);
                        }
                    }
                });
                builderIndex.setPositiveButton("Close",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       // filefb("shell", "rm "+docPath+"/tempku");
                        //filefb("shell", "rm "+docPath+"/*.base64");
                      //  filefb("shell", "rm "+docPath+"/*.file");
                    }
                });
                builderIndex.create().show();
            }
            if (metode == 1) {
                Toast.makeText(MainBrowser.this, "Success file sudah jadi: "+outTemp, Toast.LENGTH_LONG).show();
            }

        }
    }
}
