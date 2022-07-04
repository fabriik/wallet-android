package com.fabriik.trade.ui.features.assetselection

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fabriik.trade.databinding.ListItemAssetBinding

class AssetSelectionAdapter(private val callback: (AssetSelectionItem) -> Unit) :
    ListAdapter<AssetSelectionAdapter.AssetSelectionItem, AssetSelectionAdapter.ViewHolder>(
        AssetSelectionItemDiffCallback
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

    class ViewHolder(val binding: ListItemAssetBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: AssetSelectionItem, callback: (AssetSelectionItem) -> Unit) {
            with(binding) {
                //ivLogo.setImageResource(item.icon)
                //tvTitle.text = item.country.name
                root.setOnClickListener { callback(item) }
            }
        }
    }

    object AssetSelectionItemDiffCallback : DiffUtil.ItemCallback<AssetSelectionItem>() {
        override fun areItemsTheSame(oldItem: AssetSelectionItem, newItem: AssetSelectionItem) =
            oldItem.cryptoCurrencyCode == newItem.cryptoCurrencyCode

        override fun areContentsTheSame(oldItem: AssetSelectionItem, newItem: AssetSelectionItem) =
            oldItem.fiatBalance == newItem.fiatBalance &&
            oldItem.cryptoBalance == newItem.cryptoBalance &&
            oldItem.fiatCurrencyCode == newItem.fiatCurrencyCode &&
            oldItem.cryptoCurrencyCode == newItem.cryptoCurrencyCode
    }

    class AssetSelectionItem(
        val fiatBalance: String,
        val fiatCurrencyCode: String,
        val cryptoBalance: String,
        val cryptoCurrencyCode: String,
    )
}