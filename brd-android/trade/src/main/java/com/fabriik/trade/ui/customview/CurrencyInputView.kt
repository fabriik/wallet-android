package com.fabriik.trade.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.breadwallet.ext.isZero
import com.fabriik.common.utils.DecimalDigitsInputFilter
import com.fabriik.common.utils.disableCopyPaste
import com.fabriik.common.utils.afterTextChangedDebounceFocused
import com.fabriik.common.utils.showKeyboard
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

        binding.etFiatAmount.setOnFocusChangeListener { _, focus ->
            binding.etFiatAmount.hint = if (focus) ""  else "0.00"
            if (focus) binding.etFiatAmount.showKeyboard()
        }

        binding.etCryptoAmount.setOnFocusChangeListener { _, focus ->
            binding.etCryptoAmount.hint = if (focus) ""  else "0.00"
            if (focus) binding.etCryptoAmount.showKeyboard()
        }

        binding.etFiatAmount.disableCopyPaste()
        binding.etFiatAmount.filters = arrayOf(DecimalDigitsInputFilter(digitsAfterZero = 2))
        binding.etFiatAmount.afterTextChangedDebounceFocused { onFiatAmountChanged(it.toString()) }

        binding.etCryptoAmount.disableCopyPaste()
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

    fun setFiatAmount(amount: BigDecimal, changeByUser: Boolean) {
        val formatted = "%.2f".format(amount)
        val text = binding.etFiatAmount.text?.toString() ?: ""

        if (amount.isZero()) {
            binding.etFiatAmount.setText("")
        } else if (text != formatted && !changeByUser) {
            binding.etFiatAmount.setText(formatted)
        }
    }

    fun setCryptoAmount(amount: BigDecimal, changeByUser: Boolean) {
        val formatted = "%.5f".format(amount)
        val text = binding.etCryptoAmount.text?.toString() ?: ""

        if (amount.isZero()) {
            binding.etCryptoAmount.setText("")
        } else if (text != formatted && !changeByUser) {
            binding.etCryptoAmount.setText(formatted)
        }
    }

    fun getSelectionView(): View = binding.viewCurrencySelector

    fun getAnimatedViews() : List<View> = listOf(
        binding.tvTitle, binding.tvFiatCurrency, binding.etFiatAmount, binding.etCryptoAmount
    )

    fun setInputFieldsEnabled(enabled: Boolean) {
        binding.etFiatAmount.isEnabled = enabled
        binding.etCryptoAmount.isEnabled = enabled
        binding.tvFiatCurrency.isEnabled = enabled
    }

    interface Callback {
        fun onCurrencySelectorClicked()
        fun onFiatAmountChanged(amount: BigDecimal)
        fun onCryptoAmountChanged(amount: BigDecimal)
    }
}
