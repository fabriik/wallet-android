package com.fabriik.buy.ui.features.input

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.breadwallet.ext.isZero
import com.breadwallet.platform.interfaces.AccountMetaDataProvider
import com.breadwallet.tools.security.ProfileManager
import com.breadwallet.tools.util.TokenUtil
import com.fabriik.buy.R
import com.fabriik.buy.data.BuyApi
import com.fabriik.buy.data.model.PaymentInstrument
import com.fabriik.buy.ui.features.input.BuyInputContract
import com.fabriik.common.data.Status
import com.fabriik.common.ui.base.FabriikViewModel
import com.fabriik.common.utils.getString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class BuyInputViewModel(
    application: Application
) : FabriikViewModel<BuyInputContract.State, BuyInputContract.Event, BuyInputContract.Effect>(
    application
), KodeinAware {

    override val kodein by closestKodein { application }

    private val buyApi by kodein.instance<BuyApi>()
    private val metaDataManager by kodein.instance<AccountMetaDataProvider>()
    private val profileManager by kodein.instance<ProfileManager>()

    private val currentFiatCurrency = "USD"

    private val currentLoadedState: BuyInputContract.State.Loaded?
        get() = state.value as BuyInputContract.State.Loaded?

    private var currentTimerJob: Job? = null

    init {
        loadInitialData()
    }

    override fun createInitialState() = BuyInputContract.State.Loading

    override fun handleEvent(event: BuyInputContract.Event) {
        when (event) {
            BuyInputContract.Event.DismissClicked ->
                setEffect { BuyInputContract.Effect.Dismiss }

            BuyInputContract.Event.ContinueClicked ->
                onContinueClicked()

            BuyInputContract.Event.CryptoCurrencyClicked ->
                onCryptoCurrencyClicked()

            BuyInputContract.Event.PaymentMethodClicked ->
                onPaymentMethodClicked()

            is BuyInputContract.Event.CryptoCurrencyChanged ->
                onCryptoCurrencyChanged(event.currencyCode)

            is BuyInputContract.Event.PaymentMethodChanged ->
                onPaymentMethodChanged(event.paymentInstrument)

            is BuyInputContract.Event.FiatAmountChange ->
                onFiatAmountChanged(event.amount, true)

            is BuyInputContract.Event.CryptoAmountChange ->
                onCryptoAmountChanged(event.amount, true)
        }
    }

    private fun onCryptoCurrencyChanged(currencyCode: String) {
        val state = currentLoadedState ?: return

        setState {
            state.copy(
                cryptoCurrency = currencyCode
            )
        }

        requestNewQuote()
    }

    private fun onPaymentMethodChanged(paymentInstrument: PaymentInstrument) {
        val state = currentLoadedState ?: return

        setState {
            state.copy(
                selectedPaymentMethod = paymentInstrument
            ).validate()
        }
    }

    private fun onCryptoCurrencyClicked() {
        val state = currentLoadedState ?: return
        setEffect { BuyInputContract.Effect.CryptoSelection(state.supportedCurrencies) }
    }

    private fun onPaymentMethodClicked() {
        val paymentMethods = currentLoadedState?.paymentInstruments ?: return

        setEffect {
            if (paymentMethods.isEmpty()) {
                BuyInputContract.Effect.AddCard
            } else {
                BuyInputContract.Effect.PaymentMethodSelection(paymentMethods)
            }
        }
    }

    private fun onFiatAmountChanged(fiatAmount: BigDecimal, changeByUser: Boolean) {
        val state = currentLoadedState ?: return

        val cryptoAmount = fiatAmount * state.oneFiatUnitToCryptoRate
        setState {
            state.copy(
                fiatAmount = fiatAmount,
                cryptoAmount = cryptoAmount,
            ).validate()
        }

        updateAmounts(
            fiatAmountChangedByUser = changeByUser,
            cryptoAmountChangedByUser = false,
        )
    }

    private fun onCryptoAmountChanged(cryptoAmount: BigDecimal, changeByUser: Boolean) {
        val state = currentLoadedState ?: return

        val fiatAmount = cryptoAmount * state.oneCryptoUnitToFiatRate
        setState {
            state.copy(
                fiatAmount = fiatAmount,
                cryptoAmount = cryptoAmount,
            ).validate()
        }

        updateAmounts(
            fiatAmountChangedByUser = false,
            cryptoAmountChangedByUser = changeByUser,
        )
    }

    private fun onContinueClicked() {
        val state = currentLoadedState ?: return
        val quoteResponse = state.quoteResponse ?: return
        val paymentInstrument = state.selectedPaymentMethod ?: return

        //todo: validation

        setEffect {
            BuyInputContract.Effect.OpenOrderPreview(
                fiatAmount = state.fiatAmount,
                fiatCurrency = state.fiatCurrency,
                quoteResponse = quoteResponse,
                cryptoCurrency = state.cryptoCurrency,
                paymentInstrument = paymentInstrument
            )
        }
    }

    private fun updateAmounts(fiatAmountChangedByUser: Boolean, cryptoAmountChangedByUser: Boolean) {
        val state = currentLoadedState ?: return

        setEffect { BuyInputContract.Effect.UpdateFiatAmount(state.fiatAmount, fiatAmountChangedByUser) }
        setEffect { BuyInputContract.Effect.UpdateCryptoAmount(state.cryptoAmount, cryptoAmountChangedByUser) }
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            val instrumentsResponse = buyApi.getPaymentInstruments()
            val supportedCurrencies = buyApi.getSupportedCurrencies().data ?: emptyList()

            if (instrumentsResponse.status == Status.ERROR || supportedCurrencies.isEmpty()) {
                showErrorState()
                return@launch
            }

            val firstWallet = supportedCurrencies.firstOrNull { isWalletEnabled(it) }

            val quoteResponse = firstWallet?.let {
                buyApi.getQuote(
                    from = currentFiatCurrency,
                    to = it
                )
            }

            if (quoteResponse == null || quoteResponse.status == Status.ERROR) {
                showErrorState()
                return@launch
            }

            setState {
                BuyInputContract.State.Loaded(
                    quoteResponse = requireNotNull(quoteResponse.data),
                    fiatCurrency = currentFiatCurrency,
                    cryptoCurrency = firstWallet,
                    supportedCurrencies = supportedCurrencies,
                    paymentInstruments = instrumentsResponse.data ?: emptyList(),
                    profile = profileManager.getProfile()
                )
            }
        }
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
                .onEach { delay(1000) }
                .collect {
                    if (it == 0L) {
                        requestNewQuote()
                    }
                }
        }
    }

    private fun requestNewQuote() {
        viewModelScope.launch {
            val state = currentLoadedState ?: return@launch
            setState { state.copy(rateLoadingVisible = true) }

            val response = buyApi.getQuote(state.fiatCurrency, state.cryptoCurrency)
            when (response.status) {
                Status.SUCCESS -> {
                    val latestState = currentLoadedState ?: return@launch
                    val responseData = requireNotNull(response.data)

                    setState {
                        latestState.copy(
                            rateLoadingVisible = false,
                            quoteResponse = responseData
                        )
                    }
                    startQuoteTimer()
                }
                Status.ERROR -> {
                    val latestState = currentLoadedState ?: return@launch

                    setState {
                        latestState.copy(
                            rateLoadingVisible = false,
                            quoteResponse = null
                        )
                    }

                    setEffect {
                        BuyInputContract.Effect.ShowToast(
                            getString(R.string.Swap_Input_Error_NoSelectedPairData), true
                        )
                    }
                }
            }
        }
    }

    private suspend fun isWalletEnabled(currencyCode: String): Boolean {
        val enabledWallets = metaDataManager.enabledWallets().first()
        val token = TokenUtil.tokenForCode(currencyCode) ?: return false
        return token.isSupported && enabledWallets.contains(token.currencyId)
    }

    private fun showErrorState() {
        setState { BuyInputContract.State.Error }
        setEffect {
            BuyInputContract.Effect.ShowToast(
                getString(R.string.Swap_Input_Error_Network)
            )
        }
    }

    private fun BuyInputContract.State.Loaded.validate() = copy(
        continueButtonEnabled = !cryptoAmount.isZero()
                && !fiatAmount.isZero()
                && selectedPaymentMethod != null
    )
}