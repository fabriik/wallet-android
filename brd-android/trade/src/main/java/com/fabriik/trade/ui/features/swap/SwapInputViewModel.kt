package com.fabriik.trade.ui.features.swap

import android.app.Application
import com.fabriik.common.ui.base.FabriikViewModel
import java.math.BigDecimal

class SwapInputViewModel(
    application: Application
) : FabriikViewModel<SwapInputContract.State, SwapInputContract.Event, SwapInputContract.Effect>(
    application
) {

    override fun createInitialState() = SwapInputContract.State(
        originCurrency = "BSV",
        originCurrencyBalance = BigDecimal.TEN,
        destinationCurrency = "BTC",
        rateOriginToDestinationCurrency = BigDecimal.ONE
    )

    override fun handleEvent(event: SwapInputContract.Event) {
        when (event) {
            SwapInputContract.Event.DismissClicked ->
                setEffect { SwapInputContract.Effect.Dismiss }

            SwapInputContract.Event.OriginCurrencyClicked ->
                setEffect { SwapInputContract.Effect.OriginSelection }

            SwapInputContract.Event.DestinationCurrencyClicked ->
                setEffect { SwapInputContract.Effect.DestinationSelection }

            SwapInputContract.Event.ReplaceCurrenciesClicked -> {
                val originCurrency = currentState.originCurrency
                val destinationCurrency = currentState.destinationCurrency
                setState {
                    copy(
                        originCurrency = destinationCurrency,
                        destinationCurrency = originCurrency,
                    )
                }
            }
        }
    }
}