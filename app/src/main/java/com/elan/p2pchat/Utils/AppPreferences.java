package com.elan.p2pchat.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferences {

    private SharedPreferences prefs;
    private Context context;
    private static AppPreferences appPreferences;
    private SharedPreferences.Editor editor;

    private AppPreferences(Context context) {
        prefs = context.getSharedPreferences("daily_prefs",Context.MODE_PRIVATE);
        editor= prefs.edit();
    }

    public static AppPreferences getAppPreferences(Context context){
        if(appPreferences==null)
            appPreferences =  new AppPreferences(context);
        return appPreferences;
    }

    public void insertString(String key,String value) {
        editor.putString(key,value);
        editor.commit();
    }

    public void insertBoolean(String key,boolean value) {
        editor.putBoolean(key,value);
        editor.commit();
    }

    public boolean getBoolean(String key) {
        return prefs.getBoolean(key,false);
    }

    public String getString(String key) {
        return prefs.getString(key,"");
    }

    public boolean containsKey(String key) {
        return prefs.contains(key);
    }

    public void detach() {
        editor = null;
        prefs = null;
        appPreferences = null;
    }

    public void deleteValue(String key) {
        editor.remove(key);
        editor.commit();
    }
}
