package com.simple.chris.pebble.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.TransitionInflater
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.simple.chris.pebble.R
import com.simple.chris.pebble.adapters_helpers.PopupDialogButtonRecycler
import com.simple.chris.pebble.adapters_helpers.SearchColourRecyclerView
import com.simple.chris.pebble.functions.*
import kotlinx.android.synthetic.main.fragment_gradient_screen.*
import kotlinx.android.synthetic.main.fragment_gradient_screen.actionsHolder
import kotlinx.android.synthetic.main.fragment_gradient_screen.detailsHolder
import kotlinx.android.synthetic.main.fragment_search.*
import java.lang.Exception

class FragGradientScreen : Fragment(R.layout.fragment_gradient_screen), PopupDialogButtonRecycler.OnButtonListener, SearchColourRecyclerView.OnButtonListener {
    private lateinit var context: Activity
    private lateinit var gradientName: String
    private lateinit var gradientDescription: String
    private lateinit var gradientColourArray: ArrayList<String>

    private var savedFileName = ""
    private var detailsHolderExpanded = false
    private var detailsHolderHeight = 0
    private var alreadyOpened = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context = (activity as MainActivity)
    }

/*    private fun startLayout() {
        buttonFunctionality()
        colourRecycler()
        gradientViewer.transitionName = gradientName
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        //Toast.makeText(context, "${ViewCompat.getTransitionName(gradientViewer)}", Toast.LENGTH_SHORT).show()
        if (Values.currentActivity == "GradientScreen") {
            //Already Open
            UIElements.viewObjectAnimator(detailsHolder, "translationY",
                    (90 * resources.displayMetrics.density) + detailsHolderHeight, 700,
                    50, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(actionsHolder, "translationY",
                    (74 * resources.displayMetrics.density) + detailsHolderHeight, 700,
                    0, DecelerateInterpolator(3f))
            Handler(Looper.getMainLooper()).postDelayed({
                optionsHolder.visibility = View.INVISIBLE
            }, 100)
            Handler(Looper.getMainLooper()).postDelayed({
                detailsHolder.visibility = View.INVISIBLE
                animateLayout()
            }, 700)
        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                animateLayout()
            }, 700)
        }
        Values.currentActivity = "GradientScreen"
    }

    private fun animateLayout() {
        Handler(Looper.getMainLooper()).postDelayed({
            detailsHolderExpanded = false
            gradientNameText.text = gradientName.replace("\n", " ")
            gradientDescriptionText.text = gradientDescription
            if (gradientDescription == "") {
                gradientDescriptionText.visibility = View.GONE
            } else {
                gradientDescriptionText.visibility = View.VISIBLE
            }
            UIElement.gradientDrawableNew(context, gradientViewer, gradientColourArray, 20f)
            (activity as MainActivity).hideSharedElementHero()
            Handler(Looper.getMainLooper()).postDelayed({
                detailsHolderHeight = detailsHolder.height
                detailsHolder.translationY = (90 * resources.displayMetrics.density) + detailsHolderHeight
                actionsHolder.translationY = (74 * resources.displayMetrics.density) + detailsHolderHeight
                UIElements.viewVisibility(detailsHolder, View.VISIBLE, 5)
                UIElements.viewVisibility(actionsHolder, View.VISIBLE, 0)
                UIElements.viewObjectAnimator(detailsHolder, "translationY", 0f, 700,
                        0, DecelerateInterpolator(3f))
                UIElements.viewObjectAnimator(actionsHolder, "translationY", 0f, 700,
                        50, DecelerateInterpolator(3f))
            }, 10)
        }, 250)
    }

    fun bringUpUI() {
        detailsHolder.visibility = View.VISIBLE
        actionsHolder.visibility = View.VISIBLE

        UIElements.viewObjectAnimator(detailsHolder, "translationY",
                0f, 700,
                50, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(actionsHolder, "translationY",
                0f, 700,
                0, DecelerateInterpolator(3f))
    }

    fun sendDownUI() {
        UIElements.viewObjectAnimator(detailsHolder, "translationY",
                (90 * resources.displayMetrics.density) + detailsHolderHeight, 700,
                50, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(actionsHolder, "translationY",
                (74 * resources.displayMetrics.density) + detailsHolderHeight, 700,
                0, DecelerateInterpolator(3f))
        Handler(Looper.getMainLooper()).postDelayed({
            optionsHolder.visibility = View.INVISIBLE
        }, 100)
        Handler(Looper.getMainLooper()).postDelayed({
            detailsHolder.visibility = View.INVISIBLE
        }, 700)
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

    fun hideAllUIInstant() {
        Handler(Looper.getMainLooper()).postDelayed({
            if (detailsHolder != null && optionsHolder != null) {
                backgroundDimmer.alpha = 0f
                gradientViewer.background = null
                UIElements.viewObjectAnimator(detailsHolder, "translationY",
                        (90 * resources.displayMetrics.density) + detailsHolderHeight, 0,
                        0, LinearInterpolator())
                UIElements.viewObjectAnimator(actionsHolder, "translationY",
                        (74 * resources.displayMetrics.density) + detailsHolderHeight, 0,
                        0, LinearInterpolator())
                optionsHolder.visibility = View.INVISIBLE
                detailsHolder.visibility = View.INVISIBLE
            } else {
                hideAllUIInstant()
            }
        }, 5)
    }*/

    private fun colourRecycler() {
        colourElementsRecycler.setHasFixedSize(true)
        val buttonLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val buttonAdapter = SearchColourRecyclerView(context, HashMaps.colours(), this)
        Log.e("INFO", "${Values.gradientScreenColours} : ${HashMaps.colours()}")

        colourElementsRecycler.layoutManager = buttonLayoutManager
        colourElementsRecycler.adapter = buttonAdapter
    }

    @SuppressLint("ClickableViewAccessibility")
    fun buttonFunctionality() {
        buttonBack.setOnClickListener {
            Vibration.mediumFeedback(context)
            //Close Fragment
            //sendDownUI()
            Handler(Looper.getMainLooper()).postDelayed({
                //(activity as MainActivity).closeSecondary()
            }, 700)
        }
        buttonOptions.setOnClickListener {
            Vibration.mediumFeedback(context)
            if (detailsHolderExpanded) {
                UIElements.viewObjectAnimator(detailsHolder, "translationY", 0f, 500, 0, DecelerateInterpolator(3f))
                UIElements.constraintLayoutElevationAnimator(optionsHolder, 0f, 500, 0, DecelerateInterpolator())
                Handler(Looper.getMainLooper()).postDelayed({
                    if (detailsHolder.translationY == 0f) {
                        optionsHolder.visibility = View.GONE
                        detailsHolderExpanded = false
                    }
                }, 500)
            } else {
                UIElements.viewObjectAnimator(detailsHolder, "translationY", Calculations.convertToDP(context, -66f), 500, 0, DecelerateInterpolator(3f))
                UIElements.constraintLayoutElevationAnimator(optionsHolder, Calculations.convertToDP(context, 12f), 500, 100, DecelerateInterpolator())
                optionsHolder.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    if (detailsHolder.translationY != 0f) {
                        detailsHolderExpanded = true
                    }
                }, 500)
            }
        }
        buttonSaveGradient.setOnClickListener {
            Vibration.lowFeedback(context)
            //Save Gradient
        }
        buttonSetWallpaper.setOnClickListener {
            Vibration.lowFeedback(context)
            //Set Wallpaper
        }
        gradientViewer.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {

                }
                MotionEvent.ACTION_UP -> {

                }
            }
            true
        }
    }



    fun passThroughVariables() {
        try {
            Handler(Looper.getMainLooper()).postDelayed({
                if (gradientViewer != null) {
                    gradientName = Values.gradientScreenName
                    gradientDescription = Values.gradientScreenDesc
                    gradientColourArray = Values.gradientScreenColours
                    //startLayout()
                } else {
                    passThroughVariables()
                }
            }, 10)

        } catch (e: Exception) {
            Log.e("ERR", "pebble.activities.gradient_frag.pass_through_variables: ${e.localizedMessage}")
        }
    }

    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        //TODO("Not yet implemented")
    }

    override fun onButtonClick(position: Int, view: View, buttonColour: String) {
        TODO("Not yet implemented")
    }

}