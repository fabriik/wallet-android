package com.fabriik.buy.ui.billingaddress

import com.fabriik.common.ui.base.FabriikContract

class BillingAddressContract : FabriikContract {

    sealed class Event : FabriikContract.Event {
        object OnBackPressed : Event()
        object OnDismissClicked: Event()
        object OnCountryClicked: Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()
        object CountryList : Effect()
    }

    object State : FabriikContract.State
}