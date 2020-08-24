package com.simple.chris.pebble

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main_menu.*
import java.util.*

object Connection {

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

    fun getGradients(context: Context, decorView: View) {
        /** Start connecting animation **/
        UIElement.popupDialog(context, "connecting", null, R.string.dialog_title_eng_connecting, null, R.string.dialog_body_eng_connecting, null, decorView, null)

        /** Start gradient database download **/
        Values.downloadingGradients = true
        val mQueue: RequestQueue = Volley.newRequestQueue(context)
        val gradientDatabaseURL = "https://script.google.com/macros/s/AKfycbwFkoSBTbmeB6l9iIiZWGczp9sDEjqX0jiYeglczbLKFAXsmtB1/exec?action=getGradients"

        val request = JsonObjectRequest(Request.Method.GET, gradientDatabaseURL, null,
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
    }

    private fun connectionOnline() {
        Values.downloadingGradients = false
        UIElement.popupDialogHider()
    }
}