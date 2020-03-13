package com.simple.chris.pebble

import android.app.Dialog
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.media.Image
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import java.util.*
import kotlin.collections.ArrayList

/*
class ConnectingActivity : AppCompatActivity{

    lateinit var mQueue: RequestQueue

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


    var oneTime = false
    var connectedMain = false

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
        if (isInternetConnected()) {
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
        }
    }

    private fun itemsGrabbed() {
        Handler().postDelayed({
            if (connectedMain) {
                val browse = Intent(this, ActivityBrowse::class.java)
                startActivity(browse)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            } else {
                itemsGrabbed()
            }
        }, 100)
    }

    private fun getItems() {
        val gradientDatabaseURL = "https://script.google.com/macros/s/AKfycbwFkoSBTbmeB6l9iIiZWGczp9sDEjqX0jiYeglczbLKFAXsmtB1/exec?action=getItems"

        */
/*var request = JsonObjectRequest(Request.Method.GET, url, null,
                { response-> try
                {
                    connectionStatusText.setText("Status: Downloading All")
                    val mainArray = response.getJSONArray("items")
                    val list = ArrayList()
                    for (i in mainArray.length() - 1 downTo 0)
                    {
                        val items = mainArray.getJSONObject(i)
                        val backgroundName = items.getString("backgroundName")
                        val startColour = items.getString("leftColour")
                        val endColour = items.getString("rightColour")
                        val description = items.getString("description")
                        val item = HashMap()
                        item.put("backgroundName", backgroundName)
                        item.put("startColour", startColour)
                        item.put("endColour", endColour)
                        item.put("description", description)
                        list.add(item)
                        Values.browse(list)
                    }
                    connectedMain = true
                }
                catch (e: JSONException) {
                    e.printStackTrace()
                }
                }, ???({ printStackTrace() }))*//*

    }

    private fun playConnectionDialog() {

    }

    private fun showDataWarningDialog() {

    }

    private fun showNoConnectionDialog() {

    }

    private fun isInternetConnected() : Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetwork != null
    }

    private fun isNetworkTypeData() : Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetwork as NetworkInfo
        return networkInfo.type == ConnectivityManager.TYPE_MOBILE
    }
}*/
