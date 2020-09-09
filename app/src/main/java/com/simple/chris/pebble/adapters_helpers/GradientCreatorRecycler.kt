package com.simple.chris.pebble.adapters_helpers

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simple.chris.pebble.R
import com.simple.chris.pebble.functions.Calculations
import com.simple.chris.pebble.functions.Values
import com.simple.chris.pebble.functions.Vibration
import kotlinx.android.synthetic.main.activity_gradient_creator.*
import kotlinx.android.synthetic.main.button_colour_picker.view.*
import kotlinx.android.synthetic.main.button_menu_options.view.*
import kotlin.math.roundToInt

class GradientCreatorRecycler internal constructor(var context: Context, private val colours: ArrayList<String>, onButtonListener: OnButtonListener): RecyclerView.Adapter<GradientCreatorRecycler.ViewHolder>() {
    private var mOnButtonListener = onButtonListener
    private var layoutInflater = LayoutInflater.from(context)

    /**
     * @return Populated module to display in RecyclerView
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.button_colour_picker, parent, false), mOnButtonListener)
    }

    override fun getItemCount(): Int {
        return colours.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val colour = Color.parseColor(colours[position])

            val colourDrawable = GradientDrawable()
            colourDrawable.shape = GradientDrawable.OVAL
            colourDrawable.setStroke(Calculations.convertToDP(context, 5f).roundToInt(), colour)
            holder.colourPreview.background = colourDrawable
        } catch (e: Exception) {
            Log.e("ERR", "pebble.browse_menu_recycler_view.on_bind_view_holder: ${e.localizedMessage}")
        }
    }

    inner class ViewHolder internal constructor(view: View, onButtonListener: OnButtonListener): RecyclerView.ViewHolder(view), View.OnClickListener {
        var colourPreview: ImageView = view.colourPreview
        var colourButton: LinearLayout = view.colourButton
        private val myOnButtonListener = onButtonListener

        init {
            colourButton.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            myOnButtonListener.onButtonClick(adapterPosition, v as View)
            Vibration.lowFeedback(context)
        }
    }

    interface OnButtonListener {
        fun onButtonClick(position: Int, view: View)
    }

}