package com.fabriik.trade.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.fabriik.trade.R
import com.fabriik.trade.databinding.ViewCurrencyInputBinding

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

    fun setCryptoCurrency(currency: String) {
        binding.viewCurrencySelector.setCryptoCurrency(currency)
    }

    interface Callback {
        fun onCurrencySelectorClicked()
    }
}
