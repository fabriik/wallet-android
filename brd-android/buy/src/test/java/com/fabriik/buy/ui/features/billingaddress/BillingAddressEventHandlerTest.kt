package com.fabriik.buy.ui.features.billingaddress
import com.fabriik.kyc.data.model.Country
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BillingAddressEventHandlerTest {

    @Spy val handler = object : BillingAddressEventHandler {
        override fun onBackClicked() {}
        override fun onDismissClicked() {}
        override fun onConfirmClicked() {}
        override fun onCountryClicked() {}
        override fun onZipChanged(zip: String) {}
        override fun onCityChanged(city: String) {}
        override fun onStateChanged(state: String) {}
        override fun onAddressChanged(address: String) {}
        override fun onCountryChanged(country: Country) {}
        override fun onLastNameChanged(lastName: String) {}
        override fun onFirstNameChanged(firstName: String) {}
        override fun onPaymentChallengeResult(result: Int) {}
    }
}