package com.example.yettel_vignette.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.example.yettel_vignette.databinding.DefaultVignetteChoiceBinding
import com.example.yettel_vignette.databinding.HighlightedVignetteChoiceBinding
import com.example.yettel_vignette.extensions.toHungarianForint
import com.example.yettel_vignette.extensions.toVignetteChoiceBinding
import com.example.yettel_vignette.models.HighwayVignette

class VignetteAdapter(
    private val vignettes: List<HighwayVignette>,
    private val onVignetteClick: (HighwayVignette) -> Unit
) : RecyclerView.Adapter<VignetteAdapter.VignetteViewHolder>() {

    private var selectedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VignetteViewHolder {
        val binding = if (viewType == 0) {
            HighlightedVignetteChoiceBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        } else {
            DefaultVignetteChoiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        }
        return VignetteViewHolder(binding, onVignetteClick)
    }

    override fun onBindViewHolder(holder: VignetteViewHolder, position: Int) {
        val vignette = vignettes[position]
        holder.bind(vignette, position == selectedPosition)
    }

    override fun getItemCount(): Int = vignettes.size

    override fun getItemViewType(position: Int): Int {
        return if (position == selectedPosition) 0 else 1
    }

    fun selectVignette(position: Int) {
        val previousSelectedPosition = selectedPosition
        selectedPosition = position

        notifyItemChanged(previousSelectedPosition)
        notifyItemChanged(selectedPosition)
    }

    inner class VignetteViewHolder(
        private val binding: ViewBinding,
        private val onVignetteClick: (HighwayVignette) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(vignette: HighwayVignette, isSelected: Boolean) {

            val vignetteBinding = when (binding) {
                is HighlightedVignetteChoiceBinding -> binding.toVignetteChoiceBinding()
                is DefaultVignetteChoiceBinding -> binding.toVignetteChoiceBinding()
                else -> throw IllegalArgumentException("Unknown binding type")
            }

            vignetteBinding.apply {
                vignetteRadio.isChecked = isSelected
                vignetteRadio.isClickable = false
                vignetteTitle.text = vignette.display
                vignettePrice.text = vignette.cost.toHungarianForint()
            }

            binding.root.setOnClickListener {
                onVignetteClick(vignette)
            }
        }
    }
}

