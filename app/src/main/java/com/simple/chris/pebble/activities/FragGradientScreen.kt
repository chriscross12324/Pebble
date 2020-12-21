package com.simple.chris.pebble.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.WallpaperManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Toast
import androidx.core.view.marginTop
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.simple.chris.pebble.R
import com.simple.chris.pebble.adapters_helpers.PopupDialogButtonRecycler
import com.simple.chris.pebble.adapters_helpers.SaveGradientDialog
import com.simple.chris.pebble.adapters_helpers.SearchColourRecyclerView
import com.simple.chris.pebble.functions.*
import kotlinx.android.synthetic.main.fragment_gradient_screen.*
import kotlinx.android.synthetic.main.fragment_gradient_screen.actionsHolder
import kotlinx.android.synthetic.main.fragment_gradient_screen.detailsHolder
import kotlin.Exception

class FragGradientScreen : Fragment(R.layout.fragment_gradient_screen), PopupDialogButtonRecycler.OnButtonListener, SearchColourRecyclerView.OnButtonListener {
    private lateinit var context: Activity
    private lateinit var gradientName: String
    private lateinit var gradientDescription: String
    private lateinit var gradientColourArray: ArrayList<String>

    private var savedFileName = ""
    private var optionsExpanded = false
    private var optionsAnimating = false
    private var notificationShowing = false
    private var animatingFullscreen = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = (activity as MainActivity)
    }

    fun startSplitScreen() {
        /** Called if SplitScreen started from fresh, or if fullscreen**/
        try {
            gradientViewer.background = null
            backgroundDimmer.alpha = 0f
        } catch (e: Exception) {
        }

        /** Set local values from Values.kt **/
        gradientName = Values.gradientScreenName
        gradientDescription = Values.gradientScreenDesc
        gradientColourArray = Values.gradientScreenColours

        /** Wait for UI to populate **/
        Handler(Looper.getMainLooper()).postDelayed({
            if (detailsHolder != null) {
                /** Set UI Elements **/
                optionsExpanded = false
                optionsHolder.visibility = View.INVISIBLE
                gradientNameText.text = gradientName
                gradientDescriptionText.text = gradientDescription
                gradientDescriptionText.visibility = if (gradientDescription == "") View.GONE else View.VISIBLE
                UIElements.viewObjectAnimator(backgroundDimmer, "alpha", 0f, 0, 0, LinearInterpolator())
                colourRecycler()

                /** Set position of UI Elements **/
                UIElements.viewObjectAnimator(detailsHolder, "translationY",
                        (90 * resources.displayMetrics.density) + detailsHolder.height,
                        0, 0, LinearInterpolator())
                UIElements.viewObjectAnimator(actionsHolder, "translationY",
                        (74 * resources.displayMetrics.density) + detailsHolder.height,
                        0, 0, LinearInterpolator())

                /** Tell MainActivity everything is ready **/
                tellMainReady()
            } else {
                startSplitScreen()
            }
        }, 50)
    }

    fun continueSplitScreen() {
        /** Called if SplitScreen is already showing Gradient Screen**/

        /** Set local values from Values.kt **/
        gradientName = Values.gradientScreenName
        gradientDescription = Values.gradientScreenDesc
        gradientColourArray = Values.gradientScreenColours

        /** Wait for UI to populate **/
        Handler(Looper.getMainLooper()).postDelayed({
            if (detailsHolder != null) {
                /** Animates away UI Elements **/
                UIElements.viewObjectAnimator(detailsHolder, "translationY",
                        (90 * resources.displayMetrics.density) + detailsHolder.height,
                        500, 30, DecelerateInterpolator(3f))
                UIElements.viewObjectAnimator(actionsHolder, "translationY",
                        (74 * resources.displayMetrics.density) + detailsHolder.height,
                        500, 0, DecelerateInterpolator(3f))

                Handler(Looper.getMainLooper()).postDelayed({
                    /** Set UI Elements **/
                    optionsExpanded = false
                    detailsHolder.visibility = View.INVISIBLE
                    actionsHolder.visibility = View.INVISIBLE
                    optionsHolder.visibility = View.INVISIBLE
                    gradientNameText.text = gradientName
                    gradientDescriptionText.text = gradientDescription
                    gradientDescriptionText.visibility = if (gradientDescription == "") View.GONE else View.VISIBLE
                    colourRecycler()

                    /** Set position of UI Elements **/
                    UIElements.viewObjectAnimator(detailsHolder, "translationY",
                            (90 * resources.displayMetrics.density) + detailsHolder.height,
                            0, 0, LinearInterpolator())
                    UIElements.viewObjectAnimator(actionsHolder, "translationY",
                            (74 * resources.displayMetrics.density) + detailsHolder.height,
                            0, 0, LinearInterpolator())

                    /** Tell MainActivity everything is ready **/
                    tellMainReady()
                }, 700)
            } else {
                continueSplitScreen()
            }
        }, 50)


    }

    private fun tellMainReady() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (Values.canDismissSharedElement) {
                (activity as MainActivity).endSharedElement()
                buttonFunctionality()
            } else {
                tellMainReady()
            }
        }, 50)
    }

    fun showUI() {
        /** Set visibility for UI Elements **/
        UIElement.gradientDrawableNew(context, gradientViewer, gradientColourArray, 0f)
        UIElements.viewVisibility(gradientViewer, View.VISIBLE, 0)
        UIElements.viewVisibility(detailsHolder, View.VISIBLE, 0)
        UIElements.viewVisibility(actionsHolder, View.VISIBLE, 0)

        UIElements.viewObjectAnimator(detailsHolder, "translationY",
                0f, 700, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(actionsHolder, "translationY",
                0f, 700, 50, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(backgroundDimmer, "alpha", 1f, 500, 0, LinearInterpolator())

        /** Tell app ready for another gradient **/
        Handler(Looper.getMainLooper()).postDelayed({
            Values.animatingSharedElement = false
        }, 750)
    }

    private fun hideUI() {
        /** Animate UI Elements out **/
        UIElements.viewObjectAnimator(detailsHolder, "translationY",
                (90 * resources.displayMetrics.density) + detailsHolder.height,
                500, 30, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(actionsHolder, "translationY",
                (74 * resources.displayMetrics.density) + detailsHolder.height,
                500, 0, DecelerateInterpolator(3f))

        Handler(Looper.getMainLooper()).postDelayed({
            optionsHolder.visibility = View.INVISIBLE
            optionsExpanded = false
        }, 100)

        /** Tell MainActivity to hide secondary **/
        Handler(Looper.getMainLooper()).postDelayed({
            (activity as MainActivity).hideFullScreen(false)
        }, 550)
    }

    fun animateBackgroundDimmer() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (backgroundDimmer != null) {
                UIElements.viewObjectAnimator(backgroundDimmer, "alpha", 1f, 500, 0, LinearInterpolator())
            } else {
                animateBackgroundDimmer()
                Log.e("ERR", "Run")
            }
        }, 10)

    }

    private fun colourRecycler() {
        colourElementsRecycler.setHasFixedSize(true)
        val buttonLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val buttonAdapter = SearchColourRecyclerView(context, null, Values.gradientScreenColours, this)

        colourElementsRecycler.layoutManager = buttonLayoutManager
        colourElementsRecycler.adapter = buttonAdapter
    }

    private fun runNotification(icon: Int, text: Int) {
        /** Check that notification isn't already showing **/
        if (!notificationShowing) {
            /** Set initial UI Elements **/
            notificationShowing = true
            notificationIcon.setImageResource(icon)
            notificationText.setText(text)

            /** Animates notification in **/
            Handler(Looper.getMainLooper()).postDelayed({
                Vibration.notification(context)
                notification.visibility = View.VISIBLE
                UIElements.viewObjectAnimator(notification, "translationY", (notification.height + Calculations.cutoutHeight(context.window) +
                        Calculations.convertToDP(context, 16f)), 500, 0, DecelerateInterpolator(3f))
                Handler(Looper.getMainLooper()).postDelayed({
                    UIElements.viewObjectAnimator(notification, "translationY", 0f, 500, 0, DecelerateInterpolator(3f))
                    Handler(Looper.getMainLooper()).postDelayed({
                        notification.visibility = View.INVISIBLE
                        notificationShowing = false
                    }, 500)
                }, 4000)
            }, 500)

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun buttonFunctionality() {
        buttonBack.setOnClickListener {
            Values.currentGradientScreenPos = -1
            Vibration.mediumFeedback(context)
            hideUI()
            Values.currentlySplitScreened = false
            Values.currentActivity = "Browse"
        }
        buttonOptions.setOnClickListener {
            if (!optionsAnimating) {
                Vibration.mediumFeedback(context)
                optionsAnimating = true

                if (optionsExpanded) {
                    UIElements.viewObjectAnimator(detailsHolder, "translationY",
                            0f, 500, 0, DecelerateInterpolator(3f))
                    UIElements.constraintLayoutElevationAnimator(optionsHolder, 0f,
                            500, 0, DecelerateInterpolator(3f))
                    Handler(Looper.getMainLooper()).postDelayed({
                        optionsHolder.visibility = View.INVISIBLE
                        optionsAnimating = false
                        optionsExpanded = false
                    }, 500)
                } else {
                    optionsHolder.elevation = 0f
                    optionsHolder.visibility = View.VISIBLE
                    UIElements.viewObjectAnimator(detailsHolder, "translationY",
                            -(optionsHolder.height + Calculations.convertToDP(context, -66f)), 500, 0, DecelerateInterpolator(3f))
                    UIElements.constraintLayoutElevationAnimator(optionsHolder, 1f,
                            500, 0, DecelerateInterpolator(3f))
                    Handler(Looper.getMainLooper()).postDelayed({
                        optionsAnimating = false
                        optionsExpanded = true
                    }, 500)
                }
            }

            if (optionsExpanded) {
                UIElements.viewObjectAnimator(detailsHolder, "translationY", 0f, 500, 0, DecelerateInterpolator(3f))
                UIElements.constraintLayoutElevationAnimator(optionsHolder, 0f, 500, 0, DecelerateInterpolator())
                Handler(Looper.getMainLooper()).postDelayed({
                    if (detailsHolder.translationY == 0f) {
                        optionsHolder.visibility = View.GONE
                        optionsExpanded = false
                    }
                }, 500)
            } else {
                UIElements.viewObjectAnimator(detailsHolder, "translationY", Calculations.convertToDP(context, -66f), 500, 0, DecelerateInterpolator(3f))
                UIElements.constraintLayoutElevationAnimator(optionsHolder, Calculations.convertToDP(context, 12f), 500, 100, DecelerateInterpolator())
                optionsHolder.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    if (detailsHolder.translationY != 0f) {
                        optionsExpanded = true
                    }
                }, 500)
            }
        }
        buttonSaveGradient.setOnClickListener {
            Vibration.lowFeedback(context)
            //UIElements.saveGradientDialog(context, Values.gradientScreenColours, (activity as MainActivity).window)
            val fm = fragmentManager as FragmentManager
            val saveGradientDialog = SaveGradientDialog.newInstance(Values.gradientScreenColours)
            saveGradientDialog.show(fm, "saveGradientDialog")
            //Save Gradient
        }
        buttonSetWallpaper.setOnClickListener {
            Vibration.lowFeedback(context)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                UIElement.popupDialog(context, "setWallpaper", R.drawable.icon_wallpaper_new, R.string.dual_set_wallpaper,
                        null, R.string.question_set_wallpaper, HashMaps.setWallpaperArrayList(), context.window.decorView, this)
            } else {
                UIElement.popupDialog(context, "setWallpaperOutdated", R.drawable.icon_warning, R.string.dual_outdated_android,
                        null, R.string.question_outdated_android, HashMaps.arrayYesCancel(), context.window.decorView, this)
            }
            //Set Wallpaper
        }
        gradientViewer.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!animatingFullscreen) {
                        animatingFullscreen = true
                        /** Animate UI Elements out **/
                        UIElements.viewObjectAnimator(detailsHolder, "translationY",
                                (90 * resources.displayMetrics.density) + detailsHolder.height,
                                500, 30, DecelerateInterpolator(3f))
                        UIElements.viewObjectAnimator(actionsHolder, "translationY",
                                (74 * resources.displayMetrics.density) + detailsHolder.height,
                                500, 0, DecelerateInterpolator(3f))

                        Handler(Looper.getMainLooper()).postDelayed({
                            optionsHolder.visibility = View.INVISIBLE
                            optionsExpanded = false
                        }, 100)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    showUI()
                    Handler(Looper.getMainLooper()).postDelayed({
                        animatingFullscreen = false
                    }, 500)
                }
            }
            true
        }
    }

    @SuppressLint("InlinedApi")
    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        when (popupName) {
            "setWallpaper" -> {
                val wallpaperManager = WallpaperManager.getInstance(context)
                UIElement.popupDialogHider()
                when (position) {
                    0 -> {
                        try {
                            wallpaperManager.setBitmap(Calculations.createBitmap(UIElement.gradientDrawableNew(context, null, Values.gradientScreenColours, 0f) as Drawable,
                                    Calculations.screenMeasure(context, "width", context.window), Calculations.screenMeasure(context, "height", context.window)),
                                    null, true, WallpaperManager.FLAG_SYSTEM)
                            runNotification(R.drawable.icon_wallpaper_new, R.string.sentence_enjoy_your_wallpaper)
                        } catch (e: Exception) {
                            Log.e("ERR", "pebble.frag_gradient_screen.on_button_click_popup.set_wallpaper: ${e.localizedMessage}")
                        }
                    }
                    1 -> {
                        try {
                            wallpaperManager.setBitmap(Calculations.createBitmap(UIElement.gradientDrawableNew(context, null, Values.gradientScreenColours, 0f) as Drawable,
                                    Calculations.screenMeasure(context, "width", context.window), Calculations.screenMeasure(context, "height", context.window)),
                                    null, true, WallpaperManager.FLAG_LOCK)
                            runNotification(R.drawable.icon_wallpaper_new, R.string.sentence_enjoy_your_wallpaper)
                        } catch (e: Exception) {
                            Log.e("ERR", "pebble.frag_gradient_screen.on_button_click_popup.set_wallpaper: ${e.localizedMessage}")
                        }
                    }
                    2 -> UIElement.popupDialogHider()
                }
            }
            "setWallpaperOutdated" -> {
                val wallpaperManager = WallpaperManager.getInstance(context)
                UIElement.popupDialogHider()
                when (position) {
                    0 -> {
                        try {
                            wallpaperManager.setBitmap(Calculations.createBitmap(UIElement.gradientDrawableNew(context, null, Values.gradientScreenColours, 0f) as Drawable,
                                    Calculations.screenMeasure(context, "width", context.window), Calculations.screenMeasure(context, "height", context.window)))
                            runNotification(R.drawable.icon_wallpaper_new, R.string.sentence_enjoy_your_wallpaper)
                        } catch (e: Exception) {
                            Log.e("ERR", "pebble.frag_gradient_screen.on_button_click_popup.set_wallpaper_outdated: ${e.localizedMessage}")
                        }
                    }
                    1 -> UIElement.popupDialogHider()
                }
            }
        }
    }

    override fun onButtonClick(position: Int, view: View, buttonColour: String) {
        //Toast.makeText(context, buttonColour, Toast.LENGTH_SHORT).show()
        UIElements.colourDialog(context, buttonColour, context.window.decorView)
    }

}