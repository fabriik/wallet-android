package com.fabriik.trade.ui.features.swap

import com.fabriik.common.ui.base.FabriikContract

interface SwapInputContract {

    sealed class Event : FabriikContract.Event {
        object DismissClicked : Event()
        object OriginCurrencyClicked : Event()
        object DestinationCurrencyClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        object OriginSelection : Effect()
        object DestinationSelection : Effect()
    }

    object State : FabriikContract.State
}