package com.simple.chris.pebble

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.roundToInt

class CreateGradient : AppCompatActivity() {

    private lateinit var actionsHolder: ConstraintLayout
    private lateinit var gradientViewerHolder: ConstraintLayout

    private lateinit var gradientViewer: ImageView

    var screenHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_create_gradient)
        postponeEnterTransition()
        Values.currentActivity = "CreateGradient"

        actionsHolder = findViewById(R.id.actionsHolder)
        gradientViewerHolder = findViewById(R.id.gradientViewerHolder)

        gradientViewer = findViewById(R.id.gradientViewer)

        prePlacement()
        gradientViewerHolder.post {
            gradientViewer()
        }
    }

    private fun prePlacement() {
        actionsHolder.translationY = Calculations.convertToDP(this, 74f)
    }

    private fun gradientViewer() {

        val startColour = Color.parseColor(Values.createGradientStartColour)
        val endColor = Color.parseColor(Values.createGradientEndColour)

        val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(startColour, endColor)
        )
        gradientDrawable.cornerRadius = Calculations.convertToDP(this, 20f)

        gradientViewer.background = gradientDrawable

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            gradientViewer.outlineSpotShadowColor = endColor
        }

        gradientViewer.post {
            startPostponedEnterTransition()
            actionsHolder()
            getMeasurements()
        }
    }

    private fun actionsHolder() {
        Handler().postDelayed({
            UIElements.constraintLayoutObjectAnimator(actionsHolder, "alpha", 1f, 0, 0, LinearInterpolator())
            UIElements.constraintLayoutObjectAnimator(actionsHolder, "translationY",
                    0f, 700,
                    550, DecelerateInterpolator(3f))
        }, 250)

    }

    private fun getMeasurements() {
        screenHeight = Calculations.screenMeasure(this, "height")

        val params = gradientViewerHolder.layoutParams
        params.height = (screenHeight/2.2).roundToInt()
        gradientViewerHolder.layoutParams = params
    }

}
