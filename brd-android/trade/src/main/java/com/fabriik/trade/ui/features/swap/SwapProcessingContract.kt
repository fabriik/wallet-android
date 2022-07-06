package com.fabriik.trade.ui.features.swap

import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.trade.ui.features.assetselection.AssetSelectionAdapter
import java.math.BigDecimal

interface SwapProcessingContract {

    sealed class Event : FabriikContract.Event {
        object DismissClicked : Event()
        object GoHome : Event()
        object OpenSwapDetails : Event()
        object DestinationCurrencyClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
    }

    data class State(
        val originCurrency: String,
        val destinationCurrency: String
    ) : FabriikContract.State
}