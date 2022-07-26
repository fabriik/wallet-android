package com.fabriik.buy.ui.addcard

import com.fabriik.common.ui.base.FabriikContract

class AddCardContract : FabriikContract {

    sealed class Event : FabriikContract.Event {
        object OnBackClicked : Event()
        object OnDismissClicked : Event()
        object OnConfirmClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()
        object Confirm : Effect()
    }

    object State : FabriikContract.State
}