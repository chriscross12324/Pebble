package com.simple.chris.pebble

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception

object RecyclerGrid {

    fun gradientGrid(context: Context, view: RecyclerView, gradientJSON: ArrayList<HashMap<String, String>>, onGradientListener: GradientRecyclerViewAdapter.OnGradientListener, onGradientLongClickListener: GradientRecyclerViewAdapter.OnGradientLongClickListener) {
        try {
            val gridLayoutManager = GridLayoutManager(context, 2)
            val gridLayoutAdapter = GradientRecyclerViewAdapter(context, gradientJSON, onGradientListener, onGradientLongClickListener)
            view.setHasFixedSize(true)
            view.layoutManager = gridLayoutManager
            view.adapter = gridLayoutAdapter
        } catch (e: Exception) {
            Log.e("ERR", "pebble.recycler_grid.gradient_grid: ${e.localizedMessage}")

        }
    }

    fun gradientGridOnClickListener(context: Activity, gradientJSON: ArrayList<HashMap<String, String>>, view: View, position: Int) {
        try {
            val details = Intent(context, ActivityGradientDetails::class.java)
            val gradientInfo = gradientJSON[position]

            details.putExtra("gradientName", gradientInfo["backgroundName"])
            details.putExtra("startColour", gradientInfo["startColour"])
            details.putExtra("endColour", gradientInfo["endColour"])
            details.putExtra("description", gradientInfo["description"])

            val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(context, view.findViewById(R.id.gradient), gradientInfo["backgroundName"] as String)
            context.startActivity(details, activityOptions.toBundle())
        } catch (e: Exception) {
            Log.e("ERR", "pebble.recycler_grid.gradient_grid_on_click_listener: ${e.localizedMessage}")

        }
    }

    fun gradientGridOnLongClickListener(context: Activity, gradientJSON: ArrayList<HashMap<String, String>>, view: View, position: Int) {
        try {

        } catch (e: Exception){}
    }

}