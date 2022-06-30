package com.fabriik.trade.ui.features.swap

import com.fabriik.common.ui.base.FabriikContract
import java.math.BigDecimal

interface SwapInputContract {

    sealed class Event : FabriikContract.Event {
        object DismissClicked : Event()
        object OriginCurrencyClicked : Event()
        object ReplaceCurrenciesClicked : Event()
        object DestinationCurrencyClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        object OriginSelection : Effect()
        object DestinationSelection : Effect()
    }

    data class State(
        val originCurrency: String,
        val originCurrencyBalance: BigDecimal,
        val sendingNetworkFee: String? = null,
        val destinationCurrency: String,
        val receivingNetworkFee: String? = null,
        val rateOriginToDestinationCurrency: BigDecimal
    ) : FabriikContract.State
}