package com.fabriik.trade.ui.features.swapdetails

import com.fabriik.common.ui.base.FabriikContract

interface SwapDetailsContract {

    sealed class Event : FabriikContract.Event {
        object DismissClicked : Event()
        object OrderIdClicked : Event()
        object TransactionIdClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        data class CopyToClipboard(val data: String) : Effect()
    }

    data class State(
        val status: SwapStatus,
        val orderId: String,
        val swapFromCurrency: String,
        val swapToCurrency: String,
        val timestamp: String,
        val swapFromID: String,
        val swapToId: String,
        val swapFromCurrencyValue: String,
        val swapToCurrencyValue: String
    ) : FabriikContract.State
}

enum class SwapStatus {
    PENDING,
    COMPLETE,
    FAILED
}