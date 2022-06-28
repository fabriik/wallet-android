package com.fabriik.trade.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.fabriik.trade.databinding.ViewCurrencyInputBinding

class CurrencyInputView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding: ViewCurrencyInputBinding

    init {
        binding = ViewCurrencyInputBinding.inflate(
            LayoutInflater.from(context), this
        )
    }
}
