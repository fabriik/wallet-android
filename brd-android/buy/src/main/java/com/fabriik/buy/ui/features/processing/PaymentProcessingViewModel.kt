package com.fabriik.buy.ui.features.processing

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.toBundle

class PaymentProcessingViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<PaymentProcessingContract.State, PaymentProcessingContract.Event, PaymentProcessingContract.Effect>(
    application, savedStateHandle
) {

    private lateinit var arguments: PaymentProcessingFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = PaymentProcessingFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = PaymentProcessingContract.State(arguments.paymentReference)

    override fun handleEvent(event: PaymentProcessingContract.Event) {
        when (event) {
            PaymentProcessingContract.Event.BackToHomeClicked ->
                setEffect { PaymentProcessingContract.Effect.Dismiss }

            PaymentProcessingContract.Event.ContactSupportClicked ->
                setEffect { PaymentProcessingContract.Effect.ContactSupport }

            PaymentProcessingContract.Event.TryDifferentMethodClicked ->
                setEffect { PaymentProcessingContract.Effect.BackToBuy }

            PaymentProcessingContract.Event.PurchaseDetailsClicked -> {
                val purchaseId = currentState.paymentReference
                if (!purchaseId.isNullOrBlank()) {
                    setEffect { PaymentProcessingContract.Effect.GoToPurchaseDetails(purchaseId) }
                }
            }
        }
    }
}