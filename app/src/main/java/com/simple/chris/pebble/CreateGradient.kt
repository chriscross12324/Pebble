package com.simple.chris.pebble

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class CreateGradient : AppCompatActivity() {

    lateinit var gradientViewer: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_create_gradient)
        postponeEnterTransition()

        gradientViewer = findViewById(R.id.gradientViewer)

        val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(Color.parseColor(Values.createGradientStartColour), Color.parseColor(Values.createGradientEndColour))
        )
        gradientDrawable.cornerRadius = Calculations.convertToDP(this, 20f)
        gradientViewer.background = gradientDrawable

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            gradientViewer.outlineSpotShadowColor = Color.parseColor(Values.createGradientEndColour)
        }

        gradientViewer.post {
            startPostponedEnterTransition()
        }
    }
}
