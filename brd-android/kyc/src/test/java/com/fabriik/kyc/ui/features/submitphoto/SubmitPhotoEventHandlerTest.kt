package com.fabriik.kyc.ui.features.submitphoto

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SubmitPhotoEventHandlerTest {

    @Spy val handler = object : SubmitPhotoEventHandler {
        override fun onBackClicked() {}
        override fun onRetakeClicked() {}
        override fun onConfirmClicked() {}
        override fun onDismissClicked() {}
    }

    @Test
    fun handleEvent_backClicked_callOnBackClicked() {
        handler.handleEvent(SubmitPhotoContract.Event.BackClicked)
        verify(handler).onBackClicked()
    }

    @Test
    fun handleEvent_retakeClicked_callOnRetakeClicked() {
        handler.handleEvent(SubmitPhotoContract.Event.RetakeClicked)
        verify(handler).onRetakeClicked()
    }

    @Test
    fun handleEvent_confirmClicked_callOnConfirmClicked() {
        handler.handleEvent(SubmitPhotoContract.Event.ConfirmClicked)
        verify(handler).onConfirmClicked()
    }

    @Test
    fun handleEvent_dismissClicked_callOnDismissClicked() {
        handler.handleEvent(SubmitPhotoContract.Event.DismissClicked)
        verify(handler).onDismissClicked()
    }
}