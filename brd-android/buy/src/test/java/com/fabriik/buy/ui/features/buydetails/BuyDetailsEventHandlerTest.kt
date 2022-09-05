package com.fabriik.buy.ui.features.buydetails
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BuyDetailsEventHandlerTest {

    @Spy val handler = object : BuyDetailsEventHandler {
        override fun onLoadData() {}
        override fun onBackClicked() {}
        override fun onDismissClicked() {}
        override fun onOrderIdClicked() {}
        override fun onTransactionIdClicked() {}
    }
}