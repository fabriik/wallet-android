package com.fabriik.buy.ui.features.buydetails

import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.trade.data.response.ExchangeOrder

interface BuyDetailsContract {

    sealed class Event : FabriikContract.Event {
        object LoadData : Event()
        object DismissClicked : Event()
        object OrderIdClicked : Event()
        object TransactionIdClicked : Event()
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