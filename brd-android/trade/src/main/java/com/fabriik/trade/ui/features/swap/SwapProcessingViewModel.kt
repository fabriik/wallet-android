package com.fabriik.trade.ui.features.swap

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.toBundle

class SwapProcessingViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : FabriikViewModel<SwapProcessingContract.State, SwapProcessingContract.Event, SwapProcessingContract.Effect>(
    application, savedStateHandle
) {
    private lateinit var arguments: SwapProcessingFragmentArgs

    override fun parseArguments(savedStateHandle: SavedStateHandle) {
        arguments = SwapProcessingFragmentArgs.fromBundle(
            savedStateHandle.toBundle()
        )
    }

    override fun createInitialState() = SwapProcessingContract.State(
        originCurrency = arguments.coinFrom,
        destinationCurrency = arguments.coinTo
    )

    override fun handleEvent(event: SwapProcessingContract.Event) {

        when (event) {
            SwapProcessingContract.Event.DismissClicked ->
                setEffect { SwapProcessingContract.Effect.Dismiss }
            SwapProcessingContract.Event.GoHomeClicked ->
                setEffect { SwapProcessingContract.Effect.GoHome }
            SwapProcessingContract.Event.OpenSwapDetails ->
                setEffect { SwapProcessingContract.Effect.OpenDetails }
        }
    }

}