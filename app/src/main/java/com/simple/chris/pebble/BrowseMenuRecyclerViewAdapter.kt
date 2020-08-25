package com.simple.chris.pebble

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.button_menu_options.view.*
import kotlinx.android.synthetic.main.button_search_colour.view.*

class BrowseMenuRecyclerViewAdapter internal constructor(var context: Context, private val buttons: ArrayList<HashMap<String, Int>>, onButtonListener: OnButtonListener): RecyclerView.Adapter<BrowseMenuRecyclerViewAdapter.ViewHolder>() {
    private var mOnButtonListener = onButtonListener
    private var layoutInflater = LayoutInflater.from(context)

    /**
     * @return Populated module to display in RecyclerView
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.button_menu_options, parent, false), mOnButtonListener)
    }

    override fun getItemCount(): Int {
        return buttons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val details: HashMap<String, Int> = buttons[position]
            //UIElement.gradientDrawable(context, holder.buttonBackground, Color.parseColor(details["buttonColour"]), Color.parseColor(details["buttonColour"]), 20f)
            holder.buttonImage.setImageResource(details["buttonIcon"]!!.toInt())
            holder.buttonText.text = context.getString(details["buttonText"] as Int)
            if (position+1 == buttons.size) {
                holder.listDivider.alpha = 0f
                Log.e("INFO", "$position")
            } else {
                Log.e("INFO", "Not Removed $position:${buttons.size}")
            }
        } catch (e: Exception) {
            Log.e("ERR", "pebble.search_colour_recycler_view_adapter.on_bind_view_holder: ${e.localizedMessage}")
        }
    }

    inner class ViewHolder internal constructor(view: View, onButtonListener: OnButtonListener): RecyclerView.ViewHolder(view), View.OnClickListener {
        var buttonImage: ImageView = view.buttonIcon
        var buttonText: TextView = view.buttonText
        var listDivider: ImageView = view.listDivider
        private val myOnButtonListener = onButtonListener

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            myOnButtonListener.onButtonClick(adapterPosition, v as View)
            Vibration.mediumFeedback(context)
        }
    }

    interface OnButtonListener {
        fun onButtonClick(position: Int, view: View)
    }

}