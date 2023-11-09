package com.simple.chris.pebble.recyclers

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.simple.chris.pebble.R
import com.simple.chris.pebble.functions.Vibration
import com.simple.chris.pebble.functions.convertFloatToDP
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
            colourDrawable.setStroke(convertFloatToDP(context, 5f).roundToInt(), colour)
            holder.colourPreview.background = colourDrawable
        } catch (e: Exception) {
            Log.e("ERR", "pebble.browse_menu_recycler_view.on_bind_view_holder: ${e.localizedMessage}")
        }
    }

    inner class ViewHolder internal constructor(view: View, onButtonListener: OnButtonListener): RecyclerView.ViewHolder(view), View.OnClickListener {
        var colourPreview: ImageView = view.findViewById(R.id.colourPreview)
        var colourButton: LinearLayout = view.findViewById(R.id.colourButton)
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