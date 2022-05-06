package com.fabriik.kyc.ui.features.accountverification

import com.fabriik.common.ui.base.FabriikContract

interface AccountVerificationContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object InfoClicked : Event()
        object BasicClicked : Event()
        object UnlimitedClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoBack : Effect()
        object GoToPersonalInfo : Effect()
    }

    class State() : FabriikContract.State //todo: data class
}