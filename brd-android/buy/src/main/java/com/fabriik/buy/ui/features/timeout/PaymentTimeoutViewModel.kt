package com.fabriik.buy.ui.features.timeout

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class PaymentTimeoutViewModel(
    application: Application
) : FabriikViewModel<PaymentTimeoutContract.State, PaymentTimeoutContract.Event, PaymentTimeoutContract.Effect>(
    application
) {

    override fun createInitialState() = PaymentTimeoutContract.State

    override fun handleEvent(event: PaymentTimeoutContract.Event) {
        when (event) {
            PaymentTimeoutContract.Event.TryAgainClicked ->
                setEffect { PaymentTimeoutContract.Effect.BackToBuy }
        }
    }
}