package com.simple.chris.pebble

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.activity_browse.*
import kotlinx.android.synthetic.main.activity_gradient_details.*
import kotlinx.android.synthetic.main.layout_colour_picker.*
import java.lang.Exception
import java.nio.file.WatchEvent


class ColourPicker : AppCompatActivity() {

    var hexValue = 0
    var hexString = ""

    var hueProgress = 0
    var satProgress = 0
    var valProgress = 0

    var hsv = floatArrayOf(0f, 1f, 1f)
    var hue = floatArrayOf(0f, 1f, 1f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.layout_colour_picker)
        Values.currentActivity = "ColourPicker"

        setViewPositions()
        setInitialValues()

        val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(Color.parseColor("#f00000"), Color.parseColor("#ffff00"), Color.parseColor("#00ff00"), Color.parseColor("#00ffff"), Color.parseColor("#0000ff"), Color.parseColor("#ff00ff"), Color.parseColor("#f00000"))
        )
        gradientDrawable.cornerRadius = Calculations.convertToDP(this, 20f)
        hueBackground.background = gradientDrawable

        val saturationDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(Color.parseColor("#EAEAEA"), Color.HSVToColor(hue))
        )
        saturationDrawable.cornerRadius = Calculations.convertToDP(this, 20f)
        satBackground.background = saturationDrawable

        val valueDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(Color.parseColor("#000000"), Color.parseColor("#EAEAEA"))
        )
        valueDrawable.cornerRadius = Calculations.convertToDP(this, 20f)
        valBackground.background = valueDrawable

        hexValueEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length == 6) {
                    Color.colorToHSV(Color.parseColor("#$s"), hsv)
                    updateView()
                    Log.e("ERR", "6")
                } else {
                    Log.e("INFO", "${s.length}")
                }

            }

        })

        hueSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progressValue: Int, fromUser: Boolean) {
                hueProgress = progressValue
                hueUpdate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        saturationSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progressValue: Int, fromUser: Boolean) {
                satProgress = progressValue
                satUpdate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        valueSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progressValue: Int, fromUser: Boolean) {
                valProgress = progressValue
                valUpdate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        hueText.setOnKeyListener { _, _, keyEvent ->
            if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                hueProgress = hueText.text.toString().toInt()
                hueSeekBar.progress = hueProgress
                hueUpdate()
            }
            false
        }

        satText.setOnKeyListener { _, _, keyEvent ->
            if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                satProgress = satText.text.toString().toInt()
                saturationSeekBar.progress = satProgress
                satUpdate()
            }
            false
        }

        valText.setOnKeyListener { _, _, keyEvent ->
            if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                valProgress = valText.text.toString().toInt()
                valueSeekBar.progress = valProgress
                valUpdate()
            }
            false
        }

        colourPickerSaveButton.setOnClickListener {
            try {
                val colour = Color.parseColor("#$hexString")
                if (Values.currentColourPOS == "startColour") {
                    Values.gradientCreatorStartColour = "#$hexString"
                } else {
                    Values.gradientCreatorEndColour = "#$hexString"
                }

                animationOut()
            } catch (e: Exception) {
            }
        }

        colourPickerBackButton.setOnClickListener {
            animationOut()
        }

    }

    private fun setInitialValues() {
        hexValue = if (Values.currentColourPOS == "startColour") {
            Color.parseColor(Values.gradientCreatorStartColour)
        } else {
            Color.parseColor(Values.gradientCreatorEndColour)
        }

        Color.colorToHSV(hexValue, hsv)

        hueProgress = hsv[0].toInt()
        hueSeekBar.progress = hueProgress
        satProgress = (hsv[1] * 100).toInt()
        saturationSeekBar.progress = satProgress
        valProgress = (hsv[2] * 100).toInt()
        valueSeekBar.progress = valProgress

        hueUpdate()
        satUpdate()
        valUpdate()

        hexValueEditText.setOnKeyListener { _, _, keyEvent ->
            if (keyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                hexValueEditText.clearFocus()
                val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(hexValueEditText.windowToken, 0)
            }
            false
        }
    }

    private fun hueUpdate() {
        hsv[0] = 360f * hueProgress / 360
        hue[0] = 360f * hueProgress / 360
        hueText.setText("$hueProgress")
        updateView()

        val saturationDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(Color.parseColor("#EAEAEA"), Color.HSVToColor(hue))
        )
        saturationDrawable.cornerRadius = Calculations.convertToDP(this, 20f)
        satBackground.background = saturationDrawable
    }

    private fun satUpdate() {
        hsv[1] = satProgress / 100f
        satText.setText("$satProgress")
        updateView()
    }

    private fun valUpdate() {
        hsv[2] = valProgress / 100f
        valText.setText("$valProgress")
        updateView()
    }

    private fun updateView() {
        hexString = "" + Integer.toHexString(Color.HSVToColor(hsv)).substring(2)
        hexValue = Color.HSVToColor(hsv)
        colourPickerColourViewer.setBackgroundColor(hexValue)
        if (hexString != hexValueEditText.text.toString()) {
            hexValueEditText.setText(hexString)
        }

        HEXToRGB(hexString)
    }

    private fun HEXToRGB(hex: String) {
        val color = Color.parseColor("#$hex")
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        setHEXTextColour(r, g, b)
    }

    private fun setHEXTextColour(r: Int, g: Int, b: Int) {
        val luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255

        if (luminance > 0.5) {
            hexValueTextView.setTextColor(Color.parseColor("#000000"))
            hexValueEditText.setTextColor(Color.parseColor("#000000"))
        } else {
            hexValueTextView.setTextColor(Color.parseColor("#ffffff"))
            hexValueEditText.setTextColor(Color.parseColor("#ffffff"))
        }
    }

    private fun setViewPositions() {
        colourPickerSliders.translationY = Calculations.convertToDP(this, colourPickerSliders.height + 94.toFloat())
        invalidColourNotification.translationY = (-60 * resources.displayMetrics.density)

        colourPickerColourViewer.post {
            animationIn()
        }
    }

    private fun animationIn() {
        UIElements.viewObjectAnimator(colourPickerBackButton, "translationY", 0f, 700, 250, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(colourPickerSaveButton, "translationY", 0f, 700, 250, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(colourPickerSliders, "translationY", 0f, 700, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(colourCodeHolder, "alpha", 1f, 700, 500, LinearInterpolator())
    }

    private fun animationOut() {
        UIElements.viewObjectAnimator(colourPickerBackButton, "translationY", Calculations.convertToDP(this, 74f), 700, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(colourPickerSaveButton, "translationY", Calculations.convertToDP(this, 74f), 700, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(colourPickerSliders, "translationY", colourPickerSliders.height.toFloat() + Calculations.convertToDP(this, 94f), 850, 100, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(colourCodeHolder, "translationY", (colourPickerSliders.height.toFloat() + Calculations.convertToDP(this, 94f)) / 2, 850, 100, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(colourCodeHolder, "alpha", 0f, 500, 450, LinearInterpolator())

        Handler().postDelayed({
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 950)
    }

    override fun onResume() {
        super.onResume()
        /**
         * Checks if app settings unloaded during pause
         */
        if (!Values.valuesLoaded) {
            startActivity(Intent(this, SplashScreen::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        } else {
            when (Values.currentActivity) {
                else -> {
                    Values.currentActivity = "ColourPicker"
                }
            }
            Values.saveValues(this)
        }
    }
}
