package com.fabriik.trade.ui.features.swap

import android.content.Context
import com.breadwallet.breadbox.formatCryptoForUi
import com.fabriik.common.data.enums.ProfileRole
import com.fabriik.common.data.model.Profile
import com.fabriik.common.data.model.hasRole
import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.common.ui.dialog.FabriikGenericDialogArgs
import com.fabriik.trade.R
import com.fabriik.trade.data.model.AmountData
import com.fabriik.trade.data.model.FeeAmountData
import com.fabriik.trade.data.response.QuoteResponse
import java.math.BigDecimal

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
            val sendingNetworkFee: FeeAmountData? = null,
            val receivingNetworkFee: FeeAmountData? = null,
            val confirmButtonEnabled: Boolean = false,
            val swapErrorMessage: ErrorMessage? = null,
            val fullScreenLoadingVisible: Boolean = false,
            val profile: Profile?,
        ) : State() {
            val rate: BigDecimal
                get() = quoteResponse?.exchangeRate ?: BigDecimal.ZERO
            val sendingNetworkFeeRate: BigDecimal
                get() = quoteResponse?.fromFeeCurrency?.rate ?: BigDecimal.ONE
            val receivingNetworkFeeRate: BigDecimal
                get() = quoteResponse?.toFeeCurrency?.rate ?: BigDecimal.ONE
            val minCryptoAmount: BigDecimal
                get() = quoteResponse?.minimumValue ?: BigDecimal.ZERO
            val maxCryptoAmount: BigDecimal
                get() = quoteResponse?.maximumValue ?: BigDecimal.ZERO
            val isKyc1: Boolean
                get() = profile?.hasRole(ProfileRole.KYC_LEVEL_1) == true
            val isKyc2: Boolean
                get() = profile?.hasRole(ProfileRole.KYC_LEVEL_2) == true
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

        class MinSwapAmount(private val minAmount: BigDecimal, private val cryptoCurrency: String) : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.Swap_Input_Error_MinAmount, minAmount.formatCryptoForUi(cryptoCurrency)
            )
        }

        class MaxSwapAmount(private val maxAmount: BigDecimal, private val cryptoCurrency: String) : ErrorMessage() {
            override fun toString(context: Context) = context.getString(
                R.string.Swap_Input_Error_MaxAmount, maxAmount.formatCryptoForUi(cryptoCurrency)
            )
        }
    }
}
