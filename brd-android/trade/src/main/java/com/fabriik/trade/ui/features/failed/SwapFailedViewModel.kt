package com.fabriik.trade.ui.features.failed

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class SwapFailedViewModel(
    application: Application
) : FabriikViewModel<SwapFailedContract.State, SwapFailedContract.Event, SwapFailedContract.Effect>(
    application
) {

    override fun createInitialState() = SwapFailedContract.State()

    override fun handleEvent(event: SwapFailedContract.Event) {
        when (event) {
            SwapFailedContract.Event.SwapAgainClicked ->
                setEffect { SwapFailedContract.Effect.Back }

            SwapFailedContract.Event.GoHomeClicked ->
                setEffect { SwapFailedContract.Effect.Dismiss }
        }
    }
}