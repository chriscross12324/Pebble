package com.simple.chris.pebble.functions

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.simple.chris.pebble.R
import com.simple.chris.pebble.activities.MainActivity
import com.simple.chris.pebble.adapters_helpers.DialogPopup
import java.util.*

object Connection {

    lateinit var request: JsonObjectRequest
    private lateinit var fm: FragmentManager

    fun getConnectionType(context: Context) : Int {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager?.run {
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                    when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            return 1
                        }
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            return 2
                        }
                        hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                            return 1
                        }
                        else -> 0
                    }
                }
            }
        }
        return 0
    }

    fun checkConnection(context: Context, activity: Activity) {
        when (getConnectionType(context)) {
            0 -> {
                Values.dialogPopup = DialogPopup.newDialog(HashMaps.noConnectionArrayList(), "noConnection", R.drawable.icon_warning, R.string.dual_no_connection,
                        null, R.string.sentence_needs_internet_connection)
                Values.dialogPopup.show(fm, "noConnection")
            }
            1 -> {
                getGradients(context, activity)
            }
            2 -> {
                when (Values.useMobileData) {
                    "on" -> {
                        getGradients(context, activity)
                    }
                    "off" -> {
                        Toast.makeText(context, "Enable Data Usage in Settings", Toast.LENGTH_LONG).show()
                    }
                    "ask" -> {
                        Values.dialogPopup = DialogPopup.newDialog(HashMaps.dataWarningArrayList(), "askMobile", R.drawable.icon_warning, R.string.sentence_trying_mobile_data,
                                null, R.string.question_mobile_data)
                        Values.dialogPopup.show(fm, "askMobile")
                    }
                }
            }
        }
    }

    fun getGradients(context: Context, activity: Activity) {
        /** Start connecting animation **/
        //UIElement.popupDialog(context, "connecting", null, R.string.word_connecting, null, R.string.sentence_pebble_is_connecting, null, decorView, null)
        fm = (activity as MainActivity).supportFragmentManager
        Values.dialogPopup = DialogPopup.newDialog(null, "connecting", null, R.string.word_connecting,
                null, R.string.sentence_pebble_is_connecting)
        Values.dialogPopup.show(fm, "connecting")

        /** Start gradient database download **/
        Values.downloadingGradients = true
        val mQueue: RequestQueue = Volley.newRequestQueue(context)
        val gradientDatabaseURL = "https://script.google.com/macros/s/AKfycbwFkoSBTbmeB6l9iIiZWGczp9sDEjqX0jiYeglczbLKFAXsmtB1/exec?action=getGradientsV3"
        //SQLiteHelperFull(context).clearGradients()

        request = JsonObjectRequest(Request.Method.GET, gradientDatabaseURL, null,
                { response ->
                    try {
                        val gradientArray = response.getJSONArray("items")
                        val gradientList = ArrayList<HashMap<String, String>>()

                        for (i in gradientArray.length() - 1 downTo 0) {
                            val downloadedItem = gradientArray.getJSONObject(i)

                            val item = HashMap<String, String>()
                            item["gradientName"] = downloadedItem.getString("gradientName")
                            item["gradientColours"] = downloadedItem.getString("gradientColours")
                            item["gradientDescription"] = downloadedItem.getString("gradientDescription")

                            gradientList.add(item)
                            Values.gradientList = gradientList
                            /** Insert Gradient into "My Gradients" database **/
                            //val db = SQLiteHelperFull(context)
                            //db.insertGradient(item["gradientName"]!!, item["gradientColours"]!!, item["gradientDescription"]!!)
                        }
                        connectionOnline()
                        Values.connectionOffline = false
                    } catch (e: Exception) {
                        Log.e("ERR", "pebble.main_menu.get_gradients: ${e.localizedMessage}")
                    }
                },
                {
                    Log.e("ERR", "pebble.main_menu.get_gradients.request.error_listener: ${it.networkResponse}")
                })
        mQueue.add(request)
        request.retryPolicy = DefaultRetryPolicy(20000, 5, 1.25f)

        checkDownload(context)
    }

    fun checkDownload(context: Context) {
        Handler(Looper.getMainLooper()).postDelayed({
            if (Values.gradientList.isEmpty()) {
                //TODO("Add stillConnecting Dialog")
                //UIElement.popupDialog(context, "stillConnecting", R.drawable.icon_wifi_full, R.string.dual_still_connecting, null, R.string.question_still_connecting, HashMaps.arrayContinueOfflineRetry(), decorView, listener)
            }
        }, 20000)
    }

    fun cancelConnection() {
        request.cancel()
        //UIElement.popupDialogHider()
        Values.connectionOffline = true
    }

    private fun connectionOnline() {
        Values.downloadingGradients = false
    }

    fun connectionOffline(context: Activity) {
        Values.downloadingGradients = false
        Values.connectionOffline = true
        //UIElement.popupDialogHider()
        context.findViewById<TextView>(R.id.screenTitle).setText(R.string.word_offline)
        context.findViewById<TextView>(R.id.resultsText).setText(R.string.sentence_online_for_gradients)

        /*if (SQLiteHelperFull(context).readGradients().isNotEmpty()) {
            Values.gradientList = SQLiteHelperFull(context).readGradients()
            connectionOnline()
        }*/
    }
}