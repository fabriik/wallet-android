package com.fabriik.trade.ui.features.swap

import android.app.Application
import android.security.keystore.UserNotAuthenticatedException
import androidx.lifecycle.viewModelScope
import com.breadwallet.breadbox.*
import com.breadwallet.crypto.Address
import com.breadwallet.crypto.AddressScheme
import com.breadwallet.crypto.Amount
import com.breadwallet.crypto.TransferFeeBasis
import com.breadwallet.crypto.errors.FeeEstimationError
import com.breadwallet.ext.isZero
import com.breadwallet.logger.logError
import com.breadwallet.platform.interfaces.AccountMetaDataProvider
import com.breadwallet.tools.manager.BRSharedPrefs
import com.breadwallet.tools.security.BrdUserManager
import com.breadwallet.tools.security.ProfileManager
import com.breadwallet.tools.util.TokenUtil
import com.fabriik.common.data.Resource
import com.fabriik.common.data.Status
import com.fabriik.common.data.model.availableDailyLimit
import com.fabriik.common.data.model.availableLifetimeLimit
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
import org.kodein.di.direct

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
    private val acctMetaDataProvider by kodein.instance<AccountMetaDataProvider>()

    private val currentFiatCurrency = "USD"
    private val amountConverter = AmountConverter(direct.instance(), direct.instance(), currentFiatCurrency)

    private val convertSourceFiatAmount = ConvertSourceFiatAmount(amountConverter)
    private val convertSourceCryptoAmount = ConvertSourceCryptoAmount(amountConverter)
    private val convertDestinationFiatAmount = ConvertDestinationFiatAmount(amountConverter)
    private val convertDestinationCryptoAmount = ConvertDestinationCryptoAmount(amountConverter)

    private val currentLoadedState: SwapInputContract.State.Loaded?
        get() = state.value as SwapInputContract.State.Loaded?

    private val profile = profileManager.getProfile()

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
                setEffect { SwapInputContract.Effect.RequestUserAuthentication }

            SwapInputContract.Event.OnUserAuthenticationSucceed ->
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

        onSourceCurrencyFiatAmountChanged(
            state.minFiatAmount, false
        )
    }

    private fun onMaxAmountClicked() {
        val state = currentLoadedState ?: return

        val maxAmountLimitCrypto = amountConverter.fiatToCrypto(
            amount = state.maxFiatAmount,
            cryptoCurrency = state.sourceCryptoCurrency
        )

        onSourceCurrencyCryptoAmountChanged(
            min(state.sourceCryptoBalance, maxAmountLimitCrypto), false
        )
    }

    private fun onReplaceCurrenciesClicked() {
        val currentData = currentLoadedState ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val balance = loadCryptoBalance(currentData.destinationCryptoCurrency) ?: return@launch

            currentLoadedState?.let {
                val stateChange = it.copy(
                    sourceFiatAmount = it.destinationFiatAmount,
                    sourceCryptoAmount = it.destinationCryptoAmount,
                    destinationFiatAmount = it.sourceFiatAmount,
                    destinationCryptoAmount = it.sourceCryptoAmount,
                    sourceCryptoBalance = balance,
                    sourceCryptoCurrency = currentData.destinationCryptoCurrency,
                    destinationCryptoCurrency = currentData.sourceCryptoCurrency,
                    sendingNetworkFee = currentData.receivingNetworkFee,
                    receivingNetworkFee = currentData.sendingNetworkFee,
                )

                setEffect { SwapInputContract.Effect.CurrenciesReplaceAnimation(stateChange) }
            }
        }
    }

    private fun onReplaceCurrenciesAnimationCompleted(state: SwapInputContract.State.Loaded) {
        setState { state }
        updateAmounts(false)
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
                    val responseData = requireNotNull(response.data)

                    setState {
                        latestState.copy(
                            cryptoExchangeRateLoading = false,
                            quoteResponse = responseData
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
                getString(R.string.Swap_Input_Error_Network)
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

            val enabledWallets = acctMetaDataProvider.enabledWallets().first()
            val selectedPair = tradingPairs.firstOrNull {
                isWalletEnabled(enabledWallets, it.baseCurrency) &&
                        isWalletEnabled(enabledWallets, it.termCurrency)
            }

            val quoteResponse = selectedPair?.let { swapApi.getQuote(selectedPair) }
            val selectedPairQuote = quoteResponse?.data
            if (quoteResponse == null || quoteResponse.status == Status.ERROR || selectedPairQuote == null) {
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
                    fiatCurrency = currentFiatCurrency,
                    quoteResponse = selectedPairQuote,
                    sourceCryptoCurrency = selectedPair.baseCurrency,
                    destinationCryptoCurrency = selectedPair.termCurrency,
                    sourceCryptoBalance = sourceCryptoBalance,
                    minFiatAmount = requireNotNull(quoteResponse.data).minUsdValue,
                    maxFiatAmount = profile.nextExchangeLimit(),
                    dailyFiatLimit = profile.availableDailyLimit(),
                    lifetimeFiatLimit = profile.availableLifetimeLimit()
                )
            }

            startQuoteTimer()
        }
    }

    private fun isWalletEnabled(enabledWallets: List<String>, currencyCode: String): Boolean {
        val token = TokenUtil.tokenForCode(currencyCode) ?: return false
        return token.isSupported && enabledWallets.contains(token.currencyId)
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

    private fun onSourceCurrencyFiatAmountChanged(sourceFiatAmount: BigDecimal, changeByUser: Boolean = true) {
        val state = currentLoadedState ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val amounts = convertSourceFiatAmount(
                exchangeRate = state.cryptoExchangeRate,
                sourceFiatAmount = sourceFiatAmount,
                sourceCryptoCurrency = state.sourceCryptoCurrency,
                destinationCryptoCurrency = state.destinationCryptoCurrency
            )

            updateAmounts(amounts, state, changeByUser)
        }
    }

    private fun onSourceCurrencyCryptoAmountChanged(sourceCryptoAmount: BigDecimal, changeByUser: Boolean = true) {
        val state = currentLoadedState ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val amounts = convertSourceCryptoAmount(
                exchangeRate = state.cryptoExchangeRate,
                sourceCryptoAmount = sourceCryptoAmount,
                sourceCryptoCurrency = state.sourceCryptoCurrency,
                destinationCryptoCurrency = state.destinationCryptoCurrency
            )

            updateAmounts(amounts, state, changeByUser)
        }
    }

    private fun onDestinationCurrencyFiatAmountChanged(destFiatAmount: BigDecimal) {
        val state = currentLoadedState ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val amounts = convertDestinationFiatAmount(
                exchangeRate = state.cryptoExchangeRate,
                sourceCryptoCurrency = state.sourceCryptoCurrency,
                destinationFiatAmount = destFiatAmount,
                destinationCryptoCurrency = state.destinationCryptoCurrency
            )

            updateAmounts(amounts, state, true)
        }
    }

    private fun onDestinationCurrencyCryptoAmountChanged(destCryptoAmount: BigDecimal) {
        val state = currentLoadedState ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val amounts = convertDestinationCryptoAmount(
                exchangeRate = state.cryptoExchangeRate,
                sourceCryptoCurrency = state.sourceCryptoCurrency,
                destinationCryptoAmount = destCryptoAmount,
                destinationCryptoCurrency = state.destinationCryptoCurrency
            )

            updateAmounts(amounts, state, true)
        }
    }

    private fun updateAmounts(amounts: SwapInputContract.Amounts, state: SwapInputContract.State.Loaded, changeByUser: Boolean = true) {
        setState {
            state.copy(
                sourceFiatAmount = amounts.sourceFiatAmount,
                sourceCryptoAmount = amounts.sourceCryptoAmount,
                destinationFiatAmount = amounts.destinationFiatAmount,
                destinationCryptoAmount = amounts.destinationCryptoAmount,
                receivingNetworkFee = amounts.destinationFeeData.toAmountData(),
                sendingNetworkFee = amounts.sourceFeeData.toAmountData()
            ).validateAmounts()
        }

        updateAmounts(changeByUser)
    }

    private fun onConfirmClicked() {
        val state = currentLoadedState
        if (state == null) {
            setEffect {
                SwapInputContract.Effect.ShowToast(
                    getString(R.string.Swap_Input_Error_Network)
                )
            }
            return
        }

        val validationError = validate(state)
        if (validationError != null) {
            showSwapError(validationError)
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
                sendingFee = state.sendingNetworkFee!!,
                receivingFee = state.receivingNetworkFee!!,
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

        callApi(
            endState = { state.copy(fullScreenLoadingVisible = false) },
            startState = { state.copy(fullScreenLoadingVisible = true) },
            action = {
                val destinationAddress =
                    loadAddress(state.destinationCryptoCurrency) ?: return@callApi Resource.error(
                        message = getString(R.string.FabriikApi_DefaultError)
                    )

                swapApi.createOrder(
                    amount = state.destinationCryptoAmount,
                    quoteResponse = state.quoteResponse,
                    destinationAddress = destinationAddress.toString(),
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
        val state = currentLoadedState ?: return

        setState { state.copy(fullScreenLoadingVisible = true) }

        viewModelScope.launch(Dispatchers.IO) {
            val wallet = breadBox.wallet(order.currency).first()
            val address = wallet.addressFor(order.address)
            val amount = Amount.create(order.amount.toDouble(), wallet.unit)

            if (address == null || wallet.containsAddress(address)) {
                showGenericError()
                return@launch
            }

            val attributes = wallet.getTransferAttributesFor(address)
            if (attributes.any { wallet.validateTransferAttribute(it).isPresent }) {
                showGenericError()
                return@launch
            }

            val phrase = try {
                checkNotNull(userManager.getPhrase())
            } catch (e: UserNotAuthenticatedException) {
                showGenericError()
                return@launch
            }

            val feeBasis = estimateFeeBasis(
                currency = order.currency,
                orderAmount = order.amount,
                orderAddress = order.address
            )

            if (feeBasis == null) {
                showGenericError()
                return@launch
            }

            val newTransfer =
                wallet.createTransfer(address, amount, feeBasis, attributes, order.exchangeId).orNull()

            if (newTransfer == null) {
                showGenericError()
            } else {
                wallet.walletManager.submit(newTransfer, phrase)
                breadBox.walletTransfer(order.currency, newTransfer)
                    .first()

                setState { state.copy(fullScreenLoadingVisible = false) }

                setEffect {
                    SwapInputContract.Effect.ContinueToSwapProcessing(
                        exchangeId = order.exchangeId,
                        sourceCurrency = state.sourceCryptoCurrency,
                        destinationCurrency = state.destinationCryptoCurrency
                    )
                }
            }
        }
    }

    private suspend fun estimateFeeBasis(orderAddress: String, currency: String, orderAmount: BigDecimal) : TransferFeeBasis? {
        val wallet = breadBox.wallet(currency).first()

        // Skip if address is not valid
        val address = wallet.addressFor(orderAddress) ?: return null
        if (wallet.containsAddress(address))
            return null
        val amount = Amount.create(orderAmount.toDouble(), wallet.unit)
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

    private fun validate(state: SwapInputContract.State.Loaded) = when {
        state.sendingNetworkFee == null || state.receivingNetworkFee == null ->
            SwapInputContract.ErrorMessage.NetworkIssues
        state.sourceCryptoBalance < state.sourceCryptoAmount  ->
            SwapInputContract.ErrorMessage.InsufficientFunds(state.sourceCryptoBalance, state.sourceCryptoCurrency)
        state.sourceFiatAmount > state.maxFiatAmount ->
            SwapInputContract.ErrorMessage.MaxSwapAmount(state.maxFiatAmount, state.fiatCurrency)
        state.sourceFiatAmount < state.minFiatAmount ->
            SwapInputContract.ErrorMessage.MinSwapAmount(state.minFiatAmount, state.fiatCurrency)
        state.dailyFiatLimit < state.sourceFiatAmount ->
            SwapInputContract.ErrorMessage.DailyLimitReached
        state.lifetimeFiatLimit < state.sourceFiatAmount ->
            SwapInputContract.ErrorMessage.LifetimeLimitReached
        else -> null
    }

    private fun SwapInputContract.State.Loaded.validateAmounts() = copy(
        swapErrorMessage = null,
        confirmButtonEnabled = sourceCryptoAmount != BigDecimal.ZERO &&
                destinationCryptoAmount != BigDecimal.ZERO
    )

    private fun showSwapError(error: SwapInputContract.ErrorMessage) {
        val state = currentLoadedState ?: return
        setState {
            state.copy(
                swapErrorMessage = error,
                confirmButtonEnabled = false
            )
        }
    }

    private fun showGenericError() {
        val state = currentLoadedState ?: return
        setState { state.copy(fullScreenLoadingVisible = false) }

        setEffect {
            SwapInputContract.Effect.ShowToast(
                getString(R.string.FabriikApi_DefaultError)
            )
        }
    }

    companion object {
        const val QUOTE_TIMER = 15
    }
}