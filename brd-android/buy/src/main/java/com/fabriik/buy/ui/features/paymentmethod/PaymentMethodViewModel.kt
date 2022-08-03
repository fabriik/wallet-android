package com.fabriik.buy.ui.features.paymentmethod

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class PaymentMethodViewModel(application: Application) :
    FabriikViewModel<PaymentMethodContract.State, PaymentMethodContract.Event, PaymentMethodContract.Effect>(
        application
    ) {
    override fun createInitialState() = PaymentMethodContract.State

    override fun handleEvent(event: PaymentMethodContract.Event) {
        when (event) {
            PaymentMethodContract.Event.OnBackClicked ->
                setEffect { PaymentMethodContract.Effect.Back }
            PaymentMethodContract.Event.OnDismissClicked ->
                setEffect { PaymentMethodContract.Effect.Dismiss }
        }
    }
}