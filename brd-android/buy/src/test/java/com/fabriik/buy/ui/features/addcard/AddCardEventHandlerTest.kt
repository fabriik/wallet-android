package com.fabriik.buy.ui.features.addcard
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddCardEventHandlerTest {

    @Spy val handler = object : AddCardEventHandler {
        override fun onBackClicked() {}
        override fun onDismissClicked() {}
        override fun onConfirmClicked() {}
        override fun onSecurityCodeInfoClicked() {}
        override fun onCardNumberChanged(cardNumber: String) {}
        override fun onSecurityCodeChanged(securityCode: String) {}
        override fun onExpirationDateChanged(expirationDate: String) {}
    }

    @Test
    fun handleEvent_backClicked_callOnBackClicked() {
        handler.handleEvent(AddCardContract.Event.BackClicked)
        verify(handler).onBackClicked()
    }

    @Test
    fun handleEvent_dismissClicked_callOnDismissClicked() {
        handler.handleEvent(AddCardContract.Event.DismissClicked)
        verify(handler).onDismissClicked()
    }

    @Test
    fun handleEvent_confirmClicked_callOnConfirmClicked() {
        handler.handleEvent(AddCardContract.Event.ConfirmClicked)
        verify(handler).onConfirmClicked()
    }

    @Test
    fun handleEvent_onDateChanged_callOnExpirationDateChanged() {
        val date = "11/22"
        handler.handleEvent(AddCardContract.Event.OnDateChanged(date))
        verify(handler).onExpirationDateChanged(date)
    }

    @Test
    fun handleEvent_onCardNumberChanged_callOnCardNumberChanged() {
        val cardNumber = "1234 1234 1234 1234"
        handler.handleEvent(AddCardContract.Event.OnCardNumberChanged(cardNumber))
        verify(handler).onCardNumberChanged(cardNumber)
    }

    @Test
    fun handleEvent_onSecurityCodeChanged_callOnSecurityCodeChanged() {
        val securityCode = "123"
        handler.handleEvent(AddCardContract.Event.OnSecurityCodeChanged(securityCode))
        verify(handler).onSecurityCodeChanged(securityCode)
    }

    @Test
    fun handleEvent_securityCodeInfoClicked_callOnSecurityCodeInfoClicked() {
        handler.handleEvent(AddCardContract.Event.SecurityCodeInfoClicked)
        verify(handler).onSecurityCodeInfoClicked()
    }
}