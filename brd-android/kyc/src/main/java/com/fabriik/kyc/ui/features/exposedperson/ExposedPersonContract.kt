package com.fabriik.kyc.ui.features.exposedperson

import com.fabriik.common.ui.base.FabriikContract

interface ExposedPersonContract {

    sealed class Event : FabriikContract.Event {
        object ConfirmClicked : Event()
        class EmailChanged(val email: String) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
    }

    data class State(
        val email: String = "",
        val confirmEnabled: Boolean = false
    ) : FabriikContract.State
}