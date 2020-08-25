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
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import kotlinx.android.synthetic.main.activity_browse.*
import com.simple.chris.pebble.UIElement.startActivityFade
import kotlinx.android.synthetic.main.activity_browse.bottomSheet
import kotlinx.android.synthetic.main.activity_browse.buttonIcon
import kotlinx.android.synthetic.main.activity_browse.coordinatorLayout
import kotlinx.android.synthetic.main.activity_browse.resultsText
import kotlinx.android.synthetic.main.activity_browse.titleHolder
import kotlinx.android.synthetic.main.activity_browse.touchBlocker
import kotlinx.android.synthetic.main.activity_browse.wallpaperImageAlpha
import kotlinx.android.synthetic.main.activity_browse.wallpaperImageViewer
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.activity_search.*


class BrowseActivity : AppCompatActivity(), GradientRecyclerViewAdapter.OnGradientListener, GradientRecyclerViewAdapter.OnGradientLongClickListener, BrowseMenuRecyclerViewAdapter.OnButtonListener, PopupDialogButtonRecyclerAdapter.OnButtonListener {


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
        if (!Values.refreshTheme) {
            if (Values.gradientList.isEmpty()) {
                Connection.getGradients(this, window.decorView)
                if (!Permissions.readStoragePermissionGiven(this)) {
                    //UIElements.oneButtonDialog(this, R.drawable.icon_storage, R.string.dialog_title_eng_permission_storage, R.string.dialog_body_eng_permission_storage_read, R.string.text_eng_ok, storageDialogListener)
                }
            }
        } else {
            Values.refreshTheme = false
        }

        UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha)

        coordinatorLayout.post {
            getHeights()
            bottomSheet()
            connectionChecker()
            browseMenuButtons()
        }

        menuButton.setOnClickListener {
            touchBlockerDark.visibility = View.VISIBLE
            menuArrow.visibility = View.INVISIBLE
            menu.visibility = View.VISIBLE
            menu.layoutParams.height = Calculations.convertToDP(this, 60f).toInt()
            UIElements.viewObjectAnimator(touchBlockerDark, "alpha", 1f, 250, 0, LinearInterpolator())
            UIElements.viewObjectAnimator(menu, "alpha", 1f, 250, 0, LinearInterpolator())
            UIElement.animateViewWidth("height", menu, Calculations.viewWrapContent(menu, "height"), 50, 500)
            UIElements.viewObjectAnimator(menuArrow, "translationY", Calculations.convertToDP(this, 0f), 250, 250, DecelerateInterpolator())
            UIElements.viewVisibility(menuArrow, View.VISIBLE, 250)
            Vibration.lowFeedback(this)
        }

        touchBlockerDark.setOnClickListener {
            hideMenu()
        }

        searchButton.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            Vibration.mediumFeedback(this)
        }

        createButton.setOnClickListener {
            startActivity(Intent(this, GradientCreator::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            Vibration.mediumFeedback(this)
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
            buttonIcon.translationY = (((screenHeight * (0.333) - titleHolder.measuredHeight) / 8).toFloat())
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
                buttonIcon.translationY = ((screenHeight * (-0.333) * slideOffset + screenHeight * (0.333) - (titleHolder.measuredHeight)) / 8).toFloat()
                val cornerRadius = ((slideOffset * -1) + 1) * Calculations.convertToDP(this@BrowseActivity, 20f)
                val bottomShe = findViewById<CardView>(R.id.bottomSheet)
                bottomShe.radius = cornerRadius

                if (slideOffset >= 0.4f) {
                    if (createButtonExpanded) {
                        Log.e("INFO", "Don't Expand")
                        UIElement.animateViewWidth("height", createButton, Calculations.convertToDP(this@BrowseActivity, 35f).toInt(), 0, 250)
                        UIElement.animateViewWidth("width", createButton, Calculations.convertToDP(this@BrowseActivity, 50f).toInt(), 0, 250)
                        createButtonExpanded = false
                    }
                } else {
                    if (!createButtonExpanded) {
                        Log.e("Info", "Expand")
                        UIElement.animateViewWidth("height", createButton, Calculations.convertToDP(this@BrowseActivity, 60f).toInt(), 0, 250)
                        UIElement.animateViewWidth("width", createButton, Calculations.viewWrapContent(createButton, "width") + Calculations.convertToDP(this@BrowseActivity, 11f).toInt(), 0, 250)
                        createButtonExpanded = true
                    }

                }
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
                    UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha)
                }
            }
            Values.saveValues(this)

            if (Values.refreshTheme) {
                startActivity(Intent(this, SplashScreen::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }
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
        Handler().postDelayed({
            if (Values.gradientList.isNotEmpty()) {
                if (bottomSheetBehavior.peekHeight != 0) {
                    UIElements.viewObjectAnimator(browseGrid, "scaleX", 0.6f, 350, 0, AccelerateInterpolator(3f))
                    UIElements.viewObjectAnimator(browseGrid, "scaleY", 0.6f, 350, 0, AccelerateInterpolator(3f))
                    UIElements.viewObjectAnimator(browseGrid, "alpha", 0f, 150, 200, LinearInterpolator())

                    Handler().postDelayed({
                        UIElements.viewObjectAnimator(browseGrid, "scaleX", 1f, 0, 0, LinearInterpolator())
                        UIElements.viewObjectAnimator(browseGrid, "scaleY", 1f, 0, 0, LinearInterpolator())
                        UIElements.viewObjectAnimator(browseGrid, "alpha", 1f, 0, 0, LinearInterpolator())
                        RecyclerGrid.gradientGrid(this, browseGrid, Values.gradientList, this, this)
                        resultsText.text = "${Values.gradientList.size} gradients"
                        browseGrid.scheduleLayoutAnimation()
                    }, 500)
                } else {
                    RecyclerGrid.gradientGrid(this, browseGrid, Values.gradientList, this, this)
                    resultsText.text = "${Values.gradientList.size} gradients"
                    UIElements.bottomSheetPeekHeightAnim(bottomSheetBehavior, bottomSheetPeekHeight, 750, 0, DecelerateInterpolator(3f))
                    browseGrid.scheduleLayoutAnimation()
                }
            }
        }, 500)
    }

    private fun hideMenu() {
        UIElement.animateViewWidth("height", menu, Calculations.convertToDP(this, 55f).toInt(), 0, 400)
        UIElements.viewObjectAnimator(menu, "alpha", 0f, 175, 125, LinearInterpolator())
        UIElements.viewObjectAnimator(menuArrow, "translationY", Calculations.convertToDP(this, -25f), 150, 0, DecelerateInterpolator())
        UIElements.viewVisibility(menuArrow, View.INVISIBLE, 150)
        UIElements.viewObjectAnimator(touchBlockerDark, "alpha", 0f, 175, 175, LinearInterpolator())
        Handler().postDelayed({
            touchBlockerDark.visibility = View.GONE
            menuArrow.visibility = View.GONE
            menu.visibility = View.GONE
        }, 400)
    }

    private fun browseMenuButtons() {
        browseMenu.setHasFixedSize(true)
        val buttonLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        val buttonAdapter = BrowseMenuRecyclerViewAdapter(this, AppHashMaps.browseMenuArray(), this)

        browseMenu.layoutManager = buttonLayoutManager
        browseMenu.adapter = buttonAdapter
        //browseMenu.addItemDecoration(DividerItemDecoration(browseMenu.context, DividerItemDecoration.VERTICAL))
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

    override fun onButtonClick(position: Int, view: View) {
        when (position) {
            0 -> {
                /*startActivity(Intent(this, Feedback::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)*/
            }
            1 -> {
                hideMenu()
                Handler().postDelayed({
                    startActivity(Intent(this, Feedback::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }, 400)
            }
            2 -> {
                hideMenu()
                Handler().postDelayed({
                    startActivity(Intent(this, Settings::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }, 400)
            }
            3 -> {
                hideMenu()
                Handler().postDelayed({
                    Values.gradientList.clear()
                    Connection.getGradients(this, window.decorView)
                    connectionChecker()
                }, 400)
            }
        }
    }

    override fun onBackPressed() {
        UIElement.popupDialog(this, "leave", R.drawable.icon_door, R.string.dialog_title_eng_leave, null, R.string.dialog_body_eng_leave, AppHashMaps.arrayYesCancel(), window.decorView, this)
    }

    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        when (popupName) {
            "leave" -> {
                when (position) {
                    0 -> {
                        finish()
                    }
                    1 -> UIElement.popupDialogHider()
                }
            }
        }
    }
}
