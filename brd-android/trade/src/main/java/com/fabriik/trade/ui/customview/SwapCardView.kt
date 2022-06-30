package com.fabriik.trade.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
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
        binding.viewInputSellingCurrency.setFiatCurrency(currency)
    }

    fun setOriginCurrency(currency: String) {
        binding.viewInputSellingCurrency.setCryptoCurrency(currency)
    }

    fun setDestinationCurrency(currency: String) {
        binding.viewInputBuyingCurrency.setCryptoCurrency(currency)
    }

    interface Callback {
        fun onReplaceCurrenciesClicked()
        fun onBuyingCurrencySelectorClicked()
        fun onSellingCurrencySelectorClicked()
    }
}
