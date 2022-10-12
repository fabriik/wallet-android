package com.fabriik.buy.ui.features.input

import android.content.Context
import com.breadwallet.util.formatFiatForUi
import com.fabriik.buy.R
import com.fabriik.buy.ui.features.paymentmethod.PaymentMethodFragment
import com.fabriik.common.data.model.PaymentInstrument
import com.fabriik.common.data.model.Profile
import com.fabriik.common.data.model.isKyc1
import com.fabriik.common.data.model.isKyc2
import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.trade.data.model.FeeAmountData
import com.fabriik.trade.data.response.QuoteResponse
import java.math.BigDecimal
import java.math.RoundingMode

interface BuyInputContract {

    sealed class Event : FabriikContract.Event {
        object DismissClicked : Event()
        object ContinueClicked : Event()
        object PaymentMethodClicked : Event()
        object CryptoCurrencyClicked : Event()
        object QuoteTimeoutRetry : Event()

        data class FiatAmountChange(val amount: BigDecimal) : Event()
        data class CryptoAmountChange(val amount: BigDecimal) : Event()
        data class CryptoCurrencyChanged(val currencyCode: String) : Event()
        data class PaymentMethodResultReceived(val result: PaymentMethodFragment.Result) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        object AddCard : Effect()

        data class PaymentMethodSelection(val paymentInstruments: List<PaymentInstrument>) : Effect()
        data class ShowError(val message: String) : Effect()
        data class CryptoSelection(val currencies: List<String>) : Effect()
        data class UpdateFiatAmount(val amount: BigDecimal, val changeByUser: Boolean) : Effect()
        data class UpdateCryptoAmount(val amount: BigDecimal, val changeByUser: Boolean) : Effect()

        data class OpenOrderPreview(
            val networkFee: FeeAmountData,
            val fiatAmount: BigDecimal,
            val fiatCurrency: String,
            val cryptoCurrency: String,
            val quoteResponse: QuoteResponse,
            val paymentInstrument: PaymentInstrument
        ) : Effect()
    }

    sealed class State : FabriikContract.State {
        object Error : State()
        object Loading : State()
        data class Loaded(
            val supportedCurrencies: List<String>,
            val quoteResponse: QuoteResponse?,
            val paymentInstruments: List<PaymentInstrument>,
            val selectedPaymentMethod: PaymentInstrument? = null,
            val networkFee: FeeAmountData? = null,
            val fiatCurrency: String,
            val cryptoCurrency: String,
            val fiatAmount: BigDecimal = BigDecimal.ZERO,
            val cryptoAmount: BigDecimal = BigDecimal.ZERO,
            val rateLoadingVisible: Boolean = false,
            val continueButtonEnabled: Boolean = false,
            val fullScreenLoadingVisible: Boolean = false,
            val profile: Profile?
        ) : State() {
            val minFiatAmount: BigDecimal
                get() = quoteResponse?.minimumValueUsd ?: BigDecimal.ZERO
            val maxFiatAmount: BigDecimal
                get() = quoteResponse?.maximumValueUsd ?: BigDecimal.ZERO
            val oneFiatUnitToCryptoRate: BigDecimal
                get() = quoteResponse?.exchangeRate ?: BigDecimal.ZERO
            val oneCryptoUnitToFiatRate: BigDecimal
                get() = BigDecimal.ONE.divide((quoteResponse?.exchangeRate ?: BigDecimal.ONE), 20, RoundingMode.HALF_UP) ?: BigDecimal.ZERO
        }
    }


    sealed class ErrorMessage {

        abstract fun toString(context: Context): String

        object NetworkIssues : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.ErrorMessages_NetworkIssues
            )
        }

        class MinBuyAmount(private val minFiatAmount: BigDecimal, val fiatCurrency: String): ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.ErrorMessages_amountTooLow, minFiatAmount.formatFiatForUi(
                    currencyCode = fiatCurrency,
                    showCurrencyName = true,
                    showCurrencySymbol = false
                ), fiatCurrency
            )
        }

        class MaxBuyAmount(private val maxFiatAmount: BigDecimal, private val fiatCurrency: String) : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.ErrorMessages_AmountTooHigh, maxFiatAmount.formatFiatForUi(
                    currencyCode = fiatCurrency,
                    showCurrencyName = true,
                    showCurrencySymbol = false
                ),fiatCurrency
            )
        }
    }
}