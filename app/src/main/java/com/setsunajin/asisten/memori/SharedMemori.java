package com.setsunajin.asisten.memori;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedMemori {
    private SharedPreferences settings;

    public SharedMemori(Context context) {
        settings = context.getSharedPreferences("Settings", 0);
    }

    public void setSharedMemori(String key, boolean data) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, data);
        editor.commit();
    }
    public boolean getSharedMemori(String key) {
		return settings.getBoolean(key,false);
		
    }

    public void setStrSharedMemori(String key, String data) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, data);
        editor.commit();
    }
    public String getStrSharedMemori(String key) {
        return settings.getString(key,"");
    }
	
}
