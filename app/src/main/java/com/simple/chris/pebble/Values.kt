package com.simple.chris.pebble

import android.content.Context

/**
 * Stores all essential values, can be referenced and changed from Activities
 */
object Values {
    private const val SAVE = "SavedValues"

    //Vibrations
    val notificationPattern = longArrayOf(0, 5, 5, 5)
    val weakVibration = longArrayOf(0, 1)
    val mediumVibration = longArrayOf(0, 7)
    val strongVibration = longArrayOf(0, 20)

    //Hidden Values
    var firstStart: Boolean = true
    var lastVersion = 0
    var hintPushHoldDismissed = false
    var hintCreateGradientDismissed = false
    var currentActivity: String = ""
    lateinit var gradientList: ArrayList<HashMap<String, String>>

    //Settings
    var vibrationEnabled: Boolean = true
    var theme: String = "dark"
    var askMobileData: Boolean = true
    var userName: String = "User"

    //Gradient Creator
    var gradientCreatorGradientName = ""
    var gradientCreatorStartColour = "#acd77b"
    var gradientCreatorEndColour = "#74d77b"
    var gradientCreatorDescription = ""
    var currentColourPOS = ""
    var currentColourInt = 0
    var currentColourHEX = ""

    /**
     * Saves all values to a SharedPreferences file - Ran whenever a value might have changed
     */
    fun saveValues(context: Context) {
        val sharedPrefs = context.getSharedPreferences(SAVE, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean("firstStart", firstStart)
        editor.putInt("lastVersion", lastVersion)
        editor.putBoolean("hintPushHoldDismissed", hintPushHoldDismissed)
        editor.putBoolean("vibrationEnabled", vibrationEnabled)
        editor.putString("theme", theme)
        editor.putBoolean("askMobileData", askMobileData)
        editor.putString("userName", userName)
        editor.putString("gradientCreatorGradientName", gradientCreatorGradientName)
        editor.putString("gradientCreatorStartColour", gradientCreatorStartColour)
        editor.putString("gradientCreatorEndColour", gradientCreatorEndColour)
        editor.putString("gradientCreatorDescription", gradientCreatorDescription)
        editor.apply()
    }

    /**
     * Loads all values from a SharedPreferences file - Only used when app is opened or when a crash may happen
     */
    fun loadValues(context: Context) {
        val sharedPrefs = context.getSharedPreferences(SAVE, Context.MODE_PRIVATE)
        firstStart = sharedPrefs.getBoolean("firstStart", true)
        lastVersion = sharedPrefs.getInt("lastVersion", 0)
        hintPushHoldDismissed = sharedPrefs.getBoolean("hintPushHoldDismissed", false)
        vibrationEnabled = sharedPrefs.getBoolean("vibrationEnabled", true)
        theme = sharedPrefs.getString("theme", "dark")!!
        askMobileData = sharedPrefs.getBoolean("askMobileData", true)
        userName = sharedPrefs.getString("userName", "")!!
        gradientCreatorGradientName = sharedPrefs.getString("gradientCreatorGradientName", "")!!
        gradientCreatorStartColour = sharedPrefs.getString("gradientCreatorStartColour", "#acd77b")!!
        gradientCreatorEndColour = sharedPrefs.getString("gradientCreatorEndColour", "#74d77b")!!
        gradientCreatorDescription = sharedPrefs.getString("gradientCreatorDescription", "")!!
    }

}
