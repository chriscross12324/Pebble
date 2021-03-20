package com.simple.chris.pebble.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.simple.chris.pebble.R
import com.simple.chris.pebble.adapters_helpers.DialogPopup
import com.simple.chris.pebble.functions.*
import kotlinx.android.synthetic.main.activity_main.*

class SplashScreen : AppCompatActivity() {

    var acceptingPerms = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.ThemeDark)
        setContentView(R.layout.activity_splash)
        Values.loadValues(this)
        Values.browseRecyclerScrollPos = 0

        if (!Values.firstStart) {
            startMainActivity()
        } else {
            showPermissionsDialog()
        }
    }

    private fun showPermissionsDialog() {
        if (!readStoragePermissionGiven() && acceptingPerms) {
            Values.dialogPopup = DialogPopup.newDialog(HashMaps.allowDeny(), "splashStorage", R.drawable.icon_storage,
                    R.string.word_storage, null, R.string.sentence_needs_storage_permission, null)
            Values.dialogPopup.show(supportFragmentManager, "splashStorage")
        } else {
            startMainActivity()
        }
    }

    private fun startMainActivity() {
        Values.firstStart = false
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    private fun readStoragePermissionGiven(): Boolean {
        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        return readPermission == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("NewApi")
    fun popupDialogHandler(dialogName: String, position: Int) {
        when (dialogName) {
            "splashStorage" -> {
                when (position) {
                    0 -> {
                        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
                    }
                    1 -> {
                        acceptingPerms = false
                        startMainActivity()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (permissions[0]) {
            "android.permission.WRITE_EXTERNAL_STORAGE" -> {
                when (grantResults[0]) {
                    PackageManager.PERMISSION_GRANTED -> {
                        startMainActivity()
                    }
                    PackageManager.PERMISSION_DENIED -> {
                        Toast.makeText(this, "Permission Denied: App Features may not be available || Grant in App-Info", Toast.LENGTH_LONG).show()
                        startMainActivity()
                    }
                }
            }
        }
        Log.e("INFO", permissions[0])
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
