package com.simple.chris.pebble.adapters_helpers

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.simple.chris.pebble.R
import com.simple.chris.pebble.functions.UIElement
import kotlinx.android.synthetic.main.button_search_colour.view.*

class SearchColourRecyclerView internal constructor(var context: Context, private val buttons: ArrayList<HashMap<String, String>>, onButtonListener: OnButtonListener): RecyclerView.Adapter<SearchColourRecyclerView.ViewHolder>() {
    private var mOnButtonListener = onButtonListener
    private var layoutInflater = LayoutInflater.from(context)

    /**
     * @return Populated module to display in RecyclerView
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.button_search_colour, parent, false), mOnButtonListener)
    }

    override fun getItemCount(): Int {
        return buttons.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val details: HashMap<String, String> = buttons[position]
            UIElement.gradientDrawable(context, holder.buttonBackground, Color.parseColor(details["buttonColour"]), Color.parseColor(details["buttonColour"]), 20f)
        } catch (e: Exception) {
            Log.e("ERR", "pebble.search_colour_recycler_view.on_bind_view_holder: ${e.localizedMessage}")
        }
    }

    inner class ViewHolder internal constructor(view: View, onButtonListener: OnButtonListener): RecyclerView.ViewHolder(view), View.OnClickListener {
        var button: ConstraintLayout = view.colourButton
        var buttonBackground: ConstraintLayout = view.buttonBackground
        private val myOnButtonListener = onButtonListener

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            myOnButtonListener.onButtonClick(adapterPosition, v as View, buttons[adapterPosition]["buttonColour"] as String)
        }
    }

    interface OnButtonListener {
        fun onButtonClick(position: Int, view: View, buttonColour: String)
    }

}