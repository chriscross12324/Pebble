package com.simple.chris.pebble.activities

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.simple.chris.pebble.*
import kotlinx.android.synthetic.main.activity_permissions.*
import com.simple.chris.pebble.functions.Calculations.convertToDP
import com.simple.chris.pebble.functions.UIElements.viewObjectAnimator
import com.simple.chris.pebble.functions.UIElement
import com.simple.chris.pebble.functions.UIElements
import com.simple.chris.pebble.functions.Values

class Permissions : AppCompatActivity() {

    private var noticeShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElement.setTheme(this)
        setContentView(R.layout.activity_permissions)

        background.post {
            showNetworkDialog()
        }
    }

    private fun showNetworkDialog() {
        val networkDialog = Dialog(this, R.style.dialogStyle)
        networkDialog.setCancelable(false)
        networkDialog.setContentView(R.layout.dialog_network_permission)

        val networkMain = networkDialog.findViewById<ConstraintLayout>(R.id.networkMain)
        val networkNotice = networkDialog.findViewById<ConstraintLayout>(R.id.networkNotice)
        val networkUnderstand = networkDialog.findViewById<ConstraintLayout>(R.id.networkUnderstand)
        val networkUnderstandText = networkDialog.findViewById<TextView>(R.id.networkUnderstandText)

        networkMain.post {
            networkUnderstand.translationY = networkUnderstand.height + convertToDP(this, 24f)
            networkMain.translationY = networkMain.height + networkNotice.height + networkUnderstand.height + convertToDP(this, 56f)

            UIElements.viewVisibility(networkMain, View.VISIBLE, 100)
            viewObjectAnimator(networkMain, "translationY", 0f + networkNotice.height + convertToDP(this, 16f), 700, 100, DecelerateInterpolator(3f))
            viewObjectAnimator(networkUnderstand, "translationY", 0f, 700, 500, DecelerateInterpolator(3f))
        }

        networkUnderstand.setOnClickListener {
            if (!noticeShown) {
                viewObjectAnimator(networkMain, "translationY", 0f, 700, 0, DecelerateInterpolator(3f))
                UIElements.viewVisibility(networkNotice, View.VISIBLE, 0)
                noticeShown = true
                networkUnderstandText.setText(R.string.dual_i_understand)
            } else {
                Values.firstStart = false
                startActivity(Intent(this, Browse::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                networkDialog.dismiss()
                finish()
            }
        }

        val networkWindow = networkDialog.window
        networkWindow!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        networkWindow.setDimAmount(0f)
        networkWindow.setGravity(Gravity.BOTTOM)

        networkDialog.show()
    }
}
