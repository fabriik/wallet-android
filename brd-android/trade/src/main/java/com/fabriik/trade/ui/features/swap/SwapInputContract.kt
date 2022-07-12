package com.fabriik.trade.ui.features.swap

import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.common.ui.customview.FabriikSwitch
import com.fabriik.trade.data.model.TradingPair
import com.fabriik.trade.data.response.QuoteResponse
import java.math.BigDecimal

interface SwapInputContract {

    sealed class Event : FabriikContract.Event {
        object DismissClicked : Event()
        object ConfirmClicked : Event()
        object OnMinAmountClicked : Event()
        object OnMaxAmountClicked : Event()
        object SourceCurrencyClicked : Event()
        object ReplaceCurrenciesClicked : Event()
        object DestinationCurrencyClicked : Event()

        data class SourceCurrencyChanged(val currencyCode: String) : Event()
        data class DestinationCurrencyChanged(val currencyCode: String) : Event()
        data class SourceCurrencyFiatAmountChange(val amount: BigDecimal) : Event()
        data class SourceCurrencyCryptoAmountChange(val amount: BigDecimal) : Event()
        data class DestinationCurrencyFiatAmountChange(val amount: BigDecimal) : Event()
        data class DestinationCurrencyCryptoAmountChange(val amount: BigDecimal) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        object ConfirmDialog : Effect()
        object DeselectMinMaxSwitchItems : Effect()
        data class ShowToast(val message: String): Effect()
        data class ContinueToSwapProcessing(
            val sourceCurrency: String,
            val destinationCurrency: String
        ) : Effect()
        data class UpdateTimer(val timeLeft: Int) : Effect()
        data class SourceSelection(val currencies: List<String>) : Effect()
        data class DestinationSelection(val currencies: List<String>, val sourceCurrency: String) : Effect()
        data class UpdateSourceFiatAmount(val bigDecimal: BigDecimal) : Effect()
        data class UpdateSourceCryptoAmount(val bigDecimal: BigDecimal) : Effect()
        data class UpdateDestinationFiatAmount(val bigDecimal: BigDecimal) : Effect()
        data class UpdateDestinationCryptoAmount(val bigDecimal: BigDecimal) : Effect()
    }

    sealed class State : FabriikContract.State {
        object Error : State()
        object Loading : State()
        data class Loaded(
            val tradingPairs: List<TradingPair>,
            val selectedPair: TradingPair,
            val quoteResponse: QuoteResponse,
            val fiatCurrency: String,
            val sourceCryptoBalance: BigDecimal,
            val sourceCryptoCurrency: String,
            val destinationCryptoCurrency: String,
            val cryptoExchangeRate: BigDecimal,
            val cryptoExchangeRateLoading: Boolean = false,
            val sourceFiatAmount: BigDecimal = BigDecimal.ZERO,
            val sourceCryptoAmount: BigDecimal = BigDecimal.ZERO,
            val destinationFiatAmount: BigDecimal = BigDecimal.ZERO,
            val destinationCryptoAmount: BigDecimal = BigDecimal.ZERO,
            val sendingNetworkFee: NetworkFeeData? = null,
            val receivingNetworkFee: NetworkFeeData? = null,
            val confirmButtonEnabled: Boolean = false
        ) : State()
    }

    data class NetworkFeeData(
        val fiatAmount: BigDecimal,
        val fiatCurrency: String,
        val cryptoAmount: BigDecimal,
        val cryptoCurrency: String
    )
}