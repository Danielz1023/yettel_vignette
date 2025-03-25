package com.example.yettel_vignette.services

import com.example.yettel_vignette.interfaces.VignetteHandler
import com.example.yettel_vignette.interfaces.VignetteRepository
import com.example.yettel_vignette.models.HighwayVignette
import com.example.yettel_vignette.models.Payload
import com.example.yettel_vignette.models.Vehicle
import com.example.yettel_vignette.models.VehicleCategory
import com.example.yettel_vignette.models.VignetteOrderRequest
import com.example.yettel_vignette.models.VignetteOrderResponse
import com.example.yettel_vignette.models.VignetteResult
import com.example.yettel_vignette.models.Result
import javax.inject.Inject

class VignetteHandlerService @Inject constructor(
    private val repository: VignetteRepository
) : VignetteHandler {

    override suspend fun getHighwayVignetteInfo(): Result<VignetteResult> {
        return try {
            val response = repository.getHighwayVignetteInfo()
            if (response.isSuccessful) {
                response.body()?.payload?.let { payload ->
                    val vehicleCategoriesMap = mapVehicleCategories(payload)

                    val vignetteResult = VignetteResult(
                        vignettes = filterAndMapVignettes(payload, vehicleCategoriesMap),
                        countyVignettes = mapCountyVignettes(payload)
                    )

                    Result.Success(vignetteResult)
                } ?: Result.Error("Payload is null")
            } else {
                Result.Error("API call failed with code ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
    }

    override suspend fun getVehicleInfo(): Result<Vehicle> {
        return try {
            val response = repository.getVehicleInfo()

            if (response.isSuccessful) {
                response.body()?.let { vehicle ->
                    Result.Success(vehicle)
                } ?: Result.Error("Vehicle data is null")
            } else {
                Result.Error("API call failed with code ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
    }

    override suspend fun placeVignetteOrder(orderList: VignetteOrderRequest): Result<VignetteOrderResponse> {
        return try {
            val response = repository.placeVignetteOrder(orderList)

            if (response.isSuccessful) {
                response.body()?.let { orderResponse ->
                    Result.Success(orderResponse)
                } ?: Result.Error("Order response is null")
            } else {
                Result.Error("API call failed with code ${response.code()}")
            }
        } catch (e: Exception) {
            Result.Error(e.message.toString())
        }
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

    private fun mapVehicleCategories(payload: Payload): Map<String, VehicleCategory> {
        return payload.vehicleCategories.associateBy { it.category }
    }
}