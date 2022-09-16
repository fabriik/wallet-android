package com.fabriik.buy.ui.features.processing
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PaymentProcessingEventHandlerTest {

    @Spy val handler = object : PaymentProcessingEventHandler {
        override fun onBackToHomeClicked() {}
        override fun onContactSupportClicked() {}
        override fun onPurchaseDetailsClicked() {}
        override fun onTryDifferentMethodClicked() {}
        override fun onPaymentRedirectResult() {}
    }

    @Test
    fun handleEvent_backToHomeClicked_callOnBackToHomeClicked() {
        handler.handleEvent(PaymentProcessingContract.Event.BackToHomeClicked)
        verify(handler).onBackToHomeClicked()
    }

    @Test
    fun handleEvent_contactSupportClicked_callOnContactSupportClicked() {
        handler.handleEvent(PaymentProcessingContract.Event.ContactSupportClicked)
        verify(handler).onContactSupportClicked()
    }

    @Test
    fun handleEvent_purchaseDetailsClicked_callOnPurchaseDetailsClicked() {
        handler.handleEvent(PaymentProcessingContract.Event.PurchaseDetailsClicked)
        verify(handler).onPurchaseDetailsClicked()
    }

    @Test
    fun handleEvent_tryDifferentMethodClicked_callOnTryDifferentMethodClicked() {
        handler.handleEvent(PaymentProcessingContract.Event.TryDifferentMethodClicked)
        verify(handler).onTryDifferentMethodClicked()
    }

    @Test
    fun handleEvent_onPaymentRedirectResult_callOnPaymentRedirectResult() {
        handler.handleEvent(PaymentProcessingContract.Event.OnPaymentRedirectResult)
        verify(handler).onPaymentRedirectResult()
    }
}