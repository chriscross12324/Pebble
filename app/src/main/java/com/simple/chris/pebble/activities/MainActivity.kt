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
import com.simple.chris.pebble.R
import com.simple.chris.pebble.functions.Calculations
import com.simple.chris.pebble.functions.UIElement
import com.simple.chris.pebble.functions.UIElements
import com.simple.chris.pebble.functions.Values
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity() {
    var screenHeight = 0
    var bottomSheetPeekHeight = 0
    lateinit var browseFragment: Fragment
    lateinit var searchFragment: Fragment
    lateinit var gradientFragment: Fragment
    lateinit var settingsFragment: Fragment
    lateinit var fragmentManager: FragmentManager

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

    fun startSettingFrag() {
        Log.e("INFO", "Called")

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.smallScreenFragHolder, settingsFragment)
            commit()
            Log.e("INFO", "Showed")
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
}