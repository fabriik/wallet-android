package com.fabriik.kyc.ui.features.countryselection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fabriik.kyc.data.model.Country
import com.fabriik.kyc.databinding.ListItemCountryBinding

class CountrySelectionAdapter(private val callback: (Country) -> Unit) :
    ListAdapter<CountrySelectionAdapter.Item, CountrySelectionAdapter.ViewHolder>(
        CountryDiffCallback
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemCountryBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            item = getItem(position),
            callback = callback
        )
    }

    data class Item(
        val icon: Int,
        val country: Country
    )

    class ViewHolder(val binding: ListItemCountryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item, callback: (Country) -> Unit) {
            with(binding) {
                ivLogo.setImageResource(item.icon)
                tvTitle.text = item.country.name
                root.setOnClickListener { callback(item.country) }
            }
        }
    }

    object CountryDiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item) =
            oldItem.country.code == newItem.country.code

        override fun areContentsTheSame(oldItem: Item, newItem: Item) =
            oldItem.country.name == newItem.country.name
    }
}