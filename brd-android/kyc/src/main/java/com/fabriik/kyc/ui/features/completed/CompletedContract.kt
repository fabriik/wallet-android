package com.fabriik.kyc.ui.features.completed

import com.fabriik.common.ui.base.FabriikContract

interface CompletedContract {

    sealed class Event : FabriikContract.Event {
        object GotItClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
    }

    class State() : FabriikContract.State //todo: data class
}