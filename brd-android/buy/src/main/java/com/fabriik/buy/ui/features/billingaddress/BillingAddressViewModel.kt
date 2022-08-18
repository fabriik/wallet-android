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
            BillingAddressContract.Event.BackPressed ->
                setEffect { BillingAddressContract.Effect.Back }

            BillingAddressContract.Event.DismissClicked ->
                setEffect { BillingAddressContract.Effect.Dismiss }

            BillingAddressContract.Event.ConfirmClicked ->
                setEffect { BillingAddressContract.Effect.PaymentMethod } //todo: api call

            BillingAddressContract.Event.CountryClicked ->
                setEffect { BillingAddressContract.Effect.CountrySelection }

            is BillingAddressContract.Event.CountryChanged ->
                setState { copy(country = event.country).validate() }
        }
    }

    private fun BillingAddressContract.State.validate() = copy(
        confirmEnabled = country != null
        //todo: add validation for other input data
    )
}