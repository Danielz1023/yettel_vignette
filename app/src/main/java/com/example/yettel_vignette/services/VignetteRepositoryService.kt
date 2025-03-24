package com.example.yettel_vignette.services

import com.example.yettel_vignette.interfaces.VignetteApiService
import com.example.yettel_vignette.interfaces.VignetteRepository
import com.example.yettel_vignette.models.HighwayVignetteResponse
import com.example.yettel_vignette.models.Vehicle
import com.example.yettel_vignette.models.VignetteOrderRequest
import com.example.yettel_vignette.models.VignetteOrderResponse
import retrofit2.Response
import javax.inject.Inject

class VignetteRepositoryService @Inject constructor(
    private val vignetteApiService: VignetteApiService
) : VignetteRepository {
    override suspend fun getHighwayVignetteInfo(): Response<HighwayVignetteResponse> {
        return vignetteApiService.getHighwayVignetteInfo()
    }

    override suspend fun getVehicleInfo(): Response<Vehicle> {
        return vignetteApiService.getVehicleInfo()
    }

    override suspend fun placeVignetteOrder(orderList: VignetteOrderRequest): Response<VignetteOrderResponse> {
        return vignetteApiService.placeVignetteOrder(orderList)
    }
}