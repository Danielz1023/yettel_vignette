package com.example.yettel_vignette.services

import com.example.yettel_vignette.interfaces.VignetteHandler
import com.example.yettel_vignette.interfaces.VignetteRepository
import com.example.yettel_vignette.models.HighwayVignetteResponse
import com.example.yettel_vignette.models.Vehicle
import com.example.yettel_vignette.models.VignetteOrderRequest
import com.example.yettel_vignette.models.VignetteOrderResponse
import retrofit2.Response
import javax.inject.Inject

class VignetteHandlerService @Inject constructor(
    private val repository: VignetteRepository
) : VignetteHandler {

    override suspend fun getHighwayVignetteInfo(): Response<HighwayVignetteResponse> {
        return repository.getHighwayVignetteInfo()
    }

    override suspend fun getVehicleInfo(): Response<Vehicle> {
        return repository.getVehicleInfo()
    }

    override suspend fun placeVignetteOrder(orderList: VignetteOrderRequest): Response<VignetteOrderResponse> {
        return repository.placeVignetteOrder(orderList)
    }
}