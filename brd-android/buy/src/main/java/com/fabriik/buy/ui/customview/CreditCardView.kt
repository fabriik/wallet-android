package com.fabriik.buy.ui.customview

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.fabriik.buy.data.enums.CardType
import com.fabriik.buy.databinding.ViewCreditCardBinding
import com.fabriik.common.utils.dp

class CreditCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding: ViewCreditCardBinding

    init {
        binding = ViewCreditCardBinding.inflate(LayoutInflater.from(context), this)
        setPaddingRelative(12.dp, 8.dp, 12.dp, 8.dp)
    }

    fun setData(type: CardType, lastDigits: String, expirationDate: String) {
        setIcon(type.icon)
        setLastDigits(lastDigits)
        setExpirationDate(expirationDate)
    }

    private fun setIcon(icon: Int) {
        binding.ivCardLogo.setImageDrawable(
            ContextCompat.getDrawable(context, icon)
        )
    }

    @SuppressLint("SetTextI18n")
    private fun setLastDigits(digits: String) {
        binding.tvCardNumber.text = "**** **** **** $digits"
    }

    private fun setExpirationDate(date: String) {
        binding.tvDate.text = date
    }
}

