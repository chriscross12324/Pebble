package com.simple.chris.pebble

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.util.Pair
import android.util.Pair.create
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.util.Pair.create
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.simple.chris.pebble.Calculations.convertToDP
import kotlinx.android.synthetic.main.activity_browse.*
import java.util.*

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

        val wallpaper: WallpaperManager = WallpaperManager.getInstance(this)
        val wallpaperDrawable: Drawable = wallpaper.drawable
        val bm: Bitmap = (wallpaperDrawable as BitmapDrawable).bitmap
        wallpaperImageViewer.setImageDrawable(wallpaperDrawable)

        coordinatorLayout.post {
            getHeights()
            bottomSheet()
            uiSet()
            getTime()
        }
        RecyclerGrid.gradientGrid(this, browseGrid, Values.gradientList, this, this)
        createGradient()
        navigationMenu()
    }

    /**
     * Get/Set bottomSheetPeekHeight based on screen height and other elements
     */
    private fun getHeights() {
        try {
            screenHeight = Calculations.screenMeasure(this, "height")

            helloTextHeight = helloText.height
            val helloTextLayoutParam = helloText.layoutParams as ViewGroup.MarginLayoutParams
            helloTextLayoutParam.topMargin
            //createGradientBannerHeight = createGradientBanner.height

            //bottomSheetPeekHeight = screenHeight - (convertToDP(this, 160f) + helloTextHeight + createGradientBannerHeight).roundToInt()
            //bottomSheetPeekHeight = screenHeight - (helloTextHeight + (helloTextLayoutParam.topMargin * 2)) + convertToDP(this, 13f).toInt()
            bottomSheetPeekHeight = screenHeight - (helloTextHeight + (helloTextLayoutParam.topMargin) + navigationHolderBrowse.height + convertToDP(this, 13f).toInt())
            //bottomSheetPeekHeight = convertToDP(this, 120f).toInt()
            //Toast.makeText(this, "$bottomSheetPeekHeight", Toast.LENGTH_SHORT).show()
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

    }

    @SuppressLint("NewApi")
    private fun uiSet() {
        UIElements.gradientDrawable(this, true, createGradientBanner, ContextCompat.getColor(this, R.color.pebbleStart),
                ContextCompat.getColor(this, R.color.pebbleEnd), 20f)
    }

    private fun createGradient() {
        createGradientBanner.setOnClickListener {
            touchBlocker.visibility = View.VISIBLE
            //viewObjectAnimator(createGradientDetailsHolder, "alpha", 0f, 150, 0, DecelerateInterpolator())

            val createGradientIntent = Intent(this, CreateGradientNew::class.java)
            val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, createGradientBanner, "gradientCreatorViewer")

            Handler().postDelayed({
                startActivity(createGradientIntent, activityOptions.toBundle())
            }, 150)
        }
    }

    private fun getTime() {
        val currentTime = Calendar.getInstance()
        val currentHour = currentTime.get(Calendar.HOUR_OF_DAY)
        Log.e("INFO", "Time: $currentHour")

        when {
            currentHour < 12 -> {
                //Good Morning
                helloText.text = getString(R.string.greeting_morning)
            }
            currentHour in 12..16 -> {
                //Good Afternoon
                helloText.text = getString(R.string.greeting_afternoon)
            }
            else -> {
                //Good Evening
                helloText.text = getString(R.string.greeting_evening)
            }
        }
    }

    /**
     * Navigation Menu - Click Events & Animations
     */
    private fun navigationMenu() {
        /*buttonOptions.setOnClickListener {
            if (!navigationMenuExpanded) {
                *//*navigationMenuAnimation(View.VISIBLE, convertToDP(this, 50f), convertToDP(this, navigationMenuHeight),
                        convertToDP(this, 0f), convertToDP(this, 20f), R.drawable.icon_close, true)*//*
                Vibration.mediumFeedback(this)
                UIElements.bottomSheetPeekHeightAnim(bottomSheetBehavior, screenHeight - (convertToDP(this, 160f) + helloTextHeight + createGradientBannerHeight).roundToInt(), 300, 0, DecelerateInterpolator())
                navigationMenuExpanded = true
                //Toast.makeText(this, "Expanded", Toast.LENGTH_SHORT).show()
            } else {
                *//*navigationMenuAnimation(View.GONE, convertToDP(this, navigationMenuHeight), convertToDP(this, 50f),
                        convertToDP(this, 20f), convertToDP(this, 0f), R.drawable.icon_menu, false)*//*
                Vibration.mediumFeedback(this)
                UIElements.bottomSheetPeekHeightAnim(bottomSheetBehavior, bottomSheetPeekHeight, 300, 0, DecelerateInterpolator())
                navigationMenuExpanded = false
            }
        }*/

        /*buttonOptions.setOnLongClickListener(object : OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                if (!navigationMenuExpanded) {
                    navigationMenuAnimation(View.VISIBLE, convertToDP(this, 50f), convertToDP(this, navigationMenuHeight),
                            convertToDP(this, 0f), convertToDP(this, 20f), R.drawable.icon_close, true)
                    navigationMenuExpanded = true
                    //Toast.makeText(this, "Expanded", Toast.LENGTH_SHORT).show()
                } else {
                    navigationMenuAnimation(View.GONE, convertToDP(this, navigationMenuHeight), convertToDP(this, 50f),
                            convertToDP(this, 20f), convertToDP(this, 0f), R.drawable.icon_menu, false)
                    navigationMenuExpanded = false
                }
                return true
            }

        })*/

        //UIElements.bottomSheetPeekHeightAnim(bottomSheetBehavior, screenHeight - (convertToDP(this, 160f) + helloTextHeight + createGradientBannerHeight).roundToInt(), 700, 0, DecelerateInterpolator())

        touchBlockerMenu.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                if (navigationMenuExpanded) {
                    /*navigationMenuAnimation(View.GONE, convertToDP(this, navigationMenuHeight), convertToDP(this, 50f),
                            convertToDP(this, 20f), convertToDP(this, 0f), R.drawable.icon_menu, false)*/
                }
            }
            true
        }

        buttonSearch.setOnClickListener {
            touchBlocker.visibility = View.VISIBLE
            val searchGradientIntent = Intent(this, SearchGradient::class.java)
            val p1 = androidx.core.util.Pair.create<View, String>(buttonSearch, "searchBackground")
            val p2 = androidx.core.util.Pair.create<View, String>(searchText, "searchText")
            val searchShared = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1, p2)

            startActivity(searchGradientIntent, searchShared.toBundle())
        }

        /*buttonSettings.setOnClickListener {
            touchBlocker.visibility = View.VISIBLE
            viewObjectAnimator(settingsIcons, "rotation", -360f, 1000, 0, DecelerateInterpolator(3f))

            Handler().postDelayed({
                startActivity(Intent(this, Settings::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }, 550)

        }*/

        buttonFeedback.setOnClickListener {
            touchBlocker.visibility = View.VISIBLE
            startActivity(Intent(this, Feedback::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        /*buttonReload.setOnClickListener {

            viewObjectAnimator(reloadImage, "rotation", -360f, 1000, 0, DecelerateInterpolator(3f))

            Handler().postDelayed({
                startActivity(Intent(this, ConnectingActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }, 550)
        }*/
    }

    /*
    Animates the Navigation Menu
     */
    /*private fun navigationMenuAnimation(visibility: Int, startSize: Float, endSize: Float, startElevation: Float, endElevation: Float, drawable: Int, expanded: Boolean) {
        touchBlocker.visibility = View.VISIBLE
        touchBlockerMenu.visibility = visibility

        linearLayoutHeightAnimator(navigationHolder, startSize, endSize, 400, 0, DecelerateInterpolator(3f))
        linearLayoutElevationAnimator(navigationHolder, startElevation, endElevation, 400, 0, DecelerateInterpolator(3f))

        imageOptionsButton.setBackgroundResource(drawable)

        navigationMenuExpanded = expanded

        Handler().postDelayed({
            touchBlocker.visibility = View.GONE
        }, 420)
    }*/

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
