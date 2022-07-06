package com.fabriik.trade.ui.features.swap

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.breadwallet.breadbox.*
import com.breadwallet.crypto.Address
import com.breadwallet.crypto.AddressScheme
import com.breadwallet.crypto.Amount
import com.breadwallet.crypto.errors.FeeEstimationError
import com.breadwallet.repository.RatesRepository
import com.breadwallet.tools.manager.BRSharedPrefs
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.min
import com.fabriik.trade.R
import com.fabriik.trade.data.SwapApi
import com.fabriik.trade.utils.SwapAmountCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

class SwapInputViewModel(
    application: Application
) : FabriikViewModel<SwapInputContract.State, SwapInputContract.Event, SwapInputContract.Effect>(
    application
), KodeinAware {

    override val kodein by closestKodein { application }

    private val fiatIso = BRSharedPrefs.getPreferredFiatIso()
    private val swapApi = SwapApi.create(application)
    private val breadBox by kodein.instance<BreadBox>()
    private val ratesRepository by kodein.instance<RatesRepository>()
    private val swapAmountCalculator = SwapAmountCalculator(ratesRepository)

    init {
        loadSupportedCurrencies()
    }

    override fun createInitialState() = SwapInputContract.State.Empty

    override fun handleEvent(event: SwapInputContract.Event) {
        when (event) {
            SwapInputContract.Event.DismissClicked ->
                setEffect { SwapInputContract.Effect.Dismiss }

            SwapInputContract.Event.OriginCurrencyClicked ->
                onSourceCurrencyClicked()

            is SwapInputContract.Event.OriginCurrencyChanged ->
                onSourceCurrencyChanged(event.currencyCode)

            SwapInputContract.Event.DestinationCurrencyClicked ->
                onDestinationCurrencyClicked()

            is SwapInputContract.Event.DestinationCurrencyChanged ->
                onDestinationCurrencyChanged(event.currencyCode)

            is SwapInputContract.Event.OnMinAmountClicked ->
                onMinAmountClicked()

            is SwapInputContract.Event.OnMaxAmountClicked ->
                onMaxAmountClicked()

            is SwapInputContract.Event.ReplaceCurrenciesClicked ->
                onReplaceCurrenciesClicked()

            is SwapInputContract.Event.OriginCurrencyFiatAmountChange -> {
                onSourceCurrencyFiatAmountChanged(event.amount)
                setEffect { SwapInputContract.Effect.DeselectMinMaxSwitchItems }
            }

            is SwapInputContract.Event.OriginCurrencyCryptoAmountChange -> {
                onSourceCurrencyCryptoAmountChanged(event.amount)
                setEffect { SwapInputContract.Effect.DeselectMinMaxSwitchItems }
            }

            is SwapInputContract.Event.DestinationCurrencyFiatAmountChange -> {
                onDestinationCurrencyFiatAmountChanged(event.amount)
                setEffect { SwapInputContract.Effect.DeselectMinMaxSwitchItems }
            }

            is SwapInputContract.Event.DestinationCurrencyCryptoAmountChange -> {
                onDestinationCurrencyCryptoAmountChanged(event.amount)
                setEffect { SwapInputContract.Effect.DeselectMinMaxSwitchItems }
            }
        }
    }

    private fun onReplaceCurrenciesClicked() = withLoadedState { state ->
        val sourceCurrency = state.selectedPair.baseCurrency
        val destinationCurrency = state.selectedPair.termCurrency

        val newTradingPair = state.tradingPairs.find {
            it.baseCurrency == destinationCurrency && it.termCurrency == sourceCurrency
        }

        if (newTradingPair == null) {
            setEffect {
                SwapInputContract.Effect.ShowToast(
                    getString(R.string.Swap_Input_SwapNotSupported)
                )
            }
        } else {
            setState { state.copy(selectedPair = newTradingPair) }
            refreshQuote()
        }
    }

    private fun onSourceCurrencyClicked() = withLoadedState { state ->
        val currencies = state.tradingPairs
            .map { it.baseCurrency }
            //todo: filter enabled wallets
            .distinct()

        setEffect { SwapInputContract.Effect.OriginSelection(currencies) }
    }

    private fun onDestinationCurrencyClicked() = withLoadedState { state ->
        val currencies = state.tradingPairs
            .filter { it.baseCurrency == state.selectedPair.baseCurrency }
            .map { it.termCurrency }
            .distinct()

        setEffect { SwapInputContract.Effect.DestinationSelection(currencies) }
    }

    private fun onSourceCurrencyChanged(currencyCode: String) = withLoadedState { state ->
        val pairsWithSelectedBaseCurrency = state.tradingPairs.filter {
            it.baseCurrency == currencyCode
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

    private fun onDestinationCurrencyChanged(currencyCode: String) = withLoadedState { state ->
        val pairsWithSelectedBaseCurrency = state.tradingPairs.filter {
            it.baseCurrency == state.selectedPair.baseCurrency
        }

        val newSelectedPair = pairsWithSelectedBaseCurrency.find {
            it.termCurrency == currencyCode
        } ?: state.selectedPair

        setState { state.copy(selectedPair = newSelectedPair) }
        refreshQuote()
    }

    private fun onMinAmountClicked() = withLoadedState { state ->
        onSourceCurrencyCryptoAmountChanged(
            min(state.sourceCurrencyBalance, state.selectedPair.minAmount)
        )
    }

    private fun onMaxAmountClicked() = withLoadedState { state ->
        onSourceCurrencyCryptoAmountChanged(
            min(state.sourceCurrencyBalance, state.selectedPair.maxAmount)
        )
    }

    private fun onSourceCurrencyFiatAmountChanged(amount: BigDecimal) =
        withLoadedQuoteState { state, quoteState ->
            val sourceCryptoAmount = swapAmountCalculator.convertFiatToCrypto(
                fiatAmount = amount,
                cryptoCode = state.selectedPair.baseCurrency,
                fiatCode = fiatIso
            )

            val destinationCryptoAmount = swapAmountCalculator.convertCryptoToCrypto(
                quoteState = quoteState,
                tradingPair = state.selectedPair,
                cryptoAmount = sourceCryptoAmount,
                fromCryptoCode = state.selectedPair.baseCurrency
            )

            val destinationFiatAmount = swapAmountCalculator.convertCryptoToFiat(
                cryptoAmount = destinationCryptoAmount,
                cryptoCode = state.selectedPair.termCurrency,
                fiatCode = fiatIso
            )

            estimateSendingFee(sourceCryptoAmount)
            estimateReceivingFee(destinationCryptoAmount)

            setEffect { SwapInputContract.Effect.UpdateSourceCryptoAmount(sourceCryptoAmount) }
            setEffect { SwapInputContract.Effect.UpdateDestinationFiatAmount(destinationFiatAmount) }
            setEffect {
                SwapInputContract.Effect.UpdateDestinationCryptoAmount(
                    destinationCryptoAmount
                )
            }
        }

    private fun onSourceCurrencyCryptoAmountChanged(amount: BigDecimal) =
        withLoadedQuoteState { state, quoteState ->
            val sourceFiatAmount = swapAmountCalculator.convertCryptoToFiat(
                cryptoAmount = amount,
                cryptoCode = state.selectedPair.baseCurrency,
                fiatCode = fiatIso
            )

            val destinationCryptoAmount = swapAmountCalculator.convertCryptoToCrypto(
                quoteState = quoteState,
                tradingPair = state.selectedPair,
                cryptoAmount = amount,
                fromCryptoCode = state.selectedPair.baseCurrency
            )

            val destinationFiatAmount = swapAmountCalculator.convertCryptoToFiat(
                cryptoAmount = destinationCryptoAmount,
                cryptoCode = state.selectedPair.termCurrency,
                fiatCode = fiatIso
            )

            estimateSendingFee(amount)
            estimateReceivingFee(destinationCryptoAmount)

            setEffect { SwapInputContract.Effect.UpdateSourceFiatAmount(sourceFiatAmount) }
            setEffect { SwapInputContract.Effect.UpdateDestinationFiatAmount(destinationFiatAmount) }
            setEffect {
                SwapInputContract.Effect.UpdateDestinationCryptoAmount(
                    destinationCryptoAmount
                )
            }
        }

    private fun onDestinationCurrencyFiatAmountChanged(amount: BigDecimal) =
        withLoadedQuoteState { state, quoteState ->
            val destinationCryptoAmount = swapAmountCalculator.convertFiatToCrypto(
                fiatAmount = amount,
                cryptoCode = state.selectedPair.termCurrency,
                fiatCode = fiatIso
            )

            val sourceCryptoAmount = swapAmountCalculator.convertCryptoToCrypto(
                quoteState = quoteState,
                tradingPair = state.selectedPair,
                cryptoAmount = destinationCryptoAmount,
                fromCryptoCode = state.selectedPair.termCurrency
            )

            val sourceFiatAmount = swapAmountCalculator.convertCryptoToFiat(
                cryptoAmount = sourceCryptoAmount,
                cryptoCode = state.selectedPair.baseCurrency,
                fiatCode = fiatIso
            )

            estimateSendingFee(sourceCryptoAmount)
            estimateReceivingFee(destinationCryptoAmount)

            setEffect { SwapInputContract.Effect.UpdateSourceFiatAmount(sourceFiatAmount) }
            setEffect { SwapInputContract.Effect.UpdateSourceCryptoAmount(sourceCryptoAmount) }
            setEffect {
                SwapInputContract.Effect.UpdateDestinationCryptoAmount(
                    destinationCryptoAmount
                )
            }
        }

    private fun onDestinationCurrencyCryptoAmountChanged(amount: BigDecimal) =
        withLoadedQuoteState { state, quoteState ->
            val destinationFiatAmount = swapAmountCalculator.convertCryptoToFiat(
                cryptoAmount = amount,
                cryptoCode = state.selectedPair.termCurrency,
                fiatCode = fiatIso
            )

            val sourceCryptoAmount = swapAmountCalculator.convertCryptoToCrypto(
                quoteState = quoteState,
                tradingPair = state.selectedPair,
                cryptoAmount = amount,
                fromCryptoCode = state.selectedPair.termCurrency
            )

            val sourceFiatAmount = swapAmountCalculator.convertCryptoToFiat(
                cryptoAmount = sourceCryptoAmount,
                cryptoCode = state.selectedPair.baseCurrency,
                fiatCode = fiatIso
            )

            estimateSendingFee(sourceCryptoAmount)
            estimateReceivingFee(amount)

            setEffect { SwapInputContract.Effect.UpdateSourceFiatAmount(sourceFiatAmount) }
            setEffect { SwapInputContract.Effect.UpdateSourceCryptoAmount(sourceCryptoAmount) }
            setEffect { SwapInputContract.Effect.UpdateDestinationFiatAmount(destinationFiatAmount) }
        }

    private fun loadSupportedCurrencies() {
        callApi(
            endState = { currentState },
            startState = { currentState },
            action = { swapApi.getTradingPairs() },
            callback = {
                when (it.status) {
                    Status.SUCCESS -> {
                        val tradingPairs = it.data ?: emptyList()
                        val selectedPair = tradingPairs[0]

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
            val wallet = breadBox.wallets()
                .first()
                .find { it.currency.code.equals(currencyCode, ignoreCase = true) }

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
                        setupTimer()
                        //todo: refresh amounts
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

        setState {
            state.copy(timer = diffSec.toInt())
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
                        withLoadedState { latestState ->
                            setState {
                                latestState.copy(timer = it.toInt())
                            }
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

    private fun estimateSendingFee(amount: BigDecimal) = withLoadedState{ state ->
        estimateFee(
            currencyCode = state.selectedPair.baseCurrency,
            amountBig = amount
        ) { fee ->
            withLoadedState { state ->
                setState {
                    state.copy(
                        sendingNetworkFee = fee
                    )
                }
            }
        }
    }

    private fun estimateReceivingFee(amount: BigDecimal) = withLoadedState{ state ->
        estimateFee(
            currencyCode = state.selectedPair.termCurrency,
            amountBig = amount
        ) { fee ->
            withLoadedState { state ->
                setState {
                    state.copy(
                        receivingNetworkFee = fee
                    )
                }
            }
        }
    }

    private fun estimateFee(currencyCode: String, amountBig: BigDecimal, callback: (SwapInputContract.NetworkFeeData?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val wallet = breadBox.wallet(currencyCode).first()
            val amount = Amount.create(amountBig.toDouble(), wallet.unit)
            val address = if (wallet.currency.isBitcoin()) {
            wallet.getTargetForScheme(
                when (BRSharedPrefs.getIsSegwitEnabled()) {
                    true -> AddressScheme.BTC_SEGWIT
                    false -> AddressScheme.BTC_LEGACY
                }
            )
        } else {
            wallet.target
        }

            try {
                val data = wallet.estimateFee(address, amount)
                val cryptoFee = data.fee.toBigDecimal()
                val cryptoCurrency = data.currency.code
                val fiatFee = ratesRepository.getFiatForCrypto(
                    cryptoAmount = cryptoFee,
                    cryptoCode = cryptoCurrency,
                    fiatCode = fiatIso
                ) ?: return@launch

                callback(
                    SwapInputContract.NetworkFeeData(
                        fiatAmount = fiatFee,
                        fiatCurrency = fiatIso,
                        cryptoAmount = cryptoFee,
                        cryptoCurrency = cryptoCurrency
                    )
                )

                //check(!fee.isZero()) { "Estimated fee was zero" }
                //E.OnNetworkFeeUpdated(effect.address, effect.amount, fee, data)
            } catch (e: FeeEstimationError) {
                Log.i("Swap.estimateFee", "Failed get fee estimate", e)
                callback(null)
                //E.OnInsufficientBalance
            } catch (e: IllegalStateException) {
                Log.i("Swap.estimateFee", "Failed get fee estimate", e)
                callback(null)
                /*logError("Failed get fee estimate", e)
                E.OnNetworkFeeError*/
            }
        }
    }

    companion object {
        const val QUOTE_TIMER = 15
    }
}