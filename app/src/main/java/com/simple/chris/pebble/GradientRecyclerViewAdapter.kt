package com.simple.chris.pebble

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.module_browse_normal.view.*

/*
class GradientRecyclerViewAdapter internal constructor(var context: Context, private val gradients: ArrayList<HashMap<String, String>>) : RecyclerView.Adapter<GradientRecyclerViewAdapter.ViewHolder>() {
    private var clickListener: ItemClickListener? = null
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.module_browse_normal, parent, false))
    }

    override fun getItemCount(): Int {
        return gradients.size
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val details: HashMap<String, String> = gradients[position]
        holder.gradientName.text = details["backgroundName"]
        val startColour = Color.parseColor(details["startColour"])
        val endColour = Color.parseColor(details["endColour"])

        val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(startColour, endColour)
        )
        gradientDrawable.cornerRadius = Calculations.convertToDP(context, 20f)
        holder.gradientView.background = gradientDrawable

        if (Calculations.isAndroidPOrGreater()) {
            holder.gradientView.outlineSpotShadowColor = endColour
        }
    }

    inner class ViewHolder internal constructor(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var gradientName: TextView = view.gradientName
        var gradientView: ImageView = view.gradient

        override fun onClick(p0: View) {
            if (clickListener != null) clickListener!!.onItemClick(p0, adapterPosition)
        }

        init {
            view.setOnClickListener(this)
        }
    }

    internal fun setClickListener(itemClickListener: ItemClickListener) {
        clickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}*/
