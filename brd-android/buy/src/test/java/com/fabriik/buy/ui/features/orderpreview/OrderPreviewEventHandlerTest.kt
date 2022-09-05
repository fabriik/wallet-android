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
}