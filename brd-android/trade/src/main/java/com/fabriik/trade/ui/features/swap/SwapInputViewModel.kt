package com.fabriik.trade.ui.features.swap

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.breadwallet.breadbox.BreadBox
import com.breadwallet.breadbox.findByCurrencyId
import com.breadwallet.breadbox.toBigDecimal
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.trade.R
import com.fabriik.trade.data.SwapApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class SwapInputViewModel(
    application: Application
) : FabriikViewModel<SwapInputContract.State, SwapInputContract.Event, SwapInputContract.Effect>(
    application
), KodeinAware {

    override val kodein by closestKodein { application }

    private val breadBox by kodein.instance<BreadBox>()
    private val swapApi = SwapApi.create(application)

    init {
        loadSupportedCurrencies()
    }

    override fun createInitialState() = SwapInputContract.State.Empty

    override fun handleEvent(event: SwapInputContract.Event) {
        when (event) {
            SwapInputContract.Event.DismissClicked ->
                setEffect { SwapInputContract.Effect.Dismiss }

            SwapInputContract.Event.OriginCurrencyClicked -> withLoadedState { state ->
                val currencies = state.tradingPairs
                    .map { it.baseCurrency }
                    //todo: filter enabled wallets
                    .distinct()

                setEffect { SwapInputContract.Effect.OriginSelection(currencies) }
            }

            is SwapInputContract.Event.OriginCurrencyChanged -> withLoadedState { state ->
                val pairsWithSelectedBaseCurrency = state.tradingPairs.filter {
                    it.baseCurrency == event.currencyCode
                }

                val newSelectedPair = pairsWithSelectedBaseCurrency.find {
                    it.termCurrency == state.selectedPair.termCurrency
                } ?: pairsWithSelectedBaseCurrency.firstOrNull()

                newSelectedPair?.let {
                    setState { state.copy(selectedPair = newSelectedPair) }
                    getWalletBalance(newSelectedPair.baseCurrency)
                    refreshQuote()
                }
            }

            SwapInputContract.Event.DestinationCurrencyClicked -> withLoadedState { state ->
                val currencies = state.tradingPairs
                    .filter { it.baseCurrency == state.selectedPair.baseCurrency }
                    .map { it.termCurrency }
                    .distinct()

                setEffect { SwapInputContract.Effect.DestinationSelection(currencies) }
            }

            is SwapInputContract.Event.DestinationCurrencyChanged -> withLoadedState { state ->
                val pairsWithSelectedBaseCurrency = state.tradingPairs.filter {
                    it.baseCurrency == state.selectedPair.baseCurrency
                }

                val newSelectedPair = pairsWithSelectedBaseCurrency.find {
                    it.termCurrency == event.currencyCode
                } ?: state.selectedPair

                setState { state.copy(selectedPair = newSelectedPair) }
                refreshQuote()
            }

            /*
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

    private fun loadSupportedCurrencies() {
        callApi(
            endState = { currentState },
            startState = { currentState },
            action = { swapApi.getSupportedTradingPairs() },
            callback = {
                when (it.status) {
                    Status.SUCCESS -> {
                        val tradingPairs = it.data ?: emptyList()
                        val selectedPair = tradingPairs.firstOrNull()

                        if (selectedPair == null) {
                            setEffect {
                                SwapInputContract.Effect.ShowToast(
                                    getString(R.string.FabriikApi_DefaultError)
                                )
                            }
                            return@callApi
                        }

                        setState {
                            SwapInputContract.State.Loaded(
                                tradingPairs = tradingPairs,
                                selectedPair = selectedPair
                            )
                        }

                        getWalletBalance(selectedPair.baseCurrency)
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

    private fun getWalletBalance(currencyCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val wallet = breadBox.wallet(currencyCode).firstOrNull()
            if (wallet != null) {
                withLoadedState {
                    setState {
                        it.copy(
                            sourceCurrencyBalance = wallet.balance.toBigDecimal()
                        )
                    }
                }
            }
        }
    }

    private fun refreshQuote() = withLoadedState { state ->
        callApi(
            endState = { currentState },
            startState = { state.copy(quoteState = SwapInputContract.QuoteState.Loading) },
            action = { swapApi.getQuote(state.selectedPair) },
            callback = {
                when (it.status) {
                    Status.SUCCESS -> withLoadedState { latestState ->
                        setState {
                            latestState.copy(
                                quoteState = SwapInputContract.QuoteState.Loaded(
                                    sellRate = it.data!!.closeBid,
                                    buyRate = it.data!!.closeAsk,
                                    timerTimestamp = it.data!!.timestamp
                                )
                            )
                        }
                        //setupTimer()
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

    private fun setupTimer() = withLoadedQuoteState { state, quoteState ->
        val targetTimestamp = quoteState.timerTimestamp
        val currentTimestamp = System.currentTimeMillis()
        val diffSec = TimeUnit.MILLISECONDS.toSeconds(targetTimestamp - currentTimestamp)

        if (diffSec <= 0) {
            onTimerCompleted()
            return@withLoadedQuoteState
        }

        viewModelScope.launch {
            (diffSec downTo 0)
                .asSequence()
                .asFlow()
                .onEach { delay(1000) }
                .collect {
                    if (it == 0L) {
                        onTimerCompleted()
                    } else {
                        setState {
                            state.copy(timer = it.toInt())
                        }
                    }
                }
        }
    }

    private fun onTimerCompleted() {
        refreshQuote()
    }

    private fun withLoadedState(unit: (SwapInputContract.State.Loaded) -> Unit) {
        val state = currentState
        if (state is SwapInputContract.State.Loaded) {
            unit(state)
        }
    }

    private fun withLoadedQuoteState(unit: (SwapInputContract.State.Loaded, SwapInputContract.QuoteState.Loaded) -> Unit) =
        withLoadedState {
            val quoteState = it.quoteState
            if (quoteState is SwapInputContract.QuoteState.Loaded) {
                unit(it, quoteState)
            }
        }

    /*private fun onBaseCurrencyFiatChanged(amount: BigDecimal) {

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

    }*/

    companion object {
        const val QUOTE_TIMER = 15
    }
}