package com.fabriik.buy.ui.features.billingaddress

import com.fabriik.common.ui.base.FabriikEventHandler
import com.fabriik.kyc.data.model.Country

interface BillingAddressEventHandler: FabriikEventHandler<BillingAddressContract.Event> {

    override fun handleEvent(event: BillingAddressContract.Event) {
        return when (event) {
            is BillingAddressContract.Event.BackPressed -> onBackClicked()
            is BillingAddressContract.Event.DismissClicked -> onDismissClicked()
            is BillingAddressContract.Event.ConfirmClicked -> onConfirmClicked()
            is BillingAddressContract.Event.CountryClicked -> onCountryClicked()
            is BillingAddressContract.Event.ZipChanged -> onZipChanged(event.zip)
            is BillingAddressContract.Event.CityChanged -> onCityChanged(event.city)
            is BillingAddressContract.Event.StateChanged -> onStateChanged(event.state)
            is BillingAddressContract.Event.AddressChanged -> onAddressChanged(event.address)
            is BillingAddressContract.Event.CountryChanged -> onCountryChanged(event.country)
            is BillingAddressContract.Event.LastNameChanged -> onLastNameChanged(event.lastName)
            is BillingAddressContract.Event.FirstNameChanged -> onFirstNameChanged(event.firstName)
            is BillingAddressContract.Event.PaymentChallengeResult -> onPaymentChallengeResult(event.result)
        }
    }

    fun onBackClicked()

    fun onDismissClicked()

    fun onConfirmClicked()

    fun onCountryClicked()

    fun onZipChanged(zip: String)

    fun onCityChanged(city: String)

    fun onStateChanged(state: String)

    fun onAddressChanged(address: String)

    fun onCountryChanged(country: Country)

    fun onLastNameChanged(lastName: String)

    fun onFirstNameChanged(firstName: String)

    fun onPaymentChallengeResult(result: Int)
}