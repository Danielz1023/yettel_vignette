package com.example.yettel_vignette.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
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
import com.example.yettel_vignette.adapters.VignetteOrderAdapter
import com.example.yettel_vignette.databinding.FragmentOrderVignetteBinding
import com.example.yettel_vignette.databinding.SumLayoutBinding
import com.example.yettel_vignette.databinding.VehicleOrderInformationLayoutBinding
import com.example.yettel_vignette.extensions.toHungarianForint
import com.example.yettel_vignette.interfaces.VignetteActionListener
import com.example.yettel_vignette.viewmodels.VignetteViewModel
import kotlinx.coroutines.launch

class VignetteOrderFragment: Fragment(R.layout.fragment_order_vignette) {
    private var actionListener: VignetteActionListener? = null
    private lateinit var binding: FragmentOrderVignetteBinding
    private lateinit var summaryBinding: SumLayoutBinding
    private lateinit var vehicleInfoBinding: VehicleOrderInformationLayoutBinding
    private lateinit var vignetteViewModel: VignetteViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var vignetteAdapter: VignetteOrderAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is VignetteActionListener) {
            actionListener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentOrderVignetteBinding.inflate(inflater, container, false)
        summaryBinding = SumLayoutBinding.bind(binding.orderSummaryView)
        vehicleInfoBinding = VehicleOrderInformationLayoutBinding.bind(binding.vehicleOrderInformationView)
        vignetteViewModel = ViewModelProvider(requireActivity())[VignetteViewModel::class.java]
        recyclerView = binding.orderRecyclerview
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        setUpButtonListeners()
        observeViewModel()
    }

    private fun setUpRecyclerView() {
        binding.orderRecyclerview.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setUpButtonListeners() {
        binding.orderNextButton.setOnClickListener {
            vignetteViewModel.placeOrder()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { collectVehicleData() }
                launch { collectOrderResponse() }
                launch { collectBasketVignettes() }
            }
        }
    }

    private suspend fun collectVehicleData() {
        vignetteViewModel.vehicle.collect { vehicle ->
            vehicle?.let {
                vehicleInfoBinding.plateValue.text = it.plate
            }
        }
    }

    private suspend fun collectOrderResponse() {
        vignetteViewModel.orderResponse.collect { response ->
            if (response != null && response.statusCode == "OK") {
                actionListener?.hideNavbar()
                vignetteViewModel.clearOrderResponse()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, VignetteOrderSuccessFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private suspend fun collectBasketVignettes() {
        vignetteViewModel.basketVignettes.collect { vignettes ->
            if (vignettes.isNotEmpty()) {
                vignetteAdapter = VignetteOrderAdapter(vignettes)
                recyclerView.adapter = vignetteAdapter
                summaryBinding.summaryPrice.text =
                    (vignettes.sumOf { it.cost } + vignettes[0].trxFee).toHungarianForint()
                vehicleInfoBinding.vignetteTypeValue.text =
                    if (vignettes.first().vignetteCategory.isNullOrEmpty()) {
                        "Éves vármegyei"
                    } else {
                        "Országos matrica"
                    }
                binding.extraFeeValue.text = vignettes.first().trxFee.toHungarianForint()
            }
        }
    }
}