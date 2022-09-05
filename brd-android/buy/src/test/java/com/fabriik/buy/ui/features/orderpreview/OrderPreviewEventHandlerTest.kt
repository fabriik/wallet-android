package com.fabriik.buy.ui.features.orderpreview
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class OrderPreviewEventHandlerTest {

    @Spy val handler = object : OrderPreviewEventHandler {
        override fun onBackClicked() {}
        override fun onDismissClicked() {}
        override fun onConfirmClicked() {}
        override fun onCreditInfoClicked() {}
        override fun onNetworkInfoClicked() {}
        override fun onSecurityCodeInfoClicked() {}
        override fun onTermsAndConditionsClicked() {}
        override fun onUserAuthenticationSucceed() {}
        override fun onSecurityCodeChanged(securityCode: String) {}
    }

    @Test
    fun handleEvent_onBackPressed_callOnBackClicked() {
        handler.handleEvent(OrderPreviewContract.Event.OnBackPressed)
        verify(handler).onBackClicked()
    }

    @Test
    fun handleEvent_onDismissClicked_callOnDismissClicked() {
        handler.handleEvent(OrderPreviewContract.Event.OnDismissClicked)
        verify(handler).onDismissClicked()
    }

    @Test
    fun handleEvent_onConfirmClicked_callOnConfirmClicked() {
        handler.handleEvent(OrderPreviewContract.Event.OnConfirmClicked)
        verify(handler).onConfirmClicked()
    }

    @Test
    fun handleEvent_onCreditInfoClicked_callOnCreditInfoClicked() {
        handler.handleEvent(OrderPreviewContract.Event.OnCreditInfoClicked)
        verify(handler).onCreditInfoClicked()
    }

    @Test
    fun handleEvent_onNetworkInfoClicked_callOnNetworkInfoClicked() {
        handler.handleEvent(OrderPreviewContract.Event.OnNetworkInfoClicked)
        verify(handler).onNetworkInfoClicked()
    }

    @Test
    fun handleEvent_onSecurityCodeInfoClicked_callOnSecurityCodeInfoClicked() {
        handler.handleEvent(OrderPreviewContract.Event.OnSecurityCodeInfoClicked)
        verify(handler).onSecurityCodeInfoClicked()
    }

    @Test
    fun handleEvent_onTermsAndConditionsClicked_callOnTermsAndConditionsClicked() {
        handler.handleEvent(OrderPreviewContract.Event.OnTermsAndConditionsClicked)
        verify(handler).onTermsAndConditionsClicked()
    }

    @Test
    fun handleEvent_onUserAuthenticationSucceed_callOnUserAuthenticationSucceed() {
        handler.handleEvent(OrderPreviewContract.Event.OnUserAuthenticationSucceed)
        verify(handler).onUserAuthenticationSucceed()
    }

    @Test
    fun handleEvent_onSecurityCodeChanged_callOnSecurityCodeChanged() {
        val securityCode = "123"
        handler.handleEvent(OrderPreviewContract.Event.OnSecurityCodeChanged(securityCode))
        verify(handler).onSecurityCodeChanged(securityCode)
    }
}