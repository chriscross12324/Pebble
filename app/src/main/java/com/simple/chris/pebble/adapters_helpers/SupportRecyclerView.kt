package com.simple.chris.pebble.adapters_helpers

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.simple.chris.pebble.R
import kotlinx.android.synthetic.main.button_support.view.*

/**
 * Creates a gradient modules for each gradient
 *
 * @param context Activity context to perform certain tasks only available from an activity
 * @param gradients List of gradients used to populate modules
 * @param onGradientListener Actions to perform when a module is clicked
 * @param onGradientLongClickListener Actions to perform when module is long pressed
 *
 */
class SupportRecyclerView internal constructor(var context: Context, private val gradients: ArrayList<HashMap<String, String>>, onGradientListener: OnClickListener) : RecyclerView.Adapter<SupportRecyclerView.ViewHolder>() {
    private var mOnGradientListener: OnClickListener = onGradientListener
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    /**
     * @return Populated module to display in RecyclerView
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.button_support, parent, false), mOnGradientListener)
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
            holder.buttonTitle.text = details["buttonTitle"]
            holder.buttonBody.text = details["buttonBody"]
            holder.buttonAmount.text = details["buttonAmount"]
        } catch (e: Exception) {
            Toast.makeText(context, "Something went wrong displaying the options", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Referenced to get the views from the module layout
     */
    inner class ViewHolder internal constructor(view: View, onGradientListener: OnClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var buttonTitle: TextView = view.buttonTitle
        var buttonBody: TextView = view.buttonBody
        var buttonAmount: TextView = view.buttonAmount

        private val myOnGradientListener = onGradientListener

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            myOnGradientListener.onButtonClick(adapterPosition, p0 as View)
        }
    }

    interface OnClickListener {
        fun onButtonClick(position: Int, view: View)
    }
}
