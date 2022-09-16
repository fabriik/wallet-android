package com.fabriik.trade.ui.features.failed

import com.fabriik.common.ui.base.FabriikEventHandler

interface SwapFailedEventHandler: FabriikEventHandler<SwapFailedContract.Event> {

    override fun handleEvent(event: SwapFailedContract.Event) {
        return when (event) {
            is SwapFailedContract.Event.GoHomeClicked -> onGoHomeClicked()
            is SwapFailedContract.Event.SwapAgainClicked -> onSwapAgainClicked()
        }
    }

    fun onGoHomeClicked()

    fun onSwapAgainClicked()
}