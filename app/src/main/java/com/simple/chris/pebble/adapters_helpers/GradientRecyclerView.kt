package com.simple.chris.pebble.adapters_helpers

//import com.sinaseyfi.advancedcardview.AdvancedCardView
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simple.chris.pebble.R
import com.simple.chris.pebble.functions.UIElement
import com.simple.chris.pebble.functions.Values

/**
 * Creates a gradient modules for each gradient
 *
 * @param context Activity context to perform certain tasks only available from an activity
 * @param gradients List of gradients used to populate modules
 * @param onGradientListener Actions to perform when a module is clicked
 * @param onGradientLongClickListener Actions to perform when module is long pressed
 *
 */
class GradientRecyclerView internal constructor(var context: Context, private val gradients: ArrayList<HashMap<String, String>>, onGradientListener: OnGradientListener, onGradientLongClickListener: OnGradientLongClickListener) : RecyclerView.Adapter<GradientRecyclerView.ViewHolder>() {
    private var mOnGradientListener: OnGradientListener = onGradientListener
    private var mOnGradientLongClickListener: OnGradientLongClickListener = onGradientLongClickListener
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    /**
     * @return Populated module to display in RecyclerView
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(layoutInflater.inflate(R.layout.module_browse_normal, parent, false), mOnGradientListener, mOnGradientLongClickListener)
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
            holder.gradientName.text = details["gradientName"]
            val gradientColours = details["gradientColours"]!!.replace("[", "").replace("]", "").split(",").map { it.trim() }
            val nl = ArrayList<String>(gradientColours)
            UIElement.gradientDrawableNew(context, holder.gradientViewer, nl, 25f)
            holder.gradientViewer.transitionName = details["gradientName"]
        } catch (e: Exception) {
            holder.gradientViewer.setBackgroundColor(context.resources.getColor(R.color.pebbleEnd))
            holder.gradientName.text = "[Broken Data]"
            Log.e("ERR", e.localizedMessage)
        }
    }

    fun stringToWords(s: String) = s.trim().splitToSequence(",")
            .filter { it.isNotEmpty() }
            .toList()

    /**
     * Referenced to get the views from the module layout
     */
    inner class ViewHolder internal constructor(view: View, onGradientListener: OnGradientListener, onGradientLongClickListener: OnGradientLongClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener/*, View.OnTouchListener*/ {
        var gradientName: TextView = view.findViewById(R.id.gradientName)
        var gradientViewer: ImageView = view.findViewById(R.id.gradient)

        private val myOnGradientListener = onGradientListener
        private val myOnGradientLongClickListener = onGradientLongClickListener

        init {
            view.setOnClickListener(this)
            view.setOnLongClickListener(this)
        }

        override fun onClick(p0: View?) {
            myOnGradientListener.onGradientClick(adapterPosition, p0 as View)
        }

        override fun onLongClick(v: View?): Boolean {
            myOnGradientLongClickListener.onGradientLongClick(adapterPosition, v as View)
            return true
        }
    }

    interface OnGradientListener {
        fun onGradientClick(position: Int, view: View)
    }

    interface OnGradientLongClickListener {
        fun onGradientLongClick(position: Int, view: View)
    }
}
