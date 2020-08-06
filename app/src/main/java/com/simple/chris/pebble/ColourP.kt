package com.simple.chris.pebble

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.layout_colour_picker.*
import java.lang.Exception

class ColourP : AppCompatActivity() {

    private var hexValue = 0
    private var hexString = ""

    var hueValue = 0
    var satValue = 0
    var valValue = 0

    var hsv = floatArrayOf(0f, 1f, 1f)
    private var hue = floatArrayOf(0f, 1f, 1f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElement.setTheme(this)
        setContentView(R.layout.layout_colour_picker)
        Values.currentActivity = "ColourPicker"

        setInitialPositions()
        setInitialValues()
        onCreateExtension()

        colourPickerSaveButton.setOnClickListener {
            try {
                val colour = Color.parseColor("#$hexString")
                when(Values.currentColourPOS) {
                    "startColour" -> Values.gradientCreatorStartColour = "#$hexString"
                    "endColour" -> Values.gradientCreatorEndColour = "#$hexString"
                }

                animationOut()
            } catch (e: Exception) {}
        }

        colourPickerBackButton.setOnClickListener {
                animationOut()
        }
    }

    private fun setInitialValues() {
        if (Values.currentColourPOS == "startColour") {
            hexValue = Color.parseColor(Values.gradientCreatorStartColour)
            selectText.text = "Start"
        } else {
            hexValue = Color.parseColor(Values.gradientCreatorEndColour)
            selectText.text = "End"
        }
        Color.colorToHSV(hexValue, hsv)

        /** Set HSV values **/
        hueValue = hsv[0].toInt()
        satValue = (hsv[1] * 100).toInt()
        valValue = (hsv[2] * 100).toInt()

        updateValues()
    }

    fun updateValues() {
        hsv[0] = 360f * hueValue / 360
        hue[0] = 360f * hueValue / 360
        hsv[1] = satValue / 100f
        hsv[2] = valValue / 100f

        hexString = "" + Integer.toHexString(Color.HSVToColor(hsv)).substring(2)
        hexValue = Color.HSVToColor(hsv)

        updateView()
    }

    private fun updateView() {
        colourPickerColourViewer.setBackgroundColor(hexValue)
        //hexValueEditText.setText(hexString)
        setHEXTextColour()

        hueText.setText("$hueValue")
        satText.setText("$satValue")
        valText.setText("$valValue")

        if (!hueSeekBar.isFocused || !saturationSeekBar.isFocused || !valueSeekBar.isFocused) {
            hueSeekBar.progress = hueValue
            saturationSeekBar.progress = satValue
            valueSeekBar.progress = valValue
            //Toast.makeText(this, "#$valValue", Toast.LENGTH_SHORT).show()
        }

        if (!hexValueEditText.isFocused && hexValueEditText.text.toString() != "#$hexString") {
            hexValueEditText.setText(hexString)
            //Toast.makeText(this, "#$hexString", Toast.LENGTH_SHORT).show()
        }

        val satGradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(Color.parseColor("#EAEAEA"), Color.HSVToColor(hue))
        )
        satGradientDrawable.cornerRadius = Calculations.convertToDP(this, 20f)
        satBackground.background = satGradientDrawable
    }

    private fun setHEXTextColour() {
        /** Get RGB values from hexValue **/
        val r = Color.red(hexValue)
        val g = Color.green(hexValue)
        val b = Color.blue(hexValue)

        /** Set text colour based on hexValue luminance **/
        val luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255
        if (luminance > 0.5) {
            hexValueTextView.setTextColor(Color.parseColor("#000000"))
            hexValueEditText.setTextColor(Color.parseColor("#000000"))
        } else {
            hexValueTextView.setTextColor(Color.parseColor("#ffffff"))
            hexValueEditText.setTextColor(Color.parseColor("#ffffff"))
        }
    }

    private fun setInitialPositions() {
        colourPickerSliders.translationY = Calculations.convertToDP(this, colourPickerSliders.height + 94.toFloat())

        colourPickerSliders.post {
            animationIn()
        }
    }

    private fun animationIn() {
        UIElements.viewObjectAnimator(colourPickerSliders, "translationY", 0f, 700, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(colourPickerSaveButton, "translationY", 0f, 700, 250, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(colourPickerBackButton, "translationY", 0f, 700, 300, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(colourCodeHolder, "alpha", 1f, 700, 250, LinearInterpolator())
    }

    private fun animationOut() {
        UIElements.viewObjectAnimator(colourPickerSliders, "translationY", colourPickerSliders.height.toFloat() + Calculations.convertToDP(this, 94f), 700, 100, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(colourPickerSaveButton, "translationY", Calculations.convertToDP(this, 74f), 700, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(colourPickerBackButton, "translationY", Calculations.convertToDP(this, 74f), 700, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(colourCodeHolder, "translationY", (colourPickerSliders.height.toFloat() + Calculations.convertToDP(this, 94f)) / 2, 700, 100, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(colourCodeHolder, "alpha", 0f, 500, 450, LinearInterpolator())

        Handler().postDelayed({
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 950)
    }

    private fun onCreateExtension() {

        /** Set slider backgrounds **/
        val hueGradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(Color.parseColor("#f00000"), Color.parseColor("#ffff00"), Color.parseColor("#00ff00"), Color.parseColor("#00ffff"),
                        Color.parseColor("#0000ff"), Color.parseColor("#ff00ff"), Color.parseColor("#f00000"))
        )
        hueGradientDrawable.cornerRadius = Calculations.convertToDP(this, 20f)
        hueBackground.background = hueGradientDrawable

        val valGradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(Color.parseColor("#000000"), Color.parseColor("#EAEAEA"))
        )
        valGradientDrawable.cornerRadius = Calculations.convertToDP(this, 20f)
        valBackground.background = valGradientDrawable


        /** Slider listeners **/
        hueSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                hueValue = progress
                updateValues()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        saturationSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                satValue = progress
                updateValues()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        valueSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                valValue = progress
                updateValues()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })


        /** EditText listeners **/
        /*hexValueEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s!!.length == 6) {
                    Color.colorToHSV(Color.parseColor("#$s"), hsv)
                    updateValues()
                }
            }

        })*/

        hueText.setOnKeyListener { _, _, event ->
            if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
                hueValue = hueText.text.toString().toInt()
                updateValues()
            }
            false
        }

        satText.setOnKeyListener { _, _, event ->
            if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
                satValue = satText.text.toString().toInt()
                updateValues()
            }
            false
        }

        valText.setOnKeyListener { _, _, event ->
            if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
                valValue = valText.text.toString().toInt()
                updateValues()
            }
            false
        }

    }

    override fun onResume() {
        super.onResume()

        /** Checks if app setting unloaded during pause **/
        if (!Values.valuesLoaded) {
            startActivity(Intent(this, SplashScreen::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        } else {
            Values.saveValues(this)
        }

    }
}