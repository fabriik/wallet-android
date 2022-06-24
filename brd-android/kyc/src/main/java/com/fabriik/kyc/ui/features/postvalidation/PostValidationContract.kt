package com.fabriik.kyc.ui.features.postvalidation

import com.fabriik.common.ui.base.FabriikContract

interface PostValidationContract {

    sealed class Event : FabriikContract.Event {
        object ConfirmClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Profile : Effect()
    }

    object State : FabriikContract.State
}