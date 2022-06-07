package com.fabriik.kyc.ui.features.prevalidation

import com.fabriik.common.ui.base.FabriikContract

interface PreValidationContract {

    sealed class Event : FabriikContract.Event{
        object BackClicked : Event()
        object ConfirmClicked: Event()
        object DismissCLicked: Event()
    }

    sealed class Effect : FabriikContract.Effect{
        object Back : Effect()
        object ProofOfIdentity : Effect()
        object Dismiss : Effect()
    }

    object State : FabriikContract.State
}