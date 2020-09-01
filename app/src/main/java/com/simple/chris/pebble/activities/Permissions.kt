package com.simple.chris.pebble.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.simple.chris.pebble.R
import com.simple.chris.pebble.adapters_helpers.PopupDialogButtonRecycler
import com.simple.chris.pebble.functions.HashMaps
import com.simple.chris.pebble.functions.UIElement
import com.simple.chris.pebble.functions.UIElements
import com.simple.chris.pebble.functions.Values
import kotlinx.android.synthetic.main.activity_permissions.*
import kotlin.random.Random

class Permissions : AppCompatActivity(), PopupDialogButtonRecycler.OnButtonListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElement.setTheme(this)
        setContentView(R.layout.activity_permissions)
        UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha)

        getStarted.setOnClickListener {
            UIElements.viewObjectAnimator(welcomeHolder, "alpha", 0f, 125, 0, LinearInterpolator())
            UIElement.popupDialog(this, "networkNotice", R.drawable.icon_wifi_full, R.string.word_network, null, R.string.sentence_permission_network,
                    HashMaps.understandArray(), window.decorView, this)
        }

        if (Values.setupThemeChange) {
            Values.setupThemeChange = false
            UIElements.viewObjectAnimator(welcomeHolder, "alpha", 0f, 0, 0, LinearInterpolator())
            UIElement.popupDialog(this, "settingSpecialEffects", R.drawable.icon_blur_on, R.string.dual_special_effects, null, R.string.question_setting_effects,
                    HashMaps.onOff(), window.decorView, this)
        }
    }

    private fun checkStoragePermission() {
        Handler().postDelayed({
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                checkStoragePermission()
            } else {
                UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha)
            }
        }, 1000)
    }

    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        when (popupName) {
            "networkNotice" -> {
                UIElement.popupDialogHider()
                UIElement.popupDialog(this, "storagePermission", R.drawable.icon_storage, R.string.word_storage, null, R.string.sentence_needs_storage_permission,
                        HashMaps.allowDeny(), window.decorView, this)
            }
            "storagePermission" -> {
                when (position) {
                    0 -> {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                        checkStoragePermission()
                    }
                }
                UIElement.popupDialogHider()
                UIElement.popupDialog(this, "settingVibration", R.drawable.icon_vibrate_on, R.string.word_vibration, null, R.string.question_setting_vibration,
                        HashMaps.onOff(), window.decorView, this)
            }
            "settingVibration" -> {
                when (position) {
                    0 -> {
                        Values.settingVibrations = true
                    }
                    1 -> {
                        Values.settingVibrations = false
                    }
                }
                UIElement.popupDialogHider()
                UIElement.popupDialog(this, "settingTheme", R.drawable.icon_theme_dark, R.string.word_theme, null, R.string.question_setting_theme,
                        HashMaps.lightDarkDarker(), window.decorView, this)
            }
            "settingTheme" -> {
                when (position) {
                    0 -> {
                        Values.settingThemes = "light"
                    }
                    1 -> {
                        Values.settingThemes = "dark"
                    }
                    2 -> {
                        Values.settingThemes = "black"
                    }
                }
                UIElement.popupDialogHider()
                Values.setupThemeChange = true
                startActivity(Intent(this, Permissions::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
            "settingSpecialEffects" -> {
                when (position) {
                    0 -> {
                        Values.settingsSpecialEffects = true
                    }
                    1 -> {
                        Values.settingsSpecialEffects = false
                    }
                }
                UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha)
                UIElement.popupDialogHider()
                UIElement.popupDialog(this, "finishingSetup", null, R.string.word_finishing, null, R.string.sentence_finishing_setup,
                        null, window.decorView, null)
                Values.firstStart = false
                Values.saveValues(this)
                Handler().postDelayed({
                    UIElement.popupDialogHider()
                    Handler().postDelayed({
                        startActivity(Intent(this, Browse::class.java))
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }, 450)
                }, Random.nextLong(5000, 8000))
            }
        }
    }
}
