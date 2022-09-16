package com.fabriik.trade.ui.features.swapdetails

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SwapDetailsEventHandlerTest {

    @Spy val handler = object : SwapDetailsEventHandler {
        override fun onLoadData() { }
        override fun onDismissClicked() {}
        override fun onOrderIdClicked() {}
        override fun onSourceTransactionIdClicked() {}
        override fun onDestinationTransactionIdClicked() {}
    }

    @Test
    fun handleEvent_loadData_callOnLoadData() {
        handler.handleEvent(SwapDetailsContract.Event.LoadData)
        verify(handler).onLoadData()
    }

    @Test
    fun handleEvent_dismissClicked_callOnDismissClicked() {
        handler.handleEvent(SwapDetailsContract.Event.DismissClicked)
        verify(handler).onDismissClicked()
    }

    @Test
    fun handleEvent_orderIdClicked_callOnOrderIdClicked() {
        handler.handleEvent(SwapDetailsContract.Event.OrderIdClicked)
        verify(handler).onOrderIdClicked()
    }

    @Test
    fun handleEvent_sourceTransactionIdClicked_callOnSourceTransactionIdClicked() {
        handler.handleEvent(SwapDetailsContract.Event.SourceTransactionIdClicked)
        verify(handler).onSourceTransactionIdClicked()
    }

    @Test
    fun handleEvent_destinationTransactionIdClicked_callOnDestinationTransactionIdClicked() {
        handler.handleEvent(SwapDetailsContract.Event.DestinationTransactionIdClicked)
        verify(handler).onDestinationTransactionIdClicked()
    }
}