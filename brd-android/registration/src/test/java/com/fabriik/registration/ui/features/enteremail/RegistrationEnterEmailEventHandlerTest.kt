package com.fabriik.registration.ui.features.enteremail

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RegistrationEnterEmailEventHandlerTest {

    @Spy val handler = object : RegistrationEnterEmailEventHandler {
        override fun onNextClicked() {}
        override fun onDismissClicked() {}
        override fun onEmailChanged(email: String) {}
        override fun onPromotionsClicked(isChecked: Boolean) {}
    }

    @Test
    fun handleEvent_nextClicked_callOnNextClicked() {
        handler.handleEvent(RegistrationEnterEmailContract.Event.NextClicked)
        verify(handler).onNextClicked()
    }

    @Test
    fun handleEvent_dismissClicked_callOnDismissClicked() {
        handler.handleEvent(RegistrationEnterEmailContract.Event.DismissClicked)
        verify(handler).onDismissClicked()
    }

    @Test
    fun handleEvent_promotionsClicked_callOnPromotionsClicked() {
        val granted = true
        handler.handleEvent(RegistrationEnterEmailContract.Event.PromotionsClicked(granted))
        verify(handler).onPromotionsClicked(granted)
    }

    @Test
    fun handleEvent_emailChanged_callOnEmailChanged() {
        val email = "test@test.com"
        handler.handleEvent(RegistrationEnterEmailContract.Event.EmailChanged(email))
        verify(handler).onEmailChanged(email)
    }
}