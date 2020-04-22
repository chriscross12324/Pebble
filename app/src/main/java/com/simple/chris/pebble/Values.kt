package com.simple.chris.pebble

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.polyak.iconswitch.IconSwitch

object Values {
    private const val SAVE = "SavedValues"

    //Vibrations
    val notificationPattern = longArrayOf(0, 5, 5, 5)
    val weakVibration = longArrayOf(0, 1)
    val mediumVibration = longArrayOf(0, 3)
    val strongVibration = longArrayOf(0, 200)

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
    var gradientCreatorStartColour = ""
    var gradientCreatorEndColour = ""
    var gradientCreatorDescription = ""
    var currentColourPOS = ""
    var currentColourInt = 0
    var currentColourHEX = ""


    fun storagePermissionGiven(context: Context): Boolean {
        val result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }


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
