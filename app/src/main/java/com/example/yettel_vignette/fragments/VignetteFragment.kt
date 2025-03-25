package com.example.yettel_vignette.fragments

import android.content.Context
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
import androidx.recyclerview.widget.RecyclerView
import com.example.yettel_vignette.R
import com.example.yettel_vignette.adapters.VignetteAdapter
import com.example.yettel_vignette.databinding.CountryVignettesLayoutBinding
import com.example.yettel_vignette.databinding.FragmentVignetteBinding
import com.example.yettel_vignette.databinding.VehicleInformationLayoutBinding
import com.example.yettel_vignette.enums.OrderType
import com.example.yettel_vignette.interfaces.VignetteActionListener
import com.example.yettel_vignette.models.HighwayVignette
import com.example.yettel_vignette.models.Vehicle
import com.example.yettel_vignette.viewmodels.VignetteViewModel
import kotlinx.coroutines.launch

class VignetteFragment : Fragment(R.layout.fragment_vignette) {
    private var actionListener: VignetteActionListener? = null
    private lateinit var binding: FragmentVignetteBinding
    private lateinit var countryVignettesBinding: CountryVignettesLayoutBinding
    private lateinit var vehicleInformationBinding: VehicleInformationLayoutBinding

    private lateinit var vignetteViewModel: VignetteViewModel
    private lateinit var vignetteAdapter: VignetteAdapter

    private lateinit var recyclerView: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is VignetteActionListener) {
            actionListener = context
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentVignetteBinding.inflate(inflater, container, false)
        countryVignettesBinding = CountryVignettesLayoutBinding.bind(binding.countryVignettesView)
        vehicleInformationBinding = VehicleInformationLayoutBinding.bind(binding.vehicleInformationView)
        vignetteViewModel = ViewModelProvider(requireActivity())[VignetteViewModel::class.java]
        recyclerView = countryVignettesBinding.countryVignetteRecyclerview
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        actionListener?.showNavbar()
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        fetchData()
    }
    private fun fetchData() {
        vignetteViewModel.fetchVignettesAndVehicleInfo()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupListeners() {
        binding.countyVignetteOption.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CountyVignetteFragment())
                .addToBackStack(null)
                .commit()
        }

        countryVignettesBinding.buyCountryVignetteButton.setOnClickListener {
            if(vignetteViewModel.hasSelectedCountryVignette()){
                vignetteViewModel.setOrderType(OrderType.COUNTRY)
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, VignetteOrderFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { vignetteViewModel.vignettes.collect { vignettes -> updateVignettes(vignettes) } }
                launch { vignetteViewModel.vehicle.collect { vehicle -> updateVehicle(vehicle) } }
                launch { vignetteViewModel.selectedVignette.collect { selectedVignette -> updateSelectedVignette(selectedVignette) }}
            }
        }
    }
    private fun updateVignettes(vignettes: List<HighwayVignette>) {
        if (vignettes.isNotEmpty()) {
            vignetteAdapter = VignetteAdapter(vignettes) { selectedVignette ->
                vignetteViewModel.selectVignette(selectedVignette)
            }
            recyclerView.adapter = vignetteAdapter
        }
    }

    private fun updateVehicle(vehicle: Vehicle?) {
        vehicle?.let {
            vehicleInformationBinding.name.text = it.name
            vehicleInformationBinding.plate.text = it.plate
        }
    }

    private fun updateSelectedVignette(selectedVignette: HighwayVignette?) {
        if (::vignetteAdapter.isInitialized) {
            selectedVignette?.pos?.let { vignetteAdapter.selectVignette(it) }
        }
    }
}