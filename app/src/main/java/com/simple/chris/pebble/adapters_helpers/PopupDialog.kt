package com.simple.chris.pebble.adapters_helpers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.simple.chris.pebble.R

class PopupDialog : DialogFragment() {

    companion object {
        fun newPopup(context: Context, popupName: String, icon: Int?, title: Int?, titleString: String?, description: Int,
                     buttonArrayList: ArrayList<HashMap<String, Int>>?, decorView: View?, listener: PopupDialogButtonRecycler.OnButtonListener?) {
            val fragment = PopupDialog()
            val argument = Bundle()
            //argument.putParcelable("listener", listener)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.dialog_popup, container)


        return rootView
    }

}