package com.fabriik.trade.ui.features.processing

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SwapProcessingEventHandlerTest {

    @Spy val handler = object : SwapProcessingEventHandler {
        override fun onDismissClicked() {}
        override fun onGoHomeClicked() {}
        override fun onOpenSwapDetailsClicked() {}
    }

    @Test
    fun handleEvent_goHomeClicked_callOnGoHomeClicked() {
        handler.handleEvent(SwapProcessingContract.Event.GoHomeClicked)
        verify(handler).onGoHomeClicked()
    }

    @Test
    fun handleEvent_dismissClicked_callOnDismissClicked() {
        handler.handleEvent(SwapProcessingContract.Event.DismissClicked)
        verify(handler).onDismissClicked()
    }

    @Test
    fun handleEvent_openSwapDetails_callOnOpenSwapDetailsClicked() {
        handler.handleEvent(SwapProcessingContract.Event.OpenSwapDetails)
        verify(handler).onOpenSwapDetailsClicked()
    }
}