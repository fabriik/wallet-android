package com.fabriik.trade.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.fabriik.trade.R
import com.fabriik.trade.databinding.FragmentConfirmationDialogBinding
import java.math.BigDecimal

class ConfirmationDialog(val args: ConfirmationArgs) : DialogFragment() {

    companion object {
        const val CONFIRMATION_TAG = "Confirmation_dialog"
    }

    lateinit var binding: FragmentConfirmationDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfirmationDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            tvFromValue.text = formatCurrencyText(args.swapFromCurrency)
            tvToValue.text = formatCurrencyText(args.swapToCurrency)

            tvOriginCoinValue.text = args.swapFromCurrency.amount.toString()
            tvOriginFiatValue.text = args.swapFromCurrency.fiatValue.toString()

            tvDestinationCoinValue.text = args.swapToCurrency.amount.toString()
            tvDestinationFiatValue.text = args.swapToCurrency.fiatValue.toString()

            tvTotalValue.text = args.totalCost
            tvRateValue.text = args.rate

            btnDismiss.setOnClickListener {
                dialog?.dismiss()
            }

            btnCancel.setOnClickListener {
                dialog?.dismiss()
            }
        }
    }

    private fun formatCurrencyText(currency: Currency): String {
        return getString(
            R.string.Swap_Confirmation_Currency_Body,
            currency.amount,
            currency.title,
            currency.fiatValue
        )
    }
}

data class ConfirmationArgs(
    val swapFromCurrency: Currency,
    val swapToCurrency: Currency,
    val sendingFeeCurrency: Currency,
    val receivingFeeCurrency: Currency,
    val rate: String,
    val totalCost: String,
)

data class Currency(
    val title: String,
    val amount: BigDecimal,
    val fiatValue: BigDecimal
)