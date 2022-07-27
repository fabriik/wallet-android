package com.fabriik.buy.ui.addcard

import com.fabriik.common.ui.base.FabriikContract

class AddCardContract : FabriikContract {

    sealed class Event : FabriikContract.Event {
        object OnBackClicked : Event()
        object OnDismissClicked : Event()
        object OnConfirmClicked : Event()

        data class OnCardNumberChanged(
            val number: String
        ) : Event()

        data class OnDateChanged(
            val date: String
        ) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()
        object Confirm : Effect()

        data class ShowToast(
            val message: String
        ) : Effect()
    }

    data class State(
        val cardNumber: String? = "",
        val date: String? = ""
    ) : FabriikContract.State
}