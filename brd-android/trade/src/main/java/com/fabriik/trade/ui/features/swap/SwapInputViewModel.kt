package com.fabriik.trade.ui.features.swap

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel

class SwapInputViewModel(
    application: Application
) : FabriikViewModel<SwapInputContract.State, SwapInputContract.Event, SwapInputContract.Effect>(
    application
) {

    override fun createInitialState() = SwapInputContract.State

    override fun handleEvent(event: SwapInputContract.Event) {
        when (event) {
            SwapInputContract.Event.DismissClicked ->
                setEffect { SwapInputContract.Effect.Dismiss }
        }
    }
}