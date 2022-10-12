package com.fabriik.trade.ui.features.swap

import android.content.Context
import com.breadwallet.breadbox.formatCryptoForUi
import com.breadwallet.util.formatFiatForUi
import com.fabriik.common.data.model.Profile
import com.fabriik.common.data.model.isKyc1
import com.fabriik.common.data.model.isKyc2
import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.common.ui.dialog.FabriikGenericDialogArgs
import com.fabriik.trade.R
import com.fabriik.trade.data.model.AmountData
import com.fabriik.trade.data.model.FeeAmountData
import com.fabriik.trade.data.response.QuoteResponse
import com.fabriik.trade.ui.customview.SwapCardView.Companion.SCALE_CRYPTO
import com.fabriik.trade.utils.EstimateSendingFee
import java.math.BigDecimal
import java.util.*

interface SwapInputContract {

    sealed class Event : FabriikContract.Event {
        object DismissClicked : Event()
        object ConfirmClicked : Event()
        object SourceCurrencyClicked : Event()
        object ReplaceCurrenciesClicked : Event()
        object DestinationCurrencyClicked : Event()
        object OnUserAuthenticationSucceed : Event()
        object OnConfirmationDialogConfirmed : Event()
        object OnResume : Event()
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
        object ClearInputFocus : Effect()
        object RequestUserAuthentication : Effect()
        object TransactionFailedScreen : Effect()

        data class ConfirmDialog(
            val to: AmountData,
            val from: AmountData,
            val rate: BigDecimal,
            val sendingFee: FeeAmountData,
            val receivingFee: FeeAmountData,
        ) : Effect()

        data class CurrenciesReplaceAnimation(val stateChange: State.Loaded) : Effect()
        data class ShowError(val message: String) : Effect()
        data class ShowErrorMessage(val error: ErrorMessage) : Effect()
        data class ShowToast(val message: String) : Effect()
        data class ShowDialog(val args: FabriikGenericDialogArgs) : Effect()
        data class ContinueToSwapProcessing(
            val exchangeId: String,
            val sourceCurrency: String,
            val destinationCurrency: String
        ) : Effect()

        data class UpdateTimer(val timeLeft: Int) : Effect()
        data class SourceSelection(val currencies: List<String>) : Effect()
        data class DestinationSelection(val currencies: List<String>) :
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
            val supportedCurrencies: List<String>,
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
            val sendingNetworkFee: EstimateSendingFee.Result = EstimateSendingFee.Result.Unknown,
            val receivingNetworkFee: FeeAmountData? = null,
            val confirmButtonEnabled: Boolean = false,
            val fullScreenLoadingVisible: Boolean = false,
            val profile: Profile?,
        ) : State() {
            val rate: BigDecimal
                get() = quoteResponse?.exchangeRate ?: BigDecimal.ZERO
            val minCryptoAmount: BigDecimal
                get() = quoteResponse?.minimumValue ?: BigDecimal.ZERO
            private val dailySwapAmountUsed: BigDecimal
                get() = profile?.exchangeLimits?.swapUsedDaily ?: BigDecimal.ZERO
            private val dailySwapLimit: BigDecimal
                get() = profile?.exchangeLimits?.swapAllowanceDaily ?: BigDecimal.ZERO
            val dailySwapAmountLeft = dailySwapLimit - dailySwapAmountUsed
            private val lifetimeSwapAllowance: BigDecimal
                get() = profile?.exchangeLimits?.swapAllowanceLifetime ?: BigDecimal.ZERO
            private val lifetimeSwapAmountUsed: BigDecimal
                get() = profile?.exchangeLimits?.swapUsedLifetime ?: BigDecimal.ZERO
            val lifetimeSwapAmountLeft = lifetimeSwapAllowance - lifetimeSwapAmountUsed
            val isKyc1: Boolean
                get() = profile?.isKyc1() == true
            val isKyc2: Boolean
                get() = profile?.isKyc2() == true
            val requiredSourceFee: BigDecimal?
                get() = estimatedSourceFee ?: quoteResponse?.fromFee
            private val estimatedSourceFee: BigDecimal?
                get() = when (sendingNetworkFee) {
                    is EstimateSendingFee.Result.Estimated -> sendingNetworkFee.cryptoAmountIfIncludedOrNull()
                    else -> null
                }
        }
    }

    sealed class ErrorMessage {

        abstract fun toString(context: Context): String

        object NetworkIssues : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.ErrorMessages_NetworkIssues
            )
        }

        class InsufficientFunds(private val requiredFee: BigDecimal, val currencyCode: String) : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.ErrorMessages_balanceTooLow, requiredFee.formatCryptoForUi(null), currencyCode.uppercase(), currencyCode.uppercase()
            )
        }

        object InsufficientFundsForFee : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.ErrorMessages_networkFee
            )
        }

        class InsufficientEthFundsForFee(val cryptoCurrency: String) : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.ErrorMessages_notEnoughEthForFee, cryptoCurrency.uppercase()
            )
        }

        class MinSwapAmount(private val minAmount: BigDecimal, private val cryptoCurrency: String) : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.ErrorMessages_amountTooLow, minAmount.formatCryptoForUi(
                    currencyCode = null,
                    scale = SCALE_CRYPTO
                ),
                cryptoCurrency
            )
        }

        data class Kyc1DailyLimit(val limit: BigDecimal, val fiatCurrency: String) : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.ErrorMessages_overDailyLimit, limit.formatFiatForUi(
                    currencyCode = fiatCurrency,
                    showCurrencyName = false
                )
            )
        }

        data class Kyc1LifetimeLimit(val limit: BigDecimal, val fiatCurrency: String) : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.ErrorMessages_overLifetimeLimit, limit.formatFiatForUi(
                    currencyCode = fiatCurrency,
                    showCurrencyName = false
                )
            )
        }

        data class Kyc2DailyLimit(val limit: BigDecimal, val fiatCurrency: String) : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.ErrorMessages_overDailyLimit, limit.formatFiatForUi(
                    currencyCode = fiatCurrency,
                    showCurrencyName = false
                )
            )
        }
    }
}
