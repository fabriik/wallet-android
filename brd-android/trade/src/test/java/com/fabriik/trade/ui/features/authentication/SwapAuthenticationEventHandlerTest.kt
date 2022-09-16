package com.fabriik.trade.ui.features.authentication

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SwapAuthenticationEventHandlerTest {

    @Spy val handler = object : SwapAuthenticationEventHandler {
        override fun onBackClicked() {}
        override fun onDismissClicked() {}
        override fun onAuthSucceeded() {}
        override fun onAuthFailed(errorCode: Int) {}
        override fun onPinValidated(valid: Boolean) {}
    }

    @Test
    fun handleEvent_backClicked_callOnBackClicked() {
        handler.handleEvent(SwapAuthenticationContract.Event.BackClicked)
        verify(handler).onBackClicked()
    }

    @Test
    fun handleEvent_dismissClicked_callOnDismissClicked() {
        handler.handleEvent(SwapAuthenticationContract.Event.DismissClicked)
        verify(handler).onDismissClicked()
    }

    @Test
    fun handleEvent_authSucceeded_callOnAuthSucceeded() {
        handler.handleEvent(SwapAuthenticationContract.Event.AuthSucceeded)
        verify(handler).onAuthSucceeded()
    }

    @Test
    fun handleEvent_authFailed_callOnAuthFailed() {
        val errorCode = 123
        handler.handleEvent(SwapAuthenticationContract.Event.AuthFailed(errorCode))
        verify(handler).onAuthFailed(errorCode)
    }

    @Test
    fun handleEvent_pinValidated_callOnPinValidated() {
        val valid = false
        handler.handleEvent(SwapAuthenticationContract.Event.PinValidated(valid))
        verify(handler).onPinValidated(valid)
    }
}