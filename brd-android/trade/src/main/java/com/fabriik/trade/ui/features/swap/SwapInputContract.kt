package com.fabriik.trade.ui.features.swap

import com.fabriik.common.ui.base.FabriikContract

interface SwapInputContract {

    sealed class Event : FabriikContract.Event {
        object DismissClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
    }

    object State : FabriikContract.State
}