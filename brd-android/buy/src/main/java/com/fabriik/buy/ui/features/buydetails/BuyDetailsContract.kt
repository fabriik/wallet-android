package com.fabriik.buy.ui.features.buydetails

import com.fabriik.buy.data.enums.BuyDetailsFlow
import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.trade.data.response.ExchangeOrder
import java.math.BigDecimal
import java.math.RoundingMode

interface BuyDetailsContract {

    sealed class Event : FabriikContract.Event {
        object LoadData : Event()
        object BackClicked : Event()
        object DismissClicked : Event()
        object OrderIdClicked : Event()
        object TransactionIdClicked : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        data class ShowToast(val message: String): Effect()
        data class CopyToClipboard(val data: String) : Effect()
    }

    sealed class State : FabriikContract.State {
        object Loading : State()
        object Error : State()
        data class Loaded(
            val data: ExchangeOrder,
            val flow: BuyDetailsFlow
        ) : State() {
            val cardFee: BigDecimal
                get() = data.source.usdFee ?: BigDecimal.ZERO

            val networkFee: BigDecimal
                get() = data.destination.usdFee ?: BigDecimal.ZERO

            val purchasedAmount: BigDecimal
                get() = data.source.currencyAmount - (data.source.usdFee ?: BigDecimal.ZERO) - (data.destination.usdFee ?: BigDecimal.ZERO)

            val fiatPriceForOneCryptoUnit: BigDecimal
                get() = BigDecimal.ONE.divide(data.rate ?: BigDecimal.ONE, 20, RoundingMode.HALF_UP)
        }
    }
}