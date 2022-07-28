package com.fabriik.buy.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import com.fabriik.buy.R
import com.fabriik.buy.databinding.ViewCreditCardBinding
import com.fabriik.common.utils.dp
import com.google.android.material.card.MaterialCardView

class CreditCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null

) : MaterialCardView(context, attrs) {

    private val binding: ViewCreditCardBinding

    init {
        radius = 16.dp.toFloat()
        binding = ViewCreditCardBinding.inflate(LayoutInflater.from(context), this, true)
        backgroundTintList = ContextCompat.getColorStateList(context, R.color.fabriik_dark_blue_buy)
    }

    fun setLastDigits(digits: String) {
        binding.tvLastDigits.text = digits
    }

    fun setExpirationDate(date: String) {
        binding.tvLastDigits.text = date
    }
}