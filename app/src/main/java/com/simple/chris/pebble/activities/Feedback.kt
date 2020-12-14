package com.simple.chris.pebble.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.simple.chris.pebble.*
import com.simple.chris.pebble.adapters_helpers.PopupDialogButtonRecycler
import com.simple.chris.pebble.functions.*
import kotlinx.android.synthetic.main.activity_feedback.*
import java.util.*

class Feedback : AppCompatActivity(), PopupDialogButtonRecycler.OnButtonListener {

    private val timeOutConnection = 15000
    private var emailDialogShown = false

    var feedbackHolderHeight = 0
    var feedbackHolderTranslationAmount = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElement.setTheme(this)
        setContentView(R.layout.activity_feedback)
        Values.currentActivity = "Feedback"

        wallpaperImageViewer.post {
            UIElements.setWallpaper(this, wallpaperImageViewer, wallpaperImageAlpha, window)
        }

        usersEmailField.setOnFocusChangeListener { _, b ->
            if (b) {
                if (!emailDialogShown) {
                    UIElements.oneButtonDialog(this, R.drawable.icon_email, R.string.word_email, R.string.sentence_email_not_required, R.string.word_ok, emailDialogListener)
                }
            }
        }

        submitButton.setOnClickListener {
            if (usersNameField.text.toString().trim().replace(" ", "") != "" && usersMessageField.text.toString().trim().replace(" ", "") != "") {
                submitFeedback()
            } else {
                UIElements.oneButtonDialog(this, R.drawable.icon_question, R.string.dual_missing_info, R.string.sentence_feedback_missing_info, R.string.word_ok, missingInfoListener)
            }
        }

        feedbackCancelButton.setOnClickListener {
            onBackPressed()
        }

        feedbackFieldsHolder.post {
            feedbackHolderHeight = feedbackFieldsHolder.height
            enteringPlacements()
        }

    }

    private fun enteringPlacements() {
        feedbackHolderTranslationAmount = feedbackHolderHeight + submitButton.height + Calculations.convertToDP(this, 48f)
        feedbackFieldsHolder.translationY = feedbackHolderTranslationAmount
        submitButton.translationY = feedbackHolderTranslationAmount
        feedbackCancelButton.translationY = feedbackHolderTranslationAmount

        animateViewsIn()
    }

    private fun animateViewsIn() {
        UIElements.viewObjectAnimator(feedbackFieldsHolder, "translationY", 0f, 700, 350, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(submitButton, "translationY", 0f, 700, 450, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(feedbackCancelButton, "translationY", 0f, 700, 500, DecelerateInterpolator(3f))
    }

    private fun animateViewsOut() {
        UIElements.viewObjectAnimator(feedbackFieldsHolder, "translationY", feedbackHolderHeight + submitButton.height + Calculations.convertToDP(this, 48f), 700, 200, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(submitButton, "translationY", (Calculations.convertToDP(this, 48f) + submitButton.height), 700, 100, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(feedbackCancelButton, "translationY", (Calculations.convertToDP(this, 48f) + feedbackCancelButton.height), 700, 50, DecelerateInterpolator(3f))
    }

    private fun submitFeedback() {
        UIElement.popupDialog(this, "null", null, R.string.word_submitting, null, R.string.sentence_feedback_submitting,
                null, window.decorView, null)
        submitButton.isEnabled = false
        val feedbackDatabaseURL = "https://script.google.com/macros/s/AKfycbwQliBYeXxeQFNXixg-SpcoyVB_7mbkCGT0d0iLNo6cy6DxstI/exec"

        val stringRequest: StringRequest = object : StringRequest(Method.POST, feedbackDatabaseURL,
                /*
                Do this once Feedback Submitted
                 */
                Response.Listener {
                    //showDialogs(R.layout.dialog_feedback_submitted)
                    UIElement.popupDialog(this, "feedbackSubmitted", R.drawable.icon_love, R.string.dual_feedback_submitted, null, R.string.sentence_feedback_received,
                            HashMaps.BAClose(), window.decorView, this)
                },

                /*
                Do this if Feedback fails to submit
                 */
                Response.ErrorListener {
                    submitButton.isEnabled = true
                    UIElement.popupDialog(this, "feedbackFailed", R.drawable.icon_wifi_empty, R.string.word_failed, null, R.string.sentence_feedback_submit_failed,
                            HashMaps.BABackCancel(), window.decorView, this)
                }) {

            /*
            Gather information to submit
             */
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["action"] = "addFeedback"
                params["feedbackType"] = "Undefined"
                params["usersName"] = usersNameField.text.toString()
                params["usersEmail"] = usersEmailField.text.toString()
                params["usersMessage"] = usersMessageField.text.toString()
                return params
            }
        }

        val retryPolicy = DefaultRetryPolicy(timeOutConnection, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        stringRequest.retryPolicy = retryPolicy

        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }

    private val emailDialogListener = View.OnClickListener {
        emailDialogShown = true
        UIElements.oneButtonHider(this)
    }

    private val missingInfoListener = View.OnClickListener {
        UIElements.oneButtonHider(this)
    }

    override fun onBackPressed() {
        animateViewsOut()
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 550)
    }

    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        when (popupName) {
            "feedbackSubmitted" -> {
                UIElement.popupDialogHider()
                onBackPressed()
            }

            "feedbackFailed" -> {
                when (position) {
                    0 -> {
                        UIElement.popupDialogHider()
                    }

                    1 -> {
                        UIElement.popupDialogHider()
                        onBackPressed()
                    }
                }
            }
        }
    }
}
