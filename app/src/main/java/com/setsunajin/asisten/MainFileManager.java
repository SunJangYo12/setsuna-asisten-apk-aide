package com.setsunajin.asisten;


import android.app.Activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class MainFileManager extends Activity implements AdapterView.OnItemClickListener
{
    public String aksiVar[];
    private Item it;
    private ListView listView;
    private TextView fullPath;
    private String path = "";
    private boolean folder;
    ArrayList<Item> items;
    private String currPath, prevPath;
    private Map<String, Integer> mapExt = new HashMap<String, Integer>();
    private boolean chooseFile = false;
    private boolean filefb = false;
    private boolean openpage = false;
    private String xsize = "";
    private AlphabeticComparator alphabeticComparator;
    public static final int CONF = 5;
    public static final int CSS = 4;
    public static final int JS = 3;
    public static final int PHP = 2;
    public static final int HTML = 1;
    public static final int NONE = 0;
    private Map<String, Integer> supportedFiles = new HashMap<String, Integer>() {
        {
            put(".php", PHP);
            put(".sh", PHP);
            put(".xml", PHP);
            put(".java", PHP);
            put(".c", PHP);
            put(".cpp", PHP);
            put(".js", PHP);
            put(".htm", PHP);
            put(".html", PHP);
            put(".css", PHP);
            put(".config", PHP);
            put(".conf", PHP);
            put(".cfg", PHP);
            put(".ini", PHP);
            put(".txt", PHP);
            //put(".json");
        }
    };
    private static String tmpEdt = "";
    private static String tmpCloneFie = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_filemanager);

            fullPath = (TextView) findViewById(R.id.filemanager_txt_path);
            listView = (ListView) findViewById(R.id.filemanager_list);

            listView.setOnItemClickListener(this);
            listView.setOnItemLongClickListener(getLongPressListener());

            initMapExt();
            alphabeticComparator = new AlphabeticComparator();

            if (currPath == null) {
                currPath = "/sdcard";
                readFolder(currPath);
            }
            prevPath = calcBackPath();
        }catch (Exception e) {
            Toast.makeText(this, "Check setting permission!: "+e, Toast.LENGTH_LONG).show();
        }

        try {
            if (getIntent().getStringExtra("aksi").equals("filefb") || getIntent().getStringExtra("aksi").equals("openpage"))
            {
                if (getIntent().getStringExtra("aksi").equals("filefb"))
                    filefb = true;
                else if (getIntent().getStringExtra("aksi").equals("openpage"))
                    openpage = true;

                Toast.makeText(this, "Select Filefb", Toast.LENGTH_LONG).show();
            }

        }catch(Exception e){}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.FIRST, 1, 1, "Home").setIcon(R.drawable.icon_css);
        menu.add(Menu.FIRST, 2, 1, "Custom").setIcon(R.drawable.icon_css);
        menu.add(Menu.FIRST, 3, 1, "Exit").setIcon(R.drawable.icon_css);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            currPath = new MainActivity().homePath;
            readFolder(currPath);
        }
        if (item.getItemId() == 2) {
            alertCustom(this);
        }
        if (item.getItemId() == 3) {
            MainFileManager.this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        prevPath = currPath;
        it = items.get(position);

        switch (it.getType()) {
            case 1:
                currPath = currPath + "/" + it.getHeader();// build URL
                readFolder(currPath);

                fullPath.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                       alertAksi(false, currPath + '/' + it.getHeader());
                    }
                });
                break;
            case 3:
                currPath = calcBackPath();
                readFolder(currPath);
                break;
            case 2:
                selectAction(it.getHeader());// build URL

                fullPath.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        alertAksi(false, currPath + '/' + it.getHeader());
                    }
                });
                break;
        }
    }
    private AdapterView.OnItemLongClickListener getLongPressListener() {
        return new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Item it = items.get(position);
                switch (it.getType()) {
                    case 1: //folder
                        alertAksi(true, currPath + "/" + it.getHeader());
                        break;
                    case 3:
                        //back handle
                        break;
                    case 2:
                        alertAksi(false, currPath + "/" + it.getHeader());
                        break;
                }
                return true;
            }
        };
    }

    private void readFolder(String folderStr) {
        String[] lsOutputDet;
        String[] names;
        String error;
        try {
            Process proc = new ProcessBuilder().command("ls", "-l", "-a", folderStr + "/").start();
            lsOutputDet = readFromProcess(proc, false).split("\n");
            error = readFromProcess(proc, true);
            names = readFromProcess(new ProcessBuilder().command("ls", "-a", folderStr + "/").start(), false).split("\n");
            if (!error.equals("")) {

                currPath = prevPath;
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (IOException e) {
            return;
        }
        items = new ArrayList<Item>();
        ArrayList<Item> listFolder = new ArrayList<Item>();
        ArrayList<Item> listFile = new ArrayList<Item>();
        StringBuilder subheader = new StringBuilder();

        if (!currPath.equals("")) {
            items.add(new Item(R.drawable.folder_in, "..", "Parent folder", 3));
        }
        if (names[0].equals("")) {//если папка пустая
            listView.setAdapter(new MyAdapter(this, items));
            fullPath.setText(currPath);
            return;
        }
        int j = 0;//счетчик для names
        for (String str : lsOutputDet) {
            String arr[] = str.split("\\s+");
            char id = arr[0].charAt(0);
            if (id != '-' && id != 'd' && id != 'l') {
                /*Если не файл, не папка, не ссылка,
                 *а какая-то фигня, то от греха подальше, пропускаем
                 */
                //L.write(tag, id + " not known");
                continue;
            }
            subheader.delete(0, subheader.length()).append(' ');//cls subheader
            subheader.append(arr[0].substring(1)).append(' ');//add permissions to subheader
            if (id == 'd' || id == 'l') {//если папка или ссылка
                subheader.append(arr[3]).append(' ').append(arr[4]);//date folder
                if (!names[j].equals("."))
                    if (!names[j].equals(("..")))
                        listFolder.add(new Item(R.drawable.folder, names[j], subheader.toString(), 1));
            }
            else {//если файл
                xsize = arr[4];
                subheader.append("  ").append(arr[5]).append("   ").append(calcSize(Long.parseLong(arr[4])));//date file
                String ext = getExtension(names[j]);// get extension from name
                int iconId = R.drawable.file;
                if (mapExt.containsKey(ext)) {
                    iconId = mapExt.get(ext);
                }
                listFile.add(new Item(iconId, names[j], subheader.toString(), 2));
            }
            j++;
        }
        Collections.sort(listFolder, alphabeticComparator);
        Collections.sort(listFile, alphabeticComparator);
        items.addAll(listFolder.subList(0, listFolder.size()));
        items.addAll(listFile.subList(0, listFile.size()));
        //Collections.sort(items, alphabeticComparator);
        listView.setAdapter(new MyAdapter(this, items));
        fullPath.setText(currPath);
    }
    private void selectAction(String xnama)
    {
        String path = currPath+"/"+xnama;

        if (filefb) {
            Intent intent = new Intent(MainFileManager.this, MainBrowser.class);
            intent.putExtra("filefbpath", currPath);
            intent.putExtra("filefbnama", xnama);
            intent.putExtra("filefbsize", xsize);
            MainFileManager.this.startActivity(intent);
            MainFileManager.this.finish();
        }
        else if (openpage) {
            Intent intent = new Intent(MainFileManager.this, MainBrowser.class);
            intent.putExtra("url", "file://"+path);
            MainFileManager.this.startActivity(intent);
            MainFileManager.this.finish();
        } else {
            if (chooseFile) {
                Intent intent = getIntent();
                intent.setData(Uri.parse("file://" + path));
                setResult(RESULT_OK, intent);
                finish();
                return;
            }
            String mimeType;
            String ext = getExtension(path);
            /*if (supportedFiles.containsKey(ext))
            {
                Intent intent = new Intent(Intent.ACTION_EDIT, Uri.parse(path), this, MainEditor.class);
                intent.putExtra("code_type", supportedFiles.get(ext));
                startActivityForResult(intent, 1);
                return;
            }*/
            if (ext != null) {
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                mimeType = mime.getMimeTypeFromExtension(ext.substring(1));
                if (mimeType != null)
                {
                    Toast.makeText(this, "Exception: Uri "+path, Toast.LENGTH_LONG).show();
                    //Log.d(tag, mimeType);
                    /*Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse("file://" + path), mimeType);
                    intent.putExtra("data", path);
                    intent.putExtra(Intent.EXTRA_TITLE, "Что использовать?");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {}*/
                }
                else {
                    Toast.makeText(this, "Exception in intent.getdata()", Toast.LENGTH_LONG).show();
                    //Intent intent = new Intent(Intent.ACTION_VIEW);
                    //intent.setDataAndType(Uri.parse("file://"+path), "*/*");
                    //startActivity(intent);
                }
            }
        }
    }

    private void alertAksi(boolean xfolder, String xpath) {
        path = xpath;
        folder = xfolder;
        AlertDialog.Builder builderIndex = new AlertDialog.Builder(MainFileManager.this);
        builderIndex.setTitle("Pilih Aksi");
        builderIndex.setItems(aksiVar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item)
            {
                if (item == 0 && !folder)
                {
                    String[] aksi = {"Text","Intent"};
                    AlertDialog.Builder builderIndex = new AlertDialog.Builder(MainFileManager.this);
                    builderIndex.setTitle("Pilih Aksi");
                    builderIndex.setItems(aksi, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item)
                        {
                            MimeTypeMap mime = MimeTypeMap.getSingleton();
                            String ext = getExtension(path);
                            String mimeType = mime.getMimeTypeFromExtension(ext.substring(1));

                            if (item == 0) {
                                if (supportedFiles.containsKey(ext)) {
                                    //Intent intent = new Intent(Intent.ACTION_EDIT, Uri.parse(path), MainFileManager.this, MainEditor.class);
                                    //intent.putExtra("code_type", supportedFiles.get(ext));
                                    //startActivityForResult(intent, 1);
                                } else {
                                    //edtCustom.setText(executer("cat "+path));
                                }
                            }
                            else if (item == 1) {

                                if (mimeType != null) {
                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.parse("file://" + path), mimeType);
                                    intent.putExtra("data", path);
                                    intent.putExtra(Intent.EXTRA_TITLE, "Что использовать?");
                                    try {
                                        startActivity(intent);
                                    } catch (ActivityNotFoundException e) {
                                    }
                                }
                                else {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setDataAndType(Uri.parse("file://"+path), "*/*");
                                    startActivity(intent);
                                }
                            }
                        }
                    });
                    builderIndex.create().show();
                }
                else if (item == 1) {
                    if (aksiVar[1].equals("Pindah")) {
                        tmpCloneFie = path;
                        aksiVar[1] = "Paste HERE";
                    }
                    else if (aksiVar[1].equals("Paste HERE")) {
                        String namaFile[] = tmpCloneFie.split("/");

                        if (folder) {
                            try {
                                Runtime.getRuntime().exec("mv -R "+tmpCloneFie+" "+pwd(path));

                                Toast.makeText(MainFileManager.this, "sukses", Toast.LENGTH_LONG).show();
                                tmpCloneFie = "";
                                aksiVar[1] = "Pindah";
                                aksiVar[2] = "Copy";
                            }
                            catch(Exception e) {
                                tmpCloneFie = "";
                                aksiVar[1] = "Pindah";
                                aksiVar[2] = "Copy";
                                Toast.makeText(MainFileManager.this, "ERROR: "+e, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            copyFile(tmpCloneFie, namaFile[namaFile.length-1], pwd(path));
                            try {
                                Runtime.getRuntime().exec("rm -R "+tmpCloneFie);
                            }catch(Exception e) {}
                        }
                        readFolder(pwd(path));
                    }
                }
                else if (item == 2) {
                    if (aksiVar[2].equals("Copy")) {
                        tmpCloneFie = path;
                        aksiVar[2] = "Paste HERE";
                    }
                    else if (aksiVar[2].equals("Paste HERE")) {
                        String namaFile[] = tmpCloneFie.split("/");

                        if (folder) {
                            try {
                                Runtime.getRuntime().exec("cp -R "+tmpCloneFie+" "+pwd(path));

                                Toast.makeText(MainFileManager.this, "sukses", Toast.LENGTH_LONG).show();
                                tmpCloneFie = "";
                                aksiVar[1] = "Pindah";
                                aksiVar[2] = "Copy";
                            }
                            catch(Exception e) {
                                tmpCloneFie = "";
                                aksiVar[1] = "Pindah";
                                aksiVar[2] = "Copy";
                                Toast.makeText(MainFileManager.this, "ERROR: "+e, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            copyFile(tmpCloneFie, namaFile[namaFile.length-1], pwd(path));
                        }
                        readFolder(pwd(path));
                    }
                }
                else if (item == 3) {
                    try {
                        Runtime.getRuntime().exec("rm -R "+path);
                        readFolder(pwd(path));
                    }catch(Exception e) {
                        Toast.makeText(MainFileManager.this, "ERROR hapus: "+e, Toast.LENGTH_LONG).show();
                    }
                }
                else if (item == 4) {
                    currPath = MainFileManager.this.getApplicationInfo().dataDir;
                    readFolder(currPath);
                }
            }
        });
        builderIndex.create().show();
    }
    private void alertCustom(Context context) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setTitle("Custom path");
        builder1.setCancelable(false);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate(R.layout.alert_catatan_add, null);

        final EditText edtNote = (EditText) layout.findViewById(R.id.alert_cata_add_amEdit);

        Button btSave = (Button) layout.findViewById(R.id.alert_cata_add_amSave);
        btSave.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                currPath = edtNote.getText().toString();
                readFolder(currPath);
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
    public void copyFile(String inputPath, String inputFile, String outputPath)
    {
        try {
            InputStream in = null;
            OutputStream out = null;

            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath+"/"+inputFile);

            byte[] buffer = new byte[1024];
            int read;

            while ( (read=in.read(buffer)) != -1 ) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;

            Toast.makeText(MainFileManager.this, "copy file sukses", Toast.LENGTH_LONG).show();
            tmpCloneFie = "";
            aksiVar[1] = "Pindah";
            aksiVar[2] = "Copy";
        }
        catch(Exception e) {
            Toast.makeText(MainFileManager.this, "copy file ERROR! : "+e, Toast.LENGTH_LONG).show();
            Toast.makeText(MainFileManager.this, "Tekan open dan coba lagi", Toast.LENGTH_LONG).show();
            tmpCloneFie = "";
            aksiVar[1] = "Pindah";
            aksiVar[2] = "Copy";
        }
    }

    private void initMapExt() {
        mapExt.put(".php", R.drawable.icon_php);
        mapExt.put(".html", R.drawable.icon_html);
        mapExt.put(".txt", R.drawable.icon_txt);
        mapExt.put(".cfg", R.drawable.icon_config);
        mapExt.put(".conf", R.drawable.icon_config);
        mapExt.put(".config", R.drawable.icon_config);
        mapExt.put(".ini", R.drawable.icon_config);
        mapExt.put(".sh", R.drawable.icon_config);
        mapExt.put(".css", R.drawable.icon_css);
        mapExt.put(".mp3", R.drawable.icon_music);
        mapExt.put(".amr", R.drawable.icon_music);
        mapExt.put(".wav", R.drawable.icon_music);
        mapExt.put(".mid", R.drawable.icon_music);
        mapExt.put(".midi", R.drawable.icon_music);
        mapExt.put(".ogg", R.drawable.icon_music);
        mapExt.put(".mp4", R.drawable.icon_video);
        mapExt.put(".3gp", R.drawable.icon_video);
        mapExt.put(".apk", R.drawable.icon_apk);
        mapExt.put(".sql", R.drawable.icon_db);
        mapExt.put(".doc", R.drawable.icon_doc);
        mapExt.put(".docx", R.drawable.icon_doc);
        mapExt.put(".ico", R.drawable.icon_image);
        mapExt.put(".jpg", R.drawable.icon_image);
        mapExt.put(".bmp", R.drawable.icon_image);
        mapExt.put(".gif", R.drawable.icon_image);
        mapExt.put(".png", R.drawable.icon_image);
        mapExt.put(".pdf", R.drawable.icon_pdf);
        mapExt.put(".ppt", R.drawable.icon_ppt);
        mapExt.put(".zip", R.drawable.icon_zip);
        mapExt.put(".rar", R.drawable.icon_zip);
        mapExt.put(".tar", R.drawable.icon_zip);
        mapExt.put(".7z", R.drawable.icon_zip);
        mapExt.put(".jar", R.drawable.icon_zip);

        aksiVar = new String[5];
        aksiVar[0] = "Open...";
        aksiVar[1] = "Pindah";
        aksiVar[2] = "Copy";
        aksiVar[3] = "Delete!";
        aksiVar[4] = "Home";
    }

    private String calcBackPath() {
        try {
            return currPath.substring(0, currPath.lastIndexOf('/'));
        } catch (IndexOutOfBoundsException ex) {
            return "";
        }
    }
    private String pwd(String path) {
        return path.substring(0, path.lastIndexOf('/'));
    }
    private static String getExtension(String path) {
        if (path.contains(".")) {
            return path.substring(path.lastIndexOf(".")).toLowerCase();
        }
        return null;
    }
    private String calcSize(long length) {
        if (length < 1024) {
            return String.valueOf(length).concat(" b");
        } else if (length < 1048576) {
            return String.valueOf(round((float) length / 1024f)).concat(" Kb");
        } else {
            return String.valueOf(round((float) length / 1048576f)).concat(" Mb");
        }
    }
    /*rounded to two decimal places*/
    public static float round(float sourceNum) {
        int temp = (int) (sourceNum / 0.01f);
        return temp / 100f;
    }

    public static String readFromProcess(java.lang.Process process, boolean err) {
        StringBuilder result = new StringBuilder();
        String line = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(err ? process.getErrorStream() : process.getInputStream()));
        try {
            while ((line = br.readLine()) != null) {
                result.append(line).append("\n");
            }
        } catch (IOException e) {
            //Log.e("Main", "read From Process", e);
        }
        return result.toString();
    }
    public String executer(String command) {
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line+"\n");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String response = output.toString();
        return response;
    }
    public String notNexecuter(String command) {
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        String response = output.toString();
        return response;
    }
    public static String readFile(String path) {
        StringBuilder result = new StringBuilder();
        try {
            FileReader fis = new FileReader(path);
            char buffer[] = new char[1100];
            int read;

            do {
                read = fis.read(buffer);

                if (read >= 0)
                {
                    result.append(buffer, 0, read);
                }
            } while (read >= 0);


        } catch (FileNotFoundException e) {
            return "File not found (TODO)";
        } catch (IOException ioe) {
            return "IOException (TODO)";
        }
        return result.toString();
    }
    public static void saveCode(String code, String charset, String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), charset);
        osw.append(code).flush();
        osw.close();
    }
}

class MyAdapter extends BaseAdapter {

    private ArrayList<Item> list = new ArrayList<Item>();
    private Context context;
    private LayoutInflater li;

    public MyAdapter(Context context, ArrayList<Item> arr) {
        if (arr != null) {
            list = arr;
        }
        this.context = context;
        li = LayoutInflater.from(context);
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = li.inflate(R.layout.list_filemanager_row, null);
            holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.list_filemanager_image);
            holder.header = (TextView) view.findViewById(R.id.list_header);
            holder.subheader = (TextView) view.findViewById(R.id.list_filemanager_subheader);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        Item item = list.get(position);


        holder.imageView.setImageResource(item.getImageId());
        holder.header.setText(item.getHeader());
        holder.subheader.setText(item.getSubheader());
        return view;
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView header, subheader;
    }
}


class Item implements SortItem {

    private int imageId, type;
    private String header, subheader;

    public Item(int imageId_, String header_, String subheader_, int type_) {
        imageId = imageId_;
        header = header_;
        subheader = subheader_;
        type = type_;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getHeader() {
        return header;
    }

    public void setSubheader(String subheader) {
        this.subheader = subheader;
    }

    public String getSubheader() {
        return subheader;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getImageId() {
        return imageId;
    }

    public String getSortField() {
        return header;
    }
}

interface SortItem {

    public String getSortField();
}

class AlphabeticComparator implements Comparator<SortItem> {

    public int compare(SortItem p1, SortItem p2) {
        return p1.getSortField().compareToIgnoreCase(p2.getSortField());
    }
}
