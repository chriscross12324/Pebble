package com.simple.chris.pebble.activities

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.EditText
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.simple.chris.pebble.databinding.ActivityColourPickerBinding
import com.simple.chris.pebble.functions.*
import java.lang.Exception
import kotlin.random.Random

class ActivityColourPicker : AppCompatActivity() {
    private lateinit var binding: ActivityColourPickerBinding

    private var hexValue = 0
    private var hexString = ""

    var hueValue = 0
    var satValue = 0
    var valValue = 0

    var hsv = floatArrayOf(0f, 1f, 1f)
    private var hue = floatArrayOf(0f, 1f, 1f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppTheme(this)
        binding = ActivityColourPickerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        Values.currentActivity = "ColourPicker"

        setInitialPositions()
        setInitialValues()
        onCreateExtension()

        binding.colourPickerSaveButton.setOnClickListener {
            try {
                vibrateMedium(this)
                val colour = Color.parseColor("#$hexString")
                Values.gradientCreatorColours[Values.editingColourAtPos] = "#$hexString"

                animationOut()
            } catch (e: Exception) {
            }
        }

        binding.colourPickerBackButton.setOnClickListener {
            animationOut()
            vibrateWeak(this)
        }

        binding.randomColourButton.setOnClickListener {
            vibrateWeak(this)
            val rgbRNDM = Random
            val generatedColour = Color.rgb(rgbRNDM.nextInt(256), rgbRNDM.nextInt(256), rgbRNDM.nextInt(256))
            Color.colorToHSV(generatedColour, hsv)

            hueValue = hsv[0].toInt()
            satValue = (hsv[1] * 100).toInt()
            valValue = (hsv[2] * 100).toInt()
            updateValues(true)
        }
    }

    private fun setInitialValues() {
        hexValue = Color.parseColor(Values.gradientCreatorColours[Values.editingColourAtPos])
        Color.colorToHSV(hexValue, hsv)

        /** Set HSV values **/
        hueValue = hsv[0].toInt()
        satValue = (hsv[1] * 100).toInt()
        valValue = (hsv[2] * 100).toInt()

        updateValues(true)
    }

    fun updateValues(animateSeekBars: Boolean) {
        hsv[0] = 360f * hueValue / 360
        hue[0] = 360f * hueValue / 360
        hsv[1] = satValue / 100f
        hsv[2] = valValue / 100f

        hexString = "" + Integer.toHexString(Color.HSVToColor(hsv)).substring(2)
        hexValue = Color.HSVToColor(hsv)

        updateView(animateSeekBars)

    }

    private fun updateView(animateSeekBars: Boolean) {
        binding.colourPickerColourViewer.setBackgroundColor(hexValue)
        //hexValueEditText.setText(hexString)
        setHEXTextColour()
        binding.hexValueEditText.clearFocus()
        binding.hueText.clearFocus()
        binding.satText.clearFocus()
        binding.valText.clearFocus()

        /*hueText.setText("$hueValue")
        satText.setText("$satValue")
        valText.setText("$valValue")*/

        if (animateSeekBars) {
            seekBarAnimator(binding.hueSeekBar, binding.hueText, hueValue.toFloat())
            seekBarAnimator(binding.saturationSeekBar, binding.satText, satValue.toFloat())
            seekBarAnimator(binding.valueSeekBar, binding.valText, valValue.toFloat())
            //Toast.makeText(this, "#$valValue", Toast.LENGTH_SHORT).show()
        } else {
            binding.hueText.setText("$hueValue")
            binding.satText.setText("$satValue")
            binding.valText.setText("$valValue")
        }

        if (!binding.hexValueEditText.isFocused && binding.hexValueEditText.text.toString() != "#$hexString") {
            binding.hexValueEditText.setText(hexString)
            //Toast.makeText(this, "#$hexString", Toast.LENGTH_SHORT).show()
        }

        val satGradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(Color.parseColor("#EAEAEA"), Color.HSVToColor(hue))
        )
        satGradientDrawable.cornerRadius = convertFloatToDP(this, 20f)
        binding.satBackground.background = satGradientDrawable
    }

    private fun setHEXTextColour() {
        /** Get RGB values from hexValue **/
        val r = Color.red(hexValue)
        val g = Color.green(hexValue)
        val b = Color.blue(hexValue)

        /** Set text colour based on hexValue luminance **/
        val luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255
        if (luminance > 0.5) {
            binding.hexValueTextView.setTextColor(Color.parseColor("#000000"))
            binding.hexValueEditText.setTextColor(Color.parseColor("#000000"))
        } else {
            binding.hexValueTextView.setTextColor(Color.parseColor("#ffffff"))
            binding.hexValueEditText.setTextColor(Color.parseColor("#ffffff"))
        }
    }

    private fun setInitialPositions() {
        binding.colourPickerSliders.translationY = convertFloatToDP(this, binding.colourPickerSliders.height + 94.toFloat())

        binding.colourPickerSliders.post {
            animationIn()
        }
    }

    private fun animationIn() {
        UIElements.viewObjectAnimator(binding.colourPickerSliders, "translationY", 0f, 700, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.colourPickerSaveButton, "translationY", 0f, 700, 250, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.colourPickerBackButton, "translationY", 0f, 700, 300, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.randomColourButton, "translationY", 0f, 700, 300, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.colourCodeHolder, "alpha", 1f, 700, 250, LinearInterpolator())
    }

    private fun animationOut() {
        UIElements.viewObjectAnimator(binding.colourPickerSliders, "translationY", binding.colourPickerSliders.height.toFloat() + convertFloatToDP(this, 94f), 700, 100, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.colourPickerSaveButton, "translationY", convertFloatToDP(this, 74f), 700, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.colourPickerBackButton, "translationY", convertFloatToDP(this, 74f), 700, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.randomColourButton, "translationY", convertFloatToDP(this, 74f), 700, 0, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.colourCodeHolder, "translationY", (binding.colourPickerSliders.height.toFloat() + convertFloatToDP(this, 94f)) / 2, 700, 100, DecelerateInterpolator(3f))
        UIElements.viewObjectAnimator(binding.colourCodeHolder, "alpha", 0f, 500, 450, LinearInterpolator())

        Handler(Looper.getMainLooper()).postDelayed({
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
        hueGradientDrawable.cornerRadius = convertFloatToDP(this, 20f)
        binding.hueBackground.background = hueGradientDrawable

        val valGradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(Color.parseColor("#000000"), Color.parseColor("#EAEAEA"))
        )
        valGradientDrawable.cornerRadius = convertFloatToDP(this, 20f)
        binding.valBackground.background = valGradientDrawable


        /** Slider listeners **/
        binding.hueSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    hueValue = progress
                    updateValues(false)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        binding.saturationSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    satValue = progress
                    updateValues(false)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        binding.valueSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    valValue = progress
                    updateValues(false)
                }
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

        binding.hueText.setOnKeyListener { _, _, event ->
            if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
                hueValue = binding.hueText.text.toString().toInt()
                updateValues(true)
            }
            false
        }

        binding.satText.setOnKeyListener { _, _, event ->
            if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
                satValue = binding.satText.text.toString().toInt()
                updateValues(true)
            }
            false
        }

        binding.valText.setOnKeyListener { _, _, event ->
            if (event.keyCode == KeyEvent.KEYCODE_ENTER) {
                valValue = binding.valText.text.toString().toInt()
                updateValues(true)
            }
            false
        }

    }

    private fun seekBarAnimator(view: SeekBar, editText: EditText, endValue: Float) {
        val valueAnimator = ValueAnimator.ofFloat(view.progress.toFloat(), endValue)
        valueAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            view.progress = value.toInt()
            editText.setText("${value.toInt()}")
            when (view.tag) {
                "hue" -> {
                    hueValue = value.toInt()
                    updateValues(false)
                }
                "sat" -> {
                    satValue = value.toInt()
                    updateValues(false)
                }
                "val" -> {
                    valValue = value.toInt()
                    updateValues(false)
                }
            }
        }
        valueAnimator.interpolator = DecelerateInterpolator(2f)
        if (Values.settingSpecialEffects) {
            valueAnimator.duration = 500
        } else {
            valueAnimator.duration = 0
        }
        valueAnimator.start()
    }

    override fun onResume() {
        super.onResume()

        /** Checks if app setting unloaded during pause **/
        if (!Values.valuesLoaded) {
            startActivity(Intent(this, ActivityStarting::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        } else {
            Values.saveValues(this)
        }

    }
}