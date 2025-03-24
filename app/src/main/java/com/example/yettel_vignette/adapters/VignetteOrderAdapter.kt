package com.example.yettel_vignette.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yettel_vignette.databinding.VignetteOrderItemBinding
import com.example.yettel_vignette.extensions.toHungarianForint
import com.example.yettel_vignette.models.HighwayVignette

class VignetteOrderAdapter(
    private val vignettes: List<HighwayVignette>,
) : RecyclerView.Adapter<VignetteOrderAdapter.VignetteOrderViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VignetteOrderViewHolder {
        val binding =
            VignetteOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VignetteOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VignetteOrderViewHolder, position: Int) {
        val vignette = vignettes[position]
        holder.bind(vignette)
    }

    override fun getItemCount(): Int = vignettes.size

    override fun getItemViewType(position: Int): Int {
        return position
    }

    inner class VignetteOrderViewHolder(
        private val binding: VignetteOrderItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(vignette: HighwayVignette) {
            binding.vignetteLabel.text = vignette.display
            binding.vignettePrice.text = vignette.cost.toHungarianForint()
        }
    }
}