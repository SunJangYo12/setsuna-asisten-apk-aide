package com.setsunajin.asisten;

import android.app.*;
import android.os.*;
import android.text.*;
import android.widget.*;
import java.net.*;
import java.util.*;
import java.io.*;

import android.content.res.*;
import android.view.*;
import android.view.inputmethod.*;
import android.graphics.drawable.*;
import android.graphics.*;
import android.widget.AdapterView.*;

import android.app.Activity;

import javax.net.ssl.*;
import org.json.*;

public class MainSubdomner extends Activity
{
    private ScrollView tscroll;
    private TextView console;
    private SendTask mTask;
    private EditText dom;
    private RadioButton okres;
    private RadioButton otres;
    private ListView riwayat;
    private String layout = "main";

    public void banner() {
        console.setText("SubDomner 1.1 (default, April 1 2020, 21:15:35)\n");
        console.append(">>>> Discover Subdomains via Dictionary Attack <<<<\n");
        console.append("> \n");
    }
    public void setMainLayout() {
        layout = "main";
        setContentView(R.layout.activity_subdomner);
        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch(Exception e) {
            Toast.makeText(getApplicationContext(), "Google Play Services is not available!", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.subdomner_starttask).setOnClickListener(starttask);
        findViewById(R.id.subdomner_history).setOnClickListener(history);
        console = (TextView) findViewById(R.id.subdomner_console);
        tscroll = (ScrollView) findViewById(R.id.subdomner_tscroll);
        dom = (EditText) findViewById(R.id.subdomner_dom);
        File subdomner = new File(Environment.getExternalStorageDirectory()+"/SubDomner");
        if(subdomner.exists()) {
            if(subdomner.isFile()) {
                subdomner.delete();
                subdomner.mkdir();
            }
        } else {
            subdomner.mkdir();
        }
        banner();
    }
    public void setHistoryLayout() {
        File subdomner = new File(Environment.getExternalStorageDirectory()+"/SubDomner");
        String[] hx = subdomner.list();
        if(hx.length == 0) {
            setMainLayout();
            new AlertDialog.Builder(MainSubdomner.this)
                    .setMessage("No history yet")
                    .setPositiveButton("OK", null)
                    .show()
                    .getWindow()
                    .setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFEE00")));
        } else {
            layout = "history";
            setContentView(R.layout.activity_subdomner_history);
            findViewById(R.id.subdomner_historyBack).setOnClickListener(historyBack);
            final List<String> rx = new ArrayList<String>();
            ListView historyList = (ListView) findViewById(R.id.subdomner_historyList);
            for(int x = 0;x < hx.length;x++) {
                if(!hx[x].startsWith(".")) {
                    rx.add(hx[x]);
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainSubdomner.this, android.R.layout.simple_list_item_1, rx);
            historyList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            historyList.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    List<String> content_ = new ArrayList<String>();
                    try {
                        FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory()+"/SubDomner/.cache");
                        out.write(rx.get(position).getBytes());
                        out.close();
                        Scanner s = new Scanner(new File(Environment.getExternalStorageDirectory()+"/SubDomner/"+rx.get(position)), "UTF-8");
                        s.useDelimiter("$^");
                        String[] content = s.next().toString().split("\n");
                        for(int x = 0;x < content.length;x++) {
                            content_.add(content[x].split(" ")[1]);
                        }
                        setContentView(R.layout.activity_subdomner_status);
                        riwayat = (ListView) findViewById(R.id.subdomner_riwayat);
                        ArrayAdapter<String> adapter_ = new ArrayAdapter<String>(MainSubdomner.this, android.R.layout.simple_list_item_1, content_);
                        riwayat.setAdapter(adapter_);
                        adapter_.notifyDataSetChanged();
                        okres = (RadioButton) findViewById(R.id.subdomner_okresponse);
                        otres = (RadioButton) findViewById(R.id.subdomner_otherresponse);
                        findViewById(R.id.subdomner_okresponse).setOnClickListener(okresponse);
                        findViewById(R.id.subdomner_otherresponse).setOnClickListener(otherresponse);
                        findViewById(R.id.subdomner_statusBack).setOnClickListener(statusBack);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
    public class SendTask extends AsyncTask<String, String, String>
    {
        @Override
        protected void onProgressUpdate(String[] values)
        {
            super.onProgressUpdate(values);
            console.append(Html.fromHtml(values[0]));
            tscroll.post(new Runnable() {
                @Override
                public void run() {
                    tscroll.fullScroll(View.FOCUS_DOWN);
                }
            });
        }
        @Override
        protected String doInBackground(String[] list)
        {
            String[] wordlist = list[0].split(",");
            try {
                publishProgress(String.format("<font color='yellow'>> start scan: %s</font><br>", list[1]));
                FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory()+"/SubDomner/"+list[1]);
                //test connection to indoxploit api (if return ok = 200; use api service)
                publishProgress("<font color='yellow'>[+] test connection </font><font color='green'>(api.indoxploit.or.id)</font><font color='yellow'>: </font>");
                HttpURLConnection idxApi = (HttpURLConnection) new URL("https://api.indoxploit.or.id/domain/"+list[1]).openConnection();
                idxApi.setConnectTimeout(800);
                idxApi.setRequestMethod("GET");
                idxApi.setDoOutput(true);
                if(idxApi.getResponseCode() == 200) {
                    publishProgress("<font color='green'>200</font><br>");
                    InputStream is = idxApi.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader br = new BufferedReader(isr);
                    String inputLine, body = "";
                    while((inputLine = br.readLine()) != null) {
                        body+=inputLine+"\n";
                    }
                    JSONObject jsonObj = new JSONObject(body);
                    JSONArray jsonArr = jsonObj.getJSONObject("data").getJSONArray("subdomains");
                    for(int x = 0;x < jsonArr.length();x++) {
                        publishProgress("<font color='green'>[+]</font> <font color='yellow'>"+jsonArr.get(x)+"</font><br>");
                        out.write(String.format("200 %s\n", jsonArr.get(x)).getBytes());
                    }
                } else {
                    publishProgress("<font color='red'>"+idxApi.getResponseCode()+"</font><br>");
                    for(int x = 0;x < wordlist.length;x++) {
                        try {
                            HttpURLConnection conn = (HttpURLConnection) new URL("http://"+wordlist[x]+"."+list[1]).openConnection();
                            conn.setConnectTimeout(800);
                            conn.setRequestMethod("GET");
                            if(conn.getResponseCode() == 200) {
                                publishProgress("<font color='green'>[+]</font> <font color='yellow'>"+wordlist[x]+"."+list[1]+"</font><br>");
                            } else {
                                publishProgress("<font color='red'>[-]</font> <font color='yellow'>"+wordlist[x]+"."+list[1]+"</font><br>");
                            }
                            out.write(String.format("%s %s.%s\n", conn.getResponseCode(), wordlist[x], list[1]).getBytes());
                            conn.disconnect();
                        } catch(Exception e) {
                            publishProgress("<font color='red'>[-]</font> <font color='yellow'>"+wordlist[x]+"."+list[1]+"</font><br>");
                            //publishProgress("<font color='red'>[!]</font> <font color='yellow'>"+e.toString()+"</font><br>");
                            out.write(String.format("0 %s.%s\n", wordlist[x], list[1]).getBytes());
                        }
                    }
                    out.close();
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result)
        {
            console.append(Html.fromHtml("<font color='red'>[!]</font> <font color='white'>Done ...</font><br>"));
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subdomner);
        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch(Exception e) {
            Toast.makeText(getApplicationContext(), "Google Play Services is not available!", Toast.LENGTH_SHORT).show();
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.parseColor("#FFEE00"));
        }

        findViewById(R.id.subdomner_starttask).setOnClickListener(starttask);
        findViewById(R.id.subdomner_history).setOnClickListener(history);
        console = (TextView) findViewById(R.id.subdomner_console);
        tscroll = (ScrollView) findViewById(R.id.subdomner_tscroll);
        dom = (EditText) findViewById(R.id.subdomner_dom);
        File subdomner = new File(Environment.getExternalStorageDirectory()+"/SubDomner");
        if(subdomner.exists()) {
            if(subdomner.isFile()) {
                subdomner.delete();
                subdomner.mkdir();
            }
        } else {
            subdomner.mkdir();
        }
        banner();
    }
    View.OnClickListener starttask = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                AssetManager assetManager = getAssets();
                InputStream inputLine = assetManager.open("small.list");
                int size = inputLine.available();
                byte[] buffer = new byte[size];
                inputLine.read(buffer);
                inputLine.close();
                String text = new String(buffer);
                dom.onEditorAction(EditorInfo.IME_ACTION_DONE);
                mTask = new SendTask();
                mTask.execute(text.replace("\n",",").replace("\n",""),dom.getText().toString());
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    };
    View.OnClickListener history = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            File subdomner = new File(Environment.getExternalStorageDirectory()+"/SubDomner");
            String[] hx = subdomner.list();
            if(hx.length == 0) {
                setMainLayout();
                new AlertDialog.Builder(MainSubdomner.this)
                        .setMessage("No history yet")
                        .setPositiveButton("OK", null)
                        .show()
                        .getWindow()
                        .setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFEE00")));
            } else {
                layout = "history";
                setContentView(R.layout.activity_subdomner_history);
                findViewById(R.id.subdomner_historyBack).setOnClickListener(historyBack);
                final List<String> rx = new ArrayList<String>();
                ListView historyList = (ListView) findViewById(R.id.subdomner_historyList);
                for(int x = 0;x < hx.length;x++) {
                    if(!hx[x].startsWith(".")) {
                        rx.add(hx[x]);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainSubdomner.this, android.R.layout.simple_list_item_1, rx);
                historyList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                historyList.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        List<String> content_ = new ArrayList<String>();
                        try {
                            FileOutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory()+"/SubDomner/.cache");
                            out.write(rx.get(position).getBytes());
                            out.close();
                            Scanner s = new Scanner(new File(Environment.getExternalStorageDirectory()+"/SubDomner/"+rx.get(position)), "UTF-8");
                            s.useDelimiter("$^");
                            String[] content = s.next().toString().split("\n");
                            for(int x = 0;x < content.length;x++) {
                                content_.add(content[x].split(" ")[1]);
                            }
                            setContentView(R.layout.activity_subdomner_status);
                            riwayat = (ListView) findViewById(R.id.subdomner_riwayat);
                            ArrayAdapter<String> adapter_ = new ArrayAdapter<String>(MainSubdomner.this, android.R.layout.simple_list_item_1, content_);
                            riwayat.setAdapter(adapter_);
                            adapter_.notifyDataSetChanged();
                            okres = (RadioButton) findViewById(R.id.subdomner_okresponse);
                            otres = (RadioButton) findViewById(R.id.subdomner_otherresponse);
                            findViewById(R.id.subdomner_okresponse).setOnClickListener(okresponse);
                            findViewById(R.id.subdomner_otherresponse).setOnClickListener(otherresponse);
                            findViewById(R.id.subdomner_statusBack).setOnClickListener(statusBack);
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    };
    View.OnClickListener historyBack = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setMainLayout();
        }
    };
    View.OnClickListener statusBack = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            setHistoryLayout();
        }
    };
    View.OnClickListener okresponse = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(okres.isChecked()) {
                if(otres.isChecked()) {
                    otres.setChecked(false);
                }
                try {
                    List<String> ls = new ArrayList<String>();
                    Scanner sc = new Scanner(new File(Environment.getExternalStorageDirectory()+"/SubDomner/.cache"), "UTF-8");
                    sc.useDelimiter("$^");
                    String _cache = sc.next().toString();
                    File filchk = new File(Environment.getExternalStorageDirectory()+"/SubDomner/"+_cache.trim());
                    if(filchk.exists()) {
                        if(filchk.isFile()) {
                            Scanner sr = new Scanner(filchk, "UTF-8");
                            sr.useDelimiter("$^");
                            String cache_ = sr.next().toString();
                            String[] _cache_ = cache_.split("\n");
                            for(int x = 0;x < _cache_.length;x++) {
                                if("200".equals(_cache_[x].split(" ")[0].trim())) {
                                    ls.add(String.format("%s %s",_cache_[x].split(" ")[1].trim(),_cache_[x].split(" ")[0].trim()));
                                }
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainSubdomner.this, android.R.layout.simple_list_item_1, ls);
                            riwayat.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            riwayat.setBackgroundColor(Color.parseColor("#00FF00"));
                        } else {
                            filchk.delete();
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    View.OnClickListener otherresponse = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(otres.isChecked()) {
                if(okres.isChecked()) {
                    okres.setChecked(false);
                }
                try {
                    List<String> ls = new ArrayList<String>();
                    Scanner sc = new Scanner(new File(Environment.getExternalStorageDirectory()+"/SubDomner/.cache"), "UTF-8");
                    sc.useDelimiter("$^");
                    String _cache = sc.next().toString();
                    File filchk = new File(Environment.getExternalStorageDirectory()+"/SubDomner/"+_cache.trim());
                    if(filchk.exists()) {
                        if(filchk.isFile()) {
                            Scanner sr = new Scanner(filchk, "UTF-8");
                            sr.useDelimiter("$^");
                            String cache_ = sr.next().toString();
                            String[] _cache_ = cache_.split("\n");
                            for(int x = 0;x < _cache_.length;x++) {
                                if(!"200".equals(_cache_[x].split(" ")[0].trim())) {
                                    ls.add(String.format("%s %s",_cache_[x].split(" ")[1].trim(),_cache_[x].split(" ")[0].trim()));
                                }
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainSubdomner.this, android.R.layout.simple_list_item_1, ls);
                            riwayat.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            riwayat.setBackgroundColor(Color.parseColor("#FF0F00"));
                        } else {
                            filchk.delete();
                        }
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_subdomner, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()) {
            case R.id.subdomner_menu_clear: dom.setText("");console.setText("");banner();
                break;
            case R.id.subdomner_menu_about:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainSubdomner.this);
                builder.setTitle("SubDomner");
                builder.setMessage("SubDomner is an app that scans an entire domain to find as many subdomains as possible. It strive to give you the most accurate results and might take some time during scanning\nHistory Path: /sdcard/SubDomner\n\nCopyright (C) 2020 by DedSecTL\n\nDedSecTL\nCvar1984\nCiKu370\nMr.TenSwapper07\namsitlab\n[M]izuno\n3RROR_TMX\nMr.K3N\nZetSec\nTroublemaker97\nL_Viole\nX14N23N6\nMR.R45K1N\nlord.zephyrus\n4cliba788\nmr0x100\nViruz\nMr_007\nITermSec\nMicroClone\nIdannovita.\nBlackHole Security.");
                builder.setPositiveButton("OK", null);
                AlertDialog dialog = builder.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FFEE00"));
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000000")));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FFEE00")));
                break;
        }
        return true;
    }
    @Override
    public void onBackPressed()
    {
        switch(layout) {
            case "main": finish();
                break;
            default: // nothing to do
        }
    }
}

