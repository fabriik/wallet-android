package com.fabriik.trade.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.breadwallet.breadbox.formatCryptoForUi
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

            tvOriginCoinValue.text = args.swapFromCurrency.amount.formatCryptoForUi(
                args.swapFromCurrency.title
            )
            tvOriginFiatValue.text = getString(
                R.string.swap_Confirmation_ValueInDollars,
                args.swapFromCurrency.fiatValue
            )

            tvDestinationCoinValue.text = args.swapToCurrency.amount.formatCryptoForUi(
                args.swapToCurrency.title
            )
            tvDestinationFiatValue.text = getString(
                R.string.swap_Confirmation_ValueInDollars,
                args.swapToCurrency.fiatValue
            )

            tvTotalValue.text = args.totalCost.formatCryptoForUi(args.swapFromCurrency.title)
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
    val totalCost: BigDecimal,
)

data class Currency(
    val title: String,
    val amount: BigDecimal,
    val fiatValue: BigDecimal
)