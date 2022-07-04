package com.fabriik.trade.ui.features.assetselection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fabriik.trade.databinding.ListItemAssetBinding

class AssetSelectionAdapter(private val callback: (Country) -> Unit) :
    ListAdapter<AssetSelectionAdapter.Item, AssetSelectionAdapter.ViewHolder>(
        CountryDiffCallback
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemAssetBinding.inflate(
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

    class ViewHolder(val binding: ListItemAssetBinding) : RecyclerView.ViewHolder(binding.root) {

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