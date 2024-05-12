package com.simple.chris.pebble.recyclers

import android.content.Context
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.recyclerview.widget.RecyclerView
import com.simple.chris.pebble.components.GradientModule
import com.simple.chris.pebble.data.GradientObject
import com.simple.chris.pebble.functions.Values
import com.simple.chris.pebble.functions.vibrateWeak

/**
 * Creates a gradient modules for each gradient
 *
 * @param context Activity context to perform certain tasks only available from an activity
 * @param gradients List of gradients used to populate modules
 * @param onGradientListener Actions to perform when a module is clicked
 * @param onGradientLongClickListener Actions to perform when module is long pressed
 *
 */

class GradientRecyclerViewAdapter(
    private val context: Context,
    private val gradientInfo: List<GradientObject>,
) : RecyclerView.Adapter<GradientRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ComposeView(parent.context))
    }

    override fun getItemCount(): Int = gradientInfo.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gradientDetails = gradientInfo[position]
        val gradientName: String = gradientDetails.gradientName
        val gradientHexList: List<String> = gradientDetails.gradientHEXList

        holder.bind(gradientName, gradientHexList)
    }

    inner class ViewHolder(
        private val composeView: ComposeView,
    ) : RecyclerView.ViewHolder(composeView) {

        init {
            composeView.setOnClickListener {
                if (!Values.animatingSharedElement) {
                    vibrateWeak(context)
                    Values.currentGradientScreenView = composeView
                    Values.animatingSharedElement = true
                    Values.canDismissSharedElement = false
                }
            }
            composeView.setOnLongClickListener {

                true
            }
        }

        fun bind(gradientName: String, hexList: List<String>) {
            composeView.setContent {
                GradientModule(
                    gradientName = gradientName,
                    hexList = hexList
                )
            }
        }

    }


}

interface ListenerGradientClick {
    fun onClick(position: Int)
}

interface ListenerGradientLongClick {
    fun onLongClick(position: Int)
}

/*class GradientRecyclerView internal constructor(var context: Context, private val gradients: ArrayList<HashMap<String, String>>, onGradientListener: OnGradientListener, onGradientLongClickListener: OnGradientLongClickListener) : RecyclerView.Adapter<GradientRecyclerView.ViewHolder>() {
    private var mOnGradientListener: OnGradientListener = onGradientListener
    private var mOnGradientLongClickListener: OnGradientLongClickListener = onGradientLongClickListener
    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    */
/**
 * @return Populated module to display in RecyclerView
 *//*
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.module_browse_normal, parent, false)
        return ViewHolder(view, mOnGradientListener, mOnGradientLongClickListener)
    }

    override fun getItemCount(): Int {
        return gradients.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val details: HashMap<String, String> = gradients[position]
        val gradientName = details["gradientName"] ?: "[Broken Data]"
        val gradientColours = ArrayList<String>(details["gradientColours"]?.replace("[", "")?.replace("]", "")?.split(",")?.map { it.trim() } ?: emptyList())
        holder.bind(gradientName, gradientColours)
    }

    */
/**
 * Referenced to get the views from the module layout
 *//*
    inner class ViewHolder internal constructor(view: View, onGradientListener: OnGradientListener, onGradientLongClickListener: OnGradientLongClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener, View.OnLongClickListener*//*, View.OnTouchListener*//* {
        *//*var gradientName: TextView = view.findViewById(R.id.gradientName)
        var gradientViewer: ImageView = view.findViewById(R.id.gradient)*//*

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

        fun bind(gradientName: String, hexList: List<String>) {


            ComposeView(itemView.context).apply {
                itemView.findViewById<ComposeView>(R.id.compose_view).setContent {
                    GradientModule(
                        gradientName = gradientName,
                        hexList = hexList
                    )
                }
            }
        }
    }

    interface OnGradientClickListener {
        fun onGradientClick(position: Int, view: View)
    }

    interface OnGradientLongClickListener {
        fun onGradientLongClick(position: Int, view: View)
    }
}*/
