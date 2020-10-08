package com.simple.chris.pebble.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.simple.chris.pebble.R
import com.simple.chris.pebble.adapters_helpers.PopupDialogButtonRecycler
import com.simple.chris.pebble.adapters_helpers.SettingsRecyclerView
import com.simple.chris.pebble.functions.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.ssDescription
import kotlinx.android.synthetic.main.activity_main.ssIcon
import kotlinx.android.synthetic.main.activity_main.ssRecycler
import kotlinx.android.synthetic.main.activity_main.ssTitle
import kotlinx.android.synthetic.main.small_screen.*

class MainActivity : FragmentActivity(), SettingsRecyclerView.OnButtonListener, PopupDialogButtonRecycler.OnButtonListener {
    var screenHeight = 0
    var bottomSheetPeekHeight = 0
    lateinit var browseFragment: Fragment
    lateinit var searchFragment: Fragment
    lateinit var gradientFragment: Fragment
    lateinit var settingsFragment: Fragment
    lateinit var fragmentManager: FragmentManager
    private var currentSmallScreen = "null"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElement.setTheme(this)
        setContentView(R.layout.activity_main)

        browseFragment = BrowseFrag()
        searchFragment = SearchFrag()
        gradientFragment = GradientFrag()
        settingsFragment = SettingsFrag()

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentHolder, browseFragment)
            commit()
        }

        tapReturn.setOnClickListener {
            hideSmallScreen()
        }
    }

    override fun onAttachedToWindow() {
        UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha, window)
        fragmentHolder.post {
            Values.screenHeight = Calculations.screenMeasure(this, "height", window)
            bottomSheetPeekHeight = (screenHeight * (0.667)).toInt()
        }
    }

    fun startSettings() {
        if (currentSmallScreen != "settings") {
            currentSmallScreen = "settings"
            ssIcon.setImageResource(R.drawable.icon_settings)
            ssTitle.setText(R.string.word_settings)
            ssDescription.setText(R.string.sentence_customize_pebble)

            ssRecycler.setHasFixedSize(true)
            val buttonLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val buttonAdapter = SettingsRecyclerView(this, HashMaps.settingsArray(), this)

            ssRecycler.layoutManager = buttonLayoutManager
            ssRecycler.adapter = buttonAdapter
        }
        ssRecycler.post {
            showSmallScreen(smallScreenFragHolder.measuredHeight.toFloat())
        }
    }

    fun startDonating() {
        if (currentSmallScreen != "donate") {
            currentSmallScreen = "donate"
            ssIcon.setImageResource(R.drawable.icon_money)
            ssTitle.setText(R.string.word_donate)
            ssDescription.setText(R.string.sentence_donate)

            ssRecycler.setHasFixedSize(true)
            val buttonLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            val buttonAdapter = SettingsRecyclerView(this, HashMaps.donateArray(), this)

            ssRecycler.layoutManager = buttonLayoutManager
            ssRecycler.adapter = buttonAdapter
        }
        ssRecycler.post {
            showSmallScreen(smallScreenFragHolder.measuredHeight.toFloat())
        }
    }

    private fun shrinkFrag(height: Float) {
        UIElements.viewObjectAnimator(fragmentHolder, "scaleY", 0.7f, 500, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(fragmentHolder, "scaleX", 0.7f, 500, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(fragmentHolder, "translationY", -(height * 0.8).toFloat(), 500, 0, DecelerateInterpolator(3f))
        UIElement.cardRadiusAnimator(fragmentHolder, Calculations.convertToDP(this, 30f), 500, 0, DecelerateInterpolator(3f))
    }

    private fun growFrag() {
        UIElements.viewObjectAnimator(fragmentHolder, "scaleY", 1f, 500, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(fragmentHolder, "scaleX", 1f, 500, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(fragmentHolder, "translationY", 0f, 500, 0, DecelerateInterpolator(3f))
        UIElement.cardRadiusAnimator(fragmentHolder, 0f, 500, 0, DecelerateInterpolator(3f))
        Log.e("INFO", "Growing")
    }

    fun showSmallScreen(height: Float) {
        tapReturn.visibility = View.VISIBLE
        tapReturnText.visibility = View.VISIBLE
        shrinkFrag(height)
        UIElements.viewObjectAnimator(smallScreenFragHolder, "translationY", -height, 500, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(tapReturn, "alpha", 0.6f, 250, 0, LinearInterpolator())
        UIElements.viewObjectAnimator(tapReturnText, "alpha", 1f, 250, 0, LinearInterpolator())
        tapReturnText.translationY = (-height/2)
    }

    fun hideSmallScreen() {
        growFrag()
        UIElements.viewObjectAnimator(smallScreenFragHolder, "translationY", 0f, 500, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(tapReturn, "alpha", 0f, 150, 0, LinearInterpolator())
        UIElements.viewObjectAnimator(tapReturnText, "alpha", 0f, 150, 0, LinearInterpolator())
        Handler().postDelayed({
            tapReturn.visibility = View.GONE
            tapReturnText.visibility = View.GONE
        }, 150)
        //onBackPressed()
    }

    fun refreshTheme() {
        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentHolder)
        (browseFragment as BrowseFrag).gridToTop()
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 500)
    }

    override fun onButtonClick(position: Int, view: View) {
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
                val current = Values.settingThemes
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
                    if (current != Values.settingThemes) {
                        hideSmallScreen()
                        refreshTheme()
                    }
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
                UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha, window)
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
        Values.saveValues(this)
    }
}