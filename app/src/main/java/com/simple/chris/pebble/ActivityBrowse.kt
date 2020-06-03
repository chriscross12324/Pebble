package com.simple.chris.pebble

import android.app.WallpaperManager
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import kotlinx.android.synthetic.main.activity_browse.wallpaperImageViewer
import kotlinx.android.synthetic.main.activity_main_menu.*


class ActivityBrowse : AppCompatActivity(), GradientRecyclerViewAdapter.OnGradientListener, GradientRecyclerViewAdapter.OnGradientLongClickListener {


    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    private var screenHeight = 0
    private var helloTextHeight = 0
    private var createGradientBannerHeight = 0
    private var bottomSheetPeekHeight = 0

    private var navigationMenuExpanded = false
    private val navigationMenuHeight = 250f

    /**
     * Browse Activity - Handles gradient RecyclerView, Gradient Creator Banner & Click events
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_browse)
        Values.currentActivity = "Browse"

        UIElements.setWallpaper(this, wallpaperImageViewer)

        coordinatorLayout.post {
            getHeights()
            bottomSheet()
        }
        RecyclerGrid.gradientGrid(this, browseGrid, Values.gradientList, this, this)

        backButton.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        searchButton.setOnClickListener {
            startActivity(Intent(this, SearchGradient::class.java))
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

            screenTitle.translationY = (((screenHeight * (0.333) - screenTitle.measuredHeight) / 2).toFloat())
            Log.e("INFO", "$bottomSheetPeekHeight")
        } catch (e: Exception) {
            Log.e("ERR", "pebble.browse.get_screen_height: ${e.localizedMessage}")
        }
    }

    /*
    Sets anything related to the bottomSheet
     */
    private fun bottomSheet() {
        //Toast.makeText(this, "Got here", Toast.LENGTH_SHORT).show()
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bottomSheetPeekHeight = Calculations.screenMeasure(this, "height")
        } else {
            bottomSheetBehavior.peekHeight = bottomSheetPeekHeight
        }
        UIElements.bottomSheetPeekHeightAnim(bottomSheetBehavior, bottomSheetPeekHeight, 300, 0, DecelerateInterpolator(3f))

        bottomSheetBehavior.setBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                //if (BottomSheetBehavior.STATE_DRAGGING == newState) Log.i("MainActivity", "onStateChanged  >>  " + bottomSheet.height)
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.e("INFO", "${((screenHeight * (0.333) * slideOffset)/2.toInt())}")
                screenTitle.translationY = (((screenHeight * (-0.333) * slideOffset) + (screenHeight * (0.333)) - (screenTitle.measuredHeight))/2).toFloat()
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

        //Checks to see if the Gradient Grid is still populated (known to depopulate if the app is paused for too long)
        if (browseGrid.adapter == null) {
            Values.loadValues(this)
            startActivity(Intent(this, ConnectingActivity::class.java))
        } else {
            Values.saveValues(this)

            when (Values.currentActivity) {
                "CreateGradient" -> {
                    Values.currentActivity = "Browse"
                    touchBlocker.visibility = View.GONE

                    //viewObjectAnimator(createGradientDetailsHolder, "alpha", 1f, 250, 0, DecelerateInterpolator())
                }
                "GradientDetails" -> {
                    Values.currentActivity = "Browse"
                    touchBlocker.visibility = View.GONE
                }
                "SearchGradient" -> {
                    Values.currentActivity = "Browse"
                    touchBlocker.visibility = View.GONE
                    /*navigationMenuAnimation(View.GONE, convertToDP(this, navigationMenuHeight), convertToDP(this, 50f),
                            convertToDP(this, 20f), convertToDP(this, 0f), R.drawable.icon_menu, false)*/
                }
                "Feedback" -> {
                    touchBlocker.visibility = View.GONE
                    /*navigationMenuAnimation(View.GONE, convertToDP(this, navigationMenuHeight), convertToDP(this, 50f),
                            convertToDP(this, 20f), convertToDP(this, 0f), R.drawable.icon_menu, false)*/
                }
                "SubmittedGradient" -> {
                    startActivity(Intent(this, ConnectingActivity::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }
                "Settings" -> {
                    Values.currentActivity = "Browse"
                    touchBlocker.visibility = View.GONE
                    Handler().postDelayed({
                        /*navigationMenuAnimation(View.GONE, convertToDP(this, navigationMenuHeight), convertToDP(this, 50f),
                                convertToDP(this, 20f), convertToDP(this, 0f), R.drawable.icon_menu, false)*/
                    }, 250)

                }
            }
        }
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
