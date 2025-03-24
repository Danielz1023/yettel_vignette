package com.example.yettel_vignette.extensions

import com.example.yettel_vignette.models.HighwayVignette
import com.example.yettel_vignette.models.VignetteOrder

fun HighwayVignette.toVignetteOrder(): VignetteOrder {
    return VignetteOrder(
        type = vignetteType.firstOrNull() ?: "Unknown",
        category = vignetteCategory ?: "Unknown",
        cost = (cost + trxFee).toFloat()
    )
}
