package com.simple.chris.pebble;

import android.content.Context;
import android.content.SharedPreferences;

public class Values {

    public static final String SAVE = "SavedValues";


    public static String currentActivity;

    public static boolean darkMode = true;

    public static void saveValues(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SAVE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("darkMode", darkMode);
        editor.apply();
    }

    public static void loadValues(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SAVE, Context.MODE_PRIVATE);
        darkMode = sharedPreferences.getBoolean("darkMode", true);
    }
}
