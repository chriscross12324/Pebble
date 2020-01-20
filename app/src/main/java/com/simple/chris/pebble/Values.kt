package com.simple.chris.pebble

import android.content.Context

/*object Values {

    const val SAVE = "SavedValues"

    //Vibrations
    val notification = longArrayOf(0, 5, 5, 5)
    const val hapticFeedback: Long = 5

    //App Values
    var firstStart: Boolean = true
    var currentActivity: String = ""
    var vibrations: Boolean = true
    var theme: String = "dark"
    var askMobileData: Boolean = true
    var lastVersion: Int = 0

    fun saveValues(context: Context) {
        val sharedPreferences = context.getSharedPreferences(SAVE, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("firstStart", firstStart)
        editor.putBoolean("vibrations", vibrations)
        editor.putBoolean("askMobileData", askMobileData)
        editor.putString("theme", theme)
        editor.putInt("lastVersion", lastVersion)
        editor.apply()
    }

    fun loadValues(context: Context) {
        val sharedPreferences = context.getSharedPreferences(SAVE, Context.MODE_PRIVATE)
        firstStart = sharedPreferences.getBoolean("firstStart", true)
        vibrations = sharedPreferences.getBoolean("vibrations", true)
        askMobileData = sharedPreferences.getBoolean("askMobileData", true)
        theme = sharedPreferences.getString("theme", "dark").toString()
        lastVersion = sharedPreferences.getInt("lastVersion", 0)
        askMobileData = try {
            sharedPreferences.getBoolean("askMobileData", true)
        } catch (e: Exception) {
            sharedPreferences.getBoolean("askData", true)
        }


    }

}*/
