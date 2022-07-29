package com.fabriik.trade.ui.features.assetselection

import android.annotation.SuppressLint
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fabriik.trade.databinding.ListItemAssetBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.parcelize.Parcelize

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

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        holder.unbind()
    }

    inner class ViewHolder(val binding: ListItemAssetBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val boundScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        @SuppressLint("SetTextI18n")
        fun bind(item: AssetSelectionItem, callback: (AssetSelectionItem) -> Unit) {
            with(binding) {
                ivLogo.loadIcon(boundScope, item.cryptoCurrencyCode)
                tvTitle.text = item.title
                tvSubtitle.text = item.cryptoCurrencyCode
                tvFiatAmount.text = item.fiatBalance
                tvCryptoAmount.text = item.cryptoBalance

                if (item.enabled) {
                    setAlpha(binding, 1f)
                    binding.root.setOnClickListener { callback(item) }
                } else {
                    setAlpha(binding, 0.5f)
                    binding.root.setOnClickListener(null)
                }
            }
        }

        fun unbind() {
            boundScope.coroutineContext.cancelChildren()
        }
    }

    object AssetSelectionItemDiffCallback : DiffUtil.ItemCallback<AssetSelectionItem>() {
        override fun areItemsTheSame(oldItem: AssetSelectionItem, newItem: AssetSelectionItem) =
            oldItem.cryptoCurrencyCode == newItem.cryptoCurrencyCode

        override fun areContentsTheSame(oldItem: AssetSelectionItem, newItem: AssetSelectionItem) =
            oldItem.fiatBalance == newItem.fiatBalance &&
                    oldItem.title == newItem.title &&
                    oldItem.subtitle == newItem.subtitle &&
                    oldItem.cryptoBalance == newItem.cryptoBalance &&
                    oldItem.cryptoCurrencyCode == newItem.cryptoCurrencyCode
    }

    @Parcelize
    class AssetSelectionItem(
        val title: String,
        val subtitle: String,
        val fiatBalance: String?,
        val cryptoBalance: String?,
        val cryptoCurrencyCode: String,
        val enabled: Boolean
    ) : Parcelable

    private fun setAlpha(binding: ListItemAssetBinding, alpha: Float) {
        with(binding) {
            ivLogo.alpha = alpha
            tvCryptoAmount.alpha = alpha
            tvSubtitle.alpha = alpha
            tvTitle.alpha = alpha
            tvFiatAmount.alpha = alpha
        }
    }
}