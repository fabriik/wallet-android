package com.fabriik.buy.ui.features.paymentmethod

import com.fabriik.buy.data.model.PaymentInstrument
import com.fabriik.common.ui.base.FabriikContract

interface PaymentMethodContract : FabriikContract {

    sealed class Event : FabriikContract.Event {
        object BackClicked : Event()
        object DismissClicked : Event()
        object AddCardClicked : Event()
        data class PaymentInstrumentSelected(val paymentInstrument: PaymentInstrument): Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        object AddCard : Effect()
        data class Back(val selectedInstrument: PaymentInstrument? = null) : Effect()
    }

    data class State(
        val paymentInstruments: List<PaymentInstrument>
    ) : FabriikContract.State
}