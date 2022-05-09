package com.fabriik.kyc.ui.features.addressinformation

import com.fabriik.common.ui.base.FabriikContract

interface AddressInformationContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object ConfirmClicked : Event()
        object DismissClicked : Event()
        object InfoClicked : Event()

        class ZipChanged(val zip: String) : Event()
        class CityChanged(val city: String) : Event()
        class StateChanged(val state: String) : Event()
        class CountryChanged(val country: String) : Event()
        class AddressLine1Changed(val addressLine1: String) : Event()
        class AddressLine2Changed(val addressLine2: String) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoBack : Effect()
        object Dismiss : Effect()
        object GoToProofOfResidence : Effect()
    }

    data class State(
        val country: String = "",
        val state: String = "",
        val city: String = "",
        val zip: String = "",
        val addressLine1: String = "",
        val addressLine2: String = "",
        val confirmEnabled: Boolean = false
    ) : FabriikContract.State
}