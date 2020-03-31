package com.simple.chris.pebble

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
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
}