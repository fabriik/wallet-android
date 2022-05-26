package com.fabriik.kyc.ui.features.countryselection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fabriik.kyc.data.model.Country
import com.fabriik.kyc.databinding.ListItemCountryBinding

class CountrySelectionAdapter(val callback: (Country) -> Unit) :
    ListAdapter<Country, CountrySelectionAdapter.ViewHolder>(CountryDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemCountryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(val binding: ListItemCountryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Country) {
            binding.tvTitle.text = item.name
        }
    }

    object CountryDiffCallback : DiffUtil.ItemCallback<Country>() {
        override fun areItemsTheSame(oldItem: Country, newItem: Country) =
            oldItem.code == newItem.code

        override fun areContentsTheSame(oldItem: Country, newItem: Country) =
            oldItem.name == newItem.name
    }
}