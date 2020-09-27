@file:Suppress("DEPRECATION")

package com.simple.chris.pebble.activities

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.simple.chris.pebble.R
import com.simple.chris.pebble.adapters_helpers.BrowseMenuRecyclerView
import com.simple.chris.pebble.adapters_helpers.GradientRecyclerView
import com.simple.chris.pebble.adapters_helpers.PopupDialogButtonRecycler
import com.simple.chris.pebble.functions.*
import com.simple.chris.pebble.functions.Connection.checkConnection
import com.simple.chris.pebble.functions.Connection.connectionOffline
import com.simple.chris.pebble.functions.Connection.getGradients
import kotlinx.android.synthetic.main.activity_browse.*


class Browse : AppCompatActivity(), GradientRecyclerView.OnGradientListener, GradientRecyclerView.OnGradientLongClickListener, BrowseMenuRecyclerView.OnButtonListener, PopupDialogButtonRecycler.OnButtonListener {


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
                checkConnection(this, window.decorView, this)
            } else {
                Log.e("ERR", "Not empty")
            }
        } else {
            Values.refreshTheme = false
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
            startActivity(Intent(this, Search::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            Vibration.mediumFeedback(this)
        }

        createButton.setOnClickListener {
            Vibration.mediumFeedback(this)

            if (Values.connectionOffline) {
                UIElement.popupDialog(this, "noConnection", R.drawable.icon_wifi_empty, R.string.dual_no_connection, null, R.string.sentence_needs_internet_connection,
                        HashMaps.noConnectionArrayList(), window.decorView, this)
            } else {
                val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, gradientCreatorSharedElementView, "gradientCreatorViewer")
                startActivity(Intent(this, GradientCreator::class.java), activityOptions.toBundle())
                //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }
        }
    }

    override fun onAttachedToWindow() {
        bottomSheet.post {
            getHeights()
            bottomSheet()
        }
        connectionChecker()
        browseMenuButtons()
        UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha, window)
    }

    /**
     * Get/Set bottomSheetPeekHeight based on screen height and other elements
     */
    private fun getHeights() {
        try {
            screenHeight = Calculations.screenMeasure(this, "height", window)

            bottomSheetPeekHeight = (screenHeight * (0.667)).toInt()

            titleHolder.translationY = (((screenHeight * (0.333)) / 2) - (titleHolder.measuredHeight / 2)).toFloat()
            buttonIcon.translationY = (((screenHeight * (0.333)) / 8) - (titleHolder.measuredHeight / 8)).toFloat()
            createButtonExpandedWidth = createButton.measuredWidth

            menuHeight = menu.measuredHeight
            menuWidth = menu.measuredWidth
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
            bottomSheetPeekHeight = Calculations.screenMeasure(this, "height", window)
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                titleHolder.translationY = ((screenHeight * (-0.333) * slideOffset + screenHeight * (0.333) - (titleHolder.measuredHeight)) / 2).toFloat()
                buttonIcon.translationY = ((screenHeight * (-0.333) * slideOffset + screenHeight * (0.333) - (titleHolder.measuredHeight)) / 8).toFloat()
                val cornerRadius = ((slideOffset * -1) + 1) * Calculations.convertToDP(this@Browse, 20f)
                val bottomShe = findViewById<CardView>(R.id.bottomSheet)
                bottomShe.radius = cornerRadius

                if (slideOffset >= 0.4f) {
                    if (createButtonExpanded) {
                        UIElement.animateViewWidth("height", createButton, Calculations.convertToDP(this@Browse, 35f).toInt(), 0, 250)
                        UIElement.animateViewWidth("width", createButton, Calculations.convertToDP(this@Browse, 50f).toInt(), 0, 250)
                        createButtonExpanded = false
                    }
                } else {
                    if (!createButtonExpanded) {
                        UIElement.animateViewWidth("height", createButton, Calculations.convertToDP(this@Browse, 60f).toInt(), 0, 250)
                        UIElement.animateViewWidth("width", createButton, Calculations.viewWrapContent(createButton, "width") + Calculations.convertToDP(this@Browse, 11f).toInt(), 0, 250)
                        createButtonExpanded = true
                    }

                }

                //browseGrid.setPadding(browseGrid.paddingLeft, Calculations.convertToDP(this@Browse, ((Calculations.cutoutHeight(window) * slideOffset) + 16f)).toInt(), browseGrid.paddingRight, browseGrid.paddingBottom)
                //Toast.makeText(this@Browse, "${browseGrid.paddingTop + Calculations.convertToDP(this@Browse, (Calculations.cutoutHeight(window) * slideOffset))}", Toast.LENGTH_SHORT).show()
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
                    UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha, window)
                }
            }
            Values.saveValues(this)

            if (Values.refreshTheme) {
                startActivity(Intent(this, SplashScreen::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }

            if (Values.justSubmitted) {
                gradientsDownloaded()
                Values.justSubmitted = false
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
                screenTitle.setText(R.string.word_browse)
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
        val buttonAdapter = BrowseMenuRecyclerView(this, HashMaps.browseMenuArray(), this)

        browseMenu.layoutManager = buttonLayoutManager
        browseMenu.adapter = buttonAdapter
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
            /*0 -> {
                hideMenu()
                Handler().postDelayed({
                    startActivity(Intent(this, MyGradients::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }, 400)
            }*/
            0 -> {
                hideMenu()
                Handler().postDelayed({
                    startActivity(Intent(this, Feedback::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }, 400)
            }
            /*2 -> {
                hideMenu()
                Handler().postDelayed({
                    startActivity(Intent(this, Support::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }, 400)
            }*/
            1 -> {
                hideMenu()
                Handler().postDelayed({
                    startActivity(Intent(this, Settings::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                }, 400)
            }
            2 -> {
                hideMenu()
                Handler().postDelayed({
                    Values.gradientList.clear()
                    checkConnection(this, window.decorView, this)
                    connectionChecker()
                }, 400)
            }
        }
    }

    override fun onBackPressed() {
        UIElement.popupDialog(this, "leave", R.drawable.icon_door, R.string.word_leave, null, R.string.question_leave, HashMaps.arrayYesCancel(), window.decorView, this)
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
            "stillConnecting" -> {
                when (position) {
                    0 -> {
                        UIElement.popupDialog(this, "connecting", null, R.string.word_connecting, null, R.string.sentence_pebble_is_connecting, null, window.decorView, null)
                        Connection.checkDownload(this, window.decorView, this)
                    }
                    1 -> {
                        Connection.cancelConnection()
                        connectionOffline(this)
                    }
                    2 -> {
                        Connection.cancelConnection()
                        checkConnection(this, window.decorView, this)
                    }
                }
            }
            "askMobile" -> {
                when (position) {
                    0 -> {
                        getGradients(this, window.decorView, this)
                        UIElement.popupDialogHider()
                    }
                    1 -> {
                        Values.useMobileData = "on"
                        getGradients(this, window.decorView, this)
                        UIElement.popupDialogHider()
                    }
                    2 -> {
                        UIElement.popupDialogHider()
                        Handler().postDelayed({
                            checkConnection(this, window.decorView, this)
                        }, Values.dialogShowAgainTime)
                    }
                }
            }
            "gradientSaved" -> {
                when (position) {
                    0 -> {
                        UIElement.popupDialogHider()
                    }
                    1 -> {
                        UIElement.popupDialogHider()
                    }
                }
            }

            "noConnection" -> {
                when (position) {
                    0 -> {
                        UIElement.popupDialogHider()
                        Handler().postDelayed({
                            checkConnection(this, window.decorView, this)
                        }, Values.dialogShowAgainTime)
                    }
                    1 -> {
                        UIElement.popupDialogHider()
                        connectionOffline(this)
                    }
                }
            }

            "offlineMode" -> {
                when (position) {
                    0 -> {
                        UIElement.popupDialogHider()
                        Handler().postDelayed({
                            checkConnection(this, window.decorView, this)
                        }, Values.dialogShowAgainTime)
                    }
                    1 -> {
                        UIElement.popupDialogHider()
                        connectionOffline(this)
                    }
                }
            }

            "storagePermission" -> {
                when (position) {
                    0 -> {
                        UIElement.popupDialogHider()
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                    }
                    1 -> {
                        UIElement.popupDialogHider()
                    }
                    2 -> {
                        UIElement.popupDialogHider()
                        Values.dontAskStorage = true
                        Values.saveValues(this)
                    }
                }
            }
        }
    }
}
