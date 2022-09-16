package com.fabriik.trade.ui.features.failed

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SwapFailedEventHandlerTest {

    @Spy val handler = object : SwapFailedEventHandler {
        override fun onGoHomeClicked() {}
        override fun onSwapAgainClicked() {}
    }

    @Test
    fun handleEvent_goHomeClicked_callOnGoHomeClicked() {
        handler.handleEvent(SwapFailedContract.Event.GoHomeClicked)
        verify(handler).onGoHomeClicked()
    }

    @Test
    fun handleEvent_swapAgainClicked_callOnSwapAgainClicked() {
        handler.handleEvent(SwapFailedContract.Event.SwapAgainClicked)
        verify(handler).onSwapAgainClicked()
    }
}