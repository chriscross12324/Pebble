package com.simple.chris.pebble

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
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

    }

    private fun gradientViewer() {
        /*
        Creates gradient for sharedElementsView
         */
        val sharedStartColour = ContextCompat.getColor(this, R.color.pebbleStart)
        val sharedEndColour = ContextCompat.getColor(this, R.color.pebbleEnd)

        val sharedGradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(sharedStartColour, sharedEndColour)
        )
        sharedGradientDrawable.cornerRadius = convertToDP(this, 20f)
        sharedElementView.background = sharedGradientDrawable

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            sharedElementView.outlineSpotShadowColor = sharedEndColour
        }

        /*
        Creates actual gradientView gradient
         */
        val startColour = Color.parseColor(Values.createGradientStartColour)
        val endColor = Color.parseColor(Values.createGradientEndColour)

        val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(startColour, endColor)
        )
        gradientViewer.background = gradientDrawable

        UIElements.imageViewObjectAnimator(sharedElementView, "alpha", 0f, 250, 450, LinearInterpolator())
        UIElements.imageViewVisibility(sharedElementView, View.GONE, 700)
    }

    private fun preViewPlacements() {
        firstStepNextButton.translationY = convertToDP(this, (firstStepNextButton.height + 24).toFloat())
        firstStepCancelButton.translationY = convertToDP(this, (firstStepCancelButton.height + 24).toFloat())
    }

    private fun cancelAnimation() {
        UIElements.constraintLayoutObjectAnimator(firstStepNextButton, "translationY", convertToDP(this, (firstStepNextButton.height).toFloat()), 700, 0, DecelerateInterpolator(3f))
        UIElements.constraintLayoutObjectAnimator(firstStepCancelButton, "translationY", convertToDP(this, (firstStepCancelButton.height).toFloat()), 700, 0, DecelerateInterpolator(3f))

        UIElements.imageViewObjectAnimator(sharedElementView, "alpha", 1f, 150, 0, LinearInterpolator())
        UIElements.imageViewVisibility(sharedElementView, View.VISIBLE, 0)
    }

    private fun stepOneAnimationsIn() {
        UIElements.constraintLayoutObjectAnimator(firstStepNextButton, "translationY", 0f, 700, 500, DecelerateInterpolator(3f))
        UIElements.constraintLayoutObjectAnimator(firstStepCancelButton, "translationY", 0f, 700, 500, DecelerateInterpolator(3f))
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
