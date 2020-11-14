package com.simple.chris.pebble.activities

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.simple.chris.pebble.*
import com.simple.chris.pebble.adapters_helpers.PopupDialogButtonRecycler
import com.simple.chris.pebble.adapters_helpers.SettingsRecyclerView
import com.simple.chris.pebble.functions.*
import kotlinx.android.synthetic.main.activity_settings_new.*
import kotlinx.android.synthetic.main.activity_settings_new.backButton
import kotlinx.android.synthetic.main.activity_settings_new.bottomSheet
import kotlinx.android.synthetic.main.activity_settings_new.buttonIcon
import kotlinx.android.synthetic.main.activity_settings_new.coordinatorLayout
import kotlinx.android.synthetic.main.activity_settings_new.titleHolder
import kotlinx.android.synthetic.main.activity_settings_new.wallpaperImageAlpha
import kotlinx.android.synthetic.main.activity_settings_new.wallpaperImageViewer
import java.lang.Exception
import kotlin.math.roundToInt

class Settings : AppCompatActivity(), SettingsRecyclerView.OnButtonListener, PopupDialogButtonRecycler.OnButtonListener {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    private var screenHeight = 0
    private var bottomSheetPeekHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElement.setTheme(this)
        setContentView(R.layout.activity_settings_new)
        Values.currentActivity = "Settings"

        coordinatorLayout.post {
            settingsList.setHasFixedSize(true)
            val buttonLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            val buttonAdapter = SettingsRecyclerView(this, "settings", HashMaps.settingsArray(), this)

            settingsList.layoutManager = buttonLayoutManager
            settingsList.adapter = buttonAdapter
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                Log.e("INFO", "Cutout height: ${window.decorView.rootWindowInsets.displayCutout}")
            }
        }

        backButton.setOnClickListener {
            UIElement.killDialogs()
            onBackPressed()
        }
    }

    override fun onAttachedToWindow() {
        bottomSheet.post {
            calculateHeights()
            bottomSheet()
        }
        UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha, window)
    }

    private fun calculateHeights() {
        try {
            screenHeight = Calculations.screenMeasure(this, "height", window)

            bottomSheetPeekHeight = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                screenHeight
            } else {
                (screenHeight * (0.667)).toInt()
            }

            titleHolder.translationY = (((screenHeight * (0.333)) / 2) - (titleHolder.measuredHeight / 2)).toFloat()
            buttonIcon.translationY = (((screenHeight * (0.333)) / 8) - (titleHolder.measuredHeight / 8)).toFloat()
        } catch (e: Exception) {
            Log.e("ERR", "pebble.settings.calculate_height: ${e.localizedMessage}")
        }
    }

    private fun bottomSheet() {
        bottomSheet.background.alpha = (75 * 2.55).roundToInt()
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        //UIElements.bottomSheetPeekHeightAnim(bottomSheetBehavior, bottomSheetPeekHeight, 300, 0, DecelerateInterpolator(3f))
        bottomSheetBehavior.peekHeight = bottomSheetPeekHeight

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                titleHolder.translationY = ((screenHeight * (-0.333) * slideOffset + screenHeight * (0.333) - (titleHolder.measuredHeight)) / 2).toFloat()
                buttonIcon.translationY = ((screenHeight * (-0.333) * slideOffset + screenHeight * (0.333) - (titleHolder.measuredHeight)) / 8).toFloat()
                val cornerRadius = ((slideOffset * -1) + 1) * Calculations.convertToDP(this@Settings, 20f)
                val bottomShe = findViewById<CardView>(R.id.bottomSheet)
                bottomShe.radius = cornerRadius
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

        })
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

    override fun onButtonClick(screenName: String, position: Int, view: View) {
        when (position) {
            0 -> {
                //Theme
                UIElement.popupDialog(this, "settingTheme", R.drawable.icon_brush, R.string.word_theme, null, R.string.question_setting_theme,
                        HashMaps.lightDarkDarker(), window.decorView, this)
            }
            1 -> {
                //Vibration
                UIElement.popupDialog(this, "settingVibration", R.drawable.icon_vibrate_on, R.string.word_vibration, null, R.string.question_setting_vibration,
                        HashMaps.onOff(), window.decorView, this)
            }
            2 -> {
                //Special Effects
                UIElement.popupDialog(this, "settingSpecialEffects", R.drawable.icon_blur_on, R.string.dual_special_effects, null, R.string.question_setting_effects,
                        HashMaps.onOff(), window.decorView, this)
            }
            3 -> {
                //Cellular Data
                UIElement.popupDialog(this, "settingNetwork", R.drawable.icon_cell_wifi, R.string.word_network, null, R.string.question_setting_network,
                        HashMaps.onOffAsk(), window.decorView, this)
            }
        }
    }

    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        when (popupName) {
            "settingTheme" -> {
                when (position) {
                    0 -> {
                        //Light
                        Values.settingThemes = "light"
                    }
                    1 -> {
                        //Dark
                        Values.settingThemes = "dark"
                    }
                    2 -> {
                        //Darker
                        Values.settingThemes = "darker"
                    }
                }
                UIElement.popupDialogHider()
                Handler().postDelayed({
                    refreshTheme()
                }, 450)
            }
            "settingVibration" -> {
                when (position) {
                    0 -> {
                        //On
                        Values.settingVibrations = true
                    }
                    1 -> {
                        //Off
                        Values.settingVibrations = false
                    }
                }
                UIElement.popupDialogHider()
            }
            "settingSpecialEffects" -> {
                when (position) {
                    0 -> {
                        //On
                        Values.settingsSpecialEffects = true
                    }
                    1 -> {
                        //Off
                        Values.settingsSpecialEffects = false
                    }
                }
                UIElement.popupDialogHider()
                UIElement.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha)
            }
            "settingNetwork" -> {
                when (position) {
                    0 -> {
                        //On
                        Values.useMobileData = "on"
                    }
                    1 -> {
                        //Off
                        Values.useMobileData = "off"
                    }
                    2 -> {
                        //Ask Every-time
                        Values.useMobileData = "ask"
                    }
                }
                UIElement.popupDialogHider()
            }
        }
    }
}
