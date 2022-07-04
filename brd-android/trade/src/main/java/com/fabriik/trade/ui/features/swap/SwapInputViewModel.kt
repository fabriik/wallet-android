package com.fabriik.trade.ui.features.swap

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.min
import com.fabriik.trade.R
import com.fabriik.trade.data.SwapApi
import com.fabriik.trade.ui.features.assetselection.AssetSelectionContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.math.BigDecimal
import kotlin.math.min

class SwapInputViewModel(
    application: Application
) : FabriikViewModel<SwapInputContract.State, SwapInputContract.Event, SwapInputContract.Effect>(
    application
) {

    private val swapApi = SwapApi.create(application)

    init {
        loadSupportedCurrencies()
    }

    override fun createInitialState() = SwapInputContract.State(
        /*timer = QUOTE_TIMER,
        quoteLoading = true,
        originCurrency = "BSV",
        originCurrencyBalance = BigDecimal.TEN,
        destinationCurrency = "BTC",
        rateOriginToDestinationCurrency = BigDecimal.ONE,
        initialLoadingVisible = true*/
    )

    override fun handleEvent(event: SwapInputContract.Event) {
        when (event) {
            SwapInputContract.Event.DismissClicked ->
                setEffect { SwapInputContract.Effect.Dismiss }

            SwapInputContract.Event.OriginCurrencyClicked -> {
                val currencies = currentState.tradingPairs.map { it.baseCurrency }//todo: filter only enabled wallets
                setEffect { SwapInputContract.Effect.OriginSelection(currencies) }
            }

            SwapInputContract.Event.DestinationCurrencyClicked -> {
                val currencies = currentState.tradingPairs.map { it.termCurrency }
                setEffect { SwapInputContract.Effect.DestinationSelection(currencies) }
            }

            SwapInputContract.Event.OnMinAmountClicked ->
                onBaseCurrencyCryptoChanged(
                    min(
                        currentState.baseCurrencyCryptoBalance,
                        currentState.selectedTradingPair?.minAmount ?: BigDecimal.ZERO
                    )
                )

            SwapInputContract.Event.OnMaxAmountClicked ->
                onBaseCurrencyCryptoChanged(
                    min(
                        currentState.baseCurrencyCryptoBalance,
                        currentState.selectedTradingPair?.maxAmount ?: BigDecimal.ZERO
                    )
                )
/*
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
            }*/ //todo
        }
    }

    private fun startTimer() {
        /*setState { copy(timer = QUOTE_TIMER) }

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
        }*/
    }

    private fun refreshQuote() {
        /*viewModelScope.launch(Dispatchers.IO) {
            setState { copy(quoteLoading = true) }
            delay(2000) //todo: replace with API call
            startTimer()
            setState { copy(quoteLoading = false) }
        }*/
    }

    private fun loadSupportedCurrencies() {
        callApi(
            endState = { copy(initialLoadingVisible = false) },
            startState = { copy(initialLoadingVisible = true) },
            action = { swapApi.getSupportedTradingPairs() },
            callback = {
                when (it.status) {
                    Status.SUCCESS -> {
                        setState {
                            copy(
                                tradingPairs = it.data ?: emptyList(),
                                selectedTradingPair = it.data?.firstOrNull()
                            )
                        }
                    }

                    Status.ERROR ->
                        setEffect {
                            SwapInputContract.Effect.ShowToast(
                                it.message ?: getString(R.string.FabriikApi_DefaultError)
                            )
                        }
                }
            }
        )
    }

    private fun onBaseCurrencyFiatChanged(amount: BigDecimal) {

    }

    private fun onBaseCurrencyCryptoChanged(amount: BigDecimal) {
        setState {
            copy(
                baseCurrencyCryptoBalance = amount
            )
        }
    }

    private fun onTermCurrencyFiatChanged(amount: BigDecimal) {

    }

    private fun onTermCurrencyCryptoChanged(amount: BigDecimal) {

    }

    companion object {
        const val QUOTE_TIMER = 15
    }
}