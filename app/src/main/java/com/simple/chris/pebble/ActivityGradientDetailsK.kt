package com.simple.chris.pebble

import android.animation.ArgbEvaluator
import android.animation.TimeAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.renderscript.Sampler
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.simple.chris.pebble.UIElements.constraintLayoutObjectAnimator
import kotlinx.android.synthetic.main.activity_gradient_details.*
import java.util.*
import kotlin.math.roundToInt

class ActivityGradientDetailsK : AppCompatActivity() {

    private lateinit var cardView: CardView

    private lateinit var detailsHolder: ConstraintLayout
    private lateinit var actionsHolder: ConstraintLayout
    private lateinit var notification: ConstraintLayout
    private lateinit var pushHoldPopup: ConstraintLayout

    private lateinit var backButton: LinearLayout
    private lateinit var hideButton: LinearLayout
    private lateinit var startHex: LinearLayout
    private lateinit var endHex: LinearLayout
    private lateinit var hexHolder: LinearLayout
    private lateinit var optionsHolder: LinearLayout

    private lateinit var gradientViewStatic: ImageView
    private lateinit var gradientViewAnimated: ImageView
    private lateinit var startHexCircle: ImageView
    private lateinit var endHexCircle: ImageView
    private lateinit var arrow: ImageView

    private lateinit var gradientName: TextView
    private lateinit var gradientDescription: TextView
    private lateinit var gradientStartHex: TextView
    private lateinit var gradientEndHex: TextView
    private lateinit var dismissPopup: TextView

    private lateinit var gradientNameString: String
    private lateinit var gradientDescriptionString: String

    private var startColourInt: Int = 0
    private var middleColourInt: Int = 0
    private var endColourInt: Int = 0
    private var detailsHolderHeight = 0

    private var detailsHolderExpanded = false
    private var copiedAnimationPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_gradient_details)
        postponeEnterTransition()

        //Initiate CardViews
        cardView = findViewById(R.id.corners)

        //Initiate ConstraintLayouts
        detailsHolder = findViewById(R.id.detailsHolder)
        actionsHolder = findViewById(R.id.actionsHolder)
        notification = findViewById(R.id.copiedNotification)
        pushHoldPopup = findViewById(R.id.pushHoldPopup)

        //Initiate LinearLayouts
        backButton = findViewById(R.id.backButton)
        hideButton = findViewById(R.id.hideButton)
        startHex = findViewById(R.id.startHex)
        endHex = findViewById(R.id.endHex)
        hexHolder = findViewById(R.id.hexHolder)
        optionsHolder = findViewById(R.id.optionsHolder)

        //Initiate ImageViews
        gradientViewStatic = findViewById(R.id.gradientViewStatic)
        gradientViewAnimated = findViewById(R.id.gradientViewAnimated)
        startHexCircle = findViewById(R.id.startHexCircle)
        endHexCircle = findViewById(R.id.endHexCircle)
        arrow = findViewById(R.id.arrow)

        //Initiates TextViews
        gradientName = findViewById(R.id.gradientName)
        gradientDescription = findViewById(R.id.gradientDescription)
        gradientStartHex = findViewById(R.id.gradientStartHex)
        gradientEndHex = findViewById(R.id.gradientEndHex)
        dismissPopup = findViewById(R.id.dismissPopup)

        //Sets
        gradientNameString = intent.getStringExtra("gradientName") as String
        gradientDescriptionString = intent.getStringExtra("description") as String
        startColourInt = Color.parseColor(intent.getStringExtra("startColour"))
        endColourInt = Color.parseColor(intent.getStringExtra("endColour"))

        val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(startColourInt, endColourInt)
        )
        gradientViewStatic.background = gradientDrawable
        gradientViewAnimated.background = gradientDrawable

        corners.transitionName = gradientNameString

        if (gradientDescriptionString == "") {
            gradientDescription.visibility = View.GONE
        }

        gradientViewStatic.post {
            startPostponedEnterTransition()
            buttons()
            preViewPlacements()
            getCenterPixel()
            pushHoldPopup()
            animateGradient(startColourInt, middleColourInt, endColourInt)
            if (!Values.detailsPushHoldPopupClosed) {
                pushHoldPopup()
            }
        }

        gradientName.text = gradientNameString.replace("\n", " ")
        gradientDescription.text = gradientDescriptionString
        gradientStartHex.text = intent.getStringExtra("startColour")
        gradientEndHex.text = intent.getStringExtra("endColour")

        val gradientDrawableStartCircle = GradientDrawable()
        gradientDrawableStartCircle.shape = GradientDrawable.OVAL
        gradientDrawableStartCircle.setStroke(8, startColourInt)
        val gradientDrawableEndCircle = GradientDrawable()
        gradientDrawableEndCircle.shape = GradientDrawable.OVAL
        gradientDrawableEndCircle.setStroke(8, endColourInt)
        startHexCircle.background = gradientDrawableStartCircle
        endHexCircle.background = gradientDrawableEndCircle
    }

    private fun preViewPlacements() {
        detailsHolder.translationY = (90 * resources.displayMetrics.density) + detailsHolder.height
        actionsHolder.translationY = (74 * resources.displayMetrics.density) + detailsHolder.height
        notification.translationY = (-45 * resources.displayMetrics.density)

        UIElements.constraintLayoutVisibility(detailsHolder, View.VISIBLE, 500)
        UIElements.constraintLayoutVisibility(actionsHolder, View.VISIBLE, 500)
        constraintLayoutObjectAnimator(actionsHolder, "translationY",
                0f, 700,
                550, DecelerateInterpolator(3f))
        constraintLayoutObjectAnimator(detailsHolder, "translationY",
                0f, 700,
                500, DecelerateInterpolator(3f))
        detailsHolderHeight = detailsHolder.height
    }

    private fun getCenterPixel() {
        val halfWidth = (resources.displayMetrics.widthPixels) / 2
        val halfHeight = (resources.displayMetrics.heightPixels) / 2

        val mutableBitmap = Bitmap.createBitmap(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mutableBitmap)
        val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(startColourInt, endColourInt)
        )
        gradientDrawable.setBounds(0, 0, resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
        gradientDrawable.draw(canvas)
        val pixel = mutableBitmap.getPixel(halfWidth, halfHeight)

        val redValue = Color.red(pixel)
        val greenValue = Color.green(pixel)
        val blueValue = Color.blue(pixel)

        middleColourInt = Color.parseColor(String.format("#%02x%02x%02x", redValue, greenValue, blueValue))
    }

    private fun buttons() {
        val clipboardManager: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        startHex.setOnLongClickListener {
            val clipData = ClipData.newPlainText("startHex", intent.getStringExtra("startColour"))
            clipboardManager.setPrimaryClip(clipData)
            notification.visibility = View.VISIBLE

            if (!copiedAnimationPlaying) {
                copiedAnimationPlaying = true
                Vibration.notification(this)

                constraintLayoutObjectAnimator(notification, "translationY", 0f, 500,
                        0, DecelerateInterpolator(3f))
                constraintLayoutObjectAnimator(notification, "translationY",
                        (-45 * resources.displayMetrics.density).roundToInt().toFloat(), 500,
                        2000, DecelerateInterpolator(3f))
                UIElements.constraintLayoutVisibility(notification, View.INVISIBLE, 2500)

                Handler().postDelayed({
                    copiedAnimationPlaying = false
                }, 2500)
            }
            false
        }

        endHex.setOnLongClickListener {
            val clipData = ClipData.newPlainText("endHex", intent.getStringExtra("endColour"))
            clipboardManager.setPrimaryClip(clipData)
            notification.visibility = View.VISIBLE

            if (!copiedAnimationPlaying) {
                copiedAnimationPlaying = true
                Vibration.notification(this)

                constraintLayoutObjectAnimator(notification, "translationY", 0f, 500,
                        0, DecelerateInterpolator(3f))
                constraintLayoutObjectAnimator(notification, "translationY",
                        (-45 * resources.displayMetrics.density).roundToInt().toFloat(), 500,
                        2000, DecelerateInterpolator(3f))
                UIElements.constraintLayoutVisibility(notification, View.INVISIBLE, 2500)

                Handler().postDelayed({
                    copiedAnimationPlaying = false
                }, 2500)
            }
            false
        }

        /*detailsHolder.setOnClickListener {
            if (detailsHolderExpanded) {
                detailsHolderExpanded = false

                constraintLayoutValueAnimator(detailsHolder, 50 * resources.displayMetrics.density,
                        detailsHolderHeight.toFloat(), 700,
                        0, DecelerateInterpolator(3f))
                constraintLayoutObjectAnimator(actionsHolder, "translationY", 0f, 400,
                        50, DecelerateInterpolator())
                constraintLayoutObjectAnimator(detailsHolder, "translationY", 0f, 400,
                        0, DecelerateInterpolator())
                imageViewObjectAnimator(arrow, "alpha", 0f, 200,
                        0, DecelerateInterpolator())
                textViewObjectAnimator(gradientName, "alpha", 1f, 200,
                        100, DecelerateInterpolator())
            }
        }*/

        backButton.setOnClickListener {
            onBackPressed()
        }

        hideButton.setOnClickListener {
            /*constraintLayoutValueAnimator(detailsHolder, detailsHolder.measuredHeight.toFloat(),
                    50 * resources.displayMetrics.density, 700,
                    0, DecelerateInterpolator(3f))
            constraintLayoutObjectAnimator(detailsHolder, "translationY",
                    74 * resources.displayMetrics.density, 400,
                    0, DecelerateInterpolator())
            constraintLayoutObjectAnimator(actionsHolder, "translationY",
                    74 * resources.displayMetrics.density, 400,
                    25, DecelerateInterpolator())
            textViewObjectAnimator(gradientName, "alpha", 0f, 200,
                    0, DecelerateInterpolator())
            imageViewObjectAnimator(arrow, "alpha", 1f, 200,
                    200, DecelerateInterpolator())

            Handler().postDelayed({
                detailsHolderExpanded = true
            }, 400)*/
            if (!detailsHolderExpanded) {
                hexHolder.visibility = View.GONE
                optionsHolder.visibility = View.VISIBLE
                detailsHolderExpanded = true
            } else {
                hexHolder.visibility = View.VISIBLE
                optionsHolder.visibility = View.GONE
                detailsHolderExpanded = false
            }

        }
    }

    private fun pushHoldPopup() {
        Handler().postDelayed({
            if (!Values.detailsPushHoldPopupClosed) {
                pushHoldPopup.visibility = View.VISIBLE
            }
        }, 500)

        dismissPopup.setOnClickListener {
            UIElements.constraintLayoutVisibility(pushHoldPopup, View.GONE, 250)
            Values.detailsPushHoldPopupClosed = true
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun animateGradient(startColour: Int, middleColour: Int, endColour: Int) {

        Handler().postDelayed({
            gradientViewAnimated.visibility = View.VISIBLE
        }, 1000)

        gradientViewStatic.setOnClickListener {
            Log.e("INFO", "Click")
        }

        gradientViewAnimated.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                //Hide UI
                view.visibility = View.INVISIBLE
                detailsHolder.visibility = View.INVISIBLE
                actionsHolder.visibility = View.INVISIBLE
                if (pushHoldPopup.visibility != View.GONE) {
                    pushHoldPopup.visibility = View.GONE
                }
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                //Show UI
                view.visibility = View.VISIBLE
                detailsHolder.visibility = View.VISIBLE
                actionsHolder.visibility = View.VISIBLE
            }
            true
        }

        gradientViewAnimated.setOnClickListener {
            Log.e("ERR", "Clicked")
        }

        val evaluator = ArgbEvaluator()
        val gradientDrawable = gradientViewAnimated.background as GradientDrawable
        val valueAnimator = TimeAnimator.ofFloat(0.0f, 1.0f)
        valueAnimator.duration = 5000
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.repeatMode = ValueAnimator.REVERSE
        valueAnimator.addUpdateListener {
            val fraction = valueAnimator.animatedFraction
            val newStart = evaluator.evaluate(fraction, startColour, endColour) as Int
            val newEnd = evaluator.evaluate(fraction, endColour, middleColour) as Int
            val array = intArrayOf(newStart, newEnd)
            gradientDrawable.colors = array
        }
        valueAnimator.start()


    }

    override fun onBackPressed() {
        constraintLayoutObjectAnimator(detailsHolder, "translationY",
                (90 * resources.displayMetrics.density + detailsHolder.height).roundToInt().toFloat(), 250,
                50, DecelerateInterpolator()
        )
        constraintLayoutObjectAnimator(actionsHolder, "translationY",
                (74 * resources.displayMetrics.density + detailsHolder.height).roundToInt().toFloat(), 250,
                0, DecelerateInterpolator()
        )
        UIElements.constraintLayoutVisibility(detailsHolder, View.INVISIBLE, 250)
        UIElements.constraintLayoutVisibility(actionsHolder, View.INVISIBLE, 250)
        UIElements.constraintLayoutVisibility(notification, View.INVISIBLE, 250)
        gradientViewAnimated.visibility = View.INVISIBLE

        if (pushHoldPopup.visibility != View.GONE) {
            UIElements.constraintLayoutVisibility(pushHoldPopup, View.GONE, 0)
        }

        Handler().postDelayed({
            super@ActivityGradientDetailsK.onBackPressed()
        }, 250)
    }
}
