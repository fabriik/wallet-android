package com.fabriik.trade.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.fabriik.common.utils.dp
import com.fabriik.trade.databinding.ViewSwapCardBinding
import com.google.android.material.card.MaterialCardView

class SwapCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {

    private val binding: ViewSwapCardBinding
    private var callback: Callback? = null

    init {
        radius = 16.dp.toFloat()
        binding = ViewSwapCardBinding.inflate(
            LayoutInflater.from(context), this, true
        )

        with(binding) {
            btnSwap.setOnClickListener { callback?.onReplaceCurrenciesClicked() }

            viewInputBuyingCurrency.setCallback(object : CurrencyInputView.Callback {
                override fun onCurrencySelectorClicked() {
                    callback?.onBuyingCurrencySelectorClicked()
                }
            })

            viewInputSellingCurrency.setCallback(object : CurrencyInputView.Callback {
                override fun onCurrencySelectorClicked() {
                    callback?.onSellingCurrencySelectorClicked()
                }
            })
        }
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun setFiatCurrency(currency: String) {
        binding.viewInputBuyingCurrency.setFiatCurrency(currency)
        binding.viewInputSellingCurrency.setFiatCurrency(currency)
    }

    fun setOriginCurrency(currency: String) {
        binding.viewInputSellingCurrency.setCryptoCurrency(currency)
    }

    fun setDestinationCurrency(currency: String) {
        binding.viewInputBuyingCurrency.setCryptoCurrency(currency)
    }

    fun setSendingNetworkFee(feeText: String?) {
        binding.tvSellingCurrencyNetworkFee.text = feeText
        binding.tvSellingCurrencyNetworkFee.isVisible = feeText != null
        binding.tvSellingCurrencyNetworkFeeTitle.isVisible = feeText != null
    }

    fun setReceivingNetworkFee(feeText: String?) {
        binding.tvBuyingCurrencyNetworkFee.text = feeText
        binding.tvBuyingCurrencyNetworkFee.isVisible = feeText != null
        binding.tvBuyingCurrencyNetworkFeeTitle.isVisible = feeText != null
    }

    fun setSellingCurrencyTitle(title: String) {
        binding.viewInputSellingCurrency.setTitle(title)
    }

    interface Callback {
        fun onReplaceCurrenciesClicked()
        fun onBuyingCurrencySelectorClicked()
        fun onSellingCurrencySelectorClicked()
    }
}
