package com.simple.chris.pebble

import android.content.Context
import android.util.Log
import java.lang.Exception

/**
 * Stores all essential values, can be referenced and changed from Activities
 */
object Values {
    private const val SAVE = "SavedValues"
    var valuesLoaded = false

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
    var gradientList: ArrayList<HashMap<String, String>> = ArrayList()
    var filteredGradients: ArrayList<HashMap<String, String>> = ArrayList()
    var dialogShowAgainTime: Long = 450
    var downloadingGradients = false

    //Changing Values
    var gradientIsTouched = false

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
    var justSubmitted = false
    var gcFirstStart = true


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
        editor.putBoolean("gcFirstStart", gcFirstStart)
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
        valuesLoaded = true
        firstStart = sharedPrefs.getBoolean("firstStart", true)
        lastVersion = sharedPrefs.getInt("lastVersion", 0)
        hintPushHoldDismissed = sharedPrefs.getBoolean("hintPushHoldDismissed", false)
        vibrationEnabled = sharedPrefs.getBoolean("vibrationEnabled", true)
        theme = sharedPrefs.getString("theme", "dark")!!
        askMobileData = sharedPrefs.getBoolean("askMobileData", true)
        userName = sharedPrefs.getString("userName", "")!!
        gcFirstStart = sharedPrefs.getBoolean("gcFirstStart", true)
        gradientCreatorGradientName = sharedPrefs.getString("gradientCreatorGradientName", "")!!
        gradientCreatorStartColour = sharedPrefs.getString("gradientCreatorStartColour", "#acd77b")!!
        gradientCreatorEndColour = sharedPrefs.getString("gradientCreatorEndColour", "#74d77b")!!
        gradientCreatorDescription = sharedPrefs.getString("gradientCreatorDescription", "")!!
    }

    /*fun filterBrokenGradients() {
        try {
            for (count in 0 until gradientList.size) {
                if (gradientList[count]["backgroundName"]?.isNotEmpty()!! ){
                    val filtered = HashMap<String, String>()

                    filtered["backgroundName"] = gradientList[count]["backgroundName"] as String
                    filtered["startColour"] = gradientList[count]["startColour"] as String
                    filtered["endColour"] = gradientList[count]["endColour"] as String
                    filtered["description"] = gradientList[count]["description"] as String

                    filteredGradients.add(filtered)
                }
            }
            downloadingGradients = false
        } catch (e: Exception) {
            Log.e("ERR", "pebble.values.filter_broken_gradients: ${e.localizedMessage}")
        }
    }*/
}
