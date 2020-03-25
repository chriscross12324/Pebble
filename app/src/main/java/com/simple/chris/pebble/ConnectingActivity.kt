package com.simple.chris.pebble

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ConnectingActivity : AppCompatActivity() {

    private lateinit var mQueue: RequestQueue

    private lateinit var connectingDialog: ConstraintLayout
    private lateinit var notification: ConstraintLayout

    private lateinit var noConnectionDialog: Dialog
    private lateinit var dataWarningDialog: Dialog

    private lateinit var retry: Button
    private lateinit var tryWifi: Button
    private lateinit var useButton: Button
    private lateinit var openSystemSettings: Button
    private lateinit var dontAskAgain: Button

    private lateinit var notificationText: TextView
    private lateinit var connectionStatusText: TextView
    private lateinit var connectionDialogBody: TextView

    private lateinit var background: ImageView
    private lateinit var animationView: ImageView


    private var oneTime = false
    var downloaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UIElements.setTheme(this)
        Values.saveValues(this)
        setContentView(R.layout.activity_connecting)

        mQueue = Volley.newRequestQueue(this)

        //ConstraintLayout
        connectingDialog = findViewById(R.id.connectingDialog)
        notification = findViewById(R.id.notification)

        //Dialog
        noConnectionDialog = Dialog(this)
        dataWarningDialog = Dialog(this)

        //TextView
        notificationText = findViewById(R.id.notificationText)
        connectionStatusText = findViewById(R.id.connectionStatusText)
        connectionDialogBody = findViewById(R.id.connectingDialogBody)

        //ImageView
        background = findViewById(R.id.background)
        animationView = findViewById(R.id.animationView)


        //Random Connecting Body
        val randomConnectingBody = this.resources.getStringArray(R.array.connecting_array)
        connectionDialogBody.text = randomConnectingBody[Random().nextInt(randomConnectingBody.size)]

        notification.translationY = Calculations.convertToDP(this, -74f)

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
        /*if (isInternetConnected()) {
            if (isNetworkTypeData()) {
                if (Values.askMobileData) {
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
            getItems()
            showNoConnectionDialog()
        }*/
        getItems()
        playConnectionDialog()
    }

    private fun itemsGrabbed() {
        val handler = Handler()
        handler.postDelayed(object:Runnable {
            override fun run() {
                if (downloaded) {
                    startActivity(Intent(this@ConnectingActivity, ActivityBrowse::class.java))
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                } else {
                    handler.postDelayed(this, 100)
                }
            }

        }, 100)
    }

    private fun getItems() {
        val gradientDatabaseURL = "https://script.google.com/macros/s/AKfycbwFkoSBTbmeB6l9iIiZWGczp9sDEjqX0jiYeglczbLKFAXsmtB1/exec?action=getItems"

        val request = JsonObjectRequest(Request.Method.GET, gradientDatabaseURL, null,
                Response.Listener { response ->
                    try {
                        connectionStatusText.text = "Status: Downloading Gradients"
                        val gradientArray = response.getJSONArray("items")
                        val gradientList = ArrayList<HashMap<String, String>>()

                        for (i in 0 until gradientArray.length()) {
                            val downloadedItem = gradientArray.getJSONObject(i)

                            val item = HashMap<String, String>()
                            item["backgroundName"] = downloadedItem.getString("backgroundName")
                            item["startColour"] = downloadedItem.getString("leftColour")
                            item["endColour"] = downloadedItem.getString("rightColour")
                            item["description"] = downloadedItem.getString("description")

                            gradientList.add(item)
                            Values.browse = gradientList
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
        UIElements.constraintLayoutObjectAnimator(connectingDialog, "alpha", 1f, 300, 0, LinearInterpolator())

        animationView.setBackgroundResource(R.drawable.animation_loading)
        val animationDrawable = animationView.background as AnimationDrawable
        animationDrawable.start()

        Handler().postDelayed({
            UIElements.textViewTextChanger(notificationText, "It's taking a bit longer\nthan usual to connect", 0)
            playNotificationAnimation(0)
            UIElements.textViewTextChanger(notificationText, "Attempting to\nrepair issues", 6000)
            playNotificationAnimation(6000)
        }, 10000)
        Handler().postDelayed({
            startActivity(Intent(this, ConnectingActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 20000)
    }

    private fun showDataWarningDialog() {

    }

    private fun showNoConnectionDialog() {

    }

    private fun playNotificationAnimation(delay: Long) {
        Handler().postDelayed({
            notification.alpha = 1f
            Vibration.notification(this)
            UIElements.constraintLayoutObjectAnimator(notification, "translationY",
            0f, 500, 0, DecelerateInterpolator(3f))
            UIElements.constraintLayoutObjectAnimator(notification, "translationY",
            Calculations.convertToDP(this, -(notification.height).toFloat()), 500, 3000, DecelerateInterpolator(3f))
            UIElements.constraintLayoutObjectAnimator(notification, "alpha",
            0f, 0, 3500, LinearInterpolator())
        }, delay)
    }

    /*private fun isInternetConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetwork != null
    }

    private fun isNetworkTypeData(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetwork as NetworkInfo
        return networkInfo.type == ConnectivityManager.TYPE_MOBILE
    }*/
}
