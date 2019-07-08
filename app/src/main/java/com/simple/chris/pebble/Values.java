package com.simple.chris.pebble;

import android.content.Context;
import android.content.SharedPreferences;

public class Values {

    public static final String SAVE = "SavedValues";
    public static boolean firstStart;


    public static String currentActivity;

    public static boolean vibrations;
    public static boolean darkMode;
    public static boolean uiDesignerMode;
    public static boolean uppercaseHEX;
    public static boolean askData;

    public static void saveValues(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SAVE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("vibrations", vibrations);
        editor.putBoolean("firstStart", firstStart);
        editor.putBoolean("darkMode", darkMode);
        editor.putBoolean("uiDesignerMode", uiDesignerMode);
        editor.putBoolean("uppercaseHEX", uppercaseHEX);
        editor.putBoolean("askData", askData);
        editor.apply();
    }

    public static void loadValues(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SAVE, Context.MODE_PRIVATE);
        vibrations = sharedPreferences.getBoolean("vibrations", true);
        firstStart = sharedPreferences.getBoolean("firstStart", true);
        darkMode = sharedPreferences.getBoolean("darkMode", false);
        uiDesignerMode = sharedPreferences.getBoolean("uiDesignerMode", false);
        uppercaseHEX = sharedPreferences.getBoolean("uppercaseHEX", true);
        askData = sharedPreferences.getBoolean("askData", true);
    }
}
