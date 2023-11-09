package com.simple.chris.pebble.recyclers

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.simple.chris.pebble.R
import com.simple.chris.pebble.functions.ColourButtonItem
import com.simple.chris.pebble.functions.UIElement

class SearchColourRecyclerView internal constructor(
    var context: Context,
    private val buttons: List<ColourButtonItem>?,
    private val buttonsArray: ArrayList<String>?,
    onButtonListener: OnButtonListener
) : RecyclerView.Adapter<SearchColourRecyclerView.ViewHolder>() {
    private var mOnButtonListener = onButtonListener
    private var layoutInflater = LayoutInflater.from(context)

    /**
     * @return Populated module to display in RecyclerView
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            layoutInflater.inflate(R.layout.button_search_colour, parent, false),
            mOnButtonListener
        )
    }

    override fun getItemCount(): Int {
        if (buttons != null) {
            return buttons.size
        } else if (buttonsArray != null) {
            return buttonsArray.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            if (buttons != null) {
                val details: ColourButtonItem = buttons[position]
                //Log.e("COLOUR", "${details["buttonColour"]}")
                UIElement.gradientDrawable(
                    context,
                    holder.buttonBackground,
                    Color.parseColor(details.buttonHex),
                    Color.parseColor(details.buttonHex),
                    20f
                )
            } else if (buttonsArray != null) {
                val details: String = buttonsArray[position]
                UIElement.gradientDrawable(
                    context,
                    holder.buttonBackground,
                    Color.parseColor(details),
                    Color.parseColor(details),
                    20f
                )
            }

        } catch (e: Exception) {
            Log.e(
                "ERR",
                "pebble.search_colour_recycler_view.on_bind_view_holder: ${e.localizedMessage}"
            )
        }
    }

    inner class ViewHolder internal constructor(view: View, onButtonListener: OnButtonListener) :
        RecyclerView.ViewHolder(view), View.OnClickListener {
        var button: ConstraintLayout = view.findViewById(R.id.colourButton)
        var buttonBackground: ConstraintLayout = view.findViewById(R.id.buttonBackground)
        private val myOnButtonListener = onButtonListener

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (buttons != null) {
                myOnButtonListener.onButtonClick(
                    adapterPosition,
                    v as View,
                    buttons[adapterPosition].buttonColour
                )
            } else if (buttonsArray != null) {
                myOnButtonListener.onButtonClick(
                    adapterPosition,
                    v as View,
                    buttonsArray[adapterPosition]
                )
            }

        }
    }

    interface OnButtonListener {
        fun onButtonClick(position: Int, view: View, buttonColour: String)
    }

}