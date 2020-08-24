package com.simple.chris.pebble

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import kotlinx.android.synthetic.main.activity_main_menu_new.*

class MainMenuTest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElement.setTheme(this)
        setContentView(R.layout.activity_main_menu_new)
        UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha)

        UIElement.popupDialog(this, "connecting", null, R.string.dialog_title_eng_connecting, null, R.string.dialog_body_eng_connecting, null, window.decorView, null)

        Handler().postDelayed({
            UIElement.popupDialogHider()
        }, 2000)
    }
}