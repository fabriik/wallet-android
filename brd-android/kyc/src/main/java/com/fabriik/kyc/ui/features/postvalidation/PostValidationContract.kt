package com.fabriik.kyc.ui.features.postvalidation

import com.fabriik.common.ui.base.FabriikContract

interface PostValidationContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object ConfirmClicked : Event()
        object DismissClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Back : Effect()
        object Profile : Effect()
        object Dismiss : Effect()
    }

    object State : FabriikContract.State
}