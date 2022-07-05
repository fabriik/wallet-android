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
        SwapDetailsContract.State(SwapStatus.PENDING)

    override fun handleEvent(event: SwapDetailsContract.Event) {

    }
}