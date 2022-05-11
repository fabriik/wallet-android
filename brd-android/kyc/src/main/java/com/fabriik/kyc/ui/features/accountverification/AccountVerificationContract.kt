package com.fabriik.kyc.ui.features.accountverification

import com.fabriik.common.ui.base.FabriikContract

interface AccountVerificationContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object BasicClicked : Event()
        object UnlimitedClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoBack : Effect()
        object GoToPersonalInfo : Effect()
        object GoToProofOfIdentity : Effect()
    }

    data class State(
        val basicBoxEnabled: Boolean = false,
        val unlimitedBoxEnabled: Boolean = false
    ) : FabriikContract.State
}