package com.example.yettel_vignette.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yettel_vignette.R
import com.example.yettel_vignette.adapters.CountyVignetteAdapter
import com.example.yettel_vignette.databinding.FragmentCountyVignetteBinding
import com.example.yettel_vignette.databinding.SumLayoutBinding
import com.example.yettel_vignette.enums.OrderType
import com.example.yettel_vignette.extensions.toHungarianForint
import com.example.yettel_vignette.models.HighwayVignette
import com.example.yettel_vignette.viewmodels.VignetteViewModel
import kotlinx.coroutines.launch

class CountyVignetteFragment : Fragment() {
    private lateinit var binding: FragmentCountyVignetteBinding
    private lateinit var summaryBinding: SumLayoutBinding
    private lateinit var vignetteViewModel: VignetteViewModel
    private lateinit var vignetteAdapter: CountyVignetteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCountyVignetteBinding.inflate(inflater, container, false)
        summaryBinding = SumLayoutBinding.bind(binding.summaryView)
        vignetteViewModel = ViewModelProvider(requireActivity())[VignetteViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupBuyButton()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.countyVignetteRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupBuyButton() {
        binding.buyCountyVignetteButton.setOnClickListener {
            vignetteViewModel.setOrderType(OrderType.COUNTY)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, VignetteOrderFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { vignetteViewModel.countyVignettes.collect { updateCountyVignettes(it) } }
                launch { vignetteViewModel.selectedCountyVignettes.collect { updateSelectedCountyVignettes(it) } }
            }
        }
    }

    private fun updateCountyVignettes(vignettes: List<HighwayVignette>) {
        if (vignettes.isNotEmpty()) {
            vignetteAdapter = CountyVignetteAdapter(vignettes) { selectedVignette ->
                vignetteViewModel.selectCountyVignette(selectedVignette)
            }
            binding.countyVignetteRecyclerview.adapter = vignetteAdapter
        }
    }

    private fun updateSelectedCountyVignettes(vignettes: List<HighwayVignette>) {
        vignetteAdapter.selectVignettes(vignettes.mapNotNull { it.pos })
        summaryBinding.summaryPrice.text = vignettes.sumOf { it.sum }.toHungarianForint()
    }
}