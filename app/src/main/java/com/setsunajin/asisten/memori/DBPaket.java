package com.setsunajin.asisten.memori;

import java.util.Date;
import java.text.SimpleDateFormat;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBPaket extends SQLiteOpenHelper {

    private Context ctx;
    private static final int version = 1;
    private static final String DB_NAME = "paket.db";
    private static final String TABLE_NAME = "paket";
    
    //column names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "noteTitle";
    private static final String KEY_CONTENT = "noteContent";
    private static final String KEY_DATE = "date";
    
    public DBPaket(Context context) {
        super(context, DB_NAME, null, version);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (id INTEGER PRIMARY KEY AUTOINCREMENT, "+KEY_TITLE+" TEXT NOT NULL, "+KEY_CONTENT+" TEXT NOT NULL, "+KEY_DATE+" TEXT);";
        
        db.execSQL(CREATE_TABLE);
    }

    
    //in case of upgrade we're dropping the old table, and create the new one
    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        
        db.execSQL("DROP TABLE IF EXIST " + TABLE_NAME);
        
        onCreate(db);
        
    }
    
    public void addNote(String title, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        String waktu = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String hari = new SimpleDateFormat("dd-MMM").format(new Date());
        
        ContentValues cv = new ContentValues();
        cv.put("noteTitle", title);
        cv.put("noteContent", content);
        cv.put("date", waktu+" "+hari);

        db.insert(TABLE_NAME, null, cv);
        db.close();
    }
    
    
    //getting all notes
    public Cursor getNotes(SQLiteDatabase db) {
        Cursor c = db.query(TABLE_NAME, new String[] {KEY_TITLE, KEY_CONTENT}, null, null, null, null, "id DESC");
        c.moveToFirst();
        return c;
    }
    
    public Cursor getNotes2(SQLiteDatabase db) {
        //db.query is like normal sql query
        //cursor contains all notes 
        Cursor c = db.query(TABLE_NAME, new String[] {KEY_ID, KEY_TITLE}, null, null, null, null, "id DESC");
        //moving to the first note
        c.moveToFirst();
        //and returning Cursor object
        return c;
    }
    
    public Cursor getNote(SQLiteDatabase db, int id) {      
        Cursor c = db.query(TABLE_NAME, new String[] {KEY_TITLE, KEY_CONTENT, KEY_DATE}, KEY_ID + " = ?", new String[] { String.valueOf(id) }, null, null, null);
        c.moveToFirst();
        return c;
    }
    
    public void removeNote(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " = ?", new String[] { String.valueOf(id) });
        db.close();
    }

    public void updateTable(String title, String content, int index) {
        String waktu = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String hari = new SimpleDateFormat("dd-MMM").format(new Date());
        String date = waktu+" "+hari;

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("update "+TABLE_NAME+" set "+KEY_TITLE+"='"+title+"', "+KEY_CONTENT+"='"+content+"', "+KEY_DATE+"='"+date+ "' where "+KEY_ID+"='" +index+"'");        
        //db.execSQL("update paket set noteTitle='"+title+"', noteContent='"+content+"', date='"+date+ "' where id='" +index+"'");
    }
    
    public void updateNote(String title, String content, String editTitle) {
        SQLiteDatabase db = this.getWritableDatabase();
        String waktu = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String hari = new SimpleDateFormat("dd-MMM").format(new Date());
        
        ContentValues cv = new ContentValues();
        cv.put("noteTitle", title);
        cv.put("noteContent", content);
        cv.put("date", waktu+" "+hari);
        
        db.update(TABLE_NAME, cv, KEY_TITLE + " LIKE '" +  editTitle +  "'", null);
        
        db.close();
        
        
    }

}
