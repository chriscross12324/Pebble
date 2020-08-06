package com.simple.chris.pebble

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.module_browse_normal.view.*
import android.os.Handler
import android.widget.Toast

/**
 * Creates a gradient modules for each gradient
 *
 * @param context Activity context to perform certain tasks only available from an activity
 * @param gradients List of gradients used to populate modules
 * @param onGradientListener Actions to perform when a module is clicked
 * @param onGradientLongClickListener Actions to perform when module is long pressed
 *
 */
class GradientRecyclerViewAdapter internal constructor(var context: Context, private val gradients: ArrayList<HashMap<String, String>>, onGradientListener: OnGradientListener, onGradientLongClickListener: OnGradientLongClickListener) : RecyclerView.Adapter<GradientRecyclerViewAdapter.ViewHolder>() {
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
            val startColour = Color.parseColor(details["startColour"])
            val endColour = Color.parseColor(details["endColour"])
            UIElement.gradientDrawable(context, holder.gradientView, startColour, endColour, Values.gradientCornerRadius)
        } catch (e: Exception) {
            holder.gradientView.setBackgroundColor(context.resources.getColor(R.color.pebbleEnd))
            holder.gradientName.text = "[Broken Data]"
        }
    }

    /**
     * Referenced to get the views from the module layout
     */
    inner class ViewHolder internal constructor(view: View, onGradientListener: OnGradientListener, onGradientLongClickListener: OnGradientLongClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener/*, View.OnTouchListener*/ {
        var gradientName: TextView = view.gradientName
        var gradientView: ImageView = view.gradient
        /*init {
            view.setOnTouchListener(this)
        }*/
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

        /*override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            val handler = Handler()
            val runnable = Runnable {
                Toast.makeText(context, "Long", Toast.LENGTH_SHORT).show()
                Vibration.strongFeedback(context)
            }
            if (event != null) {
                var isButtonPressed: Boolean
                if (event.action == MotionEvent.ACTION_DOWN) {
                    isButtonPressed = true
                    handler.postDelayed(runnable, 2000)
                } else if (event.action == MotionEvent.ACTION_UP) {
                    if (isButtonPressed) {
                        isButtonPressed = false
                        Toast.makeText(context, "Short", Toast.LENGTH_SHORT).show()
                        Vibration.lowFeedback(context)
                        handler.removeCallbacksAndMessages(runnable)
                    }

                }
            }
            return true
        }*/

    }

    interface OnGradientListener {
        fun onGradientClick(position: Int, view: View)
    }

    interface OnGradientLongClickListener {
        fun onGradientLongClick(position: Int, view: View)
    }
}
