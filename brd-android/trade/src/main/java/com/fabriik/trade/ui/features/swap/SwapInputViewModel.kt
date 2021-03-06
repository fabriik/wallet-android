package com.fabriik.trade.ui.features.swap

import android.app.Application
import android.security.keystore.UserNotAuthenticatedException
import androidx.lifecycle.viewModelScope
import com.breadwallet.breadbox.*
import com.breadwallet.crypto.Amount
import com.breadwallet.crypto.TransferFeeBasis
import com.breadwallet.ext.isZero
import com.breadwallet.tools.security.BrdUserManager
import com.breadwallet.tools.security.ProfileManager
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
import org.kodein.di.direct

class SwapInputViewModel(
    application: Application
) : FabriikViewModel<SwapInputContract.State, SwapInputContract.Event, SwapInputContract.Effect>(
    application
), KodeinAware {

    override val kodein by closestKodein { application }

    private val currentFiatCurrency = "USD"

    private val swapApi by kodein.instance<SwapApi>()
    private val breadBox by kodein.instance<BreadBox>()
    private val userManager by kodein.instance<BrdUserManager>()
    private val profileManager by kodein.instance<ProfileManager>()

    private val helper = SwapInputHelper(
        direct.instance(), direct.instance()
    )

    private val amountConverter = AmountConverter(
        direct.instance(), direct.instance(), currentFiatCurrency
    )

    private val convertSourceFiatAmount = ConvertSourceFiatAmount(amountConverter)
    private val convertSourceCryptoAmount = ConvertSourceCryptoAmount(amountConverter)
    private val convertDestinationFiatAmount = ConvertDestinationFiatAmount(amountConverter)
    private val convertDestinationCryptoAmount = ConvertDestinationCryptoAmount(amountConverter)

    private val currentLoadedState: SwapInputContract.State.Loaded?
        get() = state.value as SwapInputContract.State.Loaded?

    private val profile = profileManager.getProfile()

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
                currencyCode.equals(it.baseCurrency, true) && helper.isWalletEnabled(it.termCurrency)
            } ?: state.selectedPair

            val sourceBalance = helper.loadCryptoBalance(
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

        setEffect { SwapInputContract.Effect.ClearInputFocus }

        onSourceCurrencyFiatAmountChanged(
            state.minFiatAmount, false
        )
    }

    private fun onMaxAmountClicked() {
        val state = currentLoadedState ?: return

        setEffect { SwapInputContract.Effect.ClearInputFocus }

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
            val balance = helper.loadCryptoBalance(currentData.destinationCryptoCurrency) ?: return@launch

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
                helper.isWalletEnabled(it.baseCurrency) && helper.isWalletEnabled(it.termCurrency)
            }

            if (selectedPair == null) {
                setEffect { SwapInputContract.Effect.ShowDialog(DIALOG_CHECK_ASSETS_ARGS) }
                return@launch
            }

            val quoteResponse = swapApi.getQuote(selectedPair)
            val selectedPairQuote = quoteResponse.data

            val sourceCryptoBalance = helper.loadCryptoBalance(selectedPair.baseCurrency)
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

    private fun onSourceCurrencyClicked() {
        val state = currentLoadedState ?: return

        setEffect {
            SwapInputContract.Effect.SourceSelection(
                helper.getAvailableSourceCurrencies(state.tradingPairs)
            )
        }
    }

    private fun onDestinationCurrencyClicked() {
        val state = currentLoadedState ?: return

        setEffect {
            SwapInputContract.Effect.DestinationSelection(
                currencies = helper.getAvailableDestinationCurrencies(
                    state.tradingPairs, state.sourceCryptoCurrency
                ),
                sourceCurrency = state.sourceCryptoCurrency
            )
        }
    }

    private fun onSourceCurrencyFiatAmountChanged(sourceFiatAmount: BigDecimal, changeByUser: Boolean) {
        convertAmount(
            amount = sourceFiatAmount,
            converter = convertSourceFiatAmount,
            changeByUser = changeByUser
        )
    }

    private fun onSourceCurrencyCryptoAmountChanged(sourceCryptoAmount: BigDecimal, changeByUser: Boolean) {
        convertAmount(
            amount = sourceCryptoAmount,
            converter = convertSourceCryptoAmount,
            changeByUser = changeByUser
        )
    }

    private fun onDestinationCurrencyFiatAmountChanged(destFiatAmount: BigDecimal, changeByUser: Boolean) {
        convertAmount(
            amount = destFiatAmount,
            converter = convertDestinationFiatAmount,
            changeByUser = changeByUser
        )
    }

    private fun onDestinationCurrencyCryptoAmountChanged(destCryptoAmount: BigDecimal, changeByUser: Boolean) {
        convertAmount(
            amount = destCryptoAmount,
            converter = convertDestinationCryptoAmount,
            changeByUser = changeByUser
        )
    }

    private fun convertAmount(converter: InputConverter, amount: BigDecimal, changeByUser: Boolean) {
        val state = currentLoadedState ?: return

        viewModelScope.launch(Dispatchers.IO) {
            val result = converter(
                amount = amount,
                changeByUser = changeByUser,
                markupFactor = state.markupFactor,
                exchangeRate = state.cryptoExchangeRate,
                sourceCurrency = state.sourceCryptoCurrency,
                destinationCurrency = state.destinationCryptoCurrency
            )

            setState {
                state.copy(
                    sourceFiatAmount = result.sourceFiatAmount,
                    sourceCryptoAmount = result.sourceCryptoAmount,
                    destinationFiatAmount = result.destinationFiatAmount,
                    destinationCryptoAmount = result.destinationCryptoAmount,
                    sendingNetworkFee = result.sourceNetworkFee,
                    receivingNetworkFee = result.destinationNetworkFee
                ).validateAmounts()
            }

            updateAmounts(
                sourceFiatAmountChangedByUser = result.sourceFiatAmountChangedByUser,
                sourceCryptoAmountChangedByUser =  result.sourceCryptoAmountChangedByUser,
                destinationFiatAmountChangedByUser =  result.destinationFiatAmountChangedByUser,
                destinationCryptoAmountChangedByUser =  result.destinationCryptoAmountChangedByUser,
            )

            checkEthFeeBalance(
                sourceFeeData = result.sourceNetworkFee,
                destinationFeeData = result.destinationNetworkFee,
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

        val ethBalance = helper.loadCryptoBalance("ETH") ?: BigDecimal.ZERO
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
                    helper.loadAddress(state.destinationCryptoCurrency) ?: return@callApi Resource.error(
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

            val feeBasisResponse = helper.estimateFeeBasis(
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

    private fun validate(state: SwapInputContract.State.Loaded) = when {
        state.sendingNetworkFee == null || state.receivingNetworkFee == null ->
            SwapInputContract.ErrorMessage.NetworkIssues
        state.sourceCryptoBalance < state.sourceCryptoAmount ->
            SwapInputContract.ErrorMessage.InsufficientFunds(state.sourceCryptoBalance, state.sourceCryptoCurrency)
        state.sourceCryptoBalance < state.sourceCryptoAmount + state.sendingNetworkFee.cryptoAmountIfIncludedOrZero() ->
            SwapInputContract.ErrorMessage.InsufficientFundsForFee
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
        confirmButtonEnabled = !sourceCryptoAmount.isZero() && !destinationCryptoAmount.isZero()
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
        private const val DIALOG_RESULT_GOT_IT = "result_got_it"
        private const val DIALOG_REQUEST_CHECK_ASSETS = "request_check_assets"
        private const val DIALOG_REQUEST_TEMP_UNAVAILABLE = "request_temp_unvailable"

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