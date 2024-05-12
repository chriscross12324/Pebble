package com.simple.chris.pebble.functions

import android.content.Context
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.simple.chris.pebble.data.GradientObject
import com.simple.chris.pebble.dialogs.DialogPopup

/**
 * Stores all essential values, can be referenced and changed from Activities
 */
object Values {
    private const val SharedPrefs = "SavedValues"

    /**
     * User Settings - Values that can be directly changed by the user
     */
    var settingTheme = "dark"
    var settingVibration = true
    var settingSpecialEffects = true
    var settingSplitScreen = true

    /**
     * Session Values - Values that get saved for a future session
     */
    var gradientCreatorName = ""
    var gradientCreatorDescription = ""
    var gradientCreatorColours = ArrayList<String>()

    /**
     * App Constants - Values that are used throughout Pebble
     */
    val vibrationWeak = longArrayOf(0, 1)
    val vibrationMedium = longArrayOf(0, 7)
    val vibrationStrong = longArrayOf(0, 15)
    val vibrationNotification = longArrayOf(0, 15, 80, 10)

    const val dialogBackgroundDim = 0.75f
    const val dialogBackgroundTint = "#33000000"

    const val animationDuration = 500L




    private lateinit var fireBase: FirebaseFirestore
    var valuesLoaded = false
    var errorOccurred = false
    var recyclerBrowseAtTop = true


    //DialogFragment
    lateinit var dialogPopup: DialogPopup

    //Connection
    var gradientList: List<GradientObject> = listOf()

    //Hidden Values
    var firstStart = true
    var currentActivity: String = ""

    var dialogShowAgainTime: Long = 450
    var downloadingGradients = false
    var refreshTheme = false
    var currentlySplitScreened = false
    var animatingSharedElement = false
    var canDismissSharedElement = true
    var browseRecyclerScrollPos = 0

    //Gradient Details
    var gradientScreenName = ""
    var gradientScreenDesc = ""
    lateinit var gradientScreenColours: ArrayList<String>
    var currentGradientScreenPos = -1
    lateinit var currentGradientScreenView: View


    //Gradient Creator
    var justSubmitted = false
    var editingColourAtPos = 0


    /**
     * Saves all values to a SharedPreferences file - Ran whenever a value might have changed
     */
    fun saveValues(context: Context) {
        val sharedPrefs = context.getSharedPreferences(SharedPrefs, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putBoolean("firstStart", firstStart)
        editor.putInt("browseRecyclerScrollPos", browseRecyclerScrollPos)
        editor.putBoolean("settingVibrations", settingVibration)
        editor.putString("settingThemes", settingTheme)
        editor.putBoolean("settingsSpecialEffects", settingSpecialEffects)
        editor.putString("gradientCreatorGradientName", gradientCreatorName)
        editor.putString("gradientCreatorDescription", gradientCreatorDescription)
        editor.apply()
    }

    /**
     * Loads all values from a SharedPreferences file - Only used when app is opened or when a crash may happen
     */
    fun loadValues(context: Context) {
        val sharedPrefs = context.getSharedPreferences(SharedPrefs, Context.MODE_PRIVATE)
        valuesLoaded = true
        firstStart = sharedPrefs.getBoolean("firstStart", true)
        browseRecyclerScrollPos = sharedPrefs.getInt("browseRecyclerScrollPos", 0)
        settingVibration = sharedPrefs.getBoolean("settingVibrations", true)
        settingTheme = sharedPrefs.getString("settingThemes", "dark")!!
        settingSpecialEffects = sharedPrefs.getBoolean("settingsSpecialEffects", true)
        gradientCreatorName = sharedPrefs.getString("gradientCreatorGradientName", "")!!
        gradientCreatorDescription = sharedPrefs.getString("gradientCreatorDescription", "")!!
    }

    fun dialogFrag(dialog: DialogPopup) {
        dialogPopup = dialog
    }

    fun setFireStore(firebase: FirebaseFirestore) {
        this.fireBase = firebase
    }

    fun getFireStore(): FirebaseFirestore {
        return fireBase
    }
}
