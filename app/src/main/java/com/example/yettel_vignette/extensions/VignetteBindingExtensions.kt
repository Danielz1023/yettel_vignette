package com.example.yettel_vignette.extensions

import android.widget.RadioButton
import android.widget.TextView
import com.example.yettel_vignette.databinding.DefaultVignetteChoiceBinding
import com.example.yettel_vignette.databinding.HighlightedVignetteChoiceBinding

interface VignetteChoiceBinding {
    val vignetteRadio: RadioButton
    val vignetteTitle: TextView
    val vignettePrice: TextView
}

fun HighlightedVignetteChoiceBinding.toVignetteChoiceBinding(): VignetteChoiceBinding {
    return object : VignetteChoiceBinding {
        override val vignetteRadio: RadioButton = this@toVignetteChoiceBinding.vignetteRadio
        override val vignetteTitle: TextView = this@toVignetteChoiceBinding.vignetteTitle
        override val vignettePrice: TextView = this@toVignetteChoiceBinding.vignettePrice
    }
}

fun DefaultVignetteChoiceBinding.toVignetteChoiceBinding(): VignetteChoiceBinding {
    return object : VignetteChoiceBinding {
        override val vignetteRadio: RadioButton = this@toVignetteChoiceBinding.vignetteRadio
        override val vignetteTitle: TextView = this@toVignetteChoiceBinding.vignetteTitle
        override val vignettePrice: TextView = this@toVignetteChoiceBinding.vignettePrice
    }
}