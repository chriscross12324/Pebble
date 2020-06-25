package com.simple.chris.pebble

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.simple.chris.pebble.Calculations.convertToDP
import com.simple.chris.pebble.UIElements.viewObjectAnimator
import io.alterac.blurkit.BlurLayout
import kotlinx.android.synthetic.main.activity_create_gradient_new.*
import kotlinx.android.synthetic.main.activity_create_gradient_new.blurLayout
import kotlinx.android.synthetic.main.activity_gradient_details.*
import org.apache.commons.lang3.RandomStringUtils

class GradientCreator : AppCompatActivity(), PopupDialogButtonRecyclerAdapter.OnButtonListener {

    lateinit var dialog: Dialog

    private var submitStep = false
    private var gradientExists = false
    var gradientUID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_create_gradient_new)
        postponeEnterTransition()
        Values.currentActivity = "CreateGradient"

        gradientCreatorGradientViewer.post {
            setViewPlacements()
            sharedElementGradientTransition()
            startPostponedEnterTransition()
            firstStepEnterAnim()
        }

        /**
         * Performs tasks when nextStepButton is pressed
         */
        nextStepButton.setOnClickListener {
            if (!submitStep) {
                firstStepExitAnim(false)
                submitStep = true
                Handler().postDelayed({
                    lastStepEnterAnim()
                }, 800)
                Toast.makeText(this, "False", Toast.LENGTH_SHORT).show()
            } else {
                gradientExists = false
                submitLogic()
                Toast.makeText(this, "True", Toast.LENGTH_SHORT).show()
            }
        }

        /**
         * Performs tasks when lastStepButton is pressed
         */
        lastStepButton.setOnClickListener {
            if (!submitStep) {
                firstStepExitAnim(true)
                Handler().postDelayed({
                    onBackPressed()
                }, 450)
            } else {
                lastStepExitAnim(false)
                submitStep = false
                Handler().postDelayed({
                    firstStepEnterAnim()
                }, 950)
            }
        }

        /**
         * Opens colourPicker for startColour
         */
        startColourPicker.setOnClickListener {
            firstStepExitAnim(false)
            Handler().postDelayed({
                Values.currentColourPOS = "startColour"
                startActivity(Intent(this, ColourPicker::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }, 500)
        }

        /**
         * Opens colourPicker for endColour
         */
        endColourPicker.setOnClickListener {
            firstStepExitAnim(false)
            Handler().postDelayed({
                Values.currentColourPOS = "endColour"
                startActivity(Intent(this, ColourPicker::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }, 500)
        }
    }

    private fun refreshGradientDrawable() {
        /**
         * Re-draws gradient after start/end colour change
         */
        val startColour = Color.parseColor(Values.gradientCreatorStartColour)
        val endColour = Color.parseColor(Values.gradientCreatorEndColour)
        UIElements.gradientDrawable(this, true, gradientCreatorGradientViewer, startColour, endColour, 0f)
    }

    private fun sharedElementGradientTransition() {
        /**
         * Creates Sunshine gradient for sharedElementTransition
         */
        val sharedStartColour = ContextCompat.getColor(this, R.color.pebbleStart)
        val sharedEndColour = ContextCompat.getColor(this, R.color.pebbleEnd)
        UIElements.gradientDrawable(this, true, sharedElementsTransitionView, sharedStartColour, sharedEndColour, 20f)

        /**
         * Creates gradient based on user choice
         */
        refreshGradientDrawable()

        /**
         * Animates gradient from sharedElement to user choice
         */
        viewObjectAnimator(sharedElementsTransitionView, "alpha", 0f, 250, 450, LinearInterpolator())
        UIElements.viewVisibility(sharedElementsTransitionView, View.GONE, 700)
    }

    private fun setViewPlacements() {
        /**
         * Sets the initial position for all views
         */
        nextStepButton.translationY = convertToDP(this, 74f)
        lastStepButton.translationY = convertToDP(this, 74f)
        gradientDescriptionHolder.translationY = convertToDP(this, 90f) + gradientDescriptionHolder.height
        gradientNameHolder.translationY = convertToDP(this, 106f) + gradientDescriptionHolder.height + gradientNameHolder.height

        /**
         * Sets prerequisites for textViews
         */
        gradientCreatorGradientName.setText(Values.gradientCreatorGradientName)
        gradientCreatorGradientDescription.setText(Values.gradientCreatorDescription)

        /**
         * Checks if Gradient Creator has been opened before
         */
        if (!Values.hintCreateGradientDismissed) {
            UIElements.oneButtonDialog(this, R.drawable.icon_apps, R.string.dialog_title_eng_gradient_create, R.string.dialog_body_eng_gradient_create, R.string.text_eng_take_tour, dialogFirstOpenListener)
        }
    }

    private fun firstStepEnterAnim() {
        /**
         * Animates all views in for firstStep
         */
        viewObjectAnimator(nextStepButton, "translationY", 0f, 700, 500, DecelerateInterpolator(3f))
        viewObjectAnimator(lastStepButton, "translationY", 0f, 700, 550, DecelerateInterpolator(3f))
        viewObjectAnimator(startColourPicker, "alpha", 1f, 700, 250, LinearInterpolator())
        viewObjectAnimator(endColourPicker, "alpha", 1f, 700, 250, LinearInterpolator())
        UIElements.viewVisibility(startColourPicker, View.VISIBLE, 0)
        UIElements.viewVisibility(endColourPicker, View.VISIBLE, 0)

        /**
         * Sets icon/text for firstStep
         */
        lastStepIcon.setImageResource(R.drawable.icon_close)
        nextStepText.setText(R.string.text_eng_next)
    }

    private fun firstStepExitAnim(mainMenu: Boolean) {
        /**
         * Animates all views out for firstStep
         */
        viewObjectAnimator(nextStepButton, "translationY", convertToDP(this, 74f), 700, 100, DecelerateInterpolator(3f))
        viewObjectAnimator(lastStepButton, "translationY", convertToDP(this, 74f), 700, 0, DecelerateInterpolator(3f))
        viewObjectAnimator(startColourPicker, "alpha", 0f, 250, 0, LinearInterpolator())
        viewObjectAnimator(endColourPicker, "alpha", 0f, 250, 0, LinearInterpolator())

        if (mainMenu) {
            viewObjectAnimator(sharedElementsTransitionView, "alpha", 1f, 150, 0, LinearInterpolator())
            UIElements.viewVisibility(sharedElementsTransitionView, View.VISIBLE, 0)
        }
    }

    private fun lastStepEnterAnim() {
        /**
         * Animates all views in for lastStep
         */
        viewObjectAnimator(nextStepButton, "translationY", 0f, 700, 600, DecelerateInterpolator(3f))
        viewObjectAnimator(lastStepButton, "translationY", 0f, 700, 650, DecelerateInterpolator(3f))
        viewObjectAnimator(gradientDescriptionHolder, "translationY", 0f, 700, 500, DecelerateInterpolator(3f))
        viewObjectAnimator(gradientNameHolder, "translationY", 0f, 700, 400, DecelerateInterpolator(3f))
        UIElements.viewVisibility(startColourPicker, View.GONE, 0)
        UIElements.viewVisibility(endColourPicker, View.GONE, 0)

        /**
         * Sets icon/text for lastStep
         */
        lastStepIcon.setImageResource(R.drawable.icon_back)
        nextStepText.setText(R.string.text_eng_submit)
    }

    private fun lastStepExitAnim(mainMenu: Boolean) {
        /**
         * Animates all views out for lastStep
         */
        viewObjectAnimator(nextStepButton, "translationY", convertToDP(this, 74f), 700, 100, DecelerateInterpolator(3f))
        viewObjectAnimator(lastStepButton, "translationY", convertToDP(this, 74f), 700, 0, DecelerateInterpolator(3f))
        viewObjectAnimator(gradientDescriptionHolder, "translationY", convertToDP(this, 90f) + gradientDescriptionHolder.height, 700, 200, DecelerateInterpolator(3f))
        viewObjectAnimator(gradientNameHolder, "translationY", convertToDP(this, 106f) + gradientDescriptionHolder.height + gradientNameHolder.height, 700, 250, DecelerateInterpolator(3f))

        if (mainMenu) {
            viewObjectAnimator(sharedElementsTransitionView, "alpha", 1f, 150, 0, LinearInterpolator())
            UIElements.viewVisibility(sharedElementsTransitionView, View.VISIBLE, 0)
        }
    }

    private fun submitLogic() {
        /**
         * Checks if gradient has a name
         */
        if (gradientCreatorGradientName.text.toString().trim().replace(" ", "") != "") {
            /**
             * Checks if gradient already exists by colour
             */
            try {
                for (count in 0 until Values.gradientList.size) {
                    /** Checks startColour */
                    if (Values.gradientList[count]["startColour"].equals(Values.gradientCreatorStartColour)) {
                        /** Checks endColour */
                        if (Values.gradientList[count]["endColour"].equals(Values.gradientCreatorEndColour)) {
                            //Gradient already exists
                            //popupDialog(R.drawable.icon_attention, "Gradient Exists", R.string.dialog_body_eng_exists, R.string.text_eng_ok, dialogExistsListener)
                            UIElement.popupDialog(this, "gradientExists", R.drawable.icon_attention, R.string.dialog_title_eng_gradient_exists, null, R.string.dialog_body_eng_gradient_exists, AppHashMaps.gradientExistsArrayList(), window.decorView, this)
                            gradientExists = true
                            break
                        }
                    }

                    /** Submits gradient if it doesn't already exist */
                    if (count + 1 == Values.gradientList.size && !gradientExists) {
                        //progressPopupDialog(R.string.dialog_title_eng_submitting, R.string.dialog_body_eng_uploading, null, null)
                        UIElement.popupDialog(this, "submittingGradient", null, R.string.dialog_title_eng_submitting, null, R.string.dialog_body_eng_uploading, null, window.decorView, null)
                        gradientPush()
                    }
                }
            } catch (e: Exception) {
                Log.e("ERR", "pebble.simple.gradient_creator.submit_logic: ${e.localizedMessage}")
            }
        } else {
            //popupDialog(R.drawable.icon_question, "Missing Info", R.string.dialog_body_eng_gradient_create_missing, R.string.text_eng_ok, dialogMissingListener)
            UIElement.popupDialog(this, "missingInfo", R.drawable.icon_question, R.string.dialog_title_eng_gradient_create_missing, null, R.string.dialog_body_eng_gradient_create_missing, AppHashMaps.missingInfoArrayList(), window.decorView, this)
        }
    }

    private fun gradientPush() {
        gradientUID = RandomStringUtils.randomAlphabetic(5)
        val gradientDatabaseURL = "https://script.google.com/macros/s/AKfycbwFkoSBTbmeB6l9iIiZWGczp9sDEjqX0jiYeglczbLKFAXsmtB1/exec"

        val stringRequest: StringRequest = object : StringRequest(Method.POST, gradientDatabaseURL,
                Response.Listener { gradientPushComplete() },
                Response.ErrorListener {
                    Log.e("ERR", "Failed")
                }) {
            override fun getParams(): MutableMap<String, String> {
                val details: MutableMap<String, String> = HashMap()
                details["action"] = "addGradientV2"
                details["gradientName"] = gradientCreatorGradientName.text.toString()
                details["startColour"] = Values.gradientCreatorStartColour
                details["endColour"] = Values.gradientCreatorEndColour
                details["gradientDescription"] = gradientCreatorGradientDescription.text.toString()
                details["gradientUID"] = gradientUID
                return details
            }
        }

        val retryPolicy = DefaultRetryPolicy(10000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        stringRequest.retryPolicy = retryPolicy

        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }

    private fun gradientPushComplete() {
        Values.justSubmitted = true
        Values.gradientCreatorGradientName = ""
        Values.gradientCreatorDescription = ""
        Values.saveValues(this)

        //popupDialog(R.drawable.icon_check, gradientUID, R.string.dialog_body_eng_submitted, R.string.text_eng_exit, dialogCompleteListener)
        UIElement.popupDialog(this, "gradientSubmitted", R.drawable.icon_check, null, gradientUID ,R.string.dialog_body_eng_submitted, AppHashMaps.gradientSubmittedArrayList(), window.decorView, this)
    }

    override fun onResume() {
        super.onResume()

        /**
         * Checks if resuming from colourPicker
         */
        if (Values.currentActivity == "ColourPicker") {
            Values.currentActivity = "GradientCreator"
            refreshGradientDrawable()
            firstStepEnterAnim()
        }

        /**
         * Checks if app settings unloaded during pause
         */
        if (!Values.valuesLoaded) {
            startActivity(Intent(this, SplashScreen::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    /*fun progressPopupDialog(title: Int, description: Int, buttonText: Int?, listener: View.OnClickListener?) {

        dialog = Dialog(this, R.style.dialogStyle)

        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_popup)

        val holder = dialog.findViewById<ConstraintLayout>(R.id.holder)
        val progressCircle = dialog.findViewById<ProgressBar>(R.id.progressCircle)
        val permissionIcon = dialog.findViewById<ImageView>(R.id.permissionIcon)
        val permissionTitle = dialog.findViewById<TextView>(R.id.permissionTitle)
        val permissionDescription = dialog.findViewById<TextView>(R.id.permissionDescription)
        val button = dialog.findViewById<ConstraintLayout>(R.id.button)
        val buttonTextView = dialog.findViewById<TextView>(R.id.text)

        progressCircle.visibility = View.VISIBLE
        permissionIcon.visibility = View.INVISIBLE
        permissionTitle.setText(title)
        permissionDescription.setText(description)

        val dialogWindow = dialog.window
        dialogWindow!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialogWindow.setDimAmount(0f)
        dialogWindow.setGravity(Gravity.CENTER)
        dialog.show()

        holder.post {
            viewObjectAnimator(holder, "scaleX", 1f, 350, 100, OvershootInterpolator())
            viewObjectAnimator(holder, "scaleY", 1f, 350, 100, OvershootInterpolator())
            viewObjectAnimator(holder, "alpha", 1f, 100, 100, LinearInterpolator())

            if (buttonText != null) {
                viewObjectAnimator(button, "scaleX", 1f, 350, 100, OvershootInterpolator())
                viewObjectAnimator(button, "scaleY", 1f, 350, 100, OvershootInterpolator())
                viewObjectAnimator(button, "alpha", 1f, 100, 100, LinearInterpolator())
                buttonTextView.setText(buttonText)
            }

        }

        if (listener != null) {
            button.setOnClickListener(listener)
        }
    }*/

    private val dialogFirstOpenListener = View.OnClickListener {
        UIElements.oneButtonHider(this)
        Values.hintCreateGradientDismissed = true
        Values.saveValues(this)
    }

    private val dialogExistsListener = View.OnClickListener {
        val holder = dialog.findViewById<ConstraintLayout>(R.id.holder)
        val button = dialog.findViewById<ConstraintLayout>(R.id.button)

        viewObjectAnimator(holder, "scaleX", 0.5f, 400, 0, AccelerateInterpolator(3f))
        viewObjectAnimator(holder, "scaleY", 0.5f, 400, 0, AccelerateInterpolator(3f))
        viewObjectAnimator(holder, "alpha", 0f, 200, 200, LinearInterpolator())

        viewObjectAnimator(button, "scaleX", 0.5f, 400, 0, AccelerateInterpolator(3f))
        viewObjectAnimator(button, "scaleY", 0.5f, 400, 0, AccelerateInterpolator(3f))
        viewObjectAnimator(button, "alpha", 0f, 200, 200, LinearInterpolator())

        Handler().postDelayed({
            dialog.dismiss()
            blurLayout.pauseBlur()
            blurLayout.visibility = View.GONE
            lastStepExitAnim(false)
            Handler().postDelayed({
                firstStepEnterAnim()
                submitStep = false
            }, 950)
        }, 450)
    }

    private val dialogCompleteListener = View.OnClickListener {
        val holder = dialog.findViewById<ConstraintLayout>(R.id.holder)
        val button = dialog.findViewById<ConstraintLayout>(R.id.button)

        viewObjectAnimator(holder, "scaleX", 0.5f, 400, 0, AccelerateInterpolator(3f))
        viewObjectAnimator(holder, "scaleY", 0.5f, 400, 0, AccelerateInterpolator(3f))
        viewObjectAnimator(holder, "alpha", 0f, 200, 200, LinearInterpolator())

        viewObjectAnimator(button, "scaleX", 0.5f, 400, 0, AccelerateInterpolator(3f))
        viewObjectAnimator(button, "scaleY", 0.5f, 400, 0, AccelerateInterpolator(3f))
        viewObjectAnimator(button, "alpha", 0f, 200, 200, LinearInterpolator())

        Handler().postDelayed({
            dialog.dismiss()
            blurLayout.pauseBlur()
            blurLayout.visibility = View.GONE
            lastStepExitAnim(true)
            Handler().postDelayed({
                onBackPressed()
            }, 850)
        }, 450)
    }

    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        when (popupName) {
            "missingInfo" -> {
                UIElement.popupDialogHider()
            }
            "gradientExists" -> {
                UIElement.popupDialogHider()
                Handler().postDelayed({
                    lastStepExitAnim(false)
                    Handler().postDelayed({
                        firstStepEnterAnim()
                        submitStep = false
                    }, 950)
                }, 450)
            }
            "gradientSubmitted" -> {
                when (position) {
                    0 -> {
                        val clipboardManager: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clipData = ClipData.newPlainText("Unique ID", gradientUID)
                        clipboardManager.setPrimaryClip(clipData)
                    }
                    1 -> {
                        UIElement.popupDialogHider()
                        Handler().postDelayed({
                            lastStepExitAnim(true)
                            Handler().postDelayed({
                                onBackPressed()
                            }, 850)
                        }, Values.dialogShowAgainTime)

                    }
                }
            }
        }
    }
}