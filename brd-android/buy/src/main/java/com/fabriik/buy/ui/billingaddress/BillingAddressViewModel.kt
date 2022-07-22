package com.fabriik.buy.ui.billingaddress

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class BillingAddressViewModel(
    application: Application
) : FabriikViewModel<BillingAddressContract.State, BillingAddressContract.Event, BillingAddressContract.Effect>(
    application
) {

    override fun createInitialState() = BillingAddressContract.State

    override fun handleEvent(event: BillingAddressContract.Event) {
        when (event) {
            BillingAddressContract.Event.OnBackPressed ->
                setEffect { BillingAddressContract.Effect.Back }

            BillingAddressContract.Event.OnDismissClicked ->
                setEffect { BillingAddressContract.Effect.Dismiss }

            BillingAddressContract.Event.OnCountryClicked ->
                setEffect { BillingAddressContract.Effect.CountryList }
        }
    }
}