package com.fabriik.trade.ui.features.failed

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class SwapFailedViewModel(
    application: Application
) : FabriikViewModel<SwapFailedContract.State, SwapFailedContract.Event, SwapFailedContract.Effect>(
    application
), SwapFailedEventHandler {

    override fun createInitialState() = SwapFailedContract.State()

    override fun onGoHomeClicked() {
        setEffect { SwapFailedContract.Effect.Dismiss }
    }

    override fun onSwapAgainClicked() {
        setEffect { SwapFailedContract.Effect.Back }
    }
}