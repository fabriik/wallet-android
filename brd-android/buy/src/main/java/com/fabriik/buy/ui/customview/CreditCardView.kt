package com.fabriik.buy.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.fabriik.common.data.model.PaymentInstrument
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

    fun setPaymentInstrument(instrument: PaymentInstrument) {
        with(binding) {
            tvDate.text = instrument.expiryDate
            tvCardNumber.text = instrument.hiddenCardNumber
            ivCardLogo.setImageResource(instrument.cardTypeIcon)
        }
    }
}

