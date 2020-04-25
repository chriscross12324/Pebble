package com.simple.chris.pebble

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.simple.chris.pebble.Calculations.convertToDP
import com.simple.chris.pebble.UIElements.gradientDrawable
import com.simple.chris.pebble.UIElements.viewObjectAnimator
import com.simple.chris.pebble.UIElements.viewVisibility
import kotlinx.android.synthetic.main.activity_create_gradient_new.*
import org.apache.commons.lang3.RandomStringUtils
import kotlin.collections.HashMap

class CreateGradientNew : AppCompatActivity() {

    var currentStep = 1
    var submittedUID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_create_gradient_new)
        postponeEnterTransition()
        Values.currentActivity = "CreateGradient"

        preViewPlacements()
        gradientViewer()

        gradientCreatorGradientViewer.post {
            preViewPlacements()
            startPostponedEnterTransition()
            stepOneAnimationsIn()
        }

        nextStepButton.setOnClickListener {
            if (currentStep == 2) {
                stepTwoAnimationsOut(true)
            } else {
                stepOneAnimationsOut()
                currentStep = 2
            }
        }

        lastStepButton.setOnClickListener {
            currentStep -= 1
            if (currentStep == 0) {
                leaveFirstStepAnimation(true)
                Handler().postDelayed({
                    onBackPressed()
                }, 300)
            } else {
                stepTwoAnimationsOut(false)
            }

        }

        startColourPicker.setOnClickListener {
            Handler().postDelayed({
                Values.currentColourPOS = "start"
                startActivity(Intent(this, ColourPicker::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }, 400)
            leaveFirstStepAnimation(false)
        }

        endColourPicker.setOnClickListener {
            Handler().postDelayed({
                Values.currentColourPOS = "end"
                startActivity(Intent(this, ColourPicker::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }, 400)
            leaveFirstStepAnimation(false)
        }

        gradientCreatorGradientName.setText(Values.gradientCreatorGradientName)
        gradientCreatorGradientDescription.setText(Values.gradientCreatorDescription)

        if (!Values.hintCreateGradientDismissed) {
            UIElements.oneButtonDialog(this, R.drawable.icon_apps, R.string.dialog_title_eng_gradient_create, R.string.dialog_body_eng_gradient_create, R.string.text_eng_i_understand, dialogCreateGradientListener)
        }
    }

    private fun gradientViewer() {
        /*
        Creates gradient for sharedElementsView
         */
        val sharedStartColour = ContextCompat.getColor(this, R.color.pebbleStart)
        val sharedEndColour = ContextCompat.getColor(this, R.color.pebbleEnd)
        gradientDrawable(this, true, sharedElementsTransitionView, sharedStartColour, sharedEndColour, 20f)

        /*
        Creates actual gradientView gradient
         */
        val startColour = Color.parseColor(Values.gradientCreatorStartColour)
        val endColor = Color.parseColor(Values.gradientCreatorEndColour)
        gradientDrawable(this, true, gradientCreatorGradientViewer, startColour, endColor, 0f)

        viewObjectAnimator(sharedElementsTransitionView, "alpha", 0f, 250, 450, LinearInterpolator())
        viewVisibility(sharedElementsTransitionView, View.GONE, 700)
    }

    private fun preViewPlacements() {
        nextStepButton.translationY = convertToDP(this, 74f)
        lastStepButton.translationY = convertToDP(this, 74f)
        gradientNameHolder.translationY = convertToDP(this, 106f) + gradientNameHolder.height + gradientDescriptionHolder.height
        gradientDescriptionHolder.translationY = convertToDP(this, 90f) + gradientDescriptionHolder.height
    }

    private fun leaveFirstStepAnimation(cancel: Boolean) {
        if (cancel) {
            viewObjectAnimator(sharedElementsTransitionView, "alpha", 1f, 150, 0, LinearInterpolator())
            viewVisibility(sharedElementsTransitionView, View.VISIBLE, 0)
        }
        viewObjectAnimator(nextStepButton, "translationY", convertToDP(this, 74f), 700, 0, DecelerateInterpolator(3f))
        viewObjectAnimator(lastStepButton, "translationY", convertToDP(this, 74f), 700, 100, DecelerateInterpolator(3f))
        viewObjectAnimator(startColourPicker, "alpha", 0f, 250, 0, LinearInterpolator())
        viewObjectAnimator(endColourPicker, "alpha", 0f, 250, 0, LinearInterpolator())
    }

    private fun stepOneAnimationsIn() {
        viewObjectAnimator(nextStepButton, "translationY", 0f, 700, 500, DecelerateInterpolator(3f))
        viewObjectAnimator(lastStepButton, "translationY", 0f, 700, 500, DecelerateInterpolator(3f))
        viewObjectAnimator(startColourPicker, "alpha", 1f, 250, 0, LinearInterpolator())
        viewObjectAnimator(endColourPicker, "alpha", 1f, 250, 0, LinearInterpolator())
        viewVisibility(startColourPicker, View.VISIBLE, 0)
        viewVisibility(endColourPicker, View.VISIBLE, 0)
    }

    private fun stepOneAnimationsOut() {
        viewObjectAnimator(nextStepButton, "translationY", convertToDP(this, 74f),
                700, 100, DecelerateInterpolator(3f))
        viewObjectAnimator(lastStepButton, "translationY", convertToDP(this, 74f),
                700, 0, DecelerateInterpolator(3f))
        viewObjectAnimator(startColourPicker, "alpha", 0f, 250, 0, LinearInterpolator())
        viewObjectAnimator(endColourPicker, "alpha", 0f, 250, 0, LinearInterpolator())
        viewVisibility(startColourPicker, View.GONE, 250)
        viewVisibility(endColourPicker, View.GONE, 250)

        Handler().postDelayed({
            lastStepIcon.setImageResource(R.drawable.icon_back)
            nextStepText.setText(R.string.text_eng_submit)
            stepTwoAnimationsIn()
        }, 800)

    }

    private fun stepTwoAnimationsIn() {
        viewObjectAnimator(gradientNameHolder, "translationY", 0f, 700, 400, DecelerateInterpolator(3f))
        viewObjectAnimator(gradientDescriptionHolder, "translationY", 0f, 700, 500, DecelerateInterpolator(3f))
        viewObjectAnimator(nextStepButton, "translationY", 0f, 700, 600, DecelerateInterpolator(3f))
        viewObjectAnimator(lastStepButton, "translationY", 0f, 700, 650, DecelerateInterpolator(3f))
    }

    private fun stepTwoAnimationsOut(submit: Boolean) {
        if (!submit) {
            viewObjectAnimator(gradientNameHolder, "translationY", convertToDP(this, 106f) + gradientNameHolder.height + gradientDescriptionHolder.height, 700, 250, DecelerateInterpolator(3f))
            viewObjectAnimator(gradientDescriptionHolder, "translationY", convertToDP(this, 90f) + gradientNameHolder.height + gradientDescriptionHolder.height, 700, 200, DecelerateInterpolator(3f))
            viewObjectAnimator(nextStepButton, "translationY", convertToDP(this, 74f),
                    700, 0, DecelerateInterpolator(3f))
            viewObjectAnimator(lastStepButton, "translationY", convertToDP(this, 74f),
                    700, 100, DecelerateInterpolator(3f))

            Handler().postDelayed({
                lastStepIcon.setImageResource(R.drawable.icon_close)
                nextStepText.setText(R.string.text_eng_next)
                stepOneAnimationsIn()
            }, 900)
            Values.gradientCreatorGradientName = gradientCreatorGradientName.text.toString()
            Values.gradientCreatorDescription = gradientCreatorGradientDescription.text.toString()
        } else {
            if (gradientCreatorGradientName.text.toString().trim().replace(" ", "") != "") {
                submitGradient()
            } else {
                UIElements.oneButtonDialog(this, R.drawable.icon_question, R.string.dialog_title_eng_gradient_create_missing, R.string.dialog_body_eng_gradient_create_missing, R.string.text_eng_ok, dialogMissingInfoListener)
            }
        }

    }

    private fun refreshGradient() {
        val startColour = Color.parseColor(Values.gradientCreatorStartColour)
        val endColor = Color.parseColor(Values.gradientCreatorEndColour)
        gradientDrawable(this, true, gradientCreatorGradientViewer, startColour, endColor, 0f)
    }

    private fun submitGradient() {
        submittedUID = RandomStringUtils.randomAlphanumeric(12)
        val gradientDatabaseURL = "https://script.google.com/macros/s/AKfycbwFkoSBTbmeB6l9iIiZWGczp9sDEjqX0jiYeglczbLKFAXsmtB1/exec"

        val stringRequest: StringRequest = object : StringRequest(Method.POST, gradientDatabaseURL,
        Response.Listener { submitSuccess() },
        Response.ErrorListener { Log.e("ERR", "Failed")
        }) {
            override fun getParams(): MutableMap<String, String> {
                val details: MutableMap<String, String> = HashMap()
                details["action"] = "addGradient"
                details["gradientName"] = gradientCreatorGradientName.text.toString()
                details["startColour"] = Values.gradientCreatorStartColour
                details["endColour"] = Values.gradientCreatorEndColour
                details["gradientDescription"] = gradientCreatorGradientDescription.text.toString()
                details["gradientAuthor"] = "chriscross12324"
                details["gradientUID"] = submittedUID
                return details
            }
        }

        val retryPolicy = DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        stringRequest.retryPolicy = retryPolicy

        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }

    @SuppressLint("NewApi")
    private fun submitSuccess() {
        Values.currentActivity = "SubmittedGradient"
        val successDialog = Dialog(this, R.style.dialogStyle)
        successDialog.setCancelable(false)
        successDialog.setContentView(R.layout.dialog_gradient_submitted)

        val leaveButton = successDialog.findViewById<Button>(R.id.leave)
        leaveButton.setOnClickListener {
            successDialog.dismiss()
            stepTwoAnimationsOut(false)
            onBackPressed()
        }
        val uidTextView = successDialog.findViewById<TextView>(R.id.UIDTextView)
        uidTextView.text = submittedUID
        uidTextView.setOnLongClickListener {
            val clipData = ClipData.newPlainText("uniqueID", submittedUID)
            val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(clipData)
            uidTextView.text = "Copied..."

            Handler().postDelayed({
                uidTextView.text = submittedUID
            }, 1500)

            Vibration.mediumFeedback(this)
            false
        }

        if (Calculations.isAndroidPOrGreater()) {
            leaveButton.outlineSpotShadowColor = ContextCompat.getColor(this, R.color.pebbleEnd)
        }

        val window = successDialog.window
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setDimAmount(0.5f)

        successDialog.show()

        Values.gradientCreatorGradientName = ""
        Values.gradientCreatorDescription = ""
        Values.gradientCreatorStartColour = "#acd77b"
        Values.gradientCreatorEndColour = "#74d77b"
    }

    private val dialogCreateGradientListener = View.OnClickListener {
        Values.hintCreateGradientDismissed = true
        UIElements.oneButtonHider(this)
    }

    private val dialogMissingInfoListener = View.OnClickListener {
        UIElements.oneButtonHider(this)
    }

    override fun onResume() {
        super.onResume()
        if (Values.currentActivity == "ColourPicker") {
            Values.currentActivity = "CreateGradient"
            refreshGradient()
            stepOneAnimationsIn()
        }
    }

}
