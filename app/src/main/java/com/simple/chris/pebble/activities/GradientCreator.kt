package com.simple.chris.pebble.activities

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.simple.chris.pebble.*
import com.simple.chris.pebble.functions.Calculations.convertToDP
import com.simple.chris.pebble.functions.UIElements.viewObjectAnimator
import com.simple.chris.pebble.adapters_helpers.PopupDialogButtonRecycler
import com.simple.chris.pebble.adapters_helpers.SQLiteHelper
import com.simple.chris.pebble.adapters_helpers.SQLiteHelperFull
import com.simple.chris.pebble.functions.*
import kotlinx.android.synthetic.main.activity_gradient_creator.*
import kotlinx.android.synthetic.main.activity_feedback.*
import org.apache.commons.lang3.RandomStringUtils
import kotlin.math.roundToInt
import kotlin.random.Random

class GradientCreator : AppCompatActivity(), PopupDialogButtonRecycler.OnButtonListener {

    lateinit var dialog: Dialog

    private var startColour = 0
    private var endColour = 0
    private var submitStep = false
    private var gradientExists = false
    var gradientUID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElement.setTheme(this)
        setContentView(R.layout.activity_gradient_creator)
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
            Vibration.mediumFeedback(this)
            if (!submitStep) {
                firstStepExitAnim(false)
                submitStep = true
                Handler().postDelayed({
                    lastStepEnterAnim()
                }, 800)
            } else {
                gradientExists = false
                checkConnection()
                nextStepButton.isEnabled = false
            }
        }

        /**
         * Performs tasks when lastStepButton is pressed
         */
        lastStepButton.setOnClickListener {
            Vibration.lowFeedback(this)
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
            Vibration.lowFeedback(this)
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
            Vibration.lowFeedback(this)
            firstStepExitAnim(false)
            Handler().postDelayed({
                Values.currentColourPOS = "endColour"
                startActivity(Intent(this, ColourPicker::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }, 500)
        }

        /**
         * Randomly generates gradient
         */
        randomGradientButton.setOnClickListener {
            Vibration.lowFeedback(this)
            UIElements.viewVisibility(touchBlocker, View.VISIBLE, 0)
            viewObjectAnimator(backgroundFadeOut, "alpha", 1f, 450, 0, LinearInterpolator())
            viewObjectAnimator(endColourPicker, "translationY", convertToDP(this, 58f), 450, 0, DecelerateInterpolator(3f))
            viewObjectAnimator(startColourPicker, "translationY", convertToDP(this, 116f), 450, 0, DecelerateInterpolator(3f))
            UIElements.viewVisibility(touchBlocker, View.GONE, 700)
            viewObjectAnimator(backgroundFadeOut, "alpha", 0f, 450, 550, LinearInterpolator())
            viewObjectAnimator(endColourPicker, "translationY", 0f, 450, 550, DecelerateInterpolator(3f))
            viewObjectAnimator(startColourPicker, "translationY", 0f, 450, 550, DecelerateInterpolator(3f))
            Values.gradientCreatorStartColour = ""
            Handler().postDelayed({
                refreshGradientDrawable()
            }, 450)
        }

        gradientCreatorGradientName.setOnKeyListener { _, _, event ->
            if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
                UIElement.hideSoftKeyboard(this)
                gradientCreatorGradientName.clearFocus()
            }
            false
        }

        gradientCreatorGradientDescription.imeOptions = EditorInfo.IME_ACTION_DONE
        gradientCreatorGradientDescription.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)

    }

    private fun refreshGradientDrawable() {
        /**
         * Re-draws gradient after start/end colour change
         */
        if (Values.gradientCreatorStartColour == "") {
            val startRNDM = Random
            startColour = Color.rgb(startRNDM.nextInt(256), startRNDM.nextInt(256), startRNDM.nextInt(256))
            endColour = Color.rgb(startRNDM.nextInt(256), startRNDM.nextInt(256), startRNDM.nextInt(256))
            Values.gradientCreatorStartColour = "#" + Integer.toHexString(startColour).substring(2)
            Values.gradientCreatorEndColour = "#" + Integer.toHexString(endColour).substring(2)
        } else {
            startColour = Color.parseColor(Values.gradientCreatorStartColour)
            endColour = Color.parseColor(Values.gradientCreatorEndColour)
        }
        UIElement.gradientDrawable(this, gradientCreatorGradientViewer, startColour, endColour, 0f)

        val gradientDrawableStartCircle = GradientDrawable()
        gradientDrawableStartCircle.shape = GradientDrawable.OVAL
        gradientDrawableStartCircle.setStroke(convertToDP(this, 5f).roundToInt(), startColour)
        val gradientDrawableEndCircle = GradientDrawable()
        gradientDrawableEndCircle.shape = GradientDrawable.OVAL
        gradientDrawableEndCircle.setStroke(convertToDP(this, 5f).roundToInt(), endColour)
        startColourPreview.background = gradientDrawableStartCircle
        endColourPreview.background = gradientDrawableEndCircle
    }

    private fun sharedElementGradientTransition() {
        /**
         * Creates Sunshine gradient for sharedElementTransition
         */
        val sharedStartColour = ContextCompat.getColor(this, R.color.pebbleStart)
        val sharedEndColour = ContextCompat.getColor(this, R.color.pebbleEnd)
        UIElement.gradientDrawable(this, sharedElementsTransitionView, sharedStartColour, sharedEndColour, 20f)

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
        startColourPicker.translationY = convertToDP(this, 190f)
        endColourPicker.translationY = convertToDP(this, 132f)
        randomGradientButton.translationY = convertToDP(this, 74f)
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
            Handler().postDelayed({
                UIElement.popupDialog(this, "gradientCreator", R.drawable.icon_apps, R.string.dual_create_gradient, null, R.string.sentence_gradient_creator_welcome,
                        HashMaps.createGradientArrayList(), window.decorView, this)
            }, 1000)
        }
    }

    private fun firstStepEnterAnim() {
        /**
         * Animates all views in for firstStep
         */
        viewObjectAnimator(nextStepButton, "translationY", 0f, 700, 500, DecelerateInterpolator(3f))
        viewObjectAnimator(lastStepButton, "translationY", 0f, 700, 550, DecelerateInterpolator(3f))
        viewObjectAnimator(startColourPicker, "translationY", 0f, 700, 550, DecelerateInterpolator(3f))
        viewObjectAnimator(endColourPicker, "translationY", 0f, 700, 550, DecelerateInterpolator(3f))
        viewObjectAnimator(randomGradientButton, "translationY", 0f, 700, 550, DecelerateInterpolator(3f))
        UIElements.viewVisibility(startColourPicker, View.VISIBLE, 0)
        UIElements.viewVisibility(endColourPicker, View.VISIBLE, 0)
        UIElements.viewVisibility(randomGradientButton, View.VISIBLE, 0)

        /**
         * Sets icon/text for firstStep
         */
        lastStepIcon.setImageResource(R.drawable.icon_close)
        nextStepText.setText(R.string.word_next)
    }

    private fun firstStepExitAnim(mainMenu: Boolean) {
        /**
         * Animates all views out for firstStep
         */
        viewObjectAnimator(nextStepButton, "translationY", convertToDP(this, 74f), 700, 100, DecelerateInterpolator(3f))
        viewObjectAnimator(lastStepButton, "translationY", convertToDP(this, 74f), 700, 0, DecelerateInterpolator(3f))
        viewObjectAnimator(startColourPicker, "translationY", convertToDP(this, 190f), 700, 0, DecelerateInterpolator(3f))
        viewObjectAnimator(endColourPicker, "translationY", convertToDP(this, 132f), 700, 0, DecelerateInterpolator(3f))
        viewObjectAnimator(randomGradientButton, "translationY", convertToDP(this, 74f), 700, 0, DecelerateInterpolator(3f))

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
        UIElements.viewVisibility(randomGradientButton, View.GONE, 0)

        /**
         * Sets icon/text for lastStep
         */
        lastStepIcon.setImageResource(R.drawable.icon_back)
        nextStepText.setText(R.string.word_submit)
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
                            UIElement.popupDialog(this, "gradientExists", R.drawable.icon_attention, R.string.dual_gradient_exists, null, R.string.sentence_colour_combination_exists, HashMaps.gradientExistsArrayList(), window.decorView, this)
                            gradientExists = true
                            break
                        }
                    }

                    /** Submits gradient if it doesn't already exist */
                    if (count + 1 == Values.gradientList.size && !gradientExists) {
                        //progressPopupDialog(R.string.dialog_title_eng_submitting, R.string.dialog_body_eng_uploading, null, null)
                        UIElement.popupDialog(this, "submittingGradient", null, R.string.word_submitting, null, R.string.sentence_uploading_gradient, null, window.decorView, null)
                        gradientPush()
                    }
                }
            } catch (e: Exception) {
                Log.e("ERR", "pebble.simple.gradient_creator.submit_logic: ${e.localizedMessage}")
            }
        } else {
            //popupDialog(R.drawable.icon_question, "Missing Info", R.string.dialog_body_eng_gradient_create_missing, R.string.text_eng_ok, dialogMissingListener)
            UIElement.popupDialog(this, "missingInfo", R.drawable.icon_question, R.string.dual_missing_info, null, R.string.sentence_missing_gradient_name, HashMaps.missingInfoArrayList(), window.decorView, this)
        }
    }

    private fun gradientPush() {
        gradientUID = RandomStringUtils.randomAlphabetic(5)
        val gradientDatabaseURL = "https://script.google.com/macros/s/AKfycbwFkoSBTbmeB6l9iIiZWGczp9sDEjqX0jiYeglczbLKFAXsmtB1/exec"

        val stringRequest: StringRequest = object : StringRequest(Method.POST, gradientDatabaseURL,
                Response.Listener { gradientPushComplete() },
                Response.ErrorListener {
                    /** Set Error Listener **/
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
        /** Insert Gradient into "My Gradients" database **/
        val db = SQLiteHelper(this)
        db.insertGradient(gradientCreatorGradientName.text.toString(), Values.gradientCreatorStartColour, Values.gradientCreatorEndColour, gradientCreatorGradientDescription.text.toString(), gradientUID)
        val offlineDB = SQLiteHelperFull(this)
        offlineDB.insertGradient(gradientCreatorGradientName.text.toString(), Values.gradientCreatorStartColour, Values.gradientCreatorEndColour, gradientCreatorGradientDescription.text.toString())

        val newGradient = HashMap<String, String>()
        newGradient["gradientName"] = gradientCreatorGradientName.text.toString()
        newGradient["startColour"] = Values.gradientCreatorStartColour
        newGradient["endColour"] = Values.gradientCreatorEndColour
        newGradient["description"] = gradientCreatorGradientDescription.text.toString()
        Values.gradientList.add(0, newGradient)

        Log.e("INFO", "Submitted: ${Values.gradientCreatorStartColour}")

        Values.justSubmitted = true
        Values.gradientCreatorGradientName = ""
        Values.gradientCreatorStartColour = ""
        Values.gradientCreatorEndColour = ""
        Values.gradientCreatorDescription = ""
        Values.saveValues(this)

        //popupDialog(R.drawable.icon_check, gradientUID, R.string.dialog_body_eng_submitted, R.string.text_eng_exit, dialogCompleteListener)
        UIElement.popupDialogHider()
        UIElement.popupDialog(this, "gradientSubmitted", R.drawable.icon_check, null, gradientUID, R.string.sentence_gradient_unique_code, HashMaps.gradientSubmittedArrayList(), window.decorView, this)
    }

    private fun checkConnection() {
        when (Connection.getConnectionType(this)) {
            0 -> {
                UIElement.popupDialog(this, "noConnection", R.drawable.icon_warning, R.string.dual_no_connection, null,
                        R.string.sentence_needs_internet_connection, HashMaps.noConnectionArrayList(), window.decorView, this)
            }
            1, 2 -> {
                if (Values.gradientList.isEmpty()) {
                    UIElement.popupDialog(this, "gradientsNotDownloaded", R.drawable.icon_warning, R.string.dual_offline_mode, null,
                            R.string.sentence_needs_updated_gradients, HashMaps.gradientArrayNotUpdated(), window.decorView, this)
                } else {
                    submitLogic()
                }
            }
        }
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

    /**
     * Button listeners for popupDialog
     */
    override fun onButtonClickPopup(popupName: String, position: Int, view: View) {
        when (popupName) {
            "gradientCreator" -> {
                when (position) {
                    0 -> {
                        UIElement.popupDialogHider()
                        Values.hintCreateGradientDismissed = true
                        Values.saveValues(this)
                    }
                    1 -> {
                        UIElement.popupDialogHider()
                        Handler().postDelayed({
                            firstStepExitAnim(true)
                            Handler().postDelayed({
                                onBackPressed()
                            }, 850)
                        }, Values.dialogShowAgainTime)
                    }
                }
            }
            "missingInfo" -> {
                UIElement.popupDialogHider()
                nextStepButton.isEnabled = true
            }
            "gradientExists" -> {
                UIElement.popupDialogHider()
                Handler().postDelayed({
                    lastStepExitAnim(false)
                    Handler().postDelayed({
                        firstStepEnterAnim()
                        submitStep = false
                        nextStepButton.isEnabled = true
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
            "noConnection" -> {
                when (position) {
                    0 -> {
                        UIElement.popupDialogHider()
                        Handler().postDelayed({
                            checkConnection()
                        }, Values.dialogShowAgainTime)
                    }
                    1 -> {
                        UIElement.popupDialogHider()
                        nextStepButton.isEnabled = true
                    }
                }
            }
            "gradientsNotDownloaded" -> {
                when (position) {
                    0 -> {
                        UIElement.popupDialogHider()
                        Values.justSubmitted = true
                        Handler().postDelayed({
                            lastStepExitAnim(true)
                            Handler().postDelayed({
                                onBackPressed()
                            }, 850)
                        }, Values.dialogShowAgainTime)
                    }
                    1 -> {
                        UIElement.popupDialogHider()
                        submitButton.isEnabled = true
                    }
                }
            }
        }
    }
}