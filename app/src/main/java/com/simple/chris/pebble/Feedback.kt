package com.simple.chris.pebble

import android.app.Dialog
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_feedback.*
import java.util.*

class Feedback : AppCompatActivity() {

    private val timeOutConnection = 15000
    private var emailDialogShown = false

    var feedbackHolderHeight = 0
    var feedbackHolderTranslationAmount = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_feedback)
        Values.currentActivity = "Feedback"

        usersEmailField.setOnFocusChangeListener { _, b ->
            if (b) {
                if (!emailDialogShown) {
                    UIElements.oneButtonDialog(this, R.drawable.icon_email, R.string.dialog_text_eng_email, R.string.dialog_text_eng_email_body, R.string.text_eng_ok, emailDialogListener)
                }
            }
        }

        submitButton.setOnClickListener {
            if (usersNameField.text.toString().trim().replace(" ", "") != "" && usersMessageField.text.toString().trim().replace(" ", "") != "") {
                submitFeedback()
            } else {
                UIElements.oneButtonDialog(this, R.drawable.icon_question, R.string.dialog_text_eng_missing_info, R.string.dialog_text_eng_missing_info_body, R.string.text_eng_ok, missingInfoListener)
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
        submitButton.isEnabled = false
        val feedbackDatabaseURL = "https://script.google.com/macros/s/AKfycbwQliBYeXxeQFNXixg-SpcoyVB_7mbkCGT0d0iLNo6cy6DxstI/exec"

        val stringRequest: StringRequest = object : StringRequest(Method.POST, feedbackDatabaseURL,
                /*
                Do this once Feedback Submitted
                 */
                Response.Listener { showDialogs(R.layout.dialog_feedback_submitted) },

                /*
                Do this if Feedback fails to submit
                 */
                Response.ErrorListener {
                    submitButton.isEnabled = true
                    if (Connection.isNetworkAvailable(this)) {
                        showDialogs(R.layout.dialog_feedback_failed)
                    } else {
                        showDialogs(R.layout.dialog_feedback_connection)
                    }

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
    private fun showDialogs(view: Int) {
        val dialog = Dialog(this, R.style.dialogStyle)
        dialog.setCancelable(false)
        dialog.setContentView(view)

        try {
            val dismissButton: LinearLayout = dialog.findViewById(R.id.dismissPopup)
            dismissButton.setOnClickListener {
                dialog.dismiss()
                Log.e("ERR", "Clicked")
            }
        } catch (e: Exception) {
            Log.e("ERR", "Failed: ${e.localizedMessage}")
        }

        try {
            val dismissButton: Button = dialog.findViewById(R.id.dismissPopup)
            dismissButton.setOnClickListener {
                dialog.dismiss()
                Log.e("ERR", "Clicked button")
                if (dismissButton.text.toString().trim() == "Continue") {
                    onBackPressed()
                }
            }
        } catch (e: Exception) {
            Log.e("ERR", "Failed button: ${e.localizedMessage}")
        }


        val window = dialog.window
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setDimAmount(0.5f)

        dialog.show()
    }

    private val missingInfoListener = View.OnClickListener {
        UIElements.oneButtonHider(this)
    }

    override fun onBackPressed() {
        animateViewsOut()
        Handler().postDelayed({
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 900)
    }
}
