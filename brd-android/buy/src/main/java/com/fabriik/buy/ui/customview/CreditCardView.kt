package com.fabriik.buy.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
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
        binding = ViewCreditCardBinding.inflate(LayoutInflater.from(context), this, true)
        radius = 8.dp.toFloat()
        setContentPadding(12,7,8,7)
        backgroundTintList = ContextCompat.getColorStateList(context, R.color.fabriik_dark_blue_buy)
        elevation = 0.dp.toFloat()
    }

    fun setContent(type: CardType, lastDigits: String, expirationDate: String) {
        setIcon(type.icon)
        setLastDigits(lastDigits)
        setExpirationDate(expirationDate)
    }

    private fun setIcon(icon: Int) {
        binding.icCardLogo.setImageDrawable(AppCompatResources.getDrawable(context, icon))
    }

    private fun setLastDigits(digits: String) {
        binding.tvLastDigits.text = digits
    }

    private fun setExpirationDate(date: String) {
        binding.tvDate.text = date
    }
}

enum class CardType(@DrawableRes val icon: Int) {
    VISA(R.drawable.ic_visa)
}