package com.example.yettel_vignette.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yettel_vignette.enums.OrderType
import com.example.yettel_vignette.extensions.toVignetteOrder
import com.example.yettel_vignette.interfaces.VignetteHandler
import com.example.yettel_vignette.models.HighwayVignette
import com.example.yettel_vignette.models.Payload
import com.example.yettel_vignette.models.Vehicle
import com.example.yettel_vignette.models.VehicleCategory
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

    fun placeOrder() {
        viewModelScope.launch {
            try {
                val vignetteOrderRequest = VignetteOrderRequest(
                    highwayOrders = _basketVignettes.value.map { it.toVignetteOrder() }
                )

                val response = withContext(Dispatchers.IO) {
                    vignetteHandler.placeVignetteOrder(vignetteOrderRequest)
                }

                if (response.isSuccessful) {
                    _orderResponse.value = response.body()
                }
            } catch (e: Exception) {
                Log.e("VignetteViewModel", "Error placing order", e)
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

    private suspend fun fetchVehicleInformation() {
        try {
            val response = withContext(Dispatchers.IO) {
                vignetteHandler.getVehicleInfo()
            }

            if (response.isSuccessful && response.body() != null) {
                val vehicle = response.body() ?: return
                _vehicle.value = vehicle
            } else {
                Log.e("VignetteViewModel", "Response was unsuccessful or body is null")
            }
        } catch (e: Exception) {
            Log.e("VignetteViewModel", "Error fetching vehicle information", e)
        }
    }

    private suspend fun fetchVignettes() {
        try {
            val response = withContext(Dispatchers.IO) {
                vignetteHandler.getHighwayVignetteInfo()
            }

            if (response.isSuccessful && response.body() != null) {
                val payload = response.body()?.payload ?: return
                val vehicleCategoriesMap = mapVehicleCategories(payload)

                _vignettes.value = filterAndMapVignettes(payload, vehicleCategoriesMap)
                _countyVignettes.value = mapCountyVignettes(payload)
            } else {
                Log.e("VignetteViewModel", "Response was unsuccessful or body is null")
            }
        } catch (e: Exception) {
            Log.e("VignetteViewModel", "Error fetching vignettes", e)
        }
    }


    private fun mapVehicleCategories(payload: Payload): Map<String, VehicleCategory> {
        return payload.vehicleCategories.associateBy { it.category }
    }

    private fun filterAndMapVignettes(
        payload: Payload,
        vehicleCategoriesMap: Map<String, VehicleCategory>
    ): List<HighwayVignette> {
        return payload.highwayVignettes
            .filter { it.vignetteType.size == 1 }
            .mapIndexed { index, vignette ->
                val vignetteCategory =
                    vehicleCategoriesMap[vignette.vehicleCategory]?.vignetteCategory ?: ""
                vignette.copy(
                    pos = index,
                    display = vignetteCategory + " - " + vignette.vignetteType.joinToString(),
                    vignetteCategory = vignetteCategory
                )
            }
    }

    private fun mapCountyVignettes(payload: Payload): List<HighwayVignette> {
        val countiesMap = payload.counties.associateBy { it.id }
        return payload.highwayVignettes.flatMapIndexed { _, vignette ->
            vignette.vignetteType.mapNotNull { id ->
                countiesMap[id]?.let { county ->
                    vignette.copy(
                        display = county.name,
                        vignetteCategory = vignette.vignetteCategory,
                        vignetteType = listOf(id),
                        pos = vignette.vignetteType.indexOf(id)
                    )
                }
            }
        }
    }
}