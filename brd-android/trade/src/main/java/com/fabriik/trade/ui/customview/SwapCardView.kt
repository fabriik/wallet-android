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

    init {
        radius = 16.dp.toFloat()
        binding = ViewSwapCardBinding.inflate(
            LayoutInflater.from(context), this, true
        )
    }
}
