package com.simple.chris.pebble

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity

class ColourPickerTester : AppCompatActivity() {

    private lateinit var redSeek: SeekBar
    private lateinit var greenSeek: SeekBar
    private lateinit var blueSeek: SeekBar
    private lateinit var done: LinearLayout
    private lateinit var editText: EditText
    private lateinit var colourDisplay: RelativeLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_colour_picker_tester)

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.colour_picker_dialog)

        redSeek = dialog.findViewById(R.id.brush_red_seek)
        greenSeek = dialog.findViewById(R.id.brush_green_seek)
        blueSeek = dialog.findViewById(R.id.brush_blue_seek)
        done = dialog.findViewById(R.id.brush_done)
        editText = dialog.findViewById(R.id.brush_edit_color)
        colourDisplay = dialog.findViewById(R.id.brush_color_linear)

        colourDisplay.setBackgroundColor(Color.parseColor("#f6d365"))

        redSeek.max = 255
        greenSeek.max = 255
        blueSeek.max = 255

    }
}
