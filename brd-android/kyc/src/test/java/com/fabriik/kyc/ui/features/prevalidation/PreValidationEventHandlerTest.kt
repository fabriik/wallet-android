package com.fabriik.kyc.ui.features.prevalidation

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PreValidationEventHandlerTest {

    @Spy val handler = object : PreValidationEventHandler {
        override fun onBackClicked() {}
        override fun onConfirmClicked() {}
        override fun onDismissCLicked() {}
    }

    @Test
    fun handleEvent_backClicked_callOnBackClicked() {
        handler.handleEvent(PreValidationContract.Event.BackClicked)
        verify(handler).onBackClicked()
    }

    @Test
    fun handleEvent_confirmClicked_callOnConfirmClicked() {
        handler.handleEvent(PreValidationContract.Event.ConfirmClicked)
        verify(handler).onConfirmClicked()
    }

    @Test
    fun handleEvent_dismissCLicked_callOnDismissCLicked() {
        handler.handleEvent(PreValidationContract.Event.DismissCLicked)
        verify(handler).onDismissCLicked()
    }
}