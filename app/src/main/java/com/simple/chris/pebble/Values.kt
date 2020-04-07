package com.simple.chris.pebble

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.polyak.iconswitch.IconSwitch

object Values {

    private const val SAVE = "SavedValues"

    var currentActivity: String = ""
    var userName: String = "User"

    //Vibrations
    val notification = longArrayOf(0, 5, 5, 5)
    const val strongVibration: Long = 5
    const val mediumVibration: Long = 3
    const val lowVibration: Long = 1

    //App Values
    var firstStart: Boolean = true
    var vibrations: Boolean = true
    var askMobileData: Boolean = true
    var detailsPushHoldPopupClosed: Boolean = false

    var theme: String = "dark"

    var lastVersion = 0
    lateinit var browse: ArrayList<HashMap<String, String>>

    var gridCount: IconSwitch.Checked = IconSwitch.Checked.RIGHT

    //Gradient Creator Saved values
    var createGradientStartColour: String = "#acd77b"
    var createGradientEndColour: String = "#74d77b"
    var createGradientName: String = ""
    var createGradientDescription = ""

    fun storagePermissionGiven(context: Context): Boolean {
        val result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return result == PackageManager.PERMISSION_GRANTED
    }


    fun saveValues(context: Context) {
        val sharedPreferences = context.getSharedPreferences(SAVE, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("firstStart", firstStart)
        editor.putBoolean("vibrations", vibrations)
        editor.putBoolean("askMobileData", askMobileData)
        editor.putString("theme", theme)
        editor.putInt("lastVersion", lastVersion)
        editor.putBoolean("detailsPushHoldPopupClosed", detailsPushHoldPopupClosed)

        editor.apply()
    }

    fun loadValues(context: Context) {
        val sharedPreferences = context.getSharedPreferences(SAVE, Context.MODE_PRIVATE)
        firstStart = sharedPreferences.getBoolean("firstStart", true)
        vibrations = sharedPreferences.getBoolean("vibrations", true)
        detailsPushHoldPopupClosed = sharedPreferences.getBoolean("detailsPushHoldPopupClosed", false)
        theme = sharedPreferences.getString("theme", "dark").toString()
        lastVersion = sharedPreferences.getInt("lastVersion", 0)
        askMobileData = try {
            sharedPreferences.getBoolean("askMobileData", true)
        } catch (e: Exception) {
            sharedPreferences.getBoolean("askData", true)
        }

    }

}
