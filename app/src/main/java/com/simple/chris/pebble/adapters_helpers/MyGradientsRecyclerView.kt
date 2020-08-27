package com.simple.chris.pebble.adapters_helpers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.module_browse_normal.view.*
import com.simple.chris.pebble.R
import com.simple.chris.pebble.functions.UIElement
import com.simple.chris.pebble.functions.Values
import kotlinx.android.synthetic.main.module_browse_normal.view.gradient
import kotlinx.android.synthetic.main.module_my_gradients.view.*

/**
 * Creates a gradient modules for each gradient
 *
 * @param context Activity context to perform certain tasks only available from an activity
 * @param gradients List of gradients used to populate modules
 * @param onGradientListener Actions to perform when a module is clicked
 *
 */
class MyGradientsRecyclerView internal constructor(var context: Context, private val gradients: ArrayList<HashMap<String, String>>, onGradientListener: OnGradientListener) : RecyclerView.Adapter<MyGradientsRecyclerView.ViewHolder>() {
    private var mOnGradientListener: OnGradientListener = onGradientListener
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    /**
     * @return Populated module to display in RecyclerView
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.module_my_gradients, parent, false), mOnGradientListener)
    }

    override fun getItemCount(): Int {
        return gradients.size
    }

    /**
     * Populates a module
     *
     * @param holder References ViewHolder to use its internal views
     * @param position Grabs required information for a gradient at a specific position from the ArrayList
     *
     * If information for a module is invalid/missing, the module will be populated with fallback info
     */
    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val details: HashMap<String, String> = gradients[position]
            val startColour = Color.parseColor(details["startColour"])
            val endColour = Color.parseColor(details["endColour"])
            UIElement.gradientDrawable(context, holder.gradientView, startColour, endColour, Values.gradientCornerRadius)
            //holder.gradientView.transitionName = details["gradientName"]
        } catch (e: Exception) {
            holder.gradientView.setBackgroundColor(context.resources.getColor(R.color.pebbleEnd))
        }
    }

    /**
     * Referenced to get the views from the module layout
     */
    inner class ViewHolder internal constructor(view: View, onGradientListener: OnGradientListener) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var removeText: TextView = view.removeText
        var viewText: TextView = view.viewText
        var gradientView: ImageView = view.gradient

        private val myOnGradientListener = onGradientListener

        init {
            removeText.setOnClickListener(this)
            viewText.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            myOnGradientListener.onGradientClick(adapterPosition, p0 as View)
        }

    }

    interface OnGradientListener {
        fun onGradientClick(position: Int, view: View)
    }
}
