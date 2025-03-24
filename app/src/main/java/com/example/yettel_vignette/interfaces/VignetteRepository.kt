package com.example.yettel_vignette.interfaces

import com.example.yettel_vignette.models.HighwayVignetteResponse
import com.example.yettel_vignette.models.Vehicle
import com.example.yettel_vignette.models.VignetteOrder
import com.example.yettel_vignette.models.VignetteOrderRequest
import com.example.yettel_vignette.models.VignetteOrderResponse
import retrofit2.Response

interface VignetteRepository {
    suspend fun getHighwayVignetteInfo(): Response<HighwayVignetteResponse>
    suspend fun getVehicleInfo(): Response<Vehicle>
    suspend fun placeVignetteOrder(orderList: VignetteOrderRequest): Response<VignetteOrderResponse>
}
