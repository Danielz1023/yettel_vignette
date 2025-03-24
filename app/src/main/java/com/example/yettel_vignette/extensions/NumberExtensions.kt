package com.example.yettel_vignette.extensions

fun Int.toHungarianForint(): String {
    return "%,d Ft".format(this).replace(",", " ")
}