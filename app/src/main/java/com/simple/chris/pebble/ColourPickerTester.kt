package com.simple.chris.pebble

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.colour_picker_test.*


class ColourPickerTester : AppCompatActivity() {

    var hexValue = 0

    var hueProgress = 0
    var satProgress = 0
    var valProgress = 0

    var hsv = floatArrayOf(0f, 1f, 1f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.colour_picker_test)
        Values.currentActivity = "ColourPicker"

        val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(Color.parseColor("#f00000"), Color.parseColor("#ffff00"), Color.parseColor("#00ff00"), Color.parseColor("#00ffff"), Color.parseColor("#0000ff"), Color.parseColor("#ff00ff"), Color.parseColor("#f00000"))
        )
        gradientDrawable.cornerRadius = Calculations.convertToDP(this, 20f)
        multiGradient.background = gradientDrawable

        val saturationDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(Color.parseColor("#EAEAEA"), Color.parseColor("#f00000"))
        )
        saturationDrawable.cornerRadius = Calculations.convertToDP(this, 20f)
        saturation.background = saturationDrawable

        val valueDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(Color.parseColor("#000000"), Color.parseColor("#EAEAEA"))
        )
        valueDrawable.cornerRadius = Calculations.convertToDP(this, 20f)
        value.background = valueDrawable


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
                hueUpdate()
            }
            false
        }

        setPaddingForSeekBars()

    }

    private fun setInitialValues() {
        if (Values.currentlyEditing == "start") {
            hexValue = Color.parseColor(Values.createGradientStartColour)
        } else {
            hexValue = Color.parseColor(Values.createGradientEndColour)
        }

        updateView()
    }

    private fun setPaddingForSeekBars() {
        hueSeekBar.setPadding(6, 0, 6, 0)
        saturationSeekBar.setPadding(6, 0, 6, 0)
        valueSeekBar.setPadding(6, 0, 6, 0)
    }

    private fun hueUpdate() {
        hsv[0] = 360f * hueProgress / 360
        hueText.setText("$hueProgress")
        updateView()

        val saturationDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(Color.parseColor("#EAEAEA"), hexValue)
        )
        saturationDrawable.cornerRadius = Calculations.convertToDP(this, 20f)
        saturation.background = saturationDrawable
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
        val hexString = "#" + Integer.toHexString(Color.HSVToColor(hsv)).substring(2)
        hexValue = Color.HSVToColor(hsv)
        imageView.setBackgroundColor(hexValue)
        hexValueTextView.text = hexString

        HEXToRGB(hexString)
    }

    private fun HEXToRGB(hex: String) {
        val color = Color.parseColor(hex)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        setHEXTextColour(r, g, b)
    }

    private fun setHEXTextColour(r: Int, g: Int, b: Int) {
        val luminance = (0.299 * r + 0.587 * g + 0.114 * b) / 255

        if (luminance > 0.5) {
            hexValueTextView.setTextColor(Color.parseColor("#000000"))
        } else {
            hexValueTextView.setTextColor(Color.parseColor("#ffffff"))
        }
    }
}
