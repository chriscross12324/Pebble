package com.simple.chris.pebble.recyclers

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simple.chris.pebble.R
import com.simple.chris.pebble.functions.ButtonItem
import com.simple.chris.pebble.functions.Vibration

class PopupDialogButtonRecycler internal constructor(var context: Context, var popupName: String, private val buttons: List<ButtonItem>, onButtonListener: OnButtonListener): RecyclerView.Adapter<PopupDialogButtonRecycler.ViewHolder>() {
    private var mOnButtonListener = onButtonListener
    private var layoutInflater = LayoutInflater.from(context)

    /**
     * @return Populated module to display in RecyclerView
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.dialog_buttons, parent, false), mOnButtonListener)
    }

    override fun getItemCount(): Int {
        return buttons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val details: ButtonItem = buttons[position]
            holder.buttonTitle.text = context.getString(details.buttonText)
            holder.buttonIcon.setImageResource(details.buttonIcon)
        } catch (e: Exception) {
            Log.e("ERR", "pebble.popup_dialog_button_recycler.on_bind_view_holder: ${e.localizedMessage}")
        }
    }

    inner class ViewHolder internal constructor(view: View, onButtonListener: OnButtonListener): RecyclerView.ViewHolder(view), View.OnClickListener {
        var buttonIcon: ImageView = view.findViewById(R.id.buttonIcon)
        var buttonTitle: TextView = view.findViewById(R.id.buttonTitle)
        private val myOnButtonListener = onButtonListener

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            myOnButtonListener.onButtonClickPopup(popupName, adapterPosition, v as View)
            Vibration.lowFeedback(context)
        }
    }

    interface OnButtonListener {
        fun onButtonClickPopup(popupName: String, position: Int, view: View)
    }

}