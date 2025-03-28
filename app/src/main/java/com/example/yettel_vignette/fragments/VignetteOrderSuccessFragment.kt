package com.example.yettel_vignette.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.yettel_vignette.databinding.FragmentOrderSuccessBinding

class VignetteOrderSuccessFragment: Fragment() {
    private lateinit var binding: FragmentOrderSuccessBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentOrderSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.orderSuccessfulOkButton.setOnClickListener {
            val firstFragmentEntry = parentFragmentManager.getBackStackEntryAt(1)
            parentFragmentManager.popBackStack(firstFragmentEntry.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }
}