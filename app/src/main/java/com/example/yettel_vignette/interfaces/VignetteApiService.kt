package com.example.yettel_vignette.interfaces

import com.example.yettel_vignette.models.HighwayVignetteResponse
import com.example.yettel_vignette.models.Vehicle
import com.example.yettel_vignette.models.VignetteOrder
import com.example.yettel_vignette.models.VignetteOrderRequest
import com.example.yettel_vignette.models.VignetteOrderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface VignetteApiService {
    @GET("v1/highway/info")
    suspend fun getHighwayVignetteInfo(): Response<HighwayVignetteResponse>

    @GET("v1/highway/vehicle")
    suspend fun getVehicleInfo(): Response<Vehicle>

    @POST("v1/highway/order")
    suspend fun placeVignetteOrder(@Body orderRequest: VignetteOrderRequest): Response<VignetteOrderResponse>
}