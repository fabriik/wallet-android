package com.fabriik.trade.ui.features.processing

import com.fabriik.common.ui.base.FabriikEventHandler

interface SwapProcessingEventHandler: FabriikEventHandler<SwapProcessingContract.Event> {

    override fun handleEvent(event: SwapProcessingContract.Event) {
        return when (event) {
            is SwapProcessingContract.Event.GoHomeClicked -> onGoHomeClicked()
            is SwapProcessingContract.Event.DismissClicked -> onDismissClicked()
            is SwapProcessingContract.Event.OpenSwapDetails -> onOpenSwapDetailsClicked()
        }
    }

    fun onDismissClicked()

    fun onGoHomeClicked()

    fun onOpenSwapDetailsClicked()
}