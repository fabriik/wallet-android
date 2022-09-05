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
}