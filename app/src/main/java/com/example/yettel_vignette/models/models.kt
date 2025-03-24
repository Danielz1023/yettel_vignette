package com.example.yettel_vignette.models

data class HighwayVignetteResponse(
    val requestId: String,
    val statusCode: String,
    val payload: Payload,
    val dataType: String
)

data class Payload(
    val highwayVignettes: List<HighwayVignette>,
    val vehicleCategories: List<VehicleCategory>,
    val counties: List<County>
)

data class HighwayVignette(
    val pos: Int? = null,
    val display: String? = null,
    val vignetteType: List<String>,
    val vehicleCategory: String,
    val vignetteCategory: String? = null,
    val cost: Int,
    val trxFee: Int,
    val sum: Int
)

data class VehicleCategory(
    val category: String,
    val vignetteCategory: String,
    val name: LanguageNames
)

data class LanguageNames(
    val hu: String,
    val en: String
)

data class County(
    val id: String,
    val name: String
)

data class Vehicle(
    val requestId: String,
    val statusCode: String,
    val internationalRegistrationCode: String,
    val type: String,
    val name: String,
    val plate: String,
    val country: LanguageNames,
    val vignetteType: String
)

data class VignetteOrder(
    val type: String,
    val category: String,
    val cost: Float
)

data class VignetteOrderResponse(
    val requestId: String,
    val statusCode: String,
    val receivedOrders: List<VignetteOrder>
)

data class VignetteOrderRequest(
    val highwayOrders: List<VignetteOrder>
)
