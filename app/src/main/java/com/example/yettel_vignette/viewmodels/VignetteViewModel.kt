package com.example.yettel_vignette.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yettel_vignette.enums.OrderType
import com.example.yettel_vignette.extensions.toVignetteOrder
import com.example.yettel_vignette.interfaces.VignetteHandler
import com.example.yettel_vignette.models.HighwayVignette
import com.example.yettel_vignette.models.Result
import com.example.yettel_vignette.models.Vehicle
import com.example.yettel_vignette.models.VignetteOrderRequest
import com.example.yettel_vignette.models.VignetteOrderResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class VignetteViewModel @Inject constructor(
    private val vignetteHandler: VignetteHandler
) : ViewModel() {

    private val _vehicle = MutableStateFlow<Vehicle?>(null)
    val vehicle: StateFlow<Vehicle?> get() = _vehicle

    private val _vignettes = MutableStateFlow<List<HighwayVignette>>(emptyList())
    val vignettes: StateFlow<List<HighwayVignette>> get() = _vignettes

    private val _selectedVignette = MutableStateFlow<HighwayVignette?>(null)
    val selectedVignette: StateFlow<HighwayVignette?> get() = _selectedVignette.asStateFlow()

    private val _countyVignettes = MutableStateFlow<List<HighwayVignette>>(emptyList())
    val countyVignettes: StateFlow<List<HighwayVignette>> get() = _countyVignettes

    private val _selectedCountyVignettes = MutableStateFlow<List<HighwayVignette>>(emptyList())
    val selectedCountyVignettes: StateFlow<List<HighwayVignette>> get() = _selectedCountyVignettes

    private val _basketVignettes = MutableStateFlow<List<HighwayVignette>>(emptyList())
    val basketVignettes: StateFlow<List<HighwayVignette>> get() = _basketVignettes

    private val _orderResponse = MutableStateFlow<VignetteOrderResponse?>(null)
    val orderResponse: StateFlow<VignetteOrderResponse?> get() = _orderResponse.asStateFlow()

    fun fetchVignettesAndVehicleInfo() {
        viewModelScope.launch {
            val fetchVehicleJob = async { fetchVehicleInformation() }
            val fetchVignettesJob = async { fetchVignettes() }

            fetchVehicleJob.await()
            fetchVignettesJob.await()
        }
    }

    fun setOrderType(orderType: OrderType) {
        when (orderType) {
            OrderType.COUNTY -> {
                _basketVignettes.value = _selectedCountyVignettes.value
            }

            OrderType.COUNTRY -> {
                _basketVignettes.value = _selectedVignette.value?.let { listOf(it) } ?: emptyList()
            }
        }
    }

    fun clearOrderResponse() {
        _orderResponse.value = null
    }

    fun selectVignette(vignette: HighwayVignette) {
        _selectedVignette.value = vignette
    }

    fun selectCountyVignette(vignette: HighwayVignette) {
        _selectedCountyVignettes.value = _selectedCountyVignettes.value.toMutableList().apply {
            if (contains(vignette)) remove(vignette) else add(vignette)
        }
    }

    fun hasSelectedCountryVignette(): Boolean {
        return _selectedVignette.value != null
    }

    fun hasSelectedCountyVignette(): Boolean {
        return _selectedCountyVignettes.value.isNotEmpty()
    }

    fun placeOrder() {
        viewModelScope.launch {
            val vignetteOrderRequest = VignetteOrderRequest(
                highwayOrders = _basketVignettes.value.map { it.toVignetteOrder() }
            )

            when (val result = withContext(Dispatchers.IO) { vignetteHandler.placeVignetteOrder(vignetteOrderRequest) }) {
                is Result.Success -> _orderResponse.value = result.data
                is Result.Error -> Log.e("VignetteViewModel", "Error placing order: ${result.message}")
            }
        }
    }

    private suspend fun fetchVehicleInformation() {
        when (val result = withContext(Dispatchers.IO) { vignetteHandler.getVehicleInfo() }) {
            is Result.Success -> _vehicle.value = result.data
            is Result.Error -> Log.e("VignetteViewModel", "Error fetching vehicle information: ${result.message}")
        }
    }

    private suspend fun fetchVignettes() {
        when (val result = withContext(Dispatchers.IO) { vignetteHandler.getHighwayVignetteInfo() }) {
            is Result.Success -> result.data.let { data ->
                _vignettes.value = data.vignettes
                _countyVignettes.value = data.countyVignettes
            }
            is Result.Error -> Log.e("VignetteViewModel", "Error fetching vignettes: ${result.message}")
        }
    }
}