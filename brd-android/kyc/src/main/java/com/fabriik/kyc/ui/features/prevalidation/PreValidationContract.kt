package com.fabriik.kyc.ui.features.prevalidation

import com.fabriik.common.ui.base.FabriikContract

interface PreValidationContract {

    sealed class Event : FabriikContract.Event{
        object BackClicked : Event()
        object ConfirmClicked: Event()
    }

    sealed class Effect : FabriikContract.Effect{
        object GoBack : Effect()
        object GoForward : Effect()
    }

    object State : FabriikContract.State
}