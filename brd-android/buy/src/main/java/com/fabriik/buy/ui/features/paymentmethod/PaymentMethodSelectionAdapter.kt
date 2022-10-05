package com.fabriik.buy.ui.features.paymentmethod

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fabriik.common.data.model.PaymentInstrument
import com.fabriik.buy.databinding.ListItemPaymentMethodBinding

class PaymentMethodSelectionAdapter(
    private val clickCallback: (PaymentInstrument) -> Unit,
    private val optionsClickCallback: (PaymentInstrument) -> Unit
) : ListAdapter<PaymentInstrument, PaymentMethodSelectionAdapter.ViewHolder>(CountryDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ListItemPaymentMethodBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(
            item = getItem(position),
            clickCallback = clickCallback,
            optionsClickCallback = optionsClickCallback
        )
    }

    class ViewHolder(val binding: ListItemPaymentMethodBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PaymentInstrument, clickCallback: (PaymentInstrument) -> Unit, optionsClickCallback: (PaymentInstrument) -> Unit) {
            with(binding) {
                tvDate.text = item.expiryDate
                tvCardNumber.text = item.hiddenCardNumber
                ivCardLogo.setImageResource(item.cardTypeIcon)
                root.setOnClickListener { clickCallback(item) }
                btnMore.setOnClickListener { optionsClickCallback(item) }
            }
        }
    }

    object CountryDiffCallback : DiffUtil.ItemCallback<PaymentInstrument>() {
        override fun areItemsTheSame(oldItem: PaymentInstrument, newItem: PaymentInstrument) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: PaymentInstrument, newItem: PaymentInstrument) =
            oldItem.last4Numbers == newItem.last4Numbers &&
                    oldItem.expiryMonth == newItem.expiryMonth &&
                    oldItem.expiryYear == newItem.expiryYear
    }
}