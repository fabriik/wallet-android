package com.fabriik.trade.ui.features.swap

import android.content.Context
import com.breadwallet.breadbox.formatCryptoForUi
import com.breadwallet.util.formatFiatForUi
import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.trade.R
import com.fabriik.trade.data.model.AmountData
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
            val sendingFee: AmountData,
            val receivingFee: AmountData,
        ) : Effect()

        data class CurrenciesReplaceAnimation(val stateChange: State.Loaded) : Effect()
        data class ShowToast(val message: String) : Effect()
        data class ContinueToSwapProcessing(
            val exchangeId: String,
            val sourceCurrency: String,
            val destinationCurrency: String
        ) : Effect()

        data class UpdateTimer(val timeLeft: Int) : Effect()
        data class SourceSelection(val currencies: List<String>) : Effect()
        data class DestinationSelection(val currencies: List<String>, val sourceCurrency: String) :
            Effect()

        data class UpdateSourceFiatAmount(val bigDecimal: BigDecimal) : Effect()
        data class UpdateSourceCryptoAmount(val bigDecimal: BigDecimal) : Effect()
        data class UpdateDestinationFiatAmount(val bigDecimal: BigDecimal) : Effect()
        data class UpdateDestinationCryptoAmount(val bigDecimal: BigDecimal) : Effect()
    }

    sealed class State : FabriikContract.State {
        object Error : State()
        object Loading : State()
        data class Loaded(
            val minFiatAmount: BigDecimal,
            val maxFiatAmount: BigDecimal,
            val dailyFiatLimit: BigDecimal,
            val lifetimeFiatLimit: BigDecimal,
            val tradingPairs: List<TradingPair>,
            val selectedPair: TradingPair,
            val quoteResponse: QuoteResponse,
            val fiatCurrency: String,
            val sourceCryptoBalance: BigDecimal,
            val sourceCryptoCurrency: String,
            val destinationCryptoCurrency: String,
            val cryptoExchangeRateLoading: Boolean = false,
            val sourceFiatAmount: BigDecimal = BigDecimal.ZERO,
            val sourceCryptoAmount: BigDecimal = BigDecimal.ZERO,
            val destinationFiatAmount: BigDecimal = BigDecimal.ZERO,
            val destinationCryptoAmount: BigDecimal = BigDecimal.ZERO,
            val sendingNetworkFee: AmountData? = null,
            val receivingNetworkFee: AmountData? = null,
            val confirmButtonEnabled: Boolean = false,
            val swapErrorMessage: ErrorMessage? = null,
            val fullScreenLoadingVisible: Boolean = false
        ) : State() {

            val cryptoExchangeRate: BigDecimal
                get() = when {
                    quoteResponse.securityId.startsWith(sourceCryptoCurrency) ->
                        quoteResponse.closeBid
                    else ->
                        BigDecimal.ONE.divide(quoteResponse.closeAsk, 20, RoundingMode.HALF_UP)
                }
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

        object DailyLimitReached : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.Swap_Input_Error_Kyc1DailyLimit
            )
        }

        object LifetimeLimitReached : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.Swap_Input_Error_Kyc1LifetimeLimit
            )
        }
    }

    data class Amounts(
        val sourceFeeData: FeeData,
        val sourceFiatAmount: BigDecimal,
        val sourceCryptoAmount: BigDecimal,
        val destinationFeeData: FeeData,
        val destinationFiatAmount: BigDecimal,
        val destinationCryptoAmount: BigDecimal,
    )

    data class FeeData(
        val fiatFeeAmount: BigDecimal,
        val fiatFeeCurrency: String,
        val cryptoOriginalFeeAmount: BigDecimal,
        val cryptoOriginalFeeCurrency: String,
        val cryptoConvertedFeeAmount: BigDecimal,
        val cryptoConvertedFeeCurrency: String,
    ) {
        fun toAmountData() = AmountData(
            fiatAmount = fiatFeeAmount,
            fiatCurrency = fiatFeeCurrency,
            cryptoAmount = cryptoOriginalFeeAmount,
            cryptoCurrency = cryptoOriginalFeeCurrency
        )
    }
}