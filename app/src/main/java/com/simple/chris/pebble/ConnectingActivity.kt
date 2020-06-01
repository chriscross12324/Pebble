package com.simple.chris.pebble

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.simple.chris.pebble.Values.askMobileData
import kotlinx.android.synthetic.main.activity_connecting.*
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ConnectingActivity : AppCompatActivity() {

    private lateinit var mQueue: RequestQueue

    private lateinit var noConnectionDialog: Dialog
    private lateinit var dataWarningDialog: Dialog

    private lateinit var retry: Button
    private lateinit var tryWifi: Button
    private lateinit var useButton: Button
    private lateinit var openSystemSettings: Button
    private lateinit var dontAskAgain: Button

    private var oneTime = false
    var downloaded = false

    private val checkDownload = Handler()
    private val connectionNotificationHandler = Handler()
    private val connectionRetryHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        Values.saveValues(this)
        setContentView(R.layout.activity_connecting)

        mQueue = Volley.newRequestQueue(this)

        //Dialog
        noConnectionDialog = Dialog(this)
        dataWarningDialog = Dialog(this)

        //Random Connecting Body
        val randomConnectingBody = this.resources.getStringArray(R.array.connecting_array)
        connectingDialogBody.text = randomConnectingBody[Random().nextInt(randomConnectingBody.size)]

        notification.post {
            notification.translationY = Calculations.convertToDP(this, -(notification.height).toFloat())
        }

        animationView.setOnClickListener {
            when (Values.theme) {
                "light" -> Values.theme = "dark"
                "dark" -> Values.theme = "black"
                "black" -> Values.theme = "light"
            }
        }

        checkConnection()
        itemsGrabbed()
    }

    private fun checkConnection() {
        if (Connection.isNetworkAvailable(this)) {
            if (Connection.isNetworkTypeData(this)) {
                if (askMobileData) {
                    if (oneTime) {
                        getItems()
                        playConnectionDialog()
                    } else {
                        showDataWarningDialog()
                    }
                } else {
                    getItems()
                    playConnectionDialog()
                }
            } else {
                getItems()
                playConnectionDialog()
            }
        } else {
            showNoConnectionDialog()
        }
    }

    private fun itemsGrabbed() {
        checkDownload.postDelayed(object : Runnable {
            @SuppressLint("SyntheticAccessor")
            override fun run() {
                if (downloaded) {
                    connectionNotificationHandler.removeCallbacksAndMessages(null)
                    connectionRetryHandler.removeCallbacksAndMessages(null)
                    startActivity(Intent(this@ConnectingActivity, ActivityBrowse::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                } else {
                    checkDownload.postDelayed(this, 100)
                }
            }

        }, 100)
    }

    private fun getItems() {
        connectionStatusText.visibility = View.VISIBLE
        val gradientDatabaseURL = "https://script.google.com/macros/s/AKfycbwFkoSBTbmeB6l9iIiZWGczp9sDEjqX0jiYeglczbLKFAXsmtB1/exec?action=getGradients"

        val request = JsonObjectRequest(Request.Method.GET, gradientDatabaseURL, null,
                Response.Listener { response ->
                    try {
                        connectionStatusText.text = getString(R.string.status_downloading)
                        val gradientArray = response.getJSONArray("items")
                        val gradientList = ArrayList<HashMap<String, String>>()

                        AsyncTask.execute {
                            for (i in gradientArray.length() - 1 downTo 0) {
                                val downloadedItem = gradientArray.getJSONObject(i)

                                val item = HashMap<String, String>()
                                item["backgroundName"] = downloadedItem.getString("gradientName")
                                item["startColour"] = downloadedItem.getString("startColour")
                                item["endColour"] = downloadedItem.getString("endColour")
                                item["description"] = downloadedItem.getString("description")

                                gradientList.add(item)
                                Values.gradientList = gradientList
                            }
                        }

                        downloaded = true
                    } catch (e: Exception) {
                        Log.e("ERR", "pebble.connecting_activity.get_items: ${e.localizedMessage}")
                    }
                },
                Response.ErrorListener {
                    Log.e("ERR", "pebble.connecting_activity.get_items.request.error_listener: ${it.networkResponse}")
                })
        mQueue.add(request)
    }

    private fun playConnectionDialog() {
        UIElements.viewObjectAnimator(connectingDialog, "alpha", 1f, 300, 0, LinearInterpolator())

        animationView.setBackgroundResource(R.drawable.animation_loading)
        val animationDrawable = animationView.background as AnimationDrawable
        animationDrawable.start()

        connectionNotificationHandler.postDelayed({
            UIElements.textViewTextChanger(notificationText, "It's taking a bit longer\nthan usual to connect", 0)
            playNotificationAnimation(0)
            UIElements.textViewTextChanger(notificationText, "Attempting to\nrepair issues", 6000)
            playNotificationAnimation(6000)
        }, 10000)
        connectionRetryHandler.postDelayed({
            startActivity(Intent(this, ConnectingActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 20000)
    }

    @SuppressLint("NewApi")
    private fun showDataWarningDialog() {
        dataWarningDialog.setContentView(R.layout.dialog_data_warning)
        useButton = dataWarningDialog.findViewById(R.id.useButton)
        dontAskAgain = dataWarningDialog.findViewById(R.id.dontAskAgain)
        tryWifi = dataWarningDialog.findViewById(R.id.tryWifi)

        notificationText.text = getString(R.string.noWifi)
        playNotificationAnimation(500)

        val window = dataWarningDialog.window as Window
        val lp = Objects.requireNonNull(window.attributes)
        lp.dimAmount = 0f
        lp.gravity = Gravity.CENTER
        dataWarningDialog.window!!.attributes = lp

        if (Calculations.isAndroidPOrGreater()) {
            useButton.outlineSpotShadowColor = ContextCompat.getColor(this, R.color.pebbleEnd)
        }

        useButton.setOnClickListener {
            oneTime = true
            dataWarningDialog.dismiss()
            checkConnection()
        }

        dontAskAgain.setOnClickListener {
            askMobileData = false
            dataWarningDialog.dismiss()
            checkConnection()
        }

        tryWifi.setOnClickListener {
            dataWarningDialog.dismiss()
            checkConnection()
        }

        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dataWarningDialog.setCancelable(false)
        dataWarningDialog.show()
    }

    @SuppressLint("NewApi")
    private fun showNoConnectionDialog() {
        noConnectionDialog.setContentView(R.layout.dialog_no_connection)
        retry = noConnectionDialog.findViewById(R.id.retryButton)
        openSystemSettings = noConnectionDialog.findViewById(R.id.openSystemSettingsButton)

        val window = noConnectionDialog.window as Window
        val lp = Objects.requireNonNull(window.attributes)
        lp.dimAmount = 0f
        lp.gravity = Gravity.CENTER
        noConnectionDialog.window!!.attributes = lp

        if (Calculations.isAndroidPOrGreater()) {
            retry.outlineSpotShadowColor = ContextCompat.getColor(this, R.color.pebbleEnd)
        }

        retry.setOnClickListener {
            noConnectionDialog.dismiss()
            checkConnection()
        }

        openSystemSettings.setOnClickListener {
            startActivityForResult(Intent(Settings.ACTION_SETTINGS), 0)
        }

        window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        noConnectionDialog.setCancelable(false)
        noConnectionDialog.show()
    }

    private fun playNotificationAnimation(delay: Long) {
        Handler().postDelayed({
            notification.alpha = 1f
            Vibration.notification(this)
            UIElements.viewObjectAnimator(notification, "translationY",
                    0f, 500, 0, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(notification, "translationY",
                    Calculations.convertToDP(this, -(notification.height).toFloat()), 500, 3000, DecelerateInterpolator(3f))
            UIElements.viewObjectAnimator(notification, "alpha",
                    0f, 0, 3500, LinearInterpolator())
        }, delay)
    }

    override fun onPause() {
        super.onPause()
        checkDownload.removeCallbacksAndMessages(null)
        connectionNotificationHandler.removeCallbacksAndMessages(null)
        connectionRetryHandler.removeCallbacksAndMessages(null)
        finish()
    }
}
