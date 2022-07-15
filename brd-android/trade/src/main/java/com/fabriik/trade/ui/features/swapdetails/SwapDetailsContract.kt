package com.fabriik.trade.ui.features.swapdetails

import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.trade.data.response.ExchangeOrder

interface SwapDetailsContract {

    sealed class Event : FabriikContract.Event {
        object LoadData : Event()
        object DismissClicked : Event()
        object OrderIdClicked : Event()
        object SourceTransactionIdClicked : Event()
        object DestinationTransactionIdClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        data class ShowToast(val message: String): Effect()
        data class CopyToClipboard(val data: String) : Effect()
    }

    sealed class State : FabriikContract.State {
        object Loading : State()
        object Error : State()
        data class Loaded(
            val data: ExchangeOrder
        ) : State()
    }
}