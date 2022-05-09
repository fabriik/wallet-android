package com.fabriik.kyc.ui.features.exposedperson

import com.fabriik.common.ui.base.FabriikContract

interface ExposedPersonContract {

    sealed class Event : FabriikContract.Event {
        object ConfirmClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
    }

    class State() : FabriikContract.State //todo: data class
}