package com.fabriik.trade.ui.features.swap

import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.trade.ui.features.assetselection.AssetSelectionAdapter
import java.math.BigDecimal

interface SwapProcessingContract {

    sealed class Event : FabriikContract.Event {
        object DismissClicked : Event()
        object GoHomeClicked : Event()
        object OpenSwapDetails : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        object OpenDetails : Effect()
        object GoHome : Effect()
    }

    data class State(
        val originCurrency: String,
        val destinationCurrency: String
    ) : FabriikContract.State
}