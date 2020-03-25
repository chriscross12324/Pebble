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
import android.view.WindowManager
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
import com.simple.chris.pebble.UIElements.imageViewObjectAnimator
import kotlinx.android.synthetic.main.activity_gradient_details.*
import java.util.*
import kotlin.math.roundToInt

class ActivityGradientDetailsK : AppCompatActivity() {

    private lateinit var cardView: CardView
    private lateinit var touchBlocker: View

    private lateinit var detailsHolder: ConstraintLayout
    private lateinit var actionsHolder: ConstraintLayout
    private lateinit var notification: ConstraintLayout

    private lateinit var backButton: LinearLayout
    private lateinit var hideButton: LinearLayout
    private lateinit var startHex: LinearLayout
    private lateinit var endHex: LinearLayout
    private lateinit var hexHolder: LinearLayout
    private lateinit var optionsHolder: LinearLayout

    private lateinit var gradientViewStatic: ImageView
    //private lateinit var gradientViewAnimated: ImageView
    private lateinit var startHexCircle: ImageView
    private lateinit var endHexCircle: ImageView
    private lateinit var arrow: ImageView

    private lateinit var gradientName: TextView
    private lateinit var gradientDescription: TextView
    private lateinit var gradientStartHex: TextView
    private lateinit var gradientEndHex: TextView

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

        //Initiate LinearLayouts
        backButton = findViewById(R.id.backButton)
        hideButton = findViewById(R.id.hideButton)
        startHex = findViewById(R.id.startHex)
        endHex = findViewById(R.id.endHex)
        hexHolder = findViewById(R.id.hexHolder)
        optionsHolder = findViewById(R.id.optionsHolder)

        //Initiate ImageViews
        gradientViewStatic = findViewById(R.id.gradientViewStatic)
        //gradientViewAnimated = findViewById(R.id.gradientViewAnimated)
        startHexCircle = findViewById(R.id.startHexCircle)
        endHexCircle = findViewById(R.id.endHexCircle)
        arrow = findViewById(R.id.arrow)

        //Initiates TextViews
        gradientName = findViewById(R.id.gradientName)
        gradientDescription = findViewById(R.id.gradientDescription)
        gradientStartHex = findViewById(R.id.gradientStartHex)
        gradientEndHex = findViewById(R.id.gradientEndHex)

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
        //gradientViewAnimated.background = gradientDrawable

        corners.transitionName = gradientNameString

        if (gradientDescriptionString == "") {
            gradientDescription.visibility = View.GONE
        }

        gradientViewStatic.post {
            startPostponedEnterTransition()
            Values.currentActivity = "GradientDetails"
            buttons()
            preViewPlacements()
            getCenterPixel()
            animateGradient(startColourInt, middleColourInt, endColourInt)
            pushHoldPopup()
            detailsHolderFixer()
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
        notification.translationY = (-60 * resources.displayMetrics.density)

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
                        Calculations.convertToDP(this, -60f), 500,
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
                        Calculations.convertToDP(this, -60f), 500,
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
            //imageViewObjectAnimator(gradientViewAnimated, "alpha", 0f, 0, 0, LinearInterpolator())
            Handler().postDelayed({
                onBackPressed()
            }, 0)

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
        if (!Values.detailsPushHoldPopupClosed) {
            val pushHoldDialog = Dialog(this, R.style.dialogStyle)
            pushHoldDialog.setCancelable(false)
            pushHoldDialog.setContentView(R.layout.dialog_push_hold)

            val dismissButton: LinearLayout = pushHoldDialog.findViewById(R.id.dismissPopup)
            dismissButton.setOnClickListener {
                Values.detailsPushHoldPopupClosed = true
                pushHoldDialog.dismiss()
                Log.e("INFO", "Clicked")
            }

            val window = pushHoldDialog.window
            window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            window.setDimAmount(0.5f)

            Handler().postDelayed({
                pushHoldDialog.show()
            }, 1000)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun animateGradient(startColour: Int, middleColour: Int, endColour: Int) {

        //imageViewObjectAnimator(gradientViewAnimated, "alpha", 1f, 250, 1000, LinearInterpolator())

        gradientViewStatic.setOnClickListener {
            Log.e("INFO", "Click")
        }

        gradientViewStatic.setOnTouchListener { view, motionEvent ->

            if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                //Hide UI
                constraintLayoutObjectAnimator(detailsHolder, "translationY", 100f, 300, 50, DecelerateInterpolator(3f))
                constraintLayoutObjectAnimator(detailsHolder, "alpha", 0f, 100, 50, LinearInterpolator())
                constraintLayoutObjectAnimator(actionsHolder, "translationY", 100f, 300, 0, DecelerateInterpolator(3f))
                constraintLayoutObjectAnimator(actionsHolder, "alpha", 0f, 100, 0, LinearInterpolator())
            } else if (motionEvent.action == MotionEvent.ACTION_UP) {
                //Show UI
                constraintLayoutObjectAnimator(detailsHolder, "translationY", 0f, 300, 0, DecelerateInterpolator(3f))
                constraintLayoutObjectAnimator(detailsHolder, "alpha", 1f, 100, 0, LinearInterpolator())
                constraintLayoutObjectAnimator(actionsHolder, "translationY", 0f, 300, 50, DecelerateInterpolator(3f))
                constraintLayoutObjectAnimator(actionsHolder, "alpha", 1f, 100, 50, LinearInterpolator())
            }
            true
        }
    }

    private fun detailsHolderFixer() {
        val handler = Handler()
        handler.postDelayed(object:Runnable {
            override fun run() {
                if (actionsHolder.alpha == 1f && detailsHolder.alpha == 0f) {
                    constraintLayoutObjectAnimator(detailsHolder, "translationY", 0f, 300, 0, DecelerateInterpolator(3f))
                    constraintLayoutObjectAnimator(detailsHolder, "alpha", 1f, 100, 0, LinearInterpolator())
                    detailsHolderFixer()
                }

                handler.postDelayed(this, 2000)
            }
        }, 2000)
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
        //imageViewObjectAnimator(gradientViewAnimated, "alpha", 0f, 0, 0, LinearInterpolator())

        Vibration.mediumFeedback(this)

        Handler().postDelayed({
            super@ActivityGradientDetailsK.onBackPressed()
        }, 250)
    }
}
