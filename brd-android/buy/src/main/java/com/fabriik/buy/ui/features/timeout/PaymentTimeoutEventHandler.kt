package com.fabriik.buy.ui.features.timeout

import com.fabriik.common.ui.base.FabriikEventHandler

interface PaymentTimeoutEventHandler: FabriikEventHandler<PaymentTimeoutContract.Event> {

    override fun handleEvent(event: PaymentTimeoutContract.Event) {
        when (event) {
            PaymentTimeoutContract.Event.TryAgainClicked -> onTryAgainClicked()
        }
    }

    fun onTryAgainClicked()
}