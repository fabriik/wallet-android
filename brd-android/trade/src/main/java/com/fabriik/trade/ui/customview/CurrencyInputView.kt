package com.fabriik.trade.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.fabriik.common.utils.DecimalDigitsInputFilter
import com.fabriik.common.utils.afterTextChangedDebounceFocused
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

        binding.etFiatAmount.filters = arrayOf(DecimalDigitsInputFilter(digitsAfterZero = 2))
        binding.etFiatAmount.afterTextChangedDebounceFocused { onFiatAmountChanged(it.toString()) }
        binding.etCryptoAmount.filters = arrayOf(DecimalDigitsInputFilter())
        binding.etCryptoAmount.afterTextChangedDebounceFocused { onCryptoAmountChanged(it.toString()) }
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
        val formatted = "%.2f".format(amount)
        val text = binding.etFiatAmount.text?.toString() ?: ""

        if (text != formatted && !binding.etFiatAmount.hasFocus()) {
            binding.etFiatAmount.setText(formatted)
        }
    }

    fun setCryptoAmount(amount: BigDecimal) {
        val formatted = "%.5f".format(amount)
        val text = binding.etCryptoAmount.text?.toString() ?: ""

        if (text != formatted && !binding.etCryptoAmount.hasFocus()) {
            binding.etCryptoAmount.setText(formatted)
        }
    }

    fun getSelectionView(): View = binding.viewCurrencySelector

    interface Callback {
        fun onCurrencySelectorClicked()
        fun onFiatAmountChanged(amount: BigDecimal)
        fun onCryptoAmountChanged(amount: BigDecimal)
    }
}
