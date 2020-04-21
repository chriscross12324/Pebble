package com.simple.chris.pebble

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.simple.chris.pebble.Calculations.convertToDP
import kotlin.math.roundToInt

object UIElements {

    lateinit var dialogOneButton: Dialog

    fun setTheme(context: Context) {
        when (Values.theme) {
            "light" -> context.setTheme(R.style.ThemeLight)
            "dark" -> context.setTheme(R.style.ThemeDark)
            "black" -> context.setTheme(R.style.ThemeBlack)
        }
    }

    @SuppressLint("NewApi")
    fun gradientDrawable(context: Context, setDrawable: Boolean, view: View?, startColour: Int, endColour: Int, cornerRadius: Float): Drawable? {
        val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(startColour, endColour)
        )
        gradientDrawable.cornerRadius = Calculations.convertToDP(context, cornerRadius)

        when (setDrawable) {
            true -> {
                view!!.background = gradientDrawable
                if (Calculations.isAndroidPOrGreater()) {
                    view!!.outlineSpotShadowColor = endColour
                }
            }
            false -> {
                return gradientDrawable
            }
        }
        return null
    }

    fun viewVisibility(view: View, visibility: Int, delay: Long) {
        Handler().postDelayed({
            view.visibility = visibility
        }, delay)
    }

    fun viewObjectAnimator(view: View, propertyName: String, endValue: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
            val objectAnimator = ObjectAnimator.ofFloat(view, propertyName, endValue)
            objectAnimator.duration = duration
            objectAnimator.interpolator = interpolator
            objectAnimator.start()
        }, delay)
    }

    fun constraintLayoutHeightAnimator(layout: ConstraintLayout, startValue: Float, endValue: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
            val valueAnimator = ValueAnimator.ofInt(startValue.roundToInt(), endValue.roundToInt())
            valueAnimator.addUpdateListener {
                val value = it.animatedValue as Int
                val layoutParams = layout.layoutParams
                layoutParams.height = value
                layout.layoutParams = layoutParams
            }
            valueAnimator.interpolator = interpolator
            valueAnimator.duration = duration
            valueAnimator.start()
        }, delay)
    }

    fun constraintLayoutElevationAnimator(layout: ConstraintLayout, startElevation: Float, endElevation: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
            val valueAnimator = ValueAnimator.ofFloat(startElevation, endElevation)
            valueAnimator.addUpdateListener {
                val current = it.animatedValue as Float
                layout.elevation = current
            }
            valueAnimator.interpolator = interpolator
            valueAnimator.duration = duration
            valueAnimator.start()
        }, delay)
    }

    fun linearLayoutHeightAnimator(layout: LinearLayout, startValue: Float, endValue: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
            val valueAnimator = ValueAnimator.ofInt(startValue.roundToInt(), endValue.roundToInt())
            valueAnimator.addUpdateListener {
                val value = it.animatedValue as Int
                val layoutParams = layout.layoutParams
                layoutParams.height = value
                layout.layoutParams = layoutParams
            }
            valueAnimator.interpolator = interpolator
            valueAnimator.duration = duration
            valueAnimator.start()
        }, delay)
    }

    fun linearLayoutElevationAnimator(layout: LinearLayout, startElevation: Float, endElevation: Float, duration: Long, delay: Long, interpolator: TimeInterpolator) {
        Handler().postDelayed({
            val valueAnimator = ValueAnimator.ofFloat(startElevation, endElevation)
            valueAnimator.addUpdateListener {
                val current = it.animatedValue as Float
                layout.elevation = current
            }
            valueAnimator.interpolator = interpolator
            valueAnimator.duration = duration
            valueAnimator.start()
        }, delay)
    }

    fun textViewTextChanger(layout: TextView, text: String, delay: Long) {
        Handler().postDelayed({
            layout.text = text
        }, delay)
    }

    fun constraintElevationColour(layout: ConstraintLayout, colour: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            layout.outlineSpotShadowColor = colour
        }
    }

    fun oneButtonDialog(context: Context, icon: Int, title: Int, body: Int, buttonText: Int, listener: View.OnClickListener) {
        dialogOneButton = Dialog(context, R.style.dialogStyle)
        dialogOneButton.setCancelable(false)
        dialogOneButton.setContentView(R.layout.dialog_one_button)

        val dialogMainHolder = dialogOneButton.findViewById<ConstraintLayout>(R.id.dialogMainHolder)
        val dialogIcon = dialogOneButton.findViewById<ImageView>(R.id.dialogIcon)
        val dialogTitle = dialogOneButton.findViewById<TextView>(R.id.dialogTitle)
        val dialogBody = dialogOneButton.findViewById<TextView>(R.id.dialogBody)
        val dialogButton = dialogOneButton.findViewById<ConstraintLayout>(R.id.dialogButton1)
        val dialogButtonText = dialogOneButton.findViewById<TextView>(R.id.dialogButton1Text)

        dialogIcon.setImageResource(icon)
        dialogTitle.setText(title)
        dialogBody.setText(body)
        dialogButtonText.setText(buttonText)

        dialogButton.setOnClickListener(listener)

        val dialogWindow = dialogOneButton.window
        dialogWindow!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialogWindow.setDimAmount(0.5f)
        dialogWindow.setGravity(Gravity.BOTTOM)
        dialogOneButton.show()

        dialogMainHolder.post {
            dialogButton.translationY = dialogButton.height + convertToDP(context, 24f)
            dialogMainHolder.translationY = dialogMainHolder.height + dialogButton.height + convertToDP(context, 40f)
            dialogMainHolder.visibility = View.VISIBLE

            viewObjectAnimator(dialogMainHolder, "translationY", 0f, 700, 0, DecelerateInterpolator(3f))
            viewObjectAnimator(dialogButton, "translationY", 0f, 700, 200, DecelerateInterpolator(3f))
        }
    }

    fun oneButtonHider(context: Context) {
        val dialogMainHolder = dialogOneButton.findViewById<ConstraintLayout>(R.id.dialogMainHolder)
        val dialogButton = dialogOneButton.findViewById<ConstraintLayout>(R.id.dialogButton1)

        viewObjectAnimator(dialogButton, "translationY", dialogButton.height + convertToDP(context, 24f), 700, 0, DecelerateInterpolator(3f))
        viewObjectAnimator(dialogMainHolder, "translationY", dialogMainHolder.height + dialogButton.height + convertToDP(context, 40f), 700, 100, DecelerateInterpolator(3f))

        Handler().postDelayed({
            dialogOneButton.dismiss()
        }, 800)
    }

}