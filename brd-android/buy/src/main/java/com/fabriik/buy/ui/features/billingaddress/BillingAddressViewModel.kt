package com.fabriik.buy.ui.features.billingaddress

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class BillingAddressViewModel(
    application: Application
) : FabriikViewModel<BillingAddressContract.State, BillingAddressContract.Event, BillingAddressContract.Effect>(
    application
) {

    override fun createInitialState() = BillingAddressContract.State()

    override fun handleEvent(event: BillingAddressContract.Event) {
        when (event) {
            BillingAddressContract.Event.OnBackPressed ->
                setEffect { BillingAddressContract.Effect.Back }

            BillingAddressContract.Event.OnDismissClicked ->
                setEffect { BillingAddressContract.Effect.Dismiss }

            BillingAddressContract.Event.OnCountryClicked ->
                setEffect { BillingAddressContract.Effect.CountrySelection }

            is BillingAddressContract.Event.OnCountryChanged ->
                setState { copy(country = event.country).validate() }
        }
    }

    private fun BillingAddressContract.State.validate() = copy(
        confirmEnabled = country != null
        //todo: add validation for other input data
    )
}