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
}