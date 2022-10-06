package com.fabriik.trade.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.breadwallet.ext.isZero
import com.fabriik.common.utils.*
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

    fun setCryptoCurrency(currency: String?, iconLoadedCallback: () -> Unit = {}) {
        binding.viewCurrencySelector.setCryptoCurrency(currency, iconLoadedCallback)
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

        if (!changeByUser && amount.isZero()) {
            binding.etFiatAmount.setText("")
        } else if (text != formatted && !changeByUser) {
            binding.etFiatAmount.setText(formatted)
        }
    }

    fun setCryptoAmount(amount: BigDecimal, changeByUser: Boolean) {
        val formatted = "%.8f".format(amount)
        val text = binding.etCryptoAmount.text?.toString() ?: ""

        if (!changeByUser && amount.isZero()) {
            binding.etCryptoAmount.setText("")
        } else if (text != formatted && !changeByUser) {
            binding.etCryptoAmount.setText(formatted)
        }
    }

    fun prepareForAnimation(callback: (Int, View, View) -> Unit) {
        val selectionView = binding.viewCurrencySelector
        val selectionAnimationView = binding.viewCurrencySelectorAnimation

        val selectionViewPosition = IntArray(2)
        selectionView.getLocationOnScreen(selectionViewPosition)

        selectionAnimationView.setCryptoCurrency(selectionView.currency) {
            selectionAnimationView.isVisible = true
            selectionView.isInvisible = true

            callback(
                selectionViewPosition[1],
                selectionView,
                selectionAnimationView
            )
        }
    }

    fun getAnimatedViews() : List<View> = listOf(
        binding.tvTitle, binding.tvFiatCurrency, binding.etFiatAmount, binding.etCryptoAmount
    )

    fun setInputFieldsEnabled(enabled: Boolean) {
        binding.etFiatAmount.isEnabled = enabled
        binding.etCryptoAmount.isEnabled = enabled
        binding.tvFiatCurrency.isEnabled = enabled
    }

    fun clearCurrentInputFieldFocus() {
        binding.etFiatAmount.clearFocus()
        binding.etCryptoAmount.clearFocus()
    }

    fun hideKeyboard() {
        with(binding) {
            etCryptoAmount.hideKeyboard()
            etFiatAmount.hideKeyboard()
        }
    }

    interface Callback {
        fun onCurrencySelectorClicked()
        fun onFiatAmountChanged(amount: BigDecimal)
        fun onCryptoAmountChanged(amount: BigDecimal)
    }
}
