package com.simple.chris.pebble

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ActivitySettings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_settings)
    }
}
