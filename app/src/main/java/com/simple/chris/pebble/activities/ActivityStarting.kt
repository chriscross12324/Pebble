package com.simple.chris.pebble.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.simple.chris.pebble.functions.Values

class ActivityStarting : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen();
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition {true}
        Values.loadValues(this)
        //Values.browseRecyclerScrollPos = 0

        startMainActivity()
        finish()
    }

    private fun startMainActivity() {
        Values.firstStart = false
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}
