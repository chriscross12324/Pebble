package com.simple.chris.pebble.ui.fragmentbrowsenew

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.simple.chris.pebble.databinding.FragmentBrowseNewBinding

class FragmentBrowseNew : Fragment() {

    companion object {
        fun newInstance() = FragmentBrowseNew()
    }

    private lateinit var binding:FragmentBrowseNewBinding
    private lateinit var viewModel: FragmentBrowseNewViewModel
    private var fragmentListener: BrowseFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[FragmentBrowseNewViewModel::class.java]
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBrowseNewBinding.inflate(inflater, container, false)

        binding.button.setOnClickListener {
            fragmentListener?.onOpenDetailsButtonClick()
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BrowseFragmentListener) {
            fragmentListener = context
        } else {
            throw ClassCastException("$context must implement BrowseFragmentListener")
        }
    }

    interface BrowseFragmentListener {
        fun onOpenDetailsButtonClick()
    }

}