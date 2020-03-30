package com.simple.chris.pebble

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.simple.chris.pebble.Calculations.convertToDP
import com.simple.chris.pebble.UIElements.constraintLayoutObjectAnimator
import com.simple.chris.pebble.UIElements.linearLayoutElevationAnimator
import com.simple.chris.pebble.UIElements.linearLayoutHeightAnimator
import kotlinx.android.synthetic.main.activity_browse.*
import java.util.*
import kotlin.math.roundToInt

class ActivityBrowse : AppCompatActivity(), GradientRecyclerViewAdapter.OnGradientListener {


    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    private var screenHeight = 0
    private var helloTextHeight = 0
    private var createGradientBannerHeight = 0
    private var bottomSheetPeekHeight = 0

    private var navigationMenuExpanded = false


    /*
    This is the Main Browse Activity, here users can view any gradient that is found on the database. This
    code allows users to navigate to different views as well as view any gradient fullscreen.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_browse)

        coordinatorLayout.post {
            getHeights()
            bottomSheet()
            uiSet()
            getTime()
        }
        browseGrid()
        createGradient()
        navigationMenu()
    }

    /*
    Gets the height of the screen and other UI elements to use for UI elements that reference screen size
     */
    private fun getHeights() {
        try {
            screenHeight = Calculations.screenMeasure(this, "height")

            helloTextHeight = helloText.measuredHeight
            createGradientBannerHeight = createGradientBanner.measuredHeight

            bottomSheetPeekHeight = screenHeight - (convertToDP(this, 160f) + helloTextHeight + createGradientBannerHeight).roundToInt()
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
    }

    @SuppressLint("NewApi")
    private fun uiSet() {
        val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(ContextCompat.getColor(this, R.color.pebbleStart), ContextCompat.getColor(this, R.color.pebbleEnd))
        )
        gradientDrawable.cornerRadius = convertToDP(this, 20f)
        createGradientBanner.background = gradientDrawable
        if (Calculations.isAndroidPOrGreater()) {
            createGradientBanner.outlineSpotShadowColor = ContextCompat.getColor(this, R.color.pebbleEnd)
        }
    }

    private fun browseGrid() {
        try {
            browseGrid.setHasFixedSize(true)

            val gridLayoutManager = GridLayoutManager(this, 2)
            browseGrid.layoutManager = gridLayoutManager
            val browseGridAdapter = GradientRecyclerViewAdapter(this, Values.browse, this)
            browseGrid.adapter = browseGridAdapter
        } catch (e: Exception) {
            Log.e("ERR", "pebble.browse.browse_grid: " + e.localizedMessage)
        }
    }

    private fun createGradient() {
        /*createGradientBanner.setOnClickListener {
            touchBlocker.visibility = View.VISIBLE
            constraintLayoutObjectAnimator(createGradientDetailsHolder, "alpha", 0f, 150, 0, DecelerateInterpolator())

            val createGradientIntent = Intent(this, CreateGradientNew::class.java)
            val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, createGradientBanner, "gradientCreatorViewer")

            Handler().postDelayed({
                startActivity(createGradientIntent, activityOptions.toBundle())
            }, 150)
        }*/
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

    /*
    Handles the Navigation Menu
     */
    private fun navigationMenu() {
        buttonOptions.setOnClickListener {
            if (!navigationMenuExpanded) {
                navigationMenuAnimation(View.VISIBLE, convertToDP(this, 50f), convertToDP(this, 150f),
                        convertToDP(this, 0f), convertToDP(this, 20f), R.drawable.icon_close, true)
                Vibration.mediumFeedback(this)
                //Toast.makeText(this, "Expanded", Toast.LENGTH_SHORT).show()
            } else {
                navigationMenuAnimation(View.GONE, convertToDP(this, 150f), convertToDP(this, 50f),
                        convertToDP(this, 20f), convertToDP(this, 0f), R.drawable.icon_menu, false)
            }
        }

        try {
            touchBlockerMenu.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    if (navigationMenuExpanded) {
                        navigationMenuAnimation(View.GONE, convertToDP(this, 150f), convertToDP(this, 50f),
                                convertToDP(this, 20f), convertToDP(this, 0f), R.drawable.icon_menu, false)
                    }
                }
                true
            }
        } catch (e: Exception) {
            Log.e("ERR", "touchBlockerMenu not available in Landscape mode")
        }

        try {
            touchBlockerMenuLeft.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    if (navigationMenuExpanded) {
                        navigationMenuAnimation(View.GONE, convertToDP(this, 150f), convertToDP(this, 50f),
                                convertToDP(this, 20f), convertToDP(this, 0f), R.drawable.icon_menu, false)
                    }
                }
                true
            }
            touchBlockerMenuRight.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    if (navigationMenuExpanded) {
                        navigationMenuAnimation(View.GONE, convertToDP(this, 150f), convertToDP(this, 50f),
                                convertToDP(this, 20f), convertToDP(this, 0f), R.drawable.icon_menu, false)
                    }
                }
                true
            }
        } catch (e: Exception) {
            Log.e("ERR", "touchBlockerMenuLeft/Right not available in Portrait mode")
        }


        buttonSearch.setOnClickListener {
            touchBlocker.visibility = View.VISIBLE
            val searchGradientIntent = Intent(this, SearchGradient::class.java)
            val searchGradientActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, searchIcon, "sharedSearchElement")

            startActivity(searchGradientIntent, searchGradientActivityOptions.toBundle())
        }

        buttonReload.setOnClickListener {

            UIElements.imageViewObjectAnimator(reloadImage, "rotation", -360f, 1000, 0, DecelerateInterpolator(3f))

            Handler().postDelayed({
                startActivity(Intent(this, ConnectingActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }, 550)
        }
    }

    /*
    Animates the Navigation Menu
     */
    private fun navigationMenuAnimation(visibility: Int, startSize: Float, endSize: Float, startElevation: Float, endElevation: Float, drawable: Int, expanded: Boolean) {
        touchBlocker.visibility = View.VISIBLE
        try {
            touchBlockerMenu.visibility = visibility
        } catch (e: Exception) {
        }
        try {
            touchBlockerMenuLeft.visibility = visibility
            touchBlockerMenuRight.visibility = visibility
        } catch (e: Exception) {
        }

        buttonSearch.visibility = visibility
        //buttonSettings.visibility = visibility
        buttonReload.visibility = visibility

        linearLayoutHeightAnimator(navigationHolder, startSize, endSize, 400, 0, DecelerateInterpolator(3f))
        linearLayoutElevationAnimator(navigationHolder, startElevation, endElevation, 400, 0, DecelerateInterpolator(3f))

        imageOptionsButton.setBackgroundResource(drawable)

        navigationMenuExpanded = expanded

        Handler().postDelayed({
            touchBlocker.visibility = View.GONE
        }, 420)
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

                    constraintLayoutObjectAnimator(createGradientDetailsHolder, "alpha", 1f, 250, 0, DecelerateInterpolator())
                }
                "GradientDetails" -> {
                    Values.currentActivity = "Browse"
                    touchBlocker.visibility = View.GONE
                }
                "SearchGradient" -> {
                    Values.currentActivity = "Browse"
                    touchBlocker.visibility = View.GONE
                    navigationMenuAnimation(View.GONE, convertToDP(this, 150f), convertToDP(this, 50f),
                            convertToDP(this, 20f), convertToDP(this, 0f), R.drawable.icon_menu, false)
                }
            }
        }
    }

    override fun onGradientClick(position: Int, view: View) {
        Vibration.lowFeedback(this)
        touchBlocker.visibility = View.VISIBLE
        val details = Intent(this, ActivityGradientDetails::class.java)
        val info = Values.browse[position]

        val gradientName = info["backgroundName"]
        val startColour = info["startColour"]
        val endColour = info["endColour"]
        val description = info["description"]

        details.putExtra("gradientName", gradientName)
        details.putExtra("startColour", startColour)
        details.putExtra("endColour", endColour)
        details.putExtra("description", description)

        val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, view.findViewById(R.id.gradient), gradientName as String)
        startActivity(details, activityOptions.toBundle())
    }
}
