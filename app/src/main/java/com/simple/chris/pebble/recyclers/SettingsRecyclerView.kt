package com.simple.chris.pebble.recyclers

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simple.chris.pebble.R
import com.simple.chris.pebble.functions.ButtonItem
import com.simple.chris.pebble.functions.vibrateMedium

class SettingsRecyclerView internal constructor(var context: Context, var screenName: String, private val buttons: List<ButtonItem>, onButtonListener: OnButtonListener): RecyclerView.Adapter<SettingsRecyclerView.ViewHolder>() {
    private var mOnButtonListener = onButtonListener
    private var layoutInflater = LayoutInflater.from(context)

    /**
     * @return Populated module to display in RecyclerView
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (screenName == "settings" || screenName == "about") {
            ViewHolder(layoutInflater.inflate(R.layout.button_settings_screen, parent, false), mOnButtonListener)
        } else {
            ViewHolder(layoutInflater.inflate(R.layout.button_about_screen, parent, false), mOnButtonListener)
        }
    }

    override fun getItemCount(): Int {
        return buttons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val details: ButtonItem = buttons[position]
            holder.buttonImage.setImageResource(details.buttonIcon)
            holder.buttonText.text = context.getString(details.buttonText)
            if (screenName == "donate" && position != 0) {
                holder.buttonImage.imageTintList = null
            }
        } catch (e: Exception) {
            Log.e("ERR", "pebble.browse_menu_recycler_view.on_bind_view_holder: ${e.localizedMessage}")
        }
    }

    inner class ViewHolder internal constructor(view: View, onButtonListener: OnButtonListener): RecyclerView.ViewHolder(view), View.OnClickListener {
        var buttonImage: ImageView = view.findViewById(R.id.optionIcon)
        var buttonText: TextView = view.findViewById(R.id.optionTitle)
        private val myOnButtonListener = onButtonListener

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            myOnButtonListener.onButtonClick(screenName, adapterPosition, v as View)
            vibrateMedium(context)
        }
    }

    interface OnButtonListener {
        fun onButtonClick(screenName: String, position: Int, view: View)
    }

}