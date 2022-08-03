package com.fabriik.buy.ui.features.paymentmethod

import com.fabriik.common.ui.base.FabriikContract

interface PaymentMethodContract : FabriikContract {

    sealed class Event : FabriikContract.Event {
        object OnBackClicked : Event()
        object OnDismissClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()
    }

    object State : FabriikContract.State
}