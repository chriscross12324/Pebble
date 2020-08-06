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
        UIElements.setTheme(this)
        setContentView(R.layout.activity_main_menu_new)
        UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha)

        refreshButton.setOnClickListener {
            refreshStatusText.text = "Connecting..."
            refreshStatusText.visibility = View.VISIBLE
            UIElements.viewObjectAnimator(refreshStatusIcon, "alpha", 0f, 100, 100, LinearInterpolator())
            UIElements.viewObjectAnimator(refreshStatusProgress, "alpha", 1f, 100, 100, LinearInterpolator())

            Handler().postDelayed({
                refreshStatusText.text = "Online"
                refreshStatusIcon.setImageResource(R.drawable.icon_check)
                UIElements.viewObjectAnimator(refreshStatusIcon, "alpha", 1f, 250, 0, LinearInterpolator())
                UIElements.viewObjectAnimator(refreshStatusProgress, "alpha", 0f, 200, 0, LinearInterpolator())

                Handler().postDelayed({
                    refreshStatusText.visibility = View.GONE
                    UIElements.textViewTextChanger(refreshStatusText, "Refresh", 250)
                    refreshStatusIcon.setImageResource(R.drawable.icon_reload)
                }, 1500)
            }, 4500)
        }

        refreshButton.setOnLongClickListener {
            refreshStatusText.visibility = View.VISIBLE
            Handler().postDelayed({
                refreshStatusText.visibility = View.GONE
            }, 2500)
            true
        }

        settingsButton.setOnClickListener {

        }

        settingsButton.setOnLongClickListener {
            settingsText.visibility = View.VISIBLE
            Handler().postDelayed({
                settingsText.visibility = View.GONE
            }, 2500)
            true
        }
    }
}