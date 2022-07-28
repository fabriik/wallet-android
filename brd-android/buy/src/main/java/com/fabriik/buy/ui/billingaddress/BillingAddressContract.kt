package com.fabriik.buy.ui.billingaddress

import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.kyc.data.model.Country

class BillingAddressContract : FabriikContract {

    sealed class Event : FabriikContract.Event {
        object OnBackPressed : Event()
        object OnDismissClicked: Event()
        object OnCountryClicked: Event()
        data class OnCountryChanged(val country: Country): Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()
        object CountrySelection : Effect()
    }

    data class State(
        val country: Country? = null,
        val confirmEnabled: Boolean = false
    ) : FabriikContract.State
}