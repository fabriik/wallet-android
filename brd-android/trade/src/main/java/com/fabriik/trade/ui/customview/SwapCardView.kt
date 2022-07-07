package com.fabriik.trade.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.view.isVisible
import com.breadwallet.breadbox.formatCryptoForUi
import com.breadwallet.util.formatFiatForUi
import com.fabriik.common.utils.dp
import com.fabriik.trade.databinding.ViewSwapCardBinding
import com.fabriik.trade.ui.features.swap.SwapInputContract
import com.google.android.material.card.MaterialCardView
import java.math.BigDecimal

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

                override fun onFiatAmountChanged(amount: BigDecimal) {
                    callback?.onBuyingCurrencyFiatAmountChanged(amount)
                }

                override fun onCryptoAmountChanged(amount: BigDecimal) {
                    callback?.onBuyingCurrencyCryptoAmountChanged(amount)
                }
            })

            viewInputSellingCurrency.setCallback(object : CurrencyInputView.Callback {
                override fun onCurrencySelectorClicked() {
                    callback?.onSellingCurrencySelectorClicked()
                }

                override fun onFiatAmountChanged(amount: BigDecimal) {
                    callback?.onSellingCurrencyFiatAmountChanged(amount)
                }

                override fun onCryptoAmountChanged(amount: BigDecimal) {
                    callback?.onSellingCurrencyCryptoAmountChanged(amount)
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

    fun setOriginCurrency(currency: String?) {
        binding.viewInputSellingCurrency.setCryptoCurrency(currency)
    }

    fun setDestinationCurrency(currency: String?) {
        binding.viewInputBuyingCurrency.setCryptoCurrency(currency)
    }

    fun setSendingNetworkFee(fee: SwapInputContract.NetworkFeeData?) {
        binding.tvSellingCurrencyNetworkFee.isVisible = fee != null
        binding.tvSellingCurrencyNetworkFeeTitle.isVisible = fee != null

        fee?.let {
            val fiatText = it.fiatAmount.formatFiatForUi(it.fiatCurrency)
            val cryptoText = it.cryptoAmount.formatCryptoForUi(it.cryptoCurrency)
            binding.tvSellingCurrencyNetworkFee.text = "$cryptoText\n$fiatText"
        }
    }

    fun setReceivingNetworkFee(fee: SwapInputContract.NetworkFeeData?) {
        binding.tvBuyingCurrencyNetworkFee.isVisible = fee != null
        binding.tvBuyingCurrencyNetworkFeeTitle.isVisible = fee != null

        fee?.let {
            val fiatText = it.fiatAmount.formatFiatForUi(it.fiatCurrency)
            val cryptoText = it.cryptoAmount.formatCryptoForUi(it.cryptoCurrency)
            binding.tvBuyingCurrencyNetworkFee.text = "$cryptoText\n$fiatText"
        }
    }

    fun setSellingCurrencyTitle(title: String) {
        binding.viewInputSellingCurrency.setTitle(title)
    }

    fun setSourceFiatAmount(amount: BigDecimal) {
        binding.viewInputSellingCurrency.setFiatAmount(amount)
    }

    fun setSourceCryptoAmount(amount: BigDecimal) {
        binding.viewInputSellingCurrency.setCryptoAmount(amount)
    }

    fun setDestinationFiatAmount(amount: BigDecimal) {
        binding.viewInputBuyingCurrency.setFiatAmount(amount)
    }

    fun setDestinationCryptoAmount(amount: BigDecimal) {
        binding.viewInputBuyingCurrency.setCryptoAmount(amount)
    }

    interface Callback {
        fun onReplaceCurrenciesClicked()
        fun onBuyingCurrencySelectorClicked()
        fun onSellingCurrencySelectorClicked()
        fun onSellingCurrencyFiatAmountChanged(amount: BigDecimal)
        fun onSellingCurrencyCryptoAmountChanged(amount: BigDecimal)
        fun onBuyingCurrencyFiatAmountChanged(amount: BigDecimal)
        fun onBuyingCurrencyCryptoAmountChanged(amount: BigDecimal)
    }
}
