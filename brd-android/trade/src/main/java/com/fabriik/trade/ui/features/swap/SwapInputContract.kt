package com.fabriik.trade.ui.features.swap

import android.content.Context
import com.breadwallet.breadbox.formatCryptoForUi
import com.breadwallet.util.formatFiatForUi
import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.common.ui.dialog.FabriikGenericDialogArgs
import com.fabriik.trade.R
import com.fabriik.trade.data.model.AmountData
import com.fabriik.trade.data.model.FeeAmountData
import com.fabriik.trade.data.model.TradingPair
import com.fabriik.trade.data.response.QuoteResponse
import java.math.BigDecimal
import java.math.RoundingMode

interface SwapInputContract {

    sealed class Event : FabriikContract.Event {
        object DismissClicked : Event()
        object ConfirmClicked : Event()
        object OnMinAmountClicked : Event()
        object OnMaxAmountClicked : Event()
        object SourceCurrencyClicked : Event()
        object ReplaceCurrenciesClicked : Event()
        object DestinationCurrencyClicked : Event()
        object OnUserAuthenticationSucceed : Event()
        object OnConfirmationDialogConfirmed : Event()
        data class OnCheckAssetsDialogResult(val result: String?) : Event()
        data class OnTempUnavailableDialogResult(val result: String?) : Event()

        data class SourceCurrencyChanged(val currencyCode: String) : Event()
        data class DestinationCurrencyChanged(val currencyCode: String) : Event()
        data class SourceCurrencyFiatAmountChange(val amount: BigDecimal) : Event()
        data class SourceCurrencyCryptoAmountChange(val amount: BigDecimal) : Event()
        data class DestinationCurrencyFiatAmountChange(val amount: BigDecimal) : Event()
        data class DestinationCurrencyCryptoAmountChange(val amount: BigDecimal) : Event()
        data class OnCurrenciesReplaceAnimationCompleted(val stateChange: State.Loaded) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        object DeselectMinMaxSwitchItems : Effect()
        object RequestUserAuthentication : Effect()
        data class ConfirmDialog(
            val to: AmountData,
            val from: AmountData,
            val rate: BigDecimal,
            val sendingFee: FeeAmountData,
            val receivingFee: FeeAmountData,
        ) : Effect()

        data class CurrenciesReplaceAnimation(val stateChange: State.Loaded) : Effect()
        data class ShowToast(val message: String, val redInfo: Boolean = false) : Effect()
        data class ShowDialog(val args: FabriikGenericDialogArgs) : Effect()
        data class ContinueToSwapProcessing(
            val exchangeId: String,
            val sourceCurrency: String,
            val destinationCurrency: String
        ) : Effect()

        data class UpdateTimer(val timeLeft: Int) : Effect()
        data class SourceSelection(val currencies: List<String>) : Effect()
        data class DestinationSelection(val currencies: List<String>, val sourceCurrency: String) :
            Effect()

        data class UpdateSourceFiatAmount(val bigDecimal: BigDecimal, val changeByUser: Boolean) : Effect()
        data class UpdateSourceCryptoAmount(val bigDecimal: BigDecimal, val changeByUser: Boolean) : Effect()
        data class UpdateDestinationFiatAmount(val bigDecimal: BigDecimal, val changeByUser: Boolean) : Effect()
        data class UpdateDestinationCryptoAmount(val bigDecimal: BigDecimal, val changeByUser: Boolean) : Effect()
    }

    sealed class State : FabriikContract.State {
        object Error : State()
        object Loading : State()
        data class Loaded(
            val minFiatAmount: BigDecimal,
            val maxFiatAmount: BigDecimal,
            val dailyFiatLimit: BigDecimal,
            val lifetimeFiatLimit: BigDecimal,
            val kyc2ExchangeFiatLimit: BigDecimal? = null,
            val tradingPairs: List<TradingPair>,
            val selectedPair: TradingPair,
            val quoteResponse: QuoteResponse?,
            val fiatCurrency: String,
            val sourceCryptoBalance: BigDecimal,
            val sourceCryptoCurrency: String,
            val destinationCryptoCurrency: String,
            val cryptoExchangeRateLoading: Boolean = false,
            val sourceFiatAmount: BigDecimal = BigDecimal.ZERO,
            val sourceCryptoAmount: BigDecimal = BigDecimal.ZERO,
            val destinationFiatAmount: BigDecimal = BigDecimal.ZERO,
            val destinationCryptoAmount: BigDecimal = BigDecimal.ZERO,
            val sendingNetworkFee: FeeAmountData? = null,
            val receivingNetworkFee: FeeAmountData? = null,
            val confirmButtonEnabled: Boolean = false,
            val swapErrorMessage: ErrorMessage? = null,
            val fullScreenLoadingVisible: Boolean = false
        ) : State() {

            val cryptoExchangeRate: BigDecimal
                get() = when {
                    sellingBaseCurrency -> quoteResponse!!.exchangeRate
                    buyingBaseCurrency -> BigDecimal.ONE.divide(quoteResponse!!.exchangeRate, 20, RoundingMode.HALF_UP)
                    else -> BigDecimal.ZERO
                }

            val markupFactor: BigDecimal
                get() = when {
                    sellingBaseCurrency -> BigDecimal.ONE.divide(quoteResponse!!.sellMarkupFactor, 20, RoundingMode.HALF_UP)
                    buyingBaseCurrency -> quoteResponse!!.buyMarkupFactor
                    else -> BigDecimal.ZERO
                }

            val sellingBaseCurrency: Boolean
                get() = quoteResponse?.securityId?.startsWith(sourceCryptoCurrency, true) ?: false

            val buyingBaseCurrency: Boolean
                get() = quoteResponse?.securityId?.startsWith(destinationCryptoCurrency, true) ?: false
        }
    }

    sealed class ErrorMessage {

        abstract fun toString(context: Context): String

        object NetworkIssues : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.Swap_Input_Error_Network
            )
        }

        class InsufficientFunds(private val balance: BigDecimal, private val currencyCode: String) : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.Swap_Input_Error_InsuficientFunds, currencyCode, currencyCode, balance.formatCryptoForUi(null)
            )
        }

        object InsufficientFundsForFee : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.Swap_Input_Error_FeeFunds
            )
        }

        class MinSwapAmount(private val minAmount: BigDecimal, private val fiatCurrency: String) : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.Swap_Input_Error_MinAmount, minAmount.formatFiatForUi(fiatCurrency)
            )
        }

        class MaxSwapAmount(private val maxAmount: BigDecimal, private val fiatCurrency: String) : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.Swap_Input_Error_MaxAmount, maxAmount.formatFiatForUi(fiatCurrency)
            )
        }

        object Kyc1DailyLimitReached : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.Swap_Input_Error_Kyc1DailyLimit
            )
        }

        object Kyc1LifetimeLimitReached : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.Swap_Input_Error_Kyc1LifetimeLimit
            )
        }

        object Kyc2ExchangeLimitReached : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.Swap_Input_Error_Kyc2ExchangeLimit
            )
        }
    }
}