package com.fabriik.trade.ui.features.swap

import android.app.Application
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
import com.breadwallet.platform.interfaces.AccountMetaDataProvider
import com.breadwallet.repository.RatesRepository
import com.breadwallet.tools.manager.BRSharedPrefs
import com.breadwallet.tools.security.BrdUserManager
import com.breadwallet.tools.security.ProfileManager
import com.breadwallet.tools.util.TokenUtil
import com.fabriik.common.data.Resource
import com.fabriik.common.data.Status
import com.fabriik.common.data.model.availableDailyLimit
import com.fabriik.common.data.model.availableLifetimeLimit
import com.fabriik.common.data.model.nextExchangeLimit
import com.fabriik.common.data.model.kyc2ExchangeLimit
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.ui.dialog.FabriikGenericDialogArgs
import com.fabriik.common.utils.getString
import com.fabriik.common.utils.min
import com.fabriik.trade.R
import com.fabriik.trade.data.SwapApi
import com.fabriik.trade.data.model.AmountData
import com.fabriik.trade.data.model.FeeAmountData
import com.fabriik.trade.data.request.CreateOrderRequest
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
import java.util.concurrent.atomic.AtomicBoolean

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

    private val currentLoadedState: SwapInputContract.State.Loaded?
        get() = state.value as SwapInputContract.State.Loaded?

    private val profile = profileManager.getProfile()
    private val ratesRepository by kodein.instance<RatesRepository>()

    private var currentTimerJob: Job? = null
    private var ethErrorSeen = false
    private var ethWarningSeen = false

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

            is SwapInputContract.Event.OnCheckAssetsDialogResult,
            is SwapInputContract.Event.OnTempUnavailableDialogResult ->
                setEffect { SwapInputContract.Effect.Dismiss }

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
                onSourceCurrencyFiatAmountChanged(event.amount, true)

            is SwapInputContract.Event.SourceCurrencyCryptoAmountChange ->
                onSourceCurrencyCryptoAmountChanged(event.amount, true)

            is SwapInputContract.Event.DestinationCurrencyFiatAmountChange ->
                onDestinationCurrencyFiatAmountChanged(event.amount, true)

            is SwapInputContract.Event.DestinationCurrencyCryptoAmountChange ->
                onDestinationCurrencyCryptoAmountChanged(event.amount, true)
        }
    }

    private fun onSourceCurrencyChanged(currencyCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val state = currentLoadedState ?: return@launch
            if (currencyCode.equals(state.sourceCryptoCurrency, true)) {
                return@launch
            }

            val newSelectedPair = state.tradingPairs.firstOrNull {
                currencyCode.equals(it.baseCurrency, true) && isWalletEnabled(it.termCurrency)
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
                    sourceFiatAmount = BigDecimal.ZERO,
                    sourceCryptoAmount = BigDecimal.ZERO,
                    destinationFiatAmount = BigDecimal.ZERO,
                    destinationCryptoAmount = BigDecimal.ZERO,
                    sendingNetworkFee = null,
                    receivingNetworkFee = null
                )
            }

            updateAmounts(
                sourceFiatAmountChangedByUser = false,
                sourceCryptoAmountChangedByUser = false,
                destinationFiatAmountChangedByUser = false,
                destinationCryptoAmountChangedByUser = false
            )

            ethErrorSeen = false
            ethWarningSeen = false

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
                destinationCryptoCurrency = newSelectedPair.termCurrency,
                sourceFiatAmount = BigDecimal.ZERO,
                sourceCryptoAmount = BigDecimal.ZERO,
                destinationFiatAmount = BigDecimal.ZERO,
                destinationCryptoAmount = BigDecimal.ZERO,
                sendingNetworkFee = null,
                receivingNetworkFee = null
            )
        }

        updateAmounts(
            sourceFiatAmountChangedByUser = false,
            sourceCryptoAmountChangedByUser = false,
            destinationFiatAmountChangedByUser = false,
            destinationCryptoAmountChangedByUser = false
        )

        ethErrorSeen = false
        ethWarningSeen = false

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

        val maxAmountLimitCrypto = state.maxFiatAmount.toCrypto(
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

        updateAmounts(
            sourceFiatAmountChangedByUser = false,
            sourceCryptoAmountChangedByUser = false,
            destinationFiatAmountChangedByUser = false,
            destinationCryptoAmountChangedByUser = false,
        )
    }

    private fun startQuoteTimer() {
        currentTimerJob?.cancel()

        val state = currentLoadedState ?: return
        val quoteResponse = state.quoteResponse ?: return
        val targetTimestamp = quoteResponse.timestamp
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
                Status.ERROR -> {
                    val latestState = currentLoadedState ?: return@launch

                    setState {
                        latestState.copy(
                            cryptoExchangeRateLoading = false,
                            quoteResponse = null
                        )
                    }

                    setEffect {
                        SwapInputContract.Effect.ShowToast(
                            getString(R.string.Swap_Input_Error_NoSelectedPairData), true
                        )
                    }
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
                setEffect { SwapInputContract.Effect.ShowDialog(DIALOG_TEMP_UNAVAILABLE_ARGS) }
                return@launch
            }

            val selectedPair = tradingPairs.firstOrNull {
                isWalletEnabled(it.baseCurrency) && isWalletEnabled(it.termCurrency)
            }

            if (selectedPair == null) {
                setEffect { SwapInputContract.Effect.ShowDialog(DIALOG_CHECK_ASSETS_ARGS) }
                return@launch
            }

            val quoteResponse = swapApi.getQuote(selectedPair)
            val selectedPairQuote = quoteResponse.data

            val sourceCryptoBalance = loadCryptoBalance(selectedPair.baseCurrency)
            if (sourceCryptoBalance == null) {
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
                    sourceCryptoBalance = sourceCryptoBalance,
                    minFiatAmount = quoteResponse.data?.minUsdValue ?: BigDecimal.ZERO,
                    maxFiatAmount = profile.nextExchangeLimit(),
                    dailyFiatLimit = profile.availableDailyLimit(),
                    lifetimeFiatLimit = profile.availableLifetimeLimit(),
                    kyc2ExchangeFiatLimit = profile.kyc2ExchangeLimit()
                )
            }

            if (selectedPairQuote == null) {
                setEffect {
                    SwapInputContract.Effect.ShowToast(
                        getString(R.string.Swap_Input_Error_NoSelectedPairData), true
                    )
                }
            } else {
                startQuoteTimer()
            }
        }
    }

    private suspend fun isWalletEnabled(currencyCode: String): Boolean {
        val enabledWallets = acctMetaDataProvider.enabledWallets().first()
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

    private fun onSourceCurrencyFiatAmountChanged(sourceFiatAmount: BigDecimal, changeByUser: Boolean) {
        val state = currentLoadedState ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val sourceCryptoAmount = sourceFiatAmount.toCrypto(
                cryptoCurrency = state.sourceCryptoCurrency,
                fiatCurrency = state.fiatCurrency
            )

            val destCryptoAmountData = sourceCryptoAmount.convertSource(
                sourceCurrency = state.sourceCryptoCurrency,
                destinationCurrency = state.destinationCryptoCurrency,
                rate = state.cryptoExchangeRate,
                markup = state.markupFactor
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
                ).validateAmounts()
            }

            updateAmounts(
                sourceFiatAmountChangedByUser = changeByUser,
                sourceCryptoAmountChangedByUser = false,
                destinationFiatAmountChangedByUser = false,
                destinationCryptoAmountChangedByUser = false,
            )

            checkEthFeeBalance(
                sourceFeeData = destCryptoAmountData.first,
                destinationFeeData = destCryptoAmountData.second,
            )
        }
    }

    private fun onSourceCurrencyCryptoAmountChanged(sourceCryptoAmount: BigDecimal, changeByUser: Boolean) {
        val state = currentLoadedState ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val sourceFiatAmount = sourceCryptoAmount.toFiat(
                cryptoCurrency = state.sourceCryptoCurrency,
                fiatCurrency = state.fiatCurrency
            )

            val destCryptoAmountData = sourceCryptoAmount.convertSource(
                sourceCurrency = state.sourceCryptoCurrency,
                destinationCurrency = state.destinationCryptoCurrency,
                rate = state.cryptoExchangeRate,
                markup = state.markupFactor
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
                ).validateAmounts()
            }

            updateAmounts(
                sourceFiatAmountChangedByUser = false,
                sourceCryptoAmountChangedByUser = changeByUser,
                destinationFiatAmountChangedByUser = false,
                destinationCryptoAmountChangedByUser = false,
            )

            checkEthFeeBalance(
                sourceFeeData = destCryptoAmountData.first,
                destinationFeeData = destCryptoAmountData.second,
            )
        }
    }

    private fun onDestinationCurrencyFiatAmountChanged(destFiatAmount: BigDecimal, changeByUser: Boolean) {
        val state = currentLoadedState ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val destCryptoAmount = destFiatAmount.toCrypto(
                cryptoCurrency = state.destinationCryptoCurrency,
                fiatCurrency = state.fiatCurrency
            )

            val sourceCryptoAmountData = destCryptoAmount.convertDestination(
                destinationCurrency = state.destinationCryptoCurrency,
                sourceCurrency = state.sourceCryptoCurrency,
                rate = state.cryptoExchangeRate,
                markup = state.markupFactor
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
                ).validateAmounts()
            }

            updateAmounts(
                sourceFiatAmountChangedByUser = false,
                sourceCryptoAmountChangedByUser = false,
                destinationFiatAmountChangedByUser = changeByUser,
                destinationCryptoAmountChangedByUser = false,
            )

            checkEthFeeBalance(
                sourceFeeData = sourceCryptoAmountData.first,
                destinationFeeData = sourceCryptoAmountData.second,
            )
        }
    }

    private fun onDestinationCurrencyCryptoAmountChanged(destCryptoAmount: BigDecimal, changeByUser: Boolean) {
        val state = currentLoadedState ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val destFiatAmount = destCryptoAmount.toFiat(
                cryptoCurrency = state.destinationCryptoCurrency,
                fiatCurrency = state.fiatCurrency
            )

            val sourceCryptoAmountData = destCryptoAmount.convertDestination(
                destinationCurrency = state.destinationCryptoCurrency,
                sourceCurrency = state.sourceCryptoCurrency,
                rate = state.cryptoExchangeRate,
                markup = state.markupFactor
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
                ).validateAmounts()
            }

            updateAmounts(
                sourceFiatAmountChangedByUser = false,
                sourceCryptoAmountChangedByUser = false,
                destinationFiatAmountChangedByUser = false,
                destinationCryptoAmountChangedByUser = changeByUser,
            )

            checkEthFeeBalance(
                sourceFeeData = sourceCryptoAmountData.first,
                destinationFeeData = sourceCryptoAmountData.second,
            )
        }
    }

    private suspend fun checkEthFeeBalance(sourceFeeData: FeeAmountData?, destinationFeeData: FeeAmountData?) {
        val sourceFeeEthAmount = when {
            sourceFeeData != null && !sourceFeeData.included -> sourceFeeData.cryptoAmount
            else -> BigDecimal.ZERO
        }

        val destinationFeeEthAmount = when {
            destinationFeeData != null && !destinationFeeData.included -> destinationFeeData.cryptoAmount
            else -> BigDecimal.ZERO
        }

        val ethSumFee = sourceFeeEthAmount + destinationFeeEthAmount
        if (ethSumFee.isZero()) {
            ethErrorSeen = false
            ethWarningSeen = false
            return
        }

        val ethBalance = loadCryptoBalance("ETH") ?: BigDecimal.ZERO
        if (ethBalance < ethSumFee && !ethErrorSeen) {
            ethErrorSeen = true
            ethWarningSeen = false

            setEffect {
                SwapInputContract.Effect.ShowToast(
                    message = getString(R.string.Swap_Input_Error_EthFeeBalance),
                    redInfo = true
                )
            }
        } else if (ethBalance > BigDecimal.ZERO && !ethWarningSeen) {
            ethErrorSeen = false
            ethWarningSeen = true

            setEffect {
                SwapInputContract.Effect.ShowToast(
                    message = getString(R.string.Swap_Input_Warning_EthFeeBalance)
                )
            }
        }
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

    private fun updateAmounts(
        sourceFiatAmountChangedByUser: Boolean,
        sourceCryptoAmountChangedByUser: Boolean,
        destinationFiatAmountChangedByUser: Boolean,
        destinationCryptoAmountChangedByUser: Boolean
    ) {
        val state = currentLoadedState ?: return

        setEffect { SwapInputContract.Effect.UpdateSourceFiatAmount(state.sourceFiatAmount, sourceFiatAmountChangedByUser) }
        setEffect { SwapInputContract.Effect.UpdateSourceCryptoAmount(state.sourceCryptoAmount, sourceCryptoAmountChangedByUser) }
        setEffect { SwapInputContract.Effect.UpdateDestinationFiatAmount(state.destinationFiatAmount, destinationFiatAmountChangedByUser) }
        setEffect { SwapInputContract.Effect.UpdateDestinationCryptoAmount(state.destinationCryptoAmount, destinationCryptoAmountChangedByUser) }

        if (sourceFiatAmountChangedByUser || sourceCryptoAmountChangedByUser || destinationFiatAmountChangedByUser || destinationCryptoAmountChangedByUser) {
            setEffect { SwapInputContract.Effect.DeselectMinMaxSwitchItems }
        }
    }

    private fun createSwapOrder() {
        val state = currentLoadedState ?: return
        val quoteResponse =  state.quoteResponse ?: return

        callApi(
            endState = { state.copy(fullScreenLoadingVisible = false) },
            startState = { state.copy(fullScreenLoadingVisible = true) },
            action = {
                val destinationAddress =
                    loadAddress(state.destinationCryptoCurrency) ?: return@callApi Resource.error(
                        message = getString(R.string.FabriikApi_DefaultError)
                    )

                val (baseQuantity, termQuantity, tradeSide) = when {
                    quoteResponse.securityId.startsWith(state.sourceCryptoCurrency, true) ->
                        Triple(
                            state.sourceCryptoAmount, // baseQuantity
                            state.destinationCryptoAmount, // termQuantity
                            CreateOrderRequest.TradeSide.SELL // tradeSide
                        )
                    else ->
                        Triple(
                            state.destinationCryptoAmount, // baseQuantity
                            state.sourceCryptoAmount, // termQuantity
                            CreateOrderRequest.TradeSide.BUY // tradeSide
                        )
                }

                swapApi.createOrder(
                    quoteId = quoteResponse.quoteId,
                    tradeSide = tradeSide,
                    baseQuantity = baseQuantity,
                    termQuantity = termQuantity,
                    destination = destinationAddress.toString(),
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
                                ), true
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

            val feeBasisResponse = estimateFeeBasis(
                currency = order.currency,
                orderAmount = order.amount,
                orderAddress = order.address
            )

            if (feeBasisResponse is SwapInputContract.ErrorMessage) {
                setEffect {
                    SwapInputContract.Effect.ShowToast(
                        feeBasisResponse.toString(getApplication()), true
                    )
                }
                return@launch
            }

            if (feeBasisResponse !is TransferFeeBasis) {
                setEffect {
                    SwapInputContract.Effect.ShowToast(
                        getString(R.string.FabriikApi_DefaultError)
                    )
                }
                return@launch
            }

            val newTransfer =
                wallet.createTransfer(address, amount, feeBasisResponse, attributes, order.exchangeId).orNull()

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

    private suspend fun estimateFee(
        cryptoAmount: BigDecimal, currencyCode: String, fiatCode: String
    ): FeeAmountData? {
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

            return FeeAmountData(
                fiatAmount = fiatFee,
                fiatCurrency = fiatCode,
                cryptoAmount = cryptoFee,
                cryptoCurrency = cryptoCurrency,
                included = currencyCode.equals(cryptoCurrency, true)
            )
        } catch (e: Exception) {
            Log.d("SwapInputViewModel", "Fee estimation failed: ${e.message}")
            null
        }
    }

    private suspend fun estimateFeeBasis(orderAddress: String, currency: String, orderAmount: BigDecimal) : Any {
        val wallet = breadBox.wallet(currency).first()

        // Skip if address is not valid
        val address = wallet.addressFor(orderAddress) ?: return SwapInputContract.ErrorMessage.NetworkIssues
        if (wallet.containsAddress(address)) return SwapInputContract.ErrorMessage.NetworkIssues

        val amount = Amount.create(orderAmount.toDouble(), wallet.unit)
        val networkFee = wallet.feeForSpeed(TransferSpeed.Regular(currency))

        return try {
            val data = wallet.estimateFee(address, amount, networkFee)
            val fee = data.fee.toBigDecimal()

            if (fee.isZero()) return SwapInputContract.ErrorMessage.NetworkIssues

            data
        } catch (e: FeeEstimationError) {
            SwapInputContract.ErrorMessage.InsufficientFundsForFee
        } catch (e: IllegalStateException) {
            SwapInputContract.ErrorMessage.NetworkIssues
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

    private suspend fun BigDecimal.convertSource(sourceCurrency: String, destinationCurrency: String, rate: BigDecimal, markup: BigDecimal): Triple<FeeAmountData?, FeeAmountData?, BigDecimal> {
        val state = currentLoadedState ?: return Triple(null, null, BigDecimal.ZERO)

        val sourceFee = estimateFee(this, sourceCurrency, state.fiatCurrency)
        val sourceAmount = if (sourceFee?.included == true) this - sourceFee.cryptoAmount else this

        val convertedAmount = sourceAmount.multiply(rate)

        val destFee = estimateFee(convertedAmount, destinationCurrency, state.fiatCurrency)
        val convertedDestAmount = if (destFee?.included == true) convertedAmount - destFee.cryptoAmount else convertedAmount
        val destAmount = convertedDestAmount.multiply(markup)

        return Triple(sourceFee, destFee, destAmount)
    }

    private suspend fun BigDecimal.convertDestination(sourceCurrency: String, destinationCurrency: String, rate: BigDecimal, markup: BigDecimal): Triple<FeeAmountData?, FeeAmountData?, BigDecimal> {
        val state = currentLoadedState ?: return Triple(null, null, BigDecimal.ZERO)

        val destFee = estimateFee(this, destinationCurrency, state.fiatCurrency)
        val destAmount = if (destFee?.included == true) this + destFee.cryptoAmount else this

        val convertedAmount = destAmount.divide(rate, 5, RoundingMode.HALF_UP)

        val sourceFee = estimateFee(convertedAmount, sourceCurrency, state.fiatCurrency)
        val convertedSourceAmount = if (sourceFee?.included == true) convertedAmount + sourceFee.cryptoAmount else convertedAmount
        val sourceAmount = convertedSourceAmount.divide(markup, 5, RoundingMode.HALF_UP)

        return Triple(sourceFee, destFee, sourceAmount)
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
            SwapInputContract.ErrorMessage.Kyc1DailyLimitReached
        state.lifetimeFiatLimit < state.sourceFiatAmount ->
            SwapInputContract.ErrorMessage.Kyc1LifetimeLimitReached
        state.kyc2ExchangeFiatLimit != null && state.kyc2ExchangeFiatLimit < state.sourceFiatAmount ->
            SwapInputContract.ErrorMessage.Kyc2ExchangeLimitReached
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
        const val DIALOG_RESULT_GOT_IT = "result_got_it"
        const val DIALOG_REQUEST_CHECK_ASSETS = "request_check_assets"
        const val DIALOG_REQUEST_TEMP_UNAVAILABLE = "request_temp_unvailable"

        val DIALOG_CHECK_ASSETS_ARGS = FabriikGenericDialogArgs(
            titleRes = R.string.Swap_Input_Dialog_CheckAssets_Title,
            descriptionRes = R.string.Swap_Input_Dialog_CheckAssets_Message,
            showDismissButton = true,
            positive = FabriikGenericDialogArgs.ButtonData(
                titleRes = R.string.Swap_Input_Dialog_Button_GotIt,
                resultKey = DIALOG_RESULT_GOT_IT
            ),
            requestKey = DIALOG_REQUEST_CHECK_ASSETS
        )

        val DIALOG_TEMP_UNAVAILABLE_ARGS = FabriikGenericDialogArgs(
            titleRes = R.string.Swap_Input_Dialog_TemporarlyUnavailable_Title,
            descriptionRes = R.string.Swap_Input_Dialog_TemporarlyUnavailable_Message,
            showDismissButton = true,
            positive = FabriikGenericDialogArgs.ButtonData(
                titleRes = R.string.Swap_Input_Dialog_Button_GotIt,
                resultKey = DIALOG_RESULT_GOT_IT
            ),
            requestKey = DIALOG_REQUEST_TEMP_UNAVAILABLE
        )
    }
}