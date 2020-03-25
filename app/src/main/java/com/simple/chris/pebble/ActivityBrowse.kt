package com.simple.chris.pebble

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.simple.chris.pebble.Calculations.convertToDP
import com.simple.chris.pebble.UIElements.constraintLayoutObjectAnimator
import com.simple.chris.pebble.UIElements.linearLayoutElevationAnimator
import com.simple.chris.pebble.UIElements.linearLayoutHeightAnimator
import kotlin.math.roundToInt

class ActivityBrowse : AppCompatActivity() {

    private lateinit var optionsHolder: LinearLayout
    private lateinit var buttonOptions: LinearLayout
    private lateinit var buttonSearch: LinearLayout
    private lateinit var buttonSettings: LinearLayout
    private lateinit var buttonReload: LinearLayout

    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var viewTreeObserver: ViewTreeObserver

    private lateinit var bottomSheet: CardView
    private lateinit var createGradientBanner: ConstraintLayout
    private lateinit var createGradientBannerDetails: ConstraintLayout
    private lateinit var browseGrid: RecyclerView
    private lateinit var touchBlockerMenu: View
    private lateinit var touchBlocker: View
    private lateinit var imageOptionsButton: ImageView
    private lateinit var searchIcon: ImageView
    private lateinit var reloadImage: ImageView
    private lateinit var helloText: TextView

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

        //Initiate LinearLayouts - Navigation Menu
        optionsHolder = findViewById(R.id.optionsHolder)
        buttonOptions = findViewById(R.id.buttonOptions)
        buttonSearch = findViewById(R.id.buttonSearch)
        buttonSettings = findViewById(R.id.buttonSettings)
        buttonReload = findViewById(R.id.buttonReload)

        //Initiate Misc. Layouts
        bottomSheet = findViewById(R.id.bottomSheet) //CardView
        createGradientBanner = findViewById(R.id.createGradientBanner) //ConstraintLayout
        createGradientBannerDetails = findViewById(R.id.createGradientBannerDetails) //ConstraintLayout
        browseGrid = findViewById(R.id.browseGrid) //RecyclerView
        touchBlockerMenu = findViewById(R.id.touchBlockerMenu) //View
        touchBlocker = findViewById(R.id.touchBlocker) //View
        imageOptionsButton = findViewById(R.id.imageOptionsButton) //ImageView
        searchIcon = findViewById(R.id.searchIcon) //ImageView
        reloadImage = findViewById(R.id.reloadImage) //ImageView
        helloText = findViewById(R.id.helloText) //TextView


        //Run code once the Gradient Grid is created
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        viewTreeObserver = coordinatorLayout.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener {
            getHeights()
            bottomSheet()
            uiSet()
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
        bottomSheet = findViewById(R.id.bottomSheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.peekHeight = bottomSheetPeekHeight
    }

    private fun uiSet() {
        val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(resources.getColor(R.color.pebbleStart), resources.getColor(R.color.pebbleEnd))
        )
        gradientDrawable.cornerRadius = convertToDP(this, 20f)
        createGradientBanner.background = gradientDrawable
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            createGradientBanner.outlineSpotShadowColor = resources.getColor(R.color.pebbleEnd)
        }
    }

    private fun browseGrid() {
        try {
            browseGrid = this.findViewById(R.id.browseGrid)
            browseGrid.setHasFixedSize(true)

            val gridLayoutManager = GridLayoutManager(this, 2)
            browseGrid.layoutManager = gridLayoutManager

            val browseGridAdapter = BrowseRecyclerViewAdapter(this, Values.browse)
            browseGrid.adapter = browseGridAdapter
            browseGridAdapter.setClickListener { view, position ->
                Vibration.lowFeedback(this)
                touchBlocker.visibility = View.VISIBLE
                val details = Intent(this, ActivityGradientDetailsK::class.java)
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
        } catch (e: Exception) {
            Log.e("ERR", "pebble.browse.browse_grid: " + e.localizedMessage)
        }
    }

    private fun createGradient(){
        createGradientBanner.setOnClickListener {
            touchBlocker.visibility = View.VISIBLE
            constraintLayoutObjectAnimator(createGradientBannerDetails, "alpha", 0f, 150, 0, DecelerateInterpolator())

            val createGradientIntent = Intent(this, CreateGradient::class.java)
            val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, createGradientBanner, "gradientCreatorViewer")

            Handler().postDelayed({
                startActivity(createGradientIntent, activityOptions.toBundle())
            }, 150)
        }
    }

    /*
    Handles the Navigation Menu
     */
    private fun navigationMenu() {
        buttonOptions.setOnClickListener {
            if (!navigationMenuExpanded) {
                navigationMenuAnimation(View.VISIBLE, convertToDP(this, 50f), convertToDP(this, 200f),
                        convertToDP(this, 0f), convertToDP(this, 20f), R.drawable.icon_close, true)
                Vibration.mediumFeedback(this)
            } else {
                navigationMenuAnimation(View.GONE, convertToDP(this, 200f), convertToDP(this, 50f),
                        convertToDP(this, 20f), convertToDP(this, 0f), R.drawable.icon_menu, false)
            }
        }

        touchBlockerMenu.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                if (navigationMenuExpanded) {
                    navigationMenuAnimation(View.GONE, convertToDP(this, 200f), convertToDP(this, 50f),
                            convertToDP(this, 20f), convertToDP(this, 0f), R.drawable.icon_menu, false)
                }
            }
            true
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
                startActivity(Intent(this, ActivityConnecting::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }, 700)

        }
    }

    /*
    Animates the Navigation Menu
     */
    private fun navigationMenuAnimation(visibility: Int, startSize: Float, endSize: Float, startElevation: Float, endElevation: Float, drawable: Int, expanded: Boolean) {
        touchBlocker.visibility = View.VISIBLE
        touchBlockerMenu.visibility = visibility
        buttonSearch.visibility = visibility
        buttonSettings.visibility = visibility
        buttonReload.visibility = visibility

        linearLayoutHeightAnimator(optionsHolder, startSize, endSize, 400, 0, DecelerateInterpolator(3f))
        linearLayoutElevationAnimator(optionsHolder, startElevation, endElevation, 400 , 0, DecelerateInterpolator(3f))

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
            startActivity(Intent(this, ActivityConnecting::class.java))
        } else {
            Values.saveValues(this)

            when (Values.currentActivity) {
                "CreateGradient" -> {
                    Values.currentActivity = "Browse"
                    touchBlocker.visibility = View.GONE

                    constraintLayoutObjectAnimator(createGradientBannerDetails, "alpha", 1f, 250, 0, DecelerateInterpolator())
                }
                "GradientDetails" -> {
                    Values.currentActivity = "Browse"
                    touchBlocker.visibility = View.GONE
                }
                "SearchGradient" -> {
                    Values.currentActivity = "Browse"
                    touchBlocker.visibility = View.GONE
                    navigationMenuAnimation(View.GONE, convertToDP(this, 200f), convertToDP(this, 50f),
                            convertToDP(this, 20f), convertToDP(this, 0f), R.drawable.icon_menu, false)
                }
            }
        }
    }
}
