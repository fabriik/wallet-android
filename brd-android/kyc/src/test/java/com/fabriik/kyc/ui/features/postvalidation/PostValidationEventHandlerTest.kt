package com.fabriik.kyc.ui.features.postvalidation

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PostValidationEventHandlerTest {

    @Spy val handler = object : PostValidationEventHandler {
        override fun onConfirmClicked() {}
    }

    @Test
    fun handleEvent_confirmClicked_callOnConfirmClicked() {
        handler.handleEvent(PostValidationContract.Event.ConfirmClicked)
        verify(handler).onConfirmClicked()
    }
}