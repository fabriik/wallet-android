package com.fabriik.buy.ui.features.paymentmethod

import com.fabriik.common.data.model.PaymentInstrument
import com.fabriik.common.ui.base.FabriikEventHandler

interface PaymentMethodEventHandler: FabriikEventHandler<PaymentMethodContract.Event> {

    override fun handleEvent(event: PaymentMethodContract.Event) {
        return when (event) {
            is PaymentMethodContract.Event.BackClicked -> onBackClicked()
            is PaymentMethodContract.Event.DismissClicked -> onDismissClicked()
            is PaymentMethodContract.Event.AddCardClicked -> onAddCardClicked()
            is PaymentMethodContract.Event.PaymentInstrumentSelected -> onPaymentInstrumentSelected(event.paymentInstrument)
        }
    }

    fun onBackClicked()

    fun onDismissClicked()

    fun onAddCardClicked()

    fun onPaymentInstrumentSelected(paymentInstrument: PaymentInstrument)
}