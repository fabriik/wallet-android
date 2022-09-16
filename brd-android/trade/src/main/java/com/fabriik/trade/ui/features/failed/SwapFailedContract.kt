package com.fabriik.trade.ui.features.failed

import com.fabriik.common.ui.base.FabriikContract

interface SwapFailedContract {

    sealed class Event : FabriikContract.Event {
        object GoHomeClicked : Event()
        object SwapAgainClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()
    }

    class State : FabriikContract.State
}