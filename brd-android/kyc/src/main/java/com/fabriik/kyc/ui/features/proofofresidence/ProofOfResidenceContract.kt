package com.fabriik.kyc.ui.features.proofofresidence

import com.fabriik.common.ui.base.FabriikContract

interface ProofOfResidenceContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object InfoClicked : Event()
        object DismissClicked : Event()
        object YesClicked : Event()
        object NoClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object GoBack : Effect()
        object Dismiss : Effect()
        object GoToProofUpload : Effect()
    }

    class State() : FabriikContract.State //todo: data class
}