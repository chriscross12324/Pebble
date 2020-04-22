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


class GradientRecyclerViewAdapter internal constructor(var context: Context, private val gradients: ArrayList<HashMap<String, String>>, onGradientListener: OnGradientListener, onGradientLongClickListener: OnGradientLongClickListener) : RecyclerView.Adapter<GradientRecyclerViewAdapter.ViewHolder>() {
    private var mOnGradientListener: OnGradientListener = onGradientListener
    private var mOnGradientLongClickListener: OnGradientLongClickListener = onGradientLongClickListener
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.module_browse_normal, parent, false), mOnGradientListener, mOnGradientLongClickListener)
    }

    override fun getItemCount(): Int {
        return gradients.size
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val details: HashMap<String, String> = gradients[position]
            holder.gradientName.text = details["backgroundName"]
            val startColour = Color.parseColor(details["startColour"])
            val endColour = Color.parseColor(details["endColour"])
            UIElements.gradientDrawable(context, true, holder.gradientView, startColour, endColour, 20f)
        } catch (e: Exception) {
            holder.gradientView.setBackgroundColor(context.resources.getColor(R.color.pebbleEnd))
            holder.gradientName.text = "[Broken Data]"
        }
    }

    inner class ViewHolder internal constructor(view: View, onGradientListener: OnGradientListener, onGradientLongClickListener: OnGradientLongClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener {
        var gradientName: TextView = view.gradientName
        var gradientView: ImageView = view.gradient
        private val myOnGradientListener = onGradientListener
        private val myOnGradientLongClickListener = onGradientLongClickListener

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(p0: View?) {
            myOnGradientListener.onGradientClick(adapterPosition, p0 as View)
        }

        override fun onLongClick(v: View?): Boolean {
            myOnGradientLongClickListener.onGradientLongClick(adapterPosition, v as View)
            return true
        }

    }

    interface OnGradientListener {
        fun onGradientClick(position: Int, view: View)
    }

    interface OnGradientLongClickListener {
        fun onGradientLongClick(position: Int, view: View)
    }
}
