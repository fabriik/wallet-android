package com.fabriik.buy.ui.features.processing

import com.fabriik.buy.R
import com.fabriik.common.ui.base.FabriikContract

class PaymentProcessingContract : FabriikContract {

    sealed class Event : FabriikContract.Event {
        object BackToHomeClicked : Event()
        object ContactSupportClicked: Event()
        object PurchaseDetailsClicked: Event()
        object TryDifferentMethodClicked: Event()
    }

    sealed class Effect : FabriikContract.Effect {
        object Dismiss : Effect()
        object BackToBuy : Effect()
        object ContactSupport : Effect()
        data class GoToPurchaseDetails(val purchaseId: String) : Effect()
    }

    data class State(
        val paymentReference: String?
    ) : FabriikContract.State {

        val status: Status
            get() = if (paymentReference != null) Status.SUCCESS else Status.FAILED
    }

    enum class Status(
        val icon: Int,
        val title: Int,
        val description: Int,
        val goHomeVisible: Boolean,
        val contactSupportVisible: Boolean,
        val purchaseDetailsVisible: Boolean,
        val tryDifferentMethodVisible: Boolean
    ) {
        SUCCESS(
            icon = R.drawable.ic_payment_succeed,
            title = R.string.Buy_ProcessingSucceed_Title,
            description = R.string.Buy_ProcessingSucceed_Description,
            goHomeVisible = true,
            contactSupportVisible = false,
            purchaseDetailsVisible = true,
            tryDifferentMethodVisible = false,
        ),

        FAILED(
            icon = R.drawable.ic_payment_failed,
            title = R.string.Buy_ProcessingFailed_Title,
            description = R.string.Buy_ProcessingFailed_Description,
            goHomeVisible = false,
            contactSupportVisible = true,
            purchaseDetailsVisible = false,
            tryDifferentMethodVisible = true
        )
    }
}