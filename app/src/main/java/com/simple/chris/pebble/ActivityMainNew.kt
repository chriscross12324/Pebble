package com.simple.chris.pebble

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.simple.chris.pebble.ui.fragmentbrowsenew.FragmentBrowseNew

class ActivityMainNew : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_new)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FragmentBrowseNew.newInstance())
                .commitNow()
        }
    }
}