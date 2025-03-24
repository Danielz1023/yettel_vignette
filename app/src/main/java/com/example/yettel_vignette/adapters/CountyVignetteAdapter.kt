package com.example.yettel_vignette.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yettel_vignette.databinding.CountyVignetteCheckboxItemBinding
import com.example.yettel_vignette.extensions.toHungarianForint
import com.example.yettel_vignette.models.HighwayVignette

class CountyVignetteAdapter(
    private val vignettes: List<HighwayVignette>,
    private val onVignetteClick: (HighwayVignette) -> Unit
) : RecyclerView.Adapter<CountyVignetteAdapter.CountyVignetteViewHolder>() {

    private var selectedPositions: List<Int> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountyVignetteViewHolder {
        val binding = CountyVignetteCheckboxItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CountyVignetteViewHolder(binding, onVignetteClick)
    }

    override fun onBindViewHolder(holder: CountyVignetteViewHolder, position: Int) {
        val vignette = vignettes[position]
        holder.bind(vignette, position in selectedPositions)
    }

    override fun getItemCount(): Int = vignettes.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun selectVignettes(positions: List<Int>) {
        val newSelectedSet = positions.toSet()

        // Notify unselected positions (those that were selected but are no longer in the new positions)
        (selectedPositions - newSelectedSet).forEach { notifyItemChanged(it) }

        // Notify newly selected positions
        (newSelectedSet - selectedPositions.toSet()).forEach { notifyItemChanged(it) }

        selectedPositions = positions
    }

    inner class CountyVignetteViewHolder(
        private val binding: CountyVignetteCheckboxItemBinding,
        private val onVignetteClick: (HighwayVignette) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(vignette: HighwayVignette, isSelected: Boolean) {
            binding.vignetteCheckbox.isChecked = isSelected
            binding.vignetteCheckbox.isClickable = false
            binding.vignetteTitle.text = vignette.display
            binding.vignettePrice.text = vignette.cost.toHungarianForint()
            binding.root.setOnClickListener {
                onVignetteClick(vignette)
            }
        }
    }
}

