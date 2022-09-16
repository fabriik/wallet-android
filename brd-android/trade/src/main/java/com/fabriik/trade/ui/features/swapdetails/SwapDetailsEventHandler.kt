package com.fabriik.trade.ui.features.swapdetails

import com.fabriik.common.ui.base.FabriikEventHandler

interface SwapDetailsEventHandler: FabriikEventHandler<SwapDetailsContract.Event> {

    override fun handleEvent(event: SwapDetailsContract.Event) {
        return when (event) {
            is SwapDetailsContract.Event.LoadData -> onLoadData()
            is SwapDetailsContract.Event.DismissClicked -> onDismissClicked()
            is SwapDetailsContract.Event.OrderIdClicked -> onOrderIdClicked()
            is SwapDetailsContract.Event.SourceTransactionIdClicked -> onSourceTransactionIdClicked()
            is SwapDetailsContract.Event.DestinationTransactionIdClicked -> onDestinationTransactionIdClicked()
        }
    }

    fun onLoadData()

    fun onDismissClicked()

    fun onOrderIdClicked()

    fun onSourceTransactionIdClicked()

    fun onDestinationTransactionIdClicked()
}