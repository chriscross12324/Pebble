package com.simple.chris.pebble

import android.animation.ValueAnimator
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import kotlinx.android.synthetic.main.activity_browse.*


class BrowseActivity : AppCompatActivity(), GradientRecyclerViewAdapter.OnGradientListener, GradientRecyclerViewAdapter.OnGradientLongClickListener {


    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    private var screenHeight = 0
    private var bottomSheetPeekHeight = 0

    /**
     * Browse Activity - Handles gradient RecyclerView, Gradient Creator Banner & Click events
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_browse)
        Values.currentActivity = "Browse"
        animateButtonIcon(0f, 0.1f)

        UIElements.setWallpaper(this, wallpaperImageViewer)

        coordinatorLayout.post {
            getHeights()
            bottomSheet()
        }
        RecyclerGrid.gradientGrid(this, browseGrid, Values.gradientList, this, this)

        backButton.setOnClickListener {
            animateButtonIcon(0.1f, 1f)
            onBackPressed()
        }

        searchButton.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    /**
     * Get/Set bottomSheetPeekHeight based on screen height and other elements
     */
    private fun getHeights() {
        try {
            screenHeight = Calculations.screenMeasure(this, "height")

            bottomSheetPeekHeight = (screenHeight * (0.667)).toInt()

            titleHolder.translationY = (((screenHeight * (0.333) - titleHolder.measuredHeight) / 2).toFloat())
            resultsText.text = "${Values.gradientList.size} gradients"
            Log.e("INFO", "$bottomSheetPeekHeight")
        } catch (e: Exception) {
            Log.e("ERR", "pebble.browse.get_screen_height: ${e.localizedMessage}")
        }
    }

    /*
    Sets anything related to the bottomSheet
     */
    private fun bottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bottomSheetPeekHeight = Calculations.screenMeasure(this, "height")
        } else {
            bottomSheetBehavior.peekHeight = bottomSheetPeekHeight
        }
        UIElements.bottomSheetPeekHeightAnim(bottomSheetBehavior, bottomSheetPeekHeight, 300, 0, DecelerateInterpolator(3f))

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                titleHolder.translationY = ((screenHeight * (-0.333) * slideOffset + screenHeight * (0.333) - (titleHolder.measuredHeight)) / 2).toFloat()
            }
        })

    }

    //Called when Activity Pauses and Finishes
    override fun onPause() {
        super.onPause()
        Values.saveValues(this)
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
        } else {
            when (Values.currentActivity) {
                else -> {
                    Values.currentActivity = "Browse"
                    touchBlocker.visibility = View.GONE
                }
            }
            Values.saveValues(this)
        }
    }

    private fun animateButtonIcon(start: Float, end: Float) {
        Handler().postDelayed({
            val valueAnimator = ValueAnimator.ofFloat(start, end)
            valueAnimator.duration = 250

            valueAnimator.addUpdateListener {
                val animatedValue = valueAnimator.animatedValue as Float
                buttonIcon.alpha = animatedValue
            }
            valueAnimator.start()
        }, 500)
    }

    override fun onGradientClick(position: Int, view: View) {
        Vibration.lowFeedback(this)
        touchBlocker.visibility = View.VISIBLE
        RecyclerGrid.gradientGridOnClickListener(this, Values.gradientList, view, position)
    }

    override fun onGradientLongClick(position: Int, view: View) {
        Vibration.strongFeedback(this)
        RecyclerGrid.gradientGridOnLongClickListener(this, Values.gradientList, position, blurLayout)
    }
}