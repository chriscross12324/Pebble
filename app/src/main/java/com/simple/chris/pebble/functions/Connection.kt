package com.simple.chris.pebble.functions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.fragment.app.FragmentManager
import com.google.firebase.firestore.Query
import com.simple.chris.pebble.R
import com.simple.chris.pebble.activities.ActivityMain
import com.simple.chris.pebble.data.GradientObject
import com.simple.chris.pebble.dialogs.DialogPopup
import java.util.*

object Connection {

    private lateinit var fm: FragmentManager

    fun isOnline(context: Context) : Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCompatibilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return networkCompatibilities != null
                && networkCompatibilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun checkConnection(context: Context, activity: Activity) {
        if (isOnline(context)) {
            getGradientsFireStore(activity)
        } else {
            Values.dialogPopup = DialogPopup.newDialog(
                arrayNoConnection(), "noConnection", R.drawable.icon_warning, R.string.dual_no_connection,
                null, R.string.sentence_needs_internet_connection, null)
            Values.dialogPopup.show(fm, "noConnection")
        }
    }

    @SuppressLint("StringFormatMatches")
    fun getGradientsFireStore(activity: Activity) {
        fm = (activity as ActivityMain).supportFragmentManager
        Values.dialogPopup = DialogPopup.newDialog(null, "connecting", null, R.string.word_connecting,
                null, R.string.sentence_pebble_is_connecting, null)
        Values.dialogPopup.show(fm, "connecting")

        Values.downloadingGradients = true
        Values.getFireStore().collection("gradientList")
                .orderBy("gradientTimestamp", Query.Direction.DESCENDING)
                .limit(20)
                .get()
                .addOnSuccessListener { it ->
                    Values.gradientList = emptyList()
                    val gradientList = mutableListOf<GradientObject>()
                    for (document in it) {
                        val gradientItem = GradientObject(
                            document.data["gradientName"] as String,
                            document.data["gradientDescription"] as String,
                            (document.data["gradientColours"] as String).replace("[", "").replace("]", "").split(",").map { it.trim() }
                        )

                        gradientList.add(gradientItem)

                    }
                    Values.gradientList = gradientList
                    Log.d("DEBUG", "Firestore: Done")
                    Values.downloadingGradients = false
                }
                .addOnFailureListener {
                    Log.e("INFO", "Firebase failure: $it")
                    Values.downloadingGradients = false
                    Values.dialogPopup = DialogPopup.newDialog(
                        arrayNoConnection(), "serverError", R.drawable.icon_wifi_empty, R.string.dual_server_error,
                            null, null, activity.getString(R.string.sentence_server_error, it.localizedMessage))
                    Values.dialogPopup.show(fm, "serverError")
                }
    }
}