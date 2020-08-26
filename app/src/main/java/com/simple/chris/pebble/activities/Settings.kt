package com.simple.chris.pebble.activities

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.simple.chris.pebble.*
import com.simple.chris.pebble.functions.Calculations
import com.simple.chris.pebble.functions.UIElement
import com.simple.chris.pebble.functions.UIElements
import com.simple.chris.pebble.functions.Values
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.bottomSheet
import kotlinx.android.synthetic.main.activity_settings.buttonIcon
import kotlinx.android.synthetic.main.activity_settings.coordinatorLayout
import kotlinx.android.synthetic.main.activity_settings.titleHolder
import kotlinx.android.synthetic.main.activity_settings.wallpaperImageAlpha
import kotlinx.android.synthetic.main.activity_settings.wallpaperImageViewer
import java.lang.Exception
import kotlin.math.roundToInt

class Settings : AppCompatActivity() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    private var screenHeight = 0
    private var bottomSheetPeekHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElement.setTheme(this)
        setContentView(R.layout.activity_settings)
        UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha)
        Values.currentActivity = "Settings"

        coordinatorLayout.post {
            calculateHeights()
            bottomSheet()
            initiateOptionsBackgrounds()
            setButtonBackgroundAlpha()
        }

        /** Set theme of app **/
        lightThemeOption.setOnClickListener {
            if (Values.settingThemes != "light") {
                Values.settingThemes = "light"
                setButtonBackgroundAlpha()
                refreshTheme()
            }
        }
        darkThemeOption.setOnClickListener {
            if (Values.settingThemes != "dark") {
                Values.settingThemes = "dark"
                setButtonBackgroundAlpha()
                refreshTheme()
            }
        }
        blackThemeOption.setOnClickListener {
            if (Values.settingThemes != "black") {
                Values.settingThemes = "black"
                setButtonBackgroundAlpha()
                refreshTheme()
            }
        }

        /** Set vibration of app **/
        onVibrationOption.setOnClickListener {
            Values.settingVibrations = true
            setButtonBackgroundAlpha()
        }
        offVibrationOption.setOnClickListener {
            Values.settingVibrations = false
            setButtonBackgroundAlpha()
        }

        /** Set specialEffects for app **/
        onSpecialOption.setOnClickListener {
            Values.settingsSpecialEffects = true
            setButtonBackgroundAlpha()
            UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha)
        }
        offSpecialOption.setOnClickListener {
            Values.settingsSpecialEffects = false
            setButtonBackgroundAlpha()
            UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha)
        }

        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun calculateHeights() {
        try {
            screenHeight = Calculations.screenMeasure(this, "height")

            bottomSheetPeekHeight = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                screenHeight
            } else {
                ((screenHeight * (0.667)) + Calculations.convertToDP(this, 16f)).toInt()
            }

            titleHolder.translationY = ((((screenHeight * (0.333) - titleHolder.measuredHeight) / 2) /*+ Calculations.convertToDP(this, 16f)*/).toFloat())
            screenDescription.text = "Customize Pebble"
        } catch (e: Exception) {
            Log.e("ERR", "pebble.settings.calculate_height: ${e.localizedMessage}")
        }
    }

    private fun bottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        //UIElements.bottomSheetPeekHeightAnim(bottomSheetBehavior, bottomSheetPeekHeight, 300, 0, DecelerateInterpolator(3f))
        bottomSheetBehavior.peekHeight = bottomSheetPeekHeight

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                titleHolder.translationY = (((screenHeight * (-0.333) * slideOffset + screenHeight * (0.333) - (titleHolder.measuredHeight)) / 2) /*+ Calculations.convertToDP(this@Settings, 16f)*/).toFloat()
                //Log.e("ERR", "$slideOffset")
                if (slideOffset > 0) {
                    titleHolder.alpha = ((slideOffset * -1) + 1)
                    buttonIcon.alpha = ((slideOffset * -0.1) + 0.1).toFloat()
                } else {
                    titleHolder.alpha = 1f
                    buttonIcon.alpha = 0.1f
                }
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

        })
    }

    private fun initiateOptionsBackgrounds() {
        themeOptions.background.alpha = (75 * 2.55).roundToInt()
        vibrationOptions.background.alpha = (75 * 2.55).roundToInt()
        specialOptions.background.alpha = (75 * 2.55).roundToInt()
    }

    private fun setButtonBackgroundAlpha() {
        when (Values.settingThemes) {
            "light" -> {
                lightThemeOption.background.alpha = (100 * 2.55).roundToInt()
                darkThemeOption.background.alpha = (40 * 2.55).roundToInt()
                blackThemeOption.background.alpha = (40 * 2.55).roundToInt()
            }
            "dark" -> {
                lightThemeOption.background.alpha = (40 * 2.55).roundToInt()
                darkThemeOption.background.alpha = (100 * 2.55).roundToInt()
                blackThemeOption.background.alpha = (40 * 2.55).roundToInt()
            }
            "black" -> {
                lightThemeOption.background.alpha = (40 * 2.55).roundToInt()
                darkThemeOption.background.alpha = (40 * 2.55).roundToInt()
                blackThemeOption.background.alpha = (100 * 2.55).roundToInt()
            }
        }

        when (Values.settingVibrations) {
            true -> {
                onVibrationOption.background.alpha = (100 * 2.55).roundToInt()
                offVibrationOption.background.alpha = (40 * 2.55).roundToInt()
            }
            false -> {
                onVibrationOption.background.alpha = (40 * 2.55).roundToInt()
                offVibrationOption.background.alpha = (100 * 2.55).roundToInt()
            }
        }

        when (Values.settingsSpecialEffects) {
            true -> {
                onSpecialOption.background.alpha = (100 * 2.55).roundToInt()
                offSpecialOption.background.alpha = (40 * 2.55).roundToInt()
            }
            false -> {
                onSpecialOption.background.alpha = (40 * 2.55).roundToInt()
                offSpecialOption.background.alpha = (100 * 2.55).roundToInt()
            }
        }
    }

    private fun refreshTheme() {
        Values.refreshTheme = true
        startActivity(Intent(this, Settings::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    override fun onResume() {
        super.onResume()

        /**
         * Checks if app settings unloaded during pause
         */
        if (!Values.valuesLoaded) {
            startActivity(Intent(this, SplashScreen::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}
