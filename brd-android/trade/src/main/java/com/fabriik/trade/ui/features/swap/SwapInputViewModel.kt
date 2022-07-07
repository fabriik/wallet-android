package com.fabriik.trade.ui.features.swap

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.fabriik.common.ui.base.FabriikViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.math.BigDecimal

class SwapInputViewModel(
    application: Application
) : FabriikViewModel<SwapInputContract.State, SwapInputContract.Event, SwapInputContract.Effect>(
    application
) {

    init {
        refreshQuote()
    }

    override fun createInitialState() = SwapInputContract.State(
        timer = QUOTE_TIMER,
        quoteLoading = true,
        originCurrency = "BSV",
        originCurrencyBalance = BigDecimal.TEN,
        destinationCurrency = "BTC",
        rateOriginToDestinationCurrency = BigDecimal.ONE,
        currencies = listOf("BTC", "BSV", "ETH")
    )

    override fun handleEvent(event: SwapInputContract.Event) {
        when (event) {
            SwapInputContract.Event.DismissClicked ->
                setEffect { SwapInputContract.Effect.Dismiss }

            SwapInputContract.Event.OriginCurrencyClicked ->
                setEffect {
                    SwapInputContract.Effect.OriginSelection(
                        currentState.currencies
                    )
                }

            SwapInputContract.Event.DestinationCurrencyClicked ->
                setEffect {
                    SwapInputContract.Effect.DestinationSelection(
                        currencies = currentState.currencies,
                        sourceCurrency = currentState.originCurrency
                    )
                }

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

            is SwapInputContract.Event.OriginCurrencyChanged ->
                setState { copy(originCurrency = event.currencyCode) } //todo: update rates, call API

            is SwapInputContract.Event.DestinationCurrencyChanged ->
                setState { copy(destinationCurrency = event.currencyCode) } //todo: update rates, call API

            is SwapInputContract.Event.OriginCurrencyFiatAmountChange -> {
                setState {
                    copy(

                    )
                }
            } //todo

            is SwapInputContract.Event.OriginCurrencyCryptoAmountChange -> {
                setState {
                    copy(

                    )
                }
            } //todo

            is SwapInputContract.Event.DestinationCurrencyFiatAmountChange -> {
                setState {
                    copy(

                    )
                }
            } //todo

            is SwapInputContract.Event.DestinationCurrencyCryptoAmountChange -> {
                setState {
                    copy(

                    )
                }
            } //todo
        }
    }

    private fun startTimer() {
        setState { copy(timer = QUOTE_TIMER) }

        viewModelScope.launch {
            (QUOTE_TIMER downTo 0)
                .asSequence()
                .asFlow()
                .onEach { delay(1000) }
                .collect {
                    if (it == 0) {
                        refreshQuote()
                    }
                    setState { copy(timer = it) }
                }
        }
    }

    private fun refreshQuote() {
        viewModelScope.launch(Dispatchers.IO) {
            setState { copy(quoteLoading = true) }
            delay(2000) //todo: replace with API call
            startTimer()
            setState { copy(quoteLoading = false) }
        }
    }

    companion object {
        const val QUOTE_TIMER = 15
    }
}