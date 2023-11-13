package com.simple.chris.pebble.ui.fragmentbrowsenew

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.simple.chris.pebble.databinding.FragmentGradientDetailsNewBinding

class FragmentGradientDetailsNew : Fragment() {

    companion object {
        fun newInstance() = FragmentGradientDetailsNew()
    }

    private lateinit var binding: FragmentGradientDetailsNewBinding
    private lateinit var viewModel: FragmentGradientDetailsNewViewModel
    private var fragmentListener: DetailsFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[FragmentGradientDetailsNewViewModel::class.java]
        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGradientDetailsNewBinding.inflate(inflater, container, false)

        binding.button.setOnClickListener {
            fragmentListener?.onCloseDetailsButtonClick()
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DetailsFragmentListener) {
            fragmentListener = context
        } else {
            throw ClassCastException("$context must implement DetailsFragmentListener")
        }
    }

    interface DetailsFragmentListener {
        fun onCloseDetailsButtonClick()
    }

}