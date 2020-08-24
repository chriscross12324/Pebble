package com.simple.chris.pebble

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
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
    private var createButtonExpanded = true
    private var createButtonExpandedWidth = 0
    private var menuHeight = 0
    private var menuWidth = 0

    /**
     * Browse Activity - Handles gradient RecyclerView, Gradient Creator Banner & Click events
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElement.setTheme(this)
        setContentView(R.layout.activity_browse)
        Values.currentActivity = "Browse"

        UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha)
        Connection.getGradients(this, window.decorView)

        coordinatorLayout.post {
            getHeights()
            bottomSheet()
            connectionChecker()
        }

        menuButton.setOnClickListener {
            touchBlockerDark.visibility = View.VISIBLE
            menuArrow.visibility = View.INVISIBLE
            menu.visibility = View.VISIBLE
            UIElements.viewObjectAnimator(menuArrow, "translationY", Calculations.convertToDP(this, 0f), 250, 500, DecelerateInterpolator())
            UIElements.viewVisibility(menuArrow, View.VISIBLE, 500)
        }
        menuButton.setOnLongClickListener {
            startActivity(Intent(this, Settings::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            true
        }

        touchBlockerDark.setOnClickListener {
            UIElements.viewObjectAnimator(menuArrow, "translationY", Calculations.convertToDP(this, -25f), 250, 0, DecelerateInterpolator())
            UIElements.viewVisibility(menuArrow, View.INVISIBLE, 250)
            Handler().postDelayed({
                touchBlockerDark.visibility = View.GONE
                menuArrow.visibility = View.GONE
                menu.visibility = View.GONE
            }, 350)

        }

        searchButton.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        createButton.setOnClickListener {
            startActivity(Intent(this, GradientCreator::class.java))
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
            createButtonExpandedWidth = createButton.measuredWidth

            menuHeight = menu.measuredHeight
            menuWidth = menu.measuredWidth
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
        bottomSheetBehavior.peekHeight = 0

        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            bottomSheetPeekHeight = Calculations.screenMeasure(this, "height")
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                titleHolder.translationY = ((screenHeight * (-0.333) * slideOffset + screenHeight * (0.333) - (titleHolder.measuredHeight)) / 2).toFloat()
                val cornerRadius = ((slideOffset * -1) + 1) * Calculations.convertToDP(this@BrowseActivity, 20f)
                val bottomShe = findViewById<CardView>(R.id.bottomSheet)
                bottomShe.radius = cornerRadius

                /*if (slideOffset >= 0.4f) {
                    if (createButtonExpanded) {
                        Log.e("INFO", "Don't Expand")
                        UIElement.animateViewWidth("height", createButton, Calculations.convertToDP(this@BrowseActivity, 50f).toInt(), 0)
                        UIElement.animateViewWidth("width", createButton, Calculations.convertToDP(this@BrowseActivity, 50f).toInt(), 0)
                        createButtonExpanded = false
                    }
                } else {
                    if (!createButtonExpanded) {
                        Log.e("Info", "Expand")
                        UIElement.animateViewWidth("height", createButton, Calculations.convertToDP(this@BrowseActivity, 60f).toInt(), 0)
                        UIElement.animateViewWidth("width", createButton, createButtonExpandedWidth, 0)
                        createButtonExpanded = true
                    }

                }*/
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

    private fun connectionChecker() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            @SuppressLint("SyntheticAccessor")
            override fun run() {
                if (Values.gradientList.isNotEmpty()) {
                    gradientsDownloaded()
                } else {
                    connectionChecker()
                }
            }
        }, 500)
    }

    private fun gradientsDownloaded() {
        if (Values.gradientList.isNotEmpty()) {
            RecyclerGrid.gradientGrid(this, browseGrid, Values.gradientList, this, this)
            UIElements.bottomSheetPeekHeightAnim(bottomSheetBehavior, bottomSheetPeekHeight, 750, 0, DecelerateInterpolator(3f))
            resultsText.text = "${Values.gradientList.size} gradients"
        } else {
            //Coming from different activity
        }
    }

    override fun onGradientClick(position: Int, view: View) {
        Vibration.lowFeedback(this)
        touchBlocker.visibility = View.VISIBLE
        RecyclerGrid.gradientGridOnClickListener(this, Values.gradientList, view, position)
    }

    override fun onGradientLongClick(position: Int, view: View) {
        Vibration.lowFeedback(this)
        val gradientScaleX = ObjectAnimator.ofFloat(view, "scaleX", 0.9f)
        val gradientScaleY = ObjectAnimator.ofFloat(view, "scaleY", 0.9f)
        gradientScaleX.duration = 125
        gradientScaleY.duration = 125
        gradientScaleX.interpolator = DecelerateInterpolator()
        gradientScaleY.interpolator = DecelerateInterpolator()
        gradientScaleX.start()
        gradientScaleY.start()

        Handler().postDelayed({
            gradientScaleX.reverse()
            gradientScaleY.reverse()

            RecyclerGrid.gradientGridOnLongClickListener(this, Values.gradientList, position, window.decorView)

            Handler().postDelayed({
                Vibration.mediumFeedback(this)
            }, 150)
        }, 150)
    }
}
