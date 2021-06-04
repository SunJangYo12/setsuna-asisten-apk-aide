package com.setsunajin.asisten.memori;

import android.app.*;
import android.os.*;
import android.widget.*;
import android.content.*;
import android.view.*;
import java.util.*;
import java.io.*;
import android.widget.AdapterView.OnItemClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import android.app.Activity;

import com.setsunajin.asisten.*;

public class MainMemori extends Activity {

    private TextView text;
    private ListView list;
    private int posisi;
    private String menu = "";
    public static int position = 1;
    public static int customList = 0;
    public static String newTitle = "";
    public static ArrayList<String> titles;
    public static ArrayList<Item> items;
    public static Cursor cursor;

    
    public static String isCatatan(int position, int pilih, Context context) {
        DBCatatan dbCatatan = new DBCatatan(context);

        SQLiteDatabase db = dbCatatan.getReadableDatabase();
        Cursor c = dbCatatan.getNote(db, items.get(position).getId());
        db.close();
        
        return c.getString(pilih).toString();
    }

    public static String isHistory(int position, int pilih, Context context) {
        DBHistory dbHistory = new DBHistory(context);

        SQLiteDatabase db = dbHistory.getReadableDatabase();
        Cursor c = dbHistory.getNote(db, items.get(position).getId());
        db.close();

        return c.getString(pilih).toString();
    }

    public static String isPaket(int position, int pilih, Context context) {
        DBPaket dbPaket = new DBPaket(context);

        SQLiteDatabase db = dbPaket.getReadableDatabase();
        Cursor c = dbPaket.getNote(db, items.get(position).getId());
        db.close();

        return c.getString(pilih).toString();
    }

    public ArrayAdapter<String> getCatatan(Context context) {
        DBCatatan dbCatatan = new DBCatatan(context);

        SQLiteDatabase db = dbCatatan.getReadableDatabase();
        cursor = dbCatatan.getNotes2(db);

        titles = new ArrayList<String>();
        items = new ArrayList<Item>();

        startManagingCursor(cursor);
        db.close();

        if (cursor.moveToFirst()) {
            do {
                items.add(new Item(cursor.getShort(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        for (Item i : items) {
            titles.add(i.getTitle());
        }

        return new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, titles);
    }

    public ArrayAdapter<String> getHistory(Context context) {
        DBHistory dbHistory = new DBHistory(context);

        SQLiteDatabase db = dbHistory.getReadableDatabase();
        cursor = dbHistory.getNotes2(db);

        titles = new ArrayList<String>();
        items = new ArrayList<Item>();

        startManagingCursor(cursor);
        db.close();

        if (cursor.moveToFirst()) {
            do {
                items.add(new Item(cursor.getShort(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        for (Item i : items) {
            titles.add(i.getTitle());
        }
        return new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, titles);
    }

    public ArrayAdapter<String> getPaket(Context context) {
        DBPaket dbPaket = new DBPaket(context);

        SQLiteDatabase db = dbPaket.getReadableDatabase();
        cursor = dbPaket.getNotes2(db);

        titles = new ArrayList<String>();
        items = new ArrayList<Item>();

        startManagingCursor(cursor);
        db.close();

        if (cursor.moveToFirst()) {
            do {
                items.add(new Item(cursor.getShort(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        for (Item i : items) {
            titles.add(i.getTitle());
        }
        if (customList != 0) {
            return new ArrayAdapter<String>(context, customList, titles);
        } else {
            return new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, titles);
        }
    }

    public void setCatatan(String aksi, String judul, String content, Context context) {
        DBCatatan dbCatatan = new DBCatatan(context);

        if (aksi.equals("new")) {
            dbCatatan.addNote(judul, content);
        }
        else if (aksi.equals("edit")) {
            //dbCatatan.updateNote(judul, content, newTitle);
            dbCatatan.updateTable(judul, content, items.get(position).getId());
        }
        else if (aksi.equals("rm")) {
            SQLiteDatabase db = dbCatatan.getReadableDatabase();
            cursor = dbCatatan.getNotes2(db);

            titles = new ArrayList<String>();
            ArrayList<Item> items = new ArrayList<Item>();

            startManagingCursor(cursor);
            db.close();

            if (cursor.moveToFirst()) {
                do {
                    items.add(new Item(cursor.getShort(0), cursor.getString(1)));
                } while (cursor.moveToNext());
            }
            dbCatatan.removeNote(items.get(position).getId());
        }
    }

    public void setHistory(String aksi, String judul, String content, Context context) {
        DBHistory dbHistory = new DBHistory(context);

        if (aksi.equals("new")) {
            dbHistory.addNote(judul, content);
        }
        else if (aksi.equals("edit")) {
            dbHistory.updateTable(judul, content, items.get(position).getId());
        }
        else if (aksi.equals("rm")) {
            SQLiteDatabase db = dbHistory.getReadableDatabase();
            cursor = dbHistory.getNotes2(db);

            titles = new ArrayList<String>();
            ArrayList<Item> items = new ArrayList<Item>();

            startManagingCursor(cursor);
            db.close();

            if (cursor.moveToFirst()) {
                do {
                    items.add(new Item(cursor.getShort(0), cursor.getString(1)));
                } while (cursor.moveToNext());
            }
            dbHistory.removeNote(items.get(position).getId());
        }
    }

    public void setPaket(String aksi, String judul, String content, Context context) {
        DBPaket dbPaket = new DBPaket(context);

        if (aksi.equals("new")) {
            dbPaket.addNote(judul, content);
        }
        else if (aksi.equals("edit")) {
            dbPaket.updateTable(judul, content, items.get(position).getId());
        }
        else if (aksi.equals("rm")) {
            SQLiteDatabase db = dbPaket.getReadableDatabase();
            cursor = dbPaket.getNotes2(db);

            titles = new ArrayList<String>();
            ArrayList<Item> items = new ArrayList<Item>();

            startManagingCursor(cursor);
            db.close();

            if (cursor.moveToFirst()) {
                do {
                    items.add(new Item(cursor.getShort(0), cursor.getString(1)));
                } while (cursor.moveToNext());
            }
            dbPaket.removeNote(items.get(position).getId());
        }
    }

    public ArrayList<String> getStringHistory(Context context) {
        DBHistory dbHistory = new DBHistory(context);

        SQLiteDatabase db = dbHistory.getReadableDatabase();
        cursor = dbHistory.getNotes2(db);

        titles = new ArrayList<String>();
        items = new ArrayList<Item>();

        startManagingCursor(cursor);
        db.close();

        if (cursor.moveToFirst()) {
            do {
                items.add(new Item(cursor.getShort(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        for (Item i : items) {
            titles.add(i.getTitle());
        }
        return titles;
    }
    public ArrayList<String> getStringCatatan(Context context) {
        DBCatatan dbCatatan = new DBCatatan(context);

        SQLiteDatabase db = dbCatatan.getReadableDatabase();
        cursor = dbCatatan.getNotes2(db);

        titles = new ArrayList<String>();
        items = new ArrayList<Item>();

        startManagingCursor(cursor);
        db.close();

        if (cursor.moveToFirst()) {
            do {
                items.add(new Item(cursor.getShort(0), cursor.getString(1)));
            } while (cursor.moveToNext());
        }
        for (Item i : items) {
            titles.add(i.getTitle());
        }
        return titles;
    }

    private void init() {
        list = (ListView) findViewById(R.id.activity_memori_list);

        try {
            if (menu.equals("catatan"))
                list.setAdapter(getCatatan(MainMemori.this));
            if (menu.equals("history"))
                list.setAdapter(getHistory(MainMemori.this));
            if (menu.equals("paket"))
                list.setAdapter(getPaket(MainMemori.this));

            list.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {
                    position = arg2;
                    alert(MainMemori.this);
                }
            });
        } catch(Exception e) {
            Toast.makeText(MainMemori.this, ""+e, Toast.LENGTH_LONG).show();
        }
    }

    private void main()
    {
        setContentView(R.layout.activity_memori);

        text = (TextView) findViewById(R.id.activity_memori_status);
        list = (ListView) findViewById(R.id.activity_memori_list);
        final String[] header = {"Catatan", "History", "Paket", "Exit"};

        AlertDialog.Builder builderIndex = new AlertDialog.Builder(MainMemori.this);
        builderIndex.setTitle("Menu memori");
        builderIndex.setItems(header, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) 
            {
                if (item == 0) {
                    menu = "catatan";
                    list.setAdapter(getCatatan(MainMemori.this));
                }
                if (item == 1) {
                    menu = "history";
                    list.setAdapter(getHistory(MainMemori.this));
                }
                if (item == 2) {
                    menu = "paket";
                    list.setAdapter(getPaket(MainMemori.this));
                }
                if (item == 3) {
                    MainMemori.this.finish();
                }
                text.setText(header[item]);
                text.append(" back to menu memori");
                text.setOnClickListener(new View.OnClickListener()
                {
                    public void onClick(View v) {
                        main();
                    }
                });
                list.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView arg0, View arg1, int arg2, long arg3) {
                        position = arg2;
                        alert(MainMemori.this);
                    }
                });
            }
        });
        builderIndex.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.FIRST, 1, 1, "Delete all").setIcon(R.drawable.icon_css);
        menu.add(Menu.FIRST, 2, 1, "Exit").setIcon(R.drawable.icon_css);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            String home = new MainActivity().homePath;
            String database = "";

            if (menu.equals("catatan")) {
                database = "catatan.db";
            } else if(menu.equals("history")) {
                database = "history.db";
            } else if (menu.equals("paket")) {
                database = "paket.db";
            }
            else
                Toast.makeText(this, "menu database tidak dipilih", Toast.LENGTH_SHORT).show();

            new MainFileManager().notNexecuter("rm "+home+"/databases/"+database);
            new MainFileManager().notNexecuter("rm "+home+"/databases/"+database+"-journal");
            Toast.makeText(this, "Selesai", Toast.LENGTH_SHORT).show();
            finish();
        }
        if (item.getItemId() == 2) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        main();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void alert(Context context) {
        final CharSequence[] dialogitem = {"Edit", "Hapus"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainMemori.this);
        builder.setTitle("Pilihan");
        builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch(item){
                    case 0 :
                        alertMenuEdit();
                        break;
                    case 1 :
                        if (menu.equals("catatan"))
                            setCatatan("rm", "", "", MainMemori.this);
                        if (menu.equals("history"))
                            setHistory("rm", "", "", MainMemori.this);
                        if (menu.equals("paket"))
                            setPaket("rm", "", "", MainMemori.this);
                        init();
                        break;
                    default:
                        break;
                }
            }
        });
        builder.create().show();
        ((ArrayAdapter)list.getAdapter()).notifyDataSetInvalidated();
    }

    private void alertMenuEdit() {
        String namaMemori = "";
        String contentMemori = "";
        String dateMemori = "";

        if (menu.equals("catatan")) {
            namaMemori = isCatatan(position, 0, MainMemori.this);
            contentMemori = isCatatan(position, 1, MainMemori.this);
            dateMemori = isCatatan(position, 2, MainMemori.this);
        }
        if (menu.equals("history")) {
            namaMemori = isHistory(position, 0, MainMemori.this);
            contentMemori = isHistory(position, 1, MainMemori.this);
            dateMemori = isHistory(position, 2, MainMemori.this);
        }
        if (menu.equals("paket")) {
            namaMemori = isPaket(position, 0, MainMemori.this);
            contentMemori = isPaket(position, 1, MainMemori.this);
            dateMemori = isPaket(position, 2, MainMemori.this);
        }

        final String[] rinci = {"Nama: \n"+namaMemori,
                          "Content: \n"+contentMemori,
                          "Date: \n"+dateMemori };

        AlertDialog.Builder builderIndex1 = new AlertDialog.Builder(MainMemori.this);
        builderIndex1.setTitle("Rincian: "+position);
        builderIndex1.setItems(rinci, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) 
            {
                alertEdit(rinci[item], item);
            }
        });
        builderIndex1.create().show();
    }

    private void alertEdit(String inEdit, int xindex) {
        final int index = xindex;
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainMemori.this);
        builder1.setTitle("Edit: "+inEdit);
        builder1.setCancelable(true);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE );
        View layout = inflater.inflate(R.layout.alert_catatan, null);

        final EditText edit_alert = (EditText) layout.findViewById(R.id.alert_catatan_amEdit);

        if (menu.equals("catatan"))
            edit_alert.setText(isCatatan(position, index, MainMemori.this));
        if (menu.equals("history"))
            edit_alert.setText(isHistory(position, index, MainMemori.this));
        if (menu.equals("paket"))
            edit_alert.setText(isPaket(position, index, MainMemori.this));

        Button bt = (Button) layout.findViewById(R.id.alert_catatan_amButton);
        bt.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                String edit = edit_alert.getText().toString();
                if (index == 0) {
                    position = position;
                    if (menu.equals("catatan"))
                        setCatatan("edit", edit, isCatatan(position, 1, MainMemori.this), MainMemori.this);
                    if (menu.equals("history"))
                        setHistory("edit", edit, isHistory(position, 1, MainMemori.this), MainMemori.this);
                    if (menu.equals("paket"))
                        setPaket("edit", edit, isPaket(position, 1, MainMemori.this), MainMemori.this);

                }
                if (index == 1) {
                    position = position;
                    if (menu.equals("catatan"))
                        setCatatan("edit", isCatatan(position, 0, MainMemori.this), edit, MainMemori.this);
                    if (menu.equals("history"))
                        setHistory("edit", isHistory(position, 0, MainMemori.this), edit, MainMemori.this);
                    if (menu.equals("paket"))
                        setPaket("edit", isPaket(position, 0, MainMemori.this), edit, MainMemori.this);

                }
                if (index == 2) {
                    Toast.makeText(MainMemori.this, "Auto edit", Toast.LENGTH_LONG).show();
                }
                init();
            }
        });
        builder1.setView(layout);
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
}

