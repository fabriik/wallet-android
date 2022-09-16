package com.fabriik.registration.ui.features.verifyemail

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class RegistrationVerifyEmailEventHandlerTest {

    @Spy val handler = object : RegistrationVerifyEmailEventHandler {
        override fun onConfirmClicked() {}
        override fun onDismissClicked() {}
        override fun onChangeEmailClicked() {}
        override fun onResendEmailClicked() {}
        override fun onCodeChanged(code: String) {}
    }

    @Test
    fun handleEvent_confirmClicked_callOnNextClicked() {
        handler.handleEvent(RegistrationVerifyEmailContract.Event.ConfirmClicked)
        verify(handler).onConfirmClicked()
    }

    @Test
    fun handleEvent_dismissClicked_callOnDismissClicked() {
        handler.handleEvent(RegistrationVerifyEmailContract.Event.DismissClicked)
        verify(handler).onDismissClicked()
    }

    @Test
    fun handleEvent_resendEmailClicked_callOnResendEmailClicked() {
        handler.handleEvent(RegistrationVerifyEmailContract.Event.ResendEmailClicked)
        verify(handler).onResendEmailClicked()
    }

    @Test
    fun handleEvent_changeEmailClicked_callOnChangeEmailClicked() {
        handler.handleEvent(RegistrationVerifyEmailContract.Event.ChangeEmailClicked)
        verify(handler).onChangeEmailClicked()
    }

    @Test
    fun handleEvent_codeChanged_callOnCodeChanged() {
        val code = "123456"
        handler.handleEvent(RegistrationVerifyEmailContract.Event.CodeChanged(code))
        verify(handler).onCodeChanged(code)
    }
}