package com.example.yettel_vignette.interfaces

import com.example.yettel_vignette.models.Vehicle
import com.example.yettel_vignette.models.VignetteOrderRequest
import com.example.yettel_vignette.models.VignetteOrderResponse
import com.example.yettel_vignette.models.VignetteResult
import com.example.yettel_vignette.models.Result

interface VignetteHandler {
    suspend fun getHighwayVignetteInfo(): Result<VignetteResult>
    suspend fun getVehicleInfo(): Result<Vehicle>
    suspend fun placeVignetteOrder(orderList: VignetteOrderRequest): Result<VignetteOrderResponse>
}
