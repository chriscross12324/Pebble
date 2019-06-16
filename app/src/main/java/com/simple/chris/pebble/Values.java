package com.simple.chris.pebble;

import android.content.Context;
import android.content.SharedPreferences;

public class Values {

    public static final String SAVE = "SavedValues";
    public static boolean firstStart;


    public static String currentActivity;

    public static boolean darkMode = true;
    public static boolean uiDesignerMode = false;
    public static boolean askData = true;

    public static void saveValues(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SAVE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstStart", firstStart);
        editor.putBoolean("darkMode", darkMode);
        editor.putBoolean("uiDesignerMode", uiDesignerMode);
        editor.putBoolean("askData", askData);
        editor.apply();
    }

    public static void loadValues(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SAVE, Context.MODE_PRIVATE);
        firstStart = sharedPreferences.getBoolean("firstStart", true);
        darkMode = sharedPreferences.getBoolean("darkMode", true);
        uiDesignerMode = sharedPreferences.getBoolean("uiDesignerMode", false);
        askData = sharedPreferences.getBoolean("askData", true);
    }
}
