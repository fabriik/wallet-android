package com.fabriik.buy.ui.features.billingaddress

import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.kyc.data.model.Country

class BillingAddressContract : FabriikContract {

    sealed class Event : FabriikContract.Event {
        object BackPressed : Event()
        object DismissClicked: Event()
        object CountryClicked: Event()
        object ConfirmClicked: Event()
        data class CountryChanged(val country: Country): Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()
        object PaymentMethod : Effect()
        object CountrySelection : Effect()
    }

    data class State(
        val country: Country? = null,
        val confirmEnabled: Boolean = false
    ) : FabriikContract.State
}