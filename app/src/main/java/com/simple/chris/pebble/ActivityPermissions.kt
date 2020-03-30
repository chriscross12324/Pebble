package com.simple.chris.pebble

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class ActivityPermissions : AppCompatActivity() {

    private lateinit var wifiPermissionDialog: Dialog
    private lateinit var dataWarningDialog: Dialog

    private lateinit var wifiUnderstandButton: Button
    private lateinit var dataUnderstandButton: Button

    private lateinit var warningNotification: ConstraintLayout

    private var dialogWidth: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        setContentView(R.layout.activity_permissions)

        wifiPermissionDialog = Dialog(this)
        dataWarningDialog = Dialog(this)

        wifiPermissionDialog.setContentView(R.layout.dialog_wifi)
        dataWarningDialog.setContentView(R.layout.dialog_data)

        wifiPermissionDialog.setCancelable(false)
        dataWarningDialog.setCancelable(false)

        val background = findViewById<ImageView>(R.id.background)
        val viewTreeObserver = background.viewTreeObserver
        viewTreeObserver.addOnGlobalLayoutListener {
            setLayout()
        }
    }

    private fun setLayout() {
        val layoutParamsWifi = wifiPermissionDialog.window?.attributes
        val windowWifi = wifiPermissionDialog.window
        layoutParamsWifi!!.dimAmount = 0f
        layoutParamsWifi.gravity = Gravity.CENTER
        windowWifi!!.attributes = layoutParamsWifi

        val layoutParamsData = dataWarningDialog.window?.attributes
        val windowData = dataWarningDialog.window
        layoutParamsData!!.dimAmount = 0f
        layoutParamsData.gravity = Gravity.CENTER
        windowData!!.attributes = layoutParamsData

        wifiUnderstandButton = wifiPermissionDialog.findViewById(R.id.understandButton)
        dataUnderstandButton = dataWarningDialog.findViewById(R.id.understandButton)

        warningNotification = findViewById(R.id.warningNotification)
        warningNotification.translationY = (-90 * resources.displayMetrics.density)

        wifiPermissionDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dataWarningDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        showWifiPermissionDialog()
    }

    private fun showWifiPermissionDialog() {
        wifiPermissionDialog.setOnShowListener {
            val dialog = wifiPermissionDialog.findViewById<View>(R.id.rootLayout)
            dialogWidth = dialog.width
        }
        wifiPermissionDialog.show()

        wifiUnderstandButton.setOnClickListener {
            wifiPermissionDialog.dismiss()
            showDataPermissionDialog()
            warningNotification.alpha = 1F
        }
    }

    private fun showDataPermissionDialog() {
        UIElements.constraintLayoutObjectAnimator(warningNotification, "translationY",
                0f, 500,
                200, DecelerateInterpolator(3f))

        dataWarningDialog.show()
        dataUnderstandButton.setOnClickListener {
            UIElements.constraintLayoutObjectAnimator(warningNotification, "translationY",
                    (-90 * resources.displayMetrics.density), 500,
                    0, DecelerateInterpolator(3f))
            Values.firstStart = false
            dataWarningDialog.dismiss()

            Handler().postDelayed({
                startActivity(Intent(this, ConnectingActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            }, 700)
        }
    }
}
