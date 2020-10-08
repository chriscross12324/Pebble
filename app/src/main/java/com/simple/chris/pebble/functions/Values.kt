package com.simple.chris.pebble.functions

import android.content.Context

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
    var setupThemeChange = false
    var lastVersion = 0
    var hintPushHoldDismissed = false
    var hintCreateGradientDismissed = false
    var currentActivity: String = ""
    var gradientList: ArrayList<HashMap<String, String>> = ArrayList()
    var dialogShowAgainTime: Long = 450
    var downloadingGradients = false
    var refreshTheme = false
    var gradientCornerRadius = 25f
    var dontAskStorage = false
    var connectionOffline = false
    var screenHeight = 0

    //Changing Values
    var gradientIsTouched = false

    //Settings
    var useMobileData: String = "ask"

    var settingThemes = "dark"
    var settingVibrations = true
    var settingsSpecialEffects = true
    var settingsForegroundOpacity = true

    //Gradient Creator
    var gradientCreatorGradientName = ""
    var gradientCreatorStartColour = ""
    var gradientCreatorEndColour = ""
    var gradientCreatorDescription = ""
    var currentColourPOS = ""
    var currentColourInt = 0
    var currentColourHEX = ""
    var justSubmitted = false
    var gcFirstStart = true
    var gradientCreatorColours: ArrayList<String> = ArrayList()
    var editingColourAtPos = 0


    /**
     * Saves all values to a SharedPreferences file - Ran whenever a value might have changed
     */
    fun saveValues(context: Context) {
        val sharedPrefs = context.getSharedPreferences(SAVE, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean("firstStart", firstStart)
        editor.putInt("lastVersion", lastVersion)
        editor.putBoolean("hintPushHoldDismissed", hintPushHoldDismissed)
        editor.putBoolean("settingVibrations", settingVibrations)
        editor.putString("settingThemes", settingThemes)
        editor.putString("useMobileData", useMobileData)
        editor.putBoolean("settingsSpecialEffects", settingsSpecialEffects)
        editor.putBoolean("gcFirstStart", gcFirstStart)
        editor.putString("gradientCreatorGradientName", gradientCreatorGradientName)
        editor.putString("gradientCreatorStartColour", gradientCreatorStartColour)
        editor.putString("gradientCreatorEndColour", gradientCreatorEndColour)
        editor.putString("gradientCreatorDescription", gradientCreatorDescription)
        editor.putBoolean("hintCreateGradientDismissed", hintCreateGradientDismissed)
        editor.putBoolean("dontAskStorage", dontAskStorage)
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
        settingVibrations = sharedPrefs.getBoolean("settingVibrations", true)
        settingThemes = sharedPrefs.getString("settingThemes", "dark")!!
        useMobileData = sharedPrefs.getString("useMobileData", "ask")!!
        settingsSpecialEffects = sharedPrefs.getBoolean("settingsSpecialEffects", true)
        gcFirstStart = sharedPrefs.getBoolean("gcFirstStart", true)
        gradientCreatorGradientName = sharedPrefs.getString("gradientCreatorGradientName", "")!!
        gradientCreatorStartColour = sharedPrefs.getString("gradientCreatorStartColour", "")!!
        gradientCreatorEndColour = sharedPrefs.getString("gradientCreatorEndColour", "")!!
        gradientCreatorDescription = sharedPrefs.getString("gradientCreatorDescription", "")!!
        hintCreateGradientDismissed = sharedPrefs.getBoolean("hintCreateGradientDismissed", false)
        dontAskStorage = sharedPrefs.getBoolean("dontAskStorage", false)
    }
}
