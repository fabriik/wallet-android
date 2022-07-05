package com.fabriik.trade.ui.features.swapdetails

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class SwapDetailsViewModel(
    application: Application
) :
    FabriikViewModel<SwapDetailsContract.State, SwapDetailsContract.Event, SwapDetailsContract.Effect>(
        application
    ) {
    override fun createInitialState(): SwapDetailsContract.State =
        SwapDetailsContract.State(
            status = SwapStatus.PENDING
        )

    override fun handleEvent(event: SwapDetailsContract.Event) {
        when (event) {
            SwapDetailsContract.Event.DismissClicked ->
                setEffect { SwapDetailsContract.Effect.Dismiss }

            SwapDetailsContract.Event.OrderIdClicked -> {
                TODO()
            }

            SwapDetailsContract.Event.TransactionIdClicked -> {
                TODO()
            }

        }
    }
}