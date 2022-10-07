package com.fabriik.trade.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.view.setPadding
import com.fabriik.common.utils.dp
import com.fabriik.common.utils.viewScope
import com.fabriik.trade.databinding.ViewCurrencySelectorBinding

class CurrencySelectorView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    var currency: String? = null
    private val binding: ViewCurrencySelectorBinding

    init {
        setPadding(8.dp)
        orientation = HORIZONTAL
        binding = ViewCurrencySelectorBinding.inflate(
            LayoutInflater.from(context), this
        )
    }

    fun setCryptoCurrency(currency: String?, iconLoadedCallback: () -> Unit = {}) {
        this.currency = currency
        if (currency == null) return

        binding.root.post {
            binding.tvCurrency.text = currency
            binding.viewIcon.loadIcon(
                callback = iconLoadedCallback,
                currencyCode = currency,
                scope = this@CurrencySelectorView.viewScope
            )
        }
    }

    override fun getBaseline() = binding.tvCurrency.baseline
}