package com.simple.chris.pebble

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.animation.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.simple.chris.pebble.Calculations.convertToDP
import com.simple.chris.pebble.UIElements.linearLayoutElevationAnimator
import com.simple.chris.pebble.UIElements.linearLayoutHeightAnimator
import com.simple.chris.pebble.UIElements.viewObjectAnimator
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.android.synthetic.main.activity_browse.*
import java.util.*
import kotlin.math.roundToInt

class ActivityBrowse : AppCompatActivity(), GradientRecyclerViewAdapter.OnGradientListener, GradientRecyclerViewAdapter.OnGradientLongClickListener {


    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>
    private var screenHeight = 0
    private var helloTextHeight = 0
    private var createGradientBannerHeight = 0
    private var bottomSheetPeekHeight = 0

    private var navigationMenuExpanded = false
    private val navigationMenuHeight = 250f

    private var secretNumber = 0
    private var secretAttempt = 0


    /*
    This is the Main Browse Activity, here users can view any gradient that is found on the database. This
    code allows users to navigate to different views as well as view any gradient fullscreen.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_browse)
        Values.currentActivity = "Browse"

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

    /*
    Gets the height of the screen and other UI elements to use for UI elements that reference screen size
     */
    private fun getHeights() {
        try {
            screenHeight = Calculations.screenMeasure(this, "height")

            helloTextHeight = helloText.height
            createGradientBannerHeight = createGradientBanner.height

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
        UIElements.gradientDrawable(this, true, createGradientBanner, ContextCompat.getColor(this, R.color.pebbleStart),
                ContextCompat.getColor(this, R.color.pebbleEnd), 20f)
    }

    private fun createGradient() {
        createGradientBanner.setOnClickListener {
            touchBlocker.visibility = View.VISIBLE
            viewObjectAnimator(createGradientDetailsHolder, "alpha", 0f, 150, 0, DecelerateInterpolator())

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

    /*
    Handles the Navigation Menu
     */
    private fun navigationMenu() {
        buttonOptions.setOnClickListener {
            if (!navigationMenuExpanded) {
                navigationMenuAnimation(View.VISIBLE, convertToDP(this, 50f), convertToDP(this, navigationMenuHeight),
                        convertToDP(this, 0f), convertToDP(this, 20f), R.drawable.icon_close, true)
                Vibration.mediumFeedback(this)
                //Toast.makeText(this, "Expanded", Toast.LENGTH_SHORT).show()
            } else {
                navigationMenuAnimation(View.GONE, convertToDP(this, navigationMenuHeight), convertToDP(this, 50f),
                        convertToDP(this, 20f), convertToDP(this, 0f), R.drawable.icon_menu, false)
            }
        }

        try {
            touchBlockerMenu.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    if (navigationMenuExpanded) {
                        navigationMenuAnimation(View.GONE, convertToDP(this, navigationMenuHeight), convertToDP(this, 50f),
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
                        navigationMenuAnimation(View.GONE, convertToDP(this, navigationMenuHeight), convertToDP(this, 50f),
                                convertToDP(this, 20f), convertToDP(this, 0f), R.drawable.icon_menu, false)
                    }
                }
                true
            }
            touchBlockerMenuRight.setOnTouchListener { _, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    if (navigationMenuExpanded) {
                        navigationMenuAnimation(View.GONE, convertToDP(this, navigationMenuHeight), convertToDP(this, 50f),
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

        buttonSettings.setOnClickListener {
            touchBlocker.visibility = View.VISIBLE
            viewObjectAnimator(settingsIcons, "rotation", -360f, 1000, 0, DecelerateInterpolator(3f))

            Handler().postDelayed({
                startActivity(Intent(this, Settings::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }, 550)

        }

        buttonFeedback.setOnClickListener {
            touchBlocker.visibility = View.VISIBLE
            startActivity(Intent(this, Feedback::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        buttonReload.setOnClickListener {

            viewObjectAnimator(reloadImage, "rotation", -360f, 1000, 0, DecelerateInterpolator(3f))

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

        /*buttonSearch.visibility = visibility
        //buttonSettings.visibility = visibility
        buttonFeedback.visibility = visibility
        buttonReload.visibility = visibility*/

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

                    viewObjectAnimator(createGradientDetailsHolder, "alpha", 1f, 250, 0, DecelerateInterpolator())
                }
                "GradientDetails" -> {
                    Values.currentActivity = "Browse"
                    touchBlocker.visibility = View.GONE
                }
                "SearchGradient" -> {
                    Values.currentActivity = "Browse"
                    touchBlocker.visibility = View.GONE
                    navigationMenuAnimation(View.GONE, convertToDP(this, navigationMenuHeight), convertToDP(this, 50f),
                            convertToDP(this, 20f), convertToDP(this, 0f), R.drawable.icon_menu, false)
                }
                "Feedback" -> {
                    touchBlocker.visibility = View.GONE
                    navigationMenuAnimation(View.GONE, convertToDP(this, navigationMenuHeight), convertToDP(this, 50f),
                            convertToDP(this, 20f), convertToDP(this, 0f), R.drawable.icon_menu, false)
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
                        navigationMenuAnimation(View.GONE, convertToDP(this, navigationMenuHeight), convertToDP(this, 50f),
                                convertToDP(this, 20f), convertToDP(this, 0f), R.drawable.icon_menu, false)
                    }, 250)

                }
            }
        }
    }

    private fun navigationDialog(x: Int, y: Int) {
        runOnUiThread {
            val navigationDialog = Dialog(this, R.style.dialogStyle)
            navigationDialog.setCancelable(true)
            navigationDialog.setContentView(R.layout.dialog_navigation_menu)

            val navigationHolder = navigationDialog.findViewById<LinearLayout>(R.id.navigationHolder)

            val dialogWindow = navigationDialog.window
            dialogWindow!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
            dialogWindow.setDimAmount(0.5f)
            dialogWindow.setGravity(Gravity.TOP)
            dialogWindow.attributes.x = x
            dialogWindow.attributes.y = y
            navigationDialog.show()

            navigationHolder.post {
                //linearLayoutHeightAnimator(navigationHolder, convertToDP(this, 50f), convertToDP(this, 200f), 700, 200, DecelerateInterpolator(3f))
                Handler().postDelayed({
                    navigationHolder.layoutParams.height = convertToDP(this, navigationMenuHeight).roundToInt()
                    navigationHolder.requestLayout()
                }, 1000)
                Log.e("INFO", "Animated")
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

        /*val radius = 20f
        val decorView = window.decorView
        val rootView = decorView.findViewById<ViewGroup>(android.R.id.content)
        val windowBackground = decorView.background

        blurView.setupWith(rootView)
                .setFrameClearDrawable(windowBackground)
                .setBlurAlgorithm(RenderScriptBlur(this))
                .setBlurRadius(radius)
                .setHasFixedTransformationMatrix(true)*/
    }
}
