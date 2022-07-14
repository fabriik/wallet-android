package com.fabriik.trade.ui.features.processing

import com.fabriik.common.ui.base.FabriikContract

interface SwapProcessingContract {

    sealed class Event : FabriikContract.Event {
        object DismissClicked : Event()
        object GoHomeClicked : Event()
        object OpenSwapDetails : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        object GoHome : Effect()
        data class OpenDetails(val id: String) : Effect()
    }

    data class State(
        val originCurrency: String,
        val destinationCurrency: String
    ) : FabriikContract.State
}