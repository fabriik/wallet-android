package com.fabriik.buy.ui.features.paymentmethod

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.fabriik.buy.data.model.PaymentInstrument
import com.fabriik.buy.databinding.ListItemPaymentMethodBinding

class PaymentMethodSelectionAdapter(private val callback: (PaymentInstrument) -> Unit) :
    ListAdapter<PaymentInstrument, PaymentMethodSelectionAdapter.ViewHolder>(
        CountryDiffCallback
    ) {

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
            callback = callback
        )
    }

    class ViewHolder(val binding: ListItemPaymentMethodBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PaymentInstrument, callback: (PaymentInstrument) -> Unit) {
            with(binding) {
                /*ivLogo.setImageResource(item.icon)
                tvTitle.text = item.country.name*/
                root.setOnClickListener { callback(item) }
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