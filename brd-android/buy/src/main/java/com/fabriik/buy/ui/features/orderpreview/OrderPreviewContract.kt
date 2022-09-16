package com.fabriik.buy.ui.features.orderpreview

import com.fabriik.common.data.model.PaymentInstrument
import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.trade.data.model.FeeAmountData
import com.fabriik.trade.data.response.QuoteResponse
import java.math.BigDecimal
import java.math.RoundingMode

class OrderPreviewContract : FabriikContract {

    sealed class Event : FabriikContract.Event {
        object OnBackPressed : Event()
        object OnDismissClicked : Event()
        object OnConfirmClicked : Event()
        object OnCreditInfoClicked : Event()
        object OnNetworkInfoClicked : Event()
        object OnSecurityCodeInfoClicked : Event()
        object OnTermsAndConditionsClicked : Event()
        object OnUserAuthenticationSucceed : Event()

        data class OnSecurityCodeChanged(val securityCode: String) : Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Back : Effect()
        object Dismiss : Effect()
        object TimeoutScreen : Effect()
        object RequestUserAuthentication : Effect()

        data class ShowError(val message: String) : Effect()
        data class PaymentProcessing(
            val paymentReference: String?,
            val redirectUrl: String?,
        ) : Effect()

        data class ShowInfoDialog(
            val image: Int? = null,
            val title: Int,
            val description: Int
        ) : Effect()

        data class OpenWebsite(val url: String): Effect()
    }

    data class State(
        val securityCode: String = "",
        val fiatCurrency: String,
        val cryptoCurrency: String,
        val fiatAmount: BigDecimal,
        val networkFee: FeeAmountData,
        val quoteResponse: QuoteResponse?,
        val paymentInstrument: PaymentInstrument,
        val confirmButtonEnabled: Boolean = false,
        val fullScreenLoadingIndicator: Boolean = false
    ) : FabriikContract.State {

        val oneFiatUnitToCryptoRate: BigDecimal
            get() = quoteResponse?.exchangeRate ?: BigDecimal.ZERO

        val oneCryptoUnitToFiatRate: BigDecimal
            get() = BigDecimal.ONE.divide(quoteResponse?.exchangeRate, 20, RoundingMode.HALF_UP) ?: BigDecimal.ZERO

        val totalFiatAmount: BigDecimal
            get() = fiatAmount + cardFee + networkFee.fiatAmount

        val cryptoAmount: BigDecimal
            get() = fiatAmount * oneFiatUnitToCryptoRate

        val cardFee: BigDecimal
            get() = fiatAmount * (cardFeePercent / 100).toBigDecimal()

        val cardFeePercent: Float
            get() = quoteResponse?.buyCardFeesPercent ?: 0f
    }
}