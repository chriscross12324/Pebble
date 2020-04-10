package com.simple.chris.pebble

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.simple.chris.pebble.Calculations.convertToDP
import com.simple.chris.pebble.UIElements.gradientDrawable
import kotlinx.android.synthetic.main.activity_create_gradient_new.*

class CreateGradientNew : AppCompatActivity() {

    private lateinit var firstStepNextButton: ConstraintLayout
    private lateinit var firstStepCancelButton: ConstraintLayout

    private lateinit var sharedElementView: ImageView
    private lateinit var gradientViewer: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_create_gradient_new)
        postponeEnterTransition()
        Values.currentActivity = "CreateGradient"

        //Initiate ConstraintLayouts
        firstStepNextButton = findViewById(R.id.firstStepNextButton)
        firstStepCancelButton = findViewById(R.id.firstStepCancelButton)

        //Initiate ImageViews
        sharedElementView = findViewById(R.id.sharedElementsTransitionView)
        gradientViewer = findViewById(R.id.gradientCreatorGradientViewer)

        preViewPlacements()
        gradientViewer()

        gradientViewer.post {
            preViewPlacements()
            startPostponedEnterTransition()
            stepOneAnimationsIn()
        }

        firstStepCancelButton.setOnClickListener {
            cancelAnimation()
            Handler().postDelayed({
                onBackPressed()
            }, 300)
        }

        startColourPicker.setOnClickListener {
            startActivity(Intent(this, ColourPickerTester::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

    }

    private fun gradientViewer() {
        /*
        Creates gradient for sharedElementsView
         */
        val sharedStartColour = ContextCompat.getColor(this, R.color.pebbleStart)
        val sharedEndColour = ContextCompat.getColor(this, R.color.pebbleEnd)
        gradientDrawable(this, true, sharedElementView, sharedStartColour, sharedEndColour, 20f)

        /*
        Creates actual gradientView gradient
         */
        val startColour = Color.parseColor(Values.createGradientStartColour)
        val endColor = Color.parseColor(Values.createGradientEndColour)
        gradientDrawable(this, true, gradientViewer, startColour, endColor, 0f)

        UIElements.viewObjectAnimator(sharedElementView, "alpha", 0f, 250, 450, LinearInterpolator())
        UIElements.viewVisibility(sharedElementView, View.GONE, 700)
    }

    private fun preViewPlacements() {
        firstStepNextButton.translationY = convertToDP(this, (firstStepNextButton.height + 24).toFloat())
        firstStepCancelButton.translationY = convertToDP(this, (firstStepCancelButton.height + 24).toFloat())
    }

    private fun cancelAnimation() {
        UIElements.viewObjectAnimator(firstStepNextButton, "translationY", convertToDP(this, (firstStepNextButton.height).toFloat()), 700, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(firstStepCancelButton, "translationY", convertToDP(this, (firstStepCancelButton.height).toFloat()), 700, 0, DecelerateInterpolator(3f))

        UIElements.viewObjectAnimator(sharedElementView, "alpha", 1f, 150, 0, LinearInterpolator())
        UIElements.viewVisibility(sharedElementView, View.VISIBLE, 0)
    }

    private fun stepOneAnimationsIn() {
        UIElements.viewObjectAnimator(firstStepNextButton, "translationY", 0f, 700, 500, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(firstStepCancelButton, "translationY", 0f, 700, 500, DecelerateInterpolator(3f))
    }

    /*private fun stepOneAnimationsOut() {

    }

    private fun stepTwoAnimationsIn() {
        UIElements.constraintLayoutObjectAnimator(firstStepNextButton, "translationY", 0f, 700, 500, DecelerateInterpolator(3f))
        UIElements.constraintLayoutObjectAnimator(firstStepCancelButton, "translationY", 0f, 700, 500, DecelerateInterpolator(3f))
    }

    private fun stepTwoAnimationsOut() {

    }

    private fun stepThreeAnimationsIn() {
        UIElements.constraintLayoutObjectAnimator(firstStepNextButton, "translationY", 0f, 700, 500, DecelerateInterpolator(3f))
        UIElements.constraintLayoutObjectAnimator(firstStepCancelButton, "translationY", 0f, 700, 500, DecelerateInterpolator(3f))
    }

    private fun stepThreeAnimationsOut() {

    }*/

}
