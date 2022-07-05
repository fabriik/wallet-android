package com.fabriik.trade.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import com.breadwallet.breadbox.formatCryptoForUi
import com.fabriik.trade.R
import com.fabriik.trade.databinding.ViewCurrencyInputBinding
import java.math.BigDecimal

class CurrencyInputView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding: ViewCurrencyInputBinding
    private var callback: Callback? = null

    init {
        binding = ViewCurrencyInputBinding.inflate(
            LayoutInflater.from(context), this
        )

        context.obtainStyledAttributes(attrs, R.styleable.CurrencyInputView).let {
            setTitle(it.getString(R.styleable.CurrencyInputView_title))
            it.recycle()
        }

        binding.etFiatAmount.doAfterTextChanged { onFiatAmountChanged(it.toString()) }
        binding.etCryptoAmount.doAfterTextChanged { onCryptoAmountChanged(it.toString()) }
        binding.viewCurrencySelector.setOnClickListener { callback?.onCurrencySelectorClicked() }
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun setTitle(title: String?) {
        binding.tvTitle.text = title
    }

    fun setFiatCurrency(currency: String) {
        binding.tvFiatCurrency.text = currency
    }

    fun setCryptoCurrency(currency: String?) {
        currency?.let { binding.viewCurrencySelector.setCryptoCurrency(it) }
    }

    private fun onFiatAmountChanged(value: String) {
        callback?.onFiatAmountChanged(value.trim().toBigDecimalOrNull() ?: BigDecimal.ZERO)
    }

    private fun onCryptoAmountChanged(value: String) {
        callback?.onCryptoAmountChanged(value.trim().toBigDecimalOrNull() ?: BigDecimal.ZERO)
    }

    fun setFiatAmount(amount: BigDecimal) {
        binding.etFiatAmount.setText(
            amount.formatCryptoForUi("")
        )
    }

    fun setCryptoAmount(amount: BigDecimal) {
        binding.etCryptoAmount.setText(
            amount.formatCryptoForUi("")
        )
    }

    interface Callback {
        fun onCurrencySelectorClicked()
        fun onFiatAmountChanged(amount: BigDecimal)
        fun onCryptoAmountChanged(amount: BigDecimal)
    }
}
