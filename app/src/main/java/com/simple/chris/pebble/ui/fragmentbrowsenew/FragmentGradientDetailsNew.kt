package com.simple.chris.pebble.ui.fragmentbrowsenew

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.simple.chris.pebble.R

class FragmentGradientDetailsNew : Fragment() {

    companion object {
        fun newInstance() = FragmentGradientDetailsNew()
    }

    private lateinit var viewModel: FragmentBrowseNewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FragmentBrowseNewViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_browse_new, container, false)
    }

}