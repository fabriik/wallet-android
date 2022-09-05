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

    @Test
    fun handleEvent_loadData_callOnLoadData() {
        handler.handleEvent(BuyDetailsContract.Event.LoadData)
        verify(handler).onLoadData()
    }

    @Test
    fun handleEvent_backClicked_callOnBackClicked() {
        handler.handleEvent(BuyDetailsContract.Event.BackClicked)
        verify(handler).onBackClicked()
    }

    @Test
    fun handleEvent_dismissClicked_callOnDismissClicked() {
        handler.handleEvent(BuyDetailsContract.Event.DismissClicked)
        verify(handler).onDismissClicked()
    }

    @Test
    fun handleEvent_orderIdClicked_callOnOrderIdClicked() {
        handler.handleEvent(BuyDetailsContract.Event.OrderIdClicked)
        verify(handler).onOrderIdClicked()
    }

    @Test
    fun handleEvent_transactionIdClicked_callOnTransactionIdClicked() {
        handler.handleEvent(BuyDetailsContract.Event.TransactionIdClicked)
        verify(handler).onTransactionIdClicked()
    }
}