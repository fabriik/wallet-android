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
                val currencies = currentState.tradingPairs.map { it.baseCurrency }.distinct()//todo: filter only enabled wallets
                setEffect { SwapInputContract.Effect.OriginSelection(currencies) }
            }

            SwapInputContract.Event.DestinationCurrencyClicked -> {
                val currencies = currentState.tradingPairs
                    .filter { it.baseCurrency == currentState.selectedTradingPair?.baseCurrency }
                    .map { it.termCurrency }
                    .distinct()

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

            SwapInputContract.Event.ReplaceCurrenciesClicked -> {
                val originCurrency = currentState.selectedTradingPair?.baseCurrency
                val destinationCurrency = currentState.selectedTradingPair?.termCurrency
                val newTradingPair = currentState.tradingPairs.find {
                    it.baseCurrency == destinationCurrency && it.termCurrency == originCurrency
                }

                if (newTradingPair == null) {
                    setEffect {
                        SwapInputContract.Effect.ShowToast(
                            getString(R.string.Swap_Input_SwapNotSupported)
                        )
                    }
                } else {
                    setState { copy(selectedTradingPair = newTradingPair) }
                    refreshQuote()
                }
            }

            is SwapInputContract.Event.OriginCurrencyChanged -> {
                val pairsWithSelectedBaseCurrency = currentState.tradingPairs.filter {
                    it.baseCurrency == event.currencyCode
                }

                val newSelectedPair = pairsWithSelectedBaseCurrency.find {
                    it.termCurrency == currentState.selectedTradingPair?.termCurrency
                } ?: pairsWithSelectedBaseCurrency.firstOrNull()

                setState { copy(selectedTradingPair = newSelectedPair) } //todo: update rates
                refreshQuote()
            }

            is SwapInputContract.Event.DestinationCurrencyChanged -> {
                val pairsWithSelectedBaseCurrency = currentState.tradingPairs.filter {
                    it.baseCurrency == currentState.selectedTradingPair?.baseCurrency
                }

                val newSelectedPair = pairsWithSelectedBaseCurrency.find {
                    it.termCurrency == event.currencyCode
                } ?: currentState.selectedTradingPair

                setState { copy(selectedTradingPair = newSelectedPair) } //todo: update rates
                refreshQuote()
            }

/*


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
        val selectedTradingPair = currentState.selectedTradingPair ?: return

        callApi(
            startState = { copy(quoteLoadingVisible = true) },
            endState = { copy(quoteLoadingVisible = false) },
            action = { swapApi.getQuote(selectedTradingPair) },
            callback = {
                when (it.status) {
                    Status.SUCCESS -> {
                        setState { copy(quoteResponse = it.data) }
                        //todo: set timer
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
                        refreshQuote()
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