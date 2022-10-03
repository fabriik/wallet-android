package com.fabriik.buy.ui.features.paymentmethod

import android.view.LayoutInflater
import android.view.ViewGroup
import com.fabriik.buy.databinding.BottomSheetPaymentMethodOptionsBinding
import com.fabriik.common.ui.dialog.FabriikBottomSheet

class PaymentMethodOptionsBottomSheet: FabriikBottomSheet<BottomSheetPaymentMethodOptionsBinding>() {

    override fun createBinding(inflater: LayoutInflater, container: ViewGroup?, attach: Boolean) =
        BottomSheetPaymentMethodOptionsBinding.inflate(inflater, container, attach)

    override fun setupBottomSheet() {
        binding.tvCancel.setOnClickListener { dismissWithResult(REQUEST_KEY, RESULT_KEY_CANCEL) }
        binding.tvRemove.setOnClickListener { dismissWithResult(REQUEST_KEY, RESULT_KEY_REMOVE) }
    }

    companion object {
        const val REQUEST_KEY = "PaymentMethodOptionsBottomSheet"
        const val RESULT_KEY_CANCEL = "PaymentMethodOptionsBottomSheet"
        const val RESULT_KEY_REMOVE = "PaymentMethodOptionsBottomSheet"
    }
}