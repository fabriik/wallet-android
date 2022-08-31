package com.fabriik.buy.ui.features.processing

import com.fabriik.common.ui.base.FabriikEventHandler

interface PaymentProcessingEventHandler: FabriikEventHandler<PaymentProcessingContract.Event> {

    override fun handleEvent(event: PaymentProcessingContract.Event) {
        return when (event) {
            PaymentProcessingContract.Event.BackToHomeClicked -> onBackToHomeClicked()
            PaymentProcessingContract.Event.ContactSupportClicked -> onContactSupportClicked()
            PaymentProcessingContract.Event.PurchaseDetailsClicked -> onPurchaseDetailsClicked()
            PaymentProcessingContract.Event.OnPaymentRedirectResult -> onPaymentRedirectResult()
            PaymentProcessingContract.Event.TryDifferentMethodClicked -> onTryDifferentMethodClicked()
        }
    }

    fun onBackToHomeClicked()

    fun onContactSupportClicked()

    fun onPurchaseDetailsClicked()

    fun onTryDifferentMethodClicked()

    fun onPaymentRedirectResult()
}