package com.fabriik.trade.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.navArgs
import com.breadwallet.breadbox.formatCryptoForUi
import com.breadwallet.util.formatFiatForUi
import com.fabriik.trade.data.model.AmountData
import com.fabriik.trade.databinding.FragmentSwapConfirmationDialogBinding

class SwapConfirmationDialog : DialogFragment() {

    private val args by navArgs<SwapConfirmationDialogArgs>()

    private lateinit var binding: FragmentSwapConfirmationDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSwapConfirmationDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            tvToValue.text = formatCurrencyAmount(args.toAmount)
            tvFromValue.text = formatCurrencyAmount(args.fromAmount)

            tvRateValue.text = RATE_FORMAT.format(
                args.fromAmount.cryptoCurrency,
                args.rateAmount.formatCryptoForUi(
                    args.toAmount.cryptoCurrency
                )
            )

            args.sendingFeeAmount.run {
                tvSendingFeeFiatValue.text = fiatAmount.formatFiatForUi(
                    fiatCurrency
                )

                tvSendingFeeCryptoValue.text = cryptoAmount.formatCryptoForUi(
                    cryptoCurrency
                )
            }

            args.receivingFeeAmount.run {
                tvReceivingFeeFiatValue.text = fiatAmount.formatFiatForUi(
                    fiatCurrency
                )

                tvReceivingFeeCryptoValue.text = cryptoAmount.formatCryptoForUi(
                    cryptoCurrency
                )
            }

            tvTotalValue.text = args.fromAmount.cryptoAmount.formatCryptoForUi(
                args.fromAmount.cryptoCurrency
            )

            btnCancel.setOnClickListener {
                notifyListener(RESULT_CANCEL)
            }

            btnConfirm.setOnClickListener {
                notifyListener(RESULT_CONFIRM)
            }
        }
    }

    private fun notifyListener(result: String) {
        dismissAllowingStateLoss()

        parentFragmentManager.setFragmentResult(
            args.requestKey, bundleOf(EXTRA_RESULT to result)
        )
    }

    private fun formatCurrencyAmount(data: AmountData): String {
        val fiatText = data.fiatAmount.formatFiatForUi(
            data.fiatCurrency
        )

        val cryptoText = data.cryptoAmount.formatCryptoForUi(
            data.cryptoCurrency
        )

        return "$cryptoText ($fiatText)"
    }

    companion object {
        const val RATE_FORMAT = "1 %s = %s"
        const val RESULT_CONFIRM = "res_confirm"
        const val RESULT_CANCEL = "res_cancel"
        const val EXTRA_RESULT = "extra_result"
    }
}