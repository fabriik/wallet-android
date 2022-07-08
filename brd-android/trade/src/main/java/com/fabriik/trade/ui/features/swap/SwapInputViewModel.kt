package com.fabriik.trade.ui.features.swap

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.breadwallet.breadbox.*
import com.breadwallet.tools.manager.BRSharedPrefs
import com.breadwallet.tools.security.ProfileManager
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.min
import com.fabriik.trade.R
import com.fabriik.trade.data.SwapApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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

    private val breadBox by kodein.instance<BreadBox>()
    private val profileManager by kodein.instance<ProfileManager>()
    private val swapApi = SwapApi.create(application)

    private val currentLoadedState: SwapInputContract.State.Loaded?
        get() = state.value as SwapInputContract.State.Loaded?

    private val maxAmountLimit = /*profileManager.getProfile().limit ?: BigDecimal.ZERO*/ BigDecimal.TEN //todo: use property from profile
    /*private val fiatIso = BRSharedPrefs.getPreferredFiatIso()

    private val ratesRepository by kodein.instance<RatesRepository>()
    private val swapAmountCalculator = SwapAmountCalculator(ratesRepository)*/

    private var currentTimerJob: Job? = null

    init {
        loadInitialData()
    }

    override fun createInitialState() = SwapInputContract.State.Loading

    override fun handleEvent(event: SwapInputContract.Event) {
        when (event) {
            SwapInputContract.Event.DismissClicked ->
                setEffect { SwapInputContract.Effect.Dismiss }

            SwapInputContract.Event.ConfirmClicked -> currentLoadedState?.let {
                setEffect {
                    SwapInputContract.Effect.ContinueToSwapProcessing(
                        sourceCurrency = it.sourceCryptoCurrency,
                        destinationCurrency = it.destinationCryptoCurrency
                    )
                }
            }

            SwapInputContract.Event.ReplaceCurrenciesClicked ->
                onReplaceCurrenciesClicked()

            SwapInputContract.Event.SourceCurrencyClicked ->
                onSourceCurrencyClicked()

            is SwapInputContract.Event.SourceCurrencyChanged ->
                onSourceCurrencyChanged(event.currencyCode)

            SwapInputContract.Event.DestinationCurrencyClicked ->
                onDestinationCurrencyClicked()

            is SwapInputContract.Event.DestinationCurrencyChanged ->
                onDestinationCurrencyChanged(event.currencyCode)

            is SwapInputContract.Event.OnMinAmountClicked ->
                onMinAmountClicked()

            is SwapInputContract.Event.OnMaxAmountClicked ->
                onMaxAmountClicked()

            is SwapInputContract.Event.SourceCurrencyFiatAmountChange ->
                onSourceCurrencyFiatAmountChanged(event.amount)

            is SwapInputContract.Event.SourceCurrencyCryptoAmountChange ->
                onSourceCurrencyCryptoAmountChanged(event.amount)

            is SwapInputContract.Event.DestinationCurrencyFiatAmountChange ->
                onDestinationCurrencyFiatAmountChanged(event.amount)

            is SwapInputContract.Event.DestinationCurrencyCryptoAmountChange ->
                onDestinationCurrencyCryptoAmountChanged(event.amount)
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            val tradingPairs = swapApi.getTradingPairs().data ?: emptyList()
            if (tradingPairs.isEmpty()) {
                showErrorState()
                return@launch
            }

            val selectedPair = tradingPairs.first()
            val selectedPairQuote = swapApi.getQuote(selectedPair).data
            if (selectedPairQuote == null) {
                showErrorState()
                return@launch
            }

            val sourceCryptoBalance = loadCryptoBalance(selectedPair.baseCurrency)
            if (sourceCryptoBalance == null) {
                showErrorState()
                return@launch
            }

            setState {
                SwapInputContract.State.Loaded(
                    tradingPairs = tradingPairs,
                    selectedPair = selectedPair,
                    fiatCurrency = BRSharedPrefs.getPreferredFiatIso(),
                    quoteResponse = selectedPairQuote,
                    sourceCryptoCurrency = selectedPair.baseCurrency,
                    destinationCryptoCurrency = selectedPair.termCurrency,
                    sourceCryptoBalance = sourceCryptoBalance,
                    cryptoExchangeRate = selectedPairQuote.closeAsk
                )
            }

            startQuoteTimer()
        }
    }

    private fun showErrorState() {
        setState { SwapInputContract.State.Error }
        setEffect {
            SwapInputContract.Effect.ShowToast(
                getString(R.string.FabriikApi_DefaultError)
            )
        }
    }

    private suspend fun loadCryptoBalance(currencyCode: String): BigDecimal? {
        val wallet = breadBox.wallets()
            .first()
            .find { it.currency.code.equals(currencyCode, ignoreCase = true) }

        return wallet?.balance?.toBigDecimal()
    }

    private fun onReplaceCurrenciesClicked() {
        val currentData = currentLoadedState ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val balance = loadCryptoBalance(currentData.destinationCryptoCurrency) ?: return@launch

            currentLoadedState?.let {
                val change = it.copy(
                    cryptoExchangeRate = when (it.sourceCryptoCurrency) {
                        it.selectedPair.baseCurrency -> BigDecimal.ONE / it.quoteResponse.closeAsk
                        else -> it.quoteResponse.closeAsk
                    },
                    sourceCryptoBalance = balance,
                    sourceCryptoCurrency = currentData.destinationCryptoCurrency,
                    destinationCryptoCurrency = currentData.sourceCryptoCurrency
                )
                setState { change }
            }
        }
    }

    private fun startQuoteTimer() {
        currentTimerJob?.cancel()

        val state = currentLoadedState ?: return
        val targetTimestamp = state.quoteResponse.timestamp
        val currentTimestamp = System.currentTimeMillis()
        val diffSec = TimeUnit.MILLISECONDS.toSeconds(targetTimestamp - currentTimestamp)

        currentTimerJob = viewModelScope.launch {
            (diffSec downTo 0)
                .asSequence()
                .asFlow()
                .onStart { setEffect { SwapInputContract.Effect.UpdateTimer(QUOTE_TIMER) } }
                .onEach { delay(1000) }
                .collect {
                    if (it == 0L) {
                        requestNewQuote()
                    } else {
                        setEffect { SwapInputContract.Effect.UpdateTimer(it.toInt()) }
                    }
                }
        }
    }

    private fun requestNewQuote() {
        viewModelScope.launch {
            val state = currentLoadedState ?: return@launch
            setState { state.copy(cryptoExchangeRateLoading = true) }

            val response = swapApi.getQuote(state.selectedPair)
            when (response.status) {
                Status.SUCCESS -> {
                    val latestState = currentLoadedState ?: return@launch
                    setState {
                        latestState.copy(
                            cryptoExchangeRateLoading = false,
                            quoteResponse = requireNotNull(response.data)
                        )
                    }
                    startQuoteTimer()
                }
                Status.ERROR ->
                    setEffect {
                        SwapInputContract.Effect.ShowToast(
                            response.message ?: getString(R.string.FabriikApi_DefaultError)
                        )
                    }
            }
        }
    }

    private fun onSourceCurrencyClicked() {
        val state = currentLoadedState ?: return
        val currencies = state.tradingPairs
            .map { it.baseCurrency }
            .distinct()

        setEffect { SwapInputContract.Effect.SourceSelection(currencies) }
    }

    private fun onSourceCurrencyChanged(currencyCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val state = currentLoadedState ?: return@launch
            if (currencyCode.equals(state.sourceCryptoCurrency, true)) {
                return@launch
            }

            val newSelectedPair = state.tradingPairs.firstOrNull {
                currencyCode.equals(it.baseCurrency, true)
            } ?: state.selectedPair

            val sourceBalance = loadCryptoBalance(
                newSelectedPair.baseCurrency
            ) ?: BigDecimal.ZERO

            val latestState = currentLoadedState ?: return@launch
            setState {
                latestState.copy(
                    selectedPair = newSelectedPair,
                    sourceCryptoBalance = sourceBalance,
                    sourceCryptoCurrency = newSelectedPair.baseCurrency,
                    destinationCryptoCurrency = newSelectedPair.termCurrency,
                )
            }

            requestNewQuote()
        }
    }

    private fun onDestinationCurrencyClicked() {
        val state = currentLoadedState ?: return
        val currencies = state.tradingPairs
            .filter { it.baseCurrency == state.sourceCryptoCurrency }
            .map { it.termCurrency }
            .distinct()

        setEffect {
            SwapInputContract.Effect.DestinationSelection(
                currencies = currencies,
                sourceCurrency = state.sourceCryptoCurrency
            )
        }
    }

    private fun onDestinationCurrencyChanged(currencyCode: String) {
        val state = currentLoadedState ?: return
        if (currencyCode.equals(state.destinationCryptoCurrency, true)) {
            return
        }

        val newSelectedPair = state.tradingPairs.firstOrNull {
            currencyCode.equals(it.termCurrency, true) &&
                    state.sourceCryptoCurrency.equals(it.baseCurrency, true)
        } ?: state.selectedPair

        setState {
            state.copy(
                selectedPair = newSelectedPair,
                destinationCryptoCurrency = newSelectedPair.termCurrency
            )
        }

        requestNewQuote()
    }

    private fun onMinAmountClicked() {
        val state = currentLoadedState ?: return

        onSourceCurrencyCryptoAmountChanged(
            state.selectedPair.minAmount, false
        )
    }

    private fun onMaxAmountClicked() {
        val state = currentLoadedState ?: return

        onSourceCurrencyCryptoAmountChanged(
            min(state.sourceCryptoBalance, maxAmountLimit), false
        )
    }

    private fun onSourceCurrencyFiatAmountChanged(amount: BigDecimal) {
        val state = currentLoadedState ?: return

        setState {
            state.copy(
                sourceFiatAmount = amount,
                sourceCryptoAmount = amount,
                destinationFiatAmount = amount,
                destinationCryptoAmount = amount
            )
        }

        updateAmounts()
    }

    private fun onSourceCurrencyCryptoAmountChanged(amount: BigDecimal, changeByUser: Boolean = true) {
        val state = currentLoadedState ?: return

        setState {
            state.copy(
                sourceFiatAmount = amount,
                sourceCryptoAmount = amount,
                destinationFiatAmount = amount,
                destinationCryptoAmount = amount
            )
        }

        updateAmounts(changeByUser)
    }

    private fun onDestinationCurrencyFiatAmountChanged(amount: BigDecimal) {
        val state = currentLoadedState ?: return

        setState {
            state.copy(
                sourceFiatAmount = amount,
                sourceCryptoAmount = amount,
                destinationFiatAmount = amount,
                destinationCryptoAmount = amount
            )
        }

        updateAmounts()
    }

    private fun onDestinationCurrencyCryptoAmountChanged(amount: BigDecimal) {
        val state = currentLoadedState ?: return

        setState {
            state.copy(
                sourceFiatAmount = amount,
                sourceCryptoAmount = amount,
                destinationFiatAmount = amount,
                destinationCryptoAmount = amount
            )
        }

        updateAmounts()
    }

    private fun updateAmounts(changeByUser: Boolean = true) {
        val state = currentLoadedState ?: return

        setEffect { SwapInputContract.Effect.UpdateSourceFiatAmount(state.sourceFiatAmount) }
        setEffect { SwapInputContract.Effect.UpdateSourceCryptoAmount(state.sourceCryptoAmount) }
        setEffect { SwapInputContract.Effect.UpdateDestinationFiatAmount(state.destinationFiatAmount) }
        setEffect { SwapInputContract.Effect.UpdateDestinationCryptoAmount(state.destinationCryptoAmount) }

        if (changeByUser) {
            setEffect { SwapInputContract.Effect.DeselectMinMaxSwitchItems }
        }
    }

/*


    private fun onSourceCurrencyFiatAmountChanged(amount: BigDecimal) {
        calculateReceivingAmount(amount, false)
    }

    private fun onSourceCurrencyCryptoAmountChanged(amount: BigDecimal) {
        calculateReceivingAmount(amount, true)
    }

    private fun onDestinationCurrencyFiatAmountChanged(amount: BigDecimal) {
        calculateSendingAmount(amount, false)
    }

    private fun onDestinationCurrencyCryptoAmountChanged(amount: BigDecimal) {
        calculateSendingAmount(amount, true)
    }

    private fun calculateReceivingAmount(sourceAmount: BigDecimal, isCryptoAmount: Boolean) =
        withLoadedQuoteState { state, quoteState ->
            val sourceFiatAmount = if (isCryptoAmount) {
                swapAmountCalculator.convertCryptoToFiat(
                    cryptoAmount = sourceAmount,
                    cryptoCode = state.selectedPair.baseCurrency,
                    fiatCode = fiatIso
                )
            } else {
                sourceAmount
            }

            val sourceCryptoAmount = if (isCryptoAmount) {
                sourceAmount
            } else {
                swapAmountCalculator.convertFiatToCrypto(
                    fiatAmount = sourceAmount,
                    cryptoCode = state.selectedPair.baseCurrency,
                    fiatCode = fiatIso
                )
            }

            estimateSendingFee(sourceCryptoAmount) { sendingFee ->
                var destinationCryptoAmount = swapAmountCalculator.convertCryptoToCrypto(
                    quoteState = quoteState,
                    tradingPair = state.selectedPair,
                    cryptoAmount = sourceCryptoAmount - (sendingFee?.cryptoAmount ?: BigDecimal.ZERO),
                    fromCryptoCode = state.selectedPair.baseCurrency
                )

                estimateReceivingFee(destinationCryptoAmount) { receivingFee ->
                    destinationCryptoAmount -= (receivingFee?.cryptoAmount ?: BigDecimal.ZERO)

                    val destinationFiatAmount = swapAmountCalculator.convertCryptoToFiat(
                        cryptoAmount = destinationCryptoAmount,
                        cryptoCode = state.selectedPair.termCurrency,
                        fiatCode = fiatIso
                    )

                    setEffect { SwapInputContract.Effect.UpdateSourceFiatAmount(sourceFiatAmount) }
                    setEffect { SwapInputContract.Effect.UpdateSourceCryptoAmount(sourceCryptoAmount) }
                    setEffect {
                        SwapInputContract.Effect.UpdateDestinationFiatAmount(
                            destinationFiatAmount
                        )
                    }
                    setEffect {
                        SwapInputContract.Effect.UpdateDestinationCryptoAmount(
                            destinationCryptoAmount
                        )
                    }
                }
            }
        }

    private fun calculateSendingAmount(receivedAmount: BigDecimal, isCryptoAmount: Boolean) =
        withLoadedQuoteState { state, quoteState ->
            val receivedFiatAmount = if (isCryptoAmount) {
                swapAmountCalculator.convertCryptoToFiat(
                    cryptoAmount = receivedAmount,
                    cryptoCode = state.selectedPair.termCurrency,
                    fiatCode = fiatIso
                )
            } else {
                receivedAmount
            }

            val receivedCryptoAmount = if (isCryptoAmount) {
                receivedAmount
            } else {
                swapAmountCalculator.convertFiatToCrypto(
                    fiatAmount = receivedAmount,
                    cryptoCode = state.selectedPair.termCurrency,
                    fiatCode = fiatIso
                )
            }

            estimateReceivingFee(receivedCryptoAmount) { receivingFee ->
                var sourceCryptoAmount = swapAmountCalculator.convertCryptoToCrypto(
                    quoteState = quoteState,
                    tradingPair = state.selectedPair,
                    cryptoAmount = receivedCryptoAmount + (receivingFee?.cryptoAmount ?: BigDecimal.ZERO),
                    fromCryptoCode = state.selectedPair.baseCurrency
                )

                estimateSendingFee(sourceCryptoAmount) { sendingFee ->
                    sourceCryptoAmount += (sendingFee?.cryptoAmount ?: BigDecimal.ZERO)

                    val sourceFiatAmount = swapAmountCalculator.convertCryptoToFiat(
                        cryptoAmount = sourceCryptoAmount,
                        cryptoCode = state.selectedPair.baseCurrency,
                        fiatCode = fiatIso
                    )

                    setEffect { SwapInputContract.Effect.UpdateSourceFiatAmount(sourceFiatAmount) }
                    setEffect { SwapInputContract.Effect.UpdateSourceCryptoAmount(sourceCryptoAmount) }
                    setEffect { SwapInputContract.Effect.UpdateDestinationFiatAmount(receivedFiatAmount) }
                    setEffect { SwapInputContract.Effect.UpdateDestinationCryptoAmount(receivedCryptoAmount) }
                }
            }
        }


    private fun estimateSendingFee(
        amount: BigDecimal,
        callback: (SwapInputContract.NetworkFeeData?) -> Unit
    ) = withLoadedState { state ->
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
                callback(fee)
            }
        }
    }

    private fun estimateReceivingFee(
        amount: BigDecimal,
        callback: (SwapInputContract.NetworkFeeData?) -> Unit
    ) = withLoadedState { state ->
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
                callback(fee)
            }
        }
    }

    private fun estimateFee(
        currencyCode: String,
        amountBig: BigDecimal,
        callback: (SwapInputContract.NetworkFeeData?) -> Unit
    ) {
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
    }*/

    companion object {
        const val QUOTE_TIMER = 15
    }
}