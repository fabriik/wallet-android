package com.fabriik.trade.ui.features.swap

import android.app.Application
import android.os.UserManager
import android.security.keystore.UserNotAuthenticatedException
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.breadwallet.breadbox.*
import com.breadwallet.crypto.Address
import com.breadwallet.crypto.AddressScheme
import com.breadwallet.crypto.Amount
import com.breadwallet.crypto.TransferFeeBasis
import com.breadwallet.crypto.errors.FeeEstimationError
import com.breadwallet.ext.isZero
import com.breadwallet.logger.logError
import com.breadwallet.repository.RatesRepository
import com.breadwallet.tools.manager.BRSharedPrefs
import com.breadwallet.tools.security.BrdUserManager
import com.breadwallet.tools.security.ProfileManager
import com.fabriik.common.data.Status
import com.fabriik.common.data.model.nextExchangeLimit
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.min
import com.fabriik.trade.R
import com.fabriik.trade.data.SwapApi
import com.fabriik.trade.data.model.AmountData
import com.fabriik.trade.data.response.CreateOrderResponse
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
import java.lang.Exception
import java.math.RoundingMode

class SwapInputViewModel(
    application: Application
) : FabriikViewModel<SwapInputContract.State, SwapInputContract.Event, SwapInputContract.Effect>(
    application
), KodeinAware {

    override val kodein by closestKodein { application }

    private val swapApi by kodein.instance<SwapApi>()
    private val breadBox by kodein.instance<BreadBox>()
    private val userManager by kodein.instance<BrdUserManager>()
    private val profileManager by kodein.instance<ProfileManager>()

    private val currentLoadedState: SwapInputContract.State.Loaded?
        get() = state.value as SwapInputContract.State.Loaded?

    private val maxAmountLimitFiat = profileManager.getProfile().nextExchangeLimit()
    private val ratesRepository by kodein.instance<RatesRepository>()

    private var currentTimerJob: Job? = null

    init {
        loadInitialData()
    }

    override fun createInitialState() = SwapInputContract.State.Loading

    override fun handleEvent(event: SwapInputContract.Event) {
        when (event) {
            SwapInputContract.Event.DismissClicked ->
                setEffect { SwapInputContract.Effect.Dismiss }

            SwapInputContract.Event.ConfirmClicked ->
                onConfirmClicked()

            SwapInputContract.Event.OnConfirmationDialogConfirmed ->
                createSwapOrder()

            SwapInputContract.Event.SourceCurrencyClicked ->
                onSourceCurrencyClicked()

            SwapInputContract.Event.DestinationCurrencyClicked ->
                onDestinationCurrencyClicked()

            SwapInputContract.Event.ReplaceCurrenciesClicked ->
                onReplaceCurrenciesClicked()

            is SwapInputContract.Event.OnCurrenciesReplaceAnimationCompleted ->
                onReplaceCurrenciesAnimationCompleted(event.stateChange)

            is SwapInputContract.Event.SourceCurrencyChanged ->
                onSourceCurrencyChanged(event.currencyCode)

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
            min(state.selectedPair.minAmount, state.sourceCryptoBalance), false
        )
    }

    private fun onMaxAmountClicked() {
        val state = currentLoadedState ?: return

        val maxAmountLimitCrypto = maxAmountLimitFiat.toCrypto(
            cryptoCurrency = state.sourceCryptoCurrency,
            fiatCurrency = state.fiatCurrency
        )

        onSourceCurrencyCryptoAmountChanged(
            min(state.sourceCryptoBalance, maxAmountLimitCrypto), false
        )
    }

    private fun onReplaceCurrenciesClicked() {
        val currentData = currentLoadedState ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val balance = loadCryptoBalance(currentData.destinationCryptoCurrency) ?: return@launch
            val destinationAddress =
                loadAddress(currentData.destinationCryptoCurrency) ?: return@launch

            currentLoadedState?.let {
                val stateChange = it.copy(
                    cryptoExchangeRate = when (it.sourceCryptoCurrency) {
                        it.selectedPair.baseCurrency -> BigDecimal.ONE.divide(
                            it.quoteResponse.closeBid,
                            10,
                            RoundingMode.HALF_UP
                        )
                        else -> it.quoteResponse.closeAsk
                    },
                    sourceFiatAmount = it.destinationFiatAmount,
                    sourceCryptoAmount = it.destinationCryptoAmount,
                    destinationFiatAmount = it.sourceFiatAmount,
                    destinationCryptoAmount = it.sourceCryptoAmount,
                    sourceCryptoBalance = balance,
                    sourceCryptoCurrency = currentData.destinationCryptoCurrency,
                    destinationCryptoCurrency = currentData.sourceCryptoCurrency,
                    destinationAddress = destinationAddress.toString(),
                    sendingNetworkFee = currentData.receivingNetworkFee,
                    receivingNetworkFee = currentData.sendingNetworkFee,
                )

                setEffect { SwapInputContract.Effect.CurrenciesReplaceAnimation(stateChange) }
            }
        }
    }

    private fun onReplaceCurrenciesAnimationCompleted(state: SwapInputContract.State.Loaded) {
        setState { state }
        updateAmounts()
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

    private fun showErrorState() {
        setState { SwapInputContract.State.Error }
        setEffect {
            SwapInputContract.Effect.ShowToast(
                getString(R.string.FabriikApi_DefaultError)
            )
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            val pairsResponse = swapApi.getTradingPairs()
            val tradingPairs = pairsResponse.data ?: emptyList()

            if (pairsResponse.status == Status.ERROR || tradingPairs.isEmpty()) {
                showErrorState()
                return@launch
            }

            val selectedPair = tradingPairs.first {
                it.baseCurrency == "BTC"
            }

            val quoteResponse = swapApi.getQuote(selectedPair)
            val selectedPairQuote = swapApi.getQuote(selectedPair).data
            if (quoteResponse.status == Status.ERROR || selectedPairQuote == null) {
                showErrorState()
                return@launch
            }

            val sourceCryptoBalance = loadCryptoBalance(selectedPair.baseCurrency)
            if (sourceCryptoBalance == null) {
                showErrorState()
                return@launch
            }

            val destinationAddress = loadAddress(selectedPair.termCurrency)
            if (destinationAddress == null) {
                showErrorState()
                return@launch
            }

            setState {
                SwapInputContract.State.Loaded(
                    tradingPairs = tradingPairs,
                    selectedPair = selectedPair,
                    fiatCurrency = "USD",
                    quoteResponse = selectedPairQuote,
                    sourceCryptoCurrency = selectedPair.baseCurrency,
                    destinationCryptoCurrency = selectedPair.termCurrency,
                    destinationAddress = destinationAddress.toString(),
                    sourceCryptoBalance = sourceCryptoBalance,
                    cryptoExchangeRate = selectedPairQuote.closeAsk
                )
            }

            startQuoteTimer()
        }
    }

    private suspend fun loadCryptoBalance(currencyCode: String): BigDecimal? {
        val wallet = breadBox.wallets()
            .first()
            .find { it.currency.code.equals(currencyCode, ignoreCase = true) }

        return wallet?.balance?.toBigDecimal()
    }

    private suspend fun loadAddress(currencyCode: String): Address? {
        val wallet = breadBox.wallets()
            .first()
            .find {
                it.currency.code.equals(currencyCode, ignoreCase = true)
            } ?: return null

        return if (wallet.currency.isBitcoin()) {
            wallet.getTargetForScheme(
                when (BRSharedPrefs.getIsSegwitEnabled()) {
                    true -> AddressScheme.BTC_SEGWIT
                    false -> AddressScheme.BTC_LEGACY
                }
            )
        } else {
            wallet.target
        }
    }

    private fun onSourceCurrencyClicked() {
        val state = currentLoadedState ?: return
        val currencies = state.tradingPairs
            .map { it.baseCurrency }
            .distinct()

        setEffect { SwapInputContract.Effect.SourceSelection(currencies) }
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

    private fun onSourceCurrencyFiatAmountChanged(sourceFiatAmount: BigDecimal) {
        val state = currentLoadedState ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val sourceCryptoAmount = sourceFiatAmount.toCrypto(
                cryptoCurrency = state.sourceCryptoCurrency,
                fiatCurrency = state.fiatCurrency
            )

            val destCryptoAmountData = sourceCryptoAmount.convertSource(
                fromCryptoCurrency = state.sourceCryptoCurrency,
                toCryptoCurrency = state.destinationCryptoCurrency,
                rate = state.cryptoExchangeRate
            )

            val destCryptoAmount = destCryptoAmountData.third

            val destFiatAmount = destCryptoAmount.toFiat(
                cryptoCurrency = state.destinationCryptoCurrency,
                fiatCurrency = state.fiatCurrency
            )

            setState {
                state.copy(
                    destinationFiatAmount = destFiatAmount,
                    destinationCryptoAmount = destCryptoAmount,
                    sourceFiatAmount = sourceFiatAmount,
                    sourceCryptoAmount = sourceCryptoAmount,
                    receivingNetworkFee = destCryptoAmountData.second,
                    sendingNetworkFee = destCryptoAmountData.first
                ).validate()
            }

            updateAmounts()
        }
    }

    private fun onSourceCurrencyCryptoAmountChanged(
        sourceCryptoAmount: BigDecimal,
        changeByUser: Boolean = true
    ) {
        val state = currentLoadedState ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val sourceFiatAmount = sourceCryptoAmount.toFiat(
                cryptoCurrency = state.sourceCryptoCurrency,
                fiatCurrency = state.fiatCurrency
            )

            val destCryptoAmountData = sourceCryptoAmount.convertSource(
                fromCryptoCurrency = state.sourceCryptoCurrency,
                toCryptoCurrency = state.destinationCryptoCurrency,
                rate = state.cryptoExchangeRate
            )

            val destCryptoAmount = destCryptoAmountData.third

            val destFiatAmount = destCryptoAmount.toFiat(
                cryptoCurrency = state.destinationCryptoCurrency,
                fiatCurrency = state.fiatCurrency
            )

            setState {
                state.copy(
                    sourceFiatAmount = sourceFiatAmount,
                    destinationFiatAmount = destFiatAmount,
                    sourceCryptoAmount = sourceCryptoAmount,
                    destinationCryptoAmount = destCryptoAmount,
                    sendingNetworkFee = destCryptoAmountData.first,
                    receivingNetworkFee = destCryptoAmountData.second
                ).validate()
            }

            updateAmounts(changeByUser)
        }
    }

    private fun onDestinationCurrencyFiatAmountChanged(destFiatAmount: BigDecimal) {
        val state = currentLoadedState ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val destCryptoAmount = destFiatAmount.toCrypto(
                cryptoCurrency = state.destinationCryptoCurrency,
                fiatCurrency = state.fiatCurrency
            )

            val sourceCryptoAmountData = destCryptoAmount.convertDestination(
                fromCryptoCurrency = state.destinationCryptoCurrency,
                toCryptoCurrency = state.sourceCryptoCurrency,
                rate = state.cryptoExchangeRate
            )

            val sourceCryptoAmount = sourceCryptoAmountData.third

            val sourceFiatAmount = sourceCryptoAmount.toFiat(
                cryptoCurrency = state.sourceCryptoCurrency,
                fiatCurrency = state.fiatCurrency
            )

            setState {
                state.copy(
                    sourceFiatAmount = sourceFiatAmount,
                    destinationCryptoAmount = destCryptoAmount,
                    sourceCryptoAmount = sourceCryptoAmount,
                    destinationFiatAmount = destFiatAmount,
                    receivingNetworkFee = sourceCryptoAmountData.second,
                    sendingNetworkFee = sourceCryptoAmountData.first
                ).validate()
            }

            updateAmounts()
        }
    }

    private fun onDestinationCurrencyCryptoAmountChanged(destCryptoAmount: BigDecimal) {
        val state = currentLoadedState ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val destFiatAmount = destCryptoAmount.toFiat(
                cryptoCurrency = state.destinationCryptoCurrency,
                fiatCurrency = state.fiatCurrency
            )

            val sourceCryptoAmountData = destCryptoAmount.convertDestination(
                fromCryptoCurrency = state.destinationCryptoCurrency,
                toCryptoCurrency = state.sourceCryptoCurrency,
                rate = state.cryptoExchangeRate
            )

            val sourceCryptoAmount = sourceCryptoAmountData.third

            val sourceFiatAmount = sourceCryptoAmount.toFiat(
                cryptoCurrency = state.sourceCryptoCurrency,
                fiatCurrency = state.fiatCurrency
            )

            setState {
                state.copy(
                    sourceFiatAmount = sourceFiatAmount,
                    sourceCryptoAmount = sourceCryptoAmount,
                    destinationFiatAmount = destFiatAmount,
                    destinationCryptoAmount = destCryptoAmount,
                    sendingNetworkFee = sourceCryptoAmountData.first,
                    receivingNetworkFee = sourceCryptoAmountData.second
                ).validate()
            }

            updateAmounts()
        }
    }

    private fun onConfirmClicked() {
        val state = currentLoadedState
        val sendingFee = state?.sendingNetworkFee
        val receivingFee = state?.receivingNetworkFee

        if (sendingFee == null || receivingFee == null) {
            setEffect {
                SwapInputContract.Effect.ShowToast(
                    getString(R.string.FabriikApi_DefaultError)
                )
            }
            return
        }

        val toAmount = AmountData(
            fiatAmount = state.destinationFiatAmount,
            fiatCurrency = state.fiatCurrency,
            cryptoAmount = state.destinationCryptoAmount,
            cryptoCurrency = state.destinationCryptoCurrency
        )

        val fromAmount = AmountData(
            fiatAmount = state.sourceFiatAmount,
            fiatCurrency = state.fiatCurrency,
            cryptoAmount = state.sourceCryptoAmount,
            cryptoCurrency = state.sourceCryptoCurrency
        )

        setEffect {
            SwapInputContract.Effect.ConfirmDialog(
                to = toAmount,
                from = fromAmount,
                rate = state.cryptoExchangeRate,
                sendingFee = sendingFee,
                receivingFee = receivingFee,
            )
        }
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

    private fun createSwapOrder() {
        val state = currentLoadedState ?: return

        callApi( //todo: enable when API is ready
            endState = { state.copy() }, //todo: loading
            startState = { state.copy() }, //todo: loading
            action = {
                swapApi.createOrder(
                    amount = state.sourceCryptoAmount,
                    quoteResponse = state.quoteResponse,
                    destinationAddress = state.destinationAddress,
                    destinationCurrency = state.destinationCryptoCurrency
                )
            },
            callback = {
                when (it.status) {
                    Status.SUCCESS ->
                        createTransaction(requireNotNull(it.data))

                    Status.ERROR ->
                        setEffect {
                            SwapInputContract.Effect.ShowToast(
                                it.message ?: getString(
                                    R.string.FabriikApi_DefaultError
                                )
                            )
                        }
                }
            }
        )
    }

    private fun createTransaction(order: CreateOrderResponse) {
        viewModelScope.launch(Dispatchers.IO) {
            val wallet = breadBox.wallet(order.currency).first()
            val address = wallet.addressFor(order.address)
            val amount = Amount.create(order.amount.toDouble(), wallet.unit)

            if (address == null || wallet.containsAddress(address)) {
                //todo: error message
                return@launch
            }

            val attributes = wallet.getTransferAttributesFor(address)
            if (attributes.any { wallet.validateTransferAttribute(it).isPresent }) {
                //todo: error message
                return@launch
            }

            val phrase = try {
                checkNotNull(userManager.getPhrase())
            } catch (e: UserNotAuthenticatedException) {
                //todo: error message
                return@launch
            }

            val feeBasis = estimateFeeBasis(
                address = order.address,
                currency = order.currency,
                amount = order.amount
            )

            if (feeBasis == null) {
                //todo: error message
                return@launch
            }

            val newTransfer =
                wallet.createTransfer(address, amount, feeBasis, attributes).orNull()

            if (newTransfer == null) {
                //todo: error message
            } else {
                wallet.walletManager.submit(newTransfer, phrase)
                breadBox.walletTransfer(order.currency, newTransfer)
                    .first()
            }
        }
    }

    private suspend fun estimateFee(
        cryptoAmount: BigDecimal,
        currencyCode: String,
        fiatCode: String
    ): AmountData? {
        val wallet = breadBox.wallet(currencyCode).first()
        val amount = Amount.create(cryptoAmount.toDouble(), wallet.unit)
        val address = loadAddress(wallet.currency.code) ?: return null

        return try {
            val data = wallet.estimateFee(address, amount)
            val cryptoFee = data.fee.toBigDecimal()
            val cryptoCurrency = data.currency.code
            val fiatFee = ratesRepository.getFiatForCrypto(
                cryptoAmount = cryptoFee,
                cryptoCode = cryptoCurrency,
                fiatCode = fiatCode
            ) ?: return null

            return AmountData(
                fiatAmount = fiatFee,
                fiatCurrency = fiatCode,
                cryptoAmount = cryptoFee,
                cryptoCurrency = cryptoCurrency
            )
        } catch (e: Exception) {
            Log.d("SwapInputViewModel", "Fee estimation failed: ${e.message}")
            null
        }
    }

    private suspend fun estimateFeeBasis(address: String, currency: String, amount: BigDecimal) : TransferFeeBasis? {
        val wallet = breadBox.wallet(currency).first()

        // Skip if address is not valid
        val address = wallet.addressFor(address) ?: return null
        if (wallet.containsAddress(address))
            return null
        val amount = Amount.create(amount.toDouble(), wallet.unit)
        val networkFee = wallet.feeForSpeed(TransferSpeed.Regular(currency))

        return try {
            val data = wallet.estimateFee(address, amount, networkFee)
            val fee = data.fee.toBigDecimal()
            check(!fee.isZero()) { "Estimated fee was zero" }
            data
        } catch (e: FeeEstimationError) {
            logError("Failed get fee estimate", e)
            null
        } catch (e: IllegalStateException) {
            logError("Failed get fee estimate", e)
            null
        }
    }

    private fun BigDecimal.toCrypto(cryptoCurrency: String, fiatCurrency: String): BigDecimal {
        return ratesRepository.getCryptoForFiat(
            fiatAmount = this,
            cryptoCode = cryptoCurrency,
            fiatCode = fiatCurrency
        ) ?: BigDecimal.ZERO
    }

    private fun BigDecimal.toFiat(cryptoCurrency: String, fiatCurrency: String): BigDecimal {
        return ratesRepository.getFiatForCrypto(
            cryptoAmount = this,
            cryptoCode = cryptoCurrency,
            fiatCode = fiatCurrency
        ) ?: BigDecimal.ZERO
    }

    private suspend fun BigDecimal.convertSource(
        fromCryptoCurrency: String,
        toCryptoCurrency: String,
        rate: BigDecimal
    ): Triple<AmountData?, AmountData?, BigDecimal> {
        val state = currentLoadedState ?: return Triple(null, null, BigDecimal.ZERO)

        val destAmount = this.multiply(rate)
        val destFee = estimateFee(destAmount, toCryptoCurrency, state.fiatCurrency)
        val sourceFee = estimateFee(this, fromCryptoCurrency, state.fiatCurrency)

        return Triple(sourceFee, destFee, destAmount)
    }

    private suspend fun BigDecimal.convertDestination(
        fromCryptoCurrency: String,
        toCryptoCurrency: String,
        rate: BigDecimal
    ): Triple<AmountData?, AmountData?, BigDecimal> {
        val state = currentLoadedState ?: return Triple(null, null, BigDecimal.ZERO)

        val sourceAmount = this.divide(rate, 5, RoundingMode.HALF_UP)
        val sourceFee = estimateFee(sourceAmount, toCryptoCurrency, state.fiatCurrency)
        val destFee = estimateFee(this, fromCryptoCurrency, state.fiatCurrency)

        return Triple(sourceFee, destFee, sourceAmount)
    }

    private fun SwapInputContract.State.Loaded.validate() = copy(
        confirmButtonEnabled = sourceCryptoAmount != BigDecimal.ZERO &&
                destinationCryptoAmount != BigDecimal.ZERO
    )

    companion object {
        const val QUOTE_TIMER = 15
    }
}