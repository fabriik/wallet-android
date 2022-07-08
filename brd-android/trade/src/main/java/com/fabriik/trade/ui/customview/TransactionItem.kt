package com.fabriik.trade.ui.customview

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.fabriik.trade.R
import com.fabriik.trade.databinding.PartialTransactionItemBinding

class TransactionItem @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private val binding: PartialTransactionItemBinding

    init {
        binding = PartialTransactionItemBinding.inflate(LayoutInflater.from(context), this)
    }

    fun setContent(transactionContent: TransactionContent) {
        with(transactionContent) {
            setStatus(status)
            setDate(date)
            setCurrency(currency)
            setValue(value)
            setDollarsValue(dollarsValue)
        }
    }

    fun setStatus(status: TransactionStatus) {
        val transactionTitle =
            if (status == TransactionStatus.PENDING) R.string.Swap_TransactionItem_Pending
            else R.string.Swap_TransactionItem_Swapped

        binding.tvTransactionTitle.text = context.getString(transactionTitle)

        when (status) {
            TransactionStatus.SWAPPED -> {
                binding.icItemBg.imageTintList =
                    ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.swap_light_green)
                    )

                binding.icItem.imageTintList =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.swap_green))

                binding.tvTransactionValue.setTextColor(context.getColor(R.color.swap_green))
            }

            else -> Unit
        }

    }

    fun setCurrency(title: String) {
        binding.tvTransactionCurrency.text = title
    }

    fun setDate(date: String) {
        binding.tvTransactionDate.text = date
    }

    fun setValue(value: String) {
        binding.tvTransactionValue.text = "+".plus(value)
    }

    fun setDollarsValue(dollarsValue: String) {
        binding.tvTransactionValueDollars.text = dollarsValue
    }
}

enum class TransactionStatus {
    PENDING,
    SWAPPED
}

data class TransactionContent(
    val status: TransactionStatus,
    val currency: String,
    val date: String,
    val value: String,
    val dollarsValue: String,
)