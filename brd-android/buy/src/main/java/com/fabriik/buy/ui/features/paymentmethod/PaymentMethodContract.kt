package com.fabriik.buy.ui.features.paymentmethod

import com.fabriik.buy.ui.features.addcard.AddCardFlow
import com.fabriik.common.data.model.PaymentInstrument
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
        data class AddCard(val flow: AddCardFlow) : Effect()
        data class Back(val selectedInstrument: PaymentInstrument? = null) : Effect()
        data class ShowError(val message: String) : Effect()
    }

    data class State(
        val paymentInstruments: List<PaymentInstrument>,
        val initialLoadingIndicator: Boolean = false
    ) : FabriikContract.State
}