package com.simple.chris.pebble

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.main_menu_buttons.view.*

class PopupDialogButtonRecyclerAdapter internal constructor(var context: Context, var popupName: String, private val buttons: ArrayList<HashMap<String, Int>>, onButtonListener: OnButtonListener): RecyclerView.Adapter<PopupDialogButtonRecyclerAdapter.ViewHolder>() {
    private var mOnButtonListener = onButtonListener
    private var layoutInflater = LayoutInflater.from(context)

    /**
     * @return Populated module to display in RecyclerView
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.popup_dialog_buttons, parent, false), mOnButtonListener)
    }

    override fun getItemCount(): Int {
        return buttons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val details: HashMap<String, Int> = buttons[position]
            holder.buttonTitle.text = context.getString(details["buttonTitle"] as Int)
            holder.buttonIcon.setImageResource(details["buttonIcon"]!!.toInt())
        } catch (e: Exception) {
            //context.startActivity(Intent(context, Feedback::class.java))
            Log.e("ERR", "pebble.main_menu_recycler_view_adapter.on_bind_view_holder: ${e.localizedMessage}")
        }
    }

    inner class ViewHolder internal constructor(view: View, onButtonListener: OnButtonListener): RecyclerView.ViewHolder(view), View.OnClickListener {
        var buttonIcon: ImageView = view.buttonIcon
        var buttonTitle: TextView = view.buttonTitle
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