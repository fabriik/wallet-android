package com.fabriik.buy.ui.features.processing

import com.fabriik.common.ui.base.FabriikContract
import com.fabriik.trade.data.response.QuoteResponse
import java.math.BigDecimal

class PaymentProcessingContract : FabriikContract {

    sealed class Event : FabriikContract.Event {
        object BackToHomeClicked : Event()
        object ContactSupportClicked: Event()
        object PurchaseDetailsClicked: Event()
        object TryDifferentMethodClicked: Event()
        object OnPaymentRedirectResult: Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        object BackToBuy : Effect()
        object ContactSupport : Effect()

        data class ShowError(val message: String) : Effect()
        data class OpenPaymentRedirect(val url: String) : Effect()
        data class GoToPurchaseDetails(val purchaseId: String) : Effect()
    }

    sealed class State: FabriikContract.State {
        data class Processing(
            val paymentReference: String?
        ): State()

        data class PaymentFailed(
            val paymentReference: String?
        ): State()

        data class PaymentCompleted(
            val paymentReference: String
        ): State()
    }
}