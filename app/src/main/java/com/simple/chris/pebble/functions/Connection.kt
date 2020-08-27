package com.simple.chris.pebble.functions

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.simple.chris.pebble.R
import com.simple.chris.pebble.adapters_helpers.PopupDialogButtonRecycler
import java.util.*

object Connection {

    lateinit var request: JsonObjectRequest

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return Objects.requireNonNull(connectivityManager).activeNetwork != null
    }

    fun isNetworkTypeData(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = Objects.requireNonNull(connectivityManager).activeNetworkInfo as NetworkInfo
        return Objects.requireNonNull(networkInfo).type == ConnectivityManager.TYPE_MOBILE
    }

    fun getConnectionType(context: Context) : Int {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager?.run {
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
                    if (hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return 1
                    } else if (hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return 2
                    }
                }
            }
        }
        return 0
    }

    fun checkConnection(context: Context, decorView: View, listener: PopupDialogButtonRecycler.OnButtonListener) {
        when (getConnectionType(context)) {
            0 -> {
                UIElement.popupDialog(context, "noConnection", R.drawable.icon_warning, R.string.dialog_title_eng_no_connection, null,
                        R.string.dialog_body_eng_no_connection, HashMaps.noConnectionArrayList(), decorView, listener)
            }
            1 -> {
                getGradients(context, decorView, listener)
            }
            2 -> {
                if (Values.askMobileData) {
                    UIElement.popupDialog(context, "askMobile", R.drawable.icon_warning, R.string.dialog_title_eng_data_warning, null,
                            R.string.dialog_body_eng_data_warning, HashMaps.dataWarningArrayList(), decorView, listener)
                } else {
                    getGradients(context, decorView, listener)
                }
            }
        }
    }

    fun getGradients(context: Context, decorView: View, listener: PopupDialogButtonRecycler.OnButtonListener) {
        /** Start connecting animation **/
        UIElement.popupDialog(context, "connecting", null, R.string.dialog_title_eng_connecting, null, R.string.dialog_body_eng_connecting, null, decorView, null)

        /** Start gradient database download **/
        Values.downloadingGradients = true
        val mQueue: RequestQueue = Volley.newRequestQueue(context)
        val gradientDatabaseURL = "https://script.google.com/macros/s/AKfycbwFkoSBTbmeB6l9iIiZWGczp9sDEjqX0jiYeglczbLKFAXsmtB1/exec?action=getGradients"

        request = JsonObjectRequest(Request.Method.GET, gradientDatabaseURL, null,
                { response ->
                    try {
                        val gradientArray = response.getJSONArray("items")
                        val gradientList = ArrayList<HashMap<String, String>>()

                        for (i in gradientArray.length() - 1 downTo 0) {
                            val downloadedItem = gradientArray.getJSONObject(i)

                            val item = HashMap<String, String>()
                            item["gradientName"] = downloadedItem.getString("gradientName")
                            item["startColour"] = downloadedItem.getString("startColour")
                            item["endColour"] = downloadedItem.getString("endColour")
                            item["description"] = downloadedItem.getString("description")

                            gradientList.add(item)
                            Values.gradientList = gradientList
                        }
                        connectionOnline()
                    } catch (e: Exception) {
                        Log.e("ERR", "pebble.main_menu.get_gradients: ${e.localizedMessage}")
                    }
                },
                {
                    Log.e("ERR", "pebble.main_menu.get_gradients.request.error_listener: ${it.networkResponse}")
                })
        mQueue.add(request)
        request.retryPolicy = DefaultRetryPolicy(20000, 5, 1.25f)

        checkDownload(context, decorView, listener)
    }

    fun checkDownload(context: Context, decorView: View, listener: PopupDialogButtonRecycler.OnButtonListener) {
        Handler().postDelayed({
            if (Values.gradientList.isEmpty()) {
                UIElement.popupDialog(context, "stillConnecting", R.drawable.icon_wifi_green, R.string.dialog_title_eng_still_connecting, null, R.string.dialog_body_eng_still_connecting, HashMaps.arrayContinueOfflineRetry(), decorView, listener)
            }
        }, 20000)
    }

    fun cancelConnection() {
        request.cancel()
        UIElement.popupDialogHider()
    }

    private fun connectionOnline() {
        Values.downloadingGradients = false
        UIElement.popupDialogHider()
    }

    fun connectionOffline(context: Activity) {
        Values.downloadingGradients = false
        UIElement.popupDialogHider()
        context.findViewById<TextView>(R.id.screenTitle).setText(R.string.dialog_title_eng_offline_mode)
        context.findViewById<TextView>(R.id.resultsText).setText(R.string.dialog_body_eng_offline_mode_desc)
    }
}