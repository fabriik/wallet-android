package com.fabriik.buy.ui.features.timeout
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PaymentTimeoutEventHandlerTest {

    @Spy val handler = object : PaymentTimeoutEventHandler {
        override fun onTryAgainClicked() {}
    }
}