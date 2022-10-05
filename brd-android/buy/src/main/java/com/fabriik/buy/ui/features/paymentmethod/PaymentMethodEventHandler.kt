package com.fabriik.buy.ui.features.paymentmethod

import com.fabriik.common.data.model.PaymentInstrument
import com.fabriik.common.ui.base.FabriikEventHandler

interface PaymentMethodEventHandler: FabriikEventHandler<PaymentMethodContract.Event> {

    override fun handleEvent(event: PaymentMethodContract.Event) {
        return when (event) {
            is PaymentMethodContract.Event.BackClicked -> onBackClicked()
            is PaymentMethodContract.Event.DismissClicked -> onDismissClicked()
            is PaymentMethodContract.Event.AddCardClicked -> onAddCardClicked()
            is PaymentMethodContract.Event.RemoveOptionClicked -> onRemoveOptionClicked(event.paymentInstrument)
            is PaymentMethodContract.Event.PaymentInstrumentClicked -> onPaymentInstrumentClicked(event.paymentInstrument)
            is PaymentMethodContract.Event.PaymentInstrumentOptionsClicked -> onPaymentInstrumentOptionsClicked(event.paymentInstrument)
            is PaymentMethodContract.Event.PaymentInstrumentRemovalConfirmed -> onPaymentInstrumentRemovalConfirmed(event.paymentInstrument)
        }
    }

    fun onBackClicked()

    fun onDismissClicked()

    fun onAddCardClicked()

    fun onRemoveOptionClicked(paymentInstrument: PaymentInstrument)

    fun onPaymentInstrumentClicked(paymentInstrument: PaymentInstrument)

    fun onPaymentInstrumentOptionsClicked(paymentInstrument: PaymentInstrument)

    fun onPaymentInstrumentRemovalConfirmed(paymentInstrument: PaymentInstrument)
}