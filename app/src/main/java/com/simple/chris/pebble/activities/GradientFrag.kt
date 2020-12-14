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
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.simple.chris.pebble.R
import com.simple.chris.pebble.adapters_helpers.PopupDialogButtonRecycler
import com.simple.chris.pebble.functions.*
import kotlinx.android.synthetic.main.activity_gradient_details.*
import kotlinx.android.synthetic.main.fragment_gradient_screen.*
import kotlinx.android.synthetic.main.fragment_gradient_screen.actionsHolder
import kotlinx.android.synthetic.main.fragment_gradient_screen.detailsHolder
import java.lang.Exception

class GradientFrag : Fragment(R.layout.fragment_gradient_screen), PopupDialogButtonRecycler.OnButtonListener {
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

    fun startLayout() {
        buttonFunctionality()
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

    fun animateLayout() {
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

    fun closingAnimation() {
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

    fun setText() {
        gradientNameText.text = gradientName.replace("\n", " ")
        gradientDescriptionText.text = gradientDescription
        if (gradientDescription == "") {
            gradientDescriptionText.visibility = View.GONE
        } else {
            gradientDescriptionText.visibility = View.VISIBLE
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun buttonFunctionality() {
        buttonBack.setOnClickListener {
            Vibration.mediumFeedback(context)
            //Close Fragment
            closingAnimation()
            Handler(Looper.getMainLooper()).postDelayed({
                (activity as MainActivity).closeSecondary()
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



    fun passThroughVariables(gradientArray: ArrayList<HashMap<String, String>>, position: Int) {
        try {
            gradientViewer.post {
                Toast.makeText(context, "Here", Toast.LENGTH_SHORT).show()
            }
            Handler(Looper.getMainLooper()).postDelayed({
                if (gradientViewer != null) {
                    val gradientInfo = gradientArray[position]

                    gradientName = gradientInfo["gradientName"] as String
                    gradientDescription = gradientInfo["gradientDescription"] as String
                    val gradientColours = gradientInfo["gradientColours"]!!.replace("[", "").replace("]", "")
                            .split(",").map { it.trim() }
                    gradientColourArray = ArrayList(gradientColours)
                    startLayout()
                    gradientViewer.background = resources.getDrawable(R.drawable.black_drawable)
                } else {
                    passThroughVariables(gradientArray, position)
                }
            }, 10)

        } catch (e: Exception) {
            Log.e("ERR", "pebble.activities.gradient_frag.pass_through_variables: ${e.localizedMessage}")
        }
    }

    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        //TODO("Not yet implemented")
    }

}