package com.simple.chris.pebble

import android.content.Context
import com.polyak.iconswitch.IconSwitch

object Values {

    private const val SAVE = "SavedValues"

    //Vibrations
    val notification = longArrayOf(0, 5, 5, 5)
    const val hapticFeedback: Long = 5

    //App Values
    var firstStart: Boolean = true
    var vibrations: Boolean = true
    var askMobileData: Boolean = true
    var detailsPushHoldPopupClosed: Boolean = false

    var theme: String = "dark"

    var lastVersion = 0
    lateinit var browse: ArrayList<HashMap<String, String>>
    lateinit var featured: ArrayList<HashMap<String, String>>

    var gridCount: IconSwitch.Checked = IconSwitch.Checked.RIGHT


    fun saveValues(context: Context) {
        val sharedPreferences = context.getSharedPreferences(SAVE, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("firstStart", firstStart)
        editor.putBoolean("vibrations", vibrations)
        editor.putBoolean("askMobileData", askMobileData)
        editor.putString("theme", theme)
        editor.putInt("lastVersion", lastVersion)
        if (gridCount == IconSwitch.Checked.RIGHT) {
            editor.putBoolean("normalGrid", true)
        } else {
            editor.putBoolean("normalGrid", false)
        }

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
        gridCount = if (sharedPreferences.getBoolean("normalGrid", true)) {
            IconSwitch.Checked.RIGHT
        } else {
            IconSwitch.Checked.LEFT
        }

    }

}
