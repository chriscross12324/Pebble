package com.simple.chris.pebble

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.Shape
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.simple.chris.pebble.UIElements.constraintLayoutObjectAnimator
import com.simple.chris.pebble.UIElements.constraintLayoutValueAnimator
import com.simple.chris.pebble.UIElements.imageViewObjectAnimator
import com.simple.chris.pebble.UIElements.textViewObjectAnimator
import kotlinx.android.synthetic.main.activity_gradient_details.*
import kotlinx.android.synthetic.main.dialog_data.view.*
import kotlin.math.roundToInt

class ActivityGradientDetailsK : AppCompatActivity() {

    private lateinit var cardView: CardView

    private lateinit var detailsHolder: ConstraintLayout
    private lateinit var actionsHolder: ConstraintLayout
    private lateinit var notification: ConstraintLayout

    private lateinit var backButton: LinearLayout
    private lateinit var hideButton: LinearLayout
    private lateinit var startHex: LinearLayout
    private lateinit var endHex: LinearLayout

    private lateinit var gradientViewStatic: ImageView
    private lateinit var gradientViewAnimated: ImageView
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
    private var endColourInt: Int = 0
    private var detailsHolderHeight = 0

    private var detailsHolderExpanded = false
    private var copiedAnimationPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_gradient_details)
        postponeEnterTransition()

        cardView = findViewById(R.id.corners)

        detailsHolder = findViewById(R.id.detailsHolder)
        actionsHolder = findViewById(R.id.actionsHolder)
        notification = findViewById(R.id.copiedNotification)

        backButton = findViewById(R.id.backButton)
        hideButton = findViewById(R.id.hideButton)
        startHex = findViewById(R.id.startHex)
        endHex = findViewById(R.id.endHex)

        gradientViewStatic = findViewById(R.id.gradientViewStatic)
        gradientViewAnimated = findViewById(R.id.gradientViewAnimated)
        startHexCircle = findViewById(R.id.startHexCircle)
        endHexCircle = findViewById(R.id.endHexCircle)
        arrow = findViewById(R.id.arrow)

        gradientName = findViewById(R.id.gradientName)
        gradientDescription = findViewById(R.id.gradientDescription)
        gradientStartHex = findViewById(R.id.gradientStartHex)
        gradientEndHex = findViewById(R.id.gradientEndHex)

        gradientNameString = intent.getStringExtra("gradientName") as String
        gradientDescriptionString = intent.getStringExtra("description") as String
        startColourInt = Color.parseColor(intent.getStringExtra("startColour"))
        endColourInt = Color.parseColor(intent.getStringExtra("endColour"))

        val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(startColourInt, endColourInt)
        )
        gradientViewStatic.background = gradientDrawable

        corners.transitionName = gradientNameString

        if (gradientDescriptionString == "") {
            gradientDescription.visibility = View.GONE
        }

        gradientViewStatic.post {
            startPostponedEnterTransition()
            buttons()
            preViewPlacements()
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

        detailsHolder.setOnClickListener {
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
        }

        backButton.setOnClickListener {
            onBackPressed()
        }

        hideButton.setOnClickListener {
            constraintLayoutValueAnimator(detailsHolder, detailsHolder.measuredHeight.toFloat(),
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
            }, 400)
        }
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
        UIElements.constraintLayoutVisibility(detailsHolder, View.INVISIBLE, 300)
        UIElements.constraintLayoutVisibility(actionsHolder, View.INVISIBLE, 300)
        UIElements.constraintLayoutVisibility(notification, View.INVISIBLE, 300)

        Handler().postDelayed({
            super@ActivityGradientDetailsK.onBackPressed()
        }, 250)
    }
}
