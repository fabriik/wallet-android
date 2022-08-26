package com.fabriik.buy.ui.features.timeout

import com.fabriik.common.ui.base.FabriikContract

class PaymentTimeoutContract : FabriikContract {

    sealed class Event : FabriikContract.Event {
        object TryAgainClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object BackToBuy : Effect()
    }

    object State : FabriikContract.State
}