package com.fabriik.kyc.ui.features.addressinformation

import com.fabriik.common.ui.base.FabriikContract

interface AddressInformationContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object ConfirmClicked : Event()
        object DismissClicked : Event()
        object InfoClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoBack : Effect()
        object Dismiss : Effect()
        object GoToProofOfResidence : Effect()
    }

    class State() : FabriikContract.State //todo: data class
}