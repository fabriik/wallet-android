package com.fabriik.buy.ui.features.billingaddress
import com.fabriik.kyc.data.model.Country
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BillingAddressEventHandlerTest {

    @Mock lateinit var country: Country

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

    @Test
    fun handleEvent_backPressed_callOnBackClicked() {
        handler.handleEvent(BillingAddressContract.Event.BackPressed)
        verify(handler).onBackClicked()
    }

    @Test
    fun handleEvent_dismissClicked_callOnDismissClicked() {
        handler.handleEvent(BillingAddressContract.Event.DismissClicked)
        verify(handler).onDismissClicked()
    }

    @Test
    fun handleEvent_confirmClicked_callOnConfirmClicked() {
        handler.handleEvent(BillingAddressContract.Event.ConfirmClicked)
        verify(handler).onConfirmClicked()
    }

    @Test
    fun handleEvent_countryClicked_callOnCountryClicked() {
        handler.handleEvent(BillingAddressContract.Event.CountryClicked)
        verify(handler).onCountryClicked()
    }

    @Test
    fun handleEvent_zipChanged_callOnZipChanged() {
        val city = "zip_test"
        handler.handleEvent(BillingAddressContract.Event.ZipChanged(city))
        verify(handler).onZipChanged(city)
    }

    @Test
    fun handleEvent_cityChanged_callOnCityChanged() {
        val city = "city_test"
        handler.handleEvent(BillingAddressContract.Event.CityChanged(city))
        verify(handler).onCityChanged(city)
    }

    @Test
    fun handleEvent_stateChanged_callOnStateChanged() {
        val state = "state_test"
        handler.handleEvent(BillingAddressContract.Event.StateChanged(state))
        verify(handler).onStateChanged(state)
    }

    @Test
    fun handleEvent_addressChanged_callOnAddressChanged() {
        val address = "address_test"
        handler.handleEvent(BillingAddressContract.Event.AddressChanged(address))
        verify(handler).onAddressChanged(address)
    }

    @Test
    fun handleEvent_lastNameChanged_callOnLastNameChanged() {
        val lastName = "last_name"
        handler.handleEvent(BillingAddressContract.Event.LastNameChanged(lastName))
        verify(handler).onLastNameChanged(lastName)
    }

    @Test
    fun handleEvent_firstNameChanged_callOnFirstNameChanged() {
        val firstName = "first_name"
        handler.handleEvent(BillingAddressContract.Event.FirstNameChanged(firstName))
        verify(handler).onFirstNameChanged(firstName)
    }

    @Test
    fun handleEvent_countryChanged_callOnCountryChanged() {
        handler.handleEvent(BillingAddressContract.Event.CountryChanged(country))
        verify(handler).onCountryChanged(country)
    }

    @Test
    fun handleEvent_paymentChallengeResult_callOnPaymentChallengeResult() {
        val result = 1231
        handler.handleEvent(BillingAddressContract.Event.PaymentChallengeResult(result))
        verify(handler).onPaymentChallengeResult(result)
    }
}