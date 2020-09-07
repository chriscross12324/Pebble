package com.simple.chris.pebble.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.simple.chris.pebble.*
import com.simple.chris.pebble.adapters_helpers.SupportRecyclerView
import com.simple.chris.pebble.functions.*
import kotlinx.android.synthetic.main.activity_support.*

class Support : AppCompatActivity(), SupportRecyclerView.OnClickListener {


    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    private var screenHeight = 0
    private var bottomSheetPeekHeight = 0

    /**
     * Browse Activity - Handles gradient RecyclerView, Gradient Creator Banner & Click events
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElement.setTheme(this)
        setContentView(R.layout.activity_support)
        UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha, window)

        coordinatorLayout.post {
            getHeights()
            bottomSheet()
            recycler()
        }

        backButton.setOnClickListener {
            onBackPressed()
        }

    }

    /**
     * Get/Set bottomSheetPeekHeight based on screen height and other elements
     */
    private fun getHeights() {
        try {
            screenHeight = Calculations.screenMeasure(this, "height", window)

            bottomSheetPeekHeight = (screenHeight * (0.667)).toInt()

            titleHolder.translationY = (((screenHeight * (0.333) - titleHolder.measuredHeight) / 2).toFloat())
            buttonIcon.translationY = (((screenHeight * (0.333) - titleHolder.measuredHeight) / 8).toFloat())
        } catch (e: Exception) {
            Log.e("ERR", "pebble.browse.get_screen_height: ${e.localizedMessage}")
        }
    }

    /**
     * Sets anything related to the bottomSheet
     */
    private fun bottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = bottomSheetPeekHeight

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bottomSheetPeekHeight = Calculations.screenMeasure(this, "height", window)
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                titleHolder.translationY = ((screenHeight * (-0.333) * slideOffset + screenHeight * (0.333) - (titleHolder.measuredHeight)) / 2).toFloat()
                buttonIcon.translationY = ((screenHeight * (-0.333) * slideOffset + screenHeight * (0.333) - (titleHolder.measuredHeight)) / 8).toFloat()
                val cornerRadius = ((slideOffset * -1) + 1) * Calculations.convertToDP(this@Support, 20f)
                val bottomShe = findViewById<CardView>(R.id.bottomSheet)
                bottomShe.radius = cornerRadius
            }
        })

    }

    private fun recycler() {
        supportGrid.setHasFixedSize(true)
        val buttonLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        val buttonAdapter = SupportRecyclerView(this, HashMaps.supportHashMaps(), this)

        supportGrid.layoutManager = buttonLayoutManager
        supportGrid.adapter = buttonAdapter
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
            Values.saveValues(this)
        }
    }

    override fun onButtonClick(position: Int, view: View) {
        when (position) {
            0 -> {

            }
            1 -> {

            }
            2 -> {

            }
            3 -> {

            }
            4 -> {

            }
            5 -> {

            }
            6 -> {

            }
            7 -> {

            }
            8 -> {

            }
            9 -> {

            }
            10 -> {

            }
        }
    }
}
